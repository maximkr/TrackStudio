package com.trackstudio.soap.bean;

public class TaskExtendedBean extends TaskBean {

    public TaskExtendedBean() {
    }

    public TaskExtendedBean(TaskBean bean) {
        this.setAbudget(bean.getAbudget());
        this.setBudget(bean.getBudget());
        this.setCategoryId(bean.getCategoryId());
        this.setClosedate(bean.getClosedate());
        this.setDeadline(bean.getDeadline());
        this.setDescription(bean.getDescription());
        this.setHandlerUserId(bean.getHandlerUserId());
        this.setHandlerGroupId(bean.getHandlerGroupId());
        this.setId(bean.getId());
        this.setName(bean.getName());
        this.setNameCutted(bean.getNameCutted());
        this.setNumber(bean.getNumber());
        this.setParentId(bean.getParentId());
        this.setPriorityId(bean.getPriorityId());
        this.setResolutionId(bean.getResolutionId());
        this.setShortname(bean.getShortname());
        this.setStatusId(bean.getStatusId());
        this.setSubmitdate(bean.getSubmitdate());
        this.setSubmitterId(bean.getSubmitterId());
        this.setUpdatedate(bean.getUpdatedate());
        this.setWorkflowId(bean.getWorkflowId());
        this.setChildrenCount(bean.getChildrenCount());
        this.setMessageCount(bean.getMessageCount());
        this.setHasAttachments(bean.isHasAttachments());
        this.setOnSight(bean.isOnSight());
    }

    private UdfBean[] udfs;
    private UdfValueBean[] udfValues;
    private PairBean[] names;

    public UdfBean[] getUdfs() {
        return udfs;
    }

    public void setUdfs(UdfBean[] udfs) {
        this.udfs = udfs;
    }

    public UdfValueBean[] getUdfValues() {
        return udfValues;
    }

    public void setUdfValues(UdfValueBean[] udfValues) {
        this.udfValues = udfValues;
    }

    public PairBean[] getNames() {
        return names;
    }

    public void setNames(PairBean[] names) {
        this.names = names;
    }
}
