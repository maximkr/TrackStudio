package com.trackstudio.securedkernel;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DrbgParameters;
import java.sql.*;
import java.util.*;

import javax.mail.Session;

import com.trackstudio.app.UdfValue;
import com.trackstudio.app.session.SessionManager;
import com.trackstudio.app.udf.GenericValue;
import com.trackstudio.kernel.cache.*;
import com.trackstudio.kernel.manager.*;
import com.trackstudio.model.*;
import com.trackstudio.secured.*;
import com.trackstudio.startup.Config;
import com.trackstudio.tools.textfilter.MacrosUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.Slider;
import com.trackstudio.app.TriggerManager;
import com.trackstudio.app.UDFFormFillHelper;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.csv.CSVImport;
import com.trackstudio.app.filter.FValue;
import com.trackstudio.app.filter.TaskFValue;
import com.trackstudio.app.filter.list.TaskFilter;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.builder.TaskBuilder;
import com.trackstudio.constants.CommonConstants;
import com.trackstudio.constants.UdfConstants;
import com.trackstudio.exception.AccessDeniedException;
import com.trackstudio.exception.GranException;
import com.trackstudio.exception.InvalidParameterException;
import com.trackstudio.exception.UserException;
import com.trackstudio.index.DocumentBuilder;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.cache.TaskRelatedInfo;
import com.trackstudio.kernel.cache.TaskRelatedManager;
import com.trackstudio.kernel.cache.UserRelatedInfo;
import com.trackstudio.kernel.cache.UserRelatedManager;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.kernel.manager.AttachmentArray;
import com.trackstudio.kernel.manager.IndexManager;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.kernel.manager.SafeString;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.ParameterValidator;
import com.trackstudio.tools.SecuredBeanUtil;
import com.trackstudio.tools.textfilter.HTMLEncoder;

import net.jcip.annotations.Immutable;
import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.jdbc.Work;
import org.hibernate.query.Query;
import org.json.JSONArray;
import org.json.JSONObject;

import static com.trackstudio.tools.textfilter.MacrosUtil.calendarToMls;
import static com.trackstudio.tools.textfilter.MacrosUtil.getHeadersMap;
import static com.trackstudio.tools.textfilter.MacrosUtil.mlsToCalendar;

/**
 * Класс SecuredTaskAdapterManager содержит методы для работы с задачами
 */
@Immutable
public class SecuredTaskAdapterManager {

    private static final Log log = LogFactory.getLog(SecuredTaskAdapterManager.class);

    private static final ParameterValidator pv = new ParameterValidator();

    private final LockManager lockManager = LockManager.getInstance();

    private static final String className = "SecuredTaskAdapterManager.";

    /**
     * Создает новую задачу
     *
     * @param sc         сессия пользователя
     * @param taskId     ID родительской задачи
     * @param categoryId ID категории
     * @param name       Название задачи
     * @param submitDate дата создания задачи
     * @param updateDate дата обновление задачи
     * @return ID созданной задачи
     * @throws GranException при необходимости
     */
    public String createTask(SessionContext sc, String taskId, String categoryId, String name, Calendar submitDate, Calendar updateDate) throws GranException {
        return createTask(sc, taskId, categoryId, name, submitDate, updateDate, null, null, false);
    }

    public String createTask(
            SessionContext sc,
            String taskId,
            String categoryId,
            String name,
            Calendar submitDate,
            Calendar updateDate,
            String statusId,
            String submitterId,
            boolean copyorMoveOpr) throws GranException {
        log.trace("createTask");
        if (sc == null) {
            throw new InvalidParameterException(this.getClass(), "createTask", "sc", sc);
        }
        if (taskId == null) {
            taskId = "1";
            log.fatal("taskId was null");
        }

        //    throw new InvalidParameterException(this.getClass(), "createTask", "taskId", sc);
        if (categoryId == null) {
            throw new InvalidParameterException(this.getClass(), "createTask", "categoryId", sc);
        }
        name = checkTaskName(name);
        if (pv.badTaskName(name)) {
            throw new InvalidParameterException(this.getClass(), "createTask", name, sc);
        }
        SecuredCategoryBean category = AdapterManager.getInstance().getSecuredFindAdapterManager().findCategoryById(sc, categoryId);

        ArrayList availableCategoryList = AdapterManager.getInstance().getSecuredCategoryAdapterManager().getAvailableCategoryList(sc, taskId);
        ArrayList creatableCategoryList = AdapterManager.getInstance().getSecuredCategoryAdapterManager().getCreatableCategoryList(sc, taskId);

        if (!copyorMoveOpr) {
            boolean b = availableCategoryList.contains(category);
            boolean c = creatableCategoryList.contains(category);
            boolean allowedCategory = b && c;
            if (!allowedCategory) {
                throw new UserException("ERROR_INCORRECT_CATEGORY", new Object[]{category.getName()});
            }
        }
        //if (!(sc.canAction(Action.createTask, taskId) && sc.allowedByACL(taskId)))
        //    throw new AccessDeniedException(this.getClass(), "createTask", sc);
        if (!sc.allowedByACL(taskId)) {
            throw new AccessDeniedException(this.getClass(), "createTask", sc, "!sc.allowedByACL(taskId)", taskId);
        }
        if (statusId == null) {
            statusId = KernelManager.getWorkflow().getStartStateId(category.getWorkflowId());
        }
        if (statusId == null) {
            throw new UserException("ERROR_WORKFLOW_HAS_NOT_START_STATE", new Object[]{category.getWorkflow().getName()});
        }
        TaskRelatedInfo parent = TaskRelatedManager.getInstance().find(taskId);
        Calendar deadline = sc.canAction(Action.editTaskDeadline, taskId) ? parent.getDeadline() : null;
        String id = KernelManager.getTask()
                .createTask(taskId,
                        (submitterId == null) ? sc.getUserId() : submitterId,
                        categoryId,
                        SafeString.createSafeString(name),
                        deadline,
                        submitDate,
                        updateDate,
                        statusId);
        Status startStatus = KernelManager.getFind().findStatus(statusId);
        if (KernelManager.getWorkflow().getStateList(category.getWorkflowId()).size() == 1 && startStatus.isFinish()) {
            Calendar closeDate = Calendar.getInstance();
            closeDate.setTimeInMillis(System.currentTimeMillis());
            KernelManager.getTask().updateTaskCloseDate(id, closeDate);
        }
        return id;
    }

    /**
     * Создает новую задачу
     *
     * @param sc         сессия пользователя
     * @param taskId     ID родительской задачи
     * @param categoryId ID категории
     * @param name       Название задачи
     * @return ID созданной задачи
     * @throws GranException при необходимости
     */
    public String createTask(SessionContext sc, String taskId, String categoryId, String name) throws GranException {
        return createTask(sc, taskId, categoryId, name, null, null);
    }

    /**
     * Проверяет корректность названия задачи
     *
     * @param name название задачи
     * @return обработанной время
     * @throws GranException в случае превышения длины имени в 200 символов
     */
    private String checkTaskName(String name) throws GranException {
        if (name != null && name.length() > 200) {
            HTMLEncoder sb = new HTMLEncoder(name);
            sb.replace("\r\n", " ");
            name = sb.toString();
            if (name.length() > 200) {
                throw new GranException("Task name is too long");
            }
        }
        return name;
    }

    /**
     * Проверяет, можно ли редактировать СУЩЕСТВУЮЩУЮ задачу. Задачу можно редактировать, если к ней есть доступ, и если
     * выставлены права на редактирование, либо создание задачи и она находится в начальном состоянии (ну или у нее нет операций)
     *
     * @param sc
     * @param taskId
     * @return
     * @throws GranException
     */
    public boolean isTaskEditable(SessionContext sc, String taskId) throws GranException {
        log.trace("isTaskEditable");
        if (sc == null) {
            throw new InvalidParameterException(this.getClass(), "isTaskEditable", "sc", sc);
        }
        if (!sc.taskOnSight(taskId)) {
            throw new AccessDeniedException(this.getClass(), "isCategoryEditable", sc, "!sc.taskOnSight(taskId)", taskId);
        }
        SecuredTaskBean tci = new SecuredTaskBean(taskId, sc);
        return KernelManager.getCategory().isCategoryEditable(taskId, sc.getUserId(), tci.getCategoryId(), sc.getPrstatusId()) || (
                KernelManager.getCategory().isCategoryCreatable(taskId, sc.getUserId()) && tci.getStatus().isStart() && tci.getSubmitterId()
                        .equals(sc.getUserId()));
    }

