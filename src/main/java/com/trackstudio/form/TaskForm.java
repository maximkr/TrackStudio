package com.trackstudio.form;

public class TaskForm extends UDFForm {
    private String category;
    private String workflowId;
    private String parentForCancel;
    private String name;
    private String shortname;
    private Double budgetDoubleYears, budgetDoubleMonths, budgetDoubleWeeks, budgetDoubleDays, budgetDoubleHours, budgetDoubleMinutes;
    private Integer budgetIntegerYears, budgetIntegerMonths, budgetIntegerWeeks, budgetIntegerDays, budgetIntegerHours, budgetIntegerMinutes, budgetIntegerSeconds;

    private String priority;
    private String handler;
    private String deadline;
    private String description;

    private String[] SELTASK;
    private String[] DELMESSAGE;
    private String[] TASKIDS;

    private String SINGLE_COPY;
    private String CUT;

    private String filterId;

    private String newTask;
    private String key;

    private String plainText;
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        if (mutable) this.category = category;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        if (mutable) this.workflowId = workflowId;
    }

    public String getParentForCancel() {
        return parentForCancel;
    }

    public void setParentForCancel(String parentForCancel) {
        if (mutable) this.parentForCancel = parentForCancel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (mutable) this.name = name;
    }

    public String getShortname() {
        return shortname;
    }

    public void setShortname(String shortname) {
        if (mutable) this.shortname = shortname;
    }


    public Double getBudgetDoubleYears() {
        return budgetDoubleYears;
    }

    public void setBudgetDoubleYears(Double budgetDoubleYears) {
        if (mutable) this.budgetDoubleYears = budgetDoubleYears;
    }

    public Double getBudgetDoubleMonths() {
        return budgetDoubleMonths;
    }

    public void setBudgetDoubleMonths(Double budgetDoubleMonths) {
        if (mutable) this.budgetDoubleMonths = budgetDoubleMonths;
    }

    public Double getBudgetDoubleWeeks() {
        return budgetDoubleWeeks;
    }

    public void setBudgetDoubleWeeks(Double budgetDoubleWeeks) {
        if (mutable) this.budgetDoubleWeeks = budgetDoubleWeeks;
    }

    public Double getBudgetDoubleDays() {
        return budgetDoubleDays;
    }

    public void setBudgetDoubleDays(Double budgetDoubleDays) {
        if (mutable) this.budgetDoubleDays = budgetDoubleDays;
    }

    public Double getBudgetDoubleHours() {
        return budgetDoubleHours;
    }

    public void setBudgetDoubleHours(Double budgetDoubleHours) {
        if (mutable) this.budgetDoubleHours = budgetDoubleHours;
    }

    public Double getBudgetDoubleMinutes() {
        return budgetDoubleMinutes;
    }

    public void setBudgetDoubleMinutes(Double budgetDoubleMinutes) {
        if (mutable) this.budgetDoubleMinutes = budgetDoubleMinutes;
    }

    public Integer getBudgetIntegerYears() {
        return budgetIntegerYears;
    }

    public void setBudgetIntegerYears(Integer budgetIntegerYears) {
        if (mutable) this.budgetIntegerYears = budgetIntegerYears;
    }

    public Integer getBudgetIntegerMonths() {
        return budgetIntegerMonths;
    }

    public void setBudgetIntegerMonths(Integer budgetIntegerMonths) {
        if (mutable) this.budgetIntegerMonths = budgetIntegerMonths;
    }

    public Integer getBudgetIntegerWeeks() {
        return budgetIntegerWeeks;
    }

    public void setBudgetIntegerWeeks(Integer budgetIntegerWeeks) {
        if (mutable) this.budgetIntegerWeeks = budgetIntegerWeeks;
    }

    public Integer getBudgetIntegerDays() {
        return budgetIntegerDays;
    }

    public void setBudgetIntegerDays(Integer budgetIntegerDays) {
        if (mutable) this.budgetIntegerDays = budgetIntegerDays;
    }

    public Integer getBudgetIntegerHours() {
        return budgetIntegerHours;
    }

    public void setBudgetIntegerHours(Integer budgetIntegerHours) {
        if (mutable) this.budgetIntegerHours = budgetIntegerHours;
    }

    public Integer getBudgetIntegerMinutes() {
        return budgetIntegerMinutes;
    }

    public void setBudgetIntegerMinutes(Integer budgetIntegerMinutes) {
        if (mutable) this.budgetIntegerMinutes = budgetIntegerMinutes;
    }

    public Integer getBudgetIntegerSeconds() {
        return budgetIntegerSeconds;
    }

    public void setBudgetIntegerSeconds(Integer budgetIntegerSeconds) {
        if (mutable) this.budgetIntegerSeconds = budgetIntegerSeconds;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        if (mutable) this.priority = priority;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        if (mutable) this.handler = handler;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        if (mutable) this.deadline = deadline;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (mutable) this.description = description;
    }

    public String[] getSELTASK() {
        return SELTASK;
    }

    public void setSELTASK(String[] SELTASK) {
        this.SELTASK = SELTASK;
    }

    public String[] getDELMESSAGE() {
        return DELMESSAGE;
    }

    public void setDELMESSAGE(String[] DELMESSAGE) {
        this.DELMESSAGE = DELMESSAGE;
    }

    public String[] getTASKIDS() {
        return TASKIDS;
    }

    public void setTASKIDS(String[] TASKIDS) {
        this.TASKIDS = TASKIDS;
    }

    public String getSINGLE_COPY() {
        return SINGLE_COPY;
    }

    public void setSINGLE_COPY(String SINGLE_COPY) {
        this.SINGLE_COPY = SINGLE_COPY;
    }

    public String getCUT() {
        return CUT;
    }

    public void setCUT(String CUT) {
        this.CUT = CUT;
    }

    public String getFilterId() {
        return filterId;
    }

    public void setFilterId(String filterId) {
        if (mutable)
            this.filterId = filterId;
    }

    public String getNewTask() {
        return newTask;
    }

    public void setNewTask(String newTask) {
        if (mutable)
            this.newTask = newTask;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        if (mutable)
            this.key = key;
    }

    public String getPlainText() {
        return plainText;
    }

    public void setPlainText(String plainText) {
        if (mutable) this.plainText = plainText;
    }

    public Number getBudgetYears() {
        if (getBudgetIntegerYears() != null) {

            return getBudgetIntegerYears();
        } else
            return getBudgetDoubleYears();

    }

    public Number getBudgetMonths() {
        if (getBudgetIntegerMonths() != null) {

            return getBudgetIntegerMonths();
        } else return getBudgetDoubleMonths();
    }

    public Number getBudgetWeeks() {
        if (getBudgetIntegerWeeks() != null) {

            return getBudgetIntegerWeeks();
        } else return getBudgetDoubleWeeks();
    }

    public Number getBudgetDays() {
        if (getBudgetIntegerDays() != null) return getBudgetIntegerDays();
        else return getBudgetDoubleDays();
    }

    public Number getBudgetHours() {
        if (getBudgetIntegerHours() != null) return getBudgetIntegerHours();
        else return getBudgetDoubleHours();
    }

    public Number getBudgetMinutes() {
        if (getBudgetIntegerMinutes() != null) {

            return getBudgetIntegerMinutes();
        } else return getBudgetDoubleMinutes();
    }

    public Number getBudgetSeconds() {

        return getBudgetIntegerSeconds();

    }
}
