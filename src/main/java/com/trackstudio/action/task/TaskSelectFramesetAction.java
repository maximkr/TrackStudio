package com.trackstudio.action.task;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.trackstudio.action.GeneralAction;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.kernel.lock.LockManager;

public class TaskSelectFramesetAction extends Action {
    private static Log log = LogFactory.getLog(TaskSelectFramesetAction.class);
    private static final LockManager lockManager = LockManager.getInstance();

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            SessionContext sc = GeneralAction.getInstance().imports(request, response);
            String pack = request.getParameter("pack");
            sc.setAttribute("pack", pack);
            String udffield = request.getParameter("udffield");
            sc.setAttribute("udffield", udffield);
            sc.setRequestAttribute(request, "pack", pack);
            sc.setRequestAttribute(request, "udffield", udffield);
            sc.setRequestAttribute(request, "udfvalue", request.getParameter("udfvalue"));
            return mapping.findForward("taskSelectFramesetJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }
}
