package com.trackstudio.secured;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.trackstudio.app.TriggerManager;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.csv.CSVImport;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.builder.TaskBuilder;
import com.trackstudio.exception.GranException;
import com.trackstudio.exception.TaskNotFoundException;
import com.trackstudio.kernel.cache.TaskRelatedInfo;
import com.trackstudio.kernel.cache.TaskRelatedManager;
import com.trackstudio.kernel.manager.AttachmentArray;
import com.trackstudio.tools.Null;
import com.trackstudio.tools.audit.trail.AuditTrailTask;
import com.trackstudio.tools.audit.trail.AuditUtil;

import net.jcip.annotations.ThreadSafe;

/**
 * Bean which represents task trigger
 */
@ThreadSafe
public class SecuredTaskTriggerBean extends SecuredTaskBean implements SecuredTaskBeanInterface {
    public  volatile ConcurrentMap<String, String> udfValues = new ConcurrentHashMap<String, String>();
    private volatile String name;
    private volatile String description;
    private volatile String shortname;
    private volatile Long abudget;
    private volatile String number;
    private volatile String submitterId;
    private volatile String handlerId;
    private volatile String handlerUserId;
    private volatile String handlerGroupId;
    private volatile String parentNumber;
    private volatile String parentId;
    private volatile String categoryId;
    private volatile String workflowId;
    private volatile String statusId;
    private volatile String resolutionId;
    private volatile String priorityId;
    private volatile String id;
    private volatile boolean taskExists = false;
    private volatile CopyOnWriteArrayList<AttachmentArray> atts;
    private volatile boolean copyOrMoveOpr;
    private volatile boolean needSend;
    private volatile String previousNumber;

    private SecuredTaskTriggerBean() {
    }

    public SecuredTaskTriggerBean(String id, String description, String name, String shortname, Calendar submitdate, Calendar updatedate, Calendar closedate, Long abudget, Long budget, Calendar deadline, String number, String submitterId, String handlerId, String handlerUserId, String handlerGroupId, String parentId, String categoryId, String workflowId, String statusId, String resolutionId, String priorityId, Map<String, String> udfMap, SessionContext sc, List<AttachmentArray> atts, boolean copyOrMoveOpr) throws GranException {
        //super(id, sc);
        this.sc = sc;
        this.id = id;

        this.parentId = parentId;
        String usingId;
        if (id != null && TaskRelatedManager.getInstance().isTaskExists(id)) {
            usingId = id;
            taskExists = true;
        } else if (TaskRelatedManager.getInstance().isTaskExists(parentId)) usingId = parentId;
        else throw new TaskNotFoundException(parentId);

        if (allowedByACL = sc.allowedByACL(usingId))
            this.onSight = true;
        else
            this.onSight = sc.taskOnSight(usingId);
        this.description = description;
        this.name = name;
        this.shortname = shortname;
        this.submitdate = submitdate;
        this.updatedate = updatedate;
        this.closedate = closedate;
        this.abudget = abudget;
        this.budget = budget;
        this.deadline = deadline;
        this.number = number;
        this.submitterId = submitterId;
        this.handlerId = handlerId;
        this.handlerUserId = handlerUserId;
        this.handlerGroupId = handlerGroupId;
        this.parentId = parentId;
        this.categoryId = categoryId;
        this.workflowId = workflowId;
        this.statusId = statusId;
        this.resolutionId = resolutionId;
        this.priorityId = priorityId;
        this.udfValues = new ConcurrentHashMap(Null.removeNullElementsFromMap(udfMap));
        this.atts = new CopyOnWriteArrayList<AttachmentArray>(Null.removeNullElementsFromList(atts));
        this.copyOrMoveOpr = copyOrMoveOpr;
    }

