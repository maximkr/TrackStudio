/*
 * @(#)UserManager.java
 * Copyright 2009 TrackStudio, Inc. All rights reserved.
 */
package com.trackstudio.kernel.manager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.AuthException;
import com.trackstudio.exception.DuplicateUserLoginException;
import com.trackstudio.exception.GranException;
import com.trackstudio.exception.LicenseException;
import com.trackstudio.exception.UserException;
import com.trackstudio.exception.UsersLimitExceedException;
import com.trackstudio.kernel.cache.AttachmentCacheItem;
import com.trackstudio.kernel.cache.TaskRelatedManager;
import com.trackstudio.kernel.cache.UserRelatedInfo;
import com.trackstudio.kernel.cache.UserRelatedManager;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.model.Acl;
import com.trackstudio.model.User;
import com.trackstudio.model.Usersource;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.startup.Config;
import com.trackstudio.startup.I18n;
import com.trackstudio.startup.TSLoader;
import com.trackstudio.tools.MD5;
import com.trackstudio.tools.MailWriter;

import net.jcip.annotations.Immutable;

import static com.trackstudio.tools.Null.isNotNull;

/**
 * Класс UserManager содержит методы для работы с пользователями
 */
@Immutable
public class UserManager extends KernelManager {

    private static final String className = "UserManager.";
    private static final Log log = LogFactory.getLog(UserManager.class);
    private static final UserManager instance = new UserManager();
    private static final LockManager lockManager = LockManager.getInstance();
    private static final boolean CASE_LOGIN = Config.isTurnItOn("trackstudio.security.password.case");
    /**
     * Конструктор по умолчанию
     */
    private UserManager() {
    }

    /**
     * Возвращает экземпляр текущего класса
     *
     * @return Экземпляр UserManager
     */
    protected static UserManager getInstance() {
        return instance;
    }

    /**
     * Производит поиск ID пользователя по логину и имени
     *
     * @param quick_go логин или имя пользователя
     * @return ID найденного пользователя
     * @throws GranException при необходимости
     */
    public String findUserIdByQuickGo(String quick_go) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            String user_id = null;
            if (quick_go != null && quick_go.length() != 0)
                user_id = KernelManager.getUser().findByLogin(quick_go);
            if (user_id == null && quick_go != null && quick_go.length() != 0)
                user_id = KernelManager.getUser().findByName(quick_go);
            return user_id;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Восстанавливает пароль пользователя - отсылается письмо на e-mail пользователя с новым паролем
     *
     * @param userId ID пользователя, чей пароль восстанавливается
     * @throws GranException при необходимости
     */
    public void forgotPassword(String userId) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            javax.mail.Session session = Config.getInstance().getSession();
            User u = KernelManager.getFind().findUser(userId);
            if (u.getEmail() == null || u.getEmail().trim().length() == 0)
                throw new UserException("ERROR_NO_EMAIL_FOR_USER");
            MailWriter mw = new MailWriter(session);
            String siteURL = Config.getInstance().getSiteURL();
            String pwd = "p" + System.currentTimeMillis() % 1001L + 'z' + System.currentTimeMillis() % 108L;
            AdapterManager.getInstance().getAuthAdapterManager().changePassword(u.getId(), pwd);
            String[] param = new String[]{u.getName(), u.getLogin(), pwd, siteURL, u.getEmail(), u.getCompany()};
            String msgbody = I18n.getString(u.getLocale(), "NEW_ACCOUNT_MESSAGE", param);

