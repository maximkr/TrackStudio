package com.trackstudio.soap.bean;

public class AclBean {

    private String id;

    private String taskId;
    private String toUserId;
    private String userId;
    private String groupId;
    private String prstatusId;
    private String ownerId;
    private boolean override;

    public AclBean() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPrstatusId() {
        return prstatusId;
    }

    public void setPrstatusId(String prstatusId) {
        this.prstatusId = prstatusId;
    }

    public String toString() {
        return getClass().getName() + '[' +
                "id=" + id + ", " +
                "taskId=" + taskId + ", " +
                "userId=" + userId + ", " +
                "ownerId=" + ownerId + ", " +
                "prstatusId=" + prstatusId + ']';
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public boolean isOverride() {
        return override;
    }

    public void setOverride(boolean override) {
        this.override = override;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
