package com.trackstudio.builder;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.trackstudio.app.session.SessionContext;
import com.trackstudio.kernel.manager.AttachmentArray;
import com.trackstudio.tools.Null;

import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class TaskBuilder {
    public enum Action {
        CREATE, UPDATE
    }

    private volatile SessionContext sc;
    private volatile String name;
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
    protected volatile Calendar submitdate = null;
    protected volatile Calendar updatedate = null;
    protected volatile Calendar closedate = null;
    protected volatile String description = null;
    protected volatile Long budget = null;
    protected volatile Long actualBudget = null;
    protected volatile Calendar deadline = null;

    protected volatile Integer totalChildrenCount = null;
    protected volatile Integer allowedChildrenCount = null;
    private volatile Boolean canViewChildren = null;
    private volatile Integer childrenCount = null;
    public ConcurrentMap<String, String> udfValues = new ConcurrentHashMap<String, String>();
    private volatile String previousNumber;

    public String getPreviousNumber() {
        return previousNumber;
    }

    public void setPreviousNumber(String previousNumber) {
        this.previousNumber = previousNumber;
    }

    public SessionContext getSc() {
        return sc;
    }

    public void setSc(SessionContext sc) {
        this.sc = sc;
    }

    public ConcurrentMap<String, String> getUdfValues() {
        return udfValues;
    }

    public void setUdfValues(Map<String, String> udfValues) {
        this.udfValues = new ConcurrentHashMap(Null.removeNullElementsFromMap(udfValues));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getShortname() {
        return shortname;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

    public Long getAbudget() {
        return abudget;
    }

    public void setAbudget(Long abudget) {
        this.abudget = abudget;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getSubmitterId() {
        return submitterId;
    }

    public void setSubmitterId(String submitterId) {
        this.submitterId = submitterId;
    }

    public String getHandlerId() {
        return handlerId;
    }

    public void setHandlerId(String handlerId) {
        this.handlerId = handlerId;
    }

    public String getHandlerUserId() {
        return handlerUserId;
    }

    public void setHandlerUserId(String handlerUserId) {
        this.handlerUserId = handlerUserId;
    }

    public String getHandlerGroupId() {
        return handlerGroupId;
    }

    public void setHandlerGroupId(String handlerGroupId) {
        this.handlerGroupId = handlerGroupId;
    }

    public String getParentNumber() {
        return parentNumber;
    }

    public void setParentNumber(String parentNumber) {
        this.parentNumber = parentNumber;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    public String getStatusId() {
        return statusId;
    }

    public void setStatusId(String statusId) {
        this.statusId = statusId;
    }

    public String getResolutionId() {
        return resolutionId;
    }

    public void setResolutionId(String resolutionId) {
        this.resolutionId = resolutionId;
    }

    public String getPriorityId() {
        return priorityId;
    }

    public void setPriorityId(String priorityId) {
        this.priorityId = priorityId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isTaskExists() {
        return taskExists;
    }

    public void setTaskExists(boolean taskExists) {
        this.taskExists = taskExists;
    }

    public List<AttachmentArray> getAtts() {
        return atts;
    }

    public void setAtts(List<AttachmentArray> atts) {
        this.atts = new CopyOnWriteArrayList<AttachmentArray>(Null.removeNullElementsFromList(atts));
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

    public Calendar getSubmitdate() {
        return submitdate;
    }

    public void setSubmitdate(Calendar submitdate) {
        this.submitdate = submitdate;
    }

    public Calendar getUpdatedate() {
        return updatedate;
    }

    public void setUpdatedate(Calendar updatedate) {
        this.updatedate = updatedate;
    }

    public Calendar getClosedate() {
        return closedate;
    }

    public void setClosedate(Calendar closedate) {
        this.closedate = closedate;
    }

    public Long getBudget() {
        return budget;
    }

    public void setBudget(Long budget) {
        this.budget = budget;
    }

    public Long getActualBudget() {
        return actualBudget;
    }

    public void setActualBudget(Long actualBudget) {
        this.actualBudget = actualBudget;
    }

    public Calendar getDeadline() {
        return deadline;
    }

    public void setDeadline(Calendar deadline) {
        this.deadline = deadline;
    }

    public Integer getTotalChildrenCount() {
        return totalChildrenCount;
    }

    public void setTotalChildrenCount(Integer totalChildrenCount) {
        this.totalChildrenCount = totalChildrenCount;
    }

    public Integer getAllowedChildrenCount() {
        return allowedChildrenCount;
    }

    public void setAllowedChildrenCount(Integer allowedChildrenCount) {
        this.allowedChildrenCount = allowedChildrenCount;
    }

    public Boolean getCanViewChildren() {
        return canViewChildren;
    }

    public void setCanViewChildren(Boolean canViewChildren) {
        this.canViewChildren = canViewChildren;
    }

    public Integer getChildrenCount() {
        return childrenCount;
    }

    public void setChildrenCount(Integer childrenCount) {
        this.childrenCount = childrenCount;
    }
}