            ArrayList<String> al = u.getEmailList();
            InternetAddress[] to = new InternetAddress[al.size()];
            int i = 0;
            for (String s : al) {
                to[i] = new InternetAddress(s, u.getName(), Config.getEncoding());
                i++;
            }
            InternetAddress[] bcc = {};
            InternetAddress from = mw.getDefaultReplyTo(null)[0];
            mw.send(from, from, to, bcc, null, "Your TrackStudio new password, " + u.getName(), msgbody, "text/html;\n charset=\"" + Config.getEncoding() + "\"", 3, new HashMap<String, String>(), null);
        } catch (UserException e) {
            throw e;
        } catch (Exception e) {
            throw new GranException(e);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Редактирует пользователя
     *
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
    public void updateUser(String userId, SafeString login, SafeString name, SafeString tel, SafeString email,
                           String prstatusId, String managerId,
                           String timezone, String locale, SafeString company,
                           SafeString template, String taskId, Calendar expire, SafeString preferences,
                           boolean enabled) throws GranException {
        log.trace("updateUser(expire='" + expire + "')");

        boolean w = lockManager.acquireConnection(className);
        try {
            String safelogin = login.toString().replace(';', '_');
            safelogin = safelogin.replace(' ', '_');
            safelogin = safelogin.replace('+', '_');
            if (managerId != null && managerId.length() == 0) {
                managerId = null;
            }
            boolean farManager = false;
            String oldManager = null;
            String oldPrstatus;
            boolean statusChanged;
            User u = (User) hu.getObject(User.class, userId);
            oldPrstatus = u.getPrstatus().getId();
            statusChanged = !oldPrstatus.equals(prstatusId);
            User um = u.getManager();
            Integer enabledInteger = enabled ? 1 : 0;
            if (u.getManager() != null) {
                oldManager = u.getManager().getId();
            }
            if (managerId != null) {
                u.setManager(managerId);
            }
            User newManager = u.getManager() == null ? null : (User) hu.getObject(User.class, u.getManager().getId());
            if (newManager != null && !KernelManager.getPrstatus().isManagerAvailable(u.getId(), newManager.getId())) {//far manager
                // ����������, ��� ������������ � ���� �������� �� ����� ���� �������� � ��������
                farManager = true;
                log.debug("Move user to far manager");
                prstatusId = newManager.getPrstatus().getId();
                List<String> userForChange = UserRelatedManager.getInstance().find(u.getId()).getDescendents();
                userForChange.add(userId);
                for (Object anUserForChange : userForChange) {
                    String childId = (String) anUserForChange;
                    User child = (User)  hu.getObject(User.class, childId);
                    child.setPrstatus(prstatusId);
                    hu.updateObject(child);
                    for (Object o : KernelManager.getAcl().getAclUserList(childId)) {
                        Acl acl = (Acl) o;
                        if (acl.getOverride() == null || acl.getOverride() == 0) {
                            acl.setPrstatus(prstatusId);
                            hu.updateObject(acl);
                        }
                    }
                }
            }
            u.setLogin(safelogin);
            u.setTimezone(timezone);
            u.setLocale(locale);
            u.setName(name != null ? name.toString() : null);
            u.setTel(tel != null ? tel.toString() : null);
            u.setEmail(email != null ? email.toString() : null);
            u.setPrstatus(prstatusId);
            u.setPreferences(preferences != null ? preferences.toString() : null);
            u.setDefaultProject(taskId);
            u.setCompany(company != null ? company.toString() : null);
            u.setTemplate(isNotNull(template) ? template.toString() : "default_html.ftl");
            u.setExpireDate(expire);
            u.setActive(enabledInteger);
            if (KernelManager.getUser().getAllowableUserQty(um != null ? um.getId() : u.getId()) < 0)
                throw new UsersLimitExceedException();
            hu.updateObject(u);
            hu.cleanSession();
            if (statusChanged) {
                TaskRelatedManager.getInstance().invalidateAclWhenChangeStatus(userId, oldPrstatus);
            }
            hu.cleanSession();
            if (oldManager != null && oldManager.equals(managerId)) {
                UserRelatedManager.getInstance().invalidateWhenUpdate(userId);
            } else if (oldManager != null) {
                UserRelatedManager.getInstance().invalidateWhenMove(userId, oldManager, managerId);
            } else {
                UserRelatedManager.getInstance().invalidateWhenUpdate(userId);
            }
            hu.cleanSession();
            if (farManager) {
                for (String childId : UserRelatedManager.getInstance().find(userId).getDescendents()) {
                    UserRelatedManager.getInstance().invalidateWhenUpdate(childId);
                }
            }
            hu.cleanSession();
        } catch (Exception e) {
            log.error("Error : params " +
                    ": userId = " + userId +
                    ": login = " + login +
                    ": name = " + name +
                    ": tel = " + tel +
                    ": email = " + email +
                    ": prstatusId = " + prstatusId +
                    ": managerId = " + managerId +
                    ": timezone = " + timezone +
                    ": locale = " + locale +
                    ": company " + company +
                    ": template, " + template +
                    ": taskId," + taskId +
                    ": expire = " + expire +
                    ": preferences = " + preferences +
                    ": enabled = " + enabled, e);
            throw new GranException(e);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Редактирует время последнего логина пользователя
     *
     * @param userId ID пользователя
     * @throws GranException при необходимости
     */
    public void updateLastLogon(String userId) throws GranException {
        log.trace("updateLastLogon");
        boolean w = lockManager.acquireConnection(className);
        try {
            User u = (User) hu.getObject(User.class, userId);
            u.setLastLogonDate(Calendar.getInstance());
            hu.updateObject(u);
            hu.cleanSession();
            UserRelatedManager.getInstance().invalidateUser(userId, false);
        } catch (Exception e) {
            throw new GranException(e);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Редактирует настройки пользователя
     *
     * @param userId      ID пользователя
     * @param preferences Настройки
     * @throws GranException при необходимости
     */
    public void setPreferences(String userId, String preferences) throws GranException {
        log.trace("updateLastLogon");
        boolean w = lockManager.acquireConnection(className);
        try {
            User u = (User) hu.getObject(User.class, userId);
            u.setPreferences(preferences);
            hu.updateObject(u);
            UserRelatedManager.getInstance().invalidateUser(userId, true);
            hu.cleanSession();
        } catch (Exception e) {
            throw new GranException(e);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Создается новый пользователя
     *
     * @param parentUserId ID менеджера
     * @param login        Логин
     * @param name         Имя пользователя
     * @param prstatusId   ID статуса пользователя
     * @param company      Компания
     * @return ID созданного пользователя
     * @throws GranException при необходимости
     */
    public String createUser(String parentUserId, SafeString login, SafeString name, String prstatusId, SafeString company) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            String safelogin = login.toString().replace(';', '_');
            safelogin = safelogin.replace(' ', '_');
            safelogin = safelogin.replace('+', '_');
            // ikozhekin: it's need because of Registration's feature (task #16314)
            if (Config.isTurnItOn("trackstudio.security.password.case"))
                safelogin = safelogin.toUpperCase(Locale.ENGLISH);
            if (isUserWithLoginExists(safelogin))
                throw new DuplicateUserLoginException();

            UserRelatedInfo manager = UserRelatedManager.getInstance().find(parentUserId);

            String userid;
            User u = new User(safelogin, name.toString(), prstatusId, manager.getId());
            if (company != null)
                u.setCompany(company.toString());
            u.setLocale(manager.getLocale());
            u.setTimezone(manager.getTimezone());
            u.setTemplate(null);
            u.setActive(1);

            u.setChildAllowed(null);
            userid = hu.createObject(u);
            hu.cleanSession();
            String pwd = "p" + System.currentTimeMillis() % 1001L;
            UserRelatedManager.getInstance().invalidateWhenAdd(userid);
            AdapterManager.getInstance().getAuthAdapterManager().changePassword(userid, pwd);
            return userid;
        } catch (UserException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new GranException(ex);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает список доступных менеджеров
     *
     * @param userId       ID пользователя, для которого ищется список менеджеров
     * @param loggedUserId ID залогиненного пользователя
     * @return список пользователей
     * @throws GranException при необходимости
     * @see com.trackstudio.kernel.cache.UserRelatedInfo
     */
    public List<UserRelatedInfo> getPossibleManagerList(String userId, String loggedUserId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            List<String> loggedUserAndChildren = KernelManager.getUser().getUserAndChildrenListIdOnly(loggedUserId);
            List<String> assignUsers = KernelManager.getAcl().getAssignedUserList(loggedUserId);
            for (String ui : assignUsers) {
                loggedUserAndChildren.addAll(KernelManager.getUser().getUserAndChildrenListIdOnly(ui));
            }
            List<String> currentUserAndChildren = KernelManager.getUser().getUserAndChildrenListIdOnly(userId);
            loggedUserAndChildren.removeAll(currentUserAndChildren);
            ArrayList<UserRelatedInfo> itemCollection = new ArrayList<UserRelatedInfo>();
            itemCollection.addAll(UserRelatedManager.getInstance().getItemCollection(loggedUserAndChildren));
            return itemCollection;

        } finally {
            if (r) lockManager.releaseConnection(className);
        }
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
        boolean w = lockManager.acquireConnection(className);
        try {
            if (TSLoader.getInitException() != null)
                throw new UserException(TSLoader.getInitException());
            String userId = KernelManager.getUser().findByLogin(login);
            log.debug(" josso find by login : " + login + " userId " + userId);
            if (userId == null) {
                throw new AuthException();
            }
            User activeUser = KernelManager.getFind().findUser(userId);
            if (activeUser != null) {
                userId = activeUser.getId();
                boolean authPassed = false;
                if (Config.getInstance().isLogonAsAnotherUser()) {
                    for (UserRelatedInfo uci : getUserChain(null, userId)) {
                        if (AdapterManager.getInstance().getAuthAdapterManager().authorize(uci.getId(), password, request)) {
                            authPassed = true;
                            break;
                        }
                    }
                } else if (AdapterManager.getInstance().getAuthAdapterManager().authorize(userId, password, request))
                    authPassed = true;
                if (!authPassed)
                    throw new AuthException();

            }
            if (KernelManager.getUser().getUserExpireDate(userId) < System.currentTimeMillis() && !login.equals("root"))
                throw new LicenseException("ACCOUNT_HAS_BEEN_EXPIRED");
            if (!KernelManager.getUser().getActive(userId))
                throw new LicenseException("ACCOUNT_HAS_BEEN_LOCKED");
            if (KernelManager.getUser().getAllowableUserQty(userId) < 0 && !login.equals("root"))
                throw new LicenseException("TOO_MANY_CHILDREN");

            UserRelatedManager.getInstance().invalidateUser(userId, true);
            return userId;
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает колличество доступнных пользователей
     *
     * @param parentUserId ID пользователя
     * @return значение <0 если subusers > child_allowed <br/>
     *         значение <=0 если subusers = child_allowed <br/>
     *         значение =1 если subusers < child_allowed
     * @throws GranException при необходимости
     */
    public int getAllowableUserQty(String parentUserId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            int result = 1;
            for (UserRelatedInfo uci : getUserChain(null, parentUserId)) {
                int childrenExists = UserRelatedManager.getInstance().getActiveDescendents(uci.getId()).size();
                Integer childrenAllowed = UserRelatedManager.getInstance().getAllowedChildren(uci.getId());
                if (childrenAllowed != null && childrenAllowed - childrenExists < result)
                    result = childrenAllowed - childrenExists;
                if (result < 0) break;
            }
            return result;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает дату истекания срока действия логина пользователя
     *
     * @param userId ID пользователя
     * @return дата
     * @throws GranException при необходимости
     */
    public long getUserExpireDate(String userId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            long expireDate = 2000000000000000000L;
            Calendar expire = UserRelatedManager.getInstance().find(userId).getExpireDate();
            if (expire != null) {
                return expire.getTimeInMillis();
            } else {
                return expireDate;
            }
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает список цепочки пользователей от одного пользователя до другого
     *
     * @param startUserId ID начального пользователя
     * @param stopUserId  ID конечного пользователя
     * @return список пользователей
     * @throws GranException при необходимости
     * @see com.trackstudio.kernel.cache.UserRelatedInfo
     */
    public ArrayList<UserRelatedInfo> getUserChain(String startUserId, String stopUserId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            return UserRelatedManager.getInstance().getUserChain(startUserId, stopUserId);
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает ID пользователя по (Email и имени) или email или имени
     *
     * @param email  Email
     * @param name   Имя
     * @param taskId ID задачи
     * @return ID пользователя
     * @throws GranException при необходимости
     */
    public String findUserIdByEmailNameProject(String email, String name, String taskId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            String userid = findUserByEmailAndNameIgnoreCase(email, name, taskId);
            if (userid == null) {
                userid = findUserByEmailIgnoreCase(email, taskId);
            }
            if (userid == null) {
                userid = findUserByNameIgnoreCase(name, taskId);
            }
            return userid;
        } catch (Exception e) {
            throw new GranException(e);
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает ID пользователя по Email и имени
     *
     * @param email  Email
     * @param name   Имя
     * @param taskId ID задачи
     * @return ID пользователя
     * @throws GranException при необходимости
     */
    private String findUserByEmailAndNameIgnoreCase(String email, String name, String taskId) throws Exception {
        if (name == null || email == null) {
            return null;
        }
        List<UserRelatedInfo> users = UserRelatedManager.getInstance().getCacheContents();
        for (UserRelatedInfo userInfo : users) {
            if (userInfo.getName() != null && userInfo.getEmail() != null) {
                if (name.toUpperCase().equals(userInfo.getName().toUpperCase()) && email.toUpperCase().equals(userInfo.getEmail().toUpperCase())) {
                    if (TaskRelatedManager.getInstance().hasAccess(userInfo.getId(), taskId, userInfo.getPrstatusId())) {
                        return userInfo.getId();
                    }
                }
            }
        }
        return null;
    }

    /**
     * Возвращает ID пользователя по имени
     *
     * @param name   Имя
     * @param taskId ID задачи
     * @return ID пользователя
     * @throws GranException при необходимости
     */
    private String findUserByNameIgnoreCase(String name, String taskId) throws Exception {
        if (name == null)
            return null;
        List<UserRelatedInfo> users = UserRelatedManager.getInstance().getCacheContents();
        for (UserRelatedInfo userInfo : users) {
            if (userInfo.getName() != null) {
                if (name.toUpperCase().equals(userInfo.getName().toUpperCase())) {
                    if (TaskRelatedManager.getInstance().hasAccess(userInfo.getId(), taskId, userInfo.getPrstatusId())) {
                        return userInfo.getId();
                    }
                }
            }
        }
        return null;
    }

    public String findIdByLoginIgnoreCase(String login) throws GranException {
        if (login == null)
            return null;
        boolean r = lockManager.acquireConnection(className);
        try {
            List<UserRelatedInfo> users = UserRelatedManager.getInstance().getCacheContents();
            for (UserRelatedInfo userInfo : users) {
                if (userInfo.getName() != null) {
                    if (login.toUpperCase().equals(userInfo.getLogin().toUpperCase())) {
                        return userInfo.getId();
                    }
                }
            }
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
        return null;
    }


    /**
     * Возвращает ID пользователя по имени
     *
     * @param email  Email
     * @param taskId ID задачи
     * @return ID пользователя
     * @throws GranException при необходимости
     */
    public String findUserByEmailIgnoreCase(String email, String taskId) throws GranException {
        if (email == null)
            return null;
        boolean r = lockManager.acquireConnection(className);
        try {
            List<UserRelatedInfo> users = UserRelatedManager.getInstance().getCacheContents();
            for (UserRelatedInfo userInfo : users) {
                if (userInfo.getEmail() != null) {
                    if (email.toUpperCase().equals(userInfo.getEmail().toUpperCase())) {
                        if (TaskRelatedManager.getInstance().hasAccess(userInfo.getId(), taskId, userInfo.getPrstatusId())) {
                            return userInfo.getId();
                        }
                    }
                }
            }
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
        return null;
    }

    public String findUserByEmailIgnoreCaseForImportEmail(String email, String taskId) throws GranException {
        if (email == null)
            return null;
        boolean r = lockManager.acquireConnection(className);
        try {
            List<UserRelatedInfo> users = UserRelatedManager.getInstance().getCacheContents();
            for (UserRelatedInfo userInfo : users) {
                if (userInfo.getEmail() != null) {
                    if (email.equalsIgnoreCase(userInfo.getEmail())) {
                        if (userInfo.isEnabled() && TaskRelatedManager.getInstance().hasAccess(userInfo.getId(), taskId, userInfo.getPrstatusId())) {
                            return userInfo.getId();
                        }
                    }
                }
            }
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
        return null;
    }

    /**
     * Возвращает ID пользователя по email и логину
     *
     * @param email Email
     * @param login Логин
     * @return ID пользователя
     * @throws GranException при необходимости
     */
    public String findUserByEmailAndName(String email, String login) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            List<UserRelatedInfo> users = UserRelatedManager.getInstance().getCacheContents();
            for (UserRelatedInfo userInfo : users) {
                if (userInfo.getLogin() != null && userInfo.getEmail() != null && userInfo.getLogin().equals(login)) {
                    for (String em : userInfo.getEmailList()) {
                        if (em.equalsIgnoreCase(email)) {
                            return userInfo.getId();
                        }
                    }
                }
            }
            throw new UserException("ERROR_LOGIN_DOES_NOT_EXISTS");
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает список, состоящий из указанного пользователя и подчиненных пользователей
     *
     * @param userId ID пользователя, для которого получается список
     * @return список пользователей
     * @throws GranException при необходимости
     * @see com.trackstudio.kernel.cache.UserRelatedInfo
     */
    public List<UserRelatedInfo> getUserAndChildrenList(String userId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            List<String> childrenList = getInstance().getUserAndChildrenListIdOnly(userId);
            ArrayList<UserRelatedInfo> itemCollection = new ArrayList<UserRelatedInfo>();
            itemCollection.addAll(UserRelatedManager.getInstance().getItemCollection(childrenList));
            return itemCollection;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает список ID, состоящий из указанного пользователя и подчиненных пользователей
     *
     * @param userId ID пользователя, для которого получается список
     * @return список ID пользователей
     * @throws GranException при необходимости
     */
    public List<String> getUserAndChildrenListIdOnly(String userId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            // Get collection of children id + current user
            List<UserRelatedInfo> users = new ArrayList<UserRelatedInfo>();
            gatherAllChildren(users, UserRelatedManager.getInstance().find(userId));
            List<String> uids = new ArrayList<String>();
            for (UserRelatedInfo userInfo : users) {
                uids.add(userInfo.getId());
            }
            uids.add(userId);
            return uids;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    private void gatherAllChildren(final List<UserRelatedInfo> beans, UserRelatedInfo userBean) throws GranException {
        for (String userId : userBean.getChildren()) {
            UserRelatedInfo user = UserRelatedManager.getInstance().find(userId);
            beans.add(user);
            if (user.getChildCount() > 0) {
                gatherAllChildren(beans, user);
            }
        }
    }

    /**
     * Возвращает список дочерних пользователей для менеджера
     *
     * @param userId ID пользователя
     * @return список пользователей
     * @throws GranException при необходимости
     * @see com.trackstudio.kernel.cache.UserRelatedInfo
     */
    public List<UserRelatedInfo> getUserManagerChildren(String userId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            ArrayList<UserRelatedInfo> itemCollection = new ArrayList<UserRelatedInfo>();
            itemCollection.addAll(UserRelatedManager.getInstance().getItemCollection(UserRelatedManager.getInstance().getManagerChildren(userId)));
            return itemCollection;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Изменяется пароль пользователя
     *
     * @param userId   ID пользователя
     * @param password пароль
     * @throws GranException при необзодимости
     */
    public void changePassword(String userId, String password) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            log.trace("changePassword");
            log.debug("Password hash:" + MD5.encode(password));
            User user = (User) hu.getObject(User.class, userId);
            String oldpass;
            if (user.getPassword() == null)
                oldpass = "";
            else
                oldpass = user.getPassword().length() <= 32 * 5 ? user.getPassword() : user.getPassword().substring(0, 32 * 5 - 1);
            user.setPassword(MD5.encode(password) + oldpass);
            Calendar expire = Calendar.getInstance();
            expire.setTimeInMillis(System.currentTimeMillis());
            user.setPasswordChangedDate(expire);
            hu.cleanSession();
            UserRelatedManager.getInstance().invalidateUser(userId, true);
        } catch (Exception e) {
            throw new GranException(e);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Удаляется пользователь
     *
     * @param userId ID пользователя, которого удаляем
     * @throws GranException при необходимости
     */
    public void deleteUser(String userId) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            for (AttachmentCacheItem att : KernelManager.getAttachment().getAttachmentList(null, null, userId))
                KernelManager.getAttachment().deleteAttachment(att.getId());
            List<String> tasksForInvalidate = KernelManager.getAcl().getDirectAccessList(userId);
            List<String> usersForInvalidate = KernelManager.getAcl().getDirectAccessUserList(userId);
            hu.deleteObject(User.class, userId);
            UserRelatedManager.getInstance().invalidateWhenRemove(userId);
            for (String aTasksForInvalidate : tasksForInvalidate)
                TaskRelatedManager.getInstance().invalidateAcl(aTasksForInvalidate, userId, null);
            for (String anUsersForInvalidate : usersForInvalidate)
                UserRelatedManager.getInstance().invalidateAcl(anUsersForInvalidate, userId, null);

        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает список ID цепочки пользоватлелей от начального до конечного
     *
     * @param startid ID начального пользователя
     * @param stopid  ID конечного пользователя
     * @return список ID пользователей
     * @throws GranException при небходимости
     */
    public ArrayList<String> getUserIdChain(String startid, String stopid) throws GranException {
        ArrayList<String> list = new ArrayList<String>();
        if (stopid == null)
            return list;
        if (startid != null && startid.equals(stopid)) {
            list.add(startid);
            return list;
        }
        // optimization
        boolean r = lockManager.acquireConnection(className);
        try {
            return UserRelatedManager.getInstance().getUserIdChain(startid, stopid);
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Проверяет является ли один пользователь родителем для другого
     *
     * @param parentid ID родительского пользователя
     * @param childid  ID дочернего пользователя
     * @return TRUE - если является, FALSE - если нет
     * @throws GranException при необходимости
     */
    public boolean isParentOf(String parentid, String childid) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            return childid != null && !parentid.equals(childid) && getUserIdChain(parentid, childid) != null;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Проверяет активен пользователь или нет
     *
     * @param userId ID пользователя, которого проверяем
     * @return TRUE - если активен, FALSE - если нет
     * @throws GranException при необходимости
     */
    public boolean getActive(String userId) throws GranException {
        return UserRelatedManager.getInstance().isActive(userId);
    }

    /**
     * Устанавливает максимальное колличество подчиненных пользователей
     *
     * @param userId ID пользвоателя
     * @param count  Колличетсво подчиненных пользователей
     * @throws GranException при необходимости
     */
    public void setMaximumChildrenAllowed(String userId, Integer count) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            User user = KernelManager.getFind().findUser(userId);
            user.setChildAllowed(count);
            hu.updateObject(user);
            UserRelatedManager.getInstance().invalidateWhenUpdate(userId);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Проверяет существование пользователя с указанным логином
     *
     * @param login Логин
     * @return TRUE - если пользователь есть, FALSE - если нет
     * @throws GranException при необходимости
     */
    public boolean isUserWithLoginExists(String login) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            List<UserRelatedInfo> users = UserRelatedManager.getInstance().getCacheContents();
            for (UserRelatedInfo userInfo : users) {
                if (userInfo.getLogin() != null && userInfo.getLogin().equalsIgnoreCase(login)) {
                    return true;
                }
            }
            return false;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает ID пользователя с указанным логином
     *
     * @param login Логин
     * @return ID пользователя
     * @throws GranException при необходимости
     */
    public String findByLogin(String login) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            List<UserRelatedInfo> users = UserRelatedManager.getInstance().getCacheContents();
            for (UserRelatedInfo userInfo : users) {
                if (userInfo.getLogin() != null) {
                    String userLogin = userInfo.getLogin();
                    if (CASE_LOGIN ? userLogin.equalsIgnoreCase(login) : userLogin.equals(login)) {
                        return userInfo.getId();
                    }
                }
            }
            return null;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * This method for search user id by name or login in cache.
     *
     * @param key search key
     * @return user id
     * @throws GranException for need
     */
    public String findByLoginOrNameFromCache(String key) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            if (key == null) {
                return null;
            }
            List<UserRelatedInfo> infos = UserRelatedManager.getInstance().getCacheContents();
            for (UserRelatedInfo info : infos) {
                if (info.getLogin().contains(key)) {
                    return info.getId();
                } else if (info.getName().contains(key)) {
                    return info.getId();
                }
            }
            return null;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает ID пользователя с указанным логином с учетом прав доступа
     *
     * @param sc    Session Context текущего пользователя
     * @param login Логин
     * @return ID пользователя
     * @throws GranException при необходимости
     */
    public String findByLogin(SessionContext sc, String login) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            List<UserRelatedInfo> users = UserRelatedManager.getInstance().getCacheContents();
            for (UserRelatedInfo userInfo : users) {
                if (userInfo.getLogin() != null) {
                    boolean isCase = !Config.isTurnItOn("trackstudio.security.password.case");
                    if (isCase ? userInfo.getLogin().equals(login) : userInfo.getLogin().equalsIgnoreCase(login)) {
                        SecuredUserBean sub = AdapterManager.getInstance().getSecuredFindAdapterManager().findUserById(sc, login);
                        if (sub != null && sub.canView()) {
                            return userInfo.getId();
                        }
                    }
                }
            }
            return null;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает ID пользователя с указанным именем
     *
     * @param name Имя
     * @return ID пользователя
     * @throws GranException при необходимости
     */
    public String findByName(String name) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            List<UserRelatedInfo> users = UserRelatedManager.getInstance().getCacheContents();
            for (UserRelatedInfo userInfo : users) {
                if (userInfo.getName() != null) {
                    if (!Config.isTurnItOn("trackstudio.security.password.case")) {
                        if (userInfo.getName().equals(name)) {
                            return userInfo.getId();
                        }
                    } else {
                        if (userInfo.getName().equalsIgnoreCase(name)) {
                            return userInfo.getId();
                        }
                    }
                }
            }
            return null;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает ID пользователя с указанным именем c учетом прав доступа
     *
     * @param sc   Seesion context текущего пользователя
     * @param name Имя
     * @return ID пользователя
     * @throws GranException при необходимости
     */
    public String findByName(SessionContext sc, String name) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            List<UserRelatedInfo> users = UserRelatedManager.getInstance().getCacheContents();
            for (UserRelatedInfo userInfo : users) {
                if (userInfo.getName() != null) {
                    if (!Config.isTurnItOn("trackstudio.security.password.case")) {
                        if (userInfo.getName().equalsIgnoreCase(name)) {
                            SecuredUserBean sub = AdapterManager.getInstance().getSecuredFindAdapterManager().findUserById(sc, userInfo.getId());
                            if (sub != null && sub.canView()) {
                                return userInfo.getId();
                            }
                        }
                    } else {
                        if (userInfo.getName().equals(name)) {
                            SecuredUserBean sub = AdapterManager.getInstance().getSecuredFindAdapterManager().findUserById(sc, userInfo.getId());
                            if (sub != null && sub.canView()) {
                                return userInfo.getId();
                            }
                        }
                    }
                }
            }
            return null;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает объект Usersource для указанного пользователя и статуса
     *
     * @param userId     ID пользоватлея
     * @param prstatusId ID статуса
     * @return ID объекта Usersource
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Usersource
     */
    public String getUsersource(String userId, String prstatusId) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            String pk;
            List<String> tmpSet;
            if (userId != null && prstatusId != null)
                throw new GranException("Both userId and prstatusId are not null");
            else if (userId == null && prstatusId == null)
                tmpSet = hu.getList("select u.id from com.trackstudio.model.Usersource u where u.user is null and u.prstatus is null");
            else if (userId != null)
                tmpSet = hu.getList("select u.id from com.trackstudio.model.Usersource u where u.user=?", userId);
            else
                tmpSet = hu.getList("select u.id from com.trackstudio.model.Usersource u where u.prstatus=?", prstatusId);

            if (!tmpSet.isEmpty())
                pk = tmpSet.iterator().next();
            else {
                Usersource usersource = new Usersource(userId, prstatusId);
                pk = hu.createObject(usersource);
            }
            return pk;
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    public void deleteUsersource(String usersourceId) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            hu.deleteObject(Usersource.class, usersourceId);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Проверяет соответствие родительского пользователя и дочерних для операции copy-paste
     *
     * @param usersId массив ID дочерних пользователей
     * @param toUser  ID родительского пользователя
     * @return TRUE - если все хорошо, FALSE - если нет
     * @throws GranException при необходимости
     */
    public boolean isParentValidForOperation(String[] usersId, String toUser) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            ArrayList<UserRelatedInfo> chain = getUserChain(null, toUser);
            for (String id : usersId)
                if (chain.contains(UserRelatedManager.getInstance().find(id)))
                    return false;
            return true;
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает список ID активных дочерних пользователей
     *
     * @param parentId ID родительского пользователя
     * @return список ID пользователей
     * @throws GranException при необходимости
     */
    public List<String> getNotDeactivChildren(String parentId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            List<String> usersActive = new ArrayList<String>();
            List<UserRelatedInfo> users = UserRelatedManager.getInstance().getCacheContents();
            for (UserRelatedInfo userInfo : users) {
                if (userInfo.getParentId() != null && userInfo.getParentId().equals(parentId) && userInfo.isEnabled()) {
                    usersActive.add(userInfo.getId());
                }
            }
            return usersActive;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    public List<String> getManagerUser(String userManagerId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            List<String> usersChild = new ArrayList<String>();
            List<UserRelatedInfo> users = UserRelatedManager.getInstance().getCacheContents();
            for (UserRelatedInfo userInfo : users) {
                if (userInfo.getParentId() != null && userInfo.getParentId().equals(userManagerId)) {
                    usersChild.add(userInfo.getId());
                }
            }
            return usersChild;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }
}