package com.trackstudio.action.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;

import javax.servlet.http.Cookie;
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
import com.trackstudio.constants.UdfConstants;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.secured.SecuredNotificationBean;
import com.trackstudio.secured.SecuredSubscriptionBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUDFValueBean;
import com.trackstudio.secured.SecuredUserAttachmentBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.EggBasket;
import com.trackstudio.tools.Null;
import com.trackstudio.tools.formatter.DateFormatter;
import com.trackstudio.tools.textfilter.HTMLEncoder;

public class UserViewAction extends TSDispatchAction {
    private static Log log = LogFactory.getLog(UserViewAction.class);

    public ActionForward page(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().userHeader(null, sc, request);

            if (!sc.allowedByUser(id))
                return null;

            SecuredUserBean sub = new SecuredUserBean(id, sc);

            ArrayList<SecuredUserAttachmentBean> container = sub.getAttachments();

            boolean isDescription = false;
            boolean isPreview = false;
            if (container != null) {
                for (SecuredUserAttachmentBean ali : container) {
                    if (ali.getDescription() != null && ali.getDescription().length() != 0)
                        isDescription = true;
                    if (ali.isThumbnailed())
                        isPreview = true;
                }
            }

            sc.setRequestAttribute(request, "isDescription", isDescription);
            sc.setRequestAttribute(request, "isPreview", isPreview);
            sc.setRequestAttribute(request, "attachments", container);
            sc.setRequestAttribute(request, "attaNum", container != null ? container.size() : 0);
            sc.setRequestAttribute(request, "viewUdfList", sub.getUDFValuesList());
            sc.setRequestAttribute(request, "userId", sub.getId());

            sc.setRequestAttribute(request, "user", sub);

            Integer childAllowed = sub.getChildrenAllowed();

            sc.setRequestAttribute(request, "childallowed", childAllowed != null ? childAllowed.toString() : "");
            sc.setRequestAttribute(request, "template", HTMLEncoder.encode(sub.getTemplate()));

            StringBuffer notice = new StringBuffer();
            notice.append(Null.stripNullText(sc.getUser().getDateFormatter().parse(sub.getUser().getEmergencyNoticeDate())));
            notice.append(" ").append(I18n.getString(sc.getLocale(), "EMERGENCY_NOTICE")).append(" ").append(I18n.getString(sc.getLocale(), "TO_FROM")).append(" ").append(sub.getUser().getName());
            sc.setRequestAttribute(request, "noticeDate", notice.toString());
            sc.setRequestAttribute(request, "notice", HTMLEncoder.encode(sub.getUser().getEmergencyNotice()));
            sc.setRequestAttribute(request, "enabled", sub.isEnabled());
            boolean himselfOrParent = (sub.getId().equals(sc.getUserId()) || AdapterManager.getInstance().getSecuredUserAdapterManager().isParentOf(sc, sub.getId(), sc.getUserId()));
            sc.setRequestAttribute(request, "canEditActive", sc.canAction(Action.editUserActive, id) && !himselfOrParent);

            EggBasket<SecuredUDFValueBean, SecuredTaskBean> refs = AdapterManager.getInstance().getSecuredIndexAdapterManager().getReferencedTasksForUser(sub);
            sc.setRequestAttribute(request, "refTasks", refs);
            EggBasket<SecuredUDFValueBean, SecuredUserBean> urefs = AdapterManager.getInstance().getSecuredIndexAdapterManager().getReferencedUsersForUser(sub);
            sc.setRequestAttribute(request, "refUsers", urefs);
            Locale locale = DateFormatter.toLocale(sub.getLocale());
            String flag = locale.getCountry().toLowerCase(Locale.ENGLISH);
            if (flag.length() == 0)
                flag = "-";
            String localeAsString = locale.getDisplayName(DateFormatter.toLocale(sub.getSecure().getLocale()));

            sc.setCurrentSpace("UserViewAction", request);

            sc.setRequestAttribute(request, "locale", localeAsString);
            sc.setRequestAttribute(request, "country", flag);
            sc.setRequestAttribute(request, "canCreateUser", sc.canAction(Action.createUser, id));
            boolean canEditUser = sub.canManage() && ((sc.getUserId().equals(sub.getId()) && sc.canAction(Action.editUserHimself, id)) || (!sc.getUserId().equals(sub.getId()) && !himselfOrParent && sc.canAction(Action.editUserChildren, id)));
            sc.setRequestAttribute(request, "canEditUser", canEditUser);
            sc.setRequestAttribute(request, "canEditUserManager", sc.canAction(Action.cutPasteUser, id) && (sc.getUserId().equals(sub.getId()) && sc.canAction(Action.editUserHimself, id)) || sc.canAction(Action.editUserChildren, id));


            Cookie[] cook = request.getCookies();
            String selectedIds = "";
            if (cook != null) {

                for (Cookie c : cook) {
                    log.debug(c.getName() + c.getValue());
                    if (c.getName().equals("_selectedId") && c.getValue() != null && c.getValue().length() > 0) {
                        selectedIds = c.getValue();
                    }
                }
            }
            ArrayList<SecuredUserBean> selected = new ArrayList<SecuredUserBean>();
            for (String s : selectedIds.split(UdfConstants.SPLIT_SEPARATOR)) {
                SecuredUserBean t = AdapterManager.getInstance().getSecuredFindAdapterManager().findUserById(sc, s);
                if (t != null) selected.add(t);

            }
            sc.setRequestAttribute(request, "selectedIds", selected);

            sc.setRequestAttribute(request, "canViewUserAttachments", sc.canAction(Action.viewUserAttachments, id));
            sc.setRequestAttribute(request, "canManageUserAttachments", canEditUser || sc.canAction(Action.manageUserAttachments, id));
            sc.setRequestAttribute(request, "canCreateUserAttachments", sc.canAction(Action.createUserAttachments, id));
            sc.setRequestAttribute(request, "canChangePassword", sub.canManage() && ((sc.getUserId().equals(sub.getId()) && sc.canAction(Action.editUserPasswordHimself, id)) || (!sc.getUserId().equals(sub.getId()) && !himselfOrParent && sc.canAction(Action.editUserChildrenPassword, id))));

            sc.setRequestAttribute(request, "canViewUserACLs", sc.canAction(Action.manageUserACLs, id));
            sc.setRequestAttribute(request, "canViewNotification", true);
            sc.setRequestAttribute(request, "canViewSubscription", true);
            sc.setRequestAttribute(request, "showClipboardButton", sc.canAction(Action.cutPasteUser, id));
            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_USER_OVERVIEW);
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_USER_OVERVIEW));
            new UserViewACLAction().viewAcl(sc, id, request);
            TreeSet<SecuredNotificationBean> retList = new TreeSet<SecuredNotificationBean>();
            List<SecuredNotificationBean> notifications = AdapterManager.getInstance().getSecuredFilterAdapterManager().getUserNotificationList(sc, id);
            for (SecuredNotificationBean snb : notifications) {
                if (snb.getTaskId() != null && sc.taskOnSight(snb.getTaskId())) {
                    retList.add(snb);
                }
            }
            sc.setRequestAttribute(request, "notifications", retList);
            TreeSet<SecuredSubscriptionBean> retList2 = new TreeSet<SecuredSubscriptionBean>();
            List<SecuredSubscriptionBean> subscriptions = AdapterManager.getInstance().getSecuredFilterAdapterManager().getUserSubscriptionList(sc, id);
            if (subscriptions != null)
                for (SecuredSubscriptionBean ssb : subscriptions) {
                    if (ssb.getTaskId() != null && sc.taskOnSight(ssb.getTaskId())) {
                        retList2.add(ssb);
                    }
                }
            sc.setRequestAttribute(request, "subscriptions", new ArrayList<SecuredSubscriptionBean>(retList2));
            selectUserTab(sc, id, "tabUser", request);

            return actionMapping.findForward("userViewJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }


}