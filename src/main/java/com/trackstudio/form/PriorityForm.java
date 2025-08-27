package com.trackstudio.form;

public class PriorityForm extends BaseForm {
    private String workflowId;
    private String name;
    private String description;
    private String order;
    private boolean def;
    private String defaultForRadioButton;
    private String priorityId;

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public boolean getDef() {
        return def;
    }

    public void setDef(boolean def) {
        this.def = def;
    }

    public String getPriorityId() {
        return priorityId;
    }

    public void setPriorityId(String priorityId) {
        this.priorityId = priorityId;
    }


    public String getDefaultForRadioButton() {
        return defaultForRadioButton;
    }

    public void setDefaultForRadioButton(String defaultForRadioButton) {
        this.defaultForRadioButton = defaultForRadioButton;
    }
}
