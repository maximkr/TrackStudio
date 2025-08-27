package com.trackstudio.action.task;

import java.util.ArrayList;
import java.util.Collections;
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
import com.trackstudio.app.Defaults;
import com.trackstudio.app.PageNaming;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.form.ResolutionForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.secured.SecuredMstatusBean;
import com.trackstudio.secured.SecuredResolutionBean;
import com.trackstudio.secured.SecuredWorkflowBean;
import com.trackstudio.secured.SecuredWorkflowUDFBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.Tab;

public class ResolutionAction extends TSDispatchAction {
    private static Log log = LogFactory.getLog(ResolutionAction.class);

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            ResolutionForm tf = (ResolutionForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().taskHeader(tf, sc, request, true);

            if (!sc.canAction(Action.manageWorkflows, id))
                return null;

            String mstatusId = tf.getMstatusId();
            SecuredMstatusBean mstatus = AdapterManager.getInstance().getSecuredFindAdapterManager().findMstatusById(sc, mstatusId);
            List<SecuredResolutionBean> res = AdapterManager.getInstance().getSecuredWorkflowAdapterManager().getResolutionList(sc, mstatusId);
            Collections.sort(res);

            if (res != null && !res.isEmpty()) {
                SecuredResolutionBean defaultResolution = Defaults.getResolution(sc, mstatusId);
                if (defaultResolution != null) {
                    tf.setDefaultForRadioButton(defaultResolution.getId());
                    sc.setRequestAttribute(request, "defaultResolution", defaultResolution.getId());
                }
            }

            sc.setRequestAttribute(request, "checked", res == null || res.isEmpty());

            String workflowId = tf.getWorkflowId();
            SecuredWorkflowBean flow = AdapterManager.getInstance().getSecuredFindAdapterManager().findWorkflowById(sc, workflowId);
            List<SecuredWorkflowUDFBean> udfs = flow.getWorkflowUDFs();
            boolean flowAllows = flow.canManage();
            sc.setRequestAttribute(request, "flow", flow);
            sc.setRequestAttribute(request, "canEdit", flowAllows);
            sc.setRequestAttribute(request, "canDelete", flowAllows);
            sc.setRequestAttribute(request, "canView", flowAllows);
            sc.setRequestAttribute(request, "canManage", flowAllows);
            sc.setRequestAttribute(request, "resolutionList", res);
            sc.setRequestAttribute(request, "mstatus", mstatus);
            sc.setRequestAttribute(request, "canViewMstatusList", flow.canManage() && sc.canAction(Action.manageWorkflows, flow.getTaskId()));
            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_TASK_WORKFLOW_RESOLUTION_LIST);
            sc.setRequestAttribute(request, "tabView", new Tab(sc.canAction(Action.manageWorkflows, mstatus.getWorkflow().getTaskId()), false));
            sc.setRequestAttribute(request, "tabEdit", new Tab(flow.canManage() && sc.canAction(Action.manageWorkflows, mstatus.getWorkflow().getTaskId()), false));
            sc.setRequestAttribute(request, "tabResolutions", new Tab(flow.canManage() && sc.canAction(Action.manageWorkflows, mstatus.getWorkflow().getTaskId()), true));
            sc.setRequestAttribute(request, "tabTransitions", new Tab(flow.canManage() && sc.canAction(Action.manageWorkflows, mstatus.getWorkflow().getTaskId()), false));
            sc.setRequestAttribute(request, "tabPermissions", new Tab(sc.canAction(Action.manageWorkflows, mstatus.getWorkflow().getTaskId()), false));
            sc.setRequestAttribute(request, "tabTriggers", new Tab(flow.canManage() && sc.canAction(Action.manageWorkflows, mstatus.getWorkflow().getTaskId()), false));
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_TASK_WORKFLOW_RESOLUTION_LIST));
            sc.setRequestAttribute(request, "tabScheduler", new Tab(flowAllows, false));
	        sc.setRequestAttribute(request, "tabUdfPermissions", new Tab(udfs.size() > 0 && flow.canView() && sc.canAction(Action.manageWorkflows, flow.getTaskId()), false));

	        return mapping.findForward("resolutionJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.trace("##########");
        ResolutionForm rf = (ResolutionForm) form;
        SessionContext sc = (SessionContext) request.getAttribute("sc");
        String defaultRes = rf.getDefaultForRadioButton();
        SecuredMstatusBean mstatus = AdapterManager.getInstance().getSecuredFindAdapterManager().findMstatusById(sc, rf.getMstatusId());
        if (sc.canAction(Action.manageWorkflows, mstatus.getWorkflow().getTaskId())) {
            ArrayList<SecuredResolutionBean> res = AdapterManager.getInstance().getSecuredWorkflowAdapterManager().getResolutionList(sc, mstatus.getId());
            if (res != null && !res.isEmpty())
                for (SecuredResolutionBean r : res) {
                    AdapterManager.getInstance().getSecuredWorkflowAdapterManager().updateResolution(sc, r.getId(), r.getName(), defaultRes.equals(r.getId()));
                }
        }
        return mapping.findForward("resolutionPage");
    }

    public ActionForward delete(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                HttpServletResponse response) throws Exception {
        log.trace("##########");
        ResolutionForm mf = (ResolutionForm) form;
        SessionContext sc = (SessionContext) request.getAttribute("sc");
        SecuredWorkflowBean flow = AdapterManager.getInstance().getSecuredFindAdapterManager().findWorkflowById(sc, mf.getWorkflowId());
        if (sc.canAction(Action.manageWorkflows, flow.getTaskId())) {
            String[] values2 = mf.getDelete();
            if (values2 != null)
                for (String aValues2 : values2)
                    AdapterManager.getInstance().getSecuredWorkflowAdapterManager().deleteResolution(sc, aValues2);
        }
        return mapping.findForward("resolutionPage");
    }

    public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                HttpServletResponse response) throws Exception {
        log.trace("##########");
        return mapping.findForward("resolutionEditPage");
    }

    public ActionForward clone(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.trace("##########");
        ResolutionForm mf = (ResolutionForm) form;
        SessionContext sc = (SessionContext) request.getAttribute("sc");
        String[] values = mf.getDelete();
        if (values != null) {
            for (String id : values) {
                SecuredResolutionBean resolution = AdapterManager.getInstance().getSecuredFindAdapterManager().findResolutionById(sc, id);
                AdapterManager.getInstance().getSecuredWorkflowAdapterManager().createResolution(sc, resolution.getMstatusId(), resolution.getName()+"_clone", false);
            }
        }
        return mapping.findForward("resolutionPage");
    }
}
