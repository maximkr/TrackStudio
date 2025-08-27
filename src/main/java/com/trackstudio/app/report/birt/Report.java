package com.trackstudio.app.report.birt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.trackstudio.app.UdfValue;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.filter.FValue;
import com.trackstudio.app.filter.TaskFValue;
import com.trackstudio.app.filter.list.MessageFilter;
import com.trackstudio.app.filter.list.TaskFilter;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.common.FieldMap;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.secured.SecuredMessageBean;
import com.trackstudio.secured.SecuredReportBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUDFBean;
import com.trackstudio.securedkernel.SecuredReportAdapterManager;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.Pair;
import com.trackstudio.tools.compare.FieldSort;
import com.trackstudio.tools.compare.SortTask;
import com.trackstudio.tools.formatter.DateFormatter;
import com.trackstudio.tools.formatter.HourFormatter;
import com.trackstudio.view.TaskView;
import com.trackstudio.view.TaskViewEmailHTML;
import com.trackstudio.view.TaskViewEmailText;

import net.jcip.annotations.NotThreadSafe;

import static com.trackstudio.securedkernel.SecuredReportAdapterManager.*;

/**
 * Класс содержит структуру и описание основы отчетов системы
 */
@NotThreadSafe
public abstract class Report {
    public Report() {
    }

    /**
     * ID задачи
     */
    protected String taskId;
    /**
     * Контекст
     */
    protected String contextPath;
    /**
     * Сессия пользователя
     */
    protected SessionContext sc;
    /**
     * Параметры фильтрации (постфильтрация)
     */
    protected TaskFValue fv = null;
    /**
     * Отчет
     */
    protected SecuredReportBean report = null;
    /**
     * Список задач
     */
    protected ArrayList<SecuredTaskBean> list = new ArrayList<SecuredTaskBean>();
    /**
     * Надо ли ипользовать сообщения
     */
    protected Boolean useMessages = false;
    /**
     * Формат вывода
     */
    protected String format;

    private HashMap<String, String> params;
    private TaskView taskView;
    private static final List<String> isHtml = Arrays.asList();

    /**
     * Конструктор
     *
     * @param contextPath контекст
     * @param sc          сессия пользователя
     * @param report      отчет
     * @param fv          параметры фильтрации
     * @param taskId      ID задачи
     * @param format      формат вывода
     * @throws GranException при необзодимости
     */
    public Report(String contextPath, SessionContext sc, SecuredReportBean report, TaskFValue fv, String taskId, String format) throws GranException {
        init(contextPath, sc, report, fv, taskId, format);
    }

    public void init(String contextPath, SessionContext sc, SecuredReportBean report, TaskFValue fv, String taskId, String format) throws GranException {
        this.contextPath = contextPath;
        this.taskId = taskId;
        this.sc = sc;
        this.report = report;
        this.fv = fv;
        TaskFilter taskList = new TaskFilter(new SecuredTaskBean(taskId, sc));
        if (fv != null) {
            boolean notwithsub = fv.get(FValue.SUBTASK) == null;
            this.list = taskList.getTaskList(fv, true, notwithsub, fv.getSortOrder());
            this.useMessages = fv.hasListValue(FValue.DISPLAY, FieldMap.MESSAGEVIEW.getFilterKey());
        }
        this.format = format;
        this.taskView = getTaskView(new SecuredTaskBean(taskId, sc));
        buildParams();
    }

    private void buildParams() throws GranException {
        params = new HashMap<String, String>();
        if (report != null) {
            params.put(TOP_TITLE, report.getName());
            params.put(FILTER_VAL, report.getFilter().getName());
            params.put(TYPE_VAL, report.getRtypeText());
        }
        params.put(TASK, I18n.getString(sc.getLocale(), "TASK"));
        params.put(TASK_VAL, taskView.getName());
        params.put(FILTER, I18n.getString(sc.getLocale(), "FILTER"));
        params.put(TYPE, I18n.getString(sc.getLocale(), "TYPE"));
        if (fv != null) {
            params.put(USE_DESCR, String.valueOf(fv.getView().contains(FieldMap.TASK_DESCRIPTION.getFilterKey())));
        }
        params.put(TASK_COUNT, I18n.getString(sc.getLocale(), "REPORT_TASK_COUNT", new Object[]{list != null ? list.size() : 0}));
    }

    public TaskView getTaskView(SecuredTaskBean task) throws GranException {
        if (isHtml.contains(format)) {
            return new TaskViewEmailHTML(task);
        } else {
            return new TaskViewEmailText(task);
        }
    }

    public final static String TOP_TITLE = "topTitle";
    public final static String TASK = "task";
    public final static String TASK_VAL = "task_val";
    public final static String FILTER = "filter";
    public final static String FILTER_VAL = "filter_val";
    public final static String TYPE = "type";
    public final static String TYPE_VAL = "type_val";
    public final static String USE_DESCR = "useDescr";
    public final static String TASK_COUNT = "taskCount";

    public static final String CATEGORY = "category";
    public static final String HANDLER = "handler";
    public static final String HANDLER_STATUS = "handler_stat";
    public static final String PRIORITY = "priority";
    public static final String RESOLUTION = "resolution";
    public static final String STATUS = "status";
    public static final String SUBMITTER = "submitter";
    public static final String SUBMITTER_STATUS = "submitter_stat";

    public String name() throws GranException {
        return I18n.getString(sc, "REPORT");
    }

    public String type() throws GranException {
        return I18n.getString(sc, "TYPE");
    }


    public String task() throws GranException {
        return I18n.getString(sc, "NAME");
    }

    public String filter() throws GranException {
        return I18n.getString(sc, "FILTER");
    }

    /**
     * This method returns name of report
     * @param sc session context
     * @param type type
     * @return name of report
     * @throws GranException for necessary
     */
    public static String getReportTypeFactory(SessionContext sc, String type) throws GranException {
        if ("List".equals(type))
            return I18n.getString(sc, "REPORT_LIST");
        else if ("Tree".equals(type))
            return I18n.getString(sc, "REPORT_TREE");
        return "";
    }

}
