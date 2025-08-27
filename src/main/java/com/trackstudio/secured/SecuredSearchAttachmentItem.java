package com.trackstudio.secured;

import com.trackstudio.tools.PropertyContainer;

import net.jcip.annotations.Immutable;

@Immutable
public class SecuredSearchAttachmentItem extends SecuredSearchItem {
    private final SecuredAttachmentBean attachment;

    public SecuredSearchAttachmentItem(SecuredAttachmentBean attachment, String surroundText, String word) {
        super(surroundText, attachment.getSecure(), word);
        this.attachment = attachment;
    }

    protected PropertyContainer getContainer() {
        PropertyContainer pc = container.get();
        if (pc != null)
            return pc; // object in cache, return it

        PropertyContainer newPC = new PropertyContainer();
        if (attachment != null) newPC.put(attachment.getName()).put(attachment.getId());

        if (container.compareAndSet(null, newPC)) // try to update
            return newPC; // we can update - return loaded value
        else
            return container.get(); // some other thread already updated it - use saved value
    }

    public SecuredAttachmentBean getAttachment() {
        return attachment;
    }

    public String getName() {
        return attachment.getName();
    }

    public String getHighlightName() {
        return getHighlightText(attachment.getName());
    }

    public String getId() {
        return attachment.getId();
    }
}
