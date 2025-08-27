package com.trackstudio.secured;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.model.Acl;
import com.trackstudio.soap.bean.AclBean;
import com.trackstudio.tools.PropertyContainer;

import net.jcip.annotations.Immutable;

/**
 * Bean which represents access control rule
 */
@Immutable
public abstract class SecuredAclBean extends Secured {

    protected final String id;
    /**
     * Группа, которая назначается. Определяет, что может делать актор(ы)
     */
    protected final String prstatusId;
    /**
     * Юзер, КОТОРОМУ даются права. Актор
     */
    protected final String userId;
    /**
     * Группа, КОТОРОЙ даются права. Актор
     */
    protected final String groupId;


    protected final String ownerId;
    protected final boolean override;

    public SecuredAclBean(Acl acl, SessionContext sec) {
        this.id = acl.getId();
        this.sc = sec;

        this.override = acl.getOverride() != null && acl.getOverride() == 1;
        this.prstatusId = acl.getPrstatus() != null ? acl.getPrstatus().getId() : null;
        this.userId = acl.getUsersource() != null && acl.getUsersource().getUser() != null ? acl.getUsersource().getUser().getId() : null;
        if (acl.getUsersource() != null && acl.getUsersource().getPrstatus() != null) {
            this.groupId = acl.getUsersource().getPrstatus().getId();
        } else {
            this.groupId = null;
        }
        this.ownerId = acl.getOwner() != null ? acl.getOwner().getId() : null;

    }

    public String getId() {
        return id;
    }

    protected PropertyContainer getContainer() {
        PropertyContainer pc = container.get();
        if (pc != null)
            return pc; // object in cache, return it

        PropertyContainer newPC = new PropertyContainer();
        newPC.put(getGroupId()).put(getUserId()).put(getId());

        if (container.compareAndSet(null, newPC)) // try to update
            return newPC; // we can update - return loaded value
        else
            return container.get(); // some other thread already updated it - use saved value
    }

    public SecuredPrstatusBean getPrstatus() throws GranException {
        return AdapterManager.getInstance().getSecuredFindAdapterManager().findPrstatusById(getSecure(), prstatusId);
    }


    public SecuredUserBean getUser() throws GranException {
        if (userId == null)
            return null;
        return new SecuredUserBean(userId, getSecure());
    }

    public String getGroupId() {
        return groupId;
    }

    public SecuredPrstatusBean getGroup() throws GranException {
        return AdapterManager.getInstance().getSecuredFindAdapterManager().findPrstatusById(getSecure(), groupId);
    }

    public SecuredUserBean getOwner() throws GranException {
        return new SecuredUserBean(ownerId, getSecure());
    }


    public boolean getCanUpdate() throws GranException {
        return canUpdate();
    }


    public boolean isOverride() {
        return override;
    }

    public boolean getOverride() {
        return override;
    }

    public abstract AclBean getSOAP();

    public String getPrstatusId() {
        return prstatusId;
    }


    public String getUserId() {
        return userId;
    }


    public String getOwnerId() {
        return ownerId;
    }


}
