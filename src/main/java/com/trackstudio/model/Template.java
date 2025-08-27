package com.trackstudio.model;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 26.10.2006
 * Time: 12:25:34
 * To change this template use File | Settings | File Templates.
 */
public class Template extends Named implements Serializable {

    private String description;

    private User owner;
    private User user;
    private Task task;
    private String folder;
    private Integer active;


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setUser(String userId) {
        if (userId != null && userId.length() > 0)
            this.user = new User(userId);
        else this.user = null;
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

    public String getFolder() {
        return folder;
    }

    public void setFolder(String file) {
        this.folder = file;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    public Template(String id) {
        this.id = id;
    }

    public Template(String name, User owner, User user, Task task) {
        this.name = name;
        this.owner = owner;
        this.user = user;
        this.task = task;
    }

    public Template(String name, String description, User owner, User user, Task task) {
        this.name = name;
        this.description = description;
        this.owner = owner;
        this.user = user;
        this.task = task;

    }

    public Template(String name, User owner, Task task) {
        this.name = name;
        this.owner = owner;

        this.task = task;
    }

    public Template(String name, String ownerId, String userId, String taskId) {
        this(name, ownerId != null ? new User(ownerId) : null, userId != null && userId.length() > 0 ? new User(userId) : null, taskId != null ? new Task(taskId) : null);
    }

    public Template(String name, String description, String ownerId, String userId, String taskId) {
        this(name, description, ownerId != null ? new User(ownerId) : null, userId != null && userId.length() > 0 ? new User(userId) : null, taskId != null ? new Task(taskId) : null);
    }

    public Template(String name, String ownerId, String taskId) {
        this(name, ownerId != null ? new User(ownerId) : null, taskId != null ? new Task(taskId) : null);
    }

    public Template() {
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
