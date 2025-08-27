package com.trackstudio.form;

public class SelectForm extends BaseForm {
    private String filterId;
    private String udfField;
    private String pack;
    private String udfvalue;
    private String addButton;

    public String getAddCurrentButton() {
        return addCurrentButton;
    }

    public void setAddCurrentButton(String addCurrentButton) {
        this.addCurrentButton = addCurrentButton;
    }

    private String addCurrentButton;

    public String getGoKey() {
        return goKey;
    }

    public void setGoKey(String goKey) {
        this.goKey = goKey;
    }

    private String goKey;
    private String[] adds;

    public String getFilterId() {
        return filterId;
    }

    public void setFilterId(String filterId) {
        this.filterId = filterId;
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

    public String[] getAdds() {
        return adds;
    }

    public void setAdds(String[] adds) {
        this.adds = adds;
    }

    public String getAddButton() {
        return addButton;
    }

    public void setAddButton(String addButton) {
        this.addButton = addButton;
    }

    public String getUdfvalue() {
        return udfvalue;
    }

    public void setUdfvalue(String udfvalue) {
        this.udfvalue = udfvalue;
    }
}
