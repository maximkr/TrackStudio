package com.trackstudio.secured;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.cache.AttachmentCacheItem;
import com.trackstudio.kernel.cache.MessageCacheItem;
import com.trackstudio.kernel.cache.TaskRelatedManager;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.soap.bean.MessageBean;
import com.trackstudio.tools.PropertyContainer;
import com.trackstudio.tools.formatter.HourFormatter;

import net.jcip.annotations.ThreadSafe;

/**
 * Bean which represents message
 */
@ThreadSafe
public class SecuredMessageBean extends Secured {

    protected volatile boolean allowedByACL;
    private final MessageCacheItem message;
    private final AtomicReference<SecuredMstatusBean> mstatus = new AtomicReference<SecuredMstatusBean>();
    private final AtomicReference<SecuredUserBean> handlerUser = new AtomicReference<SecuredUserBean>();
    private final AtomicReference<SecuredPrstatusBean> handlerGroup = new AtomicReference<SecuredPrstatusBean>();
    private final AtomicReference<SecuredPriorityBean> priority = new AtomicReference<SecuredPriorityBean>();
    private final AtomicReference<SecuredResolutionBean> resolution = new AtomicReference<SecuredResolutionBean>();
    private final AtomicReference<SecuredTaskBean> task = new AtomicReference<SecuredTaskBean>();
    private final AtomicReference<SecuredUserBean> submitter = new AtomicReference<SecuredUserBean>();
    private final AtomicReference<CopyOnWriteArrayList<SecuredMessageAttachmentBean>> attachments = new AtomicReference<CopyOnWriteArrayList<SecuredMessageAttachmentBean>>();

    protected SecuredMessageBean() {
        this.message = null;
    }

    public SecuredMessageBean(MessageCacheItem message, SessionContext secure) throws GranException {
        this.message = message;
        this.sc = secure;
        if (sc != null)
            this.allowedByACL = getSecure().allowedByACL(message.getTaskId());
        else
            this.allowedByACL = false;
    }

    public SecuredMessageBean(MessageCacheItem message, SessionContext secure, boolean allowed) throws GranException {
        this.message = message;
        this.sc = secure;
        this.allowedByACL = allowed;
    }

    public SecuredMessageBean(String message, SessionContext secure) throws GranException {
        this(TaskRelatedManager.findMessage(message), secure);
    }

    public MessageCacheItem getMessage() {
        return message;
    }

    public List<SecuredMessageAttachmentBean> getAttachments() throws GranException {
        if (!allowedByACL)
            return null;

        if (!getSecure().canAction(Action.viewTaskAttachments, getTaskId()))
            return null;

        CopyOnWriteArrayList<SecuredMessageAttachmentBean> v = attachments.get();
        if (v != null)
            return v;

        CopyOnWriteArrayList<SecuredMessageAttachmentBean> v1 = new CopyOnWriteArrayList<SecuredMessageAttachmentBean>();
        List<AttachmentCacheItem> l = KernelManager.getAttachment().getAttachmentList(getTaskId(), getId(), null);
        for (AttachmentCacheItem aca : l)
            v1.add(new SecuredMessageAttachmentBean(aca, getSecure()));

        if (attachments.compareAndSet(null, v1))
            return v1;
        else
            return attachments.get();
    }

    public String getId() {
        return message.getId();
    }

    public String getDescription() throws GranException {
        if (!allowedByACL)
            return null;

        return message.getDescription();
    }

    public String getTextDescription() throws GranException {
        return allowedByACL ? message.getTextDescription() : null;
    }

    public Calendar getTime() {
        return allowedByACL ? message.getTime() : null;
    }

    public String getTimeAsString() throws GranException {
        return getSecure().getUser().getDateFormatter().parse(getTime());
    }

    public Long getHrs() throws GranException {
        return allowedByACL && getSecure().canAction(Action.viewTaskActualBudget, getTaskId()) ? message.getHrs() : null;
    }

    public Calendar getDeadline() throws GranException {
        return allowedByACL && getSecure().canAction(Action.viewTaskDeadline, getTaskId()) ? message.getDeadline() : null;
    }

