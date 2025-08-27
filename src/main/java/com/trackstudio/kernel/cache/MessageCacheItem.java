package com.trackstudio.kernel.cache;

import java.io.Serializable;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicReference;

import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.tools.Intern;
import com.trackstudio.tools.textfilter.HTMLEncoder;

import net.jcip.annotations.Immutable;

/**
 * Вспомогательный класс, используется работы с сообщениями.
 * Вопреки названию, никакого кеша сообщений сейчас нет.
 */
@Immutable
public class MessageCacheItem implements Serializable {
    private final String id;
    private final AtomicReference<String> description;
    private final Long time;
    private final Long hrs;
    private final Long deadline;
    private final Long budget;
    private final String taskId;
    private final String submitterId;
    private final String resolutionId;
    private final String priorityId;
    private final String handlerId;
    private final String handlerUserId;
    private final String handlerGroupId;
    private final String mstatusId;
    private final String longtextId;

    private final AtomicReference<String> textDescription;

    /**
     * Конструктор по умолчанию
     *
     * @param id             ID сообщения
     * @param description    Текст сообщения
     * @param time           Дата/время сообщения
     * @param hrs            Потраченное время
     * @param deadline       Дедлайн
     * @param budget         Бюджет
     * @param taskId         ID задачи, для которой создано сообщений
     * @param submitterId    ID автора сообщения
     * @param resolutionId   ID резолюции
     * @param priorityId     ID приоритета
     * @param handlerId      ID ответственного
     * @param handlerUserId  ID ответственного пользователя
     * @param handlerGroupId ID ответственного статуса
     * @param mstatusId      ID статуса сообщения
     * @param longtextId     ID объекта longtext, который используетс яесли описание сообщения больше 2к символов
     * @throws GranException при необходимости
     *                       <p/>
     *                       Created from hibernate query in TaskRelatedManager.findMessage
     */
    public MessageCacheItem(String id, String description, Calendar time, Long hrs, Calendar deadline, Long budget, String taskId, String submitterId, String resolutionId, String priorityId, String handlerId, String handlerUserId, String handlerGroupId, String mstatusId, String longtextId) throws GranException {
        this.id = id;
        this.description = new AtomicReference<String>(Intern.process(description));
        this.textDescription = new AtomicReference(null);
        if (time!=null)
            this.time = time.getTimeInMillis();
        else
            this.time = null;

        this.hrs = hrs;

        if (deadline!=null)
            this.deadline = deadline.getTimeInMillis();
        else
            this.deadline = null;

        this.budget = budget;
        this.taskId = taskId;
        this.submitterId = submitterId;
        this.resolutionId = resolutionId;
        this.priorityId = priorityId;
        this.handlerId = handlerId;
        this.handlerUserId = handlerUserId;
        this.handlerGroupId = handlerGroupId;
        this.mstatusId = mstatusId;
        this.longtextId = longtextId;
    }

    /**
     * Возвращает текст сообещния
     *
     * @return текст сообщения
     * @throws GranException при необходимости
     */
    public String getDescription() throws GranException {
        String current = description.get();
        if (current != null)
            return current;

        if (longtextId==null)
            return "";

        String newValue = KernelManager.getLongText().getLongtext(longtextId);
        if (description.compareAndSet(null, newValue))
            return newValue;
        else
            return description.get();
    }

    /**
     * Возвращает ID ответственного пользователя
     *
     * @return ID пользователя
     */
    public String getHandlerUserId() {
        return handlerUserId;
    }

    /**
     * Возвращает ID jndtncndtyyjuj cnfnecf
     *
     * @return ID ответственного статуса
     */
    public String getHandlerGroupId() {
        return handlerGroupId;
    }

