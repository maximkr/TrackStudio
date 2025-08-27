package com.trackstudio.model;


import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Describes common workflow properties
 */
public class Workflow extends Named implements Serializable {

    private Set mstatusSet = new HashSet(); //persistent
    private Set statusSet = new HashSet(); //persistent
    private Set categorySet = new HashSet(); //persistent
    private Set taskSet = new HashSet(); //persistent
    private Set prioritySet = new HashSet(); //persistent
    private Set udfsourceSet = new HashSet(); //persistent

    private Task task;

    public Workflow(String id) {
        this.id = id;
    }

    public Workflow(String name, Task task) {
        this.name = name;
        this.task = task;
    }

    public Workflow(String name, String taskId) {
        this(name, taskId != null ? new Task(taskId) : null);
    }

    public Workflow() {
    }

    public Workflow(Task task) {
        this.task = task;
    }


    public Task getTask() {
        return this.task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Set getMstatusSet() {
        return this.mstatusSet;
    }

    public void setMstatusSet(Set mstatusSet) {
        this.mstatusSet = mstatusSet;
    }

    public Set getStatusSet() {
        return this.statusSet;
    }

    public void setStatusSet(Set statusSet) {
        this.statusSet = statusSet;
    }

    public Set getCategorySet() {
        return this.categorySet;
    }

    public void setCategorySet(Set categorySet) {
        this.categorySet = categorySet;
    }

    public Set getTaskSet() {
        return this.taskSet;
    }

    public void setTaskSet(Set taskSet) {
        this.taskSet = taskSet;
    }

    public Set getPrioritySet() {
        return this.prioritySet;
    }

    public void setPrioritySet(Set prioritySet) {
        this.prioritySet = prioritySet;
    }

    public Set getUdfsourceSet() {
        return this.udfsourceSet;
    }

    public void setUdfsourceSet(Set udfsourceSet) {
        this.udfsourceSet = udfsourceSet;
    }

    public boolean equals(Object obj) {
        return obj instanceof Workflow && ((Workflow) obj).getId().equals(this.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

}
