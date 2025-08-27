package com.trackstudio.secured;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.AttachmentCacheItem;

public class SecuredTaskAttachmentArchiveBean extends SecuredTaskAttachmentBean {
    private final AttachmentCacheItem item;

    public SecuredTaskAttachmentArchiveBean(AttachmentCacheItem attachment, SessionContext sec) {
        super(attachment, sec);
        this.item = attachment;
    }

    //TODO check is exists.
    public Boolean getDeleted() throws GranException {
        return this.item.getFile() == null || !this.item.getFile().exists();
    }
}
