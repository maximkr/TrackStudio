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
import com.trackstudio.app.PageNaming;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.form.TransitionForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.secured.SecuredMstatusBean;
import com.trackstudio.secured.SecuredStatusBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredTransitionBean;
import com.trackstudio.secured.SecuredWorkflowBean;
import com.trackstudio.secured.SecuredWorkflowUDFBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.Tab;


public class TransitionAction extends TSDispatchAction {
    private static Log log = LogFactory.getLog(TransitionAction.class);

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            TransitionForm transitionForm = (TransitionForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().taskHeader(transitionForm, sc, request, true);
            if (!sc.canAction(Action.manageWorkflows, id))
                return null;
            SecuredTaskBean tci = new SecuredTaskBean(id, sc);
            String mstatusId = transitionForm.getMstatusId();
            SecuredMstatusBean mstatus = AdapterManager.getInstance().getSecuredFindAdapterManager().findMstatusById(sc, mstatusId);
            String workflowId = transitionForm.getWorkflowId() != null ? transitionForm.getWorkflowId() : tci.getWorkflowId();
            SecuredWorkflowBean flow = AdapterManager.getInstance().getSecuredFindAdapterManager().findWorkflowById(sc, workflowId);

            List<SecuredStatusBean> ssbList = AdapterManager.getInstance().getSecuredWorkflowAdapterManager().getStateList(sc, flow.getId());
            Collections.sort(ssbList);

            List<SecuredTransitionBean> transitionList = AdapterManager.getInstance().getSecuredWorkflowAdapterManager().getTransitionList(sc, mstatus.getId());

            List<SecuredWorkflowUDFBean> udfs = flow.getWorkflowUDFs();
            sc.setRequestAttribute(request, "startStateList", getStartStatus(ssbList, transitionList));
            sc.setRequestAttribute(request, "finalStateList", ssbList);
            sc.setRequestAttribute(request, "transitionList", transitionList);
            sc.setRequestAttribute(request, "flow", flow);
            sc.setRequestAttribute(request, "mstatus", mstatus);

            selectTaskTab(sc, id, "tabWorkflows", request);
            sc.setRequestAttribute(request, "tabView", new Tab(sc.canAction(Action.manageWorkflows, mstatus.getWorkflow().getTaskId()), false));
            sc.setRequestAttribute(request, "tabEdit", new Tab(flow.canManage() && sc.canAction(Action.manageWorkflows, mstatus.getWorkflow().getTaskId()), false));
            sc.setRequestAttribute(request, "tabResolutions", new Tab(flow.canManage() && sc.canAction(Action.manageWorkflows, mstatus.getWorkflow().getTaskId()), false));
            sc.setRequestAttribute(request, "tabTransitions", new Tab(flow.canManage() && sc.canAction(Action.manageWorkflows, mstatus.getWorkflow().getTaskId()), true));
            sc.setRequestAttribute(request, "tabPermissions", new Tab(sc.canAction(Action.manageWorkflows, mstatus.getWorkflow().getTaskId()), false));
            sc.setRequestAttribute(request, "tabTriggers", new Tab(flow.canManage() && sc.canAction(Action.manageWorkflows, mstatus.getWorkflow().getTaskId()), false));
            sc.setRequestAttribute(request, "tabUdfPermissions", new Tab(udfs.size() > 0 && flow.canView() && sc.canAction(Action.manageWorkflows, flow.getTaskId()), false));
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_TASK_WORKFLOW_TRANSITION_LIST));
            sc.setRequestAttribute(request, "canManage", flow.canManage());
            sc.setRequestAttribute(request, "tabScheduler", new Tab(flow.canManage(), false));

            return mapping.findForward("transitionJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward delete(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            TransitionForm tf = (TransitionForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            SecuredWorkflowBean flow = AdapterManager.getInstance().getSecuredFindAdapterManager().findWorkflowById(sc, tf.getWorkflowId());
            if (sc.canAction(Action.manageWorkflows, flow.getTaskId())) {
                String[] values2 = tf.getDelete();
                if (values2 != null)
                    for (String aValues2 : values2)
                        AdapterManager.getInstance().getSecuredWorkflowAdapterManager().deleteTransition(sc, aValues2);
            }

            return mapping.findForward("transitionPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    private List<SecuredStatusBean> getStartStatus(List<SecuredStatusBean> ssbList, List<SecuredTransitionBean> transitionList) {
        List<SecuredStatusBean> stateList = new ArrayList<SecuredStatusBean>();
        for (SecuredStatusBean ssb : ssbList) {
            boolean impl = false;
            for (SecuredTransitionBean stb : transitionList) {
                if (stb.getStartId().equals(ssb.getId())) {
                    impl = true;
                }
            }
            if (!impl) {
                stateList.add(ssb);
            }
        }
        return stateList;
    }

    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            SessionContext sc = GeneralAction.getInstance().imports(request, response, true);
            String wf = request.getParameter("wf");
            String ms = request.getParameter("ms");
            List<SecuredStatusBean> ssbList = AdapterManager.getInstance().getSecuredWorkflowAdapterManager().getStateList(sc, wf);
            Collections.sort(ssbList);
            List<SecuredTransitionBean> transitionList = AdapterManager.getInstance().getSecuredWorkflowAdapterManager().getTransitionList(sc, ms);
            sc.setRequestAttribute(request, "startStateList", getStartStatus(ssbList, transitionList));
            return mapping.findForward("transitionStatusJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward table(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            SessionContext sc = GeneralAction.getInstance().imports(request, response, true);
            String ms = request.getParameter("ms");
            String wf = request.getParameter("wf");
            List<SecuredTransitionBean> transitionList = AdapterManager.getInstance().getSecuredWorkflowAdapterManager().getTransitionList(sc, ms);
            request.setAttribute("transitionList", transitionList);
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_TASK_WORKFLOW_TRANSITION_LIST));
            SecuredWorkflowBean flow = AdapterManager.getInstance().getSecuredFindAdapterManager().findWorkflowById(sc, wf);
            sc.setRequestAttribute(request, "canManage", flow.canManage());
            return mapping.findForward("transitionTableJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }
}
