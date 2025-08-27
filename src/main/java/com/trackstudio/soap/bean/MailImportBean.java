package com.trackstudio.soap.bean;

public class MailImportBean {

    private String id;
    private String name;
    private String domain;
    private String keywords;
    private int searchIn;
    private int order;
    private String categoryId;
    private String taskId;
    private String ownerId;
    private boolean active;
    private MstatusBean mstatus;


    public MailImportBean() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public int getSearchIn() {
        return searchIn;
    }

    public void setSearchIn(int searchIn) {
        this.searchIn = searchIn;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }


    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public MstatusBean getMstatus() {
        return mstatus;
    }

    public void setMstatus(MstatusBean mstatus) {
        this.mstatus = mstatus;
    }
}
