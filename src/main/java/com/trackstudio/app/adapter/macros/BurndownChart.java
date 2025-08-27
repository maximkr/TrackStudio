package com.trackstudio.app.adapter.macros;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import com.trackstudio.app.filter.customizer.BudgetCustomizer;
import com.trackstudio.exception.GranException;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.startup.I18n;

import net.jcip.annotations.Immutable;

@Immutable
public class BurndownChart extends Chart {

    public BurndownChart(SecuredTaskBean task, List<SecuredTaskBean> list,
                         String options) {
        super(task, list, options);

    }

    @Override
    public String calculate()
            throws GranException {
        String opt = "";
        if (options != null)
            opt = options;
        Calendar endAt = task.getDeadline();
        Long budget = task.getBudget();
        List<SecuredTaskBean> children = list;

        if (budget != null && endAt != null && children != null && !children.isEmpty()) {
            Calendar startAt = task.getSubmitdate();


            Periods periods = new DayPeriods();
            int totalPeriods = periods.since(endAt, startAt);
            Long[] real = new Long[totalPeriods + 1];
            Long[] ideal = new Long[totalPeriods + 1];
            Arrays.fill(real, budget);
            Arrays.fill(ideal, budget);
            Calendar iterate = (Calendar) startAt.clone();
            int totalDays = 0;
            for (int i = 1; i <= totalPeriods; i++) {

                if (iterate.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY && iterate.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
                    totalDays++;
                }
                iterate.add(Calendar.HOUR, 24);
            }
            Long workPerDay = budget / totalDays;
            iterate = (Calendar) startAt.clone();
            Long left = budget;
            for (int i = 1; i <= totalPeriods; i++) {

                if (iterate.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY && iterate.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
                    left -= workPerDay;
                }
                ideal[i] = left;
                iterate.add(Calendar.HOUR, 24);
            }

            for (SecuredTaskBean t : children) {
                if (t.getClosedate() != null) {
                    int period = periods.since(t.getClosedate(), startAt);
                    Long b = t.getBudget();
                    if (b == null || b == 0)
                        b = t.getAbudget();
                    if (b != null) {
                        for (int i = period; i <= totalPeriods; i++) {
                            real[i] -= b;
                        }
                    }
                }
            }

            StringBuffer s = new StringBuffer();
            String id = String.valueOf(System.currentTimeMillis());
            s.append("<div id=\"burndownchart_div").append(id).append("\"></div>\n");
            s.append("<script type=\"text/javascript\">\n");
            s.append("google.load(\'visualization\', \'1.0\', {\'packages\':[\'corechart\']});\n");
            s.append("google.setOnLoadCallback(drawBurndownChart").append(task.getId()).append(");\n");
            s.append("function drawBurndownChart").append(task.getId()).append("() {\n");
            s.append("var data = new google.visualization.DataTable();\n");
            s.append("data.addColumn(\'date\', \'").append(I18n.getString(task.getSecure(), "PERIOD")).append("\');\n");
            s.append("data.addColumn(\'number\', \'").append(I18n.getString(task.getSecure(), "IDEAL")).append("\');\n");
            s.append("data.addColumn(\'number\', \'").append(I18n.getString(task.getSecure(), "REAL")).append("\');\n");

            Calendar flow = periods.reset(startAt);
            for (int i = 0; i <= totalPeriods; i++) {
                flow.add(periods.getShift(), 1);
                Calendar out = (Calendar) flow.clone();
                out.add(Calendar.MINUTE, -1);
                s.append("data.addRow([").append("new Date(").append(out.get(Calendar.YEAR)).append(",").append(out.get(Calendar.MONTH)).append(",")
                        .append(out.get(Calendar.DAY_OF_MONTH)).append(")");

                s.append(", ").append(Math.round(ideal[i] / (BudgetCustomizer.HOURS_IN_DAY * 3600d)));
                s.append(", ").append(Math.round(real[i] / (BudgetCustomizer.HOURS_IN_DAY * 3600d)));
                s.append("]);\n");
            }


            s.append("var options = {").append(opt).append("};\n");


            s.append("		      var chart = new google.visualization.LineChart(document.getElementById(\'burndownchart_div").append(id).append("\'));\n");
            s.append(" data.sort({column: 0});");
            s.append(" var formatter = new google.visualization.DateFormat({pattern: \"").append(periods.getPattern()).append("\"});\n");
            s.append(" var dataView = new google.visualization.DataView(data);\n");
            s.append(" dataView.setColumns([{calc: function(data, row) { return formatter.formatValue(data.getValue(row, 0)); }, type:'string'}, 1]);\n");
            s.append("chart.draw(dataView, options);\n");
            s.append("}\n");
            s.append("</script>\n");
            return s.toString();
        } else return "";

    }


}
