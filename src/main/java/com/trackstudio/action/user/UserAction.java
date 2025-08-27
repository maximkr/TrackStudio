package com.trackstudio.action.user;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.trackstudio.action.GeneralAction;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.UserRelatedManager;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.secured.SecuredUserBean;

public class UserAction extends Action {

    private static Log log = LogFactory.getLog(UserAction.class);
    private static final LockManager lockManager = LockManager.getInstance();

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            SessionContext sc = GeneralAction.getInstance().imports(request, response);
            if (sc == null)
                return null;
            String userId = null;
            if (request.getAttribute("id") != null) userId = request.getAttribute("id").toString();
            if (userId == null) userId = request.getParameter("id");

            SecuredUserBean targetUser = AdapterManager.getInstance().getSecuredFindAdapterManager().findUserById(sc, userId);
            boolean containsUser = !UserRelatedManager.getInstance().getChildren(userId).isEmpty();
            if (targetUser.canView() && containsUser) {
                return mapping.findForward("userListPage");
            } else if (targetUser.canManage()) {
                return mapping.findForward("userViewPage");
            } else {
                return null;
            }
        } catch (GranException ge) {
            request.setAttribute("javax.servlet.jsp.jspException", ge);
            return mapping.findForward("error");
        } finally {
            if (w) lockManager.releaseConnection();
        }
    }
}