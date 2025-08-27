package com.trackstudio.form;

public class CategoryTemplateForm extends BaseForm {
    private String categoryId;
    private String template;

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        if (isMutable())
            this.categoryId = categoryId;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String name) {
        this.template = name;
    }

}
