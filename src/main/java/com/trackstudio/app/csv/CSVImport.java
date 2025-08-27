package com.trackstudio.app.csv;

import java.util.ArrayList;
import java.util.List;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.exception.UserException;
import com.trackstudio.kernel.cache.TaskRelatedInfo;
import com.trackstudio.kernel.cache.TaskRelatedManager;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.model.Workflow;
import com.trackstudio.secured.SecuredCategoryBean;
import com.trackstudio.secured.SecuredMstatusBean;
import com.trackstudio.secured.SecuredPrstatusBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUDFBean;
import com.trackstudio.secured.SecuredUdflistBean;
import com.trackstudio.tools.HibernateUtil;

import net.jcip.annotations.Immutable;

/**
 * Класс по хорошему надо отрефакторить и перенести методы FindManager.
 * Оставлен для совместимости со скриптами из версии 3.5
 */
@Immutable
@Deprecated
public class CSVImport {
    private static final LockManager lockManager = LockManager.getInstance();


    /**
     * Тип объекта
     */
    public static final String OBJECT_TYPE = "OBJECT_TYPE";
    /**
     * ID объекта
     */
    public static final String OBJECT_ID = "OBJECT_ID";
    /**
     * Исключение для объекта
     */
    public static final String OBJECT_EXCEPTION = "OBJECT_EXCEPTION";
    /**
     * Тип задачи
     */
    public static final String TASK_TYPE = "TASK_TYPE";
    /**
     * Тип сообщения
     */
    public static final String MESSAGE_TYPE = "MESSAGE_TYPE";
    /**
     * Тип пользователя
     */
    public static final String USER_TYPE = "USER_TYPE";
    /**
     * Алиас задачи
     */
    public static final String TASK_SHORTNAME = "TASK_SHORTNAME";
    /**
     * Название задачи
     */
    public static final String TASK_NAME = "TASK_NAME";
    /**
     * Описание задачи
     */
    public static final String TASK_DESCRIPTION = "TASK_DESCRIPTION";
    /**
     * Бюджет задачи
     */
    public static final String TASK_BUDGET = "TASK_BUDGET";
    /**
     * Дедлайн задачи
     */
    public static final String TASK_DEADLINE = "TASK_DEADLINE";
    /**
     * ID приоритета задачи
     */
    public static final String TASK_PRIORITY_ID = "TASK_PRIORITY_ID";
    //public static final String TASK_PARENT_ID = "TASK_PARENT_ID";
    /**
     * ID ответственного пользователя
     */
    public static final String TASK_HANDLER_USER_ID = "TASK_HANDLER_USER_ID";
    /**
     * ID ответственной группы
     */
    public static final String TASK_HANDLER_GROUP_ID = "TASK_HANDLER_GROUP_ID";
    /**
     * ID категории
     */
    public static final String TASK_CATEGORY_ID = "TASK_CATEGORY_ID";
    /**
     * ID автора задачи
     */
    public static final String TASK_SUBMITTER_ID = "TASK_SUBMITTER_ID";
    /**
     * Дата создания задачи
     */
    public static final String TASK_SUBMIT_DATE = "TASK_SUBMIT_DATE";
    /**
     * Дата обновления задачи
     */
    public static final String TASK_UPDATE_DATE = "TASK_UPDATE_DATE";
    /**
     * Дата закрытия задачи
     */
    public static final String TASK_CLOSE_DATE = "TASK_CLOSE_DATE";
    /**
     * ID резолюции
     */
    public static final String TASK_RESOLUTION_ID = "TASK_RESOLUTION_ID";
    /**
     * ID статуса
     */
    public static final String TASK_STATUS_ID = "TASK_STATUS_ID";
    /**
     * Карта пользовательских полей
     */
    public static final String TASK_UDF_MAP = "TASK_UDF_MAP";
    /**
     * Родительская задача
     */
    public static final String TASK_PARENT_TASK_ID = "TASK_PARENT_TASK_ID";
    /**
     * ID родительской задачи
     */
    public static final String TASK_PARENT_TASK = "TASK_PARENT_TASK";
    /**
     * Логин пользователя
     */
    public static final String USER_LOGIN = "USER_LOGIN";
    /**
     * Имя пользователя
     */
    public static final String USER_NAME = "USER_NAME";
    /**
     * Телефон пользователя
     */
    public static final String USER_PHONE = "USER_PHONE";
    /**
     * Электронная почта пользователя
     */
    public static final String USER_EMAIL = "USER_EMAIL";
    /**
     * ID статуса пользователя
     */
    public static final String USER_PRSTATUS_ID = "USER_PRSTATUS_ID";
    //public static final String USER_MANAGER_ID = "USER_MANAGER_ID";
    /**
     * Таймзона пользователя
     */
    public static final String USER_TIMEZONE = "USER_TIMEZONE";
    /**
     * Локаль пользователя
     */
    public static final String USER_LOCALE = "USER_LOCALE";
    /**
     * Компанния пользователя
     */
    public static final String USER_COMPANY = "USER_COMPANY";
    /**
     * ID проекта по умолчанию
     */
    public static final String USER_DEFAULT_PROJECT_ID = "USER_DEFAULT_PROJECT_ID";
    /**
     * Дата истекания срока действия пользователя
     */
    public static final String USER_EXPIRE_DATE = "USER_EXPIRE_DATE";
    /**
     * Активный пользователь или нет
     */
    public static final String USER_IS_ACTIVE = "USER_IS_ACTIVE";
    /**
     * Тип отображения дерева пользователей
     */
    public static final String USER_SHOW_TREE_MODE = "USER_SHOW_TREE_MODE";
    /**
     * Карта пользовательских полей для пользователей
     */
    public static final String USER_UDF_MAP = "USER_UDF_MAP";
    /**
     * Родительских пользователь
     */
    public static final String USER_PARENT_USER = "USER_PARENT_USER";
    /**
     * ID родительского пользователя
     */
    public static final String USER_PARENT_USER_ID = "USER_PARENT_USER_ID";
    /**
     * Пароль пользователя
     */
    public static final String USER_PASSWORD = "USER_PASSWORD";
    /**
     * ID задачи для сообщения
     */
    public static final String MESSAGE_TASK_ID = "MESSAGE_TASK_ID";
    /**
     * ID типа сообщения
     */
    public static final String MESSAGE_MESSAGE_TYPE_ID = "MESSAGE_MESSAGE_TYPE_ID";
    /**
     * Описание сообщения
     */
    public static final String MESSAGE_DESCRIPTION = "MESSAGE_DESCRIPTION";
    /**
     * Потраченное время
     */
    public static final String MESSAGE_HOURS = "MESSAGE_HOURS";
    /**
     * ID ответственного пользователя
     */
    public static final String MESSAGE_HANDLER_USER_ID = "MESSAGE_HANDLER_USER_ID";
    /**
     * ID ответственной группы
     */
    public static final String MESSAGE_HANDLER_GROUP_ID = "MESSAGE_HANDLER_GROUP_ID";
    /**
     * ID резолюции
     */
    public static final String MESSAGE_RESOLUTION_ID = "MESSAGE_RESOLUTION_ID";
    /**
     * ID приоритета
     */
    public static final String MESSAGE_PRIORITY_ID = "MESSAGE_PRIORITY_ID";
    /**
     * Дедлайн сообщения
     */
    public static final String MESSAGE_DEADLINE = "MESSAGE_DEADLINE";
    /**
     * Алиас сообщения
     */
    public static final String MESSAGE_SUBMIT_DATE = "MESSAGE_DATE";
    /**
     * ID автора сообщения
     */
    public static final String MESSAGE_SUBMITTER_ID = "MESSAGE_SUBMITTER_ID";
    /**
     * Бюджет сообщения
     */
    public static final String MESSAGE_BUDGET = "MESSAGE_BUDGET";
    /**
     * Карта пользовательских полей
     */
    public static final String MESSAGE_UDF_MAP = "MESSAGE_UDF_MAP";

