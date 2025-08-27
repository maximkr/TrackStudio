package com.trackstudio.action.task;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.trackstudio.action.TSDispatchAction;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.form.BaseForm;
import com.trackstudio.kernel.cache.TaskRelatedManager;

public class TaskDispatchAction extends TSDispatchAction {

    private static Log log = LogFactory.getLog(TaskDispatchAction.class);

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        log.trace("##########");
        //TaskDispatchForm bf = (TaskDispatchForm) form;
//        SessionContext sc = (SessionContext) request.getAttribute("sc");
//        String id = GeneralAction.getInstance().taskHeader(null, sc, request);

        return mapping.findForward("taskDispatchTileJSP");
    }

    public ActionForward go(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                            HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            BaseForm bf = (BaseForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String key = "1";
            if (request.getParameter("key") != null) key = request.getParameter("key");
            if (request.getParameter("autosearch") != null && request.getParameter("autosearch").length() > 0) {
                String objectId = request.getParameter("autosearch");
                if (objectId.startsWith("u-")) {
                    String userId = objectId.substring(2);
                    bf.setId(userId);
                    sc.setRequestAttribute(request, "id", userId);
                    return mapping.findForward("userAction");
                } else {
                    if (TaskRelatedManager.getInstance().isTaskExists(objectId)) {
                        bf.setId(objectId);
                        sc.setRequestAttribute(request, "id", objectId);

                        return mapping.findForward("taskAction");
                    } else key = objectId;
                }
            }
            sc.setRequestAttribute(request, "id", null);
            sc.setKey(key);
            bf.setMutable(false);
            sc.setRequestAttribute(request, "sc", sc);
            if (request.getParameter("searchIn") != null && request.getParameter("searchIn").equals("users"))
                return mapping.findForward("userSearchPage");
            else
                return mapping.findForward("taskSearchPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }
    }
}