    /**
     * Возвращает список отфильтрованных задач
     *
     * @param sc       сессия пользователя
     * @param filterId ID фильтра
     * @param withUDF  Нудна ли фильтрация пользовательских полей
     * @param taskId   ID задачи
     * @param pagen    номер страницы
     * @param order    порядок сортировки
     * @return Список задач
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredTaskBean
     */
    public Slider<SecuredTaskBean> getTaskList(SessionContext sc, String taskId, String filterId, boolean withUDF, int pagen, ArrayList<String> order)
            throws GranException {
        log.trace("getTaskList");
        if (filterId == null) {
            throw new InvalidParameterException(this.getClass(), "getTaskList", "filterId", sc);
        }
        return this.getTaskList(sc, taskId, KernelManager.getFilter().getTaskFValue(filterId), withUDF, pagen, order);
    }

    /**
     * Возвращает список отфильтрованных задач
     *
     * @param sc         сессия пользователя
     * @param taskFValue Параметры фильтрации
     * @param withUDF    Нудна ли фильтрация пользовательских полей
     * @param taskId     ID задачи
     * @param pagen      номер страницы
     * @param order      порядок сортировки
     * @return Список задач
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredTaskBean
     */
    public Slider<SecuredTaskBean> getTaskList(SessionContext sc, String taskId, TaskFValue taskFValue, boolean withUDF, int pagen, List<String> order)
            throws GranException {
        boolean lock = lockManager.acquireConnection(SecuredTaskAdapterManager.class.getName());
        try {
            log.trace("getTaskList");
            if (sc == null) {
                throw new InvalidParameterException(this.getClass(), "getTaskList", "sc", sc);
            }
            if (taskId == null) {
                throw new InvalidParameterException(this.getClass(), "getTaskList", "taskId", sc);
            }
            if (taskFValue == null) {
                throw new InvalidParameterException(this.getClass(), "getTaskList", "taskFValue", sc);
            }
            if (!sc.taskOnSight(taskId)) {
                throw new AccessDeniedException(this.getClass(), "getTaskList", sc, "!sc.taskOnSight(taskId)", taskId);
            }
            SecuredTaskBean activeTask = new SecuredTaskBean(taskId, sc);
            int onPage;
            String onPageStr = taskFValue.getAsString(FValue.ONPAGE);
            if (onPageStr != null && onPageStr.length() != 0) {
                onPage = Integer.parseInt(onPageStr);
            } else {
                onPage = 20;
            }
            TaskFilter taskList = new TaskFilter(activeTask);
            boolean notwithsub = taskFValue.get(FValue.SUBTASK) == null;
            ArrayList<SecuredTaskBean> taskCol = taskList.getTaskList(taskFValue, withUDF, notwithsub, order);
            sc.setAttribute("statictask", taskId);
            sc.setAttribute("statictasklist", taskCol);
            Slider<SecuredTaskBean> taskSlider = new Slider<SecuredTaskBean>(taskCol, onPage, order, pagen);
            taskSlider.setTotalChildrenCount(taskCol.size());
            return taskSlider;
        } finally {
            if (lock) {
                lockManager.releaseConnection(SecuredTaskAdapterManager.class.getName());
            }
        }
    }

    /**
     * Used by MK scripts
     **/
    public SecuredTaskBean findTaskByName(SessionContext sc, String name) throws Exception {
        boolean lock = lockManager.acquireConnection(SecuredTaskAdapterManager.class.getName());
        try {
            log.trace("getTaskList");

            String id = CSVImport.findTaskIdByName(sc, name);
            if (id != null) {
                SecuredTaskBean stb = AdapterManager.getInstance().getSecuredFindAdapterManager().findTaskById(sc, id);
                return stb;
            }
            return null;
        } finally {
            if (lock) {
                lockManager.releaseConnection(SecuredTaskAdapterManager.class.getName());
            }
        }

    }

    /**
     * Редактирует указанную задачу
     *
     * @param sc             сессия пользователя
     * @param taskId         ID редактируемой задачи
     * @param shortname      Алиас задачи
     * @param name           Название задачи
     * @param description    Описание задачи
     * @param budget         Бюджет задачи
     * @param deadline       Дедлайн задачи
     * @param priorityId     ID приоритета задачи
     * @param parentId       ID родительской задачи
     * @param handlerUserId  ID ответственного пользователя
     * @param handlerGroupId ID ответственного статуса
     * @param sendMail       надо ли слать почту
     * @param submitDate     Дата создания задачи
     * @param updateDate     Дата обновления задачи
     * @throws GranException при необходимости
     */
    public void updateTask(
            SessionContext sc, String taskId, String shortname, String name, String description,
            Long budget, Calendar deadline, String priorityId,
            String parentId, String handlerUserId, String handlerGroupId, boolean sendMail, Calendar submitDate, Calendar updateDate) throws GranException {
        log.trace("updateTask(parentId='" + parentId + "')");
        if (sc == null) {
            throw new InvalidParameterException(this.getClass(), "updateTask", "sc", sc);
        }
        if (taskId == null) {
            throw new InvalidParameterException(this.getClass(), "updateTask", "taskId", sc);
        }
        name = checkTaskName(name);
        if (pv.badTaskName(name)) {
            throw new InvalidParameterException(this.getClass(), "updateTask", "name", sc);
        }
        if (pv.badSmallDesc(shortname)) {
            throw new InvalidParameterException(this.getClass(), "updateTask", "shortname", sc);
        }
        if (budget != null && budget < 0.0) {
            throw new InvalidParameterException(this.getClass(), "updateTask", "budget", sc);
        }
        if (parentId == null && !taskId.equals("1")) {
            throw new InvalidParameterException(this.getClass(), "updateTask", "parentId", sc);
        }
        if (handlerUserId != null && handlerGroupId != null) {
            throw new InvalidParameterException(this.getClass(), "updateTask", "handlerGroupId, handlerGroupId", sc);
        }
        TaskRelatedInfo task = TaskRelatedManager.getInstance().find(taskId);
        boolean allowedPriority =
                !(priorityId != null && priorityId.length() != 0) || KernelManager.getWorkflow().checkPriorityId(task.getWorkflowId(), priorityId);
        TaskRelatedInfo tci = TaskRelatedManager.getInstance().find(taskId);
        boolean isNewTask = sc.getUserId().equals(tci.getSubmitterId()) && tci.getSubmitdate().equals(tci.getLastUpdateDate());

        boolean _can_edit = AdapterManager.getInstance().getSecuredTaskAdapterManager().isTaskEditable(sc, taskId);
        if (!(isNewTask || _can_edit || sc.canAction(Action.cutCopyPasteTask, taskId))) {
            throw new AccessDeniedException(this.getClass(), "updateTask", sc, "!(isNewTask || _can_edit)", taskId);
        }
        if (!sc.allowedByACL(taskId)) {
            throw new AccessDeniedException(this.getClass(), "updateTask", sc, "!sc.allowedByACL(taskId)", taskId);
        }
        if (!allowedPriority) {
            throw new AccessDeniedException(this.getClass(), "updateTask", sc, "!allowedPriority", priorityId);
        }
        String shortName = shortname;
        String name1 = name;
        Long budget1 = budget;
        String priorityId1 = priorityId;
        String parentTaskId = parentId;
        String handlerUserId1 = handlerUserId;
        String handlerGroupId1 = handlerGroupId;
        TaskRelatedInfo task1 = TaskRelatedManager.getInstance().find(taskId);
        if (!sc.canAction(Action.editTaskAlias, taskId)) {
            shortName = task1.getShortname();
        }
        if (!sc.canAction(Action.editTaskBudget, taskId)) {
            budget1 = task1.getBudget();
        }
        if (!sc.canAction(Action.editTaskDeadline, taskId)) {
            deadline = task1.getDeadline();
        }
        log.debug("Check access for editTask desc : " + sc.canAction(Action.editTaskDescription, taskId));
        if (!sc.canAction(Action.editTaskDescription, taskId)) {
            description = task1.getDescription();
        }
        //log.debug("Check task desc : " + description);
        if (!sc.canAction(Action.editTaskHandler, taskId)) {
            handlerUserId1 = task1.getHandlerUserId();
            handlerGroupId1 = task1.getHandlerGroupId();
        }
        if (!sc.canAction(Action.cutCopyPasteTask, taskId)) {
            parentTaskId = task1.getParentId();
        }
        if (!sc.canAction(Action.editTaskPriority, taskId)) {
            priorityId1 = task1.getPriorityId();
        }
        TaskRelatedInfo tci1 = TaskRelatedManager.getInstance().find(taskId);
        boolean isTime = submitDate != null && submitDate.equals(updateDate);
        boolean isNewTask1 = sc.getUserId().equals(tci1.getSubmitterId()) && isTime;

        KernelManager.getTask()
                .updateTask(taskId,
                        SafeString.createSafeString(shortName),
                        SafeString.createSafeString(name1),
                        SafeString.createSafeString(description),
                        budget1,
                        deadline,
                        priorityId1,
                        parentTaskId,
                        handlerUserId1,
                        handlerGroupId1,
                        submitDate,
                        updateDate);
    }

