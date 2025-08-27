package com.trackstudio.secured;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.cache.AttachmentCacheItem;
import com.trackstudio.soap.bean.TaskAttachmentBean;

import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class SecuredTaskAttachmentBean extends SecuredAttachmentBean {
    private volatile boolean delete;
    private volatile SecuredUserBean user;
    private volatile String shortname;
    private final AttachmentCacheItem item;

    public void init() throws GranException {
        user = super.getUser();
    }

    public boolean isDelete() throws GranException {
        boolean canEditTask = getTask().canManage() && AdapterManager.getInstance().getSecuredTaskAdapterManager().isTaskEditable(getSecure(), taskId);
        return canEditTask || delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    public SecuredTaskAttachmentBean(AttachmentCacheItem attachment, SessionContext sec) {
        super(attachment, sec);
        this.taskId = attachment.getTaskId();
        this.item = attachment;
    }


    public String getTaskId() {
        return taskId;
    }

    public SecuredTaskBean getTask() throws GranException {
        return new SecuredTaskBean(getTaskId(), getSecure());
    }


    public boolean canManage() throws GranException {
        return isAllowedByACL() && getSecure().canAction(Action.manageTaskAttachments, taskId) && getSecure().allowedByUser(getUser() != null ? getUserId() : "1");
    }

    public boolean isAllowedByACL() throws GranException {
        return getTask().isAllowedByACL();
    }

    public boolean canView() throws GranException {
        return getTask().canView();
    }

    public TaskAttachmentBean getTaskSOAP() throws GranException {
        TaskAttachmentBean bean = new TaskAttachmentBean();
        bean.setId(id);
        bean.setName(name);
        bean.setTaskId(taskId);
        bean.setUserId(userId);
        bean.setDescription(description);
        bean.setSize(this.getSize());
        bean.setLastModified(this.getLastModified().getTime().getTime());
        return bean;
    }

    
    public static String buildNameAttach(String name, int size) {
        if (name.length() > size) {
            String ext = "";
            if (name.lastIndexOf(".") != -1) {
                ext = name.lastIndexOf(".") != -1 ? name.substring(name.lastIndexOf("."), name.length()) : "";
                name = name.substring(0, name.lastIndexOf("."));
            }
            return name.length() > size ?  name.substring(0, size) + ".. " + ext : name;
        } else {
            return name;
        }
    }

    public String getShortName() {
        return buildNameAttach(this.name, 30);
    }


    @Override
    public SecuredUserBean getUser() throws GranException {
        return user;
    }

    public String getShortname() {
        return shortname;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

    public AttachmentCacheItem getItem() {
        return item;
    }
}
