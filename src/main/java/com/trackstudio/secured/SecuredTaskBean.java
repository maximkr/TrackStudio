package com.trackstudio.secured;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.trackstudio.app.UdfValue;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.csv.CSVImport;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.cache.AttachmentCacheItem;
import com.trackstudio.kernel.cache.CategoryCacheItem;
import com.trackstudio.kernel.cache.CategoryCacheManager;
import com.trackstudio.kernel.cache.MessageCacheItem;
import com.trackstudio.kernel.cache.TaskRelatedInfo;
import com.trackstudio.kernel.cache.TaskRelatedManager;
import com.trackstudio.kernel.cache.UDFCacheItem;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.kernel.manager.UdfManager;
import com.trackstudio.model.Prstatus;
import com.trackstudio.model.Udf;
import com.trackstudio.soap.bean.TaskBean;
import com.trackstudio.startup.Config;
import com.trackstudio.tools.ICommand;
import com.trackstudio.tools.Null;
import com.trackstudio.tools.PropertyContainer;
import com.trackstudio.tools.formatter.HourFormatter;
import com.trackstudio.tools.textfilter.HTMLEncoder;
import com.trackstudio.tools.tree.NodeMask;

import static com.trackstudio.tools.Null.intOrDef;

/**
 * Bean which represents task
 */
public class SecuredTaskBean extends AbstractBeanWithUdf implements SecuredTaskBeanInterface {
    private static final LockManager lockManager = LockManager.getInstance();

    protected final TaskRelatedInfo task;
    private volatile CategoryCacheItem categoryCacheItem;

    protected volatile boolean allowedByACL = false;
    protected volatile boolean onSight = false;

    protected volatile Calendar submitdate = null;
    protected volatile Calendar updatedate = null;
    protected volatile Calendar closedate = null;
    protected volatile Calendar deadline = null;

    private volatile String taskId;
    protected volatile Long budget = null;
    protected volatile Long actualBudget = null;
    protected volatile Integer totalChildrenCount = null;
    protected volatile Integer allowedChildrenCount = null;
    private volatile Boolean canViewChildren = null;
    private volatile Integer childrenCount = null;
    protected volatile String description = null;

    private volatile SecuredCategoryBean category = null;
    private volatile SecuredStatusBean status = null;
    private volatile SecuredResolutionBean resolution = null;
    private volatile SecuredPriorityBean priority = null;
    private volatile SecuredUserBean submitter = null;
    private volatile SecuredUserBean handlerUser = null;
    private volatile SecuredPrstatusBean handlerGroup = null;
    private volatile SecuredTaskBean parent = null;
    private volatile SecuredWorkflowBean workflow = null;

    protected volatile ArrayList<SecuredUDFValueBean> filteredUdfValues = null;
    private volatile ArrayList<SecuredUDFValueBean> udfValues = null;
    private volatile ArrayList<SecuredUDFBean> filterUDFs = null;
    private volatile ArrayList<SecuredUDFValueBean> workflowUdfValues = null;
    private volatile TreeSet<String> allowedPrstatuses;
    private volatile ArrayList<SecuredMessageBean> messages;
    private volatile boolean newEntity = false;
    private volatile String maskName = null;

    /**
     * Category for new task,
     * when user creates the new task
     * the UDFs don't know about category of new task
     */
    private volatile String categoryForNewTask;

    public SecuredTaskBean() {
        this.task = null;
    }

    public SecuredTaskBean(TaskRelatedInfo task, SessionContext sec) throws GranException {
        this.task = task;
        this.sc = sec;
        this.allowedByACL = sc.allowedByACL(task.getId());
        this.onSight = this.allowedByACL ? this.allowedByACL : sec.taskOnSight(task.getId());
    }

    //dnikitin: ���� ����������� ����������� � com.trackstudio.app.filter.list.TaskFilter
    //��� �� ��� ����� �����, ��� ������ onSight � ���� �� hasAccess. ���������� ��� ������ ��� ��� �������� ���-�� ������ ������ ��������.
    protected SecuredTaskBean(TaskRelatedInfo task, SessionContext sec, boolean hasAccess) {
        this.task = task;
        this.sc = sec;
        onSight = true;
        allowedByACL = hasAccess;
    }

    public SecuredTaskBean(String taskId, SessionContext sec) throws GranException {
        this(TaskRelatedManager.getInstance().find(taskId), sec);
    }

    public boolean isNewEntity() {
        return newEntity;
    }

    public void setNewEntity(boolean newEntity) {
        this.newEntity = newEntity;
    }

    public static List<SecuredMessageBean> getMessageCheckedAudit(List<SecuredMessageBean> messages, boolean audit) throws GranException {
        List<SecuredMessageBean> checkMessage = new ArrayList<SecuredMessageBean>();
        for (SecuredMessageBean message : messages) {
            if (CSVImport.LOG_MESSAGE.equals(message.getMstatus().getName())) {
                if (audit) {
                    checkMessage.add(message);
                }
            } else {
                checkMessage.add(message);
            }
        }
        return checkMessage;
    }

    public SecuredCategoryBean getCategory() throws GranException {
        if (category == null)
            category = AdapterManager.getInstance().getSecuredFindAdapterManager().findCategoryById(sc, getCategoryId());
        return category;
    }


