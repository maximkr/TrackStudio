package com.trackstudio.action.user;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SimpleTimeZone;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.trackstudio.action.AttachmentEditAction;
import com.trackstudio.action.GeneralAction;
import com.trackstudio.action.TSDispatchAction;
import com.trackstudio.action.task.ChangeEvent;
import com.trackstudio.app.PageNaming;
import com.trackstudio.app.UDFFormFillHelper;
import com.trackstudio.app.UdfValue;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.adapter.email.EmailUtil;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.app.session.SessionManager;
import com.trackstudio.constants.UdfConstants;
import com.trackstudio.containers.PrstatusListItem;
import com.trackstudio.exception.DuplicateUserLoginException;
import com.trackstudio.exception.UserException;
import com.trackstudio.form.UserForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.cache.PluginCacheManager;
import com.trackstudio.kernel.cache.UserRelatedInfo;
import com.trackstudio.kernel.cache.UserRelatedManager;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.secured.SecuredPrstatusBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUDFBean;
import com.trackstudio.secured.SecuredUDFValueBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.startup.Config;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.Null;
import com.trackstudio.tools.Pair;
import com.trackstudio.tools.ParameterValidator;
import com.trackstudio.tools.Tab;
import com.trackstudio.tools.formatter.DateFormatter;

import static com.trackstudio.tools.Null.isNotNull;

public class UserEditAction extends TSDispatchAction {
    private static Log log = LogFactory.getLog(UserEditAction.class);

