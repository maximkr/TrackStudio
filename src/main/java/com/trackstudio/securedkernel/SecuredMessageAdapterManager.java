package com.trackstudio.securedkernel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.Defaults;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.filter.TaskFValue;
import com.trackstudio.app.filter.list.MessageFilter;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.AccessDeniedException;
import com.trackstudio.exception.GranException;
import com.trackstudio.exception.InvalidParameterException;
import com.trackstudio.exception.UserException;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.cache.AttachmentCacheItem;
import com.trackstudio.kernel.cache.MessageCacheItem;
import com.trackstudio.kernel.cache.TaskRelatedInfo;
import com.trackstudio.kernel.cache.TaskRelatedManager;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.kernel.manager.SafeString;
import com.trackstudio.model.Mstatus;
import com.trackstudio.model.Resolution;
import com.trackstudio.secured.SecuredFilterBean;
import com.trackstudio.secured.SecuredMessageBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.tools.SecuredBeanUtil;

import net.jcip.annotations.Immutable;

import static com.trackstudio.tools.Null.isNull;

/**
 * Класс MessageManager содержит методы для работы с сообщениями
 */
@Immutable
public class SecuredMessageAdapterManager {

    private static final Log log = LogFactory.getLog(SecuredMessageAdapterManager.class);

    /**
     * Создает сообщение
     *
     * @param sc             сессия пользователя
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
     * @param sendMail       Надо ли слать почту при добавлении сообщения
     * @param time           Дата создания сообщения
     * @return ID созданного сообщения
     * @throws GranException при необходимости
     */
    public String createMessage(SessionContext sc, String taskId, String mstatusId, String text, Long hrs,
                                String handlerUserId, String handlerGroupId, String resolutionId, String priorityId, Calendar deadline,
                                Long budget, boolean sendMail, Calendar time) throws GranException {
        log.trace("createMessage(handlerId='" + handlerUserId + "', resolutionId='" + resolutionId + "')");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "createMessage", "sc", sc);
        if (taskId == null)
            throw new InvalidParameterException(this.getClass(), "createMessage", "taskId", sc);
        if (text == null)
            throw new InvalidParameterException(this.getClass(), "createMessage", "text", sc);
        if (hrs != null && hrs.doubleValue() < 0.0)
            throw new InvalidParameterException(this.getClass(), "createMessage", "hrs", sc);
        TaskRelatedInfo task = TaskRelatedManager.getInstance().find(taskId);
        List<String> availMstatuses = KernelManager.getStep().getProcessableMstatusList(sc.getUserId(), taskId);
        if (mstatusId == null || mstatusId.length() == 0) {
            String defMst = Defaults.getDefaultMstatusId(taskId);
            mstatusId = availMstatuses.contains(defMst) ? defMst : availMstatuses.isEmpty() ? "" : (String) availMstatuses.get(0);
        }
        boolean allowedMstatus = availMstatuses.contains(mstatusId);
        if (!allowedMstatus)
            throw new UserException("ERROR_CAN_NOT_ADD_MSG_WRONG_MSTATUS", new Object[] {"#"+task.getNumber()});

        boolean allowedResolution = resolutionId == null || KernelManager.getWorkflow().getResolutionList(mstatusId).contains(KernelManager.getFind().findResolution(resolutionId));
        boolean allowedPriority = priorityId == null || KernelManager.getWorkflow().getPriorityList(task.getWorkflowId()).contains(KernelManager.getFind().findPriority(priorityId));

        if (allowedResolution && resolutionId == null) {
            for (Resolution resolution : KernelManager.getWorkflow().getResolutionList(mstatusId)) {
                 if (resolution.isDefault()) {
                     resolutionId = resolution.getId();
                    break;
                 }
            }
        }
        if (!sc.canAction(Action.editTaskHandler, taskId)) {
            handlerUserId = task.getHandlerUserId();
            handlerGroupId = task.getHandlerGroupId();
        }
        // check for handler==none
        if (isNull(handlerUserId)) handlerUserId = null;
        if (isNull(handlerGroupId)) handlerGroupId = null;

