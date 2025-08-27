package com.trackstudio.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Describes items (task, users, workflows) that can
 * hold UDFs.
 */
public class Udfsource implements Serializable {
    protected String id; //identifier
    protected Set udfvalSet = new HashSet(); //persistent
    protected Set udfSet = new HashSet(); //persistent

    protected Task task;
    protected User user;
    protected Workflow workflow;


    public Udfsource(Task task, User user, Workflow workflow) {
        this.task = task;
        this.user = user;
        this.workflow = workflow;
    }

    public Udfsource(String taskId, String userId, String workflowId) {
        this(taskId != null ? new Task(taskId) : null, userId != null ? new User(userId) : null, workflowId != null ? new Workflow(workflowId) : null);
    }

    public Udfsource() {
    }

    public Udfsource(String id) {
        this.id = id;
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

    public Workflow getWorkflow() {
        return this.workflow;
    }

    public void setWorkflow(Workflow workflow) {
        this.workflow = workflow;
    }

    public Set getUdfvalSet() {
        return this.udfvalSet;
    }

    public void setUdfvalSet(Set udfvalSet) {
        this.udfvalSet = udfvalSet;
    }

    public Set getUdfSet() {
        return this.udfSet;
    }

    public void setUdfSet(Set udfSet) {
        this.udfSet = udfSet;
    }

    public boolean equals(Object obj) {
        return obj instanceof Udfsource && ((Udfsource) obj).getId().equals(this.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
