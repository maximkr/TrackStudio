package com.trackstudio.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.manager.KernelManager;

/**
 * Describes message (bug-note)
 */

public class Message implements Serializable {
    private String id; //identifier
    private String description; //persistent
    private Calendar time; //persistent
    private Long hrs; //persistent
    private Calendar deadline; //persistent
    private Long budget; //persistent

    private Usersource handler;
    private Resolution resolution;
    private Mstatus mstatus;
    private User submitter;
    private Task task;
    private Longtext longtext;
    private Priority priority;

    public Longtext getLongtext() {
        return longtext;
    }

    public void setLongtext(Longtext longtext) {
        this.longtext = longtext;
    }

    public void setLongtext(String longtextId) throws GranException {
        this.longtext = KernelManager.getFind().findLongtext(longtextId);
    }

    public Message(String id) {
        this.id = id;
    }

    public Message(String taskId, String userId, String mstatusId, Long hrs, String handlerId, String resolutionId, String priorityId,
                   Calendar deadline, Long budget, Calendar time) {
        this(new Task(taskId), new User(userId), mstatusId != null ? new Mstatus(mstatusId) : null, hrs, handlerId != null ? new Usersource(handlerId) : null,
                resolutionId != null ? new Resolution(resolutionId) : null, priorityId, deadline, budget, time);
    }

    public Message(Task task, User user, Mstatus mstatus, Long hrs, Usersource handler, Resolution resolution, String priorityId, Calendar deadline,
                   Long budget, Calendar time) {
        this.submitter = user;
        this.task = task;
        if (time == null) {
            this.time = new GregorianCalendar();
            this.time.setTimeInMillis(System.currentTimeMillis());
        } else
            this.time = time;
        this.mstatus = mstatus;
        this.hrs = hrs != null && hrs.equals(0L) ? null : hrs;
        this.handler = handler;
        this.resolution = resolution;
        this.priority = priorityId != null ? new Priority(priorityId) : null;
        this.deadline = deadline;
        this.budget = budget;
    }

    public Message() {
    }

    public Message(Task task, User user, Mstatus mstatus) {
        this.task = task;
        this.submitter = user;
        this.mstatus = mstatus;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getSubmitter() {
        return submitter;
    }

    public void setSubmitter(User user) {
        this.submitter = user;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Calendar getTime() {
        return this.time;
    }

    public void setTime(Calendar time) {
        this.time = time;
    }

    public Mstatus getMstatus() {
        return this.mstatus;
    }

    public void setMstatus(Mstatus mstatus) {
        this.mstatus = mstatus;
    }

    public Long getHrs() {
        return this.hrs;
    }

    public void setHrs(Long hrs) {
        this.hrs = hrs != null && hrs.equals(0L) ? null : hrs;
    }

    public Usersource getHandler() {
        return this.handler;
    }

    public void setHandler(Usersource handler) {
        this.handler = handler;
    }

    public Priority getPriority() {
        return this.priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Calendar getDeadline() {
        return this.deadline;
    }

    public void setDeadline(Calendar deadline) {
        this.deadline = deadline;
    }

    public Long getBudget() {
        return this.budget;
    }

    public void setBudget(Long budget) {
        this.budget = budget;
    }

    public Resolution getResolution() {
        return this.resolution;
    }

    public void setResolution(Resolution resolution) {
        this.resolution = resolution;
    }

    public boolean equals(Object obj) {
        return obj instanceof Message && ((Message) obj).getId().equals(this.id);
    }


    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