    /**
     * Редактирует указанную задачу
     *
     * @param sc             сессия пользователя
     * @param taskId         ID редактируемой задачи
     * @param shortname      Алиас задачи
     * @param name           Название задачи
     * @param description    Описание задачи
     * @param budget         Бюджет задачи
     * @param deadline       Дедлайн задачи
     * @param priorityId     ID приоритета задачи
     * @param parentId       ID родительской задачи
     * @param handlerUserId  ID ответственного пользователя
     * @param handlerGroupId ID ответственного статуса
     * @param sendMail       надо ли слать почту
     * @throws GranException при необходимости
     */
    public void updateTask(
            SessionContext sc, String taskId, String shortname, String name, String description,
            Long budget, Calendar deadline, String priorityId,
            String parentId, String handlerUserId, String handlerGroupId, boolean sendMail) throws GranException {
        updateTask(sc, taskId, shortname, name, description, budget, deadline, priorityId, parentId, handlerUserId, handlerGroupId, sendMail, null, null);
    }

    /**
     * Удаляет задачу
     *
     * @param sc     сессия пользователя
     * @param taskId ID удаляемой задачи
     * @throws GranException при необходимости
     */
    public void deleteTask(SessionContext sc, String taskId) throws GranException {
        log.trace("deleteTask");
        if (sc == null) {
            throw new InvalidParameterException(this.getClass(), "deleteTask", "sc", sc);
        }
        if (taskId == null) {
            throw new InvalidParameterException(this.getClass(), "deleteTask", "taskId", sc);
        }
        TaskRelatedInfo task = TaskRelatedManager.getInstance().find(taskId);
        List<TaskRelatedInfo> taskForDelete = TaskRelatedManager.getInstance().getChildrenRecursive(taskId);
        taskForDelete.add(0, TaskRelatedManager.getInstance().find(taskId));
        if (taskId.equals("1")) {
            throw new AccessDeniedException(this.getClass(), "deleteTask", sc, "taskId.equals(\"1\")", taskId);
        }
        if (!sc.allowedByACL(taskId)) {
            throw new UserException("ERROR_CAN_NOT_DELETE_TASK", new Object[]{task.getNumber(), I18n.getString("ACCESS_DENIED")});
        }
        if (!sc.allowedByACL(task.getParentId())) {
            throw new UserException("ERROR_CAN_NOT_DELETE_TASK", new Object[]{task.getNumber(), I18n.getString("ACCESS_DENIED")});
        }
        for (TaskRelatedInfo tci : taskForDelete) {
            if (tci != null) {
                if (!sc.taskOnSight(tci.getId())) {
                    throw new UserException("ERROR_CAN_NOT_DELETE_TASK", new Object[]{task.getNumber(), I18n.getString("ACCESS_DENIED")});
                }
                if (!AdapterManager.getInstance().getSecuredCategoryAdapterManager().isCategoryDeletable(sc, tci.getId(), tci.getCategoryId())) {
                    throw new UserException("ERROR_CAN_NOT_DELETE_TASK", new Object[]{tci.getNumber(), I18n.getString("TASK_CATEGORY_NOT_DELETABLE")});
                }
            }
        }
        KernelManager.getTask().deleteTask(taskId, sc.getUser().getLogin());
    }

    /**
     * Удаляет задачу
     *
     * @param sc   сессия пользователя
     * @param task delete task
     * @throws GranException при необходимости
     */
    public void deleteTask(SessionContext sc, SecuredTaskBean task) throws GranException {
        log.trace("deleteTask");
        if (sc == null) {
            throw new InvalidParameterException(this.getClass(), "deleteTask", "sc", sc);
        }
        if (task == null) {
            throw new InvalidParameterException(this.getClass(), "deleteTask", "task", sc);
        }
        List<TaskRelatedInfo> taskForDelete = TaskRelatedManager.getInstance().getChildrenRecursive(task.getId());
        taskForDelete.add(0, TaskRelatedManager.getInstance().find(task.getId()));
        if (task.getId().equals("1")) {
            throw new AccessDeniedException(this.getClass(), "deleteTask", sc, "taskId.equals(\"1\")", task.getId());
        }
        if (!sc.allowedByACL(task.getId())) {
            throw new UserException("ERROR_CAN_NOT_DELETE_TASK", new Object[]{task.getNumber(), I18n.getString("ACCESS_DENIED")});
        }
        if (!sc.allowedByACL(task.getParentId())) {
            throw new UserException("ERROR_CAN_NOT_DELETE_TASK", new Object[]{task.getNumber(), I18n.getString("ACCESS_DENIED")});
        }
        for (TaskRelatedInfo tci : taskForDelete) {
            if (tci != null) {
                if (!sc.taskOnSight(tci.getId())) {
                    throw new UserException("ERROR_CAN_NOT_DELETE_TASK", new Object[]{task.getNumber(), tci.getNumber()});
                }
                if (!AdapterManager.getInstance().getSecuredCategoryAdapterManager().isCategoryDeletable(sc, tci.getId(), tci.getCategoryId())) {
                    throw new UserException("ERROR_CAN_NOT_DELETE_TASK", new Object[]{tci.getNumber(), I18n.getString("TASK_CATEGORY_NOT_DELETABLE")});
                }
            }
        }

        KernelManager.getTask().deleteTask(task.getId(), sc.getUser().getLogin());
    }

    /**
     * Возвращает цепочку задач от начальной до конечной
     *
     * @param sc          сессия пользователя
     * @param startTaskId ID начальной задачи
     * @param stopTaskId  ID конечной задачи
     * @return список задач
     * @throws GranException при необходимости
     * @see com.trackstudio.kernel.cache.TaskRelatedInfo
     */
    public ArrayList<SecuredTaskBean> getTaskChain(SessionContext sc, String startTaskId, String stopTaskId) throws GranException {
        log.trace("getTaskChain");
        boolean lock = lockManager.acquireConnection(SecuredTaskAdapterManager.class.getName());
        try {
            if (sc == null) {
                throw new InvalidParameterException(this.getClass(), "getTaskChain", "sc", sc);
            }
            if (stopTaskId == null) {
                throw new InvalidParameterException(this.getClass(), "getTaskChain", "stopTaskId", sc);
            }
            //if (!(startTaskId == null || sc.taskOnSight(startTaskId)) && sc.taskOnSight(stopTaskId))
            //    throw new AccessDeniedException(this.getClass(), "getTaskChain", sc);
            if (startTaskId != null && !sc.taskOnSight(startTaskId)) {
                throw new AccessDeniedException(this.getClass(), "getTaskChain", sc, "startTaskId != null && !sc.taskOnSight(startTaskId)", startTaskId);
            }
            if (!sc.taskOnSight(stopTaskId)) {
                throw new AccessDeniedException(this.getClass(), "getTaskChain", sc, "!sc.taskOnSight(" + stopTaskId + ")", stopTaskId);
            }
            ArrayList<TaskRelatedInfo> taskChain = KernelManager.getTask().getTaskChain(startTaskId, stopTaskId);
            if (taskChain != null) {
                return SecuredBeanUtil.toArrayList(sc, taskChain, SecuredBeanUtil.TASK);
            } else {
                return new ArrayList<SecuredTaskBean>();
            }
        } finally {
            if (lock) {
                lockManager.releaseConnection(SecuredTaskAdapterManager.class.getName());
            }
        }
    }