    public CategoryCacheItem getCategorySubtaskView() throws GranException {
        if (categoryCacheItem == null)
            categoryCacheItem = CategoryCacheManager.getInstance().find(getCategoryId());
        return categoryCacheItem;
    }

    public SecuredStatusBean getStatus() throws GranException {
        if (status == null)
            status = AdapterManager.getInstance().getSecuredFindAdapterManager().findStatusById(sc, getStatusId());
        return status;
    }

    public SecuredResolutionBean getResolution() throws GranException {
        if (resolution == null && getResolutionId() != null)
            resolution = AdapterManager.getInstance().getSecuredFindAdapterManager().findResolutionById(sc, getResolutionId());
        return resolution;
    }

    public SecuredPriorityBean getPriority() throws GranException {
        if (priority == null && getPriorityId() != null)
            priority = AdapterManager.getInstance().getSecuredFindAdapterManager().findPriorityById(sc, getPriorityId());
        return priority;
    }

    public SecuredUserBean getSubmitter() throws GranException {
        if (submitter == null && getSubmitterId() != null)
            submitter = new SecuredUserBean(getSubmitterId(), sc);
        return submitter;
    }

    public SecuredUserBean getHandlerUser() throws GranException {
        if (handlerUser == null && getHandlerUserId() != null)
            handlerUser = new SecuredUserBean(getHandlerUserId(), sc);
        return handlerUser;
    }

    public SecuredPrstatusBean getHandlerGroup() throws GranException {
        if (handlerGroup == null && getHandlerGroupId() != null)
            handlerGroup = new SecuredPrstatusBean(KernelManager.getFind().findPrstatus(getHandlerGroupId()), sc);
        return handlerGroup;
    }

    public SecuredTaskBean getParent() throws GranException {
        if (parent == null && getParentId() != null)
            parent = new SecuredTaskBean(TaskRelatedManager.getInstance().find(getParentId()), sc);
        return parent;
    }

    public SecuredWorkflowBean getWorkflow() throws GranException {
        if (workflow == null)
            workflow = AdapterManager.getInstance().getSecuredFindAdapterManager().findWorkflowById(sc, getWorkflowId());
        return workflow;
    }

    public String getId() {
        if (taskId == null) {
            return getTask().getId();
        } else {
            return taskId;
        }
    }

    public String getMaskName() {
        return this.maskName;
    }

    @Override
    public void setId(String id) {
        this.taskId = id;
    }

    public String getName() {
        if (isOnSight())
            return getTask().getName();
        else
            return '#' + getTask().getNumber();
    }

    public String getShortname() {
        return getTask().getShortname();
    }

    /**
     * @return Encoded shortname
     * @deprecated
     */
    public String getEncodeShortname() {
        return Null.stripNullHtml(HTMLEncoder.encode(getShortname()));
    }


    /**
     * Encoding name for title
     * @return
     */
    public String getEncodeName() {
        return Null.stripNullHtml(HTMLEncoder.encode(getName()));
    }

    /**
     * @return Encoded short name
     * @deprecated
     */
    public String getEncodeShortnameInput() {
        return Null.stripNullText(HTMLEncoder.encode(getShortname()));
    }

    /**
     * @return Encoded description
     * @throws GranException ��� ������������
     * @deprecated
     */
    public String getEncodeDescription() throws GranException {
        if (getDescription() == null)
            return "";
        return getDescription().equals("<pre/>") ? "<pre></pre>" : Null.stripNullText(HTMLEncoder.encode(getDescription()));
    }

    public Calendar getSubmitdate() throws GranException {
        if (submitdate == null && getTask() != null && getTask().getSubmitdate() != null)
            submitdate = sc.canAction(Action.viewTaskSubmitDate, getId(), getAllowedPrstatuses()) ? getTask().getSubmitdate() : null;
        return submitdate;
    }

    public String getSubmitdateAsString() throws GranException {
        return getSecure().getUser().getDateFormatter().parse(getSubmitdate());
    }

    public Calendar getUpdatedate() throws GranException {
        if (updatedate == null)
            updatedate = sc.canAction(Action.viewTaskLastUpdated, getId(), getAllowedPrstatuses()) ? getTask().getLastUpdateDate() : null;
        return updatedate;
    }

    public long getUpdatedateMsec() throws GranException {
        return getTask().getLastUpdateDateMsec();
    }

    public String getUpdatedateAsString() throws GranException {
        return getSecure().getUser().getDateFormatter().parse(getUpdatedate());
    }

    public Calendar getClosedate() throws GranException {
        if (getTask() == null) return null;
        if (closedate == null && getTask().getClosedate() != null)
            closedate = sc.canAction(Action.viewTaskCloseDate, getId(), getAllowedPrstatuses()) ? getTask().getClosedate() : null;
        return closedate;
    }

    public String getClosedateAsString() throws GranException {
        return getSecure().getUser().getDateFormatter().parse(getClosedate());
    }

    public String getDescription() throws GranException {
        if (description == null && getTask() != null) {
            description = sc.canAction(Action.viewTaskDescription, getId(), getAllowedPrstatuses()) ? getTask().getDescription() : null;
        }
        return description;
    }

