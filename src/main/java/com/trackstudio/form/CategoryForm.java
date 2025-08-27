package com.trackstudio.form;

public class CategoryForm extends BaseForm {
    private String categoryId;
    private String name, action, description;
    private boolean years, months, weeks, days, hours, minutes, seconds;
    private String workflow;
    private String icon;
    private String addRelation;
    private boolean handlerRequired;
    private boolean groupHandlerAllowed;
    protected String[] ruleCategory;
    protected String submitterOnly;
    protected String handlerOnly;
    protected boolean showInToolbar;
    protected String defaultLink;
    protected String from, to, hiddento;
    protected String canview, cannotview, hiddencanview;
    protected String canedit, cannotedit, hiddencanedit;
    protected String cancreate, cannotcreate, hiddencancreate;
    protected String candelete, cannotdelete, hiddencandelete;
    protected String canhandler, cannothandler, hiddencanhandler;
    private boolean sortOrderInTree;
    private String hiddenInTree;
    private String viewCategory;
    private boolean handerlOnlyRole;

    public boolean isHanderlOnlyRole() {
        return handerlOnlyRole;
    }

    public void setHanderlOnlyRole(boolean handerlOnlyRole) {
        this.handerlOnlyRole = handerlOnlyRole;
    }

    public String getViewCategory() {
        return viewCategory;
    }

    public void setViewCategory(String viewCategory) {
        this.viewCategory = viewCategory;
    }

    public String getHiddenInTree() {
        return hiddenInTree;
    }

    public void setHiddenInTree(String hiddenInTree) {
        this.hiddenInTree = hiddenInTree;
    }

    public boolean isSortOrderInTree() {
        return sortOrderInTree;
    }

    public void setSortOrderInTree(boolean sortOrderInTree) {
        this.sortOrderInTree = sortOrderInTree;
    }

    public String getHiddento() {
        return hiddento;
    }

    public void setHiddento(String hiddento) {
        this.hiddento = hiddento;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubmitterOnly() {
        return submitterOnly;
    }

    public void setSubmitterOnly(String submitterOnly) {
        this.submitterOnly = submitterOnly;
    }

    public String getHandlerOnly() {
        return handlerOnly;
    }

    public void setHandlerOnly(String handlerOnly) {
        this.handlerOnly = handlerOnly;
    }

    public String[] getRuleCategory() {
        return ruleCategory;
    }

    public void setRuleCategory(String[] ruleCategory) {
        this.ruleCategory = ruleCategory;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        if (isMutable())
            this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWorkflow() {
        return workflow;
    }

    public void setWorkflow(String workflow) {
        this.workflow = workflow;
    }

    public String getAddRelation() {
        return addRelation;
    }

    public void setAddRelation(String addRelation) {
        this.addRelation = addRelation;
    }

    public boolean isHandlerRequired() {
        return handlerRequired;
    }

    public void setHandlerRequired(boolean handlerRequired) {
        this.handlerRequired = handlerRequired;
    }

    public boolean isGroupHandlerAllowed() {
        return groupHandlerAllowed;
    }

    public void setGroupHandlerAllowed(boolean groupHandlerAllowed) {
        this.groupHandlerAllowed = groupHandlerAllowed;
    }


    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public boolean getYears() {
        return years;
    }

    public void setYears(boolean years) {
        this.years = years;
    }

    public boolean getMonths() {
        return months;
    }

    public void setMonths(boolean months) {
        this.months = months;
    }

    public boolean getWeeks() {
        return weeks;
    }

    public void setWeeks(boolean weeks) {
        this.weeks = weeks;
    }

    public boolean getDays() {
        return days;
    }

    public void setDays(boolean days) {
        this.days = days;
    }

    public boolean getHours() {
        return hours;
    }

    public void setHours(boolean hours) {
        this.hours = hours;
    }

    public boolean getMinutes() {
        return minutes;
    }

    public void setMinutes(boolean minutes) {
        this.minutes = minutes;
    }

    public boolean getSeconds() {
        return seconds;
    }

    public void setSeconds(boolean seconds) {
        this.seconds = seconds;
    }


    public boolean isYears() {
        return years;
    }

    public boolean isMonths() {
        return months;
    }

    public boolean isWeeks() {
        return weeks;
    }

    public boolean isDays() {
        return days;
    }

    public boolean isHours() {
        return hours;
    }

    public boolean isMinutes() {
        return minutes;
    }

    public boolean isSeconds() {
        return seconds;
    }

    public boolean isShowInToolbar() {
        return showInToolbar;
    }

    public void setShowInToolbar(boolean showInToolbar) {
        if (isMutable())
            this.showInToolbar = showInToolbar;
    }

    public boolean getShowInToolbar() {
        return showInToolbar;
    }


    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        if (isMutable())
            this.icon = icon;
    }


    public String getDefaultLink() {
        return defaultLink;
    }

    public void setDefaultLink(String defaultLink) {
        this.defaultLink = defaultLink;
    }


    public String getCanview() {
        return canview;
    }

    public void setCanview(String canview) {
        this.canview = canview;
    }

    public String getCannotview() {
        return cannotview;
    }

    public void setCannotview(String cannotview) {
        this.cannotview = cannotview;
    }

    public String getHiddencanview() {
        return hiddencanview;
    }

    public void setHiddencanview(String hiddencanview) {
        this.hiddencanview = hiddencanview;
    }

    public String getCanedit() {
        return canedit;
    }

    public void setCanedit(String canedit) {
        this.canedit = canedit;
    }

    public String getCannotedit() {
        return cannotedit;
    }

    public void setCannotedit(String cannotedit) {
        this.cannotedit = cannotedit;
    }

    public String getHiddencanedit() {
        return hiddencanedit;
    }

    public void setHiddencanedit(String hiddencanedit) {
        this.hiddencanedit = hiddencanedit;
    }

    public String getCancreate() {
        return cancreate;
    }

    public void setCancreate(String cancreate) {
        this.cancreate = cancreate;
    }

    public String getCannotcreate() {
        return cannotcreate;
    }

    public void setCannotcreate(String cannotcreate) {
        this.cannotcreate = cannotcreate;
    }

    public String getHiddencancreate() {
        return hiddencancreate;
    }

    public void setHiddencancreate(String hiddencancreate) {
        this.hiddencancreate = hiddencancreate;
    }

    public String getCandelete() {
        return candelete;
    }

    public void setCandelete(String candelete) {
        this.candelete = candelete;
    }

    public String getCannotdelete() {
        return cannotdelete;
    }

    public void setCannotdelete(String cannotdelete) {
        this.cannotdelete = cannotdelete;
    }

    public String getHiddencandelete() {
        return hiddencandelete;
    }

    public void setHiddencandelete(String hiddencandelete) {
        this.hiddencandelete = hiddencandelete;
    }

    public String getCanhandler() {
        return canhandler;
    }

    public void setCanhandler(String canhandler) {
        this.canhandler = canhandler;
    }

    public String getCannothandler() {
        return cannothandler;
    }

    public void setCannothandler(String cannothandler) {
        this.cannothandler = cannothandler;
    }

    public String getHiddencanhandler() {
        return hiddencanhandler;
    }

    public void setHiddencanhandler(String hiddencanhandler) {
        this.hiddencanhandler = hiddencanhandler;
    }
}
