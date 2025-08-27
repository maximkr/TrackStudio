package com.trackstudio.soap.bean;

public class MstatusBean {

    private String id;
    private String description;
    private String action;
    private String name;
    private String preferences;

    private String workflowId;
    private String triggerBeforeId;
    private String triggerInsteadOfId;
    private String triggerAfterId;

    public MstatusBean() {

    }

    public MstatusBean(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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


    public String getTriggerBeforeId() {
        return triggerBeforeId;
    }

    public void setTriggerBeforeId(String triggerBeforeId) {
        this.triggerBeforeId = triggerBeforeId;
    }

    public String getTriggerInsteadOfId() {
        return triggerInsteadOfId;
    }

    public void setTriggerInsteadOfId(String triggerInsteadOfId) {
        this.triggerInsteadOfId = triggerInsteadOfId;
    }

    public String getTriggerAfterId() {
        return triggerAfterId;
    }

    public void setTriggerAfterId(String triggerAfterId) {
        this.triggerAfterId = triggerAfterId;
    }


    public String getPreferences() {
        return preferences;
    }

    public void setPreferences(String preferences) {
        this.preferences = preferences;
    }


    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
