package com.trackstudio.secured;

import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.model.Acl;
import com.trackstudio.soap.bean.AclBean;

import net.jcip.annotations.Immutable;

@Immutable
public class SecuredUserAclBean extends SecuredAclBean {

    protected final String toUserId;

    public String getToUserId() {
        return toUserId;
    }

    public SecuredUserAclBean(Acl acl, SessionContext sec) {
        super(acl, sec);
        this.toUserId = acl.getToUser() != null ? acl.getToUser().getId() : null;

    }

    public boolean canManage() throws GranException {
            return isAllowedByACL() && getSecure().canAction(Action.manageUserACLs, getOwnerId());
    }

    public SecuredUserBean getToUser() throws GranException {
            return toUserId == null ? null : new SecuredUserBean(toUserId, getSecure());
    }

    public boolean isAllowedByACL() throws GranException {
        return getToUser().isAllowedByACL() && getOwner().isAllowedByACL();
    }

    public boolean canView() throws GranException {
        return getToUser().canView() && getOwner().canView();
    }

    public AclBean getSOAP() {
        AclBean bean = new AclBean();
        bean.setId(id);
        bean.setPrstatusId(prstatusId);
        bean.setTaskId(null);
        bean.setToUserId(toUserId);
        bean.setUserId(userId);
        bean.setGroupId(groupId);
        bean.setOwnerId(ownerId);
        bean.setOverride(override);
        return bean;
    }
}
