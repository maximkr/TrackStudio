package com.trackstudio.action;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.exception.UserException;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.tools.Tab;

public class TSDispatchAction extends DispatchAction {
    protected static final LockManager lockManager = LockManager.getInstance();

    public static void selectTaskTab(SessionContext sc, String id, String tabName, HttpServletRequest request) throws GranException {
        SecuredTaskBean tci = new SecuredTaskBean(id, sc);
        boolean canViewChildren = tci.canViewChildren();
        boolean canContainsSubtasks = AdapterManager.getInstance().getSecuredCategoryAdapterManager().hasSubcategories(sc, tci.getCategoryId(), tci.getId());
        request.setAttribute("tabSubtasks", new Tab((canContainsSubtasks || canViewChildren), false));
        request.setAttribute("tabFilters", new Tab((canContainsSubtasks || canViewChildren) && (sc.canAction(Action.manageTaskPrivateFilters, id) || sc.canAction(Action.manageTaskPublicFilters, id)), false));
        request.setAttribute("tabReports", new Tab((canContainsSubtasks || canViewChildren) && sc.canAction(Action.viewReports, id), false));
        Object o = request.getAttribute(tabName);
        if (o != null) {
            Tab t = (Tab) o;
            t.setSelected(true);
            request.setAttribute(tabName, t);
        }
    }

    public static void selectUserTab(SessionContext sc, String id, String tabName, HttpServletRequest request) throws GranException {
        SecuredUserBean uc = new SecuredUserBean(id, sc);
        sc.setRequestAttribute(request, "tabUserList", new Tab(uc.canView(), false));
        sc.setRequestAttribute(request, "tabUserFilter", new Tab(uc.hasChildren() && sc.canAction(Action.manageUserPrivateFilters, id), false));
        sc.setRequestAttribute(request, "tabUser", new Tab(uc.canManage(), false));
        Object o = request.getAttribute(tabName);
        if (o != null) {
            Tab t = (Tab) o;
            t.setSelected(true);
            request.setAttribute(tabName, t);
        }
    }

    protected String getMethodName(ActionMapping mapping,
                                   ActionForm form,
                                   HttpServletRequest request,
                                   HttpServletResponse response,
                                   String parameter)
            throws Exception {


        if (request.getParameter(parameter) == null)
            return "page";
        return request.getParameter(parameter);
    }

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
        try {
            SessionContext sc = GeneralAction.getInstance().imports(request, response);
            if (sc == null)
                return null;
            if (request.getParameter("method") != null && request.getParameter("method").equals("page"))
                return super.execute(mapping, form, request, response);
            else {
                try {
                    return super.execute(mapping, form, request, response);
                } catch (UserException ue) {
                    log.error("Error", ue);
                    if (ue.getActionMessages() != null) {
                        saveMessages(request, ue.getActionMessages());
                        return mapping.getInputForward();
                    } else
                        throw ue;
                }
            }
        } catch (UserException ue) {
            request.getSession().removeAttribute("autologin");
            request.getSession().removeAttribute("autopassword");
            log.error("Error", ue);
            request.setAttribute("javax.servlet.jsp.jspException", ue);
            return mapping.findForward("error");
        } catch (GranException ge) {
            log.error("Error", ge);
            request.setAttribute("javax.servlet.jsp.jspException", ge);
            return mapping.findForward("error");
        } catch (Exception ge) {
            log.error("Error", ge);
            request.setAttribute("javax.servlet.jsp.jspException", new GranException(ge));
            return mapping.findForward("error");
        }
    }

    public static Cookie createCookie(String space, String value) {
        Cookie cookie = new Cookie(space, value);
        cookie.setMaxAge(31536000); // 1 year
        cookie.setPath("/");
        return cookie;
    }

    public static Cookie removeCookie(String space) {
        Cookie cookie = new Cookie(space, "");
        cookie.setMaxAge(-1);
        cookie.setPath("/");
        return cookie;
    }
}
