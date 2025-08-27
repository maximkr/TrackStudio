package com.trackstudio.app.adapter.macros;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.adapter.MacrosTaskAdapter;
import com.trackstudio.app.filter.TaskFValue;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.common.FieldMap;
import com.trackstudio.exception.GranException;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.tools.textfilter.MacrosUtil;
import com.trackstudio.view.TaskView;
import com.trackstudio.view.TaskViewEmailText;

import net.jcip.annotations.Immutable;

import static com.trackstudio.tools.textfilter.MacrosUtil.getListTask;


@Immutable
public class SubtaskFilter extends AbstractOptionPatternMacro implements MacrosTaskAdapter {

    private static final List<String> dateFields = Arrays.asList(FieldMap.TASK_SUBMITDATE.getFilterKey(), FieldMap.TASK_UPDATEDATE.getFilterKey(),
            FieldMap.TASK_CLOSEDATE.getFilterKey());

    @Override
    public void convertSingle(TaskView view, StringBuffer current,
                              Matcher matcher) throws GranException {
        SessionContext sc = view.getTask().getSecure();
        SecuredTaskBean task = AdapterManager.getInstance().getSecuredTaskAdapterManager().findTaskByNumber(sc, taskNumber);
        if (task != null) {

            String filterId = MacrosUtil.getFilterId(filterName, task.getId(), sc.getUserId(), true);
            if (filterId != null) {
                TaskFValue taskFValue = AdapterManager.getInstance().getSecuredFilterAdapterManager().getTaskFValue(sc, filterId).getFValue();
                TaskFValue taskFValueForShow = AdapterManager.getInstance().getSecuredFilterAdapterManager().getTaskFValue(sc, filterId).getFValue();
                if (taskFValueForShow.get("display").contains(FieldMap.TASK_DESCRIPTION.getFilterKey())) {
                    taskFValueForShow.get("display").remove(FieldMap.TASK_DESCRIPTION.getFilterKey());
                }
                if (options!=null && !options.isEmpty())
                    taskFValue = filterByOptions(taskFValue, options);
                Map<String, String> headerMap = MacrosUtil.getHeadersMap(task, taskFValueForShow, sc.getLocale());
                List<SecuredTaskBean> listTasks = getListTask(task, taskFValue, filterId);

                String numValue = null;
                if (filterName.contains("|num:")) {
                    numValue = filterName.substring(filterName.indexOf("|num:") + "|num:".length(), filterName.length());
                } else if (options!=null && options.length()>0) {
                    String[] opt = options.split(",");
                    for (String s: opt){

                        int separator = s.indexOf(":");
                        if (separator>-1){
                            String key = s.substring(0, separator);
                            if (key.contains("limit")){
                                numValue = s.substring(separator);
                            }
                        }
                    }
                }
                if (numValue!=null){
                    int num = 0;
                    Pattern digits = Pattern.compile("(\\d+)");
                    Matcher m = digits.matcher(numValue);
                    if (m.find())
                        num = Integer.parseInt(m.group(1));

                    if (num > 0 && listTasks.size() > num) {
                        listTasks = listTasks.subList(0, num);
                    }
                }
                HashMap<String, HashMap<String, String>> tasksMap = MacrosUtil.getTasksMap(headerMap, listTasks, view);
                String table = "";
                if (view instanceof TaskViewEmailText)
                    table = buildPlainText(listTasks, headerMap, tasksMap);
                else		table = buildTable(sc, listTasks, headerMap, tasksMap);
                table = table.replaceAll("\\$","\\\\\\$");
                matcher.appendReplacement(current, table);
            }
        }



    }

    private TaskFValue filterByOptions(TaskFValue filter, String options){
        Matcher matcher = optionPattern.matcher(options);
        while (matcher.find()) {
            if (matcher.group(1)!=null && matcher.group(2)!=null){
                String key = matcher.group(1).trim();
                String value = matcher.group(2).trim();
                if (value.contains(";")) {
                    for (String item : value.split(";")) {
                        filter.putItem(key, item);
                    }
                } else {
                    filter.putItem(key, value);
                }
            }
        }
        return filter;
    }

    public static String buildTable(SessionContext sc, List<SecuredTaskBean> tasks, Map<String, String> headerMap, HashMap<String, HashMap<String, String>> tasksMap) throws GranException {
        StringBuffer sb = new StringBuffer();
        sb.append("<table class=\"general sortable\">").append("<tr class=\"wide\">");

        for (String header : headerMap.keySet()) {
            if (!FieldMap.FULLPATH.getFilterKey().equals(header)) {
                sb.append("<th").append(sc.getLocale().contains("ru") && dateFields.contains(header) ? " class=date-ru " : "").append(">").append(headerMap.get(header)).append("</th>");
            }
        }
        sb.append("</tr>");
        for (SecuredTaskBean task : tasks) {
            sb.append("<tr>");

            HashMap<String, String> valueMap = tasksMap.get(task.getId());
            for (String header : headerMap.keySet()) {
                if (!FieldMap.FULLPATH.getFilterKey().equals(header)) {
                    String data = valueMap.get(header);
                    data = data != null && !data.isEmpty() ? data : " - ";
                    sb.append("<td>").append(data).append("</td>");
                }
            }
            sb.append("</tr>");
        }
        sb.append("</table>");
        return sb.toString();
    }

    private String buildPlainText(List<SecuredTaskBean> tasks, Map<String, String> headerMap, HashMap<String, HashMap<String, String>> tasksMap) {
        StringBuffer sb = new StringBuffer();
        /*
        int columns = headerMap.size();
        int size = 160/columns;
        sb.append("<@table columns=");
        sb.append(columns);
        sb.append("positions=");
        for (int i = 0; i<size; i++)
        sb.append(i*size).append(",");
        sb.append(160);
        sb.append(">\n");
      
        for (Iterator<String> it = headerMap.keySet().iterator(); it.hasNext();) {
        	String header = it.next();
            sb.append(headerMap.get(header));
            if (it.hasNext()) sb.append("::");
        }
        sb.append("\n");
        
        for (SecuredTaskBean task : tasks) {
            HashMap<String, String> valueMap = tasksMap.get(task.getId());
            for (Iterator<String> it = headerMap.keySet().iterator(); it.hasNext();) {
            	String header = it.next();
                String data = valueMap.get(header);
                data = data != null && !data.isEmpty() ? data : " - ";
                sb.append(data);
                if (it.hasNext()) sb.append("::");
            }
            sb.append("\n");
        }
        sb.append("</@table>\n");
        */
        return sb.toString();
    }

    @Override
    public boolean init() {
        return true;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public boolean match(String t) {

        return "filter".equals(t);
    }


}
