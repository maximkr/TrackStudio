package com.trackstudio.securedkernel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.AccessDeniedException;
import com.trackstudio.exception.CantFindObjectException;
import com.trackstudio.exception.GranException;
import com.trackstudio.exception.InvalidParameterException;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.cache.TaskRelatedInfo;
import com.trackstudio.kernel.cache.TaskRelatedManager;
import com.trackstudio.kernel.cache.UserRelatedManager;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.secured.SecuredPrstatusBean;
import com.trackstudio.secured.SecuredTaskAclBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUserAclBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.SecuredBeanUtil;

import net.jcip.annotations.Immutable;

/**
 * Класс AclManager содержит методы для работы с правилами доступа (Access Control Level - ACL).<br/>
 * ACL определяют к каким задачам и пользователям может иметь доступ авторизованный пользователь.<br/>
 * Правила доступа могут быть назначены для задач, для пользователей и для статусов.
 */
@Immutable
public class SecuredAclAdapterManager {

    private static final Log log = LogFactory.getLog(SecuredAclAdapterManager.class);
    private static final LockManager lockManager = LockManager.getInstance();

    /**
     * Возвращает список правил доступа для указанной задачи
     *
     * @param sc     сессия пользователя
     * @param taskId ID задачи, для которой возвращаем правила
     * @return список правил доступа для задач
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredTaskAclBean
     */
    public ArrayList<SecuredTaskAclBean> getTaskAclList(SessionContext sc, String taskId) throws GranException {
        boolean w = lockManager.acquireConnection(SecuredAclAdapterManager.class.getSimpleName());
        try {
            log.trace("getTaskAclList");
            if (sc == null)
                throw new InvalidParameterException(this.getClass(), "getTaskAclList", "sessionId", null);
            if (taskId == null)
                throw new InvalidParameterException(this.getClass(), "getTaskAclList", "taskId", sc);

            if (!sc.canAction(Action.manageTaskACLs, taskId)) {
                SecuredTaskBean t = new SecuredTaskBean(taskId, sc);
                throw new AccessDeniedException(this.getClass(), "getTaskAclList", sc, I18n.getString(sc, "Action.manageTaskACLs"), "#" + t.getNumber(), taskId);
            }
            if (!sc.allowedByACL(taskId)) {
                SecuredTaskBean t = new SecuredTaskBean(taskId, sc);
                throw new AccessDeniedException(this.getClass(), "getTaskAclList", sc, I18n.getString(sc, "MSG_NOT_ALLOWED_BY_ACL"), "#" + t.getNumber(), taskId);
            }
            ArrayList<SecuredTaskAclBean> ret = new ArrayList<SecuredTaskAclBean>();
            ArrayList<String> aclids = new ArrayList<String>();
            for (TaskRelatedInfo it : TaskRelatedManager.getInstance().getTaskRelatedInfoChain(null, taskId))
                aclids.addAll(TaskRelatedManager.getInstance().getAclList(it.getId()));
            for (String aid : aclids) {
                SecuredTaskAclBean b = AdapterManager.getInstance().getSecuredFindAdapterManager().findTaskAclById(sc, aid);
                if (b.getUser() == null || sc.allowedByUser(b.getUser().getId()))
                    ret.add(b);
            }
            return ret;
        } finally {
            if (w) lockManager.releaseConnection(SecuredAclAdapterManager.class.getSimpleName());
        }
    }

