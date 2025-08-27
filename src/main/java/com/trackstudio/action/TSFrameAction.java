package com.trackstudio.action;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.form.BaseForm;
import com.trackstudio.kernel.lock.LockManager;

public class TSFrameAction extends Action {
    private static final LockManager lockManager = LockManager.getInstance();

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            BaseForm bf = (BaseForm) form;
            SessionContext sc = GeneralAction.getInstance().imports(request, response);
            if (sc == null)
                return null;
            String contextPath = request.getContextPath();
            String id = bf.getId();
            if (request.getParameter("userId") != null) {
                RequestDispatcher requestDispatcher;
                requestDispatcher = request.getRequestDispatcher("/UserAction.do?method=page&id=" + request.getParameter("userId") + "&thisframe=true");
                requestDispatcher.forward(request, response);

            } else {
                RequestDispatcher requestDispatcher;
                requestDispatcher = request.getRequestDispatcher(id == null ? contextPath + "/TaskAction.do?id=" + AdapterManager.getInstance().getSecuredFindAdapterManager().quickGoHandler(sc, "") + "&thisframe=true" : contextPath + "/TaskAction.do?id=" + id + "&thisframe=true");
                requestDispatcher.forward(request, response);
            }
            return null;
        } catch (GranException ge) {
            request.setAttribute("javax.servlet.jsp.jspException", ge);
            return mapping.findForward("error");
        } finally {
            if (w) lockManager.releaseConnection();
        }
    }
}