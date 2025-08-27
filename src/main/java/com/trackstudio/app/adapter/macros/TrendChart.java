package com.trackstudio.app.adapter.macros;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.apache.xerces.dom.DOMImplementationImpl;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.trackstudio.app.report.handmade.HandMadeReportManager;
import com.trackstudio.exception.GranException;
import com.trackstudio.secured.SecuredMessageBean;
import com.trackstudio.secured.SecuredMstatusBean;
import com.trackstudio.secured.SecuredStatusBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredTransitionBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.formatter.DateFormatter;

import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class TrendChart extends Chart {

    public TrendChart(SecuredTaskBean task, List<SecuredTaskBean> list,
                      String options) {
        super(task, list, options);
        try {
            SecuredUserBean user = task.getSecure().getUser();
            this.formatter = user.getDateFormatter();
        } catch (GranException ge) {
            ge.printStackTrace();
        }
    }

    public static final String YEAR = "year";
    public static final String MONTH = "month";
    public static final String WEEK = "weeks";
    public static final String DAY = "day";
    public static final String HOUR = "hour";
    protected volatile DateFormatter formatter = null;


    @Override
    public String calculate() throws GranException {
        if (list != null && !list.isEmpty()) {
            try {

                Calendar endAt = task.getUpdatedate();
                Calendar startAt = list.get(0).getSubmitdate();
                Periods periods = null;
                int totalPeriods = 0;
                if (options != null && options.length() > 0) {
                    String[] opt = options.split(",");
                    for (String s : opt) {

                        int separator = s.indexOf(":");
                        if (separator > -1) {
                            String key = s.substring(0, separator);
                            if (key.contains("period")) {
                                String value = s.substring(separator);
                                if (value.contains("days")) periods = new DayPeriods();
                                else if (value.contains("weeks")) periods = new WeekPeriods();
                                else if (value.contains("months")) periods = new MonthPeriods();
                            }
                        }
                    }
                }
                if (periods == null) {
                    periods = new DayPeriods();
                    totalPeriods = periods.since(endAt, startAt);
                    if (totalPeriods > PERIODS) {
                        periods = new WeekPeriods();
                        totalPeriods = periods.since(endAt, startAt);
                        if (totalPeriods > PERIODS) {
                            periods = new MonthPeriods();
                            totalPeriods = periods.since(endAt, startAt);
                        }
                    }
                } else totalPeriods = periods.since(endAt, startAt);

                ArrayList<SecuredStatusBean> states = new ArrayList<SecuredStatusBean>();
                HashMap<SecuredStatusBean, Integer[]> chartTable = calculateTrend(startAt, periods, totalPeriods, states);


                StringBuffer s = new StringBuffer();
                String id = String.valueOf(System.currentTimeMillis());
                s.append("<div id=\"trendchart_div").append(id).append("\"></div>\n");
                s.append("<script type=\"text/javascript\">\n");
                s.append("google.load(\'visualization\', \'1.0\', {\'packages\':[\'corechart\']});\n");
                s.append("google.setOnLoadCallback(drawtrendchart").append(task.getId()).append(");\n");
                s.append("function drawtrendchart").append(task.getId()).append("() {\n");
                s.append("var data = new google.visualization.DataTable();\n");
                s.append("data.addColumn(\'date\', \'").append(I18n.getString(task.getSecure(), "PERIOD")).append("\');\n");
                for (SecuredStatusBean state : states)
                    s.append("data.addColumn(\'number\', \'").append(state.getName()).append("\');\n");


                Calendar flow = periods.reset(startAt);
                for (int i = 0; i <= totalPeriods; i++) {
                    flow.add(periods.getShift(), 1);
                    Calendar out = (Calendar) flow.clone();
                    out.add(Calendar.MINUTE, -1);
                    s.append("data.addRow([").append("new Date(").append(out.get(Calendar.YEAR)).append(",").append(out.get(Calendar.MONTH)).append(",")
                            .append(out.get(Calendar.DAY_OF_MONTH)).append(")");
                    for (SecuredStatusBean b : states) {
                        Integer[] calculations = chartTable.get(b);
                        s.append(", ").append(calculations[i]);
                    }
                    s.append("]);\n");
                }
                s.append("var options = {");
                s.append("\'isStacked\': true,\n");
                s.append("\'colors\': [");
                for (SecuredStatusBean state : states)
                    s.append("\'").append(state.getColor()).append("\', ");
                s.append("\'black\']\n");
                if (options != null && options.length() > 0) s.append(", ").append(options);
                s.append("};\n");
                s.append("		      var chart = new google.visualization.ColumnChart(document.getElementById(\'trendchart_div").append(id).append("\'));\n");
                s.append(" data.sort({column: 0});\n");
                s.append(" var formatter = new google.visualization.DateFormat({pattern: \"").append(periods.getPattern()).append("\"});\n");
                s.append(" var dataView = new google.visualization.DataView(data);\n");
                s.append(" dataView.setColumns([{calc: function(data, row) { return formatter.formatValue(data.getValue(row, 0)); }, type:'string'}");
                for (int j = 1; j <= states.size(); j++) s.append(", " + j);
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

    protected HashMap<SecuredStatusBean, Integer[]> calculateTrend(Calendar startAt, Periods periods,
                                                                   int totalPeriods,
                                                                   ArrayList<SecuredStatusBean> states) throws GranException {

        HashMap<SecuredStatusBean, Integer[]> chartTable = new HashMap<SecuredStatusBean, Integer[]>();

        for (SecuredTaskBean ts : list) {
            SecuredStatusBean[] currentStatesTable = new SecuredStatusBean[totalPeriods + 1];
            SecuredStatusBean prev = null, now = ts.getStatus();
            int lastPeriod = 0;
            ArrayList<SecuredMessageBean> messages = ts.getMessages();
            if (messages == null || messages.isEmpty()) {
                currentStatesTable[periods.since(ts.getSubmitdate(), startAt)] = ts.getStatus();
                lastPeriod = totalPeriods;
            } else {
                lastPeriod = processMessage(messages, prev, now, startAt, periods, lastPeriod, ts, currentStatesTable);
            }

            if (lastPeriod < totalPeriods + 1) {
                for (int i = lastPeriod; i <= totalPeriods; i++) {
                    currentStatesTable[i] = currentStatesTable[lastPeriod];
                }
            }
            for (int i = 0; i <= totalPeriods; i++) {
                if (currentStatesTable[i] != null) {
                    if (chartTable.get(currentStatesTable[i]) == null) {
                        if (!states.contains(currentStatesTable[i])) {
                            states.add(currentStatesTable[i]);
                        }
                        Integer[] array = new Integer[totalPeriods + 1];
                        Arrays.fill(array, 0);
                        chartTable.put(currentStatesTable[i], array);
                    }
                    chartTable.get(currentStatesTable[i])[i]++;
                }
            }


        }
        return chartTable;
    }

    private int processMessage(ArrayList<SecuredMessageBean> messages, SecuredStatusBean prev,
                               SecuredStatusBean now, Calendar startAt, Periods periods, int lastPeriod,
                               SecuredTaskBean ts, SecuredStatusBean[] currentStatesTable) throws GranException {
        for (SecuredMessageBean msg : messages) {
            SecuredTransitionBean t = nextState(msg.getMstatus(), prev);
            if (t == null) {
                continue;
            }
            SecuredStatusBean start = t.getStart();
            SecuredStatusBean finish = t.getFinish();
            if (start != finish && !start.equals(finish)) {
                now = finish;
                prev = start;
            }
            int since = periods.since(msg.getTime(), startAt);
            if (prev == null) {
                int begin = periods.since(ts.getSubmitdate(), startAt);
                if (since >= begin) { //ignore incorrect msgs
                    currentStatesTable[since] = now;
                    lastPeriod = since;
                    prev = now;
                }
            } else {
                if (since == lastPeriod) {
                    currentStatesTable[since] = now;
                } else if (since>=0) {
                    currentStatesTable[since] = now;
                    if (since > lastPeriod) {
                        for (int i = lastPeriod; i < since; i++) {
                            currentStatesTable[i] = prev;
                        }
                    }
                    lastPeriod = since;
                    prev = now;
                }
            }
        }
        return lastPeriod;
    }

    public String getTableCVS(String period, String delimiter) throws GranException {
        if (list == null || list.isEmpty()) return "";
        Calendar endAt = task.getUpdatedate();
        Calendar startAt = list.get(0).getSubmitdate();


        Periods periods = null;
        if (YEAR.equals(period)) {
            periods = new YearPeriods();
        } else if (MONTH.equals(period)) {
            periods = new MonthPeriods();
        } else if (WEEK.equals(period)) {
            periods = new WeekPeriods();
        } else if (DAY.equals(period)) {
            periods = new DayPeriods();
        } else if (HOUR.equals(period)) {
            periods = new HourPeriods();
        }
        int totalPeriods = periods.since(endAt, startAt);
        ArrayList<SecuredStatusBean> states = new ArrayList<SecuredStatusBean>();
        HashMap<SecuredStatusBean, Integer[]> table = calculateTrend(startAt, periods, totalPeriods, states);
        StringBuilder buffer = new StringBuilder();
        buffer.append(I18n.getInstance().getString("DATE"));
        for (SecuredStatusBean state : states) {
            String head = state.getName().replaceAll("\\s", "_");
            buffer.append(delimiter).append(head);
        }
        buffer.append("\r\n");

        Calendar flow = periods.reset(startAt);
        for (int i = 0; i <= totalPeriods; i++) {
            flow.add(periods.getShift(), 1);
            Calendar out = (Calendar) flow.clone();
            out.add(Calendar.MINUTE, -1);
            buffer.append(formatter.parse(out));
            for (SecuredStatusBean b : states) {
                Integer[] calculations = table.get(b);
                buffer.append(delimiter).append(calculations[i]);
            }
            buffer.append("\r\n");
        }

        return buffer.toString();
    }

    public String getTableXML(String period) throws GranException {
        if (list == null || list.isEmpty()) return "";
        Calendar endAt = task.getUpdatedate();
        Calendar startAt = list.get(0).getSubmitdate();

        Periods periods = null;
        if (YEAR.equals(period)) {
            periods = new YearPeriods();
        } else if (MONTH.equals(period)) {
            periods = new MonthPeriods();
        } else if (WEEK.equals(period)) {
            periods = new WeekPeriods();
        } else if (DAY.equals(period)) {
            periods = new DayPeriods();
        } else if (HOUR.equals(period)) {
            periods = new HourPeriods();
        }
        int totalPeriods = periods.since(endAt, startAt);
        ArrayList<SecuredStatusBean> states = new ArrayList<SecuredStatusBean>();
        HashMap<SecuredStatusBean, Integer[]> table = calculateTrend(startAt, periods, totalPeriods, states);

        DOMImplementationImpl domImpl = new DOMImplementationImpl();
        Document xmlDoc = domImpl.createDocument(null, "trackstudio-task", null);
        Element root = xmlDoc.getDocumentElement();

        Calendar flow = periods.reset(startAt);
        for (int i = 0; i <= totalPeriods; i++) {
            flow.add(periods.getShift(), 1);
            Calendar out = (Calendar) flow.clone();
            out.add(Calendar.MINUTE, -1);

            Element trend = xmlDoc.createElement("trend");
            Element data = xmlDoc.createElement("date");
            data.appendChild(xmlDoc.createTextNode(formatter.parse(out)));
            trend.appendChild(data);
            Element operations = xmlDoc.createElement("operations");
            for (SecuredStatusBean b : states) {
                Element operation = xmlDoc.createElement("operation");
                Integer[] calculations = table.get(b);
                String header = b.getName();
                Element name = xmlDoc.createElement("name");
                name.appendChild(xmlDoc.createTextNode(HandMadeReportManager.stripNonValidXMLCharacters(header)));
                Element total = xmlDoc.createElement("total");
                total.appendChild(xmlDoc.createTextNode(String.valueOf(calculations[i])));
                operation.appendChild(name);
                operation.appendChild(total);
                operations.appendChild(operation);
            }
            trend.appendChild(operations);

            root.appendChild(trend);
        }
        StringWriter sw = new StringWriter();
        try {
            XMLSerializer serializer = new XMLSerializer(sw, new OutputFormat(xmlDoc));
            serializer.serialize(xmlDoc);
        } catch (IOException ee) {
            ee.printStackTrace();
        } finally {
            sw.flush();
        }
        return sw.toString();
    }

    private SecuredTransitionBean nextState(SecuredMstatusBean msg, SecuredStatusBean prev) throws GranException {
        ArrayList<SecuredTransitionBean> tr = msg.getTransitions();

        for (SecuredTransitionBean t : tr) {
            if (prev == null)
                if (t.getStart().isStart()) {
                    return t;
                } else if (t.getStart().equals(prev)) return t;
        }
        if (tr.size() > 0) {
            return tr.get(0);
        }
        return null;
    }
}
