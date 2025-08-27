package com.trackstudio.form;

public class MstatusForm extends BaseForm {
    private String workflowId;
    private String name;
    private String description;
    private String action;

    private String mstatusId;
    private boolean showInToolbar;
    private boolean def;
    private String scheduler;

    public String getScheduler() {
        return scheduler;
    }

    public void setScheduler(String scheduler) {
        this.scheduler = scheduler;
    }

    public boolean isDef() {
        return def;
    }

    public void setDef(boolean def) {
        this.def = def;
    }

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


    public String getMstatusId() {
        return mstatusId;
    }

    public void setMstatusId(String mstatusId) {
        if (isMutable())
            this.mstatusId = mstatusId;
    }

    public boolean isShowInToolbar() {
        return showInToolbar;
    }

    public void setShowInToolbar(boolean showInToolbar) {
        if (isMutable())
            this.showInToolbar = showInToolbar;
    }

    public boolean getShowInToolbar() {
        return showInToolbar;
    }


    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
