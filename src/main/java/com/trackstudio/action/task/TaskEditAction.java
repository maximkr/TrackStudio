package com.trackstudio.action.task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.trackstudio.action.AttachmentEditAction;
import com.trackstudio.action.GeneralAction;
import com.trackstudio.action.TSDispatchAction;
import com.trackstudio.app.Defaults;
import com.trackstudio.app.PageNaming;
import com.trackstudio.app.Preferences;
import com.trackstudio.app.TriggerManager;
import com.trackstudio.app.UDFFormFillHelper;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.builder.TaskBuilder;
import com.trackstudio.constants.CategoryConstants;
import com.trackstudio.exception.GranException;
import com.trackstudio.exception.TriggerException;
import com.trackstudio.exception.UserException;
import com.trackstudio.form.TaskForm;
import com.trackstudio.jmx.TimingTaskMXBeanImpl;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.manager.AttachmentArray;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.secured.SecuredCategoryBean;
import com.trackstudio.secured.SecuredPriorityBean;
import com.trackstudio.secured.SecuredPrstatusBean;
import com.trackstudio.secured.SecuredStatusBean;
import com.trackstudio.secured.SecuredTaskAttachmentBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredTaskTriggerBean;
import com.trackstudio.secured.SecuredUDFValueBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.Null;
import com.trackstudio.tools.compare.FieldSort;
import com.trackstudio.tools.compare.SortPriority;
import com.trackstudio.tools.formatter.HourFormatter;
import com.trackstudio.tools.textfilter.HTMLEncoder;
import com.trackstudio.tools.textfilter.MacrosUtil;

import static com.trackstudio.tools.Null.isNull;

