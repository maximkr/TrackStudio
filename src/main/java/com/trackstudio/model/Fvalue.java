package com.trackstudio.model;

import java.io.Serializable;

/**
 * Describes filter conditions
 */
public class Fvalue implements Serializable {
    private String id; //identifier
    private String key; //persistent

    private String value; //persistent

    private Filter filter;


    public Fvalue(String id) {
        this.id = id;
    }

    public Fvalue(Filter filter, String key, String value) {
        this.filter = filter;
        this.key = key;
        this.value = value;
    }

    public Fvalue(String filterId, String key, String value) {
        this(filterId != null ? new Filter(filterId) : null, key, value);
    }

    public Fvalue() {
    }

    public Fvalue(Filter filter) {
        this.filter = filter;
    }

    public Fvalue(String id, String filterId) {
        this(filterId != null ? new Filter(filterId) : null);
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Filter getFilter() {
        return this.filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean equals(Object obj) {
        return obj instanceof Fvalue && ((Fvalue) obj).getId().equals(this.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}