    /**
     * Возвращает ID задачи. Поиск идет вначале по номеру, потом по алиасу, потом по названию, если задача не найдена, то возвращается null
     *
     * @param sc      сессия пользователя
     * @param quickGo номер, алиас или название задачи
     * @return ID задачи
     * @throws GranException при необходимости
     */
    public String findTaskIdByQuickGo(SessionContext sc, String quickGo) throws GranException {
        log.trace("findTaskIdByQuickGo");
        if (sc == null) {
            throw new InvalidParameterException(this.getClass(), "findTaskIdByQuickGo", "sc", sc);
        }
        if (quickGo == null) {
            throw new InvalidParameterException(this.getClass(), "findTaskIdByQuickGo", "quickGo", sc);
        }
        return KernelManager.getTask().findTaskIdByQuickGo(quickGo);
    }

    public List<String> findTaskIdByQuickGoIndexOf(SessionContext sc, String quickGo, int limit) throws GranException {
        log.trace("findTaskIdByQuickGoIndexOf");
        if (sc == null) {
            throw new InvalidParameterException(this.getClass(), "findTaskIdByQuickGoIndexOf", "sc", sc);
        }
        if (quickGo == null) {
            throw new InvalidParameterException(this.getClass(), "findTaskIdByQuickGoIndexOf", "quickGo", sc);
        }
        return KernelManager.getTask().getListByKey(quickGo, limit);
    }

    /**
     * Возвращает задачи, подобные указазнной
     *
     * @param sc     сессия пользователя
     * @param taskId ID задачи, для которой изем подобные
     * @return карта (Map), содержащая ID найденных задач и числовые значения, означающие степень "подобности"
     * @throws GranException при необходимости
     */
    public HashMap<SecuredTaskBean, Float> findSimilar(SessionContext sc, String taskId) throws GranException {
        log.trace("findSimilar");
        if (sc == null) {
            throw new InvalidParameterException(this.getClass(), "findSimilar", "sc", null);
        }
        if (taskId == null) {
            throw new InvalidParameterException(this.getClass(), "findSimilar", "taskId", sc);
        }
        if (!sc.allowedByACL(taskId)) {
            throw new AccessDeniedException(this.getClass(), "findSimilar", sc, "!sc.allowedByACL(taskId)", taskId);
        }
        Map<String, String> t = KernelManager.getTask().findSimilar(taskId);
        HashMap<SecuredTaskBean, Float> v = new HashMap<SecuredTaskBean, Float>();
        for (String aTaskId : t.keySet()) {
            SecuredTaskBean b = new SecuredTaskBean(aTaskId, sc);
            if (b.canManage()) {
                v.put(b, 0F);
            }
        }
        return v;
    }

    /**
     * Полнотекстовый поиск задач
     *
     * @param from         задача, с которой начинаем поиск
     * @param searchString что ищем
     * @return список задач
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredSearchTaskItem
     */
    public ArrayList<SecuredSearchTaskItem> fullTextSearch(SecuredTaskBean from, String searchString) throws GranException {
        log.trace("fullTextSearch for tasks");
        if (from == null) {
            throw new InvalidParameterException(this.getClass(), "fullTextSearch", "from", null);
        }
        Map<String, String> t = IndexManager.getIndex().searchTasksWithHighLight(searchString);
        ArrayList<SecuredSearchTaskItem> v = new ArrayList<SecuredSearchTaskItem>();
        if (t == null) {
            return v;
        }
        for (String aTaskId : t.keySet()) {
            SecuredTaskBean b = new SecuredTaskBean(aTaskId, from.getSecure());
            if (b.canView()) {
                v.add(new SecuredSearchTaskItem(b, t.getOrDefault(aTaskId, ""), searchString));
            }
        }
        Collections.sort(v);
        return v;
    }

    /**
     * Возвращает задачу по ее номеру
     *
     * @param sc     сессия пользователя
     * @param number Номер задачи
     * @return ID задачи
     * @throws GranException при необзодимости
     * @see com.trackstudio.secured.SecuredTaskBean
     */
    public SecuredTaskBean findTaskByNumber(SessionContext sc, String number) throws GranException {
        log.trace("findTaskByNumber");
        if (sc == null) {
            throw new InvalidParameterException(this.getClass(), "findTaskByNumber", "sc", null);
        }
        if (number == null) {
            throw new InvalidParameterException(this.getClass(), "findTaskByNumber", "number", sc);
        }
        String id = KernelManager.getTask().findByNumber(number);
        if (id != null) {
            return new SecuredTaskBean(id, sc);
        } else {
            return null;
        }
    }

    /**
     * Возвращает список дочерних задач для проекта
     *
     * @param sc     сессия пользователя
     * @param taskId ID задачи
     * @return список задач
     * @throws GranException при необходимости
     */
    public ArrayList<SecuredTaskBean> getProjectChildren(SessionContext sc, String taskId) throws GranException {
        log.trace("getProjectChildren");
        if (sc == null) {
            throw new InvalidParameterException(this.getClass(), "getProjectChildren", "sc", null);
        }
        if (taskId == null) {
            throw new InvalidParameterException(this.getClass(), "getProjectChildren", "taskId", sc);
        }
        ArrayList<SecuredTaskBean> result = new ArrayList<SecuredTaskBean>();
        List<String> projectList = TaskRelatedManager.getInstance().getProjectChildren(taskId);
        for (String aCollection : projectList) {
            SecuredTaskBean stb = new SecuredTaskBean(aCollection, sc);
            if (stb.isOnSight()) {
                result.add(stb);
            }
        }
        return result;
    }

    /**
     * Возвращает список дочерних задач
     *
     * @param sc     сессия пользователя
     * @param taskId ID задачи, для которой ищутся дочерние
     * @return список задач
     * @throws GranException при необходимости
     */
    public ArrayList<SecuredTaskBean> getChildren(SessionContext sc, String taskId) throws GranException {
        log.trace("getChildren");
        if (sc == null) {
            throw new InvalidParameterException(this.getClass(), "getChildren", "sc", null);
        }
        if (taskId == null) {
            throw new InvalidParameterException(this.getClass(), "getChildren", "taskId", sc);
        }
        ArrayList<SecuredTaskBean> result = new ArrayList<SecuredTaskBean>();
        List<TaskRelatedInfo> childList = TaskRelatedManager.getInstance().find(taskId).getChildren();
        for (TaskRelatedInfo aCollection : childList) {
            SecuredTaskBean stb = new SecuredTaskBean(aCollection, sc);
            if (stb.isOnSight()) {
                result.add(stb);
            }
        }
        return result;
    }

    /**
     * Возвращает список открытых задач
     *
     * @param sc     сессия пользователя
     * @param taskId ID задачи, для которой возвращаем список открытых подзадач
     * @return Список ID задач
     * @throws GranException при необходимости
     */
    public ArrayList<SecuredTaskBean> getNotFinishChildren(SessionContext sc, String taskId) throws GranException {
        log.trace("getNotFinishChildren");
        if (sc == null) {
            throw new InvalidParameterException(this.getClass(), "getChildren", "sc", null);
        }
        if (taskId == null) {
            throw new InvalidParameterException(this.getClass(), "getChildren", "taskId", sc);
        }
        ArrayList<SecuredTaskBean> result = new ArrayList<SecuredTaskBean>();
        List<String> list = KernelManager.getTask().getNotFinishChildren(taskId);
        for (String id : list) {
            SecuredTaskBean stb = new SecuredTaskBean(id, sc);
            if (stb.isOnSight()) {
                result.add(stb);
            }
        }
        return result;
    }

    public int getTotalNotFinishChildren(SessionContext sc, String taskId) throws GranException {
        log.trace("getNotFinishChildren");
        if (sc == null) {
            throw new InvalidParameterException(this.getClass(), "getChildren", "sc", null);
        }
        if (taskId == null) {
            throw new InvalidParameterException(this.getClass(), "getChildren", "taskId", sc);
        }
        return KernelManager.getTask().getTotalNotFinishChildren(taskId, sc.getUserId(), sc.getPrstatusId());
    }

