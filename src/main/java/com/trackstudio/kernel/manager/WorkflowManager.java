/*
 * @(#)WorkflowManager.java
 * Copyright 2009 TrackStudio, Inc. All rights reserved.
 */
package com.trackstudio.kernel.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.constants.StateConstants;
import com.trackstudio.constants.WorkflowConstants;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.CategoryCacheManager;
import com.trackstudio.kernel.cache.MprstatusCacheManager;
import com.trackstudio.kernel.cache.TaskRelatedInfo;
import com.trackstudio.kernel.cache.TaskRelatedManager;
import com.trackstudio.kernel.cache.UDFCacheItem;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.model.Category;
import com.trackstudio.model.Mprstatus;
import com.trackstudio.model.Mstatus;
import com.trackstudio.model.Priority;
import com.trackstudio.model.Resolution;
import com.trackstudio.model.Status;
import com.trackstudio.model.Transition;
import com.trackstudio.model.Trigger;
import com.trackstudio.model.Workflow;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.EggBasket;
import com.trackstudio.tools.ExternalAdapterManagerUtil;
import com.trackstudio.tools.Null;

import net.jcip.annotations.Immutable;

/**
 * Класс WorkflowManager содержит методы для работы с процессами
 */
@Immutable
public class WorkflowManager extends KernelManager {

    private static final String className = "WorkflowManager.";
    private static final WorkflowManager instance = new WorkflowManager();
    private static final Log log = LogFactory.getLog(WorkflowManager.class);
    private static final LockManager lockManager = LockManager.getInstance();
    /**
     * Конструктор по умолчанию
     */
    private WorkflowManager() {
    }

    /**
     * Возвращает экземпляр текущего класса
     *
     * @return Экземпляр WorkflowManager
     */
    protected static WorkflowManager getInstance() {
        return instance;
    }

