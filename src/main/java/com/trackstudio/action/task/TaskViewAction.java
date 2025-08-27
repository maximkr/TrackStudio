package com.trackstudio.action.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.trackstudio.form.TaskListForm;
import com.trackstudio.kernel.cache.*;
import com.trackstudio.secured.*;
import com.trackstudio.securedkernel.SecuredTaskAdapterManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

import com.trackstudio.action.GeneralAction;
import com.trackstudio.action.TSDispatchAction;
import com.trackstudio.app.PageNaming;
import com.trackstudio.app.Preferences;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.csv.CSVImport;
import com.trackstudio.app.filter.FValue;
import com.trackstudio.app.filter.FilterSettings;
import com.trackstudio.app.filter.TaskFValue;
import com.trackstudio.app.filter.list.TaskFilter;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.constants.UdfConstants;
import com.trackstudio.exception.GranException;
import com.trackstudio.exception.UserException;
import com.trackstudio.form.BaseForm;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.model.Category;
import com.trackstudio.startup.Config;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.EggBasket;
import com.trackstudio.tools.compare.FieldSort;
import com.trackstudio.tools.compare.SortTask;
import com.trackstudio.tools.compare.SortUdf;
import com.trackstudio.tools.formatter.HourFormatter;
import com.trackstudio.tools.textfilter.MacrosUtil;
import org.json.JSONObject;

