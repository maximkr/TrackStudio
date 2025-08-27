package com.trackstudio.app.adapter.macros;

import java.util.HashMap;
import java.util.List;

import com.trackstudio.exception.GranException;
import com.trackstudio.secured.SecuredResolutionBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.startup.I18n;

import net.jcip.annotations.Immutable;

@Immutable
public class ResolutionChart extends StateChart {

    public ResolutionChart(SecuredTaskBean task,
                           List<SecuredTaskBean> list, String options) {
        super(task, list, options);

    }

    @Override
    public String calculate() throws GranException {
        String opt = "";
        if (options!=null)
            opt = options;

        List<SecuredTaskBean> children = list;

        if (children != null && !children.isEmpty()) {

            HashMap<SecuredResolutionBean, Integer> states = new HashMap<SecuredResolutionBean, Integer>();
            for (SecuredTaskBean t : children) {
                if (t.getResolutionId() != null) {
                    if (states.containsKey(t.getResolution())) {
                        Integer amo = states.get(t.getResolution());
                        states.put(t.getResolution(), amo + 1);
                    } else states.put(t.getResolution(), 1);
                }
            }
            StringBuffer s = new StringBuffer();
            String id = String.valueOf(System.currentTimeMillis());
            s.append("<div id=\"resolutionchart_div").append(id).append("\"></div>\n");
            s.append("<script type=\"text/javascript\">\n");
            s.append("google.load(\'visualization\', \'1.0\', {\'packages\':[\'corechart\']});\n");
            s.append("google.setOnLoadCallback(drawResolutionChart").append(task.getId()).append(");\n");
            s.append("function drawResolutionChart").append(task.getId()).append("() {\n");
            s.append("var data = new google.visualization.DataTable();\n");
            s.append("data.addColumn(\'string\', \'").append(I18n.getString(task.getSecure(), "RESOLUTION")).append("\');\n");
            s.append("data.addColumn(\'number\', \'").append(I18n.getString(task.getSecure(), "TASKS_AMOUNT")).append("\');\n");

            for (SecuredResolutionBean u : states.keySet()) {
                s.append("data.addRow([\'").append(u.getName()).append("\', ").append(states.get(u)).append("]);\n");
            }
            s.append("var options = {").append(opt).append("};\n");

            s.append("		      var chart = new google.visualization.PieChart(document.getElementById(\'resolutionchart_div").append(id).append("\'));\n");
            //s.append(" data.sort({column: 0});");
            s.append("chart.draw(data, options);\n");
            s.append("}\n");
            s.append("</script>\n");
            return s.toString();
        } else return "";

    }
}
