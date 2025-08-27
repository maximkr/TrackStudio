package com.trackstudio.action.task;

import java.util.ArrayList;
import java.util.Collections;
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
import com.trackstudio.containers.SubscriptionListItem;
import com.trackstudio.form.NotifySubscribeForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.secured.SecuredFilterBean;
import com.trackstudio.secured.SecuredPrstatusBean;
import com.trackstudio.secured.SecuredSubscriptionBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.EggBasket;

public class TaskSubscribeAction extends TSDispatchAction {
    private static Log log = LogFactory.getLog(TaskSubscribeAction.class);

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            NotifySubscribeForm ff = (NotifySubscribeForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().taskHeader(ff, sc, request, true);
            if (!sc.canAction(Action.manageEmailSchedules, id)) return null;
            TreeSet<SecuredUserBean> users = new TreeSet<SecuredUserBean>(AdapterManager.getInstance().getSecuredUserAdapterManager().getUserListForNewAcl(sc, sc.getUserId()));
            users.add(sc.getUser());
            TreeSet<SecuredUserBean> finalusers = new TreeSet<SecuredUserBean>();
            finalusers.addAll(users);

            ArrayList<SubscriptionListItem> subscriptions = new ArrayList<SubscriptionListItem>();
            EggBasket<SecuredTaskBean, SubscriptionListItem> parentSubscriptions = new EggBasket<SecuredTaskBean, SubscriptionListItem>();
            EggBasket<SecuredTaskBean, SubscriptionListItem> childrenSubscriptions = new EggBasket<SecuredTaskBean, SubscriptionListItem>();
            ArrayList<EggBasket> seeAlso = new ArrayList<EggBasket>();
            ArrayList<SecuredTaskBean> parentTasks = AdapterManager.getInstance().getSecuredTaskAdapterManager().getTaskChain(sc, null, id);

            ArrayList<SecuredFilterBean> fli = AdapterManager.getInstance().getSecuredFilterAdapterManager().getAllTaskFilterList(sc, id);
            for (SecuredFilterBean currentFilter : fli) {
                List<SecuredSubscriptionBean> subscriptionSet = AdapterManager.getInstance().getSecuredFilterAdapterManager().getAllSubscriptionList(sc, currentFilter.getId());
                for (SecuredSubscriptionBean s : subscriptionSet) {
                    SecuredTaskBean task = s.getTask();
                    SubscriptionListItem ni = new SubscriptionListItem(s.getId(), s.getName());
                    ni.setCanUpdate(sc.canAction(Action.manageEmailSchedules, s.getTaskId()) && s.canManage());
                    if (s.getUserId() != null) ni.setUser(s.getUser());
                    if (s.getGroupId() != null) ni.setGroup(s.getGroup());
                    ni.setTask(s.getTask());
                    ni.setFilter(s.getFilter().getName());
                    ni.setFilterId(s.getFilter().getId());
                    if (s.getTemplate() != null)
                        ni.setTemplate(s.getTemplate());
                    else
                        ni.setTemplate(I18n.getString(sc.getLocale(), "USER_DEFINED"));
                    if (sc.canAction(Action.manageEmailSchedules, task.getId())) {
                        if (task.getId().equals(id)) {
                            subscriptions.add(ni);
                        } else if (parentTasks.contains(task)) {
                            parentSubscriptions.putItem(task, ni);
                        } else childrenSubscriptions.putItem(task, ni);
                    }
                }
            }
            Collections.sort(subscriptions);

            if (!parentSubscriptions.isEmpty()) seeAlso.add(parentSubscriptions);
            if (!childrenSubscriptions.isEmpty()) seeAlso.add(childrenSubscriptions);

            TreeSet<SecuredPrstatusBean> statuses = new TreeSet<SecuredPrstatusBean>(AdapterManager.getInstance().getSecuredPrstatusAdapterManager().getEditablePrstatusList(sc));
            ArrayList<SecuredPrstatusBean> al = new ArrayList<SecuredPrstatusBean>(statuses);
            Collections.sort(al);
            sc.setRequestAttribute(request, "seeAlso", seeAlso);
            sc.setRequestAttribute(request, "statusCollection", al);
            sc.setRequestAttribute(request, "userCollection", finalusers);
            sc.setRequestAttribute(request, "paramCollection", finalusers);
            sc.setRequestAttribute(request, "msgHowCreateObject", I18n.getString(sc.getLocale(), "SUBSCRIPTION_ADD"));
            sc.setRequestAttribute(request, "firstParamMsg", I18n.getString(sc.getLocale(), "ADD_NEW"));
            sc.setRequestAttribute(request, "firstParamName", "user");
            sc.setRequestAttribute(request, "msgAddObject", I18n.getString(sc.getLocale(), "SUBSCRIPTION_ADD"));
            sc.setRequestAttribute(request, "createObjectAction", "/TaskSubscribeEditAction.do");


            sc.setRequestAttribute(request, "subscriptions", subscriptions);
            sc.setRequestAttribute(request, "canSubscribeSelf", sc.canAction(Action.manageEmailSchedules, id));
            sc.setRequestAttribute(request, "canSubscribeOthers", sc.canAction(Action.manageEmailSchedules, id));
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_TASK_EMAIL_SUBSCRIPTION_LIST));
            return mapping.findForward("taskSubscribeJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    /**
     * This method removes subscribes
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward deleteSubscribe(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            NotifySubscribeForm ff = (NotifySubscribeForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String[] del = ff.getDelete();
            String id = GeneralAction.getInstance().taskHeader(ff, sc, request, false);
            for (String delSub : del) {
                SecuredSubscriptionBean sub = AdapterManager.getInstance().getSecuredFindAdapterManager().findSubscriptionById(sc, delSub);
                if (sc.canAction(Action.manageEmailSchedules, id))
                    AdapterManager.getInstance().getSecuredFilterAdapterManager().unSubscribe(sc, delSub);
            }

            return mapping.findForward("taskSubscribePage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward clone(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            NotifySubscribeForm ff = (NotifySubscribeForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String[] del = ff.getDelete();
            String id = GeneralAction.getInstance().taskHeader(ff, sc, request, false);
            if (del != null) {
                for (String delSub : del) {
                    SecuredSubscriptionBean sub = AdapterManager.getInstance().getSecuredFindAdapterManager().findSubscriptionById(sc, delSub);
                    String subId = AdapterManager.getInstance().getSecuredFilterAdapterManager().createSubscription(sc, sub.getName() + "_clone", sub.getUserId(), sub.getGroupId(), sub.getTaskId(), sub.getFilterId(), sub.getInterval());
                    log.debug("CLOSE SESSION TaskSubscribeEditActiont");
                    //            HibernateSession.closeSession();
                    for (SecuredSubscriptionBean bean : AdapterManager.getInstance().getSecuredFilterAdapterManager().getSubscriptionList(sc, sub.getFilterId(), id)) {
                        if (bean.getId().equals(subId)) {
                            AdapterManager.getInstance().getSecuredFilterAdapterManager().updateSubscription(sc, subId, sub.getName() + "_clone", sub.getFilterId(), sub.getTemplate(), sub.getStartdate().getTimeInMillis(), sub.getStopdate().getTimeInMillis(), sub.getNextrun().getTimeInMillis(), sub.getInterval(), null, null);
                            break;
                        }
                    }
                }
            }
            return mapping.findForward("taskSubscribePage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }
}