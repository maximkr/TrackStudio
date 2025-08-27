package com.trackstudio.form;

public class PrstatusForm extends CustomForm {

    protected String statusId;//for copy
    protected String createNewStatus;
    protected String prstatusId;
    
    protected String[] role;
    protected String[] ruleCategory;
    protected String submitterOnly;
    protected String handlerOnly;
    protected boolean showInToolbar;

    
    protected String canprocess, cannotprocess, hiddencanprocess;
    
    protected String cancreate, cannotcreate, hiddencancreate;
    protected String candelete, cannotdelete, hiddencandelete;
    protected String canhandler, cannothandler, hiddencanhandler;

    protected String[] view, edit, viewH, viewS, viewSH, editH, editS, editSH;

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
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

    public String[] getRuleCategory() {
        return ruleCategory;
    }

    public void setRuleCategory(String[] ruleCategory) {
        this.ruleCategory = ruleCategory;
    }


    public String getStatusId() {
        return statusId;
    }

    public void setStatusId(String statusId) {
        if (isMutable())
            this.statusId = statusId;
    }

    public String getPrstatusId() {
        return prstatusId;
    }

    public void setPrstatusId(String prstatusId) {
        if (isMutable())
            this.prstatusId = prstatusId;
    }

    public String[] getRole() {
        return role;
    }

    public void setRole(String[] role) {
        this.role = role;
    }

    public String getCreateNewStatus() {
        return createNewStatus;
    }

    public void setCreateNewStatus(String createNewStatus) {
        this.createNewStatus = createNewStatus;
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

    public String getCancreate() {
        return cancreate;
    }

    public void setCancreate(String cancreate) {
        this.cancreate = cancreate;
    }

    public String getCannotcreate() {
        return cannotcreate;
    }

    public void setCannotcreate(String cannotcreate) {
        this.cannotcreate = cannotcreate;
    }

    public String getHiddencancreate() {
        return hiddencancreate;
    }

    public void setHiddencancreate(String hiddencancreate) {
        this.hiddencancreate = hiddencancreate;
    }

    public String getCandelete() {
        return candelete;
    }

    public void setCandelete(String candelete) {
        this.candelete = candelete;
    }

    public String getCannotdelete() {
        return cannotdelete;
    }

    public void setCannotdelete(String cannotdelete) {
        this.cannotdelete = cannotdelete;
    }

    public String getHiddencandelete() {
        return hiddencandelete;
    }

    public void setHiddencandelete(String hiddencandelete) {
        this.hiddencandelete = hiddencandelete;
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

    public String[] getViewH() {
        return viewH;
    }

    public void setViewH(String[] viewH) {
        this.viewH = viewH;
    }

    public String[] getViewS() {
        return viewS;
    }

    public void setViewS(String[] viewS) {
        this.viewS = viewS;
    }

    public String[] getViewSH() {
        return viewSH;
    }

    public void setViewSH(String[] viewSH) {
        this.viewSH = viewSH;
    }

    public String[] getEditH() {
        return editH;
    }

    public void setEditH(String[] editH) {
        this.editH = editH;
    }

    public String[] getEditS() {
        return editS;
    }

    public void setEditS(String[] editS) {
        this.editS = editS;
    }

    public String[] getEditSH() {
        return editSH;
    }

    public void setEditSH(String[] editSH) {
        this.editSH = editSH;
    }
}
