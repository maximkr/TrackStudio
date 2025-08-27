package com.trackstudio.simple;

import com.trackstudio.tools.PropertyComparable;
import com.trackstudio.tools.PropertyContainer;

import net.jcip.annotations.ThreadSafe;

/**
 * Вспомогательный класс для почтовых подписок
 */
@ThreadSafe
public class Notification extends PropertyComparable {

    private volatile String id;

    private volatile String template;
    private volatile String filter;

    private volatile String name;
    private volatile String task;
    private volatile String user;

    private volatile boolean canUpdate;

    private volatile boolean fireNewTask;
    private volatile boolean fireNewAttachment;
    private volatile boolean fireNewMessage;
    private volatile boolean fireUpdatedTask;


    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }


    public void setTemplate(String template) {
        this.template = template;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getTemplate() {
        return template;
    }


    public String getName() {
        return name;
    }

    public Notification(String id, String name) {
        this.id = id;
        this.name = name;
    }


    public boolean isCanUpdate() {
        return canUpdate;
    }

    public void setCanUpdate(boolean canUpdate) {
        this.canUpdate = canUpdate;
    }

    public boolean isFireNewTask() {
        return fireNewTask;
    }

    public void setFireNewTask(boolean fireNewTask) {
        this.fireNewTask = fireNewTask;
    }

    public boolean isFireNewAttachment() {
        return fireNewAttachment;
    }

    public void setFireNewAttachment(boolean fireNewAttachment) {
        this.fireNewAttachment = fireNewAttachment;
    }

    public boolean isFireNewMessage() {
        return fireNewMessage;
    }

    public void setFireNewMessage(boolean fireNewMessage) {
        this.fireNewMessage = fireNewMessage;
    }

    public boolean isFireUpdatedTask() {
        return fireUpdatedTask;
    }

    public void setFireUpdatedTask(boolean fireUpdatedTask) {
        this.fireUpdatedTask = fireUpdatedTask;
    }


    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    protected PropertyContainer getContainer() {
        PropertyContainer pc = container.get();
        if (pc != null)
            return pc; // object in cache, return it

        PropertyContainer newPC = new PropertyContainer();
        newPC.put(name).put(task).put(filter).put(template).put(id);

        if (container.compareAndSet(null, newPC)) // try to update
            return newPC; // we can update - return loaded value
        else
            return container.get(); // some other thread already updated it - use saved value
    }


}
