package com.trackstudio.form;

import org.apache.struts.action.ActionForm;

import com.trackstudio.tools.formatter.HourFormatter;

public class MessageForm extends UDFForm {
    protected String priority;
    protected String returnToTask;

    protected String deadline;
    private Double budgetDoubleYears, budgetDoubleMonths, budgetDoubleWeeks, budgetDoubleDays, budgetDoubleHours, budgetDoubleMinutes;
    private Integer budgetIntegerYears, budgetIntegerMonths, budgetIntegerWeeks, budgetIntegerDays, budgetIntegerHours, budgetIntegerMinutes, budgetIntegerSeconds;

    private String actualBudgetDoubleYears, actualBudgetDoubleMonths, actualBudgetDoubleWeeks, actualBudgetDoubleDays, actualBudgetDoubleHours, actualBudgetDoubleMinutes;
    private Integer actualBudgetIntegerYears, actualBudgetIntegerMonths, actualBudgetIntegerWeeks, actualBudgetIntegerDays, actualBudgetIntegerHours, actualBudgetIntegerMinutes, actualBudgetIntegerSeconds;


    protected String mstatus;
    protected String handler;
    protected String bugnote;
    protected String resolution;
    protected String[] deleteMessage;//пїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅ delete-checkbox-пїЅпїЅ

    protected String selectedText;

    public String getSelectedText() {
        return selectedText;
    }

    public void setSelectedText(String selectedText) {
        this.selectedText = selectedText;
    }

    public String[] getDeleteMessage() {
        return deleteMessage;
    }

    public void setDeleteMessage(String[] deleteMessage) {
        this.deleteMessage = deleteMessage;
    }

    public void restore(ActionForm form) {
        MessageForm t = (MessageForm) form;

        budgetDoubleDays = t.budgetDoubleDays;
        budgetDoubleHours = t.budgetDoubleHours;
        budgetDoubleMinutes = t.budgetDoubleMinutes;
        budgetDoubleMonths = t.budgetDoubleMonths;
        budgetDoubleWeeks = t.budgetDoubleWeeks;
        budgetDoubleYears = t.budgetDoubleYears;
        budgetIntegerDays = t.budgetIntegerDays;
        budgetIntegerHours = t.budgetIntegerHours;
        budgetIntegerMinutes = t.budgetIntegerMinutes;
        budgetIntegerMonths = t.budgetIntegerMonths;
        budgetIntegerSeconds = t.budgetIntegerSeconds;
        budgetIntegerWeeks = t.budgetIntegerWeeks;
        budgetIntegerYears = t.budgetIntegerYears;

        actualBudgetDoubleDays = t.actualBudgetDoubleDays;
        actualBudgetDoubleHours = t.actualBudgetDoubleHours;
        actualBudgetDoubleMinutes = t.actualBudgetDoubleMinutes;
        actualBudgetDoubleMonths = t.actualBudgetDoubleMonths;
        actualBudgetDoubleWeeks = t.actualBudgetDoubleWeeks;
        actualBudgetDoubleYears = t.actualBudgetDoubleYears;
        actualBudgetIntegerDays = t.actualBudgetIntegerDays;
        actualBudgetIntegerHours = t.actualBudgetIntegerHours;
        actualBudgetIntegerMinutes = t.actualBudgetIntegerMinutes;
        actualBudgetIntegerMonths = t.actualBudgetIntegerMonths;
        actualBudgetIntegerSeconds = t.actualBudgetIntegerSeconds;
        actualBudgetIntegerWeeks = t.actualBudgetIntegerWeeks;
        actualBudgetIntegerYears = t.actualBudgetIntegerYears;

        deadline = t.deadline;
        handler = t.handler;
        priority = t.priority;
        resolution = t.resolution;
        mstatus = t.mstatus;

        bugnote = t.bugnote;
        udf = t.udf;
    }

    public String getMstatus() {
        return mstatus;
    }

