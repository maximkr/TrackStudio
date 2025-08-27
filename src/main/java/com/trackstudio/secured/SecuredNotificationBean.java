package com.trackstudio.secured;

import java.util.Locale;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.constants.FilterConstants;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.model.Notification;
import com.trackstudio.soap.bean.NotificationBean;
import com.trackstudio.tools.PropertyContainer;

import net.jcip.annotations.ThreadSafe;

/**
 * Bean which represents e-mail notification rule
 */
@ThreadSafe
public class SecuredNotificationBean extends Secured {

    private final String id;
    private final String condition;
    private final String name;
    private volatile String userId;
    private volatile String groupId;
    private final String taskId;
    private final String filterId;
    private final String template;

    public SecuredNotificationBean(Notification n, SessionContext sc) throws GranException {
        this.sc = sc;
        this.id = n.getId();
        this.name = n.getName();
        this.filterId = n.getFilter().getId();
        this.template = n.getTemplate();
        boolean canView = n.getUser().getUser() != null && sc.userOnSight(n.getUser().getUser().getId());
        if (!canView && n.getUser().getPrstatus() != null) {
            canView = new SecuredPrstatusBean(n.getUser().getPrstatus(), sc).canView() && n.getUser().getPrstatus().getUser() != null && sc.userOnSight(n.getUser().getPrstatus().getUser().getId());
        }
        if (canView) {
            this.condition = n.getCondition();
            this.userId = n.getUser().getUser() != null ? n.getUser().getUser().getId() : null;
            this.groupId = n.getUser().getPrstatus() != null ? n.getUser().getPrstatus().getId() : null;
            this.taskId = n.getTask().getId();
        } else {
            this.condition = null;
            this.userId = null;
            this.groupId = null;
            this.taskId = null;
        }
    }

    public String getCondition() {
        return condition;
    }

    public SecuredTaskBean getTask() throws GranException {
        return new SecuredTaskBean(taskId, getSecure());
    }

    public SecuredUserBean getUser() throws GranException {
        if (userId != null)
            return new SecuredUserBean(userId, getSecure());
        else return null;
    }

    public SecuredPrstatusBean getGroup() throws GranException {
        if (groupId != null)
            return AdapterManager.getInstance().getSecuredFindAdapterManager().findPrstatusById(getSecure(), groupId);
        else return null;
    }

    public SecuredFilterBean getFilter() throws GranException {
        if (filterId != null)
            return AdapterManager.getInstance().getSecuredFindAdapterManager().findFilterById(getSecure(), filterId);
        return null;
    }

    public String getTemplate() throws GranException {
        return template;

    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getFilterId() {
        return filterId;
    }

    public boolean isFireNewTask() {
        return condition != null && condition.toUpperCase(Locale.ENGLISH).indexOf(FilterConstants.FIRE_NEW_TASK) != -1;
    }

    public boolean isFireNotI() {
        return condition != null && condition.toUpperCase(Locale.ENGLISH).indexOf(FilterConstants.NOT_I) != -1;
    }

    public boolean isFireUpdatedTask() {
        return condition != null && condition.toUpperCase(Locale.ENGLISH).indexOf(FilterConstants.FIRE_UPDATED_TASK) != -1;
    }

    public boolean isFireNewAttachment() {
        return condition != null && condition.toUpperCase(Locale.ENGLISH).indexOf(FilterConstants.FIRE_NEW_ATTACHMENT) != -1;
    }

    public boolean isFireNewMessage() {
        return condition != null && condition.toUpperCase(Locale.ENGLISH).indexOf(FilterConstants.FIRE_NEW_MESSAGE) != -1;
    }

    public boolean isEmailSender() {
        return  condition != null && condition.toUpperCase(Locale.ENGLISH).contains("E");
    }

    public boolean isJabberSender() {
        return  condition != null && condition.toUpperCase(Locale.ENGLISH).contains("J");
    }

    protected PropertyContainer getContainer() {
        PropertyContainer pc = container.get();
        if (pc != null)
            return pc; // object in cache, return it

        PropertyContainer newPC = new PropertyContainer();
        try {
            newPC.put(getName()).put((getUserId() != null ? getUser().getName() : getGroup().getName())).put(getFilter().getName()).put(getId());
        } catch (GranException ge) {
            newPC.put(getName()).put(getId());
        }

        if (container.compareAndSet(null, newPC)) // try to update
            return newPC; // we can update - return loaded value
        else
            return container.get(); // some other thread already updated it - use saved value
    }

    public boolean isAllowedByACL() throws GranException {
        return (getUserId() != null ? getUser().isAllowedByACL() : getGroup().isAllowedByACL()) && getFilter().canView();
    }


    public boolean canView() throws GranException {
        return getFilter() != null && getFilter().canView() && getTaskId() != null && getTask() != null && getTask().canView() && (getUserId() != null ? getUser().canView() : getGroup().canView());
    }


    public boolean canManage() throws GranException {
        return isAllowedByACL() && getSecure().canAction(Action.manageEmailSchedules, getTaskId());
    }

    public NotificationBean getSOAP() throws GranException {
        NotificationBean bean = new NotificationBean();
        bean.setId(id);
        bean.setName(name);
        bean.setFilterId(filterId);
        bean.setUserId(userId);
        bean.setGroupId(groupId);
        bean.setTemplate(template);
        bean.setTaskId(taskId);
        bean.setCondition(condition);
        return bean;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
