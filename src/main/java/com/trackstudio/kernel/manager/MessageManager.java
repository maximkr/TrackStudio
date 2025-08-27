/*
 * @(#)MessageManager.java
 * Copyright 2009 TrackStudio, Inc. All rights reserved.
 */
package com.trackstudio.kernel.manager;

import java.util.Calendar;
import java.util.List;

import com.trackstudio.app.Defaults;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.AttachmentCacheItem;
import com.trackstudio.kernel.cache.MessageCacheItem;
import com.trackstudio.kernel.cache.TaskRelatedManager;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.model.Longtext;
import com.trackstudio.model.Message;
import com.trackstudio.model.Mstatus;
import com.trackstudio.model.Task;
import com.trackstudio.tools.audit.trail.AuditUtil;

import net.jcip.annotations.Immutable;

/**
 * Класс MessageManager содержит методы для работы с сообщениями
 */
@Immutable
public class MessageManager extends KernelManager {

    private static final String className = "MessageManager.";
    private static final MessageManager instance = new MessageManager();
    private static final LockManager lockManager = LockManager.getInstance();
    /**
     * Конструктор по умолчанию
     */
    private MessageManager() {
    }

    /**
     * Возвращает экземпляр текущего класса
     *
     * @return Экземпляр MessageManager
     */
    protected static MessageManager getInstance() {
        return instance;
    }

    public String createMessage(String userId, String taskId, String mstatusId, SafeString text, Long hrs,
                                String handlerUserId, String handlerGroupId, String resolutionId, String priorityId, Calendar deadline, Long budget, Calendar submitDate) throws GranException {
        return createMessage(userId, taskId, mstatusId, text, hrs, handlerUserId, handlerGroupId, resolutionId, priorityId, deadline, budget, submitDate, AuditUtil.Type.USUAL);
    }

    /**
     * Создает сообщение
     *
     * @param userId         ID пользователя, который создает сообщение
     * @param taskId         ID задачи, для которой создается сообещщние
     * @param mstatusId      ID типа сообщения
     * @param text           текст сообщения
     * @param hrs            потраченное время
     * @param handlerUserId  ID пользователя, назначенного в качестве ответственного
     * @param handlerGroupId ID группы, назначченной в качестве отвественной
     * @param resolutionId   ID резолюции
     * @param priorityId     ID приоритета
     * @param deadline       Дата Deadline
     * @param budget         Бюджет
     * @param submitDate     Дата создания сообщения
     * @return ID созданного сообщения
     * @throws GranException при необходимости
     */
    public String createMessage(String userId, String taskId, String mstatusId, SafeString text, Long hrs,
                                String handlerUserId, String handlerGroupId, String resolutionId, String priorityId, Calendar deadline, Long budget, Calendar submitDate, AuditUtil.Type auditOperation) throws GranException {
        String messageId = null;
        boolean w = lockManager.acquireConnection(className);
        lockManager.getLock(taskId).lock();
        try {
            if (text == null) text = SafeString.createSafeString("");
            if (mstatusId == null || mstatusId.length() == 0)
                mstatusId = Defaults.getDefaultMstatusId(taskId);
            String newLongtextId = null;
            if (text.length() > 2000)
                newLongtextId = KernelManager.getLongText().createLongtext(null, text.toString());
            Task task = KernelManager.getFind().findTask(taskId);
            String msgPriorityId = priorityId;
            Calendar msgDeadline = deadline;
            Long msgBudget = AuditUtil.Type.USUAL != auditOperation ? task.getBudget() : budget;
            if (task.getPriority() != null && priorityId != null && priorityId.equals(task.getPriority().getId()))
                msgPriorityId = null;
            if (deadline != null && task.getDeadline() != null && deadline.equals(task.getDeadline()))
                msgDeadline = null;
            if ((budget == null || budget == 0L) || (task.getBudget() == null && budget != null)
                    || (task.getBudget() != null && task.getBudget().equals(budget))) {
                msgBudget = null;
            }
            String handlerSource = KernelManager.getUser().getUsersource(handlerUserId, handlerGroupId);
            Message message = new Message(taskId, userId, mstatusId, hrs, handlerSource, resolutionId, msgPriorityId, msgDeadline, msgBudget, submitDate);
            if (newLongtextId != null)
                message.setLongtext(KernelManager.getFind().findLongtext(newLongtextId));
            else
                message.setDescription(text.toString());
            messageId = hu.createObject(message);
            task.setBudget(AuditUtil.Type.USUAL != auditOperation ? task.getBudget() : budget);
            task.setDeadline(deadline);
            task.setPriority(priorityId);
            if (AuditUtil.Type.ATTACHMENT == auditOperation) {
                task.setUpdatedate(submitDate);
            }
            hu.updateObject(task);
            hu.cleanSession();
            
            resolutionId = resolutionId != null ? resolutionId : task.getResolution() != null ? task.getResolution().getId() : null;
            if (AuditUtil.Type.USUAL == auditOperation) {
                KernelManager.getStep().step(mstatusId, taskId, handlerUserId, handlerGroupId, resolutionId);
            }
            hu.cleanSession();
            
            actualizeActualBudget(taskId);
        } finally {
            if (w) lockManager.releaseConnection(className);
            lockManager.getLock(taskId).unlock();
        }
        return messageId;
    }