    private static final HibernateUtil hu = new HibernateUtil();
    private static final String className = "CSVImport.";
    /**
     * Пустое название задачи
     */
    protected static final String TASK_EMPTY_NAME = "Not specified";
    /**
     * Пустой логин задачи
     */
    protected static final String USER_EMPTY_LOGIN = "Not specified";
    /**
     * Пустое имя пользователя
     */
    protected static final String USER_EMPTY_NAME = "Not specified";

    /**
     * This constrain for message audit trail
     */
    public static final String LOG_MESSAGE = "*";

    /**
     * Возвращает ID категории по ее названию
     *
     * @param categoryName название категории
     * @return ID категории
     * @throws GranException при необходимости
     */
    public static String findCategoryIdByName(String categoryName) throws GranException {
        if (categoryName == null || categoryName.length()==0)
            return null;
        boolean r = lockManager.acquireConnection(className);
        try {
            List<String> list = hu.getList("select c.id from com.trackstudio.model.Category c where c.name=?", categoryName);
            if (list==null || list.isEmpty())
                return null;
            if (list.size() > 1)
                throw new UserException("ERROR_CATEGORY_NAME_IS_NOT_UNIQUE", new String[]{categoryName});
            return list.get(0);
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает ID категории по ее названию
     *
     * @param sc           сессия пользователя
     * @param categoryName название категории
     * @return ID категории
     * @throws GranException при необходимости
     */
    public static String findCategoryIdByName(SessionContext sc, String categoryName) throws GranException {
        if (categoryName == null)
            return null;
        boolean r = lockManager.acquireConnection(className);
        try {
            List<String> list = hu.getList("select c.id from com.trackstudio.model.Category c where c.name=?", categoryName);
            List<String> catids = new ArrayList<String>();
            if (list!=null) for (String s : list) {
                SecuredCategoryBean scb = AdapterManager.getInstance().getSecuredFindAdapterManager().findCategoryById(sc, s);
                if (scb != null && scb.canView())
                    catids.add(s);
            }
            if (catids.isEmpty())
                return null;
            if (catids.size() > 1)
                throw new UserException("ERROR_CATEGORY_NAME_IS_NOT_UNIQUE", new String[]{categoryName});
            return catids.get(0);
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает ID категории по ее названию
     *
     * @param sc           сессия пользователя
     * @param parentTaskId ID родительской задачи
     * @param categoryName название категории
     * @return ID категории
     * @throws GranException при необходимости
     */
    public static String findCategoryIdByName(SessionContext sc, String categoryName, String parentTaskId) throws GranException {
        if (categoryName == null)
            return null;
        boolean r = lockManager.acquireConnection(className);
        try {
            List<String> list = hu.getList("select c.id from com.trackstudio.model.Category c where c.name=?", categoryName);
            List<String> catids = new ArrayList<String>();
            if (list!=null) for (String s : list) {
                SecuredCategoryBean scb = AdapterManager.getInstance().getSecuredFindAdapterManager().findCategoryById(sc, s);
                if (scb != null && scb.canView() && TaskRelatedManager.getInstance().hasPath(parentTaskId, scb.getTaskId()))
                    catids.add(s);
            }
            if (catids.isEmpty())
                return null;
            if (catids.size() > 1)
                throw new UserException("ERROR_CATEGORY_NAME_IS_NOT_UNIQUE", new String[]{categoryName});
            return catids.get(0);
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает ID резолюции по ее названию
     *
     * @param resolutionName  название резолюции
     * @param messageTypeName название типа сообщения
     * @return ID резолюции
     * @throws GranException при необходимости
     */
    public static String findResolutionIdByName(String resolutionName, String messageTypeName) throws GranException {
        if (resolutionName == null)
            return null;
        boolean r = lockManager.acquireConnection(className);
        try {
            List<String> list = hu.getList("select r.id from com.trackstudio.model.Resolution r, com.trackstudio.model.Mstatus m where r.name=? and m.name=? and r.mstatus=m.id", resolutionName, messageTypeName);
            if (list==null || list.isEmpty())
                return null;
            if (list.size() > 1)
                throw new UserException("ERROR_RESOLUTION_NAME_IS_NOT_UNIQUE", new String[]{resolutionName});
            return list.get(0);
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает ID резолюции по ее названию
     *
     * @param resolutionName название резолюции
     * @param messageTypeId  ID типа сообщения
     * @return ID резолюции
     * @throws GranException при необходимости
     */
    public static String findResolutionIdByNameInMstatus(String resolutionName, String messageTypeId) throws GranException {
        if (resolutionName == null)
            return null;
        boolean r = lockManager.acquireConnection(className);
        try {
            List<String> list = hu.getList("select r.id from com.trackstudio.model.Resolution r, com.trackstudio.model.Mstatus m where r.name=? and m.id=? and r.mstatus=m.id", resolutionName, messageTypeId);
            if (list==null || list.isEmpty()) return null;
            else return list.get(0);
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает ID резолюции для задачи
     *
     * @param resolutionName название резолюции
     * @param categoryName   название категории
     * @return ID резолюции
     * @throws GranException при необходимости
     */
    public static String findTaskResolutionIdByName(String resolutionName, String categoryName) throws GranException {
        if (resolutionName == null)
            return null;
        boolean r = lockManager.acquireConnection(className);
        try {
            List<String> list = hu.getList("select r.id from com.trackstudio.model.Resolution r, com.trackstudio.model.Mstatus m, com.trackstudio.model.Category c where r.name=? and c.name=? and r.mstatus=m.id and m.workflow=c.workflow", resolutionName, categoryName);
            if (list==null || list.isEmpty())
                return null;
            if (list.size() > 1)
                throw new UserException("ERROR_RESOLUTION_NAME_IS_NOT_UNIQUE", new String[]{resolutionName});
            return list.get(0);
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает ID резолюции для задачи
     *
     * @param resolutionName название резолюции
     * @param categoryId     ID категории
     * @return ID резолюции
     * @throws GranException при необходимости
     */
    public static String findTaskResolutionIdByNameInCategory(String resolutionName, String categoryId) throws GranException {
        if (resolutionName == null)
            return null;
        boolean r = lockManager.acquireConnection(className);
        try {
            List<String> list = hu.getList("select r.id from com.trackstudio.model.Resolution r, com.trackstudio.model.Mstatus m, com.trackstudio.model.Category c where r.name=? and c.id=? and r.mstatus=m.id and m.workflow=c.workflow", resolutionName, categoryId);
            if (list==null || list.isEmpty()) return null;
            else return list.get(0);
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает ID приоритета для задачи
     *
     * @param priorityName название приоритета
     * @param categoryName название категории
     * @return ID приоритета
     * @throws GranException при необходимости
     */
    public static String findPriorityIdByName(String priorityName, String categoryName) throws GranException {
        if (priorityName == null)
            return null;
        boolean r = lockManager.acquireConnection(className);
        try {
            List<String> list = hu.getList("select p.id from com.trackstudio.model.Priority p, com.trackstudio.model.Category w where p.name=? and w.name=? and p.workflow=w.workflow", priorityName, categoryName);
            if (list==null || list.isEmpty())
                return null;
            if (list.size() > 1)
                throw new UserException("ERROR_PRIORITY_NAME_IS_NOT_UNIQUE", new String[]{priorityName});
            return list.get(0);
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает ID приоритета для задачи
     *
     * @param priorityName название приоритета
     * @param categoryId   ID категории
     * @return ID приоритета
     * @throws GranException при необходимости
     */
    public static String findPriorityIdByNameInCategory(String priorityName, String categoryId) throws GranException {
        if (priorityName == null || priorityName.length()==0)
            return null;
        boolean r = lockManager.acquireConnection(className);
        try {
            List<String> list = hu.getList("select p.id from com.trackstudio.model.Priority p, com.trackstudio.model.Category w where p.name=? and w.id=? and p.workflow=w.workflow", priorityName, categoryId);
            if (list!=null && !list.isEmpty())
                return list.get(0);
            else return null;
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает ID состояния по его имени
     *
     * @param stateName    название состояния
     * @param categoryName название категории
     * @return ID состояния
     * @throws GranException при необходимости
     */
    public static String findStateIdByName(String stateName, String categoryName) throws GranException {
        if (stateName == null)
            return null;
        boolean r = lockManager.acquireConnection(className);
        try {
            List<String> list = hu.getList("select s.id from com.trackstudio.model.Status s, com.trackstudio.model.Category c where s.name=? and c.name=? and c.workflow=s.workflow", stateName, categoryName);
            if (list.isEmpty())
                return null;
            if (list.size() > 1)
                throw new UserException("ERROR_STATUS_NAME_IS_NOT_UNIQUE", new String[]{stateName});
            return list.get(0);
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает ID состояния по его имени
     *
     * @param stateName  название состояния
     * @param categoryId ID категории
     * @return ID состояния
     * @throws GranException при необходимости
     */
    public static String findStateIdByNameInCategory(String stateName, String categoryId) throws GranException {
        if (stateName == null)
            return null;
        boolean r = lockManager.acquireConnection(className);
        try {
            List<String> list = hu.getList("select s.id from com.trackstudio.model.Status s, com.trackstudio.model.Category c where s.name=? and c.id=? and c.workflow=s.workflow", stateName, categoryId);
            if (list==null || list.isEmpty()) return null;
            else return list.get(0);
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает ID задачи по ее названию
     *
     * @param taskName название задачи
     * @return ID задачи
     * @throws GranException при необходимости
     */
    public static String findTaskIdByName(String taskName) throws GranException {
        TaskRelatedInfo tci = TaskRelatedManager.getInstance().findByName(taskName);
        if (tci!=null) {
            return tci.getId();
        } else {
            return null;
        }
    }

    /**
     * Возвращает ID задачи по ее названию
     *
     * @param sc       сессия пользователя
     * @param taskName название задачи
     * @return ID задачи
     * @throws GranException при необходимости
     */
    public static String findTaskIdByName(SessionContext sc, String taskName) throws GranException {
        return findTaskIdByName(taskName); // sc not required
    }

    /**
     * Возвращает ID пользователя по его имени
     *
     * @param userName имя пользователя
     * @return ID пользователя
     * @throws GranException при необходимости
     */
    public static String findUserIdByName(String userName) throws GranException {
        if (userName == null)
            return null;
        return KernelManager.getUser().findByName(userName);
    }

    /**
     * Возвращает ID пользователя по его имени
     *
     * @param sc       сессия пользователя
     * @param userName имя пользователя
     * @return ID пользователя
     * @throws GranException при необходимости
     */
    public static String findUserIdByName(SessionContext sc, String userName) throws GranException {
        if (userName == null)
            return null;
        return KernelManager.getUser().findByName(sc, userName);
    }

    /**
     * Возвращает ID пользователя по его логину
     *
     * @param userLogin логин пользователя
     * @return ID пользователя
     * @throws GranException при необходимости
     */
    public static String findUserIdByLogin(String userLogin) throws GranException {
        if (userLogin == null)
            return null;
        return KernelManager.getUser().findByLogin(userLogin);
    }

    /**
     * Возвращает ID пользователя по его логину
     *
     * @param sc        сессия пользователя
     * @param userLogin логин пользователя
     * @return ID пользователя
     * @throws GranException при необходимости
     */
    public static String findUserIdByLogin(SessionContext sc, String userLogin) throws GranException {
        if (userLogin == null)
            return null;
        return KernelManager.getUser().findByLogin(sc, userLogin);
    }

    /**
     * Возвращает ID задачи по ее номеру
     *
     * @param taskNumber номер задачи
     * @return ID задачи
     * @throws GranException при необходимости
     */
    public static String findTaskIdByNumber(String taskNumber) throws GranException {
        TaskRelatedInfo tci = TaskRelatedManager.getInstance().findByNumber(taskNumber);
        if (tci!=null) {
            return tci.getId();
        } else {
            return null;
        }
    }

    /**
     * Возвращает ID задачи по ее номеру
     *
     * @param sc         сессия пользователя
     * @param taskNumber номер задачи
     * @return ID задачи
     * @throws GranException при необходимости
     */
    public static String findTaskIdByNumber(SessionContext sc, String taskNumber) throws GranException {
        return findTaskIdByNumber(taskNumber); // ignore sc
    }

    /**
     * Возвращает ID типа сообщения по его названию
     *
     * @param mstatusName  название типа сообщения
     * @param categoryName название категории
     * @return ID типа сообщения
     * @throws GranException при необходимости
     */
    public static String findMessageTypeIdByName(String mstatusName, String categoryName) throws GranException {
        if (mstatusName == null)
            return null;
        boolean r = lockManager.acquireConnection(className);
        try {
            List<String> list = hu.getList("select m.id from com.trackstudio.model.Mstatus m, com.trackstudio.model.Category w where m.name=? and w.name=? and m.workflow=w.workflow", mstatusName, categoryName);
            if (list.isEmpty())
                return null;
            if (list.size() > 1)
                throw new UserException("ERROR_MSTATUS_NAME_IS_NOT_UNIQUE", new String[]{mstatusName});
            return list.get(0);
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает ID типа сообщения по его названию
     *
     * @param sc           сессия пользователя
     * @param mstatusName  название типа сообщения
     * @param categoryName название категории
     * @return ID типа сообщения
     * @throws GranException при необходимости
     */
    public static String findMessageTypeIdByName(SessionContext sc, String mstatusName, String categoryName) throws GranException {
        if (mstatusName == null)
            return null;
        boolean r = lockManager.acquireConnection(className);
        try {
            List<String> list = hu.getList("select m.id from com.trackstudio.model.Mstatus m, com.trackstudio.model.Category w where m.name=? and w.name=? and m.workflow=w.workflow", mstatusName, categoryName);
            List<String> catids = new ArrayList<String>();
            for (String s : list) {
                SecuredMstatusBean smb = AdapterManager.getInstance().getSecuredFindAdapterManager().findMstatusById(sc, s);
                if (smb != null && smb.getCanView()) catids.add(s);
            }
            if (catids.isEmpty())
                return null;
            if (catids.size() > 1)
                throw new UserException("ERROR_MSTATUS_NAME_IS_NOT_UNIQUE", new String[]{mstatusName});
            return catids.get(0);
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает ID статуса пользователя
     *
     * @param prstatusName название статуса
     * @return ID статуса
     * @throws GranException при необходимости
     */
    public static String findUserStatusIdByName(String prstatusName) throws GranException {
        if (prstatusName == null)
            return null;
        boolean r = lockManager.acquireConnection(className);
        try {
            List<String> list = hu.getList("select p.id from com.trackstudio.model.Prstatus p where p.name=?", prstatusName);
            if (list.isEmpty())
                return null;
            if (list.size() > 1)
                throw new UserException("ERROR_PRSTATUS_NAME_IS_NOT_UNIQUE", new String[]{prstatusName});
            return list.get(0);
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает ID статуса пользователя
     *
     * @param sc           сессия пользователя
     * @param prstatusName название статуса
     * @return ID статуса
     * @throws GranException при необходимости
     */
    public static String findUserStatusIdByName(SessionContext sc, String prstatusName) throws GranException {
        if (prstatusName == null)
            return null;
        boolean r = lockManager.acquireConnection(className);
        try {
            List<String> list = hu.getList("select p.id from com.trackstudio.model.Prstatus p where p.name=?", prstatusName);
            List<String> catids = new ArrayList<String>();
            for (String s : list) {
                SecuredPrstatusBean spb = AdapterManager.getInstance().getSecuredFindAdapterManager().findPrstatusById(sc, s);
                if (spb != null && spb.canView()) catids.add(s);
            }
            if (catids.isEmpty())
                return null;
            if (catids.size() > 1)
                throw new UserException("ERROR_PRSTATUS_NAME_IS_NOT_UNIQUE", new String[]{prstatusName});
            return catids.get(0);
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает ID пользовательского поля по его названию
     *
     * @param udfName название поля
     * @return ID поля
     * @throws GranException при необходимости
     */
    public static String findUDFIdByName(String udfName) throws GranException {
        if (udfName == null)
            return null;
        boolean r = lockManager.acquireConnection(className);
        try {
            List<String> list = hu.getList("select u.id from com.trackstudio.model.Udf u where u.caption=?", udfName);
            if (list.isEmpty())
                return null;
            if (list.size() > 1)
                throw new UserException("ERROR_UDF_NAME_IS_NOT_UNIQUE", new String[]{udfName});
            return list.get(0);
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает ID пользовательского поля по его названию
     *
     * @param sc      сессия пользователя
     * @param udfName название поля
     * @return ID поля
     * @throws GranException при необходимости
     */
    public static String findUDFIdByName(SessionContext sc, String udfName) throws GranException {
        if (udfName == null)
            return null;
        boolean r = lockManager.acquireConnection(className);
        try {
            List<String> list = hu.getList("select u.id from com.trackstudio.model.Udf u where u.caption=?", udfName);
            List<String> catids = new ArrayList<String>();
            for (String s : list) {
                SecuredUDFBean sub = AdapterManager.getInstance().getSecuredFindAdapterManager().findUDFById(sc, s);
                if (sub != null && sub.canView()) catids.add(s);
            }
            if (catids.isEmpty())
                return null;
            if (catids.size() > 1)
                throw new UserException("ERROR_UDF_NAME_IS_NOT_UNIQUE", new String[]{udfName});
            return catids.get(0);
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает ID списка значений
     *
     * @param udflistValue значение
     * @return ID списка значений
     * @throws GranException при необходимости
     */
    public static String findUDFListIdByValue(String udflistValue) throws GranException {
        if (udflistValue == null)
            return null;
        boolean r = lockManager.acquireConnection(className);
        try {
            List<String> list = hu.getList("select ul.id from com.trackstudio.model.Udflist ul where ul.val=?", udflistValue);
            if (list.isEmpty())
                return null;
            if (list.size() > 1)
                throw new UserException("ERROR_UDFLIST_NAME_IS_NOT_UNIQUE", new String[]{udflistValue});
            return list.get(0);
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    /**
     * Возвращает ID списка значений
     *
     * @param sc           сессия пользователя
     * @param udflistValue значение
     * @return ID списка значений
     * @throws GranException при необходимости
     */
    public static String findUDFListIdByValue(SessionContext sc, String udflistValue) throws GranException {
        if (udflistValue == null)
            return null;
        boolean r = lockManager.acquireConnection(className);
        try {
            List<String> list = hu.getList("select ul.id from com.trackstudio.model.Udflist ul where ul.val=?", udflistValue);
            List<String> catids = new ArrayList<String>();
            for (String s : list) {
                SecuredUdflistBean sulb = AdapterManager.getInstance().getSecuredFindAdapterManager().findUdflistById(sc, s);
                if (sulb != null && sulb.canView()) catids.add(s);
            }
            if (catids.isEmpty())
                return null;
            if (catids.size() > 1)
                throw new UserException("ERROR_UDFLIST_NAME_IS_NOT_UNIQUE", new String[]{udflistValue});
            return catids.get(0);
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }

    public static String findWorkflowIdByName(SessionContext sc, String workflowName) throws GranException {
        if (workflowName == null)
            return null;
        boolean r = lockManager.acquireConnection(className);
        try {
            List<String> list = hu.getList("select workflow.id from " + Workflow.class.getName() + " as workflow where workflow.name=?", workflowName);
            if (list.isEmpty())
                return null;
            if (list.size() > 1)
                throw new UserException("ERROR_WORKFLOW_NAME_IS_NOT_UNIQUE", new String[]{workflowName});
            return list.get(0);
        } finally {
            if (r) lockManager.releaseConnection(className);
        }
    }
}
