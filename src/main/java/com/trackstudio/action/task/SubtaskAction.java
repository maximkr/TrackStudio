package com.trackstudio.action.task;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeSet;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.trackstudio.kernel.cache.*;
import com.trackstudio.secured.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.trackstudio.action.GeneralAction;
import com.trackstudio.action.TSDispatchAction;
import com.trackstudio.app.CalculatedValue;
import com.trackstudio.app.PageNaming;
import com.trackstudio.app.Preferences;
import com.trackstudio.app.Slider;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.filter.FValue;
import com.trackstudio.app.filter.FilterSettings;
import com.trackstudio.app.filter.TaskFValue;
import com.trackstudio.app.filter.comparator.TaskComparator;
import com.trackstudio.app.filter.list.MessageFilter;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.common.FieldMap;
import com.trackstudio.constants.CommonConstants;
import com.trackstudio.constants.UdfConstants;
import com.trackstudio.exception.GranException;
import com.trackstudio.exception.TriggerException;
import com.trackstudio.exception.UserException;
import com.trackstudio.form.TaskListForm;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.startup.Config;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.EggBasket;
import com.trackstudio.tools.Null;
import com.trackstudio.tools.compare.FieldSort;
import com.trackstudio.tools.compare.SortUdf;
import com.trackstudio.tools.formatter.SortedFactory;
import com.trackstudio.tools.formatter.SortedLink;
import com.trackstudio.tools.textfilter.HTMLEncoder;
import com.trackstudio.tools.textfilter.MacrosUtil;
import com.trackstudio.tools.tree.NodeMask;

import bsh.EvalError;
import bsh.TargetError;

public class SubtaskAction extends TSDispatchAction {
    private static Log log = LogFactory.getLog(SubtaskAction.class);

    private final static String[] EMPTY_ARRAY = new String[] {};
    /**
     * Method for get filter id.
     *
     * @param id        task
     * @param sc        Sessin context
     * @param filterSet sets filter
     * @param request   HttpServletRequest
     * @return current filter id
     * @throws Exception for need
     */
    private String getFilterId(String id, SessionContext sc, ArrayList<SecuredFilterBean> filterSet, HttpServletRequest request) throws GranException {
        String filterId;
        if (sc.getAttribute("permlinkfilter") != null) {
            String tempFilterId = sc.getAttribute("permlinkfilter").toString();
            boolean filterInList = false;
            for (SecuredFilterBean filt : filterSet)
                if (filt.getId().equals(tempFilterId))
                    filterInList = true;

            if (filterInList) {
                filterId = sc.getAttribute("permlinkfilter").toString();
                sc.removeAttribute("permlinkfilter");
                AdapterManager.getInstance().getSecuredFilterAdapterManager().setCurrentFilter(sc, id, filterId);
            } else {
                AdapterManager.getInstance().getSecuredFilterAdapterManager().setCurrentFilter(sc, id, "1");
                filterId = "1";
            }
        } else {
            filterId = request.getParameter("filterId") != null ? request.getParameter("filterId") : null;
            if (filterId != null) {
                return filterId;
            } else {
                filterId = AdapterManager.getInstance().getSecuredFilterAdapterManager().getCurrentTaskFilterId(sc, id);
            }
        }
        return filterId;
    }

    private InitParam buildSlider(SessionContext sc, SecuredTaskBean tci, HttpServletRequest request, TaskListForm bf) throws GranException {
        String taskId = tci.getId();
        ArrayList<SecuredFilterBean> filterSet = AdapterManager.getInstance().getSecuredFilterAdapterManager().getTaskFilterList(sc, taskId);

        sc.setRequestAttribute(request, "filters", filterSet);
        sc.setRequestAttribute(request, "viewMessageCheckbox", false);

        String filterId = getFilterId(taskId, sc, filterSet, request);

        FilterSettings filterSettings = null;
        boolean filterEqual = false;
        Object filterObject = sc.getAttribute("taskfilter");
        if (filterObject != null) {
            filterSettings = (FilterSettings) filterObject;
            if (!filterSettings.getFilterId().equals(filterId) || !filterSettings.getObjectId().equals(taskId)) {
                filterEqual = true;
                filterSettings = null;

            }
        }
        sc.removeAttribute("statictask");
        sc.removeAttribute("next");
        sc.removeAttribute("statictasklist");
        TaskFValue val = filterSettings != null ? (TaskFValue) filterSettings.getSettings() : AdapterManager.getInstance().getSecuredFilterAdapterManager().getTaskFValue(sc, filterId).getFValue();
        FilterSettings originFilterSettings;
        originFilterSettings = new FilterSettings(val, taskId, filterId);
        if (filterSettings == null) {
            filterSettings = originFilterSettings;
            sc.setRequestAttribute(request, "taskfilter", originFilterSettings);
        }

        if (filterSettings.compareTo(originFilterSettings) != 0)
            sc.setRequestAttribute(request, "canViewRSS", false);
        else
            sc.setRequestAttribute(request, "canViewRSS", true);


        String rSSLink = MacrosUtil.buildRealURL(request) + "/task/" + tci.getNumber() + "/rss/" + filterId;
        sc.setRequestAttribute(request, "RSSLink", rSSLink);

        SecuredFilterBean filter = AdapterManager.getInstance().getSecuredFindAdapterManager().findFilterById(sc, filterId);
        sc.setRequestAttribute(request, "filter", filter);
        HTMLEncoder ec = new HTMLEncoder(filter.getName());
        ec.replace("'", "\\'");
        ec.replace("\"", "\\\"");
        ec.replace("\r\n", " ");
        ec.replace("\n", " ");
        String title = request.getAttribute("title") != null ? (String) request.getAttribute("title") : "";
        sc.setRequestAttribute(request, "title", title + " - " + ec.toString());

        log.debug("Filter id is " + filterId);

        boolean taskUDFView = filterSettings.getSettings().needFilterUDF();
        if (bf.getSliderPage() != null && bf.getSliderPage().length() != 0) {
            filterSettings.setCurrentPage(Integer.parseInt(bf.getSliderPage()));
        } else {
            filterSettings.setCurrentPage(1);
        }
        if (bf.isAll()) {
            filterSettings.setAll(true);
        }

        if (bf.getSliderOrder() != null && bf.getSliderOrder().length() != 0) {
            ArrayList<String> sliderOrderList = new ArrayList<String>();
            sliderOrderList.add(bf.getSliderOrder());
            filterSettings.setSortedBy(sliderOrderList);
            originFilterSettings.setSortedBy(sliderOrderList);
        }

        Slider<SecuredTaskBean> taskSlider = AdapterManager.getInstance().getSecuredTaskAdapterManager().getTaskList(sc, taskId, (TaskFValue) filterSettings.getSettings(), taskUDFView, filterSettings.getCurrentPage(), filterSettings.getSortedBy());
        taskSlider.setAll(filterSettings.isAll(), sc.getLocale());
        InitParam initParam = new InitParam();
        initParam.slider = taskSlider;
        initParam.filter = filter;
        initParam.filterId = filterId;
        initParam.filterSettings = filterSettings;
        initParam.originFilterSettings = originFilterSettings;
        initParam.filterEqual = filterEqual;
        initParam.taskUDFView = taskUDFView;
        return initParam;
    }

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");

