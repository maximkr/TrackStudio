package com.trackstudio.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.manager.KernelManager;

/**
 * This class describes task category.
 */
public class Category extends Named implements Serializable {

    /**
     * Workflow, related to this category
     */
    private Workflow workflow;
    /**
     * Parent task for this category
     */
    private Task task;
    private Longtext template;
    private String action, description, budget;
    private String preferences;
    private String icon;
    private Trigger crTrigger;
    private Trigger updTrigger;
    private Integer handlerRequired;
    private Integer groupHandlerAllowed;
    private Set catrelationSet = new HashSet(); //persistent
    private Set catrelationchildSet = new HashSet(); //persistent
    private Set cprstatusSet;
    private Set mailImportSet;
    private Set registrationSet;

    public Category(String id) {
        this.id = id;
    }

    public Category(String name, Workflow workflow, Task task, Longtext template, boolean handlerRequired, boolean groupHandlerAllowed) {
        this.name = name;
        this.workflow = workflow;
        this.task = task;
        this.template = template;
        this.handlerRequired = handlerRequired ? 1 : 0;
        this.groupHandlerAllowed = groupHandlerAllowed ? 1 : 0;
    }

    public Category(String name, String workflowId, String taskId, String templateId, boolean handlerRequired, boolean groupHandlerAllowed) throws GranException {
        this(name, workflowId != null ? new Workflow(workflowId) : null, taskId != null ? new Task(taskId) : null, templateId != null ? KernelManager.getFind().findLongtext(templateId) : null, handlerRequired, groupHandlerAllowed);
    }

    public Category() {
    }

    public Category(Workflow workflow, Task task) {
        this.workflow = workflow;
        this.task = task;
    }

    public Category(String workflowId, String taskId) {
        this(workflowId != null ? new Workflow(workflowId) : null, taskId != null ? new Task(taskId) : null);
    }

    public Workflow getWorkflow() {
        return this.workflow;
    }

    public void setWorkflow(Workflow workflow) {
        this.workflow = workflow;
    }

    public void setWorkflow(String workflowId) {
        this.workflow = new Workflow(workflowId);
    }


    public Task getTask() {
        return this.task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Integer getHandlerRequired() {
        return handlerRequired;
    }

    public void setHandlerRequired(Integer handlerRequired) {
        this.handlerRequired = handlerRequired;
    }

    public Set getCatrelationSet() {
        return this.catrelationSet;
    }

    public void setCatrelationSet(Set catrelationSet) {
        this.catrelationSet = catrelationSet;
    }

    public Set getCatrelationchildSet() {
        return this.catrelationchildSet;
    }

    public void setCatrelationchildSet(Set catrelationSet) {
        this.catrelationchildSet = catrelationSet;
    }

    public Set getCprstatusSet() {
        return this.cprstatusSet;
    }

    public void setCprstatusSet(Set cprstatusSet) {
        this.cprstatusSet = cprstatusSet;
    }

    public Set getMailImportSet() {
        return this.mailImportSet;
    }

    public void setMailImportSet(Set mailImportSet) {
        this.mailImportSet = mailImportSet;
    }

    public Set getRegistrationSet() {
        return registrationSet;
    }

    public void setRegistrationSet(Set registrationSet) {
        this.registrationSet = registrationSet;
    }

    public Trigger getCrTrigger() {
        return crTrigger;
    }

    public void setCrTrigger(Trigger crTrigger) {
        this.crTrigger = crTrigger;
    }

    public void setCrTrigger(String triggerId) {
        this.crTrigger = triggerId != null ? new Trigger(triggerId) : null;
    }

    public Trigger getUpdTrigger() {
        return updTrigger;
    }

    public void setUpdTrigger(Trigger updTrigger) {
        this.updTrigger = updTrigger;
    }

    public void setUpdTrigger(String triggerId) {
        this.updTrigger = triggerId != null ? new Trigger(triggerId) : null;
    }

    public boolean equals(Object obj) {
        return obj instanceof Category && ((Category) obj).getId().equals(this.id);
    }

    public Longtext getTemplate() {
        return template;
    }

    public void setTemplate(Longtext template) {
        this.template = template;
    }

    public Integer getGroupHandlerAllowed() {
        return groupHandlerAllowed;
    }

    public void setGroupHandlerAllowed(Integer groupHandlerAllowed) {
        this.groupHandlerAllowed = groupHandlerAllowed;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getBudget() {
        return budget;
    }

    public void setBudget(String budget) {
        this.budget = budget;
    }

    public String getPreferences() {
        return preferences;
    }

    public void setPreferences(String preferences) {
        this.preferences = preferences;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

}
