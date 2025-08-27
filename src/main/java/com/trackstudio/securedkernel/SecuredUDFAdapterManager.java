package com.trackstudio.securedkernel;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.UdfValue;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.constants.UdfConstants;
import com.trackstudio.exception.AccessDeniedException;
import com.trackstudio.exception.EmptyListException;
import com.trackstudio.exception.GranException;
import com.trackstudio.exception.InvalidParameterException;
import com.trackstudio.exception.TypesMismatchException;
import com.trackstudio.exception.UserException;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.cache.TaskRelatedInfo;
import com.trackstudio.kernel.cache.TaskRelatedManager;
import com.trackstudio.kernel.cache.UDFCacheItem;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.kernel.manager.SafeString;
import com.trackstudio.kernel.manager.UdfManager;
import com.trackstudio.model.Task;
import com.trackstudio.model.Udf;
import com.trackstudio.model.User;
import com.trackstudio.secured.SecuredMstatusBean;
import com.trackstudio.secured.SecuredPrstatusBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredTaskUDFBean;
import com.trackstudio.secured.SecuredUDFBean;
import com.trackstudio.secured.SecuredUDFValueBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.secured.SecuredUserUDFBean;
import com.trackstudio.secured.SecuredWorkflowBean;
import com.trackstudio.secured.SecuredWorkflowUDFBean;
import com.trackstudio.startup.Config;
import com.trackstudio.tools.Null;
import com.trackstudio.tools.Pair;
import com.trackstudio.tools.ParameterValidator;
import com.trackstudio.tools.formatter.DateFormatter;
import com.trackstudio.view.UDFValueViewCSV;

import net.jcip.annotations.Immutable;

/**
 * Класс SecuredUDFAdapterManager содержит методы для работы с настраиваемыми пользовательскими полями
 */
@Immutable
public class SecuredUDFAdapterManager {

    private static final Log log = LogFactory.getLog(SecuredUDFAdapterManager.class);
    private static final ParameterValidator pv = new ParameterValidator();
    private static final LockManager lockManager = LockManager.getInstance();

    /**
     * Создает  пользовательское поле для задачи
     *
     * @param sc                    сессия пользователя
     * @param taskId                ID задачи
     * @param caption               Название поля
     * @param referencedbycaption   Обратное название
     * @param order                 Порядок
     * @param def                   Значение по умолчанию
     * @param udflistId             Значение объекта Udflist
     * @param required              Обязательность поля
     * @param htmlview              Вид html или текстовый
     * @param type                  Тип поля
     * @param formulaLongtext       ID скрипта
     * @param lookupformulaLongtext ID lookup-скрпита
     * @param lookuponly            Использовать тольок lookup-значения или нет
     * @param cachevalues           Кешировать вычисляемые значения или нет
     * @param initial               Начальное выбранное значение из списка значений (если он есть)
     * @return ID созданного поля
     * @throws GranException при необходимости
     */
    public String createTaskUdf(SessionContext sc, String taskId, String caption, String referencedbycaption, int order, String def,
                                String udflistId, boolean required, boolean htmlview, Integer type, String formulaLongtext, String lookupformulaLongtext, boolean lookuponly, boolean cachevalues, String initial) throws GranException {
        log.trace("createTaskUdf");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "createTaskUdf", "sc", sc);
        if (taskId == null)
            throw new InvalidParameterException(this.getClass(), "createTaskUdf", "taskId", sc);
        if (caption == null || pv.badSmallDesc(caption))
            throw new InvalidParameterException(this.getClass(), "createTaskUdf", "caption", sc);
        if (pv.badSmallDesc(def))
            throw new InvalidParameterException(this.getClass(), "createTaskUdf", "def", sc);
        //if (!(sc.canAction(com.trackstudio.kernel.cache.Action.createTaskCustomization, taskId) && sc.allowedByACL(taskId)))
        //    throw new AccessDeniedException(this.getClass(), "createTaskUdf", sc);
        if (!sc.canAction(Action.manageTaskUDFs, taskId))
            throw new AccessDeniedException(this.getClass(), "createTaskUdf", sc, "!sc.canAction(com.trackstudio.kernel.cache.Action.createTaskCustomization, taskId)", taskId);
        if (!sc.allowedByACL(taskId))
            throw new AccessDeniedException(this.getClass(), "createTaskUdf", sc, "!sc.allowedByACL(taskId)", taskId);
        if (udflistId != null && (type == UdfConstants.LIST || type == UdfValue.MULTILIST) && udflistId.length() == 0)
            throw new EmptyListException();
        def = getDefValue(sc, type, def);
        String id = KernelManager.getUdf().createTaskUdf(taskId, SafeString.createSafeString(caption), SafeString.createSafeString(referencedbycaption), order, SafeString.createSafeString(def), udflistId, required, htmlview, type, formulaLongtext, lookupformulaLongtext, lookuponly, cachevalues, SafeString.createSafeString(initial));

        for (SecuredPrstatusBean prsI : new TreeSet<SecuredPrstatusBean>(AdapterManager.getInstance().getSecuredPrstatusAdapterManager().getAvailablePrstatusList(sc, sc.getUserId()))) {
            AdapterManager.getInstance().getSecuredUDFAdapterManager().setUDFRule(sc, id, prsI.getId(), UdfConstants.VIEW_ALL, UdfConstants.EDIT_ALL);
        }
//        HibernateSession.closeSession();
        return id;

    }

    /**
     * Создает  пользовательское поле для процесса
     *
     * @param sc                    сессия пользователя
     * @param workflowId            ID процесса
     * @param caption               Название поля
     * @param referencedbycaption   Обратное название
     * @param order                 Порядок
     * @param def                   Значение по умолчанию
     * @param udflistId             Значение объекта Udflist
     * @param required              Обязательность поля
     * @param htmlview              Вид html или текстовый
     * @param type                  Тип поля
     * @param formulaLongtext       ID скрипта
     * @param lookupformulaLongtext ID lookup-скрпита
     * @param lookuponly            Использовать тольок lookup-значения или нет
     * @param cachevalues           Кешировать вычисляемые значения или нет
     * @param initial               Начальное выбранное значение из списка значений (если он есть)
     * @return ID созданного поля
     * @throws GranException при необходимости
     */
    public String createWorkflowUdf(SessionContext sc, String workflowId, String caption, String referencedbycaption, int order, String def,
                                    String udflistId, boolean required, boolean htmlview, Integer type,
                                    String formulaLongtext, String lookupformulaLongtext, boolean lookuponly, boolean cachevalues, String initial) throws GranException {
        log.trace("createWorkflowUdf");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "createWorkflowUdf", "sc", sc);
        if (workflowId == null)
            throw new InvalidParameterException(this.getClass(), "createWorkflowUdf", "workflowId", sc);
        if (caption == null || pv.badSmallDesc(caption))
            throw new InvalidParameterException(this.getClass(), "createWorkflowUdf", "caption", sc);
        if (pv.badSmallDesc(def))
            throw new InvalidParameterException(this.getClass(), "createWorkflowUdf", "def", sc);
        SecuredWorkflowBean workflow = AdapterManager.getInstance().getSecuredFindAdapterManager().findWorkflowById(sc, workflowId);
        if (!sc.canAction(Action.manageWorkflows, workflow.getTaskId()))
            throw new AccessDeniedException(this.getClass(), "createWorkflowUdf", sc, "!sc.canAction(Action.createWorkflowCustomization, workflow.getTaskId())", workflowId);
        if (!workflow.canManage())
            throw new AccessDeniedException(this.getClass(), "createWorkflowUdf", sc, "!workflow.canUpdate()", workflowId);
        if (udflistId != null && (type == UdfConstants.LIST || type == UdfValue.MULTILIST) && udflistId.length() == 0)
            throw new EmptyListException();
        def = getDefValue(sc, type, def);
        String id = KernelManager.getUdf().createWorkflowUdf(workflowId, SafeString.createSafeString(caption), SafeString.createSafeString(referencedbycaption), order, SafeString.createSafeString(def), udflistId, required, htmlview, type, formulaLongtext, lookupformulaLongtext, lookuponly, cachevalues, SafeString.createSafeString(initial));

        for (SecuredPrstatusBean prsI : new TreeSet<SecuredPrstatusBean>(AdapterManager.getInstance().getSecuredPrstatusAdapterManager().getAvailablePrstatusList(sc, sc.getUserId()))) {
            AdapterManager.getInstance().getSecuredUDFAdapterManager().setUDFRule(sc, id, prsI.getId(), UdfConstants.VIEW_ALL, UdfConstants.EDIT_ALL);
        }
