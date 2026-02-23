package com.trackstudio.secured;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.model.Status;
import com.trackstudio.soap.bean.StatusBean;
import com.trackstudio.tools.Null;
import com.trackstudio.tools.PropertyContainer;
import com.trackstudio.tools.textfilter.MacrosUtil;

import net.jcip.annotations.Immutable;

/**
 * Bean which represents status
 */
@Immutable
public class SecuredStatusBean extends Secured implements Comparable {

    private final String id;
    private final String color;
    private final String name;
    private final String workflowId;
    private final boolean isStart;
    private final boolean isFinish;
    private final boolean isSecondaryStart;

    public SecuredStatusBean(Status status, SessionContext secure) throws GranException {
        this.id = status.getId();
        this.sc = secure;
        this.workflowId = status.getWorkflow() != null ? status.getWorkflow().getId() : null;
        this.name = status.getName();
        this.color = status.getColor();
        this.isFinish = status.isFinish();
        this.isStart = status.isStart();
        this.isSecondaryStart = status.isSecondaryStart();
    }

    public boolean isSecondaryStart() {
        return isSecondaryStart;
    }

    /**
     * Returns validated color value safe for CSS injection.
     * Validates against hex colors (#RGB, #RRGGBB, #RGBA, #RRGGBBAA) and rgb() formats.
     * @return validated color or "transparent" if invalid
     */
    public String getColor() {
        // R1: Use MacrosUtil.validateColor() to avoid duplication, add toUpperCase() for backward compatibility
        return MacrosUtil.validateColor(color).toUpperCase();
    }

    public String getEncodeColor() {
        return Null.stripNullText(color);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }


    public boolean isStart() {
        return this.isStart;
    }

    public boolean isFinish() {
        return this.isFinish;
    }

    public SecuredWorkflowBean getWorkflow() throws GranException {
        return AdapterManager.getInstance().getSecuredFindAdapterManager().findWorkflowById(sc, this.workflowId);
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

    public StatusBean getSOAP() throws GranException {
        StatusBean bean = new StatusBean();
        bean.setColor(color);
        bean.setFinish(isFinish);
        bean.setId(id);
        bean.setName(name);
        bean.setStart(isStart);
        bean.setWorkflowId(workflowId);
        return bean;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public String getImage() {
        StringBuffer sb = new StringBuffer();
        String imageState = isStart && isFinish ? "finish" : isStart ? "start" : isFinish ? "finish" : "";
        sb.append("/cssimages/").append(imageState).append("state.png");
        return sb.toString();
    }
}