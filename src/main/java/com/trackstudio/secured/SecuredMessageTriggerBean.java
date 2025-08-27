package com.trackstudio.secured;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.csv.CSVImport;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.kernel.manager.AttachmentArray;
import com.trackstudio.tools.Null;
import com.trackstudio.tools.textfilter.MacrosUtil;

import net.jcip.annotations.ThreadSafe;

/**
 * Bean which represents message trigger
 */
@ThreadSafe
public class SecuredMessageTriggerBean extends SecuredMessageBean  {
    private volatile ConcurrentMap udfValues;
    private volatile String id;
    private volatile String description;
    private volatile Calendar time;
    private volatile Long hrs;
    private volatile Calendar deadline;
    private volatile Long budget;
    private volatile String taskNumber;
    private volatile String taskId;
    private volatile String submitterId;
    private volatile String resolutionId;
    private volatile String priorityId;
    private volatile String handlerId;
    private volatile String handlerUserId;
    private volatile String handlerGroupId;
    private volatile String mstatusId;
    private final CopyOnWriteArrayList<AttachmentArray> atts;
    private static final LockManager lockManager = LockManager.getInstance();

    public SecuredMessageTriggerBean(String id, String description, Calendar time, Long hrs, Calendar deadline, Long budget, String taskId, String submitterId, String resolutionId, String priorityId, String handlerId, String handlerUserId, String handlerGroupId, String mstatusId, Map udfMap, SessionContext secure, List<AttachmentArray> atts) throws GranException {
        this.sc = secure;
        this.allowedByACL = sc.allowedByACL(taskId);
        this.id = id;
        this.description = description;
        if (time!=null)
            this.time = (Calendar)time.clone();
        else
            this.time = null;
        this.hrs = hrs;
        if (deadline!=null)
            this.deadline = (Calendar)deadline.clone();
        else
            this.deadline = null;
        this.budget = budget;
        this.taskId = taskId;
        this.submitterId = submitterId;
        this.resolutionId = resolutionId;
        this.priorityId = priorityId;
        this.handlerId = handlerId;
        this.handlerUserId = handlerUserId;
        this.handlerGroupId = handlerGroupId;
        this.mstatusId = mstatusId;
        this.udfValues = new ConcurrentHashMap(Null.removeNullElementsFromMap(udfMap));
        this.atts = new CopyOnWriteArrayList(Null.removeNullElementsFromList(atts));
    }

    public SecuredMessageTriggerBean(String taskId, SessionContext secure) throws GranException {
        this.sc = secure;
        this.allowedByACL = sc.allowedByACL(taskId);
        this.udfValues = new ConcurrentHashMap();
        this.atts = new CopyOnWriteArrayList();    }

    public SecuredMessageTriggerBean(SecuredMessageBean source, Map udf) throws GranException {

        this.sc = source.getSecure();
        this.allowedByACL = sc.allowedByACL(source.getTaskId());
        this.id = source.getId();
        this.description = source.getDescription();
        if (source.getTime()!=null)
            this.time = (Calendar)source.getTime().clone();
        else
            this.time = null;

        this.hrs = source.getHrs();
        if (source.getDeadline()!=null)
            this.deadline = (Calendar)source.getDeadline().clone();
        else
            this.deadline  = null;

        this.budget = source.getBudget();
        this.taskId = source.getTaskId();
        this.submitterId = source.getSubmitterId();
        this.resolutionId = source.getResolutionId();
        this.priorityId = source.getPriorityId();
        this.handlerId = source.getHandlerId();
        this.handlerUserId = source.getHandlerUserId();
        this.handlerGroupId = source.getHandlerGroupId();
        this.mstatusId = source.getMstatusId();
        this.udfValues = new ConcurrentHashMap(Null.removeNullElementsFromMap(udf));
        this.atts = new CopyOnWriteArrayList();
    }

    public List<AttachmentArray> getAtts() {
        return atts;
    }

    public Map<String, String> getUdfValues() {
        return udfValues;
    }

    /**
     * Set custom field's values
     *
     * @param udfValues map were keys are custom field's captions and values are new values
     */
    public void setUdfValues(Map udfValues) {
        this.udfValues = new ConcurrentHashMap(Null.removeNullElementsFromMap(udfValues));
    }

    public String toString() {
        return getDescription();
    }

    public SecuredMessageTriggerBean create(boolean sendMail) throws GranException {
        lockManager.getLock(taskId).lock();
        try {
            if (getUdfValues() != null) {
                for (String caption : getUdfValues().keySet()) {
                    String udfValue = getUdfValues().get(caption);
                    AdapterManager.getInstance().getSecuredUDFAdapterManager().setMessageUDFValueSimple(getSecure(), getTaskId(), caption, udfValue, mstatusId);
                }
            }
            String messageId = AdapterManager.getInstance().getSecuredMessageAdapterManager().createMessage(getSecure(), getTaskId(), getMstatusId(), getDescription(), getHrs(), getHandlerUserId(), getHandlerGroupId(), getResolutionId(), getPriorityId(), getDeadline(), getBudget(), sendMail, time);
            if (getAtts() != null && getAtts().size() != 0) {
                AdapterManager.getInstance().getSecuredAttachmentAdapterManager().createAttachment(getSecure(), null, messageId, getSecure().getUserId(), getAtts(), false);
                String convertedDesc = getDescription();
                for (AttachmentArray array : getAtts()) {
                    if (array.isTinyMCEImage()) {
                        convertedDesc = convertedDesc.replaceAll(array.getName().toString(), String.format(MacrosUtil.lyteImg, array.getContext(), getTask().getNumber(),array.getInitialID(), array.getInitialID(), array.getContext(), array.getInitialID()));
                    }
                }
                AdapterManager.getInstance().getSecuredMessageAdapterManager().updateDescription(getSecure(), messageId, convertedDesc);
            }
            return new SecuredMessageTriggerBean(new SecuredMessageBean(messageId, getSecure()), getUdfValues());
        } finally {
            lockManager.getLock(taskId).unlock();
        }
    }

