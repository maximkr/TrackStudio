package com.trackstudio.app.adapter.macros;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.secured.SecuredMessageBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.startup.I18n;

import net.jcip.annotations.Immutable;

@Immutable
public class SpentTimeChart extends Chart {

	public SpentTimeChart(SecuredTaskBean task,
			List<SecuredTaskBean> list, String options) {
		super(task, list, options);
		// TODO Auto-generated constructor stub
	}
	protected HashMap<SecuredUserBean, Long> getParticipants(SecuredTaskBean securedTaskBean) throws GranException {
        SessionContext sc = securedTaskBean.getSecure();
        ArrayList<SecuredUserBean> userList = AdapterManager.getInstance().getSecuredAclAdapterManager().getUserList(sc, securedTaskBean.getId());
        Collections.sort(userList);
        HashMap<SecuredUserBean, Long> ret = new HashMap<SecuredUserBean, Long>();
        for (SecuredUserBean u : userList) 
            ret.put(u, 0L);
        return ret;
    }

	@Override
	public String calculate() throws GranException {
		        String opt = ";";
		        if (options!=null)
                    opt = options;
		        
				List<SecuredTaskBean> children = list;
				
				if (children!=null && !children.isEmpty()){
					
	                HashMap<SecuredUserBean, Long> participants = getParticipants(task);
	                for (SecuredTaskBean t : children) {
	                    for (SecuredMessageBean msg : t.getMessages()) {
	                        if (msg.getHrs()!=null) {
	                            if (participants.containsKey(msg.getSubmitter())){
	                                Long spentTime=participants.get(msg.getSubmitter());
	                                participants.put(msg.getSubmitter(), spentTime+msg.getHrs());
	                            }
	                        }
	                    }
	                }	
				
				
		        
		        
		        StringBuffer s = new StringBuffer();
                String id = String.valueOf(System.currentTimeMillis());
		        s.append("<div id=\"spenttimechart_div").append(id).append("\"></div>\n");
		        s.append("<script type=\"text/javascript\">\n");
		        s.append("google.load(\'visualization\', \'1.0\', {\'packages\':[\'corechart\']});\n");
		        s.append("google.setOnLoadCallback(drawSpenttimeChart").append(task.getId()).append(");\n");
		        s.append("function drawSpenttimeChart").append(task.getId()).append("() {\n");
		        s.append("var data = new google.visualization.DataTable();\n");
		        s.append("data.addColumn(\'string\', \'").append(I18n.getString(task.getSecure(), "PARTICIPANTS")).append("\');\n");
		        s.append("data.addColumn(\'number\', \'").append(I18n.getString(task.getSecure(), "ABUDGET")).append("\');\n");

		        for (SecuredUserBean u :participants.keySet()){
                	s.append("data.addRow([\'").append(u.getName()).append("\', ").append((float)participants.get(u)/3600f).append("]);\n");
                }
		        s.append("var options = {").append(opt).append("};\n");
		 		s.append("		      var chart = new google.visualization.BarChart(document.getElementById(\'spenttimechart_div").append(id).append("\'));\n");
		 		s.append(" data.sort({column: 0});");
		 		s.append("chart.draw(data, options);\n");
		 		s.append("}\n");
		 		s.append("</script>\n");
		        return s.toString();
				} else return "";
				
			}    
	

}
