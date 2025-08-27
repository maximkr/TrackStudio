package com.trackstudio.form;

public class MstatusPermissionForm extends BaseForm {
    private String workflowId;
    private String mstatusId;
    protected String[] ruleCategory;
    protected String submitterOnly;
    protected String handlerOnly;
    protected String canview, cannotview, hiddencanview;
    protected String canprocess, cannotprocess, hiddencanprocess;
    protected String canhandler, cannothandler, hiddencanhandler;
    protected String canedit, cannotedit, hiddencanedit;
    protected String[] view, edit;


    public String[] getView() {
        return view;
    }

    public void setView(String[] view) {
        this.view = view;
    }

    public String[] getEdit() {
        return edit;
    }

    public void setEdit(String[] edit) {
        this.edit = edit;
    }

    public String[] getRuleCategory() {
        return ruleCategory;
    }

    public void setRuleCategory(String[] ruleCategory) {
        this.ruleCategory = ruleCategory;
    }

    public String getSubmitterOnly() {
        return submitterOnly;
    }

    public void setSubmitterOnly(String submitterOnly) {
        this.submitterOnly = submitterOnly;
    }

    public String getHandlerOnly() {
        return handlerOnly;
    }

    public void setHandlerOnly(String handlerOnly) {
        this.handlerOnly = handlerOnly;
    }

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
        if (isMutable())
            this.mstatusId = mstatusId;
    }


    public String getCanview() {
        return canview;
    }

    public void setCanview(String canview) {
        this.canview = canview;
    }

    public String getCannotview() {
        return cannotview;
    }

    public void setCannotview(String cannotview) {
        this.cannotview = cannotview;
    }

    public String getHiddencanview() {
        return hiddencanview;
    }

    public void setHiddencanview(String hiddencanview) {
        this.hiddencanview = hiddencanview;
    }

    public String getCanprocess() {
        return canprocess;
    }

    public void setCanprocess(String canprocess) {
        this.canprocess = canprocess;
    }

    public String getCannotprocess() {
        return cannotprocess;
    }

    public void setCannotprocess(String cannotprocess) {
        this.cannotprocess = cannotprocess;
    }

    public String getHiddencanprocess() {
        return hiddencanprocess;
    }

    public void setHiddencanprocess(String hiddencanprocess) {
        this.hiddencanprocess = hiddencanprocess;
    }

    public String getCanhandler() {
        return canhandler;
    }

    public void setCanhandler(String canhandler) {
        this.canhandler = canhandler;
    }

    public String getCannothandler() {
        return cannothandler;
    }

    public void setCannothandler(String cannothandler) {
        this.cannothandler = cannothandler;
    }

    public String getHiddencanhandler() {
        return hiddencanhandler;
    }

    public void setHiddencanhandler(String hiddencanhandler) {
        this.hiddencanhandler = hiddencanhandler;
    }


    public String getCanedit() {
        return canedit;
    }

    public void setCanedit(String canedit) {
        this.canedit = canedit;
    }

    public String getCannotedit() {
        return cannotedit;
    }

    public void setCannotedit(String cannotedit) {
        this.cannotedit = cannotedit;
    }

    public String getHiddencanedit() {
        return hiddencanedit;
    }

    public void setHiddencanedit(String hiddencanedit) {
        this.hiddencanedit = hiddencanedit;
    }


}
