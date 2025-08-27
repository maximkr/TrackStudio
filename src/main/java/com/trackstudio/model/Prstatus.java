package com.trackstudio.model;

import java.io.Serializable;
import java.util.Set;

/**
 * This class describes general properties of user group
 */
public class Prstatus extends Named implements Serializable {

    //private Integer priv; //persistent

    private Set rolestatusSet; //persistent
    private Set mprstatusSet; //persistent
    private Set userSet; //persistent
    private String preferences, description;
    private Set cprstatusSet;
    private Set uprstatusSet;
    private Set aclSet;

    private Set registrationSet;

    private Set usersourceSet;

    private User user;

    public Prstatus(String id) {
        this.id = id;
    }

    public Prstatus(String name, User user) {
        this.user = user;
        this.name = name;
    }

    public Prstatus(String name, String userId) {
        this(name, new User(userId));
    }

    public Prstatus() {
    }

    public Prstatus(User user) {
        this.user = user;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set getRolestatusSet() {
        return this.rolestatusSet;
    }

    public void setRolestatusSet(Set rolestatusSet) {
        this.rolestatusSet = rolestatusSet;
    }

    public Set getMprstatusSet() {
        return this.mprstatusSet;
    }

    public void setMprstatusSet(Set mprstatusSet) {
        this.mprstatusSet = mprstatusSet;
    }

    public Set getUserSet() {
        return this.userSet;
    }

    public void setUserSet(Set userSet) {
        this.userSet = userSet;
    }

    public Set getCprstatusSet() {
        return this.cprstatusSet;
    }

    public void setCprstatusSet(Set cprstatusSet) {
        this.cprstatusSet = cprstatusSet;
    }

    public Set getAclSet() {
        return aclSet;
    }

    public void setAclSet(Set aclSet) {
        this.aclSet = aclSet;
    }

    public Set getRegistrationSet() {
        return registrationSet;
    }

    public void setRegistrationSet(Set registrationSet) {
        this.registrationSet = registrationSet;
    }

    public String toString() {
        return name;
    }

    public boolean equals(Object obj) {
        return obj instanceof Prstatus && ((Prstatus) obj).getId().equals(this.id);
    }

    public Set getUprstatusSet() {
        return uprstatusSet;
    }

    public void setUprstatusSet(Set uprstatusSet) {
        this.uprstatusSet = uprstatusSet;
    }

    public Set getUsersourceSet() {
        return usersourceSet;
    }

    public void setUsersourceSet(Set usersourceSet) {
        this.usersourceSet = usersourceSet;
    }

    public String getPreferences() {
        return preferences;
    }

    public void setPreferences(String preferences) {
        this.preferences = preferences;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}