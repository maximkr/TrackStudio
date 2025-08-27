package com.trackstudio.model;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 10.07.2007
 * Time: 14:18:47
 * To change this template use File | Settings | File Templates.
 */
public class Umstatus implements Serializable {
    private String id; //identifier
    private String type; //persistent
    private Udf udf;
    private Mstatus mstatus;


    public Umstatus(String id) {
        this.id = id;
    }

    public Umstatus(Udf udf, Mstatus mstatus, String type) {
        this.udf = udf;
        this.type = type;
        this.mstatus = mstatus;

    }


    public Umstatus(String udfId, String mstatusId, String type) {
        this(udfId != null ? new Udf(udfId) : null, mstatusId != null ? new Mstatus(mstatusId) : null, type);
    }

    public Umstatus() {
    }


    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Udf getUdf() {
        return this.udf;
    }

    public void setUdf(Udf udf) {
        this.udf = udf;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public boolean equals(Object obj) {
        return obj instanceof Umstatus && ((Umstatus) obj).getId().equals(this.id);
    }


    public Mstatus getMstatus() {
        return mstatus;
    }

    public void setMstatus(Mstatus mstatus) {
        this.mstatus = mstatus;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
