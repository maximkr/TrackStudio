package com.trackstudio.securedkernel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.filter.FValue;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.constants.FilterConstants;
import com.trackstudio.exception.AccessDeniedException;
import com.trackstudio.exception.GranException;
import com.trackstudio.exception.InvalidParameterException;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.kernel.manager.SafeString;
import com.trackstudio.secured.SecuredFilterBean;
import com.trackstudio.secured.SecuredNotificationBean;
import com.trackstudio.secured.SecuredSubscriptionBean;
import com.trackstudio.secured.SecuredTaskFValueBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.secured.SecuredUserFValueBean;
import com.trackstudio.tools.ParameterValidator;
import com.trackstudio.tools.SecuredBeanUtil;
import com.trackstudio.tools.formatter.DateFormatter;

import net.jcip.annotations.Immutable;

/**
 * Класс FilterManager содержит методы для работы с фильтрами.
 */
@Immutable
public class SecuredFilterAdapterManager {

    private static final Log log = LogFactory.getLog(SecuredFilterAdapterManager.class);
    private static final ParameterValidator pv = new ParameterValidator();
    private static final LockManager lockManager = LockManager.getInstance();
    /**
     * Возвращает список нотификаций для заданного пользователя
     *
     * @param sc     сессия пользователя
     * @param userId ID пользователя, для которого достаются нотификации
     * @return Спсок нотификаций
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredNotificationBean
     */
    public List<SecuredNotificationBean> getUserNotificationList(SessionContext sc, String userId) throws GranException {
        boolean w = lockManager.acquireConnection(SecuredFilterAdapterManager.class.getSimpleName());
        try {
            log.trace("getUserNotificationList");
            if (sc == null)
                throw new InvalidParameterException(this.getClass(), "getUserNotificationList", "sc", sc);
            if (userId == null)
                throw new InvalidParameterException(this.getClass(), "getUserNotification", "userId", sc);
            if (!sc.allowedByUser(userId))
                throw new AccessDeniedException(this.getClass(), "getUserNotification", sc, "!sc.allowedByACL(userId)", userId);
            List<SecuredNotificationBean> ret = new ArrayList<SecuredNotificationBean>();
            List<SecuredNotificationBean> userNotificationList = SecuredBeanUtil.toArrayList(sc, KernelManager.getFilter().getUserNotificationList(userId), SecuredBeanUtil.NOTIFICATION);
            for (SecuredNotificationBean snb : userNotificationList) {
                if (snb.getFilter().getOwnerId() != null)//filter is not private
                    ret.add(snb);
            }
            return ret;
        } finally {
            if (w) lockManager.releaseConnection(SecuredFilterAdapterManager.class.getSimpleName());
        }
    }

    /**
     * Возвращает список нотификаций для заданного статуса
     *
     * @param sc         сессия пользователя
     * @param prstatusId ID статуса, для которого достаются нотификации
     * @return Спсок нотификаций
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredNotificationBean
     */
    public List<SecuredNotificationBean> getPrstatusNotificationList(SessionContext sc, String prstatusId) throws GranException {
        boolean w = lockManager.acquireConnection(SecuredFilterAdapterManager.class.getName());
        try {
            log.trace("getPrstatusNotificationList");
            if (sc == null)
                throw new InvalidParameterException(this.getClass(), "getPrstatusNotificationList", "sc", sc);
            if (prstatusId == null)
                throw new InvalidParameterException(this.getClass(), "getPrstatusNotificationList", "prstatusId", sc);
            List<SecuredNotificationBean> ret = new ArrayList<SecuredNotificationBean>();
            List<SecuredNotificationBean> prstatusNotificationList = SecuredBeanUtil.toArrayList(sc, KernelManager.getFilter().getPrstatusNotificationList(prstatusId), SecuredBeanUtil.NOTIFICATION);
            for (SecuredNotificationBean snb : prstatusNotificationList) {
                if (snb.getFilter().getOwnerId() != null)//filter is not private
                    ret.add(snb);
            }
            return ret;
        } finally {
            if (w) lockManager.releaseConnection(SecuredFilterAdapterManager.class.getName());
        }
    }

    /**
     * Возвращает список подписок для заданного пользователя
     *
     * @param sc     сессия пользователя
     * @param userId ID пользователя, для которого достаются подписки
     * @return Спсок нотификаций
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredSubscriptionBean
     */
    public List<SecuredSubscriptionBean> getUserSubscriptionList(SessionContext sc, String userId) throws GranException {
        boolean w = lockManager.acquireConnection(SecuredFilterAdapterManager.class.getSimpleName());
        try {
            log.trace("getUserSubscriptionList");
            if (sc == null)
                throw new InvalidParameterException(this.getClass(), "getUserSubscriptionList", "sc", sc);
            if (userId == null)
                throw new InvalidParameterException(this.getClass(), "getUserSubscriptionList", "userId", sc);
            if (!sc.allowedByUser(userId))
                throw new AccessDeniedException(this.getClass(), "getUserSubscriptionList", sc, "!sc.allowedByACL(userId)", userId);
            List<SecuredSubscriptionBean> ret = new ArrayList<SecuredSubscriptionBean>();
            List<SecuredSubscriptionBean> userSubscriptionList = SecuredBeanUtil.toArrayList(sc, KernelManager.getFilter().getUserSubscriptionList(userId), SecuredBeanUtil.SUBSCRIPTION);
            for (SecuredSubscriptionBean ssb : userSubscriptionList) {
                if (ssb.getFilter().getOwnerId() != null)//filter is not private
                    ret.add(ssb);
            }
            return ret;
        } finally {
            if (w) lockManager.releaseConnection(SecuredFilterAdapterManager.class.getSimpleName());
        }
    }

    /**
     * Возвращает список подписок для заданного статуса
     *
     * @param sc         сессия пользователя
     * @param prstatusId ID статуса, для которого достаются подписки
     * @return Спсок нотификаций
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredSubscriptionBean
     */
    public List<SecuredSubscriptionBean> getPrstatusSubscriptionList(SessionContext sc, String prstatusId) throws GranException {
        boolean w = lockManager.acquireConnection(SecuredFilterAdapterManager.class.getSimpleName());
        try {
            log.trace("getPrstatusSubscriptionList");
            if (sc == null)
                throw new InvalidParameterException(this.getClass(), "getPrstatusSubscriptionList", "sc", sc);
            if (prstatusId == null)
                throw new InvalidParameterException(this.getClass(), "getPrstatusSubscriptionList", "prstatusId", sc);
            List<SecuredSubscriptionBean> ret = new ArrayList<SecuredSubscriptionBean>();
            List<SecuredSubscriptionBean> prstatusSubscriptionList = SecuredBeanUtil.toArrayList(sc, KernelManager.getFilter().getPrstatusSubscriptionList(prstatusId), SecuredBeanUtil.SUBSCRIPTION);
            for (SecuredSubscriptionBean ssb : prstatusSubscriptionList) {
                if (ssb.getFilter().getOwnerId() != null)//filter is not private
                    ret.add(ssb);
            }
            return ret;
        } finally {
            if (w) lockManager.releaseConnection(SecuredFilterAdapterManager.class.getSimpleName());
        }
    }