    public String getDeadlineAsString() throws GranException {
        return getSecure().getUser().getDateFormatter().parse(getDeadline());
    }

    public Long getBudget() throws GranException {
        return allowedByACL && getSecure().canAction(Action.viewTaskBudget, getTaskId()) ? message.getBudget() : null;
    }

    protected PropertyContainer getContainer() {
        PropertyContainer pc = container.get();
        if (pc != null)
            return pc; // object in cache, return it

        PropertyContainer newPC = new PropertyContainer();
        newPC.put(message.getTime()).put(getId());

        if (container.compareAndSet(null, newPC)) // try to update
            return newPC; // we can update - return loaded value
        else
            return container.get(); // some other thread already updated it - use saved value
    }

    public SecuredMstatusBean getMstatus() throws GranException {
        if (!allowedByACL)
            return null;

        SecuredMstatusBean v = mstatus.get();
        if (v != null)
            return v;

        SecuredMstatusBean v1 = AdapterManager.getInstance().getSecuredFindAdapterManager().findMstatusById(getSecure(), getMstatusId());
        if (mstatus.compareAndSet(null, v1))
            return v1;
        else
            return mstatus.get();
    }

    public SecuredUserBean getHandlerUser() throws GranException {
        if (getHandlerUserId() == null || getHandlerUserId().length() == 0)
            return null;

        SecuredUserBean v = handlerUser.get();
        if (v != null)
            return v;

        SecuredUserBean v1 = new SecuredUserBean(getHandlerUserId(), getSecure());
        if (handlerUser.compareAndSet(null, v1))
            return v1;
        else
            return handlerUser.get();
    }

    public SecuredPrstatusBean getHandlerGroup() throws GranException {
        if (getHandlerGroupId() == null)
            return null;

        SecuredPrstatusBean v = handlerGroup.get();
        if (v != null)
            return v;

        SecuredPrstatusBean v1 = new SecuredPrstatusBean(KernelManager.getFind().findPrstatus(getHandlerGroupId()), getSecure());
        if (handlerGroup.compareAndSet(null, v1))
            return v1;
        else
            return handlerGroup.get();
    }

    public SecuredPriorityBean getPriority() throws GranException {
        if (getPriorityId() == null)
            return null;

        if (!getSecure().canAction(Action.viewTaskPriority, getTaskId()))
            return null;

        SecuredPriorityBean v = priority.get();
        if (v != null)
            return v;

        SecuredPriorityBean v1 = AdapterManager.getInstance().getSecuredFindAdapterManager().findPriorityById(getSecure(), getPriorityId());
        if (priority.compareAndSet(null, v1))
            return v1;
        else
            return priority.get();
    }

    public SecuredResolutionBean getResolution() throws GranException {
        if (getResolutionId() == null)
            return null;

        if (!getSecure().canAction(Action.viewTaskResolution, getTaskId()))
            return null;

        SecuredResolutionBean v = resolution.get();
        if (v != null)
            return v;

        SecuredResolutionBean v1 = AdapterManager.getInstance().getSecuredFindAdapterManager().findResolutionById(getSecure(), getResolutionId());
        if (resolution.compareAndSet(null, v1))
            return v1;
        else
            return resolution.get();
    }

    public boolean isAllowedByACL() throws GranException {
        return getTask().isAllowedByACL() && getSubmitter().isAllowedByACL();
    }

    public boolean canManage() throws GranException {
        return isAllowedByACL() && getSecure().canAction(Action.deleteOperations, getTaskId());
    }

    public SecuredTaskBean getTask() throws GranException {
        if (getTaskId() == null)
            return null;

        SecuredTaskBean v = task.get();
        if (v != null)
            return v;

        SecuredTaskBean v1 = new SecuredTaskBean(getTaskId(), getSecure());
        if (task.compareAndSet(null, v1))
            return v1;
        else
            return task.get();
    }

