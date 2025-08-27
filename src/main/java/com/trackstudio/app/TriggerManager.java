package com.trackstudio.app;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.adapter.email.change.Change;
import com.trackstudio.app.adapter.email.change.NewMessageChange;
import com.trackstudio.app.adapter.email.change.NewMessageWithAttachmentChange;
import com.trackstudio.app.adapter.email.change.NewTaskChange;
import com.trackstudio.app.adapter.email.change.NewTaskWithAttachmentChange;
import com.trackstudio.app.adapter.email.change.TaskUpdatedChange;
import com.trackstudio.app.adapter.scheduler.SchedulerManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.exception.TriggerException;
import com.trackstudio.exception.UserException;
import com.trackstudio.external.OperationTrigger;
import com.trackstudio.external.TaskTrigger;
import com.trackstudio.kernel.cache.AbstractPluginCacheItem;
import com.trackstudio.kernel.cache.CompiledPluginCacheItem;
import com.trackstudio.kernel.cache.PluginCacheItem;
import com.trackstudio.kernel.cache.PluginCacheManager;
import com.trackstudio.kernel.cache.PluginType;
import com.trackstudio.kernel.cache.TaskRelatedManager;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.kernel.manager.AttachmentArray;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.model.Trigger;
import com.trackstudio.secured.Secured;
import com.trackstudio.secured.SecuredCategoryBean;
import com.trackstudio.secured.SecuredMessageBean;
import com.trackstudio.secured.SecuredMessageTriggerBean;
import com.trackstudio.secured.SecuredMstatusBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredTaskTriggerBean;
import com.trackstudio.tools.audit.trail.AuditTrailMessage;
import com.trackstudio.tools.audit.trail.AuditTrailTask;
import com.trackstudio.tools.audit.trail.AuditUtil;
import com.trackstudio.tools.textfilter.MacrosUtil;

import bsh.EvalError;
import bsh.TargetError;
import net.jcip.annotations.Immutable;

/**
 * Класс содержит методы для управления триггерами
 */
@Immutable
public class TriggerManager extends KernelManager {

    private static final Log log = LogFactory.getLog(TriggerManager.class);
    private static final TriggerManager instance = new TriggerManager();
    private static final String className = "TriggerManager.";

    private static final LockManager lockManager = LockManager.getInstance();

    private final TriggerExecute triggers = new TriggerExecute();

    private TriggerManager() {
    }

    /**
     * Возвращает экземпляр текущего класса
     *
     * @return экземпляр класса TriggerManager
     */
    public static TriggerManager getInstance() {
        return instance;
    }

