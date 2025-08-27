package com.trackstudio.action.task;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.trackstudio.action.GeneralAction;
import com.trackstudio.action.TSDispatchAction;
import com.trackstudio.app.PageNaming;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.secured.SecuredAttachmentBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUserBean;

public class UploadAppletAction extends TSDispatchAction {

    private static Log log = LogFactory.getLog(TaskDispatchAction.class);

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            boolean isUser = request.getParameter("user") != null;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id;
            if (!isUser)
                id = GeneralAction.getInstance().taskHeader(null, sc, request, true);
            else
                id = GeneralAction.getInstance().userHeader(null, sc, request);
            int idx = request.getRequestURL().lastIndexOf("/");
            String url = request.getRequestURL().substring(0, idx);
            sc.setRequestAttribute(request, "contextPath", url);
            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_TASK_UPLOAD_APPLET);
            sc.setRequestAttribute(request, "locale", sc.getLocale());
            sc.setRequestAttribute(request, "sessionId", sc.getId());
            boolean canDelete = (isUser && id.equals(sc.getUserId()) || sc.canAction(Action.manageTaskAttachments, id));
            sc.setRequestAttribute(request, "canDeleteUpload", canDelete);
            sc.setRequestAttribute(request, "ownerId", isUser ? "user" + id : id);
            List attachmentList;
            if (!isUser)
                attachmentList = new SecuredTaskBean(id, sc).getAttachments();
            else
                attachmentList = new SecuredUserBean(id, sc).getAttachments();
            String atts = "";
            if (attachmentList != null) {
                for (SecuredAttachmentBean att : (List<SecuredAttachmentBean>) attachmentList) {
                    if (att != null) {
                        boolean canDel = false;
                        if (att.getUserId() != null) {
                            canDel = att.getUserId().equals(sc.getUserId());
                            for (Iterator it2 = sc.getUser().getChildren().iterator(); it2.hasNext() && !canDel; )
                                if (att.getUser().equals(it2.next())) canDel = true;
                        }
                        atts += att.getId() + ';' + att.getName() + ';' + (!att.getDeleted() ? att.getSize() : 0L) + ';' + FixDescription(att.getDescription()) + ';' + canDel + ';';
                    }
                }
            }
            sc.setRequestAttribute(request, "attaches", atts);
            return mapping.findForward("AppletJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    static String FixDescription(String desc) {
        String ret = "";
        if (desc != null) for (int c = 0; c < desc.length(); c++)
            if (desc.charAt(c) != '\"' && desc.charAt(c) != '\'' && desc.charAt(c) != ';')
                ret += desc.charAt(c);
        return ret;
    }
}
