package com.trackstudio.form;

public class TransitionForm extends BaseForm {
    private String workflowId;
    private String mstatusId;
    private String[] start;
    private String finish;

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    public String getMstatusId() {
        return mstatusId;
    }

    public void setMstatusId(String mstatusId) {
        this.mstatusId = mstatusId;
    }

    public String[] getStart() {
        return start;
    }

    public void setStart(String[] start) {
        this.start = start;
    }

    public String getFinish() {
        return finish;
    }

    public void setFinish(String finish) {
        this.finish = finish;
    }
}
