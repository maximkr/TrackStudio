package com.trackstudio.form;

public class TemplateForm extends BaseForm {
    private String templateId;
    private String name;
    private String description;
    private Boolean active;
    private String user;
    private String folder;

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        if (isMutable())

            this.templateId = templateId;
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

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }
}
