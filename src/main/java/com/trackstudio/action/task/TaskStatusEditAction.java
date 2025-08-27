package com.trackstudio.action.task;

import java.util.List;

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
import com.trackstudio.constants.StateConstants;
import com.trackstudio.form.TaskStatusForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.secured.SecuredStatusBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredWorkflowBean;
import com.trackstudio.startup.I18n;

public class TaskStatusEditAction extends TSDispatchAction {
    private static Log log = LogFactory.getLog(TaskStatusAction.class);

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            TaskStatusForm stateForm = (TaskStatusForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().taskHeader(stateForm, sc, request, true);

            if (!sc.canAction(Action.manageWorkflows, id)) return null;

            SecuredTaskBean tci = new SecuredTaskBean(id, sc);
            String workflowId = stateForm.getWorkflowId() != null ? stateForm.getWorkflowId() : tci.getWorkflowId();

            String stateId = request.getParameter("state");
            boolean createNewState = stateId == null || stateId.length() == 0;
            String stateColor = "";

            SecuredWorkflowBean flow = AdapterManager.getInstance().getSecuredFindAdapterManager().findWorkflowById(sc, workflowId);
            boolean flowAllows = flow.canManage();

            boolean canManage = flow.canManage();

            List<SecuredStatusBean> stateList = AdapterManager.getInstance().getSecuredWorkflowAdapterManager().getStateList(sc, workflowId);

            boolean start = false;
            if (!createNewState) {
                SecuredStatusBean ssb = AdapterManager.getInstance().getSecuredFindAdapterManager().findStatusById(sc, stateId);
                stateForm.setStateId(ssb.getId());
                stateForm.setName(ssb.getName());
                stateForm.setColor(ssb.getColor());
                stateColor = ssb.getColor();
                stateForm.setStart(ssb.isStart());
                start = ssb.isStart();
                stateForm.setFinish(ssb.isFinish());
            }
            sc.setRequestAttribute(request, "newlist", !createNewState);
            sc.setRequestAttribute(request, "checked", stateList.isEmpty() || start);

            boolean canCreate = sc.canAction(Action.manageWorkflows, flow.getTaskId()) && flowAllows && sc.canAction(Action.manageWorkflows, id);

            sc.setRequestAttribute(request, "canManage", canManage);
            sc.setRequestAttribute(request, "flow", flow);
            sc.setRequestAttribute(request, "canCreate", canCreate);
            sc.setRequestAttribute(request, "color", createNewState ? StateConstants.DEFAULT_COLOR : stateColor);
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_TASK_WORKFLOW_STATE_PROPERTIES));
            return mapping.findForward("taskStatusEditJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward delete(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            TaskStatusForm pf = (TaskStatusForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String[] values = pf.getDelete();
            if (values != null)
                for (String value : values)
                    AdapterManager.getInstance().getSecuredWorkflowAdapterManager().deleteState(sc, value);
            return mapping.findForward("taskStatusPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            TaskStatusForm pf = (TaskStatusForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            boolean startState = pf.getStart();
            String stateName = pf.getName();
            String stateColor = pf.getColor();
            String workflowId = pf.getWorkflowId();
            if (stateName != null && stateName.length() > 0) {
                boolean finishState = pf.getFinish();
                boolean st = startState;
                boolean foundStart = false;
                for (SecuredStatusBean status : AdapterManager.getInstance().getSecuredWorkflowAdapterManager().getStateList(sc, workflowId)) {
                    foundStart = foundStart ? foundStart : status.isStart();
                    if (st)
                        AdapterManager.getInstance().getSecuredWorkflowAdapterManager().updateState(sc, status.getId(), status.getName(), false, status.isFinish(), status.getColor());
                }
                st = st ? st : !foundStart;
                AdapterManager.getInstance().getSecuredWorkflowAdapterManager().createState(sc, stateName, st, finishState, workflowId, stateColor);
            }
        } finally {
            if (w) lockManager.releaseConnection();
        }

        return mapping.findForward("taskStatusPage");
    }

    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            TaskStatusForm taskStatusForm = (TaskStatusForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");

            String stateName = taskStatusForm.getName();
            boolean stateStart = taskStatusForm.getStart();
            boolean stateFinish = taskStatusForm.getFinish();
            String stateColor = taskStatusForm.getColor();
            String stateId = taskStatusForm.getStateId();
            String workflowId = taskStatusForm.getWorkflowId();
            for (String name : stateName.split("\n")) {
                if (name != null && !name.isEmpty()) {
                    if (name.endsWith("\r")) {
                        name = name.substring(0, name.lastIndexOf("\r"));
                    }
                    if (stateStart) {
                        List<SecuredStatusBean> list = AdapterManager.getInstance().getSecuredWorkflowAdapterManager().getStateList(sc, workflowId);
                        for (SecuredStatusBean ssb : list) {
                            AdapterManager.getInstance().getSecuredWorkflowAdapterManager().updateState(sc, ssb.getId(), ssb.getName(), false, ssb.isFinish(), ssb.getColor());
                        }
                    }
                    if (stateId != null && stateId.length() > 0) {
                        AdapterManager.getInstance().getSecuredWorkflowAdapterManager().updateState(sc, stateId, name, stateStart, stateFinish, stateColor);
                    } else {
                        AdapterManager.getInstance().getSecuredWorkflowAdapterManager().createState(sc, name, stateStart, stateFinish, workflowId, "#FFFFFF");
                    }
                }
            }
            return mapping.findForward("taskStatusPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }
}