    /**
     * Возвращает список всех ACL на пути taskId
     *
     * @param sc     сессия пользователя
     * @param taskId ID задачи
     * @return список правил доступа
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredTaskAclBean
     */
    public ArrayList<SecuredTaskAclBean> getAllTaskAclList(SessionContext sc, String taskId) throws GranException {
        boolean w = lockManager.acquireConnection(SecuredAclAdapterManager.class.getSimpleName());
        try {
            log.trace("getAclList");
            if (sc == null)
                throw new InvalidParameterException(this.getClass(), "getAclList", "sessionId", null);
            if (taskId == null)
                throw new InvalidParameterException(this.getClass(), "getAclList", "taskId", sc);
            if (!sc.canAction(Action.manageTaskACLs, taskId)) {
                SecuredTaskBean t = new SecuredTaskBean(taskId, sc);
                throw new AccessDeniedException(this.getClass(), "getAllTaskAclList", sc, I18n.getString(sc, "Action.manageTaskACLs"), "#" + t.getNumber(), taskId);
            }
            if (!sc.allowedByACL(taskId)) {
                SecuredTaskBean t = new SecuredTaskBean(taskId, sc);
                throw new AccessDeniedException(this.getClass(), "getAllTaskAclList", sc, I18n.getString(sc, "MSG_NOT_ALLOWED_BY_ACL"), "#" + t.getNumber(), taskId);
            }
            return SecuredBeanUtil.toArrayList(sc, KernelManager.getAcl().getAllTaskAclList(taskId), SecuredBeanUtil.ACL);
        } finally {
            if (w) lockManager.releaseConnection(SecuredAclAdapterManager.class.getSimpleName());
        }

    }

    /**
     * Возвращает список правил доступа для указанного пользователя
     *
     * @param sc     сессия пользователя
     * @param userId ID пользователя
     * @return список правил доступа для пользователя
     * @throws GranException при необходимости
     */
    public ArrayList<SecuredUserAclBean> getUserAclList(SessionContext sc, String userId) throws GranException {
        boolean w = lockManager.acquireConnection(SecuredAclAdapterManager.class.getSimpleName());
        try {
            log.trace("getUserAclList");
            if (sc == null)
                throw new InvalidParameterException(this.getClass(), "getUserAclList", "sessionId", null);
            if (userId == null)
                throw new InvalidParameterException(this.getClass(), "getUserAclList", "taskId", sc);
            if (!sc.canAction(Action.manageUserACLs, userId)) {
                SecuredUserBean t = new SecuredUserBean(userId, sc);
                throw new AccessDeniedException(this.getClass(), "getUserAclList", sc, I18n.getString(sc, "Action.manageUserACLs"), t.getLogin(), userId);
            }

            if (!sc.allowedByUser(userId)) {
                SecuredUserBean t = new SecuredUserBean(userId, sc);
                throw new AccessDeniedException(this.getClass(), "getUserAclList", sc, I18n.getString(sc, "MSG_NOT_ALLOWED_BY_USER_ACL"), t.getLogin(), userId);
            }
            ArrayList<SecuredUserAclBean> ret = new ArrayList<SecuredUserAclBean>();
            ArrayList<String> aclids = new ArrayList<String>();
            for (String it : UserRelatedManager.getInstance().getUserIdChain(null, userId))
                aclids.addAll(UserRelatedManager.getInstance().getAclList(it));
            for (String aid : aclids) {
                SecuredUserAclBean b = AdapterManager.getInstance().getSecuredFindAdapterManager().findUserAclById(sc, aid);
                if (b.getUser() == null || sc.allowedByUser(b.getUser().getId()))
                    ret.add(b);
            }
            return ret;
        } finally {
            if (w) lockManager.releaseConnection(SecuredAclAdapterManager.class.getSimpleName());
        }
    }

