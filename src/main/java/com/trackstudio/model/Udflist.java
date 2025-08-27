package com.trackstudio.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Describes available values in dropdown UDF list
 */
public class Udflist implements Serializable {
    private String id; //identifier
    private String val; //persistent

    private Set udfvalSet = new HashSet(); //persistent

    private Udf udf;


    public Udflist(String val, Udf udf) {
        this.val = val;
        this.udf = udf;
    }

    public Udflist(String val, String udfId) {
        this(val, udfId != null ? new Udf(udfId) : null);
    }

    public Udflist(String id) {
        this.id = id;
    }

    public Udflist() {
    }

    public Udflist(Udf udf) {
        this.udf = udf;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVal() {
        return this.val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public Udf getUdf() {
        return this.udf;
    }

    public void setUdf(Udf udf) {
        this.udf = udf;
    }

    public Set getUdfvalSet() {
        return this.udfvalSet;
    }

    public void setUdfvalSet(Set udfvalSet) {
        this.udfvalSet = udfvalSet;
    }

    public boolean equals(Object obj) {
        return obj instanceof Udflist && ((Udflist) obj).getId().equals(this.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
