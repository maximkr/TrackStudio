package com.trackstudio.form;

public class NotifySubscribeForm extends BaseForm {

    private String name;
    private String condition;
    private String template;
    private String filterId;
    private String oldFilterId;
    private String notificationId;
    private String subscriptionId;
    private String task;
    private String user;
    private String startDate;
    private String stopDate;
    private String nextRun;
    private String interval;

    private boolean fireNewTask;
    private boolean fireUpdatedTask;
    private boolean fireNewAttachment;
    private boolean fireNewMessage;
    private boolean fireNotI;

    public boolean isFireNewTask() {
        return fireNewTask;
    }

    public void setFireNewTask(boolean fireNewTask) {
        this.fireNewTask = fireNewTask;
    }

    public boolean isFireUpdatedTask() {
        return fireUpdatedTask;
    }

    public void setFireUpdatedTask(boolean fireUpdatedTask) {
        this.fireUpdatedTask = fireUpdatedTask;
    }

    public boolean isFireNewAttachment() {
        return fireNewAttachment;
    }

    public void setFireNewAttachment(boolean fireNewAttachment) {
        this.fireNewAttachment = fireNewAttachment;
    }

    public boolean isFireNewMessage() {
        return fireNewMessage;
    }

    public void setFireNewMessage(boolean fireNewMessage) {
        this.fireNewMessage = fireNewMessage;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public String getNextRun() {
        return nextRun;
    }

    public void setNextRun(String nextRun) {
        this.nextRun = nextRun;
    }

    public String getStopDate() {
        return stopDate;
    }

    public void setStopDate(String stopDate) {
        this.stopDate = stopDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(String subscriptionId) {
        if (isMutable())
            this.subscriptionId = subscriptionId;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        if (isMutable())
            this.notificationId = notificationId;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String type) {
        this.template = type;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getFilterId() {
        return filterId;
    }

    public void setFilterId(String filterId) {
        if (isMutable())
            this.filterId = filterId;
    }

    public String getOldFilterId() {
        return oldFilterId;
    }

    public void setOldFilterId(String oldFilterId) {
        this.oldFilterId = oldFilterId;
    }

    public boolean isFireNotI() {
        return fireNotI;
    }

    public void setFireNotI(boolean fireNotI) {
        this.fireNotI = fireNotI;
    }
}
