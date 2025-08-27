package com.trackstudio.securedkernel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.Slider;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.filter.FValue;
import com.trackstudio.app.filter.UserFValue;
import com.trackstudio.app.filter.list.UserFilter;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.app.session.SessionManager;
import com.trackstudio.exception.AccessDeniedException;
import com.trackstudio.exception.GranException;
import com.trackstudio.exception.InvalidParameterException;
import com.trackstudio.exception.PasswordMismatchException;
import com.trackstudio.exception.SecurityViolationException;
import com.trackstudio.exception.SimplePasswordException;
import com.trackstudio.exception.UserException;
import com.trackstudio.exception.UsersLimitExceedException;
import com.trackstudio.index.DocumentBuilder;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.cache.UserAction;
import com.trackstudio.kernel.cache.UserRelatedInfo;
import com.trackstudio.kernel.cache.UserRelatedManager;
import com.trackstudio.kernel.manager.IndexManager;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.kernel.manager.SafeString;
import com.trackstudio.secured.SecuredFilterBean;
import com.trackstudio.secured.SecuredPrstatusBean;
import com.trackstudio.secured.SecuredSearchUserItem;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.startup.Config;
import com.trackstudio.tools.MD5;
import com.trackstudio.tools.ParameterValidator;
import com.trackstudio.tools.PasswordValidator;
import com.trackstudio.tools.SecuredBeanUtil;

import net.jcip.annotations.Immutable;

/**
 * Класс SecuredUserAdapterManager содержит методы для работы с пользователями
 */
@Immutable
public class SecuredUserAdapterManager {

    private static final Log log = LogFactory.getLog(SecuredUserAdapterManager.class);
    private static final ParameterValidator pv = new ParameterValidator();

