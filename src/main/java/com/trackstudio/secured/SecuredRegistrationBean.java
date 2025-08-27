package com.trackstudio.secured;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.model.Registration;
import com.trackstudio.soap.bean.RegistrationBean;
import com.trackstudio.tools.PropertyContainer;

import net.jcip.annotations.Immutable;

/**
 * Bean which represents self registration rule
 */
@Immutable
public class SecuredRegistrationBean extends Secured {

    private final String id;
    private final String name;
    private final String userId;
    private final String prstatusId;
    private final boolean priv;
    private final String taskId;
    private final String categoryId;
    private final Integer childAllowed;
    private final Integer expireDays;

    public SecuredRegistrationBean(Registration bean, SessionContext sess) throws GranException {
        this.id = bean.getId();
        this.sc = sess;
        this.userId = bean.getUser() != null ? bean.getUser().getId() : null;
        this.priv = bean.getPriv() != null && bean.getPriv() == 1;
        if (sess.userOnSight(userId)) {
            this.name = bean.getName();
            this.categoryId = bean.getCategory() != null ? bean.getCategory().getId() : null;
            this.childAllowed = bean.getChildAllowed();
            this.expireDays = bean.getExpireDays();
            this.prstatusId = bean.getPrstatus() != null ? bean.getPrstatus().getId() : null;
            this.taskId = bean.getTask() != null ? bean.getTask().getId() : null;
        } else {
            this.name = null;
            this.categoryId = null;
            this.childAllowed = null;
            this.expireDays = null;
            this.prstatusId = null;
            this.taskId = null;
        }

    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getChildAllowed() {
        return childAllowed;
    }

    public Integer getExpireDays() {
        return expireDays;
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

    public SecuredPrstatusBean getPrstatus() throws GranException {
        return AdapterManager.getInstance().getSecuredFindAdapterManager().findPrstatusById(sc, this.prstatusId);
    }

    public SecuredUserBean getUser() throws GranException {
        return new SecuredUserBean(userId, sc);
    }

    public SecuredTaskBean getTask() throws GranException {
        return new SecuredTaskBean(taskId, sc);
    }

    public SecuredCategoryBean getCategory() throws GranException {
        return AdapterManager.getInstance().getSecuredFindAdapterManager().findCategoryById(sc, this.categoryId);
    }

    public boolean isAllowedByACL() throws GranException {
        return getUser().isAllowedByACL();
    }

    public boolean canView() throws GranException {
        return getUser().canView() && getTask().canView();
    }

    public boolean canManage() throws GranException {
        return isAllowedByACL() && sc.canAction(Action.manageRegistrations, getTaskId());
    }

    public RegistrationBean getSOAP() throws GranException {
        RegistrationBean bean = new RegistrationBean();
        bean.setCategoryId(categoryId);
        bean.setChildAllowed(childAllowed != null ? childAllowed : 0);
        bean.setExpireDays(expireDays != null ? expireDays : 0);
        bean.setId(id);
        bean.setName(name);
        bean.setPrstatusId(prstatusId);
        bean.setTaskId(taskId);
        bean.setUserId(userId);
        bean.setPriv(priv);
        return bean;
    }

    public String getUserId() {
        return userId;
    }

    public String getPrstatusId() {
        return prstatusId;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getCategoryId() {
        return categoryId;
    }


    public boolean isPriv() {
        return priv;
    }
}