    /**
     * Возвращает список фильтров задач для задачи taskId, которые доступны текущему пользователю
     *
     * @param sc     сессия пользователя
     * @param taskId ID задачи, на уровне которой созданы фильтры (по дереву задач)
     * @return Спсок фильтров
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredFilterBean
     */
    public ArrayList<SecuredFilterBean> getTaskFilterList(SessionContext sc, String taskId) throws GranException {
        log.trace("getTaskFilterList");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getTaskFilterList", "sc", sc);
        if (taskId == null)
            throw new InvalidParameterException(this.getClass(), "getTaskFilterList", "taskId", sc);
        if (!sc.taskOnSight(taskId))
            throw new AccessDeniedException(this.getClass(), "getTaskFilterList", sc, "!(sc.taskOnSight(taskId))", taskId);
        ArrayList<SecuredFilterBean> result = SecuredBeanUtil.toArrayList(sc, KernelManager.getFilter().getTaskFilterList(taskId, sc.getUserId()), SecuredBeanUtil.FILTER);
        Collections.sort(result);
        return result;
    }

    /**
     * Возвращает список фильтров задач для задачи taskId, которые доступны текущему пользователю,
     * включая фильтры, созданные для подзадач
     *
     * @param sc     сессия пользователя
     * @param taskId ID задачи, на уровне которой созданы фильтры (по дереву задач)
     * @return Спсок фильтров
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredFilterBean
     */
    public ArrayList<SecuredFilterBean> getAllTaskFilterList(SessionContext sc, String taskId) throws GranException {
        log.trace("getAllTaskFilterList");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getAllTaskFilterList", "sc", sc);
        if (taskId == null)
            throw new InvalidParameterException(this.getClass(), "getAllTaskFilterList", "taskId", sc);
        if (!sc.taskOnSight(taskId))
            throw new AccessDeniedException(this.getClass(), "getAllTaskFilterList", sc, "!(sc.taskOnSight(taskId))", taskId);
        ArrayList<SecuredFilterBean> result = SecuredBeanUtil.toArrayList(sc, KernelManager.getFilter().getAllTaskFilterList(taskId, sc.getUserId()), SecuredBeanUtil.FILTER);
        Collections.sort(result);
        return result;
    }

