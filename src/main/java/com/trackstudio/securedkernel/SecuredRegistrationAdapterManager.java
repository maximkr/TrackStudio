package com.trackstudio.securedkernel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.AccessDeniedException;
import com.trackstudio.exception.GranException;
import com.trackstudio.exception.InvalidParameterException;
import com.trackstudio.exception.TaskNotFoundException;
import com.trackstudio.exception.UserException;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.kernel.manager.SafeString;
import com.trackstudio.model.Registration;
import com.trackstudio.secured.SecuredCategoryBean;
import com.trackstudio.secured.SecuredPrstatusBean;
import com.trackstudio.secured.SecuredRegistrationBean;
import com.trackstudio.tools.ParameterValidator;
import com.trackstudio.tools.SecuredBeanUtil;

import net.jcip.annotations.Immutable;

/**
 * Класс SecuredRegistrationAdapterManager содержит методы для работы с регистрациями
 */
@Immutable
public class SecuredRegistrationAdapterManager {

    private static final Log log = LogFactory.getLog(SecuredRegistrationAdapterManager.class);
    private static final ParameterValidator pv = new ParameterValidator();

    /**
     * Создает правило регистрации
     *
     * @param sc         сессия пользователя
     * @param name       Название праивла регшистрации
     * @param prstatusId Статус регистрируемых пользователей
     * @param id         ID Задачи
     * @return ID созданного правила
     * @throws GranException при необходимости
     */
    public String createRegistration(SessionContext sc, String id, String name, String prstatusId) throws GranException {
        log.trace("createRegistration");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "createRegistration", "sc", null);
        if (name == null || pv.badSmallDesc(name))
            throw new InvalidParameterException(this.getClass(), "createRegistration", "name", sc);
        if (prstatusId == null)
            throw new InvalidParameterException(this.getClass(), "createRegistration", "prstatusId", sc);
        if (id == null)
            throw new TaskNotFoundException(null);
        SecuredPrstatusBean prstatus = AdapterManager.getInstance().getSecuredFindAdapterManager().findPrstatusById(sc, prstatusId);
        if (!sc.allowedByACL(id))
            throw new UserException("ERROR_TASK_NOT_ACCESSIBLE");
        if (!sc.canAction(Action.manageRegistrations, id))
            throw new AccessDeniedException(this.getClass(), "createRegistration", sc, "!sc.canAction(Action.createRegistration, toUserId)", id);
        if (!prstatus.canView())
            throw new AccessDeniedException(this.getClass(), "createRegistration", sc, "!prstatus.canView()", prstatusId);

