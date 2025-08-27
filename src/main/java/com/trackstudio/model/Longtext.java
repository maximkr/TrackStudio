package com.trackstudio.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.manager.KernelManager;

public class Longtext implements Serializable, Comparable {
    private String id; //identifier
    private Longtext reference;
    private Integer order;
    private String value;

    private Set taskSet = new HashSet(); //persistent
    private Set values;
    private Set messageSet;
    private Set udfvalSet;
    private Set cattemplateSet;


    public Set getCattemplateSet() {
        return cattemplateSet;
    }

    public void setCattemplateSet(Set cattemplateSet) {
        this.cattemplateSet = cattemplateSet;
    }

    public Set getMessageSet() {
        return messageSet;
    }

    public void setMessageSet(Set messageSet) {
        this.messageSet = messageSet;
    }

    public Set getUdfvalSet() {
        return udfvalSet;
    }

    public void setUdfvalSet(Set udfvalSet) {
        this.udfvalSet = udfvalSet;
    }

    public Set getValues() {
        return values;
    }

    public void setValues(Set values) {
        this.values = values;
    }

    public Set getTaskSet() {
        return taskSet;
    }

    public void setTaskSet(Set taskSet) {
        this.taskSet = taskSet;
    }

    public Longtext(String referenceId, int order, String value) throws GranException {
        this.reference = KernelManager.getFind().findLongtext(referenceId);
        this.order = order;
        this.value = value;
    }


    public Longtext() {
    }


    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }


    public void setValue(String value) {
        this.value = value;
    }

    public Longtext getReference() {
        return reference;
    }

    public void setReference(Longtext reference) {
        this.reference = reference;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }


    public boolean equals(Longtext c) {
        return this.getId().equals(c.getId());
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public int compareTo(Object lon) {
        int i = getOrder().compareTo(((Longtext) lon).getOrder());
        if (i == 0)
            return (getOrder().toString() + getId()).compareTo(((Longtext) lon).getOrder() + ((Longtext) lon).getId());
        else
            return i;
    }

    public String toString() {
        return this.value;
    }

    public boolean equals(Object obj) {
        return obj instanceof Longtext && ((Longtext) obj).getId().equals(this.id);
    }


}
