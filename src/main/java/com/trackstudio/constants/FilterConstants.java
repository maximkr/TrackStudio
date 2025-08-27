package com.trackstudio.constants;

/**
 * Интерфейс, описывающий константы для фильтров
 */
public interface FilterConstants {
    /**
     * Фильтр для задачи
     */
    Integer TASK_FILTER = 0;
    /**
     * Фильтр для пользователя
     */
    Integer USER_FILTER = 1;
    /**
     * ID фильтра для пользовате5лей по умолчанию
     */
    String DEFAULT_TASK_FILTER_ID = "1";
    /**
     * ID фильтра для задач по умолчанию
     */
    String DEFAULT_USER_FILTER_ID = "0";
    /**
     * Действие - добавлена новая задача
     */
    String FIRE_NEW_TASK = "N";
    /**
     * Действие - добавлена новая задача с прилоежнным файлом
     */
    String FIRE_NEW_TASK_WITH_ATTACHMENT = "NA";
    /**
     * Действие - добавлено новое сообщение
     */
    String FIRE_NEW_MESSAGE = "M";
    /**
     * Действие - добавлено новое сообщение с приложенным файлом
     */
    String FIRE_NEW_MESSAGE_WITH_ATTACHMENT = "MA";
    /**
     * Действие - добавлен новый приложенный файл
     */
    String FIRE_NEW_ATTACHMENT = "A";
    /**
     * Действие - изменена задача
     */
    String FIRE_UPDATED_TASK = "U";
    /**
     * Действие - рассылка подписки по расписанию
     */
    String FIRE_ON_TIME = "S"; //Subscription
    /**
     * Действие - проверка
     */
    String FIRE_ON_TEST = "T"; //Test

    String NOT_I = "I"; //Test
}
