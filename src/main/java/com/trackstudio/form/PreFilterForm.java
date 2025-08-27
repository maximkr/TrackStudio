package com.trackstudio.form;

public class PreFilterForm extends BaseForm {

    protected String field;
    protected String oldfield;
    protected String filter;
    protected String reportId;
    protected String search;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getOldfield() {
        return oldfield;
    }

    public void setOldfield(String oldfield) {
        this.oldfield = oldfield;
    }


    public String getReportId() {
        return reportId;
    }

    public void setReportId(String report) {
        this.reportId = report;
    }
}