    public String getTextDescription() throws GranException {
        return getDescription() != null ? getTask().getTextDescription() : null;
    }

    public Long getBudget() throws GranException {
        if (budget == null && getTask() != null && getTask().getBudget() != null)
            budget = sc.canAction(Action.viewTaskBudget, getId(), getAllowedPrstatuses()) ? getTask().getBudget() : null;
        return budget;
    }

    public Long getActualBudget() throws GranException {
        if (actualBudget == null && getTask() != null && getTask().getActualBudget() != null)
            actualBudget = sc.canAction(Action.viewTaskActualBudget, getId(), getAllowedPrstatuses()) ? getTask().getActualBudget() : null;
        return actualBudget;
    }

    public Calendar getDeadline() throws GranException {
        if (deadline == null && getTask() != null && getTask().getDeadline() != null)
            deadline = sc.canAction(Action.viewTaskDeadline, getId(), getAllowedPrstatuses()) ? getTask().getDeadline() : null;
        return deadline;
    }

    public String getTimezone() throws GranException {
        return getSecure().getUser().getTimezone();
    }

    public boolean hasFinishedStatus() throws GranException {
        return getStatus().isFinish();
    }

    public String getDeadlineAsString() throws GranException {
        return getSecure().getUser().getDateFormatter().parse(getDeadline());
    }

