package com.trackstudio.app.adapter.macros;

import java.util.HashMap;
import java.util.List;

import com.trackstudio.exception.GranException;
import com.trackstudio.secured.SecuredStatusBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.startup.I18n;

import net.jcip.annotations.Immutable;

@Immutable
public class StateChart extends Chart {

    public StateChart(SecuredTaskBean task, List<SecuredTaskBean> list,
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

            HashMap<SecuredStatusBean, Integer> states = new HashMap<SecuredStatusBean, Integer>();
            for (SecuredTaskBean t : children) {
                if (states.containsKey(t.getStatus())){
                    Integer amo=states.get(t.getStatus());
                    states.put(t.getStatus(), amo+1);
                }
                else states.put(t.getStatus(), 1);
            }
            StringBuffer s = new StringBuffer();
            String id = String.valueOf(System.currentTimeMillis());
            s.append("<div id=\"statechart_div").append(id).append("\"></div>\n");
            s.append("<script type=\"text/javascript\">\n");
            s.append("google.load(\'visualization\', \'1.0\', {\'packages\':[\'corechart\']});\n");
            s.append("google.setOnLoadCallback(drawStateChart").append(task.getId()).append(");\n");
            s.append("function drawStateChart").append(task.getId()).append("() {\n");
            s.append("var data = new google.visualization.DataTable();\n");
            s.append("data.addColumn(\'string\', \'").append(I18n.getString(task.getSecure(), "STATE")).append("\');\n");
            s.append("data.addColumn(\'number\', \'").append(I18n.getString(task.getSecure(), "TASKS_AMOUNT")).append("\');\n");

            for (SecuredStatusBean u :states.keySet()){
                s.append("data.addRow([\'").append(u.getName()).append("\', ").append(states.get(u)).append("]);\n");
            }
            s.append("var options = {");
            s.append("\'colors\': [");
            for (SecuredStatusBean state: states.keySet())
                s.append("\'").append(state.getColor()).append("\', ");
            s.append("\'black\']\n");
            if (opt!=null && opt.length()>0) s.append(", ").append(opt);
            s.append("};\n");

            s.append("		      var chart = new google.visualization.PieChart(document.getElementById(\'statechart_div").append(id).append("\'));\n");
            //s.append(" data.sort({column: 0});");
            s.append("chart.draw(data, options);\n");
            s.append("}\n");
            s.append("</script>\n");
            return s.toString();
        } else return "";

    }


}