    /**
     * Триггер при создании сообщения
     *
     * @param sc             сессия
     * @param id             ID
     * @param mstatusId      ID типа сообщения
     * @param text           текст
     * @param hrs            потраченное время
     * @param handlerUserId  ID ответственного пользователя
     * @param handlerGroupId ID ответственного статуса
     * @param resolutionId   ID резолюции
     * @param priorityId     ID приоритета
     * @param deadline       Дедлайн
     * @param budget         Бюджет
     * @param udfMap         карта полей
     * @param sendMail       надо ли отправлять почту или нет
     * @param atts           Список аттачей
     * @return ID созданного сообщения
     * @throws GranException при необходимости
     */
    public String createMessage(SessionContext sc, String id, String mstatusId, String text, Long hrs, String handlerUserId, String handlerGroupId, String resolutionId, String priorityId,
                                Calendar deadline, Long budget, HashMap<String, String> udfMap, boolean sendMail, List<AttachmentArray> atts) throws GranException {
        boolean w = lockManager.acquireConnection(TriggerManager.class.getSimpleName());
        lockManager.getLock(id).lock();
        try {
            log.debug("createMessage called");
            Calendar timeAudit = Calendar.getInstance();
            Calendar time = new GregorianCalendar();
            time.setTimeInMillis(System.currentTimeMillis());
            SecuredMessageTriggerBean input = new SecuredMessageTriggerBean(null, text, time, hrs, deadline, budget, id, sc.getUserId(), resolutionId, priorityId, null, handlerUserId, handlerGroupId, mstatusId, udfMap, sc, atts);
            SecuredMstatusBean smb = AdapterManager.getInstance().getSecuredFindAdapterManager().findMstatusById(sc, mstatusId);
            if (smb != null)
                input = this.triggers.executeTrigger(sc, PluginCacheManager.getInstance().find(PluginType.BEFORE_ADD_MESSAGE, smb.getBefore()), input);
            final String auditId;
            if (smb != null && smb.getInsteadOf() != null) {
                log.info("execute insteadOf");
                input = this.triggers.executeTrigger(sc, PluginCacheManager.getInstance().find(PluginType.INSTEAD_OF_ADD_MESSAGE, smb.getInsteadOf()), input);
                auditId = null;
            } else {
                AuditUtil util = new AuditUtil(new StringBuilder(), id, timeAudit, AuditUtil.Type.MESSAGE);
                auditId = new AuditTrailMessage(sc, id, util).auditMessage(input);
                input = input.create(sendMail);
            }
            SchedulerManager.getInstance().observedTask(input);
            if (smb != null) {
                input = this.triggers.executeTrigger(sc, PluginCacheManager.getInstance().find(PluginType.AFTER_ADD_MESSAGE, smb.getAfter()), input);
            }

            log.debug("CLOSE SESSION TriggerManager");
            if (sendMail) {
                Calendar now = Calendar.getInstance();
                Change change;
                if (atts==null || atts.isEmpty()) {
                    change = new NewMessageChange(now, sc.getUserId(), new SecuredMessageBean(input.getId(), sc), auditId);
                } else {
                    change = new NewMessageWithAttachmentChange(now, sc.getUserId(), new SecuredMessageBean(input.getId(), sc), auditId);
                }
                AdapterManager.getInstance().getFilterNotifyAdapterManager().sendNotifyForTask(input.getId(), input.getTaskId(), sc.getUserId(), mstatusId, change);
            }
/*
            if (Config.isTurnItOn("trackstudio.google.calendar.use") && Preferences.isUseGoogleCalendar(input.getTask().getCategory().getPreferences())) {
                CalendarUtil.getInstance().updateEvent(sc, input.getId());
            }
            if (Config.isTurnItOn("trackstudio.yandex.calendar.use") && Preferences.isUseYandexCalendar(input.getTask().getCategory().getPreferences())) {
                YandexCalendarUtil.getInstance().updateEvent(sc, input.getTaskId());
            }
            */
            return input.getId();
        } finally {
            if (w) lockManager.releaseConnection(TriggerManager.class.getSimpleName());
            lockManager.getLock(id).unlock();
        }
    }

    /**
     * Триггер при редактировании задачи
     *
     * @param sc             сессия
     * @param id             ID
     * @param shortname      алиас
     * @param name           название
     * @param description    описание
     * @param budget         бюджет
     * @param deadline       дедлайн
     * @param priorityId     ID приоритета
     * @param parentId       ID родительской задачи
     * @param handlerUserId  ID ответственного пользователя
     * @param handlerGroupId ID ответственного статуса
     * @param sendMail       надо ли отправить мообщение
     * @param udf            карта полей
     * @throws GranException при необходимости
     */
    public void updateTask(SessionContext sc, String id, String shortname, String name, String description,
                           Long budget, Calendar deadline, String priorityId,
                           String parentId, String handlerUserId, String handlerGroupId, boolean sendMail, HashMap udf) throws GranException {
        updateTask(sc, id, shortname, name, description, budget, deadline, priorityId, parentId, handlerUserId, handlerGroupId, sendMail, udf, true);
    }

