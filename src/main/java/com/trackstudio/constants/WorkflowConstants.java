package com.trackstudio.constants;

/**
 * Интерфейс, описывающий константы для процессов
 */
public interface WorkflowConstants {

    String NONE = "";

    String VIEW_ALL = "V";
    String VIEW_HANDLER = "VIEW_HANDLER";
    String VIEW_SUBMITTER = "VIEW_SUBMITTER";
    String VIEW_SUBMITTER_AND_HANDLER = "VIEW_SH";

    String PROCESS_ALL = "A";
    String PROCESS_HANDLER = "PROCESS_HANDLER";
    String PROCESS_SUBMITTER = "PROCESS_SUBMITTER";
    String PROCESS_SUBMITTER_AND_HANDLER = "PROCESS_SH";

    String BE_HANDLER_ALL = "B";
    String BE_HANDLER_HANDLER = "BE_HANDLER_HANDLER";
    String BE_HANDLER_SUBMITTER = "BE_HANDLER_SUBMITTER";
    String BE_HANDLER_SUBMITTER_AND_HANDLER = "BE_HANDLER_SH";

    String MAIL_ALL = "M";
    String MAIL_HANDLER = "MAIL_HANDLER";
    String MAIL_SUBMITTER = "MAIL_SUBMITTER";
    String MAIL_SUBMITTER_AND_HANDLER = "MAIL_SH";

    String MSTATUS_VIEW_PREFIX = "MSTATUS_VIEW_";
    String MSTATUS_EDIT_PREFIX = "MSTATUS_EDIT_";
}
