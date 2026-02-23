package com.trackstudio.action;

import java.io.IOException;
import java.util.Locale;

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
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.secured.SecuredTaskBean;

public class TaskLinkServlet extends HttpServlet {
    private static final LockManager lockManager = LockManager.getInstance();
    private static Log log = LogFactory.getLog(TaskLinkServlet.class);

    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        SessionContext sc = null;
        try {
            sc = GeneralAction.getInstance().imports(req, resp, true);
        } catch (Exception ex) {/*Empty*/}
        if (sc == null) {
            return;
        } else {
            StringBuffer rurl = req.getRequestURL();
            int i = rurl.indexOf(req.getContextPath() + "/task/");
            int irss = rurl.indexOf("/rss");
            if (i != -1 && irss != -1) {
                try {
                    String s = rurl.toString().toLowerCase(Locale.ENGLISH);
                    String key = rurl.substring(i + (req.getContextPath() + "/task/").length(), irss);
                    SecuredTaskBean goTo = AdapterManager.getInstance().getSecuredTaskAdapterManager().findTaskByNumber(sc, key);
                    RequestDispatcher requestDispatcher;
                    if (s.endsWith("/rss")) {
                        requestDispatcher = req.getRequestDispatcher("/RSSDiscoveryAction.do?id=" + goTo.getId() + "&sc=" + sc.getId());
                    } else {
                        String keyFilter = rurl.substring(irss + "/rss/".length());
                        requestDispatcher = req.getRequestDispatcher("/RSSAction.do?id=" + goTo.getId() + "&sc=" + sc.getId() + "&filter=" + keyFilter);
                    }
                    requestDispatcher.forward(req, resp);
                    return;
                } catch (Exception ex) {
                    req.setAttribute("javax.servlet.jsp.jspException", ex);
                    RequestDispatcher requestDispatcher = req.getRequestDispatcher("/jsp/Error.jsp");
                    requestDispatcher.forward(req, resp);
                }
            }
        }
        super.service(req, resp);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        boolean w = lockManager.acquireConnection();
        try {
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            if (sc == null) {
                sc = GeneralAction.getInstance().imports(request, response, false);
                if (sc == null) {
                    return;
                }
            }
            String goTo = "1";
            String url = request.getRequestURL().toString();
            if (url.contains(request.getContextPath() + "/task/")) {
                String key = url.substring(url.indexOf(request.getContextPath() + "/task/") + (request.getContextPath() + "/task/").length());
                if (url.contains("/filter/")) {
                    String keyF = url.substring(url.indexOf("/filter/") + "/filter/".length());
                    sc.setAttribute("permlinkfilter", keyF);
                    key = key.substring(0, key.indexOf("/filter/"));
                }
                String id = KernelManager.getTask().findByNumber(key);
                if (id == null) {
                    request.setAttribute("key", key);
                    request.setAttribute("status", HttpServletResponse.SC_BAD_REQUEST);
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                } else if (!sc.taskOnSight(id)) {
                    request.setAttribute("login", sc.getUser().getLogin());
                    request.setAttribute("key", key);
                    request.setAttribute("status", HttpServletResponse.SC_FORBIDDEN);
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }
                goTo = id;
            }

            //String path = request.getContextPath() + "/TaskAction.do?id=" + goTo;
            String path = "/TaskAction.do?id=" + goTo;
            RequestDispatcher requestDispatcher = request.getRequestDispatcher(path);
            requestDispatcher.forward(request, response);
        } catch (IOException ignore) {
        } catch (Exception ex) {
            log.error("Error",ex);
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