            TaskListForm bf = (TaskListForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            sc.setCurrentSpace("SubtasksAction", request);
            String taskId = GeneralAction.getInstance().taskHeader(null, sc, request, true);

            SecuredTaskBean tci = new SecuredTaskBean(taskId, sc);
            String contextPath = request.getContextPath();

            boolean canPaste = sc.getAttribute("TASKS") != null && sc.getAttribute("TASKS").toString().length() > 0 && sc.allowedByACL(taskId) && sc.canAction(Action.cutCopyPasteTask, taskId);
            sc.setRequestAttribute(request, "canPaste", canPaste);
            InitParam initParam = buildSlider(sc, tci, request, bf);
            Slider<SecuredTaskBean> taskSlider = initParam.slider;
            String filterId = initParam.filterId;
            boolean taskUDFView = initParam.taskUDFView;
            FilterSettings filterSettings = initParam.filterSettings;
            FilterSettings originFilterSettings = initParam.originFilterSettings;
            SecuredFilterBean filter = initParam.filter;
            boolean canDelete = false;
            for (SecuredTaskBean task : taskSlider) {
                if (task.canDelete()) {
                    canDelete = true;
                    break;
                }
            }
            sc.setRequestAttribute(request, "canDelete", canDelete);
            sc.setRequestAttribute(request, "headerSlider", taskSlider.drawSlider(contextPath + "/SubtaskAction.do?method=page&amp;id=" + taskId + "&amp;filterId=" + filterId, "span", "floatlink"));
            sc.setRequestAttribute(request, "slider", taskSlider.drawSlider(contextPath + "/SubtaskAction.do?method=page&amp;id=" + taskId + "&amp;filterId=" + filterId, "div", "slider"));
            sc.setRequestAttribute(request, "sliderSize", taskSlider.getColSize() != null ? taskSlider.getColSize() : I18n.getString(sc.getLocale(), "HUNDREDS"));

            boolean useCookies = true;

            if (!useCookies) sc.setRequestAttribute(request, "canViewPermLink", Boolean.FALSE);

            sc.setRequestAttribute(request, "useCookies", useCookies);
            HashMap<String, SortedLink> udfHeaderLink = new HashMap<String, SortedLink>();
            HashMap<String, String> udfHeaderCaption = new HashMap<String, String>();

            highlightOperation(sc, request);

            ArrayList<SecuredUDFBean> udfList = null;
            if (taskUDFView) {
                udfList = tci.getFilterUDFs();
                Collections.sort(udfList, new SortUdf(FieldSort.ORDER));
            }

            ArrayList<String> defaultSortString = new ArrayList<String>();
            defaultSortString.add("task_updatedate");
            List<String> sortstring;
            if (filterSettings.getSortedBy() != null && filterSettings.getSortedBy().size() != 0)
                sortstring = filterSettings.getSortedBy();
            else if (filterSettings.getSettings().getSortOrder() != null && filterSettings.getSettings().getSortOrder().size() != 0)
                sortstring = filterSettings.getSettings().getSortOrder();
            else
                sortstring = defaultSortString;

            SortedFactory fact = new SortedFactory(originFilterSettings, sortstring);
            SortedLink fullPathLnk = fact.getLink(FieldMap.FULLPATH.getFieldKey(), FieldMap.FULLPATH.getFilterKey(), 12);
            SortedLink taskNameLnk = fact.getLink(FieldMap.TASK_NAME.getFieldKey(), FieldMap.TASK_NAME.getFilterKey(), 12);

            sc.setRequestAttribute(request, "headerNumber", fact.getLink(FieldMap.TASK_NUMBER.getFieldKey(), FieldMap.TASK_NUMBER.getFilterKey(), 1));
            sc.setRequestAttribute(request, "headerAlias", fact.getLink(FieldMap.TASK_SHORTNAME.getFieldKey(), FieldMap.TASK_SHORTNAME.getFilterKey(), 2));
            sc.setRequestAttribute(request, "headerCategory", fact.getLink(FieldMap.TASK_CATEGORY.getFieldKey(), FieldMap.TASK_CATEGORY.getFilterKey(), 3));
            sc.setRequestAttribute(request, "headerStatus", fact.getLink(FieldMap.TASK_STATUS.getFieldKey(), FieldMap.TASK_STATUS.getFilterKey(), 3));
            sc.setRequestAttribute(request, "headerResolution", fact.getLink(FieldMap.TASK_RESOLUTION.getFieldKey(), FieldMap.TASK_RESOLUTION.getFilterKey(), 3));
            sc.setRequestAttribute(request, "headerPriority", fact.getLink(FieldMap.TASK_PRIORITY.getFieldKey(), FieldMap.TASK_PRIORITY.getFilterKey(), 3));
            sc.setRequestAttribute(request, "headerSubmitter", fact.getLink(FieldMap.SUSER_NAME.getFieldKey(), FieldMap.SUSER_NAME.getFilterKey(), 4));
            sc.setRequestAttribute(request, "headerSubmitterStatus", fact.getLink(FieldMap.SUSER_STATUS.getFieldKey(), FieldMap.SUSER_STATUS.getFilterKey(), 3));
            sc.setRequestAttribute(request, "headerHandler", fact.getLink(FieldMap.HUSER_NAME.getFieldKey(), FieldMap.HUSER_NAME.getFilterKey(), 4));
            sc.setRequestAttribute(request, "headerHandlerStatus", fact.getLink(FieldMap.HUSER_STATUS.getFieldKey(), FieldMap.HUSER_STATUS.getFilterKey(), 3));
            sc.setRequestAttribute(request, "headerSubmitDate", fact.getLink(FieldMap.TASK_SUBMITDATE.getFieldKey(), FieldMap.TASK_SUBMITDATE.getFilterKey(), 3));
            sc.setRequestAttribute(request, "headerUpdateDate", fact.getLink(FieldMap.TASK_UPDATEDATE.getFieldKey(), FieldMap.TASK_UPDATEDATE.getFilterKey(), 3));
            sc.setRequestAttribute(request, "headerCloseDate", fact.getLink(FieldMap.TASK_CLOSEDATE.getFieldKey(), FieldMap.TASK_CLOSEDATE.getFilterKey(), 3));
            sc.setRequestAttribute(request, "headerDeadline", fact.getLink(FieldMap.TASK_DEADLINE.getFieldKey(), FieldMap.TASK_DEADLINE.getFilterKey(), 3));
            sc.setRequestAttribute(request, "headerBudget", fact.getLink(FieldMap.TASK_BUDGET.getFieldKey(), FieldMap.TASK_BUDGET.getFilterKey(), 2));
            sc.setRequestAttribute(request, "headerActualBudget", fact.getLink(FieldMap.TASK_ABUDGET.getFieldKey(), FieldMap.TASK_ABUDGET.getFilterKey(), 2));
            sc.setRequestAttribute(request, "headerChildrenCount", fact.getLink(FieldMap.TASK_CHILDCOUNT.getFieldKey(), FieldMap.TASK_CHILDCOUNT.getFilterKey(), 1));
            sc.setRequestAttribute(request, "headerMessageCount", fact.getLink(FieldMap.TASK_MESSAGECOUNT.getFieldKey(), FieldMap.TASK_MESSAGECOUNT.getFilterKey(), 1));
            sc.setRequestAttribute(request, "headerTaskParent", fact.getLink(FieldMap.TASK_PARENT.getFieldKey(), FieldMap.TASK_PARENT.getFilterKey(), 2));
            sc.setRequestAttribute(request, "canViewDescription", filterSettings.getSettings().getView().contains(FieldMap.TASK_DESCRIPTION.getFilterKey()));
            sc.setRequestAttribute(request, "canDeleteMessages", tci.canDelete());
            sc.setRequestAttribute(request, "canViewMessages", filterSettings.getSettings().getView().contains(FieldMap.MESSAGEVIEW.getFilterKey()));
            sc.setRequestAttribute(request, "canViewPermLink", !filter.isPrivate() && ((sc.getAttribute("taskfilter") == null || originFilterSettings.getSettings().equals(((FilterSettings) sc.getAttribute("taskfilter")).getSettings())) || initParam.filterEqual));

            List<String> udfs = new ArrayList<String>();

            if (taskUDFView) {
                FValue settings = originFilterSettings.getSettings();
                List<String> view = settings.getView();
                for (String udfKey : view) {
                    if (udfKey.contains(FValue.UDF)) {
                        SecuredUDFBean udf = AdapterManager.getInstance().getSecuredFindAdapterManager().findUDFById(sc, udfKey.substring(FValue.UDF.length()));
                        if (udf != null) {
                            udfHeaderLink.put(udf.getId(), fact.getLink(FValue.UDF_SORT + udf.getId(), FValue.UDF + udf.getId(), 2));
                            udfHeaderCaption.put(udf.getId(), Null.stripNullHtml(HTMLEncoder.encode(udf.getCaption())));
                            udfs.add(udf.getId());
                        }
                    }
                }
            }
            sc.setRequestAttribute(request, "udfHeaderLink", udfHeaderLink);
            sc.setRequestAttribute(request, "udfHeaderCaption", udfHeaderCaption);
            sc.setRequestAttribute(request, "udfs", udfs);
            sc.setRequestAttribute(request, "totalTasks", taskSlider.getTotalChildrenCount());

            List<SecuredTaskBean> subtaskList = taskSlider.getCol();

            fullingSubtasks(sc, request, subtaskList, filterSettings, sortstring, udfList, !udfs.isEmpty());

            int sizeOfPart = 100 / fact.getParts();
            int freePercents = 100 - fact.getParts() * sizeOfPart;
            if (fullPathLnk.getCanView() && taskNameLnk.getCanView()) {
                int type = freePercents >> 1;
                fullPathLnk.setParts(12 * sizeOfPart + type);
                taskNameLnk.setParts(12 * sizeOfPart + (freePercents - type));
                sc.setRequestAttribute(request, "headerFullPath", fullPathLnk);
                sc.setRequestAttribute(request, "headerName", taskNameLnk);
            } else {
                fullPathLnk.setParts(12 * sizeOfPart + freePercents);
                taskNameLnk.setParts(12 * sizeOfPart + freePercents);
                sc.setRequestAttribute(request, "headerFullPath", fullPathLnk);
                sc.setRequestAttribute(request, "headerName", taskNameLnk);
            }
            sc.setRequestAttribute(request, "sizeOfPart", sizeOfPart);
            Boolean showClipboardButton = sc.canAction(Action.cutCopyPasteTask, taskId) &&
                    (
                            (filterSettings.getSettings().getOriginValues(FValue.SUBTASK) == null) ||
                                    (filterSettings.getSettings().getOriginValues(FValue.SUBTASK) != null && sc.canAction(Action.bulkProcessingTask, taskId))
                    );
            sc.setRequestAttribute(request, "showClipboardButton", showClipboardButton);
            sc.setRequestAttribute(request, "canPerformBulkProcessing", sc.canAction(Action.bulkProcessingTask, taskId));

            sc.setRequestAttribute(request, "taskfilter", filterSettings);
            sc.setRequestAttribute(request, "isList", "true");
            selectTaskTab(sc, taskId, "tabSubtasks", request);
            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_TASK_LIST);
            sc.setRequestAttribute(request, "showViewSubtasks", false);
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_TASK_LIST));
            sc.setRequestAttribute(request, "canManageTaskFilters", filter.canView() && (sc.canAction(Action.manageTaskPrivateFilters, taskId) || sc.canAction(Action.manageTaskPublicFilters, taskId)));
            Map<PluginType, List<AbstractPluginCacheItem>> scripts = PluginCacheManager.getInstance().list(
                    PluginType.BULK, PluginType.MULTI_BULK
            );
            List<AbstractPluginCacheItem> scriptCollection = scripts.get(PluginType.BULK);
            sc.setRequestAttribute(request, "bulkProcessing", scriptCollection);
            List<AbstractPluginCacheItem> multiBulk = scripts.get(PluginType.MULTI_BULK);
            sc.setRequestAttribute(request, "multiBulk", multiBulk);
            Cookie[] cook = request.getCookies();
            String selectedIds = "";
            if (cook != null) {

                for (Cookie c : cook) {
                    // log.debug(c.getName() + c.getValue());
                    if (c.getName().equals("_selectedId") && c.getValue() != null && c.getValue().length() > 0) {
                        selectedIds = c.getValue();
                    }
                }
            }
            ArrayList<SecuredTaskBean> selected = new ArrayList<SecuredTaskBean>();
            for (String s : selectedIds.split(UdfConstants.SPLIT_SEPARATOR)) {
                SecuredTaskBean t = AdapterManager.getInstance().getSecuredFindAdapterManager().findTaskById(sc, s);
                if (t != null) selected.add(t);

            }
            sc.setRequestAttribute(request, "selectedIds", selected);
            //!sc.canAction(Action.showOtherFilterTab, tci.getId()) reverse for default value true
            sc.setRequestAttribute(request, "showOtherFilterTab", !sc.canAction(Action.showOtherFilterTab, tci.getId()));
            sc.setRequestAttribute(request, "canUsePostFiltration", !sc.canAction(Action.canUsePostFiltration, tci.getId()));
            sc.setRequestAttribute(request, "canArchive", sc.canAction(Action.canArchive, tci.getId()));
            return mapping.findForward("subtaskJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    private void fullingSubtasks(SessionContext sc, HttpServletRequest request, List<SecuredTaskBean> subtaskList, FilterSettings filterSettings, List<String> sortstring, ArrayList<SecuredUDFBean> udfList, boolean initUdf) throws GranException {
        boolean lock = lockManager.acquireConnection(SubtaskAction.class.getName());
        try {
            EggBasket<SecuredTaskBean, SecuredMessageBean> taskLines = new EggBasket<SecuredTaskBean, SecuredMessageBean>(new TaskComparator(sortstring, udfList));
            boolean showOpenAll = false;
            for (SecuredTaskBean task : subtaskList) {
                task.getHandlerUser(); //initial in lock block
                task.getSubmitter();
                if (Config.getProperty("trackstudio.tree.node.mask") != null)
                {
                    task.setMaskName(NodeMask.nameByMask(task, false));
                }
                MessageFilter msgList = new MessageFilter(task);
                boolean showAudit = Preferences.isViewAutidTrail(sc.getUser().getPreferences());
                List<SecuredMessageBean> listMes = msgList.getMessageList(sc, (TaskFValue) filterSettings.getSettings(), false, showAudit);
                if (!showOpenAll) showOpenAll = !listMes.isEmpty();
                taskLines.put(task, listMes);
                if (initUdf) {
                    task.getAliasUdfValues();
                }
            }
            sc.setRequestAttribute(request, "showOpenAll", showOpenAll);
            sc.setRequestAttribute(request, "taskLines", taskLines);
        } finally {
            if (lock) lockManager.releaseConnection(SubtaskAction.class.getName());
        }
    }

    public static void highlightOperation(SessionContext sc, HttpServletRequest request) {
        String ids;
        String operationType = "\'\'";
        String idsArray = "\'\'";
        if (sc.getAttribute("TASKS") != null && sc.getAttribute("OPERATION") != null) {
            ids = (String) sc.getAttribute("TASKS");
            idsArray = "";
            String[] strings = ids.split(UdfConstants.SPLIT_SEPARATOR);
            for (int i = 0; i < (strings.length - 1); i++) {
                idsArray += "\'" + strings[i] + "\',";
            }
            idsArray += "\'" + strings[strings.length - 1] + "\'";
            if ((sc.getAttribute("OPERATION")).equals("CUT")) operationType = "\'cut_in_list\'";
            if ((sc.getAttribute("OPERATION")).equals("SINGLE_COPY")) operationType = "\'copy_in_list\'";
            if ((sc.getAttribute("OPERATION")).equals("RECURSIVELY_COPY"))
                operationType = "\'copy_recursively_in_list\'";
        }
        sc.setRequestAttribute(request, "operationType", operationType);
        sc.setRequestAttribute(request, "selectedTasks", idsArray);
    }

    public ActionForward delete(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            TaskListForm taskListForm = (TaskListForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            List<String> tasksToClearOperation = new ArrayList<String>();
            if (sc.getAttribute("TASKS") != null && sc.getAttribute("OPERATION") != null) {
                String ids = (String) sc.getAttribute("TASKS");
                for (String id : ids.split(UdfConstants.SPLIT_SEPARATOR)) {
                    SecuredTaskBean stb = AdapterManager.getInstance().getSecuredFindAdapterManager().findTaskById(sc, id);
                    if (stb != null)
                        tasksToClearOperation.add("#" + stb.getNumber() + " " + HTMLEncoder.encodeTree(stb.getName()));
                }
            } else {
                if (taskListForm.getSELTASK() != null) {
                    String[] id_s = taskListForm.getSELTASK();
                    String TASKS = null;
                    String tasksId = "";
                    for (String id : id_s) {
                        SecuredTaskBean stb = AdapterManager.getInstance().getSecuredFindAdapterManager().findTaskById(sc, id);
                        if (stb != null) {
                            TASKS = stb.getId();
                            tasksId = tasksId + TASKS + '*';
                            sc.setAttribute("TASKS", tasksId);
                            tasksToClearOperation.add("#" + stb.getNumber() + " " + HTMLEncoder.encodeTree(stb.getName()));
                        }
                    }
                }
            }
            sc.removeAttribute("TASKS");
            String[] ids = taskListForm.getSELTASK();
            if (ids != null) {
                deleteTasks(sc, ids);
                SecuredTaskBean parent = new SecuredTaskBean(taskListForm.getId(), sc);
                sc.setAttribute("jsEvent",
                        new ChangeEvent(ChangeEvent.EVENT_TASK_DELETED,
                                parent.getParent() != null ? parent.getParent().getNumber() : "1",
                                EMPTY_ARRAY, EMPTY_ARRAY,
                                EMPTY_ARRAY, EMPTY_ARRAY, EMPTY_ARRAY,
                                EMPTY_ARRAY, EMPTY_ARRAY, EMPTY_ARRAY, ""));
            }
            FilterSettings flthm = null;
            Object filterObject = sc.getAttribute("taskfilter");
            if (filterObject != null) {
                flthm = (FilterSettings) filterObject;
            }
            if (flthm != null)
                flthm.setCurrentPage(1);
            sc.setAttribute("taskfilter", flthm);
            sc.removeAttribute("statictask");
            sc.removeAttribute("next");
            sc.removeAttribute("statictasklist");
            sc.removeAttribute("collector");
            request.removeAttribute("collector");
            request.setAttribute("method", "page");
            return mapping.findForward("taskAction");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public List<String> deleteTasks(SessionContext sc, String[] ids) throws GranException{
        List<String> tasksToDelete = new ArrayList<String>();
        boolean lock = lockManager.acquireConnection(SubtaskAction.class.getName());
        try {
            for (String id : ids) {
                SecuredTaskBean stb = AdapterManager.getInstance().getSecuredFindAdapterManager().findTaskById(sc, id);
                if (stb != null) {
                    if (AdapterManager.getInstance().getSecuredCategoryAdapterManager().isCategoryDeletable(sc, stb.getId(), stb.getCategoryId())) {
                        tasksToDelete.add("#" + stb.getNumber() + " " + HTMLEncoder.encodeTree(stb.getName()));
                        List<String> val = stb.getMessagesIds();
                        if (val != null && val.size() != 0) {
                            for (String messageId : val) {
                                AdapterManager.getInstance().getSecuredMessageAdapterManager().deleteMessageOnlyForDeleteTask(sc, stb, messageId);
                            }
                        }
                        AdapterManager.getInstance().getSecuredTaskAdapterManager().deleteTask(sc, stb);
                    } else {
                        throw new UserException("ERROR_CAN_NOT_DELETE_TASK", new Object[]{stb.getNumber(), I18n.getString(new Locale(sc.getUser().getLocale()), "TASK_CATEGORY_NOT_DELETABLE")});
                    }
                }
            }
        } finally {
            if (lock) lockManager.releaseConnection(SubtaskAction.class.getName());
        }
        return tasksToDelete;
    }

    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            UserException ue = new UserException("Exceptions occures");
            if (!ue.getActionMessages().isEmpty()) throw ue;
            return mapping.findForward("taskAction");
        } finally {
            if (w) lockManager.releaseConnection();
        }
    }

    public ActionForward archive(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            TaskListForm taskListForm = (TaskListForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            if (!sc.canAction(Action.canArchive, taskListForm.getId())) {
                throw new IllegalAccessException("Don't have an access");
            }
            List<String> tasksForArchivation = new ArrayList();
            if (taskListForm.getSELTASK() != null) {
                String[] id_s = taskListForm.getSELTASK();
                for (String id : id_s) {
                    SecuredTaskBean stb = AdapterManager.getInstance().getSecuredFindAdapterManager().findTaskById(sc, id);
                    if (stb != null) {
                        tasksForArchivation.add(id);
                    }
                }
            }
            AdapterManager.getInstance().getSecuredTaskAdapterManager().archive(tasksForArchivation, sc);
            sc.setAttribute("jsEvent",
                    new ChangeEvent(ChangeEvent.EVENT_TASK_DELETED,
                            new SecuredTaskBean(taskListForm.getId(), sc).getNumber(),
                            EMPTY_ARRAY, EMPTY_ARRAY,
                            EMPTY_ARRAY, EMPTY_ARRAY, EMPTY_ARRAY,
                            EMPTY_ARRAY, EMPTY_ARRAY, EMPTY_ARRAY, ""));
            sc.removeAttribute("TASKS");
            sc.setRequestAttribute(request, "TASK", null);
        } catch (UserException ue) {
            saveMessages(request, ue.getActionMessages());
        } finally {
            if (w) lockManager.releaseConnection();
        }
        return mapping.findForward("taskAction");
    }

    public ActionForward archiveView(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            TaskListForm bf = (TaskListForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            if (!sc.canAction(Action.canArchive, bf.getId())) {
                throw new IllegalAccessException("Don't have an access");
            }
            String taskId = GeneralAction.getInstance().taskHeader(null, sc, request, true);
            String key = request.getParameter("archive_search");
            sc.setRequestAttribute(request,"archive_search", request.getParameter("archive_search"));
            int page = request.getParameter("sliderPage") != null ? Integer.parseInt(request.getParameter("sliderPage")) : 1;
            var list = AdapterManager.getInstance().getSecuredTaskAdapterManager().archives(sc, key, 20, (page - 1) * 20);
            int total = AdapterManager.getInstance().getSecuredTaskAdapterManager().totalArchives(sc, key);
            Slider<SecuredTaskArchiveBean> tasks = new Slider<SecuredTaskArchiveBean>(list, 20, null, page, total);
            if (request.getParameter("all") != null) {
                tasks.setAll(true, sc.getLocale());
            }
            sc.setRequestAttribute(request, "tasks", list);
            sc.setRequestAttribute(request, "slider", tasks.drawSlider(request.getContextPath() + "/SubtaskAction.do?method=archiveView&amp;id=" + taskId + "&amp;filterId=" + "1", "div", "slider"));
            sc.setRequestAttribute(request, "canDeleteArchive", sc.canAction(Action.canDeleteArchive, bf.getId()));
            return mapping.findForward("archiveJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }
    }

    public ActionForward deleteArchive(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            TaskListForm bf = (TaskListForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            if (!sc.canAction(Action.canDeleteArchive, bf.getId())) {
                throw new IllegalAccessException("Don't have an access");
            }
            String taskId = GeneralAction.getInstance().taskHeader(null, sc, request, true);
            String archiveId = request.getParameter("archiveId");
            AdapterManager.getInstance().getSecuredTaskAdapterManager().deleteArchive(sc, archiveId);
            ActionForward af = new ActionForward("SubtaskAction.do?method=archiveView&id=" + taskId);
            af.setRedirect(true);
            return af;
        } finally {
            if (w) lockManager.releaseConnection();
        }
    }

    public ActionForward bulk(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            TaskListForm sf = (TaskListForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String taskId = GeneralAction.getInstance().taskHeader(null, sc, request, true);
            SecuredTaskBean tci = new SecuredTaskBean(taskId, sc);

            List<SecuredTaskBean> tasks = buildBulkTask(sc, tci, sf, request);


            String bulk = sf.getBulk();
            AbstractPluginCacheItem script = PluginCacheManager.getInstance().find(PluginType.BULK, bulk);
            log.debug("TS tries to treat tasks : " + tasks.size());
            if (!tasks.isEmpty()) {
                if (script != null && script instanceof CompiledPluginCacheItem) {
                    try {
                        Class compiledClass = ((CompiledPluginCacheItem) script).getCompiled();
                        Object compiled = compiledClass.newInstance();
                        if (compiled != null) {
                            log.debug("TS has started to execute script : " + compiledClass.getName());
                            Method mi = compiledClass.getMethod("execute", SecuredTaskBean.class);
                            for (SecuredTaskBean task : tasks) {
                                try {
                                    mi.invoke(compiled, task);
                                } catch (Exception e) {
                                    log.error("Error in invoking " + task + " " + compiledClass.getName(), e);
                                }
                            }
                            log.debug("TS has finished to execute script : " + compiledClass.getName());
                        }
                    } catch (InstantiationException ie) {
                        throw new TriggerException(script.getName(), ie);
                    } catch (NoSuchMethodException e) {
                        throw new TriggerException(script.getName(), e);
                    } catch (IllegalAccessException e) {
                        throw new TriggerException(script.getName(), e);
                    }
                } else if (script != null) {

                    PluginCacheItem ret = (PluginCacheItem) script;
                    String formula = ret.getText();
                    for (SecuredTaskBean task : tasks) {
                        CalculatedValue cv = new CalculatedValue(formula, task);
                        try {
                            cv.getValue();
                        } catch (TargetError e) {
                            throw new TriggerException(script.getName(), e.getTarget());
                        } catch (EvalError ee) {
                            throw new TriggerException(script.getName(), ee);
                        } catch (Exception ex) {
                            throw new TriggerException(script.getName(), ex);
                        }
                    }

                }
            }

            FilterSettings flthm = null;
            Object filterObject = sc.getAttribute("taskfilter");
            if (filterObject != null) {
                flthm = (FilterSettings) filterObject;
            }
            if (flthm != null)
                flthm.setCurrentPage(1);
            sc.setAttribute("taskfilter", flthm);
            sc.removeAttribute("statictask");
            sc.removeAttribute("next");
            sc.removeAttribute("statictasklist");
            request.setAttribute("method", "page");
            if (request.getParameter("taskView") != null) {
                SecuredTaskBean parent = new SecuredTaskBean(tci.getId(), sc);
                sc.setAttribute("jsEvent",
                        new ChangeEvent(ChangeEvent.EVENT_TASK_DELETED,
                                parent.getParent() != null ? parent.getParent().getNumber() : "1",
                                EMPTY_ARRAY, EMPTY_ARRAY,
                                EMPTY_ARRAY, EMPTY_ARRAY, EMPTY_ARRAY,
                                EMPTY_ARRAY, EMPTY_ARRAY, EMPTY_ARRAY, ""));
                ActionForward af = new ActionForward(mapping.findForward("taskViewPage").getPath() + "&id=" + tci.getId());
                af.setRedirect(true);
                return af;
            }
            ActionForward af = new ActionForward(mapping.findForward("taskAction").getPath() + "&id=" + taskId);
            af.setRedirect(true);
            return af;
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    private List<SecuredTaskBean> buildBulkTask(SessionContext sc, SecuredTaskBean tci, TaskListForm sf, HttpServletRequest request) throws GranException {
        String ids = sf.getCollector();
        boolean usedByFilter = sf.isUseByFilter();
        List<SecuredTaskBean> tasks = new ArrayList<SecuredTaskBean>();
        if (Null.isNotNull(ids)) {
            for (String id : ids.split(UdfConstants.SPLIT_SEPARATOR)) {
                if (!id.isEmpty() && TaskRelatedManager.getInstance().isTaskExists(id)) {
                    tasks.add(new SecuredTaskBean(id, sc));
                }
            }
        } else if (usedByFilter) {
            InitParam initParam = buildSlider(sc, tci, request, sf);
            tasks.addAll(initParam.slider.getOriginalList());
        }
        return tasks;
    }

    public ActionForward multibulk(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            TaskListForm sf = (TaskListForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String taskId = GeneralAction.getInstance().taskHeader(null, sc, request, true);
            SecuredTaskBean tci = new SecuredTaskBean(taskId, sc);

            List<SecuredTaskBean> tasks = buildBulkTask(sc, tci, sf, request);

            String bulk = sf.getMultiBulk();
            AbstractPluginCacheItem script = PluginCacheManager.getInstance().find(PluginType.MULTI_BULK, bulk);
            if (!tasks.isEmpty()) {
                if (script != null && script instanceof CompiledPluginCacheItem) {
                    try {
                        Class compiledClass = ((CompiledPluginCacheItem) script).getCompiled();
                        Object compiled = compiledClass.newInstance();
                        if (compiled != null) {
                            Method mi = compiledClass.getMethod("execute", List.class);
                            if (!tasks.isEmpty()) {
                                mi.invoke(compiled, tasks);
                            }
                        }
                    } catch (InstantiationException ie) {
                        throw new TriggerException(script.getName(), ie);
                    } catch (NoSuchMethodException e) {
                        throw new TriggerException(script.getName(), e);
                    } catch (IllegalAccessException e) {
                        throw new TriggerException(script.getName(), e);
                    } catch (InvocationTargetException e) {
                        throw new TriggerException(script.getName(), e.getTargetException());
                    }
                } else if (script != null) {

                    PluginCacheItem ret = (PluginCacheItem) script;
                    String formula = ret.getText();
                    if (!tasks.isEmpty()) {
                        CalculatedValue cv = new CalculatedValue(formula, tasks, sc);
                        try {
                            cv.getValue();
                        } catch (TargetError e) {
                            throw new TriggerException(script.getName(), e.getTarget());
                        } catch (EvalError ee) {
                            throw new TriggerException(script.getName(), ee);
                        } catch (Exception ex) {
                            throw new TriggerException(script.getName(), ex);
                        }
                    }
                }

            }

            FilterSettings flthm = null;
            Object filterObject = sc.getAttribute("taskfilter");
            if (filterObject != null) {
                flthm = (FilterSettings) filterObject;
            }
            if (flthm != null)
                flthm.setCurrentPage(1);
            sc.setAttribute("taskfilter", flthm);
            sc.removeAttribute("statictask");
            sc.removeAttribute("next");
            sc.removeAttribute("statictasklist");
            return mapping.findForward("taskAction");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }


    public ActionForward clipboardOperation(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                            HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");

            TaskListForm sf = (TaskListForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            List<String> tasksToClearOperation = new ArrayList<String>();
            //StringBuffer selected = new StringBuffer();
            if (sc.getAttribute("TASKS") != null && sc.getAttribute("OPERATION") != null) {
                String ids = (String) sc.getAttribute("TASKS");
                for (String id : ids.split(UdfConstants.SPLIT_SEPARATOR)) {
                    SecuredTaskBean stb = AdapterManager.getInstance().getSecuredFindAdapterManager().findTaskById(sc, id);
                    if (stb != null) {
                        tasksToClearOperation.add(stb.getNumber());
                        //   if (selected.indexOf(stb.getId())==-1) selected.append(stb.getId()).append(":");
                    }
                }
            }
            String ids = sf.getCollector();
            if (ids != null) {
                sc.setAttribute("TASKS", ids);
                String operation = sf.getOperation();
                sc.setAttribute("OPERATION", operation);
                List<String> tasksToOperate = new ArrayList<String>();
                for (String id : ids.split(UdfConstants.SPLIT_SEPARATOR)) {
                    SecuredTaskBean stb = AdapterManager.getInstance().getSecuredFindAdapterManager().findTaskById(sc, id);
                    if (stb != null) {
                        tasksToOperate.add(stb.getNumber());
                    }
                }
                if (tasksToOperate.size() == 0 && sf.getSELTASK() != null) {
                    String[] id_s = sf.getSELTASK();
                    String TASKS = null;
                    String tasksId = "";
                    for (String id : id_s) {
                        SecuredTaskBean stb = AdapterManager.getInstance().getSecuredFindAdapterManager().findTaskById(sc, id);
                        if (stb != null) {
                            TASKS = stb.getId();
                            tasksId = tasksId + TASKS + '*';
                            sc.setAttribute("TASKS", tasksId);
                            tasksToOperate.add(stb.getNumber());
                        }
                    }
                }
                if (operation.equals("CUT")) sc.setAttribute("jsEvent",
                        new ChangeEvent(ChangeEvent.EVENT_TASK_CUT,
                                "1", new String[]{}, new String[]{},
                                tasksToOperate.toArray(new String[]{}), new String[]{},
                                new String[]{}, new String[]{}, tasksToOperate.toArray(new String[tasksToOperate.size()]), new String[]{}, ""));

                if (operation.equals("SINGLE_COPY"))
                    sc.setAttribute("jsEvent", new ChangeEvent(ChangeEvent.EVENT_TASK_COPIED,
                            "1", new String[]{},
                            new String[]{}, new String[]{}, new String[]{},
                            new String[]{}, new String[]{}, tasksToOperate.toArray(new String[tasksToOperate.size()]), new String[]{}, ""));

                if (operation.equals("RECURSIVELY_COPY"))
                    sc.setAttribute("jsEvent", new ChangeEvent(ChangeEvent.EVENT_TASK_COPIED_RECURSIVELY,
                            "1", new String[]{}, new String[]{},
                            tasksToOperate.toArray(new String[]{}), new String[]{},
                            new String[]{}, new String[]{}, tasksToOperate.toArray(new String[tasksToOperate.size()]), new String[]{}, ""));

                //            sc.setAttribute("CATEGORIES", categories.toArray(new String[]{}));
            }

            return mapping.findForward("subtaskPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward paste(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                               HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            TaskListForm sf = (TaskListForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            if (sc.getAttribute("TASKS") != null && sc.getAttribute("OPERATION") != null) {

                String ids = (String) sc.getAttribute("TASKS");
                List<SecuredTaskBean> errorCategoryTaskList = new ArrayList<SecuredTaskBean>();
                List<SecuredTaskBean> errorParentTaskList = new ArrayList<SecuredTaskBean>();
                ArrayList<SecuredCategoryBean> availColl = AdapterManager.getInstance().getSecuredCategoryAdapterManager().getAvailableCategoryList(sc, sf.getId());
                ArrayList<SecuredCategoryBean> categoryColl = AdapterManager.getInstance().getSecuredCategoryAdapterManager().getCreatableCategoryList(sc, sf.getId(), true);
                categoryColl.retainAll(availColl);
                List<String> parentHintForTaskCopeid = new ArrayList<String>();
                TreeSet<String> root = new TreeSet<String>();
                // �����-�������, ������� �� ���� ���� ����� ����� �������� � ���������� ����� ������ �� �����.
                for (String id : ids.split(UdfConstants.SPLIT_SEPARATOR)) {
                    if (root.isEmpty()) root.add(id);
                    else {
                        boolean clear = true;
                        for (String rt : root) {
                            if (TaskRelatedManager.getInstance().hasPath(id, rt)) {
                                clear = false;
                                if (TaskRelatedManager.getInstance().getTaskRelatedInfoChain(id, rt) != null) {
                                    // id is parent of rt, need to replace rt
                                    root.remove(rt);
                                    clear = true;
                                    break;
                                } else {
                                    // rt is parent of id, it's ok
                                    break;
                                }
                            }
                        }
                        if (clear) root.add(id);
                    }
                }
                StringBuffer rootstring = new StringBuffer();

                for (String id : root) {
                    SecuredTaskBean stb = AdapterManager.getInstance().getSecuredFindAdapterManager().findTaskById(sc, id);
                    if (stb != null) {
                        String parentHintForTask = AdapterManager.getInstance().getSecuredFindAdapterManager().findTaskById(sc, stb.getParentId()).getNumber();
                        parentHintForTaskCopeid.add(parentHintForTask);
                        if (!sc.canAction(Action.cutCopyPasteTask, sf.getId())) {
                            errorCategoryTaskList.add(stb);
                        } else if (!KernelManager.getTask().isValidParent(sc.getUserId(), stb.getId(), sf.getId())) {
                            errorParentTaskList.add(stb);
                        } else {
                            rootstring.append(id).append(",");
                        }
                    }
                }

                if (CommonConstants.COPY_RECURSIVELY.equals(sc.getAttribute("OPERATION"))) {
                    if (Arrays.asList(rootstring.toString().split(UdfConstants.SPLIT_SEPARATOR)).contains(sf.getId())) {
                        throw new UserException(I18n.getString(sc, "ERROR_COPY_RECURSIVELY"), false);
                    }
                }

                if (errorCategoryTaskList.isEmpty() && errorParentTaskList.isEmpty()) {
                    List<String> newIds = AdapterManager.getInstance().getSecuredTaskAdapterManager().pasteTasks(sc, sf.getId(), rootstring.toString(), (String) sc.getAttribute("OPERATION"));
                    if (!newIds.isEmpty()) {
                        sc.removeAttribute("TASKS");

                        List<String> taskNumbersToPaste = new ArrayList<String>();
                        List<String> taskNamesToPaste = new ArrayList<String>();
                        List<String> iconsToPaste = new ArrayList<String>();
                        List<String> statusIconsToPaste = new ArrayList<String>();
                        List<String> actionsToPaste = new ArrayList<String>();
                        List<String> taskIdsToPaste = new ArrayList<String>();
                        String context = request.getContextPath();
                        for (String id : newIds) {
                            SecuredTaskBean stb = AdapterManager.getInstance().getSecuredFindAdapterManager().findTaskById(sc, id);
                            if (stb != null) {
                                taskNumbersToPaste.add(stb.getNumber());
                                taskNamesToPaste.add(HTMLEncoder.encodeTree(stb.getName()));
                                String imageServlet = "/ImageServlet/" + GeneralAction.SERVLET_KEY;
                                iconsToPaste.add(context + imageServlet + "/icons/categories/" + stb.getCategory().getIcon());
                                String statusIcon = MacrosUtil.buildImageForState(stb.getStatus(), context + imageServlet);
                                statusIconsToPaste.add(statusIcon);
                                actionsToPaste.add("javascript:{self.top.frames[1].location = '" + context + "/task/" + stb.getNumber() + "?thisframe=true'; childs='" + stb.getChildrenCount() + "(" + AdapterManager.getInstance().getSecuredTaskAdapterManager().getTotalNotFinishChildren(sc, stb.getId()) + ")'; sortorder='" + Preferences.isSortOrgerInTree(stb.getCategory().getPreferences()) + "'; hide='" + Preferences.getHiddenInTree(stb.getCategory().getPreferences()) + "';}");
                                taskIdsToPaste.add(id);
                            }
                        }

                        List<String> tasksToClearOperation = new ArrayList<String>();
                        for (String id : ids.split(UdfConstants.SPLIT_SEPARATOR)) {
                            SecuredTaskBean stb = AdapterManager.getInstance().getSecuredFindAdapterManager().findTaskById(sc, id);
                            if (stb != null) tasksToClearOperation.add("#" + stb.getNumber());
                        }

                        String parentHint = "1";//AdapterManager.getInstance().getSecuredFindAdapterManager().findTaskById(sc, newIds.get(0)).getParent().getNumber();
                        String parentId = AdapterManager.getInstance().getSecuredFindAdapterManager().findTaskById(sc, newIds.get(0)).getParentId();
                        if (sc.getAttribute("OPERATION").equals("CUT")) {
                            sc.setAttribute("jsEvent", new ChangeEvent(ChangeEvent.EVENT_TASK_PASTED_CUT,
                                    parentHint,
                                    iconsToPaste.toArray(new String[]{}),
                                    statusIconsToPaste.toArray(new String[]{}),
                                    taskNumbersToPaste.toArray(new String[]{}),
                                    taskNamesToPaste.toArray(new String[]{}),
                                    actionsToPaste.toArray(new String[]{}),
                                    taskIdsToPaste.toArray(new String[]{}),
                                    new String[]{},
                                    parentHintForTaskCopeid.toArray(new String[]{}),
                                    parentId));
                        }

                        if (sc.getAttribute("OPERATION").equals("SINGLE_COPY"))
                            sc.setAttribute("jsEvent", new ChangeEvent(ChangeEvent.EVENT_TASK_PASTED_COPIED, parentHint,
                                    iconsToPaste.toArray(new String[]{}), statusIconsToPaste.toArray(new String[]{}),
                                    taskNumbersToPaste.toArray(new String[]{}), taskNamesToPaste.toArray(new String[]{}),
                                    actionsToPaste.toArray(new String[]{}), taskIdsToPaste.toArray(new String[]{}), tasksToClearOperation.toArray(new String[]{}), new String[]{}, parentId));

                        if (sc.getAttribute("OPERATION").equals("RECURSIVELY_COPY"))
                            sc.setAttribute("jsEvent", new ChangeEvent(ChangeEvent.EVENT_TASK_PASTED_COPIED_RECURSIVELY, parentHint,
                                    iconsToPaste.toArray(new String[]{}), statusIconsToPaste.toArray(new String[]{}),
                                    taskNumbersToPaste.toArray(new String[]{}), taskNamesToPaste.toArray(new String[]{}),
                                    actionsToPaste.toArray(new String[]{}), taskIdsToPaste.toArray(new String[]{}), tasksToClearOperation.toArray(new String[]{}), new String[]{}, parentId));
                    }
                } else {
                    sc.setRequestAttribute(request, "errorCategoryTaskList", errorCategoryTaskList);
                    sc.setRequestAttribute(request, "errorParentTaskList", errorParentTaskList);
                    return mapping.findForward("errorClipboardOperationPage");
                }
                sc.setRequestAttribute(request, "TASK", null);
            }
            return mapping.findForward("taskAction");
        } finally {
            if (w) lockManager.releaseConnection();
        }
    }

    private class InitParam {
        private Slider<SecuredTaskBean> slider;
        private String filterId;
        private boolean taskUDFView;
        private FilterSettings filterSettings;
        private FilterSettings originFilterSettings;
        private SecuredFilterBean filter;
        private boolean filterEqual;
    }
}
