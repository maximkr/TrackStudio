package com.trackstudio.constants;

/**
 * Интерфейс, описывающий константы для задач
 */
public interface TaskActions {

    String tasks = "tasks";
    String taskFields = "taskFields";
    String viewTaskPriority = "viewTaskPriority";
    String editTaskPriority = "editTaskPriority";
    String editTaskAlias = "editTaskAlias";
    String viewTaskDeadline = "viewTaskDeadline";
    String editTaskDeadline = "editTaskDeadline";
    String viewTaskBudget = "viewTaskBudget";
    String editTaskBudget = "editTaskBudget";
    String viewTaskActualBudget = "viewTaskActualBudget";
    String editTaskActualBudget = "editTaskActualBudget";
    String viewTaskSubmitDate = "viewTaskSubmitDate";
    String viewTaskLastUpdated = "viewTaskLastUpdated";
    String editTaskHandler = "editTaskHandler";
    String viewTaskCloseDate = "viewTaskCloseDate";
    String viewTaskResolution = "viewTaskResolution";
    String viewTaskDescription = "viewTaskDescription";
    String editTaskDescription = "editTaskDescription";

    String viewFilters = "viewFilters";
    String manageTaskPrivateFilters = "manageTaskPrivateFilters";
    String manageTaskPublicFilters = "manageTaskPublicFilters";

    String manageEmailSchedules = "manageEmailSchedules";

    String bulkProcessingTask = "bulkProcessingTask";
    String cutCopyPasteTask = "cutCopyPasteTask";

    String viewTaskAttachments = "viewTaskAttachments";
    String createTaskAttachments = "createTaskAttachments";
    String manageTaskAttachments = "manageTaskAttachments";
    String createTaskMessageAttachments = "createTaskMessageAttachments";
    String manageTaskMessageAttachments = "manageTaskMessageAttachments";

    String manageRegistrations = "manageRegistrations";
    String manageTaskUDFs = "manageTaskUDFs";
    String manageEmailImportRules = "manageEmailImportRules";
    String manageTaskTemplates = "manageTaskTemplates";
    String manageTaskACLs = "manageTaskACLs";

    String viewReports = "viewReports";
    String managePrivateReports = "managePrivateReports";
    String managePublicReports = "managePublicReports";

    String manageCategories = "manageCategories";
    String manageWorkflows = "manageWorkflows";
    String deleteOperations = "deleteOperations";

    String deleteTheirTaskAttachment = "deleteTheirTaskAttachment";
    String deleteTheirMessageAttachment = "deleteTheirMessageAttachment";
    String viewScriptsBrowser = "viewScriptsBrowser";
    String viewTemplatesBrowser = "viewTemplatesBrowser";
    String showView = "showView";
    String showOtherFilterTab = "showOtherFilterTab";
    String canCreateTaskByOperation = "canCreateTaskByOperation";
    String canUsePostFiltration = "canUsePostFiltration";
    String canArchive = "canArchive";
    String canDeleteArchive = "canDeleteArchive";

    String[] actions = new String[]{
            viewFilters,
            manageTaskPrivateFilters,
            manageTaskPublicFilters,

            manageEmailSchedules,

            cutCopyPasteTask,
            viewTaskPriority,
            editTaskPriority,
            editTaskAlias,
            viewTaskDeadline,
            editTaskDeadline,
            viewTaskBudget,
            editTaskBudget,
            viewTaskActualBudget,
            editTaskActualBudget,
            viewTaskSubmitDate,
            viewTaskLastUpdated,
            editTaskHandler,
            viewTaskCloseDate,
            viewTaskResolution,
            viewTaskDescription,
            editTaskDescription,
            viewTaskAttachments,
            createTaskAttachments,
            manageTaskAttachments,
            createTaskMessageAttachments,
            manageTaskMessageAttachments,
            manageTaskUDFs,
            manageEmailImportRules,
            manageTaskTemplates,
            manageTaskACLs,

            viewReports,
            managePrivateReports,
            managePublicReports,
            manageCategories,
            manageWorkflows,

            deleteOperations,
            manageRegistrations,
            deleteTheirTaskAttachment,
            deleteTheirMessageAttachment,
            viewScriptsBrowser,
            viewTemplatesBrowser,
            showView,
            showOtherFilterTab,
            canCreateTaskByOperation,
            canUsePostFiltration,
            canArchive,
            canDeleteArchive
    };

    String[] unOverridedActions = new String[]{
            viewFilters,
            manageTaskPrivateFilters,
            manageTaskPublicFilters,

            manageEmailSchedules,
            managePrivateReports,
            managePublicReports,
            viewReports
    };
}
