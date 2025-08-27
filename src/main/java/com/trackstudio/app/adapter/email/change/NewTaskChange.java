package com.trackstudio.app.adapter.email.change;

import java.util.Calendar;
import java.util.List;

import com.trackstudio.constants.FilterConstants;
import com.trackstudio.exception.GranException;
import com.trackstudio.secured.SecuredAttachmentBean;
import com.trackstudio.secured.SecuredTaskBean;

import net.jcip.annotations.Immutable;

/**
 * Изменение, при добавлении новой задачи
 */
@Immutable
public class NewTaskChange extends Change {

    private final String task;

    /**
     * Конструктор
     *
     * @param when дата изменения
     * @param by   ID пользователя
     * @param task ID задачи
     * @param code код
     */
    public NewTaskChange(Calendar when, String by, String task, String code, final String auditId) {
        super(when, by, code, auditId);
        this.task = task;
    }

    /**
     * Конструктор
     *
     * @param when дата изменения
     * @param by   ID пользователя
     * @param task ID задачи
     */
    public NewTaskChange(Calendar when, String by, String task, final String auditId) {
        super(when, by, FilterConstants.FIRE_NEW_TASK, auditId);
        this.task = task;
    }

    /**
     * Возвращает задачу
     *
     * @return задача
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredTaskBean
     */
    public SecuredTaskBean getTask() throws GranException {
        if (getSession() != null)
            return new SecuredTaskBean(task, getSession());
        else
            return null;
    }

    @Override
    public List<SecuredAttachmentBean> getAttachments() throws GranException {
        return null;
    }
}