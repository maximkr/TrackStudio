package com.trackstudio.model;

import java.io.Serializable;
import java.util.Set;

public class Trigger implements Serializable {
    private String id; //identifier
    private String before;
    private String insteadOf;
    private String after;

    private Set categoryCrSet;
    private Set categoryUpdSet;
    private Set mstatusSet;

    public Trigger() {

    }

    public Trigger(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBefore() {
        return before;
    }

    public void setBefore(String before) {
        this.before = before;
    }


    public String getInsteadOf() {
        return insteadOf;
    }

    public void setInsteadOf(String insteadof) {
        this.insteadOf = insteadof;
    }


    public String getAfter() {
        return after;
    }

    public void setAfter(String after) {
        this.after = after;
    }


    public Set getCategoryCrSet() {
        return categoryCrSet;
    }

    public void setCategoryCrSet(Set categoryCrSet) {
        this.categoryCrSet = categoryCrSet;
    }

    public Set getCategoryUpdSet() {
        return categoryUpdSet;
    }

    public void setCategoryUpdSet(Set categoryUpdSet) {
        this.categoryUpdSet = categoryUpdSet;
    }

    public Set getMstatusSet() {
        return mstatusSet;
    }

    public void setMstatusSet(Set mstatusSet) {
        this.mstatusSet = mstatusSet;
    }

    public boolean equals(Object obj) {
        return obj instanceof Trigger && ((Trigger) obj).getId().equals(this.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
