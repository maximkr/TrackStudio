package com.trackstudio.model;

import java.io.Serializable;

/**
 * This class describes email notification settings
 * for specified task, filter and user.
 */
public class Notification implements Serializable {
    private String id; //identifier

    private String condition;
    private String name;

    private Usersource user;
    private Filter filter;
    private Task task;
    private String template;

    public Notification(String id) {
        this.id = id;
    }

    public Notification(String name, Usersource user, Filter filter, Task task, String template, String condition) {
        this.name = name;
        this.condition = condition;
        this.user = user;
        this.filter = filter;
        this.task = task;
        this.template = template;
    }

    public Notification(String name, String userId, String filterId, String taskId, String template, String condition) {
        this(name, userId != null ? new Usersource(userId) : null, filterId != null ? new Filter(filterId) : null, taskId != null ? new Task(taskId) : null, template != null ? template : null, condition);
    }

    public Notification() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public Usersource getUser() {
        return this.user;
    }

    public void setUser(Usersource user) {
        this.user = user;
    }

    public Filter getFilter() {
        return this.filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public Task getTask() {
        return this.task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public boolean equals(Object obj) {
        return obj instanceof Notification && ((Notification) obj).getId().equals(this.id);
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
