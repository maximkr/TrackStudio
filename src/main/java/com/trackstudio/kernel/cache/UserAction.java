package com.trackstudio.kernel.cache;

import net.jcip.annotations.Immutable;

/**
 * Класс UserAction описывет действия в системе, которые может делать пользователя над пользователями
 */
@Immutable
public class UserAction extends Action {

    /**
     * Конструктор по умолчанию
     *
     * @param name название действия
     */
    public UserAction(String name) {
        super(name);
    }
}
