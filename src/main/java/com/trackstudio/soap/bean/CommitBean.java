package com.trackstudio.soap.bean;

import java.util.Calendar;


public class CommitBean {

    private String author;
    private String message;
    private String revision;
    private Calendar date;
    private String[] changedPath;
    private String[] changedPathTypes;

    public CommitBean() {

    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public String[] getChangedPath() {
        return changedPath;
    }

    public void setChangedPath(String[] changedPath) {
        this.changedPath = changedPath;
    }

    public String[] getChangedPathTypes() {
        return changedPathTypes;
    }

    public void setChangedPathTypes(String[] changedPathTypes) {
        this.changedPathTypes = changedPathTypes;
    }
}
