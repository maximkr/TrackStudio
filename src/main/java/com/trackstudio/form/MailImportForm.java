package com.trackstudio.form;

public class MailImportForm extends BaseForm {
    private String mailImportId;
    private String name;
    private String domain;
    private String enableMailImport;
    private String keywords;
    private String searchIn;
    private String order;
    private String category;
    private String messageType;
    private String owner;
    private boolean active;
    private boolean importUnknown;

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getEnableMailImport() {
        return enableMailImport;
    }

    public void setEnableMailImport(String enableMailImport) {
        this.enableMailImport = enableMailImport;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getSearchIn() {
        return searchIn;
    }

    public void setSearchIn(String searchIn) {
        this.searchIn = searchIn;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMailImportId() {
        return mailImportId;
    }

    public void setMailImportId(String mailImportId) {
        if (isMutable())
            this.mailImportId = mailImportId;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public boolean getImportUnknown() {
        return importUnknown;
    }

    public void setImportUnknown(boolean importUnknown) {
        this.importUnknown = importUnknown;
    }
}
