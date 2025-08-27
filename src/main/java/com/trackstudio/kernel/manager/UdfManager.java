/*
 * @(#)UdfManager.java
 * Copyright 2009 TrackStudio, Inc. All rights reserved.
 */
package com.trackstudio.kernel.manager;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.UdfValue;
import com.trackstudio.constants.UdfConstants;
import com.trackstudio.exception.GranException;
import com.trackstudio.exception.UserException;
import com.trackstudio.kernel.cache.TaskRelatedInfo;
import com.trackstudio.kernel.cache.TaskRelatedManager;
import com.trackstudio.kernel.cache.UDFCacheItem;
import com.trackstudio.kernel.cache.UdfvalCacheItem;
import com.trackstudio.kernel.cache.UprstatusCacheManager;
import com.trackstudio.kernel.cache.UserRelatedManager;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.model.Task;
import com.trackstudio.model.Udf;
import com.trackstudio.model.Udflist;
import com.trackstudio.model.Udfsource;
import com.trackstudio.model.Udfval;
import com.trackstudio.model.Umstatus;
import com.trackstudio.model.Uprstatus;
import com.trackstudio.model.User;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.Null;
import com.trackstudio.tools.Pair;
import com.trackstudio.tools.formatter.DateFormatter;
import com.trackstudio.tools.formatter.HourFormatter;

import net.jcip.annotations.Immutable;

import static com.trackstudio.tools.Null.isNotNull;

/**
 * Класс UdfManager содержит методы для работы с настраиваемыми пользовательскими полями
 */
@Immutable
public class UdfManager extends KernelManager {

    private static final Log log = LogFactory.getLog(UdfManager.class);
    private static final String className = "UdfManager.";
    private static final int EDIT = 2;
    private static final int VIEW = 4;

    public static final String WORKFLOW = "workflow";
    public static final String TASK = "task";
    public static final String USER = "user";

    private static final UdfManager instance = new UdfManager();
    private static final LockManager lockManager = LockManager.getInstance();
    /**
     * Конструктор по умолчанию
     */
    private UdfManager() {
    }

    /**
     * Возвращает экземпляр текущего класса
     *
     * @return Экземпляр UdfManager
     */
    protected static UdfManager getInstance() {
        return instance;
    }

