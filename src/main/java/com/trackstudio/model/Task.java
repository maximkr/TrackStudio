package com.trackstudio.model;


import java.io.Serializable;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

/**
 * Describes tasks
 */
public class Task extends Named implements Serializable {


    private String shortname; //persistent

    private Calendar submitdate; //persistent
    private Calendar updatedate; //persistent
    private Calendar closedate; //persistent
    private String description; //persistent
    private Long abudget; //persistent
    private Long budget; //persistent
    private Calendar deadline; //persistent
    private String number;

    private Set filterSet = new HashSet(); //persistent
    private Set subscriptionSet = new HashSet(); //persistent
    private Set notificationSet = new HashSet(); //persistent
    private Set udfValSet = new HashSet(); //persistent
    private Set categorySet = new HashSet(); //persistent
    private Set workflowSet = new HashSet(); //persistent
    private Set udfsourceSet = new HashSet(); //persistent
    private Set childSet = new HashSet(); //persistent
    private Set templateSet = new HashSet(); //persistent
    private Set reportSet = new HashSet(); //persistent
    private Set bookmarkSet = new HashSet(); //persistent
    private Set mailImportSet;
    private Set registrationSet;
    private Set defaultProjectSet;

    private Set aclSet;
    private Set currentFilterSet;
    private Set messageSet;

    private User submitter;
    private Usersource handler;
    private Task parent;
    private Category category;
    private Status status;
    private Resolution resolution;
    private Priority priority;

    private Longtext longtext;

    public Longtext getLongtext() {
        return longtext;
    }

    public void setLongtext(Longtext longtext) {
        this.longtext = longtext;
    }

    public Task(String id) {
        this.id = id;
    }

    public Task(String shortname, String name, Calendar submitdate, Calendar updatedate, Calendar closedate, String description, Long abudget, Long budget, Calendar deadline, Category category, Status status, Resolution resolution, Priority priority, User submitter, Usersource handler, Task parent) {
        this.shortname = shortname;
        this.name = name;
        this.submitdate = submitdate;
        this.updatedate = updatedate;
        this.closedate = closedate;
        this.description = description;
        this.abudget = abudget;
        this.budget = budget == null || budget.equals(0L) ? null : budget;
        this.deadline = deadline;
        this.category = category;
        this.status = status;
        this.resolution = resolution;
        this.priority = priority;
        this.submitter = submitter;
        this.handler = handler;
        this.parent = parent;
    }

    public Task() {
    }

    public Task(String name, String categoryId, Status status, Resolution resolution, User submitter, Usersource handler, Task parent) {
        this(name, new Category(categoryId), status, resolution, submitter, handler, parent);
    }


    public Task(String name, Category category, Status status, Resolution resolution, User submitter, Usersource handler, Task parent) {
        this.name = name;
        this.category = category;
        this.status = status;
        this.resolution = resolution;
        this.submitter = submitter;
        this.handler = handler;
        this.parent = parent;
    }

    public Task(String name, String categoryId, String statusId, String userId, String parentId) {
        this.name = name;
        this.category = new Category(categoryId);
        this.status = new Status(statusId);
        this.submitter = new User(userId);
        this.parent = parentId != null ? new Task(parentId) : null;
    }

    public Task(String name, Category category, String statusId, Resolution resolution, String submitterId,
                String handlerId, String parentId) {
        this(name, category, statusId != null ? new Status(statusId) : null, resolution, submitterId != null ? new User(submitterId) : null,
                handlerId != null ? new Usersource(handlerId) : null, parentId != null ? new Task(parentId) : null);
    }


    public String getShortname() {
        return this.shortname;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
    }


    public Calendar getSubmitdate() {
        return this.submitdate;
    }

    public void setSubmitdate(Calendar submitdate) {
        this.submitdate = submitdate;
    }


    public Calendar getUpdatedate() {
        return this.updatedate;
    }

    public void setUpdatedate(Calendar updatedate) {
        this.updatedate = updatedate;
    }


    public Calendar getClosedate() {
        return this.closedate;
    }