    /**
     * Редактирует тип сообщения
     *
     * @param mstatusId   ID типа сообщения, который редактируем
     * @param name        Название типа сообщения
     * @param description Описание типа сообщения
     * @param preferences Настройки типа сообщения
     * @param action      Описание действия, которое выполняет тип сообщения
     * @throws GranException при необходимости
     *//**
     * Редактирует тип сообщения
     *
     * @param mstatusId   ID типа сообщения, который редактируем
     * @param name        Название типа сообщения
     * @param description Описание типа сообщения
     * @param preferences Настройки типа сообщения
     * @param action      Описание действия, которое выполняет тип сообщения
     * @throws GranException при необходимости
     */
    public void updateMstatus(String mstatusId, SafeString name, SafeString description, String preferences, SafeString action) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            Mstatus mstatus = KernelManager.getFind().findMstatus(mstatusId);
            mstatus.setName(name != null && name.length() != 0 ? name.toString() : null);
            mstatus.setDescription(description != null ? description.toString() : null);
            mstatus.setAction(action != null ? action.toString() : null);
            mstatus.setPreferences(preferences);
            hu.updateObject(mstatus);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Создается приоритет
     *
     * @param name        Название приоритета
     * @param description Описание приоритета
     * @param order       Порядок приоритета
     * @param isdefault   Является ли приоритет по умолчанию
     * @param workflowId  ID процесса, для которого создан приоритет
     * @return ID созданного приоритета
     * @throws GranException при необходимости
     */
    public String createPriority(SafeString name, SafeString description, int order, boolean isdefault, String workflowId) throws GranException {
        log.trace("createPriority(name='" + name + "')");
        boolean w = lockManager.acquireConnection(className);
        try {
            Integer priorityOrder = null;
            try {
                priorityOrder = order;
            } catch (Exception er) {
                priorityOrder = 1;
            }
            return hu.createObject(new Priority(name != null ? name.toString() : null, description != null ? description.toString() : null, priorityOrder, isdefault, workflowId));
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает список доступных процессов для задачи
     *
     * @param taskId ID задачи, для которой возвращается список процессов
     * @return список процессов
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Workflow
     */
    public List<Workflow> getAvailableWorkflowList(String taskId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            ArrayList<Workflow> result = new ArrayList<Workflow>();
            for (TaskRelatedInfo it : TaskRelatedManager.getInstance().getTaskRelatedInfoChain(null, taskId))
                result.addAll(hu.getList("select w from com.trackstudio.model.Workflow w where w.task=?", it.getId()));
            return result;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает список всех доступных процессов для задачи
     *
     * @param taskId ID задачи, для которой возвращается список процессов
     * @return список процессов
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Workflow
     */
    public List<Workflow> getAllAvailableWorkflowList(String taskId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            ArrayList<Workflow> result = new ArrayList<Workflow>();
            List<Workflow> list = hu.getList("select w from com.trackstudio.model.Workflow w");
            for (Workflow w : list) {
                if (TaskRelatedManager.getInstance().hasPath(taskId, w.getTask().getId()))
                    result.add(w);
            }
            return result;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает список всех процессов
     *
     * @return список процессов
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Workflow
     */
    public List<Workflow> getAllWorkflowList() throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            ArrayList<Workflow> result = new ArrayList<Workflow>();
            result.addAll(hu.getList("select w from com.trackstudio.model.Workflow w"));
            return result;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Создается новый процесс
     *
     * @param taskId ID задачи, для которой создается новый процесс
     * @param name   Название процесса
     * @return ID созданного процесса
     * @throws GranException при необходимости
     */
    public String createWorkflow(String taskId, SafeString name) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            String workflowId = hu.createObject(new Workflow(name != null ? name.toString() : null, taskId));
            createState(SafeString.createSafeString(I18n.getString(StateConstants.DEFAULT_NAME)), true, false, workflowId, StateConstants.DEFAULT_COLOR);
            return workflowId;
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Удаляется процесс
     *
     * @param workflowId ID процесса, который удаляется
     * @throws GranException при необходимости
     */
    public void deleteWorkflow(String workflowId) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            hu.deleteObject(Workflow.class, workflowId);
            TaskRelatedManager.getInstance().invalidateWhenChangeWorkflow();
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает список переходов для типа сообщения
     *
     * @param mstatusId ID типа сообщения, для которого создается переход
     * @return список переходов
     * @throws GranException при необходимости
     * @see org.hibernate.Transaction
     */
    public List<Transition> getTransitionList(String mstatusId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            return hu.getList("select transition from com.trackstudio.model.Transition as transition where transition.mstatus = ?", mstatusId);
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает список приоритетов для процесса
     *
     * @param workflowId ID процесса, для которого возвращается список приоритетов
     * @return список приоритетов
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Priority
     */
    public List<Priority> getPriorityList(String workflowId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            return hu.getList("select p from com.trackstudio.model.Priority p where p.workflow=? order by p.order asc", workflowId);
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    public boolean checkPriorityId(String workflowId, String priorityId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            return !hu.getList("select count(p.id) from com.trackstudio.model.Priority p where p.workflow=? and p.id=?", workflowId, priorityId).isEmpty();
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает список типов сообщения для процесса
     *
     * @param workflowId ID процесса, для которого возвращается список приоритетов
     * @return список приоритетов
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Mstatus
     */
    public List<Mstatus> getMstatusList(String workflowId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            return hu.getList("select m from com.trackstudio.model.Mstatus m where m.workflow=?", workflowId);
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает список категорий для процесса
     *
     * @param workflowId ID процесса, для которого возвращается список категшорий
     * @return список категорий
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Category
     */
    public List<Category> getCategoryList(String workflowId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            return hu.getList("select c from com.trackstudio.model.Category c where c.workflow=? order by c.name",  workflowId);
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Редактируется резолюция
     *
     * @param resolutionId ID резолюции, которая редактируется
     * @param name         Название резолюции
     * @param isdefault    По умолчанию ли резолюция
     * @throws GranException при необходимости
     */
    public void updateResolution(String resolutionId, SafeString name, boolean isdefault) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            Resolution res = KernelManager.getFind().findResolution(resolutionId);
            res.setName(name != null ? name.toString() : null);
            res.setIsdefault(isdefault ? 1 : 0);
            hu.updateObject(res);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Создается резолюция
     *
     * @param mstatusId ID типа сообщения, для которого создается резолюция
     * @param name      Название резолюции
     * @param isdefault По умолчанию ли резолюция
     * @return ID созданной резолюции
     * @throws GranException при необходимости
     */
    public String createResolution(String mstatusId, SafeString name, boolean isdefault) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            return hu.createObject(new Resolution(name != null ? name.toString() : null, mstatusId, isdefault));
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Удляляется переход
     *
     * @param transitionId ID перехода, который удаляется
     * @throws GranException при необходимости
     */
    public void deleteTransition(String transitionId) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            hu.deleteObject(Transition.class, transitionId);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Создается переход
     *
     * @param mstatusId      ID типа сообщения для которого создается переход
     * @param startStatusId  ID начального состояния
     * @param finishStatusId ID конечного состояния
     * @throws GranException при необходимости
     */
    public void createTransition(String mstatusId, String startStatusId, String finishStatusId) throws GranException {
        hu.createObject(new Transition(startStatusId, finishStatusId, mstatusId));
    }

    /**
     * Редактируется переход
     *
     * @param mstatusId      ID типа сообщения для которого редактируется переход
     * @param startStatusId  ID начального состояния
     * @param finishStatusId ID конечного состояния
     * @throws GranException при необходимости
     */
    public void updateTransition(String mstatusId, String startStatusId, String finishStatusId) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            boolean needCreate = false;
            List<Transition> tmpList = hu.getList("select t from com.trackstudio.model.Transition t where t.mstatus=? and t.start=?", mstatusId, startStatusId);
            if (tmpList == null || tmpList.isEmpty()) {
                needCreate = true;
            } else {
                Transition transition = tmpList.get(0);
                transition.setFinish(finishStatusId);
                hu.updateObject(transition);
            }
            if (needCreate) {
                KernelManager.getWorkflow().createTransition(mstatusId, startStatusId, finishStatusId);
            }
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Удаляется резолюция
     *
     * @param resolutionId ID резолюции, которая удаляется
     * @throws GranException при необходимости
     */
    public void deleteResolution(String resolutionId) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            hu.deleteObject(Resolution.class, resolutionId);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Удаляется тип сообщения
     *
     * @param mstatusId ID удаляемого типа сообщения
     * @throws GranException при необходимости
     */
    public void deleteMstatus(String mstatusId) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            MprstatusCacheManager.getInstance().invalidateRemoveMstatus(mstatusId);
            hu.deleteObject(Mstatus.class, mstatusId);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Создается тип сообщения
     *
     * @param workflowId  ID процесса, для которого создается тип сообщения
     * @param name        Название типа сообщения
     * @param description Описание типа сообщения
     * @param preferences показывать на панели или нет
     * @return ID созданного типа сообщения
     * @throws GranException при необходимости
     */
    public String createMstatus(String workflowId, SafeString name, SafeString description, String preferences) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            return hu.createObject(new Mstatus(name != null ? name.toString() : null, description != null ? description.toString() : null, workflowId, preferences != null ? preferences : null));
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Редактируется состояние
     *
     * @param statusId ID состояния, которое редактируется
     * @param name     Название состояния
     * @param start    Является ли состояние начальным
     * @param finish   Является ли состояние конечным
     * @param color    Цвет состояния
     * @throws GranException при необходимости
     */
    public void updateState(String statusId, SafeString name, boolean start, boolean finish, String color) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            Status obj = KernelManager.getFind().findStatus(statusId);
            List<Status> stateList = KernelManager.getWorkflow().getStateList(obj.getWorkflow().getId());
            boolean secondaryStart = false;
            for (Status status : stateList) {
                if (status.isStart() && !status.isSecondaryStart() && !statusId.equals(status.getId())) {
                    secondaryStart = true;
                    break;
                }
            }
            if (start) {
                if (secondaryStart) {
                    obj.makeSecondaryStart();
                } else {
                    obj.makeStart();
                }
            } else {
                obj.resetStart();
            }

            if (finish)
                obj.makeFinish();
            else
                obj.resetFinish();
            obj.setName(name != null ? name.toString() : null);
            obj.setColor(checkColor(color));
            for (Object o : obj.getWorkflow().getCategorySet()) {
                CategoryCacheManager.getInstance().invalidateCategoryIsValid(((Category) o).getId());
            }
            hu.updateObject(obj);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Удаляется состояние
     *
     * @param statusId ID удаляемого состояния
     * @throws GranException при необходимости
     */
    public void deleteState(String statusId) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            Status obj = KernelManager.getFind().findStatus(statusId);
            for (Object o : obj.getWorkflow().getCategorySet()) {
                CategoryCacheManager.getInstance().invalidateCategoryIsValid(((Category) o).getId());
            }
            hu.deleteObject(Status.class, statusId);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Создается состояние
     *
     * @param name       Название состояния
     * @param start      Является ли состояние начальным
     * @param finish     Является ли состояние конечным
     * @param workflowId ID процесса, для которого создается состояние
     * @param color      Цвет состояния
     * @return ID созданного состояния
     * @throws GranException при необходимости
     */
    public String createState(SafeString name, boolean start, boolean finish, String workflowId, String color) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            Workflow obj = KernelManager.getFind().findWorkflow(workflowId);
            for (Object o : obj.getCategorySet()) {
                CategoryCacheManager.getInstance().invalidateCategoryIsValid(((Category) o).getId());
            }
            return hu.createObject(new Status(name != null ? name.toString() : null, start ? 1 : 0,
                    finish ? 1 : 0, workflowId, checkColor(color)));
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает список состояний для процесса
     *
     * @param workflowId ID процесса, для которого возвращается список состояний
     * @return список состояний
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Status
     */
    public List<Status> getStateList(String workflowId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            return hu.getList("select s from com.trackstudio.model.Status s where s.workflow=?", workflowId);
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Редактируется название процесса
     *
     * @param workflowId ID процесса, название которого редактируется
     * @param name       Название процесса
     * @throws GranException при необходимости
     */
    public void updateWorkflowName(String workflowId, SafeString name) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            Workflow obj = KernelManager.getFind().findWorkflow(workflowId);
            obj.setName(name != null ? name.toString() : null);
            hu.updateObject(obj);
            TaskRelatedManager.getInstance().invalidateWhenChangeWorkflow();
            CategoryCacheManager.getInstance().invalidate();
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Редактируется приоритет
     *
     * @param priorityId  ID приоритета, который редактируется
     * @param name        Название приоритета
     * @param description Описание приоритета
     * @param order       Порядок приоритета
     * @param isDefault   Является ли приоритет по умолчанию
     * @throws GranException при необходимости
     */
    public void updatePriority(String priorityId, SafeString name, SafeString description, int order, boolean isDefault) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            Priority obj = KernelManager.getFind().findPriority(priorityId);
            obj.setName(name != null ? name.toString() : null);
            obj.setDescription(description != null ? description.toString() : null);
            obj.setOrder(order);
            if (isDefault)
                obj.setDefault();
            else
                obj.unsetDefault();
            hu.updateObject(obj);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Удаляется приоритет
     *
     * @param priorityId ID приоритета, который удаляется
     * @throws GranException при необходимости
     */
    public void deletePriority(String priorityId) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            hu.deleteObject(Priority.class, priorityId);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает ID начально8го состояния для процесса
     *
     * @param workflowId ID процесса
     * @return ID состояния
     * @throws GranException при необходимости
     */
    public String getStartStateId(String workflowId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            List<String> list = hu.getList("select s.id from com.trackstudio.model.Status s where s.workflow=? and (s.isstart=1 or s.isstart=2) order by s.isstart", workflowId);
            if (list.isEmpty())
                return null;
            else
                return list.get(0);
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает список пользовательских полей для процесса
     *
     * @param workflowId ID процесса
     * @return список полей
     * @throws GranException при необходимости
     * @see com.trackstudio.kernel.cache.UDFCacheItem
     */
    public List<UDFCacheItem> getUDFs(String workflowId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            return KernelManager.getUdf().getListWorkflowUDFCacheItem(workflowId);
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает список резолюций для типа сообщения
     *
     * @param mstatusId ID типа сообщения
     * @return список резолюций
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Resolution
     */
    public List<Resolution> getResolutionList(String mstatusId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            return hu.getList("select r from com.trackstudio.model.Resolution r where r.mstatus=?", mstatusId);
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Проверяет корректность цвета
     *
     * @param color цвет
     * @return TRUE - если корректно, FALSE - если нет
     */
    private String checkColor(String color) {
        if (color == null || color.length() < 4)
            return StateConstants.DEFAULT_COLOR;
        if (color.charAt(0) == '#')
            color = color.substring(1, color.length());
        if (color.length() != 6)
            return StateConstants.DEFAULT_COLOR;
        color = color.toLowerCase(Locale.ENGLISH);
        String goodChars = "0123456789abcdef";
        for (int i = 0; i < color.length(); i++) {
            if (goodChars.indexOf((int) color.charAt(i)) == -1)
                return StateConstants.DEFAULT_COLOR;
        }
        return '#' + color;
    }

    /**
     * Задает права доступа статуса к типа сообщения
     *
     * @param access     Вид доступа
     * @param prstatusId ID статуса
     * @param mstatusId  ID типа сообщения
     * @throws GranException при необходимости
     */
    public void grant(String access, String prstatusId, String mstatusId) throws GranException {
        if (mstatusId != null && access != null && access.length() != 0) {
            Mstatus obj = KernelManager.getFind().findMstatus(mstatusId);
            for (Object o : obj.getWorkflow().getCategorySet()) {
                CategoryCacheManager.getInstance().invalidateCategoryIsValid(((Category) o).getId());
            }
            hu.createObject(new Mprstatus(access, mstatusId, prstatusId));
        }
    }

    /**
     * Задает права доступа для списка типов сообщений
     *
     * @param prstatusId  ID статуса
     * @param mstatusList список типов сообщений
     * @throws GranException при необходимости
     */
    public void grantMap(String prstatusId, List<String> mstatusList) throws GranException {
        List<Object> objs = new ArrayList<Object>();
        for (String mstatusId : mstatusList) {
            EggBasket<String, String> rules = ExternalAdapterManagerUtil.getMprstatusMap(mstatusId);
            List<String> accessList = rules.get(prstatusId);
            if (accessList != null) {
                for (String access : accessList) {
                    if (mstatusId != null && access != null && access.length() != 0) {
                        Mstatus obj = KernelManager.getFind().findMstatus(mstatusId);
                        for (Object o : obj.getWorkflow().getCategorySet()) {
                            CategoryCacheManager.getInstance().invalidateCategoryIsValid(((Category) o).getId());
                        }
                        objs.add(new Mprstatus(access, mstatusId, prstatusId));
                    }
                }
            }
        }
        hu.createObjects(objs);
    }

    /**
     * Удаляет права доступа для указанного статуса на тип сообщения
     *
     * @param mstatusId  ID типа сообщения
     * @param prstatusId ID статуса
     * @param types      Виды доступа
     * @throws GranException при необходимости
     */
    private void revoke(String mstatusId, String prstatusId, String[] types) throws GranException {
        try {
            List list;
            //dnikitin: � ��� mstatusId ����� ���� null (and m.mstatus is null), ���� ��� � ��� NOT NULL?
            if (mstatusId == null) {
                Map<String, String> paramList = new LinkedHashMap<String, String>();
                paramList.put("prstatus", prstatusId);
                Map<String, Collection> paramMap = new LinkedHashMap<String, Collection>();
                paramMap.put("types", Arrays.asList(types));
                hu.executeDMLListMap("delete from com.trackstudio.model.Mprstatus m where m.prstatus=:prstatus and m.mstatus is null and m.type in (:types)", paramList, paramMap);
            } else {
                Map<String, String> paramList = new LinkedHashMap<String, String>();
                paramList.put("prstatus", prstatusId);
                paramList.put("mstatus", mstatusId);
                Map<String, Collection> paramMap = new LinkedHashMap<String, Collection>();
                paramMap.put("types", Arrays.asList(types));
                hu.executeDMLListMap("delete from com.trackstudio.model.Mprstatus m where m.prstatus=:prstatus and m.mstatus=:mstatus and m.type in (:types)", paramList, paramMap);
            }
            if (mstatusId != null) {
                Mstatus obj = KernelManager.getFind().findMstatus(mstatusId);
                for (Object o : obj.getWorkflow().getCategorySet()) {
                    CategoryCacheManager.getInstance().invalidateCategoryIsValid(((Category) o).getId());
                }
                MprstatusCacheManager.getInstance().invalidateSingle(prstatusId, mstatusId);
            }
            hu.cleanSession();
        } catch (Exception e) {
            throw new GranException(e);
        }
    }

    /**
     * Задает права доступа на просмотр статуса к типа сообщения
     *
     * @param access     Вид доступа
     * @param prstatusId ID статуса
     * @param mstatusId  ID типа сообщения
     * @throws GranException при необходимости
     */
    public void grantView(String access, String prstatusId, String mstatusId) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            revoke(mstatusId, prstatusId, new String[]{WorkflowConstants.VIEW_ALL, WorkflowConstants.VIEW_HANDLER, WorkflowConstants.VIEW_SUBMITTER, WorkflowConstants.VIEW_SUBMITTER_AND_HANDLER});
            grant(access, prstatusId, mstatusId);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Задает права доступа на выполнение статуса к типа сообщения
     *
     * @param access     Вид доступа
     * @param prstatusId ID статуса
     * @param mstatusId  ID типа сообщения
     * @throws GranException при необходимости
     */
    public void grantProcess(String access, String prstatusId, String mstatusId) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            revoke(mstatusId, prstatusId, new String[]{WorkflowConstants.PROCESS_ALL, WorkflowConstants.PROCESS_HANDLER, WorkflowConstants.PROCESS_SUBMITTER, WorkflowConstants.PROCESS_SUBMITTER_AND_HANDLER});
            grant(access, prstatusId, mstatusId);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Задает права доступа быть ответственным статуса к типа сообщения
     *
     * @param access     Вид доступа
     * @param prstatusId ID статуса
     * @param mstatusId  ID типа сообщения
     * @throws GranException при необходимости
     */
    public void grantBeHandler(String access, String prstatusId, String mstatusId) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        lockManager.getLock(mstatusId).lock();
        try {
            revoke(mstatusId, prstatusId, new String[]{WorkflowConstants.BE_HANDLER_ALL, WorkflowConstants.BE_HANDLER_HANDLER, WorkflowConstants.BE_HANDLER_SUBMITTER, WorkflowConstants.BE_HANDLER_SUBMITTER_AND_HANDLER});
            grant(access, prstatusId, mstatusId);
        } finally {
            if (w) lockManager.releaseConnection(className);
            lockManager.getLock(mstatusId).unlock();
        }
    }

    public void removeBeMstatusByPrstatus(String prstatusId, String mstatusId) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        lockManager.getLock(mstatusId).lock();
        try {
            revoke(mstatusId, prstatusId, new String[]{WorkflowConstants.VIEW_ALL, WorkflowConstants.VIEW_HANDLER, WorkflowConstants.PROCESS_SUBMITTER, WorkflowConstants.VIEW_SUBMITTER_AND_HANDLER});
            revoke(mstatusId, prstatusId, new String[]{WorkflowConstants.PROCESS_ALL, WorkflowConstants.PROCESS_HANDLER, WorkflowConstants.PROCESS_SUBMITTER, WorkflowConstants.PROCESS_SUBMITTER_AND_HANDLER});
            revoke(mstatusId, prstatusId, new String[]{WorkflowConstants.BE_HANDLER_ALL, WorkflowConstants.BE_HANDLER_HANDLER, WorkflowConstants.BE_HANDLER_SUBMITTER, WorkflowConstants.BE_HANDLER_SUBMITTER_AND_HANDLER});
        } finally {
            if (w) lockManager.releaseConnection(className);
            lockManager.getLock(mstatusId).unlock();
        }
    }


    /**
     * Создает копию процесса (клонирует его) со всеми состояниями, приоритетами, типа сообщений, резолючиями и т.д.
     *
     * @param workflowId ID копируемого процесса
     * @param taskId     ID задачи
     * @param locale     Локаль пользователя - необходимос для переименования
     * @return ID созаднного процесса
     * @throws GranException при необходимости
     */
    public String cloneWorkflow(String workflowId, String taskId, String locale) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            Workflow wf = KernelManager.getFind().findWorkflow(workflowId);
            String name = wf.getName();
            Workflow wfCopy = new Workflow(name.endsWith(' ' + I18n.getString(locale, "CLONED")) ? name : name + ' ' + I18n.getString(locale, "CLONED"), taskId);
            String wfid = hu.createObject(wfCopy);

            for (Object o : hu.getList("select priority from com.trackstudio.model.Priority as priority where priority.workflow=?", workflowId)) {
                Priority p = (Priority) o;
                KernelManager.getWorkflow().createPriority(SafeString.createSafeString(p.getName()), SafeString.createSafeString(p.getDescription()), p.getOrder(), p.isDefault(), wfid);
            }

            Map<String, String> newStatusId = new HashMap<String, String>();
            for (Object o1 : hu.getList("select status from com.trackstudio.model.Status as status where status.workflow=?",
                    workflowId)) {
                Status s = (Status) o1;
                newStatusId.put(s.getId(), KernelManager.getWorkflow().createState(SafeString.createSafeString(s.getName()), s.isStart(), s.isFinish(), wfid, s.getColor()));
            }
            HashMap<String, String> newMstatusId = new HashMap<String, String>();
            List mstatuses = hu.getList("select mstatus from com.trackstudio.model.Mstatus as mstatus where mstatus.workflow=?", workflowId);
            for (Object mstatuse : mstatuses) {
                Mstatus m = (Mstatus) mstatuse;
                String mstatusId = m.getId();
                Trigger tr = m.getTrigger();
                if (tr != null) tr = FindManager.getFind().findTrigger(tr.getId());
                String before = tr == null || tr.getBefore() == null ? null : tr.getBefore();
                String insteadOf = tr == null || tr.getInsteadOf() == null ? null : tr.getInsteadOf();
                String after = tr == null || tr.getAfter() == null ? null : tr.getAfter();
	            final String mID = hu.createObject(new Mstatus(m.getName(), m.getDescription(), m.getAction(), wfid, m.getPreferences()));
                newMstatusId.put(mstatusId, mID);
                if (tr != null) {
                    setMstatusTrigger(mID, SafeString.createSafeString(before), SafeString.createSafeString(insteadOf), SafeString.createSafeString(after));
                }
                for (Object o : hu.getList("select resolution from com.trackstudio.model.Resolution as resolution where resolution.mstatus=?", mstatusId)) {
                    Resolution r = (Resolution) o;
                    KernelManager.getWorkflow().createResolution(mID, SafeString.createSafeString(r.getName()), r.isDefault());
                }
                for (Object o : hu.getList("select transition from com.trackstudio.model.Transition as transition where transition.mstatus=?", mstatusId)) {
                    Transition t = (Transition) o;
                    KernelManager.getWorkflow().updateTransition(mID, newStatusId.get(t.getStart().getId()), newStatusId.get(t.getFinish().getId()));
                }
                for (Object o : hu.getList("select mprstatus from com.trackstudio.model.Mprstatus as mprstatus where mprstatus.mstatus=?", mstatusId)) {
                    Mprstatus mps = (Mprstatus) o;
                    grant(mps.getType(), mps.getPrstatus().getId(), mID);
                }
                for (Object o : hu.getList("select transition from com.trackstudio.model.Transition as transition where transition.mstatus=?", mstatusId)) {
                    Transition t = (Transition) o;
                    KernelManager.getWorkflow().updateTransition(mID, newStatusId.get(t.getStart().getId()), newStatusId.get(t.getFinish().getId()));
                }
            }
            List<UDFCacheItem> udfs = KernelManager.getWorkflow().getUDFs(workflowId);
            for (UDFCacheItem udf1 : udfs) {
                KernelManager.getUdf().cloneUdf(wfid, udf1.getId(), UdfManager.WORKFLOW, newMstatusId);
            }
            TaskRelatedManager.getInstance().invalidateWhenChangeWorkflow();
            return wfid;
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Устанавливает триггеры для типа сообщения
     *
     * @param mstatusId ID типа сообщения
     * @param before    before-триггер
     * @param insteadOf instanseOf-триггер
     * @param after     after-триггер
     * @throws GranException при необходимости
     */
    public void setMstatusTrigger(String mstatusId, SafeString before, SafeString insteadOf, SafeString after) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            Mstatus mstatus = (Mstatus) hu.getObject(Mstatus.class, mstatusId);
            Trigger trg;
            if (mstatus.getTrigger() != null) {
                trg = mstatus.getTrigger();
            } else {
                trg = new Trigger();
                hu.createObject(trg);
                hu.cleanSession();
            }
            trg.setBefore(Null.isNotNull(before) ? before.toString() : null);
            trg.setInsteadOf(Null.isNotNull(insteadOf) ? insteadOf.toString() : null);
            trg.setAfter(Null.isNotNull(after) ? after.toString() : null);
            mstatus.setTrigger(trg);
            hu.updateObject(mstatus);
            hu.cleanSession();
        } catch (Exception e) {
            e.printStackTrace();
            throw new GranException(e);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Проверяет процесс на валидность
     *
     * @param workflowId ID процесса, который проверяем на валидность
     * @return TRUE - если валидный, FALSE - если нет
     * @throws GranException при необходимости
     */
    public Boolean getWorkflowIsValid(String workflowId) throws GranException {
        //��������� �� ��������� ���������
        if (getStartStateId(workflowId) == null)
            return false;
        //����� ��� ������� � ��������� ������������ ���� � ���
        if (!getValidPermissions(workflowId))
            return false;
        return true;

    }

    /**
     * Проверяет на правильность установки прав для процесса
     *
     * @param workflowId ID процесса, для которого проверяются права
     * @return TRUE - если права установлены верно, FALSE - если нет
     * @throws GranException при необзодимости
     */
    public boolean getValidPermissions(String workflowId) throws GranException {
        List<Mstatus> mstatusList = KernelManager.getWorkflow().getMstatusList(workflowId);
        for (Mstatus smsb : mstatusList) {
            EggBasket<String, String> rules = ExternalAdapterManagerUtil.getMprstatusMap(smsb.getId());
            Set<String> prstatusList = rules.keySet();
            for (String prstatus : prstatusList) {
                List<String> types = rules.get(prstatus);
                if (types != null) {
                    boolean viewAll = false;
                    boolean viewS = false;
                    boolean viewH = false;
                    boolean viewSAH = false;
                    if (types.contains(WorkflowConstants.VIEW_ALL)) {
                        viewAll = true;
                    } else if (types.contains(WorkflowConstants.VIEW_SUBMITTER)) {
                        viewS = true;
                    } else if (types.contains(WorkflowConstants.VIEW_HANDLER)) {
                        viewH = true;
                    } else if (types.contains(WorkflowConstants.VIEW_SUBMITTER_AND_HANDLER)) {
                        viewSAH = true;
                    }

                    if ((types.contains(WorkflowConstants.PROCESS_ALL) && !viewAll) ||
                            (types.contains(WorkflowConstants.PROCESS_SUBMITTER) && !viewAll && !viewS && !viewSAH) ||
                            (types.contains(WorkflowConstants.PROCESS_HANDLER) && !viewAll && !viewH && !viewSAH) ||
                            (types.contains(WorkflowConstants.PROCESS_SUBMITTER_AND_HANDLER) && !viewAll && !viewSAH)) {

                        return false;
                    }
                }
            }
        }
        return true;
    }

}
