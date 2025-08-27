package com.trackstudio.kernel.cache;

import net.jcip.annotations.Immutable;

/**
 * Вспомогательный класс, используется для хренения и сравнения типов плагинов
 */
@Immutable
public class PluginType implements Comparable {

    /**
     * Плагин-скрипт для вычисляемого пользовательского поля для задачи
     */
    public static final PluginType TASK_CUSTOM_FIELD_VALUE = new PluginType("scripts/task_custom_field_value");
    /**
     * Плагин-скрипт для вычисляемого пользовательского поля с lookup для задачи
     */
    public static final PluginType TASK_CUSTOM_FIELD_LOOKUP = new PluginType("scripts/task_custom_field_lookup");
    /**
     * Плагин-скрипт для вычисляемого пользовательского поля для пользователя
     */
    public static final PluginType USER_CUSTOM_FIELD_VALUE = new PluginType("scripts/user_custom_field_value");
    /**
     * Плагин-скрипт для вычисляемого пользовательского поля c lookup для пользователя
     */
    public static final PluginType USER_CUSTOM_FIELD_LOOKUP = new PluginType("scripts/user_custom_field_lookup");
    /**
     * Плагин-скрипт для before-триггера при создании задачи
     */
    public static final PluginType BEFORE_CREATE_TASK = new PluginType("scripts/before_create_task");
    /**
     * Плагин-скрипт для instedof-триггера при создании задачи
     */
    public static final PluginType INSTEAD_OF_CREATE_TASK = new PluginType("scripts/instead_of_create_task");
    /**
     * Плагин-скрипт для after-триггера при создании задачи
     */
    public static final PluginType AFTER_CREATE_TASK = new PluginType("scripts/after_create_task");
    /**
     * Плагин-скрипт для before-триггера при редактировании задачи
     */
    public static final PluginType BEFORE_EDIT_TASK = new PluginType("scripts/before_edit_task");
    /**
     * Плагин-скрипт для instedof-триггера при редактировании задачи
     */
    public static final PluginType INSTEAD_OF_EDIT_TASK = new PluginType("scripts/instead_of_edit_task");
    /**
     * Плагин-скрипт для after-триггера при редактировании задачи
     */
    public static final PluginType AFTER_EDIT_TASK = new PluginType("scripts/after_edit_task");
    /**
     * Плагин-скрипт для before-триггера при создании сообщения
     */
    public static final PluginType BEFORE_ADD_MESSAGE = new PluginType("scripts/before_add_message");
    /**
     * Плагин-скрипт для instedof-триггера при создании сообщения
     */
    public static final PluginType INSTEAD_OF_ADD_MESSAGE = new PluginType("scripts/instead_of_add_message");
    /**
     * Плагин-скрипт для after-триггера при создании сообщения
     */
    public static final PluginType AFTER_ADD_MESSAGE = new PluginType("scripts/after_add_message");
    /**
     * Плагин-иконка
     */
    public static final PluginType ICON = new PluginType("icons/categories");
    /**
     * Плагин-web
     */
    public static final PluginType WEB = new PluginType("web");
    /**
     * Плагин-шаблон email
     */
    public static final PluginType EMAIL = new PluginType("e-mail");
    /**
     * Плагин для групповых операций над задачами
     */
    public static final PluginType BULK = new PluginType("scripts/bulk");

    /**
     * Плагин xslt скриптов для преобразования отчетов вида xml
     */
    public static final PluginType XSLT = new PluginType("xslt");

    public static final PluginType TXT = new PluginType("convert");

    public static final PluginType EMPTY = new PluginType("");

    public static final PluginType MULTI_BULK = new PluginType("scripts/multibulk");

    /**
     * This constrain is used for report's handler
     */
    public static final PluginType REPORT_ACTION = new PluginType("scripts/report");

    /**
     * This constrain is used for scheduler job
     */
    public static final PluginType SCHEDULER_JOB = new PluginType("scripts/scheduler");

    public static final PluginType MACROS = new PluginType("scripts/macros");

    public static final PluginType BEFORE_MAIL_IMPORT = new PluginType("scripts/before_mail_import");

    public static final PluginType INSTEAD_OF_MAIL_IMPORT = new PluginType("scripts/instead_of_mail_import");

    public static final PluginType AFTER_MAIL_IMPORT = new PluginType("scripts/after_mail_import");

    /**
     * Название типа плагина
     */
    private final String myName; // for debug only

    /**
     * Приватный конструктор по умолчанию
     *
     * @param name название типа плагина
     */
    private PluginType(String name) {
        myName = name;
    }

    /**
     * Возврашает строковое представление типа плагина
     *
     * @return строковое преставление типа плагина
     */
    public String toString() {
        return myName;
    }

    /**
     * Сравнивает два объекта текущего класса
     *
     * @param o Сравниваемый обхект
     * @return +1, 0 или -1
     */
    public int compareTo(Object o) {
        return toString().compareTo(o.toString());
    }
}
