package com.trackstudio.form;

import java.util.ArrayList;
import java.util.List;

import org.apache.struts.upload.FormFile;

public class UploadForm extends BaseForm {
    private String[] filedesc;
    private String attachmentId;
    private String description;
    private String taskId, userId;
    private ArrayList<FormFile> file = new ArrayList<FormFile>();

    public String[] getFiledesc() {
        return filedesc == null ? new String[] {} : this.filedesc;
    }

    public void setFiledesc(String[] filedesc) {
        this.filedesc = filedesc;
    }

    public Object getFile() {
        return file;
    }

    public void setFile(Object obj) {
        if (obj instanceof List) {
            this.file.addAll((List) obj);
        } else {
            this.file.add((FormFile) obj);
        }
    }

    public void setFile(int index, FormFile f){
        while (this.file.size()<=index)
        this.file.add(null);

        this.file.set(index, f);
    }

    public FormFile getFile(int index){
        if (this.file.size()>index)
        return this.file.get(index);
        else return null;
    }

    public String getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(String attachmentId) {
        this.attachmentId = attachmentId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