    /**
     * Вставляет указанные задачи, операция PASTE
     *
     * @param sc        сессия пользователя
     * @param parentId  ID задачи, куда вставляем
     * @param taskIds   ID вставляемых задач
     * @param operation тип операцуии, COPY или CUT
     * @return список ID вставленных задач
     * @throws GranException при необходимости
     */
    public List<String> pasteTasks(SessionContext sc, String parentId, String taskIds, String operation) throws GranException {
        log.trace("pasteTasks");
        if (sc == null) {
            throw new InvalidParameterException(this.getClass(), "pasteTasks", "sc", sc);
        }
        if (parentId == null) {
            throw new InvalidParameterException(this.getClass(), "pasteTasks", "parentId", sc);
        }
        if (taskIds == null) {
            throw new InvalidParameterException(this.getClass(), "pasteTasks", "taskIds", sc);
        }
        if (!KernelManager.getTask().isParentValidForOperation(taskIds, parentId)) {
            throw new UserException("ERROR_CAN_NOT_PASTE_TASKS");
        }
        for (String taskId1 : taskIds.split(UdfConstants.SPLIT_SEPARATOR)) {
            TaskRelatedInfo task = TaskRelatedManager.getInstance().find(taskId1);
            if (task != null) {
                if (operation.equals(CommonConstants.COPY_RECURSIVELY)) {
                    if (!KernelManager.getTask().canRecursivelyCopyTask(taskId1, parentId, sc.getUserId())) {
                        throw new UserException("ERROR_CAN_NOT_PASTE_TASK", new Object[]{task.getNumber()});
                    }
                }
                if (operation.equals(CommonConstants.CUT)) {
                    if (!(sc.canAction(Action.cutCopyPasteTask, task.getId()) && sc.allowedByACL(task.getId()) && task.getParentId() != null)) {
                        throw new UserException("ERROR_CAN_NOT_MOVE_TASK", new Object[]{task.getNumber()});
                    }
                }
            }
        }
        List<String> ids = new ArrayList<String>();
        if (operation.equals(CommonConstants.CUT)) {
            for (String taskId : taskIds.split(UdfConstants.SPLIT_SEPARATOR)) {
                moveTask(sc, taskId, parentId);
                ids.add(taskId);
            }
        } else {
            for (String taskId : taskIds.split(UdfConstants.SPLIT_SEPARATOR)) {
                if (taskId != null && taskId.length() > 0) {
                    ids.add(copyTask(sc, taskId, parentId, operation.equals(CommonConstants.COPY_RECURSIVELY)));
                }
            }
        }
        return ids;
    }

    /**
     * В качестве исключение оборачиваем этот метод здесь
     * При перемещении задач зачем-то считаются вычисляемые udf [#78855]
     * Из за локов в базе и наших мы получаем дидлок
     */
    private void moveTask(SessionContext sc, String taskId, String parentId) throws GranException {
        boolean w = lockManager.acquireConnection(className);
        try {
            SecuredTaskBean old = new SecuredTaskBean(taskId, sc);
            SecuredTaskBean parent = new SecuredTaskBean(parentId, sc);

            HashMap<String, String> udfMap = new HashMap<String, String>();
            HashMap<String, String> simple = UDFFormFillHelper.simplifyUdf(old);
            for (SecuredUDFValueBean suv : parent.getUDFValuesForNewTask(old.getWorkflowId())) {
                if (simple.containsKey(suv.getCaption())) {
                    udfMap.put(suv.getCaption(), simple.get(suv.getCaption()));
                }
            }
            TriggerManager.getInstance().moveTask(sc,
                    old.getId(),
                    old.getParent().getNumber(),
                    old.getShortname(),
                    old.getName(),
                    old.getDescription(),
                    old.getBudget(),
                    old.getDeadline(),
                    old.getPriorityId(),
                    parentId,
                    old.getHandlerUserId(),
                    old.getHandlerGroupId(),
                    true,
                    udfMap);
        } finally {
            if (w) {
                lockManager.releaseConnection(className);
            }
        }

    }

    private String copyTask(SessionContext sc, String taskId, String parentId, boolean recursively) throws GranException {
        String newId = copySingleTask(sc, taskId, parentId);
        if (recursively) {
            List<String> childList = TaskRelatedManager.getInstance().getChildrenId(taskId);
            for (String id : childList) {
                copyTask(sc, id, newId, true);
            }
        }
        return newId;
    }

    private String copySingleTask(SessionContext sc, String taskId, String parentId) throws GranException {
        String newId = null;
        boolean w = lockManager.acquireConnection(className);
        try {
            SecuredTaskBean old = new SecuredTaskBean(taskId, sc);
            SecuredTaskBean parent = new SecuredTaskBean(parentId, sc);

            HashMap<String, String> udfMap = new HashMap<String, String>();
            HashMap<String, String> simple = UDFFormFillHelper.simplifyUdf(old);
            for (SecuredUDFValueBean suv : parent.getUDFValuesForNewTask(old.getWorkflowId())) {
                if (simple.containsKey(suv.getCaption())) {
                    udfMap.put(suv.getCaption(), simple.get(suv.getCaption()));
                }
            }

            String defaultHandlerId;
            String defaultHandlerGroupId;
            //[#78812] don't uncomment
            //        if (!isAllowedHandlerForClipboardOperations(sc, old, true)) {
            //            defaultHandlerId = parent.getHandlerUserId();
            //            defaultHandlerGroupId = parent.getHandlerGroupId();
            //        } else {
            defaultHandlerId = old.getHandlerUserId();
            defaultHandlerGroupId = old.getHandlerGroupId();
            //        }
            TaskBuilder taskBuilder = new TaskBuilder();
            taskBuilder.setUpdatedate(Calendar.getInstance());
            taskBuilder.setSc(sc);
            taskBuilder.setSubmitterId(sc.getUserId());
            taskBuilder.setPreviousNumber(old.getNumber());
            taskBuilder.setCategoryId(old.getCategoryId());
            taskBuilder.setShortname(old.getShortname());
            taskBuilder.setName(old.getName());
            taskBuilder.setDescription(old.getDescription());
            taskBuilder.setBudget(old.getBudget());
            taskBuilder.setDeadline(old.getDeadline());
            taskBuilder.setPriorityId(old.getPriorityId());
            taskBuilder.setParentId(parentId);
            taskBuilder.setHandlerId(defaultHandlerId);
            taskBuilder.setHandlerUserId(defaultHandlerId);
            taskBuilder.setHandlerGroupId(defaultHandlerGroupId);
            taskBuilder.setNeedSend(false);
            taskBuilder.setUdfValues(udfMap);
            taskBuilder.setStatusId(null);
            taskBuilder.setAtts(null);
            taskBuilder.setCopyOrMoveOpr(true);

            newId = TriggerManager.getInstance().createTask(SecuredTaskTriggerBean.build(taskBuilder, TaskBuilder.Action.CREATE));
            ArrayList<AttachmentArray> atts = new ArrayList<AttachmentArray>();
            if (old.getAttachments() != null) {
                for (SecuredTaskAttachmentBean attachmentBean : old.getAttachments()) {
                    if (!attachmentBean.isDeleted()) {
                        try {
                            SafeString fileName = SafeString.createSafeString(attachmentBean.getName());
                            SafeString fileDescription = SafeString.createSafeString(attachmentBean.getDescription());
                            InputStream is = new BufferedInputStream(new FileInputStream(attachmentBean.getFile()));
                            atts.add(new AttachmentArray(fileName, fileDescription, is));
                        } catch (IOException ioe) {
                            log.error("ERROR COPY ATTACHMENT:", ioe);
                        }
                    }
                }
            }
            if (!atts.isEmpty()) {
                AdapterManager.getInstance().getSecuredAttachmentAdapterManager().createAttachment(sc, newId, null, sc.getUserId(), atts, false);
            }
            List<SecuredTaskAclBean> sourceAcl = SecuredBeanUtil.toArrayList(sc, KernelManager.getAcl().getAllTaskAclList(old.getId()), SecuredBeanUtil.ACL);
            for (SecuredTaskAclBean acl : sourceAcl) {
                if (acl.getTask().getId().equals(old.getId())) {
                    String userId = acl.getUser() != null ? acl.getUser().getId() : null;
                    String groupId = acl.getPrstatusId() != null ? acl.getPrstatusId() : acl.getGroupId();
                    String aclNewId = KernelManager.getAcl().createAcl(newId, null, userId, acl.getGroupId(), acl.getOwnerId());
                    KernelManager.getAcl().updateAcl(aclNewId, groupId, acl.isOverride());
                }
            }
        } finally {
            if (w) {
                lockManager.releaseConnection(className);
            }
        }
        return newId;
    }

