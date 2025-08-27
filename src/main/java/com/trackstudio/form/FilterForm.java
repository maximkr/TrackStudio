package com.trackstudio.form;

public class FilterForm extends BaseForm {


    private boolean shared;
    private boolean showInToolbar;
    private String name;
    private String description;
    private String fields;
    private String from;
    private String to;

    private String task;
    private String user;
    protected boolean subtask;
    protected String onpage;
    protected String search;
    private String[] select;
    private String filterId;
    private boolean showmsg;
    private String msgnum;


    public boolean isShowmsg() {
        return showmsg;
    }

    public void setShowmsg(boolean showmsg) {
        this.showmsg = showmsg;
    }

    public String getMsgnum() {
        return msgnum;
    }

    public void setMsgnum(String msgnum) {
        this.msgnum = msgnum;
    }

    public boolean isShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
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


    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }


    public boolean isSubtask() {
        return subtask;
    }

    public boolean getSubtask() {
        return subtask;
    }

    public void setSubtask(boolean subtask) {
        this.subtask = subtask;
    }

    public String getOnpage() {
        return onpage;
    }

    public void setOnpage(String onpage) {
        this.onpage = onpage;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }


    public String[] getSelect() {
        return select;
    }

    public void setSelect(String[] select) {
        this.select = select;
    }

    public String getFilterId() {
        return filterId;
    }

    public void setFilterId(String filterId) {
        if (isMutable())
            this.filterId = filterId;
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


    public String getFields() {
        return fields;
    }

    public void setFields(String fields) {
        this.fields = fields;
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
}
