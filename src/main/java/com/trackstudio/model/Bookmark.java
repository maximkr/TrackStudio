package com.trackstudio.model;

import java.io.Serializable;
import java.util.Calendar;

public class Bookmark implements Serializable {

    private String id;
    private String name;
    private Calendar createdate;
    private Task task;
    private Filter filter;
    private User user;
    private User owner;

    public Bookmark() {
    }

    public Bookmark(String id) {
        this.id = id;
    }

    /**
     * Create Bookmark
     *
     * @param name
     * @param createdate
     * @param task
     * @param filter
     * @param user
     * @param owner
     */
    public Bookmark(String name, Calendar createdate, Task task, Filter filter, User user, User owner) {
        this.name = name;
        this.createdate = createdate;
        this.task = task;
        this.filter = filter;
        this.user = user;
        this.owner = owner;
    }

    /**
     * Create Bookmark
     *
     * @param name
     * @param createdate
     * @param taskId
     * @param filterId
     * @param userId
     * @param ownerId
     */
    public Bookmark(String name, Calendar createdate, String taskId, String filterId, String userId, String ownerId) {
        this(name != null ? name : null,
                createdate != null ? createdate : null,
                taskId != null ? new Task(taskId) : null,
                filterId != null ? new Filter(filterId) : null,
                userId != null ? new User(userId) : null,
                ownerId != null ? new User(ownerId) : null);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public void setTask(String taskId) {
        this.task = new Task(taskId);
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public void setFilter(String filterId) {
        this.filter = new Filter(filterId);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setUser(String userId) {
        this.user = new User(userId);
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Calendar getCreatedate() {
        return createdate;
    }

    public void setCreatedate(Calendar createdate) {
        this.createdate = createdate;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

}
