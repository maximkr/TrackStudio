package com.trackstudio.soap.bean;


public class TaskAttachmentBean {
    protected String id; //identifier
        protected String description;
        protected String name;
        protected String userId;

        protected long size;
        protected long lastModified;

        public TaskAttachmentBean() {
        }

        public String getId() {
            return this.id;
        }

        public void setId(String id) {
            this.id = id;
        }


        public String getUserId() {
            return userId;
        }

        public String getDescription() {
            return this.description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public long getSize() {
            return size;
        }

        public void setSize(long size) {
            this.size = size;
        }

        public long getLastModified() {
            return lastModified;
        }

        public void setLastModified(long lastModified) {
            this.lastModified = lastModified;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

    protected String taskId;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String toString() {
        return getClass().getName() + '[' +
                "id=" + id + ", " +
                "taskId=" + taskId + ", " +
                "userId=" + userId + ", " +
                "name=" + name + ", " +
                "description=" + description + ']';
    }
}
