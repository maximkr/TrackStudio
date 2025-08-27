package com.trackstudio.model;

import java.io.Serializable;

/**
 * This class defines relation between message type and user group
 */
public class Mprstatus implements Serializable {
    private String id; //identifier
    private String type; //persistent

    private Mstatus mstatus;
    private Prstatus prstatus;

    public Mprstatus(String id) {
        this.id = id;
    }

    public Mprstatus(String type, String mstatusId, String prstatusId) {
        this.type = type;
        this.mstatus = new Mstatus(mstatusId);
        this.prstatus = new Prstatus(prstatusId);
    }


    public Mprstatus(String type, Mstatus mstatus, Prstatus prstatus) {
        this.type = type;
        this.mstatus = mstatus;
        this.prstatus = prstatus;
    }

    public Mprstatus() {
    }

    public Mprstatus(Mstatus mstatus, Prstatus prstatus) {
        this.mstatus = mstatus;
        this.prstatus = prstatus;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Mstatus getMstatus() {
        return this.mstatus;
    }

    public void setMstatus(Mstatus mstatus) {
        this.mstatus = mstatus;
    }

    public Prstatus getPrstatus() {
        return this.prstatus;
    }

    public void setPrstatus(Prstatus prstatus) {
        this.prstatus = prstatus;
    }

    public boolean equals(Object obj) {
        return obj instanceof Mprstatus && ((Mprstatus) obj).getId().equals(this.id);
    }


    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