//        HibernateSession.stopTimer();
        return id;

    }

    /**
     * Создает  пользовательское поле для пользователя
     *
     * @param sc                    сессия пользователя
     * @param userId                ID пользователя
     * @param caption               Название поля
     * @param referencedbycaption   Обратное название
     * @param order                 Порядок
     * @param def                   Значение по умолчанию
     * @param udflistId             Значение объекта Udflist
     * @param required              Обязательность поля
     * @param htmlview              Вид html или текстовый
     * @param type                  Тип поля
     * @param formulaLongtext       ID скрипта
     * @param lookupformulaLongtext ID lookup-скрпита
     * @param lookuponly            Использовать тольок lookup-значения или нет
     * @param cachevalues           Кешировать вычисляемые значения или нет
     * @param initial               Начальное выбранное значение из списка значений (если он есть)
     * @return ID созданного поля
     * @throws GranException при необходимости
     */
    public String createUserUdf(SessionContext sc, String userId, String caption, String referencedbycaption, int order, String def,
                                String udflistId, boolean required, boolean htmlview, Integer type,
                                String formulaLongtext, String lookupformulaLongtext, boolean lookuponly, boolean cachevalues, String initial) throws GranException {
        log.trace("createUserUdf");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "createUserUdf", "sc", sc);
        if (userId == null)
            throw new InvalidParameterException(this.getClass(), "createUserUdf", "userId", sc);
        if (caption == null || pv.badSmallDesc(caption))
            throw new InvalidParameterException(this.getClass(), "createUserUdf", "caption", sc);
        if (pv.badSmallDesc(def))
            throw new InvalidParameterException(this.getClass(), "createUserUdf", "def", sc);
        //if (!(sc.canAction(com.trackstudio.kernel.cache.Action.createUserCustomization, userId) && sc.allowedByACL(userId)))
        //    throw new AccessDeniedException(this.getClass(), "createUserUdf", sc);
        if (!sc.canAction(Action.manageUserUDFs, userId))
            throw new AccessDeniedException(this.getClass(), "createUserUdf", sc, "!sc.canAction(com.trackstudio.kernel.cache.Action.createUserCustomization, userId)", userId);
        if (!sc.allowedByUser(userId))
            throw new AccessDeniedException(this.getClass(), "createUserUdf", sc, "!sc.allowedByACL(userId)", userId);
        if (udflistId != null && (type == UdfConstants.LIST || type == UdfValue.MULTILIST) && udflistId.length() == 0)
            throw new EmptyListException();
        def = getDefValue(sc, type, def);
        String id = KernelManager.getUdf().createUserUdf(userId, SafeString.createSafeString(caption), SafeString.createSafeString(referencedbycaption), order, SafeString.createSafeString(def), udflistId, required, htmlview, type, formulaLongtext, lookupformulaLongtext, lookuponly, cachevalues, SafeString.createSafeString(initial));

        for (SecuredPrstatusBean prsI : new TreeSet<SecuredPrstatusBean>(AdapterManager.getInstance().getSecuredPrstatusAdapterManager().getAvailablePrstatusList(sc, sc.getUserId()))) {
            AdapterManager.getInstance().getSecuredUDFAdapterManager().setUDFRule(sc, id, prsI.getId(), UdfConstants.VIEW_ALL, UdfConstants.EDIT_ALL);
        }
