package com.trackstudio.secured;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.model.Priority;
import com.trackstudio.soap.bean.PriorityBean;
import com.trackstudio.tools.Null;
import com.trackstudio.tools.PropertyContainer;
import com.trackstudio.tools.textfilter.HTMLEncoder;

import net.jcip.annotations.Immutable;

/**
 * Bean which represents task priority
 */
@Immutable
public class SecuredPriorityBean extends Secured {

    private final String id;
    private final String name;
    private final Integer order;
    private final String description;
    private final boolean isDefault;
    private final String workflowId;


    public SecuredPriorityBean(Priority priority, SessionContext secure) throws GranException {
        this.id = priority.getId();
        this.sc = secure;
        this.workflowId = priority.getWorkflow() != null ? priority.getWorkflow().getId() : null;
        if (sc.taskOnSight(priority.getWorkflow().getTask().getId())) {
            this.name = priority.getName();
            this.order = priority.getOrder();
            this.description = priority.getDescription();
            this.isDefault = priority.isDefault();
        } else {
            this.name = null;
            this.order = null;
            this.description = null;
            this.isDefault = false;
        }
    }


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }


    public Integer getOrder() {
        return order;
    }

    public String getEncodeOrder() {
        return Null.stripNullHtml(order);
    }

    public String getDescription() {
        return description;
    }

    public String getEncodeDescription() {
        return Null.stripNullHtml(HTMLEncoder.encode(getDescription()));
    }


    public boolean isDef() {
        return this.isDefault;
    }

    public SecuredWorkflowBean getWorkflow() throws GranException {
        return AdapterManager.getInstance().getSecuredFindAdapterManager().findWorkflowById(getSecure(), this.workflowId);
    }

    
    protected PropertyContainer getContainer() {
        PropertyContainer pc = container.get();
        if (pc != null)
            return pc; // object in cache, return it

        PropertyContainer newPC = new PropertyContainer();
        newPC.putInverse(getOrder()).put(getName()).put(getId());

        if (container.compareAndSet(null, newPC)) // try to update
            return newPC; // we can update - return loaded value
        else
            return container.get(); // some other thread already updated it - use saved value
   }
    

    public boolean isAllowedByACL() throws GranException {
        return getWorkflow().isAllowedByACL();
    }


    public boolean canManage() throws GranException {
        return getWorkflow().canManage();
    }

    public boolean canView() throws GranException {
        return getWorkflow().canView();
    }

    public PriorityBean getSOAP() throws GranException {
        PriorityBean bean = new PriorityBean();
        bean.setId(id);
        bean.setName(name);
        bean.setOrder(order != null ? order : 0);
        bean.setDefault(isDefault);
        bean.setDescription(description);
        bean.setWorkflowId(workflowId);
        return bean;
    }

    public String getWorkflowId() {
        return workflowId;
    }
}
