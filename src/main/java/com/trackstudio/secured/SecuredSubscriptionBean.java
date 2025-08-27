package com.trackstudio.secured;

import java.util.Calendar;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.model.Subscription;
import com.trackstudio.soap.bean.SubscriptionBean;
import com.trackstudio.tools.PropertyContainer;

import net.jcip.annotations.ThreadSafe;

/**
 * Bean which represents filter subscription rule
 */
@ThreadSafe
public class SecuredSubscriptionBean extends Secured {
    private final String id;
    private final Calendar startdate;
    private final Calendar stopdate;
    private final Calendar nextrun;
    private final Integer interval;
    private volatile String userId;
    private volatile String groupId;
    private final String name;
    private final String taskId;
    private final String filterId;
    private final String template;

    public SecuredSubscriptionBean(Subscription sb, SessionContext secure) throws GranException {
        this.id = sb.getId();
        this.sc = secure;
        this.name = sb.getName();
        this.userId = sb.getUser().getUser() != null ? sb.getUser().getUser().getId() : null;
        this.groupId = sb.getUser().getPrstatus() != null ? sb.getUser().getPrstatus().getId() : null;
        this.template = sb.getTemplate();
        this.filterId = sb.getFilter() != null ? sb.getFilter().getId() : null;
        boolean canViewUsersource = sb.getUser().getUser() != null && sc.allowedByUser(sb.getUser().getUser().getId());
        if (!canViewUsersource && sb.getUser().getPrstatus() != null) {
            canViewUsersource = new SecuredPrstatusBean(sb.getUser().getPrstatus(), sc).canView() && sb.getUser().getPrstatus().getUser() != null && sc.allowedByUser(sb.getUser().getPrstatus().getUser().getId());
        }
        String tid = sb.getTask() != null ? sb.getTask().getId() : null;
        if (canViewUsersource && sc.taskOnSight(tid)) {
            this.taskId = tid;
            if (sb.getStartdate()!=null)
                this.startdate = (Calendar)sb.getStartdate().clone();
            else
                this.startdate = null;
            if (sb.getStopdate()!=null)
                this.stopdate = (Calendar)sb.getStopdate().clone();
            else
                this.stopdate = null;
            if (sb.getNextrun() != null)
                this.nextrun = (Calendar)sb.getNextrun().clone();
            else
                this.nextrun = null;
            this.interval = sb.getInterval();
        } else {
            this.taskId = null;
            this.startdate = null;
            this.stopdate = null;
            this.nextrun = null;
            this.interval = null;
        }
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Calendar getStartdate() {
        if (this.startdate!=null)
            return (Calendar)this.startdate.clone();
        else
            return null;
    }

    public Calendar getStopdate() {
        if (this.stopdate!=null)
            return (Calendar)this.stopdate.clone();
        else
            return null;
    }

    public Calendar getNextrun() {
        if (this.nextrun!=null)
            return (Calendar)this.nextrun.clone();
        else
            return null;
    }

    public Integer getInterval() {
        return interval;
    }

    public SecuredUserBean getUser() throws GranException {
        return userId != null ? new SecuredUserBean(userId, sc) : null;
    }

    public SecuredPrstatusBean getGroup() throws GranException {
        return AdapterManager.getInstance().getSecuredFindAdapterManager().findPrstatusById(sc, groupId);
    }

    public SecuredTaskBean getTask() throws GranException {
        return new SecuredTaskBean(taskId, sc);
    }

    public SecuredFilterBean getFilter() throws GranException {
        return AdapterManager.getInstance().getSecuredFindAdapterManager().findFilterById(sc, this.filterId);
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
        return (getUserId() != null ? getUser().canManage() : getGroup().isAllowedByACL()) && getFilter().canView();
    }


    public boolean canManage() throws GranException {
        return isAllowedByACL() && sc.canAction(Action.manageEmailSchedules, getTaskId());
    }


    public boolean canView() throws GranException {
        boolean result = false;
        if (getFilter() != null && getFilter().canView()) {
            boolean userView;
            if (getUserId() != null) {
                    userView = getUser().canView();
            } else {
                    userView = getGroupId() != null ? getGroup().canView() : false;
            }
            if (userView) {
                result = getTaskId() != null && getTask() != null && getTask().canView();
            }
        }
        return result;
    }

    public SubscriptionBean getSOAP() {
        SubscriptionBean bean = new SubscriptionBean();
        bean.setFilterId(filterId);
        bean.setId(id);
        bean.setName(name);
        bean.setInterval(interval != null ? interval : 0);
        bean.setNextrun(nextrun.getTimeInMillis());
        bean.setStartdate(startdate.getTimeInMillis());
        bean.setStopdate(stopdate.getTimeInMillis());
        bean.setTaskId(taskId);
        bean.setUserId(userId);
        bean.setGroupId(groupId);
        return bean;
    }

    public String getUserId() {
        return userId;
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

    public String getTemplate() {
        return template;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}