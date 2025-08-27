/*
 * @(#)StepManager.java
 * Copyright 2009 TrackStudio, Inc. All rights reserved.
 */
package com.trackstudio.kernel.manager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.Preferences;
import com.trackstudio.constants.WorkflowConstants;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.MessageCacheItem;
import com.trackstudio.kernel.cache.MprstatusCacheManager;
import com.trackstudio.kernel.cache.TaskRelatedInfo;
import com.trackstudio.kernel.cache.TaskRelatedManager;
import com.trackstudio.kernel.cache.UserRelatedInfo;
import com.trackstudio.kernel.cache.UserRelatedManager;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.model.Category;
import com.trackstudio.model.Mstatus;
import com.trackstudio.model.Prstatus;
import com.trackstudio.model.Resolution;
import com.trackstudio.model.Status;
import com.trackstudio.model.Task;
import com.trackstudio.model.User;
import com.trackstudio.model.Usersource;

import net.jcip.annotations.Immutable;

/**
 * Класс StepManager содержит методы для работы с типами сообщений
 */
@Immutable
public class StepManager extends KernelManager {

    private static final String className = "StepManager.";
    private static final StepManager instance = new StepManager();
    private static final Log log = LogFactory.getLog(StepManager.class);

    private static final int VIEW = 0;
    private static final int PROCESS = 1;
    private static final int BE_HANDLER = 3;
    private static final LockManager lockManager = LockManager.getInstance();
    /**
     * Конструктор по умолчанию
     */
    private StepManager() {
    }

    /**
     * Возвращает экземпляр текущего класса
     *
     * @return Экземпляр StepManager
     */

    protected static StepManager getInstance() {
        return instance;
    }

    /**
     * Возвраает список типов сообщений для задачи
     *
     * @param taskId ID задачи, для которой получаем типы сообщений
     * @return список типов сообщений
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Mstatus
     */
    private List<Mstatus> getMstatuses(String taskId) throws GranException {
        return hu.getList("select m from com.trackstudio.model.Mstatus m, com.trackstudio.model.Transition tr," +
                " com.trackstudio.model.Task t, com.trackstudio.model.Category cat where tr.start=t.status and " +
                "tr.mstatus=m.id and m.workflow=cat.workflow and cat.id=t.category and t.id=? order by m.name", taskId);
    }

    public String getMstatusesForAudit(String taskId, String nameMstatus) throws GranException {
            List<Mstatus> list = hu.getList("select m from com.trackstudio.model.Mstatus m," +
                    " com.trackstudio.model.Task t, com.trackstudio.model.Category cat where " +
                    "m.workflow=cat.workflow and cat.id=t.category and t.id=? and m.name=?", taskId, nameMstatus);
            if (list.isEmpty()) {
                return null;
            } else {
                return list.get(0).getId();
            }
    }

