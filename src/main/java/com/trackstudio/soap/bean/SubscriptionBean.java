package com.trackstudio.soap.bean;

public class SubscriptionBean {

    private String id;
    private long startdate;
    private long stopdate;
    private long nextrun;
    private int interval;
    private String userId;
    private String name;
    private String taskId;
    private String filterId;
    private String groupId;

    public SubscriptionBean() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getStartdate() {
        return startdate;
    }

    public void setStartdate(long startdate) {
        this.startdate = startdate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getStopdate() {
        return stopdate;
    }

    public void setStopdate(long stopdate) {
        this.stopdate = stopdate;
    }

    public long getNextrun() {
        return nextrun;
    }

    public void setNextrun(long nextrun) {
        this.nextrun = nextrun;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getFilterId() {
        return filterId;
    }

    public void setFilterId(String filterId) {
        this.filterId = filterId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
