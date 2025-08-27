package com.trackstudio.action.task;

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
import com.trackstudio.form.WorkflowForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredWorkflowBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.Tab;
import com.trackstudio.view.TaskViewHTMLShort;

public class WorkflowEditAction extends TSDispatchAction {
    private static Log log = LogFactory.getLog(WorkflowEditAction.class);

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            WorkflowForm tf = (WorkflowForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().taskHeader(tf, sc, request, true);
            if (!sc.canAction(Action.manageWorkflows, id))
                return null;
            SecuredTaskBean tci = new SecuredTaskBean(id, sc);
            String wfId = tf.getWorkflowId();
            boolean createNewWorkflow = wfId == null || wfId.length() == 0;
            if (createNewWorkflow) {
                sc.setRequestAttribute(request, "createNewWorkflow", "true");
                sc.setRequestAttribute(request, "connected", new TaskViewHTMLShort(tci, request.getContextPath()).getView(tci).getName());
                sc.setRequestAttribute(request, "tabView", new Tab(false, false));
                sc.setRequestAttribute(request, "tabEdit", new Tab(true, true));
                sc.setRequestAttribute(request, "tabPriorities", new Tab(false, false));
                sc.setRequestAttribute(request, "tabStates", new Tab(false, false));
                sc.setRequestAttribute(request, "tabMessageTypes", new Tab(false, false));
                sc.setRequestAttribute(request, "tabCustomize", new Tab(false, false));
            } else {
                SecuredWorkflowBean flow = AdapterManager.getInstance().getSecuredFindAdapterManager().findWorkflowById(sc, wfId);
                tf.setName(flow.getName());
                boolean canEdit = flow.canManage();
                sc.setRequestAttribute(request, "connected", new TaskViewHTMLShort(flow.getTask(), request.getContextPath()).getView(flow.getTask()).getName());
                sc.setRequestAttribute(request, "workflowId", wfId);
                sc.setRequestAttribute(request, "flow", flow);

                sc.setRequestAttribute(request, "tabView", new Tab(canEdit, false));
                sc.setRequestAttribute(request, "tabEdit", new Tab(canEdit, true));
                sc.setRequestAttribute(request, "tabPriorities", new Tab(canEdit, false));
                sc.setRequestAttribute(request, "tabStates", new Tab(canEdit, false));
                sc.setRequestAttribute(request, "tabMessageTypes", new Tab(canEdit, false));
                sc.setRequestAttribute(request, "tabCustomize", new Tab(canEdit, false));
            }
            selectTaskTab(sc, id, "tabWorkflows", request);

            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_TASK_WORKFLOW_PROPERTIES);
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_TASK_WORKFLOW_PROPERTIES));
            return mapping.findForward("workflowEditJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            WorkflowForm tf = (WorkflowForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String workflowId = tf.getWorkflowId();
            if (workflowId == null || workflowId.equals("null") || workflowId.length() == 0) {
                if (tf.getName() != null && tf.getName().trim().length() != 0)
                    workflowId = AdapterManager.getInstance().getSecuredWorkflowAdapterManager().createWorkflow(sc, tf.getId(), tf.getName());
                else
                    return mapping.findForward("workflowPage");
            } else {
                if (tf.getName() != null && tf.getName().trim().length() != 0)
                    AdapterManager.getInstance().getSecuredWorkflowAdapterManager().updateWorkflowName(sc, workflowId, tf.getName());
            }
            tf.setWorkflowId(workflowId);
            tf.setMutable(false);
            return mapping.findForward("workflowViewPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }
}