    /**
     * Возвращает параметры для фильтра
     *
     * @param sc         сессия пользователя
     * @param filterId   ID фильтра, для которого получаем параметры
     * @param methodName название метода
     * @return параметры для фильтра в виде объекта UserFValue
     * @throws GranException при необходимости
     * @see com.trackstudio.app.filter.UserFValue
     */
    private FValue getFValue(SessionContext sc, String filterId, String methodName) throws GranException {
        log.trace(methodName);
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), methodName, "sc", null);
        if (filterId == null)
            throw new InvalidParameterException(this.getClass(), methodName, "filterId", sc);
        SecuredFilterBean filter = AdapterManager.getInstance().getSecuredFindAdapterManager().findFilterById(sc, filterId);
        if (!filter.canView())
            throw new AccessDeniedException(this.getClass(), methodName, sc, "!filter.canView()", filterId);
        return KernelManager.getFilter().getTaskFValue(filterId);
    }

    /**
     * Возвращает параметры для фильтра задач
     *
     * @param sc       сессия пользователя
     * @param filterId ID фильтра, для которого получаем параметры
     * @return параметры для фильтра в виде объекта UserFValue
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredTaskFValueBean
     */
    public SecuredTaskFValueBean getTaskFValue(SessionContext sc, String filterId) throws GranException {
        return new SecuredTaskFValueBean(getFValue(sc, filterId, "getTaskFValue"), sc);
    }

    /**
     * Возвращает параметры для фильтра пользователя
     *
     * @param sc       сессия пользователя
     * @param filterId ID фильтра, для которого получаем параметры
     * @return параметры для фильтра в виде объекта UserFValue
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredTaskFValueBean
     */
    public SecuredUserFValueBean getUserFValue(SessionContext sc, String filterId) throws GranException {
        return new SecuredUserFValueBean(getFValue(sc, filterId, "getUserFValue"), sc);
    }

    /**
     * Сохраняет параметры фильтра
     *
     * @param sc       сессия пользователя
     * @param filterId Id фильтра, параметры которого сохраняются
     * @param map      Сохраняемые параметры
     * @throws GranException при необходимости
     */
    public void setFValue(SessionContext sc, String filterId, SecuredTaskFValueBean map) throws GranException {
        log.trace("setFValue");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "setFValue", "sc", sc);
        if (filterId == null)
            throw new InvalidParameterException(this.getClass(), "setFValue", "filterId", sc);
        if (map == null)
            throw new InvalidParameterException(this.getClass(), "setFValue", "map", sc);
        SecuredFilterBean filter = AdapterManager.getInstance().getSecuredFindAdapterManager().findFilterById(sc, filterId);
        if (!sc.canAction(Action.manageTaskPrivateFilters, filter.getTaskId()))
            throw new AccessDeniedException(this.getClass(), "setFValue", sc, "!sc.canAction(Action.editTaskFilter, filter.getTaskId())", filterId);
        if (!filter.canManage())
            throw new AccessDeniedException(this.getClass(), "setFValue", sc, "!filter.canUpdate()", filterId);
        KernelManager.getFilter().setFValue(filterId, map.getFValue());
    }

    /**
     * Сохраняет параметры фильтра
     *
     * @param sc       сессия пользователя
     * @param filterId Id фильтра, параметры которого сохраняются
     * @param map      Сохраняемые параметры
     * @throws GranException при необходимости
     */
    public void setFValue(SessionContext sc, String filterId, SecuredUserFValueBean map) throws GranException {
        log.trace("setFValue");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "setFValue", "sc", sc);
        if (filterId == null)
            throw new InvalidParameterException(this.getClass(), "setFValue", "filterId", sc);
        if (map == null)
            throw new InvalidParameterException(this.getClass(), "setFValue", "map", sc);
        SecuredFilterBean filter = AdapterManager.getInstance().getSecuredFindAdapterManager().findFilterById(sc, filterId);
        if (!sc.canAction(Action.manageUserPrivateFilters, filter.getOwnerId()))
            throw new AccessDeniedException(this.getClass(), "setFValue", sc, "!sc.canAction(Action.manageUserPrivateFilters, filter.getOwnerId())", filterId);
        if (!filter.canManage())
            throw new AccessDeniedException(this.getClass(), "setFValue", sc, "!filter.canUpdate()", filterId);
        KernelManager.getFilter().setFValue(filterId, map.getFValue());
    }

    /**
     * Возвращает список подписок для задачи и фильтра
     *
     * @param sc       сессия пользователя
     * @param filterId ID фильтра, для которого получаем подписки
     * @param taskId   ID задачи, для которой получаем подписки
     * @return список подписок
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredSubscriptionBean
     */
    public List<SecuredSubscriptionBean> getSubscriptionList(SessionContext sc, String filterId, String taskId) throws GranException {

        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getSubscriptionList", "sc", sc);
        if (filterId == null)
            throw new InvalidParameterException(this.getClass(), "getSubscriptionId", "filterId", sc);

        if (taskId == null)
            throw new InvalidParameterException(this.getClass(), "getSubscriptionId", "taskId", sc);
        SecuredFilterBean filter = AdapterManager.getInstance().getSecuredFindAdapterManager().findFilterById(sc, filterId);
        if (!(sc.canAction(Action.manageEmailSchedules, taskId) && sc.taskOnSight(taskId) && filter.canView()))
            return new ArrayList<SecuredSubscriptionBean>();
        List<SecuredSubscriptionBean> retList = new ArrayList<SecuredSubscriptionBean>();
        boolean w = lockManager.acquireConnection(SecuredFilterAdapterManager.class.getSimpleName());
        try {
            List<SecuredSubscriptionBean> subList = SecuredBeanUtil.toArrayList(sc, KernelManager.getFilter().getSubscriptionList(filterId, taskId), SecuredBeanUtil.SUBSCRIPTION);
            for (SecuredSubscriptionBean ssb : subList) {
                if (ssb.canView()) retList.add(ssb);
            }
        } finally {
            if (w) lockManager.releaseConnection(SecuredFilterAdapterManager.class.getSimpleName());
        }
        return retList;
    }

    /**
     * Возвращает список всех подписок для фильтра
     *
     * @param sc       сессия пользователя
     * @param filterId ID фильтра, для которого получаем подписки
     * @return список подписок
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredSubscriptionBean
     */
    public List<SecuredSubscriptionBean> getAllSubscriptionList(SessionContext sc, String filterId) throws GranException {
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getAllSubscriptionList", "sc", sc);
        if (filterId == null)
            throw new InvalidParameterException(this.getClass(), "getAllSubscriptionList", "filterId", sc);

        SecuredFilterBean filter = AdapterManager.getInstance().getSecuredFindAdapterManager().findFilterById(sc, filterId);

        if (!filter.canView())
            throw new AccessDeniedException(this.getClass(), "getAllSubscriptionList", sc, "!filter.canView()", filterId);

        List<SecuredSubscriptionBean> retList = new ArrayList<SecuredSubscriptionBean>();
        boolean w = lockManager.acquireConnection(SecuredFilterAdapterManager.class.getSimpleName());
        try {
            List<SecuredSubscriptionBean> subList = SecuredBeanUtil.toArrayList(sc, KernelManager.getFilter().getAllSubscriptionList(filterId), SecuredBeanUtil.SUBSCRIPTION);
            for (SecuredSubscriptionBean ssb : subList) {
                if (ssb.canView()) retList.add(ssb);
            }
        } finally {
            if (w) lockManager.releaseConnection(SecuredFilterAdapterManager.class.getSimpleName());
        }
        return retList;
    }

    /**
     * Удаляет подписку
     *
     * @param sc             сессия пользователя
     * @param subscriptionId ID подписки, которую удаляем
     * @throws GranException при необходимости
     */
    public void unSubscribe(SessionContext sc, String subscriptionId) throws GranException {
        log.trace("unSubscribe");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "unSubscribe", "sc", sc);
        if (subscriptionId == null)
            throw new InvalidParameterException(this.getClass(), "unSubscribe", "subscriptionId", sc);
        SecuredSubscriptionBean sub = AdapterManager.getInstance().getSecuredFindAdapterManager().findSubscriptionById(sc, subscriptionId);
        //if (!(sc.canAction(Action.deleteSubscription, sub.getTaskId()) && (sub.getUserId() != null ? sc.allowedByACL(sub.getUserId()) : sub.getGroup().canUpdate())))
        //    throw new AccessDeniedException(this.getClass(), "unSubscribe", sc);
        if (!(sc.canAction(Action.manageEmailSchedules, sub.getTaskId())))
            throw new AccessDeniedException(this.getClass(), "unSubscribe", sc, "!sc.canAction(Action.manageEmailSchedules, sub.getTaskId())", subscriptionId);
        if (!(sub.getUserId() != null ? sc.allowedByUser(sub.getUserId()) : sub.getGroup().isAllowedByACL()))
            throw new AccessDeniedException(this.getClass(), "unSubscribe", sc, "!(sub.getUserId() != null ? sc.allowedByACL(sub.getUserId()) : sub.getGroup().canUpdate())", subscriptionId);
        SecuredFilterBean filter = sub.getFilter();
        if (filter.isPrivate() && sub.getUserId() != null && !sub.getUserId().equals(sc.getUserId()))
            throw new AccessDeniedException(this.getClass(), "unSubscribe", sc, "filter.isPrivate() && sub.getUserId()!= null && !sub.getUserId().equals(sc.getUserId())", subscriptionId);
        KernelManager.getFilter().unSubscribe(subscriptionId);
    }

    /**
     * Редактирует существующую подписку
     *
     * @param sc             сессия пользователя
     * @param name           Название подписки
     * @param subscriptionId ID редактируемой подписки
     * @param filterId       ID фильтра, для которого получаем подписки
     * @param startDate      Дата/время начала рассылки
     * @param stopDate       Дата/время окончания рассылки
     * @param nextRun        Время следующего запуска
     * @param interval       Истервал рассылки
     * @param templateId     ID шаблона письма
     * @throws GranException при необходимости
     */
    public void updateSubscription(SessionContext sc, String subscriptionId, String name, String filterId, String templateId, long startDate, long stopDate, long nextRun, Integer interval, String userId, String groupId) throws GranException {
        log.trace("updateSubscription");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "updateSubscription", "sc", sc);
        if (name == null)
            throw new InvalidParameterException(this.getClass(), "updateSubscription", "name", sc);
        if (filterId == null)
            throw new InvalidParameterException(this.getClass(), "updateSubscriber", "filterId", sc);
        if (interval == null)
            throw new InvalidParameterException(this.getClass(), "updateSubscription", "interval", sc);
        SecuredSubscriptionBean subscription = AdapterManager.getInstance().getSecuredFindAdapterManager().findSubscriptionById(sc, subscriptionId);
        if (userId != null || groupId != null) {
            subscription.setUserId(userId);
            subscription.setGroupId(groupId);
        }
        SecuredFilterBean filter = AdapterManager.getInstance().getSecuredFindAdapterManager().findFilterById(sc, filterId);
        if (filter.isPrivate() && subscription.getUserId() != null && !subscription.getUserId().equals(sc.getUserId()))
            throw new AccessDeniedException(this.getClass(), "updateSubscription", sc, "filter.isPrivate() && subscription.getUserId() != null && !subscription.getUserId().equals(sc.getUserId())",subscriptionId);
        //if (!(sc.canAction(com.trackstudio.kernel.cache.Action.editSubscription, subscription.getTaskId()) && (subscription.getUserId() != null ? sc.allowedByACL(subscription.getUserId()) : subscription.getGroup().canUpdate()) && filter.canView()))
        //    throw new AccessDeniedException(this.getClass(), "updateSubscription", sc);

        if (!(sc.canAction(Action.manageEmailSchedules, subscription.getTaskId())))
            throw new AccessDeniedException(this.getClass(), "updateSubscription", sc, "!(sc.canAction(Action.manageEmailSchedules, subscription.getTaskId()))", subscriptionId);
        if (!(subscription.getUserId() != null ? sc.allowedByUser(subscription.getUserId()) : subscription.getGroup().isAllowedByACL()))
            throw new AccessDeniedException(this.getClass(), "updateSubscription", sc, "!(subscription.getUserId() != null ? sc.allowedByACL(subscription.getUserId()) : subscription.getGroup().canUpdate())", subscriptionId);
        if (!filter.canView())
            throw new AccessDeniedException(this.getClass(), "updateSubscription", sc, "!filter.canView()", filterId);

        Calendar start = new GregorianCalendar();
        if (startDate == -1L) {
            start = null;
        } else {
            start.setTimeInMillis(startDate);
        }
        Calendar stop = new GregorianCalendar();
        if (stopDate == -1L) {
            stop = null;
        } else {
            stop.setTimeInMillis(stopDate);
        }
        Calendar next = new GregorianCalendar();
        if (nextRun == -1L) {
            next = null;
        } else {
            next.setTimeInMillis(nextRun);
        }
        KernelManager.getFilter().updateSubscription(SafeString.createSafeString(name), subscriptionId, filterId, templateId, start, stop, next, interval, subscription.getUserId(), subscription.getGroupId());
    }

    /**
     * Создает новую подписку
     *
     * @param sc       сессия пользователя
     * @param name     Название подписки
     * @param userId   ID подписанного пользователя
     * @param groupId  ID подписанной группы
     * @param taskId   ID задаче, на уровне которой создается подписка
     * @param filterId ID фильтра, для которого получаем подписки
     * @return ID созданной подписки
     * @throws GranException при необходимости
     */
    public String createSubscription(SessionContext sc, String name, String userId, String groupId, String taskId, String filterId, Integer interval) throws GranException {
        log.trace("createSubscription");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "createSubscription", "sc", sc);
        if (name == null)
            throw new InvalidParameterException(this.getClass(), "createSubscription", "name", sc);
        if (filterId == null)
            throw new InvalidParameterException(this.getClass(), "createSubscription", "filterId", sc);
        if (userId == null && groupId == null)
            throw new InvalidParameterException(this.getClass(), "createSubscription", "userId||groupId", sc);
        if (taskId == null)
            throw new InvalidParameterException(this.getClass(), "createSubscription", "taskId", sc);
        SecuredFilterBean filter = AdapterManager.getInstance().getSecuredFindAdapterManager().findFilterById(sc, filterId);
        //if (!(sc.canAction(com.trackstudio.kernel.cache.Action.createSubscription, taskId) && (userId != null ?sc.allowedByACL(userId) : AdapterManager.getInstance().getSecuredFindAdapterManager().findPrstatusById(sc, groupId).canUpdate()) && filter.canView()))
        //    throw new AccessDeniedException(this.getClass(), "createSubscription", sc);

        if (!sc.canAction(Action.manageEmailSchedules, taskId))
            throw new AccessDeniedException(this.getClass(), "createSubscription", sc, "!sc.canAction(Action.manageEmailSchedules, taskId)", taskId);
        if (!(userId != null ? sc.allowedByUser(userId) : AdapterManager.getInstance().getSecuredFindAdapterManager().findPrstatusById(sc, groupId).isAllowedByACL()))
            throw new AccessDeniedException(this.getClass(), "createSubscription", sc, "!(userId != null ? sc.allowedByUser(userId) : AdapterManager.getInstance().getSecuredFindAdapterManager().findPrstatusById(sc, groupId).isAllowedByACL())", userId);
        if (!filter.canView())
            throw new AccessDeniedException(this.getClass(), "createSubscription", sc, "!filter.canView()", filterId);
        if (filter.isPrivate() && userId != null && !userId.equals(sc.getUserId()))
            throw new AccessDeniedException(this.getClass(), "createSubscription", sc, "filter.isPrivate() && userId != null && !userId.equals(sc.getUserId())", filterId + " * " + userId);
        DateFormatter df = new DateFormatter(sc.getTimezone(), sc.getLocale());
        Calendar startDate = df.getCalendar();
        Calendar stopDate = df.getCalendar();
        stopDate.add(Calendar.YEAR, 10);
        Calendar nextRun = df.getCalendar();
        return KernelManager.getFilter().createSubscription(SafeString.createSafeString(name), userId, groupId, taskId, filterId, startDate, stopDate, nextRun, interval, userId != null ? new SecuredUserBean(userId, sc).getTemplate() : sc.getUser().getTemplate());
    }

    /**
     * Создает фильтр для задач
     *
     * @param sc          сессия пользователя
     * @param name        название фильтра
     * @param description описание фильтра
     * @param priv        указывает приватный фильтр или нет
     * @param taskId      ID задачи на уровне которой создается фильтр
     * @param ownerId     ID пользователя, который создает фильтр
     * @return ID созданного фильтра
     * @throws GranException при необходимости
     */
    public String createTaskFilter(SessionContext sc, String name, String description, boolean priv, String taskId, String ownerId, String preferences) throws GranException {
        log.trace("createTaskFilter");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "createTaskFilter", "sc", sc);
        if (name == null || pv.badSmallDesc(name))
            throw new InvalidParameterException(this.getClass(), "createTaskFilter", "name", sc);
        if (pv.badSmallDesc(description))
            throw new InvalidParameterException(this.getClass(), "createTaskFilter", "description", sc);
        if (taskId == null)
            throw new InvalidParameterException(this.getClass(), "createTaskFilter", "taskId", sc);
        if (ownerId == null)
            throw new InvalidParameterException(this.getClass(), "createTaskFilter", "ownerId", sc);

        if (!sc.taskOnSight(taskId))
            throw new AccessDeniedException(this.getClass(), "createTaskFilter", sc, "!sc.taskOnSight(taskId)", taskId);
        if (priv) {
            if (!sc.canAction(Action.manageTaskPrivateFilters, taskId))
                throw new AccessDeniedException(this.getClass(), "createTaskFilter", sc, "!(sc.canAction(Action.manageTaskPrivateFilters, taskId)", taskId);
        } else {
            if (!(sc.canAction(Action.manageTaskPublicFilters, taskId) && sc.allowedByACL(taskId)))
                throw new AccessDeniedException(this.getClass(), "createTaskFilter", sc, "!(sc.canAction(Action.manageTaskPublicFilters, taskId)", taskId);
        }
        return KernelManager.getFilter().createTaskFilter(SafeString.createSafeString(name), SafeString.createSafeString(description), priv, taskId, ownerId, preferences);
    }

    /**
     * Создает фильтр для полдьзователей
     *
     * @param sc          сессия пользователя
     * @param toUserId    ID пользователя на уровне которого создается фильтр
     * @param name        Название задачи
     * @param description Описание задачи
     * @param priv        указывает приватный фильтр или нет
     * @param preferences показывать на панели или нет
     * @return ID созданного фильтра
     * @throws GranException при необходимости
     */
    public String createUserFilter(SessionContext sc, String toUserId, String name, String description, boolean priv, String preferences) throws GranException {
        log.trace("createUserFilter");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "createUserFilter", "sc", null);
        if (name == null || pv.badSmallDesc(name))
            throw new InvalidParameterException(this.getClass(), "createUserFilter", "name", sc);
        if (description == null || pv.badSmallDesc(description))
            throw new InvalidParameterException(this.getClass(), "createUserFilter", "description", sc);
        if (priv) {
            if (!sc.canAction(Action.manageUserPrivateFilters, toUserId))
                throw new AccessDeniedException(this.getClass(), "createUserFilter", sc, "!sc.canAction(com.trackstudio.kernel.cache.Action.createUserFilter, toUserId)", toUserId);
        } else {
            if (!(sc.canAction(Action.manageUserPublicFilters, toUserId) && sc.allowedByUser(toUserId)))
                throw new AccessDeniedException(this.getClass(), "createUserFilter", sc, "!sc.canAction(Action.createPublicUserFilter, toUserId)", toUserId);
        }
        return KernelManager.getFilter().createUserFilter(SafeString.createSafeString(name), SafeString.createSafeString(description), priv, toUserId, sc.getUserId(), preferences);
    }

    /**
     * Создает копию указанного фильтра для задач (клонирует)
     *
     * @param sc            сессия пользователя
     * @param filterId      ID фильтра, который клонируем
     * @param currentTaskId ID задачи, на уровне которой клонируется фильтр
     * @return ID нового фильтра
     * @throws GranException при необходимости
     */
    public String cloneTaskFilter(SessionContext sc, String filterId, String currentTaskId) throws GranException {
        log.trace("cloneTaskFilter");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "cloneTaskFilter", "sc", sc);
        if (filterId == null)
            throw new InvalidParameterException(this.getClass(), "cloneTaskFilter", "filterId", sc);
        if (currentTaskId == null)
            throw new InvalidParameterException(this.getClass(), "cloneTaskFilter", "currentTaskId", sc);
        SecuredFilterBean filter = AdapterManager.getInstance().getSecuredFindAdapterManager().findFilterById(sc, filterId);
        //if (!(sc.canAction(Action.copyTaskFilter, currentTaskId) && filter.canView()))
        //    throw new AccessDeniedException(this.getClass(), "cloneTaskFilter", sc);
        if (!sc.canAction(Action.manageTaskPrivateFilters, currentTaskId))
            throw new AccessDeniedException(this.getClass(), "cloneTaskFilter", sc, "!sc.canAction(Action.copyTaskFilter, currentTaskId)", currentTaskId);
        if (!filter.canView())
            throw new AccessDeniedException(this.getClass(), "cloneTaskFilter", sc, "!filter.canView()", filterId);
        return KernelManager.getFilter().cloneTaskFilter(filterId, currentTaskId, sc.getLocale(), sc.getUserId());
    }

    /**
     * Создает копию указанного фильтра для пользоватлея (клонирует)
     *
     * @param sc            сессия пользователя
     * @param filterId      ID фильтра, который клонируем
     * @param currentUserId ID пользователя, который клонирует фильтр
     * @return ID нового фильтра
     * @throws GranException при необходимости
     */
    public String cloneUserFilter(SessionContext sc, String currentUserId, String filterId) throws GranException {
        log.trace("cloneUserFilter");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "cloneUserFilter", "sc", null);
        if (filterId == null)
            throw new InvalidParameterException(this.getClass(), "cloneUserFilter", "filterId", sc);
        if (currentUserId == null)
            throw new InvalidParameterException(this.getClass(), "cloneUserFilter", "currentUserId", sc);
        SecuredFilterBean filter = AdapterManager.getInstance().getSecuredFindAdapterManager().findFilterById(sc, filterId);
        //if (!sc.canAction(Action.copyUserFilter, filter.getOwnerId()) && filter.canView())
        //    throw new AccessDeniedException(this.getClass(), "cloneUserFilter", sc);
        if (!sc.canAction(Action.manageUserPrivateFilters, sc.getUserId(currentUserId)))
            throw new AccessDeniedException(this.getClass(), "cloneUserFilter", sc, "!sc.canAction(Action.copyUserFilter, currentUserId)", currentUserId);
        if (!filter.canView())
            throw new AccessDeniedException(this.getClass(), "cloneUserFilter", sc, "!filter.canView()", filterId);
        return KernelManager.getFilter().cloneUserFilter(filterId, sc.getLocale(), sc.getUserId(currentUserId));
    }

    /**
     * Редактирование фильтра для задач
     *
     * @param sc          сессия пользователя
     * @param filterId    ID фильтра
     * @param name        Название фильтра
     * @param description Описание фильтра
     * @param priv        Видимость фильтра (приватный или публичный)
     * @param preferences Настройки фильтра
     * @throws GranException при необходимости
     * @see com.trackstudio.kernel.manager.SafeString
     */
    public void updateTaskFilter(SessionContext sc, String filterId, String name, String description, boolean priv, String preferences) throws GranException {
        log.trace("updateTaskFilter");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "updateTaskFilter", "sc", sc);
        if (filterId == null)
            throw new InvalidParameterException(this.getClass(), "updateTaskFilter", "filterId", sc);
        if (name == null || pv.badSmallDesc(name))
            throw new InvalidParameterException(this.getClass(), "updateTaskFilter", "name", sc);
        if (description == null || pv.badSmallDesc(description))
            throw new InvalidParameterException(this.getClass(), "updateTaskFilter", "description", sc);
        SecuredFilterBean filter = AdapterManager.getInstance().getSecuredFindAdapterManager().findFilterById(sc, filterId);
        if (!filter.isPrivate() && priv)
            throw new InvalidParameterException(this.getClass(), "updateTaskFilter", "priv", sc);
        if (!sc.canAction(Action.manageTaskPrivateFilters, filter.getTaskId()))
            throw new AccessDeniedException(this.getClass(), "updateTaskFilter", sc, "!sc.canAction(com.trackstudio.kernel.cache.Action.editTaskFilter, filter.getTaskId())", filterId);
        if (!filter.canManage())
            throw new AccessDeniedException(this.getClass(), "updateTaskFilter", sc, "!filter.canUpdate()", filterId);
        if (filter.isPrivate() && !priv && !(sc.canAction(Action.manageTaskPublicFilters, filter.getTaskId()) && sc.allowedByACL(filter.getTaskId())))
            throw new AccessDeniedException(this.getClass(), "updateTaskFilter", sc, "(filter.isPrivate()) && (!priv) && !sc.canAction(Action.createPublicTaskFilter, filter.getTaskId())", filterId);

        KernelManager.getFilter().updateTaskFilter(filterId, SafeString.createSafeString(name), SafeString.createSafeString(description), priv, SafeString.createSafeString(preferences));
    }

    /**
     * Редактирование фильтра для задач
     *
     * @param sc          Сессия пользователя
     * @param filterId    ID фильтра
     * @param name        Название фильтра
     * @param description Описание фильтра
     * @param priv        Видимость фильтра (приватный или публичный)
     * @param preferences Настройки фильтра
     * @throws GranException при необходимости
     * @see com.trackstudio.kernel.manager.SafeString
     */
    public void updateUserFilter(SessionContext sc, String filterId, String name, String description, boolean priv, String preferences) throws GranException {
        log.trace("updateUserFilter");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "updateUserFilter", "sc", sc);
        if (filterId == null)
            throw new InvalidParameterException(this.getClass(), "updateUserFilter", "filterId", sc);
        if (name == null || pv.badSmallDesc(name))
            throw new InvalidParameterException(this.getClass(), "updateUserFilter", "name", sc);
        if (description == null || pv.badSmallDesc(description))
            throw new InvalidParameterException(this.getClass(), "updateUserFilter", "description", sc);
        SecuredFilterBean bean = AdapterManager.getInstance().getSecuredFindAdapterManager().findFilterById(sc, filterId);
        /*if (!bean.isPrivate() && priv)
            throw new InvalidParameterException(this.getClass(), "updateUserFilter", "priv", sc);
            */

        //if (!(sc.canAction(Action.editUserFilter, bean.getOwnerId()) && bean.canUpdate()))
        //    throw new AccessDeniedException(this.getClass(), "updateUserFilter", sc);