public class TaskEditAction extends TSDispatchAction {
    private static Log log = LogFactory.getLog(TaskEditAction.class);

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            TaskForm tf = (TaskForm) form;

            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().taskHeader(tf, sc, request, true);
            boolean isNew = request.getParameter("newTask") != null;
            SecuredTaskBean tci = new SecuredTaskBean(id, sc);
            tci.setNewEntity(isNew);
            if (!(AdapterManager.getInstance().getSecuredTaskAdapterManager().isTaskEditable(sc, id) || isNew))
                return null;
            TimingTaskMXBeanImpl.getInstance().setStartPageTask(Calendar.getInstance());
            sc.setRequestAttribute(request, "tci", tci);
            boolean taskEditable = true;
            String description;
            String referer = request.getContextPath() + "/TaskViewAction.do?method=page&amp;id=" + id;
            String viewCategory = "";
            final String categoryId, workflowId;
            categoryId = isNew ? request.getParameter("category") : tci.getCategoryId();
            SecuredCategoryBean categoryBean = AdapterManager.getInstance().getSecuredFindAdapterManager().findCategoryById(sc, categoryId);
            workflowId = categoryBean.getWorkflowId();
            boolean throwUserException = sc.getAttribute("taskForm") != null;
            if (throwUserException) {
                TaskForm tff = (TaskForm) sc.getAttribute("taskForm");
                Map<String, List<SecuredTaskBean>> udfsTasks = new HashMap<String, List<SecuredTaskBean>>();
                Map<String, SecuredUDFValueBean> values = tci.getUDFValues();
                for (SecuredUDFValueBean sub : tci.getUDFValuesForNewTask(workflowId)) {
                    if (sub.getType().equals("task")) {
                        Object o = tff.getUdf(sub.getId());
                        if (o != null) {
                            String[] nums = (String[]) o;
                            SecuredUDFValueBean saved = values.get(sub.getId());
                            List<SecuredTaskBean> uTasks = new ArrayList<SecuredTaskBean>();
                            for (String number : nums) {
                                SecuredTaskBean udfTask = AdapterManager.getInstance().getSecuredTaskAdapterManager().findTaskByNumber(sc, number);
                                if (saved != null && saved.getValue() != null && ((List<SecuredTaskBean>) saved.getValue()).contains(udfTask)) {
                                    continue;
                                }
                                uTasks.add(udfTask);
                            }
                            if (!uTasks.isEmpty()) {
                                udfsTasks.put(sub.getId(), uTasks);
                            }
                        }
                    }
                }
                sc.setRequestAttribute(request, "udfsTasks", udfsTasks);
            }
            if (isNew) {
                referer = request.getContextPath() + "/TaskAction.do?method=page&amp;id=" + id;
                viewCategory = categoryBean.getPreferences();
                String taskName = tf.getName();

                SecuredStatusBean startStatus = AdapterManager.getInstance().getSecuredFindAdapterManager().findStatusById(sc, KernelManager.getWorkflow().getStartStateId(workflowId));
                List<SecuredStatusBean> stateList = AdapterManager.getInstance().getSecuredWorkflowAdapterManager().getStateList(sc, workflowId);
                List<SecuredStatusBean> starts = new ArrayList<SecuredStatusBean>();
                for (SecuredStatusBean ssb : stateList) {
                    if (ssb.isStart()) {
                        starts.add(ssb);
                    }
                }
                sc.setRequestAttribute(request, "isOne", starts.size() == 1);
                sc.setRequestAttribute(request, "starts", starts);
                sc.setRequestAttribute(request, "taskName", taskName);
                sc.setRequestAttribute(request, "taskAlias", "");
                description = throwUserException ? ((TaskForm) sc.getAttribute("taskForm")).getDescription() : Null.stripNullHtml(categoryBean.getTemplate());
                //sc.setRequestAttribute(request, "description", description);
                if (isNull(description)) {
                    sc.setRequestAttribute(request, "emptyDescription", Boolean.TRUE);
                }
                sc.setRequestAttribute(request, "taskCategory", categoryBean.getName());
                tci.setCategoryForNewTask(categoryId);
                ArrayList<SecuredUDFValueBean> list = tci.getUDFValuesForNewTask(workflowId);
                try {
                    UDFFormFillHelper.isValidateScript(list);
                } catch (UserException e) {
                    UserException ue = new UserException("LOORUP_SCRIPT_EXCEPTION", false);
                    ue.addActionMessages(e.getActionMessages());
                    saveMessages(request, e.getActionMessages());
                }
                UDFFormFillHelper.fillUdf(tci, tci.getId(), tf, list, request, "taskForm", true, false, startStatus);
                sc.setRequestAttribute(request, "startStatus", startStatus);
                sc.setRequestAttribute(request, "category", categoryId);
                sc.setRequestAttribute(request, "newTaskCategory", categoryBean);

                List<SecuredUserBean> handlerList = AdapterManager.getInstance().getSecuredStepAdapterManager().getTaskEditHandlerList(sc, id, categoryId, true);
                Set<SecuredUserBean> handlerSet = new TreeSet<SecuredUserBean>(handlerList);
                Set<SecuredPrstatusBean> handlerGroupSet = new TreeSet<SecuredPrstatusBean>(AdapterManager.getInstance().getSecuredStepAdapterManager().getTaskEditGroupHandlerList(sc, id, categoryId, true));
                if (!categoryBean.isHandlerRequired()) {
                    boolean userIDandGroupID = tci.getHandlerUserId() == null && tci.getHandlerGroupId() == null;
                    boolean userSetID = tci.getHandlerUserId() != null && !handlerSet.contains(tci.getHandlerUser());
                    boolean gruopSetID = tci.getHandlerGroupId() != null && !handlerGroupSet.contains(tci.getHandlerGroup());
                    if (userIDandGroupID || userSetID || gruopSetID) {
                        tf.setHandler(I18n.getString(sc.getLocale(), "MSG_NOBODY"));
                    } else {
                        if (tci.getHandlerUserId() != null) {
                            tf.setHandler(tci.getHandlerUserId());
                        } else {
                            tf.setHandler("PR_" + tci.getHandlerGroupId());
                        }
                    }
                } else {
                    if (handlerGroupSet.size() != 0) {
                        sc.setRequestAttribute(request, "handlerGroup", handlerGroupSet.iterator().next());
                    } else if (handlerSet.size() != 0) {
                        sc.setRequestAttribute(request, "handler", tci.getHandlerUser());
                    }
                }
                sc.setRequestAttribute(request, "handlers", Preferences.isHandlerOnlyRole(categoryBean.getPreferences()) ? new ArrayList<SecuredUserBean>(0) : new ArrayList<SecuredUserBean>(handlerSet));
                sc.setRequestAttribute(request, "handlerGroups", new ArrayList<SecuredPrstatusBean>(handlerGroupSet));

                sc.setRequestAttribute(request, "NOT_CHOOSEN", I18n.getString(sc, "NOT_CHOOSEN"));
                tf.setParentForCancel(tci.getParentId());
                tf.setWorkflowId(tci.getWorkflowId());
                tf.setCategory(categoryId);

                String format = AdapterManager.getInstance().getSecuredFindAdapterManager().findCategoryById(sc, tf.getCategory()).getBudget();
                format = format != null ? format : "";
                String shortFormat = format.indexOf("(") != -1 ? format.substring(0, format.indexOf("(")) : format;
                sc.setRequestAttribute(request, "budgetY", shortFormat.indexOf("Y") > -1);
                sc.setRequestAttribute(request, "budgetM", shortFormat.indexOf("M") > -1);
                sc.setRequestAttribute(request, "budgetW", shortFormat.indexOf("W") > -1);
                sc.setRequestAttribute(request, "budgetD", shortFormat.indexOf("D") > -1);
                sc.setRequestAttribute(request, "budgeth", shortFormat.indexOf("h") > -1);
                sc.setRequestAttribute(request, "budgetm", shortFormat.indexOf("m") > -1);
                sc.setRequestAttribute(request, "budgets", shortFormat.indexOf("s") > -1);

            } else {
                viewCategory = tci.getCategory().getPreferences();
                description = throwUserException ? ((TaskForm) sc.getAttribute("taskForm")).getDescription() : tci.getDescription();
                sc.setRequestAttribute(request, "description", description);
                if (tci.getHandler() != null) {
                    sc.setRequestAttribute(request, "handler", tci.getHandler());
                }
                HourFormatter budgetFormatter = new HourFormatter(tci.getBudget(), tci.getBudgetFormat(), sc.getLocale());
                if (isNull(sc.getAttribute("taskForm")) && tci.getBudget() != null) {
                    tf.setBudgetDoubleYears(budgetFormatter.getYears());
                    tf.setBudgetIntegerYears(budgetFormatter.getYears().intValue());
                    tf.setBudgetDoubleMonths(budgetFormatter.getMonths());
                    tf.setBudgetIntegerMonths(budgetFormatter.getMonths().intValue());
                    tf.setBudgetDoubleWeeks(budgetFormatter.getWeeks());
                    tf.setBudgetIntegerWeeks(budgetFormatter.getWeeks().intValue());
                    tf.setBudgetDoubleDays(budgetFormatter.getDays());
                    tf.setBudgetIntegerDays(budgetFormatter.getDays().intValue());
                    tf.setBudgetDoubleHours(budgetFormatter.getHours());
                    tf.setBudgetIntegerHours(budgetFormatter.getHours().intValue());
                    tf.setBudgetDoubleMinutes(budgetFormatter.getMinutes());
                    tf.setBudgetIntegerMinutes(budgetFormatter.getMinutes().intValue());

                    tf.setBudgetIntegerSeconds(budgetFormatter.getSeconds().intValue());
                }
                sc.setRequestAttribute(request, "budgetY", budgetFormatter.getFormat().indexOf(CategoryConstants.Y) > -1);
                sc.setRequestAttribute(request, "budgetM", budgetFormatter.getFormat().indexOf(CategoryConstants.M) > -1);
                sc.setRequestAttribute(request, "budgetW", budgetFormatter.getFormat().indexOf(CategoryConstants.W) > -1);
                sc.setRequestAttribute(request, "budgetD", budgetFormatter.getFormat().indexOf(CategoryConstants.D) > -1);
                sc.setRequestAttribute(request, "budgeth", budgetFormatter.getFormat().indexOf(CategoryConstants.h) > -1);
                sc.setRequestAttribute(request, "budgetm", budgetFormatter.getFormat().indexOf(CategoryConstants.m) > -1);
                sc.setRequestAttribute(request, "budgets", budgetFormatter.getFormat().indexOf(CategoryConstants.s) > -1);

                taskEditable = AdapterManager.getInstance().getSecuredTaskAdapterManager().isTaskEditable(sc, id);
                sc.setRequestAttribute(request, "wikiDescription", Null.stripNullHtml(tci.getDescription()));

                sc.setRequestAttribute(request, "taskName", throwUserException ? tf.getName() : tci.getName());
                if (!throwUserException) tf.setName(tci.getName());
                if (!throwUserException) tf.setShortname(tci.getShortname());
                sc.setRequestAttribute(request, "taskAlias", throwUserException ? tf.getShortname() : tci.getShortname());
                tci.setCategoryForNewTask(null);
                ArrayList<SecuredUDFValueBean> list = tci.getUDFValuesList();
                try {
                    UDFFormFillHelper.isValidateScript(list);
                } catch (UserException e) {
                    UserException ue = new UserException(I18n.getString("LOOKUP_SCRIPT_EXCEPTION"), false);
                    ue.addActionMessages(e.getActionMessages());
                    saveMessages(request, ue.getActionMessages());
                }
                UDFFormFillHelper.fillUdf(tci, tci.getId(), tf, list, request, "taskForm", true, false, null);
                tf.setParentForCancel(tci.getParentId());
                tf.setWorkflowId(tci.getWorkflowId());
                Map<SecuredTaskAttachmentBean, Boolean> container = tci.getAttachsTaskOrMessage();
                if (container != null) {
                    HashMap<String, Boolean> deleteAttach = TaskViewAction.buildDeleteAttachMap(sc, tci, container);
                    sc.setRequestAttribute(request, "deleteAttach", deleteAttach);
                    List<SecuredTaskAttachmentBean> attachments = new ArrayList<SecuredTaskAttachmentBean>(container.keySet());
                    Collections.sort(attachments, new TaskViewAction.SortAttachmentForTime());
                    sc.setRequestAttribute(request, "attaNum", attachments.size());
                    sc.setRequestAttribute(request, "canViewTaskAttachments", sc.canAction(Action.viewTaskAttachments, id));
                    sc.setRequestAttribute(request, "attachments", attachments);
                }
            }
            List<SecuredUserBean> handlerList = AdapterManager.getInstance().getSecuredStepAdapterManager().getTaskEditHandlerList(sc, id, categoryId, true);
            Map<String, String> handlerRole = GeneralAction.getInstance().handlerRole(sc, request, handlerList, tci.getId());
            sc.setRequestAttribute(request, "handlerMap", handlerRole);
            if (description == null || description.length() == 0)
                sc.setRequestAttribute(request, "emptyDescription", Boolean.TRUE);

