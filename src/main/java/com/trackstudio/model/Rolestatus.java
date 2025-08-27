package com.trackstudio.model;

import java.io.Serializable;

/**
 * Describe information about user right (role) and
 * user group (prstatus) relation
 */
public class Rolestatus implements Serializable {
    private String id; //identifier

    private String role;
    private Prstatus prstatus;

    public Rolestatus(String id) {
        this.id = id;
    }

    public Rolestatus(Prstatus prstatus, String role) {
        this.prstatus = prstatus;
        this.role = role;
    }

    public Rolestatus(String prstatusId, String role) {
        this(prstatusId != null ? new Prstatus(prstatusId) : null, role);
    }

    public Rolestatus() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Prstatus getPrstatus() {
        return this.prstatus;
    }

    public void setPrstatus(Prstatus prstatus) {
        this.prstatus = prstatus;
    }

    public String getRole() {
        return this.role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean equals(Object obj) {
        return obj instanceof Rolestatus && ((Rolestatus) obj).getId().equals(this.id);
    }

    public String toString() {
        return role;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
