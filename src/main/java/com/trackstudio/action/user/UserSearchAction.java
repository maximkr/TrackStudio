package com.trackstudio.action.user;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.trackstudio.action.GeneralAction;
import com.trackstudio.action.TSDispatchAction;
import com.trackstudio.app.Slider;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.form.UserForm;
import com.trackstudio.secured.SecuredSearchAttachmentItem;
import com.trackstudio.secured.SecuredSearchItem;
import com.trackstudio.secured.SecuredSearchUserItem;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.tools.Null;

public class UserSearchAction extends TSDispatchAction {
    private static Log log = LogFactory.getLog(UserSearchAction.class);
    public static final NumberFormat NF = new DecimalFormat("#0.#");

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            UserForm bf = (UserForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().userHeader(null, sc, request);
            String contextPath = request.getContextPath();
            String key = sc.getKey();
            String pageNumber = bf.getSliderPage();
            int pn = 1;
            if (Null.isNotNull(pageNumber)) {
                try {
                    pn = Integer.parseInt(pageNumber);
                } catch (Exception exc) {
                    // just skip
                }
            }
            //получаем поисковый аргумент

            String searchIn = request.getParameter("searchIn");
            if (searchIn == null) searchIn = "users";
            // Search users by name and login
            SecuredUserBean uci = new SecuredUserBean(id, sc);
            SecuredTaskBean task = AdapterManager.getInstance().getSecuredFindAdapterManager().searchTaskByQuickGo(sc, key);
            if (task != null && (request.getParameter("autosearch") == null || request.getParameter("autosearch").length() == 0)) {
                bf.setId(task.getId());
                ActionForward af = new ActionForward(mapping.findForward("taskAction").getPath() + "?id=" + task.getId());
                af.setRedirect(true);
                return af;
            }
            SecuredUserBean user = AdapterManager.getInstance().getSecuredFindAdapterManager().searchUserByQuickGo(sc, key);
            if (user != null && (request.getParameter("autosearch") == null || request.getParameter("autosearch").length() == 0)) {
                bf.setId(user.getId());
                ActionForward af = new ActionForward(mapping.findForward("userAction").getPath() + "?id=" + user.getId());
                af.setRedirect(true);
                return af;
            }

            sc.setRequestAttribute(request, "founduser", user);
            sc.setRequestAttribute(request, "foundtask", task);
            ArrayList<SecuredSearchUserItem> users = AdapterManager.getInstance().getSecuredUserAdapterManager().fullTextSearch(uci, key);

            // Search attachments by name
            ArrayList<SecuredSearchAttachmentItem> attachments = AdapterManager.getInstance().getSecuredAttachmentAdapterManager().fullTextSearch(sc, key);

            Slider<SecuredSearchUserItem> userSlider = new Slider<SecuredSearchUserItem>(users, 20, null, searchIn.equals("users") ? pn : 1);
            Slider<SecuredSearchAttachmentItem> attachmentSlider = new Slider<SecuredSearchAttachmentItem>(attachments, 20, null, searchIn.equals("attachments") ? pn : 1);
            userSlider.setAll(bf.isAll(), sc.getLocale());
            attachmentSlider.setAll(bf.isAll(), sc.getLocale());
            sc.setRequestAttribute(request, "key", key);
            sc.setRequestAttribute(request, "users", userSlider);
            sc.setRequestAttribute(request, "userSlider", userSlider.drawSlider(contextPath + "/UserSearchAction.do?method=page&amp;id=" + id + "&amp;searchIn=users", "div", "slider"));
            sc.setRequestAttribute(request, "attachments", attachmentSlider);
            sc.setRequestAttribute(request, "attachmentSlider", attachmentSlider.drawSlider(contextPath + "/UserSearchAction.do?method=page&amp;id=" + id + "&amp;searchIn=attachment", "div", "slider"));
            return mapping.findForward("userSearchJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }
}
