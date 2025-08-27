/*
 * @(#)AclManager.java
 * Copyright 2009 TrackStudio, Inc. All rights reserved.
 */
package com.trackstudio.kernel.manager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.constants.CommonConstants;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.TaskRelatedInfo;
import com.trackstudio.kernel.cache.TaskRelatedManager;
import com.trackstudio.kernel.cache.UserRelatedInfo;
import com.trackstudio.kernel.cache.UserRelatedManager;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.model.Acl;
import com.trackstudio.model.Prstatus;
import com.trackstudio.model.Usersource;
import com.trackstudio.startup.Config;
import com.trackstudio.tools.Null;

import net.jcip.annotations.Immutable;

/**
 * Класс AclManager содержит методы для работы с правилами доступа (Access Control Level - ACL).<br/>
 * ACL определяют к каким задачам и пользователям может иметь доступ авторизованный пользователь.<br/>
 * Правила доступа могут быть назначены для задач, для пользователей и для статусов.
 */
@Immutable
public class AclManager extends KernelManager {

    private static final Log log = LogFactory.getLog(AclManager.class);
    private static final String className = "AclManager.";
    private static final AclManager instance = new AclManager();
    private static final LockManager lockManager = LockManager.getInstance();
    /**
     * Конструктор по умолчанию
     */
    private AclManager() {
    }

    /**
     * Возвращает экземпляр текущего класса
     *
     * @return Экземпляр AclManager
     */
    protected static AclManager getInstance() {
        return instance;
    }

