package com.trackstudio.model;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Describes filter subscription settings for
 * specified user, task and filter
 */
public class Subscription implements Serializable {
    private String id; //identifier

    private Calendar startdate; //persistent
    private Calendar stopdate; //persistent
    private Calendar nextrun; //persistent
    private Integer interval; //persistent
    private String name; //persistent

    private Usersource user;
    private Filter filter;
    private Task task;
    private String template;

    public Subscription(String id) {
        this.id = id;
    }


    public Subscription(String name, Usersource user, Filter filter, Task task, Calendar startdate, Calendar stopdate, Calendar nextrun, Integer interval, String template) {
        this.name = name;
        this.user = user;
        this.filter = filter;
        this.task = task;
        this.startdate = startdate;
        this.stopdate = stopdate;
        this.nextrun = nextrun;
        this.interval = interval;
        this.template = template;
    }

    public Subscription(String name, String userId, String filterId, String taskId, Calendar startdate, Calendar stopdate,
                        Calendar nextrun, Integer interval, String template) {
        this(name, userId != null ? new Usersource(userId) : null, filterId != null ? new Filter(filterId) : null, taskId != null ? new Task(taskId) : null,
                startdate, stopdate, nextrun, interval, template);

    }

    public Subscription() {
    }

    public Subscription(String name, Usersource user, Filter filter, Task task) {
        this.name = name;
        this.user = user;
        this.filter = filter;
        this.task = task;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Usersource getUser() {
        return this.user;
    }

    public void setUser(Usersource user) {
        this.user = user;
    }

    public Filter getFilter() {
        return this.filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public void setFilter(String filterId) {
        this.filter = new Filter(filterId);
    }

    public Task getTask() {
        return this.task;
    }

    public void setTask(Task task) {
        this.task = task;
    }


    public Calendar getStartdate() {
        return this.startdate;
    }

    public void setStartdate(Calendar startdate) {
        this.startdate = startdate;
    }

    public Calendar getStopdate() {
        return this.stopdate;
    }

    public void setStopdate(Calendar stopdate) {
        this.stopdate = stopdate;
    }

    public Calendar getNextrun() {
        return this.nextrun;
    }

    public void setNextrun(Calendar nextrun) {
        this.nextrun = nextrun;
    }

    public Integer getInterval() {
        return this.interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    public boolean equals(Object obj) {
        return obj instanceof Subscription && ((Subscription) obj).getId().equals(this.id);
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