//        HibernateSession.stopTimer();
        return id;

    }

    /**
     * Удаляет пользовательское поле для задачи
     *
     * @param sc    сессия пользователя
     * @param udfId ID поля
     * @throws GranException при необходимости
     */
    public void deleteTaskUdf(SessionContext sc, String udfId) throws GranException {
        log.trace("deleteTaskUdf");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "deleteTaskUdf", "sc", sc);
        if (udfId == null)
            throw new InvalidParameterException(this.getClass(), "deleteTaskUdf", "udfId", sc);
        SecuredTaskUDFBean taskUdf = AdapterManager.getInstance().getSecuredFindAdapterManager().findTaskUDFById(sc, udfId);
        if (!sc.canAction(Action.manageTaskUDFs, taskUdf.getTaskId()))
            throw new AccessDeniedException(this.getClass(), "deleteTaskUdf", sc, "!sc.canAction(Action.deleteTaskCustomization, taskUdf.getTaskId())", udfId);
        if (!taskUdf.canManage())
            throw new AccessDeniedException(this.getClass(), "deleteTaskUdf", sc, "!taskUdf.canUpdate()", udfId);
        KernelManager.getUdf().deleteTaskUdf(udfId);
    }

    /**
     * Удаляет пользовательское поле для процесса
     *
     * @param sc    сессия пользователя
     * @param udfId ID поля
     * @throws GranException при необходимости
     */
    public void deleteWorkflowUdf(SessionContext sc, String udfId) throws GranException {
        log.trace("deleteWorkflowUdf");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "deleteWorkflowUdf", "sc", sc);
        if (udfId == null)
            throw new InvalidParameterException(this.getClass(), "deleteWorkflowUdf", "udfId", sc);
        SecuredWorkflowUDFBean workflowUdf = AdapterManager.getInstance().getSecuredFindAdapterManager().findWorkflowUDFById(sc, udfId);
        if (!sc.canAction(Action.manageWorkflows, workflowUdf.getWorkflow().getTaskId()))
            throw new AccessDeniedException(this.getClass(), "deleteWorkflowUdf", sc, "!sc.canAction(com.trackstudio.kernel.cache.Action.deleteWorkflowCustomization, workflowUdf.getWorkflow().getTaskId())", udfId);
        if (!workflowUdf.canManage())
            throw new AccessDeniedException(this.getClass(), "deleteWorkflowUdf", sc, "!workflowUdf.canUpdate()", udfId);
        KernelManager.getUdf().deleteWorkflowUdf(udfId);
    }

    /**
     * Удаляет пользовательское поле для пользователя
     *
     * @param sc    сессия пользователя
     * @param udfId ID поля
     * @throws GranException при необходимости
     */
    public void deleteUserUdf(SessionContext sc, String udfId) throws GranException {
        log.trace("deleteUserUdf");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "deleteUserUdf", "sc", sc);
        if (udfId == null)
            throw new InvalidParameterException(this.getClass(), "deleteUserUdf", "udfId", sc);
        SecuredUserUDFBean userUdf = AdapterManager.getInstance().getSecuredFindAdapterManager().findUserUDFById(sc, udfId);
        if (!sc.canAction(Action.manageUserUDFs, userUdf.getUserId()))
            throw new AccessDeniedException(this.getClass(), "deleteUserUdf", sc, "!sc.canAction(com.trackstudio.kernel.cache.Action.deleteUserCustomization, userUdf.getUserId())", udfId);
        if (!userUdf.canManage())
            throw new AccessDeniedException(this.getClass(), "deleteUserUdf", sc, "!userUdf.canUpdate()", udfId);
        KernelManager.getUdf().deleteUserUdf(udfId);
    }

    /**
     * Устанавливает значение пользовательского поля для задачи
     *
     * @param sc        сессия пользователя
     * @param udfId     ID поля
     * @param value     Одно значение, или несколько значыений, разделенных ;
     * @param taskId    ID задачи
     * @param mstatusId ID типа сообщения
     * @throws GranException при необходимости
     */
    public void setTaskUdfValue(SessionContext sc, String udfId, String taskId, String value, String mstatusId) throws GranException {
        log.trace("setTaskUdfValue");
        log.debug("value: (" + value + ')');
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "setTaskUdfValue", "sc", sc);
        if (udfId == null)
            throw new InvalidParameterException(this.getClass(), "setTaskUdfValue", "udfId", sc);
        if (taskId == null)
            throw new InvalidParameterException(this.getClass(), "setTaskUdfValue", "taskId", sc);
        if (pv.badUdfValue(udfId, value, sc.getLocale(), sc.getTimezone()))
            throw new UserException("ERROR_CANNOT_SAVE_UDF_VALUE");
        boolean canEdit = KernelManager.getUdf().isTaskUdfEditable(taskId, sc.getUserId(), udfId);

        boolean canMstEdit = false;
        if (mstatusId != null) {
            List editableUDf = KernelManager.getUdf().getEditableUDFId(mstatusId);
            canMstEdit = editableUDf.contains(udfId);
        }
        if (!(canEdit || canMstEdit))
            throw new AccessDeniedException(this.getClass(), "setTaskUdfValue", sc, "!(canEdit || canMstEdit)", udfId + " * " + mstatusId);
        if (!sc.allowedByACL(taskId))
            throw new AccessDeniedException(this.getClass(), "setTaskUdfValue", sc, "!sc.allowedByACL(taskId)", taskId);
        KernelManager.getUdf().setTaskUdfValue(udfId, taskId, SafeString.createSafeString(value), sc.getLocale(), sc.getTimezone());
    }

    /**
     * Устанавливает значение пользовательского поля для задачи
     *
     * @param sc     сессия пользователя
     * @param udfId  ID поля
     * @param value  Одно значение, или несколько значыений, разделенных ;
     * @param taskId ID задачи
     * @throws GranException при необходимости
     */
    public void setTaskUdfValue(SessionContext sc, String udfId, String taskId, String value) throws GranException {
        setTaskUdfValue(sc, udfId, taskId, value, null);
    }

    /**
     * Устанавливает значение пользовательского поля для пользователя
     *
     * @param sc     сессия пользователя
     * @param udfId  ID поля
     * @param userId ID пользователя
     * @param value  Одно значение, или несколько значыений, разделенных ;
     * @throws GranException при необходимости
     */
    public void setUserUdfValue(SessionContext sc, String udfId, String userId, String value) throws GranException {
        log.trace("setUserUdfValue");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "setUserUdfValue", "sc", sc);
        if (udfId == null)
            throw new InvalidParameterException(this.getClass(), "setUserUdfValue", "udfId", sc);
        if (userId == null)
            throw new InvalidParameterException(this.getClass(), "setUserUdfValue", "userId", sc);
        if (pv.badUdfValue(udfId, value, sc.getLocale(), sc.getTimezone()))
            throw new UserException("ERROR_CANNOT_SAVE_UDF_VALUE");
        boolean canEdit = KernelManager.getUdf().isUserUdfEditable(userId, sc.getUserId(), udfId);
        if (!canEdit)
            throw new AccessDeniedException(this.getClass(), "setUserUdfValue", sc, "!canEdit", udfId);
        if (!sc.allowedByUser(userId))
            throw new AccessDeniedException(this.getClass(), "setUserUdfValue", sc, "!sc.allowedByACL(userId)", userId);
        KernelManager.getUdf().setUserUdfValue(udfId, userId, SafeString.createSafeString(value), sc.getLocale(), sc.getTimezone());
    }

    /**
     * Редактирует пользовательское поле для задачи
     *
     * @param sc                  сессия пользователя
     * @param udfId               ID поля
     * @param caption             Название поля
     * @param referencedbycaption Обратное название
     * @param order               Порядок
     * @param def                 Значение по умолчанию
     * @param required            Обязательность поля
     * @param htmlview            Вид html или текстовый
     * @param formula             ID скрипта
     * @param lookupformula       ID lookup-скрпита
     * @param lookuponly          Использовать тольок lookup-значения или нет
     * @param cachevalues         Кешировать вычисляемые значения или нет
     * @param initial             Начальное выбранное значение из списка значений (если он есть)
     * @throws GranException при необходимости
     */
    public void updateTaskUdf(SessionContext sc, String udfId, String caption, String referencedbycaption, int order, String def, boolean required, boolean htmlview,
                              String formula, String lookupformula, boolean lookuponly, boolean cachevalues, String initial) throws GranException {
        log.trace("updateTaskUdf");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "updateTaskUdf", "sc", sc);
        if (udfId == null)
            throw new InvalidParameterException(this.getClass(), "updateTaskUdf", "udfId", sc);
        if (caption == null || pv.badSmallDesc(caption))
            throw new InvalidParameterException(this.getClass(), "updateTaskUdf", "caption", sc);
        if (pv.badSmallDesc(def))
            throw new InvalidParameterException(this.getClass(), "updateTaskUdf", "def", sc);
        SecuredTaskUDFBean taskUdf = AdapterManager.getInstance().getSecuredFindAdapterManager().findTaskUDFById(sc, udfId);
        if (!sc.canAction(Action.manageTaskUDFs, taskUdf.getTaskId()))
            throw new AccessDeniedException(this.getClass(), "updateTaskUdf", sc, "!sc.canAction(Action.editTaskCustomization, taskUdf.getTaskId())", udfId);
        if (!taskUdf.canManage())
            throw new AccessDeniedException(this.getClass(), "updateTaskUdf", sc, "!taskUdf.canUpdate()", udfId);
        def = getDefValue(sc, taskUdf.getType(), def);
        KernelManager.getUdf().updateTaskUdf(udfId, SafeString.createSafeString(caption), SafeString.createSafeString(referencedbycaption), order, SafeString.createSafeString(def), required, htmlview, formula, lookupformula, lookuponly, cachevalues, SafeString.createSafeString(initial));
    }

    /**
     * Редактирует пользовательское поле для процесса
     *
     * @param sc                  сессия пользователя
     * @param udfId               ID поля
     * @param caption             Название поля
     * @param referencedbycaption Обратное название
     * @param order               Порядок
     * @param def                 Значение по умолчанию
     * @param required            Обязательность поля
     * @param htmlview            Вид html или текстовый
     * @param formula             ID скрипта
     * @param lookupformula       ID lookup-скрпита
     * @param lookuponly          Использовать тольок lookup-значения или нет
     * @param cachevalues         Кешировать вычисляемые значения или нет
     * @param initial             Начальное выбранное значение из списка значений (если он есть)
     * @throws GranException при необходимости
     */
    public void updateWorkflowUdf(SessionContext sc, String udfId, String caption, String referencedbycaption, int order, String def,
                                  boolean required, boolean htmlview, String formula, String lookupformula, boolean lookuponly, boolean cachevalues, String initial) throws GranException {
        log.trace("updateWorkflowUdf");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "updateWorkflowUdf", "sc", sc);
        if (udfId == null)
            throw new InvalidParameterException(this.getClass(), "updateWorkflowUdf", "udfId", sc);
        if (caption == null || pv.badSmallDesc(caption))
            throw new InvalidParameterException(this.getClass(), "updateWorkflowUdf", "caption", sc);
        if (pv.badSmallDesc(def))
            throw new InvalidParameterException(this.getClass(), "updateWorkflowUdf", "caption", sc);
        SecuredWorkflowUDFBean workflowUdf = AdapterManager.getInstance().getSecuredFindAdapterManager().findWorkflowUDFById(sc, udfId);
        if (!sc.canAction(Action.manageWorkflows, workflowUdf.getWorkflow().getTaskId()))
            throw new AccessDeniedException(this.getClass(), "updateWorkflowUdf", sc, "!sc.canAction(Action.editWorkflowCustomization, workflowUdf.getWorkflow().getTaskId())", udfId);
        if (!workflowUdf.canManage())
            throw new AccessDeniedException(this.getClass(), "updateWorkflowUdf", sc, "!workflowUdf.canUpdate()", udfId);
        //  if (UdfValue.LIST == workflowUdf.getType() && !required && def.length()==0)
        //      def = workflowUdf.getDefaultUDF();
        def = getDefValue(sc, workflowUdf.getType(), def);
        KernelManager.getUdf().updateWorkflowUdf(udfId, SafeString.createSafeString(caption), SafeString.createSafeString(referencedbycaption), order, SafeString.createSafeString(def), required, htmlview, formula, lookupformula, lookuponly, cachevalues, SafeString.createSafeString(initial));
    }

    /**
     * Редактирует пользовательское поле для пользователя
     *
     * @param sc                  сессия пользователя
     * @param udfId               ID поля
     * @param caption             Название поля
     * @param referencedbycaption Обратное название
     * @param order               Порядок
     * @param def                 Значение по умолчанию
     * @param required            Обязательность поля
     * @param htmlview            Вид html или текстовый
     * @param formula             ID скрипта
     * @param lookupformula       ID lookup-скрпита
     * @param lookuponly          Использовать тольок lookup-значения или нет
     * @param cachevalues         Кешировать вычисляемые значения или нет
     * @param initial             Начальное выбранное значение из списка значений (если он есть)
     * @throws GranException при необходимости
     */
    public void updateUserUdf(SessionContext sc, String udfId, String caption, String referencedbycaption, int order, String def, boolean required, boolean htmlview,
                              String formula, String lookupformula, boolean lookuponly, boolean cachevalues, String initial) throws GranException {
        log.trace("updateUserUdf");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "updateUserUdf", "sc", sc);
        if (udfId == null)
            throw new InvalidParameterException(this.getClass(), "updateUserUdf", "udfId", sc);
        if (caption == null || pv.badSmallDesc(caption))
            throw new InvalidParameterException(this.getClass(), "updateUserUdf", "udfId", sc);
        if (pv.badSmallDesc(def))
            throw new InvalidParameterException(this.getClass(), "updateUserUdf", "def", sc);
        SecuredUserUDFBean userUdf = AdapterManager.getInstance().getSecuredFindAdapterManager().findUserUDFById(sc, udfId);
        if (!sc.canAction(Action.manageUserUDFs, userUdf.getUserId()))
            throw new AccessDeniedException(this.getClass(), "updateUserUdf", sc, "!sc.canAction(Action.editUserCustomization, userUdf.getUserId())", udfId);
        if (!userUdf.canManage())
            throw new AccessDeniedException(this.getClass(), "updateUserUdf", sc, "userUdf.canUpdate()", udfId);
        def = getDefValue(sc, userUdf.getType(), def);
        KernelManager.getUdf().updateUserUdf(udfId, SafeString.createSafeString(caption), SafeString.createSafeString(referencedbycaption), order, SafeString.createSafeString(def), required, htmlview, formula, lookupformula, lookuponly, cachevalues, SafeString.createSafeString(initial));
    }

    private String getDefValue(SessionContext sc, Integer type, String def) throws GranException {
        log.trace("getDefValue");
        log.debug("def: (" + def + ')');
        if (def != null && def.length() != 0)
            try {
                switch (type) {
                    case UdfValue.LIST:
                        if (KernelManager.getFind().findUdflist(def) == null)
                            throw new NullPointerException("Can't find Udflist with id = " + def);
                        return def;
                    case UdfValue.MULTILIST:
                        if (KernelManager.getFind().findUdflist(def) == null)
                            throw new NullPointerException("Can't find Udflist with id = " + def);
                        return def;
                    case UdfValue.DATE:
                        return new Timestamp(new DateFormatter(sc.getTimezone(), sc.getLocale()).parseToCalendar(def).getTimeInMillis()).toString();
                    case UdfValue.FLOAT:
//                        return Double.toString(NumberFormat.getNumberInstance(DateFormatter.toLocale(sc.getLocale())).parse(def).doubleValue());
                        return def;
                    case UdfValue.INTEGER:
                        return new Integer(def).toString();
                    default:
                        return def;
                }
            } catch (Exception e) {
                throw new TypesMismatchException();
            }
        else
            return null;
    }

    /**
     * Редактирует список значений для поля Udf типа List или MultiList для задачи
     *
     * @param sc        сессия пользователя
     * @param taskId    ID задачи
     * @param udflistId ID списка значений
     * @param value     Значения
     * @throws GranException при необходимости
     */
    public void updateTaskUdflist(SessionContext sc, String taskId, String udflistId, String value) throws GranException {
        log.trace("updateUdflist");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "updateUdflist", "sc", sc);
        if (udflistId == null)
            throw new InvalidParameterException(this.getClass(), "updateUdflist", "udflistId", sc);
        if (value == null || pv.badSmallDesc(value))
            throw new InvalidParameterException(this.getClass(), "updateUdflist", "value", sc);
        if (!sc.canAction(Action.manageTaskUDFs, taskId))
            throw new AccessDeniedException(this.getClass(), "updateTaskUdflist", sc, "!sc.canAction(Action.editTaskCustomization, taskId)", taskId);
        if (!sc.allowedByACL(taskId))
            throw new AccessDeniedException(this.getClass(), "updateTaskUdflist", sc, "!sc.allowedByACL(taskId)", taskId);
        KernelManager.getUdf().updateTaskUdflist(udflistId, SafeString.createSafeString(value));
    }

    /**
     * Редактирует список значений для поля Udf типа List или MultiList для пользователя
     *
     * @param sc        сессия пользователя
     * @param userId    ID пользователя
     * @param udflistId ID списка значений
     * @param value     Значения
     * @throws GranException при необходимости
     */
    public void updateUserUdflist(SessionContext sc, String userId, String udflistId, String value) throws GranException {
        log.trace("updateUdflist");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "updateUdflist", "sc", sc);
        if (udflistId == null)
            throw new InvalidParameterException(this.getClass(), "updateUdflist", "udflistId", sc);
        if (value == null || pv.badSmallDesc(value))
            throw new InvalidParameterException(this.getClass(), "updateUdflist", "value", sc);
        if (!sc.canAction(Action.manageUserUDFs, userId))
            throw new AccessDeniedException(this.getClass(), "updateUserUdflist", sc, "!sc.canAction(Action.editUserCustomization, userId)", userId);
        if (!sc.allowedByUser(userId))
            throw new AccessDeniedException(this.getClass(), "updateUserUdflist", sc, "!sc.allowedByACL(userId)", userId);
        KernelManager.getUdf().updateUserUdflist(userId, udflistId, SafeString.createSafeString(value));
    }

    /**
     * Редактирует список значений для поля Udf типа List или MultiList для процесса
     *
     * @param sc        сессия пользователя
     * @param udflistId ID списка значений
     * @param value     Значения
     * @throws GranException при необходимости
     */
    public void updateWorkflowUdflist(SessionContext sc, String udflistId, String value) throws GranException {
        log.trace("updateUdflist");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "updateUdflist", "sc", sc);
        if (udflistId == null)
            throw new InvalidParameterException(this.getClass(), "updateUdflist", "udflistId", sc);
        if (value == null || pv.badSmallDesc(value))
            throw new InvalidParameterException(this.getClass(), "updateUdflist", "value", sc);
        String taskId = KernelManager.getFind().findWorkflow(KernelManager.getFind().findUdfsource(KernelManager.getFind().findUdf(KernelManager.getFind().findUdflist(udflistId).getUdf().getId()).getUdfsource().getId()).getWorkflow().getId()).getTask().getId();
        if (!sc.canAction(Action.manageWorkflows, taskId))
            throw new AccessDeniedException(this.getClass(), "updateWorkflowUdflist", sc, "!sc.canAction(com.trackstudio.kernel.cache.Action.editWorkflowCustomization, taskId)", taskId);
        if (!sc.allowedByACL(taskId))
            throw new AccessDeniedException(this.getClass(), "updateWorkflowUdflist", sc, "!sc.allowedByACL(taskId)", taskId);
        KernelManager.getUdf().updateWorkflowUdflist(udflistId, SafeString.createSafeString(value));
    }

    /**
     * Удаляет список значений для поля Udf типа List или MultiList для процесса
     *
     * @param sc        сессия пользователя
     * @param taskid    ID задачи
     * @param udflistId ID списка значений
     * @throws GranException при необходимости
     */
    public void deleteTaskUdflist(SessionContext sc, String taskid, String udflistId) throws GranException {
        log.trace("deleteUdflist");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "deleteUdflist", "sc", sc);
        if (udflistId == null)
            throw new InvalidParameterException(this.getClass(), "deleteUdflist", "udflistId", sc);
        if (!sc.canAction(Action.manageTaskUDFs, taskid))
            throw new AccessDeniedException(this.getClass(), "deleteTaskUdflist", sc, "!sc.canAction(Action.editTaskCustomization, taskid)", taskid);
        if (!sc.allowedByACL(taskid))
            throw new AccessDeniedException(this.getClass(), "deleteTaskUdflist", sc, "!sc.allowedByACL(taskid)", taskid);
        KernelManager.getUdf().deleteTaskUdflist(udflistId);
    }

    /**
     * Удаляет список значений для поля Udf типа List или MultiList для пользователя
     *
     * @param sc        сессия пользователя
     * @param userId    ID пользователя
     * @param udflistId ID списка значений
     * @throws GranException при необходимости
     */
    public void deleteUserUdflist(SessionContext sc, String userId, String udflistId) throws GranException {
        log.trace("deleteUdflist");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "deleteUdflist", "sc", sc);
        if (udflistId == null)
            throw new InvalidParameterException(this.getClass(), "deleteUdflist", "udflistId", sc);
        if (!sc.canAction(Action.manageUserUDFs, userId))
            throw new AccessDeniedException(this.getClass(), "deleteUserUdflist", sc, "!sc.canAction(Action.editUserCustomization, userId)", userId);
        if (!sc.allowedByUser(userId))
            throw new AccessDeniedException(this.getClass(), "deleteUserUdflist", sc, "!sc.allowedByACL(userId)", userId);
        KernelManager.getUdf().deleteUserUdflist(userId, udflistId);
    }

    /**
     * Удаляет список значений для поля Udf типа List или MultiList для процесса
     *
     * @param sc         сессия пользователя
     * @param workflowId ID процесса
     * @param udflistId  ID списка значений
     * @throws GranException при необходимости
     */
    public void deleteWorkflowUdflist(SessionContext sc, String workflowId, String udflistId) throws GranException {
        log.trace("deleteUdflist");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "deleteUdflist", "sc", sc);
        if (udflistId == null)
            throw new InvalidParameterException(this.getClass(), "deleteUdflist", "udflistId", sc);
        SecuredWorkflowBean bean = AdapterManager.getInstance().getSecuredFindAdapterManager().findWorkflowById(sc, workflowId);
        if (!sc.canAction(Action.manageWorkflows, bean.getTaskId()))
            throw new AccessDeniedException(this.getClass(), "deleteTaskUdflist", sc, "!sc.canAction(Action.editWorkflowCustomization, bean.getTaskId())", workflowId);
        if (!bean.canManage())
            throw new AccessDeniedException(this.getClass(), "deleteTaskUdflist", sc, "!bean.canUpdate()", workflowId);
        KernelManager.getUdf().deleteWorkflowUdflist(udflistId);
    }

    /**
     * Создает список значений для поля Udf типа List или MultiList для задачи
     *
     * @param sc     сессия пользователя
     * @param taskid ID задачи
     * @param udfId  ID пользовательского поля
     * @param value  Значения
     * @return ID созданного списка
     * @throws GranException при необходимости
     */
    public String addTaskUdflist(SessionContext sc, String taskid, String udfId, String value) throws GranException {
        log.trace("addUdflist");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "addUdflist", "sc", sc);
        if (udfId == null)
            throw new InvalidParameterException(this.getClass(), "addUdflist", "udfId", sc);
        if (value == null || pv.badSmallDesc(value))
            throw new InvalidParameterException(this.getClass(), "addUdflist", "value", sc);
        if (!sc.canAction(Action.manageTaskUDFs, taskid))
            throw new AccessDeniedException(this.getClass(), "addTaskUdflist", sc, "!sc.canAction(Action.editTaskCustomization, taskid)", taskid);
        if (!sc.allowedByACL(taskid))
            throw new AccessDeniedException(this.getClass(), "addTaskUdflist", sc, "!sc.allowedByACL(taskid)", taskid);
        return KernelManager.getUdf().addTaskUdflist(udfId, SafeString.createSafeString(value));
    }

    /**
     * Создает список значений для поля Udf типа List или MultiList для пользователя
     *
     * @param sc     сессия пользователя
     * @param userId ID пользователя
     * @param udfId  ID пользовательского поля
     * @param value  Значения
     * @return ID созданного списка
     * @throws GranException при необходимости
     */
    public String addUserUdflist(SessionContext sc, String userId, String udfId, String value) throws GranException {
        log.trace("addUdflist");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "addUdflist", "sc", sc);
        if (udfId == null)
            throw new InvalidParameterException(this.getClass(), "addUdflist", "udfId", sc);
        if (value == null || pv.badSmallDesc(value))
            throw new InvalidParameterException(this.getClass(), "addUdflist", "value", sc);
        if (!sc.canAction(Action.manageUserUDFs, userId))
            throw new AccessDeniedException(this.getClass(), "addUserUdflist", sc, "!sc.canAction(Action.editUserCustomization, userId)", userId);
        if (!sc.allowedByUser(userId))
            throw new AccessDeniedException(this.getClass(), "addUserUdflist", sc, "!sc.allowedByACL(userId)", userId);
        return KernelManager.getUdf().addUserUdflist(userId, udfId, SafeString.createSafeString(value));
    }

    /**
     * Создает список значений для поля Udf типа List или MultiList для процесса
     *
     * @param sc    сессия пользователя
     * @param udfId ID пользовательского поля
     * @param value Значения
     * @return ID созданного списка
     * @throws GranException при необходимости
     */
    public String addWorkflowUdflist(SessionContext sc, String workflowId, String udfId, String value) throws GranException {
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "addUdflist", "sc", sc);
        if (udfId == null)
            throw new InvalidParameterException(this.getClass(), "addUdflist", "udfId", sc);
        if (value == null || pv.badSmallDesc(value))
            throw new InvalidParameterException(this.getClass(), "addUdflist", "value", sc);
        SecuredWorkflowBean bean = AdapterManager.getInstance().getSecuredFindAdapterManager().findWorkflowById(sc, workflowId);
        if (!sc.canAction(Action.manageWorkflows, bean.getTaskId()))
            throw new AccessDeniedException(this.getClass(), "addWorkflowflist", sc, "!sc.canAction(Action.editWorkflowCustomization, bean.getTaskId())", workflowId);
        if (!bean.canManage())
            throw new AccessDeniedException(this.getClass(), "addWorkflowflist", sc, "!bean.canUpdate()", workflowId);
        return KernelManager.getUdf().addWorkflowUdflist(udfId, SafeString.createSafeString(value));
    }

    /**
     * Возвращает значение пользовательских полей для указанной задачи
     *
     * @param sc     сессия пользователя
     * @param taskId ID задачи
     * @return список значений пользовательских полей
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredUDFValueBean
     */
    public ArrayList<SecuredUDFValueBean> getUdfValues(SessionContext sc, String taskId) throws GranException {
        log.trace("getUdfValues");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getUdfValues", "sc", sc);
        if (taskId == null)
            throw new InvalidParameterException(this.getClass(), "getUdfValues", "taskId", sc);
        if (!sc.allowedByACL(taskId))
            throw new AccessDeniedException(this.getClass(), "getUdfValues", sc, "!sc.allowedByACL(taskId)", taskId);
        ArrayList<SecuredUDFValueBean> result = new ArrayList<SecuredUDFValueBean>();
        SecuredTaskBean tci = new SecuredTaskBean(taskId, sc);
        for (SecuredUDFValueBean securedUDFValueBean : tci.getUDFValuesList())
            result.add(securedUDFValueBean);
        return result;
    }

    /**
     * Возвращает Карту (Map) списка возможных значений UDF типа List и Multilist
     *
     * @param sc    сессия пользователя
     * @param udfId ID пользовательского поля
     * @return Катра (Map) списка возможных значений UDF типа List и Multilist
     * @throws GranException при необходимости
     */
    public HashMap<String, String> getUdflist(SessionContext sc, String udfId) throws GranException {
        log.trace("getUdflist");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getUdfValues", "sc", sc);
        if (udfId == null)
            throw new InvalidParameterException(this.getClass(), "getUdfValues", "taskId", sc);
        return KernelManager.getUdf().getUdflist(udfId);
    }

    /**
     * Возвращает список значений пользовательских полей для фильтра
     *
     * @param sc     сессия пользователя
     * @param taskId ID задачи
     * @return список значений пользовательских полей
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredUDFValueBean
     */
    public ArrayList<SecuredUDFValueBean> getFilterUDFValues(SessionContext sc, String taskId) throws GranException {
        log.trace("getFilterUDFValues");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getFilterUDFValues", "sc", sc);
        if (taskId == null)
            throw new InvalidParameterException(this.getClass(), "getFilterUDFValues", "taskId", sc);
        return new SecuredTaskBean(taskId, sc).getFilterUDFValues();
    }

    /**
     * Возвращает список прав доступа указанного статуса к пользовательскому полю
     *
     * @param sc         сессия пользователя
     * @param prstatusId ID статуса
     * @param udfId      ID поля
     * @return список прав доступа
     * @throws GranException при необходимости
     */
    public List<String> getUDFRuleList(SessionContext sc, String prstatusId, String udfId) throws GranException {
        log.trace("getUDFRuleList");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getUDFRuleList", "sc", sc);
        if (prstatusId == null)
            throw new InvalidParameterException(this.getClass(), "getUDFRuleList", "prstatusId", sc);
        if (udfId == null)
            throw new InvalidParameterException(this.getClass(), "getUDFRuleList", "udfId", sc);
        SecuredUDFBean udf = AdapterManager.getInstance().getSecuredFindAdapterManager().findUDFById(sc, udfId);
        SecuredPrstatusBean prstatus = AdapterManager.getInstance().getSecuredFindAdapterManager().findPrstatusById(sc, prstatusId);
        if (!udf.canView())
            throw new AccessDeniedException(this.getClass(), "getCategoryRuleList", sc, "!udf.canView()", udfId);
        if (!prstatus.canView())
            throw new AccessDeniedException(this.getClass(), "getCategoryRuleList", sc, "!prstatus.canView()", udfId);
        return KernelManager.getUdf().getUDFRuleList(prstatusId, udfId);
    }

    /**
     * Устанавливает права доступа указанного типа для статуса и поля
     *
     * @param sc         сессия пользователя
     * @param udfId      ID поля
     * @param prstatusId ID статуса
     * @param viewType   Тип права доступа на просмотр
     * @param modifyType Тип права доступа на изменение
     * @throws GranException при необходимости
     */
    public void setTaskUDFRule(SessionContext sc, String udfId, String prstatusId, String viewType, String modifyType) throws GranException {
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "setTaskUDFRule", "sc", sc);
        if (udfId == null)
            throw new InvalidParameterException(this.getClass(), "setTaskUDFRule", "udfId", sc);
        if (prstatusId == null)
            throw new InvalidParameterException(this.getClass(), "setTaskUDFRule", "prstatusId", sc);
        SecuredUDFBean udfBean = AdapterManager.getInstance().getSecuredFindAdapterManager().findUDFById(sc, udfId);
        SecuredPrstatusBean prstatusBean = AdapterManager.getInstance().getSecuredFindAdapterManager().findPrstatusById(sc, prstatusId);
        String taskId = KernelManager.getFind().findUdfsource(udfBean.getUdfSourceId()).getTask().getId();
        if (!sc.canAction(Action.manageTaskUDFs, taskId))
            throw new AccessDeniedException(this.getClass(), "setUDFRule", sc, "!sc.canAction(com.trackstudio.kernel.cache.Action.editTaskCustomizationPermission, taskId)", taskId);
        if (!(udfBean.canManage() || prstatusBean.isAllowedByACL()))
            throw new AccessDeniedException(this.getClass(), "setUDFRule", sc, "!(udfBean.canUpdate() || prstatusBean.canUpdate())", udfId + " * " + prstatusId);
        setUDFRule(sc, udfId, prstatusId, viewType, modifyType);
    }

    /**
     * Устанавливает права доступа указанного типа для статуса и поля
     *
     * @param sc         сессия пользователя
     * @param udfId      ID поля
     * @param prstatusId ID статуса
     * @param viewType   Тип права доступа на просмотр
     * @param modifyType Тип права доступа на изменение
     * @throws GranException при необходимости
     */
    public void setWorkflowUDFRule(SessionContext sc, String udfId, String prstatusId, String viewType, String modifyType) throws GranException {
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "setWorkflowUDFRule", "sc", sc);
        if (udfId == null)
            throw new InvalidParameterException(this.getClass(), "setWorkflowUDFRule", "udfId", sc);
        if (prstatusId == null)
            throw new InvalidParameterException(this.getClass(), "setWorkflowUDFRule", "prstatusId", sc);
        SecuredUDFBean udfBean = AdapterManager.getInstance().getSecuredFindAdapterManager().findUDFById(sc, udfId);
        String workflowId = KernelManager.getFind().findUdfsource(udfBean.getUdfSourceId()).getWorkflow().getId();
        SecuredWorkflowBean bean = AdapterManager.getInstance().getSecuredFindAdapterManager().findWorkflowById(sc, workflowId);
        if (!sc.canAction(Action.manageWorkflows, bean.getTaskId()))
            throw new AccessDeniedException(this.getClass(), "setWorkflowUDFRule", sc, "!sc.canAction(com.trackstudio.kernel.cache.Action.editWorkflowCustomPermission, bean.getTaskId())", workflowId);
        if (!(bean.canManage() || udfBean.canManage() || sc.canAction(Action.manageRoles, sc.getUserId())))
            throw new AccessDeniedException(this.getClass(), "setWorkflowUDFRule", sc, "!(bean.canUpdate() || udfBean.canUpdate())", udfId);
        setUDFRule(sc, udfId, prstatusId, viewType, modifyType);
    }

    /**
     * Устанавливает права доступа указанного типа для статуса и типа сообщения
     *
     * @param sc        сессия пользователя
     * @param udfId     ID поля
     * @param mstatusId ID типа сообщения
     * @param view      Тип права доступа на просмотр
     * @param edit      Тип права доступа на изменение
     * @throws GranException при необходимости
     */
    public void setMstatusUDFRule(SessionContext sc, String udfId, String mstatusId, String view, String edit) throws GranException {
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "setMstatusUDFRule", "sc", sc);
        if (udfId == null)
            throw new InvalidParameterException(this.getClass(), "setMstatusUDFRule", "udfId", sc);
        if (mstatusId == null)
            throw new InvalidParameterException(this.getClass(), "setMstatusUDFRule", "mstatusId", sc);
        SecuredUDFBean udfBean = AdapterManager.getInstance().getSecuredFindAdapterManager().findUDFById(sc, udfId);
        SecuredMstatusBean mstatus = AdapterManager.getInstance().getSecuredFindAdapterManager().findMstatusById(sc, mstatusId);
        if (!(sc.canAction(Action.manageWorkflows, mstatus.getWorkflow().getTaskId()) || sc.canAction(Action.manageWorkflows, mstatus.getWorkflow().getTaskId())))
            throw new AccessDeniedException(this.getClass(), "setMstatusUDFRule", sc, "!sc.canAction(com.trackstudio.kernel.cache.Action.createWorkflowCustomization, bean.getTaskId())", mstatusId);
        if (!(mstatus.canManage() || udfBean.canManage()))
            throw new AccessDeniedException(this.getClass(), "setMstatusUDFRule", sc, "!(mstatus.canUpdate() || udfBean.canUpdate())", mstatusId + " * " + udfId);

        lockManager.getLock(udfId).lock();
        try {
        KernelManager.getUdf().removeMstatusUDFRule(udfId, mstatusId);
        KernelManager.getUdf().setMstatusUDFRule(udfId, mstatusId, view);
        KernelManager.getUdf().setMstatusUDFRule(udfId, mstatusId, edit);
        } finally {
            lockManager.getLock(udfId).unlock();
        }
    }

    /**
     * Устанавливает права доступа указанного типа для статуса и поля
     *
     * @param sc         сессия пользователя
     * @param udfId      ID поля
     * @param prstatusId ID статуса
     * @param viewType   Тип права доступа на просмотр
     * @param modifyType Тип права доступа на изменение
     * @throws GranException при необходимости
     */
    public void setUserUDFRule(SessionContext sc, String udfId, String prstatusId, String viewType, String modifyType) throws GranException {
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "setUserUDFRule", "sc", sc);
        if (udfId == null)
            throw new InvalidParameterException(this.getClass(), "setUserUDFRule", "udfId", sc);
        if (prstatusId == null)
            throw new InvalidParameterException(this.getClass(), "setUserUDFRule", "prstatusId", sc);
        SecuredUDFBean udfBean = AdapterManager.getInstance().getSecuredFindAdapterManager().findUDFById(sc, udfId);
        SecuredPrstatusBean prstatusBean = AdapterManager.getInstance().getSecuredFindAdapterManager().findPrstatusById(sc, prstatusId);
        String userId = KernelManager.getFind().findUdfsource(udfBean.getUdfSourceId()).getUser().getId();
        if (!sc.canAction(Action.manageUserUDFs, userId))
            throw new AccessDeniedException(this.getClass(), "setUserUDFRule", sc, "!sc.canAction(Action.editUserCustomizationPermission, userId)", userId);
        if (!(udfBean.canManage() || prstatusBean.isAllowedByACL()))
            throw new AccessDeniedException(this.getClass(), "setUserUDFRule", sc, "!(udfBean.canUpdate() || prstatusBean.canUpdate())", udfId);
        setUDFRule(sc, udfId, prstatusId, viewType, modifyType);
    }

    /**
     * Устанавливает права доступа указанного типа для статуса и поля
     *
     * @param sc         сессия пользователя
     * @param udfId      ID поля
     * @param prstatusId ID статуса
     * @param viewType   Тип права доступа на просмотр
     * @param modifyType Тип права доступа на изменение
     * @throws GranException при необходимости
     */
    protected void setUDFRule(SessionContext sc, String udfId, String prstatusId, String viewType, String modifyType) throws GranException {
        log.trace("setUDFRule");
        lockManager.getLock(udfId).lock();
        try {
        KernelManager.getUdf().resetUDFRule(udfId, prstatusId);
        KernelManager.getUdf().setUDFRule(udfId, prstatusId, viewType);
        KernelManager.getUdf().setUDFRule(udfId, prstatusId, modifyType);
        } finally {
            lockManager.getLock(udfId).unlock();
        }
    }

    /**
     * Проверяет может ли пользователь редактировать пользовательское поле для задачи
     *
     * @param sc     сессия пользователя
     * @param taskId ID задачи
     * @param udfId  ID поля
     * @return TRUE если доступно, FALSE если нет
     * @throws GranException при необходимости
     */
    public boolean isTaskUdfEditable(SessionContext sc, String taskId, String udfId) throws GranException {
        log.trace("isTaskUdfEditableImpl");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "isTaskUdfEditableImpl", "sc", sc);
        if (udfId == null)
            throw new InvalidParameterException(this.getClass(), "isTaskUdfEditableImpl", "udfId", sc);
        if (!sc.taskOnSight(taskId))
            throw new AccessDeniedException(this.getClass(), "isTaskUdfEditableImpl", sc, "!sc.taskOnSight(taskId)", taskId);
        return KernelManager.getUdf().isTaskUdfEditable(taskId, sc.getUserId(), udfId);
    }

    /**
     * Проверяет может ли пользователь просматривать пользовательское поле для задачи
     *
     * @param sc     сессия пользователя
     * @param taskId ID задачи
     * @param udfId  ID поля
     * @return TRUE если доступно, FALSE если нет
     * @throws GranException при необходимости
     */
    public boolean isTaskUdfViewable(SessionContext sc, String taskId, String udfId) throws GranException {
        log.trace("isTaskUdfViewableFast");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "isTaskUdfViewableFast", "sc", sc);
        if (udfId == null)
            throw new InvalidParameterException(this.getClass(), "isTaskUdfViewableFast", "udfId", sc);
        if (!sc.taskOnSight(taskId))
            throw new AccessDeniedException(this.getClass(), "isTaskUdfViewableFast", sc, "!sc.taskOnSight(taskId)", taskId);
        Set<String> prstatuses = TaskRelatedManager.getInstance().getAllowedPrstatuses(sc.getUserId(), taskId);
        return KernelManager.getUdf().isTaskUdfViewableFast(prstatuses, taskId, sc.getUserId(), udfId);
    }

    /**
     * Взвращает список доступных пользовательских полей для задачи
     *
     * @param sc     сессия пользователя
     * @param taskId ID задачи, для которой возвращаются пользовательские поля
     * @return список полей
     * @throws GranException при необходимости
     * @see com.trackstudio.kernel.cache.UDFCacheItem
     */
    public List<SecuredTaskUDFBean> getAvailableTaskUdfList(SessionContext sc, String taskId) throws GranException {
        log.trace("getAvailableTaskUDFCacheItems");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getAvailableTaskUDFCacheItems", "sc", sc);
        if (taskId == null)
            throw new InvalidParameterException(this.getClass(), "getAvailableTaskUDFCacheItems", "taskId", sc);
        if (!sc.taskOnSight(taskId))
            throw new AccessDeniedException(this.getClass(), "isUdfEditable", sc, "!sc.taskOnSight(taskId)", taskId);
        List<SecuredTaskUDFBean> res = new ArrayList<SecuredTaskUDFBean>();
        for (UDFCacheItem udf : KernelManager.getUdf().getAvailableTaskUDFCacheItems(taskId)) {
            res.add(new SecuredTaskUDFBean(udf, sc));
        }
        return res;
    }

    /**
     * Взвращает список всех доступных пользовательских полей для задачи
     *
     * @param sc     сессия пользователя
     * @param taskId ID задачи, для которой возвращаются пользовательские поля
     * @return список полей
     * @throws GranException при необходимости
     * @see com.trackstudio.kernel.cache.UDFCacheItem
     */
    public List<SecuredTaskUDFBean> getAllAvailableTaskUdfList(SessionContext sc, String taskId) throws GranException {
        log.trace("getAvailableTaskUDFCacheItems");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getAllAvailableTaskUdfList", "sc", sc);
        if (taskId == null)
            throw new InvalidParameterException(this.getClass(), "getAllAvailableTaskUdfList", "taskId", sc);
        if (!sc.taskOnSight(taskId))
            throw new AccessDeniedException(this.getClass(), "isUdfEditable", sc, "!sc.taskOnSight(taskId)", taskId);
        List<SecuredTaskUDFBean> res = new ArrayList<SecuredTaskUDFBean>();
        for (UDFCacheItem udf : KernelManager.getUdf().getAllAvailableTaskUDFCacheItems(taskId)) {
            if (sc.taskOnSight(udf.getTaskId()))
                res.add(new SecuredTaskUDFBean(udf, sc));
        }
        return res;
    }

    /**
     * Возвращает список всех пользовательских полей для статуса
     *
     * @param sc       сессия пользователя
     * @param statusId ID статуса
     * @return список пользовательских полей
     * @throws GranException при необходимости
     * @see com.trackstudio.kernel.cache.UDFCacheItem
     */
    public List<SecuredTaskUDFBean> getAllAvailableTaskUdfListForStatus(SessionContext sc, String statusId) throws GranException {
        log.trace("getAvailableTaskUDFCacheItems");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getAvailableTaskUDFCacheItems", "sc", sc);
        if (statusId == null)
            throw new InvalidParameterException(this.getClass(), "getAvailableTaskUDFCacheItems", "statusId", sc);
        List<SecuredTaskUDFBean> res = new ArrayList<SecuredTaskUDFBean>();
        for (UDFCacheItem udf : KernelManager.getUdf().getListAllTaskUDFCacheItem()) {
            if (udf.getTaskId() != null) {
                if (sc.taskOnSight(udf.getTaskId()))
                    res.add(new SecuredTaskUDFBean(udf, sc));
            }
        }
        return res;
    }

    /**
     * Взвращает список доступных пользовательских полей для пользователя
     *
     * @param sc     сессия пользователя
     * @param userId ID пользователя, для которой возвращаются пользовательские поля
     * @return список полей
     * @throws GranException при необходимости
     * @see com.trackstudio.kernel.cache.UDFCacheItem
     */
    public List<SecuredUserUDFBean> getAvailableUserUdfList(SessionContext sc, String userId) throws GranException {
        log.trace("getAvailableUserUDFCacheItems");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getAvailableUserUDFCacheItems", "sc", sc);
        if (userId == null)
            throw new InvalidParameterException(this.getClass(), "getAvailableUserUDFCacheItems", "userId", sc);
        if (!sc.userOnSight(userId))
            throw new AccessDeniedException(this.getClass(), "getAvailableUserUDFCacheItems", sc, "!sc.userOnSight(userId)", userId);
        List<SecuredUserUDFBean> res = new ArrayList<SecuredUserUDFBean>();
        for (UDFCacheItem udf : KernelManager.getUdf().getAvailableUserUDFCacheItems(userId)) {
            res.add(new SecuredUserUDFBean(udf, sc));
        }
        return res;
    }

    /**
     * Взвращает список всех доступных пользовательских полей для пользователя
     *
     * @param sc     сессия пользователя
     * @param userId ID пользователя, для которой возвращаются пользовательские поля
     * @return список полей
     * @throws GranException при необходимости
     * @see com.trackstudio.kernel.cache.UDFCacheItem
     */
    public ArrayList<SecuredUserUDFBean> getAllAvailableUserUdfList(SessionContext sc, String userId) throws GranException {
        log.trace("getAvailableUserUDFCacheItems");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getAvailableUserUDFCacheItems", "sc", sc);
        if (userId == null)
            throw new InvalidParameterException(this.getClass(), "getAvailableUserUDFCacheItems", "userId", sc);
        if (!sc.userOnSight(userId))
            throw new AccessDeniedException(this.getClass(), "getAvailableUserUDFCacheItems", sc, "!sc.userOnSight(userId)", userId);
        ArrayList<SecuredUserUDFBean> res = new ArrayList<SecuredUserUDFBean>();
        for (UDFCacheItem udf : KernelManager.getUdf().getAllAvailableUserUDFCacheItems(userId)) {
            if (sc.userOnSight(udf.getUserId()))
                res.add(new SecuredUserUDFBean(udf, sc));
        }
        return res;
    }

    /**
     * Возвращает список всех доступных пользовательских полей для статуса
     *
     * @param sc       сессия пользователя
     * @param statusId ID статуса
     * @return список пользовательских полей
     * @throws GranException при необходимости
     * @see com.trackstudio.kernel.cache.UDFCacheItem
     */
    public List<SecuredUserUDFBean> getAllAvailableUserUdfListForStatus(SessionContext sc, String statusId) throws GranException {
        log.trace("getAvailableTaskUDFCacheItems");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getAvailableTaskUDFCacheItems", "sc", sc);
        if (statusId == null)
            throw new InvalidParameterException(this.getClass(), "getAvailableTaskUDFCacheItems", "statusId", sc);
        List<SecuredUserUDFBean> res = new ArrayList<SecuredUserUDFBean>();
        for (UDFCacheItem udf : KernelManager.getUdf().getListAllUserUDFCacheItem()) {
            if (sc.userOnSight(udf.getUserId()))
                res.add(new SecuredUserUDFBean(udf, sc));
        }
        return res;
    }

    /**
     * Возвращает значение пользовательских полей для указанного пользователя
     *
     * @param sc         сессия пользователя
     * @param userId     ID пользователя
     * @param udfCaption название поля
     * @return список значений пользовательских полей
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredUDFValueBean
     */
    public String getUserUDFValue(SessionContext sc, String userId, String udfCaption) throws GranException {
        log.trace("getUserUDFValue");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getUserUDFValue", "sc", sc);
        if (userId == null)
            throw new InvalidParameterException(this.getClass(), "getUserUDFValue", "userId", sc);
        if (!sc.userOnSight(userId))
            return null;
        ArrayList<SecuredUDFValueBean> udfList = new SecuredUserBean(userId, sc).getUDFValuesList();
        for (SecuredUDFValueBean udf : udfList) {
            if (udfCaption != null && udf.getCaption().equals(udfCaption)) {
                return new UDFValueViewCSV(udf).getValue(new SecuredUserBean(userId, sc));
            }
        }
        return null;
    }

    /**
     * Возвращает значение пользовательских полей для указанной задачи
     *
     * @param sc         сессия пользователя
     * @param taskId     ID задачи
     * @param udfCaption название поля
     * @return список значений пользовательских полей
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredUDFValueBean
     */
    public String getTaskUDFValue(SessionContext sc, String taskId, String udfCaption) throws GranException {
        log.trace("getTaskUDFValue");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getTaskUDFValue", "sc", sc);
        if (taskId == null)
            throw new InvalidParameterException(this.getClass(), "getTaskUDFValue", "userId", sc);
        if (!sc.taskOnSight(taskId))
            return null;
        ArrayList<SecuredUDFValueBean> udfList = new SecuredTaskBean(taskId, sc).getUDFValuesList();

        for (SecuredUDFValueBean udf : udfList) {
            if (udfCaption != null && udf.getCaption().equals(udfCaption)) {
                return new UDFValueViewCSV(udf).getValue(new SecuredTaskBean(taskId, sc));
            }
        }

        return null;
    }

    /**
     * Устанавливает значение пользовательского поля для задачи
     *
     * @param sc         сессия пользователя
     * @param taskId     ID задачи
     * @param udfCaption название поля
     * @param udfValue   значение поля
     * @throws GranException при необходимости
     */
    public void setTaskUDFValueSimple(SessionContext sc, String taskId, String udfCaption, String udfValue) throws GranException {
        List<String> udfList = new SecuredTaskBean(taskId, sc).getUDFsId();
        setUdfValue(sc, taskId, udfCaption, udfValue, udfList);
    }

    public void setTaskUDFValueSimple(SessionContext sc, String taskId, String udfCaption, String udfValue, List<String> udfList) throws GranException {
        setUdfValue(sc, taskId, udfCaption, udfValue, udfList);
    }

    public void setMessageUDFValueSimple(SessionContext sc, String taskId, String udfCaption, String udfValue, String mstatusId) throws GranException {
        List<String> udfList = new ArrayList<String>(new TreeSet<String>(KernelManager.getUdf().getEditableUDFId(mstatusId)));
        boolean editableTasksUdfsInOperation = Config.isTurnItOn("trackstudio.editable.tasks.udf.in.operation");
        if (editableTasksUdfsInOperation) {
            TaskRelatedInfo info = TaskRelatedManager.getInstance().find(taskId);
            for (UDFCacheItem cacheItem : info.getHierarchicalUDFCacheItems()) {
                udfList.add(cacheItem.getId());
            }
        }
        setUdfValue(sc, taskId, udfCaption, udfValue, udfList);
    }

    private void setUdfValue(SessionContext sc, String taskId, String udfCaption, String udfValue, List<String> udfList) throws GranException {
        log.trace("setTaskUDFValueSimple");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "setTaskUDFValueSimple", "sc", sc);
        if (taskId == null)
            throw new InvalidParameterException(this.getClass(), "setTaskUDFValueSimple", "taskId", sc);
        if (!sc.allowedByACL(taskId))
            throw new AccessDeniedException(this.getClass(), "setTaskUDFValueSimple", sc, "!sc.allowedByACL(taskId)", taskId);
        List<Pair<String>> udfNext = AdapterManager.getInstance().getSecuredFindAdapterManager().findUdfField(sc, udfList, udfCaption);
        Pair<String> udf = null;
        if (!udfNext.isEmpty()) {
            if (udfNext.size() == 1) {
                udf = udfNext.get(0);
            } else {
                throw new UserException("ERROR_OBJECT_NAME_IS_NOT_UNIQUE", new String[]{udfCaption});
            }
        }
        if (udf != null) {
            boolean canEditUDF1 = KernelManager.getUdf().isTaskUdfEditable(taskId, sc.getUserId(), udf.getKey(), new SecuredTaskBean(taskId, sc).getStatusId());
            boolean canEditUDF2 = false;
            if (!canEditUDF1) {
                boolean isWFUDF = KernelManager.getFind().findUdfsource(udf.getValue()).getWorkflow() != null;
                if (isWFUDF) {
                    List<String> mstatList = KernelManager.getStep().getAvailableMstatusList(taskId, sc.getUserId());
                    for (String mhrli : mstatList) {
                        List editableUDf = KernelManager.getUdf().getEditableUDFId(mhrli);
                        canEditUDF2 = editableUDf.contains(udf.getKey());
                        if (canEditUDF2)
                            break;
                    }
                }
            }
            if (canEditUDF1 || canEditUDF2) {
                String udfId = udf.getKey();
                udfValue = convertValue(udfId, udfValue);
                KernelManager.getUdf().setTaskUdfValue(udfId, taskId, SafeString.createSafeString(udfValue), sc.getLocale(), sc.getTimezone());
            }
        }
    }

    /**
     * Устанавливает значение пользовательского поля для пользователя
     *
     * @param sc         сессия пользователя
     * @param userId     ID пользователя
     * @param udfCaption название поля
     * @param udfValue   значение поля
     * @throws GranException при необходимости
     */
    public void setUserUDFValueSimple(SessionContext sc, String userId, String udfCaption, String udfValue) throws GranException {
        log.trace("setUserUDFValueSimple");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "setUserUDFValueSimple", "sc", sc);
        if (userId == null)
            throw new InvalidParameterException(this.getClass(), "setUserUDFValueSimple", "userId", sc);
        if (!sc.allowedByUser(userId))
            throw new AccessDeniedException(this.getClass(), "setUserUDFValueSimple", sc, "!sc.allowedByACL(userId)", userId);
        List udfList = new SecuredUserBean(userId, sc).getUDFs();
        SecuredUDFBean udf = null;
        for (Object anUdfList : udfList) {
            SecuredUDFBean udfNext = (SecuredUDFBean) anUdfList;
            if (udfCaption != null && udfNext.getCaption().equals(udfCaption)) {
                if (udf == null) udf = udfNext;
                else throw new UserException("ERROR_OBJECT_NAME_IS_NOT_UNIQUE", new String[]{udfCaption});
            }
        }
        if (udf != null) {
            if (KernelManager.getUdf().isUserUdfEditable(userId, sc.getUserId(), udf.getId())) {
                if (udf.getCaptionEx().equals(udfCaption)) {
                    String udfId = udf.getId();
                    udfValue = convertValue(udfId, udfValue);
                    KernelManager.getUdf().setUserUdfValue(udfId, userId, SafeString.createSafeString(udfValue), sc.getLocale(), sc.getTimezone());
                }
            }
        }
    }

    /**
     * Конвертирует значение пользовательского поля в "правильный" формат
     *
     * @param udfId ID поля
     * @param value значение поля
     * @return "правильное" значение
     * @throws GranException при необходимости
     */
    private String convertValue(String udfId, String value) throws GranException {
        if (value == null)
            return null;
        StringBuffer ret = new StringBuffer();
        Udf udf = KernelManager.getFind().findUdf(udfId);
        int type = udf.getType();
        if (type == UdfConstants.STRING && value.length() > 2000) {
            if (Null.isNull(udf.getScript())) {
                throw new UserException("STRING_TOO_LONG", new String[]{udf.getCaption()});
            } else {
                return null;
            }
        }
        if (type == UdfConstants.INTEGER) {
            try {
                return new Integer(value).toString();
            } catch (Exception e) {
                return null;
            }
        }
        if (type == UdfConstants.LIST) {
            Map hm = KernelManager.getUdf().getUdflist(udfId);
            for (Object o : hm.keySet()) {
                String id = (String) o;
                if (hm.get(id) != null && hm.get(id).toString().equals(value)) {
                    return id;
                }
            }
            return null;
        }
        if (type == UdfValue.MULTILIST) {
            Map hm = KernelManager.getUdf().getUdflist(udfId);
            StringTokenizer tk = new StringTokenizer(value.trim(), ";\n");

            while (tk.hasMoreTokens()) {
                String udflistVal = tk.nextToken();
                for (Object o : hm.keySet()) {
                    String id = (String) o;
                    if (hm.get(id) != null && hm.get(id).toString().equals(udflistVal)) {
                        ret.append(id);
                        ret.append(tk.hasMoreTokens() ? "\n" : "");
                        break;
                    }
                }
            }
            return ret.length() == 0 ? value.trim() : ret.toString();
        }
        if (type == UdfValue.TASK) {
            Boolean existWrongTasks = false;
            StringBuilder wrongTasks = new StringBuilder();
            StringTokenizer tk = new StringTokenizer(value.trim(), "; ");

            while (tk.hasMoreTokens()) {
                String number = tk.nextToken().trim();
                if (number.startsWith("#"))
                    number = number.substring(1);
                String taskId = KernelManager.getTask().findByNumber(number);
                if (taskId == null) {
                    Task task = KernelManager.getFind().findTask(number);
                    taskId = task != null ? task.getId() : null;
                }
                if (taskId != null) {
                    ret.append(taskId);
                    ret.append(tk.hasMoreTokens() ? ";" : "");
                } else {
                    existWrongTasks = true;
                    if (wrongTasks.length() > 0)
                        wrongTasks.append("; ");
                    wrongTasks.append(number);
                }
            }
            if (existWrongTasks) {
                throw new UserException("INVALID_TASK", new String[]{wrongTasks.toString()});
            }
            return ret.length() == 0 ? null : ret.toString();
        }
        if (type == UdfValue.USER) {
            Boolean existWrongUsers = false;
            StringBuilder wrongUsers = new StringBuilder();
            StringTokenizer tk = new StringTokenizer(value.trim(), ";,");

            while (tk.hasMoreTokens()) {
                String login = tk.nextToken().trim();
                if (login.startsWith("@"))
                    login = login.substring(1);
                String userId = KernelManager.getUser().findByLogin(login);
                if (userId == null) {
                    userId = KernelManager.getUser().findByLogin("@" + login);
//                    User user = KernelManager.getFind().findUser(login);
//                    userId = user != null ? user.getId() : null;
                }
                if (userId != null) {
                    ret.append(userId);
                    ret.append(tk.hasMoreTokens() ? ";" : "");
                } else {
                    existWrongUsers = true;
                    if (wrongUsers.length() > 0)
                        wrongUsers.append("; ");
                    wrongUsers.append(login);
                }
            }
            if (existWrongUsers) {
                throw new UserException("INVALID_USER", new String[]{wrongUsers.toString()});
            }
            return ret.length() == 0 ? null : ret.toString();
        }
        if (type == UdfValue.URL) {
            if (value.contains("](")) {
                value = value.substring(1, value.length() - 1);
                String[] link = value.split("\\]\\(");
                value = link[1] + "\n" + link[0];
            }
            return value;
        }
        return value;
    }

    /**
     * Метод клонирования кастом поля для задачи
     * @param sc сессия
     * @param udfId клонируемое поле
     * @throws com.trackstudio.exception.GranException при необходимости.
     */
    public void cloneTaskUdf(SessionContext sc, String udfId) throws GranException {
        log.trace("cloneUdflist");
        SecuredTaskUDFBean stu = AdapterManager.getInstance().getSecuredFindAdapterManager().findTaskUDFById(sc, udfId);
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "cloneUdflist", "sc", sc);
        if (udfId == null)
            throw new InvalidParameterException(this.getClass(), "cloneUdflist", "udflistId", sc);
        if (!sc.canAction(Action.manageTaskUDFs, stu.getTaskId()))
            throw new AccessDeniedException(this.getClass(), "cloneTaskUdflist", sc, "!sc.canAction(Action.editWorkflowCustomization, bean.getTaskId())", udfId);
        if (!stu.canManage())
            throw new AccessDeniedException(this.getClass(), "cloneTaskUdflist", sc, "!bean.canUpdate()", udfId);
        KernelManager.getUdf().cloneUdf(stu.getTaskId(), udfId, UdfManager.TASK, null);
    }

    /**
     * Метод для клонирования кастом поля процесса
     * @param sc сессия пользователя
     * @param udfId клонируемое поле
     * @throws GranException при необходимости
     */
    public void cloneWorkflowUdf(SessionContext sc, String udfId) throws GranException {
        log.trace("cloneWorkflowUdf");
        SecuredWorkflowUDFBean swu = AdapterManager.getInstance().getSecuredFindAdapterManager().findWorkflowUDFById(sc, udfId);
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "cloneWorkflowUdf", "sc", sc);
        if (udfId == null)
            throw new InvalidParameterException(this.getClass(), "cloneWorkflowUdf", "udfId", sc);

        if (!sc.canAction(Action.manageWorkflows, swu.getWorkflow().getTaskId()))
            throw new AccessDeniedException(this.getClass(), "cloneWorkflowUdf", sc, "!sc.canAction(com.trackstudio.kernel.cache.Action.cloneWorkflowCustomization, workflowUdf.getWorkflow().getTaskId())", udfId);
        if (!swu.canManage())
            throw new AccessDeniedException(this.getClass(), "cloneWorkflowUdf", sc, "!workflowUdf.canUpdate()", udfId);
        KernelManager.getUdf().cloneUdf(swu.getWorkflowId(), udfId, UdfManager.WORKFLOW, null);
    }

    /**
     * Метод клонирования кастом поля для пользователя
     * @param sc сессия
     * @param udfId клонируемое поле
     * @throws com.trackstudio.exception.GranException при необходимости.
     */
    public void cloneUserUdf(SessionContext sc, String udfId) throws GranException {
        log.trace("cloneUdflist");
        SecuredUserUDFBean stu = AdapterManager.getInstance().getSecuredFindAdapterManager().findUserUDFById(sc, udfId);
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "cloneUdflist", "sc", sc);
        if (udfId == null)
            throw new InvalidParameterException(this.getClass(), "cloneUdflist", "udflistId", sc);
        if (!sc.canAction(Action.manageUserUDFs, stu.getUserId()))
            throw new AccessDeniedException(this.getClass(), "cloneTaskUdflist", sc, "!sc.canAction(Action.editWorkflowCustomization, bean.getTaskId())", udfId);
        if (!stu.canManage())
            throw new AccessDeniedException(this.getClass(), "cloneTaskUdflist", sc, "!bean.canUpdate()", udfId);
        KernelManager.getUdf().cloneUdf(stu.getUserId(), udfId, UdfManager.USER, null);
    }

    public List<Udf> getListTriggerScript(SessionContext sc) throws GranException {
        log.trace("getUdflist");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getUdfValues", "sc", sc);
        return KernelManager.getUdf().getListTriggerScript();
    }


    public List<Udf> getListTriggerLookup(SessionContext sc) throws GranException {
        log.trace("getUdflist");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getUdfValues", "sc", sc);
        return KernelManager.getUdf().getListTriggerLookup();
    }
}
