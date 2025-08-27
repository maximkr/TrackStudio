package com.trackstudio.secured;

import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.model.Prstatus;
import com.trackstudio.soap.bean.PrstatusBean;
import com.trackstudio.tools.PropertyContainer;

import net.jcip.annotations.Immutable;

/**
 * Bean which represents user status
 */
@Immutable
public class SecuredPrstatusBean extends Secured {

    private final String id;
    private final String name;
    private final String userId; //owner
    private final String preferences;

    public SecuredPrstatusBean(Prstatus prstatus, SessionContext secure) throws GranException {
        this.id = prstatus.getId();
        this.sc = secure;
        this.userId = prstatus.getUser() != null ? prstatus.getUser().getId() : null;
        this.name = prstatus.getName();
        this.preferences = prstatus.getPreferences();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public SecuredUserBean getUser() throws GranException {
        return new SecuredUserBean(userId, sc);
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

    public boolean isAllowedByACL() throws GranException {
        return getUser().isAllowedByACL();
    }

    public boolean canManage() throws GranException {
        return getUser().isAllowedByACL() && getSecure().canAction(Action.manageRoles, getUserId());
    }

    public boolean canView() throws GranException {
        return getUser().canView();
    }

    public PrstatusBean getSOAP()
            throws GranException {
        PrstatusBean bean = new PrstatusBean();
        bean.setId(id);
        bean.setName(name);
        bean.setUserId(userId);
        return bean;
    }

    public String getUserId() {
        return userId;
    }

    public String getPreferences() {
        return preferences;
    }

    /**
     * Возвращает владельца статуса
     *
     * @return пользователя
     * @throws GranException при необходимости
     */
    public SecuredUserBean getOwner() throws GranException {
        return getUser();
    }

    
}