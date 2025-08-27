package com.trackstudio.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * This class describes task resolution
 */
public class Resolution extends Named implements Serializable, Comparable {

    private Integer isdefault; //persistent
    private Set taskSet = new HashSet(); //persistent
    private Set messageSet = new HashSet(); //persistent

    private Mstatus mstatus;

    public Resolution(String id) {
        this.id = id;
    }

    public Resolution(String name, Mstatus mstatus, boolean isdefault) {
        this.name = name;
        this.mstatus = mstatus;
        this.isdefault = isdefault ? 1 : 0;
    }

    public Resolution(String name, String mstatusId, boolean isdefault) {
        this(name, mstatusId != null ? new Mstatus(mstatusId) : null, isdefault);
    }

    public Resolution() {
    }

    public Mstatus getMstatus() {
        return this.mstatus;
    }

    public void setMstatus(Mstatus mstatus) {
        this.mstatus = mstatus;
    }

    public Set getTaskSet() {
        return this.taskSet;
    }

    public void setTaskSet(Set taskSet) {
        this.taskSet = taskSet;
    }

    public Set getMessageSet() {
        return this.messageSet;
    }

    public void setMessageSet(Set messageSet) {
        this.messageSet = messageSet;
    }

    public void setIsdefault(Integer i) {
        this.isdefault = i;
    }

    public boolean isDefault() {
        return this.isdefault != null && this.isdefault == 1;
    }

    public Integer getIsdefault() {
        return isdefault;
    }

    public boolean equals(Object obj) {
        return obj instanceof Resolution && ((Resolution) obj).getId().equals(this.id);
    }

    public int compareTo(Object obj) {
        return ((Resolution) obj).getName().compareTo(this.getName());
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

}