    public void setMstatus(String mstatus) {
        if (mutable) this.mstatus = mstatus;
    }


    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        if (mutable) this.priority = priority;
    }


    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        if (mutable) this.deadline = deadline;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        if (mutable) this.handler = handler;
    }

    public String getBugnote() {
        return bugnote;
    }

    public void setBugnote(String bugnote) {
        if (mutable) this.bugnote = bugnote;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        if (mutable) this.resolution = resolution;
    }


    public String getReturnToTask() {
        return returnToTask;
    }

    public void setReturnToTask(String returnToTask) {
        if (mutable) this.returnToTask = returnToTask;
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

    public String getActualBudgetDoubleYears() {
        return actualBudgetDoubleYears;
    }

    public void setActualBudgetDoubleYears(String actualBudgetDoubleYears) {
        this.actualBudgetDoubleYears = actualBudgetDoubleYears;
    }

    public String getActualBudgetDoubleMonths() {
        return actualBudgetDoubleMonths;
    }

    public void setActualBudgetDoubleMonths(String actualBudgetDoubleMonths) {
        this.actualBudgetDoubleMonths = actualBudgetDoubleMonths;
    }

    public String getActualBudgetDoubleWeeks() {
        return actualBudgetDoubleWeeks;
    }

    public void setActualBudgetDoubleWeeks(String actualBudgetDoubleWeeks) {
        this.actualBudgetDoubleWeeks = actualBudgetDoubleWeeks;
    }

    public String getActualBudgetDoubleDays() {
        return actualBudgetDoubleDays;
    }

    public void setActualBudgetDoubleDays(String actualBudgetDoubleDays) {
        this.actualBudgetDoubleDays = actualBudgetDoubleDays;
    }

    public String getActualBudgetDoubleHours() {
        return actualBudgetDoubleHours;
    }

    public void setActualBudgetDoubleHours(String actualBudgetDoubleHours) {
        this.actualBudgetDoubleHours = actualBudgetDoubleHours;
    }

    public String getActualBudgetDoubleMinutes() {
        return actualBudgetDoubleMinutes;
    }

    public void setActualBudgetDoubleMinutes(String actualBudgetDoubleMinutes) {
        this.actualBudgetDoubleMinutes = actualBudgetDoubleMinutes;
    }

    public Integer getActualBudgetIntegerYears() {
        return actualBudgetIntegerYears;
    }

    public void setActualBudgetIntegerYears(Integer actualBudgetIntegerYears) {
        if (mutable) this.actualBudgetIntegerYears = actualBudgetIntegerYears;
    }

    public Integer getActualBudgetIntegerMonths() {
        return actualBudgetIntegerMonths;
    }

    public void setActualBudgetIntegerMonths(Integer actualBudgetIntegerMonths) {
        if (mutable) this.actualBudgetIntegerMonths = actualBudgetIntegerMonths;
    }

    public Integer getActualBudgetIntegerWeeks() {
        return actualBudgetIntegerWeeks;
    }

    public void setActualBudgetIntegerWeeks(Integer actualBudgetIntegerWeeks) {
        if (mutable) this.actualBudgetIntegerWeeks = actualBudgetIntegerWeeks;
    }

    public Integer getActualBudgetIntegerDays() {
        return actualBudgetIntegerDays;
    }

    public void setActualBudgetIntegerDays(Integer actualBudgetIntegerDays) {
        if (mutable) this.actualBudgetIntegerDays = actualBudgetIntegerDays;
    }

    public Integer getActualBudgetIntegerHours() {
        return actualBudgetIntegerHours;
    }

    public void setActualBudgetIntegerHours(Integer actualBudgetIntegerHours) {
        if (mutable) this.actualBudgetIntegerHours = actualBudgetIntegerHours;
    }

    public Integer getActualBudgetIntegerMinutes() {
        return actualBudgetIntegerMinutes;
    }

    public void setActualBudgetIntegerMinutes(Integer actualBudgetIntegerMinutes) {
        if (mutable) this.actualBudgetIntegerMinutes = actualBudgetIntegerMinutes;
    }

    public Integer getActualBudgetIntegerSeconds() {
        return actualBudgetIntegerSeconds;
    }

    public void setActualBudgetIntegerSeconds(Integer actualBudgetIntegerSeconds) {
        if (mutable) this.actualBudgetIntegerSeconds = actualBudgetIntegerSeconds;
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

    public Number getActualBudgetYears() {
        if (getActualBudgetIntegerYears() != null) {

            return getActualBudgetIntegerYears();
        } else
            return HourFormatter.parseDouble(getActualBudgetDoubleYears());

    }

    public Number getActualBudgetMonths() {
        if (getActualBudgetIntegerMonths() != null) {

            return getActualBudgetIntegerMonths();
        } else return HourFormatter.parseDouble(getActualBudgetDoubleMonths());
    }

    public Number getActualBudgetWeeks() {
        if (getActualBudgetIntegerWeeks() != null) {

            return getActualBudgetIntegerWeeks();
        } else return HourFormatter.parseDouble(getActualBudgetDoubleWeeks());
    }

    public Number getActualBudgetDays() {
        if (getActualBudgetIntegerDays() != null) return getActualBudgetIntegerDays();
        else return HourFormatter.parseDouble(getActualBudgetDoubleDays());
    }

    public Number getActualBudgetHours() {
        if (getActualBudgetIntegerHours() != null) return getActualBudgetIntegerHours();
        else return HourFormatter.parseDouble(getActualBudgetDoubleHours());
    }

    public Number getActualBudgetMinutes() {
        if (getActualBudgetIntegerMinutes() != null) {

            return getActualBudgetIntegerMinutes();
        } else return HourFormatter.parseDouble(getActualBudgetDoubleMinutes());
    }

    public Number getActualBudgetSeconds() {

        return getActualBudgetIntegerSeconds();

    }
}
