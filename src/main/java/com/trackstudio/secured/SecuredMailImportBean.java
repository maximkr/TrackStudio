package com.trackstudio.secured;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.model.MailImport;
import com.trackstudio.soap.bean.MailImportBean;
import com.trackstudio.soap.bean.MstatusBean;
import com.trackstudio.tools.PropertyContainer;

import net.jcip.annotations.Immutable;

/**
 * Bean which represents e-mail import rule
 */
@Immutable
public class SecuredMailImportBean extends Secured {

    private final String id;
    private final String name;
    private final String domain;
    private final String keywords;
    private final Integer searchIn;
    private final Integer order;
    private final String categoryId;
    private final String taskId;
    private final String ownerId;
    private final String mstatusId;
    private final boolean active;
    private final boolean importUnknown;

    public SecuredMailImportBean(MailImport m, SessionContext sc) throws GranException {
        this.id = m.getId();
        this.sc = sc;
        this.taskId = m.getTask() != null ? m.getTask().getId() : null;
        if (sc.allowedByACL(taskId)) {
            this.name = m.getName();
            this.domain = m.getDomain();
            this.keywords = m.getKeywords();
            this.searchIn = m.getSearchIn();
            this.order = m.getOrder();
            this.categoryId = m.getCategory() != null ? m.getCategory().getId() : null;
            this.ownerId = m.getOwner() != null ? m.getOwner().getId() : null;
            this.mstatusId = m.getMstatus() != null ? m.getMstatus().getId() : null;
            this.active = m.getActive() == 1;
            this.importUnknown = m.getImportUnknown() == 1;
        } else {
            this.name = null;
            this.domain = null;
            this.keywords = null;
            this.searchIn = null;
            this.order = null;
            this.categoryId = null;
            this.ownerId = null;
            this.mstatusId = null;
            this.active = false;
            this.importUnknown = false;

        }
    }

    public String getId() {
        return id;
    }

    public String getKeywords() {
        return keywords;
    }

    public Integer getSearchIn() {
        return searchIn;
    }

    public String getName() {
        return name;
    }

    public String getDomain() {
        return domain;
    }

    public SecuredCategoryBean getCategory() throws GranException {
        return AdapterManager.getInstance().getSecuredFindAdapterManager().findCategoryById(getSecure(), this.categoryId);
    }

    public SecuredMstatusBean getMstatus() throws GranException {
        return AdapterManager.getInstance().getSecuredFindAdapterManager().findMstatusById(getSecure(), this.mstatusId);
    }

    public SecuredUserBean getOwner() throws GranException {
        if (ownerId != null) {
            return new SecuredUserBean(this.ownerId, getSecure());
        }
        return null;
    }

    public SecuredTaskBean getTask() throws GranException {
        return new SecuredTaskBean(taskId, getSecure());
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
        return getTask().isAllowedByACL() && (getOwnerId() == null || getOwner().isAllowedByACL());
    }

    public boolean canManage() throws GranException {
        return isAllowedByACL() && getSecure().canAction(Action.manageEmailImportRules, getTaskId());
    }

    public boolean canView() throws GranException {
        return getTask().canView();
    }

    public MailImportBean getSOAP() throws GranException {
        MailImportBean bean = new MailImportBean();
        bean.setCategoryId(categoryId);
        bean.setId(id);
        bean.setName(name);
        bean.setDomain(domain);
        bean.setKeywords(keywords);
        bean.setSearchIn(searchIn != null ? searchIn : 0);
        bean.setTaskId(taskId);
        bean.setOwnerId(ownerId);
        bean.setMstatus(new MstatusBean(mstatusId));
        bean.setActive(active);
        bean.setOrder(order != null ? order : 0);
        return bean;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public String getTaskId() {
        return taskId;
    }

    public Integer getOrder() {
        return order;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getMStatusId() {
        return mstatusId;
    }

    public boolean isActive() {
        return active;
    }

    public boolean getImportUnknown() {
        return importUnknown;
    }

}