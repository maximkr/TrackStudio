package com.trackstudio.action.task;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

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
import com.trackstudio.constants.FilterConstants;
import com.trackstudio.exception.AccessDeniedException;
import com.trackstudio.form.NotifySubscribeForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.cache.PluginCacheManager;
import com.trackstudio.kernel.cache.PluginType;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.model.Filter;
import com.trackstudio.secured.SecuredFilterBean;
import com.trackstudio.secured.SecuredNotificationBean;
import com.trackstudio.secured.SecuredPrstatusBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.Null;
import com.trackstudio.tools.Tab;
import com.trackstudio.view.PrstatusViewHTML;
import com.trackstudio.view.TaskViewHTMLShort;
import com.trackstudio.view.UserViewHTMLLinked;

public class TaskNotifyEditAction extends TSDispatchAction {
    private static Log log = LogFactory.getLog(TaskNotifyEditAction.class);

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");

            NotifySubscribeForm nsf = (NotifySubscribeForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().taskHeader(nsf, sc, request, true);

            if (!sc.canAction(Action.manageEmailSchedules, id))
                return null;

            String notificationId = nsf.getNotificationId();
            String userId = nsf.getUser();
            ArrayList<SecuredFilterBean> filters = new ArrayList<SecuredFilterBean>();
            ArrayList<SecuredFilterBean> filtersRaw = AdapterManager.getInstance().getSecuredFilterAdapterManager().getTaskFilterList(sc, id);

            if (notificationId != null) {
                SecuredNotificationBean bean = AdapterManager.getInstance().getSecuredFindAdapterManager().findNotificationById(sc, notificationId);
                sc.setRequestAttribute(request, "filterId", bean.getFilterId());
                sc.setRequestAttribute(request, "notification", bean);
                if (!bean.canManage())
                    throw new AccessDeniedException(this.getClass(), "updateNotification", sc, "!notification.canUpdate()", notificationId);
                nsf.setTemplate(bean.getTemplate());
                nsf.setNotificationId(bean.getId());
                nsf.setFilterId(bean.getFilterId());
                nsf.setName(bean.getName());
                nsf.setFireNewTask(bean.isFireNewTask());
                nsf.setFireUpdatedTask(bean.isFireUpdatedTask());
                nsf.setFireNewAttachment(bean.isFireNewAttachment());
                nsf.setFireNewMessage(bean.isFireNewMessage());
                nsf.setFireNotI(bean.isFireNotI());
                nsf.setMutable(true);
                userId = bean.getUserId();
                if (bean.getUserId() != null) {
                    nsf.setUser(bean.getUserId());
                    //                sc.setRequestAttribute(request, "connectedToUser", bean.getUser());
                } else {
                    nsf.setUser("PR_" + bean.getGroupId());
                    //                sc.setRequestAttribute(request, "connectedToGroup", bean.getGroup());
                }
                TreeSet<SecuredUserBean> users = new TreeSet<SecuredUserBean>(AdapterManager.getInstance().getSecuredUserAdapterManager().getUserListForNewAcl(sc, sc.getUserId()));
                users.add(sc.getUser());
                sc.setRequestAttribute(request, "userCollection", users);
                TreeSet<SecuredPrstatusBean> statuses = new TreeSet<SecuredPrstatusBean>(AdapterManager.getInstance().getSecuredPrstatusAdapterManager().getEditablePrstatusList(sc));
                sc.setRequestAttribute(request, "groupCollection", statuses);
            } else {
                if (userId != null && sc.getUserId().equals(userId)) {
                    nsf.setUser(userId);
                    sc.setRequestAttribute(request, "connectedToUser", sc.getUser());
                } else {
                    TreeSet<SecuredUserBean> users = new TreeSet<SecuredUserBean>(AdapterManager.getInstance().getSecuredUserAdapterManager().getUserListForNewAcl(sc, sc.getUserId()));
                    users.add(sc.getUser());
                    sc.setRequestAttribute(request, "userCollection", users);
                    TreeSet<SecuredPrstatusBean> statuses = new TreeSet<SecuredPrstatusBean>(AdapterManager.getInstance().getSecuredPrstatusAdapterManager().getEditablePrstatusList(sc));
                    sc.setRequestAttribute(request, "groupCollection", statuses);
                }
                nsf.setFireNewTask(true);
                nsf.setFireUpdatedTask(true);
                nsf.setFireNewAttachment(true);
                nsf.setFireNewMessage(true);
                nsf.setFireNotI(false);
            }

            if (userId != null && sc.getUserId().equals(userId)) {
                for (SecuredFilterBean t : filtersRaw) {
                    if (!t.isPrivate() || t.getOwnerId().equals(userId)) filters.add(t);
                }
            } else {
                for (SecuredFilterBean t : filtersRaw) {
                    if (!t.isPrivate()) filters.add(t);
                }
            }

            sc.setRequestAttribute(request, "filterList", filters);
            sc.setRequestAttribute(request, "templates", PluginCacheManager.getInstance().getOnlyFtlTemplateForEmail());
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_TASK_EMAIL_NOTIFICATION_PROPERTIES));

            return mapping.findForward("taskNotifyEditJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward updateFilter(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            NotifySubscribeForm nsf = (NotifySubscribeForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            GeneralAction.getInstance().taskHeader(nsf, sc, request, false);
            String userId = request.getParameter("user");
            String taskId = request.getParameter("taskId");
            List<Filter> filterList = KernelManager.getFilter().getTaskFilterList(taskId, userId);
            sc.setRequestAttribute(request, "filterList", filterList);
            return mapping.findForward("taskNotifyEditFilterJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");

            NotifySubscribeForm nsf = (NotifySubscribeForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().taskHeader(nsf, sc, request, true);
            SecuredTaskBean stb = new SecuredTaskBean(id, sc);
            if (nsf.getUser() != null && nsf.getUser().length() > 0) {
                SecuredPrstatusBean group = null;
                SecuredUserBean user = null;
                if (nsf.getUser().startsWith("PR_"))
                    group = AdapterManager.getInstance().getSecuredFindAdapterManager().findPrstatusById(sc, nsf.getUser().substring("PR_".length()));
                else
                    user = new SecuredUserBean(nsf.getUser(), sc);

                List<SecuredFilterBean> filterList = new ArrayList<SecuredFilterBean>();
                for (SecuredFilterBean sfb : AdapterManager.getInstance().getSecuredFilterAdapterManager().getTaskFilterList(sc, id)) {
                    if (!sfb.isPrivate() || (user != null && sc.getUserId().equals(user.getId()))) {
                        filterList.add(sfb);
                    }
                }

                nsf.setTemplate(user != null ? user.getTemplate() : sc.getUser().getTemplate());
                nsf.setFireNewTask(true);
                nsf.setFireUpdatedTask(true);
                nsf.setFireNewAttachment(true);
                nsf.setFireNewMessage(true);
                sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_TASK_EMAIL_NOTIFICATION_PROPERTIES);
                sc.setRequestAttribute(request, "connectedTo", (new TaskViewHTMLShort(stb, request.getContextPath())).getView(stb).getName());
                sc.setRequestAttribute(request, "notificationId", null);
                sc.setRequestAttribute(request, "usersourceId", user != null ? user.getId() : "PR_" + group.getId());
                sc.setRequestAttribute(request, "usersource", user != null ? new UserViewHTMLLinked(user, request.getContextPath()).getPath() : new PrstatusViewHTML(group, request.getContextPath()).getName());
                sc.setRequestAttribute(request, "templates", PluginCacheManager.getInstance().list(PluginType.EMAIL).get(PluginType.EMAIL));
                sc.setRequestAttribute(request, "filterList", filterList);
                sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_TASK_EMAIL_NOTIFICATION_PROPERTIES));
                sc.setRequestAttribute(request, "id", id);
                sc.setRequestAttribute(request, "tabSubscribeView", new Tab(false, false));
                sc.setRequestAttribute(request, "tabSubscribeEdit", new Tab(false, false));
                sc.setRequestAttribute(request, "tabNotifyEdit", new Tab(false, false));
                sc.setRequestAttribute(request, "tabNotifyView", new Tab(false, false));
                sc.setRequestAttribute(request, "tabNotifyCreate", new Tab(true, true));
                sc.setRequestAttribute(request, "tabSubscribeCreate", new Tab(false, false));
                return mapping.findForward("taskNotifyEditJSP");
            }
            return mapping.findForward("taskNotifyPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward addNotify(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                   HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");

            NotifySubscribeForm nsf = (NotifySubscribeForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().taskHeader(nsf, sc, request, false);
            String filterId = nsf.getFilterId();
            String template = nsf.getTemplate();
            String name = nsf.getName();
            if ("USERDEFINED".equals(template)) template = null;
            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_TASK_EMAIL_NOTIFICATION_PROPERTIES);
            String userId = null;
            String groupId = null;
            String user = nsf.getUser();
            if (user.startsWith("PR_"))
                groupId = user.substring("PR_".length());
            else
                userId = user;
            String condition = buildCondition(nsf);
            String notId;
            if (Null.isNotNull(nsf.getNotificationId())) {
                notId = nsf.getNotificationId();
                AdapterManager.getInstance().getSecuredFilterAdapterManager().updateNotification(sc, notId, nsf.getFilterId(), name, template, condition, groupId, userId);
            } else {
                notId = AdapterManager.getInstance().getSecuredFilterAdapterManager().setNotification(sc, name, filterId, userId, groupId, id);
            }
            for (SecuredNotificationBean bean : AdapterManager.getInstance().getSecuredFilterAdapterManager().getNotificationList(sc, filterId, id)) {
                if (bean.getId().equals(notId) && sc.canAction(Action.manageEmailSchedules, id)) {
                    AdapterManager.getInstance().getSecuredFilterAdapterManager().updateNotification(sc, notId, bean.getFilterId(), name, template, condition, bean.getGroupId(), bean.getUserId());
                    break;
                }
            }
            nsf.setNotificationId(notId);
            nsf.setFilterId(filterId);
            nsf.setMutable(false);
            log.debug("CLOSE SESSION TaskNotifyEditAction");
            return mapping.findForward("taskNotifyViewPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    private static String buildCondition(NotifySubscribeForm nsf) {
        StringBuilder sb = new StringBuilder();
        sb.append(nsf.isFireNotI() ? FilterConstants.NOT_I : "");
        sb.append(nsf.isFireNewTask() ? FilterConstants.FIRE_NEW_TASK : "");
        sb.append(nsf.isFireUpdatedTask() ? FilterConstants.FIRE_UPDATED_TASK : "");
        sb.append(nsf.isFireNewAttachment() ? FilterConstants.FIRE_NEW_ATTACHMENT : "");
        sb.append(nsf.isFireNewMessage() ? FilterConstants.FIRE_NEW_MESSAGE : "");
        return sb.toString();
    }
}
