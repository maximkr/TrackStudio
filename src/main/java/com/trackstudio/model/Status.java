package com.trackstudio.model;


import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Describes task status.
 * Possible, this class should be refactored to State pattern
 */

public class Status extends Named implements Serializable {

    private Integer isstart; //persistent
    private Integer isfinish; //persistent
    private String color; //persistent
    private Set transitionStartSet = new HashSet(); //persistent
    private Set transitionFinishSet = new HashSet(); //persistent
    private Set taskSet = new HashSet(); //persistent

    private Workflow workflow;

    public Status(String id) {
        this.id = id;
    }

    public Status(String name, Integer start, Integer finish, Workflow workflow, String color) {
        this.name = name;
        this.isstart = start;
        this.isfinish = finish;
        this.workflow = workflow;
        this.color = color;
    }

    public Status(String name, Integer start, Integer finish, String workflowId, String color) {
        this(name, start, finish, workflowId != null ? new Workflow(workflowId) : null, color);
    }

    public Status() {
    }

    public Status(Workflow workflow) {
        this.workflow = workflow;
    }


    public void setIsstart(Integer start) {
        this.isstart = start;
    }


    public void setIsfinish(Integer finish) {
        this.isfinish = finish;
    }

    public Workflow getWorkflow() {
        return this.workflow;
    }

    public void setWorkflow(Workflow workflow) {
        this.workflow = workflow;
    }

    public String getColor() {
        return this.color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Set getTransitionStartSet() {
        return this.transitionStartSet;
    }

    public void setTransitionStartSet(Set transitionStartSet) {
        this.transitionStartSet = transitionStartSet;
    }

    public Set getTransitionFinishSet() {
        return this.transitionFinishSet;
    }

    public void setTransitionFinishSet(Set transitionFinishSet) {
        this.transitionFinishSet = transitionFinishSet;
    }

    public Set getTaskSet() {
        return this.taskSet;
    }

    public void setTaskSet(Set taskSet) {
        this.taskSet = taskSet;
    }

    public boolean isStart() {
        return this.isstart != null && (this.isstart == 1 || this.isstart == 2);
    }

    public boolean isSecondaryStart() {
        return this.isstart != null && this.isstart == 2;
    }

    public boolean isFinish() {
        return this.isfinish != null && this.isfinish == 1;
    }

    public boolean makeFinish() {
        this.isfinish = 1;
        return true;
    }

    public boolean resetFinish() {
        this.isfinish = 0;
        return false;
    }

    public boolean makeStart() {
        this.isstart = 1;
        return true;
    }

    public boolean makeSecondaryStart() {
        this.isstart = 2;
        return true;
    }

    public boolean resetStart() {
        this.isstart = 0;
        return false;
    }

    public Integer getIsstart() {
        return isstart;
    }

    public Integer getIsfinish() {
        return isfinish;
    }

    public boolean equals(Object obj) {
        return obj instanceof Status && ((Status) obj).getId().equals(this.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

}