package com.trackstudio.form;


public class ExportForm extends BaseForm {
    private String exportFormat;
    private String zipped;
    private String filter;
    private String charset;

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getZipped() {
        return zipped;
    }

    public void setZipped(String zipped) {
        this.zipped = zipped;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getExportFormat() {
        return exportFormat;
    }

    public void setExportFormat(String exportFormat) {
        this.exportFormat = exportFormat;
    }
}