        return KernelManager.getRegistration().createRegistration(SafeString.createSafeString(name), prstatusId, id, sc.getUserId(), false);
    }

    /**
     * Удаляет правило регистрации пользователей
     *
     * @param sc             сессия пользователя
     * @param registrationId ID удаляемого правила
     * @throws GranException при необходимости
     */
    public void deleteRegistration(SessionContext sc, String registrationId) throws GranException {
        log.trace("deleteRegistration");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "deleteRegistration", "sc", sc);
        if (registrationId == null)
            throw new InvalidParameterException(this.getClass(), "deleteRegistration", "registrationId", sc);
        SecuredRegistrationBean registration = AdapterManager.getInstance().getSecuredFindAdapterManager().findRegistrationById(sc, registrationId);
        //if (!(sc.canAction(com.trackstudio.kernel.cache.Action.deleteRegistration, registration.getUserId()) && registration.canUpdate()))
        //    throw new AccessDeniedException(this.getClass(), "deleteRegistration", sc);
        if (!sc.canAction(Action.manageRegistrations, registration.getUserId()))
            throw new AccessDeniedException(this.getClass(), "deleteRegistration", sc, "!sc.canAction(com.trackstudio.kernel.cache.Action.deleteRegistration, registration.getUserId())", registrationId);
        if (!registration.canManage())
            throw new AccessDeniedException(this.getClass(), "deleteRegistration", sc, "!registration.canUpdate()", registrationId);
        KernelManager.getRegistration().deleteRegistration(registrationId);
    }

    /**
     * Редактирует правило регистрации пользователя
     *
     * @param sc             сессия пользователя
     * @param registrationId ID редактируемого правила регистрации
     * @param name           Название правила регистрации
     * @param prstatusId     Статус регистрируемых пользователей
     * @param child          Сколько пользователей может регистрировать текущий
     * @param expire         Срок действия учетных записей пользователей
     * @param categoryId     ID категории
     * @param priv           Приватное правило илил нет
     * @throws GranException при необходимости
     */
    public void updateRegistration(SessionContext sc, String registrationId, String name, String prstatusId,
                                   Integer child, Integer expire, String categoryId, boolean priv) throws GranException {
        log.trace("updateRegistration");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "updateRegistration", "sc", sc);
        if (registrationId == null)
            throw new InvalidParameterException(this.getClass(), "updateRegistration", "registrationId", sc);
        if (name == null || pv.badSmallDesc(name))
            throw new InvalidParameterException(this.getClass(), "updateRegistration", "name", sc);
        if (prstatusId == null)
            throw new InvalidParameterException(this.getClass(), "updateRegistration", "prstatusId", sc);

        SecuredRegistrationBean registration = AdapterManager.getInstance().getSecuredFindAdapterManager().findRegistrationById(sc, registrationId);
        SecuredCategoryBean category = AdapterManager.getInstance().getSecuredFindAdapterManager().findCategoryById(sc, categoryId);
        if (!sc.canAction(Action.manageRegistrations, registration.getTaskId()))
            throw new AccessDeniedException(this.getClass(), "updateRegistration", sc, "!sc.canAction(com.trackstudio.kernel.cache.Action.editRegistration, registration.getUserId())", registrationId);
        if (!registration.canManage())
            throw new AccessDeniedException(this.getClass(), "updateRegistration", sc, "!registration.canUpdate()", registrationId);
        if (categoryId != null && !category.canView())
            throw new AccessDeniedException(this.getClass(), "updateRegistration", sc, "categoryId != null && !category.canView()", categoryId);
        KernelManager.getRegistration().updateRegistration(registrationId, SafeString.createSafeString(name), prstatusId, child, expire, categoryId, priv);
    }

    /**
     * Возвращаяет список правил регистрации для текущего пользователя
     *
     * @param sc     сессия пользователя
     * @param taskId ID задачи
     * @return Список правил регистрации пользователя
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Registration
     */
    public ArrayList<SecuredRegistrationBean> getRegistrationList(SessionContext sc, String taskId) throws GranException {
        log.trace("getPublicRegistrationList");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getPublicRegistrationList", "sc", null);
        if (taskId == null)
            throw new InvalidParameterException(this.getClass(), "getPublicRegistrationList", "taskId", sc);
        //if (!(sc.canAction(Action.viewRegistration, userId) && sc.allowedByACL(userId)))
        //    throw new AccessDeniedException(this.getClass(), "getPublicRegistrationList", sc);
        //if (!sc.canAction(Action.viewRegistration, id))
        if (!sc.canAction(Action.manageRegistrations, taskId))
            throw new AccessDeniedException(this.getClass(), "getPublicRegistrationList", sc, "!sc.canAction(Action.viewRegistration, userId)", taskId);
        if (!sc.allowedByACL(taskId))
            throw new AccessDeniedException(this.getClass(), "getPublicRegistrationList", sc, "!sc.allowedByACL(taskId)", taskId);
        ArrayList<SecuredRegistrationBean> c = SecuredBeanUtil.toArrayList(sc, KernelManager.getRegistration().getRegistrationList(sc.getUserId()), SecuredBeanUtil.REGISTRATION);

        Collections.sort(c);
        return c;
    }

    /**
     * This method return registration rules for user and plus all shares registration rules
     * @param sc session user
     * @param taskId taskId
     * @return List
     * @throws GranException - unpredictable situation
     */
    public ArrayList<SecuredRegistrationBean> getRegistrationSharesList(SessionContext sc, String taskId) throws GranException {
        log.trace("getPublicRegistrationList");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getPublicRegistrationList", "sc", null);
        if (taskId == null)
            throw new InvalidParameterException(this.getClass(), "getPublicRegistrationList", "taskId", sc);
        if (!sc.canAction(Action.manageRegistrations, taskId))
            throw new AccessDeniedException(this.getClass(), "getPublicRegistrationList", sc, "!sc.canAction(Action.viewRegistration, userId)", taskId);
        if (!sc.allowedByACL(taskId))
            throw new AccessDeniedException(this.getClass(), "getPublicRegistrationList", sc, "!sc.allowedByACL(taskId)", taskId);
        ArrayList<SecuredRegistrationBean> c = SecuredBeanUtil.toArrayList(sc, KernelManager.getRegistration().getRegistrationSharesList(sc.getUserId()), SecuredBeanUtil.REGISTRATION);

        Collections.sort(c);
        return c;
    }


    /**
     * Возвращает список публичных регистраций
     *
     * @param sc сессия пользователя
     * @return список регистраций
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Registration
     */
    public List<Registration> getRegistrationList(SessionContext sc) throws GranException {
        log.trace("getProjects");
        return KernelManager.getRegistration().getPublicRegistrationList();
    }

    /**
     * Регистрирует нового пользователя
     *
     * @param sc             сессия пользователя
     * @param login          Логин регистрируемого пользователя
     * @param name           Имя пользователя
     * @param email          Email пользователя
     * @param locale         Локаль
     * @param timezone       Таймзона пользователя
     * @param company        Компания
     * @param registrationId ID правила регистрации
     * @return Новый пароль пользователя
     * @throws GranException при необходимости
     */
    public String register(SessionContext sc, String login, String name, String email, String locale, String timezone,
                           String company, String registrationId) throws GranException {
        log.trace("register");
        return KernelManager.getRegistration().register(SafeString.createSafeString(login), SafeString.createSafeString(name), SafeString.createSafeString(email), locale, timezone, SafeString.createSafeString(company), registrationId);
    }
}
