package com.trackstudio.form;

public class UserListForm extends BaseForm {

    protected String field;
    protected String filter;
    protected String search;
    protected String filterId;
    protected String farManagerAgree;

    private String udfField;
    private String pack;
    private String udfvalue;
    protected String[] SELUSER;
    protected String[] DELMESSAGE;
    protected String[] USERIDS;
    protected String SINGLE_COPY;
    protected String CUT;


    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getFilterId() {
        return filterId;
    }

    public void setFilterId(String filterId) {
        if (isMutable())
            this.filterId = filterId;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String[] getSELUSER() {
        return SELUSER;
    }

    public void setSELUSER(String[] SELUSER) {
        this.SELUSER = SELUSER;
    }

    public String[] getDELMESSAGE() {
        return DELMESSAGE;
    }

    public void setDELMESSAGE(String[] DELMESSAGE) {
        this.DELMESSAGE = DELMESSAGE;
    }

    public String getSINGLE_COPY() {
        return SINGLE_COPY;
    }

    public void setSINGLE_COPY(String SINGLE_COPY) {
        this.SINGLE_COPY = SINGLE_COPY;
    }

    public String getCUT() {
        return CUT;
    }

    public void setCUT(String CUT) {
        this.CUT = CUT;
    }

    public String[] getUSERIDS() {
        return USERIDS;
    }

    public void setUSERIDS(String[] USERIDS) {
        this.USERIDS = USERIDS;
    }

    public String getFarManagerAgree() {
        return farManagerAgree;
    }

    public void setFarManagerAgree(String farManagerAgree) {
        this.farManagerAgree = farManagerAgree;
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
}
