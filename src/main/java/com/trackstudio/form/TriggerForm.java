package com.trackstudio.form;

public class TriggerForm extends BaseForm {
    private String mstatusId;
    private String workflowId;
    private String categoryId;
    private String before;
    private String insteadOf;
    private String after;
    private String updBefore;
    private String updInsteadOf;
    private String updAfter;

    public String getBefore() {
        return before;
    }

    public void setBefore(String before) {
        this.before = before;
    }

    public String getInsteadOf() {
        return insteadOf;
    }

    public void setInsteadOf(String insteadOf) {
        this.insteadOf = insteadOf;
    }

    public String getAfter() {
        return after;
    }

    public void setAfter(String after) {
        this.after = after;
    }

    public String getUpdBefore() {
        return updBefore;
    }

    public void setUpdBefore(String updBefore) {
        this.updBefore = updBefore;
    }

    public String getUpdInsteadOf() {
        return updInsteadOf;
    }

    public void setUpdInsteadOf(String updInsteadOf) {
        this.updInsteadOf = updInsteadOf;
    }

    public String getUpdAfter() {
        return updAfter;
    }

    public void setUpdAfter(String updAfter) {
        this.updAfter = updAfter;
    }

    public String getMstatusId() {
        return mstatusId;
    }

    public void setMstatusId(String mstatusId) {
        this.mstatusId = mstatusId;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
}