            ArrayList<SecuredPriorityBean> priorityList = AdapterManager.getInstance().getSecuredWorkflowAdapterManager().getPriorityList(sc, workflowId);
            String onePriorityName = priorityList.size() == 1 ? priorityList.get(0).getName() : "";
            if (isNull(tf.getPriority()) && !priorityList.isEmpty()) {
                String currentPriorityId = null;
                if (!isNew && tci.getId() != null) currentPriorityId = tci.getPriorityId();
                if (currentPriorityId == null)
                    currentPriorityId = Defaults.getPriority(sc, workflowId) == null ? null : Defaults.getPriority(sc, workflowId).getId();
                if (currentPriorityId == null)
                    currentPriorityId = priorityList.get(0).getId();
                tf.setPriority(currentPriorityId);
                if (currentPriorityId != null)
                    sc.setRequestAttribute(request, "priorityName", AdapterManager.getInstance().getSecuredFindAdapterManager().findPriorityById(sc, currentPriorityId).getName());
            }
            tf.setDescription(description);

            sc.setRequestAttribute(request, "workflowId", workflowId);
            sc.setRequestAttribute(request, "isNew", isNew);
            sc.setRequestAttribute(request, "referer", referer);
            sc.setRequestAttribute(request, "canModify", taskEditable);
            Collections.sort(priorityList, new SortPriority(FieldSort.ORDER, false));
            sc.setRequestAttribute(request, "priorityList", priorityList);
            sc.setRequestAttribute(request, "onePriorityName", onePriorityName);
            if (isNull(tf.getDeadline())) {
                String deadlineParsed = Null.stripNullText(sc.getUser().getDateFormatter().parse(tci.getDeadline()));
                sc.setRequestAttribute(request, "parsedDeadline", deadlineParsed);
                tf.setDeadline(deadlineParsed);
            }
            sc.setRequestAttribute(request, "newTask", isNew);
            Object nextObject = sc.getAttribute("next");
            if (nextObject != null)
                sc.setRequestAttribute(request, "next", (SecuredTaskBean) nextObject);
            sc.setRequestAttribute(request, "canEdit", taskEditable && sc.allowedByACL(id));
            sc.setRequestAttribute(request, "canEditTaskAlias", sc.canAction(Action.editTaskAlias, id));
            sc.setRequestAttribute(request, "canCreateTaskAttachments", sc.canAction(Action.createTaskAttachments, id));
            sc.setRequestAttribute(request, "canViewTaskResolution", !isNew && sc.canAction(Action.viewTaskResolution, id));
            sc.setRequestAttribute(request, "canViewTaskPriority", sc.canAction(Action.viewTaskPriority, id));
            sc.setRequestAttribute(request, "canEditTaskPriority", sc.canAction(Action.editTaskPriority, id));
            sc.setRequestAttribute(request, "canViewTaskSubmitDate", !isNew && sc.canAction(Action.viewTaskSubmitDate, id));
            sc.setRequestAttribute(request, "canViewTaskLastUpdated", !isNew && sc.canAction(Action.viewTaskLastUpdated, id));
            sc.setRequestAttribute(request, "canViewTaskCloseDate", !isNew && sc.canAction(Action.viewTaskCloseDate, id) && tci.getClosedate() != null);
            sc.setRequestAttribute(request, "canViewTaskDeadline", sc.canAction(Action.viewTaskDeadline, id));
            sc.setRequestAttribute(request, "canEditTaskDeadline", sc.canAction(Action.editTaskDeadline, id));
            sc.setRequestAttribute(request, "canViewTaskBudget", sc.canAction(Action.viewTaskBudget, id));
            sc.setRequestAttribute(request, "canEditTaskBudget", sc.canAction(Action.editTaskBudget, id));
            sc.setRequestAttribute(request, "canViewTaskActualBudget", !isNew && sc.canAction(Action.viewTaskActualBudget, id));
            sc.setRequestAttribute(request, "canViewTaskDescription", sc.canAction(Action.viewTaskDescription, id));
            sc.setRequestAttribute(request, "canEditTaskDescription", sc.canAction(Action.editTaskDescription, id));
            sc.setRequestAttribute(request, "canEditTaskHandler", sc.canAction(Action.editTaskHandler, id) && isNew);


