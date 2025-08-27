package com.trackstudio.secured;

import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.model.Template;
import com.trackstudio.soap.bean.TemplateBean;
import com.trackstudio.tools.PropertyContainer;

import net.jcip.annotations.Immutable;


/**
 * Bean which represents e-mail message template
 */
@Immutable
public class SecuredTemplateBean extends Secured {
    private final String id;
    private final String name;
    private final String description;
    private final String userId;
    private final String ownerId;
    private final String taskId;

    private final String folder;
    private final boolean active;


    public SecuredTemplateBean(Template bean, SessionContext sess) {
        this.id = bean.getId();
        this.name = bean.getName();
        this.description = bean.getDescription();
        this.active = bean.getActive() != null && bean.getActive() == 1;
        this.sc = sess;

        this.ownerId = bean.getOwner() != null ? bean.getOwner().getId() : null;
        this.userId = bean.getUser() != null ? bean.getUser().getId() : null;
        this.taskId = bean.getTask() != null ? bean.getTask().getId() : null;
        this.folder = bean.getFolder();


    }


    protected PropertyContainer getContainer() {
        PropertyContainer pc = container.get();
        if (pc != null)
            return pc; // object in cache, return it

        PropertyContainer newPC = new PropertyContainer();
        newPC.put(getName()).put(getId());

        if (container.compareAndSet(null, newPC)) // try to update
            return newPC; // we can update - return loaded value
        else
            return container.get(); // some other thread already updated it - use saved value
    }

    public boolean canManage() throws GranException {
        return isAllowedByACL() && sc.canAction(Action.manageTaskTemplates, taskId);
    }

    public boolean isAllowedByACL() throws GranException {
        return getOwner().isAllowedByACL();
    }

    public boolean canView() throws GranException {
        return getOwner().canView();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getUserId() {
        return userId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getTaskId() {
        return taskId;
    }


    public boolean getActive() {
        return active;
    }

    public SecuredUserBean getUser() throws GranException {
        if (getUserId() != null)
            return new SecuredUserBean(getUserId(), getSecure());
        else return null;
    }

    public SecuredUserBean getOwner() throws GranException {
        if (getOwnerId() != null)
            return new SecuredUserBean(getOwnerId(), getSecure());
        else return null;
    }

    public SecuredTaskBean getTask() throws GranException {
        if (getTaskId() != null)
            return new SecuredTaskBean(getTaskId(), getSecure());
        else return null;
    }

    public String getFolder() throws GranException {
        return folder;

    }

    public boolean isActive() {
        return active;
    }

    public TemplateBean getSOAP() {
        TemplateBean bean = new TemplateBean();
        bean.setId(id);
        bean.setActive(active);
        bean.setDescription(description);
        bean.setFolder(folder);
        bean.setName(name);
        bean.setOwnerId(ownerId);
        bean.setTaskId(taskId);
        bean.setUserId(userId);
        return bean;
    }
}