package com.trackstudio.form;

import java.util.HashMap;
import java.util.Map;

import org.apache.struts.action.ActionForm;

public class BaseForm extends ActionForm {
    public Map getMap() {
        return map;
    }

    private Map map = new HashMap();
    protected String method;//нужно для DispatchAction-а
    protected String session;
    protected String id;
    protected String collector;
    private String name;

    private String[] delete;//нужен для delete-checkbox-ов
    private String[] delete2;//нужно на WF->Mstatus(и еще где-то) - там удаляем и mstatus-ы и msgResolution-ы

    private String deleteButton;
    private String createButton;
    private String saveButton;
    private String go;
    private String reset;
    private String cancelButton;
    private String parentButton;

    private String sliderPage;
    private String sliderOrder;

    private String cloneButton;
    private boolean all;

    public boolean isAll() {
        return all;
    }

    public void setAll(boolean all) {
        this.all = all;
    }

    boolean mutable = true;//нужно для передачи данных между action-ами через form:
    //записываем form.setXXX(yyy); а потом form.setMutable(false);

    public String getId() {
        return id;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public void setId(String id) {
        if (isMutable())
            this.id = id;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setValue(String key, Object value) {
        map.put(key, value);
    }

    public Object getValue(String key) {
        return map.get(key);
    }

    public String getDeleteButton() {
        return deleteButton;
    }

    public void setDeleteButton(String deleteButton) {
        this.deleteButton = deleteButton;
    }

    public String[] getDelete() {
        return delete;
    }

    public void setDelete(String[] delete) {
        this.delete = delete;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSaveButton() {
        return saveButton;
    }

    public void setSaveButton(String saveButton) {
        this.saveButton = saveButton;
    }

    public String getCreateButton() {
        return createButton;
    }

    public void setCreateButton(String createButton) {
        this.createButton = createButton;
    }

    public String[] getDelete2() {
        return delete2;
    }

    public void setDelete2(String[] delete2) {
        this.delete2 = delete2;
    }

    public boolean isMutable() {
        return mutable;
    }

    public void setMutable(boolean mutable) {
        this.mutable = mutable;
    }

    public String getSliderPage() {
        return sliderPage;
    }

    public void setSliderPage(String sliderPage) {
        this.sliderPage = sliderPage;
    }

    public String getSliderOrder() {
        return sliderOrder;
    }

    public void setSliderOrder(String sliderOrder) {
        this.sliderOrder = sliderOrder;
    }

    public String getGo() {
        return go;
    }

    public void setGo(String goButton) {
        this.go = goButton;
    }

    public boolean savePressed() {
        return saveButton != null && saveButton.length() != 0;
    }

    public boolean deletePressed() {
        return deleteButton != null && deleteButton.length() != 0;
    }

    public boolean createPressed() {
        return createButton != null && createButton.length() != 0;
    }

    public boolean goPressed() {
        return go != null && go.length() != 0;
    }

    public boolean parentPressed() {
        return parentButton != null && parentButton.length() != 0;
    }

    public boolean cancelPressed() {
        return cancelButton != null && cancelButton.length() != 0;
    }

    public String getCancelButton() {
        return cancelButton;
    }

    public void setCancelButton(String cancelButton) {
        this.cancelButton = cancelButton;
    }

    public String getParentButton() {
        return parentButton;
    }

    public void setParentButton(String parentButton) {
        this.parentButton = parentButton;
    }


    public String getReset() {
        return reset;
    }

    public void setReset(String reset) {
        this.reset = reset;
    }

    public String getCollector() {
        return collector;
    }

    public void setCollector(String collector) {
        this.collector = collector;
    }

    public String getCloneButton() {
        return cloneButton;
    }

    public void setCloneButton(String cloneButton) {
        this.cloneButton = cloneButton;
    }

    public boolean clonePressed() {
        return cloneButton != null && cloneButton.length() != 0;
    }
}
