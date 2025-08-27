package com.trackstudio.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * This class describes message type, for example, note,
 * resolve, close.
 */
public class Mstatus extends Named implements Serializable {

    private String description; //persistent
    private String action; //persistent

    private String preferences;
    private Set resolutionSet = new HashSet(); //persistent
    private Set transitionSet = new HashSet(); //persistent
    private Set mprstatusSet = new HashSet(); //persistent
    private Set umstatusSet = new HashSet(); //persistent
    private Set messageSet = new HashSet(); //persistent
    private Set mailImportSet = new HashSet(); //persistent

    private Workflow workflow;
    private Trigger trigger;

    public Mstatus(String id) {
        this.id = id;
    }

    public Mstatus(String name, String description, Workflow workflow, String preferences) {
        this.name = name;
        this.description = description;
        this.workflow = workflow;
        this.preferences = preferences;

    }

    public Mstatus(String name, String description, String workflowId, String preferences) {
        this(name, description, workflowId != null ? new Workflow(workflowId) : null, preferences);
    }

	public Mstatus(String name, String description, String action, String workflowId, String preferences) {
		this(name, description, workflowId != null ? new Workflow(workflowId) : null, preferences);
		this.action = action;
	}

    public Mstatus() {
    }

    public Mstatus(Workflow workflow) {
        this.workflow = workflow;
    }


    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Workflow getWorkflow() {
        return this.workflow;
    }

    public void setWorkflow(Workflow workflow) {
        this.workflow = workflow;
    }

    public Set getResolutionSet() {
        return this.resolutionSet;
    }

    public void setResolutionSet(Set resolutionSet) {
        this.resolutionSet = resolutionSet;
    }

    public Set getTransitionSet() {
        return this.transitionSet;
    }

    public void setTransitionSet(Set transitionSet) {
        this.transitionSet = transitionSet;
    }

    public Set getMprstatusSet() {
        return this.mprstatusSet;
    }

    public void setMprstatusSet(Set mprstatusSet) {
        this.mprstatusSet = mprstatusSet;
    }

    public Set getMessageSet() {
        return this.messageSet;
    }

    public void setMessageSet(Set messageSet) {
        this.messageSet = messageSet;
    }


    public Trigger getTrigger() {
        return trigger;
    }

    public void setTrigger(Trigger trigger) {
        this.trigger = trigger;
    }

    public void setTrigger(String triggerId) {
        this.trigger = triggerId != null ? new Trigger(triggerId) : null;
    }


    public boolean equals(Object obj) {
        return obj instanceof Mstatus && ((Mstatus) obj).getId().equals(this.id);
    }


    public String getPreferences() {
        return preferences;
    }

    public void setPreferences(String preferences) {
        this.preferences = preferences;
    }


    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }


    public Set getUmstatusSet() {
        return umstatusSet;
    }

    public void setUmstatusSet(Set umstatusSet) {
        this.umstatusSet = umstatusSet;
    }


    public Set getMailImportSet() {
        return mailImportSet;
    }

    public void setMailImportSet(Set mailImportSet) {
        this.mailImportSet = mailImportSet;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
