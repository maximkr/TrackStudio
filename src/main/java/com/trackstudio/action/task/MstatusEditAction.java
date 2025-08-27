package com.trackstudio.action.task;

import java.util.List;

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
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.form.MstatusForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.secured.SecuredMstatusBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredWorkflowBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.Tab;

public class MstatusEditAction extends TSDispatchAction {

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
            boolean createNewMstatus = mstatusId == null || mstatusId.length() == 0;
            String wfId = tf.getWorkflowId() == null ? tci.getWorkflowId() : tf.getWorkflowId();
            SecuredWorkflowBean flow = AdapterManager.getInstance().getSecuredFindAdapterManager().findWorkflowById(sc, wfId);
            boolean canManage = flow.canManage();
            if (createNewMstatus) {
                sc.setRequestAttribute(request, "createNewMstatus", "true");
                sc.setRequestAttribute(request, "tabView", new Tab(false, false));
                sc.setRequestAttribute(request, "tabEdit", new Tab(true, true));
                sc.setRequestAttribute(request, "tabResolutions", new Tab(false, false));
                sc.setRequestAttribute(request, "tabTransitions", new Tab(false, false));
                sc.setRequestAttribute(request, "tabPermissions", new Tab(false, false));
                sc.setRequestAttribute(request, "tabTriggers", new Tab(false, false));
            } else {
                SecuredMstatusBean mstatus = AdapterManager.getInstance().getSecuredFindAdapterManager().findMstatusById(sc, mstatusId);
                tf.setName(mstatus.getName());
                tf.setDescription(mstatus.getDescription());
                tf.setAction(mstatus.getAction());
                tf.setShowInToolbar(Preferences.showInToolbar(mstatus.getPreferences()));
                sc.setRequestAttribute(request, "mstatus", mstatus);
                sc.setRequestAttribute(request, "mstatusId", mstatusId);
                sc.setRequestAttribute(request, "tabView", new Tab(canManage, false));
                sc.setRequestAttribute(request, "tabEdit", new Tab(canManage, true));
                sc.setRequestAttribute(request, "tabResolutions", new Tab(canManage, false));
                sc.setRequestAttribute(request, "tabTransitions", new Tab(canManage, false));
                sc.setRequestAttribute(request, "tabPermissions", new Tab(canManage, false));
                sc.setRequestAttribute(request, "tabTriggers", new Tab(flow.canManage() && sc.canAction(Action.manageWorkflows, mstatus.getWorkflow().getTaskId()), false));
                sc.setRequestAttribute(request, "tabUdfPermissions", new Tab(canManage, false));
                sc.setRequestAttribute(request, "tabScheduler", new Tab(canManage, false));
            }
            sc.setRequestAttribute(request, "workflowId", wfId);
            sc.setRequestAttribute(request, "canViewMstatusList", canManage);
            sc.setRequestAttribute(request, "flow", flow);
            sc.setRequestAttribute(request, "canEdit", canManage);
            sc.setRequestAttribute(request, "canManage", canManage);


            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_TASK_WORKFLOW_MSTATUS_PROPERTIES);
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_TASK_WORKFLOW_MSTATUS_PROPERTIES));
            return mapping.findForward("mstatusEditJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

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
            if (mStatusForm.getName() != null && mStatusForm.getName().trim().length() != 0) {
                SecuredMstatusBean mstatus = AdapterManager.getInstance().getSecuredFindAdapterManager().findMstatusById(sc, mstatusId);
                Preferences preferences = new Preferences(mstatus.getPreferences());
                preferences.setShowInToolbar(mStatusForm.getShowInToolbar());
                AdapterManager.getInstance().getSecuredWorkflowAdapterManager().updateMstatus(sc, mstatusId, mStatusForm.getName(), mStatusForm.getDescription(), preferences.getPreferences(), mStatusForm.getAction());
                if (mStatusForm.isDef()) {
                    List<SecuredMstatusBean> mstatusList = AdapterManager.getInstance().getSecuredWorkflowAdapterManager().getMstatusList(sc, mStatusForm.getWorkflowId());
                    for (SecuredMstatusBean mst : mstatusList) {
                        if (!mst.getId().equals(mstatusId))
                            AdapterManager.getInstance().getSecuredWorkflowAdapterManager().updateMstatus(sc, mst.getId(), mst.getName(), mst.getDescription(), mst.getPreferences(), mst.getAction());
                    }
                }
            }
            mStatusForm.setMstatusId(mstatusId);
            mStatusForm.setMutable(false);
            return mapping.findForward("mstatusViewPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }
}
