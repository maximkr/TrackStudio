package com.trackstudio.app.filter;

/**
 * Общие константы для фильтров задач и пользователей.
 * Используется для устранения дублирования между ListCustomizer и TaskFilterViewAction.
 */
public final class FilterConstants {

    /** Псевдо-значение для текущего пользователя */
    public static final String CURRENT_USER_ID = "CurrentUserID";

    /** Псевдо-значение для "Я и подчинённые" */
    public static final String I_AND_SUB_USERS = "IandSubUsers";

    /** Псевдо-значение для "Я и менеджер" */
    public static final String I_AND_MANAGER = "IandManager";

    /** Псевдо-значение для "Я и менеджеры" */
    public static final String I_AND_MANAGERS = "IandManagers";

    /** Префикс для групп пользователей */
    public static final String GROUP_PREFIX = "GROUP_";

    /** Ключ для значения "нет" (null) */
    public static final String NONE_KEY = "null";

    private FilterConstants() {
        // Utility class
    }
}
