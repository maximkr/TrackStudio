package com.trackstudio.model;


import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Describes common filter properties.
 */
public class Filter extends Named implements Serializable {

    private String description; //persistent
    private Integer priv; //persistent
    private String preferences;
    private Set subscriptionSet = new HashSet(); //persistent
    private Set notificationSet = new HashSet(); //persistent
    private Set currentFilterSet = new HashSet(); //persistent
    private Set fvalueSet = new HashSet(); //persistent
    private Set reportSet = new HashSet(); //persistent
    private Set bookmarkSet = new HashSet(); //persistent

    private Task task;
    private User owner;
    private User user;

    public Filter(String id) {
        this.id = id;
    }

    public Filter(String name, String description, boolean priv, Task task, User owner) {
        this.name = name;
        this.description = description;
        this.priv = priv ? 1 : 0;
        this.task = task;
        this.owner = owner;
    }

    public Filter(String name, String description, boolean priv, User user, User owner) {
        this.name = name;
        this.description = description;
        this.priv = priv ? 1 : 0;
        this.user = user;
        this.owner = owner;
    }

    public Filter(String name, String description, boolean priv, Task task, User user, User owner, String preferences) {
        this.name = name;
        this.description = description;
        this.priv = priv ? 1 : 0;
        this.user = user;
        this.task = task;
        this.owner = owner;
        this.preferences = preferences;
    }

    public Filter(String name, String description, boolean priv, String taskId, String userId, String ownerId, String preferences) {
        this(name, description, priv, taskId != null ? new Task(taskId) : null, userId != null ? new User(userId) : null, ownerId != null ? new User(ownerId) : null, preferences);
    }

    public Filter() {
    }

    public Filter(Task task, User owner) {
        this.task = task;
        this.owner = owner;
    }

    public Filter(String taskId, String ownerId) {
        this(taskId != null ? new Task(taskId) : null, ownerId != null ? new User(ownerId) : null);
    }


    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPriv() {
        return this.priv;
    }

    public void setPriv(Integer priv) {
        this.priv = priv;
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

    public Set getSubscriptionSet() {
        return this.subscriptionSet;
    }

    public void setSubscriptionSet(Set subscriptionSet) {
        this.subscriptionSet = subscriptionSet;
    }

    public Set getCurrentFilterSet() {
        return this.currentFilterSet;
    }

    public void setCurrentFilterSet(Set currentFilterSet) {
        this.currentFilterSet = currentFilterSet;
    }

    public Set getFvalueSet() {
        return this.fvalueSet;
    }

    public void setFvalueSet(Set fvalueSet) {
        this.fvalueSet = fvalueSet;
    }

    public Set getReportSet() {
        return reportSet;
    }

    public void setReportSet(Set reportSet) {
        this.reportSet = reportSet;
    }

    public Set getNotificationSet() {
        return this.notificationSet;
    }

    public void setNotificationSet(Set notificationSet) {
        this.notificationSet = notificationSet;
    }

    public boolean equals(Object obj) {
        return obj instanceof Filter && ((Filter) obj).getId().equals(this.id);
    }

    public boolean isPrivate() {
        return priv == 1;
    }

    public void setPrivate(boolean priv) {
        this.priv = priv ? 1 : 0;
    }

    public String getPreferences() {
        return preferences;
    }

    public void setPreferences(String preferences) {
        this.preferences = preferences;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set getBookmarkSet() {
        return bookmarkSet;
    }

    public void setBookmarkSet(Set bookmarkSet) {
        this.bookmarkSet = bookmarkSet;
    }

    @Override
    public String toString() {
        return "Filter{" +
                "name=" + name +
                "user=" + user +
                ", task=" + task +
                '}';
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}


