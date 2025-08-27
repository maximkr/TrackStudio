package com.trackstudio.action.task;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.trackstudio.action.GeneralAction;
import com.trackstudio.action.TSDispatchAction;
import com.trackstudio.app.PageNaming;
import com.trackstudio.app.Preferences;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.adapter.scheduler.SchedulerManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.external.ICategoryScheduler;
import com.trackstudio.form.MstatusForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.secured.SecuredCategoryBean;
import com.trackstudio.secured.SecuredMstatusBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredWorkflowBean;
import com.trackstudio.secured.SecuredWorkflowUDFBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.Tab;

public class MstatusSchedulerAction  extends TSDispatchAction {

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            MstatusForm tf = (MstatusForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().taskHeader(tf, sc, request, true);
            SecuredTaskBean tci = new SecuredTaskBean(id, sc);
            String mstatusId = tf.getMstatusId();
            String wfId = tf.getWorkflowId() == null ? tci.getWorkflowId() : tf.getWorkflowId();
            SecuredWorkflowBean flow = AdapterManager.getInstance().getSecuredFindAdapterManager().findWorkflowById(sc, wfId);
	        List<SecuredWorkflowUDFBean> udfs = flow.getWorkflowUDFs();
	        boolean canManage = flow.canManage();
            SecuredMstatusBean mstatus = AdapterManager.getInstance().getSecuredFindAdapterManager().findMstatusById(sc, mstatusId);
            tf.setName(mstatus.getName());
            tf.setDescription(mstatus.getDescription());
            tf.setAction(mstatus.getAction());
            tf.setScheduler(Preferences.scheduler(mstatus.getPreferences()));
            sc.setRequestAttribute(request, "mstatus", mstatus);
            sc.setRequestAttribute(request, "mstatusId", mstatusId);
            sc.setRequestAttribute(request, "tabView", new Tab(canManage, false));
            sc.setRequestAttribute(request, "tabEdit", new Tab(canManage, false));
            sc.setRequestAttribute(request, "tabResolutions", new Tab(canManage, false));
            sc.setRequestAttribute(request, "tabTransitions", new Tab(canManage, false));
            sc.setRequestAttribute(request, "tabPermissions", new Tab(canManage, false));
            sc.setRequestAttribute(request, "tabTriggers", new Tab(flow.canManage() && sc.canAction(com.trackstudio.kernel.cache.Action.manageWorkflows, mstatus.getWorkflow().getTaskId()), false));
            sc.setRequestAttribute(request, "tabUdfPermissions", new Tab(canManage, false));
            sc.setRequestAttribute(request, "tabScheduler", new Tab(canManage, true));
            sc.setRequestAttribute(request, "workflowId", wfId);
            sc.setRequestAttribute(request, "canViewMstatusList", canManage);
            sc.setRequestAttribute(request, "flow", flow);
            sc.setRequestAttribute(request, "canEdit", canManage);
            sc.setRequestAttribute(request, "canManage", canManage);
            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_TASK_WORKFLOW_MSTATUS_PROPERTIES);

            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_TASK_WORKFLOW_MSTATUS_PROPERTIES);
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_TASK_WORKFLOW_MSTATUS_PROPERTIES));
            sc.setRequestAttribute(request, "categorySchedulers", this.getSchedulerCategoryByWorkflowId(sc, wfId));
	        sc.setRequestAttribute(request, "tabUdfPermissions", new Tab(udfs.size() > 0 && flow.canView() && sc.canAction(Action.manageWorkflows, flow.getTaskId()), false));

	        return mapping.findForward("mstatusSchedulerJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    private Map<SecuredCategoryBean, ICategoryScheduler> getSchedulerCategoryByWorkflowId(SessionContext sc, String workflowId) throws GranException {
        Map<SecuredCategoryBean, ICategoryScheduler> map = new LinkedHashMap<SecuredCategoryBean, ICategoryScheduler>();
        for (SecuredCategoryBean bean : AdapterManager.getInstance().getSecuredWorkflowAdapterManager().getCategoryList(sc, workflowId)) {
            ICategoryScheduler categoryScheduler = SchedulerManager.getInstance().reloadCategoryScheduler(bean.getId());
            if (categoryScheduler != null) {
                map.put(bean, categoryScheduler);
            }
        }
        return map;
    }

    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            MstatusForm mStatusForm = (MstatusForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String mstatusId = mStatusForm.getMstatusId();
            if (mstatusId == null || mstatusId.equals("null") || mstatusId.length() == 0) {
                if (mStatusForm.getName() != null && mStatusForm.getName().trim().length() != 0)
                    mstatusId = AdapterManager.getInstance().getSecuredWorkflowAdapterManager().createMstatus(sc, mStatusForm.getWorkflowId(), mStatusForm.getName(), mStatusForm.getDescription(), mStatusForm.isShowInToolbar() ? "T" : "");
                else
                    return mapping.findForward("mstatusPage");
            }
            SecuredMstatusBean mstatus = AdapterManager.getInstance().getSecuredFindAdapterManager().findMstatusById(sc, mstatusId);
            Preferences preferences = new Preferences(mstatus.getPreferences());
            preferences.setScheduler(mStatusForm.getScheduler());
            AdapterManager.getInstance().getSecuredWorkflowAdapterManager().updateMstatus(sc, mstatusId, mstatus.getName(), mstatus.getDescription(), preferences.getPreferences(), mstatus.getAction());
            mStatusForm.setMstatusId(mstatusId);
            mStatusForm.setMutable(false);
            return mapping.findForward("mstatusSchedulerPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }
    }
}
