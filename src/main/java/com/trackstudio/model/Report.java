package com.trackstudio.model;


import java.io.Serializable;

public class Report extends Named implements Serializable {

    private Integer priv; //persistent
    private String rtype;
    private Task task;
    private User owner;
    private String preferences;
    private Filter filter;
    /**
     * Report-type-specific params. Used for distribution reports, for example
     */
    private String params; //persistent

    public Report(String id) {
        this.id = id;
    }


    public Report(String name, boolean priv, String rtype, Filter filter, Task task, User owner) {
        this.name = name;
        this.priv = priv ? 1 : 0;
        this.rtype = rtype;
        this.task = task;
        this.owner = owner;
        this.filter = filter;
    }

    public Report(String name, boolean priv, String rtypeId, String filterId, String taskId, String ownerId) {
        this(name, priv, rtypeId, filterId != null ? new Filter(filterId) : null,
                taskId != null ? new Task(taskId) : null, ownerId != null ? new User(ownerId) : null);
    }

    public Report(String name, Integer priv, String rtype, Filter filter, Task task, User owner, String params) {
        this.name = name;
        this.priv = priv;
        this.rtype = rtype;
        this.task = task;
        this.owner = owner;
        this.filter = filter;
        this.params = params;
    }

    public Report() {
    }

    public Report(Task task, User owner, Filter filter, String rtype) {
        this.task = task;
        this.owner = owner;
        this.filter = filter;
        this.rtype = rtype;
    }


    public Integer getPriv() {
        return this.priv;
    }

    public void setPriv(Integer priv) {
        this.priv = priv;
    }

    public String getRtype() {
        return this.rtype;
    }

    public void setRtype(String rtype) {
        this.rtype = rtype;
    }


    public Task getTask() {
        return this.task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public User getOwner() {
        return this.owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Filter getFilter() {
        return this.filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public void setFilter(String filterId) {
        this.filter = new Filter(filterId);
    }

    /**
     * Возвращает параметры отчета
     *
     * @return параметры отчета
     */
    public String getParams() {
        return this.params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public boolean equals(Object obj) {
        return obj instanceof Report && ((Report) obj).getId().equals(this.id);
    }

    public String getPreferences() {
        return preferences;
    }

    public void setPreferences(String preferences) {
        this.preferences = preferences;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