    /**
     * Возвращает список ID доступных типов сообщений для задачи и пользователя
     *
     * @param taskId ID задачи
     * @param userId ID пользователя
     * @return список сообещний
     * @throws GranException при необходимости
     */
    public ArrayList<String> getAvailableMstatusList(String taskId, String userId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            ArrayList<String> list = new ArrayList<String>();
            TaskRelatedInfo tci = TaskRelatedManager.getInstance().find(taskId);
            ArrayList prstatuses = KernelManager.getAcl().getAllowedPrstatusList(taskId, userId);
            String handlerid = tci.getHandlerUserId();
            String handlerGroupId = tci.getHandlerGroupId();
            String submitterid = tci.getSubmitterId();
            for (Object o : getMstatuses(taskId)) {
                Mstatus mstatus = (Mstatus) o;
                for (Object prstatuse : prstatuses) {
                    Prstatus prstatus = (Prstatus) prstatuse;
                    List<String> mprsList = MprstatusCacheManager.getInstance().find(prstatus.getId(), mstatus.getId()).getMprsList();
                    boolean userAbilities = getUserAbilities(userId, prstatus.getId(), handlerid, handlerGroupId, submitterid, PROCESS, mprsList);
                    if (userAbilities) {
                        list.add(mstatus.getId());
                        break;
                    }
                }
            }
            return list;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Осуществляет смену типа сообщения для задачи
     *
     * @param mstatusId      ID нового типа сообщения
     * @param taskId         ID задачи, для которой меняется тип сообщения
     * @param handlerUserId  ID ответсмтвенного пользователя
     * @param handlerGroupId ID jndtncndtyyjq uheggs
     * @param resolutionId   ID резолюции
     * @throws GranException при необходимости
     */
    public void step(String mstatusId, String taskId, String handlerUserId, String handlerGroupId, String resolutionId) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        lockManager.getLock(taskId).lock();
        try {
            String nextstep = getNextStatusId(taskId, mstatusId);
            Task task = KernelManager.getFind().findTask(taskId);
            Resolution res = null;
            if (resolutionId != null && resolutionId.length() != 0)
                res = KernelManager.getFind().findResolution(resolutionId);
            Status newStatus = null;
            Status oldStatus = task.getStatus();
            Usersource han = KernelManager.getFind().findUsersource(KernelManager.getUser().getUsersource(handlerUserId, handlerGroupId));
            if (nextstep != null && nextstep.length() != 0) {
                newStatus = KernelManager.getFind().findStatus(nextstep);
                task.setStatus(newStatus);
            }
            task.setResolution(res);
            task.setHandler(han);

            Calendar updateTime = (Calendar) hu.getList("select m.time from com.trackstudio.model.Message as m where m.task=? order by m.time desc", taskId).get(0);
            if (newStatus != null && newStatus.isFinish()) {
                if (!oldStatus.isFinish())
                    task.setClosedate(updateTime);
            } else {
                Calendar nullCalendar = null;
                task.setClosedate(nullCalendar);
            }
            task.setUpdatedate(updateTime);
            hu.updateObject(task);
        } finally {
            if (w) lockManager.releaseConnection(className);
            lockManager.getLock(taskId).unlock();
        }
    }

    /**
     * Возвращает ID следующего статуса для типа сообщения
     *
     * @param taskId    ID задачи
     * @param mstatusId ID типа сообщения
     * @return ID статуса
     * @throws GranException при необходимости
     */
    public String getNextStatusId(String taskId, String mstatusId) throws GranException {
        List<String> list = hu.getList("select t.finish.id from com.trackstudio.model.Transition t, " +
                "com.trackstudio.model.Task tsk where t.start=tsk.status and tsk.id=? and t.mstatus=?", taskId, mstatusId);
        if (list.isEmpty())
            return null;
        else
            return list.get(0);
    }

