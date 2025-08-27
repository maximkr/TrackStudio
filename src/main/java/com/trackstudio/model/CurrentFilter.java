package com.trackstudio.model;


import java.io.Serializable;


/**
 * This class describes relation between user and task.
 * Also this class store information about active filter
 * and ACL settings.
 */
public class CurrentFilter implements Serializable {
    private String id; //identifier

    private Task task;
    private User user;
    private User owner;
    private Filter filter;


    public CurrentFilter(Task task, User user, Filter filter) {
        this.task = task;
        this.owner = user;
        this.filter = filter;
    }

    public CurrentFilter(User user, User owner, Filter filter) {
        this.user = user;
        this.owner = owner;
        this.filter = filter;
    }

    public CurrentFilter(Task task, User user, User owner, Filter filter) {

        this.user = user;
        this.task = task;
        this.owner = owner;
        this.filter = filter;
    }


    public CurrentFilter(String taskId, String userId, String ownerId, String filterId) {
        this(taskId != null ? new Task(taskId) : null, userId != null ? new User(userId) : null, ownerId != null ? new User(ownerId) : null, filterId != null ? new Filter(filterId) : null);

    }

    public CurrentFilter() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Task getTask() {
        return this.task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
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


    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public boolean equals(Object obj) {
        return obj instanceof CurrentFilter && ((CurrentFilter) obj).getId().equals(this.id);
    }
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
