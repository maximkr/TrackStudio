package com.trackstudio.app;

/**
 * Интерфейс содержит константы с заголовками страниц в ТС
 */
public interface PageNaming {

    /**
     * Страница списка фильтров
     */
    String TAB_TASK_FILTER_LIST = "TASK_FILTERS_LIST";
    /**
     * Страница свойств фильтра
     */
    String TAB_TASK_FILTER_PROPERTIES = "TASK_FILTER_PROPERTIES";
    /**
     * Страница свойств параметров фильтрации задач фильтра
     */
    String TAB_TASK_FILTER_TASK_PARAMETERS = "TASK_FILTER_TASK_PARAMETERS";
    /**
     * Страница свойств параметров фильтрации сообщений фильтров
     */
    String TAB_TASK_FILTER_MESSAGE_PARAMETERS = "FILTER_MESSAGE_PARAMETERS";
    /**
     * Страница списка шаблонов
     */
    String TAB_TASK_TEMPLATE_LIST = "TEMPLATES_LIST";
    /**
     * Страница свойств фильтров пользователей
     */
    String TAB_USER_FILTER_PROPERTIES = "USER_FILTER_PROPERTIES";
    /**
     * Страница свойств параметров фильтрации пользователей списка пользователей
     */
    String TAB_USER_FILTER_USER_PARAMETERS = "USER_FILTER_USER_PARAMETERS";
    /**
     * Страница списка отчетов
     */
    String TAB_TASK_REPORT_LIST = "REPORTS_LIST";
    /**
     * Страница просмотра общей онформации по отчеты
     */
    String TAB_TASK_REPORT_OVERVIEW = "REPORT_OVERVIEW";
    /**
     * Страница настроек отчета
     */
    String TAB_TASK_REPORT_PROPERTIES = "REPORT_PROPERTIES";
    /**
     * Страница списка подписок
     */
    String TAB_TASK_EMAIL_SUBSCRIPTION_LIST = "SUBSCRIPTIONS_LIST";
    /**
     * Страница настроек подписки
     */
    String TAB_TASK_EMAIL_SUBSCRIPTION_PROPERTIES = "SUBSCRIPTION_PROPERTIES";
    /**
     * Страница списка уведомлений
     */
    String TAB_TASK_EMAIL_NOTIFICATION_LIST = "NOTIFICATIONS_LIST";
    /**
     * Страница настроек уведомлений
     */
    String TAB_TASK_EMAIL_NOTIFICATION_PROPERTIES = "NOTIFICATION_PROPERTIES";
    /**
     * Страница прав доступа к задачам
     */
    String TAB_TASK_ASSIGNED_STATUSES = "ASSIGNED_PRSTATUSES_TASK";
    /**
     * Страница эффективных статусов для задач
     */
    String TAB_TASK_EFFECTIVE_STATUSES = "TASK_EFFECTIVE_PRSTATUSES";
    /**
     * Страница эффективных статусов для пользователей
     */
    String TAB_USER_EFFECTIVE_STATUSES = "USER_EFFECTIVE_PRSTATUSES";
    /**
     * Страница списка задач
     */
    String TAB_TASK_LIST = "TASKS_LIST";
    /**
     * Страница просмотра задачи
     */
    String TAB_TASK_OVERVIEW = "TASK_OVERVIEW";
    /**
     * Страница редактирования задачи
     */
    String TASK_EDIT = "TASK_EDIT";
    /**
     * Страница списка пользовательских полей для задачи
     */
    String TAB_TASK_CUSTOM_FIELD_LIST = "CUSTOM_FIELDS_LIST_TASK";
    /**
     * Страница настроек пользовательского поля для задачи
     */
    String TAB_TASK_CUSTOM_FIELD_PROPERTIES = "CUSTOM_FIELD_PROPERTIES_TASK";
    /**
     * Страница значений пользовательского поля для задачи
     */
    String TAB_TASK_CUSTOM_FIELD_VALUES_LIST = "TASK_CUSTOM_FIELD_VALUES_LIST";
    /**
     * Страница сводной информации по пользовательскому полю для задачи
     */
    String TAB_TASK_CUSTOM_FIELD_OVERVIEW = "CUSTOM_FIELD_OVERVIEW_TASK";
    /**
     * Страница настройки прад для пользовательского поля
     */
    String TAB_TASK_CUSTOM_FIELD_PERMISSIONS = "CUSTOM_FIELD_PERMISSIONS_TASK";
    /**
     * Страница списка правил импорта
     */
    String TAB_TASK_EMAIL_IMPORT_LIST = "EMAIL_IMPORT_LIST";
    /**
     * Страница просмотра сводной информаци по шаблону
     */
    String TAB_TASK_TEMPLATE_OVERVIEW = "TEMPLATE_OVERVIEW";
    /**
     * Страница редактирования шаблона
     */
    String TAB_TASK_TEMPLATE_PROPERTIES = "TEMPLATE_EDIT_PAGE";
    /**
     * Страница списка пользователей
     */
    String TAB_USER_LIST = "USERS_LIST";
    /**
     * Страница просмотра настроек пользователя
     */
    String TAB_USER_PROPERTIES = "USER_PROPERTIES";
    /**
     * Страница изменения пароля пользователя
     */
    String TAB_USER_CHANGE_PASSWORD = "CHANGE_PASSWORD";
    /**
     * Страница просмотра пользователя
     */
    String TAB_USER_OVERVIEW = "USER_OVERVIEW";
    /**
     * Страница списка пошльзовательских полей для пользователя
     */
    String TAB_USER_CUSTOM_FIELD_LIST = "USER_CUSTOM_FIELDS_LIST";
    /**
     * страница списка значений пользовательского поля для пользователя
     */
    String TAB_USER_CUSTOM_FIELD_VALUES_LIST = "USER_CUSTOM_FIELD_VALUES_LIST";
    /**
     * СТраница просмотра настроек пользовательского поля для пользователя
     */
    String TAB_USER_CUSTOM_FIELD_PROPERTIES = "USER_CUSTOM_FIELD_PROPERTIES";
    /**
     * Страница просмотра сводной информации по пользовательскому полю
     */
    String TAB_USER_CUSTOM_FIELD_OVERVIEW = "USER_CUSTOM_FIELD_OVERVIEW";
    /**
     * Страница настроек прав доступа для пользовательского поля пользователя
     */
    String TAB_USER_CUSTOM_FIELD_PERMISSIONS = "USER_CUSTOM_FIELD_PERMISSIONS";
    /**
     * Страница настроек статуса
     */
    String TAB_USER_STATUS_PROPERTIES = "PRSTATUS_PROPERTIES";
    /**
     * Страница просмотра сводной информации по статусу
     */
    String TAB_USER_STATUS_OVERVIEW = "PRSTATUS_OVERVIEW";
    /**
     * Страница списка статусов
     */
    String TAB_USER_STATUS_LIST = "PRSTATUSES_LIST";
    /**
     * Страница настроек категории
     */
    String TAB_TASK_CATEGORY_PROPERTIES = "CATEGORY_PROPERTIES";
    /**
     * Страница шаблона категории
     */
    String TAB_TASK_CATEGORY_TEMPLATE = "CATEGORY_TEMPLATE";
    /**
     * Страница просмотра сводной информации по категории
     */
    String TAB_TASK_CATEGORY_OVERVIEW = "CATEGORY_OVERVIEW";
    /**
     * Страница настройки отношений между категориями
     */
    String TAB_TASK_CATEGORY_RELATIONS = "CATEGORY_RELATIONS";
    /**
     * Страница настройки прав доступа к категориям
     */
    String TAB_TASK_CATEGORY_PERMISSIONS = "CATEGORY_PERMISSIONS";
    /**
     * Страница триггеров к категориям
     */
    String TAB_TASK_CATEGORY_TRIGGERS = "CATEGORY_TRIGGERS";
    /**
     * Страница списка категорий
     */
    String TAB_TASK_CATEGORY_LIST = "CATEGORIES_LIST";
    /**
     * Страница настроек процесса
     */
    String TAB_TASK_WORKFLOW_PROPERTIES = "WORKFLOW_PROPERTIES";
    /**
     * Страница списка процессов
     */
    String TAB_TASK_WORKFLOW_LIST = "WORKFLOWS_LIST";
    /**
     * Страница просмотра сводной информации по процессу
     */
    String TAB_TASK_WORKFLOW_OVERVIEW = "WORKFLOW_OVERVIEW";
    /**
     * Страница редактирования приоритета
     */
    String TAB_TASK_WORKFLOW_PRIORITY_PROPERTIES = "PRIORITY_EDIT";
    /**
     * Страница списка резолюций
     */
    String TAB_TASK_WORKFLOW_RESOLUTION_LIST = "RESOLUTIONS_LIST";
    /**
     * Страница списка состояний
     */
    String TAB_TASK_WORKFLOW_STATE_LIST = "STATES_LIST";
    /**
     * Страница настройки состояний
     */
    String TAB_TASK_WORKFLOW_STATE_PROPERTIES = "STATE_PROPERTIES";
    /**
     * Страница списка переходов
     */
    String TAB_TASK_WORKFLOW_TRANSITION_LIST = "TRANSITIONS_LIST";
    /**
     * Страница списка приоритетов
     */
    String TAB_TASK_WORKFLOW_PRIORITY_LIST = "PRIORITIES_LIST";
    /**
     * Страница списка пользовательских полей для процесса
     */
    String TAB_TASK_WORKFLOW_CUSTOM_FIELD_LIST = "WORKFLOW_CUSTOM_FIELDS_LIST";
    /**
     * Страница свойств пользовательских полей для процесса
     */
    String TAB_TASK_WORKFLOW_CUSTOM_FIELD_PROPERTIES = "WORKFLOW_CUSTOM_FIELD_PROPERTIES";
    /**
     * Страница просмотра значений пользовательского поля для процесса
     */
    String TAB_TASK_WORKFLOW_CUSTOM_FIELD_VALUES_LIST = "WORKFLOW_CUSTOM_FIELD_VALUES_LIST";
    /**
     * Страница просмотра сводной информации по пользовательскому полю для процесса
     */
    String TAB_TASK_WORKFLOW_CUSTOM_FIELD_OVERVIEW = "WORKFLOW_CUSTOM_FIELD_OVERVIEW";
    /**
     * Страница настройки прав доступа к пользовательским поля процесса
     */
    String TAB_TASK_WORKFLOW_CUSTOM_FIELD_PERMISSIONS = "WORKFLOW_CUSTOM_FIELD_PERMISSIONS";
    /**
     * Страница списка типов сообщений
     */
    String TAB_TASK_WORKFLOW_MSTATUS_LIST = "MESSAGE_TYPES_LIST";
    /**
     * Страница настройки прав доступа к типу сообщения
     */
    String TAB_TASK_WORKFLOW_MSTATUS_PERMISSIONS = "MESSAGE_TYPE_PERMISSIONS";
    /**
     * Страница триггеров для типа сообщений
     */
    String TAB_TASK_WORKFLOW_MSTATUS_TRIGGERS = "MESSAGE_TYPE_TRIGGERS";
    /**
     * Страница настроек типа сооб9шения
     */
    String TAB_TASK_WORKFLOW_MSTATUS_PROPERTIES = "MESSAGE_TYPE_PROPERTIES";
    /**
     * Страница просмотра сводной информации по типу сообщения
     */
    String TAB_TASK_WORKFLOW_MSTATUS_OVERVIEW = "MESSAGE_TYPE_OVERVIEW";
    /**
     * Страница редактирования резолюции
     */
    String TAB_TASK_WORKFLOW_MSTATUS_RESOLUTION_PROPERTIES = "RESOLUTION_EDIT";
    /**
     * Страница просмотра прав доступа для пользщовательских полей типа сообщения
     */
    String TAB_TASK_WORKFLOW_MSTATUS_UDF_PERMISSIONS = "MESSAGE_TYPE_CUSTOM_FIELDS_PERMISSIONS";
    /**
     * Страница редактирования прав доступа для пользщовательских полей типа сообщения
     */
    String TAB_TASK_WORKFLOW_MSTATUS_UDF_PERMISSIONS_EDIT = "MESSAGE_TYPE_CUSTOM_FIELDS_PERMISSIONS";
    /**
     * Страница просмотра прав доступа для пользщовательских полей типа сообщения
     */
    String TAB_TASK_WORKFLOW_MSTATUS_UDF_PERMISSIONS_VIEW = "MESSAGE_TYPE_CUSTOM_FIELDS_PERMISSIONS";
    /**
     * Страница со списком регистраций
     */
    String TAB_USER_REGISTRATION_LIST = "REGISTRATIONS";
    /**
     * Страница просмотра настроек регистрации
     */
    String TAB_USER_REGISTRATION_PROPERTIES = "REGISTRATION_PROPERTIES";
    /**
     * СТраница просмотра сводной информации по регистрации
     */
    String TAB_USER_REGISTRATION_OVERVIEW = "REGISTRATION_OVERVIEW";
    /**
     * Страница апплета
     */
    String TAB_TASK_UPLOAD_APPLET = "UPLOAD_APPLET";
    /**
     * Страница настроек прав доступа статуса к полям пользователя
     */
    String TAB_USER_STATUS_USER_FIELD_SECURITY = "PRSTATUS_USER_FIELDS_PERMISSIONS";
    /**
     * Страница настроек прав доступа статуса к полям задачи
     */
    String TAB_USER_STATUS_TASK_FIELD_SECURITY = "PRSTATUS_TASK_FIELDS_PERMISSIONS";
    /**
     * Страница настроек прав доступа статуса к настройкам категорий
     */
    String TAB_USER_STATUS_CATEGORY_SECURITY = "PRSTATUS_CATEGORIES_PERMISSIONS";
    /**
     * Страница настроек прав доступа статуса к настройкам задач
     */
    String TAB_USER_STATUS_TASK_SECURITY = "PRSTATUS_TASK_ACTIONS_PERMISSIONS";
    /**
     * Страница настроек прав доступа статуса к настройкам пользователей
     */
    String TAB_USER_STATUS_USER_SECURITY = "PRSTATUS_USER_ACTIONS_PERMISSIONS";
    /**
     * Страница настроек прав доступа статуса к настройкам типов сообщений
     */
    String TAB_USER_MTYPE_USER_SECURITY = "USER_MESSAGE_TYPE_PERMISSIONS";
    /**
     * Страница настроек прав доступа статуса к настройкам процессов
     */
    String TAB_USER_STATUS_WORKFLOW_LIST = "PRSTATUS_WORKFLOWS_LIST";
    /**
     * Страница настроек прав доступа статуса к настройкам процессов
     */
    String TAB_USER_WORKFLOW_OVERVIEW = "USER_WORKFLOW_OVERVIEW";
}
