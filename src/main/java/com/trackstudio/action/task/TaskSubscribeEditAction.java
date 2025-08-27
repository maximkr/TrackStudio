package com.trackstudio.action.task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
import com.trackstudio.exception.AccessDeniedException;
import com.trackstudio.form.NotifySubscribeForm;
import com.trackstudio.kernel.cache.AbstractPluginCacheItem;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.cache.PluginCacheManager;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.model.Filter;
import com.trackstudio.secured.SecuredFilterBean;
import com.trackstudio.secured.SecuredPrstatusBean;
import com.trackstudio.secured.SecuredSubscriptionBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.Null;
import com.trackstudio.tools.Tab;
import com.trackstudio.view.PrstatusViewHTML;
import com.trackstudio.view.TaskViewHTMLShort;
import com.trackstudio.view.UserViewHTMLLinked;

public class TaskSubscribeEditAction extends TSDispatchAction {
    private static Log log = LogFactory.getLog(TaskSubscribeEditAction.class);

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");

            NotifySubscribeForm nsf = (NotifySubscribeForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().taskHeader(nsf, sc, request, true);
            String userId = nsf.getUser();
            String subscriptionId = nsf.getSubscriptionId();

            if (!sc.canAction(Action.manageEmailSchedules, id))
                return null;

            SecuredSubscriptionBean bean = AdapterManager.getInstance().getSecuredFindAdapterManager().findSubscriptionById(sc, nsf.getSubscriptionId());

            List<SecuredFilterBean> filterList = new ArrayList<SecuredFilterBean>();
            for (SecuredFilterBean sfb : AdapterManager.getInstance().getSecuredFilterAdapterManager().getTaskFilterList(sc, id)) {
                if (sfb.isPrivate() && (bean.getUserId() == null || !sc.getUserId().equals(bean.getUser().getId())))
                    continue;
                filterList.add(sfb);
            }

            if (subscriptionId != null) {
                sc.setRequestAttribute(request, "notification", bean);
                if (!bean.canManage())
                    throw new AccessDeniedException(this.getClass(), "updateNotification", sc, "!notification.canUpdate()", nsf.getSubscriptionId());
                nsf.setTemplate(bean.getTemplate());
                nsf.setNotificationId(bean.getId());
                nsf.setFilterId(bean.getFilterId());
                nsf.setName(bean.getName());
                nsf.setMutable(true);
                if (bean.getUserId() != null) {
                    nsf.setUser(bean.getUserId());
                    //sc.setRequestAttribute(request, "connectedToUser", bean.getUser());
                } else {
                    nsf.setUser("PR_" + bean.getGroupId());
                    //sc.setRequestAttribute(request, "connectedToGroup", bean.getGroup());
                }
            }
            TreeSet<SecuredUserBean> users = new TreeSet<SecuredUserBean>(AdapterManager.getInstance().getSecuredUserAdapterManager().getUserListForNewAcl(sc, sc.getUserId()));
            users.add(sc.getUser());
            sc.setRequestAttribute(request, "userCollection", users);
            TreeSet<SecuredPrstatusBean> statuses = new TreeSet<SecuredPrstatusBean>(AdapterManager.getInstance().getSecuredPrstatusAdapterManager().getEditablePrstatusList(sc));
            sc.setRequestAttribute(request, "groupCollection", statuses);

            List<AbstractPluginCacheItem> templates = PluginCacheManager.getInstance().getOnlyFtlTemplateForEmail();

            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_TASK_EMAIL_SUBSCRIPTION_PROPERTIES);
            sc.setRequestAttribute(request, "subscription", bean);
            sc.setRequestAttribute(request, "subscriptionId", bean.getId());
            sc.setRequestAttribute(request, "connectedTo", new TaskViewHTMLShort(bean.getTask(), request.getContextPath()).getView(bean.getTask()).getName());
            sc.setRequestAttribute(request, "usersourceId", bean.getUserId() != null ? bean.getUserId() : "PR_" + bean.getGroupId());
            sc.setRequestAttribute(request, "usersource", bean.getUserId() != null ? new UserViewHTMLLinked(bean.getUser(), request.getContextPath()).getPath() : new PrstatusViewHTML(bean.getGroup(), request.getContextPath()).getName());
            sc.setRequestAttribute(request, "templates", templates);
            sc.setRequestAttribute(request, "filterId", bean.getFilterId());
            sc.setRequestAttribute(request, "startDate", Null.stripNullText(sc.getUser().getDateFormatter().parse(bean.getStartdate())));
            sc.setRequestAttribute(request, "stopDate", Null.stripNullText(sc.getUser().getDateFormatter().parse(bean.getStopdate())));
            sc.setRequestAttribute(request, "nextRun", Null.stripNullText(sc.getUser().getDateFormatter().parse(bean.getNextrun())));
            sc.setRequestAttribute(request, "filterList", filterList);
            sc.setRequestAttribute(request, "intervalList", sc.getUser().getDateFormatter().getIntervalSelectTag(bean.getInterval()));
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_TASK_EMAIL_SUBSCRIPTION_PROPERTIES));

            sc.setRequestAttribute(request, "tabSubscribeView", new Tab(true, false));
            sc.setRequestAttribute(request, "tabSubscribeEdit", new Tab(true, true));
            sc.setRequestAttribute(request, "tabNotifyEdit", new Tab(false, false));
            sc.setRequestAttribute(request, "tabNotifyView", new Tab(false, false));
            sc.setRequestAttribute(request, "tabNotifyCreate", new Tab(false, false));
            sc.setRequestAttribute(request, "tabSubscribeCreate", new Tab(false, false));

            return mapping.findForward("taskSubscribeEditJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward addSubscribe(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");

            NotifySubscribeForm nsf = (NotifySubscribeForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = nsf.getId();
            if (!sc.canAction(Action.manageEmailSchedules, id)) return null;
            String filterId = nsf.getFilterId();
            String oldFilterId = nsf.getOldFilterId();
            String subId = nsf.getSubscriptionId();
            String template = nsf.getTemplate();
            if ("USERDEFINED".equals(template)) template = null;
            String startDate = nsf.getStartDate();
            String stopDate = nsf.getStopDate();
            String nextRun = nsf.getNextRun();
            String interval = nsf.getInterval();
            String name = nsf.getName();
            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_TASK_EMAIL_SUBSCRIPTION_PROPERTIES);

            if (oldFilterId == null || oldFilterId.length() == 0) oldFilterId = filterId;

            try {
                if (startDate != null)
                    sc.getUser().getDateFormatter().parseToCalendar(startDate);
            } catch (Exception e) {
                throw new Exception("Incorrect Start Date");
            }

            try {
                if (stopDate != null)
                    sc.getUser().getDateFormatter().parseToCalendar(stopDate);
            } catch (Exception e) {
                throw new Exception("Incorrect Stop Date");
            }

            try {
                if (nextRun != null)
                    sc.getUser().getDateFormatter().parseToCalendar(nextRun);
            } catch (Exception e) {
                throw new Exception("Incorrect Next Run Date");
            }

            Calendar cStartDate = sc.getUser().getDateFormatter().parseToCalendar(startDate);
            Calendar cStopDate = sc.getUser().getDateFormatter().parseToCalendar(stopDate);
            Calendar cNextRun = sc.getUser().getDateFormatter().parseToCalendar(nextRun);
            long startDate2 = cStartDate != null ? cStartDate.getTimeInMillis() : -1L;
            long stopDate2 = cStopDate != null ? cStopDate.getTimeInMillis() : -1L;
            long nextRun2 = cNextRun != null ? cNextRun.getTimeInMillis() : -1L;
            Integer interval2 = new Integer(interval);
            String userId = null;
            String groupId = null;
            String user = nsf.getUser();
            if (user.startsWith("PR_"))
                groupId = user.substring("PR_".length());
            else
                userId = user;

            if (!Null.isNotNull(subId)) {
                subId = AdapterManager.getInstance().getSecuredFilterAdapterManager().createSubscription(sc, name, userId, groupId, id, filterId, interval2);
                log.debug("CLOSE SESSION TaskSubscribeEditActiont");
                //            HibernateSession.closeSession();
            }
            for (SecuredSubscriptionBean bean : AdapterManager.getInstance().getSecuredFilterAdapterManager().getSubscriptionList(sc, oldFilterId, id)) {
                if (bean.getId().equals(subId)) {
                    if (!bean.getInterval().equals(interval2)) {//to change interval -> reload time nextRun = currentTime + interval. user confuse with it #23861
                        Calendar cal = new GregorianCalendar();
                        cal.setTimeZone(sc.getUser().getDateFormatter().getTimeZone());
                        cal.setTimeInMillis((new Date()).getTime());
                        cal.add(Calendar.MINUTE, interval2);
                        nextRun2 = cal.getTimeInMillis();
                    }
                    AdapterManager.getInstance().getSecuredFilterAdapterManager().updateSubscription(sc, subId, name, filterId, template, startDate2, stopDate2, nextRun2, interval2, userId, groupId);
                    log.debug("CLOSE SESSION TaskSubscribeEditActiont");
                    //                HibernateSession.stopTimer();
                }
            }

            nsf.setSubscriptionId(subId);
            nsf.setFilterId(filterId);
            nsf.setMutable(false);
            log.debug("CLOSE SESSION TaskSubscribeEditActiont");
            //        HibernateSession.stopTimer();
            return mapping.findForward("taskSubscribeViewPage");
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
            return mapping.findForward("taskSubscribeEditFilterJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            NotifySubscribeForm nsf = (NotifySubscribeForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().taskHeader(nsf, sc, request, true);
            String userId = nsf.getUser();
            String subscriptionId = nsf.getSubscriptionId();
            ArrayList<SecuredFilterBean> filters = new ArrayList<SecuredFilterBean>();
            ArrayList<SecuredFilterBean> filtersRaw = AdapterManager.getInstance().getSecuredFilterAdapterManager().getTaskFilterList(sc, id);

            if (!sc.canAction(Action.manageEmailSchedules, id))
                return null;

            SecuredTaskBean stb = new SecuredTaskBean(id, sc);
            SecuredSubscriptionBean bean = AdapterManager.getInstance().getSecuredFindAdapterManager().findSubscriptionById(sc, nsf.getSubscriptionId());
            List<AbstractPluginCacheItem> templates = PluginCacheManager.getInstance().getOnlyFtlTemplateForEmail();
            nsf.setTemplate(sc.getUser().getTemplate());

            Calendar cal = new GregorianCalendar();
            cal.setTimeZone(sc.getUser().getDateFormatter().getTimeZone());
            cal.setTimeInMillis((new Date()).getTime());

            Calendar stop = new GregorianCalendar();
            stop.setTimeZone(sc.getUser().getDateFormatter().getTimeZone());
            stop.setTimeInMillis((new Date()).getTime());
            stop.add(Calendar.YEAR, 10);

            if (subscriptionId != null) {
                sc.setRequestAttribute(request, "notification", bean);
                if (!bean.canManage())
                    throw new AccessDeniedException(this.getClass(), "updateNotification", sc, "!notification.canUpdate()", nsf.getSubscriptionId());
                nsf.setTemplate(bean.getTemplate());
                nsf.setNotificationId(bean.getId());
                nsf.setFilterId(bean.getFilterId());
                nsf.setName(bean.getName());
                nsf.setMutable(true);
                userId = bean.getUserId();
                if (bean.getUserId() != null) {
                    nsf.setUser(bean.getUserId());
                    sc.setRequestAttribute(request, "connectedToUser", bean.getUser());
                } else {
                    nsf.setUser("PR_" + bean.getGroupId());
                    sc.setRequestAttribute(request, "connectedToGroup", bean.getGroup());
                }
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
            }

            if (userId != null && sc.getUserId().equals(userId)) {
                for (SecuredFilterBean t : filtersRaw) {
                    if (!t.isPrivate() || t.getOwnerId().equals(userId))
                        filters.add(t);
                }
            } else {
                for (SecuredFilterBean t : filtersRaw) {
                    if (!t.isPrivate()) filters.add(t);
                }
            }

            sc.setRequestAttribute(request, "filterList", filters);
            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_TASK_EMAIL_SUBSCRIPTION_PROPERTIES);
            sc.setRequestAttribute(request, "startDate", Null.stripNullText(sc.getUser().getDateFormatter().parse(cal)));
            sc.setRequestAttribute(request, "stopDate", Null.stripNullText(sc.getUser().getDateFormatter().parse(stop)));
            sc.setRequestAttribute(request, "nextRun", Null.stripNullText(sc.getUser().getDateFormatter().parse(cal)));
            sc.setRequestAttribute(request, "subscriptionId", null);
            sc.setRequestAttribute(request, "connectedTo", new TaskViewHTMLShort(stb, request.getContextPath()).getView(stb).getName());
            //sc.setRequestAttribute(request,"usersourceId", user != null ? user.getId() : "PR_" + group.getId());
            //sc.setRequestAttribute(request,"usersource", user != null ? new UserViewHTMLLinked(user, request.getContextPath()).getPath() : new PrstatusViewHTML(group, request.getContextPath()).getName());
            sc.setRequestAttribute(request, "templates", templates);
            sc.setRequestAttribute(request, "filterId", null);
            sc.setRequestAttribute(request, "id", id);
            sc.setRequestAttribute(request, "intervalList", sc.getUser().getDateFormatter().getIntervalSelectTag(30));
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_TASK_EMAIL_SUBSCRIPTION_PROPERTIES));

            sc.setRequestAttribute(request, "tabSubscribeView", new Tab(false, false));
            sc.setRequestAttribute(request, "tabSubscribeEdit", new Tab(false, false));
            sc.setRequestAttribute(request, "tabNotifyEdit", new Tab(false, false));
            sc.setRequestAttribute(request, "tabNotifyView", new Tab(false, false));
            sc.setRequestAttribute(request, "tabNotifyCreate", new Tab(false, false));
            sc.setRequestAttribute(request, "tabSubscribeCreate", new Tab(true, true));

            return mapping.findForward("taskSubscribeEditJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }
}