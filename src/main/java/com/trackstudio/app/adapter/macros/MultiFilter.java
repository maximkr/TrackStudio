package com.trackstudio.app.adapter.macros;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.adapter.MacrosTaskAdapter;
import com.trackstudio.app.filter.TaskFValue;
import com.trackstudio.exception.GranException;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.tools.textfilter.MacrosUtil;
import com.trackstudio.view.TaskView;

import net.jcip.annotations.Immutable;

import static com.trackstudio.app.adapter.macros.SubtaskFilter.buildTable;
import static com.trackstudio.tools.textfilter.MacrosUtil.getFilterId;
import static com.trackstudio.tools.textfilter.MacrosUtil.getListTask;

@Immutable
public class MultiFilter implements MacrosTaskAdapter {
    public static final Pattern multi = Pattern.compile("(#)(\\d+)(\\{)(multi)(\\[)(.+?)(\\])(\\})");

    @Override
    public String convert(TaskView view, String desc) throws GranException {
        String userId = view.getTask().getSecure().getUserId();
        Matcher matcher = multi.matcher(desc);
        StringBuffer current = new StringBuffer();
        while (matcher.find()) {
            SecuredTaskBean task = AdapterManager.getInstance().getSecuredTaskAdapterManager().findTaskByNumber(view.getTask().getSecure(), matcher.group(2));
            String expression = matcher.group(6);
            List<SecuredTaskBean> listTasks = new ArrayList<SecuredTaskBean>();
            Map<String, String> headerMap = null;
            HashMap<String, HashMap<String, String>> tasksMap = null;
            for (String filterName : expression.split(";")) {
                String filterId = getFilterId(filterName, task.getId(), userId, true);
                TaskFValue taskFValue = AdapterManager.getInstance().getSecuredFilterAdapterManager().getTaskFValue(view.getTask().getSecure(), filterId).getFValue();
                listTasks.addAll(getListTask(task, taskFValue, filterId));
                if (headerMap == null) {
                    headerMap = MacrosUtil.getHeadersMap(task, taskFValue, task.getSecure().getLocale());
                    tasksMap = MacrosUtil.getTasksMap(headerMap, listTasks, view);
                }
            }
            matcher.appendReplacement(current, Matcher.quoteReplacement(buildTable(view.getTask().getSecure(), listTasks, headerMap, tasksMap)));
            current.append("</br>");
        }
        matcher.appendTail(current);
        String result = current.toString();
        return result.isEmpty() ? desc : result;
    }

    @Override
    public boolean init() {
        return true;
    }

    @Override
    public String getDescription() {
        return "Macros uses multi filters";
    }
}