    private boolean isAllowedHandlerForClipboardOperations(SessionContext sc, SecuredTaskBean task, boolean isNew) throws GranException {
        ArrayList<UserRelatedInfo> taskEditHandlerList = KernelManager.getStep()
                .getTaskEditHandlerList(task.getId(), task.getCategoryId(), isNew, sc.getUserId());
        ArrayList<Prstatus> taskEditGroupHandlerList = KernelManager.getStep().getTaskEditGroupHandlerList(task.getId(), task.getCategoryId(), isNew);

        boolean newHandlerIsNull = false;
        boolean newHandlerUserAllowed = false;
        boolean newHandlerGroupAllowed = false;
        if (task.getHandlerUserId() == null && task.getHandlerGroupId() == null) {
            newHandlerIsNull = true;
        }

        if (task.getHandlerUserId() != null && taskEditHandlerList.contains(UserRelatedManager.getInstance().find(task.getHandlerUserId()))) {
            newHandlerUserAllowed = true;
        }

        if (task.getHandlerGroupId() != null && taskEditGroupHandlerList.contains(KernelManager.getFind().findPrstatus(task.getHandlerGroupId()))) {
            newHandlerGroupAllowed = true;
        }

        return newHandlerIsNull || newHandlerUserAllowed || newHandlerGroupAllowed;
    }

    /**
     * Проверяет правильность родительской задачи для дочерней
     *
     * @param sc       сессия пользователя
     * @param taskId   Дочерняя задача
     * @param parentId Родительская задача
     * @return TRUE - если все хорошо, FALSE - если нет
     * @throws GranException при необходимости
     */
    public boolean isParentValidForOperation(SessionContext sc, String taskId, String parentId) throws GranException {
        log.trace("pasteTasks");
        if (sc == null) {
            throw new InvalidParameterException(this.getClass(), "isParentValidForOperation", "sc", sc);
        }
        if (parentId == null) {
            throw new InvalidParameterException(this.getClass(), "isParentValidForOperation", "parentId", sc);
        }
        if (taskId == null) {
            throw new InvalidParameterException(this.getClass(), "isParentValidForOperation", "taskId", sc);
        }
        return KernelManager.getTask().isParentValidForOperation(taskId, parentId);
    }

    /**
     * Возвращает список задач по SQL-запросу
     *
     * @param sc    сессия пользователя
     * @param query запрос вида SELECT t.id FROM com.trackstudio.model.Task AS t WHERE ...
     * @return список ID Задач
     * @throws GranException при необзодимости
     */
    public List<SecuredTaskBean> getTaskListByQuery(SessionContext sc, String query) throws GranException {
        if (sc == null) {
            throw new InvalidParameterException(this.getClass(), "isParentValidForOperation", "sc", sc);
        }
        if (query == null || query.length() == 0) {
            throw new InvalidParameterException(this.getClass(), "isParentValidForOperation", "parentId", sc);
        }
        List<String> ids = KernelManager.getTask().getTaskListByQuery(query);
        List<SecuredTaskBean> list = new ArrayList<SecuredTaskBean>();
        for (String id : ids) {
            if (sc.taskOnSight(id)) {
                list.add(new SecuredTaskBean(id, sc));
            }
        }
        return list;
    }

    public List<TaskRelatedInfo> getTaskUseUserList(SessionContext sc, String userId) throws GranException {
        log.trace("getReportUserList");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getReportList", "sc", null);
        if (userId == null)
            throw new InvalidParameterException(this.getClass(), "getReportList", "taskId", sc);
        return KernelManager.getTask().getTaskUseUserList(userId);
    }

    /**
     * This method archives the tasks to jsons.
     *
     * @param taskIds list of ids
     * @throws GranException possible
     */
    public void archive(List<String> taskIds, SessionContext sc) throws GranException {
        boolean lock = lockManager.acquireConnection(SecuredTaskAdapterManager.class.getName());
        try {
            LinkedList<TaskRelatedInfo> tasks = new LinkedList<>();
            for (final String taskId : taskIds) {
                TaskRelatedInfo task = TaskRelatedManager.getInstance().find(taskId);
                tasks.add(task);
                LinkedList<TaskRelatedInfo> children = new LinkedList<>(task.getChildren());
                while (!children.isEmpty()) {
                    TaskRelatedInfo child = children.pop();
                    tasks.add(child);
                    children.addAll(child.getChildren());
                }
            }
            while (!tasks.isEmpty()) {
                TaskRelatedInfo tsk = tasks.pollLast();
                final JSONObject json = buildTask(tsk);
                lockManager.getDBSession().getSession().doWork(
                        connect -> {
                            try (PreparedStatement st = connect.prepareStatement(
                                    "insert into gr_archive (archive_task, archive_taskid) values(to_json(?::json), ?)")) {
                                st.setObject(1, json, Types.OTHER);
                                st.setString(2, tsk.getId());
                                st.executeUpdate();
                            }
                        }
                );
                List<AttachmentCacheItem> attachs = tsk.getAttachments();
                for (AttachmentCacheItem attach : attachs) {
                    if (!attach.isDeleted()) {
                        log.error("file " + attach.getFile().getAbsolutePath());
                        log.error("upload dir " + Config.getInstance().getUploadDir());
                        log.error("archive dir " + Config.getInstance().getArchiveDir());
                        File upload = new File(Config.getInstance().getUploadDir());
                        File archive = new File(Config.getInstance().getArchiveDir());
                        log.error("upload dir absolute" + upload.getAbsolutePath());
                        log.error("archive dir absolute" + archive.getAbsolutePath());
                        String toArchive = archive.getAbsolutePath() + attach.getFile().getAbsolutePath()
                                .substring(upload.getAbsolutePath().length());
                        log.error("file to archive" + toArchive);
                        new File(toArchive.substring(0, toArchive.lastIndexOf(File.separator))).mkdirs();
                        Files.copy(Path.of(attach.getFile().getAbsolutePath()), Path.of(toArchive));
                    }
                }
                for (MessageCacheItem msg : tsk.getMessages()) {
                    for (AttachmentCacheItem att : KernelManager.getAttachment()
                            .getAttachmentList(tsk.getId(), msg.getId(), null)) {
                        KernelManager.getAttachment().deleteAttachment(att.getId());
                    }
                    KernelManager.getMessage().deleteMessageOnlyForDeleteTask(msg.getId());
                }
                AdapterManager.getInstance().getSecuredTaskAdapterManager().deleteTask(sc, tsk.getId());
            }
        } catch (IOException e) {
            throw new GranException(e);
        } finally {
            if (lock) {
                lockManager.releaseConnection(SecuredTaskAdapterManager.class.getName());
            }
        }
    }