public class TaskViewAction extends TSDispatchAction {
    private static Log log = LogFactory.getLog(TaskViewAction.class);

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            if (sc == null) {
                sc = GeneralAction.getInstance().imports(request, response, false);
                if (sc == null) {
                    return null;
                }
            }
            String id = GeneralAction.getInstance().taskHeader(null, sc, request, true);
            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_TASK_OVERVIEW);
            SecuredTaskBean tci = new SecuredTaskBean(id, sc);
            sc.setCurrentSpace("TaskViewAction", request);
            EggBasket<SecuredUDFValueBean, SecuredTaskBean> refs = AdapterManager.getInstance().getSecuredIndexAdapterManager().getReferencedTasksForTask(tci);
            sc.setRequestAttribute(request, "refTasks", refs);
            EggBasket<SecuredUDFValueBean, SecuredUserBean> refsUser = AdapterManager.getInstance().getSecuredIndexAdapterManager().getReferencedUsersForTask(tci);
            sc.setRequestAttribute(request, "refsUser", refsUser);

            Map<SecuredTaskAttachmentBean, Boolean> container = tci.getAttachsTaskOrMessage();
            ArrayList<SecuredMstatusBean> mstatusColl = AdapterManager.getInstance().getSecuredStepAdapterManager().getAvailableMstatusList(sc, id);

            sc.setRequestAttribute(request, "isMstatuses", mstatusColl.size() > 0);
            sc.setRequestAttribute(request, "mstatuses", mstatusColl);

            boolean value = Preferences.isAscMessageSortOrder(sc.getUser().getPreferences());
            HourFormatter budgetFormatter = new HourFormatter(tci.getBudget(), tci.getBudgetFormat(), sc.getLocale());
            sc.setRequestAttribute(request, "sortMessageAsc", value);
            sc.setRequestAttribute(request, "budgeth", budgetFormatter.getFormat().contains("h"));
            sc.setRequestAttribute(request, "budgetm", budgetFormatter.getFormat().contains("m"));
            sc.setRequestAttribute(request, "budgets", budgetFormatter.getFormat().contains("s"));

            if (container != null) {
                HashMap<String, Boolean> deleteAttach = buildDeleteAttachMap(sc, tci, container);
                sc.setRequestAttribute(request, "deleteAttach", deleteAttach);
                List<SecuredTaskAttachmentBean> attachments = new ArrayList<SecuredTaskAttachmentBean>(container.keySet());
                Collections.sort(attachments, new SortAttachmentForTime());
                sc.setRequestAttribute(request, "attaNum", attachments.size());
                sc.setRequestAttribute(request, "attachments", attachments);
            }
            boolean showAudit = Preferences.isViewAutidTrail(sc.getUser().getPreferences());
            sc.setRequestAttribute(request, "isAudit", ((KernelManager.getStep().getMstatusesForAudit(tci.getId(), CSVImport.LOG_MESSAGE) != null) && AdapterManager.getInstance().getSecuredWorkflowAdapterManager().isAuditAvailable(sc, tci.getWorkflowId())));
            sc.setRequestAttribute(request, "showAudit", showAudit);
	        String viewCategory = Preferences.getViewCategory(tci.getCategory().getPreferences());
	        String asView = request.getParameter("asView");
	        boolean isDocument = "document".equals(asView) || asView == null && viewCategory != null && viewCategory.indexOf(Preferences.VIEW_CATEGORY_AS_TASK_AS_DOCUMENT) != -1;

            List<SecuredMessageBean> securedMessageBeans = SecuredTaskBean.getMessageCheckedAudit(tci.getMessages(), showAudit || isDocument);
            sc.setRequestAttribute(request, "isMessagesList", !(securedMessageBeans.size() == 1));
            if (securedMessageBeans != null && securedMessageBeans.size() > 0) {
                SecuredMessageBean lastMessage = securedMessageBeans.get(securedMessageBeans.size() - 1);
                sc.setRequestAttribute(request, "lastMessage", lastMessage);
            }
            boolean showClipboardButton = sc.canAction(Action.cutCopyPasteTask, id) && tci.getParentId() != null;
            sc.setRequestAttribute(request, "showClipboardButton", showClipboardButton);
            List<SecuredUDFValueBean> udfs = validateUdf(request, tci.getUDFValuesList());
            boolean isViewUdf = !udfs.isEmpty();

            Collections.sort(udfs, new SortUdf(FieldSort.ORDER));
            sc.setRequestAttribute(request, "isViewUdf", isViewUdf);
            sc.setRequestAttribute(request, "viewUdfList", udfs);
            sc.setRequestAttribute(request, "showViewSubtasks", true);
            sc.setRequestAttribute(request, "showViewTask", false);

            sc.setRequestAttribute(request, "canView", tci.canManage());
            boolean canEditTask = tci.canManage() && AdapterManager.getInstance().getSecuredTaskAdapterManager().isTaskEditable(sc, id);
            sc.setRequestAttribute(request, "canEditTask", canEditTask);
            sc.setRequestAttribute(request, "canEditTaskActualBudget", sc.canAction(Action.editTaskActualBudget, id));
            sc.setRequestAttribute(request, "messagesCount", tci.getMessageCount());
            sc.setRequestAttribute(request, "canViewTaskAttachments", sc.canAction(Action.viewTaskAttachments, id));
            sc.setRequestAttribute(request, "canManageTaskAttachments", canEditTask || sc.canAction(Action.manageTaskAttachments, id));
            sc.setRequestAttribute(request, "canManageTaskMessageAttachments", sc.canAction(Action.manageTaskMessageAttachments, id));
            sc.setRequestAttribute(request, "canCreateTaskAttachments", sc.canAction(Action.createTaskAttachments, id));

            Cookie[] cook = request.getCookies();
            String selectedIds = "";
            if (cook != null) {
                for (Cookie c : cook) {
                    if (c.getName().equals("selectedId") && c.getValue() != null && c.getValue().length() > 0) {
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

            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_TASK_OVERVIEW));
            sc.setRequestAttribute(request, "canViewPermLink", true);
            sc.setRequestAttribute(request, "useCookies", true);
            Object filterObject = sc.getAttribute("taskfilter");
            FilterSettings filterSettings;
            if (filterObject != null) {
                filterSettings = (FilterSettings) filterObject;
                String listTaskId = filterSettings.getObjectId();
                ArrayList<SecuredTaskBean> taskCol = null;
                Object staticListApply = sc.getAttribute("statictask");

                if (staticListApply != null && staticListApply.toString().equals(listTaskId)) {
                    Object staticListObject = sc.getAttribute("statictasklist");
                    if (staticListObject != null) {
                        taskCol = (ArrayList<SecuredTaskBean>) staticListObject;
                    } else {
                        sc.removeAttribute("statictask");
                        sc.removeAttribute("next");
                    }
                } else {
                    SecuredTaskBean activeTask = new SecuredTaskBean(listTaskId, sc);
                    boolean taskUDFView = filterSettings.getSettings().needFilterUDF() && !activeTask.getFilterUDFs().isEmpty();
                    TaskFilter taskList = new TaskFilter(activeTask);
                    boolean notwithsub = filterSettings.getSettings().get(FValue.SUBTASK) == null;
                    taskCol = taskList.getTaskList(((TaskFValue) filterSettings.getSettings()), taskUDFView, notwithsub, filterSettings.getSortedBy());
                    sc.setAttribute("statictask", listTaskId);
                    sc.setAttribute("statictasklist", taskCol);
                }

                int position = taskCol.indexOf(tci);
                if (position != -1) {
                    sc.setRequestAttribute(request, "position", position);
                    sc.setRequestAttribute(request, "listSize", taskCol.size());
                    if (position > 0) {
                        SecuredTaskBean prev = taskCol.get(position - 1);
                        sc.setRequestAttribute(request, "prev", prev);
                    }
                    if (position < taskCol.size() - 1) {
                        SecuredTaskBean next = taskCol.get(position + 1);
                        sc.setRequestAttribute(request, "next", next);
                        sc.setAttribute("next", next);
                    } else sc.removeAttribute("next");

                } else sc.removeAttribute("next");

            }
            sc.setRequestAttribute(request, "isDescription", tci.getDescription() != null && tci.getDescription().length() > 0);
            sc.setRequestAttribute(request, "showView", sc.canAction(Action.showView, tci.getId()));
            sc.setRequestAttribute(request, "taskId", tci.getId());
            sc.setRequestAttribute(request, "canPerformBulkProcessing", sc.canAction(Action.bulkProcessingTask, tci.getId()));
            sc.setRequestAttribute(request, "canArchive", sc.canAction(Action.canArchive, tci.getId()));
            Map<PluginType, List<AbstractPluginCacheItem>> scripts = PluginCacheManager.getInstance().list(
                    PluginType.BULK, PluginType.MULTI_BULK
            );
            List<AbstractPluginCacheItem> scriptCollection = scripts.get(PluginType.BULK);
            sc.setRequestAttribute(request, "bulkProcessing", scriptCollection);
            boolean createByThisLevel = Config.isTurnItOn("trackstudio.create.by.operation.this.level", "true");
            String copyParentId = tci.getParentId() != null ? tci.getParentId() : "1";
            copyParentId = createByThisLevel ? copyParentId : tci.getId();
            //!sc.canAction(Action.canCreateTaskByOperation, tci.getId()) reverse for default value true
            if (sc.allowedByACL(copyParentId) && !sc.canAction(Action.canCreateTaskByOperation, tci.getId())) {
                sc.setRequestAttribute(request, "canCreateTaskByOperation", AdapterManager.getInstance().getSecuredCategoryAdapterManager().getCreatableCategoryList(sc, createByThisLevel ? copyParentId : tci.getId()).contains(tci.getCategory()));
            }
            if (sc.getAttribute("after_user_exception") != null) {
                saveMessages(request, (ActionMessages) sc.getAttribute("after_user_exception"));
                sc.removeAttribute("after_user_exception");
            }
            if ("document".equals(asView) || asView == null && viewCategory != null && viewCategory.indexOf(Preferences.VIEW_CATEGORY_AS_TASK_AS_DOCUMENT) != -1) {
                List<SecuredTaskBean> documents = new ArrayList<SecuredTaskBean>();
                for (SecuredTaskBean stb : tci.getChildren()) {
                    TaskRelatedInfo info = TaskRelatedManager.getInstance().find(stb.getId());
                    log.debug("Document category id : " + info.getCategoryId());
                    Category category = KernelManager.getFind().findCategory(info.getCategoryId());
                    String view = category.getPreferences();
                    if (view != null && view.contains(Preferences.VIEW_CATEGORY_AS_TASK_AS_DOCUMENT)) {
                        documents.add(stb);
                    }
                    sc.setRequestAttribute(request, "documents", documents);
                }
                Collections.sort(documents, new SortTask(FieldSort.NAME, true));
                sc.setRequestAttribute(request, "isHistory", !securedMessageBeans.isEmpty());
                sc.setRequestAttribute(request, "asView", "document");
                return mapping.findForward("viewDocumentJSP");
            } else if ("container".equals(asView) || asView == null && viewCategory != null && viewCategory.indexOf(Preferences.VIEW_CATEGORY_AS_TASK_AS_FILE_CONTAINER) != -1) {
                List<SecuredTaskBean> containers = new ArrayList<SecuredTaskBean>();
                for (SecuredTaskBean stb : tci.getChildren()) {
                    String view = stb.getCategory().getPreferences();
                    if (view != null && view.indexOf(Preferences.VIEW_CATEGORY_AS_TASK_AS_FILE_CONTAINER) != -1) {
                        containers.add(stb);
                    }
                    sc.setRequestAttribute(request, "containers", containers);
                }
                sc.setRequestAttribute(request, "attachmentMatrix", MacrosUtil.getMatrixAttachment(tci.getAttachmentsCheckedName(), 4));
                sc.setRequestAttribute(request, "asView", "container");
                return mapping.findForward("viewFileJSP");
            } else {
                sc.setRequestAttribute(request, "asView", "task");
                return mapping.findForward("viewTaskJSP");
            }
        } finally {
            if (w) lockManager.releaseConnection();
        }
    }

    public ActionForward archive(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            if (sc == null) {
                sc = GeneralAction.getInstance().imports(request, response, false);
                if (sc == null) {
                    return null;
                }
            }
            String id = GeneralAction.getInstance().taskHeader(null, sc, request, true);
            if (!sc.canAction(Action.canArchive, id)) {
                throw new IllegalAccessException("Don't have an access");
            }
            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_TASK_OVERVIEW);
            SecuredTaskArchiveBean archive = AdapterManager.getInstance().getSecuredTaskAdapterManager().archiveById(sc, request.getParameter("archiveId"));
            sc.setRequestAttribute(request, "tci", new SecuredTaskBean(id, sc));
            sc.setRequestAttribute(request, "archive", archive);
            Map<String, List<SecuredTaskAttachmentBean>> attachmentsMap = new HashMap<>();
            for (SecuredTaskAttachmentBean at : archive.getAtts()) {
                String messageId = at.getItem().getMessageId();
                if (messageId != null) {
                    attachmentsMap.putIfAbsent(messageId, new ArrayList<>());
                    attachmentsMap.get(messageId).add(at);
                }
            }
            sc.setRequestAttribute(request, "attachmentsMsg", attachmentsMap);
            return mapping.findForward("archiveTaskJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }
    }

    private List<SecuredUDFValueBean> validateUdf(HttpServletRequest request, List<SecuredUDFValueBean> udfs) throws GranException {
        List<SecuredUDFValueBean> validateUdfs = new ArrayList<SecuredUDFValueBean>();
        for (SecuredUDFValueBean udf : udfs) {
            try {
                udf.getValue();
                validateUdfs.add(udf);
            } catch (Exception e) {
                UserException ue = new UserException("udf.getValue() returns the exception : " + udf.getCaption() + " > " + e.getMessage(), false);
                saveMessages(request, ue.getActionMessages());
                break;
            }
        }
        return validateUdfs;
    }

    public static HashMap<String, Boolean> buildDeleteAttachMap(SessionContext sc, SecuredTaskBean tci, Map<SecuredTaskAttachmentBean, Boolean> container) throws GranException {
        HashMap<String, Boolean> deleteAttach = new HashMap<String, Boolean>();
        if (container != null) {
            for (Map.Entry<SecuredTaskAttachmentBean, Boolean> entry : container.entrySet()) {
                boolean can;
                SecuredTaskAttachmentBean ali = entry.getKey();
                if (entry.getValue()) {
                    can = (sc.getUserId().equals(ali.getUserId()) && tci.getStatus().isStart()) || sc.canAction(Action.manageTaskAttachments, tci.getId());
                    if (!can) {
                        if (sc.canAction(Action.deleteTheirTaskAttachment, tci.getId())) {
                            can = ali.getUserId().equals(sc.getUserId());
                        }
                    }
                } else {
                    can = sc.canAction(Action.manageTaskMessageAttachments, tci.getId());
                    if (!can) {
                        if (sc.canAction(Action.deleteTheirMessageAttachment, tci.getId())) {
                            can = ali.getUserId().equals(sc.getUserId());
                        }
                    }
                }
                deleteAttach.put(ali.getId(), can);
            }
        }
        return deleteAttach;
    }

    public static class SortAttachmentForTime implements Comparator<SecuredTaskAttachmentBean> {
        @Override
        public int compare(SecuredTaskAttachmentBean o1, SecuredTaskAttachmentBean o2) {
            if (o1.getLastModified() == null && o2.getLastModified() == null) {
                return 0;
            }
            if (o1.getLastModified() == null) {
                return -1;
            }
            if (o2.getLastModified() == null) {
                return 1;
            }
            return -o1.getLastModified().compareTo(o2.getLastModified());
        }
    }
}