/*
 * @(#)RegistrationManager.java
 * Copyright 2009 TrackStudio, Inc. All rights reserved.
 */
package com.trackstudio.kernel.manager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.mail.Session;
import javax.mail.internet.InternetAddress;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.adapter.email.EmailUtil;
import com.trackstudio.exception.GranException;
import com.trackstudio.exception.UserException;
import com.trackstudio.kernel.cache.TaskRelatedManager;
import com.trackstudio.kernel.cache.UserRelatedInfo;
import com.trackstudio.kernel.cache.UserRelatedManager;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.model.Registration;
import com.trackstudio.startup.Config;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.MailWriter;

import net.jcip.annotations.Immutable;

/**
 * Класс RegistrationManager содержит методы для работы с регистрациями
 */
@Immutable
public class RegistrationManager extends KernelManager {

    private static final String className = "RegistrationManager.";
    private static final RegistrationManager instance = new RegistrationManager();
    private static final LockManager lockManager = LockManager.getInstance();
    /**
     * Конструктор по умолчанию
     */
    private RegistrationManager() {
    }

    /**
     * Возвращает экземпляр текущего класса
     *
     * @return Экземпляр RegistrationManager
     */
    protected static synchronized RegistrationManager getInstance() {
        return instance;
    }

    /**
     * Создает копию правила регистрации (клонирует)
     *
     * @param registrationId ID копируемого правила регистрации
     * @param locale         Локаль
     * @param userId         ID Прользователя
     * @return ID нового правила
     * @throws GranException при необходимости
     */
    public String cloneRegistration(String registrationId, String locale, String userId) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            Registration reg = KernelManager.getFind().findRegistration(registrationId);
            String name = reg.getName();
            return hu.createObject(new Registration(name.endsWith(' ' + I18n.getString(locale, "CLONED")) ? name : name + ' ' +
                    I18n.getString(locale, "CLONED"), userId, reg.getPrstatus(), reg.getTask(),
                    reg.getCategory(), reg.getChildAllowed(), reg.getExpireDays(), reg.getPriv()));
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращаяет список правил регистрации для пользователя
     *
     * @param userId ID пользователя, для которого возвращаем правила регистрации
     * @return Список правил регистрации пользователя
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Registration
     */
    public Set<Registration> getRegistrationList(String userId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            Set<Registration> result = new HashSet<Registration>();
            result.addAll(hu.getList("from com.trackstudio.model.Registration r where r.user=?", userId));
            return result;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * This method return registration rules for user and plus all shares registration rules
     * @param userId user id
     * @return List
     * @throws GranException - unpredictable situation
     */
    public Set<Registration> getRegistrationSharesList(String userId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            Set<Registration> result = new HashSet<Registration>();
            result.addAll(hu.getList("select r from com.trackstudio.model.Registration r where r.user=? or r.priv=1", userId));
            return result;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Редактирует правило регистрации пользователя
     *
     * @param registrationId ID редактируемого правила регистрации
     * @param name           Название правила регистрации
     * @param prstatusId     Статус регистрируемых пользователей
     * @param child          Сколько пользователей может регистрировать текущий
     * @param expire         Срок действия учетных записей пользователей
     * @param categoryId     ID категории
     * @param priv           Приватное правило илил нет
     * @throws GranException при необходимости
     */
    public void updateRegistration(String registrationId, SafeString name, String prstatusId, Integer child, Integer expire,
                                   String categoryId, boolean priv) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            Registration obj = KernelManager.getFind().findRegistration(registrationId);
            obj.setName(name != null ? name.toString() : null);
            obj.setPrstatus(prstatusId);
            obj.setChildAllowed(child);
            obj.setExpireDays(expire);
            obj.setPriv(priv ? 1 : 0);
            obj.setCategory(categoryId);
            hu.updateObject(obj);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Удаляет правило регистрации пользователей
     *
     * @param registrationId ID удаляемого правила
     * @throws GranException при необходимости
     */
    public void deleteRegistration(String registrationId) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            hu.deleteObject(Registration.class, registrationId);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Создает правило регистрации
     *
     * @param name       Название праивла регшистрации
     * @param prstatusId Статус регистрируемых пользователей
     * @param taskId     ID Задачи
     * @param userId     ID пользователя
     * @param priv       Приватное правило илил нет
     * @return ID созданного правила
     * @throws GranException при необходимости
     */
    public String createRegistration(SafeString name, String prstatusId, String taskId, String userId, boolean priv) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            return hu.createObject(new Registration(name != null ? name.toString() : null, userId, prstatusId, taskId, null, null, null, priv ? 1 : 0));
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Регистрирует нового пользователя
     *
     * @param login    Логин регистрируемого пользователя
     * @param name     Имя пользователя
     * @param email    Email пользователя
     * @param locale   Локаль
     * @param timezone Таймзона пользователя
     * @param company  Компания
     * @param reg_id   ID правила регистрации
     * @return Новый пароль пользователя
     * @throws GranException при необходимости
     */
    public String register(SafeString login, SafeString name, SafeString email, String locale, String timezone, SafeString company, String reg_id) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            Registration reg = KernelManager.getFind().findRegistration(reg_id);
            if (KernelManager.getUser().getAllowableUserQty(reg.getUser().getId()) <= 0) {
                throw new UserException("ERROR_USER_LICENSE_EXCEED");
            }
            String userId = KernelManager.getUser().createUser(reg.getUser().getId(), login, name, reg.getPrstatus().getId(), company);
            String preferences = "";
            UserRelatedInfo newUser = UserRelatedManager.getInstance().find(userId);
            String edays = reg.getExpireDays() != null ? reg.getExpireDays().toString() : null;
            Calendar expire = null;
            if (edays != null) {
                int expireindays = Integer.valueOf(edays);
                expire = Calendar.getInstance();
                expire.add(Calendar.DATE, expireindays);
            }

            boolean newTask = reg.getCategory() != null;
            String task;
            if (newTask) {
                task = KernelManager.getTask().createTask(reg.getTask().getId(), newUser.getId(), reg.getCategory().getId(), SafeString.createSafeString(name + " Root Task"), TaskRelatedManager.getInstance().find(reg.getTask().getId()).getDeadline());
            } else {
                task = reg.getTask().getId();
            }
            KernelManager.getUser().updateUser(newUser.getId(), SafeString.createSafeString(newUser.getLogin()), SafeString.createSafeString(newUser.getName()), SafeString.createSafeString(newUser.getTel()), email,
                    newUser.getPrstatusId(), reg.getUser().getId(), timezone, locale, company, SafeString.createSafeString(newUser.getTemplate()),
                    task, expire, SafeString.createSafeString(preferences), true);
            KernelManager.getAcl().createAcl(task, null, newUser.getId(), null, newUser.getId());
            KernelManager.getUser().setMaximumChildrenAllowed(newUser.getId(), reg.getChildAllowed());
            String pwd = "p" + System.currentTimeMillis() % 1001L;
            AdapterManager.getInstance().getAuthAdapterManager().changePassword(newUser.getId(), pwd);
            if (Config.getInstance().isSendMail())
                sendRegisterMessage(newUser.getId(), pwd);
            return pwd;
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Отсылает письмо о регистрации
     *
     * @param user_id ID пользователя
     * @param pwd     пароль пользователя
     * @throws GranException при необходимости
     */
    public void sendRegisterMessage(String user_id, String pwd) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            Session session = Config.getInstance().getSession();
            UserRelatedInfo userRelatedInfo = UserRelatedManager.getInstance().find(user_id);
            EmailUtil.sendEmail(EmailUtil.buildDataForUser(userRelatedInfo, pwd), EmailUtil.buildTo(userRelatedInfo), "registration.ftl_h", userRelatedInfo);
            if (userRelatedInfo.getEmail() != null && userRelatedInfo.getEmail().length() != 0 && userRelatedInfo.getName() != null && userRelatedInfo.getName().length() != 0) {
                if (Config.getInstance().isTSHost()) {
                    MailWriter mw = new MailWriter(session);
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("X-TrackStudio", "user registered");
                    String msgbody2 = "<pre>User Login: " + userRelatedInfo.getLogin() + "\nUser Name: " + userRelatedInfo.getName() + "\nEmail: " + userRelatedInfo.getEmail();
                    msgbody2 += "\n<a href='mailto:" + userRelatedInfo.getName() + " <" + userRelatedInfo.getEmail() + ">?subject=TrackStudio&amp;body=" + userRelatedInfo.getName() + ",'>mailto</a></pre>";
                    InternetAddress[] to2 = {new InternetAddress("maximkr@trackstudio.com", "Maxim Kramarenko", Config.getEncoding())};
                    InternetAddress[] bcc2 = {};
                    String subject2 = "[Live Lead] " + userRelatedInfo.getName();
                    MailWriter mw2 = new MailWriter(Config.getInstance().getSession());
                    InternetAddress from2 = mw2.getDefaultReplyTo(null)[0];
                    mw.send(from2, from2, to2, bcc2, null, subject2, msgbody2, "text/html; charset=\"" + Config.getEncoding() + "\"", 3, headers, null);
                }
            }
        } catch (UserException e) {
            throw e;
        } catch (Exception e) {
            throw new GranException(e);
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает список публичных регистраций
     *
     * @return список регистраций
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Registration
     */
    public List<Registration> getPublicRegistrationList() throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            List<Registration> result = new ArrayList<Registration>();
            for (Object o : hu.getList("from com.trackstudio.model.Registration r order by r.name")) {
                Registration reg = (Registration) o;
                if (reg.getPriv() == null || reg.getPriv() == 1) {
                    boolean active = KernelManager.getUser().getActive(reg.getUser().getId());
                    long expire = KernelManager.getUser().getUserExpireDate(reg.getUser().getId());
                    if (active && (expire == 0 || expire > System.currentTimeMillis()))
                        result.add(reg);
                }
            }
            return result;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает список всех регистраций
     *
     * @return список регистраций
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Registration
     */
    public List<Registration> getAllRegistrastionList() throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            List<Registration> result = new ArrayList<Registration>();
            for (Object o : hu.getList("from com.trackstudio.model.Registration r order by r.name")) {
                Registration reg = (Registration) o;
                boolean active = KernelManager.getUser().getActive(reg.getUser().getId());
                long expire = KernelManager.getUser().getUserExpireDate(reg.getUser().getId());
                if (active && (expire == 0 || expire > System.currentTimeMillis()))
                    result.add(reg);
            }
            return result;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает ID правила регистрации по его названию
     *
     * @param name       Название правила регистрации
     * @param taskNumber Номер задачи
     * @return ID правила регистрации
     * @throws GranException при необходимости
     */
    public String getRegistrationByName(String name, String taskNumber) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            String taskId = FindManager.getTask().findByNumber(taskNumber);
            String registrationId = null;
            if (taskId != null) {
                ArrayList<String> tasks = KernelManager.getTask().getTaskIdChain(null, taskId);
                Map<String, Collection> paramsList = new LinkedHashMap<String, Collection>();
                paramsList.put("tasks", tasks);
                Map<String, String> paramsString = new LinkedHashMap<String, String>();
                paramsString.put("registration", name);
                List<String> list = hu.getList("select r.id from com.trackstudio.model.Registration r where r.name=:registration and r.task.id in (:tasks)", paramsString, paramsList);
                if (list != null && !list.isEmpty())
                    registrationId = list.get(0);
            }
            return registrationId;
        } catch (Exception e) {
            throw new GranException(e);
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }
}