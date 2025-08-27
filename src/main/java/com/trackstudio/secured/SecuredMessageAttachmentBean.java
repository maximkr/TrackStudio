package com.trackstudio.secured;

import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.cache.AttachmentCacheItem;
import com.trackstudio.soap.bean.MessageAttachmentBean;

import net.jcip.annotations.Immutable;


@Immutable
public class SecuredMessageAttachmentBean extends SecuredTaskAttachmentBean {
    protected final String messageId;


    public String getMessageId() {
        return messageId;
    }


    public SecuredMessageAttachmentBean(AttachmentCacheItem attachment, SessionContext sec) {
        super(attachment, sec);
        this.messageId = attachment.getMessageId();
    }
    
    public MessageAttachmentBean getMessageSOAP() throws GranException {
        MessageAttachmentBean bean = new MessageAttachmentBean();
        bean.setId(id);
        bean.setName(name);
        bean.setMessageId(messageId);
        bean.setUserId(userId);
        bean.setDescription(description);
        bean.setSize(this.getSize());
        bean.setLastModified(this.getLastModified().getTime().getTime());
        return bean;
    }


    public SecuredMessageBean getMessage() throws GranException {
        return new SecuredMessageBean(getMessageId(), getSecure());
    }

    public boolean canManage() throws GranException {
        return isAllowedByACL() && getSecure().canAction(Action.manageTaskMessageAttachments, taskId);
    }

    public boolean isAllowedByACL() throws GranException {
        return getMessage().isAllowedByACL();
    }


    public boolean canView() throws GranException {
        return getMessage().canView();
    }
}
