package com.trackstudio.form;

public class RoleTaskSecurityForm extends PrstatusForm {

    public boolean manageRegistrations;
    public boolean cutCopyPasteTask;
    public boolean bulkProcessingTask;
    public boolean viewTaskAttachments;
    public boolean createTaskAttachments;
    public boolean manageTaskAttachments;
    public boolean createTaskMessageAttachments;
    public boolean manageTaskMessageAttachments;

    public boolean viewFilters;
    public boolean manageTaskPrivateFilters;
    public boolean manageTaskPublicFilters;

    public boolean viewReports;
    public boolean managePrivateReports;
    public boolean managePublicReports;

    public boolean manageEmailSchedules;
    public boolean manageTaskACLs;
    public boolean manageTaskUDFs;
    public boolean manageEmailImportRules;
    public boolean manageTaskTemplates;
    public boolean manageCategories;
    public boolean manageWorkflows;
    public boolean deleteOperations;
    public boolean deleteTheirTaskAttachment;
    public boolean deleteTheirMessageAttachment;

    public boolean viewScriptsBrowser;
    public boolean viewTemplatesBrowser;
    public boolean showView;
    public boolean showOtherFilterTab;
    public boolean canCreateTaskByOperation;
    public boolean canUsePostFiltration;
    public boolean canArchive;
    public boolean canDeleteArchive;

    public boolean isCanDeleteArchive() {
        return canDeleteArchive;
    }

    public void setCanDeleteArchive(boolean canDeleteArchive) {
        this.canDeleteArchive = canDeleteArchive;
    }

    public boolean isCanArchive() {
        return canArchive;
    }

    public void setCanArchive(boolean canArchive) {
        this.canArchive = canArchive;
    }

    public boolean isCanUsePostFiltration() {
        return canUsePostFiltration;
    }

    public void setCanUsePostFiltration(boolean canUsePostFiltration) {
        this.canUsePostFiltration = canUsePostFiltration;
    }

    public boolean isShowOtherFilterTab() {
        return !showOtherFilterTab;
    }

    public void setShowOtherFilterTab(boolean showOtherFilterTab) {
        this.showOtherFilterTab = showOtherFilterTab;
    }

    public boolean isCanCreateTaskByOperation() {
        return !canCreateTaskByOperation;
    }

    public void setCanCreateTaskByOperation(boolean canCreateTaskByOperation) {
        this.canCreateTaskByOperation = canCreateTaskByOperation;
    }

    public boolean isShowView() {
        return showView;
    }

    public void setShowView(boolean showView) {
        this.showView = showView;
    }

    public boolean isViewTemplatesBrowser() {
        return viewTemplatesBrowser;
    }

    public void setViewTemplatesBrowser(boolean viewTemplatesBrowser) {
        this.viewTemplatesBrowser = viewTemplatesBrowser;
    }

    public boolean isViewScriptsBrowser() {
        return viewScriptsBrowser;
    }

    public void setViewScriptsBrowser(boolean viewScriptsBrowser) {
        this.viewScriptsBrowser = viewScriptsBrowser;
    }


    public boolean isDeleteTheirMessageAttachment() {
        return deleteTheirMessageAttachment;
    }

    public void setDeleteTheirMessageAttachment(boolean deleteTheirMessageAttachment) {
        this.deleteTheirMessageAttachment = deleteTheirMessageAttachment;
    }

    public boolean isDeleteTheirTaskAttachment() {
        return deleteTheirTaskAttachment;
    }

    public void setDeleteTheirTaskAttachment(boolean deleteTheirTaskAttachment) {
        this.deleteTheirTaskAttachment = deleteTheirTaskAttachment;
    }

    public boolean isManageWorkflows() {
        return manageWorkflows;
    }

    public void setManageWorkflows(boolean manageWorkflows) {
        this.manageWorkflows = manageWorkflows;
    }

    public boolean isManageCategories() {
        return manageCategories;
    }

    public void setManageCategories(boolean manageCategories) {
        this.manageCategories = manageCategories;
    }

    public boolean isManageTaskTemplates() {
        return manageTaskTemplates;
    }

    public void setManageTaskTemplates(boolean manageTaskTemplates) {
        this.manageTaskTemplates = manageTaskTemplates;
    }

