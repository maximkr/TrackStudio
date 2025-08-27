/*
 * @(#)KernelManager.java
 * Copyright 2009 TrackStudio, Inc. All rights reserved.
 */
package com.trackstudio.kernel.manager;

import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.PluginCacheManager;
import com.trackstudio.tools.HibernateUtil;

import net.jcip.annotations.Immutable;

/**
 * Класс KernelManager является базовым для всех остальных классов в Kernel
 */
@Immutable
public abstract class KernelManager {

    /**
     * Объект для быстрого доступа к Hibernate Util
     */
    protected final HibernateUtil hu = new HibernateUtil();

    /**
     * Возвращает экземпляр класса TSInfoManager
     *
     * @return Экземпляр TSInfoManager
     */
    public static TSInfoManager getTSInfo() {
        return new TSInfoManager();
    }

    /**
     * Возвращает экземпляр класса AclManager
     *
     * @return Экземпляр AclManager
     */
    public static AclManager getAcl() {
        return AclManager.getInstance();
    }

    /**
     * Возвращает экземпляр класса AttachmentManager
     *
     * @return Экземпляр AttachmentManager
     */
    public static AttachmentManager getAttachment() {
        return AttachmentManager.getInstance();
    }

    /**
     * Возвращает экземпляр класса BookmarkManager
     *
     * @return Экземпляр BookmarkManager
     */
    public static BookmarkManager getBookmark() {
        return BookmarkManager.getInstance();
    }

    /**
     * Возвращает экземпляр класса CategoryManager
     *
     * @return Экземпляр CategoryManager
     */
    public static CategoryManager getCategory() {
        return CategoryManager.getInstance();
    }

    /**
     * Возвращает экземпляр класса TemplateManager
     *
     * @return Экземпляр TemplateManager
     */
    public static TemplateManager getTemplate() {
        return TemplateManager.getInstance();
    }

    /**
     * Возвращает экземпляр класса FilterManager
     *
     * @return Экземпляр FilterManager
     */
    public static FilterManager getFilter() {
        return FilterManager.getInstance();
    }

    /**
     * Возвращает экземпляр класса FindManager
     *
     * @return Экземпляр FindManager
     */
    public static FindManager getFind() {
        return FindManager.getInstance();
    }

    /**
     * Возвращает экземпляр класса IndexManager
     *
     * @return Экземпляр IndexManager
     */
    public static IndexManager getIndex() {
        return IndexManager.getInstance();
    }


    /**
     * Возвращает экземпляр класса LongTextManager
     *
     * @return Экземпляр LongTextManager
     */
    public static LongTextManager getLongText() {
        return LongTextManager.getInstance();
    }

    /**
     * Возвращает экземпляр класса MailImportManager
     *
     * @return Экземпляр MailImportManager
     */
    public static MailImportManager getMailImport() {
        return MailImportManager.getInstance();
    }

    /**
     * Возвращает экземпляр класса MessageManager
     *
     * @return Экземпляр MessageManager
     */
    public static MessageManager getMessage() {
        return MessageManager.getInstance();
    }

    /**
     * Возвращает экземпляр класса PrstatusManager
     *
     * @return Экземпляр PrstatusManager
     */
    public static PrstatusManager getPrstatus() {
        return PrstatusManager.getInstance();
    }

    /**
     * Возвращает экземпляр класса RegistrationManager
     *
     * @return Экземпляр RegistrationManager
     */
    public static RegistrationManager getRegistration() {
        return RegistrationManager.getInstance();
    }

    /**
     * Возвращает экземпляр класса ReportManager
     *
     * @return Экземпляр ReportManager
     */
    public static ReportManager getReport() {
        return ReportManager.getInstance();
    }

    /**
     * Возвращает экземпляр класса PluginCacheManager
     *
     * @return Экземпляр PluginCacheManager
     * @throws GranException при необходимости
     */
    public static PluginCacheManager getPlugin() throws GranException {
        return PluginCacheManager.getInstance();
    }

    /**
     * Возвращает экземпляр класса StepManager
     *
     * @return Экземпляр StepManager
     */
    public static StepManager getStep() {
        return StepManager.getInstance();
    }

    /**
     * Возвращает экземпляр класса TaskManager
     *
     * @return Экземпляр TaskManager
     */
    public static TaskManager getTask() {
        return TaskManager.getInstance();
    }

    /**
     * Возвращает экземпляр класса UdfManager
     *
     * @return Экземпляр UdfManager
     */
    public static UdfManager getUdf() {
        return UdfManager.getInstance();
    }

    /**
     * Возвращает экземпляр класса UserManager
     *
     * @return Экземпляр UserManager
     */
    public static UserManager getUser() {
        return UserManager.getInstance();
    }

    /**
     * Возвращает экземпляр класса WorkflowManager
     *
     * @return Экземпляр WorkflowManager
     */
    public static WorkflowManager getWorkflow() {
        return WorkflowManager.getInstance();
    }
}