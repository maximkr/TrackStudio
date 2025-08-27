package com.trackstudio.secured;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.model.Resolution;
import com.trackstudio.soap.bean.ResolutionBean;
import com.trackstudio.tools.PropertyContainer;

import net.jcip.annotations.Immutable;

/**
 * Bean which represents resolution
 */
@Immutable
public class SecuredResolutionBean extends Secured {

    private final String id;
    private final String name;
    private final String mstatusId;
    private final boolean isDefault;

    public SecuredResolutionBean(Resolution resolution, SessionContext secure) throws GranException {
        this.id = resolution.getId();
        this.sc = secure;
        this.mstatusId = resolution.getMstatus() != null ? resolution.getMstatus().getId() : null;
        if (sc.taskOnSight(resolution.getMstatus().getWorkflow().getTask().getId())) {
            this.name = resolution.getName();
            this.isDefault = resolution.isDefault();
        } else {
            this.name = null;
            this.isDefault = false;
        }
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }


    public SecuredMstatusBean getMstatus() throws GranException {
        return AdapterManager.getInstance().getSecuredFindAdapterManager().findMstatusById(sc, this.mstatusId);
    }

    public boolean isDefault() {
        return this.isDefault;
    }

    public boolean getIsDefault() {
        return this.isDefault;
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
        return getMstatus().isAllowedByACL();
    }

    public boolean canView() throws GranException {
        return getMstatus().canView();
    }

    public boolean canManage() throws GranException {
        return getMstatus().canManage();
    }

    public ResolutionBean getSOAP()
            throws GranException {
        ResolutionBean bean = new ResolutionBean();
        bean.setDefault(isDefault);
        bean.setId(id);
        bean.setMstatusId(mstatusId);
        bean.setName(name);
        return bean;
    }

    public String getMstatusId() {
        return mstatusId;
    }
}