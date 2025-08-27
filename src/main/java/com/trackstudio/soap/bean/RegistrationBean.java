package com.trackstudio.soap.bean;

public class RegistrationBean {

    private String id;
    private String name;
    private String userId;
    private String prstatusId;
    private String taskId;
    private String categoryId;
    private boolean priv;
    private int childAllowed;
    private int expireDays;

    public RegistrationBean() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPrstatusId() {
        return prstatusId;
    }

    public void setPrstatusId(String prstatusId) {
        this.prstatusId = prstatusId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public int getChildAllowed() {
        return childAllowed;
    }

    public void setChildAllowed(int childAllowed) {
        this.childAllowed = childAllowed;
    }

    public int getExpireDays() {
        return expireDays;
    }

    public void setExpireDays(int expireDays) {
        this.expireDays = expireDays;
    }


    public boolean isPriv() {
        return priv;
    }

    public void setPriv(boolean priv) {
        this.priv = priv;
    }
}
