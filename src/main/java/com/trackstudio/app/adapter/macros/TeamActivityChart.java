package com.trackstudio.app.adapter.macros;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import com.trackstudio.exception.GranException;
import com.trackstudio.secured.SecuredMessageBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.startup.I18n;

import net.jcip.annotations.Immutable;

@Immutable
public class TeamActivityChart extends PersonalActivityChart {

	public TeamActivityChart(SecuredTaskBean task,
			List<SecuredTaskBean> list, String options) {
		super(task, list, options);
		
	}
	@Override
	public String calculate() throws GranException{
		if (list!=null && !list.isEmpty()){
	    try {

              Calendar endAt = task.getUpdatedate();
              Calendar startAt = list.get(0).getSubmitdate();
              Periods periods = null;
              int totalPeriods = 0;
              if (options!=null && options.length()>0) {
              	String[] opt = options.split(",");
              	for (String s: opt){
              		
              		int separator = s.indexOf(":");
              		if (separator>-1){
              		String key = s.substring(0, separator);
              		if (key.contains("period")){
              			String value = s.substring(separator);
              			if (value.contains("days")) periods = new DayPeriods();
              			else if (value.contains("weeks")) periods = new WeekPeriods();
              			else if (value.contains("months")) periods = new MonthPeriods();
              		}
              		}
              	}
              }
              if (periods==null){
              periods = new DayPeriods();
              totalPeriods = periods.since(endAt, startAt);
              if (totalPeriods> PERIODS){
                  periods = new WeekPeriods();
                  totalPeriods = periods.since(endAt, startAt);
                  if (totalPeriods>PERIODS){
                      periods = new MonthPeriods();
                      totalPeriods = periods.since(endAt, startAt);
                  }
              }
              } else totalPeriods = periods.since(endAt, startAt);
              HashMap<SecuredUserBean, Long[]> chartTable = new HashMap<SecuredUserBean, Long[]>();
              long[] spentTimeTable = new long[totalPeriods+1];
              
              for (SecuredTaskBean t : list) {
            	  long[] currentSpentTimeTable = new long[totalPeriods+1];
            	  Arrays.fill(currentSpentTimeTable, 0L);
                  
                  int lastPeriod = 0;
                  for (SecuredMessageBean msg : t.getMessages()) {
                      if (msg.getHrs()!=null) {
                       long  spentTime=msg.getHrs();
                          SecuredUserBean author = msg.getSubmitter();
                        	if (chartTable.get(author)==null){
                        		Long[] array = new Long[totalPeriods+1];
                        		Arrays.fill(array, 0L);
                        		chartTable.put(author, array);
                        	}
                        	Long[] userSpentTime =  chartTable.get(author);
                        	if (msg.getTime().before(startAt) || (periods.since(msg.getTime(), startAt)==0)){
                                userSpentTime[0] = userSpentTime[0]+spentTime;
                                currentSpentTimeTable[0] = currentSpentTimeTable[0]+spentTime;
                                 lastPeriod = 0;
                             } else {
                                 int since = periods.since( msg.getTime(), startAt);
                                 	userSpentTime[since]=userSpentTime[since]+ spentTime;
                                 	currentSpentTimeTable[since] = currentSpentTimeTable[since]+spentTime;
                                     lastPeriod = since;
                             }
                      }
                  }

                  for (int i = 0; i<=totalPeriods; i++){
                      spentTimeTable[i]+=currentSpentTimeTable[i];
                  }
              }

              for (SecuredUserBean u: chartTable.keySet()){
            	  Long[] summaryTable =  chartTable.get(u);  
              for (int i = 1; i<=totalPeriods; i++){
                      summaryTable[i]+=summaryTable[i-1];
                  }
              }
             StringBuffer s = new StringBuffer();
            String id = String.valueOf(System.currentTimeMillis());
             s.append("<div id=\"tactivitychart_div").append(id).append("\"></div>\n");
             s.append("<script type=\"text/javascript\">\n");
             s.append("google.load(\'visualization\', \'1.0\', {\'packages\':[\'corechart\']});\n");
             s.append("google.setOnLoadCallback(drawtactivitychart").append(task.getId()).append(");\n");
             s.append("function drawtactivitychart").append(task.getId()).append("() {\n");
             s.append("var data = new google.visualization.DataTable();\n");
             s.append("data.addColumn(\'date\', \'").append(I18n.getString(task.getSecure(), "PERIOD")).append("\');\n");
             for (SecuredUserBean u: chartTable.keySet())
             s.append("data.addColumn(\'number\', \'").append(u.getName()).append("\');\n");
             
             
              Calendar flow = periods.reset(startAt);
              Calendar out = (Calendar)flow.clone();
              out.add(Calendar.MINUTE, -1);
              s.append("data.addRow([").append("new Date(").append(out.get(Calendar.YEAR)).append(",").append(out.get(Calendar.MONTH)).append(",").append(out.get(Calendar.DAY_OF_MONTH)).append(")");
              for (SecuredUserBean u: chartTable.keySet())
              s.append(", ").append(0);
              s.append("]);\n");
              for (int i =0 ; i<= totalPeriods; i++){
              	flow.add(periods.getShift(), 1);
              	out = (Calendar)flow.clone();
              	out.add(Calendar.MINUTE, -1);
                        //if (spentTimeTable[i]>0)
                  s.append("data.addRow([").append("new Date(").append(out.get(Calendar.YEAR)).append(",").append(out.get(Calendar.MONTH)).append(",").append(out.get(Calendar.DAY_OF_MONTH)).append(")");
                  for (SecuredUserBean u: chartTable.keySet())
                  s.append(", ").append(Math.round((float)chartTable.get(u)[i]/3600f));
                  s.append("]);\n");
                  
              }
              
              s.append("var options = {").append(options).append("};\n");
      		

      		s.append("		      var chart = new google.visualization.ComboChart(document.getElementById(\'tactivitychart_div").append(id).append("\'));\n");
      		s.append(" data.sort({column: 0});\n");
      		s.append(" var formatter = new google.visualization.DateFormat({pattern: \"").append(periods.getPattern()).append("\"});\n");
      		s.append(" var dataView = new google.visualization.DataView(data);\n");
      		s.append(" dataView.setColumns([{calc: function(data, row) { return formatter.formatValue(data.getValue(row, 0)); }, type:'string'}");
      		for (int j=1; j<=chartTable.size(); j++) s.append(", "+j);
      		s.append("]);\n");

      		s.append("chart.draw(dataView, options);\n");
      		s.append("}\n");
      		s.append("</script>\n");
              return s.toString();


         } catch (Exception e) {
          e.printStackTrace();
          throw new GranException(e);
      }
		} else return "";
	}
}