        ArrayList<String> handlerList = KernelManager.getStep().getHandlerList(mstatusId, taskId);
        ArrayList<String> handlerGroupList = KernelManager.getStep().getHandlerGroupList(mstatusId, taskId);

        // If no handlers available, set handler to Nobody.
        boolean newHandlerIsNull = false;
        boolean newHandlerUserSame = false;
        boolean newHandlerGroupSame = false;
        boolean newHandlerUserAllowed = false;
        boolean newHandlerGroupAllowed = false;


        if (task.getHandlerUserId() != null && task.getHandlerUserId().equals(handlerUserId))
            newHandlerUserSame = true;

        if (task.getHandlerGroupId() != null && task.getHandlerGroupId().equals(handlerGroupId))
            newHandlerGroupSame = true;

        if (handlerUserId != null && handlerList.contains(handlerUserId))
            newHandlerUserAllowed = true;

        if (handlerGroupId != null && handlerGroupList.contains(handlerGroupId))
            newHandlerGroupAllowed = true;

        if (handlerUserId == null && handlerGroupId == null) {
            newHandlerIsNull = true;
        }

        if (handlerList.isEmpty() && handlerGroupList.isEmpty() && !(newHandlerGroupSame || newHandlerUserSame)) {
            handlerUserId = null;
            handlerGroupId = null;
        }



        boolean allowedHandler = newHandlerIsNull || newHandlerUserSame || newHandlerGroupSame || newHandlerUserAllowed || newHandlerGroupAllowed;

        if (!sc.allowedByACL(taskId))
            throw new AccessDeniedException(this.getClass(), "createMessage", sc, "!sc.allowedByACL(taskId)", taskId);
        if (!allowedMstatus)
            throw new AccessDeniedException(this.getClass(), "createMessage", sc, "!allowedMstatus", taskId);
        if (!allowedHandler)
            throw new AccessDeniedException(this.getClass(), "createMessage", sc, "!allowedHandler", taskId);
        if (!allowedResolution)
            throw new AccessDeniedException(this.getClass(), "createMessage", sc, "!allowedResolution", taskId);
        if (!allowedPriority)
            throw new AccessDeniedException(this.getClass(), "createMessage", sc, "!allowedPriority", taskId);


        Long hrs1 = hrs;
        TaskRelatedInfo task1 = TaskRelatedManager.getInstance().find(taskId);

        Calendar deadline1 = task1.getDeadline();
        String priority = task1.getPriorityId();
        if (!sc.canAction(Action.editTaskBudget, taskId))
            budget = task1.getBudget();
        if (sc.canAction(Action.editTaskDeadline, taskId)) {
            deadline1 = deadline;
        }
        if (sc.canAction(Action.editTaskPriority, taskId) && priorityId != null && priorityId.length() != 0)
            priority = priorityId;
        if (!sc.canAction(Action.editTaskActualBudget, taskId))
            hrs1 = null;

