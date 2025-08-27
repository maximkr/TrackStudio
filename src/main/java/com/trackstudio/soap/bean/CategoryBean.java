package com.trackstudio.soap.bean;

public class CategoryBean {

    private String id;
    private String name;
    private String budget;
    private String icon;
    private String preferences;
    private String action;
    private String description;
    private String workflowId;
    private String taskId;
    private boolean handlerRequired;
    private String template;
    private String createTriggerBeforeId;
    private String createTriggerInsteadOfId;
    private String createTriggerAfterId;
    private String updateTriggerBeforeId;
    private String updateTriggerInsteadOfId;
    private String updateTriggerAfterId;
    private boolean groupHandlerAllowed;

    public CategoryBean() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public boolean isHandlerRequired() {
        return handlerRequired;
    }

    public void setHandlerRequired(boolean handlerRequired) {
        this.handlerRequired = handlerRequired;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getCreateTriggerBeforeId() {
        return createTriggerBeforeId;
    }

    public void setCreateTriggerBeforeId(String createTriggerBeforeId) {
        this.createTriggerBeforeId = createTriggerBeforeId;
    }

    public String getCreateTriggerInsteadOfId() {
        return createTriggerInsteadOfId;
    }

    public void setCreateTriggerInsteadOfId(String createTriggerInsteadOfId) {
        this.createTriggerInsteadOfId = createTriggerInsteadOfId;
    }

    public String getCreateTriggerAfterId() {
        return createTriggerAfterId;
    }

    public void setCreateTriggerAfterId(String createTriggerAfterId) {
        this.createTriggerAfterId = createTriggerAfterId;
    }

    public String getUpdateTriggerBeforeId() {
        return updateTriggerBeforeId;
    }

    public void setUpdateTriggerBeforeId(String updateTriggerBeforeId) {
        this.updateTriggerBeforeId = updateTriggerBeforeId;
    }

    public String getUpdateTriggerInsteadOfId() {
        return updateTriggerInsteadOfId;
    }

    public void setUpdateTriggerInsteadOfId(String updateTriggerInsteadOfId) {
        this.updateTriggerInsteadOfId = updateTriggerInsteadOfId;
    }

    public String getUpdateTriggerAfterId() {
        return updateTriggerAfterId;
    }

    public void setUpdateTriggerAfterId(String updateTriggerAfterId) {
        this.updateTriggerAfterId = updateTriggerAfterId;
    }

    public boolean isGroupHandlerAllowed() {
        return groupHandlerAllowed;
    }

    public void setGroupHandlerAllowed(boolean groupHandlerAllowed) {
        this.groupHandlerAllowed = groupHandlerAllowed;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getBudget() {
        return budget;
    }

    public void setBudget(String budget) {
        this.budget = budget;
    }


    public String getPreferences() {
        return preferences;
    }

    public void setPreferences(String preferences) {
        this.preferences = preferences;
    }


    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}

