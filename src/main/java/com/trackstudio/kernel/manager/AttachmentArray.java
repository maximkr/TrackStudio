/*
 * @(#)AclManager.java
 * Copyright 2009 TrackStudio, Inc. All rights reserved.
 */
package com.trackstudio.kernel.manager;

import java.io.InputStream;

import net.jcip.annotations.ThreadSafe;

/**
 * Вспомогательный класс AttachmentArray, используется для целей передачи данных о приложенных файлов внутри системы
 */
@ThreadSafe
public class AttachmentArray {

    private final SafeString name;
    private volatile SafeString description;
    private final InputStream data;
    private final int len;
    private volatile boolean isTinyMCEImage = false;
    private volatile String initialID;
    private volatile String context;
    private volatile boolean uploadFromApplet = false;
    private volatile String contentId; //this variable is used when we fetch images from email imports

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public boolean isUploadFromApplet() {
        return uploadFromApplet;
    }

    public void setUploadFromApplet(boolean uploadFromApplet) {
        this.uploadFromApplet = uploadFromApplet;
    }

    public AttachmentArray(SafeString name, SafeString description, InputStream data) {
        this.name = name;
        this.description = description;
        this.data = data;
        this.len = 0;
    }

    public AttachmentArray(SafeString name, SafeString description, InputStream data, int len) {
        this.name = name;
        this.description = description;
        this.data = data;
        this.len = len;
    }

    public int getLen() {
        return len;
    }

    public SafeString getName() {
        return name;
    }

    public SafeString getDescription() {
        return description;
    }

    public InputStream getData() {
        return data;
    }

    public void setDescription(SafeString description) {
        this.description = description;
    }

    public boolean isTinyMCEImage() {
        return isTinyMCEImage;
    }

    public void setTinyMCEImage(boolean tinyMCEImage) {
        isTinyMCEImage = tinyMCEImage;
    }

    public String getInitialID() {
        return initialID;
    }

    public void setInitialID(String initialID) {
        this.initialID = initialID;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    @Override
    public String toString() {
        return "AttachmentArray{" +
                "name=" + name +
                ", description=" + description +
                ", initialID='" + initialID + '\'' +
                ", context='" + context + '\'' +
                ", contentId='" + contentId + '\'' +
                '}';
    }
}