//        if (!sc.canAction(Action.manageUserPrivateFilters, bean.getOwnerId()))
//            throw new AccessDeniedException(this.getClass(), "updateUserFilter", sc, "!sc.canAction(Action.editUserFilter, bean.getOwnerId())");
        if (!bean.canManage())
            throw new AccessDeniedException(this.getClass(), "updateUserFilter", sc, "!bean.canUpdate()", filterId);
        if (bean.isPrivate() && !priv && !(sc.canAction(Action.manageUserPublicFilters, bean.getOwnerId()) && sc.allowedByUser(bean.getOwnerId())))
            throw new AccessDeniedException(this.getClass(), "updateUserFilter", sc, "(bean.isPrivate()) && (!priv) && !sc.canAction(Action.createPublicUserFilter, bean.getOwnerId())", filterId);

        KernelManager.getFilter().updateTaskFilter(filterId, SafeString.createSafeString(name), SafeString.createSafeString(description), priv, SafeString.createSafeString(preferences));
    }

    /**
     * Удаляет фильтр для задач
     *
     * @param sc       сессия пользователя
     * @param filterId ID фильтра, Который удаляем
     * @throws GranException при необходимости
     */
    public void deleteTaskFilter(SessionContext sc, String filterId) throws GranException {
        log.trace("deleteTaskFilter");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "deleteTaskFilter", "sc", sc);
        if (filterId == null)
            throw new InvalidParameterException(this.getClass(), "deleteTaskFilter", "filterId", sc);
        SecuredFilterBean filter = AdapterManager.getInstance().getSecuredFindAdapterManager().findFilterById(sc, filterId);
        if (filterId.equals("1"))
            throw new AccessDeniedException(this.getClass(), "deleteTaskFilter", sc, "filterId.equals(\"1\")", filterId);