            sc.setRequestAttribute(request, "canSubmit", (taskEditable || isNew));
            sc.setRequestAttribute(request, "currentTab", PageNaming.TASK_EDIT);
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TASK_EDIT));
            TimingTaskMXBeanImpl.getInstance().setFinishPageTask(Calendar.getInstance());
            sc.removeAttribute("taskForm");
            String asView = request.getParameter("asView");
            if (viewCategory != null && viewCategory.contains(Preferences.VIEW_CATEGORY_AS_TASK_AS_DOCUMENT) && "document".equals(asView)) {
                return mapping.findForward("editDocumentJSP");
            } else if (viewCategory != null && viewCategory.contains(Preferences.VIEW_CATEGORY_AS_TASK_AS_FILE_CONTAINER) && "container".equals(asView)) {
                return mapping.findForward("editFileJSP");
            } else {
                sc.setRequestAttribute(request, "asView", "task");
                return mapping.findForward("editTaskJSP");
            }
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward saveTask(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws GranException {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            //todo laposhin ����� ���� ����������� ������ �� ����� ��������� �������� "0", ����� ���� ����� ������.
            //todo laposhin �� ���� ������ ���� "null".
            TaskForm tf = (TaskForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            try {
                String id = commonSaveTask(sc, tf, request);
                SecuredTaskBean tci = new SecuredTaskBean(id, sc);
                if (tci.isAllowedByACL()) {
                    tf.setId(id);
                    sc.setRequestAttribute(request, "id", id);

                } else {
                    String idLoccal = tci.getParentId() != null && tci.getParent().isOnSight() ? tci.getParentId() : "1";
                    tf.setId(idLoccal);
                    sc.setRequestAttribute(request, "id", idLoccal);

                }
            } catch (TriggerException te) {
                sc.setAttribute("taskForm", tf);
                throw te;
            } catch (UserException ue) {
                if (ue.getActionMessages() != null) {
                    tf.setMutable(false);
                    saveMessages(request, ue.getActionMessages());
                    return mapping.getInputForward();
                } else
                    throw ue;
            }

            ActionForward af = new ActionForward(mapping.findForward("taskViewPage").getPath() + (tf.getId() != null ? "&id=" + tf.getId() : ""));
            af.setRedirect(true);
            return af;
        } finally {
            if (w) lockManager.releaseConnection();
        }
    }

    public ActionForward saveGoToParent(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                        HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            TaskForm tf = (TaskForm) form;
            try {

                SessionContext sc = (SessionContext) request.getAttribute("sc");

                String id = commonSaveTask(sc, tf, request);
                SecuredTaskBean tci = new SecuredTaskBean(id, sc);
                if (tci.getParentId() != null) {
                    sc.setRequestAttribute(request, "id", tci.getParentId());
                    tf.setId(tci.getParentId());
                }

            } catch (UserException ue) {
                if (ue.getActionMessages() != null) {
                    tf.setMutable(false);
                    saveMessages(request, ue.getActionMessages());
                    //return fromOverview ? mapping.findForward("userOverviewPage") : mapping.getInputForward();
                    return mapping.getInputForward();
                } else
                    throw ue;
            }
            ActionForward af = new ActionForward(mapping.findForward("subtaskPage").getPath() + "&id=" + tf.getId());
            af.setRedirect(true);
            return af;
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward saveGoToNext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            TaskForm tf = (TaskForm) form;
            try {
                SessionContext sc = (SessionContext) request.getAttribute("sc");
                String id = commonSaveTask(sc, tf, request);

                if (sc.getAttribute("next") != null) {
                    SecuredTaskBean b = (SecuredTaskBean) sc.getAttribute("next");
                    sc.setRequestAttribute(request, "id", b.getId());
                    tf.setId(b.getId());
                }

            } catch (UserException ue) {
                if (ue.getActionMessages() != null) {
                    tf.setMutable(false);
                    saveMessages(request, ue.getActionMessages());
                    //return fromOverview ? mapping.findForward("userOverviewPage") : mapping.getInputForward();
                    return mapping.getInputForward();
                } else
                    throw ue;
            }
            ActionForward af = new ActionForward(mapping.findForward("taskViewPage").getPath() + "&id=" + tf.getId());
            af.setRedirect(true);
            return af;
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward saveDocument(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            TaskForm tf = (TaskForm) form;
            String id;
            try {
                SessionContext sc = (SessionContext) request.getAttribute("sc");
                boolean isNew = tf.getNewTask() != null && tf.getNewTask().length() != 0;
                if (isNew) {
                    id = commonSaveTask(sc, tf, request);
                } else {
                    id = GeneralAction.getInstance().taskHeader(tf, sc, request, false);
                    SecuredTaskTriggerBean task = new SecuredTaskTriggerBean(id, sc);
                    String description = tf.getDescription();
                    if (tf.getPlainText() != null) {
                        description = HTMLEncoder.text2HTML(description);
                    }
                    ArrayList<AttachmentArray> atts = new ArrayList<AttachmentArray>();
                    if (tf.getFile() != null && ((List) tf.getFile()).size() != 0) {
                        atts = AttachmentEditAction.uploadForm(form);
                    }
                    if (description != null && description.contains("src=\"data:image/png;base64,")) {
                        description = MacrosUtil.parseImagesFromText(atts, description, id);
                    }
                    description = TriggerManager.getInstance().createAttachments(sc, task.getId(), task.getNumber(), description, atts);
                    TriggerManager.getInstance().updateTask(sc, task.getId(), task.getShortname(), tf.getName(), description, task.getBudget(), task.getDeadline(), task.getPriorityId(), task.getParentId(), task.getHandlerUserId(), task.getHandlerGroupId(), true, task.getUdfValues(), false);
                }
                if (sc.getAttribute("next") != null) {
                    SecuredTaskBean b = (SecuredTaskBean) sc.getAttribute("next");
                    sc.setRequestAttribute(request, "id", b.getId());
                    tf.setId(b.getId());
                }

            } catch (UserException ue) {
                if (ue.getActionMessages() != null) {
                    tf.setMutable(false);
                    saveMessages(request, ue.getActionMessages());
                    //return fromOverview ? mapping.findForward("userOverviewPage") : mapping.getInputForward();
                    return mapping.getInputForward();
                } else
                    throw ue;
            }
            ActionForward af = new ActionForward(mapping.findForward("taskViewPage").getPath() + "&id=" + id);
            af.setRedirect(true);
            return af;
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }


    private String commonSaveTask(SessionContext sc, TaskForm aform, HttpServletRequest request) throws GranException {
        log.trace("##########");
        TaskForm form = aform;

        SecuredTaskBean stb = new SecuredTaskBean(aform.getId(), sc);
        boolean isNew = form.getNewTask() != null && form.getNewTask().length() != 0;
        String workflowId;
        String startStatus = form.getStatus();
        SecuredStatusBean startStatusTree;
        if (startStatus == null) {
            startStatusTree = stb.getStatus();
        } else {
            startStatusTree = AdapterManager.getInstance().getSecuredFindAdapterManager().findStatusById(sc, startStatus);
        }
        String parent;
        String id = stb.getId();
        if (isNew) {
            parent = stb.getId();
        } else {
            parent = stb.getParent() != null ? stb.getParent().getId() : null;
        }
        Collection udfColl = isNew ? stb.getUDFs(AdapterManager.getInstance().getSecuredFindAdapterManager().findCategoryById(sc, form.getCategory()).getWorkflowId()) : stb.getUDFs();
        if (isNew && startStatus == null) {
            SecuredCategoryBean categoryBean = AdapterManager.getInstance().getSecuredFindAdapterManager().findCategoryById(sc, form.getCategory() != null && form.getCategory().length() != 0 ? form.getCategory() : "");
            workflowId = categoryBean.getWorkflowId();
            startStatus = KernelManager.getWorkflow().getStartStateId(workflowId);
            startStatusTree = AdapterManager.getInstance().getSecuredFindAdapterManager().findStatusById(sc, KernelManager.getWorkflow().getStartStateId(workflowId));
        }
        HashMap<String, String> udfMap = new UDFFormFillHelper().getUdfMap(sc, id, udfColl, form, null, startStatus);
        String description = form.getDescription();
        String handler = form.getHandler();

        log.debug("handler=" + handler);
        String handlerGroup = null;
        String handlerUser = null;
        if (handler == null || handler.length() != 0) {
            if (handler != null && handler.startsWith("PR_"))
                handlerGroup = handler.substring("PR_".length());
            else if (handler != null)
                handlerUser = handler;
            else {
                if (!isNew) {
                    handlerUser = stb.getHandlerId();
                    handlerGroup = stb.getHandlerGroupId();
                }
            }
        }
        Calendar deadline = form.getDeadline() != null && form.getDeadline().length() > 0 ? sc.getUser().getDateFormatter().parseToCalendar(form.getDeadline()) : null;
        Long budget = HourFormatter.parseInput(form.getBudgetYears(), form.getBudgetMonths(), form.getBudgetWeeks(), form.getBudgetDays(), form.getBudgetHours(), form.getBudgetMinutes(), form.getBudgetSeconds());
        String context = request.getContextPath();
        String imageServlet = "/ImageServlet/" + GeneralAction.SERVLET_KEY;
        if (form.getPlainText() != null)
            description = HTMLEncoder.text2HTML(description);
        int total = AdapterManager.getInstance().getSecuredTaskAdapterManager().getTotalNotFinishChildren(sc, stb.getId());
        if (isNew) {
            TimingTaskMXBeanImpl.getInstance().setStartCreateTask(Calendar.getInstance());
            ArrayList<AttachmentArray> atts = new ArrayList<AttachmentArray>();
            if (form.getFile() != null && ((List) form.getFile()).size() != 0) {
                atts.addAll(AttachmentEditAction.uploadForm(form));
            }

            if (description != null && description.contains("src=\"data:image/png;base64,")) {
                description = MacrosUtil.parseImagesFromText(atts, description, null);
            }

            TaskBuilder taskBuilder = new TaskBuilder();
            taskBuilder.setUpdatedate(Calendar.getInstance());
            taskBuilder.setSc(sc);
            taskBuilder.setCategoryId(form.getCategory());
            taskBuilder.setShortname(form.getShortname());
            taskBuilder.setName(form.getName());
            taskBuilder.setDescription(description);
            taskBuilder.setBudget(budget);
            taskBuilder.setDeadline(deadline);
            taskBuilder.setPriorityId(form.getPriority());
            taskBuilder.setParentId(parent);
            taskBuilder.setHandlerId(handlerUser);
            taskBuilder.setHandlerUserId(handlerUser);
            taskBuilder.setHandlerGroupId(handlerGroup);
            taskBuilder.setUdfValues(udfMap);
            taskBuilder.setStatusId(startStatusTree.getId());
            taskBuilder.setAtts(atts);
            taskBuilder.setSubmitterId(sc.getUserId());
            taskBuilder.setCopyOrMoveOpr(false);
            taskBuilder.setNeedSend(true);

            String newId = TriggerManager.getInstance().createTask(SecuredTaskTriggerBean.build(taskBuilder, TaskBuilder.Action.CREATE));

            stb = AdapterManager.getInstance().getSecuredFindAdapterManager().findTaskById(sc, newId);
            sc.setRequestAttribute(request, "tci", stb);
            form.setId(newId);
            form.setNewTask(null);

            String statusIcon = MacrosUtil.buildImageForState(new SecuredTaskBean(newId, sc).getStatus(), context + imageServlet);
            sc.setAttribute("jsEvent",
                    new ChangeEvent(ChangeEvent.EVENT_TASK_ADDED,
                            stb.getParent() != null && stb.getParent() != null ? stb.getParent().getNumber() : "1",
                            new String[]{context + imageServlet + "/icons/categories/" + stb.getCategory().getIcon()},
                            new String[]{statusIcon},
                            new String[]{"#" + stb.getNumber() + " " + HTMLEncoder.encodeTree(stb.getName())},
                            new String[]{HTMLEncoder.encodeTree(stb.getName())},
                            new String[]{"javascript:{self.top.frames[1].location = '" + context + "/task/" + stb.getNumber() + "?thisframe=true';  childs='" + stb.getChildrenCount() + "(" + total + ")'; sortorder='" + Preferences.isSortOrgerInTree(stb.getCategory().getPreferences()) + "'; hide='" + Preferences.getHiddenInTree(stb.getCategory().getPreferences()) + "';}"},
                            new String[]{stb.getId()},
                            new String[]{},
                            new String[]{},
                            stb.getParentId()));
            TimingTaskMXBeanImpl.getInstance().setFinishCreateTask(Calendar.getInstance());
            return newId;
        } else {
            String statusIcon = MacrosUtil.buildImageForState(stb.getStatus(), context + imageServlet);
            boolean changeName = !stb.getName().equals(form.getName());
            sc.setAttribute("jsEvent",
                    new ChangeEvent(ChangeEvent.EVENT_TASK_UPDATED,
                            stb.getParent() != null && stb.getParent() != null ? stb.getParent().getNumber() : "1",
                            new String[]{context + imageServlet + "/icons/categories/" + stb.getCategory().getIcon()},
                            new String[]{statusIcon},
                            new String[]{"#" + stb.getNumber() + " " +  HTMLEncoder.encodeTree(stb.getName())},
                            new String[]{HTMLEncoder.encodeTree(changeName ? form.getName() : stb.getName())},
                            new String[]{"javascript:{self.top.frames[1].location = '" + context + "/task/" + stb.getNumber() + "?thisframe=true';  childs='" + stb.getChildrenCount() + "(" + total + ")'; sortorder='" + Preferences.isSortOrgerInTree(stb.getCategory().getPreferences()) + "'; hide='" + Preferences.getHiddenInTree(stb.getCategory().getPreferences()) + "';}"},
                            new String[]{stb.getId()},
                            new String[]{},
                            new String[]{},
                            stb.getParentId()));
            ArrayList<AttachmentArray> atts = new ArrayList<AttachmentArray>();
            if (description != null && description.contains("src=\"data:image/png;base64,")) {
                description = MacrosUtil.parseImagesFromText(atts, description, stb.getId());
            }
            description = TriggerManager.getInstance().createAttachments(sc, stb.getId(), stb.getNumber(), description, atts);
        }
        TimingTaskMXBeanImpl.getInstance().setStartCreateTask(Calendar.getInstance());
        TriggerManager.getInstance().updateTask(sc, stb.getId(), form.getShortname(), form.getName(), description, budget, deadline, form.getPriority(), parent, handlerUser, handlerGroup, true, udfMap);
        TimingTaskMXBeanImpl.getInstance().setFinishCreateTask(Calendar.getInstance());
        return stb.getId();
    }
}