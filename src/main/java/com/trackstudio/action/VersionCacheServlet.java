package com.trackstudio.action;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.kernel.lock.LockManager;

public class VersionCacheServlet extends HttpServlet {
    private static final LockManager lockManager = LockManager.getInstance();
    private static final Log log = LogFactory.getLog(VersionCacheServlet.class);

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        boolean w = lockManager.acquireConnection();
        try {
            StringBuffer requestURL = request.getRequestURL();
            int i = requestURL.indexOf("/cssjs/");
            if (i > -1) {
                int j = requestURL.indexOf("/", i + 7);
                if (j > -1) {
                    String url = requestURL.substring(j);
                    RequestDispatcher requestDispatcher  = request.getRequestDispatcher(url);
                    requestDispatcher.forward(request, response);
                    return;
                }
            }
            log.debug("Cannot process url " + requestURL); // should never reach this
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
