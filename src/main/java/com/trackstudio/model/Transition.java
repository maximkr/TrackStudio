package com.trackstudio.model;

import java.io.Serializable;

/**
 * Describes transitions between 2 states. Each mstatus
 * is a set of transitions
 */
public class Transition implements Serializable {
    private String id; //identifier

    private Status start;
    private Status finish;
    private Mstatus mstatus;

    public Transition(String id) {
        this.id = id;
    }

    public Transition(Status start, Status finish, Mstatus mstatus) {
        this.start = start;
        this.finish = finish;
        this.mstatus = mstatus;
    }

    public Transition(String startId, String finishId, String mstatusId) {
        this(startId != null ? new Status(startId) : null, finishId != null ? new Status(finishId) : null, mstatusId != null ? new Mstatus(mstatusId) : null);
    }

    public Transition() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Status getStart() {
        return this.start;
    }

    public void setStart(Status start) {
        this.start = start;
    }

    public Status getFinish() {
        return this.finish;
    }

    public void setFinish(Status finish) {
        this.finish = finish;
    }

    public void setFinish(String finishId) {
        this.finish = finishId != null ? new Status(finishId) : null;
    }


    public Mstatus getMstatus() {
        return this.mstatus;
    }

    public void setMstatus(Mstatus mstatus) {
        this.mstatus = mstatus;
    }

    public boolean equals(Object obj) {
        return obj instanceof Transition && ((Transition) obj).getId().equals(this.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

}
