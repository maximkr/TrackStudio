package com.trackstudio.soap.bean;

import com.trackstudio.kernel.manager.SafeString;

public class TaskBean {

    private String id;
    private long abudget;
    private long budget;
    private long closedate;
    private long deadline;
    private String description;
    private String name;
    private String nameCutted;
    private String number;
    private String shortname;
    private long submitdate;
    private long updatedate;
    private String categoryId;
    private String statusId;
    private String resolutionId;
    private String priorityId;
    private String submitterId;
    private String handlerUserId;
    private String handlerGroupId;
    private String parentId;
    private String workflowId;
    private int childrenCount;
    private int messageCount;
    private boolean hasAttachments;
    private boolean onSight;

    public TaskBean() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getAbudget() {
        return abudget;
    }

    public void setAbudget(long abudget) {
        this.abudget = abudget;
    }

    public long getBudget() {
        return budget;
    }

    public void setBudget(long budget) {
        this.budget = budget;
    }

    public long getClosedate() {
        return closedate;
    }

    public void setClosedate(long closedate) {
        this.closedate = closedate;
    }

    public long getDeadline() {
        return deadline;
    }

    public void setDeadline(long deadline) {
        this.deadline = deadline;
    }

    public String getDescription() {
        return SafeString.createSafeString(description).toString();
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return SafeString.createSafeString(name).toString();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameCutted() {
        return SafeString.createSafeString(nameCutted).toString();
    }

    public void setNameCutted(String nameCutted) {
        this.nameCutted = nameCutted;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getShortname() {
        return SafeString.createSafeString(shortname).toString();
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

    public long getSubmitdate() {
        return submitdate;
    }

    public void setSubmitdate(long submitdate) {
        this.submitdate = submitdate;
    }

    public long getUpdatedate() {
        return updatedate;
    }

    public void setUpdatedate(long updatedate) {
        this.updatedate = updatedate;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getStatusId() {
        return statusId;
    }

    public void setStatusId(String statusId) {
        this.statusId = statusId;
    }

    public String getResolutionId() {
        return resolutionId;
    }

    public void setResolutionId(String resolutionId) {
        this.resolutionId = resolutionId;
    }

    public String getPriorityId() {
        return priorityId;
    }

    public void setPriorityId(String priorityId) {
        this.priorityId = priorityId;
    }

    public String getSubmitterId() {
        return submitterId;
    }

    public void setSubmitterId(String submitterId) {
        this.submitterId = submitterId;
    }

    public String getHandlerUserId() {
        return handlerUserId;
    }

    public void setHandlerUserId(String handlerUserId) {
        this.handlerUserId = handlerUserId;
    }

    public String getHandlerGroupId() {
        return handlerGroupId;
    }

    public void setHandlerGroupId(String handlerGroupId) {
        this.handlerGroupId = handlerGroupId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    public int getChildrenCount() {
        return childrenCount;
    }

    public void setChildrenCount(int i) {
        this.childrenCount = i;
    }

    public int getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(int i) {
        this.messageCount = i;
    }

    public boolean isHasAttachments() {
        return hasAttachments;
    }

    public void setHasAttachments(boolean b) {
        hasAttachments = b;
    }

    public boolean isOnSight() {
        return onSight;
    }

    public void setOnSight(boolean b) {
        onSight = b;
    }

    public String toString() {
        return getName() + " [#" + getNumber() + "]";
    }

    public String getString() {
        return getName() + " [#" + getNumber() + "]";
    }

    
}
