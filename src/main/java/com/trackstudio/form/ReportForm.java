package com.trackstudio.form;

import java.util.HashMap;
import java.util.Map;

public class ReportForm extends BaseForm {
    private String name;
    private String type;
    private String filter;
    private String owner;

    private String reportId;
    private String zipped;
    private boolean shared;
    private String format;
    private String[] select;

    private String delimiter;
    private String field;
    private String oldfield;
    private String search;
    private String charset;

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    private HashMap<String, String> params = new HashMap<String, String>();

    public String getZipped() {
        return zipped;
    }

    public void setZipped(String zipped) {
        this.zipped = zipped;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }


    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        if (isMutable())
            this.reportId = reportId;
    }

    public boolean isShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }

    public void setParams(String key, String value) {
        params.put(key, value);
    }

    public String getParams(String key) {
        Object r = params.get(key);
        if (r != null) return r.toString();
        else return null;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(HashMap<String, String> params) {
        this.params = params;
    }


    public String[] getSelect() {
        return select;
    }

    public void setSelect(String[] select) {
        this.select = select;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getOldfield() {
        return oldfield;
    }

    public void setOldfield(String oldfield) {
        this.oldfield = oldfield;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

}