    /**
     * Производится редактирование ACL c aclId, выставляется указаный статус и переопределение.<br/>
     * После этого производится обновление соответствующего кеша.
     *
     * @param aclId      Редактируемый объект ACL
     * @param prstatusId Устанавливаемый статус
     * @param override   Есть ли переопределение
     * @throws GranException при необходимости
     */
    public void updateAcl(String aclId, String prstatusId, boolean override) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            Acl acl = KernelManager.getFind().findAcl(aclId);
            acl.setPrstatus(prstatusId);
            acl.setOverride(override ? 1 : 0);
            hu.updateObject(acl);
            String userId = null, userGroupId = null;
            if (acl.getUsersource() != null) {
                Usersource usersource = KernelManager.getFind().findUsersource(acl.getUsersource().getId());
                if (usersource.getUser() != null)
                    userId = usersource.getUser().getId();
                if (usersource.getPrstatus() != null)
                    userGroupId = usersource.getPrstatus().getId();
            }
            hu.cleanSession();
            if (acl.getTask() != null)
                TaskRelatedManager.getInstance().invalidateAcl(acl.getTask().getId(), userId, userGroupId);
            else
                UserRelatedManager.getInstance().invalidateAcl(acl.getToUser().getId(), userId, userGroupId);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Производится удаление ACL c aclId.<br/>
     * После этого производится обновление соответствующего кеша.
     *
     * @param aclId Удаляемый объект ACL
     * @throws GranException при необходимости
     */
    public void deleteAcl(String aclId) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            Acl acl = KernelManager.getFind().findAcl(aclId);
            hu.deleteObject(Acl.class, aclId);
            if (acl.getTask() != null) {
                String userId = null;
                String groupId = null;
                String taskId = acl.getTask().getId();
                if (acl.getUsersource().getUser() != null)
                    userId = acl.getUsersource().getUser().getId();
                else
                    groupId = acl.getUsersource().getPrstatus().getId();
                TaskRelatedManager.getInstance().invalidateAcl(taskId, userId, groupId);
            } else {
                String toUserId = acl.getToUser().getId();
                String userId = null;
                String groupId = null;
                if (acl.getUsersource().getUser() != null)
                    userId = acl.getUsersource().getUser().getId();
                else
                    groupId = acl.getUsersource().getPrstatus().getId();
                UserRelatedManager.getInstance().invalidateAcl(toUserId, userId, groupId);
            }
        } finally {
            if (w) lockManager.releaseConnection(className);
        }

    }

    /**
     * Возвращает список пользователей, которые имеют доступ к указанной задаче и ее подзадачам
     *
     * @param userId Пользователь
     * @param taskId Задача, для которой возвращается список пользователей
     * @return Список UserRelatedInfo
     * @throws GranException при необходимости
     * @see com.trackstudio.kernel.cache.UserRelatedInfo
     */
    public ArrayList<UserRelatedInfo> getHandlerForFilter(String userId, String taskId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            Set<String> used = new HashSet<String>();
            if (TaskRelatedManager.getInstance().hasAccess(userId, taskId, UserRelatedManager.getInstance().find(userId).getPrstatusId())) {
                used.addAll(TaskRelatedManager.getInstance().getUsedUsersIdList(taskId));
                used.addAll(TaskRelatedManager.getInstance().getAllowedUsers(taskId));
            } else {
                List<TaskRelatedInfo> l = getTaskList(userId);
                for (TaskRelatedInfo t : l) {
                    if (TaskRelatedManager.getInstance().hasPath(t.getId(), taskId)) {
                        used.addAll(TaskRelatedManager.getInstance().getUsedUsersIdList(t.getId()));
                        used.addAll(TaskRelatedManager.getInstance().getAllowedUsers(t.getId()));
                    }
                }
            }
            // initialize
            ArrayList<UserRelatedInfo> itemCollection = new ArrayList<UserRelatedInfo>();
            itemCollection.addAll(UserRelatedManager.getInstance().getItemCollection(used));
            return itemCollection;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает список статусов пользователей, которые имеют доступ к указанной задаче и ее подзадачам
     *
     * @param userId Пользователь
     * @param taskId Задача, для которой возвращается список статусов
     * @return Список Prstatus
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Prstatus
     */
    public ArrayList<Prstatus> getHandlerStatusesForFilter(String userId, String taskId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            Set<String> used = new HashSet<String>();
            if (TaskRelatedManager.getInstance().hasAccess(userId, taskId, UserRelatedManager.getInstance().find(userId).getPrstatusId())) {
                used.addAll(TaskRelatedManager.getInstance().getUsedStatusesIdList(taskId));
                used.addAll(TaskRelatedManager.getInstance().getAllowedUsersStatuses(taskId));
            } else {
                List<TaskRelatedInfo> l = getTaskList(userId);
                for (TaskRelatedInfo t : l) {
                    if (TaskRelatedManager.getInstance().hasPath(t.getId(), taskId)) {
                        used.addAll(TaskRelatedManager.getInstance().getUsedStatusesIdList(t.getId()));
                        used.addAll(TaskRelatedManager.getInstance().getAllowedUsersStatuses(t.getId()));
                    }
                }
            }
            // initialize
            ArrayList<Prstatus> statuses = new ArrayList<Prstatus>();
            for (String status : used) {
                statuses.add(KernelManager.getFind().findPrstatus(status));
            }
            return statuses;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает список ACL для указанного пользователя
     *
     * @param userId Пользователь
     * @return Список Acl
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Acl
     */
    protected List<Acl> getAclUserList(String userId) throws GranException {
        return hu.getList("from com.trackstudio.model.Acl acl where acl.usersource.user=?", userId);
    }

    /**
     * Возвращает список ACL для указанного пользователя и его статуса
     *
     * @param userId Пользователь
     * @return Список Acl
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Acl
     */
    protected List<Acl> getAclForUserAndPrstatusList(String userId) throws GranException {
        return hu.getList("from com.trackstudio.model.Acl acl, com.trackstudio.model.User user where (acl.usersource.user=user.id or acl.usersource.prstatus=user.prstatus) and user.id=?", userId);
    }

    /**
     * Производит удаление ACL для указанного статуса
     *
     * @param prstatusId Статус
     * @throws GranException при необходимости
     */
    public void deleteAclByPrstatus(String prstatusId) throws GranException {
        for (Iterator it = hu.getList("select acl.id from com.trackstudio.model.Acl acl where acl.prstatus=?", prstatusId).iterator(); it.hasNext();)
            deleteAcl(it.next().toString());

        for (Iterator it = hu.getList("select acl.id from com.trackstudio.model.Acl acl where acl.usersource.prstatus is not null and acl.usersource.prstatus=?", prstatusId).iterator(); it.hasNext();)
            deleteAcl(it.next().toString());
    }

    /**
     * Возвращает список задач, для которых есть ACL для указанного пользователя и его статуса
     *
     * @param userId Пользователь
     * @return Список TaskRelatedInfo
     * @throws GranException при необходимости
     * @see com.trackstudio.kernel.cache.TaskRelatedInfo
     */
    public ArrayList<TaskRelatedInfo> getTaskList(String userId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            Set<String> result = new HashSet<String>();
            result.addAll(hu.getList("select acl.task.id from com.trackstudio.model.Acl acl where acl.usersource.user=? and acl.task is not null", userId));
            String prstatusId = UserRelatedManager.getInstance().find(userId).getPrstatusId();
            for (Object o : hu.getList("select acl.owner.id, acl.task.id from com.trackstudio.model.Acl acl where acl.usersource.prstatus is not null and acl.usersource.prstatus=? and acl.task is not null", prstatusId)) {
                Object[] acl = (Object[]) o;
                if (KernelManager.getUser().isParentOf(acl[0].toString(), userId)) {
                    result.add(acl[1].toString());
                }
            }
            ArrayList<TaskRelatedInfo> initResult = new ArrayList<TaskRelatedInfo>();
            for (String taskId : result) {
                initResult.add(TaskRelatedManager.getInstance().find(taskId));
            }
            return initResult;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * This method check all prstatus for user id and special prstatus id.
     * @param userId user id
     * @param prstatusId defualt prstatus
     * @param groupId to be checked prstatus
     * @return boolean
     * @throws GranException for necessery
     */
    public boolean isExistPrstatus(String userId, String prstatusId, String groupId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("select acl.id from com.trackstudio.model.Acl acl where ");
            sb.append("((acl.usersource.user=? and acl.task is not null) or ");
            sb.append("(acl.usersource.prstatus is not null and acl.usersource.prstatus=? and acl.task is not null)) and ");
            sb.append("acl.prstatus.id=?");
            return !hu.getList(sb.toString(), userId, prstatusId, groupId).isEmpty();
        } catch (Exception e) {
            throw new GranException(e);
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает список пользовательских ACL для указанного статуса
     *
     * @param prstatusId Статус
     * @return Список Acl
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Acl
     */
    public ArrayList<Acl> getGroupUserAclList(String prstatusId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            Set<Acl> result = new HashSet<Acl>();
            result.addAll(hu.getList("from com.trackstudio.model.Acl acl where acl.usersource.prstatus=? and acl.task is null", prstatusId));
            return new ArrayList<Acl>(result);
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает список ACL для указанного статуса
     *
     * @param prstatusId Статус
     * @return Список Acl
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Acl
     */
    public ArrayList<Acl> getAclForOverridePrstatusList(String prstatusId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            Set<Acl> result = new HashSet<Acl>();
            result.addAll(hu.getList("from com.trackstudio.model.Acl acl where acl.prstatus=?", prstatusId));
            return new ArrayList<Acl>(result);
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает список тасковых ACL для указанной задачи
     *
     * @param taskId Задача
     * @return Список Acl
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Acl
     */
    public ArrayList<Acl> getAllTaskAclList(String taskId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            int totalAclSeeAlso = Null.getIntegerOrDefaultValue(Config.getProperty("trackstudio.total.acl.in.see.also"), 0);
            ArrayList<Acl> result = new ArrayList<Acl>();
            result.addAll(hu.getList("select acl from com.trackstudio.model.Acl acl where acl.task.id=?", taskId));
            List<Acl> seeAlso = new ArrayList<Acl>();
            boolean addInList = true;
            List<Acl> list = hu.getList("select acl from com.trackstudio.model.Acl acl where acl.task is not null and acl.task.id!=?", taskId);
            for (Acl acl : list) {
                if (TaskRelatedManager.getInstance().hasPath(taskId, acl.getTask().getId())) {
                    if (totalAclSeeAlso > 0) {
                        if (totalAclSeeAlso > (seeAlso.size() + result.size())) {
                            seeAlso.add(acl);
                        } else {
                            addInList = false;
                            break;
                        }

                    } else {
                        seeAlso.add(acl);
                    }
                }
            }

            if (addInList) {
                result.addAll(seeAlso);
            }
            return result;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }

    }

    /**
     * Возвращает список пользовательских ACL для указанного пользоватлея
     *
     * @param userId Пользователь
     * @return Список Acl
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Acl
     */
    public ArrayList<Acl> getAllUserAclList(String userId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            ArrayList<Acl> result = new ArrayList<Acl>();
            List<Acl> list = hu.getList("from com.trackstudio.model.Acl acl where acl.toUser is not null");
            for (Acl acl : list) {
                if (UserRelatedManager.getInstance().hasPath(userId, acl.getToUser().getId())) result.add(acl);
            }
            return result;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }

    }

    /**
     * Возвращает список тасковых ACL для указанного статуса
     *
     * @param prstatusId Статус
     * @return Список Acl
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Acl
     */
    public ArrayList<Acl> getGroupTaskAclList(String prstatusId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            return new ArrayList<Acl>(hu.getList("select acl from com.trackstudio.model.Acl acl where acl.usersource.prstatus=? and acl.task is not null order by acl.task.name", prstatusId));
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает список ID пользователей, для которых есть ACL для указанного пользователя
     *
     * @param userId ПОльзователь
     * @return Список IDs
     * @throws GranException при необходимости
     */
    public ArrayList<String> getAssignedUserList(String userId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            Set<String> result = new HashSet<String>();
            result.addAll(hu.getList("select acl.toUser.id from com.trackstudio.model.Acl acl where acl.usersource.user is not null and acl.usersource.user=? and acl.toUser is not null", userId));
            String prstatusId = UserRelatedManager.getInstance().find(userId).getPrstatusId();
            List<Acl> groupAclList = hu.getList("from com.trackstudio.model.Acl acl where acl.usersource.prstatus is not null and  acl.usersource.prstatus=? and acl.toUser is not null", prstatusId);
            for (Acl acl : groupAclList) {
                String ownerId = acl.getOwner().getId();
                if (KernelManager.getUser().isParentOf(ownerId, userId))
                    result.add(acl.getToUser().getId());
            }
            return new ArrayList<String>(result);
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает список доступных пользователей для указанной задачи
     *
     * @param taskId Задача
     * @return Список UserRelatedInfo
     * @throws GranException при необходимости
     * @see com.trackstudio.kernel.cache.UserRelatedInfo
     */
    public ArrayList<UserRelatedInfo> getAllowedUserList(String taskId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            TreeSet<String> users = TaskRelatedManager.getInstance().getAllowedUsers(taskId);
            ArrayList<UserRelatedInfo> itemCollection = new ArrayList<UserRelatedInfo>();
            itemCollection.addAll(UserRelatedManager.getInstance().getItemCollection(users));
            return itemCollection;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Производится создание ACL c aclId, выставляется указаный статус и переопределение.<br/>
     * После этого производится обновление соответствующего кеша.
     *
     * @param taskId   Задача, для которой создается ACL - относится к асл для таксков
     * @param toUserId Пользователь, для которого создается ACL - относится к асл для пользователей
     * @param userId   Пользователь, которому назначается ACL
     * @param groupId  Статус, назначаемый пользователю (при переопределении статуса)
     * @param ownerId  Пользователь, создающий ACL (автор)
     * @return Список IDs
     * @throws GranException при необходимости
     */
    public String createAcl(String taskId, String toUserId, String userId, String groupId, String ownerId) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            String usersourceId = KernelManager.getUser().getUsersource(userId, groupId);
            String aclId = hu.createObject(new Acl(taskId, toUserId, usersourceId,
                    userId != null ? UserRelatedManager.getInstance().find(userId).getPrstatusId() : groupId, ownerId));
            hu.cleanSession();
            if (taskId != null)
                TaskRelatedManager.getInstance().invalidateAcl(taskId, userId, groupId);
            else
                UserRelatedManager.getInstance().invalidateAcl(toUserId, userId, groupId);
            return aclId;
        } catch (Exception e) {
            throw new GranException(e);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает список ID задач для которых есть ACL для указанного пользователя
     *
     * @param userId ПОльзователь
     * @return Список IDs
     * @throws GranException при необходимости
     */
    public List<String> getDirectAccessList(String userId) throws GranException {
        return hu.getList("select p.task.id from com.trackstudio.model.Acl p where p.usersource.user = ? and p.task is not null", userId);
    }

    /**
     * Возвращает список ID пользователей для которых есть ACL для указанного пользователя
     *
     * @param userId ПОльзователь
     * @return Список IDs
     * @throws GranException при необходимости
     */
    public List<String> getDirectAccessUserList(String userId) throws GranException {
        return hu.getList("select p.toUser.id from com.trackstudio.model.Acl p where p.usersource.user = ? and p.toUser is not null", userId);
    }

    /**
     * Для заданного пользователя и статуса, начиная с заданной задачи и вверх
     * достаем наборы ACL, привязанные к задаче (assigned ACL) через этого
     * пользователя, т.е. не через prstatus. Из этих ACL достаем prstatusы,
     * добавляем к ним собственный статус пользователя.
     *
     * @param taskId Задача
     * @param userId Пользователь
     * @return Список Prstatus
     * @throws GranException если нужно
     * @see com.trackstudio.model.Prstatus
     */
    public ArrayList<Prstatus> getAllowedPrstatusList(String taskId, String userId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            TreeSet<String> acl = TaskRelatedManager.getInstance().getAllowedPrstatuses(userId, taskId);
            Set<Prstatus> ret = new HashSet<Prstatus>();
            for (String anAcl : acl)
                ret.add(KernelManager.getFind().findPrstatus(anAcl));
            return new ArrayList<Prstatus>(ret);
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Для заданного пользователя и статуса, начиная с заданной задачи и вверх
     * достаем наборы ACL, привязанные к задаче (assigned ACL) через этого
     * пользователя, т.е. не через prstatus. Из этих ACL достаем prstatusы,
     * добавляем к ним собственный статус пользователя.
     *
     * @param taskId Задача
     * @param userId Пользователь
     * @return Список IDs
     * @throws GranException если нужно
     */
    public ArrayList<String> getEffectiveStatuses(String taskId, String userId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            TreeSet<String> effectiveStatuses = TaskRelatedManager.getInstance().getAllowedPrstatuses(userId, taskId);
            return new ArrayList<String>(effectiveStatuses);
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Для заданного пользователя и статуса, начиная с заданного пользователя и вверх
     * достаем наборы ACL, привязанные к пользователю (assigned ACL) через этого
     * пользователя, т.е. не через prstatus. Из этих ACL достаем prstatusы,
     * добавляем к ним собственный статус пользователя.
     *
     * @param toUserId  Пользователь
     * @param forUserId Пользователь
     * @return Список Prstatus
     * @throws GranException если нужно
     * @see com.trackstudio.model.Prstatus
     */
    public ArrayList<Prstatus> getUserAllowedPrstatusList(String toUserId, String forUserId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            TreeSet<String> acl = UserRelatedManager.getInstance().getAllowedPrstatuses(toUserId, forUserId);
            Set<Prstatus> ret = new HashSet<Prstatus>();
            for (String i : acl)
                ret.add(KernelManager.getFind().findPrstatus(i));
            return new ArrayList<Prstatus>(ret);
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Для заданного пользователя и статуса, начиная с заданного пользователя и вверх
     * достаем наборы ACL, привязанные к пользователю (assigned ACL) через этого
     * пользователя, т.е. не через prstatus. Из этих ACL достаем prstatusы,
     * добавляем к ним собственный статус пользователя.
     *
     * @param toUserId  Пользователь
     * @param forUserId Пользователь
     * @return Список IDs
     * @throws GranException если нужно
     */
    public ArrayList<String> getUserEffectiveStatuses(String toUserId, String forUserId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            TreeSet<String> acl = UserRelatedManager.getInstance().getAllowedPrstatuses(toUserId, forUserId);
            return new ArrayList<String>(acl);
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Производится копирование или перемещение выбранных ACL в зависимости от указанной операции
     *
     * @param userId    Пользователь
     * @param taskId    Задача
     * @param aclIds    Список вставляемых ACL
     * @param operation Операция: копирование или перемещение
     * @throws GranException если нужно
     * @see com.trackstudio.constants.CommonConstants
     */
    public void pasteAcls(String userId, String taskId, String[] aclIds, String operation) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            for (String aclId : aclIds) {
                Acl acl = KernelManager.getFind().findAcl(aclId);
                Acl newAcl = new Acl(taskId, null, acl.getUsersource().getId(), acl.getPrstatus() != null ? acl.getPrstatus().getId() : null, userId);
                newAcl.setOverride(acl.getOverride());
                String aTaskId = acl.getTask().getId();
                hu.createObject(newAcl);
                String aUserId = null;
                String aGroupId = null;
                if (acl.getUsersource().getUser() != null)
                    aUserId = acl.getUsersource().getUser().getId();
                else
                    aGroupId = acl.getUsersource().getPrstatus().getId();
                if (operation.equals(CommonConstants.CUT)) {
                    hu.deleteObject(Acl.class, acl.getId());
                    TaskRelatedManager.getInstance().invalidateAcl(aTaskId, aUserId, aGroupId);
                }
                TaskRelatedManager.getInstance().invalidateAcl(taskId, aUserId, aGroupId);
            }
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    public List<String> getAclOwnerList(String userId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            return hu.getList("from com.trackstudio.model.Acl acl where acl.owner=?", userId);
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }
}