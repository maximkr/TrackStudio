package com.trackstudio.model;


import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * This class describes task priority.
 */

public class Priority extends Named implements Serializable {

    private String description; //persistent
    private Integer order; //persistent
    private Integer def; //persistent
    private Workflow workflow;

    private Set taskSet = new HashSet(); //persistent
    private Set messageSet = new HashSet(); //persistent

    public Priority(String id) {
        this.id = id;
    }

    public Priority(String name, String description, Integer order, boolean isdefault, Workflow workflow) {
        this.name = name;
        this.workflow = workflow;
        this.order = order;
        this.description = description;
        this.def = isdefault ? 1 : 0;
    }

    public Priority(String name, String description, Integer order, boolean isdefault, String workflowId) {
        this(name, description, order, isdefault, workflowId != null ? new Workflow(workflowId) : null);
    }

    public Priority() {
    }

    public Priority(String name, Integer order, Workflow workflow) {
        this.name = name;
        this.workflow = workflow;
        this.order = order;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Workflow getWorkflow() {
        return this.workflow;
    }

    public void setWorkflow(Workflow workflow) {
        this.workflow = workflow;
    }

    public Integer getOrder() {
        return this.order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public void setDef(Integer isdef) {
        if (isdef == null || isdef == 0) {
            this.def = 0;
        } else
            this.def = 1;
    }

    public void setDefault(Integer isdef) {
        this.setDef(isdef);
    }

    public boolean isDefault() {
        return !(def == null || def == 0);
    }

    public void setDefault() {
        this.def = 1;
    }

    public void unsetDefault() {
        this.def = 0;
    }

    public Set getTaskSet() {
        return this.taskSet;
    }

    public void setTaskSet(Set taskSet) {
        this.taskSet = taskSet;
    }

    public Set getMessageSet() {
        return this.messageSet;
    }

    public void setMessageSet(Set messageSet) {
        this.messageSet = messageSet;
    }

    public Integer getDef() {
        return def;
    }

    public boolean equals(Object obj) {
        return obj instanceof Priority && ((Priority) obj).getId().equals(this.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

}