    /**
     * Set custom field's value
     *
     * @param caption custom field's caption
     * @param value   new value
     */
    public void setUdfValue(String caption, String value) {
        if (getUdfValues()== null) {
            this.udfValues = new ConcurrentHashMap();
        }
        if (value == null || value.isEmpty()) {
            getUdfValues().remove(caption);
        } else {
            getUdfValues().put(caption, value);
        }
    }

    /**
     * Get custom field's value
     *
     * @param caption custom field's caption
     * @return value
     */
    public String getUdfValue(String caption) {
        if (getUdfValues()!=null){
            Object o = getUdfValues().get(caption);
            if (o != null) return o.toString();
            else return null;
        }
        else return null;
    }

    public String getId() {
        return id;
    }

    /**
     * Set id
     *
     * @param id id
     */
    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Set description
     *
     * @param description description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    public Calendar getTime() {
        if (time!=null)
            return (Calendar)time.clone();
        else
            return null;
    }

    /**
     * Set time
     *
     * @param time time
     */
    public void setTime(Calendar time) {
        if (time!=null)
            this.time = (Calendar)time.clone();
        else
            this.time = null;
    }

    public Long getHrs() {
        return hrs;
    }

    /**
     * Set actual budget
     *
     * @param hrs actual budget
     */
    public void setHrs(Long hrs) {
        this.hrs = hrs;
    }

    public Calendar getDeadline() {
        if (deadline!=null)
            return (Calendar)deadline.clone();
        else
            return null;
    }

    /**
     * Set deadline
     *
     * @param deadline deadline
     */
    public void setDeadline(Calendar deadline) {
        if (deadline!=null)
            this.deadline = (Calendar)deadline.clone();
        else
            this.deadline = null;
    }

    public Long getBudget() {
        return budget;
    }

    /**
     * Set budget
     *
     * @param budget budget
     */
    public void setBudget(Long budget) {
        this.budget = budget;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getSubmitterId() {
        return submitterId;
    }

    /**
     * Set submitter
     *
     * @param submitterId submitter's id
     * @throws GranException
     */
    public void setSubmitterId(String submitterId) throws GranException {
        if (this.submitterId == null || !this.submitterId.equals(submitterId)) {
            this.submitterId = submitterId;
        }
    }

    public String getResolutionId() {
        return resolutionId;
    }

    public String getPriorityId() {
        return priorityId;
    }

    /**
     * Set priority
     *
     * @param priorityId priority's id
     * @throws GranException
     */
    public void setPriorityId(String priorityId) throws GranException {
        this.priorityId = priorityId;
    }

    public String getHandlerId() {
        return handlerId;
    }

    public String getHandlerUserId() {
        return handlerUserId;
    }

    /**
     * Set handler user
     *
     * @param handlerUserId user's id
     * @throws GranException
     */
    public void setHandlerUserId(String handlerUserId) throws GranException {
        this.handlerUserId = handlerUserId;
    }

    public String getHandlerGroupId() {
        return handlerGroupId;
    }

    /**
     * Set handler group
     *
     * @param handlerGroupId group's id
     * @throws GranException
     */
    public void setHandlerGroupId(String handlerGroupId) throws GranException {
        this.handlerGroupId = handlerGroupId;
    }

    public String getMstatusId() {
        return mstatusId;
    }

    /**
     * Set handler user
     *
     * @param handlerUser user's login
     * @throws GranException
     */
    public void setHandlerUser(String handlerUser) throws GranException {
        this.handlerUserId = CSVImport.findUserIdByLogin(handlerUser);
    }

    /**
     * Set hadler grop
     *
     * @param handlerGroup group's login
     * @throws GranException
     */
    public void setHandlerGroup(String handlerGroup) throws GranException {
        this.handlerGroupId = CSVImport.findUserIdByLogin(handlerGroup);
    }

    /**
     * Set priority
     *
     * @param priority priority's name
     * @throws GranException
     */
    public void setPriority(String priority) throws GranException {
        try {
            this.priorityId = CSVImport.findPriorityIdByName(priority, getTask().getCategory().getName());
        }
        catch (NullPointerException npe) {
            throw new GranException("Task ID is null");
        }
    }

    /**
     * Set submitter
     *
     * @param submitter submitter's login
     * @throws GranException
     */
    public void setSubmitter(String submitter) throws GranException {
        this.submitterId = CSVImport.findUserIdByLogin(submitter);
    }

    /**
     * Set task which ownes the message
     *
     * @param number task's number
     * @throws GranException
     */
    public void setTask(String number) throws GranException {
        if (this.taskNumber == null || !this.taskNumber.equals(number)) {
            this.taskNumber = number;
            this.taskId = CSVImport.findTaskIdByNumber(number);
        }
    }

    public void setMstatus(String mstatus) throws GranException {

        try {
            this.mstatusId = CSVImport.findMessageTypeIdByName(mstatus, getTask().getCategory().getName());
        } catch (NullPointerException npe) {
            throw new GranException("Task ID is null");
        }
    }

    /**
     * Set resolution
     *
     * @param resolution resolution's name
     * @throws GranException
     */
    public void setResolution(String resolution) throws GranException {
        try {
            this.resolutionId = CSVImport.findResolutionIdByName(resolution, getMstatus().getName());
        } catch (NullPointerException npe) {
            throw new GranException("Mstatus ID is null");
        }
    }
}

