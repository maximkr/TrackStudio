package com.trackstudio.form;

/**
 * Created by IntelliJ IDEA.
 * User: Eugene
 * Date: 02.02.2007
 * Time: 1:09:25
 * To change this template use File | Settings | File Templates.
 */
public class MstatusUdfPermissionForm extends BaseForm {
    private String workflowId;
    private String mstatusId;
    private String udfId;
    protected String canview, cannotview, canedit, cannotedit, hiddencanview, hiddencanedit;
    protected String[] rules;


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

    public String getUdfId() {
        return udfId;
    }

    public void setUdfId(String udfId) {
        this.udfId = udfId;
    }

    public String[] getRules() {
        return rules;
    }

    public void setRules(String[] rules) {
        this.rules = rules;
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


    public String getHiddencanview() {
        return hiddencanview;
    }

    public void setHiddencanview(String hiddencanview) {
        this.hiddencanview = hiddencanview;
    }

    public String getHiddencanedit() {
        return hiddencanedit;
    }

    public void setHiddencanedit(String hiddencanedit) {
        this.hiddencanedit = hiddencanedit;
    }
}