    public void setClosedate(Calendar closedate) {
        this.closedate = closedate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {

        this.description = description;
    }

    public Long getAbudget() {
        return this.abudget;
    }

    public void setAbudget(Long abudget) {
        this.abudget = abudget;
    }

    public Long getBudget() {
        return this.budget;
    }

    public void setBudget(Long budget) {
        this.budget = budget;
    }

    public Calendar getDeadline() {
        return this.deadline;
    }

    public void setDeadline(Calendar deadline) {
        this.deadline = deadline;
    }

    public Category getCategory() {
        return this.category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Resolution getResolution() {
        return this.resolution;
    }

    public void setResolution(Resolution resolution) {
        this.resolution = resolution;
    }

    public void setResolution(String resolutionId) {
        this.resolution = resolutionId != null ? new Resolution(resolutionId) : null;
    }

    public Priority getPriority() {
        return this.priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public void setPriority(String priorityId) {
        this.priority = priorityId != null ? new Priority(priorityId) : null;
    }


    public User getSubmitter() {
        return this.submitter;
    }

    public void setSubmitter(User submitter) {
        this.submitter = submitter;
    }

    public Usersource getHandler() {
        return this.handler;
    }

    public String getHandlerId() {
        if (this.handler != null)
            return this.handler.getId();
        else return null;
    }

    public String getHandlerUserId() {
        if (handler != null && handler.getUser() != null)
            return handler.getUser().getId();
        else return null;
    }

    public String getHandlerGropuId() {
        if (handler != null && handler.getPrstatus() != null)
            return handler.getPrstatus().getId();
        else return null;
    }

    public void setHandler(Usersource handler) {
        this.handler = handler;
    }

    public void setHandler(String handlerId) {
        this.handler = handlerId != null ? new Usersource(handlerId) : null;
    }

    public Task getParent() {
        return this.parent;
    }

    public void setParent(Task parent) {
        this.parent = parent;
    }

    public void setParent(String parentId) {
        this.parent = parentId != null ? new Task(parentId) : null;
    }


    public Set getFilterSet() {
        return this.filterSet;
    }

    public void setFilterSet(Set filterSet) {
        this.filterSet = filterSet;
    }

    public Set getSubscriptionSet() {
        return this.subscriptionSet;
    }

    public void setSubscriptionSet(Set subscriptionSet) {
        this.subscriptionSet = subscriptionSet;
    }

    public Set getNotificationSet() {
        return this.notificationSet;
    }

    public void setNotificationSet(Set subscriberSet) {
        this.notificationSet = subscriberSet;
    }


    public Set getCategorySet() {
        return this.categorySet;
    }

    public void setCategorySet(Set categorySet) {
        this.categorySet = categorySet;
    }

    public Set getWorkflowSet() {
        return this.workflowSet;
    }

    public void setWorkflowSet(Set workflowSet) {
        this.workflowSet = workflowSet;
    }

    public Set getUdfsourceSet() {
        return this.udfsourceSet;
    }

    public void setUdfsourceSet(Set udfsourceSet) {
        this.udfsourceSet = udfsourceSet;
    }

    public Set getChildSet() {
        return childSet;
    }

    public void setChildSet(Set childSet) {
        this.childSet = childSet;
    }

    public Set getReportSet() {
        return reportSet;
    }


    public void setReportSet(Set reportSet) {
        this.reportSet = reportSet;
    }

    public Set getMailImportSet() {
        return this.mailImportSet;
    }

    public void setMailImportSet(Set mailImportSet) {
        this.mailImportSet = mailImportSet;
    }

    public Set getDefaultProjectSet() {
        return this.defaultProjectSet;
    }

    public void setDefaultProjectSet(Set defaultProjectSet) {
        this.defaultProjectSet = defaultProjectSet;
    }

    public Set getAclSet() {
        return aclSet;
    }

    public void setAclSet(Set aclSet) {
        this.aclSet = aclSet;
    }

    public Set getCurrentFilterSet() {
        return currentFilterSet;
    }

    public void setCurrentFilterSet(Set currentFilterSet) {
        this.currentFilterSet = currentFilterSet;
    }

    public Set getMessageSet() {
        return messageSet;
    }

    public void setMessageSet(Set messageSet) {
        this.messageSet = messageSet;
    }

    public String getNumber() {
        return this.number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Set getRegistrationSet() {
        return registrationSet;
    }

    public void setRegistrationSet(Set registrationSet) {
        this.registrationSet = registrationSet;
    }

    public boolean equals(Object obj) {
        return obj instanceof Task ? ((Task) obj).getId().equals(this.id) : obj.toString().equals(this.id);
    }

    public String toString() {
        return id;
    }

    public Set getUdfValSet() {
        return udfValSet;
    }

    public void setUdfValSet(Set udfValSet) {
        this.udfValSet = udfValSet;
    }

    public Set getTemplateSet() {
        return templateSet;
    }

    public void setTemplateSet(Set templateSet) {
        this.templateSet = templateSet;
    }

    public Set getBookmarkSet() {
        return bookmarkSet;
    }

    public void setBookmarkSet(Set bookmarkSet) {
        this.bookmarkSet = bookmarkSet;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
