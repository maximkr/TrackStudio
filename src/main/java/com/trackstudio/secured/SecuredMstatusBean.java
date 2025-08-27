package com.trackstudio.secured;

import java.util.ArrayList;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.model.Mstatus;
import com.trackstudio.model.Trigger;
import com.trackstudio.model.Workflow;
import com.trackstudio.soap.bean.MstatusBean;
import com.trackstudio.tools.Null;
import com.trackstudio.tools.PropertyContainer;
import com.trackstudio.tools.textfilter.HTMLEncoder;

import net.jcip.annotations.Immutable;

/**
 * Bean which represents message type
 */
@Immutable
public class SecuredMstatusBean extends Secured {

    private final String id;
    private final String description;
    private final String action;
    private final String name;
    private final String preferences;

    private final String workflowId;
    private final String before;
    private final String insteadOf;
    private final String after;


    public SecuredMstatusBean(Mstatus mstatus, SessionContext secure) throws GranException {
        this.id = mstatus.getId();
        this.sc = secure;
        this.workflowId = mstatus.getWorkflow() != null ? mstatus.getWorkflow().getId() : null;
        this.preferences = mstatus.getPreferences();
        Workflow workflow = KernelManager.getFind().findWorkflow(this.workflowId);
        if (sc.taskOnSight(workflow.getTask().getId())) {
            this.description = mstatus.getDescription();
            this.action = mstatus.getAction();
            this.name = mstatus.getName();
            Trigger trg = mstatus.getTrigger();
            if (trg != null) {
                this.before = trg.getBefore();
                this.after = trg.getAfter();
                this.insteadOf = trg.getInsteadOf();
            } else {
                this.before = null;
                this.after = null;
                this.insteadOf = null;
            }
        } else {
            this.description = null;
            this.action = null;
            this.name = null;
            this.before = null;
            this.after = null;
            this.insteadOf = null;
        }

    }

    public String getDescription() {
        return description;
    }

    public String getEncodeDescription() {
        return Null.stripNullHtml(HTMLEncoder.encode(getDescription()));
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }


    public SecuredWorkflowBean getWorkflow() throws GranException {
        return AdapterManager.getInstance().getSecuredFindAdapterManager().findWorkflowById(getSecure(), this.workflowId);
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
        return getWorkflow().isAllowedByACL();
    }

    public boolean canManage() throws GranException {
        return getWorkflow().canManage();
    }

    public boolean canView() throws GranException {
        return getWorkflow().canView();
    }

    public MstatusBean getSOAP()
            throws GranException {
        MstatusBean bean = new MstatusBean();
        bean.setDescription(description);
        bean.setPreferences(preferences);
        bean.setAction(action);
        bean.setId(id);
        bean.setName(name);
        bean.setWorkflowId(workflowId);
        bean.setTriggerAfterId(after);
        bean.setTriggerBeforeId(before);
        bean.setTriggerInsteadOfId(insteadOf);
        return bean;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public String getBefore() {
        return before;
    }

    public String getInsteadOf() {
        return insteadOf;
    }


    public String getAfter() {
        return after;
    }


    public String getPreferences() {
        return preferences;
    }


    public String getAction() {
        return action != null ? action : name;
    }

    public ArrayList<SecuredTransitionBean> getTransitions() throws GranException {
        return AdapterManager.getInstance().getSecuredWorkflowAdapterManager().getTransitionList(getSecure(), getId());
    }

    public boolean getCanView() throws GranException {
        return canView();
    }
}