    /**
     * Создается новый пользователя
     *
     * @param sc         сессия пользователя
     * @param parentId   ID менеджера
     * @param login      Логин
     * @param name       Имя пользователя
     * @param prstatusId ID статуса пользователя
     * @return ID созданного пользователя
     * @throws GranException при необходимости
     */
    public String createUser(SessionContext sc, String parentId, String login, String name, String prstatusId) throws GranException {
        log.trace("createUser");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "createUser", "sc", sc);
        if (parentId == null)
            throw new InvalidParameterException(this.getClass(), "createUser", "parentId", sc);
        if (login == null || pv.badSmallDesc(login))
            throw new InvalidParameterException(this.getClass(), "createUser", "login", sc);
        if (name == null || pv.badSmallDesc(name))
            throw new InvalidParameterException(this.getClass(), "createUser", "name", sc);
        if (prstatusId == null)
            throw new InvalidParameterException(this.getClass(), "createUser", "prstatusId", sc);
        SecuredPrstatusBean prstatus = AdapterManager.getInstance().getSecuredFindAdapterManager().findPrstatusById(sc, prstatusId);
        if (!sc.canAction(Action.createUser, parentId))
            throw new AccessDeniedException(this.getClass(), "createUser", sc, "!sc.canAction(com.trackstudio.kernel.cache.Action.createUser, parentId)", parentId);
        if (!sc.allowedByUser(parentId))
            throw new AccessDeniedException(this.getClass(), "createUser", sc, "!sc.allowedByACL(parentId)", parentId);
        if (!prstatus.canView())
            throw new AccessDeniedException(this.getClass(), "createUser", sc, "!prstatus.canView()", prstatusId);
        if (KernelManager.getUser().getAllowableUserQty(parentId) <= 0)
            throw new UsersLimitExceedException();
        String company = sc.canAction(Action.editUserCompany, parentId) ? UserRelatedManager.getInstance().find(parentId).getCompany() : null;
        return KernelManager.getUser().createUser(parentId, SafeString.createSafeString(login), SafeString.createSafeString(name), prstatusId, SafeString.createSafeString(company));
    }

    /**
     * Удаляется пользователь
     *
     * @param sc     сессия пользователя
     * @param userId ID пользователя, которого удаляем
     * @throws GranException при необходимости
     */
    public void deleteUser(SessionContext sc, String userId) throws GranException {
        log.trace("deleteUser");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "deleteUser", "sc", sc);
        if (userId == null)
            throw new InvalidParameterException(this.getClass(), "deleteUser", "userId", sc);
        if (userId.equals(sc.getUserId()))
            throw new AccessDeniedException(this.getClass(), "deleteUser", sc, "userId.equals(sc.getUserId())", userId);
        if (!sc.canAction(Action.deleteUser, userId))
            throw new AccessDeniedException(this.getClass(), "deleteUser", sc, "!sc.canAction(Action.deleteUser, userId)", userId);
        if (!sc.allowedByUser(userId))
            throw new AccessDeniedException(this.getClass(), "deleteUser", sc, "!sc.allowedByACL(userId)", userId);
        UserRelatedInfo uci = UserRelatedManager.getInstance().find(userId);
        if (UserRelatedManager.getInstance().getChildrenCount(uci.getId()) > 0)
            throw new UserException("ERROR_CAN_NOT_DELETE_USER_2", new String[]{uci.getName()});
        KernelManager.getUser().deleteUser(userId);
    }

    /**
     * Редактирует время последнего логина пользователя
     *
     * @param sc     сессия пользователя
     * @param userId ID пользователя
     * @throws GranException при необходимости
     */
    public void updateLastLogonDate(SessionContext sc, String userId) throws GranException {
        log.trace("updateLastLogonDate");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "updateLastLogonDate", "sc", sc);

        if (!(userId.equals(sc.getUserId()) || sc.allowedByUser(userId)))
            throw new AccessDeniedException(this.getClass(), "updateLastLogonDate", sc, "!(userId.equals(sc.getUserId()) || sc.allowedByACL(userId))", userId);
        if (userId.equals(sc.getUserId())) {
            // update previous logon date in SessionContext
            sc.savePrevLogonDate();
        }
        KernelManager.getUser().updateLastLogon(userId);
    }

    /**
     * Меняет пароль пользователя
     *
     * @param sc        сессия пользователя
     * @param userId    ID пользователя
     * @param password1 пароль
     * @param password2 повтор пароля
     * @return TRUE - если пароль сменился успешно, FALSE - если нет
     * @throws GranException при необходимости
     */
    public Boolean changePassword(SessionContext sc, String userId, String password1, String password2) throws GranException {
        log.trace("changePassword");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "changePassword", "sc", sc);
        if (userId == null)
            throw new InvalidParameterException(this.getClass(), "changePassword", "userId", sc);
        if (password1 == null /*|| pv.badSmallDesc(password1)*/)
            throw new InvalidParameterException(this.getClass(), "changePassword", "password1", sc);
        if (password2 == null)
            throw new InvalidParameterException(this.getClass(), "changePassword", "password2", sc);
        boolean isParent = AdapterManager.getInstance().getSecuredUserAdapterManager().isParentOf(sc, userId, sc.getUserId());
        if (isParent)
            throw new AccessDeniedException(this.getClass(), "changePassword", sc, "isParent", userId);
        if (!sc.allowedByUser(userId))
            throw new AccessDeniedException(this.getClass(), "changePassword", sc, "!sc.allowedByACL(userId)", userId);
        if (!(sc.canAction(Action.editUserPasswordHimself, userId)
                && (userId.equals(sc.getUserId()) ||
                sc.canAction(Action.editUserChildrenPassword, userId))))
            throw new AccessDeniedException(this.getClass(), "changePassword", sc, "sc.canAction(com.trackstudio.kernel.cache.Action.editUserPasswordHimself, userId)\n" +
                    "                && (userId.equals(sc.getUserId()) ||\n" +
                    "                sc.canAction(com.trackstudio.kernel.cache.Action.editUserChildrenPassword, userId))", userId);
        if (password1.compareTo(password2) != 0)
            throw new PasswordMismatchException();
        if (!PasswordValidator.passwordIsValid(password1))
            throw new SimplePasswordException();

        String minLength = Config.getInstance().getProperty("trackstudio.security.password.history");
        if (minLength != null && minLength.length() > 0) {
            int min = Integer.parseInt(minLength);
            if (min > 0) {
                if (min > 8) min = 8; // database limits
                SecuredUserBean bean = new SecuredUserBean(userId, sc);
                String phistory = bean.getPasswordHistory();
                int j = 0;
                int i = 0;
                while (i < min && j + PasswordValidator.END_INDEX <= phistory.length()) {
                    String cc = phistory.substring(j, j + PasswordValidator.END_INDEX);
                    if (cc.equals(MD5.encode(password1)))
                        throw new SecurityViolationException("ERROR_PASSWORD_HISTORY");
                    if (cc.equals(MD5.encode(password1)))
                        return false;
                    i++;
                    j = i * PasswordValidator.END_INDEX;
                }
            }
        }
        AdapterManager.getInstance().getAuthAdapterManager().changePassword(userId, password1);
        return true;
    }

    /**
     * Редактирует пользователя
     *
     * @param sc          сессия пользователя
     * @param userId      ID редактируемого пользователя
     * @param login       Логин пользователя
     * @param name        Имя пользователя
     * @param tel         Телефон пользователя
     * @param email       E-mail пользователя
     * @param prstatusId  ID статуса пользователя
     * @param managerId   ID менеджера пользователя
     * @param timezone    Таймзона
     * @param locale      Локаль
     * @param company     Компания
     * @param template    Шаблон
     * @param taskId      ID задачи
     * @param expire      Дата истекания срока действия пользователя
     * @param preferences Настройки пользователя
     * @param enabled     Активен пользователь или нет
     * @throws GranException при необходимости
     */
    public void updateUser(SessionContext sc, String userId, String login, String name, String tel, String email,
                           String prstatusId, String managerId, String timezone, String locale, String company,
                           String template, String taskId, Calendar expire, String preferences, boolean enabled) throws GranException {
        log.trace("updateUser");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "updateUser", "sc", sc);
        if (userId == null)
            throw new InvalidParameterException(this.getClass(), "updateUser", "userId", sc);
        if (managerId == null && !userId.equals("1"))
            throw new InvalidParameterException(this.getClass(), "updateUser", "managerId", sc);
        if (login == null || pv.badSmallDesc(login))
            throw new InvalidParameterException(this.getClass(), "updateUser", "login", sc);
        if (name == null || pv.badSmallDesc(name))
            throw new InvalidParameterException(this.getClass(), "updateUser", "name", sc);
        if (pv.badSmallDesc(tel))
            throw new InvalidParameterException(this.getClass(), "updateUser", "tel", sc);
        if (pv.badEmail(email))
            throw new InvalidParameterException(this.getClass(), "updateUser", "email", sc);
        if (pv.badSmallDesc(company))
            throw new InvalidParameterException(this.getClass(), "updateUser", "company", sc);
        if (locale != null && locale.length() > 0 && pv.badLocale(locale))
            throw new UserException("ERROR_INCORRECT_LOCALE");
        if (timezone != null && timezone.length() > 0 && pv.badTimeZone(timezone))
            throw new UserException("ERROR_INCORRECT_TIMEZONE");
        UserRelatedInfo user = UserRelatedManager.getInstance().find(userId);
        boolean allowedManager = managerId == null && userId.equals("1");
        if (locale == null || locale.length() == 0) locale = null;
        if (timezone != null && timezone.length() == 0) timezone = null;
        if (!allowedManager)
            allowedManager = user.getParentId().equals(managerId);
        if (!allowedManager)
            allowedManager = KernelManager.getUser().getPossibleManagerList(userId, sc.getUserId()).contains(UserRelatedManager.getInstance().find(managerId));
        boolean allowedPrstatus = true;
        log.debug("allowedPrstatus=" + allowedPrstatus + ";allowedManager=" + allowedManager);
        if (!sc.allowedByUser(userId))
            throw new AccessDeniedException(this.getClass(), "updateUser", sc, "!sc.allowedByACL(userId)", userId);
        if (!(sc.getUserId().equals(userId) ? sc.canAction(Action.editUserHimself, userId) : sc.canAction(Action.editUserChildren, userId)))
            throw new AccessDeniedException(this.getClass(), "updateUser", sc, "!(sc.getUserId().equals(userId) ? sc.canAction(com.trackstudio.kernel.cache.Action.editUserHimself, userId) : sc.canAction(Action.editUserChildren, userId))", userId);
        if (!allowedPrstatus)
            throw new AccessDeniedException(this.getClass(), "updateUser", sc, "allowedPrstatus", userId);
        if (!allowedManager && !"1".equals(sc.getUserId())) {
            throw new AccessDeniedException(this.getClass(), "updateUser", sc, "allowedManager", userId);
        }
        String oldPrstatus = user.getPrstatusId();
        String tel1 = tel;
        String email1 = email;
        String managerId1 = managerId;
        String timezone1 = timezone;
        String locale1 = locale;
        String company1 = company;
        UserRelatedInfo user1 = UserRelatedManager.getInstance().find(userId);
        boolean himselfOrParent = sc.getUserId().equals(userId) || AdapterManager.getInstance().getSecuredUserAdapterManager().isParentOf(sc, user1.getId(), sc.getUserId());
        if (!(sc.canAction(Action.cutPasteUser, userId) && (managerId1 == null && sc.allowedByUser(userId) || sc.allowedByUser(managerId1))))
            managerId1 = user1.getParentId();
        if (!sc.canAction(Action.editUserTimezone, userId))
            timezone1 = user1.getTimezone();
        if (!sc.canAction(Action.editUserLocale, userId))
            locale1 = user1.getLocale();
        if (!sc.canAction(Action.editUserPhone, userId))
            tel1 = user1.getTel();
        if (!sc.canAction(Action.editUserEmail, userId))
            email1 = user1.getEmail();
        if (!sc.canAction(Action.editUserDefaultProject, userId))
            taskId = user1.getDefaultProjectId();
        if (!sc.canAction(Action.editUserCompany, userId))
            company1 = user1.getCompany();
        if (!sc.canAction(Action.editUserEmailType, userId))
            template = user1.getTemplate();
        if (!sc.canAction(Action.editUserExpireDate, userId) || himselfOrParent)
            expire = user1.getExpireDate();
        if (!sc.canAction(Action.editUserActive, userId) || himselfOrParent)
            enabled = user1.isEnabled();
        KernelManager.getUser().updateUser(userId, SafeString.createSafeString(login), SafeString.createSafeString(name), SafeString.createSafeString(tel1), SafeString.createSafeString(email1), prstatusId, managerId1, timezone1, locale1, SafeString.createSafeString(company1),
                SafeString.createSafeString(template), taskId, expire, SafeString.createSafeString(preferences), enabled);
        user = UserRelatedManager.getInstance().find(userId);
        if (!oldPrstatus.equals(user.getPrstatusId())) {
            SessionManager.getInstance().create(user, true);
        }
    }

    /**
     * Метод для обновля срочный сообщения пользователя
     * @param sc сессия
     * @param userId id пользователя
     * @param emegencyNotice сообщения
     * @param emergencyNoticeDate время начало действия сообщения
     * @throws GranException  при необходимости
     */
    public void updateNoticeUser(SessionContext sc, String userId, String emegencyNotice, Calendar emergencyNoticeDate) throws GranException {
        log.trace("updateUser");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "updateUser", "sc", sc);
        if (userId == null)
            throw new InvalidParameterException(this.getClass(), "updateUser", "userId", sc);
        UserRelatedInfo user = UserRelatedManager.getInstance().find(userId);
        user.setEmergencyNotice(emegencyNotice);
        user.setEmergencyNoticeDate(emergencyNoticeDate);
        UserRelatedManager.getInstance().invalidateUser(user);
    }

    /**
     * Возвращает список доступных менеджеров
     *
     * @param sc     сессия пользователя
     * @param userId ID пользователя, для которого ищется список менеджеров
     * @return список пользователей
     * @throws GranException при необходимости
     * @see com.trackstudio.kernel.cache.UserRelatedInfo
     */
    public ArrayList<SecuredUserBean> getPossibleManagerList(SessionContext sc, String userId) throws GranException {
        log.trace("getPossibleManagerList");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getPossibleManagerList", "sc", sc);
        if (userId == null)
            throw new InvalidParameterException(this.getClass(), "getPossibleManagerList", "userId", sc);
        if (!sc.allowedByUser(userId))
            throw new AccessDeniedException(this.getClass(), "getPossibleManagerList", sc, "!sc.allowedByACL(userId)", userId);
        return SecuredBeanUtil.toArrayList(sc, KernelManager.getUser().getPossibleManagerList(userId, sc.getUserId()), SecuredBeanUtil.USER);
    }

    /**
     * Возвращает список отфильтрованных пользователей
     *
     * @param sc        сессия пользователя
     * @param managerId ID менеджера
     * @param filterId  ID фильтра
     * @param page      страница
     * @param withUdf   Нужно ли фильтровать пользовательские поля
     * @param order     порядок сортировки
     * @return список пользователей
     * @throws GranException при необзодимости
     */
    public Slider<SecuredUserBean> getUserList(SessionContext sc, String managerId, String filterId, int page, boolean withUdf, List<String> order) throws GranException {
        log.trace("getUserList");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getUserList", "sc", sc);
        if (managerId == null)
            throw new InvalidParameterException(this.getClass(), "getUserList", "managerId", sc);
        if (filterId == null)
            throw new InvalidParameterException(this.getClass(), "getUserList", "filter", sc);
        SecuredFilterBean filter = AdapterManager.getInstance().getSecuredFindAdapterManager().findFilterById(sc, filterId);
        //if (!(sc.userOnSight(managerId) && filter.canView()))
        //    throw new AccessDeniedException(this.getClass(), "getUserList", sc);
        if (!sc.userOnSight(managerId))
            throw new AccessDeniedException(this.getClass(), "getUserList", sc, "!sc.userOnSight(managerId)", managerId);
        if (!filter.canView())
            throw new AccessDeniedException(this.getClass(), "getUserList", sc, "!filter.canView()", filterId);
        UserFValue flthm = AdapterManager.getInstance().getSecuredFilterAdapterManager().getUserFValue(sc, filterId).getFValue();
        SecuredUserBean activeUser = new SecuredUserBean(managerId, sc);
        int onPage;
        String onPageStr = flthm.getAsString(FValue.ONPAGE);
        if (onPageStr != null && onPageStr.length() != 0)
            onPage = Integer.parseInt(onPageStr);
        else
            onPage = 20;
        UserFilter userList = new UserFilter(activeUser);
        return new Slider<SecuredUserBean>(userList.getList(flthm, withUdf, false, order), onPage, order, page);
    }

    /**
     * Возвращает список отфильтрованных пользователей
     *
     * @param sc        сессия пользователя
     * @param managerId ID менеджера
     * @param filter    фильтр
     * @param page      страница
     * @param withUdf   Нужно ли фильтровать пользовательские поля
     * @param order     порядок сортировки
     * @return список пользователей
     * @throws GranException при необзодимости
     */
    public Slider<SecuredUserBean> getUserList(SessionContext sc, String managerId, UserFValue filter, int page, boolean withUdf, List<String> order) throws GranException {
        log.trace("getUserList");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getUserList", "sc", sc);
        if (managerId == null)
            throw new InvalidParameterException(this.getClass(), "getUserList", "managerId", sc);
        if (filter == null)
            throw new InvalidParameterException(this.getClass(), "getUserList", "filter", sc);

        if (!sc.userOnSight(managerId))
            throw new AccessDeniedException(this.getClass(), "getUserList", sc, "!(sc.userOnSight(managerId)", managerId);
        SecuredUserBean activeUser = new SecuredUserBean(managerId, sc);
        int onPage;
        String onPageStr = filter.getAsString(FValue.ONPAGE);
        if (onPageStr != null && onPageStr.length() != 0)
            onPage = Integer.parseInt(onPageStr);
        else
            onPage = 20;
        UserFilter userList = new UserFilter(activeUser);
        return new Slider<SecuredUserBean>(userList.getList(filter, withUdf, false, order), onPage, order, page);
    }

    /**
     * Возвращает список дочерних пользователей для менеджера
     *
     * @param sc     сессия пользователя
     * @param userId ID пользователя
     * @return список пользователей
     * @throws GranException при необходимости
     * @see com.trackstudio.kernel.cache.UserRelatedInfo
     */
    public ArrayList<SecuredUserBean> getUserManagerChildren(SessionContext sc, String userId) throws GranException {
        log.trace("getUserManagerChildren");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getUserManagerChildren", "sc", sc);
        if (userId == null)
            throw new InvalidParameterException(this.getClass(), "getUserManagerChildren", "userId", sc);
        if (!sc.userOnSight(userId))
            throw new AccessDeniedException(this.getClass(), "getUserManagerChildren", sc, "!sc.userOnSight(userId)", userId);
        ArrayList<SecuredUserBean> result = new ArrayList<SecuredUserBean>();
        for (UserRelatedInfo o : KernelManager.getUser().getUserManagerChildren(userId)) {
            SecuredUserBean sub = new SecuredUserBean(o, sc);
            if (sub.isOnSight())
                result.add(sub);
        }
        return result;
    }

    /**
     * Возвращает список, состоящий из указанного пользователя и подчиненных пользователей
     *
     * @param sc     сессия пользователя
     * @param userId ID пользователя, для которого получается список
     * @return список пользователей
     * @throws GranException при необходимости
     * @see com.trackstudio.kernel.cache.UserRelatedInfo
     */
    public ArrayList<SecuredUserBean> getUserAndChildrenList(SessionContext sc, String userId) throws GranException {
        log.trace("getUserAndChildrenList");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getUserAndChildrenList", "sc", sc);
        if (userId == null)
            throw new InvalidParameterException(this.getClass(), "getUserAndChildrenList", "userId", sc);
        if (!sc.userOnSight(userId))
            throw new AccessDeniedException(this.getClass(), "getUserAndChildren", sc, "!sc.userOnSight(userId) sc[userId:"+sc.getUserId()+"],userId:"+userId, userId);
        ArrayList<SecuredUserBean> result = new ArrayList<SecuredUserBean>();
        result.add(new SecuredUserBean(userId, sc));
        for (Object o : KernelManager.getUser().getUserAndChildrenList(userId)) {
            SecuredUserBean sub = new SecuredUserBean((UserRelatedInfo) o, sc);
            if (sub.isOnSight())
                result.add(sub);
        }
        return result;
    }

    /**
     * Возвращает список пользователей для нового правила доступа
     *
     * @param sc        сессия пользователя
     * @param forUserId для какого пользователя созадется правило
     * @return список пользователей
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredUserBean
     */
    public ArrayList<SecuredUserBean> getUserListForNewAcl(SessionContext sc, String forUserId) throws GranException {
        log.trace("getUserListForNewAcl");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getUserListForNewAcl", "sc", sc);
        if (forUserId == null)
            throw new InvalidParameterException(this.getClass(), "getUserListForNewAcl", "forUserId", sc);
        if (!sc.userOnSight(forUserId))
            throw new AccessDeniedException(this.getClass(), "getUserListForNewAcl", sc, "!sc.userOnSight(forUserId)", forUserId);
        Set<SecuredUserBean> set = new HashSet<SecuredUserBean>();
        for (SecuredUserBean sub : AdapterManager.getInstance().getSecuredUserAdapterManager().getUserAndChildrenList(sc, forUserId)) {
            if (sub.isActive())
                set.add(sub);
        }
        //remove current user
        set.remove(sc.getUser());
        List<String> assignUsers = KernelManager.getAcl().getAssignedUserList(forUserId);
        for (String ui : assignUsers) {
            for (Object o : KernelManager.getUser().getUserAndChildrenList(ui)) {
                SecuredUserBean sub = new SecuredUserBean((UserRelatedInfo) o, sc);
                if (sub.isOnSight() && sub.isActive() && !sub.equals(sc.getUser()))
                    set.add(sub);
            }
        }
        return new ArrayList<SecuredUserBean>(set);
    }

    /**
     * Возвращает дочерних пользователей для указанного
     *
     * @param sc     сессия пользователя
     * @param userId ID пользователя
     * @return список пользователей
     * @throws GranException при необходимости
     */
    public ArrayList<SecuredUserBean> getChildren(SessionContext sc, String userId) throws GranException {
        log.trace("getChildren");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getChildren", "sc", sc);
        if (userId == null)
            throw new InvalidParameterException(this.getClass(), "getChildren", "userId", sc);
        if (!sc.userOnSight(userId))
            throw new AccessDeniedException(this.getClass(), "getChildren", sc, "!sc.userOnSight(userId)", userId);
        ArrayList<SecuredUserBean> result = new ArrayList<SecuredUserBean>();
        List<String> childList = UserRelatedManager.getInstance().find(userId).getChildren();
        for (String aCollection : childList) {
            SecuredUserBean sub = new SecuredUserBean(aCollection, sc);
            if (sub.isOnSight())
                result.add(sub);
        }
        return result;
    }
    /**
     * Возвращает дочерних пользователей для указанного, имеющих указанную роль (prstatId)
     *
     * @param sc     сессия пользователя
     * @param userId ID пользователя
     * @param prstatusId ID роли пользователя
     * @return список пользователей
     * @throws GranException при необходимости
     */
    public ArrayList<SecuredUserBean> getChildrenWithPrstatus(SessionContext sc, String userId, String prstatusId) throws GranException {
        log.trace("getChildren");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getChildrenWithPrstatus", "sc", sc);
        if (userId == null)
            throw new InvalidParameterException(this.getClass(), "getChildrenWithPrstatus", "userId", sc);
        if (prstatusId == null)
            throw new InvalidParameterException(this.getClass(), "getChildrenWithPrstatus", "prstatusId", sc);
        if (!sc.userOnSight(userId))
            throw new AccessDeniedException(this.getClass(), "getChildrenWithPrstatus", sc, "!sc.userOnSight(userId)", userId);
        ArrayList<SecuredUserBean> result = new ArrayList<SecuredUserBean>();
        List<String> childList = UserRelatedManager.getInstance().find(userId).getChildren();
        for (String aCollection : childList) {
            SecuredUserBean sub = new SecuredUserBean(aCollection, sc);
            if (sub.isOnSight() && sub.getPrstatusId().equals(prstatusId))
                result.add(sub);
        }
        return result;
    }
    /**
     * Возвращает список цепочки пользователей от текущего пользователя до указанного
     *
     * @param sc         сессия пользователя
     * @param stopUserId ID конечного пользователя
     * @return список пользователей
     * @throws GranException при необходимости
     * @see com.trackstudio.kernel.cache.UserRelatedInfo
     */
    public ArrayList<SecuredUserBean> getUserChain(SessionContext sc, String stopUserId) throws GranException {
        log.trace("getUserChain");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getUserChain", "sc", sc);
        if (stopUserId == null)
            throw new InvalidParameterException(this.getClass(), "getUserChain", "stopUserId", sc);
        if (!sc.userOnSight(stopUserId))
            throw new AccessDeniedException(this.getClass(), "getUserChain", sc, "!sc.userOnSight(stopUserId)", stopUserId);
        ArrayList<UserRelatedInfo> defaultChain = KernelManager.getUser().getUserChain(sc.getUserId(), stopUserId);
        return SecuredBeanUtil.toArrayList(sc, defaultChain == null ? KernelManager.getUser().getUserChain("1", stopUserId) : defaultChain, SecuredBeanUtil.USER);
    }

    /**
     * Возвращает менеджеров текущего пользователя
     * @param sc сесссия
     * @return список менеджеров
     * @throws GranException
     */
    public ArrayList<SecuredUserBean> getManagerUser(SessionContext sc) throws GranException {
        log.trace("getUserChain");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getUserChain", "sc", sc);
        return SecuredBeanUtil.toArrayList(sc, KernelManager.getUser().getUserChain("1", sc.getUserId()), SecuredBeanUtil.USER);
    }

    /**
     * Возвращает список цепочки пользователей от одного пользователя до другого
     *
     * @param sc          сессия пользователя
     * @param startUserId ID начального пользователя
     * @param stopUserId  ID конечного пользователя
     * @return список пользователей
     * @throws GranException при необходимости
     * @see com.trackstudio.kernel.cache.UserRelatedInfo
     */
    public ArrayList<SecuredUserBean> getUserChain(SessionContext sc, String startUserId, String stopUserId) throws GranException {
        log.trace("getUserChain");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getUserChain", "sc", sc);
        if (stopUserId == null)
            throw new InvalidParameterException(this.getClass(), "getUserChain", "stopUserId", sc);
//        if (startUserId == null)
//            throw new InvalidParameterException(this.getClass(), "getUserChain", "startUserId", sc);
        if (!sc.userOnSight(stopUserId))
            throw new AccessDeniedException(this.getClass(), "getUserChain", sc, "!sc.userOnSight(stopUserId)", stopUserId);
        ArrayList<UserRelatedInfo> defaultChain = KernelManager.getUser().getUserChain(startUserId, stopUserId);
        if (defaultChain == null)
            return null;
        return SecuredBeanUtil.toArrayList(sc, defaultChain, SecuredBeanUtil.USER);
    }

    /**
     * Производится авторизация пользователя по логину и паролю
     *
     * @param login    Логин
     * @param password Пароль
     * @return ID найденного пользователя
     * @throws GranException при необходимости
     */
    public String authenticate(String login, String password, HttpServletRequest request) throws GranException {
        log.trace("authenticate");
        if (login == null)
            throw new InvalidParameterException(this.getClass().getName(), "authenticate", "login", null);
        if (password == null)
            throw new InvalidParameterException(this.getClass().getName(), "authenticate", "login", null);
        String userId = KernelManager.getUser().authenticate(login, password, request);
        log.debug(" josso  userId : " + userId);
        UserRelatedInfo uri = UserRelatedManager.getInstance().find(userId);
        //create new session with last logon date
        return SessionManager.getInstance().create(uri);
    }

    /**
     * Возвращает ID залогиненного пользователя
     *
     * @param sc сессия пользователя
     * @return ID ntreotuj gjkmpjdfntkz
     * @throws GranException ghb ytj,[jlbvjcnb
     */
    public String getUserId(SessionContext sc) throws GranException {
        log.trace("getUserId");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getUserId", "sc", sc);
        return sc.getUserId();
    }

    /**
     * Возвращает дату истекания срока действия логина пользователя
     *
     * @param sc     сессия пользователя
     * @param userId ID пользователя
     * @return дата
     * @throws GranException при необходимости
     */
    public long getUserExpireDate(SessionContext sc, String userId) throws GranException {
        log.trace("getUserExpireDate");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getUserExpireDate", "sc", sc);
        if (userId == null)
            throw new InvalidParameterException(this.getClass(), "getUserExpireDate", "userId", sc);
        if (!sc.allowedByUser(userId))
            throw new AccessDeniedException(this.getClass(), "getUserExpireDate", sc, "!sc.allowedByACL(userId)", userId);
        return KernelManager.getUser().getUserExpireDate(userId);
    }

    /**
     * Устанавливает максимальное колличество подчиненных пользователей
     *
     * @param sc     сессия пользователя
     * @param userId ID пользвоателя
     * @param count  Колличетсво подчиненных пользователей
     * @throws GranException при необходимости
     */
    public void setMaximumChildrenAllowed(SessionContext sc, String userId, Integer count) throws GranException {
        log.trace("setMaximumChildrenAllowed");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "setMaximumChildrenAllowed", "sc", sc);
        if (userId == null)
            throw new InvalidParameterException(this.getClass(), "setMaximumChildrenAllowed", "userId", sc);
        if (!(sc.allowedByUser(userId) || userId.equals(sc.getUserId())))
            throw new AccessDeniedException(this.getClass(), "setMaximumChildrenAllowed", sc, "!(sc.allowedByACL(userId) || userId.equals(sc.getUserId()))", userId);
        Integer chAl = UserRelatedManager.getInstance().find(userId).getChildAllowed();
        KernelManager.getUser().setMaximumChildrenAllowed(userId, count);
        if (KernelManager.getUser().getAllowableUserQty(userId) < 0) {
            KernelManager.getUser().setMaximumChildrenAllowed(userId, chAl);
            throw new UsersLimitExceedException();
        }
    }

    /**
     * GПроверяет активен пользователь или нет
     *
     * @param sc     сессия пользователя
     * @param userId ID пользователя, которого проверяем
     * @return TRUE - если активен, FALSE - если нет
     * @throws GranException при необходимости
     */
    public boolean getActive(SessionContext sc, String userId) throws GranException {
        log.trace("getActive");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getActive", "sc", sc);
        if (userId == null)
            throw new InvalidParameterException(this.getClass(), "getActive", "userId", sc);
        if (!sc.userOnSight(userId))
            throw new AccessDeniedException(this.getClass(), "getActive", sc, "!sc.userOnSight(userId)", userId);
        return KernelManager.getUser().getActive(userId);
    }

    /**
     * Восстанавливает пароль пользователя - отсылается письмо на e-mail пользователя с новым паролем
     *
     * @param email почта пользователя
     * @param login логин пользователя
     * @throws GranException при необходимости
     */
    public void forgotPassword(String login, String email) throws GranException {
        log.trace("forgotPassword");
        String userId = KernelManager.getUser().findUserByEmailAndName(email, login);
        SessionContext sc = SessionManager.getInstance().getSessionContext(SessionManager.getInstance().
                create(UserRelatedManager.getInstance().find(userId)));
        if (sc == null)
            return;
        if (!sc.canAction(UserAction.editUserPasswordHimself, sc.getUserId()))
            throw new UserException("ERROR_NO_RIGHT_FOR_OPERATION");
        KernelManager.getUser().forgotPassword(userId);
    }

    /**
     * Проверяет является ли один пользователь родителем для другого
     *
     * @param sc       сессия пользователя
     * @param parentId ID родительского пользователя
     * @param userId   ID дочернего пользователя
     * @return TRUE - если является, FALSE - если нет
     * @throws GranException при необходимости
     */
    public boolean isParentOf(SessionContext sc, String parentId, String userId) throws GranException {
        log.trace("isParentOf");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "isParentOf", "sc", sc);
        return KernelManager.getUser().isParentOf(parentId, userId);
    }

    /**
     * Возвращает ID пользователя с указанным логином
     *
     * @param sc    сессия пользователя
     * @param login Логин
     * @return ID пользователя
     * @throws GranException при необходимости
     */
    public SecuredUserBean findByLogin(SessionContext sc, String login) throws GranException {
        log.trace("findByLogin");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "findByLogin", "sc", sc);
        if (login == null)
            throw new InvalidParameterException(this.getClass(), "findByLogin", "login", sc);
        String id = KernelManager.getUser().findByLogin(login);
        if (id == null)
            return null;
        return new SecuredUserBean(id, sc);
    }

    /**
     * Возвращает ID пользователя с указанным именем
     *
     * @param sc   сессия пользователя
     * @param name Имя
     * @return ID пользователя
     * @throws GranException при необходимости
     */
    public SecuredUserBean findByName(SessionContext sc, String name) throws GranException {
        log.trace("findByLogin");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "findByName", "sc", sc);
        if (name == null)
            throw new InvalidParameterException(this.getClass(), "findByName", "name", sc);
        String id = KernelManager.getUser().findByName(name);
        if (id == null)
            return null;
        return new SecuredUserBean(id, sc);
    }

    /**
     * Производит поиск ID пользователя по логину и имени
     *
     * @param sc      сессия пользователя
     * @param quickGo логин или имя пользователя
     * @return ID найденного пользователя
     * @throws GranException при необходимости
     */
    public String findUserIdByQuickGo(SessionContext sc, String quickGo) throws GranException {
        log.trace("findUserIdByQuickGo");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "findUserIdByQuickGo", "sc", sc);
        if (quickGo == null)
            throw new InvalidParameterException(this.getClass(), "findUserIdByQuickGo", "quickGo", sc);
        return KernelManager.getUser().findByLoginOrNameFromCache(quickGo);
    }

    /**
     * Вставляет пользователя при операции PASTE
     *
     * @param sc       сессия пользователя
     * @param parentId ID пользователя, куда вставляем
     * @param userIds  список ID вставляемых пользователей
     * @throws GranException при необходимости
     */
    public void pasteUsers(SessionContext sc, String parentId, String[] userIds) throws GranException {
        log.trace("pasteUsers");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "pasteUsers", "sc", sc);
        if (parentId == null)
            throw new InvalidParameterException(this.getClass(), "pasteUsers", "parentId", sc);
        if (userIds == null)
            throw new InvalidParameterException(this.getClass(), "pasteUsers", "userIds", sc);
        if (!KernelManager.getUser().isParentValidForOperation(userIds, parentId))
            throw new UserException("ERROR_CAN_NOT_PASTE_USERS");
        for (String userId : userIds) {
            UserRelatedInfo user = UserRelatedManager.getInstance().find(userId);
            if (!(AdapterManager.getInstance().getSecuredPrstatusAdapterManager().isManagerAvailable(sc, userId, parentId) && sc.canAction(Action.cutPasteUser, user.getId()) && sc.allowedByUser(user.getId()) && user.getParentId() != null))
                throw new UserException("ERROR_CAN_NOT_MOVE_USER", new Object[]{user.getName()});
        }

        for (String id : userIds) {
            SecuredUserBean bean = new SecuredUserBean(id, sc);
            boolean himselfOrParent = AdapterManager.getInstance().getSecuredUserAdapterManager().isParentOf(sc, id, sc.getUserId());
            if (!himselfOrParent)
                AdapterManager.getInstance().getSecuredUserAdapterManager().updateUser(sc, id, bean.getLogin(),
                        bean.getName(), bean.getTel(), bean.getEmail(),
                        bean.getPrstatusId(), parentId, bean.getTimezone(),
                        bean.getLocale(), bean.getCompany(), bean.getTemplate(),
                        bean.getDefaultProjectId(), bean.getExpireDate(), bean.getPreferences(), bean.isEnabled());
        }
    }

    /**
     * Полнотекстовый поиск пользователей
     *
     * @param from         начальный пользователь, от которого ищем
     * @param searchString искомая строка
     * @return список найденных пользователей
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredSearchUserItem
     */
    public ArrayList<SecuredSearchUserItem> fullTextSearch(SecuredUserBean from, String searchString) throws GranException {
        log.trace("fullTextSearch for users");
        HashMap<String, String> t = IndexManager.getIndex().searchUsersWithHighLight(searchString);
        ArrayList<SecuredSearchUserItem> v = new ArrayList<SecuredSearchUserItem>();
        if (t == null)
            return v;
        UserRelatedManager userRelatedManager = UserRelatedManager.getInstance();
        for (Map.Entry<String, String> e : t.entrySet()) {
            SecuredUserBean b = new SecuredUserBean(e.getKey(), from.getSecure());
            if (b.canView()) {
                if (userRelatedManager.getUserChain(from.getId(), b.getId()) != null)
                    v.add(new SecuredSearchUserItem(1, b, t.get(e.getKey()), searchString));
                else
                    v.add(new SecuredSearchUserItem(0, b, t.get(e.getKey()), searchString));
            }
        }
        return v;
    }

    /**
     * Возвращает список ID активных дочерних пользователей
     *
     * @param sc     сессия пользователя
     * @param userId ID родительского пользователя
     * @return список ID пользователей
     * @throws GranException при необходимости
     */
    public ArrayList<SecuredUserBean> getNotDeactivChildren(SessionContext sc, String userId) throws GranException {
        log.trace("getNotDeavtivChildren");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getChildren", "sc", null);
        if (userId == null)
            throw new InvalidParameterException(this.getClass(), "getChildren", "taskId", sc);
        ArrayList<SecuredUserBean> result = new ArrayList<SecuredUserBean>();
        List<String> list = KernelManager.getUser().getNotDeactivChildren(userId);
        for (String id : list) {
            SecuredUserBean sub = new SecuredUserBean(id, sc);
            if (sub.isOnSight())
                result.add(sub);
        }
        return result;
    }

     public List<String> getManagerUserList(SessionContext sc, String userId) throws GranException {
        log.trace("getPossibleManagerList");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getPossibleManagerList", "sc", sc);
        if (userId == null)
            throw new InvalidParameterException(this.getClass(), "getPossibleManagerList", "userId", sc);
        if (!sc.allowedByUser(userId))
            throw new AccessDeniedException(this.getClass(), "getPossibleManagerList", sc, "!sc.allowedByACL(userId)", userId);
        return KernelManager.getUser().getManagerUser(userId);
    }
}