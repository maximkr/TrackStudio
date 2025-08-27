package com.trackstudio.model;

import java.io.Serializable;

public class Attachment implements Serializable {

    private String id; //identifier
    private String description;
    private String name;
    private Message message;
    private Task task;
    private User user;


    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public Attachment(String id) {
        this.id = id;
    }

    public Attachment(Task task, Message message, User user, String name, String description) {
        this.task = task;
        this.user = user;
        this.name = name;
        this.message = message;
        this.description = description;
    }

    public Attachment(String taskId, String messageId, String userId, String name, String description) {
        this(taskId != null ? new Task(taskId) : null, messageId != null ? new Message(messageId) : null,
                userId != null ? new User(userId) : null, name, description);
    }

    public Attachment() {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean equals(Object obj) {
        if (obj instanceof Attachment)
            return ((Attachment) obj).getName().equals(this.name);
        if (obj instanceof String)
            return obj.equals(this.getName());
        return false;
    }

    public String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
