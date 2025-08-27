package com.trackstudio.constants;

/**
 * Интерфейс, описывающий константы для пользовательских полей
 */
public interface UdfConstants {
    int STRING = 0;
    int FLOAT = 1;
    int DATE = 2;
    int LIST = 3;
    int MLIST = 6;
    int INTEGER = 4;
    int MEMO = 5;
    int TASK = 7;
    int USER = 8;
    int URL = 9;

    int UDFSOURCE = 0;
    int TASK_ALL = 1;
    int USER_ALL = 2;
    int WORKFLOW_ALL = 5;

    String EDIT_ALL = "E";
    String EDIT_HANDLER = "EDIT_HANDLER";
    String EDIT_SUBMITTER = "EDIT_SUBMITTER";
    String EDIT_SUBMITTER_AND_HANDLER = "EDIT_SH";

    String VIEW_ALL = "V";
    String VIEW_SUBMITTER = "VIEW_SUBMITTER";
    String VIEW_HANDLER = "VIEW_HANDLER";
    String VIEW_SUBMITTER_AND_HANDLER = "VIEW_SH";
    //    String NONE = "";
    /**
     * Означает, что UDF можно видеть в задаче при любых ее состояниях
     */
    String STATUS_VIEW_ALL = "STATUS_VIEW_ALL";
    /**
     * Означает, что UDF можно редактировать в задаче при любых ее состояниях
     */
    String STATUS_EDIT_ALL = "STATUS_EDIT_ALL";

    String MSTATUS_VIEW_ALL = "V";
    String MSTATUS_EDIT_ALL = "E";
    String SPLIT_SEPARATOR = "[;,:* ]";
}
