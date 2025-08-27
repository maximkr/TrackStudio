package com.trackstudio.action;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionServlet;

import com.trackstudio.app.session.SessionContext;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.startup.Config;

public class TSStrutsServlet extends ActionServlet {
    private static final LockManager lockManager = LockManager.getInstance();

    public void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException {
        boolean w = lockManager.acquireConnection();
        try {
            if (Config.getInstance() != null)
                httpServletRequest.setCharacterEncoding(Config.getEncoding());
            String url = httpServletRequest.getRequestURL().toString();
            boolean isNoCacheAction = url.contains("DownloadAction") || url.contains("ExportAction") || url.contains("ExportAction") || url.contains("writeOut");
            String method = httpServletRequest.getParameter("method");
            boolean isReportView = "browse".equals(method) && url.contains("ReportViewAction");
            if (!(isNoCacheAction || isReportView)) {
                if (Config.getInstance() != null)
                    httpServletResponse.setContentType("text/html; charset=" + Config.getEncoding());
                httpServletResponse.setHeader("Cache-Control", "public"); //HTTP 1.1
                httpServletResponse.setHeader("Pragma", "public"); //HTTP 1.0
                httpServletResponse.setDateHeader("Expires", 0L); //prevents caching at the proxy server
            }
            httpServletRequest.setAttribute("servletConfig", getServletConfig());
            SessionContext.setServletConfig(getServletConfig());
            super.doPost(httpServletRequest, httpServletResponse);
        } catch (IOException ignore) {
        } finally {
            if (w) lockManager.releaseConnection();
        }
    }

    public void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException {
        doGet(httpServletRequest, httpServletResponse);
    }
}