    private JSONObject buildTask(TaskRelatedInfo tsk) throws GranException {
        JSONObject task = new JSONObject();
        FindManager findManager = FindManager.getFind();
        /*собираем доп. поля*/
        List<SecuredUDFValueBean> listudfvb = new SecuredTaskBean(
                tsk.getId(),
                SessionManager.getInstance().getSessionContext(
                        SessionManager.getInstance().create(
                                UserRelatedManager.getInstance().find("1")))).getUDFValuesList();
        JSONArray udfs = new JSONArray();
        for (SecuredUDFValueBean sudfvb : listudfvb) {
            JSONObject udf = new JSONObject();
            udf.put("name", sudfvb.getCaption());
            udf.put("value", UDFFormFillHelper.convert(sudfvb));
            udfs.put(udf);
        }

        /*собираем аттачи*/
        JSONArray attachments = new JSONArray();
        for (AttachmentCacheItem attItem : tsk.getAttachments()) {
            JSONObject att = new JSONObject(attItem);
            attachments.put(att);
        }

        /*собираем сообщения*/
        JSONArray mcacheItems = new JSONArray();
        for (MessageCacheItem item : TaskRelatedManager.getInstance().find(tsk.getId()).getMessages()) {
            JSONArray messageAttachments = new JSONArray();
            for (AttachmentCacheItem attItem : tsk.getAttachments()) {
                JSONObject att = new JSONObject(attItem);
                messageAttachments.put(att);
            }
            JSONObject message = new JSONObject();
            message.put("description", item.getDescription());
            message.put("hrs", item.getHrs());
            JSONObject mstatus = new JSONObject();
            mstatus.put("id", item.getMstatusId());
            mstatus.put("name", KernelManager.getFind().findMstatus(item.getMstatusId()).getName());
            message.put("mstatus", mstatus);
            message.put("id", item.getId());
            message.put("time", calendarToMls(item.getTime(), null));
            message.put("budget", item.getBudget());
            message.put("dealine", calendarToMls(item.getDeadline(), null));
            message.put("handlergroupid", item.getHandlerGroupId());
            message.put("handleruserid", item.getHandlerUserId());
            message.put("handler", packUser(item.getHandlerUserId()));
            message.put("handlerGroup", packPrstatus(item.getHandlerGroupId()));
            message.put("priorityid", item.getPriorityId());
            JSONObject messagePriority = new JSONObject();
            messagePriority.put("priorityId", item.getPriorityId());
            if (findManager.findPriority(item.getPriorityId()) != null) {
                messagePriority.put("priorityName", findManager.findPriority(item.getPriorityId()).getName());
            }
            message.put("priority", messagePriority);
            JSONObject messageResolution = new JSONObject();
            messageResolution.put("resolutionid", item.getResolutionId());
            if (findManager.findResolution(item.getResolutionId()) != null) {
                messageResolution.put("resolutionName", findManager.findResolution(item.getResolutionId()).getName());
            }
            message.put("resolution", messageResolution);
            message.put("submitterid", item.getSubmitterId());
            message.put("submitter", packUser(item.getSubmitterId()));
            message.put("taskid", item.getTaskId());
            message.put("attachments", messageAttachments);
            mcacheItems.put(message);
        }

        /*собираем статус*/
        JSONObject status = new JSONObject();
        Status sstatus = findManager.findStatus(tsk.getStatusId());
        status.put("id", sstatus.getId());
        status.put("name", sstatus.getName());
        status.put("color", sstatus.getColor());
        status.put("isFinish", sstatus.getIsfinish());
        status.put("isStart", sstatus.getIsstart());

        JSONObject priority = new JSONObject();
        priority.put("id", tsk.getPriorityId());
        if (findManager.findPriority(tsk.getPriorityId()) != null) {
            priority.put("name", findManager.findPriority(tsk.getPriorityId()).getName());
        }

        JSONObject resolution = new JSONObject();
        resolution.put("id", tsk.getResolutionId());
        if (findManager.findResolution(tsk.getResolutionId()) != null) {
            resolution.put("name", findManager.findResolution(tsk.getResolutionId()).getName());
        }

        /*собираем категорию*/
        JSONObject category = new JSONObject();
        Category scategory = findManager.findCategory(tsk.getCategoryId());
        category.put("name", scategory.getName());
        category.put("icon", scategory.getIcon());
        category.put("id", scategory.getId());

        /*собираем объект задачи*/
        task.put("id", tsk.getId());
        task.put("path", extractFullPath(tsk));
        task.put("number", tsk.getNumber());
        task.put("shortname", tsk.getShortname());
        task.put("name", tsk.getName());
        task.put("submitdate", calendarToMls(tsk.getSubmitdate(), null));
        task.put("updatedate", calendarToMls(tsk.getUpdatedate(), null));
        task.put("closedate", calendarToMls(tsk.getClosedate(), null));
        task.put("description", tsk.getDescription());
        task.put("abudget", tsk.getAbudget());
        task.put("budget", tsk.getBudget());
        task.put("deadline", calendarToMls(tsk.getDeadline(), null));
        task.put("category", category);
        task.put("status", status);
        task.put("resolutionid", tsk.getResolutionId());
        task.put("priorityid", tsk.getPriorityId());
        task.put("priority", priority);
        task.put("resolution", resolution);
        task.put("submitterid", tsk.getSubmitterId());
        task.put("submitter", packUser(tsk.getSubmitterId()));
        task.put("handlerGroup", packPrstatus(tsk.getHandlerGroupId()));
        task.put("handlerid", tsk.getHandlerId());
        task.put("handlerUserId", tsk.getHandlerUserId());
        task.put("handlerGroupId", tsk.getHandlerGroupId());
        task.put("handler", packUser(tsk.getHandlerUserId()));
        task.put("parentid", tsk.getParentId());
        task.put("messages", mcacheItems);
        task.put("udfs", udfs);
        task.put("attachments", attachments);
        return task;
    }

    public JSONObject packPrstatus(String prstatusId) throws GranException {
        JSONObject json = new JSONObject();
        if (prstatusId != null) {
            Prstatus prstatus = KernelManager.getFind().findPrstatus(prstatusId);
            json.put("groupid", prstatus.getId());
            json.put("groupname", prstatus.getName());
        }
        return json;
    }

    public JSONObject packUser(String userId) throws GranException {
        JSONObject json = new JSONObject();
        if (userId != null) {
            User user = KernelManager.getFind().findUser(userId);
            json.put("id", user.getId());
            json.put("name", user.getName());
            json.put("login", user.getLogin());
            json.put("email", user.getEmail());
            json.put("active", user.getActive());
            json.put("tel", user.getTel());
        }
        return json;
    }

    private String extractFullPath(TaskRelatedInfo task) {
        StringBuilder path = new StringBuilder();
        LinkedList<String> list = new LinkedList<>();
        while (task.getParentId() != null) {
            task = TaskRelatedManager.getInstance().find(task.getParentId());
            list.add(task.getName() + " [#" + task.getNumber() + "]");
        }
        while (!list.isEmpty()) {
            path.append(list.pollLast());
            if (!list.isEmpty()) {
                path.append(" > ");
            }
        }
        return path.toString();
    }

    public int totalArchives(SessionContext sc, String key) {
        boolean lock = lockManager.acquireConnection(SecuredTaskAdapterManager.class.getName());
        try {
            var session = lockManager.getDBSession().getSession();
            final String query;
            final boolean byNumber = key != null && !key.isEmpty();
            if (byNumber) {
                query = "select count(archive_taskid) from gr_archive where archive_task ->> 'number' = ?";
            } else {
                query = "select count(archive_taskid) from gr_archive";
            }
            StringBuilder hold = new StringBuilder();
            session.doWork(
                    connect -> {
                        try (PreparedStatement st = connect.prepareStatement(query)) {
                            if (byNumber) {
                                st.setString(1, key);
                            }
                            try (ResultSet cursor = st.executeQuery()) {
                                if (cursor.next()) {
                                    hold.append(cursor.getString(1));
                                }
                            }
                        }
                    }
            );
            return Integer.valueOf(hold.toString());
        } finally {
            if (lock) {
                lockManager.releaseConnection(SecuredTaskAdapterManager.class.getName());
            }
        }
    }

