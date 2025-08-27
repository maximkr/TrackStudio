package com.trackstudio.secured;

import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.action.task.CategoryEditAction;
import com.trackstudio.app.Preferences;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.model.Category;
import com.trackstudio.model.Trigger;
import com.trackstudio.soap.bean.CategoryBean;
import com.trackstudio.tools.PropertyContainer;

/**
 * Bean which represents category
 */
public class SecuredCategoryBean extends Secured {
    private static final Log log = LogFactory.getLog(CategoryEditAction.class);

    private final String id;
    private final String name;
    private final String action, description;
    private final String workflowId;
    private AtomicReference<String> template = new AtomicReference<String>();
    private final String budget;
    private final String icon;
    private final String taskId;
    private final boolean handlerRequired;
    private final boolean groupHandlerAllowed;
    private final String createBefore;
    private final String createInsteadOf;
    private final String createAfter;
    private final String updateBefore;
    private final String updateInsteadOf;
    private final String updateAfter;
    private final String preferences;

    public SecuredCategoryBean(Category cat, SessionContext sec) throws GranException {
        this.id = cat.getId();
        this.sc = sec;
        handlerRequired = cat.getHandlerRequired() != null && cat.getHandlerRequired() == 1;
        groupHandlerAllowed = cat.getGroupHandlerAllowed() != null && cat.getGroupHandlerAllowed() == 1;
        this.taskId = cat.getTask() != null ? cat.getTask().getId() : null;
        if (sec.taskOnSight(this.taskId)) {
            this.name = cat.getName();
            this.action = cat.getAction();
            this.budget = cat.getBudget();
            this.icon = cat.getIcon();
            this.description = cat.getDescription();
            this.workflowId = cat.getWorkflow() != null ? cat.getWorkflow().getId() : null;
            this.preferences = cat.getPreferences();

            Trigger trg = cat.getCrTrigger();
            if (trg != null) {
                this.createBefore = trg.getBefore();
                this.createAfter = trg.getAfter();
                this.createInsteadOf = trg.getInsteadOf();
            } else {
                this.createBefore = null;
                this.createAfter = null;
                this.createInsteadOf = null;
            }

            trg = cat.getUpdTrigger();
            if (trg != null) {
                this.updateBefore = trg.getBefore();
                this.updateAfter = trg.getAfter();
                this.updateInsteadOf = trg.getInsteadOf();
            } else {
                this.updateBefore = null;
                this.updateAfter = null;
                this.updateInsteadOf = null;
            }
        } else {
            this.name = null;
            this.action = null;
            this.budget = null;
            this.icon = null;
            this.description = null;
            this.workflowId = null;
            this.preferences = null;

            this.createBefore = null;
            this.createAfter = null;
            this.createInsteadOf = null;

            this.updateBefore = null;
            this.updateAfter = null;
            this.updateInsteadOf = null;

        }
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }


    public String getTemplate() throws GranException {
        String t = template.get();
        if (t != null)
            return t; // object in cache, return it

        String t1 = AdapterManager.getInstance().getSecuredCategoryAdapterManager().getTemplate(getSecure(), id);

        if (template.compareAndSet(null, t1)) // try to update
            return t1; // we can update - return loaded value
        else
            return template.get(); // some other thread already updated it - use saved value
    }

    public SecuredTaskBean getTask() throws GranException {
        return new SecuredTaskBean(taskId, getSecure());
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
        return getTask().isAllowedByACL();
    }

    public boolean canManage() throws GranException {
        return isAllowedByACL() && getSecure().canAction(Action.manageCategories, getTaskId());
    }


    public boolean canView() throws GranException {
        return getTask().canView();
    }

    public boolean isHandlerRequired() {
        return handlerRequired;
    }

    public boolean getHandlerRequired() {
        return handlerRequired;
    }

    public CategoryBean getSOAP() throws GranException {
        CategoryBean bean = new CategoryBean();
        bean.setId(id);
        bean.setName(name);
        bean.setAction(action);
        bean.setDescription(description);
        bean.setBudget(budget);
        bean.setPreferences(preferences);
        bean.setTaskId(taskId);
        bean.setIcon(icon);
        bean.setWorkflowId(workflowId);
        bean.setHandlerRequired(handlerRequired);
        bean.setGroupHandlerAllowed(groupHandlerAllowed);
        bean.setTemplate(template.get());
        bean.setCreateTriggerAfterId(createAfter);
        bean.setCreateTriggerBeforeId(createBefore);
        bean.setCreateTriggerInsteadOfId(createInsteadOf);
        bean.setUpdateTriggerBeforeId(updateBefore);
        bean.setUpdateTriggerInsteadOfId(updateInsteadOf);
        bean.setUpdateTriggerAfterId(updateAfter);
        return bean;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getCreateBefore() {
        return createBefore;
    }

    public String getCreateInsteadOf() {
        return createInsteadOf;
    }

    public boolean hasCreateInsteadOfTrigger() {
        return createInsteadOf != null;
    }

    public boolean hasUpdateInsteadOfTrigger() {
        return updateInsteadOf != null;
    }

    public boolean hasCreateBeforeTrigger() {
        return createBefore != null;
    }

    public String getCreateAfter() {
        return createAfter;
    }

    public boolean hasCreateAfterTrigger() {
        return createAfter != null;
    }

    public boolean hasUpdateBeforeTrigger() {
        return updateBefore != null;
    }

    public String getUpdateBefore() {
        return updateBefore;
    }

    public String getUpdateInsteadOf() {
        return updateInsteadOf;
    }

    public String getUpdateAfter() {
        return updateAfter;
    }

    public boolean hasUpdateAfterTrigger() {
        return updateAfter != null;
    }

    public boolean isGroupHandlerAllowed() {
        return groupHandlerAllowed;
    }


    public String getAction() {
        return action;
    }

    public String getDescription() {
        return description;
    }

    public String getBudget() {
        return budget != null ? budget : "";
    }


    public String getPreferences() {
        return preferences != null ? preferences : "";
    }


    public String getIcon() {
        return icon;
    }

    public Boolean isValidParentCategory() throws GranException {
        return !AdapterManager.getInstance().getSecuredCategoryAdapterManager().getParentCategoryList(getSecure(), id, taskId).isEmpty();
    }

    public Boolean getIsValidParentCategory() throws GranException {
        return isValidParentCategory();
    }

    public boolean isValid() throws GranException {
        return AdapterManager.getInstance().getSecuredCategoryAdapterManager().getCategoryIsValid(getSecure(), id, taskId);
    }

    public boolean getIsValid() throws GranException {
        return isValid();
    }

    public boolean isActive() {
        return Preferences.isCategoryHidden(getPreferences());
    }

}