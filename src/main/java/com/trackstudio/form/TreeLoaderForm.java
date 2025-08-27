package com.trackstudio.form;

public class TreeLoaderForm extends BaseForm {
    private String udfSelect;
    private String ui;
    private String ti;
    private String className;
    private String prefix;

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getUdfSelect() {
        return udfSelect;
    }

    public void setUdfSelect(String udfSelect) {
        this.udfSelect = udfSelect;
    }

    public String getUi() {
        return ui;
    }

    public void setUi(String ui) {
        this.ui = ui;
    }

    public String getTi() {
        return ti;
    }

    public void setTi(String ti) {
        this.ti = ti;
    }
}