    public void updateTask(SessionContext sc, String id, String shortname, String name, String description, Long budget, Calendar deadline, String priorityId, String parentId, String handlerUserId, String handlerGroupId, boolean sendMail, Map<String, String> udf, boolean isTask) throws GranException {
        lockManager.getLock(id).lock();
        try {
            Calendar timeAudit = Calendar.getInstance();
            Calendar time = new GregorianCalendar();
            time.setTimeInMillis(System.currentTimeMillis());
            SecuredTaskBean source = new SecuredTaskBean(id, sc);
            SecuredTaskTriggerBean input = new SecuredTaskTriggerBean(id, description, name, shortname, source.getSubmitdate(), time, source.getClosedate(), source.getActualBudget(), budget, deadline, source.getNumber(), source.getSubmitterId(), null, handlerUserId, handlerGroupId, parentId, source.getCategoryId(), source.getWorkflowId(), source.getStatusId(), source.getResolutionId(), priorityId, udf, sc, null, false);

            SecuredCategoryBean scb = AdapterManager.getInstance().getSecuredFindAdapterManager().findCategoryById(sc, TaskRelatedManager.getInstance().find(id).getCategoryId());
            input = this.triggers.executeTrigger(sc, PluginCacheManager.getInstance().find(PluginType.BEFORE_EDIT_TASK, scb.getUpdateBefore()), input);
            final String auditId;
            if (scb.getUpdateInsteadOf() != null) {
                log.info("execute insteadOf");
                input = this.triggers.executeTrigger(sc, PluginCacheManager.getInstance().find(PluginType.INSTEAD_OF_EDIT_TASK, scb.getUpdateInsteadOf()), input);
                auditId = null;
            } else {
                AuditUtil builder = new AuditUtil(new StringBuilder(), id, timeAudit, AuditUtil.Type.TASK);
                auditId = new AuditTrailTask(sc, id, builder).auditTask(input, isTask);
                input = input.update(sendMail);
            }
            log.debug("CLOSE SESSION TriggerManager");
            input = this.triggers.executeTrigger(sc, PluginCacheManager.getInstance().find(PluginType.AFTER_EDIT_TASK, scb.getUpdateAfter()), input);
            this.sendNotify(sc, input.getId(), true, false, false, auditId);

/*            if (Config.isTurnItOn("trackstudio.google.calendar.use") && Preferences.isUseGoogleCalendar(input.getCategory().getPreferences())) {
                CalendarUtil.getInstance().updateEvent(input);
            }
            if (Config.isTurnItOn("trackstudio.yandex.calendar.use") && Preferences.isUseYandexCalendar(input.getCategory().getPreferences())) {
                YandexCalendarUtil.getInstance().updateEvent(input);
            }
            */
        } finally {
            lockManager.getLock(id).unlock();
        }
    }

    public void moveTask(SessionContext sc, String id, String shortname, String name, String description,
                         Long budget, Calendar deadline, String priorityId,
                         String parentId, String handlerUserId, String handlerGroupId, boolean sendMail, HashMap udf) throws GranException {
        moveTask(sc,
                id, shortname, name,
                description, budget, deadline, priorityId, parentId, handlerUserId, handlerGroupId, sendMail, udf);
    }

