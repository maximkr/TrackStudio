package com.trackstudio.action.user;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.trackstudio.action.GeneralAction;
import com.trackstudio.action.TSDispatchAction;
import com.trackstudio.app.Preferences;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.form.PrstatusForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.secured.SecuredPrstatusBean;
import com.trackstudio.tools.Tab;

public class UserStatusEditAction extends TSDispatchAction {
    private static Log log = LogFactory.getLog(UserStatusEditAction.class);

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            PrstatusForm pf = (PrstatusForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().userHeader(pf, sc, request);
            if (!sc.canAction(Action.manageRoles, id))
                return null;
            String prstatusId = pf.getPrstatusId();

            boolean canEdit;

            boolean createNewStatus = prstatusId == null || prstatusId.length() == 0;
            SecuredPrstatusBean prstatus = AdapterManager.getInstance().getSecuredFindAdapterManager().findPrstatusById(sc, prstatusId);
            if (createNewStatus) {
                canEdit = true;
                sc.setRequestAttribute(request, "owner", sc.getUser());
                sc.setRequestAttribute(request, "tabView", new Tab(false, false));
                sc.setRequestAttribute(request, "tabEdit", new Tab(sc.canAction(Action.manageWorkflows, id), true));
                sc.setRequestAttribute(request, "tabTaskFieldSecurity", new Tab(false, false));
                sc.setRequestAttribute(request, "tabUserFieldSecurity", new Tab(false, false));
                sc.setRequestAttribute(request, "tabTaskSecurity", new Tab(false, false));
                sc.setRequestAttribute(request, "tabUserSecurity", new Tab(false, false));
                sc.setRequestAttribute(request, "tabCategorySecurity", new Tab(false, false));

                sc.setRequestAttribute(request, "createNewStatus", Boolean.TRUE);
                sc.setRequestAttribute(request, "canManage", sc.canAction(Action.manageRoles, id));
            } else {
                pf.setName(prstatus.getName());
                pf.setShowInToolbar(Preferences.showInToolbar(prstatus.getPreferences()));
                canEdit = prstatus.canManage();

                sc.setRequestAttribute(request, "owner", prstatus.getUser());
                sc.setRequestAttribute(request, "currentPrstatus", prstatus);
                sc.setRequestAttribute(request, "name", prstatus.getName());
                sc.setRequestAttribute(request, "prstatusId", prstatusId);

                sc.setRequestAttribute(request, "canManage", canEdit);
                sc.setRequestAttribute(request, "tabWorkflowSecurity", new Tab(canEdit, false));
                sc.setRequestAttribute(request, "tabEdit", new Tab(canEdit, true));
                sc.setRequestAttribute(request, "tabView", new Tab(canEdit, false));
                sc.setRequestAttribute(request, "tabTaskFieldSecurity", new Tab(canEdit, false));
                sc.setRequestAttribute(request, "tabUserFieldSecurity", new Tab(canEdit, false));
                sc.setRequestAttribute(request, "tabUserSecurity", new Tab(canEdit, false));
                sc.setRequestAttribute(request, "tabTaskSecurity", new Tab(canEdit, false));
                sc.setRequestAttribute(request, "tabCategorySecurity", new Tab(canEdit, false));
            }
            return mapping.findForward("userStatusEditJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward editUserStatus(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            PrstatusForm pf = (PrstatusForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String prstatusId = pf.getPrstatusId();
            boolean nameOK = pf.getName() != null && pf.getName().trim().length() != 0;
            if (nameOK) {
                if (prstatusId == null || prstatusId.length() == 0)
                    prstatusId = AdapterManager.getInstance().getSecuredPrstatusAdapterManager().createPrstatus(sc, pf.getId(), pf.getName());
                SecuredPrstatusBean prstatus = AdapterManager.getInstance().getSecuredFindAdapterManager().findPrstatusById(sc, prstatusId);
                Preferences preferences = new Preferences(prstatus.getPreferences());
                preferences.setShowInToolbar(pf.getShowInToolbar());
                AdapterManager.getInstance().getSecuredPrstatusAdapterManager().updatePrstatus(sc, prstatusId, pf.getName(), preferences.getPreferences());
            }
            pf.setPrstatusId(prstatusId);
            pf.setMutable(false);
            return mapping.findForward("userStatusViewPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }
}