    public ActionForward page(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            UserForm userForm = (UserForm) actionForm;
            String id = GeneralAction.getInstance().userHeader(userForm, sc, request);
            SecuredUserBean uc = new SecuredUserBean(id, sc);
            boolean isNew = userForm.getNewUser() != null && userForm.getNewUser().length() != 0;

            String referer = request.getContextPath() + "/UserViewAction.do?method=page&amp;id=" + id;

            if (isNew) {
                String ref = request.getHeader("Referer");
                if (ref != null && ref.length() != 0 && ref.indexOf("UserCreateAction") == -1) {
                    referer = ref;
                } else if (uc.hasChildren())
                    referer = request.getContextPath() + "/UserListAction.do?method=page&amp;id=" + id;

                userForm.setLocale(uc.getLocale());
                userForm.setTimezone(uc.getTimezone());
                userForm.setTemplate("1");
                userForm.setEnabled(true);

                userForm.setCompany(uc.getCompany());
                userForm.setProject(I18n.getString(sc.getLocale(), "NUMBER") + (uc.getDefaultProject() != null ? uc.getDefaultProject().getNumber() : ""));
                userForm.setTemplate(uc.getTemplate());
                sc.setRequestAttribute(request, "newUser", Boolean.TRUE);
                sc.setRequestAttribute(request, "userPrstatusId", userForm.getPrstatus());
                sc.setRequestAttribute(request, "userManagerId", uc.getId());
                sc.setRequestAttribute(request, "userManagerName", uc.getName());
                sc.setRequestAttribute(request, "userLogin", userForm.getLogin());
                sc.setRequestAttribute(request, "login", userForm.getLogin());
                sc.setRequestAttribute(request, "name", userForm.getLogin());
                sc.setRequestAttribute(request, "userName", userForm.getName());
                sc.setRequestAttribute(request, "userPrstatus", AdapterManager.getInstance().getSecuredFindAdapterManager().findPrstatusById(sc, userForm.getPrstatus()));
                sc.setRequestAttribute(request, "userProject", I18n.getString(sc.getLocale(), "NUMBER") + (uc.getDefaultProject() != null ? uc.getDefaultProject().getNumber() : ""));
            } else {
                sc.setRequestAttribute(request, "userLogin", uc.getLogin());
                sc.setRequestAttribute(request, "userName", uc.getName());
                sc.setRequestAttribute(request, "userCompany", uc.getCompany());
                sc.setRequestAttribute(request, "userPrstatus", uc.getPrstatus());
                sc.setRequestAttribute(request, "userEmail", uc.getEmail());
                sc.setRequestAttribute(request, "userTel", uc.getTel());
                sc.setRequestAttribute(request, "userProject", I18n.getString(sc.getLocale(), "NUMBER") + (uc.getDefaultProject() != null ? uc.getDefaultProject().getNumber() : ""));

                userForm.setId(uc.getId());
                userForm.setSession(sc.getSession());
                userForm.setTel(uc.getTel());
                userForm.setOldManager(uc.getManagerId() != null ? uc.getManagerId() : uc.getId());
                userForm.setManager(uc.getManagerId() != null ? uc.getManagerId() : uc.getId());
                userForm.setLogin(uc.getLogin());
                userForm.setName(uc.getName());
                userForm.setCompany(uc.getCompany());
                userForm.setTemplate(uc.getTemplate());
                userForm.setPrstatus(uc.getPrstatusId());
                userForm.setEmail(uc.getEmail());
                userForm.setLocale(uc.getLocale());
                userForm.setExpireDate(Null.stripNullText(sc.getUser().getDateFormatter().parse(uc.getExpireDate())));
                userForm.setLicensedUsers(uc.getChildrenAllowed() != null ? uc.getChildrenAllowed().toString() : "");
                userForm.setProject(I18n.getString(sc.getLocale(), "NUMBER") + (uc.getDefaultProject() != null ? uc.getDefaultProject().getNumber() : ""));
                userForm.setEnabled(uc.isEnabled());
                String currentTimeZoneID = uc.getTimezone();

                userForm.setTimezone(currentTimeZoneID);
                sc.setRequestAttribute(request, "userPrstatusId", uc.getPrstatusId());
                sc.setRequestAttribute(request, "userManagerId", uc.getManagerId());
                sc.setRequestAttribute(request, "userChildAllowed", uc.getChildrenAllowed());
                if (uc.getManager() != null)
                    sc.setRequestAttribute(request, "userManagerName", uc.getManager().getName());

            }

            String managerId = isNew ? uc.getId() : uc.getManagerId() != null ? uc.getManagerId() : uc.getId();
            boolean canEditPrstatus = sc.canAction(Action.editUserStatus, id);
            if (canEditPrstatus) {
                ArrayList<SecuredPrstatusBean> prstatus = AdapterManager.getInstance().getSecuredPrstatusAdapterManager().getCreatablePrstatusList(sc, managerId);

                ArrayList<PrstatusListItem> listPr = new ArrayList<PrstatusListItem>();
                for (SecuredPrstatusBean n : prstatus) {
                    listPr.add(new PrstatusListItem(n.getId(), n.getName()));
                }
                Collections.sort(listPr);
                sc.setRequestAttribute(request, "allowedPrstatuses", listPr);
            }

            TreeSet<Pair> locales = Config.getInstance().getAvailableLocales(DateFormatter.toLocale(sc.getLocale()));
            Boolean configDefaultLocale = Config.getInstance().getDefaultLocaleTrue();
            Locale defaultLocale = DateFormatter.toLocale(Config.getInstance().getDefaultLocale());
            sc.setRequestAttribute(request, "defaultLocale", defaultLocale.getDisplayName(defaultLocale));
            sc.setRequestAttribute(request, "defaultTimezone", Config.getInstance().getDefaultTimezone());
            sc.setRequestAttribute(request, "configDefaultLocale", configDefaultLocale);
            TreeSet<Pair> timezones = Config.getInstance().getAvailableTimeZones(DateFormatter.toLocale(sc.getLocale()));

            Integer cCount = uc.getCountUDF();
            if (cCount != null && cCount > 0) {
                ArrayList<SecuredUDFValueBean> udfValues = isNew ? uc.getUDFValuesForNewUser() : uc.getUDFValuesList();
                try {
                    UDFFormFillHelper.isValidateScript(udfValues);
                } catch (UserException e) {
                    UserException ue = new UserException(I18n.getString("LOOKUP_SCRIPT_EXCEPTION"), false);
                    ue.addActionMessages(e.getActionMessages());
                    saveMessages(request, ue.getActionMessages());
                }
                UDFFormFillHelper.fillUdf(uc, uc.getId(), userForm, udfValues, request, "userForm", false, false, null);
            }


            userForm.setMethod("edit");

            sc.setRequestAttribute(request, "templates", PluginCacheManager.getInstance().getOnlyFtlTemplateForEmail());
            sc.setRequestAttribute(request, "datepattern", sc.getUser().getDateFormatter().getPattern2());

            sc.setRequestAttribute(request, "locales", locales);
            sc.setRequestAttribute(request, "timezones", timezones);

            sc.setRequestAttribute(request, "useLdap", Config.isTurnItOn("trackstudio.useLDAP"));

            boolean himselfOrParent = !isNew && (uc.getId().equals(sc.getUserId()) || AdapterManager.getInstance().getSecuredUserAdapterManager().isParentOf(sc, uc.getId(), sc.getUserId()));

            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_USER_PROPERTIES);
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_USER_PROPERTIES));
            sc.setRequestAttribute(request, "correctPassword", Config.getInstance().getProperty("trackstudio.security.password.complex").equals("yes"));
            sc.setRequestAttribute(request, "canViewUserCompany", sc.canAction(Action.viewUserCompany, id));
            sc.setRequestAttribute(request, "canEditUserCompany", sc.canAction(Action.editUserCompany, id));
            sc.setRequestAttribute(request, "canEditUserStatus", canEditPrstatus);
            sc.setRequestAttribute(request, "canEditUserEmail", sc.canAction(Action.editUserEmail, id));
            sc.setRequestAttribute(request, "canViewUserPhone", sc.canAction(Action.viewUserPhone, id));
            sc.setRequestAttribute(request, "canEditUserPhone", sc.canAction(Action.editUserPhone, id));
            sc.setRequestAttribute(request, "canEditUserLocale", sc.canAction(Action.editUserLocale, id));
            sc.setRequestAttribute(request, "canEditUserExpireDate", !himselfOrParent && sc.canAction(Action.editUserExpireDate, id));
            sc.setRequestAttribute(request, "canEditUserLicensed", !himselfOrParent && sc.canAction(Action.editUserLicensed, id));
            sc.setRequestAttribute(request, "canEditDefaultProject", sc.canAction(Action.editUserDefaultProject, id));
            sc.setRequestAttribute(request, "canEditUserEmailType", sc.canAction(Action.editUserEmailType, id));
            sc.setRequestAttribute(request, "canEditActive", sc.canAction(Action.editUserActive, id) && !himselfOrParent);
            sc.setRequestAttribute(request, "canEditUserManager", !isNew && sc.canAction(Action.cutPasteUser, id) && !uc.getId().equals(sc.getUserId()));
            sc.setRequestAttribute(request, "canSaveChanges", uc.canManage() && ((sc.getUserId().equals(uc.getId()) && sc.canAction(Action.editUserHimself, id)) || (!sc.getUserId().equals(uc.getId()) && !himselfOrParent && sc.canAction(Action.editUserChildren, id))));

            sc.setRequestAttribute(request, "canViewList", uc.getManager() != null && !himselfOrParent);
            sc.setRequestAttribute(request, "canCreateUserAttachments", sc.canAction(Action.createUserAttachments, id));
            sc.setRequestAttribute(request, "pattern", sc.getUser().getDateFormatter().getPattern2());
            sc.setRequestAttribute(request, "canEditUserTimezone", sc.canAction(Action.editUserTimezone, id));
            sc.setRequestAttribute(request, "tabView", new Tab(false, false));
            sc.setRequestAttribute(request, "tabEdit", new Tab(sc.getUserId().equals(id) && sc.canAction(Action.editUserHimself, id) || sc.canAction(Action.editUserChildren, id), true));
            sc.setRequestAttribute(request, "tabChangePassword", new Tab(false, false));
            sc.setRequestAttribute(request, "tabViewACL", new Tab(false, false));
            sc.setRequestAttribute(request, "tabViewNotification", new Tab(false, false));
            sc.setRequestAttribute(request, "tabViewSubscription", new Tab(false, false));
            sc.setRequestAttribute(request, "referer", referer);
            selectUserTab(sc, id, "tabUser", request);
            return actionMapping.findForward("userEditJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }


    }

    public ActionForward edit(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            UserForm uf = (UserForm) actionForm;
            String id = uf.getId();
            boolean isNew = uf.getNewUser() != null && uf.getNewUser().length() != 0;
            if (isNew) {
                id = AdapterManager.getInstance().getSecuredUserAdapterManager().createUser(sc, uf.getId(), uf.getLogin(), uf.getName(), uf.getPrstatus());
                uf.setId(id);
                sc.setRequestAttribute(request, "id", id);
                uf.setNewUser(null);
                uf.setMutable(false);
                AttachmentEditAction.uploadUserForm(uf, sc, id);

                SecuredUserBean sub = AdapterManager.getInstance().getSecuredFindAdapterManager().findUserById(sc, id);
                String context = request.getContextPath();
                String imageServlet = "/ImageServlet/" + GeneralAction.SERVLET_KEY;
                if (sub != null)
                    sc.setAttribute("jsEvent", new ChangeEvent(ChangeEvent.EVENT_USER_ADDED,
                            sub.getParent().getLogin(),
                            new String[]{context + imageServlet + "/html/xtree/images/userNode.gif"},
                            new String[]{""},
                            new String[]{sub.getLogin()},
                            new String[]{sub.getName()},
                            new String[]{"javascript:{self.top.frames[1].location = '" + context + "/UserAction.do?method=page&id=" + sub.getId() + "&thisframe=true'; active='_" + uf.isEnabled() + "';}"},
                            new String[]{sub.getId()}, new String[]{}, new String[]{}, ""));
            }
            if (uf.savePressed() || uf.parentPressed()) {
                SecuredUserBean sub = new SecuredUserBean(id, sc);

                String defaultProject = null;
                String taskNumber = uf.getProject();
                if (taskNumber != null && taskNumber.trim().length() != 0) {
                    taskNumber = taskNumber.indexOf('#') == 0 ? taskNumber.substring(1) : taskNumber;
                    defaultProject = AdapterManager.getInstance().getSecuredTaskAdapterManager().findTaskIdByQuickGo(sc, taskNumber);
                }
                String managerId;
                if (isNew)
                    managerId = sub.getManagerId();
                else {
                    managerId = uf.getManager() == null ? uf.getOldManager() : uf.getManager();
                    if (id.equals("1"))
                        managerId = null;
                }
                Calendar expire = null;
                if (uf.getExpireDate() != null && uf.getExpireDate().length() > 0) {
                    expire = sc.getUser().getDateFormatter().parseToCalendar(uf.getExpireDate());
                }
                String exists = KernelManager.getUser().findByLogin(uf.getLogin());
                if (!isNew && (exists != null) && !exists.equals(sub.getId())) {
                    throw new DuplicateUserLoginException();
                }

                for (SecuredUDFBean uitem : sub.getUDFs()) {
                    if (KernelManager.getUdf().isUserUdfEditable(id, sc.getUserId(), uitem.getId())) {
                        String valueUDF = "";
                        int type = uitem.getType();
                        if (type == UdfValue.URL) {
                            valueUDF = uf.getUdf(uitem.getId()) + "\n" + uf.getUrl(uitem.getId());
                            AdapterManager.getInstance().getSecuredUDFAdapterManager().setUserUdfValue(sc, uitem.getId(), id, valueUDF);
                        } else if (type == UdfValue.MULTILIST) {
                            Object[] values = uf.getUdflist(uitem.getId());
                            if (values != null) {
                                for (Object value : values) valueUDF += value + "\n";
                            }
                            AdapterManager.getInstance().getSecuredUDFAdapterManager().setUserUdfValue(sc, uitem.getId(), id, valueUDF);
                        } else if (type == UdfValue.LIST) {
                            valueUDF = "";
                            Map hm = KernelManager.getUdf().getUdflist(uitem.getId());
                            String listUdfval = uf.getUdf(uitem.getId()).toString();
                            for (Object o1 : hm.keySet()) {
                                String listId = (String) o1;
                                if (listId.equals(listUdfval)) {
                                    valueUDF = listId;
                                    break;
                                }
                            }
                            AdapterManager.getInstance().getSecuredUDFAdapterManager().setUserUdfValue(sc, uitem.getId(), id, valueUDF);
                        } else if (type == UdfValue.TASK) {
                            List<String> sel = new ArrayList<String>();
                            if (uf.getUdf(uitem.getId()) != null) {
                                String[] values = (String[]) uf.getUdf(uitem.getId());
                                if (values != null) for (String v : values) {
                                    SecuredTaskBean stb = AdapterManager.getInstance().getSecuredTaskAdapterManager().findTaskByNumber(sc, v.replace('#', ' ').trim());
                                    if (stb != null) sel.add(stb.getId());

                                }
                                valueUDF = UDFFormFillHelper.listToString(sel, ";");
                                AdapterManager.getInstance().getSecuredUDFAdapterManager().setUserUdfValue(sc, uitem.getId(), sub.getId(), valueUDF);
                            }
                        } else if (type == UdfValue.USER) {
                            List<String> sel = new ArrayList<String>();
                            Object[] values = uf.getUdflist(uitem.getId());
                            if (values != null) for (Object v : values) {
                                SecuredUserBean bean1 = AdapterManager.getInstance().getSecuredUserAdapterManager().findByLogin(sc, v.toString().trim());
                                if (bean1 != null) sel.add(bean1.getId());
                            }
                            valueUDF = UDFFormFillHelper.listToString(sel, ";");
                            AdapterManager.getInstance().getSecuredUDFAdapterManager().setUserUdfValue(sc, uitem.getId(), sub.getId(), valueUDF);
                        } else {
                            valueUDF = uf.getUdf(uitem.getId()).toString();
                            AdapterManager.getInstance().getSecuredUDFAdapterManager().setUserUdfValue(sc, uitem.getId(), id, valueUDF);
                        }
                    }
                }

                if (!isNew) {
                    if (!sub.getName().equals(uf.getName()) || !sub.getLogin().equals(uf.getLogin())) {
                        sc.setAttribute("jsEvent", new ChangeEvent(ChangeEvent.EVENT_USER_RENAMED,
                                sub.getParent() != null ? sub.getParent().getLogin() : "root", new String[]{}, new String[]{}, new String[]{uf.getLogin()}, new String[]{uf.getName()},
                                new String[]{}, new String[]{}, new String[]{sub.getLogin()}, new String[]{}, ""));
                    }
                }
                String timezone = isNotNull(uf.getTimezone()) ? uf.getTimezone() : Config.getInstance().getDefaultTimezone();
                if (ParameterValidator.badTimeZone(timezone)) {
                    timezone = SimpleTimeZone.getAvailableIDs()[0];
                }
                AdapterManager.getInstance().getSecuredUserAdapterManager().updateUser(sc, id, uf.getLogin(),
                        uf.getName(), uf.getTel(), uf.getEmail(),
                        uf.getPrstatus(), managerId, timezone,
                        uf.getLocale(), uf.getCompany(), uf.getTemplate(),
                        defaultProject, expire, sub.getPreferences(), uf.isEnabled());

                if (isNew) {
                    String pwd = "";
                    if (uf.getPassword() != null && uf.getConfirmation() != null && uf.getPassword().length() > 0 && uf.getConfirmation().equals(uf.getPassword()))
                        pwd = uf.getPassword();
                    else {
                        for (int i = 0; i < 7; ++i) {
                            if ((int) (Math.random() * 26) % 2 == 0)
                                pwd += (char) ((int) 'a' + ((int) (Math.random() * 26)));
                            else
                                pwd += (char) ((int) '0' + ((int) (Math.random() * 10)));
                        }
                    }
                    AdapterManager.getInstance().getAuthAdapterManager().changePassword(id, pwd);
                    if (Config.getInstance().isSendMail()) {
                        if (isNotNull(uf.getEmail()) && isNotNull(uf.getName())) {
                            UserRelatedInfo uri = UserRelatedManager.getInstance().find(id);
                            if (!Config.getInstance().isLDAP()) {
                                EmailUtil.sendEmail(EmailUtil.buildDataForUser(uri, pwd), EmailUtil.buildTo(uri), "registration.ftl_h", uri);
                            }
                        }
                    }
                }
                boolean himselfOrParent = AdapterManager.getInstance().getSecuredUserAdapterManager().isParentOf(sc, id, sc.getUserId());
                if (!id.equals(sc.getUserId()) && !himselfOrParent && sc.canAction(Action.editUserLicensed, id)) {
                    String lu = uf.getLicensedUsers();
                    lu = lu != null ? lu.trim() : null;
                    boolean isUserTotal = lu != null && !lu.isEmpty() && !"0".equals(lu);
                    Integer lic = isUserTotal ? Integer.parseInt(lu) : null;
                    AdapterManager.getInstance().getSecuredUserAdapterManager().setMaximumChildrenAllowed(sc, id, lic);
                }
                log.debug("CLOSE SESSION UserEditAction");
                //            HibernateSession.stopTimer();

                if (uf.savePressed()) { // Add new item
                    ActionForward af = new ActionForward(actionMapping.findForward("userViewPage").getPath() + "&id=" + uf.getId());
                    af.setRedirect(true);
                    return af;
                }
                if (uf.parentPressed()) { // Add new item
                    uf.setId(managerId);
                    sc.setRequestAttribute(request, "id", managerId);
                    uf.setMutable(false);
                    ActionForward af = new ActionForward(actionMapping.findForward("userListPage").getPath() + "&id=" + managerId);
                    af.setRedirect(true);
                    return af;
                }
            }

            return actionMapping.getInputForward();
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward activate(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            UserForm userForm = (UserForm) actionForm;
            String userId = GeneralAction.getInstance().userHeader(userForm, sc, request);
            SecuredUserBean sub = new SecuredUserBean(userId, sc);

            AdapterManager.getInstance().getSecuredUserAdapterManager().updateUser(sc, userId, sub.getLogin(),
                    sub.getName(), sub.getTel(), sub.getEmail(),
                    sub.getPrstatusId(), sub.getManagerId(), sub.getTimezone(),
                    sub.getLocale(), sub.getCompany(), sub.getTemplate(),
                    sub.getDefaultProjectId(), sub.getExpireDate(), sub.getPreferences(), !sub.isEnabled());

            if (sub.isEnabled()) {
                List<String> usersToClearOperation = new ArrayList<String>();
                if (sc.getAttribute("USERS") != null && sc.getAttribute("USER_OPERATION") != null) {
                    String ids = (String) sc.getAttribute("USERS");
                    for (String sid : ids.split(UdfConstants.SPLIT_SEPARATOR)) {
                        SecuredUserBean userBean = AdapterManager.getInstance().getSecuredFindAdapterManager().findUserById(sc, sid);
                        if (userBean != null) usersToClearOperation.add(userBean.getLogin());
                    }
                }
                sc.setAttribute("jsEvent", new ChangeEvent(ChangeEvent.EVENT_USER_DELETED,
                        "", new String[]{}, new String[]{},
                        new String[]{sub.getLogin()}, new String[]{}, new String[]{},
                        new String[]{}, usersToClearOperation.toArray(new String[]{}), new String[]{}, ""));
                SessionManager.getInstance().removeByUserId(sub.getId());
            } else {
                String context = request.getContextPath();
                String imageServlet = "/ImageServlet/" + GeneralAction.SERVLET_KEY;
                sc.setAttribute("jsEvent", new ChangeEvent(ChangeEvent.EVENT_USER_ADDED,
                        sub.getParent().getLogin(),
                        new String[]{context + imageServlet + "/html/xtree/images/userNode.gif"},
                        new String[]{""},
                        new String[]{sub.getLogin()},
                        new String[]{sub.getName()},
                        new String[]{"javascript:{self.top.frames[1].location = '" + context + "/UserAction.do?method=page&id=" + sub.getId() + "&thisframe=true'; active='_" + sub.isActive() + "';}"},
                        new String[]{sub.getId()}, new String[]{}, new String[]{}, ""));
            }
            ActionForward af = new ActionForward(actionMapping.findForward("userViewPage").getPath() + "&id=" + sub.getId());
            af.setRedirect(true);
            return af;

        } finally {
            if (w) lockManager.releaseConnection();
        }
    }
}