    /**
     * Триггер при перемещении задачи
     *
     * @param sc             сессия
     * @param id             ID
     * @param number         number of previous task
     * @param shortname      алиас
     * @param name           название
     * @param description    описание
     * @param budget         бюджет
     * @param deadline       дедлайн
     * @param priorityId     ID приоритета
     * @param parentId       ID родительской задачи
     * @param handlerUserId  ID ответственного пользователя
     * @param handlerGroupId ID ответственного статуса
     * @param sendMail       надо ли отправить мообщение
     * @param udf            карта полей
     * @throws GranException при необходимости
     */
    public void moveTask(SessionContext sc, String id, String number, String shortname, String name, String description,
                         Long budget, Calendar deadline, String priorityId,
                         String parentId, String handlerUserId, String handlerGroupId, boolean sendMail, HashMap udf) throws GranException {
        lockManager.getLock(id).lock();
        try {
            Calendar timeAudit = Calendar.getInstance();
            SecuredTaskBean source = new SecuredTaskBean(id, sc);
            SecuredTaskTriggerBean input = new SecuredTaskTriggerBean(id, description, name, shortname, source.getSubmitdate(), source.getUpdatedate(), source.getClosedate(), source.getActualBudget(), budget, deadline, source.getNumber(), source.getSubmitterId(), null, handlerUserId, handlerGroupId, parentId, source.getCategoryId(), source.getWorkflowId(), source.getStatusId(), source.getResolutionId(), priorityId, udf, sc, null, true);
            SecuredCategoryBean scb = AdapterManager.getInstance().getSecuredFindAdapterManager().findCategoryById(sc, TaskRelatedManager.getInstance().find(id).getCategoryId());
            input = this.triggers.executeTrigger(sc, PluginCacheManager.getInstance().find(PluginType.BEFORE_EDIT_TASK, scb.getUpdateBefore()), input);
            if (scb.getUpdateInsteadOf() != null) {
                log.info("execute insteadOf");
                input = this.triggers.executeTrigger(sc, PluginCacheManager.getInstance().find(PluginType.INSTEAD_OF_EDIT_TASK, scb.getUpdateInsteadOf()), input);
            } else {
                AuditUtil builder = new AuditUtil(new StringBuilder("'" + name + " was moved from [#" + number + "]'<br/>"), id, timeAudit, AuditUtil.Type.TASK);
                new AuditTrailTask(sc, id, builder).auditTask(input, true);
                input = input.update(sendMail);
            }
            input = this.triggers.executeTrigger(sc, PluginCacheManager.getInstance().find(PluginType.AFTER_EDIT_TASK, scb.getUpdateAfter()), input);
        } finally {
            lockManager.getLock(id).unlock();
        }
    }


    /**
     * Триггер при создании задачи
     *
     * @param sc             сессия
     * @param categoryId     ID категории
     * @param shortname      алиас
     * @param name           название
     * @param description    описание
     * @param budget         бюджет
     * @param deadline       дедлайн
     * @param priorityId     ID приоритета
     * @param parentId       ID родительской задачи
     * @param handlerUserId  ID ответственного пользователя
     * @param handlerGroupId ID ответственного статуса
     * @param sendMail       надо ли отправить мообщение
     * @param udf            карта полей
     * @return ID задачи
     * @throws GranException при необходимости
     */
    @Deprecated
    public String createTask(SessionContext sc, String categoryId, String shortname, String name, String description,
                             Long budget, Calendar deadline, String priorityId,
                             String parentId, String handlerUserId, String handlerGroupId, boolean sendMail, HashMap udf) throws GranException {
        return createTask(sc, categoryId, shortname, name, description, budget, deadline, priorityId, parentId, handlerUserId, handlerGroupId, sendMail, udf, null, null, false);
    }

    @Deprecated
    public String createTask(SessionContext sc, String categoryId, String shortname, String name, String description,
                             Long budget, Calendar deadline, String priorityId,
                             String parentId, String handlerUserId, String handlerGroupId, boolean sendMail, HashMap udf, String statusId, List<AttachmentArray> atts) throws GranException {
        return createTask(sc, categoryId, shortname, name, description, budget, deadline, priorityId, parentId, handlerUserId, handlerGroupId, sendMail, udf, statusId, atts, false);
    }