        return KernelManager.getMessage().createMessage(sc.getUserId(), taskId, mstatusId, SafeString.createSafeString(text),
                hrs1, handlerUserId, handlerGroupId, resolutionId, priority, deadline1, budget, time);
    }

    /**
     * Создает сообщение
     *
     * @param sc             сессия пользователя
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
     * @param sendMail       Надо ли слать почту при добавлении сообщения
     * @return ID созданного сообщения
     * @throws GranException при необходимости
     */
    public String createMessage(SessionContext sc, String taskId, String mstatusId, String text, Long hrs,
                                String handlerUserId, String handlerGroupId, String resolutionId, String priorityId, Calendar deadline,
                                Long budget, boolean sendMail) throws GranException {
        return createMessage(sc, taskId, mstatusId, text, hrs, handlerUserId, handlerGroupId, resolutionId, priorityId, deadline, budget, sendMail, null);
    }

    /**
     * Удаляет сообщение
     *
     * @param sc        сессия пользователя
     * @param messageId ID удаляемого сообщения
     * @throws GranException при необходимости
     */
    public void deleteMessage(SessionContext sc, String messageId) throws GranException {
        log.trace("deleteMessage");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "deleteMessage", "sc", sc);
        if (messageId == null)
            throw new InvalidParameterException(this.getClass(), "deleteMessage", "messageId", sc);
        MessageCacheItem message = TaskRelatedManager.findMessage(messageId);
        //if (!(sc.canAction(com.trackstudio.kernel.cache.Action.deleteMessage, message.getTaskId()) && (sc.allowedByACL(message.getTaskId()))))
        //    throw new AccessDeniedException(this.getClass(), "deleteMessage", sc);
        if (message == null)
            return;
        TaskRelatedInfo task = TaskRelatedManager.getInstance().find(message.getTaskId());
        if (!sc.allowedByACL(message.getTaskId()))
            throw new AccessDeniedException(this.getClass(), "deleteMessage", sc, "!sc.allowedByACL(message.getTaskId())", messageId);
        if (!sc.canAction(Action.deleteOperations, message.getTaskId()))
            if (!AdapterManager.getInstance().getSecuredCategoryAdapterManager().isCategoryDeletable(sc, task.getId(), task.getCategoryId()))
                throw new AccessDeniedException(this.getClass(), "deleteMessage", sc, "!sc.canAction(com.trackstudio.kernel.cache.Action.deleteMessage, message.getTaskId())", messageId);
        KernelManager.getMessage().deleteMessage(messageId);
    }

    /**
     * Method used only for delete task!
     * @param sc session context user
     * @param stb task for get message
     * @param messageId message ID
     * @throws GranException for need
     */
    public void deleteMessageOnlyForDeleteTask(SessionContext sc, SecuredTaskBean stb, String messageId) throws GranException {
        log.trace("deleteMessage");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "deleteMessage", "sc", sc);
        if (messageId == null)
            throw new InvalidParameterException(this.getClass(), "deleteMessage", "messageId", sc);
        MessageCacheItem message = null;
        for (SecuredMessageBean m : stb.getMessages()) {
            if (m.getId().equals(messageId)) {
                message = m.getMessage();
                break;
            }
        }
        if (message == null)
            return;
        TaskRelatedInfo task = TaskRelatedManager.getInstance().find(message.getTaskId());
        if (!sc.allowedByACL(message.getTaskId()))
            throw new AccessDeniedException(this.getClass(), "deleteMessage", sc, "!sc.allowedByACL(message.getTaskId())", messageId);
        if (!sc.canAction(Action.deleteOperations, message.getTaskId()))
            if (!AdapterManager.getInstance().getSecuredCategoryAdapterManager().isCategoryDeletable(sc, task.getId(), task.getCategoryId()))
                throw new AccessDeniedException(this.getClass(), "deleteMessage", sc, "!sc.canAction(com.trackstudio.kernel.cache.Action.deleteMessage, message.getTaskId())", messageId);
        for (AttachmentCacheItem att : KernelManager.getAttachment().getAttachmentList(stb.getId(), messageId, null)) {
            KernelManager.getAttachment().deleteAttachment(att.getId());
        }
        KernelManager.getMessage().deleteMessageOnlyForDeleteTask(messageId);
    }


    /**
     * Возвращает список проходящих по фильтру сообщений
     *
     * @param sc       сессия пользователя
     * @param filterId ID фильтра
     * @param taskId   ID задачи
     * @return список сообщений
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredMessageBean
     */                                                                              //forcedAllMessagesView - надо для workload отчета
    public List<SecuredMessageBean> getMessageList(SessionContext sc, String taskId, String filterId) throws GranException {
        log.trace("getMessageList");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getMessageList", "sc", sc);
        if (taskId == null)
            throw new InvalidParameterException(this.getClass(), "getMessageList", "taskId", sc);
        if (filterId == null)
            throw new InvalidParameterException(this.getClass(), "getMessageList", "filterId", sc);
        SecuredFilterBean filter = AdapterManager.getInstance().getSecuredFindAdapterManager().findFilterById(sc, filterId);
        if (!(sc.allowedByACL(taskId) && filter.canView()))
            return new ArrayList<SecuredMessageBean>();
        List<SecuredMessageBean> result1;
        try {
            SecuredTaskBean task = new SecuredTaskBean(taskId, sc);
            MessageFilter msgList = new MessageFilter(task);
            TaskFValue flthm = AdapterManager.getInstance().getSecuredFilterAdapterManager().getTaskFValue(sc, filterId).getFValue();
            result1 = msgList.getMessageList(sc, flthm, false, true);
        } catch (Exception e) {
            throw new GranException(e);
        }
        return result1;
    }

    /**
     * Возвращает список сообщений для задачи и текущегопользователя
     *
     * @param sc     сессия пользователя
     * @param taskId ID задачи
     * @return список сообщений
     * @throws GranException при необходимости
     * @see com.trackstudio.kernel.cache.MessageCacheItem
     */
    public ArrayList<SecuredMessageBean> getMessageList(SessionContext sc, String taskId) throws GranException {
        log.trace("getMessageList");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getMessageList", "sc", sc);
        if (taskId == null)
            throw new InvalidParameterException(this.getClass(), "getMessageList", "taskId", sc);
        //if (!(sc.canAction(com.trackstudio.kernel.cache.Action.viewMessage, taskId) && sc.allowedByACL(taskId)))
        //    throw new AccessDeniedException(this.getClass(), "getMessageList", sc);
        if (!sc.allowedByACL(taskId))
            return new ArrayList<SecuredMessageBean>(0);
        return SecuredBeanUtil.toArrayList(sc, KernelManager.getStep().getMessageList(taskId, sc.getUserId()), SecuredBeanUtil.MESSAGE);
    }

    /**
     * Метод возвращает все сообщения пользователя
     * @param sc сессия
     * @param ownerId идентификатор пользователя
     * @return список идентификаторов сообщений
     * @throws GranException при необходимости
     */
    public List<MessageCacheItem> getMessageUserList(SessionContext sc, String ownerId) throws GranException {
        log.trace("getBookmarkList");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getMessageUserList", "sc", sc);
        if (ownerId == null)
            throw new InvalidParameterException(this.getClass(), "getMessageUserList", "owner", sc);
        return KernelManager.getMessage().getMessageUser(ownerId);
    }

    public List<Mstatus> getListTrigger(SessionContext sc, String triggerId) throws GranException {
        log.trace("getBookmarkList");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getMessageUserList", "sc", sc);
        if (triggerId == null)
            throw new InvalidParameterException(this.getClass(), "getMessageUserList", "triggerId", sc);
        return KernelManager.getMessage().getListTrigger(triggerId);
    }

    public void updateDescription(SessionContext sc, String messageId, String convertedDesc) throws GranException {
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getMessageUserList", "sc", sc);
        if (messageId == null)
            throw new InvalidParameterException(this.getClass(), "getMessageUserList", "triggerId", sc);
        KernelManager.getMessage().updateDescription(messageId, convertedDesc);
    }

    /**
     * This method gets all attachments for messages
     * @param sc Session context
     * @param taskId task id where to be check ACL
     * @param tasksId tasks for attachments
     * @return map
     * @throws GranException for necessary
     */
    public Map<String, List<AttachmentCacheItem>> getCollectAttachmentsForMessage(SessionContext sc, String taskId, List<String> tasksId) throws GranException {
        if (sc == null) {
            throw new InvalidParameterException(this.getClass(), "getCollectAttachmentsForMessage", "sc", sc);
        }
        if (tasksId == null) {
            throw new InvalidParameterException(this.getClass(), "getCollectAttachmentsForMessage", "tasksId == null || tasksId.isEmpty()", sc);
        }
        if (!sc.canAction(Action.viewTaskAttachments, taskId)) {
            throw new InvalidParameterException(this.getClass(), "getCollectAttachmentsForMessage", "!sc.canAction(Action.viewTaskAttachments, taskId", sc);
        }
        return KernelManager.getAttachment().getAttachmentsMap(tasksId);
    }
}
