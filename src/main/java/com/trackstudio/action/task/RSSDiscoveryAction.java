package com.trackstudio.action.task;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.trackstudio.action.GeneralAction;
import com.trackstudio.app.report.handmade.RSSHandMadeReport;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.form.RSSForm;
import com.trackstudio.kernel.cache.TaskRelatedManager;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.startup.Config;
import com.trackstudio.startup.I18n;

public class RSSDiscoveryAction extends Action {//не наследует из-за того, что используется redirect TSDispatchAction
    private static Log log = LogFactory.getLog(RSSDiscoveryAction.class);
    private static final LockManager lockManager = LockManager.getInstance();

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            RSSForm rf = (RSSForm) form;

            SessionContext sc = GeneralAction.getInstance().imports(request, response);
            boolean taskExists = false;
            if (sc != null) {
                taskExists = TaskRelatedManager.getInstance().isTaskExists(rf.getId());
            }
            if (sc != null && taskExists) {
                response.setContentType("text/html; charset=" + Config.getEncoding());
                response.setCharacterEncoding(Config.getEncoding());
                PrintWriter wr = response.getWriter();
                try {
                    String export = new RSSHandMadeReport().generateDiscovery(sc, rf.getId());
                    wr.print(export);
                } catch (GranException ge) {
                    throw new GranException(ge);
                } finally {
                    wr.close();
                }
            } else {
                String error = "";
                if (sc == null)
                    error = I18n.getString("KLIP_AUTHENTIFICATION");
                else
                    error = I18n.getString(sc, "KLIP_TASK_NOT_FOUND");

                String export = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<error>" + error + "</error>";
                PrintWriter out = response.getWriter();
                out.print(export);
                out.close();
            }
            return null;
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }


}