//        if (!((filter.isPrivate() && sc.canAction(Action.manageTaskPrivateFilters, filter.getTaskId())) || sc.canAction(Action.manageTaskPublicFilters, filter.getTaskId())))
//            throw new AccessDeniedException(this.getClass(), "deleteTaskFilter", sc, "!sc.canAction(com.trackstudio.kernel.cache.Action.deleteTaskFilter, filter.getTaskId())");
        if (!filter.canManage())
            throw new AccessDeniedException(this.getClass(), "deleteTaskFilter", sc, "!filter.canUpdate()", filterId);
        KernelManager.getFilter().deleteTaskFilter(filterId);
    }

    /**
     * Удаляет фильтр для пользователей
     *
     * @param sc       сессия ользователя
     * @param filterId ID фильтра, который удаляем
     * @throws GranException при необходимости
     */
    public void deleteUserFilter(SessionContext sc, String filterId) throws GranException {
        log.trace("deleteUserFilter");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "deleteUserFilter", "sc", sc);
        if (filterId == null)
            throw new InvalidParameterException(this.getClass(), "deleteUserFilter", "filterId", sc);
        SecuredFilterBean filter = AdapterManager.getInstance().getSecuredFindAdapterManager().findFilterById(sc, filterId);
        if (filterId.equals("0"))
            throw new AccessDeniedException(this.getClass(), "deleteUserFilter", sc, "filterId.equals(\"0\")", filterId);
