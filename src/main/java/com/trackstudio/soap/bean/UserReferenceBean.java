package com.trackstudio.soap.bean;

public class UserReferenceBean {
    private UdfValueBean udf;
    private UserBean[] users;

    public UdfValueBean getUdf() {
        return udf;
    }

    public void setUdf(UdfValueBean udf) {
        this.udf = udf;
    }

    public UserBean[] getUsers() {
        return users;
    }

    public void setUsers(UserBean[] users) {
        this.users = users;
    }

    public UserReferenceBean() {
    }
}
