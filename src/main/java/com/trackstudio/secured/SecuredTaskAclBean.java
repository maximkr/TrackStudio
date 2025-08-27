package com.trackstudio.secured;

import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.model.Acl;
import com.trackstudio.soap.bean.AclBean;

import net.jcip.annotations.Immutable;

@Immutable
public class SecuredTaskAclBean extends SecuredAclBean {
    protected final String taskId;


    public String getTaskId() {
        return taskId;
    }

    public SecuredTaskAclBean(Acl acl, SessionContext sec) {
        super(acl, sec);
        this.taskId = acl.getTask() != null ? acl.getTask().getId() : null;
    }

    public boolean canManage() throws GranException {
            return isAllowedByACL() && getSecure().canAction(Action.manageTaskACLs, this.getTaskId());
    }

    public SecuredTaskBean getTask() throws GranException {
            return taskId == null ? null : new SecuredTaskBean(taskId, getSecure());
    }

    public boolean isAllowedByACL() throws GranException {
        return getTask().isAllowedByACL() && getOwner().isAllowedByACL();

    }

    public boolean canView() throws GranException {
        return getTask().canView();
    }

    public AclBean getSOAP() {
        AclBean bean = new AclBean();
        bean.setId(id);
        bean.setPrstatusId(prstatusId);
        bean.setTaskId(taskId);
        bean.setToUserId(null);
        bean.setUserId(userId);
        bean.setGroupId(groupId);
        bean.setOwnerId(ownerId);
        bean.setOverride(override);
        return bean;
    }
}
