package com.trackstudio.form;

public class ACLForm extends BaseForm {
    private String[] aclUser;
    private String SINGLE_COPY;
    private String ALL_COPY;
    private String CUT;

    public String[] getAclUser() {
        return aclUser;
    }

    public void setAclUser(String[] aclUser) {
        this.aclUser = aclUser;
    }

    public String getSINGLE_COPY() {
        return SINGLE_COPY;
    }

    public void setSINGLE_COPY(String SINGLE_COPY) {
        this.SINGLE_COPY = SINGLE_COPY;
    }

    public String getALL_COPY() {
        return ALL_COPY;
    }

    public void setALL_COPY(String ALL_COPY) {
        this.ALL_COPY = ALL_COPY;
    }

    public String getCUT() {
        return CUT;
    }

    public void setCUT(String CUT) {
        this.CUT = CUT;
    }
}