    /**
     * Возвращает список всех ACL на пути userId
     *
     * @param sc     сессия пользователя
     * @param userId ID задачи
     * @return список правил доступа
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredUserAclBean
     */
    public ArrayList<SecuredUserAclBean> getAllUserAclList(SessionContext sc, String userId) throws GranException {
        boolean w = lockManager.acquireConnection(SecuredAclAdapterManager.class.getSimpleName());
        try {
            log.trace("getAclList");
            if (sc == null)
                throw new InvalidParameterException(this.getClass(), "getAclList", "sessionId", null);
            if (userId == null)
                throw new InvalidParameterException(this.getClass(), "getAclList", "userId", sc);

            if (!sc.canAction(Action.manageUserACLs, userId)) {
                SecuredUserBean t = new SecuredUserBean(userId, sc);
                throw new AccessDeniedException(this.getClass(), "getAllUserAclList", sc, I18n.getString(sc, "Action.manageUserACLs"), t.getLogin(), userId);
            }
//        if (!sc.allowedByACL(userId)){
//            SecuredUserBean t = new SecuredUserBean(userId, sc);
//            throw new AccessDeniedException(this.getClass(), "getAllUserAclList", sc, I18n.getString(sc, "MSG_NOT_ALLOWED_BY_USER_ACL"), t.getLogin());
//        }
            return SecuredBeanUtil.toArrayList(sc, KernelManager.getAcl().getAllUserAclList(userId), SecuredBeanUtil.ACL);
        } finally {
            if (w) lockManager.releaseConnection(SecuredAclAdapterManager.class.getSimpleName());
        }

    }

    /**
     * Удаляет правило доступа для задачи по его ID
     *
     * @param sc    сессия пользователя
     * @param aclId ID правила доступа
     * @throws GranException при необходимости
     */
    public void deleteTaskAcl(SessionContext sc, String aclId) throws GranException {
        boolean w = lockManager.acquireConnection(SecuredFindAdapterManager.class.getSimpleName());
        try {
            log.trace("deleteAcl");
            if (sc == null)
                throw new InvalidParameterException(this.getClass().getName(), "deleteAcl", "sessionId", null);
            if (aclId == null)
                throw new InvalidParameterException(this.getClass().getName(), "deleteAcl", "aclId", sc.getUserId());
            SecuredTaskAclBean bean = AdapterManager.getInstance().getSecuredFindAdapterManager().findTaskAclById(sc, aclId);
            if (!bean.canManage()) {
                throw new AccessDeniedException(this.getClass(), "deleteTaskAcl", sc, I18n.getString(sc, "Action.manageTaskACLs"), "#" + bean.getTask().getNumber(), aclId);
            }
            KernelManager.getAcl().deleteAcl(aclId);
        } finally {
            if (w) lockManager.releaseConnection(SecuredFindAdapterManager.class.getSimpleName());
        }
    }

    /**
     * Удаляет правило доступа для пользователя по его ID
     *
     * @param sc    сессия пользователя
     * @param aclId ID правила доступа
     * @throws GranException при необходимости
     */
    public void deleteUserAcl(SessionContext sc, String aclId) throws GranException {
        log.trace("deleteAcl");
        if (sc == null)
            throw new InvalidParameterException(this.getClass().getName(), "deleteAcl", "sessionId", null);
        if (aclId == null)
            throw new InvalidParameterException(this.getClass().getName(), "deleteAcl", "aclId", sc.getUserId());
        SecuredUserAclBean bean = AdapterManager.getInstance().getSecuredFindAdapterManager().findUserAclById(sc, aclId);
        if (!bean.canManage()) {
            throw new AccessDeniedException(this.getClass(), "deleteUserAcl", sc, I18n.getString(sc, "Action.manageTaskACLs"), bean.getUser().getLogin(), aclId);
        }
        KernelManager.getAcl().deleteAcl(aclId);
    }

    /**
     * Создает новое правило доступа для задачи, пользователя или статуса
     *
     * @param sc       сессия пользователя
     * @param taskId   ID задачи для которой создается правило доступа
     * @param toUserId ID пользователя, для которого создается правило доступа
     * @param userId   ID пользователя, который создает правило доступа
     * @param groupId  ID статуса, для которого создается правило доступа
     * @return ID созданного правила доступа
     * @throws GranException при необходимости
     */
    public String createAcl(SessionContext sc, String taskId, String toUserId, String userId, String groupId) throws GranException {
        log.trace("createAcl");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "createAcl", "sessionId", null);
        if (taskId == null && toUserId == null)
            throw new InvalidParameterException(this.getClass(), "createAcl", "taskId||toUserId", sc);
        if (userId == null && groupId == null)
            throw new InvalidParameterException(this.getClass(), "createAcl", "userId||prstatusId", sc);
//        if (!((!sc.getUserId().equals(userId) &&
//                sc.allowedByACL(userId))) || (taskId !=null && !(sc.canAction(com.trackstudio.kernel.cache.Action.createAccessControl, taskId) && sc.allowedByACL(taskId)))
//        ||(taskId ==null && !(/*sc.canAction(com.trackstudio.kernel.cache.Action.createAccessControl, taskId) && */sc.allowedByACL(toUserId))))
//            throw new AccessDeniedException(this.getClass().getName(), "createAcl", sc.getUserId());

