package com.trackstudio.action.task;

import java.util.ArrayList;
import java.util.Collections;
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
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.containers.NotificationListItem;
import com.trackstudio.form.NotifySubscribeForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.secured.SecuredFilterBean;
import com.trackstudio.secured.SecuredNotificationBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.EggBasket;

public class TaskNotifyAction extends TSDispatchAction {
    private static Log log = LogFactory.getLog(TaskNotifyAction.class);

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            NotifySubscribeForm nsf = (NotifySubscribeForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().taskHeader(nsf, sc, request, true);
            if (!sc.canAction(Action.manageEmailSchedules, id)) return null;

            ArrayList<SecuredFilterBean> fli = AdapterManager.getInstance().getSecuredFilterAdapterManager().getAllTaskFilterList(sc, id);
            ArrayList<NotificationListItem> notifications = new ArrayList<NotificationListItem>();
            EggBasket<SecuredTaskBean, NotificationListItem> parentNotifications = new EggBasket<SecuredTaskBean, NotificationListItem>();
            EggBasket<SecuredTaskBean, NotificationListItem> childrenNotifications = new EggBasket<SecuredTaskBean, NotificationListItem>();
            ArrayList<EggBasket> seeAlso = new ArrayList<EggBasket>();
            ArrayList<SecuredTaskBean> parentTasks = AdapterManager.getInstance().getSecuredTaskAdapterManager().getTaskChain(sc, null, id);

            for (SecuredFilterBean aFilter : fli) {
                List<SecuredNotificationBean> notificationSet = AdapterManager.getInstance().getSecuredFilterAdapterManager().getAllNotificationList(sc, aFilter.getId());
                for (SecuredNotificationBean n : notificationSet) {
                    SecuredTaskBean task = n.getTask();
                    NotificationListItem ni = new NotificationListItem(n.getId(), n.getName());
                    ni.setCanUpdate(sc.canAction(Action.manageEmailSchedules, n.getTaskId()) && n.canManage());
                    if (n.getUserId() != null) ni.setUser(n.getUser());
                    if (n.getGroupId() != null) ni.setGroup(n.getGroup());
                    ni.setTask(n.getTask());
                    ni.setFilter(n.getFilter().getName());
                    ni.setFilterId(n.getFilter().getId());
                    if (n.getTemplate() != null)
                        ni.setTemplate(n.getTemplate());
                    else
                        ni.setTemplate(I18n.getString(sc.getLocale(), "USER_DEFINED"));
                    ni.setFireNewTask(n.isFireNewTask());
                    ni.setFireNewAttachment(n.isFireNewAttachment());
                    ni.setFireNewMessage(n.isFireNewMessage());
                    ni.setFireUpdatedTask(n.isFireUpdatedTask());
                    ni.setFireNotI(n.isFireNotI());
                    if (sc.canAction(Action.manageEmailSchedules, n.getTaskId())) {
                        if (task.getId().equals(id)) {
                            notifications.add(ni);
                        } else if (parentTasks.contains(task)) {
                            parentNotifications.putItem(task, ni);
                        } else childrenNotifications.putItem(task, ni);
                    }
                }
            }
            Collections.sort(notifications);

            if (!parentNotifications.isEmpty()) seeAlso.add(parentNotifications);
            if (!childrenNotifications.isEmpty()) seeAlso.add(childrenNotifications);

            sc.setRequestAttribute(request, "seeAlso", seeAlso);
            sc.setRequestAttribute(request, "notifications", notifications);
            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_TASK_EMAIL_NOTIFICATION_LIST);
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_TASK_EMAIL_NOTIFICATION_LIST));
            sc.setRequestAttribute(request, "canManageEmailSchedules", sc.canAction(Action.manageEmailSchedules, id));
            return mapping.findForward("taskNotifyJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward deleteNotify(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            NotifySubscribeForm nsf = (NotifySubscribeForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String[] notifies = nsf.getDelete();

            if (notifies != null && sc.canAction(Action.manageEmailSchedules, nsf.getId())) {
                for (String notify : notifies) {
                    AdapterManager.getInstance().getSecuredFilterAdapterManager().unsetNotification(sc, notify);
                }
            }
            return mapping.findForward("taskNotifyPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward clone(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            NotifySubscribeForm nsf = (NotifySubscribeForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String[] notifies = nsf.getDelete();

            if (notifies != null && sc.canAction(Action.manageEmailSchedules, nsf.getId())) {
                for (String notify : notifies) {
                    SecuredNotificationBean snb = AdapterManager.getInstance().getSecuredFindAdapterManager().findNotificationById(sc, notify);
                    String notId = AdapterManager.getInstance().getSecuredFilterAdapterManager().setNotification(sc, snb.getName() + "_clone", snb.getFilterId(), snb.getUserId(), snb.getGroupId(), snb.getTaskId());
                    for (SecuredNotificationBean bean : AdapterManager.getInstance().getSecuredFilterAdapterManager().getNotificationList(sc, snb.getFilterId(), snb.getTaskId())) {
                        if (bean.getId().equals(notId) && sc.canAction(Action.manageEmailSchedules, snb.getTaskId())) {
                            String condition = snb.getCondition();
                            AdapterManager.getInstance().getSecuredFilterAdapterManager().updateNotification(sc, notId, bean.getFilterId(), snb.getName() + "_clone", snb.getTemplate(), condition, bean.getGroupId(), bean.getUserId());
                            break;
                        }
                    }
                }
            }
            return mapping.findForward("taskNotifyPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }
}
