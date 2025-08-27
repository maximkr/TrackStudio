package com.trackstudio.action.task;

import java.util.Calendar;
import java.util.GregorianCalendar;

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
import com.trackstudio.app.adapter.email.change.Change;
import com.trackstudio.app.adapter.email.change.TestChange;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.UserException;
import com.trackstudio.form.NotifySubscribeForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.secured.SecuredNotificationBean;
import com.trackstudio.simple.Notification;
import com.trackstudio.startup.Config;
import com.trackstudio.startup.I18n;

public class TaskNotifyViewAction extends TSDispatchAction {
    private static Log log = LogFactory.getLog(TaskNotifyViewAction.class);

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");

            NotifySubscribeForm nsf = (NotifySubscribeForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().taskHeader(nsf, sc, request, true);
            if (!sc.canAction(Action.manageEmailSchedules, id)) return null;
            SecuredNotificationBean bean = AdapterManager.getInstance().getSecuredFindAdapterManager().findNotificationById(sc, nsf.getNotificationId());
            sc.setRequestAttribute(request, "notificationId", nsf.getNotificationId());
            sc.setRequestAttribute(request, "notification", bean);
            if (bean.getTemplate() == null)
                sc.setRequestAttribute(request, "template", I18n.getString(sc.getLocale(), "USER_DEFINED"));
            else
                sc.setRequestAttribute(request, "template", I18n.getString(sc.getLocale(), bean.getTemplate()));
            sc.setRequestAttribute(request, "canEdit", (sc.canAction(Action.manageEmailSchedules, id)) && bean.canManage());
            sc.setRequestAttribute(request, "canCreate", sc.canAction(Action.manageEmailSchedules, id));

            return mapping.findForward("taskNotifyViewJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward test(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            NotifySubscribeForm notifySubscribeForm = (NotifySubscribeForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            if (Config.getInstance().isSendMail()) {
                String taskId = GeneralAction.getInstance().taskHeader(notifySubscribeForm, sc, request, false);
                String notificationId = notifySubscribeForm.getNotificationId();
                SecuredNotificationBean securedNotificationBean = AdapterManager.getInstance().getSecuredFindAdapterManager().findNotificationById(sc, notificationId);
                Notification n = new Notification(notificationId, securedNotificationBean.getName());
                n.setFilter(securedNotificationBean.getFilter().getName());
                n.setTask(securedNotificationBean.getTask().getName());
                n.setTemplate(securedNotificationBean.getTemplate());
                n.setUser(sc.getUser().getName());
                if (securedNotificationBean.getUser() != null && (securedNotificationBean.getUser().getEmail() == null || securedNotificationBean.getUser().getEmail().length() == 0))
                    sc.setRequestAttribute(request, "testResult", I18n.getString(sc.getLocale(), "USER_EMAIL_IS_EMPTY"));
                else {
                    Calendar now = new GregorianCalendar();
                    now.setTimeInMillis(System.currentTimeMillis());

                    Change change = new TestChange(now, sc.getUserId(), n);
                    boolean result = AdapterManager.getInstance().getFilterNotifyAdapterManager().sendNotifyForTask(null, taskId, sc.getUserId(), null, change);
                    sc.setRequestAttribute(request, "testResult", result ? I18n.getString(sc.getLocale(), "NOTIFICATION_WAS_SUCCESSFULLY_SENT") : I18n.getString(sc.getLocale(), "NOTIFICATION_WASNT_SENT"));
                }
            } else
                throw new UserException("CAN_NOT_SEND_NOTIFICATION");
            return mapping.findForward("taskNotifyViewPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }


}
