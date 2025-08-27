package com.trackstudio.model;

import java.io.Serializable;

/**
 * Describe relation between udf and prstatus.
 * Type defines the type of relation and set, can
 * user of specified user group (prstatus)
 * view/edit specified udf
 */
public class Uprstatus implements Serializable {
    private String id; //identifier
    private String type; //persistent
    private Udf udf;
    private Prstatus prstatus;

    public Uprstatus(String id) {
        this.id = id;
    }

    public Uprstatus(String type, Udf udf, Prstatus prstatus) {
        this.udf = udf;
        this.type = type;
        this.prstatus = prstatus;
    }

    public Uprstatus(String type, String udfId, String prstatusId) {
        this(type, udfId != null ? new Udf(udfId) : null, prstatusId != null ? new Prstatus(prstatusId) : null);
    }

    public Uprstatus() {
    }

    public Uprstatus(Udf udf, Prstatus prstatus) {
        this.udf = udf;
        this.prstatus = prstatus;
    }

    public Uprstatus(String udfId, String prstatusId) {
        this(udfId != null ? new Udf(udfId) : null, prstatusId != null ? new Prstatus(prstatusId) : null);
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

    public Prstatus getPrstatus() {
        return this.prstatus;
    }

    public void setPrstatus(Prstatus prstatus) {
        this.prstatus = prstatus;
    }

    public boolean equals(Object obj) {
        return obj instanceof Uprstatus && ((Uprstatus) obj).getId().equals(this.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

}
