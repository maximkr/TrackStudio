package com.trackstudio.secured;

import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.model.Udflist;
import com.trackstudio.soap.bean.UdflistBean;
import com.trackstudio.tools.PropertyContainer;

import net.jcip.annotations.ThreadSafe;

/**
 * Bean which represents custom field possible values list
 */
@ThreadSafe
public class SecuredUdflistBean extends Secured {

    private volatile String id;
    private volatile String val;


    public SecuredUdflistBean(Udflist u, SessionContext sc) {
        this.id = u.getId();
        this.sc = sc;
        this.val = u.getVal();
    }

    public String getId() {
        return id;
    }

    public String getVal() {
        return val;
    }

    protected PropertyContainer getContainer() {
        PropertyContainer pc = container.get();
        if (pc != null)
            return pc; // object in cache, return it

        PropertyContainer newPC = new PropertyContainer();
        newPC.put(getVal()).put(getId());

        if (container.compareAndSet(null, newPC)) // try to update
            return newPC; // we can update - return loaded value
        else
            return container.get(); // some other thread already updated it - use saved value
    }

    public boolean isAllowedByACL() {
        return true;
    }

    public boolean canManage() throws GranException {
        return true;
    }

    public boolean canView() {
        return true;
    }

    public UdflistBean getSOAP()
            throws GranException {
        UdflistBean bean = new UdflistBean();
        bean.setId(id);
        bean.setVal(val);
        return bean;
    }


}
