package com.trackstudio.action.user;

import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.trackstudio.action.GeneralAction;
import com.trackstudio.action.TSDispatchAction;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.form.UserForm;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.tools.Null;

public class UserEmegencyNoticeAction extends TSDispatchAction {
    private static Log log = LogFactory.getLog(UserEmegencyNoticeAction.class);

    public ActionForward page(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            UserForm form = (UserForm) actionForm;
            form.setMethod("save");
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().userHeader(null, sc, request);

            if (!sc.allowedByUser(id))
                return null;

            SecuredUserBean sub = new SecuredUserBean(id, sc);
            String notice = sub.getUser().getEmergencyNotice() != null ? sub.getUser().getEmergencyNotice() : "";
            form.setEmergencyNotice(notice);
            form.setEmergencyNoticeDate(Null.stripNullText(sc.getUser().getDateFormatter().parse(sub.getUser().getEmergencyNoticeDate())));
            String referer = request.getContextPath() + "/UserViewAction.do?method=page&amp;id=" + id;
            sc.setRequestAttribute(request, "referer", referer);
            sc.setRequestAttribute(request, "datepattern", sc.getUser().getDateFormatter().getPattern2());
            sc.setRequestAttribute(request, "pattern", sc.getUser().getDateFormatter().getPattern2());
            return actionMapping.findForward("userEmegencyNoticeViewJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward save(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            UserForm uf = (UserForm) actionForm;
            Calendar noticeDate = Calendar.getInstance();
            AdapterManager.getInstance().getSecuredUserAdapterManager().updateNoticeUser(sc, uf.getId(), uf.getEmergencyNotice(), noticeDate);
            ActionForward af = new ActionForward(actionMapping.findForward("userViewPage").getPath() + "&id=" + uf.getId());
            af.setRedirect(true);
            return af;
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }
}