    public String getNumber() {
        return getTask().getNumber();
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

    public Integer getMessageCount() throws GranException {
        return isAllowedByACL() && getTask() != null ? getTask().getMessageCount() : null;
    }

    public Integer getChildrenCount() throws GranException {
        if (!isOnSight())
            return null;

        if (childrenCount == null) {
            childrenCount = getAllowedChildrenMap().size();
        }
        return childrenCount;
    }

    public boolean canViewChildren() throws GranException {
        if (!isOnSight())
            return false;

        if (canViewChildren == null) {
            if (getTask() != null) for (TaskRelatedInfo t : getTask().getChildren()) {
                if (TaskRelatedManager.getInstance().onSight(sc.getUserId(), t.getId(), sc.getPrstatusId(), true)) {
                    canViewChildren = Boolean.TRUE;
                    return true;
                }
            }
            canViewChildren = Boolean.FALSE;
            return false;
        }
        return canViewChildren;

    }

    public ArrayList<SecuredTaskBean> getChildren() throws GranException {
        ArrayList<SecuredTaskBean> l = new ArrayList<SecuredTaskBean>();
        if (isAllowedByACL() && getTask() != null) {
            List<TaskRelatedInfo> children = getTask().getChildren();
            if (children != null) {
                for (TaskRelatedInfo aCollection : children) {
                    SecuredTaskBean tb = new SecuredTaskBean((aCollection), getSecure());
                    l.add(tb);
                }
            }
        }
        return l;
    }

    public ArrayList<SecuredMessageBean> getMessages() throws GranException {
        if (messages == null) {
            ArrayList<SecuredMessageBean> localMessages = new ArrayList<SecuredMessageBean>();
            if (isAllowedByACL() && getTask() != null) {
                List<MessageCacheItem> children = KernelManager.getStep().getMessageList(getTask().getId(), sc.getUserId());
                for (MessageCacheItem aChildren : children) {
                    SecuredMessageBean tb = new SecuredMessageBean(aChildren, getSecure());
                    localMessages.add(tb);
                }
            }
            this.messages = localMessages;
        }
        return (ArrayList<SecuredMessageBean>) messages.clone();
    }

    public ArrayList<String> getMessagesIds() throws GranException {
        List<SecuredMessageBean> message = getMessages();
        ArrayList<String> ret = new ArrayList<String>();
        for (SecuredMessageBean m : message) {
            ret.add(m.getId());
        }
        return ret;
    }

    public Integer getTotalChildrenCount() throws GranException {
        if (!isOnSight())
            return null;

        if (totalChildrenCount == null) {
            totalChildrenCount = getAllowedChildrenWithSubtasksMap().size();
        }
        return totalChildrenCount;
    }

    public Integer getAllowedChildrenCount() throws GranException {
        if (!isOnSight())
            return null;

        if (allowedChildrenCount == null) {
            allowedChildrenCount = getAllowedChildrenMap().size();
        }
        return allowedChildrenCount;
    }

    public String getTaskNumber() {
        if (getNumber() == null || getNumber().length() == 0) {
            return "";
        } else
            return new DecimalFormat("#").format((new Integer(getNumber())).longValue());
    }

    private void collectUdfValuesForNewTask(String workflowId, ICommand<SecuredUDFValueBean> command) throws GranException {
        if (getTask() != null) {
            ArrayList<UDFCacheItem> old = getTask().getUDFCacheItemsForNewTask(workflowId);
            ArrayList<UdfValue> map = new ArrayList<UdfValue>();
            for (UDFCacheItem udf : old) {
                map.add(new UdfValue(udf));
            }

            Set<String> prstatuses = TaskRelatedManager.getInstance().getAllowedPrstatuses(sc.getUserId(), getTask().getId());
            for (UdfValue u : map) {
                if (KernelManager.getUdf().isTaskUdfViewable(prstatuses, getTask().getId(), sc.getUserId(), u.getUdfId(),  sc.getUserId()))
                    command.doAction(new SecuredUDFValueBean(u, this));
            }
        }
    }

    public ArrayList<SecuredUDFValueBean> getUDFValuesForNewTask(String workflowId) throws GranException {
        final ArrayList<SecuredUDFValueBean> udfValuesList = new ArrayList<SecuredUDFValueBean>();
        collectUdfValuesForNewTask(workflowId, new ICommand<SecuredUDFValueBean>() {
            public void doAction(SecuredUDFValueBean udf) {
                udfValuesList.add(udf);
            }
        });
        Collections.sort(udfValuesList);
        return udfValuesList;
    }

    public Map<String, SecuredUDFValueBean> getMapUDFValuesForNewTask(String workflowId) throws GranException {
        final Map<String, SecuredUDFValueBean> udfValuesMap = new HashMap<String, SecuredUDFValueBean>();
        collectUdfValuesForNewTask(workflowId, new ICommand<SecuredUDFValueBean>() {
            public void doAction(SecuredUDFValueBean udf) {
                udfValuesMap.put(udf.getUdfId(), udf);
            }
        });
        return udfValuesMap;
    }

    public ArrayList<SecuredUDFValueBean> getUDFValuesForNewTask(String workflowId, boolean edit) throws GranException {
        ArrayList<SecuredUDFValueBean> udfValuesList = new ArrayList<SecuredUDFValueBean>();
        if (getTask() != null) {
            ArrayList<UdfValue> map = new ArrayList<UdfValue>();
            if (workflowId != null) {
                ArrayList<UDFCacheItem> old = getTask().getUDFCacheItemsForNewTask(workflowId);
                for (UDFCacheItem udf : old) {
                    map.add(new UdfValue(udf));
                }
            } else {
                map.addAll(getTask().getWorkflowUDFValues());
            }
            Set<String> prstatuses = TaskRelatedManager.getInstance().getAllowedPrstatuses(sc.getUserId(), getTask().getId());
            for (UdfValue u : map) {
                String statusId = null;
                if (KernelManager.getFind().findUdf(u.getUdfId()).getUdfsource().getWorkflow() != null)
                    statusId = KernelManager.getWorkflow().getStartStateId(KernelManager.getFind().findUdf(u.getUdfId()).getUdfsource().getWorkflow().getId());
                if (edit) {
                    if (KernelManager.getUdf().isTaskUdfEditable(getTask().getId(), sc.getUserId(), u.getUdfId(), statusId)) {
                        udfValuesList.add(new SecuredUDFValueBean(u, this));
                    }
                } else {
                    if (KernelManager.getUdf().isTaskUdfViewableFast(prstatuses, getTask().getId(), sc.getUserId(), u.getUdfId())) {
                        udfValuesList.add(new SecuredUDFValueBean(u, this));
                    }
                }
            }
        }
        Collections.sort(udfValuesList);
        return udfValuesList;
    }

    public HashMap<String, SecuredUDFValueBean> getUDFValues() throws GranException {
        HashMap<String, SecuredUDFValueBean> ret = new HashMap<String, SecuredUDFValueBean>();
        for (SecuredUDFValueBean suvb : getUDFValuesList()) {
            ret.put(suvb.getUdfId(), suvb);
        }
        return ret;
    }

    /**
     * Method need for jsp
     *
     * @return map udfId, value
     * @throws GranException for need
     */
    public HashMap<String, SecuredUDFValueBean> getAliasUdfValues() throws GranException {
        return getUDFValues();
    }

    public ArrayList<SecuredUDFValueBean> getUDFValuesList() throws GranException {
        boolean lock = lockManager.acquireConnection(SecuredTaskBean.class.getName());
        try {
            if (udfValues == null) {
                ArrayList<SecuredUDFValueBean> localUdfValues = new ArrayList<SecuredUDFValueBean>();
                if (getTask() != null) {
                    List<UdfValue> udfValueList = TaskRelatedManager.getInstance().getUDFValues(getId());
                    Set<String> prstatuses = TaskRelatedManager.getInstance().getAllowedPrstatuses(sc.getUserId(), getTask().getId());
                    for (UdfValue u : udfValueList) {
                        if (u != null) {
                            if (KernelManager.getUdf().isTaskUdfViewable(prstatuses, getTask().getId(), sc.getUserId(), u.getUdfId(), null)) {
                                localUdfValues.add(new SecuredUDFValueBean(u, this));
                            }
                        }
                    }
                }
                this.udfValues = localUdfValues;
            }
        } finally {
            if (lock) lockManager.releaseConnection(SecuredTaskBean.class.getName());
        }
        return (ArrayList<SecuredUDFValueBean>)udfValues.clone();
    }

    public ArrayList<SecuredUDFValueBean> getUdfValuesList() throws GranException {
        return getUDFValuesList();
    }

    public ArrayList<SecuredUDFBean> getFilterUDFs() throws GranException {
        boolean lock = lockManager.acquireConnection(SecuredTaskBean.class.getName());
        try {
            if (filterUDFs == null) {
                ArrayList<SecuredUDFBean> localFilterUDFs = new ArrayList<SecuredUDFBean>();
                if (getTask() != null) {
                    ArrayList<UDFCacheItem> udfCacheItemList = getTask().getFilterUDFs();
                    if (udfCacheItemList != null)
                        for (UDFCacheItem udfCacheItem : udfCacheItemList) {
                            SecuredUDFBean udfBean = null;
                            //todo #46481
                            if (udfCacheItem.getTaskId() != null) {
                                udfBean = new SecuredTaskUDFBean(udfCacheItem, sc);
                            } else if (KernelManager.getFind().findWorkflow(udfCacheItem.getWorkflowId()) != null)
                                udfBean = new SecuredWorkflowUDFBean(udfCacheItem, sc);
                            if (udfBean != null && udfBean.getType() != null)
                                localFilterUDFs.add(udfBean);
                        }
                }
                this.filterUDFs = localFilterUDFs;
            }
            return (ArrayList<SecuredUDFBean>)filterUDFs.clone();
        } finally {
            if (lock) lockManager.releaseConnection(SecuredTaskBean.class.getName());
        }
    }

    //������������ � SecuredTaskBean-��, ���������� �� TaskFilter. ���� - ���������� isTaskUdfViewableFast ������ ���� ��� ��� ������ ���������� ������.
    public ArrayList<SecuredUDFValueBean> getFilteredUDFValues() throws GranException {
        if (filteredUdfValues == null)
            filteredUdfValues = getUDFValuesList();
        return filteredUdfValues;
    }

    //���������� � TaskFilter
    public ArrayList<SecuredUDFValueBean> getWorkflowUDFValues() throws GranException {
        if (workflowUdfValues == null) {
            ArrayList<SecuredUDFValueBean> localWorkflowUdfValues = new ArrayList<SecuredUDFValueBean>();
            if (getTask() != null) {
                List<SecuredMstatusBean> mstatusList = AdapterManager.getInstance().getSecuredStepAdapterManager().getAvailableMstatusList(sc, getTask().getId());
                List old = getTask().getWorkflowUDFValues();
                for (Object anOld : old) {
                    UdfValue u = (UdfValue) anOld;
                    for (SecuredMstatusBean aMstatusList : mstatusList) {
                        List<String> viewvableUDF = KernelManager.getUdf().getViewableUDFId(aMstatusList.getId());
                        if (viewvableUDF.contains(u.getUdfId())) {
                            localWorkflowUdfValues.add(new SecuredUDFValueBean(u, this));
                            break;
                        }
                    }
                }
            }
            this.workflowUdfValues = localWorkflowUdfValues;
        }
        return (ArrayList<SecuredUDFValueBean>)workflowUdfValues.clone();
    }

    public ArrayList<SecuredUDFBean> getUDFs() throws GranException {
        return getUDFs(getWorkflowId());
    }

    public List<String> getUDFsId() throws GranException {
        List<String> udfId = new ArrayList<String>();
        for (SecuredUDFBean udf : getUDFs()) {
            udfId.add(udf.getId());
        }
        return udfId;
    }

    public ArrayList<SecuredUDFBean> getUDFs(String workflowId) throws GranException {
        ArrayList<SecuredUDFBean> c = new ArrayList<SecuredUDFBean>();
        if (getTask() != null) {
            ArrayList<UDFCacheItem> udfCacheItemList = workflowId == null ? getTask().getUDFCacheItems() : getTask().getUDFCacheItemsForNewTask(workflowId);
            if (udfCacheItemList != null) {
                Set<String> prstatuses = TaskRelatedManager.getInstance().getAllowedPrstatuses(sc.getUserId(), getTask().getId());
                for (UDFCacheItem udfCacheItem : udfCacheItemList) {
                    if (KernelManager.getUdf().isTaskUdfViewableFast(prstatuses, getTask().getId(), sc.getUserId(), udfCacheItem.getId()))
                        if (udfCacheItem.getTaskId() != null)
                            c.add(new SecuredTaskUDFBean(udfCacheItem, sc));
                        else
                            c.add(new SecuredWorkflowUDFBean(udfCacheItem, sc));
                }
            }
        }
        return c;
    }

    public ArrayList<SecuredUDFBean> getUDFs(String workflowId, boolean edit) throws GranException {
        ArrayList<SecuredUDFBean> c = new ArrayList<SecuredUDFBean>();
        if (getTask() != null) {
            ArrayList<UDFCacheItem> udfCacheItemList = workflowId == null ? getTask().getUDFCacheItems() : getTask().getUDFCacheItemsForNewTask(workflowId);
            if (udfCacheItemList != null) {
                Set<String> prstatuses = TaskRelatedManager.getInstance().getAllowedPrstatuses(sc.getUserId(), getTask().getId());
                for (UDFCacheItem udfCacheItem : udfCacheItemList) {
                    if (edit) {
                        if (KernelManager.getUdf().isTaskUdfEditable(getTask().getId(), sc.getUserId(), udfCacheItem.getId()))
                            if (udfCacheItem.getTaskId() != null)
                                c.add(new SecuredTaskUDFBean(udfCacheItem, sc));
                            else
                                c.add(new SecuredWorkflowUDFBean(udfCacheItem, sc));
                    } else {
                        if (KernelManager.getUdf().isTaskUdfViewableFast(prstatuses, getTask().getId(), sc.getUserId(), udfCacheItem.getId()))
                            if (udfCacheItem.getTaskId() != null)
                                c.add(new SecuredTaskUDFBean(udfCacheItem, sc));
                            else
                                c.add(new SecuredWorkflowUDFBean(udfCacheItem, sc));
                    }
                }
            }
        }
        return c;
    }

    public boolean canDelete() throws GranException {
        return canView() && AdapterManager.getInstance().getSecuredCategoryAdapterManager().isCategoryDeletable(sc, getId(), getCategoryId());
    }

    public boolean getCanDelete() throws GranException {
        return canDelete();
    }

    public ArrayList<SecuredUDFValueBean> getFilterUDFValues() throws GranException {
        boolean lock = lockManager.acquireConnection(SecuredTaskBean.class.getName());
        try {
            ArrayList<SecuredUDFValueBean> r = new ArrayList<SecuredUDFValueBean>();
            HashMap<String, UdfValue> map = new HashMap<String, UdfValue>();
            if (isOnSight() && getTask() != null) {
                ArrayList<UdfValue> h = getTask().getFilterUDFValues();
                ArrayList<UDFCacheItem> filterUDFs = getTask().getFilterUDFs();
                UdfManager udfManager = KernelManager.getUdf();
                for (UdfValue k : h) {
                    map.put(k.getUdfId(), k);
                }

                Set<String> prstatuses = TaskRelatedManager.getInstance().getAllowedPrstatuses(sc.getUserId(), getTask().getId());
                for (UDFCacheItem udfci : filterUDFs) {
                    if (udfci == null)       // Can occure when the task-owner has just been deleted. (#43284)
                        continue;
                    String workflowId = udfci.getWorkflowId();
                    if (workflowId != null) {
                        if (!sc.taskOnSight(KernelManager.getFind().findWorkflow(workflowId).getTask().getId()))
                            continue;
                    }

                    if (udfManager.isTaskUdfViewableFast(prstatuses, getTask().getId(), sc.getUserId(), udfci.getId())) {
                        UdfValue o = map.get(udfci.getId());
                        if (o != null)
                            r.add(new SecuredUDFValueBean(o, this));
                    }
                }
            }
            return r;
        } finally {
            if (lock) lockManager.releaseConnection(SecuredTaskBean.class.getName());
        }
    }

    public String getTaskNameCutted() {
        return getTask().getTaskNameCutted();
    }

    public String getProjectAlias() throws GranException {
        String id = getTask().getId();
        while (id != null) {
            TaskRelatedInfo tci = TaskRelatedManager.getInstance().find(id);
            if (tci.getShortname() != null && tci.getShortname().trim().length() > 0) {
                return tci.getShortname();
            }
            id = tci.getParentId();
        }
        return "ROOT";
    }

    public ArrayList<SecuredTaskAttachmentBean> getAttachments() throws GranException {
        Map<SecuredTaskAttachmentBean, Boolean> map = getAttachsTaskOrMessage();
        if (map != null) {
            return new ArrayList<SecuredTaskAttachmentBean>(map.keySet());
        }
        return null;
    }

    public List<SecuredTaskAttachmentBean> getAttachmentsCheckedName() throws GranException {
        List<SecuredTaskAttachmentBean> list = new ArrayList<SecuredTaskAttachmentBean>();
        if (getAttachments() != null) {
            for (SecuredTaskAttachmentBean attachment : getAttachments()) {
                attachment.setShortname(SecuredTaskAttachmentBean.buildNameAttach(attachment.getName(), 50));
                list.add(attachment);
            }
        }
        return list;
    }

    public Map<SecuredTaskAttachmentBean, Boolean> getAttachsTaskOrMessage() throws GranException {
        boolean lock = lockManager.acquireConnection(SecuredTaskBean.class.getName());
        try {
            List<String> msgs = getMessagesIds();
            if (isAllowedByACL() && getTask() != null && sc.canAction(Action.viewTaskAttachments, getId(), getAllowedPrstatuses())) {
                Map<SecuredTaskAttachmentBean, Boolean> map = new HashMap<SecuredTaskAttachmentBean, Boolean>();
                for (AttachmentCacheItem attachment : KernelManager.getAttachment().getAttachmentList(task.getId(), null, null)) {
                    SecuredTaskAttachmentBean att = null;
                    if (attachment.getMessageId() != null) {
                        if (msgs.contains(attachment.getMessageId())) {
                            att = new SecuredMessageAttachmentBean(attachment, sc);
                            att.setDelete(sc.canAction(Action.manageTaskMessageAttachments, getId()));
                            map.put(att, false);
                        }
                    } else {
                        att = new SecuredTaskAttachmentBean(attachment, sc);
                        att.setDelete(sc.canAction(Action.manageTaskAttachments, getId()));
                        map.put(att, true);
                    }
                    if (att != null) {
                        att.init();
                    }
                }
                if (map.size() > 0) {
                    return map;
                }
            }
        } finally {
            if (lock) lockManager.releaseConnection(SecuredTaskBean.class.getName());
        }
        return null;
    }

    public boolean hasAttachments() throws GranException {
        boolean noAttachments = KernelManager.getAttachment().getAttachmentList(getTask().getId(), null, null).isEmpty();
        return isAllowedByACL() && getTask() != null && sc.canAction(Action.viewTaskAttachments, getId(), getAllowedPrstatuses()) && !noAttachments;
    }

    public List<String> getHandlerPrstatuses() throws GranException {
        if (!isAllowedByACL() || getTask().getHandlerUserId() == null || getTask() == null)
            return null;
        List<String> result = new ArrayList<String>();
        for (Prstatus prStatus : KernelManager.getAcl().getAllowedPrstatusList(getTask().getId(), getTask().getHandlerUserId()))
            result.add(prStatus.getName());
        return result;
    }

    public List<String> getSubmitterPrstatuses() throws GranException {
        List<String> result = new ArrayList<String>();
        if (isAllowedByACL()) {
            for (Prstatus prstatus : KernelManager.getAcl().getAllowedPrstatusList(getId(), getSubmitterId())) {
                result.add(prstatus.getName());
            }
        }
        return result;
    }

    public int hashCode() {
        return getId().hashCode();
    }

    public boolean canView() throws GranException {
        return isOnSight();
    }

    public boolean canManage() throws GranException {
        return isAllowedByACL() && AdapterManager.getInstance().getSecuredCategoryAdapterManager().isCategoryViewable(sc, getId(), getCategoryId());
    }

    public TaskBean getSOAP() throws GranException {
        boolean w = lockManager.acquireConnection();
        try {
            TaskBean bean = new TaskBean();
            bean.setAbudget(getActualBudget() != null ? getActualBudget() : 0);
            bean.setBudget(getBudget() != null ? getBudget() : 0);
            bean.setCategoryId(getCategoryId());
            bean.setClosedate(this.getClosedate() != null ? getClosedate().getTimeInMillis() : -1L);
            bean.setDeadline(getDeadline() != null ? getDeadline().getTimeInMillis() : -1L);
            bean.setDescription(getDescription() != null ? getDescription() : "");
            bean.setHandlerUserId(getHandlerUserId());
            bean.setHandlerGroupId(getHandlerGroupId());
            bean.setId(getId());
            bean.setName(getName());
            bean.setNameCutted(getTaskNameCutted());
            bean.setNumber(getNumber());
            bean.setParentId(getParentId());
            bean.setPriorityId(getPriorityId());
            bean.setResolutionId(getResolutionId());
            bean.setShortname(getShortname());
            bean.setStatusId(getStatusId());
            bean.setSubmitdate(getSubmitdate() != null ? getSubmitdate().getTimeInMillis() : -1L);
            bean.setSubmitterId(getSubmitterId());
            bean.setUpdatedate(getUpdatedate() != null ? getUpdatedate().getTimeInMillis() : -1L);
            bean.setWorkflowId(getWorkflowId());
            bean.setChildrenCount(intOrDef(getChildrenCount(), 0));
            bean.setMessageCount(intOrDef(getMessageCount(), 0));
            bean.setHasAttachments(hasAttachments());
            bean.setOnSight(isOnSight());
            return bean;
        } finally {
            if (w) {
                lockManager.releaseConnection();
            }
        }
    }

    public String getCategoryId() {
        return isOnSight() ? getTask().getCategoryId() : null;
    }

    public String getStatusId() {
        return isOnSight() ? getTask().getStatusId() : null;
    }

    public String getResolutionId()
            throws GranException {
        return sc.canAction(Action.viewTaskResolution, getId(), getAllowedPrstatuses()) ? getTask().getResolutionId() : null;
    }

    public String getPriorityId()
            throws GranException {
        return sc.canAction(Action.viewTaskPriority, getId(), getAllowedPrstatuses()) ? getTask().getPriorityId() : null;
    }

    public String getSubmitterId() {
        return isAllowedByACL() ? getTask().getSubmitterId() : null;
    }

    public String getHandlerUserId() {
        return isAllowedByACL() && getTask() != null ? getTask().getHandlerUserId() : null;
    }

    public String getHandlerGroupId() {
        return isAllowedByACL() && getTask() != null ? getTask().getHandlerGroupId() : null;
    }

    public Collection<String> getHandlerPrstatusesId() throws GranException {
        List<String> result = new ArrayList<String>();
        if (!isAllowedByACL() || getTask().getHandlerUserId() == null || getTask() == null)
            return result;
        for (Prstatus prstatus : KernelManager.getAcl().getAllowedPrstatusList(getTask().getId(), getTask().getHandlerUserId()))
            result.add(prstatus.getId());
        return result;
    }

    public Collection<String> getSubmitterPrstatusesId() throws GranException {
        List<String> result = new ArrayList<String>();
        if (!isAllowedByACL())
            return result;
        for (Prstatus prstatus : KernelManager.getAcl().getAllowedPrstatusList(getId(), getSubmitterId()))
            result.add(prstatus.getId());
        return result;
    }

    public String getParentId() {
        return isOnSight() ? getTask().getParentId() : null;
    }

    public String getWorkflowId() {
        return isOnSight() ? getTask().getWorkflowId() : null;
    }

    public boolean isOnSight() {
        return onSight;
    }

    //dnikitin: Map ���������� � �����, ����� ��� �������� SecuredTaskBean-a � TaskFilter
    //�� ������ �������� �� onSight � hasAccess. �.�. ��� �������� ��������� ������.
    public Map<String, Boolean> getAllowedChildrenMap() throws GranException {
        boolean lock = lockManager.acquireConnection(SecuredTaskBean.class.getName());
        try {
            Map<String, Boolean> result = TaskRelatedManager.getInstance().getAllowedChildrenMap(sc.getUserId(), sc.getPrstatusId(), getId());
            allowedChildrenCount = result.size();
            return result;
        } finally {
            if (lock) lockManager.releaseConnection(SecuredTaskBean.class.getName());
        }

    }

    public Map<String, Boolean> getAllowedChildrenWithSubtasksMap() throws GranException {
        Map<String, Boolean> result = TaskRelatedManager.getInstance().getAllowedChildrenWithSubtasksMap(sc.getUserId(), sc.getPrstatusId(), getId());
        totalChildrenCount = result.size();
        return result;
    }

    public SecuredUserBean getHandler() throws GranException {
        return getHandlerUser();
    }

    @Deprecated
    public String getHandlerId() throws GranException {
        return getHandlerUserId();
    }

    private TreeSet<String> getAllowedPrstatuses() throws GranException {
        if (allowedPrstatuses == null)
            allowedPrstatuses = sc.getAllowedPrstatusesForTask(getId());
        return allowedPrstatuses; //defensive copy not used here, method used internally only
    }

    public TaskRelatedInfo getTask() {
        return task;
    }

    public boolean isAllowedByACL() {
        return allowedByACL;
    }

    public String toString() {
        return getId() + ' ' + getName();
    }



    public boolean equals(Object o) {
        if (this == o) return true;
        //if (o == null || getClass() != o.getClass()) return false;
        //if (!super.equals(o)) return false;

        final SecuredTaskBean that = (SecuredTaskBean) o;

        if (task != null ? !task.equals(that.task) : that.task != null)
            return false;

        return true;
    }

    public String getBudgetFormat() throws GranException {
        String b = getCategory().getBudget();
        return b != null ? b : "";
    }

    public ArrayList<SecuredTaskBean> getAncestors() throws GranException {
        if (getParentId() != null)
            return AdapterManager.getInstance().getSecuredTaskAdapterManager().getTaskChain(sc, null, getParentId());
        else return new ArrayList<SecuredTaskBean>();
    }

    public boolean isOverdue() throws GranException {
        return (getDeadline() != null && !getStatus().isFinish() && getDeadline().getTimeInMillis() < System.currentTimeMillis());
    }

    public String getBudgetAsString() throws GranException {
        Long budget_ = getBudget();
        if (budget_ != null && budget_ > 0) {
            HourFormatter hf = new HourFormatter(getBudget(), getBudgetFormat(), sc.getLocale());
            return hf.getString();
        } else return "";
    }

    public String getActualBudgetAsString() throws GranException {

        Long actualBudget_ = getActualBudget();
        if (actualBudget_ != null && actualBudget_ > 0) {
            HourFormatter hf = new HourFormatter(actualBudget_, getBudgetFormat(), sc.getLocale());
            return hf.getString();
        } else return "";
    }

    public Long getAbudget() throws GranException {
        long abudget = 0L;
        for (SecuredMessageBean message : getMessages()) {
            if (message.getHrs() != null) {
                abudget += message.getHrs();
            }
        }
        return abudget;
    }

    public String getAbudgetToString() throws GranException {
        long abudget = getAbudget();
        if (abudget > 0) {
            HourFormatter hf = new HourFormatter(abudget, getBudgetFormat(), sc.getLocale());
            return hf.getString();
        } else {
            return "-";
        }
    }

    /**
     * This method returns a value of udf field. It is used in templates
     * The value depends of type UDF
     * Integer : Integer
     * String : String
     * Float : Float
     * List : Pair
     * MultiList : List of Pair
     * Task : List of SecuredTaskBean
     * User : List of SecuredUserBean
     * URL : String
     * Memo : String
     *
     * Important: You should use casting for return the necessary type
     * For example : List<SecuredUserBean> userUdfs = (List) task.getUdfValueByName("name");
     *
     * @param name name of field
     * @return value of empty string
     * @throws GranException for necessary
     */
    public Object getUdfValueByName(String name) throws GranException {
        for (Map.Entry<String, SecuredUDFValueBean> entity : getUDFValues().entrySet()) {
            if (name.equals(entity.getValue().getCaption())) {
                return entity.getValue().getValue();
            }
        }
        return "";
    }

    public String getNameOfUsageCalendar() throws GranException {
        return getProjectAlias();
    }

    public String getContext() {
        return "";
    }

    public String getTitle() {
        return getName();
    }


    /**
     * Get category for new task.
     * @return Category of new task
     */
    public String getCategoryForNewTask() {
        return this.categoryForNewTask;
    }

    /**
     * Set category for new task.
     * @param categoryForNewTask Category of new task
     */
    public void setCategoryForNewTask(String categoryForNewTask) {
        this.categoryForNewTask = categoryForNewTask;
    }

    public void setMaskName(String name) {
        try {
            this.maskName = NodeMask.nameByMask(this, false);
        } catch (GranException e) {
            e.printStackTrace();
        }
    }

    public boolean isArchived() {
        return false;
    }
}