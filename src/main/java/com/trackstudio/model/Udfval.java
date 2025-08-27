package com.trackstudio.model;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Describes value of UDF
 */
public class Udfval implements Serializable {

    private String id; //identifier
    private String str; //persistent
    private Double num; //persistent
    private Calendar dat; //persistent

    private User user;
    private Task task;
    private Udflist udflist;
    private Udf udf;
    private Udfsource udfsource;

    private Longtext longtext;

    public Longtext getLongtext() {
        return longtext;
    }

    public void setLongtext(Longtext longtext) {
        this.longtext = longtext;
    }

    public Udfval(String id) {
        this.id = id;
    }

    public Udfval(String str, Double num, Calendar dat, Udflist udflist, Task task, User user, Udf udf, Udfsource udfsource) {
        this.str = str;
        this.num = num;
        this.dat = dat;
        this.udflist = udflist;
        this.udf = udf;
        this.udfsource = udfsource;
        this.user = user;
        this.task = task;
    }

    public Udfval(String str, Double num, Calendar dat, String udflistId, String taskId, String userId, String udfId, String udfsourceId) {
        this(str, num, dat, udflistId != null ? new Udflist(udflistId) : null, taskId != null ? new Task(taskId) : null, userId != null ? new User(userId) : null, udfId != null ? new Udf(udfId) : null,
                udfsourceId != null ? new Udfsource(udfsourceId) : null);
    }

    public Udfval() {
    }

    public Udfval(Udf udf, Udfsource udfsource) {
        this.udf = udf;
        this.udfsource = udfsource;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStr() {
        return this.str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    public /*Integer*/Double getNum() {

        return this.num;
    }

    public void setNum(/*Integer*/Double num) {
        this.num = num;
    }

    public Calendar getDat() {
        return this.dat;
    }

    public void setDat(Calendar dat) {
        this.dat = dat;
    }

    public Udflist getUdflist() {
        return this.udflist;
    }

    public void setUdflist(Udflist udflist) {
        this.udflist = udflist;
    }

    public Udf getUdf() {
        return this.udf;
    }

    public void setUdf(Udf udf) {
        this.udf = udf;
    }

    public Udfsource getUdfsource() {
        return this.udfsource;
    }

    public void setUdfsource(Udfsource udfsource) {
        this.udfsource = udfsource;
    }

    public boolean equals(Object obj) {
        return obj instanceof Udfval && ((Udfval) obj).getId().equals(this.id);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