    public SecuredTaskTriggerBean(String id, String description, String name, String shortname, Calendar submitdate, Calendar updatedate, Calendar closedate, Long abudget, Long budget, Calendar deadline, String number, String submitterId, String handlerId, String handlerUserId, String handlerGroupId, String parentId, String categoryId, String workflowId, String statusId, String resolutionId, String priorityId, HashMap<String, String> udfMap, SessionContext sc) throws GranException {
        this.sc = sc;
        this.id = id;

        this.parentId = parentId;
        String usingId;
        if (id!= null && TaskRelatedManager.getInstance().isTaskExists(id)) {
            usingId = id;
            taskExists = true;
        } else if (TaskRelatedManager.getInstance().isTaskExists(parentId)) usingId = parentId;
        else throw new TaskNotFoundException(parentId);

        if (allowedByACL = sc.allowedByACL(usingId))
            this.onSight = true;
        else
            this.onSight = sc.taskOnSight(usingId);
        this.description = description;
        this.name = name;
        this.shortname = shortname;
        this.submitdate = submitdate;
        this.updatedate = updatedate;
        this.closedate = closedate;
        this.abudget = abudget;
        this.budget = budget;
        this.deadline = deadline;
        this.number = number;
        this.submitterId = submitterId;
        this.handlerId = handlerId;
        this.handlerUserId = handlerUserId;
        this.handlerGroupId = handlerGroupId;
        this.parentId = parentId;
        this.categoryId = categoryId;
        this.workflowId = workflowId;
        this.statusId = statusId;
        this.resolutionId = resolutionId;
        this.priorityId = priorityId;
        this.udfValues = new ConcurrentHashMap(Null.removeNullElementsFromMap(udfMap));
    }

    public SecuredTaskTriggerBean(String id, SessionContext sc) throws GranException {
        String usingId;
        if (TaskRelatedManager.getInstance().isTaskExists(id)) {
            usingId = id;
            taskExists = true;
        } else throw new TaskNotFoundException(parentId);

        if (allowedByACL = sc.allowedByACL(usingId))
            this.onSight = true;
        else
            this.onSight = sc.taskOnSight(usingId);
        SecuredTaskBean task = new SecuredTaskBean(id, sc);
        init(task);
        this.udfValues = new ConcurrentHashMap<String, String>();
    }



    public SecuredTaskTriggerBean(SecuredTaskBean task, Map<String, String> udfMap) throws GranException {
        //super(task.getId(), task.getSecure());
        init(task);
        this.udfValues = new ConcurrentHashMap(Null.removeNullElementsFromMap(udfMap));
    }

    public static SecuredTaskTriggerBean build(TaskBuilder builder, TaskBuilder.Action action) throws GranException {
        SecuredTaskTriggerBean triggerBean;
        if (action == TaskBuilder.Action.CREATE) {
            triggerBean = new SecuredTaskTriggerBean(builder.getId(), builder.getDescription(),
                    builder.getName(), builder.getShortname(), builder.getUpdatedate(), builder.getUpdatedate(),
                    null, null, builder.getBudget(), builder.getDeadline(),
                    builder.getNumber(), builder.getSubmitterId(), builder.getHandlerId(),
                    builder.getHandlerUserId(), builder.getHandlerGroupId(), builder.getParentId(),
                    builder.getCategoryId(), null, builder.getStatusId(),
                    null, builder.getPriorityId(), builder.getUdfValues(),
                    builder.getSc(), builder.getAtts(), builder.isCopyOrMoveOpr());
            triggerBean.setPreviousNumber(builder.getPreviousNumber());
            triggerBean.setNeedSend(builder.isNeedSend());
            triggerBean.setAtts(builder.getAtts());
        } else {
            throw new UnsupportedOperationException("This operation is not implemented yet. Action :" + action);
        }
        return triggerBean;
    }

    public String getPreviousNumber() {
        return previousNumber;
    }

    public void setPreviousNumber(String previousNumber) {
        this.previousNumber = previousNumber;
    }

    private void init(SecuredTaskBean task) throws GranException {
        this.sc = task.getSecure();
        if (allowedByACL = sc.allowedByACL(task.getId()))
            this.onSight = true;
        else
            this.onSight = sc.taskOnSight(task.getId());
        this.id = task.getId();
        this.taskExists = true;
        this.parentId = task.getParentId();

        this.description = task.getDescription();
        this.name = task.getName();
        this.shortname = task.getShortname();
        this.submitdate = task.getSubmitdate();
        this.updatedate = task.getUpdatedate();
        this.closedate = task.getClosedate();
        this.abudget = task.getActualBudget();
        this.budget = task.getBudget();
        this.deadline = task.getDeadline();
        this.number = task.getNumber();
        this.submitterId = task.getSubmitterId();
        this.handlerId = task.getHandlerId();
        this.handlerUserId = task.getHandlerUserId();
        this.handlerGroupId = task.getHandlerGroupId();
        this.parentId = task.getParentId();
        this.categoryId = task.getCategoryId();
        this.workflowId = task.getWorkflowId();
        this.statusId = task.getStatusId();
        this.resolutionId = task.getResolutionId();
        this.priorityId = task.getPriorityId();
    }

