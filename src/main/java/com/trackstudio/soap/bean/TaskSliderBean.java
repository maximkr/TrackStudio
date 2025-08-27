package com.trackstudio.soap.bean;

public class TaskSliderBean {

    private TaskBean[] tasks;
    private int page;
    private int pageSize;
    private int pagesCount;
    private String[] sortOrder;

    public TaskSliderBean() {

    }

    public TaskBean[] getTasks() {
        return tasks;
    }

    public void setTasks(TaskBean[] tasks) {
        this.tasks = tasks;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPagesCount() {
        return pagesCount;
    }

    public void setPagesCount(int pagesCount) {
        this.pagesCount = pagesCount;
    }


    public String[] getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String[] sortOrder) {
        this.sortOrder = sortOrder;
    }


}