//        if (!sc.canAction(Action.manageUserPrivateFilters, filter.getOwnerId()))
//            throw new AccessDeniedException(this.getClass(), "deleteUserFilter", sc, "!sc.canAction(Action.deleteUserFilter, filter.getOwnerId())");
        if (!filter.canManage())
            throw new AccessDeniedException(this.getClass(), "deleteUserFilter", sc, "!filter.canUpdate()", filterId);
        KernelManager.getFilter().deleteUserFilter(filterId);
    }

    /**
     * Возвращает текущий выбранный пользователем фильтр на уровне задачи taskId
     *
     * @param sc     сессия ользователя
     * @param taskId ID Задачи, на уровне которой берется фильтр
     * @return ID текущего фильтра пользователя
     * @throws GranException при необходимости
     */
    public String getCurrentTaskFilterId(SessionContext sc, String taskId) throws GranException {
        log.trace("getCurrentFilterId");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getCurrentFilterId", "sc", null);
        if (taskId == null)
            throw new InvalidParameterException(this.getClass(), "getCurrentFilterId", "taskId", sc);
        if (!sc.taskOnSight(taskId))
            throw new AccessDeniedException(this.getClass(), "getCurrentFilterId", sc, "!sc.taskOnSight(taskId)", taskId);
        String filterId = KernelManager.getFilter().getCurrentTaskFilterId(taskId, sc.getUserId());
        // проверка секурности. Проверка нужна на случай, если у юзера были права на фильтр, а потом их отобрали.
        if (!AdapterManager.getInstance().getSecuredFindAdapterManager().findFilterById(sc, filterId).canView())
            filterId = FilterConstants.DEFAULT_TASK_FILTER_ID;
        return filterId;
    }

    /**
     * Возвращает текущий выбранный пользователем фильтр на уровне пользователя userId
     *
     * @param sc     сессия пользователя
     * @param userId ID Пользователя, на уровле которого берется фильтр
     * @return ID текущего фильтра пользователя
     * @throws GranException при необходимости
     */
    public String getCurrentUserFilterId(SessionContext sc, String userId) throws GranException {
        log.trace("getCurrentUserFilterId");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getCurrentUserFilterId", "sc", null);
        if (userId == null)
            throw new InvalidParameterException(this.getClass(), "getCurrentUserFilterId", "userId", sc);
        if (!sc.userOnSight(userId))
            userId = sc.getUserId();
        String filterId = KernelManager.getFilter().getCurrentUserFilterId(userId, sc.getUserId());
        if (!AdapterManager.getInstance().getSecuredFindAdapterManager().findFilterById(sc, filterId).canView())
            filterId = FilterConstants.DEFAULT_USER_FILTER_ID;
        return filterId;
    }

    /**
     * Устанавливает текущий выбранный фильтр для текущего пользователя или задачи
     *
     * @param sc       сессия пользователя
     * @param taskId   ID задачи, для которой устанавливается текущий выбранный фильтр
     * @param filterId ID фильтра, который устанавливается
     * @return ID установленного фильтра
     * @throws GranException при необходимости
     */
    public String setCurrentFilter(SessionContext sc, String taskId, String filterId) throws GranException {
        log.trace("setCurrentFilter");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "setCurrentFilter", "sc", sc);

        if (taskId == null)
            throw new InvalidParameterException(this.getClass(), "setCurrentFilter", "taskId", sc);
        if (filterId == null)
            throw new InvalidParameterException(this.getClass(), "setCurrentFilter", "filterId", sc);
        SecuredFilterBean filter = AdapterManager.getInstance().getSecuredFindAdapterManager().findFilterById(sc, filterId);
        //if (!(filter.canView() && sc.taskOnSight(taskId)))
        //    throw new AccessDeniedException(this.getClass(), "setCurrentFilter", sc);
        if (!sc.taskOnSight(taskId))
            throw new AccessDeniedException(this.getClass(), "setCurrentFilter", sc, "!sc.taskOnSight(taskId)", taskId);
        if (!filter.canView())
            throw new AccessDeniedException(this.getClass(), "setCurrentFilter", sc, "!filter.canView()", filterId);
        return KernelManager.getFilter().setCurrentFilter(taskId, sc.getUserId(), filterId);
    }

    /**
     * Устанавливает текущий выбранный фильтр для пользователя или задачи
     *
     * @param sc       сессия пользователя
     * @param userId   ID пользователя, для которого устанавливается текущий выбранный фильтр
     * @param filterId ID фильтра, который устанавливается
     * @return ID установленного фильтра
     * @throws GranException при необходимости
     */
    public String setCurrentUserFilter(SessionContext sc, String userId, String filterId) throws GranException {
        log.trace("setCurrentUserFilter");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "setCurrentUserFilter", "sc", sc);

        if (userId == null)
            throw new InvalidParameterException(this.getClass(), "setCurrentFilter", "userId", sc);
        if (filterId == null)
            throw new InvalidParameterException(this.getClass(), "setCurrentFilter", "filterId", sc);
        SecuredFilterBean filter = AdapterManager.getInstance().getSecuredFindAdapterManager().findFilterById(sc, filterId);
        //if (!(filter.canView() && sc.userOnSight(userId)))
        //    throw new AccessDeniedException(this.getClass(), "setCurrentUserFilter", sc);
        if (!sc.userOnSight(userId))
            throw new AccessDeniedException(this.getClass(), "setCurrentUserFilter", sc, "!sc.userOnSight(userId)", userId);
        if (!filter.canView())
            throw new AccessDeniedException(this.getClass(), "setCurrentUserFilter", sc, "!filter.canView()", filterId);
        return KernelManager.getFilter().setCurrentUserFilter(userId, filterId, sc.getUserId());
    }

    /**
     * Возвращает список оповещений для определенного фильтра, которые могу рассылаться для указанной задачи.
     *
     * @param sc       сессия пользователя
     * @param filterId ID фильтра
     * @param taskId   текущая задача
     * @return список уведомлений
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredNotificationBean
     */
    public List<SecuredNotificationBean> getNotificationList(SessionContext sc, String filterId, String taskId) throws GranException {
        log.trace("getNotificationList");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getNotificationList", "sc", null);
        if (filterId == null)
            throw new InvalidParameterException(this.getClass(), "getNotificationList", "filterId", sc);
        if (taskId == null)
            throw new InvalidParameterException(this.getClass(), "getNotificationList", "taskId", sc);
        SecuredFilterBean filter = AdapterManager.getInstance().getSecuredFindAdapterManager().findFilterById(sc, filterId);
        //if (!(sc.canAction(Action.viewNotification, taskId) && sc.taskOnSight(taskId) && filter.canView()))
        //    throw new AccessDeniedException(this.getClass(), "getNotificationList", sc);
        if (!sc.taskOnSight(taskId))
            throw new AccessDeniedException(this.getClass(), "getNotificationList", sc, "!sc.taskOnSight(taskId)", taskId);
        if (!filter.canView())
            throw new AccessDeniedException(this.getClass(), "getNotificationList", sc, "!filter.canView()", filterId);
        if (!sc.canAction(Action.manageEmailSchedules, taskId))
            throw new AccessDeniedException(this.getClass(), "getNotificationList", sc, "!sc.canAction(Action.manageEmailSchedules, taskId)", taskId);
        List<SecuredNotificationBean> retList = new ArrayList<SecuredNotificationBean>();
        boolean w = lockManager.acquireConnection(SecuredFilterAdapterManager.class.getSimpleName());
        try {
            List<SecuredNotificationBean> notList = SecuredBeanUtil.toArrayList(sc, KernelManager.getFilter().getNotificationList(filterId, taskId), SecuredBeanUtil.NOTIFICATION);
            for (SecuredNotificationBean snb : notList) {
                if (snb.canView())
                    retList.add(snb);
            }
        } finally {
            if (w) lockManager.releaseConnection(SecuredFilterAdapterManager.class.getSimpleName());
        }
        return retList;
    }

    /**
     * Возвращает список оповещений, созданных для данного фильтра
     *
     * @param sc       сессия пользователя
     * @param filterId ID фильтра, для которого беруться уведомления
     * @return список уведомлений
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredNotificationBean
     */
    public List<SecuredNotificationBean> getAllNotificationList(SessionContext sc, String filterId) throws GranException {
        log.trace("getAllNotificationList");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getAllNotificationList", "sc", null);
        if (filterId == null)
            throw new InvalidParameterException(this.getClass(), "getAllNotificationList", "filterId", sc);

        List<SecuredNotificationBean> retList = new ArrayList<SecuredNotificationBean>();
        boolean w = lockManager.acquireConnection(SecuredFilterAdapterManager.class.getSimpleName());
        try {
            SecuredFilterBean filter = AdapterManager.getInstance().getSecuredFindAdapterManager().findFilterById(sc, filterId);

            if (!filter.canView())
                throw new AccessDeniedException(this.getClass(), "getAllNotificationList", sc, "!filter.canView()", filterId);

            List<SecuredNotificationBean> notList = SecuredBeanUtil.toArrayList(sc, KernelManager.getFilter().getAllNotificationList(filterId), SecuredBeanUtil.NOTIFICATION);
            for (SecuredNotificationBean snb : notList) {
                if (snb.canView())
                    retList.add(snb);
            }
        } finally {
            if (w) lockManager.releaseConnection(SecuredFilterAdapterManager.class.getSimpleName());
        }
        return retList;
    }

    /**
     * Создает уведомление о событиях
     *
     * @param sc       сессия пользователя
     * @param name     Название уведомления
     * @param filterId ID фильтра, с использованием которого фильтруются задачи для рассылки уведомлений
     * @param userId   ID пользователя, для которого создается уведомление
     * @param groupId  ID группы, для которой создается уведомление
     * @param taskId   ID хадачи, на уровне которой создается уведомление
     * @return ID созданного уведомления
     * @throws GranException при необходимости
     */
    public String setNotification(SessionContext sc, String name, String filterId, String userId, String groupId, String taskId) throws GranException {
        log.trace("setNotification");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "setNotification", "sc", sc);
        if (name == null)
            throw new InvalidParameterException(this.getClass(), "setNotification", "name", sc);
        if (filterId == null)
            throw new InvalidParameterException(this.getClass(), "setNotification", "filterId", sc);
        if (userId == null && groupId == null)
            throw new InvalidParameterException(this.getClass(), "setNotification", "userId||groupId", sc);
        if (taskId == null)
            throw new InvalidParameterException(this.getClass(), "setNotification", "taskId", sc);
        SecuredFilterBean filter = AdapterManager.getInstance().getSecuredFindAdapterManager().findFilterById(sc, filterId);
        //if (!(sc.canAction(Action.createNotification, taskId) && (userId != null ?sc.allowedByACL(userId) : AdapterManager.getInstance().getSecuredFindAdapterManager().findPrstatusById(sc, groupId).canUpdate()) && filter.canView()))
        //    throw new AccessDeniedException(this.getClass(), "setNotification", sc);
        if (!(sc.canAction(Action.manageEmailSchedules, taskId)))
            throw new AccessDeniedException(this.getClass(), "setNotification", sc, "!(sc.canAction(Action.manageEmailSchedules, taskId))", taskId);
        if (!(userId != null ? sc.allowedByUser(userId) : AdapterManager.getInstance().getSecuredFindAdapterManager().findPrstatusById(sc, groupId).isAllowedByACL()))
            throw new AccessDeniedException(this.getClass(), "setNotification", sc, "!(userId != null ?sc.allowedByACL(userId) : AdapterManager.getInstance().getSecuredFindAdapterManager().findPrstatusById(sc, groupId).canUpdate())", userId + " * " + groupId );
        if (!filter.canView())
            throw new AccessDeniedException(this.getClass(), "setNotification", sc, "!filter.canView()", filterId);
        if (filter.isPrivate() && userId != null && !userId.equals(sc.getUserId()))
            throw new AccessDeniedException(this.getClass(), "setNotification", sc, "filter.isPrivate() && userId != null && !userId.equals(sc.getUserId())", filterId + " * " + userId);
        return KernelManager.getFilter().setNotification(SafeString.createSafeString(name), filterId, userId, groupId, taskId, userId != null ? new SecuredUserBean(userId, sc).getTemplate() : sc.getUser().getTemplate());
    }

    /**
     * Произодится редактирование уведомления о событиях
     *
     * @param sc             сессия пользователя
     * @param notificationId ID редактируемого уведомления
     * @param name           Название уведомления
     * @param template       ID шаблона письма
     * @param filter         ID фильтра, на основании которого фильтруются задачи для рассылки уведомлений
     * @param condition      Перечень события, на которые идет реакция
     * @param groupId        group id
     * @param userId         user id
     * @throws GranException при необходимости
     */
    public void updateNotification(SessionContext sc, String notificationId, String filter, String name, String template, String condition, String groupId, String userId) throws GranException {
        log.trace("updateNotification");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "updateNotification", "sc", sc);
        if (notificationId == null)
            throw new InvalidParameterException(this.getClass(), "updateNotification", "notificationId", sc);
        if (filter == null)
            throw new InvalidParameterException(this.getClass(), "updateNotification", "filterId", sc);
        if (name == null)
            throw new InvalidParameterException(this.getClass(), "updateNotification", "name", sc);
        SecuredNotificationBean notification = AdapterManager.getInstance().getSecuredFindAdapterManager().findNotificationById(sc, notificationId);
        if (userId != null || groupId != null) {
            notification.setUserId(userId);
            notification.setGroupId(groupId);
        }
        if (!(sc.canAction(Action.manageEmailSchedules, notification.getTaskId()) && notification.canManage()))
            throw new AccessDeniedException(this.getClass(), "updateNotification", sc, "!(sc.canAction(Action.manageEmailSchedules, notification.getTaskId()) && notification.canManage())", notificationId);
        if (!(notification.getUserId() != null ? sc.allowedByUser(notification.getUserId()) : notification.getGroup().isAllowedByACL()))
            throw new AccessDeniedException(this.getClass(), "updateNotification", sc, "!(notification.getUserId() != null ? sc.allowedByACL(notification.getUserId()) : notification.getGroup().canUpdate())", notificationId);
        SecuredFilterBean f = AdapterManager.getInstance().getSecuredFindAdapterManager().findFilterById(sc, filter);
        if (f.isPrivate() && notification.getUserId() != null && !notification.getUserId().equals(sc.getUserId()))
            throw new AccessDeniedException(this.getClass(), "updateNotification", sc, "notification.getFilter().isPrivate() && notification.getUserId() != null && !notification.getUserId().equals(sc.getUserId())", notificationId);
        if (!notification.canView())
            throw new AccessDeniedException(this.getClass(), "updateNotification", sc, "!notification.canView()", notificationId);
        if (!f.canView())
            throw new AccessDeniedException(this.getClass(), "updateNotification", sc, "filter.canView()", filter);

        KernelManager.getFilter().updateNotification(notificationId, SafeString.createSafeString(name), SafeString.createSafeString(template), filter, condition, notification.getUserId(), notification.getGroupId());
    }

    /**
     * Удаляет уведомление о событиях
     *
     * @param sc             сессия пользователя
     * @param notificationid ID удаляемого уведомления
     * @throws GranException при необходимости
     */
    public void unsetNotification(SessionContext sc, String notificationid) throws GranException {
        log.trace("deleteNotification");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "deleteNotification", "sc", sc);
        if (notificationid == null)
            throw new InvalidParameterException(this.getClass(), "deleteNotification", "notificationid", sc);
        SecuredNotificationBean notification = AdapterManager.getInstance().getSecuredFindAdapterManager().findNotificationById(sc, notificationid);
        //if (!(sc.canAction(Action.deleteNotification, notification.getTaskId()) && (notification.getUserId() != null ? sc.allowedByACL(notification.getUserId()) : notification.getGroup().canUpdate()) && notification.canView()))
        //    throw new AccessDeniedException(this.getClass(), "updateNotification", sc);

        if (!(sc.canAction(Action.manageEmailSchedules, notification.getTaskId()) && notification.canManage()))
            throw new AccessDeniedException(this.getClass(), "updateNotification", sc, "!sc.canAction(Action.deleteNotification, notification.getTaskId())", notificationid);
        if (!(notification.getUserId() != null ? sc.allowedByUser(notification.getUserId()) : notification.getGroup().isAllowedByACL()))
            throw new AccessDeniedException(this.getClass(), "updateNotification", sc, "!(notification.getUserId() != null ? sc.allowedByACL(notification.getUserId()) : notification.getGroup().canUpdate())", notificationid);
        if (!notification.canView())
            throw new AccessDeniedException(this.getClass(), "updateNotification", sc, "!notification.canView()", notificationid);
        KernelManager.getFilter().deleteNotification(notificationid);
    }

    /**
     * Возвращает список фильтров пользоватлелей для пользователя ownerId, которые доступны пользователю currentUserId
     *
     * @param sc     сессия пользователя
     * @param userId ID пользователя, для которого достаются фильтры
     * @return Спсок фильтров
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredFilterBean
     */
    public ArrayList<SecuredFilterBean> getUserFilterList(SessionContext sc, String userId) throws GranException {
        log.trace("getUserFilterList");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getUserFilterList", "sc", sc);
        if (userId == null)
            throw new InvalidParameterException(this.getClass(), "getTaskFilterList", "userId", sc);
        if (!sc.userOnSight(userId))
            throw new AccessDeniedException(this.getClass(), "getUserFilterList", sc, "!sc.userOnSight(userId)", userId);
        ArrayList<SecuredFilterBean> result = SecuredBeanUtil.toArrayList(sc, KernelManager.getFilter().getUserFilterList(userId, sc.getUserId()), SecuredBeanUtil.FILTER);
        ArrayList<SecuredFilterBean> t = new ArrayList<SecuredFilterBean>();
        for (SecuredFilterBean bean : result) {
            if (bean.canView() && !t.contains(bean)) t.add(bean);
        }
        Collections.sort(t);
        return t;
    }

    /**
     * Возвращает список фильтров пользоватлелей для пользователя ownerId, которые доступны пользователю currentUserId,
     * включая фильтры, созданные для дочерних пользовталей
     *
     * @param sc     сессия пользователя
     * @param userId ID пользователя, для которого достаются фильтры
     * @return Спсок фильтров
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredFilterBean
     */
    public ArrayList<SecuredFilterBean> getAllUserFilterList(SessionContext sc, String userId) throws GranException {
        log.trace("getUserFilterList");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getUserFilterList", "sc", sc);
        if (userId == null)
            throw new InvalidParameterException(this.getClass(), "getTaskFilterList", "userId", sc);
        if (!sc.userOnSight(userId))
            throw new AccessDeniedException(this.getClass(), "getUserFilterList", sc, "!sc.userOnSight(userId)", userId);
        ArrayList<SecuredFilterBean> result = SecuredBeanUtil.toArrayList(sc, KernelManager.getFilter().getAllUserFilterList(userId, sc.getUserId(userId)), SecuredBeanUtil.FILTER);
        ArrayList<SecuredFilterBean> t = new ArrayList<SecuredFilterBean>();
        for (SecuredFilterBean bean : result) {
            if (bean.canView() && !t.contains(bean)) t.add(bean);
        }
        Collections.sort(t);
        return t;
    }
}
