package com.trackstudio.action.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.trackstudio.action.GeneralAction;
import com.trackstudio.action.TSDispatchAction;
import com.trackstudio.app.PageNaming;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.UserException;
import com.trackstudio.form.TaskStatusForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.cache.TaskRelatedInfo;
import com.trackstudio.secured.SecuredStatusBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredWorkflowBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.Tab;
import com.trackstudio.view.StateViewHTML;

public class TaskStatusAction extends TSDispatchAction {

    private static Log log = LogFactory.getLog(TaskStatusAction.class);

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            TaskStatusForm tsf = (TaskStatusForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().taskHeader(tsf, sc, request, true);
            if (!sc.canAction(Action.manageWorkflows, id))
                return null;
            SecuredTaskBean tci = new SecuredTaskBean(id, sc);

            String workflowId = tsf.getWorkflowId() != null ? tsf.getWorkflowId() : tci.getWorkflowId();
            SecuredWorkflowBean flow = AdapterManager.getInstance().getSecuredFindAdapterManager().findWorkflowById(sc, workflowId);
            boolean flowAllows = flow.canManage();
            boolean canModify = sc.canAction(Action.manageWorkflows, flow.getTaskId()) && flowAllows && sc.canAction(Action.manageWorkflows, id);
            boolean canCreate = sc.canAction(Action.manageWorkflows, flow.getTaskId()) && flowAllows && sc.canAction(Action.manageWorkflows, id);
            boolean canDelete = sc.canAction(Action.manageWorkflows, flow.getTaskId()) && flowAllows && sc.canAction(Action.manageWorkflows, id);
            boolean canManage = flow.canManage();

            List<SecuredStatusBean> statusList = AdapterManager.getInstance().getSecuredWorkflowAdapterManager().getStateList(sc, flow.getId());
            Map<String, String> stateViewMap = new HashMap<String, String>();
            String lastStateId;
            for (SecuredStatusBean ssb : statusList) {
                lastStateId = ssb.getId();
                if (!canModify)
                    stateViewMap.put(lastStateId, new StateViewHTML(ssb, request.getContextPath()).getColorPictureOnly());
                if (ssb.isFinish())
                    tsf.setValue("finish-" + lastStateId, "true");
                if (ssb.isStart() || ssb.isSecondaryStart()) {
                    tsf.setValue("start-" + lastStateId, "true");
                }
            }
            Collections.sort(statusList);
            sc.setRequestAttribute(request, "createObjectAction", "/TaskStatusAction.do");
            sc.setRequestAttribute(request, "flow", flow);
            sc.setRequestAttribute(request, "canCreate", canCreate);
            sc.setRequestAttribute(request, "canDelete", canDelete);
            sc.setRequestAttribute(request, "canManage", canManage);
            sc.setRequestAttribute(request, "stateViewMap", stateViewMap);
            sc.setRequestAttribute(request, "statusList", statusList);
            sc.setRequestAttribute(request, "tabView", new Tab(flow.canView() && sc.canAction(Action.manageWorkflows, id), false));
            sc.setRequestAttribute(request, "tabEdit", new Tab(flow.canManage() && sc.canAction(Action.manageWorkflows, flow.getTaskId()), false));
            sc.setRequestAttribute(request, "tabPriorities", new Tab(flow.canManage() && sc.canAction(Action.manageWorkflows, flow.getTaskId()), false));
            sc.setRequestAttribute(request, "tabStates", new Tab(flow.canManage() && sc.canAction(Action.manageWorkflows, flow.getTaskId()), true));
            sc.setRequestAttribute(request, "tabMessageTypes", new Tab(flow.canView() && sc.canAction(Action.manageWorkflows, flow.getTaskId()), false));
            sc.setRequestAttribute(request, "tabCustomize", new Tab(flow.canView() && sc.canAction(Action.manageWorkflows, flow.getTaskId()), false));
            selectTaskTab(sc, id, "tabWorkflows", request);
            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_TASK_WORKFLOW_STATE_LIST);
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_TASK_WORKFLOW_STATE_LIST));

            return mapping.findForward("taskStatusJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward delete(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            TaskStatusForm taskStatusFrom = (TaskStatusForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String[] values = taskStatusFrom.getDelete();

            if (values != null) {
                List<String[]> exceptions = new ArrayList<String[]>();
                for (String statusId : values) {
                    List<TaskRelatedInfo> taskRelatedInfos = AdapterManager.getInstance().getSecuredWorkflowAdapterManager().getStateTask(sc, statusId);
                    SecuredStatusBean securedStatusBean = AdapterManager.getInstance().getSecuredFindAdapterManager().findStatusById(sc, statusId);
                    if (securedStatusBean.isStart()) {
                        throw new UserException(I18n.getString(sc, "MSG_DELETE_STATE_IS_START"), false);
                    }
                    if (taskRelatedInfos.size() > 0) {
                        String exception = "";
                        for (TaskRelatedInfo info : taskRelatedInfos) {
                            exception += "#" + info.getNumber() + "; ";
                        }
                        exceptions.add(new String[]{securedStatusBean.getName() + "(" + securedStatusBean.getWorkflow().getName() + ")", exception});
                        continue;
                    }
                    AdapterManager.getInstance().getSecuredWorkflowAdapterManager().deleteState(sc, statusId);
                }
                if (exceptions.size() > 0) {
                    UserException statusException = new UserException(I18n.getString(sc, "ERROR_CAN_NOT_DELETE_STATE"), false);
                    for (String[] message : exceptions) {
                        UserException ue = new UserException(I18n.getString(sc, "STATUS_EXCEPTION_CONSTRAINT", new Object[]{message[0], message[1]}), false);
                        statusException.addActionMessages(ue.getActionMessages());
                    }
                    throw statusException;
                }
            }
            return mapping.findForward("taskStatusPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.trace("##########");
        return mapping.findForward("taskStatusEditPage");
    }

    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            TaskStatusForm taskStatusForm = (TaskStatusForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String workflowId = taskStatusForm.getWorkflowId();

            List<SecuredStatusBean> stateList = AdapterManager.getInstance().getSecuredWorkflowAdapterManager().getStateList(sc, workflowId);
            List<TaskStatusForm> updates = new ArrayList<TaskStatusForm>();
            for (SecuredStatusBean state : stateList) {
                String startStatus = (String) taskStatusForm.getValue("start-" + state.getId());
                String finish = (String) taskStatusForm.getValue("finish-" + state.getId());
                String color = (String) taskStatusForm.getValue("color-" + state.getId());
                boolean fn = finish != null && finish.equals("on");
                boolean start = startStatus != null && startStatus.equals("on");
                TaskStatusForm statusForm = new TaskStatusForm();
                statusForm.setId(state.getId());
                statusForm.setName(state.getName());
                statusForm.setStart(start);
                statusForm.setFinish(fn);
                statusForm.setColor(color);
                updates.add(statusForm);
            }
            Collections.sort(updates, new Comparator<TaskStatusForm>() {
                @Override
                public int compare(TaskStatusForm o1, TaskStatusForm o2) {
                    return Boolean.compare(o1.getStart(), o2.getStart());
                }
            });
            for (TaskStatusForm statusForm : updates) {
                AdapterManager.getInstance().getSecuredWorkflowAdapterManager().updateState(sc, statusForm.getId(),
                        statusForm.getName(), statusForm.getStart(), statusForm.getFinish(), statusForm.getColor());
            }
            return mapping.findForward("taskStatusPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward clone(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            TaskStatusForm taskStatusFrom = (TaskStatusForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String[] values = taskStatusFrom.getDelete();

            if (values != null) {
                for (String statusId : values) {
                    SecuredStatusBean ssb = AdapterManager.getInstance().getSecuredFindAdapterManager().findStatusById(sc, statusId);
                    AdapterManager.getInstance().getSecuredWorkflowAdapterManager().createState(sc, ssb.getName() + "_clone", false, ssb.isFinish(), ssb.getWorkflowId(), ssb.getColor());
                }
            }
            return mapping.findForward("taskStatusPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

}
