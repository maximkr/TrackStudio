package com.trackstudio.app.adapter.macros;

import java.util.HashMap;
import java.util.List;

import com.trackstudio.exception.GranException;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.startup.I18n;

import net.jcip.annotations.Immutable;

@Immutable
public class WorkloadChart extends SpentTimeChart {

    public WorkloadChart(SecuredTaskBean task, List<SecuredTaskBean> list,
                         String options) {
        super(task, list, options);
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

                if (t.getBudget()!=null && participants.containsKey(t.getHandlerUser())) {
                    Long spentTime=participants.get(t.getHandlerUser());
                    participants.put(t.getHandlerUser(), spentTime+t.getBudget());
                }

            }
            StringBuffer s = new StringBuffer();
            String id = String.valueOf(System.currentTimeMillis());
            s.append("<div id=\"workloadchart_div").append(id).append("\"></div>\n");
            s.append("<script type=\"text/javascript\">\n");
            s.append("google.load(\'visualization\', \'1.0\', {\'packages\':[\'corechart\']});\n");
            s.append("google.setOnLoadCallback(drawWorkloadChart").append(task.getId()).append(");\n");
            s.append("function drawWorkloadChart").append(task.getId()).append("() {\n");
            s.append("var data = new google.visualization.DataTable();\n");
            s.append("data.addColumn(\'string\', \'").append(I18n.getString(task.getSecure(), "PARTICIPANTS")).append("\');\n");
            s.append("data.addColumn(\'number\', \'").append(I18n.getString(task.getSecure(), "BUDGET")).append("\');\n");

            for (SecuredUserBean u :participants.keySet()){
                s.append("data.addRow([\'").append(u.getName()).append("\', ").append((float)participants.get(u)/3600f).append("]);\n");
            }
            s.append("var options = {").append(opt).append("};\n");
            s.append("		      var chart = new google.visualization.BarChart(document.getElementById(\'workloadchart_div").append(id).append("\'));\n");
            s.append(" data.sort({column: 0});");
            s.append("chart.draw(data, options);\n");
            s.append("}\n");
            s.append("</script>\n");
            return s.toString();
        } else return "";

    }


}