    public String createTask(SecuredTaskTriggerBean input) throws GranException {
        boolean w = lockManager.acquireConnection(TriggerManager.class.getSimpleName());
        try {
            SessionContext sc = input.getSecure();
	        boolean send = input.isNeedSend();
            boolean hasAttach = (input.getAtts() != null) && !input.getAtts().isEmpty();
            SecuredCategoryBean scb = AdapterManager.getInstance().getSecuredFindAdapterManager().findCategoryById(sc, input.getCategoryId());
            input = this.triggers.executeTrigger(sc, PluginCacheManager.getInstance().find(PluginType.BEFORE_CREATE_TASK, scb.getCreateBefore()), input);
            if (scb.hasCreateInsteadOfTrigger()) {
                input = this.triggers.executeTrigger(sc, PluginCacheManager.getInstance().find(PluginType.INSTEAD_OF_CREATE_TASK, scb.getCreateInsteadOf()), input);
            } else {
                input = input.create(input.isNeedSend());
            }
            log.debug("CLOSE SESSION TriggerManager");
            input = this.triggers.executeTrigger(sc, PluginCacheManager.getInstance().find(PluginType.AFTER_CREATE_TASK, scb.getCreateAfter()), input);
            this.sendNotify(input.getSecure(), input.getId(), send, true, hasAttach, null);
            return input.getId();
        } finally {
            if (w) lockManager.releaseConnection(TriggerManager.class.getSimpleName());
        }
    }

    /**
     * This method executes module notifications events
     * @param sc SessionContext sc
     * @param taskId task ID
     * @param sendMail necessary sending
     * @param newTask create or update
     * @throws GranException for necessary
     */
    private void sendNotify(SessionContext sc, String taskId, boolean sendMail, boolean newTask, boolean hasAttachments, final String auditId) throws GranException {
        if (sendMail) {
            Change taskChange;
            Calendar now = new GregorianCalendar();
            now.setTimeInMillis(System.currentTimeMillis());
            if (newTask) {
                if (hasAttachments) {
                    taskChange = new NewTaskWithAttachmentChange(now, sc.getUserId(), taskId);
                } else {
                    taskChange = new NewTaskChange(now, sc.getUserId(), taskId, auditId);
                }
            } else {
                taskChange = new TaskUpdatedChange(now, sc.getUserId(), taskId, auditId);
            }
            AdapterManager.getInstance().getFilterNotifyAdapterManager().sendNotifyForTask(null, taskId, sc.getUserId(), null, taskChange);
        }
    }

    public String createAttachments(SessionContext sc, String taskId, String taskNumber, String desc, final List<AttachmentArray> atts) throws GranException {
        lockManager.getLock(taskId).lock();
        try {
            AdapterManager.getInstance().getSecuredAttachmentAdapterManager().createAttachment(sc, taskId, null, sc.getUserId(), atts, false);
            String convertedDesc = desc;
            for (AttachmentArray array : atts) {
                if (array.isTinyMCEImage()) {
                    convertedDesc = convertedDesc.replaceAll(array.getName().toString(), String.format(MacrosUtil.lyteImg, array.getContext(), taskNumber, array.getInitialID(), array.getInitialID(), array.getContext(), array.getInitialID()));
                }
            }
            return convertedDesc;
        } finally {
            lockManager.getLock(taskId).unlock();
        }
    }

    @Deprecated
    public String createTask(SessionContext sc, String categoryId, String shortname, String name, String description,
                             Long budget, Calendar deadline, String priorityId,
                             String parentId, String handlerUserId, String handlerGroupId, boolean sendMail, Map udf, String statusId, List<AttachmentArray> atts, Boolean copyOrMoveOps) throws GranException {
        Calendar time = new GregorianCalendar();
        time.setTimeInMillis(System.currentTimeMillis());
        SecuredTaskTriggerBean input = new SecuredTaskTriggerBean(parentId, description, name, shortname, time, time, null, null, budget, deadline, null, sc.getUserId(), handlerUserId, handlerUserId, handlerGroupId, parentId, categoryId, null, statusId, null, priorityId, udf, sc, atts, copyOrMoveOps);
        input.setNeedSend(sendMail);
        return createTask(input);
    }

    public List<Trigger> getAllTriggers() throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            return hu.getList("from com.trackstudio.model.Trigger t");
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }
}
