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
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.form.ResolutionForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.secured.SecuredMstatusBean;
import com.trackstudio.secured.SecuredResolutionBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredWorkflowBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.Tab;

public class ResolutionEditAction extends TSDispatchAction {

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            ResolutionForm rf = (ResolutionForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().taskHeader(rf, sc, request, true);

            if (!sc.canAction(Action.manageWorkflows, id)) return null;

            String resolutionId = request.getParameter("resolution");
            boolean createNewResolution = resolutionId == null || resolutionId.length() == 0;

            SecuredTaskBean tci = new SecuredTaskBean(id, sc);

            String workflowId = rf.getWorkflowId() != null ? rf.getWorkflowId() : tci.getWorkflowId();
            SecuredWorkflowBean flow = AdapterManager.getInstance().getSecuredFindAdapterManager().findWorkflowById(sc, workflowId);
            boolean canManage = flow.canManage();
            sc.setRequestAttribute(request, "flow", flow);

            String mstatusId = rf.getMstatusId();
            SecuredMstatusBean mst = AdapterManager.getInstance().getSecuredFindAdapterManager().findMstatusById(sc, mstatusId);
            sc.setRequestAttribute(request, "mstatus", mst);

            List<SecuredResolutionBean> resolutionList = AdapterManager.getInstance().getSecuredWorkflowAdapterManager().getResolutionList(sc, mst.getId());

            boolean def = false;
            if (!createNewResolution) {
                SecuredResolutionBean resolution = AdapterManager.getInstance().getSecuredFindAdapterManager().findResolutionById(sc, resolutionId);
                def = resolution.isDefault();
                rf.setResolutionId(resolutionId);
                rf.setName(resolution.getName());
                rf.setDef(resolution.isDefault());
            }

            sc.setRequestAttribute(request, "checked", resolutionList.isEmpty() || def);

            boolean canView = canManage;
            boolean canCreate = canManage;

            sc.setRequestAttribute(request, "canView", canView);
            sc.setRequestAttribute(request, "canCreate", canCreate);
            sc.setRequestAttribute(request, "canManage", canManage);
            sc.setRequestAttribute(request, "tabView", new Tab(canManage, false));
            sc.setRequestAttribute(request, "tabEdit", new Tab(canManage, false));
            sc.setRequestAttribute(request, "tabPriorities", new Tab(canManage, true));
            sc.setRequestAttribute(request, "tabStates", new Tab(canManage, false));
            sc.setRequestAttribute(request, "tabMessageTypes", new Tab(canManage, false));
            sc.setRequestAttribute(request, "tabCustomize", new Tab(canManage, false));
            selectTaskTab(sc, id, "tabWorkflows", request);
            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_TASK_WORKFLOW_RESOLUTION_LIST);
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_TASK_WORKFLOW_MSTATUS_RESOLUTION_PROPERTIES));
            return mapping.findForward("resolutionEditJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            ResolutionForm rf = (ResolutionForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String resolutionName = rf.getName();
            String resolutionId = rf.getResolutionId();
            boolean resolutionDefault = rf.getDef();
            String matatusId = rf.getMstatusId();
            if (resolutionName != null && resolutionName.length() > 0) {
                if (resolutionDefault) {
                    List<SecuredResolutionBean> resolutionList = AdapterManager.getInstance().getSecuredWorkflowAdapterManager().getResolutionList(sc, matatusId);
                    for (SecuredResolutionBean res : resolutionList) {
                        AdapterManager.getInstance().getSecuredWorkflowAdapterManager().updateResolution(sc, res.getId(), res.getName(), false);
                    }
                }
                if (resolutionId != null && resolutionId.length() > 0) {
                    AdapterManager.getInstance().getSecuredWorkflowAdapterManager().updateResolution(sc, resolutionId, resolutionName, resolutionDefault);
                } else {
                    AdapterManager.getInstance().getSecuredWorkflowAdapterManager().createResolution(sc, rf.getMstatusId(), resolutionName, resolutionDefault);
                }
            }
            log.debug("CLOSE SESSION ResolutionAction");
            //        HibernateSession.closeSession();
            return mapping.findForward("resolutionPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }
}
