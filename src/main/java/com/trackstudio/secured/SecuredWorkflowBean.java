package com.trackstudio.secured;

import java.util.ArrayList;
import java.util.List;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.cache.UDFCacheItem;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.model.Workflow;
import com.trackstudio.soap.bean.WorkflowBean;
import com.trackstudio.tools.PropertyContainer;

import net.jcip.annotations.Immutable;

/**
 * Bean which represents workflow
 */
@Immutable
public class SecuredWorkflowBean extends Secured {

    private final String id;
    private final String name;
    private final String taskId;

    public SecuredWorkflowBean(Workflow workflow, SessionContext secure) throws GranException {
        this.id = workflow.getId();
        this.sc = secure;
        this.taskId = workflow.getTask() != null ? workflow.getTask().getId() : null;
        this.name = workflow.getName();
    }


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }


    public SecuredTaskBean getTask() throws GranException {
        return new SecuredTaskBean(taskId, sc);
    }

    public List<SecuredWorkflowUDFBean> getWorkflowUDFs() throws GranException {
        List u = KernelManager.getWorkflow().getUDFs(getId());
        ArrayList<SecuredWorkflowUDFBean> c = new ArrayList<SecuredWorkflowUDFBean>();
        if (u != null)
            for (Object anU : u) {
                c.add(new SecuredWorkflowUDFBean((UDFCacheItem) anU, sc));
            }
        return c;
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
        return getTask().isAllowedByACL();
    }


    public boolean canManage() throws GranException {
        return isAllowedByACL() && sc.canAction(Action.manageWorkflows, getTaskId());
    }

    public boolean canView() throws GranException {
        return getTask().canView();
    }

    public WorkflowBean getSOAP() throws GranException {
        WorkflowBean bean = new WorkflowBean();
        bean.setId(id);
        bean.setName(name);
        bean.setTaskId(taskId);
        return bean;
    }

    public String getTaskId() {
        return taskId;
    }

    public boolean hasStart() throws GranException {
        return KernelManager.getWorkflow().getStartStateId(id) != null;
    }

    public boolean getHasStart() throws GranException {
        return hasStart();
    }

    public boolean getIsValid() throws GranException {
        return isValid();
    }

    public boolean isValid() throws GranException {
        return AdapterManager.getInstance().getSecuredWorkflowAdapterManager().getWorkflowIsValid(sc, id);
    }

    public List<SecuredCategoryBean> getCategories() throws GranException {
        return AdapterManager.getInstance().getSecuredWorkflowAdapterManager().getCategoryList(sc, getId());
    }
}