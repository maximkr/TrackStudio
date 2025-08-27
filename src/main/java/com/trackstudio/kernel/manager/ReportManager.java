/*
 * @(#)ReportManager.java
 * Copyright 2009 TrackStudio, Inc. All rights reserved.
 */
package com.trackstudio.kernel.manager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.filter.TaskFValue;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.TaskRelatedInfo;
import com.trackstudio.kernel.cache.TaskRelatedManager;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.model.Fvalue;
import com.trackstudio.model.Report;

import net.jcip.annotations.Immutable;

/**
 * Класс ReportManager содержит методы для работы с отчетами
 */
@Immutable
public class ReportManager extends KernelManager {

    private static final String className = "ReportManager.";
    private static final ReportManager instance = new ReportManager();
    private static final Log log = LogFactory.getLog(ReportManager.class);
    private static final LockManager lockManager = LockManager.getInstance();
    /**
     * Конструктор по умолчанию
     */
    private ReportManager() {
    }

    /**
     * Возвращает экземпляр текущего класса
     *
     * @return Экземпляр ReportManager
     */
    protected static ReportManager getInstance() {
        return instance;
    }

    /**
     * Возвращает список отчетов для указанных задачи и пользователя
     *
     * @param taskId ID задачи, для которой получаются отчеты
     * @param userId ID пользователя, для которого получаются отчеты
     * @return список отчетов
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Report
     */
    public Set<Report> getReportList(String taskId, String userId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            Set<Report> result = new HashSet<Report>();
            ArrayList<TaskRelatedInfo> l = TaskRelatedManager.getInstance().getTaskRelatedInfoChain(null, taskId);
            for (TaskRelatedInfo it : l) {
                result.addAll(hu.getList("from com.trackstudio.model.Report r where (r.priv=0 or (r.priv=1 and r.owner=?)) and r.task=?", userId, it.getId()));
            }
            return result;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает список всех отчетов для указанных задачи и пользователя
     *
     * @param taskId ID задачи, для которой получаются отчеты
     * @param userId ID пользователя, для которого получаются отчеты
     * @return список отчетов
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Report
     */
    public Set<Report> getAllReportList(String taskId, String userId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            Set<Report> result = new HashSet<Report>();
            List<Report> l = hu.getList("from com.trackstudio.model.Report r where (r.priv=0 or (r.priv=1 and r.owner=?) )", userId);
            for (Report rep : l) {
                if (TaskRelatedManager.getInstance().hasPath(taskId, rep.getTask().getId())) {
                    result.add(rep);
                }
            }
            return result;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает настройки фильтра для отчета
     *
     * @param reportId ID отчета
     * @return настройки фильтра для отчета
     * @throws GranException при необходимости
     * @see com.trackstudio.app.filter.TaskFValue
     */
    public TaskFValue getFValue(String reportId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        lockManager.getLock(reportId).lock();
        try {
            TaskFValue map = new TaskFValue();
            List<Fvalue> l = hu.getList("select fvalue.id from com.trackstudio.model.Fvalue as fvalue, com.trackstudio.model.Report as report where fvalue.filter=report.filter and report.id=?", reportId);
            for (Fvalue fv : l) {
                String key = fv.getKey();
                String val = fv.getValue();
                map.putItem(key, val);
            }
            return map;
        } finally {
            if (r) lockManager.releaseConnection(className);
            lockManager.getLock(reportId).unlock();

        }
    }

    /**
     * Удаляется отчет по его ID
     *
     * @param reportId ID отчета, который удаляется
     * @throws GranException при необходимости
     */
    public void deleteReport(String reportId) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        lockManager.getLock(reportId).lock();
        try {
            hu.deleteObject(Report.class, reportId);
        } finally {
            if (w) lockManager.releaseConnection(className);
            lockManager.getLock(reportId).unlock();
        }
    }

    /**
     * Редактируется параметры для отчета
     *
     * @param reportId ID отчета, параметры которого обновляются
     * @param params   параметры
     * @throws GranException при необходимости
     */
    public void updateReportParams(String reportId, String params) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        lockManager.getLock(reportId).lock();
        try {
            Report obj = KernelManager.getFind().findReport(reportId);
            obj.setParams(params);
            hu.updateObject(obj);
        } finally {
            if (w) lockManager.releaseConnection(className);
            lockManager.getLock(reportId).unlock();
        }
    }

    /**
     * Редактируется отчет
     *
     * @param reportId ID отчета, который редактируется
     * @param name     Название отчета
     * @param priv     Приватный или публичный отчет
     * @param rtypeId  Тип отчета
     * @param filterId ID фильтра для отчета
     * @throws GranException при необходимости
     */
    public void updateReport(String reportId, SafeString name, boolean priv, String rtypeId, String filterId) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        lockManager.getLock(reportId).lock();

        try {
            Report obj = KernelManager.getFind().findReport(reportId);
            obj.setName(name != null ? name.toString() : null);
            obj.setRtype(rtypeId);
            obj.setPriv(priv ? 1 : 0);
            obj.setFilter(filterId);
            hu.updateObject(obj);
        } finally {
            if (w) lockManager.releaseConnection(className);
            lockManager.getLock(reportId).unlock();
        }
    }

    /**
     * Создается отчет
     *
     * @param name     Название отчета
     * @param rtypeId  Тип отчета
     * @param priv     Приватный или публичный отчет
     * @param filterId ID фильтра для отчета
     * @param taskId   ID задачи, для которой создается отчет
     * @param ownerId  ID пользователя, Владелец отчета
     * @return ID созданного отчета
     * @throws GranException при необходимости
     */
    public String createReport(SafeString name, String rtypeId, boolean priv, String filterId, String taskId, String ownerId) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            return hu.createObject(new Report(name != null ? name.toString() : null, priv, rtypeId, filterId, taskId, ownerId));
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает список отчетов
     *
     * @param filterId ID фильтра
     * @return Список отчетов
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Report
     */
    protected List<Report> getFilterReportList(String filterId) throws GranException {
        return hu.getList("from com.trackstudio.model.Report as r where r.filter=?", filterId);
    }
}
