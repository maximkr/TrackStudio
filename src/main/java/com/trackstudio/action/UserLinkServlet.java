package com.trackstudio.action;

import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.startup.Config;

public class UserLinkServlet extends HttpServlet {
    private static final LockManager lockManager = LockManager.getInstance();
    private static final Log log = LogFactory.getLog(UserLinkServlet.class);

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        boolean w = lockManager.acquireConnection();
        try {
            GeneralAction.getInstance().imports(request, response);
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            if (sc == null)
                return;
            String goTo = "1";
            int i = request.getRequestURL().indexOf(request.getContextPath() + "/user/");
            if (i != -1) {
                String login = request.getRequestURL().substring(i + (request.getContextPath() + "/user/").length());
                login = URLDecoder.decode(login, Config.getEncoding());
                SecuredUserBean sub = AdapterManager.getInstance().getSecuredUserAdapterManager().findByLogin(sc, login);
                if (sub != null)
                    goTo = sub.getId();
            }
            RequestDispatcher requestDispatcher;
            requestDispatcher = request.getRequestDispatcher("/UserAction.do?id=" + goTo);
            requestDispatcher.forward(request, response);
        } catch (IOException ignore) {
        } catch (Exception ex) {
            log.error("Error", ex);
            request.setAttribute("javax.servlet.jsp.jspException", ex);
            RequestDispatcher requestDispatcher = request.getRequestDispatcher("/jsp/Error.jsp");
            requestDispatcher.forward(request, response);
        } finally {
            if (w) lockManager.releaseConnection();
        }
    }

    public void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException {
        doGet(httpServletRequest, httpServletResponse);
    }
}
