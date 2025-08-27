package com.trackstudio.constants;

/**
 * Интерфейс, описывающий константы для категорий
 */
public interface CategoryConstants {
    /**
     * Создавать могу все
     */
    String CREATE_ALL = "C";
    /**
     * создавать может только ответственный
     */
    String CREATE_HANDLER = "CREATE_HANDLER";
    /**
     * Создавать может только автор
     */
    String CREATE_SUBMITTER = "CREATE_SUBMITTER";
    /**
     * Создавать может и автор и ответственный
     */
    String CREATE_SUBMITTER_AND_HANDLER = "CREATE_SH";
    /**
     * Редактировать могут все
     */
    String EDIT_ALL = "E";
    /**
     * Редактировать может только ответственный
     */
    String EDIT_HANDLER = "EDIT_HANDLER";
    /**
     * Редактировать может только автор
     */
    String EDIT_SUBMITTER = "EDIT_SUBMITTER";
    /**
     * Редактировать могут и автор и ответственный
     */
    String EDIT_SUBMITTER_AND_HANDLER = "EDIT_SH";
    /**
     * Удалять могут все
     */
    String DELETE_ALL = "D";
    /**
     * Удалять может ответственный
     */
    String DELETE_HANDLER = "DELETE_HANDLER";
    /**
     * Удалять может автор
     */
    String DELETE_SUBMITTER = "DELETE_SUBMITTER";
    /**
     * Удалять может и автор и ответственный
     */
    String DELETE_SUBMITTER_AND_HANDLER = "DELETE_SH";
    /**
     * ВСе могут быть ответственными
     */
    String BE_HANDLER_ALL = "H";
    /**
     * Ответственный может быть только ответственный
     */
    String BE_HANDLER_HANDLER = "BE_HANDLER_HANDLER";
    /**
     * Ответственным может быть только автор
     */
    String BE_HANDLER_SUBMITTER = "BE_HANDLER_SUBMITTER";
    /**
     * Ответственным может быть и автор и ответственный
     */
    String BE_HANDLER_SUBMITTER_AND_HANDLER = "BE_HANDLER_SH";
    /**
     * Смотреть могут все
     */
    String VIEW_ALL = "V";
    /**
     * Смотреть может только автор
     */
    String VIEW_SUBMITTER = "VIEW_SUBMITTER";
    /**
     * Прав нет
     */
    String NONE = "";
    /**
     * Формат года для бюджета
     */
    String Y = "Y";
    /**
     * Формат месяца для бюджета
     */
    String M = "M";
    /**
     * Формат недели для бюджета
     */
    String W = "W";
    /**
     * Формат дня для бюджета
     */
    String D = "D";
    /**
     * Формат минуты для бюджета
     */
    String m = "m";
    /**
     * Формат часа для бюджета
     */
    String h = "h";
    /**
     * Формат секунды для бюджета
     */
    String s = "s";
}
