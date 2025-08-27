package com.trackstudio.soap.bean;

public class PairBean {

    private String key;
    private String value;

    public PairBean() {
    }

    public PairBean(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
