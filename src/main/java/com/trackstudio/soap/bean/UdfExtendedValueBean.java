package com.trackstudio.soap.bean;

public class UdfExtendedValueBean extends UdfValueBean {

    public UdfExtendedValueBean() {
    }

    public UdfExtendedValueBean(UdfValueBean bean) {
        this.setUdfId(bean.getUdfId());
        this.setValue(bean.getValue());
    }

    private String str;
    private long dat;
    private double num;

    public long getDat() {
        return dat;
    }

    public void setDat(long dat) {
        this.dat = dat;
    }

    public double getNum() {
        return num;
    }

    public void setNum(double num) {
        this.num = num;
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    UdfBean udf;
    String scriptValue;
    UdflistBean[] udflist;
    TaskBean[] tasks;
    UserBean[] users;

    public UdfBean getUdf() {
        return udf;
    }

    public void setUdf(UdfBean udf) {
        this.udf = udf;
    }

    public String getScriptValue() {
        return scriptValue;
    }

    public void setScriptValue(String scriptValue) {
        this.scriptValue = scriptValue;
    }

    public UdflistBean[] getUdflist() {
        return udflist;
    }

    public void setUdflist(UdflistBean[] udflist) {
        this.udflist = udflist;
    }

    public TaskBean[] getTasks() {
        return tasks;
    }

    public void setTasks(TaskBean[] tasks) {
        this.tasks = tasks;
    }

    public UserBean[] getUsers() {
        return users;
    }

    public void setUsers(UserBean[] users) {
        this.users = users;
    }
}
