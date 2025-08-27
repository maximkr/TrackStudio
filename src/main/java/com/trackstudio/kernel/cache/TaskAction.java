package com.trackstudio.kernel.cache;

import net.jcip.annotations.Immutable;

/**
 * Класс TaskAction описывет действия в системе, которые может делать пользователя над задачами
 */
@Immutable
public class TaskAction extends Action {

    /**
     * Конструктор по умолчанию
     *
     * @param name название действия
     */
    public TaskAction(String name) {
        super(name);
    }
}