package com.trackstudio.model;

import java.io.Serializable;

public class MailImport implements Serializable, Comparable {

    private String id; //identifier
    private String keywords; //persistent
    private String name;
    private String domain;
    private Integer searchIn; //persistent
    private Integer order; //persistent
    private Category category;
    private Mstatus mstatus;
    private Task task;
    private User owner;
    private Integer active;
    private Integer importUnknown;

    public MailImport(String id) {
        this.id = id;
    }

    public MailImport(String name, String keywords, Integer searchIn, Integer order, Category category, Mstatus mstatus, Task task, User owner, String domain, Integer active, Integer importUnknown) {
        this.name = name;
        this.domain = domain;
        this.keywords = keywords;
        this.searchIn = searchIn;
        this.order = order;
        this.category = category;
        this.task = task;
        this.owner = owner;
        this.active = active;
        this.mstatus = mstatus;
        this.importUnknown = importUnknown;
    }

    public MailImport(String name, String keywords, Integer searchIn, Integer order, String categoryId, String mstatusId, String taskId, String ownerId, String domain, Integer active, Integer importUnknown) {
        this(name, keywords, searchIn, order, categoryId != null ? new Category(categoryId) : null, mstatusId != null && !mstatusId.isEmpty() ? new Mstatus(mstatusId) : null, taskId != null ? new Task(taskId) : null, ownerId != null ? new User(ownerId) : null, domain, active, importUnknown);
    }

    public MailImport() {
    }

    public String getId() {
        return this.id;
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

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getKeywords() {
        return this.keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public Integer getSearchIn() {
        return this.searchIn;
    }

    public void setSearchIn(Integer searchIn) {
        this.searchIn = searchIn;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Category getCategory() {
        return this.category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setCategory(String categoryId) {
        this.category = new Category(categoryId);
    }

    public Task getTask() {
        return this.task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public void setOwner(String ownerId) {
        this.owner = new User(ownerId);
    }

    public Mstatus getMstatus() {
        return mstatus;
    }

    public void setMstatus(String mstatusId) {
        this.mstatus = mstatusId == null ? null : new Mstatus(mstatusId);
    }

    public void setMstatus(Mstatus mstatus) {
        this.mstatus = mstatus;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    public boolean equals(Object obj) {
        return obj instanceof MailImport && ((MailImport) obj).getId().equals(this.id);
    }

    public int compareTo(Object o) {
        MailImport mi = (MailImport) o;
        if (mi.getOrder().equals(this.getOrder())) return 0;
        if (mi.getOrder() < this.getOrder()) return 1;
        if (mi.getOrder() > this.getOrder()) return -1;
        return 0;
    }

    public Integer getImportUnknown() {
        return importUnknown;
    }

    public void setImportUnknown(Integer importUnknown) {
        this.importUnknown = importUnknown;
    }

    @Override
    public String toString() {
        return "MailImport{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
