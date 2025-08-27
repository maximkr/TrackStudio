package com.trackstudio.soap.bean;

public class UdfBean {

    private String udfId;
    private String caption;
    private int order;
    private String def;
    private String udfSourceId;
    private String script;
    private String lookupscript;
    private boolean required;
    private boolean htmlview;
    private boolean lookuponly;
    private boolean cachevalues;
    private int type;
    private String initial;

    public UdfBean() {
    }

    public String getUdfId() {
        return udfId;
    }

    public void setUdfId(String udfId) {
        this.udfId = udfId;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getDef() {
        return def;
    }

    public void setDef(String def) {
        this.def = def;
    }

    public String getUdfSourceId() {
        return udfSourceId;
    }

    public void setUdfSourceId(String udfSourceId) {
        this.udfSourceId = udfSourceId;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isHtmlview() {
        return htmlview;
    }

    public void setHtmlview(boolean htmlview) {
        this.htmlview = htmlview;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String getLookupscript() {
        return lookupscript;
    }

    public void setLookupscript(String lookupscript) {
        this.lookupscript = lookupscript;
    }

    public boolean isLookuponly() {
        return lookuponly;
    }

    public void setLookuponly(boolean lookuponly) {
        this.lookuponly = lookuponly;
    }

    public boolean isCachevalues() {
        return cachevalues;
    }

    public void setCachevalues(boolean cachevalues) {
        this.cachevalues = cachevalues;
    }

    public String getInitial() {
        return initial;
    }

    public void setInitial(String initial) {
        this.initial = initial;
    }

}
