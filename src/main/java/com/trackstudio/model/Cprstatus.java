package com.trackstudio.model;

import java.io.Serializable;

/**
 * Describe relation between category and prstatus.
 * Type defines the type of relation and set, can
 * user of specified user group (prstatus)
 * create/mofidy/delete tasks of specified category
 */
public class Cprstatus implements Serializable {
    private String id; //identifier
    private String type; //persistent
    private Category category;
    private Prstatus prstatus;

    public Cprstatus(String id) {
        this.id = id;
    }

    public Cprstatus(String type, Category category, Prstatus prstatus) {
        this.category = category;
        this.type = type;
        this.prstatus = prstatus;
    }

    public Cprstatus(String type, String categoryId, String prstatusId) {
        this(type, categoryId != null ? new Category(categoryId) : null, prstatusId != null ? new Prstatus(prstatusId) : null);
    }

    public Cprstatus() {
    }

    public Cprstatus(Category category, Prstatus prstatus) {
        this.category = category;
        this.prstatus = prstatus;
    }

    public Cprstatus(String categoryId, String prstatusId) {
        this(categoryId != null ? new Category(categoryId) : null, prstatusId != null ? new Prstatus(prstatusId) : null);
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Category getCategory() {
        return this.category;
    }

    public void setCategory(Category category) {
        this.category = category;
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
        return obj instanceof Cprstatus && ((Cprstatus) obj).getId().equals(this.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