    public Map<String, String> getUdfValues() {
        return udfValues;
    }

    /**
     * Обычная процедура создания задачи, как она у нас делается через TriggerManager. Используются адапдеры, вызов стандартный, так что права проверяются
     * Тонкость тут такая - данные, которые передаются через форму, например, мы берем из текущего таска, а данные, которые формируются при создании задачи - берем после create из свежесозданного таска
     *
     * @return Новый SecuredTriggerBean, в котором отражены изменения
     * @throws GranException
     */
    public SecuredTaskTriggerBean create() throws GranException {
        return create(true);
    }

    public SecuredTaskTriggerBean create(boolean sendMail) throws GranException {
        String id = AdapterManager.getInstance().getSecuredTaskAdapterManager().createTask(getSecure(), getParentId(), getCategoryId(), getName(), getSubmitdate(), getUpdatedate(), getStatusId(), getSubmitterId(), isCopyOrMoveOpr());
        this.setId(id);
        Calendar timeAudit = Calendar.getInstance();
        SecuredTaskBean tci = new SecuredTaskBean(id, getSecure());
        SecuredTaskTriggerBean sttb = new SecuredTaskTriggerBean(tci.getId(), getDescription(), getName(), getShortname(), tci.getSubmitdate(),
                tci.getUpdatedate(), tci.getClosedate(), tci.getActualBudget(), getBudget(), getDeadline(), tci.getNumber(),
                getSubmitterId(), getHandlerId(), getHandlerUserId(), getHandlerGroupId(), tci.getParentId(), getCategoryId(),
                tci.getWorkflowId(), tci.getStatusId(), tci.getResolutionId(), getPriorityId(), getUdfValues(), getSecure(), getAtts(), isCopyOrMoveOpr());
        if (getAtts() != null && getAtts().size() != 0) {
            sttb.setDescription(TriggerManager.getInstance().createAttachments(getSecure(), tci.getId(), tci.getNumber(), getDescription(), getAtts()));
        }
        if (isCopyOrMoveOpr()) {
            AuditUtil builder = new AuditUtil(new StringBuilder("This task was Copied from '"+getName()+" [#" + getPreviousNumber() + "]'<br/><br/>"), id, timeAudit, AuditUtil.Type.TASK);
            new AuditTrailTask(sc, id, builder).auditTask(sttb, true);
        }
        return sttb.update(sendMail);
    }

    /**
     * Обычная процедура изменения задачи, как она у нас делается через TriggerManager. Используются адапдеры, вызов стандартный, так что права проверяются
     *
     * @return Новый SecuredTriggerBean, в котором отражены изменения
     * @throws GranException
     */
    public SecuredTaskTriggerBean update() throws GranException {
        return update(true);
    }

    public SecuredTaskTriggerBean update(boolean sendMail) throws GranException {
        Map<String, SecuredUDFValueBean> udfs = getUDFValues();
        List<String> udfList = getUDFsId();
        for (String s : udfs.keySet()) {
            SecuredUDFValueBean udf = getUDFValues().get(s);
            if (udf.isTaskUdfEditable(getId()) && udfValues.containsKey(udf.getCaption())) {
                String udfCaption = udfValues.get(udf.getCaption());
                AdapterManager.getInstance().getSecuredUDFAdapterManager().setTaskUDFValueSimple(getSecure(), getId(), udf.getCaption(), udfCaption, udfList);
            }
        }
        // log.debug("Update task desctiptop : " + getDescription());
        AdapterManager.getInstance().getSecuredTaskAdapterManager().updateTask(getSecure(), getId(), getShortname(), getName(), getDescription(), getBudget(), getDeadline(), getPriorityId(), getParentId(), getHandlerUserId(), getHandlerGroupId(), sendMail, getSubmitdate(), getUpdatedate());
        return new SecuredTaskTriggerBean(new SecuredTaskBean(getId(), getSecure()), getUdfValues());
    }

