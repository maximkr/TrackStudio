package com.trackstudio.model;

import java.io.Serializable;

import com.trackstudio.tools.DualKey;
import com.trackstudio.tools.PropertyComparator;
import com.trackstudio.tools.PropertyContainer;

public class Registration extends Named implements Serializable, Comparable {

    private User user;
    private Prstatus prstatus;
    private Task task;
    private Category category;
    private Integer childAllowed;
    private Integer expireDays;
    private Integer priv;

    public Registration(String id) {
        this.id = id;
    }

    public Registration(String name, User user, Prstatus prstatus, Task task, Category category, Integer childAllowed, Integer expireDays, Integer priv) {
        this.name = name;
        this.user = user;
        this.prstatus = prstatus;
        this.task = task;
        this.category = category;
        this.childAllowed = childAllowed;
        this.expireDays = expireDays;
    }

    public Registration(String name, String userId, Prstatus prstatus, Task task, Category category,
                        Integer childAllowed, Integer expireDays, Integer priv) {
        this(name, userId != null ? new User(userId) : null, prstatus, task, category, childAllowed, expireDays, priv);

    }

    public Registration(String name, String userId, String prstatusId, String taskId, Category category,
                        Integer childAllowed, Integer expireDays, Integer priv) {
        this(name, userId != null ? new User(userId) : null, prstatusId != null ? new Prstatus(prstatusId) : null,
                taskId != null ? new Task(taskId) : null, category, childAllowed, expireDays, priv);

    }

    public Registration() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Prstatus getPrstatus() {
        return prstatus;
    }

    public void setPrstatus(String prstatusId) {
        this.prstatus = new Prstatus(prstatusId);
    }

    public void setPrstatus(Prstatus prstatus) {
        this.prstatus = prstatus;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(String taskId) {
        this.task = new Task(taskId);
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setCategory(String categoryId) {
        this.category = categoryId != null ? new Category(categoryId) : null;
    }

    public Integer getChildAllowed() {
        return childAllowed;
    }

    public void setChildAllowed(Integer childAllowed) {
        this.childAllowed = childAllowed;
    }

    public Integer getExpireDays() {
        return expireDays;
    }

    public void setExpireDays(Integer expireDays) {
        this.expireDays = expireDays;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean equals(Object obj) {
        return obj instanceof Registration && ((Registration) obj).getId().equals(this.id);
    }

    protected PropertyContainer container;

    protected PropertyContainer getContainer() {
        if (container == null) {
            container = new PropertyContainer();
            container.put(name).put(id);
        }
        return container;
    }

    public int compareTo(Object o) {
        Registration li = (Registration) o;
        return PropertyComparator.compare(getContainer(), li.getContainer());
    }


    public Integer getPriv() {
        return priv;
    }

    public void setPriv(Integer priv) {
        this.priv = priv;
    }

    //mloskutov #64038
    public DualKey getDualKey() {
        return new DualKey(this.name, this.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