    public SecuredUserBean getSubmitter() throws GranException {
        if (getSubmitterId() == null)
            return null;

        SecuredUserBean v = submitter.get();
        if (v != null)
            return v;

        SecuredUserBean v1 = new SecuredUserBean(getSubmitterId(), getSecure());
        if (submitter.compareAndSet(null, v1))
            return v1;
        else
            return submitter.get();
    }

    public boolean canView() throws GranException {
        return getTask().canView();
    }

    public MessageBean getSOAP() throws GranException {
        MessageBean bean = new MessageBean();
        bean.setBudget(getBudget() != null ? getBudget() : 0.0d);
        bean.setDeadline(getDeadline() != null ? getDeadline().getTimeInMillis() : -1L);
        bean.setDescription(getDescription() != null ? getDescription() : "");
        bean.setHandlerUserId(getHandlerUserId());
        bean.setHandlerGroupId(getHandlerGroupId());
        bean.setHrs(getHrs() != null ? getHrs() : 0.0d);
        bean.setId(getId());
        bean.setMstatusId(getMstatusId());
        bean.setPriorityId(getPriorityId());
        bean.setResolutionId(getResolutionId());
        bean.setSubmitterId(getSubmitterId());
        bean.setTaskId(getTaskId());
        bean.setTime(getTime() != null ? getTime().getTimeInMillis() : -1L);
        return bean;
    }

    public String toString() {
        try {
            return getDescription();
        } catch (Exception e) {
            return null;
        }
    }

    public String getTaskId() {
        return allowedByACL ? message.getTaskId() : null;
    }

    public String getSubmitterId() {
        return allowedByACL ? message.getSubmitterId() : null;
    }

    public String getResolutionId() {
        return allowedByACL ? message.getResolutionId() : null;
    }

    public String getPriorityId() {
        return allowedByACL ? message.getPriorityId() : null;
    }

    public String getHandlerUserId() {
        return allowedByACL ? message.getHandlerUserId() : null;
    }

    public String getHandlerGroupId() {
        return allowedByACL ? message.getHandlerGroupId() : null;
    }

    public String getMstatusId() {
        return allowedByACL ? message.getMstatusId() : null;
    }

    public SecuredUserBean getHandler() throws GranException {
        return getHandlerUser();
    }

    public String getHandlerId() throws GranException {
        return getHandlerUserId();
    }

    public String getBudgetAsString() throws GranException {
        if (getBudget() != null && getBudget() > 0) {

            try {

                HourFormatter hf = new HourFormatter(getBudget(), getTask().getBudgetFormat(), getSecure().getLocale());
                return hf.getString();
/*                HourFormatter hf =   new HourFormatter(getBudget(), task.getBudgetFormat());
                return I18n.getString(getSecure().getLocale(), "MSG_BUDGET_FORMAT", new Object[]{hf.getYears(), hf.getMonths(), hf.getWeeks(), hf.getDays(), hf.getHours(), hf.getMinutes(), hf.getSeconds()});*/

            } catch (Exception e) {
                throw new GranException(e);
            }
        } else return "";
    }

    public String getActualBudgetAsString() throws GranException {
        if (getHrs() != null && getHrs() > 0) {
            try {

                /*HourFormatter hf =   new HourFormatter(getHrs(), task.getBudgetFormat());
      return I18n.getString(getSecure().getLocale(), "MSG_BUDGET_FORMAT", new Object[]{hf.getYears(), hf.getMonths(), hf.getWeeks(), hf.getDays(), hf.getHours(), hf.getMinutes(), hf.getSeconds()});*/
                HourFormatter hf = new HourFormatter(getHrs(), getTask().getBudgetFormat(), getSecure().getLocale());
                return hf.getString();

            } catch (Exception e) {
                throw new GranException(e);
            }
        } else return "";
    }

    public boolean isTestEmpty() throws GranException {
        return this.getPriorityId() == null && this.getDeadline() == null && this.getBudgetAsString().isEmpty() && this.getActualBudgetAsString().isEmpty() && (this.getAttachments() == null || this.getAttachments().isEmpty()) && (this.getDescription() == null || this.getDescription().isEmpty());
    }
}
