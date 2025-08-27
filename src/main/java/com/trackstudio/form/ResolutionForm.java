package com.trackstudio.form;

public class ResolutionForm extends BaseForm {
    private String workflowId;
    private String name;
    private boolean def;
    private String defaultForRadioButton;
    private String resolutionId;
    private String mstatusId;

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

    public boolean getDef() {
        return def;
    }

    public void setDef(boolean def) {
        this.def = def;
    }

    public String getResolutionId() {
        return resolutionId;
    }

    public void setResolutionId(String resolutionId) {
        this.resolutionId = resolutionId;
    }


    public String getDefaultForRadioButton() {
        return defaultForRadioButton;
    }

    public void setDefaultForRadioButton(String defaultForRadioButton) {
        this.defaultForRadioButton = defaultForRadioButton;
    }

    public String getMstatusId() {
        return mstatusId;
    }

    public void setMstatusId(String mstatusId) {
        this.mstatusId = mstatusId;
    }
}