    /**
     * Set priority
     *
     * @param priority priority's name
     * @throws GranException
     */
    public void setPriority(String priority) throws GranException {
        this.priorityId = CSVImport.findPriorityIdByName(priority, getCategory().getName());
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
     * Set handler user
     *
     * @param handlerUser user's login
     * @throws GranException
     */
    public void setHandlerUser(String handlerUser) throws GranException {
        this.handlerUserId = CSVImport.findUserIdByLogin(handlerUser);
    }

    /**
     * Set handler group
     *
     * @param handlerGroup group's login
     * @throws GranException
     */
    public void setHandlerGroup(String handlerGroup) throws GranException {
        this.handlerGroupId = CSVImport.findUserIdByLogin(handlerGroup);
    }

    /**
     * Set value of custom field
     *
     * @param caption custom field's caption
     * @param value   new value
     */
    public void setUdfValue(String caption, String value) {
        // udfValues cannot hold nulls, so remove items if null required
        if (value != null)
            udfValues.put(caption, value);
        else
            udfValues.remove(caption);
    }

    /**
     * Get custom field value
     *
     * @param caption custom field's caption
     * @return value
     */
    public String getUdfValue(String caption) {
        return udfValues.get(caption);

    }

    /**
     * Set parent
     *
     * @param number parent's number
     * @throws GranException
     */
    public void setParent(String number) throws GranException {
        if (this.parentNumber == null || !this.parentNumber.equals(number)) {
            this.parentNumber = number;
            this.parentId = CSVImport.findTaskIdByNumber(number);
        }
    }

    /**
     * Set category
     *
     * @param category category's name
     * @throws GranException
     */
    public void setCategory(String category) throws GranException {
        this.categoryId = CSVImport.findCategoryIdByName(sc, category, parentId);
    }

    public String getName() {
        return name;
    }

    /**
     * Set name
     *
     * @param name name
     */
    public void setName(String name) {
        this.name = name;
    }

    public String getShortname() {
        return shortname;
    }

    /**
     * Set short name
     *
     * @param name short name
     */
    public void setShortname(String name) {
        this.shortname = name;
    }

    public Long getAbudget() {
        return abudget;
    }

    public String getNumber() {
        return number;
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
        if (this.submitterId != null) {
            this.submitterId = submitterId;
        }
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

    public String getParentId() {
        return parentId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public String getStatusId() {
        return statusId;
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
        if (!this.priorityId.equals(priorityId)) {
            this.priorityId = priorityId;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int hashCode() {
        if (getId() != null) return super.hashCode();
        else return "null".hashCode();
    }

    public String getDescription() throws GranException {
        return description;
    }

    /**
     * Set description
     *
     * @param description description
     * @throws GranException
     */
    public void setDescription(String description) throws GranException {
        this.description = description;
    }

    public Calendar getDeadline() throws GranException {
        if (deadline != null)
            return (Calendar)deadline.clone();
        else
            return null;
    }

    /**
     * Set deadline
     *
     * @param deadline deadline
     * @throws GranException
     */
    public void setDeadline(Calendar deadline) throws GranException
    {
        if (deadline!=null)
            this.deadline = (Calendar)deadline.clone();
        else
            this.deadline = null;
    }

    public Long getBudget() throws GranException {
        return budget;
    }

    /**
     * Set budget
     *
     * @param budget budget in hours
     * @throws GranException
     */
    public void setBudget(Long budget) throws GranException {
        this.budget = budget;
    }

    public Long getActualBudget() throws GranException {
        return abudget;
    }

    public TaskRelatedInfo getTask() {
        if (taskExists)
            return TaskRelatedManager.getInstance().find(getId());
        return null;
    }

    public Calendar getSubmitdate() throws GranException {
        if (submitdate != null)
            return (Calendar)submitdate.clone();
        else
            return null;
    }

    public Calendar getUpdatedate() throws GranException {
        if (updatedate != null)
            return (Calendar)updatedate.clone();
        else
            return null;
    }

    public Calendar getClosedate() throws GranException {
        if (closedate != null)
            return (Calendar)closedate.clone();
        else
            return null;
    }

    public List<AttachmentArray> getAtts() {
        return atts;
    }

    public void setAtts(List<AttachmentArray> atts) {
        this.atts = new CopyOnWriteArrayList(Null.removeNullElementsFromList(atts));
    }

    public boolean isCopyOrMoveOpr() {
        return copyOrMoveOpr;
    }

    public void setCopyOrMoveOpr(boolean copyOrMoveOpr) {
        this.copyOrMoveOpr = copyOrMoveOpr;
    }

    public boolean isNeedSend() {
        return needSend;
    }

    public void setNeedSend(boolean needSend) {
        this.needSend = needSend;
    }
}
