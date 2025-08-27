package com.trackstudio.model;

public class Property {

    private String id;
    private String name;
    private String value;

    public Property() {

    }

    public Property(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

}