    /**
     * Проверяются права доступа пользователя к типам сообщений. (какие типы операций может выполнять пользователь над сообщениями
     *
     * @param userId         ID пользователя
     * @param prstatusId     ID статуса
     * @param handlerId      ID ответственного
     * @param handlerGroupId ID отетственной группы
     * @param submitterId    ID автора
     * @param operationType  Тип операции
     * @param mprsList       Cgbcjr nbgjd cjj,otybq
     * @return TRUE - если права есть, FALSE если нет
     * @throws GranException при необходимости
     */
    private boolean getUserAbilities(String userId, String prstatusId, String handlerId, String handlerGroupId, String submitterId, int operationType, List<String> mprsList) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            boolean ret = false;
            if (mprsList != null && !mprsList.isEmpty()) {
                for (String type : mprsList) {
                    switch (operationType) {
                        case VIEW:
                            if (WorkflowConstants.VIEW_ALL.equals(type)) {
                                ret = true;
                            } else if ((WorkflowConstants.VIEW_HANDLER.equals(type) || WorkflowConstants.VIEW_SUBMITTER_AND_HANDLER.equals(type)) && (userId.equals(handlerId) || (prstatusId != null && prstatusId.equals(handlerGroupId)))) {

                                ret = true;
                            } else if ((WorkflowConstants.VIEW_SUBMITTER.equals(type) || WorkflowConstants.VIEW_SUBMITTER_AND_HANDLER.equals(type)) && userId.equals(submitterId)) {
                                ret = true;
                            }
                            break;
                        case PROCESS:
                            if (WorkflowConstants.PROCESS_ALL.equals(type)) {
                                ret = true;
                            } else if ((WorkflowConstants.PROCESS_HANDLER.equals(type) || WorkflowConstants.PROCESS_SUBMITTER_AND_HANDLER.equals(type)) && (userId.equals(handlerId) || (prstatusId != null && prstatusId.equals(handlerGroupId)))) {
                                ret = true;
                            } else if ((WorkflowConstants.PROCESS_SUBMITTER.equals(type) || WorkflowConstants.PROCESS_SUBMITTER_AND_HANDLER.equals(type)) && userId.equals(submitterId)) {
                                ret = true;
                            }
                            break;
                        case BE_HANDLER:
                            if (WorkflowConstants.BE_HANDLER_ALL.equals(type)) {
                                ret = true;
                            } else if (WorkflowConstants.BE_HANDLER_HANDLER.equals(type) && (userId.equals(handlerId) || (prstatusId != null && prstatusId.equals(handlerGroupId))) ||
                                    WorkflowConstants.BE_HANDLER_SUBMITTER_AND_HANDLER.equals(type) && ((userId.equals(handlerId) || (prstatusId != null && prstatusId.equals(handlerGroupId)) || userId.equals(submitterId)))) {
                                ret = true;
                            } else if (WorkflowConstants.BE_HANDLER_SUBMITTER.equals(type) && userId.equals(submitterId) ||
                                    WorkflowConstants.BE_HANDLER_SUBMITTER_AND_HANDLER.equals(type) && ((userId.equals(handlerId) || (prstatusId != null && prstatusId.equals(handlerGroupId)) || userId.equals(submitterId)))) {
                                ret = true;
                            }
                            break;
                    }
                }
            }
            return ret;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает список ответственных для типа сообщения
     *
     * @param mstatusId ID типа сообщения
     * @param taskId    ID задачи
     * @return спсиок IDs ответственных
     * @throws GranException при необходимости
     */
    public ArrayList<String> getHandlerList(String mstatusId, String taskId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            Set<String> handlerList = new HashSet<String>();
            TreeSet<String> users2 = TaskRelatedManager.getInstance().getAllowedUsers(taskId);
            TreeSet<String> users = new TreeSet<String>();
            for (String userId : users2) {
                if (KernelManager.getUser().getActive(userId) && KernelManager.getUser().getUserExpireDate(userId) > System.currentTimeMillis())
                    users.add(userId);
            }
            String submitterid = TaskRelatedManager.getInstance().find(taskId).getSubmitterId();
            String handlerid = TaskRelatedManager.getInstance().find(taskId).getHandlerId();
            TaskRelatedInfo tci = TaskRelatedManager.getInstance().find(taskId);
            User handlerUser = handlerid == null ? null : KernelManager.getFind().findUsersource(handlerid).getUser();
            handlerid = handlerUser == null ? null : handlerUser.getId();
            for (String userid : users) {
                for (String prstatus : TaskRelatedManager.getInstance().getAllowedPrstatuses(userid, taskId)) {
                    // try {
                    List<String> mprsList = MprstatusCacheManager.getInstance().find(prstatus, mstatusId).getMprsList();
                    if (getUserAbilities(userid, prstatus, handlerid, tci.getHandlerGroupId(), submitterid, BE_HANDLER, mprsList)) {
                        handlerList.add(userid);
                        break;
                    }
                    //} catch (NullPointerException e) {
                    //    System.out.println("\n----------- #69391 ------------\n"+"prstatus: "+prstatus+"\n"+"mstatusId: "+mstatusId+"\n------------------------------");
                    //}
                }
            }
            return new ArrayList<String>(handlerList);
        } catch (Exception e) {
            throw new GranException(e);
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает список ответственных статусов для типа сообщения
     *
     * @param mstatusId ID типа сообщения
     * @param taskId    ID задачи
     * @return спсиок IDs ответственных статусов
     * @throws GranException при необходимости
     */
    public ArrayList<String> getHandlerGroupList(String mstatusId, String taskId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            Category category = KernelManager.getFind().findCategory(TaskRelatedManager.getInstance().find(taskId).getCategoryId());
            Integer handlerRole = category.getGroupHandlerAllowed();
            boolean handlerOnlyRole = Preferences.isHandlerOnlyRole(category.getPreferences());
            if (!handlerOnlyRole && (handlerRole == null || handlerRole == 0))
                return new ArrayList<String>();
            Set<String> handlerGroupList = new HashSet<String>();
            TreeSet<String> groups = TaskRelatedManager.getInstance().getAllowedGroups(taskId);
            String handlerSourceid = TaskRelatedManager.getInstance().find(taskId).getHandlerId();
            Prstatus handlerGroup = null;
            User user = null;
            if (handlerSourceid != null) {
                Usersource src = KernelManager.getFind().findUsersource(handlerSourceid);
                handlerGroup = src.getPrstatus();
                user = src.getUser();
            }
            if (handlerGroup == null && user != null) {
                handlerGroup = user.getPrstatus();
            }
            for (String groupId : groups) {
                List<String> mprsList = MprstatusCacheManager.getInstance().find(groupId, mstatusId).getMprsList();
                if (mprsList != null && !mprsList.isEmpty())
                    for (String type : mprsList) {
                        if (WorkflowConstants.BE_HANDLER_ALL.equals(type) || (WorkflowConstants.BE_HANDLER_HANDLER.equals(type) || WorkflowConstants.BE_HANDLER_SUBMITTER_AND_HANDLER.equals(type)) && handlerGroup != null && handlerGroup.getId().equals(groupId))
                            handlerGroupList.add(groupId);
                    }
            }
            return new ArrayList<String>(handlerGroupList);
        } catch (Exception e) {
            throw new GranException(e);
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает список ответственных для редактирвоания задачи
     *
     * @param taskId     ID хажачи
     * @param categoryId ID категории
     * @param isNew      Новая задача или нет
     * @param submitter  ID Ответственного
     * @return спсиок пользователей, которые могут быть ответственными
     * @throws GranException при необходимости
     * @see com.trackstudio.kernel.cache.UserRelatedInfo
     */
    public ArrayList<UserRelatedInfo> getTaskEditHandlerList(String taskId, String categoryId, boolean isNew, String submitter) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            Set<UserRelatedInfo> handlerList = new HashSet<UserRelatedInfo>();
            TreeSet<String> usersHasAccess = TaskRelatedManager.getInstance().getAllowedUsers(taskId);
            // исключаем из списка неактивных юзеров
            for (String userId : usersHasAccess) {
                UserRelatedInfo userCi = UserRelatedManager.getInstance().find(userId);
                // User effective statuses
                ArrayList<String> efectiveStatuses = AclManager.getInstance().getEffectiveStatuses(taskId, userId);
                for (String statusId : efectiveStatuses) {
                    if (!KernelManager.getCategory().isCategoryCanBeHandler(taskId, userId, categoryId, statusId, isNew, submitter))
                        continue;
                    if (KernelManager.getUser().getActive(userId) && KernelManager.getUser().getUserExpireDate(userId) > System.currentTimeMillis())
                        handlerList.add(userCi);
                    break;
                }
            }
            return new ArrayList<UserRelatedInfo>(handlerList);
        } catch (Exception e) {
            throw new GranException(e);
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает список ответственных стутасов для редактирвоания задачи
     *
     * @param taskId     ID хажачи
     * @param categoryId ID категории
     * @param isNew      Новая задача или нет
     * @return спсиок статусов, которые могут быть ответственными
     * @throws GranException при необходимости
     * @see com.trackstudio.kernel.cache.UserRelatedInfo
     */
    public ArrayList<Prstatus> getTaskEditGroupHandlerList(String taskId, String categoryId, boolean isNew) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            Integer gha = KernelManager.getFind().findCategory(categoryId).getGroupHandlerAllowed();
            if (gha == null || gha == 0)
                return new ArrayList<Prstatus>();
            Set<Prstatus> handlerList = new HashSet<Prstatus>();
            TreeSet<String> groupsHasAccess = TaskRelatedManager.getInstance().getAllowedGroups(taskId);
            for (String groupId : groupsHasAccess) {
                if (KernelManager.getCategory().isCategoryCanBeHandlerForGroup(taskId, groupId, categoryId, isNew)) {
                    handlerList.add(KernelManager.getFind().findPrstatus(groupId));
                }
            }
            return new ArrayList<Prstatus>(handlerList);
        } catch (Exception e) {
            throw new GranException(e);
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает список доступных пользователей для указанной задачи которым можно отправлять почтовые уведомления
     *
     * @param taskId ID задачи
     * @return список доступных пользователей для задачи
     * @throws GranException при необходимости
     * @see com.trackstudio.kernel.cache.UserRelatedInfo
     */
    public List<UserRelatedInfo> getAllowedMsgRecepientList(String taskId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            ArrayList<UserRelatedInfo> list = new ArrayList<UserRelatedInfo>();
            list.addAll(KernelManager.getAcl().getAllowedUserList(taskId));
            return list;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает список сообщений для задачи и пользователя
     *
     * @param taskId ID задачи
     * @param userId ID пользователя
     * @return список сообщений
     * @throws GranException при необходимости
     * @see com.trackstudio.kernel.cache.MessageCacheItem
     */
    public ArrayList<MessageCacheItem> getMessageList(String taskId, String userId) throws GranException {
        log.trace("getMessageList");
        boolean r = lockManager.acquireConnection(className);
        try {
            ArrayList<MessageCacheItem> retlist = new ArrayList<MessageCacheItem>();
            TaskRelatedInfo task = TaskRelatedManager.getInstance().find(taskId);
            String handlerid = task.getHandlerUserId() != null ? task.getHandlerUserId() : null;
            String submitterid = task.getSubmitterId() != null ? task.getSubmitterId() : null;
            TreeSet<String> prst = TaskRelatedManager.getInstance().getAllowedPrstatuses(userId, taskId);
            List<MessageCacheItem> messages = task.getMessages();
            for (MessageCacheItem m : messages) {
                for (String prstatusid : prst) {
                    if (m.getMstatusId() == null)
                        continue;
                    List<String> mprs = MprstatusCacheManager.getInstance().find(prstatusid, m.getMstatusId()).getMprsList();
                    if (getUserAbilities(userId, prstatusid, handlerid, task.getHandlerGroupId(), submitterid, VIEW, mprs)) {
                        retlist.add(m);
                        break;
                    }
                }
            }
            return retlist;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Проверяет может ли пользователь просматривать сообщения указанного типа
     *
     * @param userId    ID пользователя
     * @param taskId    ID задачи
     * @param mstatusId ID типа сообщений
     * @return TRUE - если может, FALSE - если нет
     * @throws GranException при необзодимости
     */
    public boolean isMessageViewable(String userId, String taskId, String mstatusId) throws GranException {
        log.trace("getProcessableMstatusList");
        boolean r = lockManager.acquireConnection(className);
        try {
            TaskRelatedInfo task = TaskRelatedManager.getInstance().find(taskId);
            String handlerid = task.getHandlerUserId() != null ? task.getHandlerUserId() : null;
            String submitterid = task.getSubmitterId() != null ? task.getSubmitterId() : null;
            ArrayList<Prstatus> prstatuses = KernelManager.getAcl().getAllowedPrstatusList(taskId, userId);
            for (Prstatus prstatus : prstatuses) {
                List<String> mprsList = MprstatusCacheManager.getInstance().find(prstatus.getId(), mstatusId).getMprsList();
                boolean userAbilities = getUserAbilities(userId, prstatus.getId(), handlerid, task.getHandlerGroupId(), submitterid, VIEW, mprsList);
                if (userAbilities) return true;
            }
            return false;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает списов всех возможных статусов сообщений
     *
     * @param userId ID gjkmpjdfntkz
     * @param taskId ID задачи
     * @return список ID статусов
     * @throws GranException при необходимости
     */
    // todo winzard Потенциально это место для ускорения, т.к. на самом деле список нам нужен только в одной специальной ситуации, когда поступает email submission
    public List<String> getProcessableMstatusList(String userId, String taskId) throws GranException {
        log.trace("getProcessableMstatusList");
        boolean r = lockManager.acquireConnection(className);
        try {
            List<String> retlist = new ArrayList<String>();
            TaskRelatedInfo task = TaskRelatedManager.getInstance().find(taskId);
            String handlerid = task.getHandlerUserId() != null ? task.getHandlerUserId() : null;
            String submitterid = task.getSubmitterId() != null ? task.getSubmitterId() : null;
            ArrayList<Prstatus> prstatuses = KernelManager.getAcl().getAllowedPrstatusList(taskId, userId);
            for (Mstatus mstatus : getMstatuses(taskId)) {
                for (Prstatus prstatus : prstatuses) {
                    List<String> mprsList = MprstatusCacheManager.getInstance().find(prstatus.getId(), mstatus.getId()).getMprsList();
                    boolean userAbilities = getUserAbilities(userId, prstatus.getId(), handlerid, task.getHandlerGroupId(), submitterid, PROCESS, mprsList);
                    if (userAbilities)
                        retlist.add(mstatus.getId());
                }
            }
            return retlist;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }
}