    /**
     * Возвращает "чистый" текст сообщения, без html-тегов
     *
     * @return текст сообщения
     * @throws GranException при необходимости
     */
    public String getTextDescription() throws GranException {
        String current = textDescription.get();
        if (current != null)
            return current;

        // we have empty text, nothing to convert
        if (getDescription() == null)
            return null;

        String newValue = HTMLEncoder.stripHtmlTags(HTMLEncoder.br2nl(getDescription()));

        if (textDescription.compareAndSet(null, newValue))
            return newValue;
        else
            return textDescription.get();
    }

    /**
     * Возвращает потраченное время
     *
     * @return потраченное время
     */
    public Long getHours() {
        return this.hrs;
    }

    /**
     * Возвращает ответственного
     *
     * @return ответственный пользователь
     * @throws GranException при необходимости
     * @see com.trackstudio.kernel.cache.UserRelatedInfo
     */
    public UserRelatedInfo getHandler() throws GranException {
        if (handlerId != null)
            return UserRelatedManager.getInstance().find(handlerId);
        else
            return null;
    }

    /**
     * Возвращает дату/время создания сообщения
     *
     * @return дата/время создания сообщения
     */
    public Calendar getDate() {
        if (time==null)
            return null;

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        return cal;
    }


    /**
     * Возвращает автора сообщения
     *
     * @return автор сообщения
     * @throws GranException при необходимости
     * @see com.trackstudio.kernel.cache.UserRelatedInfo
     */
    public UserRelatedInfo getSubmitter() throws GranException {
        return UserRelatedManager.getInstance().find(submitterId);
    }

    /**
     * Возвращает ID сообщения
     *
     * @return ID сообщения
     */
    public String getId() {
        return id;
    }

    /**
     * Возвращает дату/время создания сообщения
     *
     * @return дата/время
     */
    public Calendar getTime() {
        if (time==null)
            return null;

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        return cal;
    }

    /**
     * Возвращает потраченное время
     *
     * @return потраченное время
     */
    public Long getHrs() {
        return hrs;
    }

    /**
     * Возвращает дату/время дедлайна
     *
     * @return дата/время
     */
    public Calendar getDeadline() {
        Calendar cal = null;
        if (deadline != null) {
            cal = Calendar.getInstance();
            cal.setTimeInMillis(deadline);
        }
        return cal;
    }

    /**
     * Возвращает бюджет
     *
     * @return бюджет
     */
    public Long getBudget() {
        return budget;
    }

    /**
     * Возвращает ID задачи
     *
     * @return ID задачи
     */
    public String getTaskId() {
        return taskId;
    }

    /**
     * Возвращает ID автора сообщения
     *
     * @return ID автора сообщения
     */
    public String getSubmitterId() {
        return submitterId;
    }

    /**
     * Возвращает ID резолюции
     *
     * @return ID резолюции
     */
    public String getResolutionId() {
        return resolutionId;
    }

    /**
     * Возвращает ID приоритета
     *
     * @return ID приоритета
     */
    public String getPriorityId() {
        return priorityId;
    }

    /**
     * Возвращает ID ответственного
     *
     * @return ID ответственного
     */
    public String getHandlerId() {
        return handlerId;
    }

    /**
     * Возвращает ID типа сообщения
     *
     * @return ID типа сообщения
     */
    public String getMstatusId() {
        return mstatusId;
    }

    @Override
    public String toString() {
        return "MessageCacheItem{" +
                "id='" + id + '\'' +
                ", description=" + description +
                ", time=" + time +
                ", hrs=" + hrs +
                ", deadline=" + deadline +
                ", budget=" + budget +
                ", taskId='" + taskId + '\'' +
                ", submitterId='" + submitterId + '\'' +
                ", resolutionId='" + resolutionId + '\'' +
                ", priorityId='" + priorityId + '\'' +
                ", handlerId='" + handlerId + '\'' +
                ", handlerUserId='" + handlerUserId + '\'' +
                ", handlerGroupId='" + handlerGroupId + '\'' +
                ", mstatusId='" + mstatusId + '\'' +
                ", longtextId='" + longtextId + '\'' +
                ", textDescription=" + textDescription +
                '}';
    }
}