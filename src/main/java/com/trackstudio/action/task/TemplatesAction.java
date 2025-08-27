package com.trackstudio.action.task;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.trackstudio.action.GeneralAction;
import com.trackstudio.action.TSDispatchAction;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.form.BaseForm;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.model.Notification;
import com.trackstudio.model.Subscription;
import com.trackstudio.model.Template;
import com.trackstudio.secured.SecuredNotificationBean;
import com.trackstudio.tools.SecuredBeanUtil;

public class TemplatesAction extends TSDispatchAction {
    private static final LockManager lockManager = LockManager.getInstance();

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            BaseForm bs = (BaseForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            GeneralAction.getInstance().taskHeader(bs, sc, request, true);
            List<Notification> notifications = KernelManager.getFilter().getAllNotificationList();
            List<SecuredNotificationBean> notList = SecuredBeanUtil.toArrayList(sc, notifications, SecuredBeanUtil.NOTIFICATION);
            sc.setRequestAttribute(request, "notifications", notList);
            List<Subscription> subscribes = KernelManager.getFilter().getAllSubscriptionList();
            List<SecuredNotificationBean> subList = SecuredBeanUtil.toArrayList(sc, subscribes, SecuredBeanUtil.SUBSCRIPTION);
            sc.setRequestAttribute(request, "subscribes", subList);
            List<Template> templates = KernelManager.getTemplate().getAllTemplatesList();
            List<SecuredNotificationBean> templatesList = SecuredBeanUtil.toArrayList(sc, templates, SecuredBeanUtil.TEMPLATE);
            sc.setRequestAttribute(request, "templates", templatesList);
            return mapping.findForward("templatesJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

}