    public boolean isManageEmailImportRules() {
        return manageEmailImportRules;
    }

    public void setManageEmailImportRules(boolean manageEmailImportRules) {
        this.manageEmailImportRules = manageEmailImportRules;
    }

    public boolean isManageTaskUDFs() {
        return manageTaskUDFs;
    }

    public void setManageTaskUDFs(boolean manageTaskUDFs) {
        this.manageTaskUDFs = manageTaskUDFs;
    }

    public boolean isManageTaskACLs() {
        return manageTaskACLs;
    }

    public void setManageTaskACLs(boolean manageTaskACLs) {
        this.manageTaskACLs = manageTaskACLs;
    }

    public boolean isManageEmailSchedules() {
        return manageEmailSchedules;
    }

    public void setManageEmailSchedules(boolean manageEmailSchedules) {
        this.manageEmailSchedules = manageEmailSchedules;
    }

    public boolean isManagePublicReports() {
        return managePublicReports;
    }

    public void setManagePublicReports(boolean managePublicReports) {
        this.managePublicReports = managePublicReports;
    }

    public boolean isManagePrivateReports() {
        return managePrivateReports;
    }

    public void setManagePrivateReports(boolean managePrivateReports) {
        this.managePrivateReports = managePrivateReports;
    }

    public boolean isViewReports() {
        return viewReports;
    }

    public void setViewReports(boolean viewReports) {
        this.viewReports = viewReports;
    }

    public boolean isViewFilters() {
        return viewFilters;
    }

    public void setViewFilters(boolean viewFilters) {
        this.viewFilters = viewFilters;
    }

    public boolean isManageTaskPublicFilters() {
        return manageTaskPublicFilters;
    }

    public void setManageTaskPublicFilters(boolean manageTaskPublicFilters) {
        this.manageTaskPublicFilters = manageTaskPublicFilters;
    }

    public boolean isManageTaskPrivateFilters() {
        return manageTaskPrivateFilters;
    }

    public void setManageTaskPrivateFilters(boolean manageTaskPrivateFilters) {
        this.manageTaskPrivateFilters = manageTaskPrivateFilters;
    }

    public boolean isManageTaskAttachments() {
        return manageTaskAttachments;
    }

    public void setManageTaskAttachments(boolean manageTaskAttachments) {
        this.manageTaskAttachments = manageTaskAttachments;
    }

    public boolean isCreateTaskAttachments() {
        return createTaskAttachments;
    }

    public void setCreateTaskAttachments(boolean createTaskAttachments) {
        this.createTaskAttachments = createTaskAttachments;
    }

    public boolean isViewTaskAttachments() {
        return viewTaskAttachments;
    }

    public void setViewTaskAttachments(boolean viewTaskAttachments) {
        this.viewTaskAttachments = viewTaskAttachments;
    }

    public boolean isBulkProcessingTask() {
        return bulkProcessingTask;
    }

    public void setBulkProcessingTask(boolean bulkProcessingTask) {
        this.bulkProcessingTask = bulkProcessingTask;
    }

    public boolean isCutCopyPasteTask() {
        return cutCopyPasteTask;
    }

    public void setCutCopyPasteTask(boolean cutCopyPasteTask) {
        this.cutCopyPasteTask = cutCopyPasteTask;
    }

    public boolean isManageRegistrations() {
        return manageRegistrations;
    }

    public void setManageRegistrations(boolean manageRegistrations) {
        this.manageRegistrations = manageRegistrations;
    }


    public boolean isDeleteOperations() {
        return deleteOperations;
    }

    public void setDeleteOperations(boolean deleteOperations) {
        this.deleteOperations = deleteOperations;
    }

    public boolean isCreateTaskMessageAttachments() {
        return createTaskMessageAttachments;
    }

    public void setCreateTaskMessageAttachments(boolean createTaskMessageAttachments) {
        this.createTaskMessageAttachments = createTaskMessageAttachments;
    }

    public boolean isManageTaskMessageAttachments() {
        return manageTaskMessageAttachments;
    }

    public void setManageTaskMessageAttachments(boolean manageTaskMessageAttachments) {
        this.manageTaskMessageAttachments = manageTaskMessageAttachments;
    }
}