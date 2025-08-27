package com.trackstudio.app.adapter.email.change;

import java.util.Calendar;

import com.trackstudio.constants.FilterConstants;

import net.jcip.annotations.Immutable;

/**
 * Изменение, при редактировании задачи
 */
@Immutable
public class TaskUpdatedChange extends NewTaskChange {

    /**
     * Конструктор
     *
     * @param when дата изменения
     * @param by   ID пользователя
     * @param task ID задачи
     */
    public TaskUpdatedChange(Calendar when, String by, String task, final String auditId) {
        super(when, by, task, FilterConstants.FIRE_UPDATED_TASK, auditId);
    }
}