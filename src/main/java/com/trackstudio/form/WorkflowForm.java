package com.trackstudio.form;

public class WorkflowForm extends BaseForm {
    private String workflowId;
    private String name;

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        if (isMutable())
            this.workflowId = workflowId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
