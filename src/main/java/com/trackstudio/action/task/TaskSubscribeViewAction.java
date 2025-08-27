package com.trackstudio.action.task;

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
import com.trackstudio.exception.UserException;
import com.trackstudio.form.NotifySubscribeForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.secured.SecuredSubscriptionBean;
import com.trackstudio.startup.Config;
import com.trackstudio.startup.I18n;

public class TaskSubscribeViewAction extends TSDispatchAction {
    private static Log log = LogFactory.getLog(TaskSubscribeEditAction.class);

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");

            NotifySubscribeForm nsf = (NotifySubscribeForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().taskHeader(nsf, sc, request, true);
            if (!sc.canAction(Action.manageEmailSchedules, id)) return null;
            SecuredSubscriptionBean bean = AdapterManager.getInstance().getSecuredFindAdapterManager().findSubscriptionById(sc, nsf.getSubscriptionId());
            sc.setRequestAttribute(request, "subscription", bean);
            if (bean.getTemplate() == null)
                sc.setRequestAttribute(request, "template", I18n.getString(sc.getLocale(), "USER_DEFINED"));
            else
                sc.setRequestAttribute(request, "template", I18n.getString(sc.getLocale(), bean.getTemplate()));
            sc.setRequestAttribute(request, "subscriptionId", bean.getId());
            sc.setRequestAttribute(request, "canEdit", sc.canAction(Action.manageEmailSchedules, id) && bean.canManage());
            sc.setRequestAttribute(request, "canCreate", sc.canAction(Action.manageEmailSchedules, id));

            return mapping.findForward("taskSubscribeViewJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward test(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            NotifySubscribeForm nsf = (NotifySubscribeForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String taskId = GeneralAction.getInstance().taskHeader(nsf, sc, request, false);
            String subId = nsf.getSubscriptionId();
            SecuredSubscriptionBean ssb = AdapterManager.getInstance().getSecuredFindAdapterManager().findSubscriptionById(sc, subId);
            if (Config.getInstance().isSendMail()) {
                if (ssb.getUserId() != null && (ssb.getUser().getEmail() == null || ssb.getUser().getEmail().length() == 0)) {
                    sc.setRequestAttribute(request, "testResult", I18n.getString(sc.getLocale(), "USER_EMAIL_IS_EMPTY"));
                } else {
                    boolean result = AdapterManager.getInstance().getFilterNotifyAdapterManager().processSubscription(subId, taskId, true);
                    String msg = result ? I18n.getString(sc.getLocale(), "NOTIFICATION_WAS_SUCCESSFULLY_SENT") : I18n.getString(sc.getLocale(), "NOTIFICATION_WASNT_SENT");
                    sc.setRequestAttribute(request, "testResult", msg);
                }
            } else
                throw new UserException("CAN_NOT_SEND_NOTIFICATION");
            return mapping.findForward("taskSubscribeViewPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }
    }
}