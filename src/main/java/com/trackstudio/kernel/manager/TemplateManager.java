/*
 * @(#)TemplateManager.java
 * Copyright 2009 TrackStudio, Inc. All rights reserved.
 */
package com.trackstudio.kernel.manager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.TaskRelatedInfo;
import com.trackstudio.kernel.cache.TaskRelatedManager;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.model.Template;
import com.trackstudio.secured.SecuredTemplateBean;

import net.jcip.annotations.Immutable;

/**
 * Класс TemplateManager содержит методы для работы c шаблонами
 */
@Immutable
public class TemplateManager extends KernelManager {

    private static final String className = "TemplateManager.";
    private static final TemplateManager instance = new TemplateManager();
    private static final LockManager lockManager = LockManager.getInstance();
    /**
     * Конструктор по умолчанию
     */
    private TemplateManager() {
    }

    /**
     * Возвращает экземпляр текущего класса
     *
     * @return Экземпляр TemplateManager
     */
    protected static TemplateManager getInstance() {
        return instance;
    }

    /**
     * Редактируется шаблон
     *
     * @param templateId  ID шаблона
     * @param name        Название шаблона
     * @param description Описание шаблона
     * @param userId      ID пользователя
     * @param folder      Каталог шаблона
     * @param active      Активен шаблон или нет
     * @throws GranException при необходимости
     */
    public void updateTemplate(String templateId, SafeString name, SafeString description, String userId, SafeString folder, Integer active) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            Template obj = (Template) hu.getObject(Template.class, templateId);
            obj.setFolder(folder != null ? folder.toString() : null);
            obj.setName(name != null ? name.toString() : null);
            obj.setDescription(description != null ? description.toString() : null);
            obj.setUser(userId);
            obj.setActive(active);
            hu.updateObject(obj);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает список шаблонов
     *
     * @param taskId ID задачи, для которой возвращается вписок шаблонов
     * @return список шаблонов
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Template
     */
    public Set<Template> getTemplateList(String taskId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            Set<Template> result = new HashSet<Template>();
            for (TaskRelatedInfo o : KernelManager.getTask().getTaskChain(null, taskId))
                result.addAll(hu.getList("select t from com.trackstudio.model.Template t, com.trackstudio.model.Task v " +
                        "where t.task=? and v.id = t.task order by t.name", o.getId()));
            return result;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает список всех шаблонов
     *
     * @param taskId ID задачи
     * @return список шиблогов
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Template
     */
    public Set<Template> getAllTemplatesList(String taskId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            Set<Template> result = new HashSet<Template>();
            List<Template> templates = hu.getList("select t from com.trackstudio.model.Template t, com.trackstudio.model.Task v where v.id = t.task order by t.name");
            for (Template t : templates) {
                if (TaskRelatedManager.getInstance().hasPath(taskId, t.getTask().getId())) {
                    result.add(t);
                }
            }
            return result;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * This method returns all templates in the system
     * @return all templates
     * @throws GranException for necessary
     */
    public List<Template> getAllTemplatesList() throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            return hu.getList("select template from com.trackstudio.model.Template as template where template.task is not null");
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    public Set<SecuredTemplateBean> getTemplatesList(SessionContext sc, String taskId) throws GranException {
        Set<SecuredTemplateBean> templs = new TreeSet<SecuredTemplateBean>();
        for (Template tem : getAllTemplatesList(taskId)) {
            templs.add(new SecuredTemplateBean(tem, sc));
        }
        return templs;
    }

    /**
     * Создает шаблон
     *
     * @param name    Название шаблона
     * @param ownerId Владелец шаблона
     * @param taskId  ID адачи, к которой приявязывается шаблон
     * @return ID созданного шаблона
     * @throws GranException пи еобходимости
     */
    public String createTemplate(SafeString name, String ownerId, String taskId) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            return hu.createObject(new Template(name != null ? name.toString() : null, ownerId, taskId));
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Создает шаблон
     *
     * @param name    Название шаблона
     * @param ownerId Владелец шаблона
     * @param taskId  ID адачи, к которой приявязывается шаблон
     * @param userId  ID пользователя, который создает шаблон
     * @return ID созданного шаблона
     * @throws GranException пи еобходимости
     */
    public String createTemplate(SafeString name, String ownerId, String userId, String taskId) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            return hu.createObject(new Template(name != null ? name.toString() : null, ownerId, userId, taskId));
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Создает шаблон
     *
     * @param name        Название шаблона
     * @param ownerId     Владелец шаблона
     * @param taskId      ID адачи, к которой приявязывается шаблон
     * @param userId      ID пользователя, который создает шаблон
     * @param description описание шаблона
     * @return ID созданного шаблона
     * @throws GranException пи еобходимости
     */
    public String createTemplate(SafeString name, SafeString description, String ownerId, String userId, String taskId) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            return hu.createObject(new Template(name != null ? name.toString() : null, description != null ? description.toString() : null, ownerId, userId, taskId));
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Удаляет шаблон
     *
     * @param templateId ID удаляемого шаблона
     * @throws GranException при необходимости
     */
    public void deleteTemplate(String templateId) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            hu.deleteObject(Template.class, templateId);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    public List<Template> getTemplateOwnerList(String userId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            return hu.getList("select t from com.trackstudio.model.Template t where t.owner=? ", userId);
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }
}