    /**
     * Возвращает объект Udfsource определяющий пользовательские полея для задачи, пользователя или процесса
     *
     * @param id   ID задачи, пользователя или процесса, для которого получается объект
     * @param mode Параметр, определяющий для каких полей получается объект (UdfConstants.TASK_ALL, UdfConstants.USER_ALL или UdfConstants.WORKFLOW_ALL)
     * @return ID объекта Udfsource
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Udfsource
     */
    public String getUDFSource(String id, int mode) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            String query;
            if (mode == UdfConstants.TASK_ALL)
                query = "select u.id from com.trackstudio.model.Udfsource u where u.task=?";
            else if (mode == UdfConstants.USER_ALL)
                query = "select u.id from com.trackstudio.model.Udfsource u where u.user=?";
            else if (mode == UdfConstants.WORKFLOW_ALL)
                query = "select u.id from com.trackstudio.model.Udfsource u where u.workflow=?";
            else
                throw new GranException("bad type");
            List<String> tmpSet = hu.getList(query, id);
            String pk;
            if (!tmpSet.isEmpty()) {
                pk = tmpSet.iterator().next();
            } else {
                Udfsource udfsource = null;
                if (mode == UdfConstants.TASK_ALL) {
                    udfsource = new Udfsource(id, null, null);
                }
                if (mode == UdfConstants.USER_ALL) {
                    udfsource = new Udfsource(null, id, null);
                }
                if (mode == UdfConstants.WORKFLOW_ALL) {
                    udfsource = new Udfsource(null, null, id);
                }
                pk = hu.createObject(udfsource);
            }
            return pk;
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает Карту (Map) списка возможных значений UDF типа List и Multilist
     *
     * @param udfId ID пользовательского поля
     * @return Катра (Map) списка возможных значений UDF типа List и Multilist
     * @throws GranException при необходимости
     */
    public HashMap<String, String> getUdflist(String udfId) throws GranException {
        log.trace("########");
        boolean r = lockManager.acquireConnection(className);
        try {
            HashMap<String, String> result = new HashMap<String, String>();
            for (Object o : hu.getList("select ul from com.trackstudio.model.Udflist ul where ul.udf=?", udfId)) {
                Udflist ulist = (Udflist) o;
                if (ulist.getVal() != null)
                    result.put(ulist.getId(), ulist.getVal());
            }
            return result;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает список пользовательских полей для процесса
     *
     * @param workflowId ID процесса, для которого получается список пользовательских полей
     * @return список пользовательских полей
     * @throws GranException при необходимости
     * @see com.trackstudio.kernel.cache.UDFCacheItem
     */
    public List<UDFCacheItem> getListWorkflowUDFCacheItem(String workflowId) throws GranException {
        log.trace("########");
        boolean r = lockManager.acquireConnection(className);
        try {
            if (workflowId != null) {
                return hu.getList("select new com.trackstudio.kernel.cache.UDFCacheItem(udf.id, udf.udfsource.id, udf.udfsource.workflow.id, udf.udfsource.task.id, udf.udfsource.user.id, udf.caption, udf.referencedbycaption, udf.type, udf.order, udf.def, udf.initialtask.id, udf.initialuser.id, udf.required, udf.htmlview, udf.script, udf.cachevalues, udf.lookupscript, udf.lookuponly) from com.trackstudio.model.Udf as udf where udf.udfsource.workflow.id=? order by udf.order", workflowId);
            } else {
                return new ArrayList<UDFCacheItem>();
            }
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    public List<UDFCacheItem> getUdfCacheItemTasks(List<String> tasksId, String workflowId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            Map<String, Collection> paramMap = new LinkedHashMap<String, Collection>();
            paramMap.put("tasks", tasksId);
            Map<String, String> paramString = new LinkedHashMap<String, String>();
            paramString.put("workflowId", workflowId);
            return hu.getListMap("select new com.trackstudio.kernel.cache.UDFCacheItem (udf.id, udf.udfsource.id, udf.udfsource.workflow.id, udf.udfsource.task.id, udf.udfsource.user.id, udf.caption, udf.referencedbycaption, udf.type, udf.order, udf.def, udf.initialtask.id, udf.initialuser.id, udf.required, udf.htmlview, udf.script, udf.cachevalues, udf.lookupscript, udf.lookuponly) from com.trackstudio.model.Udf as udf " +
                    "where udf.udfsource.task.id in (:tasks) or udf.udfsource.workflow.id=:workflowId", paramString, paramMap);
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращается список пользовательских полей, которые видны для указанного типа сообщения
     *
     * @param mstatusId ID типа сообщения, для которого берется список полей
     * @return список ID полей
     * @throws GranException при необходимости
     */
    public List<String> getViewableUDFId(String mstatusId) throws GranException {
        log.trace("########");
        boolean r = lockManager.acquireConnection(className);
        try {

            List<String> list = hu.getList("select udf.id from com.trackstudio.model.Udf udf, com.trackstudio.model.Umstatus u where u.udf=udf.id and u.type='V' and u.mstatus.id=?", mstatusId);
            if (list == null)
                return new ArrayList<String>();
            else
                return list;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращается список пользовательских полей, которые можно редактировать для указанного типа сообщения
     *
     * @param mstatusId ID типа сообщения, для которого берется список полей
     * @return список ID полей
     * @throws GranException при необходимости
     */
    public List<String> getEditableUDFId(String mstatusId) throws GranException {
        log.trace("########");
        boolean r = lockManager.acquireConnection(className);
        try {
            List<String> list = hu.getList("select udf.id from com.trackstudio.model.Udf udf, com.trackstudio.model.Umstatus u where u.udf=udf.id and u.type='E' and u.mstatus.id=?", mstatusId);
            if (list == null)
                return new ArrayList<String>();
            else
                return list;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращается список типов сообщений в которых можно редактировать указанное пользовательское поля процесса
     *
     * @param workflowUDFId ID пользовательского поля процесса, для которого получается список типов сообщений
     * @return список ID типов сообщений
     * @throws GranException при необходимости
     */
    public List<String> getOperationsWhereUDFIsEditable(String workflowUDFId) throws GranException {
        log.trace("########");
        boolean r = lockManager.acquireConnection(className);
        try {

            List<String> list = hu.getList("select u.mstatus.id from com.trackstudio.model.Umstatus u where u.udf.id=? and u.type='E'", workflowUDFId);
            if (list == null) return new ArrayList<String>();
            else return list;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращается список типов сообщений в которых можно просматривать указанное пользовательское поля процесса
     *
     * @param workflowUDFId ID пользовательского поля процесса, для которого получается список типов сообщений
     * @return список ID типов сообщений
     * @throws GranException при необходимости
     */
    public List<String> getOperationsWhereUDFIsViewable(String workflowUDFId) throws GranException {
        log.trace("########");
        boolean r = lockManager.acquireConnection(className);
        try {

            List<String> list = hu.getList("select u.mstatus.id from com.trackstudio.model.Umstatus u where u.udf.id=? and u.type='V'", workflowUDFId);
            if (list == null) return new ArrayList<String>();
            else return list;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает список пользовательских полей для пользователя
     *
     * @param userId ID пользователя, для которого получается список пользовательских полей
     * @return список пользовательских полей
     * @throws GranException при необходимости
     * @see com.trackstudio.kernel.cache.UDFCacheItem
     */
    public List<UDFCacheItem> getListUserUDFCacheItem(String userId) throws GranException {
        log.trace("########");
        boolean r = lockManager.acquireConnection(className);
        try {

            return hu.getList("select new com.trackstudio.kernel.cache.UDFCacheItem (udf.id, udf.udfsource.id, udf.udfsource.workflow.id, udf.udfsource.task.id, udf.udfsource.user.id, udf.caption, udf.referencedbycaption, udf.type, udf.order, udf.def, udf.initialtask.id, udf.initialuser.id, udf.required, udf.htmlview, udf.script, udf.cachevalues, udf.lookupscript, udf.lookuponly) from com.trackstudio.model.Udf as udf where udf.udfsource.user.id=? order by udf.order", userId);
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает список пользовательских полей для задачи
     *
     * @param taskId ID задачи, для которого получается список пользовательских полей
     * @return список пользовательских полей
     * @throws GranException при необходимости
     * @see com.trackstudio.kernel.cache.UDFCacheItem
     */
    public List<UDFCacheItem> getListTaskUDFCacheItem(String taskId) throws GranException {
        log.trace("########");
        boolean r = lockManager.acquireConnection(className);
        try {

            return hu.getList("select new com.trackstudio.kernel.cache.UDFCacheItem (udf.id, udf.udfsource.id, udf.udfsource.workflow.id, udf.udfsource.task.id, udf.udfsource.user.id, udf.caption, udf.referencedbycaption, udf.type, udf.order, udf.def, udf.initialtask.id, udf.initialuser.id, udf.required, udf.htmlview, udf.script, udf.cachevalues, udf.lookupscript, udf.lookuponly) from com.trackstudio.model.Udf as udf where udf.udfsource.task.id=? order by udf.order", taskId);
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает список всех пользовательских полей для задачи
     *
     * @return список пользовательских полей
     * @throws GranException при необходимости
     * @see com.trackstudio.kernel.cache.UDFCacheItem
     */
    public List<UDFCacheItem> getListAllTaskUDFCacheItem() throws GranException {
        log.trace("########");
        boolean r = lockManager.acquireConnection(className);
        try {

            return hu.getList("select new com.trackstudio.kernel.cache.UDFCacheItem (udf.id, udf.udfsource.id, udf.udfsource.workflow.id, udf.udfsource.task.id, udf.udfsource.user.id, udf.caption, udf.referencedbycaption, udf.type, udf.order, udf.def, udf.initialtask.id, udf.initialuser.id, udf.required, udf.htmlview, udf.script, udf.cachevalues, udf.lookupscript, udf.lookuponly) from com.trackstudio.model.Udf as udf where (udf.udfsource.task.id != null or udf.udfsource.workflow.id != null) order by udf.order");
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает список всех пользовательских полей для пользователя
     *
     * @return список пользовательских полей
     * @throws GranException при необходимости
     * @see com.trackstudio.kernel.cache.UDFCacheItem
     */
    public List<UDFCacheItem> getListAllUserUDFCacheItem() throws GranException {
        log.trace("########");
        boolean r = lockManager.acquireConnection(className);
        try {

            return hu.getList("select new com.trackstudio.kernel.cache.UDFCacheItem (udf.id, udf.udfsource.id, udf.udfsource.workflow.id, udf.udfsource.task.id, udf.udfsource.user.id, udf.caption, udf.referencedbycaption, udf.type, udf.order, udf.def, udf.initialtask.id, udf.initialuser.id, udf.required, udf.htmlview, udf.script, udf.cachevalues, udf.lookupscript, udf.lookuponly) from com.trackstudio.model.Udf as udf where udf.udfsource.user.id  != null order by udf.order");
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает пользовательское поле по его ID
     *
     * @param udfId ID пользовательского поля
     * @return пользовательское поле
     * @throws GranException при необходимости
     * @see com.trackstudio.kernel.cache.UDFCacheItem
     */
    public UDFCacheItem getUDFCacheItem(String udfId) throws GranException {
        // log.trace("########");
        boolean r = lockManager.acquireConnection(className);
        try {
            List list = hu.getList("select new com.trackstudio.kernel.cache.UDFCacheItem (udf.id, udf.udfsource.id, udf.udfsource.workflow.id, udf.udfsource.task.id, udf.udfsource.user.id, udf.caption, udf.referencedbycaption, udf.type, udf.order, udf.def, udf.initialtask.id, udf.initialuser.id, udf.required, udf.htmlview, udf.script, udf.cachevalues, udf.lookupscript, udf.lookuponly) from com.trackstudio.model.Udf as udf where udf.id=? order by udf.order", udfId);
            return list.isEmpty() ? null : (UDFCacheItem) list.get(0);
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * This method gets Pair which has udf.id, udf.caption, udf.usersource.id
     * @param udfIds
     * @return
     * @throws GranException
     */
    public List<Pair<String>> getUdfField(List<String> udfIds, String caption) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            if (!udfIds.isEmpty()) {
                Map<String, String> param = new LinkedHashMap<String, String>();
                param.put("caption", caption);
                Map<String, Collection> map = new LinkedHashMap<String, Collection>();
                map.put("key", udfIds);
                return hu.getListMap("select new com.trackstudio.tools.Pair(udf.id, udf.udfsource.id, udf.caption) from com.trackstudio.model.Udf as udf where udf.id in (:key) and udf.caption=:caption", param, map);
            } else {
                return new ArrayList<Pair<String>>();
            }
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает список значений пользовательского поля
     *
     * @param idVal   ID задачи или пользователя
     * @param modeVal Параметр, определяющий для каких полей получается объект (UdfConstants.TASK_ALL, UdfConstants.USER_ALL или UdfConstants.WORKFLOW_ALL)
     * @param udfList список пользовательских полей
     * @return список значение полей
     * @throws GranException при необходимости
     * @see com.trackstudio.app.UdfValue
     */
    public ArrayList<UdfValue> getUDFValues(String idVal, int modeVal, ArrayList<UDFCacheItem> udfList) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {

            // Prepare UdfValue container for each udf.
            ArrayList<UdfValue> retUdfvalList = new ArrayList<UdfValue>();
            HashMap<String, UdfValue> retUdfvalMap = new HashMap<String, UdfValue>();
            for (UDFCacheItem udf : udfList) {
                UdfValue udfval = new UdfValue(udf);
                retUdfvalList.add(udfval);
                retUdfvalMap.put(udf.getId(), udfval);
            }

            // ���������� UDF �� ������
            List<UdfvalCacheItem> udfvalCacheItems = new ArrayList<UdfvalCacheItem>();
            // todo � ��� ���������� winzard
            if (modeVal == UdfConstants.UDFSOURCE)
                throw new GranException("Incorrect mode");
            else if (modeVal == UdfConstants.TASK_ALL)
                udfvalCacheItems = hu.getList("select new com.trackstudio.kernel.cache.UdfvalCacheItem (udfval.id, udfval.udf.id, udfval.str, udfval.num, udfval.dat, udfval.udflist.id, udfval.longtext.id, udfval.task.id, udfval.user.id) from com.trackstudio.model.Udfval as udfval where udfval.udfsource.task.id=? order by udfval.udf.order", idVal);
            else if (modeVal == UdfConstants.USER_ALL)
                udfvalCacheItems = hu.getList("select new com.trackstudio.kernel.cache.UdfvalCacheItem (udfval.id, udfval.udf.id, udfval.str, udfval.num, udfval.dat, udfval.udflist.id, udfval.longtext.id, udfval.task.id, udfval.user.id) from com.trackstudio.model.Udfval as udfval where udfval.udfsource.user.id=? order by udfval.udf.order", idVal);

            // ��������� �������� ������� �� id
            for (UdfvalCacheItem uval : udfvalCacheItems) {
                if (uval.getUdflistId() != null)
                    uval.setUdflistVal(KernelManager.getFind().findUdflist(uval.getUdflistId()).getVal());
                UdfValue uv = retUdfvalMap.get(uval.getUdfId());
                if (uv != null) {
                    uv.setValue(uval);
                }
            }
            return retUdfvalList;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Создает список значений для поля Udf типа List или MultiList для задачи
     *
     * @param udfId  ID пользовательского поля
     * @param val    Значения
     * @return ID созданного списка
     * @throws GranException при необходимости
     */
    public String addTaskUdflist(String udfId, SafeString val) throws GranException {
        log.trace("########");
        boolean w = lockManager.acquireConnection(className);
        try {
            String id = addUdflist(udfId, val);
            TaskRelatedManager.getInstance().invalidateUDFWhenChangeList(udfId, val != null ? val.toString() : null, id);
            return id;
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Создает список значений для поля Udf типа List или MultiList для пользователя
     *
     * @param userId ID пользователя
     * @param udfId  ID пользовательского поля
     * @param val    Значения
     * @return ID созданного списка
     * @throws GranException при необходимости
     */
    public String addUserUdflist(String userId, String udfId, SafeString val) throws GranException {
        log.trace("########");
        boolean w = lockManager.acquireConnection(className);
        try {
            String listId = addUdflist(udfId, val);
            UserRelatedManager.getInstance().invalidateUDFWhenChangeList(userId, udfId, val != null ? val.toString() : null, listId);
            return listId;
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Создает список значений для поля Udf типа List или MultiList для процесса
     *
     * @param udfId ID пользовательского поля
     * @param val   Значения
     * @return ID созданного списка
     * @throws GranException при необходимости
     */
    public String addWorkflowUdflist(String udfId, SafeString val) throws GranException {
        log.trace("########");
        boolean w = lockManager.acquireConnection(className);
        try {
            String id = addUdflist(udfId, val);
            TaskRelatedManager.getInstance().invalidateWFUDFWhenChangeList(udfId, val != null ? val.toString() : null, id);
            return id;
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Создает список значений для поля Udf типа List или MultiList
     *
     * @param udfId ID пользовательского поля
     * @param val   Значения
     * @return ID созданного списка
     * @throws GranException при необходимости
     */
    private String addUdflist(String udfId, SafeString val) throws GranException {
        log.trace("########");
        return hu.createObject(new Udflist(val != null ? val.toString() : null, udfId));
    }

    /**
     * Удаляет список значений для поля Udf типа List или MultiList для задачи
     *
     * @param udflistId ID списка значений
     * @throws GranException при необходимости
     */
    public void deleteTaskUdflist(String udflistId) throws GranException {
        log.trace("########");
        boolean w = lockManager.acquireConnection(className);
        try {
            String udfId = KernelManager.getFind().findUdflist(udflistId).getUdf().getId();
            deleteUdflist(udflistId);
            TaskRelatedManager.getInstance().invalidateUDFWhenChangeList(udfId, null, udflistId);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Удаляет список значений для поля Udf типа List или MultiList для пользователя
     *
     * @param userId    ID пользователя
     * @param udflistId ID списка значений
     * @throws GranException при необходимости
     */
    public void deleteUserUdflist(String userId, String udflistId) throws GranException {
        log.trace("########");
        boolean w = lockManager.acquireConnection(className);
        try {
            String udfId = KernelManager.getFind().findUdflist(udflistId).getUdf().getId();
            deleteUdflist(udflistId);
            UserRelatedManager.getInstance().invalidateUDFWhenChangeList(userId, udfId, null, udflistId);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Удаляет список значений для поля Udf типа List или MultiList для процесса
     *
     * @param udfId ID списка значений
     * @throws GranException при необходимости
     */
    public void deleteWorkflowUdflist(String udfId) throws GranException {
        log.trace("########");
        boolean w = lockManager.acquireConnection(className);
        try {
            String udf = KernelManager.getFind().findUdflist(udfId).getUdf().getId();
            deleteUdflist(udfId);
            TaskRelatedManager.getInstance().invalidateWFUDFWhenChangeList(udf, null, udfId);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Удаляет список значений для поля Udf типа List или MultiList
     *
     * @param udflistId ID списка значений
     * @throws GranException при необходимости
     */
    private void deleteUdflist(String udflistId) throws GranException {
        log.trace("########");
        Udflist ul = (Udflist) hu.getObject(Udflist.class, udflistId);
        Udf udf = (Udf) hu.getObject(Udf.class, ul.getUdf().getId());
        if (udflistId.equals(udf.getDef())) {
            udf.setDef(null);
            hu.updateObject(udf);
        }
        hu.deleteObject(Udflist.class, udflistId);
    }

    /**
     * Редактирует список значений для поля Udf типа List или MultiList для задачи
     *
     * @param udflistId ID списка значений
     * @param value     Значения
     * @throws GranException при необходимости
     */
    public void updateTaskUdflist(String udflistId, SafeString value) throws GranException {
        log.trace("########");
        boolean w = lockManager.acquireConnection(className);
        try {
            updateUdflist(udflistId, value);
            String udfId = KernelManager.getFind().findUdflist(udflistId).getUdf().getId();
            TaskRelatedManager.getInstance().invalidateUDFWhenChangeList(udfId, value != null ? value.toString() : null, udflistId);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Редактирует список значений для поля Udf типа List или MultiList для пользователя
     *
     * @param userId    ID пользователя
     * @param udflistId ID списка значений
     * @param value     Значения
     * @throws GranException при необходимости
     */
    public void updateUserUdflist(String userId, String udflistId, SafeString value) throws GranException {
        log.trace("########");
        boolean w = lockManager.acquireConnection(className);
        try {
            updateUdflist(udflistId, value);
            String udfId = KernelManager.getFind().findUdflist(udflistId).getUdf().getId();
            UserRelatedManager.getInstance().invalidateUDFWhenChangeList(userId, udfId, value != null ? value.toString() : null, udflistId);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Редактирует список значений для поля Udf типа List или MultiList для процесса
     *
     * @param udflistId ID списка значений
     * @param value     Значения
     * @throws GranException при необходимости
     */
    public void updateWorkflowUdflist(String udflistId, SafeString value) throws GranException {
        log.trace("########");
        boolean w = lockManager.acquireConnection(className);
        try {
            updateUdflist(udflistId, value);
            String udfId = KernelManager.getFind().findUdflist(udflistId).getUdf().getId();
            TaskRelatedManager.getInstance().invalidateWFUDFWhenChangeList(udfId, value != null ? value.toString() : null, udflistId);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Редактирует список значений для поля Udf типа List или MultiList
     *
     * @param udflistId ID списка значений
     * @param value     Значения
     * @throws GranException при необходимости
     */
    private void updateUdflist(String udflistId, SafeString value) throws GranException {
        Udflist ulist = KernelManager.getFind().findUdflist(udflistId);
        ulist.setVal(value != null ? value.toString() : null);
        hu.updateObject(ulist);
    }

    /**
     * Редактирует пользовательское поле для задачи
     *
     * @param udfId               ID поля
     * @param caption             Название поля
     * @param referencedbycaption Обратное название
     * @param order               Порядок
     * @param def                 Значение по умолчанию
     * @param required            Обязательность поля
     * @param htmlview            Вид html или текстовый
     * @param scriptId            ID скрипта
     * @param lookupscriptId      ID lookup-скрпита
     * @param lookuponly          Использовать тольок lookup-значения или нет
     * @param cachevalues         Кешировать вычисляемые значения или нет
     * @param initial             Начальное выбранное значение из списка значений (если он есть)
     * @throws GranException при необходимости
     */
    public void updateTaskUdf(String udfId, SafeString caption, SafeString referencedbycaption, int order, SafeString def, boolean required, boolean htmlview,
                              String scriptId, String lookupscriptId, boolean lookuponly, boolean cachevalues, SafeString initial) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            String taskid = updateUdf(UdfConstants.TASK_ALL, udfId, caption, referencedbycaption, order, def, required, htmlview, scriptId, lookupscriptId, lookuponly, cachevalues, initial);
            if (taskid != null) TaskRelatedManager.getInstance().invalidateUDFs(taskid, udfId);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Редактирует пользовательское поле для процесса
     *
     * @param udfId               ID поля
     * @param caption             Название поля
     * @param referencedbycaption Обратное название
     * @param order               Порядок
     * @param def                 Значение по умолчанию
     * @param required            Обязательность поля
     * @param htmlview            Вид html или текстовый
     * @param scriptId            ID скрипта
     * @param lookupscriptId      ID lookup-скрпита
     * @param lookuponly          Использовать тольок lookup-значения или нет
     * @param cachevalues         Кешировать вычисляемые значения или нет
     * @param initial             Начальное выбранное значение из списка значений (если он есть)
     * @throws GranException при необходимости
     */
    public void updateWorkflowUdf(String udfId, SafeString caption, SafeString referencedbycaption, int order, SafeString def, boolean required, boolean htmlview,
                                  String scriptId, String lookupscriptId, boolean lookuponly, boolean cachevalues, SafeString initial) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            updateUdf(UdfConstants.WORKFLOW_ALL, udfId, caption, referencedbycaption, order, def, required, htmlview, scriptId, lookupscriptId, lookuponly, cachevalues, initial);
            hu.cleanSession();
            String udfSourceId = KernelManager.getFind().findUdf(udfId).getUdfsource().getId();
            if (udfSourceId!=null){
                Udfsource usource = KernelManager.getFind().findUdfsource(udfSourceId);
                if (usource!=null)
                    TaskRelatedManager.getInstance().invalidateWFUDFs(usource.getWorkflow().getId(), udfId);
            }
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Редактирует пользовательское поле для пользователя
     *
     * @param udfId               ID поля
     * @param caption             Название поля
     * @param referencedbycaption Обратное название
     * @param order               Порядок
     * @param def                 Значение по умолчанию
     * @param required            Обязательность поля
     * @param htmlview            Вид html или текстовый
     * @param scriptId            ID скрипта
     * @param lookupscriptId      ID lookup-скрпита
     * @param lookuponly          Использовать тольок lookup-значения или нет
     * @param cachevalues         Кешировать вычисляемые значения или нет
     * @param initial             Начальное выбранное значение из списка значений (если он есть)
     * @throws GranException при необходимости
     */
    public void updateUserUdf(String udfId, SafeString caption, SafeString referencedbycaption, int order, SafeString def, boolean required, boolean htmlview, String scriptId, String lookupscriptId, boolean lookuponly, boolean cachevalues, SafeString initial) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            String userid = updateUdf(UdfConstants.USER_ALL, udfId, caption, referencedbycaption, order, def, required, htmlview, scriptId, lookupscriptId, lookuponly, cachevalues, initial);
            if (userid != null) UserRelatedManager.getInstance().invalidateUDFs(udfId);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }

    }

    /**
     * Редактирует пользовательское поле
     *
     * @param mode                Параметр, определяющий для каких полей получается объект (UdfConstants.TASK_ALL, UdfConstants.USER_ALL или UdfConstants.WORKFLOW_ALL)
     * @param udfId               ID поля
     * @param caption             Название поля
     * @param referencedbycaption Обратное название
     * @param order               Порядок
     * @param def                 Значение по умолчанию
     * @param required            Обязательность поля
     * @param htmlview            Вид html или текстовый
     * @param scriptId            ID скрипта
     * @param lookupscriptId      ID lookup-скрпита
     * @param lookuponly          Использовать тольок lookup-значения или нет
     * @param cachevalues         Кешировать вычисляемые значения или нет
     * @param initial             Начальное выбранное значение из списка значений (если он есть)
     * @return ID задачи или пользователя, для которых создано поле, или NULL, если поле создано для процесса
     * @throws GranException при необходимости
     */
    private String updateUdf(int mode, String udfId, SafeString caption, SafeString referencedbycaption, int order, SafeString def, boolean required, boolean htmlview, String scriptId, String lookupscriptId, boolean lookuponly, boolean cachevalues, SafeString initial) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {

            String objectid = null;
            Udf udf = KernelManager.getFind().findUdf(udfId);
            switch (mode) {
                case UdfConstants.TASK_ALL:
                    objectid = udf.getUdfsource().getTask().getId();
                    break;
                case UdfConstants.USER_ALL:
                    objectid = udf.getUdfsource().getUser().getId();
                    break;
                case UdfConstants.WORKFLOW_ALL:
                    objectid = null;
                    break;
            }
            udf.setCaption(caption != null ? caption.toString() : I18n.getString(Locale.US, "NO_CAPTION"));
            udf.setReferencedbycaption(referencedbycaption != null ? referencedbycaption.toString() : null);
            udf.setOrder(order);
            if (scriptId != null && scriptId.length() > 0)
                udf.setScript(scriptId);
            else
                udf.setNullScript();
            if (lookupscriptId != null && lookupscriptId.length() > 0)
                udf.setLookupscript(lookupscriptId);
            else
                udf.setNullLookupScript();
            udf.setDef(def != null ? def.toString() : "");
            udf.setRequired(required ? 1 : 0);
            udf.setHtmlview(htmlview ? 1 : 0);
            udf.setLookuponly(lookuponly ? 1 : 0);
            udf.setCachevalues(cachevalues ? 1 : 0);
            int type = udf.getType();
            if (type == UdfConstants.TASK) {
                udf.setInitialtask(initial != null ? new Task(initial.toString().trim()) : null);
            } else if (type == UdfConstants.USER) {
                udf.setInitialuser(initial != null ? new User(initial.toString().trim()) : null);
            }
            hu.updateObject(udf);
            return objectid;
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Устанавливает значение пользовательского поля
     *
     * @param udfId    ID поля
     * @param sourceId ID объекта UdfSource
     * @param value    Одно значение, или несколько значыений, разделенных ;
     * @param locale   Локаль пользователя
     * @param tz       Таймзона пользователя
     * @throws GranException при необходимости
     */
    private void setUdfValue(String udfId, String sourceId, SafeString value, String locale, String tz) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            Udf u = KernelManager.getFind().findUdf(udfId);
            deleteUDFValue(udfId, sourceId);
            if (value != null && value.toString() != null && value.toString().trim().length() > 0) {
                //sess.save(new Udfval(null, null, null, null, udfId, sourceId));
                int type = u.getType();
                String newLongtextId = null;
                if (type == UdfConstants.MEMO) {
                    Udfval udfval = new Udfval(null, null, null, null, null, null, udfId, sourceId);
                    if (value.length() > 2000)
                        newLongtextId = KernelManager.getLongText().createLongtext(null, value.toString());
                    if (newLongtextId != null)
                        udfval.setLongtext(KernelManager.getFind().findLongtext(newLongtextId));
                    else
                        udfval.setStr(value.toString());
                    hu.createObject(udfval);
                }
                if (type == UdfConstants.STRING || type == UdfValue.URL)
                    hu.createObject(new Udfval(value.toString(), null, null, null, null, null, udfId, sourceId));
                if (type == UdfConstants.FLOAT) {
                    Double doubleValue = HourFormatter.parseDouble(value.toString());
//                	Double doubleValue = null;
//                	try{
//                		doubleValue = NumberFormat.getNumberInstance(DateFormatter.toLocale(locale)).parse(value.toString()).doubleValue();
//                	}
//                	catch (Exception e) {
//                		throw new GranException(e);
//                	}	
                    hu.createObject(new Udfval(null, doubleValue, null, null, null, null, udfId, sourceId));
                }
                if (type == UdfConstants.INTEGER)
                    hu.createObject(new Udfval(null, new Double(value.toString()), null, null, null, null, udfId, sourceId));

                if (type == UdfConstants.DATE) {
                    DateFormatter dateFormatter = new DateFormatter(tz, locale);
                    Calendar timestampValue = dateFormatter.parseToCalendar(value.toString());
                    hu.createObject(new Udfval(null, null, timestampValue, null, null, null, udfId, sourceId));
                }
                if (type == UdfConstants.LIST)
                    hu.createObject(new Udfval(null, null, null, value.toString(), null, null, udfId, sourceId));
                if (type == UdfValue.MULTILIST) {
                    StringTokenizer tk = new StringTokenizer(value.toString(), "\n");
                    while (tk.hasMoreTokens()) {
                        String udflistId = tk.nextToken();
                        if (udflistId != null && udflistId.length() > 0)
                            hu.createObject(new Udfval(null, null, null, udflistId, null, null, udfId, sourceId));
                    }
                }
                if (type == UdfValue.TASK) {
                    StringTokenizer tk = new StringTokenizer(value.toString(), ";");
                    while (tk.hasMoreTokens()) {
                        hu.createObject(new Udfval(null, null, null, null, tk.nextToken(), null, udfId, sourceId));
                    }
                }
                if (type == UdfValue.USER) {
                    StringTokenizer tk = new StringTokenizer(value.toString(), ";");
                    while (tk.hasMoreTokens()) {
                        hu.createObject(new Udfval(null, null, null, null, null, tk.nextToken(), udfId, sourceId));
                    }
                }
            }
        } finally {
            if (w) lockManager.releaseConnection(className);
        }

    }


    /**
     * Удаляет значения пользовательского поля
     *
     * @param udfId    ID поля
     * @param sourceId ID объекта Udfsource
     * @throws GranException при необходимости
     */
    private void deleteUDFValue(String udfId, String sourceId) throws GranException {
        hu.executeDML("delete from com.trackstudio.model.Udfval uv where uv.udf=? and uv.udfsource=?", udfId, sourceId);
        hu.cleanSession();
    }

    /**
     * Устанавливает значение пользовательского поля для задачи
     *
     * @param udfId    ID поля
     * @param sourceId ID объекта UdfSource
     * @param value    Одно значение, или несколько значыений, разделенных ;
     * @param locale   Локаль пользователя
     * @param tz       Таймзона пользователя
     * @throws GranException при необходимости
     */
    public void setTaskUdfValue(String udfId, String sourceId, SafeString value, String locale, String tz) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            String sourceid = KernelManager.getUdf().getUDFSource(sourceId, 1);
            setUdfValue(udfId, sourceid, value, locale, tz);
            TaskRelatedManager.getInstance().invalidateUDF(sourceId);
        } catch (Exception e) {
            throw new GranException(e);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Устанавливает значение пользовательского поля для пользователя
     *
     * @param udfId    ID поля
     * @param sourceId ID объекта UdfSource
     * @param value    Одно значение, или несколько значыений, разделенных ;
     * @param locale   Локаль пользователя
     * @param tz       Таймзона пользователя
     * @throws GranException при необходимости
     */
    public void setUserUdfValue(String udfId, String sourceId, SafeString value, String locale, String tz) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            String sourceid = KernelManager.getUdf().getUDFSource(sourceId, 2);
            setUdfValue(udfId, sourceid, value, locale, tz);
            UserRelatedManager.getInstance().invalidateUDFsValues(sourceId);
        } catch (Exception e) {
            throw new GranException(e);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Удаляет пользовательское поле
     *
     * @param udfId ID поля
     * @param mode  Параметр, определяющий для каких полей получается объект (UdfConstants.TASK_ALL, UdfConstants.USER_ALL или UdfConstants.WORKFLOW_ALL)
     * @return ID задачи или пользователя, которого удаляем
     * @throws GranException при необходимости
     */
    private String deleteUdf(String udfId, int mode) throws GranException {
        String objectid = null;
        Udf u = KernelManager.getFind().findUdf(udfId);
        switch (mode) {
            case UdfConstants.TASK_ALL:
                objectid = u.getUdfsource().getTask().getId();
                break;
            case UdfConstants.USER_ALL:
                objectid = u.getUdfsource().getUser().getId();
                break;
            case UdfConstants.WORKFLOW_ALL:
                objectid = null;
                break;
        }
        UprstatusCacheManager.getInstance().invalidateRemoveUdf(udfId);
        hu.deleteObject(Udf.class, udfId);
        return objectid;
    }

    /**
     * Удаляет пользовательское поле для задачи
     *
     * @param udfId ID поля
     * @throws GranException при необходимости
     */
    public void deleteTaskUdf(String udfId) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            String taskid = deleteUdf(udfId, UdfConstants.TASK_ALL);
            if (taskid != null) TaskRelatedManager.getInstance().invalidateUDFs(taskid, udfId);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Удаляет пользовательское поле для процесса
     *
     * @param udfId ID поля
     * @throws GranException при необходимости
     */
    public void deleteWorkflowUdf(String udfId) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            String wfId = KernelManager.getFind().findUdf(udfId).getUdfsource().getWorkflow().getId();
            deleteUdf(udfId, UdfConstants.WORKFLOW_ALL);
            hu.cleanSession();
            TaskRelatedManager.getInstance().invalidateWFUDFs(wfId, udfId);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Удаляет пользовательское поле для пользователя
     *
     * @param udfId ID поля
     * @throws GranException при необходимости
     */
    public void deleteUserUdf(String udfId) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            hu.deleteObject(Udf.class, udfId);
            hu.cleanSession();
            UserRelatedManager.getInstance().invalidateUDFs(udfId);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Создает  пользовательское поле
     *
     * @param sourceId            ID объекта Udfsource
     * @param caption             Название поля
     * @param referencedbycaption Обратное название
     * @param order               Порядок
     * @param def                 Значение по умолчанию
     * @param ul                  Значение объекта Udflist
     * @param required            Обязательность поля
     * @param htmlview            Вид html или текстовый
     * @param type                Тип поля
     * @param scriptId            ID скрипта
     * @param lookupscriptId      ID lookup-скрпита
     * @param lookuponly          Использовать тольок lookup-значения или нет
     * @param cachevalues         Кешировать вычисляемые значения или нет
     * @param initial             Начальное выбранное значение из списка значений (если он есть)
     * @return ID созданного поля
     * @throws GranException при необходимости
     */
    private String createUdf(String sourceId, SafeString caption, SafeString referencedbycaption, int order, SafeString def, String ul, boolean required, boolean htmlview,
                             Integer type, String scriptId, String lookupscriptId, boolean lookuponly, boolean cachevalues, SafeString initial) throws GranException {

        boolean w = lockManager.acquireConnection(className);
        try {
            if (scriptId != null && scriptId.length() == 0)
                scriptId = null;
            if (lookupscriptId != null && lookupscriptId.length() == 0)
                lookupscriptId = null;
            if ((type == UdfConstants.USER || type == UdfConstants.TASK) && Null.isNull(initial.toString()))  {
                initial.append("1"); //add root task/user
            }
            Udf udf = new Udf(caption != null ? caption.toString() : I18n.getString(Locale.US, "NO_CAPTION"), referencedbycaption != null ? referencedbycaption.toString() : "", order, def != null ? def.toString() : "", required, htmlview, type, scriptId, lookupscriptId, lookuponly, cachevalues, initial != null ? initial.toString() : null, sourceId);
            return hu.createObject(udf);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Создает  пользовательское поле для задачи
     *
     * @param taskId                  ID задачи
     * @param caption                 Название поля
     * @param referencedbycaption     Обратное название
     * @param order                   Порядок
     * @param def                     Значение по умолчанию
     * @param ul                      Значение объекта Udflist
     * @param required                Обязательность поля
     * @param htmlview                Вид html или текстовый
     * @param type                    Тип поля
     * @param formulaLongtextId       ID скрипта
     * @param lookupformulaLongtextId ID lookup-скрпита
     * @param lookuponly              Использовать тольок lookup-значения или нет
     * @param cachevalues             Кешировать вычисляемые значения или нет
     * @param initial                 Начальное выбранное значение из списка значений (если он есть)
     * @return ID созданного поля
     * @throws GranException при необходимости
     */
    public String createTaskUdf(String taskId, SafeString caption, SafeString referencedbycaption, int order, SafeString def, String ul, boolean required, boolean htmlview,
                                Integer type, String formulaLongtextId, String lookupformulaLongtextId, boolean lookuponly, boolean cachevalues, SafeString initial) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            String sourceid = KernelManager.getUdf().getUDFSource(taskId, UdfConstants.TASK_ALL);
            String uid = createUdf(sourceid, caption, referencedbycaption, order, def, ul, required, htmlview, type, formulaLongtextId, lookupformulaLongtextId, lookuponly, cachevalues, initial);
            hu.cleanSession();
            TaskRelatedManager.getInstance().invalidateUDFs(taskId, uid);
            return uid;
        } finally {
            if (w) lockManager.releaseConnection(className);
        }

    }

    /**
     * Создает  пользовательское поле для процесса
     *
     * @param workflowId              ID процесса
     * @param caption                 Название поля
     * @param referencedbycaption     Обратное название
     * @param order                   Порядок
     * @param def                     Значение по умолчанию
     * @param ul                      Значение объекта Udflist
     * @param required                Обязательность поля
     * @param htmlview                Вид html или текстовый
     * @param type                    Тип поля
     * @param formulaLongtextId       ID скрипта
     * @param lookupformulaLongtextId ID lookup-скрпита
     * @param lookuponly              Использовать тольок lookup-значения или нет
     * @param cachevalues             Кешировать вычисляемые значения или нет
     * @param initial                 Начальное выбранное значение из списка значений (если он есть)
     * @return ID созданного поля
     * @throws GranException при необходимости
     */
    public String createWorkflowUdf(String workflowId, SafeString caption, SafeString referencedbycaption, int order, SafeString def, String ul,
                                    boolean required, boolean htmlview, Integer type, String formulaLongtextId, String lookupformulaLongtextId, boolean lookuponly, boolean cachevalues, SafeString initial) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            String sourceid = KernelManager.getUdf().getUDFSource(workflowId, 5);
            String uid = createUdf(sourceid, caption, referencedbycaption, order, def, ul, required, htmlview, type, formulaLongtextId, lookupformulaLongtextId, lookuponly, cachevalues, initial);
            hu.cleanSession();
            TaskRelatedManager.getInstance().invalidateWFUDFs(workflowId, uid);
            return uid;
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Создает  пользовательское поле для пользователя
     *
     * @param userId                  ID пользователя
     * @param caption                 Название поля
     * @param referencedbycaption     Обратное название
     * @param order                   Порядок
     * @param def                     Значение по умолчанию
     * @param ul                      Значение объекта Udflist
     * @param required                Обязательность поля
     * @param htmlview                Вид html или текстовый
     * @param type                    Тип поля
     * @param formulaLongtextId       ID скрипта
     * @param lookupformulaLongtextId ID lookup-скрпита
     * @param lookuponly              Использовать тольок lookup-значения или нет
     * @param cachevalues             Кешировать вычисляемые значения или нет
     * @param initial                 Начальное выбранное значение из списка значений (если он есть)
     * @return ID созданного поля
     * @throws GranException при необходимости
     */
    public String createUserUdf(String userId, SafeString caption, SafeString referencedbycaption, int order, SafeString def, String ul, boolean required, boolean htmlview,
                                Integer type, String formulaLongtextId, String lookupformulaLongtextId, boolean lookuponly, boolean cachevalues, SafeString initial) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            String sourceid = KernelManager.getUdf().getUDFSource(userId, UdfConstants.USER_ALL);
            String uid = createUdf(sourceid, caption, referencedbycaption, order, def, ul, required, htmlview, type, formulaLongtextId, lookupformulaLongtextId, lookuponly, cachevalues, initial);
            hu.cleanSession();
            UserRelatedManager.getInstance().invalidateUDFs(uid);
            return uid;
        } finally {
            if (w) lockManager.releaseConnection(className);
        }

    }

    /**
     * Возвращает локализованное значение по умоляанию для пользовательского поля
     *
     * @param udfId  ID поля
     * @param locale Локаль пользователя
     * @param tz     Таймзона пользователя
     * @return значение
     * @throws GranException при небоходимости
     */
    public String getLocalizedDefaultValue(String udfId, String locale, String tz) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            DateFormatter df = new DateFormatter(tz, locale);
            Udf udf = KernelManager.getFind().findUdf(udfId);
            String def = udf.getDef();
            if ((isNotNull(def)) || (udf.getType().equals(UdfValue.TASK) || udf.getType().equals(UdfValue.USER)))
                switch (udf.getType()) {
                    case UdfValue.LIST:
                        if (!KernelManager.getFind().findUdflist(def).getUdf().getId().equals(udf.getId()))
                            throw new UserException("ERROR_TYPES_MISMATCH", new Object[]{def, udf.getCaption()});
                        return def;
                    case UdfValue.MULTILIST:
                        if (!KernelManager.getFind().findUdflist(def).getUdf().getId().equals(udf.getId()))
                            throw new UserException("ERROR_TYPES_MISMATCH", new Object[]{def, udf.getCaption()});
                        return def;
                    case UdfValue.DATE:
                        return df.parse(Timestamp.valueOf(def));
                    case UdfValue.FLOAT:
                        return String.valueOf(HourFormatter.parseDouble(def));
                    case UdfValue.INTEGER:
                        return Integer.valueOf(def).toString();
                    case UdfValue.TASK:
                        return udf.getInitialtask() != null ? udf.getInitialtask().getId() : "";
                    case UdfValue.USER:
                        return udf.getInitialuser() != null ? udf.getInitialuser().getId() : "";
                    default:
                        return def;
                }
            return "";
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает список прав доступа указанного статуса к пользовательскому полю
     *
     * @param prstatusId ID статуса
     * @param udfId      ID поля
     * @return список прав доступа
     * @throws GranException при необходимости
     */
    public List<String> getUDFRuleList(String prstatusId, String udfId) throws GranException {
        log.trace("########");
        boolean r = lockManager.acquireConnection(className);
        try {
            return hu.getList("select upr.type from com.trackstudio.model.Uprstatus upr where upr.udf = ? and upr.prstatus = ?", udfId, prstatusId);
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Удаляет права доступа статуса к пользовательскому полю
     *
     * @param udfId      ID поля
     * @param prstatusId ID стптуса
     * @throws GranException при необходимости
     */
    public void resetUDFRule(String udfId, String prstatusId) throws GranException {
        log.trace("########");
        boolean w = lockManager.acquireConnection(className);
        try {
            hu.executeDML("delete from com.trackstudio.model.Uprstatus u where u.udf=? and u.prstatus=?", udfId, prstatusId);
            hu.cleanSession();
            UprstatusCacheManager.getInstance().invalidateSingle(prstatusId, udfId);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Создается право доступа указанного типа для указанного статуса и пользовательского поля
     *
     * @param type       тип доступа
     * @param udfId      ID поля
     * @param prstatusId ID статуса
     * @throws GranException при необходимости
     */
    private void addPrstatusUDF(String type, String udfId, String prstatusId) throws GranException {
        UprstatusCacheManager.getInstance().invalidateSingle(prstatusId, udfId);
        if (type != null && type.length() != 0) {
            hu.createObject(new Uprstatus(type, udfId, prstatusId));
        }
    }

    /**
     * Удаляет права доступа для поля и типа сообщения
     *
     * @param udfId     ID поля
     * @param mstatusId ID типа сообщения
     * @throws GranException при необходимости
     */
    public void removeMstatusUDFRule(String udfId, String mstatusId) throws GranException {
        log.trace("########");
        boolean w = lockManager.acquireConnection(className);
        try {
            hu.executeDML("delete from com.trackstudio.model.Umstatus u where u.type is not null and u.udf=? and u.mstatus=?", udfId, mstatusId);
            hu.cleanSession();
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Устанавливает права доступа указанного типа для статуса и поля
     *
     * @param udfId      ID поля
     * @param prstatusId ID статуса
     * @param type       Тип права доступа
     * @throws GranException при необходимости
     */
    public void setUDFRule(String udfId, String prstatusId, String type) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            addPrstatusUDF(type, udfId, prstatusId);
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Устанавливает права доступа указанного типа для статуса и типа сообщения
     *
     * @param udfId     ID поля
     * @param mstatusId ID типа сообщения
     * @param type      Тип права доступа
     * @throws GranException при необходимости
     */
    public void setMstatusUDFRule(String udfId, String mstatusId, String type) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            if (type != null && type.length() != 0)
                hu.createObject(new Umstatus(udfId, mstatusId, type));
        } finally {
            if (w) lockManager.releaseConnection(className);
        }
    }

    /**
     * Проверяет доступность права доступа к пользовательскому полю для пользователя
     *
     * @param type       тип проверяемого права доступа
     * @param udfId      ID поля
     * @param prstatuses Статусы
     * @return TRUE - если доступно, FALSE - если нет
     * @throws GranException при необходимости
     */
    public boolean isUdfAvailableForUser(String type, String udfId, Set<String> prstatuses) throws GranException {
        for (String prstatusId : prstatuses) {
            if (UprstatusCacheManager.getInstance().find(prstatusId, udfId).getUprsList().contains(type))
                return true;
        }
        return false;
    }

    /**
     * Проверяет доступность права доступа к пользовательскому полю для статуса
     *
     * @param type    тип проверяемого права доступа
     * @param udfId   ID поля
     * @param groupId Статус
     * @return TRUE - если доступно, FALSE - если нет
     * @throws GranException при необходимости
     */
    private boolean isUdfAvailableForGroup(String type, String groupId, String udfId) throws GranException {
        return UprstatusCacheManager.getInstance().find(groupId, udfId).getUprsList().contains(type);
    }

    /**
     * Проверяет доступность операции для пользователяского поля пользователя
     *
     * @param udfId      ID поля
     * @param type       Тип операции
     * @param prstatuses Cписок статусов
     * @return TRUE - если доступно, FALSE - если нет
     * @throws GranException при необходимости
     */
    private boolean isAvailableUserUdfOperation(String udfId, int type, Set<String> prstatuses) throws GranException {
        if (type == EDIT)
            if (isUdfAvailableForUser(UdfConstants.EDIT_ALL, udfId, prstatuses))
                return true;
        if (type == VIEW)
            if (isUdfAvailableForUser(UdfConstants.VIEW_ALL, udfId, prstatuses))
                return true;
        return false;
    }

    /**
     * Мы храним правила доступа к UDF следующим образом:<BR>
     * для ВСЕХ не-юзерных UDF (т.е. Task UDF и Workflow UDF мы прописываем в gr_uprstatus:<BR>
     * V - для правила View All<BR>
     * E - для правила Edit All<BR>
     * VIEW_HANDLER - для правила View Handler<BR>
     * VIEW_SUBMITER - для правила View Submitter<BR>
     * VIEW_SH - View Submitter and Handler.<BR>
     * Аналогично для Edit<BR>
     * Для случаев 'None' ничего не прописывается, а, наоборот, удаляется.<BR>
     * Для Workflow UDF все несколько сложнее - там еще учитываются статусы.<BR>
     * Т.е. к вышеуказанным правилам ДОБАВЛЯЮТСЯ, ничего не заменяя, правила:<BR>
     * VIEW_STATUS_ALL - можно видеть во всех состояниях задачи<BR>
     * VIEW_STATUS_+status_id - можно видеть только в определенном состоянии задачи (если указано несколько состояний - будет несколько записей).<BR>
     * Для EDIT - аналогично.<BR>
     * <p/>
     * Метод работает по этим правилам. Т.е. если это workflow UDF и указан статус - проверяется сначала для статуса.
     * Если такая запись ЕСТЬ - дальше проверяется как для обычных UDF, то есть на Handler, Submitter и т.п.
     *
     * @param taskId     ID задачи
     * @param userId     ID пользователя
     * @param udfId      ID поля
     * @param type       Типа права
     * @param prstatuses Список статусов
     * @param submitter  Автор
     * @param handler    Ответственный
     * @return TRUE если доступно, FALSE если нет
     * @throws GranException при необходимости
     */
    private boolean isAvailableTaskUdfOperation(String taskId, String userId, String udfId, int type, Set<String> prstatuses, String submitter, String handler) throws GranException {
        // если udf определен для workflow
//        Workflow workflow = KernelManager.getFind().findUdf(udfId).getUdfsource().getWorkflow();
        //if (isWorkflowUdf){
//            workflow.getTask().getId();
//        }
        if (type == EDIT)
            if (isUdfAvailableForUser(UdfConstants.EDIT_ALL, udfId, prstatuses))
                return true;
        if (type == VIEW)
            if (isUdfAvailableForUser(UdfConstants.VIEW_ALL, udfId, prstatuses))
                return true;

        TaskRelatedInfo task = TaskRelatedManager.getInstance().find(taskId);
        String handlerUserId = task.getHandlerUserId();
        String handlerGroupId = task.getHandlerGroupId();
        String submitterid = task.getSubmitterId();
        if (submitter != null)
            submitterid = submitter;
        if (handler != null) {
            handlerUserId = handler;
            handlerGroupId = handler;
        }

        if (userId.equals(handlerUserId)) {
            if (type == EDIT)
                if (isUdfAvailableForUser(UdfConstants.EDIT_HANDLER, udfId, prstatuses))
                    return true;
            if (type == VIEW)
                if (isUdfAvailableForUser(UdfConstants.VIEW_HANDLER, udfId, prstatuses))
                    return true;
        }
        String userPrstatusId = UserRelatedManager.getInstance().find(userId).getPrstatusId();
        if (handlerGroupId != null && userPrstatusId.equals(handlerGroupId)) {
            if (type == EDIT)
                if (isUdfAvailableForGroup(UdfConstants.EDIT_HANDLER, handlerGroupId, udfId) || isUdfAvailableForGroup(UdfConstants.EDIT_SUBMITTER_AND_HANDLER, handlerGroupId, udfId))
                    return true;
            if (type == VIEW)
                if (isUdfAvailableForGroup(UdfConstants.VIEW_HANDLER, handlerGroupId, udfId) || isUdfAvailableForGroup(UdfConstants.VIEW_SUBMITTER_AND_HANDLER, handlerGroupId, udfId))
                    return true;
        }

        if (userId.equals(submitterid)) {
            if (type == EDIT)
                if (isUdfAvailableForUser(UdfConstants.EDIT_SUBMITTER, udfId, prstatuses))
                    return true;
            if (type == VIEW)
                if (isUdfAvailableForUser(UdfConstants.VIEW_SUBMITTER, udfId, prstatuses))
                    return true;
        }

        if (userId.equals(submitterid) || userId.equals(handlerUserId)) {
            if (type == EDIT)
                if (isUdfAvailableForUser(UdfConstants.EDIT_SUBMITTER_AND_HANDLER, udfId, prstatuses))
                    return true;
            if (type == VIEW)
                if (isUdfAvailableForUser(UdfConstants.VIEW_SUBMITTER_AND_HANDLER, udfId, prstatuses))
                    return true;
        }
        return false;
    }

    /**
     * Проверяет может ли пользователь редактировать пользовательское поле для задачи
     *
     * @param taskId ID задачи
     * @param userId ID пользователя
     * @param udfId  ID поля
     * @return TRUE если доступно, FALSE если нет
     * @throws GranException при необходимости
     */
    public boolean isTaskUdfEditable(String taskId, String userId, String udfId) throws GranException {
        return isTaskUdfEditable(taskId, userId, udfId, null);
    }

    /**
     * Проверяет может ли пользователь просматривать пользовательское поле для задачи
     *
     * @param taskId   ID задачи
     * @param userId   ID пользователя
     * @param udfId    ID поля
     * @return TRUE если доступно, FALSE если нет
     * @throws GranException при необходимости
     */
    public boolean isTaskUdfViewableFast(Set<String> prstatuses, String taskId, String userId, String udfId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            return isAvailableTaskUdfOperation(taskId, userId, udfId, VIEW, prstatuses, null, null);
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    public boolean isTaskUdfViewable(Set<String> prstatuses, String taskId, String userId, String udfId, String submitterId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            return isAvailableTaskUdfOperation(taskId, userId, udfId, VIEW, prstatuses, submitterId, null);
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Проверяет может ли пользователь редактировать пользовательское поле для задачи
     *
     * @param taskId   ID задачи
     * @param userId   ID пользователя
     * @param udfId    ID поля
     * @param statusId ID статуса
     * @return TRUE если доступно, FALSE если нет
     * @throws GranException при необходимости
     */
    public boolean isTaskUdfEditable(String taskId, String userId, String udfId, String statusId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            Set<String> prstatuses = TaskRelatedManager.getInstance().getAllowedPrstatuses(userId, taskId);
            return isAvailableTaskUdfOperation(taskId, userId, udfId, EDIT, prstatuses, null, null);
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Проверяет может ли пользователь редактировать пользовательское поле для создаваемой задачи
     *
     * @param taskId    ID задачи
     * @param userId    ID пользователя
     * @param udfId     ID пользовательского поля
     * @param statusId  ID статуса
     * @param submitter ID автора задачи
     * @param handler   ID ответственного задачи
     * @return TRUE - если может, FALSE - если нет
     * @throws GranException при необходимости
     */
    public boolean isNewTaskUdfEditable(String taskId, String userId, String udfId, String statusId, String submitter, String handler) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            Set<String> prstatuses = TaskRelatedManager.getInstance().getAllowedPrstatuses(userId, taskId);
            return isAvailableTaskUdfOperation(taskId, userId, udfId, EDIT, prstatuses, submitter, handler);
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Проверяет может ли пользователь редактировать пользовательское поле для создаваемого поля
     *
     * @param userId    ID пользователя
     * @param udfId     ID пользовательского поля
     * @param forUserId ID пользователя, для которого создается
     * @return TRUE - если может, FALSE - если нет
     * @throws GranException при необходимости
     */
    public boolean isUserUdfEditable(String userId, String forUserId, String udfId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            Set<String> prstatuses = UserRelatedManager.getInstance().getAllowedPrstatuses(userId, forUserId);
            return isAvailableUserUdfOperation(udfId, EDIT, prstatuses);
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Проверяет может ли пользователь просматривать пользовательское поле для создаваемого поля
     *
     * @param userId    ID пользователя
     * @param udfId     ID пользовательского поля
     * @param forUserId ID пользователя, для которого создается
     * @return TRUE - если может, FALSE - если нет
     * @throws GranException при необходимости
     */
    public boolean isUserUdfViewable(String userId, String forUserId, String udfId) throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            Set<String> prstatuses = UserRelatedManager.getInstance().getAllowedPrstatuses(userId, forUserId);
            return isAvailableUserUdfOperation(udfId, VIEW, prstatuses);
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Взвращает список доступных пользовательских полей для задачи
     *
     * @param taskId ID задачи, для которой возвращаются пользовательские поля
     * @return список полей
     * @throws GranException при необходимости
     * @see com.trackstudio.kernel.cache.UDFCacheItem
     */
    public List<UDFCacheItem> getAvailableTaskUDFCacheItems(String taskId) throws GranException {
        log.trace("########");
        boolean r = lockManager.acquireConnection(className);
        try {
            ArrayList<UDFCacheItem> result = new ArrayList<UDFCacheItem>();
            for (TaskRelatedInfo it : TaskRelatedManager.getInstance().getTaskRelatedInfoChain(null, taskId))
                result.addAll(getListTaskUDFCacheItem(it.getId()));
            return result;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Взвращает список всех доступных пользовательских полей для задачи
     *
     * @param taskId ID задачи, для которой возвращаются пользовательские поля
     * @return список полей
     * @throws GranException при необходимости
     * @see com.trackstudio.kernel.cache.UDFCacheItem
     */
    public List<UDFCacheItem> getAllAvailableTaskUDFCacheItems(String taskId) throws GranException {
        log.trace("########");
        boolean r = lockManager.acquireConnection(className);
        try {
            ArrayList<UDFCacheItem> result = new ArrayList<UDFCacheItem>();
            List<UDFCacheItem> list = hu.getList("select new com.trackstudio.kernel.cache.UDFCacheItem (udf.id, udf.udfsource.id, udf.udfsource.workflow.id, udf.udfsource.task.id, udf.udfsource.user.id, udf.caption, udf.referencedbycaption, udf.type, udf.order, udf.def, udf.initialtask.id, udf.initialuser.id, udf.required, udf.htmlview, udf.script, udf.cachevalues, udf.lookupscript, udf.lookuponly) from com.trackstudio.model.Udf as udf where udf.udfsource.task is not null order by udf.order");
            for (UDFCacheItem it : list) {
                if (TaskRelatedManager.getInstance().hasPath(taskId, it.getTaskId()))
                    result.add(it);
            }
            return result;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Взвращает список доступных пользовательских полей для пользователя
     *
     * @param userId ID пользователя, для которой возвращаются пользовательские поля
     * @return список полей
     * @throws GranException при необходимости
     * @see com.trackstudio.kernel.cache.UDFCacheItem
     */
    public List<UDFCacheItem> getAvailableUserUDFCacheItems(String userId) throws GranException {
        log.trace("########");
        boolean r = lockManager.acquireConnection(className);
        try {
            ArrayList<UDFCacheItem> result = new ArrayList<UDFCacheItem>();
            for (String s : UserRelatedManager.getInstance().getUserIdChain(null, userId))
                result.addAll(getListUserUDFCacheItem(s));
            return result;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Взвращает список всех доступных пользовательских полей для пользователя
     *
     * @param userId ID пользователя, для которой возвращаются пользовательские поля
     * @return список полей
     * @throws GranException при необходимости
     * @see com.trackstudio.kernel.cache.UDFCacheItem
     */
    public List<UDFCacheItem> getAllAvailableUserUDFCacheItems(String userId) throws GranException {
        log.trace("########");
        boolean r = lockManager.acquireConnection(className);
        try {
            ArrayList<UDFCacheItem> result = new ArrayList<UDFCacheItem>();
            List<UDFCacheItem> list = hu.getList("select new com.trackstudio.kernel.cache.UDFCacheItem (udf.id, udf.udfsource.id, udf.udfsource.workflow.id, udf.udfsource.task.id, udf.udfsource.user.id, udf.caption, udf.referencedbycaption, udf.type, udf.order, udf.def, udf.initialtask.id, udf.initialuser.id, udf.required, udf.htmlview, udf.script, udf.cachevalues, udf.lookupscript, udf.lookuponly) from com.trackstudio.model.Udf as udf where udf.udfsource.user is not null order by udf.order");
            for (UDFCacheItem udf : list) {
                if (UserRelatedManager.getInstance().hasPath(userId, udf.getUserId()))
                    result.add(udf);
            }
            return result;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Метод клонирует кастом поля
     * @param parendId идентификатор процесса
     * @param idUdf идентификатор кастом поля
     * @param typeParend родитель кастом поля (Задача, пользователь, процесс)
     * @throws GranException при необходимости
     */
    public void cloneUdf(String parendId, String idUdf, String typeParend, Map<String, String> mstatuses) throws GranException{
        Udf oldUdf = KernelManager.getFind().findUdf(idUdf);
        String udfId = "";
        if (typeParend.equals(WORKFLOW)) {
            udfId =createWorkflowUdf(
                    parendId, SafeString.createSafeString(oldUdf.getCaption()+"_clone"), SafeString.createSafeString(oldUdf.getReferencedbycaption()), oldUdf.getOrder(),
                    SafeString.createSafeString(oldUdf.getDef()), null, oldUdf.getRequired() == 1, oldUdf.getHtmlview() != null && oldUdf.getHtmlview() == 1, oldUdf.getType(),
                    oldUdf.getScript(), oldUdf.getLookupscript(), oldUdf.getLookuponly() != null && oldUdf.getLookuponly() == 1,
                    oldUdf.getCachevalues() != null && oldUdf.getCachevalues() == 1,
                    SafeString.createSafeString(oldUdf.getInitialtask() != null && oldUdf.getType() == UdfConstants.TASK ? oldUdf.getInitialtask().getId() : oldUdf.getInitialuser() != null && oldUdf.getType() == UdfConstants.USER ? oldUdf.getInitialuser().getId() : null));
        } else if (typeParend.equals(TASK)) {
            udfId =createTaskUdf(
                    parendId, SafeString.createSafeString(oldUdf.getCaption()+"_clone"), SafeString.createSafeString(oldUdf.getReferencedbycaption()), oldUdf.getOrder(),
                    SafeString.createSafeString(oldUdf.getDef()), null, oldUdf.getRequired() == 1, oldUdf.getHtmlview() != null && oldUdf.getHtmlview() == 1, oldUdf.getType(),
                    oldUdf.getScript(), oldUdf.getLookupscript(), oldUdf.getLookuponly() != null && oldUdf.getLookuponly() == 1,
                    oldUdf.getCachevalues() != null && oldUdf.getCachevalues() == 1,
                    SafeString.createSafeString(oldUdf.getInitialtask() != null && oldUdf.getType() == UdfConstants.TASK ? oldUdf.getInitialtask().getId() : oldUdf.getInitialuser() != null && oldUdf.getType() == UdfConstants.USER ? oldUdf.getInitialuser().getId() : null));
        } else if (typeParend.equals(USER)) {
            udfId =createUserUdf(
                    parendId, SafeString.createSafeString(oldUdf.getCaption()+"_clone"), SafeString.createSafeString(oldUdf.getReferencedbycaption()), oldUdf.getOrder(),
                    SafeString.createSafeString(oldUdf.getDef()), null, oldUdf.getRequired() == 1, oldUdf.getHtmlview() != null && oldUdf.getHtmlview() == 1, oldUdf.getType(),
                    oldUdf.getScript(), oldUdf.getLookupscript(), oldUdf.getLookuponly() != null && oldUdf.getLookuponly() == 1,
                    oldUdf.getCachevalues() != null && oldUdf.getCachevalues() == 1,
                    SafeString.createSafeString(oldUdf.getInitialtask() != null && oldUdf.getType() == UdfConstants.TASK ? oldUdf.getInitialtask().getId() : oldUdf.getInitialuser() != null && oldUdf.getType() == UdfConstants.USER ? oldUdf.getInitialuser().getId() : null));
        }
        log.debug("CLOSE SESSION WorkflowManager copy");
        for (Object o : hu.getList("select umstatus from com.trackstudio.model.Umstatus as umstatus where umstatus.udf=?", oldUdf.getId())) {
            Umstatus u = (Umstatus) o;
            String type = u.getType();
            String mstatusId = u.getMstatus().getId();
            if (mstatuses != null && mstatuses.get(mstatusId) != null) {
                mstatusId = mstatuses.get(mstatusId);
            }
            KernelManager.getUdf().setMstatusUDFRule(udfId, mstatusId, type);
        }
        for (Object o : hu.getList("select uprstatus from com.trackstudio.model.Uprstatus as uprstatus where uprstatus.udf=?", oldUdf.getId())) {
            Uprstatus uprs = (Uprstatus) o;
            String type = uprs.getType();
            KernelManager.getUdf().setUDFRule(udfId, uprs.getPrstatus().getId(), type);
        }
        HashMap<String, String> set4 = KernelManager.getUdf().getUdflist(oldUdf.getId());
        if (set4 != null && typeParend.equals(WORKFLOW)) {
            for (String ulid : set4.keySet()) {
                String value = set4.get(ulid);
                String newUlid = KernelManager.getUdf().addWorkflowUdflist(udfId, SafeString.createSafeString(value));
                if ((oldUdf.getType() == UdfValue.LIST || oldUdf.getType() == UdfValue.MULTILIST) && oldUdf.getDef() != null && oldUdf.getDef().equals(ulid)) {
                    if (typeParend.equals(WORKFLOW)) {
                        KernelManager.getUdf().updateWorkflowUdf(udfId, SafeString.createSafeString(oldUdf.getCaption()+"_clone"), SafeString.createSafeString(oldUdf.getReferencedbycaption()), oldUdf.getOrder(),
                                SafeString.createSafeString(newUlid), oldUdf.getRequired() == 1, oldUdf.getHtmlview() != null && oldUdf.getHtmlview() == 1, oldUdf.getScript(), oldUdf.getLookupscript(), oldUdf.getLookuponly() != null && oldUdf.getLookuponly() == 1, oldUdf.getCachevalues() != null && oldUdf.getCachevalues() == 1, SafeString.createSafeString(oldUdf.getInitialtask() != null && oldUdf.getType() == UdfConstants.TASK ? oldUdf.getInitialtask().getId() : oldUdf.getInitialuser() != null && oldUdf.getType() == UdfConstants.USER ? oldUdf.getInitialuser().getId() : null));
                    } else if (typeParend.equals(TASK)) {
                        KernelManager.getUdf().updateTaskUdf(udfId, SafeString.createSafeString(oldUdf.getCaption()+"_clone"), SafeString.createSafeString(oldUdf.getReferencedbycaption()), oldUdf.getOrder(),
                                SafeString.createSafeString(newUlid), oldUdf.getRequired() == 1, oldUdf.getHtmlview() != null && oldUdf.getHtmlview() == 1, oldUdf.getScript(), oldUdf.getLookupscript(), oldUdf.getLookuponly() != null && oldUdf.getLookuponly() == 1, oldUdf.getCachevalues() != null && oldUdf.getCachevalues() == 1, SafeString.createSafeString(oldUdf.getInitialtask() != null && oldUdf.getType() == UdfConstants.TASK ? oldUdf.getInitialtask().getId() : oldUdf.getInitialuser() != null && oldUdf.getType() == UdfConstants.USER ? oldUdf.getInitialuser().getId() : null));
                    } else {
                        KernelManager.getUdf().updateUserUdf(udfId, SafeString.createSafeString(oldUdf.getCaption()+"_clone"), SafeString.createSafeString(oldUdf.getReferencedbycaption()), oldUdf.getOrder(),
                                SafeString.createSafeString(newUlid), oldUdf.getRequired() == 1, oldUdf.getHtmlview() != null && oldUdf.getHtmlview() == 1, oldUdf.getScript(), oldUdf.getLookupscript(), oldUdf.getLookuponly() != null && oldUdf.getLookuponly() == 1, oldUdf.getCachevalues() != null && oldUdf.getCachevalues() == 1, SafeString.createSafeString(oldUdf.getInitialtask() != null && oldUdf.getType() == UdfConstants.TASK ? oldUdf.getInitialtask().getId() : oldUdf.getInitialuser() != null && oldUdf.getType() == UdfConstants.USER ? oldUdf.getInitialuser().getId() : null));
                    }
                }
            }
        }
    }

    public List<Udf> getListTriggerScript() throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            return hu.getList("select u from com.trackstudio.model.Udf u where u.script != ''");
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    public List<Udf> getListTriggerLookup() throws GranException {
        boolean r = lockManager.acquireConnection(className);
        try {
            return hu.getList("select u from com.trackstudio.model.Udf u where u.lookupscript != ''");
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }
}