    public List<SecuredTaskArchiveBean> archives(SessionContext sc, String key, int limit, int offset) throws GranException {
        boolean lock = lockManager.acquireConnection(SecuredTaskAdapterManager.class.getName());
        try {
            var session = lockManager.getDBSession().getSession();
            var rsl = new ArrayList<SecuredTaskArchiveBean>();
            final List<String> lst = new ArrayList<>();
            if (key != null && !key.isEmpty()) {
                session.doWork(
                        connect -> {
                            try (PreparedStatement st = connect.prepareStatement(
                                    "select archive_task #>> '{}' from gr_archive where archive_task ->> 'number' = ? LIMIT ? OFFSET ?")) {
                                st.setString(1, key);
                                st.setInt(2, limit);
                                st.setInt(3, offset);
                                try (ResultSet cursor = st.executeQuery()) {
                                    while (cursor.next()) {
                                        lst.add(cursor.getString(1));
                                    }
                                }
                            }
                        }
                );
            } else {
                Query query = session.createNativeQuery("select archive_task #>> '{}' from gr_archive LIMIT ?1 OFFSET ?2 ");
                query.setParameter(1, limit);
                query.setParameter(2, offset);
                lst.addAll((List<String>) query.list());
            }
            for (String text : lst) {
                JSONObject task = new JSONObject(text);
                JSONObject status = task.getJSONObject("status");
                JSONObject category = task.getJSONObject("category");
                JSONObject submitter = task.getJSONObject("submitter");
                JSONObject handler = task.getJSONObject("handler");
                JSONObject handlerGroup = task.getJSONObject("handlerGroup");
                JSONObject priority = task.getJSONObject("priority");
                JSONObject resolution = task.getJSONObject("resolution");
                rsl.add(
                        new SecuredTaskArchiveBean(jsonToTaskRelatedInfo(task), sc, null, null, null, task.optString("path"), status, category, submitter, handler, handlerGroup, resolution, priority)
                );
            }
            return rsl;
        } finally {
            if (lock) {
                lockManager.releaseConnection(SecuredTaskAdapterManager.class.getName());
            }
        }
    }

    private TaskRelatedInfo jsonToTaskRelatedInfo(JSONObject task) {
        JSONObject status = task.getJSONObject("status");
        JSONObject category = task.getJSONObject("category");
        return new TaskRelatedInfo(
                task.getString("id"), task.optString("description", null), null, task.optString("name"),
                task.optString("shortname"), mlsToCalendar(task.optLong("submitdate"), null),
                mlsToCalendar(task.optLong("updatedate"), null), mlsToCalendar(task.optLong("closedate"), null),
                task.optLong("abudget"), task.optLong("budget"),
                mlsToCalendar(task.optLong("deadline"), null), task.getString("number"),
                task.optString("submitterid"),
                task.optString("handlerid", null),
                task.optString("handlerUserId", null), task.optString("handlerGroupId", null),
                task.optString("parentid", null), category.optString("id", null), task.optString("workflowid", null),
                status.optString("id", null), task.optString("resolutionid", null), task.optString("priorityid", null)
        );
    }

    public SecuredTaskArchiveBean archiveById(SessionContext sc, final String id) throws GranException {
        return archiveBy(sc, "select archive_task #>> '{}' from gr_archive where archive_taskid = ?", id);
    }

    private SecuredTaskArchiveBean archiveBy(SessionContext sc, String query, final String arg) throws GranException {
        boolean lock = lockManager.acquireConnection(SecuredTaskAdapterManager.class.getName());
        try {
            var session = lockManager.getDBSession().getSession();
            StringBuilder rsl = new StringBuilder();
            session.doWork(
                    connect -> {
                        try (PreparedStatement st = connect.prepareStatement(query)) {
                            st.setString(1, arg);
                            try (ResultSet cursor = st.executeQuery()) {
                                if (cursor.next()) {
                                    rsl.append(cursor.getString(1));
                                }
                            }
                        }
                    }
            );
            HashMap<String, String> udfs = new HashMap<>();
            JSONObject json = new JSONObject(rsl.toString());
            JSONArray judfs = json.getJSONArray("udfs");
            for (int i = 0; i < judfs.length(); i++) {
                JSONObject obj = judfs.getJSONObject(i);
                udfs.put(obj.optString("name"), obj.optString("value", null));
            }
            List<SecuredMessageArchiveBean> msgs = new ArrayList<>();
            JSONArray msges = json.getJSONArray("messages");
            for (int i = 0; i < msges.length(); i++) {
                JSONObject obj = msges.getJSONObject(i);
                List<SecuredTaskAttachmentBean> attachments = new ArrayList<>();
                JSONArray matts = json.getJSONArray("attachments");
                for (int j = 0; j < matts.length(); j++) {
                    JSONObject matt = matts.getJSONObject(j);
                    AttachmentCacheItem item = new AttachmentCacheItem(matt.optString("id"), matt.optString("taskId"), obj.optString("messageId", null),
                            matt.optString("userId"), matt.optString("name"), matt.optString("description")
                    );
                    item.setFile(AttachmentManager.getInstance().getAttachmentFile(item.getTaskId(), item.getUserId(), item.getId(),true));
                    attachments.add(new SecuredTaskAttachmentArchiveBean(item, sc));
                }
                msgs.add(new SecuredMessageArchiveBean(
                        new MessageCacheItem(
                                obj.optString("id"),
                                obj.optString("description"),
                                MacrosUtil.mlsToCalendar(obj.optLong("time"), null),
                                obj.optLong("hrs"),
                                MacrosUtil.mlsToCalendar(obj.optLong("deadline"), null),
                                obj.optLong("budget"),
                                obj.optString("taskid"),
                                obj.optString("submitterid", null),
                                obj.optString("resolutionid", null),
                                obj.optString("priorityid", null),
                                obj.optString("handlerid", null),
                                obj.optString("handlerUserid", null),
                                obj.optString("handlergroupid", null),
                                obj.optString("mstatusid"),
                                null),
                        obj.optJSONObject("mstatus"), obj.optJSONObject("resolution"), obj.optJSONObject("priority"), obj.optJSONObject("submitter"),
                        obj.optJSONObject("handler"), obj.optJSONObject("handlerGroup"),
                        attachments)
                );
            }

            List<SecuredTaskAttachmentBean> attachments = new ArrayList<>();
            JSONArray messages = json.getJSONArray("attachments");
            for (int i = 0; i < messages.length(); i++) {
                JSONObject obj = messages.getJSONObject(i);
                AttachmentCacheItem item = new AttachmentCacheItem(obj.optString("id"), obj.optString("taskId"), obj.optString("messageId", null),
                        obj.optString("userId"), obj.optString("name"), obj.optString("description")
                );
                item.setFile(AttachmentManager.getInstance().getAttachmentFile(item.getTaskId(), item.getUserId(), item.getId(),true));
                attachments.add(new SecuredTaskAttachmentArchiveBean(item, sc));
            }
            JSONObject status = json.getJSONObject("status");
            JSONObject category = json.getJSONObject("category");
            JSONObject submitter = json.getJSONObject("submitter");
            JSONObject handler = json.getJSONObject("handler");
            JSONObject handlerGroup = json.getJSONObject("handlerGroup");
            JSONObject priority = json.getJSONObject("priority");
            JSONObject resolution = json.getJSONObject("resolution");
            return new SecuredTaskArchiveBean(jsonToTaskRelatedInfo(json), sc, udfs, attachments, msgs, json.optString("path"), status, category, submitter, handler, handlerGroup, resolution, priority);
        } catch (Exception e) {
            throw new GranException(e);
        } finally {
            if (lock) {
                lockManager.releaseConnection(SecuredTaskAdapterManager.class.getName());
            }
        }
    }

    public SecuredTaskArchiveBean archiveByNumber(SessionContext sc, final String number) throws GranException {
        return archiveBy(
                sc,
                "select archive_task #>> '{}' from gr_archive where archive_task ->> 'number' = ?", number);
    }

    private boolean isArchived(String id) {
        boolean lock = lockManager.acquireConnection(SecuredTaskAdapterManager.class.getName());
        try {
            var session = lockManager.getDBSession().getSession();
            Query query = session.createSQLQuery("select count(archive_taskid) as count from gr_archive where archive_taskid = ?");
            query.setParameter(1, id);
            return ((BigInteger) query.list().iterator().next()).shortValue() > 0;
        } finally {
            if (lock) {
                lockManager.releaseConnection(SecuredTaskAdapterManager.class.getName());
            }
        }
    }

    public void deleteArchive(SessionContext sc, String archiveId) {
        boolean lock = lockManager.acquireConnection(SecuredTaskAdapterManager.class.getName());
        try {
            var session = lockManager.getDBSession().getSession();
            Query query = session.createSQLQuery("delete from gr_archive where archive_taskid = ?");
            query.setParameter(1, archiveId);
            query.executeUpdate();
        } finally {
            if (lock) {
                lockManager.releaseConnection(SecuredTaskAdapterManager.class.getName());
            }
        }
    }
}
