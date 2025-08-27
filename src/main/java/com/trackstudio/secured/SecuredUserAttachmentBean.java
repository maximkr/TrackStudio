package com.trackstudio.secured;

import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.cache.AttachmentCacheItem;
import com.trackstudio.soap.bean.UserAttachmentBean;

import net.jcip.annotations.Immutable;

@Immutable
public class SecuredUserAttachmentBean extends SecuredAttachmentBean {


    public SecuredUserAttachmentBean(AttachmentCacheItem attachment, SessionContext sec) {
        super(attachment, sec);
    }

    public UserAttachmentBean getSOAP() throws GranException {
        UserAttachmentBean bean = new UserAttachmentBean();
        bean.setId(id);
        bean.setName(name);
        bean.setUserId(userId);
        bean.setDescription(description);
        bean.setSize(this.getSize());
        bean.setLastModified(this.getLastModified().getTime().getTime());
        return bean;
    }


    public boolean isAllowedByACL() throws GranException {
        return getUser().isAllowedByACL();
    }


    public boolean canManage() throws GranException {
        return isAllowedByACL() && getSecure().canAction(Action.manageUserAttachments, getUserId()) && getSecure().allowedByUser(getUser() != null ? getUserId() : "1");
    }

    public boolean canView() throws GranException {
        return getUser().canView();
    }
}
