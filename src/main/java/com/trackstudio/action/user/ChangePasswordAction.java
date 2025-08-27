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
import com.trackstudio.app.PageNaming;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.form.ChangePasswordForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.startup.Config;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.Tab;

public class ChangePasswordAction extends TSDispatchAction {
    private static Log log = LogFactory.getLog(ChangePasswordAction.class);

    public ActionForward page(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            ChangePasswordForm form = (ChangePasswordForm) actionForm;
            form.setPassword("");
            form.setConfirmation("");
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().userHeader(form, sc, request);
            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_USER_CHANGE_PASSWORD);
            SecuredUserBean uc = new SecuredUserBean(id, sc);
            sc.setRequestAttribute(request, "userName", uc.getName());
            sc.setRequestAttribute(request, "correctPassword", Config.getInstance().getProperty("trackstudio.security.password.complex").equals("yes"));
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_USER_CHANGE_PASSWORD));
            sc.setRequestAttribute(request, "tabChangePassword", new Tab(!(!id.equals(sc.getUserId()) && AdapterManager.getInstance().getSecuredUserAdapterManager().isParentOf(sc, id, sc.getUserId())) && (sc.getUserId().equals(id) && sc.canAction(Action.editUserPasswordHimself, id) || sc.canAction(Action.editUserChildrenPassword, id)), true));
            sc.setRequestAttribute(request, "canChangePassword", !(!id.equals(sc.getUserId()) && AdapterManager.getInstance().getSecuredUserAdapterManager().isParentOf(sc, id, sc.getUserId())) && (sc.getUserId().equals(id) && sc.canAction(Action.editUserPasswordHimself, id) || sc.canAction(Action.editUserChildrenPassword, id)));
            String referer = request.getContextPath() + "/UserViewAction.do?method=page&amp;id=" + id;
            sc.setRequestAttribute(request, "referer", referer);
            selectUserTab(sc, id, "tabUser", request);
            return actionMapping.findForward("changePasswordJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward changePassword(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            ChangePasswordForm bf = (ChangePasswordForm) actionForm;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            sc.setRequestAttribute(request, "changed", false);
            Boolean oldPassword = AdapterManager.getInstance().getSecuredUserAdapterManager().changePassword(sc, bf.getId(), bf.getPassword(), bf.getConfirmation());
            sc.setRequestAttribute(request, "oldPassword", oldPassword);
            sc.setRequestAttribute(request, "changed", true);
            return actionMapping.findForward("changePasswordPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }
}
