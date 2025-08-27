package com.trackstudio.model;

import java.util.HashSet;
import java.util.Set;

public class Usersource {
    protected String id; //identifier
    protected Set taskSet = new HashSet(); //persistent
    protected Set messageSet = new HashSet(); //persistent
    private Set aclSet = new HashSet();
    private Set subscriptionSet = new HashSet(); //persistent
    private Set notificationSet = new HashSet(); //persistent
    protected User user;
    protected Prstatus prstatus;


    public Usersource(User user, Prstatus prstatus) {
        this.user = user;
        this.prstatus = prstatus;
    }

    public Usersource(String userId, String prstatusId) {
        this(userId != null ? new User(userId) : null, prstatusId != null ? new Prstatus(prstatusId) : null);
    }

    public Usersource() {
    }

    public Usersource(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Prstatus getPrstatus() {
        return prstatus;
    }

    public void setPrstatus(Prstatus prstatus) {
        this.prstatus = prstatus;
    }


    public Set getTaskSet() {
        return taskSet;
    }

    public void setTaskSet(Set taskSet) {
        this.taskSet = taskSet;
    }

    public Set getMessageSet() {
        return messageSet;
    }

    public void setMessageSet(Set messageSet) {
        this.messageSet = messageSet;
    }

    public Set getAclSet() {
        return aclSet;
    }

    public void setAclSet(Set aclSet) {
        this.aclSet = aclSet;
    }

    public Set getSubscriptionSet() {
        return subscriptionSet;
    }

    public void setSubscriptionSet(Set subscriptionSet) {
        this.subscriptionSet = subscriptionSet;
    }

    public Set getNotificationSet() {
        return notificationSet;
    }

    public void setNotificationSet(Set notificationSet) {
        this.notificationSet = notificationSet;
    }

    public boolean equals(Object obj) {
        return obj instanceof Udfsource && ((Udfsource) obj).getId().equals(this.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
