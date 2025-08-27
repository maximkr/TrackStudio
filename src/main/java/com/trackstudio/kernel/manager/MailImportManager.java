/*
 * @(#)MailImportManager.java
 * Copyright 2009 TrackStudio, Inc. All rights reserved.
 */
package com.trackstudio.kernel.manager;

import java.util.ArrayList;
import java.util.List;

import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.model.MailImport;

import net.jcip.annotations.Immutable;

/**
 * Класс MailImportManager предназначен для рпботы с правила импорта почтовых сообщений
 */
@Immutable
public class MailImportManager extends KernelManager {

    private static final String className = "MailImportManager.";
    private static final MailImportManager instance = new MailImportManager();
    private static final LockManager lockManager = LockManager.getInstance();
    /**
     * Конструктор по умолчанию
     */
    private MailImportManager() {
    }

    /**
     * Возвращает экземпляр текущего класса
     *
     * @return Экземпляр MailImportManager
     */
    protected static MailImportManager getInstance() {
        return instance;
    }

    /**
     * Редактирование правила импорта
     *
     * @param mailImportId  ID правила, которое редактируем
     * @param name          Название правила
     * @param keywords      Ключевые слова
     * @param searchIn      Где искать ключавые слова
     * @param order         Порядок
     * @param categoryId    ID категории, которая используется для создания задачи
     * @param ownerId       ID пользователя, который создал правило
     * @param mstatusId     ID типа сообщений с использованием которого создаются испортируемые сообщения
     * @param domain        Домен, с которого принимается почта
     * @param active        Активно правило или нет
     * @param importUnknown Испортировать ли почту с неизвесных адресов
     * @throws GranException при необходимости
     */
    public void updateMailImport(String mailImportId, SafeString name, SafeString keywords, int searchIn, int order, String categoryId,
                                 String ownerId, String mstatusId, SafeString domain, boolean active, boolean importUnknown) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            MailImport obj = KernelManager.getFind().findMailImport(mailImportId);
            obj.setKeywords(keywords != null ? keywords.toString() : null);
            obj.setName(name != null ? name.toString() : null);
            if (obj.getOwner() == null && ownerId != null)
                obj.setOwner(ownerId);
            obj.setDomain(domain != null ? domain.toString() : null);
            obj.setSearchIn(searchIn);
            obj.setOrder(order);
            obj.setCategory(categoryId);
            mstatusId = mstatusId == null || mstatusId.isEmpty() ? null : mstatusId;
            obj.setMstatus(mstatusId);
            obj.setActive(active ? 1 : 0);
            obj.setImportUnknown(importUnknown ? 1 : 0);
            hu.updateObject(obj);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Редактирование правила импорта
     *
     * @param name          Название правила
     * @param taskId        ID задачи, которая будет родительской для импортируемых
     * @param keywords      Ключевые слова
     * @param searchIn      Где искать ключавые слова
     * @param order         Порядок
     * @param categoryId    ID категории, которая используется для создания задачи
     * @param ownerId       ID пользователя, который создал правило
     * @param mStatusId     ID типа сообщений с использованием которого создаются испортируемые сообщения
     * @param domain        Домен, с которого принимается почта
     * @param active        Активно правило или нет
     * @param importUnknown Испортировать ли почту с неизвесных адресов
     * @return ID созданного правила
     * @throws GranException при необходимости
     */
    public String createMailImport(SafeString name, String taskId, SafeString keywords, int searchIn, int order, String categoryId,
                                   String mStatusId, String ownerId, SafeString domain, boolean active, boolean importUnknown) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            return hu.createObject(new MailImport(name != null ? name.toString() : null, keywords != null ? keywords.toString() : null, searchIn, order,
                    categoryId, mStatusId, taskId, ownerId, domain != null ? domain.toString() : null, active ? 1 : 0, importUnknown ? 1 : 0));
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Удаляет правило импорта
     *
     * @param smi ID правила, которое удаляем
     * @throws GranException при необходимости
     */
    public void deleteMailImport(String smi) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            hu.deleteObject(MailImport.class, smi);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает список доступных правил импорта почтовых сообщений для задачи
     *
     * @param sc сессия пользователя для проверки доступа в задачам
     * @return список правил импорта
     * @throws GranException при необходимости
     * @see com.trackstudio.model.MailImport
     */
    public List<MailImport> getAllAvailableMailImportList(SessionContext sc) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            ArrayList<MailImport> result = new ArrayList<MailImport>();
            ArrayList<MailImport> list = (ArrayList<MailImport>) hu.getList("from com.trackstudio.model.MailImport m ");
            for (MailImport mi : list)
                if (sc.allowedByACL(mi.getTask().getId()))
                    result.add(mi);
            return result;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает все правила импорта почтовых сообщений для всех задач.
     *
     * @return список всех правил импорта
     * @throws GranException при необходимости
     * @see com.trackstudio.model.MailImport
     */
    public List<MailImport> getAllMailImports() throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            return hu.getList("from com.trackstudio.model.MailImport m order by m.order");
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }
}
