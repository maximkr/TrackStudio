package com.trackstudio.secured;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.model.CurrentFilter;
import com.trackstudio.soap.bean.CurrentFilterBean;
import com.trackstudio.tools.PropertyContainer;

import net.jcip.annotations.Immutable;

/**
 * Bean which represents filter
 */
@Immutable
public class SecuredCurrentFilterBean extends Secured {

    private final String id;
    private final String taskId;
    private final String userId;
    private final String ownerId;
    private final String filterId;

    public SecuredCurrentFilterBean(CurrentFilter cf, SessionContext sec) throws GranException {
        this.id = cf.getId();
        this.sc = sec;
        this.taskId = cf.getTask() != null ? cf.getTask().getId() : null;
        this.userId = cf.getUser() != null ? cf.getUser().getId() : null;
        this.ownerId = cf.getOwner() != null ? cf.getOwner().getId() : null;
        this.filterId = cf.getFilter() != null ? cf.getFilter().getId() : null;
    }

    public String getId() {
        return id;
    }

    protected PropertyContainer getContainer() {
        PropertyContainer pc = container.get();
        if (pc != null)
            return pc; // object in cache, return it

        PropertyContainer newPC = new PropertyContainer();
        newPC.put(getId());

        if (container.compareAndSet(null, newPC)) // try to update
            return newPC; // we can update - return loaded value
        else
            return container.get(); // some other thread already updated it - use saved value
    }

    public SecuredFilterBean getFilter() throws GranException {
        return AdapterManager.getInstance().getSecuredFindAdapterManager().findFilterById(getSecure(), filterId);
    }

    public SecuredTaskBean getTask() throws GranException {
        return new SecuredTaskBean(taskId, getSecure());
    }

    public SecuredUserBean getUser() throws GranException {
        return new SecuredUserBean(userId, getSecure());
    }

    public SecuredUserBean getOwner() throws GranException {
        return new SecuredUserBean(ownerId, getSecure());
    }


    public boolean isAllowedByACL() throws GranException {

        return getTask().isAllowedByACL() && getOwner().isAllowedByACL();
    }

    public boolean canManage() throws GranException {
        SessionContext s = getSecure();
        return isAllowedByACL() && ((s.canAction(Action.manageTaskPrivateFilters, getTaskId()) && getOwnerId().equals(s.getUserId())) || (s.canAction(Action.manageTaskPublicFilters, getTaskId())));
    }

    public boolean canView() throws GranException {
        return getTask().canView() && getFilter().canView() && getUser().canView() && getOwner().canView();
    }

    public CurrentFilterBean getSOAP() {
        CurrentFilterBean bean = new CurrentFilterBean();
        bean.setFilterId(filterId);
        bean.setId(id);
        bean.setTaskId(taskId);
        bean.setUserId(userId);
        bean.setOwnerId(ownerId);
        return bean;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getUserId() {
        return userId;
    }

    public String getFilterId() {
        return filterId;
    }

    public String getOwnerId() {
        return ownerId;
    }
}


