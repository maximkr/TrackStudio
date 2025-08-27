package com.trackstudio.action.task;

import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.trackstudio.action.CustomEditAction;
import com.trackstudio.action.GeneralAction;
import com.trackstudio.action.TSDispatchAction;
import com.trackstudio.app.PageNaming;
import com.trackstudio.app.UdfValue;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.form.CustomForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.secured.SecuredUDFBean;
import com.trackstudio.secured.SecuredWorkflowBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.Tab;

public class WorkflowUdfListValuesAction extends TSDispatchAction {
    public ActionForward page(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            CustomForm form = (CustomForm) actionForm;
            SessionContext sc = GeneralAction.getInstance().imports(request, response);
            if (sc == null)
                return null;
            String id = GeneralAction.getInstance().taskHeader(form, sc, request, true);
            CustomEditAction.fillListValuesForm(sc, "1", form, request);
            sc.setRequestAttribute(request, "hideUDFIcon", true);
            sc.setRequestAttribute(request, "editUdfAction", "/WorkflowUdfListValuesAction.do");
            sc.setRequestAttribute(request, "_can_view", sc.canAction(Action.manageWorkflows, id));
            SecuredUDFBean udf = (SecuredUDFBean) request.getAttribute("udf");
            boolean canEdit = (udf == null || udf.canManage()) && sc.canAction(Action.manageWorkflows, id) && (udf == null || sc.canAction(Action.manageWorkflows, KernelManager.getFind().findUdfsource(udf.getUdfSourceId()).getWorkflow().getTask().getId()));
            if (udf != null)
                sc.setRequestAttribute(request, "_can_modify", canEdit);
            else
                sc.setRequestAttribute(request, "_can_modify", Boolean.TRUE);
            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_TASK_WORKFLOW_CUSTOM_FIELD_VALUES_LIST);
            selectTaskTab(sc, id, "tabWorkflows", request);
            sc.setRequestAttribute(request, "tabView", new Tab(sc.canAction(Action.manageWorkflows, id) && sc.allowedByACL(id), false));
            sc.setRequestAttribute(request, "tabListValues", new Tab(udf != null && sc.canAction(Action.manageWorkflows, id) && sc.allowedByACL(id) && (udf.getType() == UdfValue.LIST || udf.getType() == UdfValue.MULTILIST), true));
            sc.setRequestAttribute(request, "tabEdit", new Tab(canEdit && udf != null, false));
            sc.setRequestAttribute(request, "tabPermission", new Tab(sc.canAction(Action.manageWorkflows, id) && request.getAttribute("udf") != null && sc.allowedByACL(id), false));
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_TASK_WORKFLOW_CUSTOM_FIELD_VALUES_LIST));
            sc.setRequestAttribute(request, "workflowId", form.getWorkflowId());
            SecuredWorkflowBean flow = AdapterManager.getInstance().getSecuredFindAdapterManager().findWorkflowById(sc, form.getWorkflowId());
            sc.setRequestAttribute(request, "flow", flow);
            sc.setRequestAttribute(request, "canManage", flow.canManage());
            return actionMapping.findForward("workflowUdfListValuesJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward save(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            CustomForm form = (CustomForm) actionForm;
            SessionContext sc = GeneralAction.getInstance().imports(request, response);
            if (sc == null)
                return null;
            String flowId = form.getWorkflowId();
            SecuredUDFBean udf = AdapterManager.getInstance().getSecuredFindAdapterManager().findUDFById(sc, form.getUdfId());
            if (udf.getType() == UdfValue.LIST || udf.getType() == UdfValue.MULTILIST) {
                if (udf.getUL() != null)
                    for (Object o : udf.getUL().keySet()) {
                        String ulist = (String) o;
                        AdapterManager.getInstance().getSecuredUDFAdapterManager().updateWorkflowUdflist(sc, ulist, form.getLists(ulist));
                    }
                if (form.getAddlist() != null && form.getAddlist().length() > 0) {
                    StringTokenizer tk = new StringTokenizer(form.getAddlist(), "\r\n");
                    while (tk.hasMoreTokens()) {
                        String token = tk.nextToken().trim();
                        if (token.length() > 0)
                            AdapterManager.getInstance().getSecuredUDFAdapterManager().addWorkflowUdflist(sc, flowId, udf.getId(), token);
                    }
                }
            }
            return actionMapping.findForward("workflowUdfListValuesPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward delete(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            CustomForm form = (CustomForm) actionForm;
            SessionContext sc = GeneralAction.getInstance().imports(request, response);
            if (sc == null)
                return null;
            SecuredUDFBean udf = AdapterManager.getInstance().getSecuredFindAdapterManager().findUDFById(sc, form.getUdfId());
            for (Object o : udf.getUL().keySet()) {
                String key = o.toString();
                if (form.getValue(key) != null)
                    AdapterManager.getInstance().getSecuredUDFAdapterManager().deleteWorkflowUdflist(sc, form.getWorkflowId(), key);
            }
            return actionMapping.findForward("workflowUdfListValuesPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }
}
