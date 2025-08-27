package com.trackstudio.action;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.secured.SecuredStatusBean;
import com.trackstudio.secured.SecuredWorkflowBean;

public class TransitionCreateServlet extends HttpServlet {
    private static Log log = LogFactory.getLog(TransitionCreateServlet.class);
    private static String className = "TransitionCreateServlet";
    private static final LockManager lockManager = LockManager.getInstance();

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        boolean w = lockManager.acquireConnection();
        try {
            SessionContext sc = GeneralAction.getInstance().imports(request, response, true);
            String wf = request.getParameter("wf");
            String ms = request.getParameter("ms");
            response.setLocale(new Locale(sc.getLocale()));
            response.setCharacterEncoding("UTF-8");
            if ("true".equals(request.getParameter("update"))) {
                String[] start = request.getParameterValues("start");
                String finish = request.getParameter("finish");
                if (start != null && start.length > 0 && finish != null && finish.length() > 0) {
                    SecuredWorkflowBean flow = AdapterManager.getInstance().getSecuredFindAdapterManager().findWorkflowById(sc, wf);
                    if (sc.canAction(Action.manageWorkflows, flow.getTaskId())) {
                        setTransition(sc, start, finish, ms);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Exception ", e);
        } finally {
            if (w) lockManager.releaseConnection();
        }
    }

    public void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException {
        doGet(httpServletRequest, httpServletResponse);
    }

    public void setTransition(SessionContext sc, String[] start, String finalId, String ms) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            for (String aStart : start) {
                SecuredStatusBean startSsb = AdapterManager.getInstance().getSecuredFindAdapterManager().findStatusById(sc, aStart);
                SecuredStatusBean finishSsb = AdapterManager.getInstance().getSecuredFindAdapterManager().findStatusById(sc, finalId);
                AdapterManager.getInstance().getSecuredWorkflowAdapterManager().updateTransition(sc, ms, startSsb.getId(), finishSsb.getId());
            }
        } catch (Exception e) {
            throw new GranException(e);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }
}
