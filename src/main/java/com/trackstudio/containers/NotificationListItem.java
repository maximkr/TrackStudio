package com.trackstudio.containers;

import com.trackstudio.secured.SecuredPrstatusBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.tools.PropertyComparable;
import com.trackstudio.tools.PropertyContainer;

import net.jcip.annotations.ThreadSafe;

/**
 * Класс-контейнер для хранения данных о почтовых уведомлениях
 */
@ThreadSafe
public class NotificationListItem extends PropertyComparable {

    public volatile String id;

    public volatile String template;
    public volatile String filter;
    public volatile String filterId;
    public volatile String name;
    public volatile SecuredTaskBean task;
    public volatile SecuredUserBean user;
    public volatile SecuredPrstatusBean group;

    public volatile boolean canUpdate;

    public volatile boolean fireNewTask;
    public volatile boolean fireNewAttachment;
    public volatile boolean fireNewMessage;
    public volatile boolean fireUpdatedTask;
    public volatile boolean fireNotI;

    public boolean isFireNotI() {
        return fireNotI;
    }

    public void setFireNotI(boolean fireNotI) {
        this.fireNotI = fireNotI;
    }

    public String getFilterId() {
        return filterId;
    }

    public void setFilterId(String filterId) {
        this.filterId = filterId;
    }

    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }


    public void setTemplate(String template) {
        this.template = template;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTemplate() {
        return template;
    }

    public String getFilter() {
        return filter;
    }

    public String getName() {
        return name;
    }

    public NotificationListItem(String id, String name) {
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


    public SecuredTaskBean getTask() {
        return task;
    }

    public void setTask(SecuredTaskBean task) {
        this.task = task;
    }

    public SecuredUserBean getUser() {
        return user;
    }

    public void setUser(SecuredUserBean user) {
        this.user = user;
    }

    public SecuredPrstatusBean getGroup() {
        return group;
    }

    public void setGroup(SecuredPrstatusBean group) {
        this.group = group;
    }

    protected PropertyContainer getContainer() {
        PropertyContainer pc = container.get();
        if (pc != null)
            return pc; // object in cache, return it

        PropertyContainer newPC = new PropertyContainer();
        newPC.put(name).put(task.getName()).put(filter).put(template).put(id);

        if (container.compareAndSet(null, newPC)) // try to update
            return newPC; // we can update - return loaded value
        else
            return container.get(); // some other thread already updated it - use saved value
    }


}
