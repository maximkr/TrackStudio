package com.trackstudio.model;


import java.io.Serializable;

/**
 * This class describes relation between user and task.
 * Also this class store information about active filter
 * and ACL settings.
 */
public class Acl implements Serializable {
    private String id; //identifier


    private Task task;
    private User toUser;
    private Usersource usersource;
    private User owner;
    private Prstatus prstatus;
    private Integer override;

    public Acl(String id) {
        this.id = id;
    }

    public Acl(Task task, User toUser, Usersource usersource, Prstatus prstatus, User owner) {
        this.task = task;
        this.toUser = toUser;
        this.usersource = usersource;
        this.prstatus = prstatus;
        this.owner = owner;

    }

    public Acl(String taskId, String toUserId, String usersourceId, String prstatusId, String ownerId) {
        this(taskId != null ? new Task(taskId) : null, toUserId != null ? new User(toUserId) : null, usersourceId != null ? new Usersource(usersourceId) : null,
                prstatusId != null ? new Prstatus(prstatusId) : null, ownerId != null ? new User(ownerId) : null);
    }

    public Acl() {
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

    public User getToUser() {
        return toUser;
    }

    public void setToUser(User toUser) {
        this.toUser = toUser;
    }

    public Usersource getUsersource() {
        return this.usersource;
    }

    public void setUsersource(Usersource usersource) {
        this.usersource = usersource;
    }


    public Prstatus getPrstatus() {
        return prstatus;
    }

    public void setPrstatus(Prstatus prstatus) {
        this.prstatus = prstatus;
    }

    public void setPrstatus(String prstatusId) {
        this.prstatus = new Prstatus(prstatusId);
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Integer getOverride() {
        return override;
    }

    public void setOverride(Integer override) {
        this.override = override;
    }

    public boolean equals(Object obj) {
        return obj instanceof Acl && ((Acl) obj).getId().equals(this.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
