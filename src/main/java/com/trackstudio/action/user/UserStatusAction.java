package com.trackstudio.action.user;

import java.util.ArrayList;
import java.util.Collections;

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
import com.trackstudio.form.PrstatusForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.cache.UserRelatedManager;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.secured.SecuredPrstatusBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.EggBasket;
import com.trackstudio.tools.Tab;

public class UserStatusAction extends TSDispatchAction {
    private static Log log = LogFactory.getLog(UserStatusAction.class);
    private static final LockManager lockManager = LockManager.getInstance();


    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            PrstatusForm pf = (PrstatusForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().userHeader(pf, sc, request);
            if (!sc.canAction(Action.manageRoles, id))
                return null;
            SecuredUserBean currentUser = new SecuredUserBean(id, sc);
            String currentUserStatusId = pf.getPrstatusId() == null ? currentUser.getPrstatusId() : pf.getPrstatusId();

            ArrayList<SecuredPrstatusBean> availablePrstatusList = AdapterManager.getInstance().getSecuredPrstatusAdapterManager().getAvailablePrstatusList(sc, id);

            ArrayList<SecuredPrstatusBean> roles = new ArrayList<SecuredPrstatusBean>();
            EggBasket<SecuredUserBean, SecuredPrstatusBean> parentRoles = new EggBasket<SecuredUserBean, SecuredPrstatusBean>();
            EggBasket<SecuredUserBean, SecuredPrstatusBean> childrenRoles = new EggBasket<SecuredUserBean, SecuredPrstatusBean>();
            ArrayList<EggBasket> seeAlso = new ArrayList<EggBasket>();
            ArrayList<SecuredUserBean> parentUsers = AdapterManager.getInstance().getSecuredUserAdapterManager().getUserChain(sc, null, id);


            for (SecuredPrstatusBean pr : availablePrstatusList) {
                SecuredUserBean user = pr.getUser();
                if (user.canView() && sc.canAction(Action.manageRoles, user.getId())) {
                    if (pr.getUserId().equals(id)) {
                        roles.add(pr);
                    } else if (parentUsers.contains(pr.getUser())) {
                        parentRoles.putItem(user, pr);
                    } else {
                        childrenRoles.putItem(user, pr);
                    }
                }
            }
            Collections.sort(roles);

            if (!parentRoles.isEmpty()) seeAlso.add(parentRoles);
            if (!childrenRoles.isEmpty()) seeAlso.add(childrenRoles);
            boolean canManage = sc.canAction(Action.manageRoles, id);
            String roleAdmin = UserRelatedManager.getInstance().find("1").getPrstatusId();

            sc.setRequestAttribute(request, "roleAdmin", roleAdmin);
            sc.setRequestAttribute(request, "rolesSet", roles);
            sc.setRequestAttribute(request, "currentUserStatusId", currentUserStatusId);
            sc.setRequestAttribute(request, "seeAlso", seeAlso);
            sc.setRequestAttribute(request, "canViewList", canManage);
            sc.setRequestAttribute(request, "canCreateObject", canManage && sc.allowedByUser(id));
            sc.setRequestAttribute(request, "canCopy", canManage && sc.allowedByUser(id));
            sc.setRequestAttribute(request, "canDelete", canManage && sc.allowedByUser(id));
            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_USER_STATUS_LIST);
            sc.setRequestAttribute(request, "tabStatuses", new Tab(canManage, true));
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_USER_STATUS_LIST));
            sc.setRequestAttribute(request, "helpTile", "HELP_TILE_ADD_NEW_STATUS");
            return mapping.findForward("userStatusListJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward clone(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            PrstatusForm pf = (PrstatusForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");

            String[] selected = pf.getDelete();
            if (selected != null)
                for (String aSelected : selected)
                    AdapterManager.getInstance().getSecuredPrstatusAdapterManager().clonePrstatus(sc, pf.getId(), aSelected);
            return mapping.findForward("userStatusListPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.trace("##########");
        return mapping.findForward("userStatusEditPage");
    }


    public ActionForward delete(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.trace("##########");
        boolean w = lockManager.acquireConnection();

        try {
            PrstatusForm pf = (PrstatusForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String[] values = pf.getDelete();

            UserException uexception = null;
            if (values != null) {
                for (String value : values) {
//                    SecuredPrstatusBean spb = AdapterManager.getInstance().getSecuredFindAdapterManager().findPrstatusById(sc, value);
//                    if (sc.getUser().getPrstatusId().equals(spb.getId()))
//                        continue;
                    try {
                        AdapterManager.getInstance().getSecuredPrstatusAdapterManager().deletePrstatus(sc, value);
                    }
                    catch (UserException ue) {
                        if (uexception == null) {
                            uexception = ue;
                        } else {
                            uexception.addActionMessages(ue.getActionMessages());
                        }
                    }
                }
                if (uexception != null) throw uexception;
            }
            pf.setPrstatusId(null);
            pf.setMutable(false);
        } catch (UserException ue) {
            if (ue.getActionMessages() != null) {
                saveMessages(request, ue.getActionMessages());
            } else {
                throw ue;
            }
        } finally {
            if (w) lockManager.releaseConnection();
        }

        return mapping.findForward("userStatusListPage");
    }

}
