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
import com.trackstudio.action.TSDispatchAction;
import com.trackstudio.app.PageNaming;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.startup.I18n;

public class ErrorClipboardOperationAction extends Action {

    private static Log log = LogFactory.getLog(ErrorClipboardOperationAction.class);
    private static final LockManager lockManager = LockManager.getInstance();

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {

        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            sc.setCurrentSpace("SubtasksAction", request);
            String id = GeneralAction.getInstance().taskHeader(null, sc, request, true);
            TSDispatchAction.selectTaskTab(sc, id, "tabSubtasks", request);
            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_TASK_LIST);
            sc.setRequestAttribute(request, "showViewSubtasks", false);
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_TASK_LIST));
            return mapping.findForward("errorClipboardOperationJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }
}
