package com.trackstudio.form;

public class TaskListForm extends MessageForm {


    //protected String field, filter, search;

    protected String[] SELTASK;
    protected String[] deleteMessage;
    protected String[] TASKIDS;
    private String operation;
    protected String bulk;
    private String filter;
    private String udfField;
    private String pack;
    private String udfvalue;
    private String addButton;
    private String multiBulk;
    private boolean useByFilter;

    public boolean isUseByFilter() {
        return useByFilter;
    }

    public void setUseByFilter(boolean useByFilter) {
        this.useByFilter = useByFilter;
    }

    public String getMultiBulk() {
        return multiBulk;
    }

    public void setMultiBulk(String multiBulk) {
        this.multiBulk = multiBulk;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        if (mutable) this.operation = operation;
    }


    public String[] getSELTASK() {
        return SELTASK;
    }

    public void setSELTASK(String[] SELTASK) {
        this.SELTASK = SELTASK;
    }

    public String[] getDeleteMessage() {
        return deleteMessage;
    }

    public void setDeleteMessage(String[] deleteMessage) {
        this.deleteMessage = deleteMessage;
    }

    public String[] getTASKIDS() {
        return TASKIDS;
    }

    public void setTASKIDS(String[] TASKIDS) {
        this.TASKIDS = TASKIDS;
    }


    public String getBulk() {
        return bulk;
    }

    public void setBulk(String bulk) {
        this.bulk = bulk;

    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getUdfField() {
        return udfField;
    }

    public void setUdfField(String udfField) {
        this.udfField = udfField;
    }

    public String getPack() {
        return pack;
    }

    public void setPack(String pack) {
        this.pack = pack;
    }

    public String getUdfvalue() {
        return udfvalue;
    }

    public void setUdfvalue(String udfvalue) {
        this.udfvalue = udfvalue;
    }

    public String getAddButton() {
        return addButton;
    }

    public void setAddButton(String addButton) {
        this.addButton = addButton;
    }
}