    /**
     * Удаляет сообщение
     *
     * @param messageId ID удаляемого сообщения
     * @throws GranException при необходимости
     */
    public void deleteMessage(String messageId) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        lockManager.getLock(messageId).lock();
        try {
            String taskId = TaskRelatedManager.findMessage(messageId).getTaskId();
            for (AttachmentCacheItem att : KernelManager.getAttachment().getAttachmentList(taskId, messageId, null))
                KernelManager.getAttachment().deleteAttachment(att.getId());
            hu.deleteObject(Message.class, messageId);
            hu.cleanSession();
            
            actualizeActualBudget(taskId);
        } finally {
            if (w) lockManager.releaseConnection(className);
            lockManager.getLock(messageId).unlock();
        }
    }

    /**
     * Method used only for delete task. It doesn't make actualize time!
     *
     * @param messageId message ID
     * @throws GranException for need
     */
    public void deleteMessageOnlyForDeleteTask(String messageId) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        lockManager.getLock(messageId).lock();
        try {
            hu.deleteObject(Message.class, messageId);
        } finally {
            if (w) lockManager.releaseConnection(className);
            lockManager.getLock(messageId).unlock();
        }
    }

    /**
     * Высчитывает новое кооличетсво потраченного времени для задачи, которое могло измениться в результате добавления сообщения
     *
     * @param taskId ID задачи
     * @throws GranException при необходимости
     */
    private void actualizeActualBudget(String taskId) throws GranException {
        Long sum = 0L;
        for (MessageCacheItem msg : TaskRelatedManager.getInstance().find(taskId).getMessages()) {
            if (msg.getHrs() != null)
                sum += msg.getHrs();
        }
        Task task = KernelManager.getFind().findTask(taskId);
        task.setAbudget(sum);
        hu.updateObject(task);
        hu.cleanSession();
        
        TaskRelatedManager.getInstance().invalidateTask(taskId);
    }

    /**
     * Метод возвращает все сообщения пользователя
     *
     * @param ownerId идентификатор пользователя
     * @return список идентификаторов сообщений
     * @throws GranException при необходимости
     */
    public List<MessageCacheItem> getMessageUser(String ownerId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            return hu.getList("select new com.trackstudio.kernel.cache.MessageCacheItem(message.id, message.description, message.time, message.hrs, message.deadline, message.budget, message.task.id, message.submitter.id, message.resolution.id, message.priority.id, message.handler.id, message.handler.user.id, message.handler.prstatus.id, message.mstatus.id, message.longtext.id) from com.trackstudio.model.Message message where message.submitter=?", ownerId);
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    public List<MessageCacheItem> getMessageMstatusList(String mstatusId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            return hu.getList("select new com.trackstudio.kernel.cache.MessageCacheItem(message.id, message.description, message.time, message.hrs, message.deadline, message.budget, message.task.id, message.submitter.id, message.resolution.id, message.priority.id, message.handler.id, message.handler.user.id, message.handler.prstatus.id, message.mstatus.id, message.longtext.id) from com.trackstudio.model.Message message where message.mstatus=?", mstatusId);
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    public List<Mstatus> getListTrigger(String triggerId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            return hu.getList("from com.trackstudio.model.Mstatus m where m.trigger.id=?", triggerId);
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    public void updateDescription(String messageId, String convertedDesc) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        lockManager.getLock(messageId).lock();
        try {
            Message msg = (Message) hu.getObject(Message.class, messageId);
            Longtext longtext = msg.getLongtext();
            String longtextId = longtext != null ? longtext.getId() : null;
            if (longtextId != null || convertedDesc.length() > 2000) {
                msg.setLongtext(KernelManager.getLongText().createLongtext(longtextId, convertedDesc));
                msg.setDescription(null);
            } else {
                msg.setDescription(convertedDesc);
            }
            hu.updateObject(msg);
            hu.cleanSession();
            
            actualizeActualBudget(msg.getTask().getId());
        } finally {
            if (r) lockManager.releaseConnection(className);
            lockManager.getLock(messageId).unlock();
        }
    }

    public List<Object[]> getMessageForScheduler() throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            return hu.getList("select msg.task.id, msg.time, msg.mstatus.preferences, msg.task.category.id from com.trackstudio.model.Message msg " +
                    "where msg.mstatus.preferences like '%C%' or msg.mstatus.preferences like '%I%'");
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }
}
