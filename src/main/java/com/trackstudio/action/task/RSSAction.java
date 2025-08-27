package com.trackstudio.action.task;

import java.io.PrintWriter;
import java.util.List;

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
import com.trackstudio.app.filter.TaskFValue;
import com.trackstudio.app.report.handmade.HandMadeReportManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.form.RSSForm;
import com.trackstudio.kernel.cache.TaskRelatedManager;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.secured.SecuredFilterBean;
import com.trackstudio.securedkernel.SecuredReportAdapterManager;
import com.trackstudio.startup.Config;
import com.trackstudio.startup.I18n;

public class RSSAction extends Action {//не наследует из-за того, что используется redirect TSDispatchAction
    private static Log log = LogFactory.getLog(RSSAction.class);
    private static final LockManager lockManager = LockManager.getInstance();

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            response.setContentType("text/xml");
            RSSForm rf = (RSSForm) form;

            SessionContext sc = GeneralAction.getInstance().imports(request, response);
            boolean filterExists = false;
            boolean taskExists = false;
            if (sc != null) {
                taskExists = TaskRelatedManager.getInstance().isTaskExists(rf.getId());
                if (taskExists) {
                    List<SecuredFilterBean> list = AdapterManager.getInstance().getSecuredFilterAdapterManager().getTaskFilterList(sc, rf.getId());
                    for (SecuredFilterBean sfb : list) {
                        if (sfb.getId().equals(rf.getFilter())) {
                            filterExists = true;
                            break;
                        }
                    }
                }
            }
            if (sc != null && taskExists && filterExists) {
                PrintWriter wr = response.getWriter();
                try {
                    TaskFValue fval = AdapterManager.getInstance().getSecuredFilterAdapterManager().getTaskFValue(sc, rf.getFilter()).getFValue();
                    String export = new HandMadeReportManager().generate(sc, rf.getId(), rf.getFilter(), fval, SecuredReportAdapterManager.RT_RSS, null, null);
                    wr.print("<?xml version=\"1.0\" encoding=\"" + Config.getEncoding() + "\"?>");
                    export = export.indexOf('\n') == -1 ? export : export.substring(export.indexOf("\n"));
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
                else if (!taskExists)
                    error = I18n.getString(sc, "KLIP_TASK_NOT_FOUND");
                else
                    error = I18n.getString(sc, "KLIP_FILTER_NOT_FOUND");

                String export = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<error>" + error + "</error>";
                PrintWriter out = response.getWriter();
                out.print(export);
                out.close();
            }
        } catch (GranException ge) {
            //dnikitin тут форвардить на errorPage не нужно. Т.к. этот action вызывает только klip
            //кроме того здесь форвард не сработает, т.к. в response.writer уже есть данные
        } finally {
            if (w) lockManager.releaseConnection();
        }
        return null;
    }


}
