package com.trackstudio.soap.bean;

public class TaskReferenceBean {
    private UdfValueBean udf;
    private TaskBean[] tasks;

    public UdfValueBean getUdf() {
        return udf;
    }

    public void setUdf(UdfValueBean udf) {
        this.udf = udf;
    }

    public TaskBean[] getTasks() {
        return tasks;
    }

    public void setTasks(TaskBean[] tasks) {
        this.tasks = tasks;
    }

    public TaskReferenceBean() {
    }
}
