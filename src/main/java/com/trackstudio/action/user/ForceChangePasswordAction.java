package com.trackstudio.action.user;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.trackstudio.action.TSDispatchAction;
import com.trackstudio.app.PageNaming;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.form.ChangePasswordForm;
import com.trackstudio.startup.Config;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.textfilter.HTMLEncoder;

public class ForceChangePasswordAction extends TSDispatchAction {
    private static Log log = LogFactory.getLog(ForceChangePasswordAction.class);

    public ActionForward page(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            ChangePasswordForm form = (ChangePasswordForm) actionForm;
            SessionContext sc = (SessionContext) request.getAttribute("sc");

            if (request.getParameter("lastPath") != null) form.setLastPath(request.getParameter("lastPath"));
            //SecuredUserBean uc = new SecuredUserBean(id, sc);
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_USER_CHANGE_PASSWORD));
            sc.setRequestAttribute(request, "helpTopic", I18n.getString(sc.getLocale(), "LOGIN_PAGE"));
            sc.setRequestAttribute(request, "helpContent", I18n.getString(sc.getLocale(), "ACCOUNT_INFO_HELP"));
            sc.setRequestAttribute(request, "sc", sc);
            sc.setRequestAttribute(request, "tsHost", Config.getInstance().isTSHost() ? "true" : null);
            return actionMapping.findForward("forceChangePasswordJSP");
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
            AdapterManager.getInstance().getSecuredUserAdapterManager().changePassword(sc, bf.getId(), bf.getPassword(), bf.getConfirmation());
            AdapterManager.getInstance().getSecuredUserAdapterManager().updateLastLogonDate(sc, bf.getId());
            String lastPath = bf.getLastPath();

            sc.setRequestAttribute(request, "changed", Boolean.TRUE);
            if (lastPath != null && lastPath.length() > 0) {
                HTMLEncoder en = new HTMLEncoder(lastPath);
                en.replace("session", "OLDSESSION");
                StringBuffer sb = en.getResult();
                if (sb.indexOf("?") > -1)
                    sb.append("&thisframe=true");
                else {
                    int pos = sb.indexOf("&");
                    if (pos == -1)
                        sb.append("?thisframe=true");
                    else {
                        sb.replace(pos, pos + 1, "?");
                        sb.append("&thisframe=true");
                    }
                }

                sc.setAttribute("mainFrameGoTo", sb.toString());
                  /*response.sendRedirect(sb.toString() + "&session=" + sessionId);

                 return null;*/
            }

            response.sendRedirect(request.getContextPath() + "/TaskAction.do");

            return null;
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }


}