        if (!(!sc.getUserId().equals(userId) && sc.allowedByUser(userId)))
            throw new AccessDeniedException(this.getClass(), "createAcl", sc, "!((!sc.getUserId().equals(userId) && sc.allowedByACL(userId)))", userId);
        if (taskId != null && !(sc.canAction(Action.manageTaskACLs, taskId) && sc.allowedByACL(taskId)))
            throw new AccessDeniedException(this.getClass(), "createAcl", sc, "taskId !=null && !(sc.canAction(com.trackstudio.kernel.cache.Action.createAccessControl, taskId) && sc.allowedByACL(taskId))", taskId);
        if (taskId == null && (!sc.allowedByUser(toUserId) || sc.getUserId().equals(toUserId)))
            throw new AccessDeniedException(this.getClass(), "createAcl", sc, "taskId ==null && !(sc.allowedByACL(toUserId))", toUserId);

        return KernelManager.getAcl().createAcl(taskId, toUserId, userId, groupId, sc.getUserId(toUserId));
    }

    /**
     * Редактирует правило доступа для задачи
     *
     * @param sc         сессия пользователя
     * @param aclId      ID правила доступа
     * @param prstatusId ID статуса
     * @param override   Нужно ли переопределять статуса. TRUE - нужноz, FALSE - нет
     * @throws GranException при необходимости
     */
    public void updateTaskAcl(SessionContext sc, String aclId, String prstatusId, boolean override) throws GranException {
        boolean w = lockManager.acquireConnection(SecuredAclAdapterManager.class.getSimpleName());
        try {
            log.trace("updateTaskAcl");
            if (sc == null)
                throw new InvalidParameterException(this.getClass(), "updateTaskAcl", "sessionId", null);
            if (aclId == null)
                throw new InvalidParameterException(this.getClass(), "updateTaskAcl", "aclId", sc);
            if (prstatusId == null)
                throw new InvalidParameterException(this.getClass(), "updateTaskAcl", "prstatusId", sc);
            SecuredTaskAclBean acl = AdapterManager.getInstance().getSecuredFindAdapterManager().findTaskAclById(sc, aclId);
            SecuredPrstatusBean prstatus = AdapterManager.getInstance().getSecuredFindAdapterManager().findPrstatusById(sc, prstatusId);
            if (!(acl.canManage() && prstatus.canView()))
                throw new AccessDeniedException(this.getClass(), "updateTaskAcl", sc, "!(acl.canUpdate() && prstatus.canView())", prstatusId + " * " + aclId);

            KernelManager.getAcl().updateAcl(aclId, prstatusId, override);
        } finally {
            if (w) lockManager.releaseConnection(SecuredAclAdapterManager.class.getSimpleName());
        }
    }

    /**
     * Редактирует правило доступа для пользователя
     *
     * @param sc         сессия пользователя
     * @param aclId      ID правила доступа
     * @param prstatusId ID статуса
     * @param override   Нужно ли переопределять статуса. TRUE - нужно, FALSE - нет
     * @throws GranException при необходимости
     */
    public void updateUserAcl(SessionContext sc, String aclId, String prstatusId, boolean override) throws GranException {
        log.trace("updateUserAcl");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "updateUserAcl", "sessionId", null);
        if (aclId == null)
            throw new InvalidParameterException(this.getClass(), "updateUserAcl", "aclId", sc);
        if (prstatusId == null)
            throw new InvalidParameterException(this.getClass(), "updateUserAcl", "prstatusId", sc);
        SecuredUserAclBean acl = AdapterManager.getInstance().getSecuredFindAdapterManager().findUserAclById(sc, aclId);
        SecuredPrstatusBean prstatus = AdapterManager.getInstance().getSecuredFindAdapterManager().findPrstatusById(sc, prstatusId);
        if (!(acl.canManage() && prstatus.canView()))
            throw new AccessDeniedException(this.getClass(), "updateUserAcl", sc, "!(acl.canManage() && prstatus.canView())", aclId + " * " + prstatusId);
        KernelManager.getAcl().updateAcl(aclId, prstatusId, override);
    }

    /**
     * Для заданного пользователя и статуса, начиная с заданной задачи и вверх
     * достаем наборы ACL, привязанные к задаче (assigned ACL) через этого
     * пользователя, т.е. не через prstatus. Из этих ACL достаем prstatusы,
     * добавляем к ним собственный статус пользователя.
     *
     * @param sc     сессия пользователя
     * @param taskId ID задачи
     * @param userId ID пользователя
     * @return список статусов
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredPrstatusBean
     */
    public ArrayList<SecuredPrstatusBean> getAllowedPrstatusList(SessionContext sc, String taskId, String userId) throws GranException {
        log.trace("getAllowedPrstatusList");
        if (sc == null)
            throw new InvalidParameterException(this.getClass().getName(), "getAllowedPrstatusList", "sessionId", null);
        if (taskId == null)
            throw new InvalidParameterException(this.getClass(), "getAllowedPrstatusList", "taskId", sc);
        if (userId == null)
            throw new InvalidParameterException(this.getClass(), "getAllowedPrstatusList", "userId", sc);
        if (!sc.taskOnSight(taskId))
            throw new AccessDeniedException(this.getClass(), "getAllowedPrstatusList", sc, "!(sc.taskOnSight(taskId))", taskId);
        return new ArrayList<SecuredPrstatusBean>(new TreeSet<SecuredPrstatusBean>(SecuredBeanUtil.toArrayList(sc, KernelManager.getAcl().getAllowedPrstatusList(taskId, userId), SecuredBeanUtil.PRSTATUS)));
    }

    /**
     * Возвращает список статусов, для которых есть доступ у текущего юзера посредством правил доступа
     *
     * @param sc       сессия пользователя
     * @param toUserId ID пользователя, для котрого создается правило доступа
     * @param userId   ID пользователя
     * @return array список статусов
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredPrstatusBean
     */
    public ArrayList<SecuredPrstatusBean> getUserAllowedPrstatusList(SessionContext sc, String toUserId, String userId) throws GranException {
        log.trace("getUserAllowedPrstatusList");
        if (sc == null)
            throw new InvalidParameterException(this.getClass().getName(), "getUserAllowedPrstatusList", "sessionId", null);
        if (toUserId == null)
            throw new InvalidParameterException(this.getClass(), "getUserAllowedPrstatusList", "toUserId", sc);
        if (userId == null)
            throw new InvalidParameterException(this.getClass(), "getUserAllowedPrstatusList", "userId", sc);
        if (!sc.userOnSight(toUserId))
            throw new AccessDeniedException(this.getClass(), "getUserAllowedPrstatusList", sc, "!(sc.userOnSight(toUserId))", toUserId);
        return new ArrayList<SecuredPrstatusBean>(new TreeSet<SecuredPrstatusBean>(SecuredBeanUtil.toArrayList(sc, KernelManager.getAcl().getUserAllowedPrstatusList(toUserId, userId), SecuredBeanUtil.PRSTATUS)));
    }

    /**
     * Возвращает список ответственных для фильтра
     *
     * @param sc     сессия пользователя
     * @param taskId ID задачи
     * @return список пользователей
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredUserBean
     */
    public ArrayList<SecuredUserBean> getHandlerForFilter(SessionContext sc, String taskId) throws GranException {
        log.trace("getHandlerForFilter");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getHandlerForFilter", "sc", null);
        if (taskId == null)
            throw new InvalidParameterException(this.getClass(), "getHandlerForFilter", "taskId", null);
        return new ArrayList<SecuredUserBean>(new TreeSet<SecuredUserBean>(SecuredBeanUtil.toArrayListWithoutCanView(sc, KernelManager.getAcl().getHandlerForFilter(sc.getUserId(), taskId))));
    }

    /**
     * Возвращает список ответственных статусов для фильтра
     *
     * @param sc     сессия пользователя
     * @param taskId ID задачи
     * @return список пользователей
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredPrstatusBean
     */
    public ArrayList<SecuredPrstatusBean> getHandlerStatusesForFilter(SessionContext sc, String taskId) throws GranException {
        log.trace("getHandlerStatusesForFilter");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getHandlerStatusesForFilter", "sc", null);
        if (taskId == null)
            throw new InvalidParameterException(this.getClass(), "getHandlerStatusesForFilter", "taskId", null);
        return new ArrayList<SecuredPrstatusBean>(new TreeSet<SecuredPrstatusBean>(SecuredBeanUtil.toArrayList(sc, KernelManager.getAcl().getHandlerStatusesForFilter(sc.getUserId(), taskId), SecuredBeanUtil.PRSTATUS)));
    }

    /**
     * Возвращает список эффективных статусов для пользователя
     *
     * @param sc     сессия пользователя
     * @param userId ID пользователя
     * @return список пользователей
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredUserBean
     */
    public ArrayList<SecuredUserBean> getUserEffectiveStatusesList(SessionContext sc, String userId) throws GranException {
        log.trace("getUserList");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getUserEffectiveStatusesList", "sessionId", null);
        if (userId == null)
            throw new InvalidParameterException(this.getClass(), "getUserEffectiveStatusesList", "userId", sc);
        if (!sc.userOnSight(userId))
            throw new AccessDeniedException(this.getClass(), "getUserEffectiveStatusesList", sc, "!sc.userOnSight(userId)", userId);
        ArrayList<SecuredUserBean> ret = new ArrayList<SecuredUserBean>();
        for (String id : UserRelatedManager.getInstance().getAllowedUsers(userId))
            ret.add(new SecuredUserBean(id, sc));
        return ret;
    }

    /**
     * Возвращает список доступных пользователей для задачи
     *
     * @param sc     сессия пользователя
     * @param taskId ID задачи
     * @return список пользователей
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredUserBean
     */
    public ArrayList<SecuredUserBean> getUserList(SessionContext sc, String taskId) throws GranException {
        boolean lock = lockManager.acquireConnection(SecuredAclAdapterManager.class.getName());
        try {
            log.trace("getUserList");
            if (sc == null)
                throw new InvalidParameterException(this.getClass(), "getUserList", "sessionId", null);
            if (taskId == null)
                throw new InvalidParameterException(this.getClass(), "getUserList", "taskId", sc);
            if (!sc.taskOnSight(taskId))
                throw new AccessDeniedException(this.getClass(), "getViewableUserList", sc, "!sc.taskOnSight(taskId)", taskId);
            ArrayList<SecuredUserBean> ret = new ArrayList<SecuredUserBean>();
            for (String id : TaskRelatedManager.getInstance().getAllowedUsers(taskId))
                ret.add(new SecuredUserBean(id, sc));
            return ret;
        } finally {
            if (lock) lockManager.releaseConnection(SecuredAclAdapterManager.class.getName());
        }
    }

    public HashMap<SecuredUserBean, Boolean> getEffectiveList(SessionContext sc, String taskId) throws GranException {
        log.trace("getUserList");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getUserList", "sessionId", null);
        if (taskId == null)
            throw new InvalidParameterException(this.getClass(), "getUserList", "taskId", sc);
        if (!sc.taskOnSight(taskId))
            throw new AccessDeniedException(this.getClass(), "getViewableUserList", sc, "!sc.taskOnSight(taskId)", taskId);
        HashMap<SecuredUserBean, Boolean> ret = new  HashMap<SecuredUserBean, Boolean>();
        for (Map.Entry<String, Boolean> entry : TaskRelatedManager.getInstance().getEffectiveUsers(taskId).entrySet())
            ret.put(new SecuredUserBean(entry.getKey(), sc), entry.getValue());
        return ret;
    }

    /**
     * Возвращает список доступных задач для пользователя
     *
     * @param sc     сессия пользователя
     * @param userId ID пользователя
     * @return список задач
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredTaskBean
     */
    public ArrayList<SecuredTaskBean> getTaskList(SessionContext sc, String userId) throws GranException {
        log.trace("getTaskList");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getTaskList", "sessionId", null);
        if (userId == null)
            throw new InvalidParameterException(this.getClass(), "getTaskList", "userId", sc);
        return SecuredBeanUtil.toArrayList(sc, KernelManager.getAcl().getTaskList(userId), SecuredBeanUtil.TASK);
    }

    /**
     * Возвращает список назначенных пользователей для указанного
     *
     * @param sc     сессия пользователя
     * @param userId ID пользователя
     * @return список пользователей
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredUserBean
     */
    public ArrayList<SecuredUserBean> getAssignedUserList(SessionContext sc, String userId) throws GranException {
        log.trace("getTaskList");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getAssignedUserList", "sessionId", null);
        if (userId == null)
            throw new InvalidParameterException(this.getClass(), "getAssignedUserList", "userId", sc);
        ArrayList<SecuredUserBean> ret = new ArrayList<SecuredUserBean>();
        for (String userId1 : KernelManager.getAcl().getAssignedUserList(userId)) {
            ret.add(new SecuredUserBean(userId1, sc));
        }
        return ret;
    }

    /**
     * Возвращает список пользовательских правил доступа для указанного статуса
     *
     * @param sc         сессия пользователя
     * @param prstatusId ID статуса
     * @return список правил доступа
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredUserAclBean
     */
    public ArrayList<SecuredUserAclBean> getGroupUserAclList(SessionContext sc, String prstatusId) throws GranException {
        boolean w = lockManager.acquireConnection(SecuredAclAdapterManager.class.getSimpleName());
        try {
            log.trace("getGroupTaskList");
            if (sc == null)
                throw new InvalidParameterException(this.getClass(), "getGroupAclList", "sessionId", null);
            if (prstatusId == null)
                throw new InvalidParameterException(this.getClass(), "getGroupAclList", "prstatusId", sc);
            ArrayList<SecuredUserAclBean> res = new ArrayList<SecuredUserAclBean>();
            for (Object o : SecuredBeanUtil.toArrayList(sc, KernelManager.getAcl().getGroupUserAclList(prstatusId), SecuredBeanUtil.ACL)) {
                SecuredUserAclBean sab = (SecuredUserAclBean) o;
                if (sab.canView())
                    res.add(sab);
            }
            return res;
        } finally {
            if (w) lockManager.releaseConnection(SecuredAclAdapterManager.class.getSimpleName());
        }
    }

    /**
     * Возвращает список правил доступа для задач для указанного статуса
     *
     * @param sc         сессия пользователя
     * @param prstatusId ID статуса
     * @return список правил доступа
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredTaskAclBean
     */
    public ArrayList<SecuredTaskAclBean> getGroupTaskAclList(SessionContext sc, String prstatusId) throws GranException {
        boolean w = lockManager.acquireConnection(SecuredAclAdapterManager.class.getSimpleName());
        try {
            log.trace("getGroupTaskList");
            if (sc == null)
                throw new InvalidParameterException(this.getClass(), "getGroupAclList", "sessionId", null);
            if (prstatusId == null)
                throw new InvalidParameterException(this.getClass(), "getGroupAclList", "prstatusId", sc);
            ArrayList<SecuredTaskAclBean> res = new ArrayList<SecuredTaskAclBean>();
            for (Object o : SecuredBeanUtil.toArrayList(sc, KernelManager.getAcl().getGroupTaskAclList(prstatusId), SecuredBeanUtil.ACL)) {
                SecuredTaskAclBean sab = (SecuredTaskAclBean) o;
                if (sab.canView())
                    res.add(sab);
            }
            return res;
        } finally {
            if (w) lockManager.releaseConnection(SecuredAclAdapterManager.class.getSimpleName());
        }
    }

    /**
     * Вставляет правила доступа в указанную задачу. В операциях copy/paste
     *
     * @param sc        сессия пользователя
     * @param taskId    ID задачи
     * @param aclIds    список ID правил доступа
     * @param operation тип операции, CUT или COPY
     * @throws GranException при необходимости
     */
    public void pasteAcls(SessionContext sc, String taskId, String[] aclIds, String operation) throws GranException {
        log.trace("pasteAcls");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "pasteAcls", "sc", sc);
        if (taskId == null)
            throw new InvalidParameterException(this.getClass(), "pasteAcls", "taskId", sc);
        if (aclIds == null)
            throw new InvalidParameterException(this.getClass(), "pasteAcls", "aclIds", sc);
        if (operation == null)
            throw new InvalidParameterException(this.getClass(), "pasteAcls", "operation", sc);
        if (!sc.canAction(Action.manageTaskACLs, taskId))
            throw new AccessDeniedException(this.getClass(), "pasteAcls", sc, "!sc.canAction(Action.manageTaskACLs, taskId)", taskId);
        for (String id : aclIds) {
            try {
                SecuredTaskAclBean bean = AdapterManager.getInstance().getSecuredFindAdapterManager().findTaskAclById(sc, id);
                if (!bean.canManage())
                    throw new AccessDeniedException(this.getClass(), "pasteAcls", sc, "!bean.canUpdate()", id);
            } catch (CantFindObjectException cfo) {
                log.debug("acl empty : " + cfo.getMessage());
                /* just skip because acl can be deleted when user wants to past it
                    to do bad approach to use try/catch for handle behavior.
                * */
                return;
            }
        }
        KernelManager.getAcl().pasteAcls(sc.getUserId(), taskId, aclIds, operation);
    }

    /**
     * Возвращает список доступных статусов для указанной задачи
     *
     * @param sc     сессия пользователя
     * @param taskId ID задачи
     * @return список ID статусов
     * @throws GranException при необходимости
     */
    public ArrayList<String> getAllowedGroups(SessionContext sc, String taskId) throws GranException {
        log.trace("getAllowedGroups");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getAllowedGroups", "sessionId", null);
        if (taskId == null)
            throw new InvalidParameterException(this.getClass(), "getAllowedGroups", "taskId", sc);
        if (!sc.taskOnSight(taskId))
            throw new AccessDeniedException(this.getClass(), "getAllowedGroups", sc, "!sc.taskOnSight(taskId)", taskId);
        return new ArrayList<String>(TaskRelatedManager.getInstance().getAllowedGroups(taskId));
    }

    public List<String> getAclUserList(SessionContext sc, String userId) throws GranException {
        log.trace("getAclUserList");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getAclUserList", "sessionId", null);
        if (userId == null)
            throw new InvalidParameterException(this.getClass(), "getAclUserList", "userId", sc);

        if (!sc.canAction(Action.manageUserACLs, userId)) {
            SecuredUserBean t = new SecuredUserBean(userId, sc);
            throw new AccessDeniedException(this.getClass(), "getAllUserAclList", sc, I18n.getString(sc, "Action.manageUserACLs"), t.getLogin(), userId);
        }
        return KernelManager.getAcl().getAclOwnerList(userId);

    }
}

