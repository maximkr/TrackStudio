package com.trackstudio.soap.bean;

public class UserSliderBean {

    private UserBean[] users;
    private int page;
    private int pageSize;


    private String[] sortOrder;
    private String id;

    public UserSliderBean() {

    }

    public UserBean[] getUsers() {
        return users;
    }

    public void setUsers(UserBean[] users) {
        this.users = users;
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

    public String[] getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String[] sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


}
