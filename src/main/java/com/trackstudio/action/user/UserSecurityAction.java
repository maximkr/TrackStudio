package com.trackstudio.action.user;

import java.util.ArrayList;
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
import com.trackstudio.form.RoleUserSecurityForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.cache.ActionCacheManager;
import com.trackstudio.secured.SecuredPrstatusBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.Tab;


public class UserSecurityAction extends TSDispatchAction {
    private static Log log = LogFactory.getLog(UserSecurityAction.class);

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            RoleUserSecurityForm pf = (RoleUserSecurityForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().userHeader(pf, sc, request);
            if (!sc.canAction(Action.manageRoles, id))
                return null;

            String prstatusId = pf.getPrstatusId();

            SecuredPrstatusBean prstatus = AdapterManager.getInstance().getSecuredFindAdapterManager().findPrstatusById(sc, prstatusId);
            pf.setName(prstatus.getName());
            boolean canEdit = prstatus.canManage();

            List<Action> choosed = ActionCacheManager.getInstance().getActions(prstatus.getId());
            List<Action> choosedCurrent = ActionCacheManager.getInstance().getActions(sc.getPrstatusId());

            pf.setEditUserHimself(choosed.contains(Action.editUserHimself));
            pf.setEditUserChildren(choosed.contains(Action.editUserChildren));
            pf.setCreateUser(choosed.contains(Action.createUser));
            pf.setDeleteUser(choosed.contains(Action.deleteUser));
            pf.setCutPasteUser(choosed.contains(Action.cutPasteUser));
            pf.setEditUserPasswordHimself(choosed.contains(Action.editUserPasswordHimself));
            pf.setEditUserChildrenPassword(choosed.contains(Action.editUserChildrenPassword));
            pf.setViewUserFilters(choosed.contains(Action.viewUserFilters));
            pf.setManageUserPrivateFilters(choosed.contains(Action.manageUserPrivateFilters));
            pf.setManageUserPublicFilters(choosed.contains(Action.manageUserPublicFilters));
            pf.setManageUserACLs(choosed.contains(Action.manageUserACLs));
            pf.setManageUserUDFs(choosed.contains(Action.manageUserUDFs));
            pf.setManageRoles(choosed.contains(Action.manageRoles));
            pf.setViewUserAttachments(choosed.contains(Action.viewUserAttachments));
            pf.setCreateUserAttachments(choosed.contains(Action.createUserAttachments));
            pf.setManageUserAttachments(choosed.contains(Action.manageUserAttachments));
            sc.setRequestAttribute(request, "currentPrstatus", prstatus);
            sc.setRequestAttribute(request, "prstatusId", prstatusId);
            sc.setRequestAttribute(request, "canView", canEdit);
            sc.setRequestAttribute(request, "canEdit", canEdit);
            sc.setRequestAttribute(request, "tabEdit", new Tab(canEdit, false));
            sc.setRequestAttribute(request, "tabTaskFieldSecurity", new Tab(canEdit, false));
            sc.setRequestAttribute(request, "tabUserFieldSecurity", new Tab(canEdit, false));
            sc.setRequestAttribute(request, "tabUserSecurity", new Tab(canEdit, true));
            sc.setRequestAttribute(request, "tabTaskSecurity", new Tab(canEdit, false));
            sc.setRequestAttribute(request, "tabCategorySecurity", new Tab(choosedCurrent.contains(Action.manageCategories), false));
            sc.setRequestAttribute(request, "tabWorkflowSecurity", new Tab(choosedCurrent.contains(Action.manageWorkflows), false));
            sc.setRequestAttribute(request, "tabView", new Tab(canEdit, false));
            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_USER_STATUS_USER_SECURITY);
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_USER_STATUS_PROPERTIES));
            return mapping.findForward("userSecurityJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            RoleUserSecurityForm pf = (RoleUserSecurityForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().userHeader(pf, sc, request);
            if (!sc.canAction(Action.manageRoles, id))
                return null;

            String prstatusId = pf.getPrstatusId();

            List<String> allowed = new ArrayList<String>();
            List<String> denied = new ArrayList<String>();

            if (pf.isEditUserHimself())
                allowed.add(Action.editUserHimself.toString());
            else
                denied.add(Action.editUserHimself.toString());
            if (pf.isEditUserChildren())
                allowed.add(Action.editUserChildren.toString());
            else
                denied.add(Action.editUserChildren.toString());
            if (pf.isCreateUser())
                allowed.add(Action.createUser.toString());
            else
                denied.add(Action.createUser.toString());
            if (pf.isDeleteUser())
                allowed.add(Action.deleteUser.toString());
            else
                denied.add(Action.deleteUser.toString());
            if (pf.isCutPasteUser())
                allowed.add(Action.cutPasteUser.toString());
            else
                denied.add(Action.cutPasteUser.toString());
            if (pf.isEditUserPasswordHimself())
                allowed.add(Action.editUserPasswordHimself.toString());
            else
                denied.add(Action.editUserPasswordHimself.toString());
            if (pf.isEditUserChildrenPassword())
                allowed.add(Action.editUserChildrenPassword.toString());
            else
                denied.add(Action.editUserChildrenPassword.toString());

            if (pf.isViewUserFilters())
                allowed.add(Action.viewUserFilters.toString());
            else
                denied.add(Action.viewUserFilters.toString());
            if (pf.isManageUserPrivateFilters())
                allowed.add(Action.manageUserPrivateFilters.toString());
            else
                denied.add(Action.manageUserPrivateFilters.toString());
            if (pf.isManageUserPublicFilters())
                allowed.add(Action.manageUserPublicFilters.toString());
            else
                denied.add(Action.manageUserPublicFilters.toString());

            if (pf.isManageUserACLs())
                allowed.add(Action.manageUserACLs.toString());
            else
                denied.add(Action.manageUserACLs.toString());
            if (pf.isManageUserUDFs())
                allowed.add(Action.manageUserUDFs.toString());
            else
                denied.add(Action.manageUserUDFs.toString());
            if (pf.isManageRoles())
                allowed.add(Action.manageRoles.toString());
            else
                denied.add(Action.manageRoles.toString());
            if (pf.isViewUserAttachments())
                allowed.add(Action.viewUserAttachments.toString());
            else
                denied.add(Action.viewUserAttachments.toString());
            if (pf.isCreateUserAttachments())
                allowed.add(Action.createUserAttachments.toString());
            else
                denied.add(Action.createUserAttachments.toString());
            if (pf.isManageUserAttachments())
                allowed.add(Action.manageUserAttachments.toString());
            else
                denied.add(Action.manageUserAttachments.toString());

            AdapterManager.getInstance().getSecuredPrstatusAdapterManager().setRoles(sc, prstatusId, allowed, denied);

            return mapping.findForward("userSecurityPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }


}
