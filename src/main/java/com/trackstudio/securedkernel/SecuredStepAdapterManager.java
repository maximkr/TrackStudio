package com.trackstudio.securedkernel;

import java.util.ArrayList;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.AccessDeniedException;
import com.trackstudio.exception.GranException;
import com.trackstudio.exception.InvalidParameterException;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.secured.SecuredMstatusBean;
import com.trackstudio.secured.SecuredPrstatusBean;
import com.trackstudio.secured.SecuredStatusBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.tools.SecuredBeanUtil;

import net.jcip.annotations.Immutable;

/**
 * Класс SecuredStepAdapterManager содержит методы для работы с типами сообщений
 */
@Immutable
public class SecuredStepAdapterManager {

    private static final Log log = LogFactory.getLog(SecuredStepAdapterManager.class);
    private static final LockManager lockManager = LockManager.getInstance();

    /**
     * Возвращает список ID доступных типов сообщений для задачи и текущего пользователя
     *
     * @param sc     сессия пользователя
     * @param taskId ID задачи
     * @return список сообещний
     * @throws GranException при необходимости
     */
    public ArrayList<SecuredMstatusBean> getAvailableMstatusList(SessionContext sc, String taskId) throws GranException {
        log.trace("getAvailableMstatusList");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getAvailableMstatusList", "sc", sc);
        if (taskId == null)
            throw new InvalidParameterException(this.getClass(), "getAvailableMstatusList", "taskId", sc);
        //if (!((sc.canAction(com.trackstudio.kernel.cache.Action.viewMessageType, taskId) || sc.canAction(Action.viewMessage, taskId) || sc.canAction(Action.createMessage, taskId)) && sc.allowedByACL(taskId)))
        //    throw new AccessDeniedException(this.getClass(), "getAvailableMstatusList", sc);
        if (!sc.allowedByACL(taskId))
            throw new AccessDeniedException(this.getClass(), "getAvailableMstatusList", sc, "!sc.allowedByACL(taskId)", taskId);
        ArrayList<SecuredMstatusBean> ret = new ArrayList<SecuredMstatusBean>();
        boolean w  = lockManager.acquireConnection(SecuredStepAdapterManager.class.getSimpleName());
        try {
            SecuredFindAdapterManager find = AdapterManager.getInstance().getSecuredFindAdapterManager();
            for (String s : KernelManager.getStep().getAvailableMstatusList(taskId, sc.getUserId()))
                ret.add(find.findMstatusById(sc, s));
        } finally {
            if (w) lockManager.releaseConnection(SecuredStepAdapterManager.class.getSimpleName());
        }
        return ret;
    }

    /**
     * Возвращает список ответственных для редактирвоания задачи
     *
     * @param sc     сессия пользователя
     * @param taskId ID хажачи
     * @param isNew  Новая задача или нет
     * @return спсиок пользователей, которые могут быть ответственными
     * @throws GranException при необходимости
     * @see com.trackstudio.kernel.cache.UserRelatedInfo
     */
    public ArrayList<SecuredUserBean> getTaskEditHandlerList(SessionContext sc, String taskId, boolean isNew) throws GranException {
        log.trace("getTaskEditHandlerList");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getTaskEditHandlerList", "sc", sc);
        if (taskId == null)
            throw new InvalidParameterException(this.getClass(), "getTaskEditHandlerList", "taskId", sc);
        String categoryId = new SecuredTaskBean(taskId, sc).getCategoryId();
        return getTaskEditHandlerList(sc, taskId, categoryId, isNew);
    }

    /**
     * Возвращает список ответственных для редактирвоания задачи
     *
     * @param sc     сессия пользователя
     * @param taskId ID хажачи
     * @return спсиок пользователей, которые могут быть ответственными
     * @throws GranException при необходимости
     * @see com.trackstudio.kernel.cache.UserRelatedInfo
     */
    public ArrayList<SecuredUserBean> getTaskEditHandlerList(SessionContext sc, String taskId) throws GranException {
        return getTaskEditHandlerList(sc, taskId, false);
    }

    /**
     * Возвращает список ответственных для редактирвоания задачи
     *
     * @param sc         сессия пользователя
     * @param taskId     ID хажачи
     * @param categoryId ID категории
     * @param isNew      Новая задача или нет
     * @return спсиок пользователей, которые могут быть ответственными
     * @throws GranException при необходимости
     * @see com.trackstudio.kernel.cache.UserRelatedInfo
     */
    public ArrayList<SecuredUserBean> getTaskEditHandlerList(SessionContext sc, String taskId, String categoryId, boolean isNew) throws GranException {
        log.trace("getTaskEditHandlerList");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getTaskEditHandlerList", "sc", sc);
        if (taskId == null)
            throw new InvalidParameterException(this.getClass(), "getTaskEditHandlerList", "taskId", sc);
        if (categoryId == null)
            throw new InvalidParameterException(this.getClass(), "getTaskEditHandlerList", "categoryId", sc);
        if (!sc.allowedByACL(taskId))
            throw new AccessDeniedException(this.getClass(), "getTaskEditHandlerList", sc, "!sc.allowedByACL(taskId)", taskId);
        return new ArrayList<SecuredUserBean>(new TreeSet<SecuredUserBean>(SecuredBeanUtil.toArrayListWithoutCanView(sc, KernelManager.getStep().getTaskEditHandlerList(taskId, categoryId, isNew, sc.getUserId()))));
    }

    /**
     * Возвращает список ответственных стутасов для редактирвоания задачи
     *
     * @param sc         сессия пользователя
     * @param taskId     ID
     * @param categoryId ID категории
     * @return спсиок статусов, которые могут быть ответственными
     * @throws GranException при необходимости
     * @see com.trackstudio.kernel.cache.UserRelatedInfo
     */
    public ArrayList<SecuredPrstatusBean> getTaskEditGroupHandlerList(SessionContext sc, String taskId, String categoryId) throws GranException {
        return getTaskEditGroupHandlerList(sc, taskId, categoryId, false);
    }

    /**
     * Возвращает список ответственных стутасов для редактирвоания задачи
     *
     * @param sc         сессия пользователя
     * @param taskId     ID хажачи
     * @param categoryId ID категории
     * @param isNew      Новая задача или нет
     * @return спсиок статусов, которые могут быть ответственными
     * @throws GranException при необходимости
     * @see com.trackstudio.kernel.cache.UserRelatedInfo
     */
    public ArrayList<SecuredPrstatusBean> getTaskEditGroupHandlerList(SessionContext sc, String taskId, String categoryId, boolean isNew) throws GranException {
        log.trace("getTaskEditGroupHandlerList");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getTaskEditGroupHandlerList", "sc", sc);
        if (taskId == null)
            throw new InvalidParameterException(this.getClass(), "getTaskEditGroupHandlerList", "taskId", sc);
        if (categoryId == null)
            throw new InvalidParameterException(this.getClass(), "getTaskEditGroupHandlerList", "categoryId", sc);
        if (!sc.allowedByACL(taskId))
            throw new AccessDeniedException(this.getClass(), "getTaskEditGroupHandlerList", sc, "!sc.allowedByACL(taskId)", taskId);
        return new ArrayList<SecuredPrstatusBean>(new TreeSet<SecuredPrstatusBean>(SecuredBeanUtil.toArrayListWithoutCanView(sc, KernelManager.getStep().getTaskEditGroupHandlerList(taskId, categoryId, isNew))));
    }

    /**
     * Возвращает следующий статуса для типа сообщения
     *
     * @param sc        сессия пользователя
     * @param taskId    ID задачи
     * @param mstatusId ID типа сообщения
     * @return ID статуса
     * @throws GranException при необходимости
     */
    public SecuredStatusBean getNextStatus(SessionContext sc, String taskId, String mstatusId) throws GranException {
        log.trace("getNextStatus");
        if (sc == null)
            throw new InvalidParameterException(this.getClass(), "getNextStatus", "sc", sc);
        if (taskId == null)
            throw new InvalidParameterException(this.getClass(), "getNextStatus", "taskId", sc);
        if (mstatusId == null)
            throw new InvalidParameterException(this.getClass(), "getNextStatus", "mstatusId", sc);
        if (!sc.allowedByACL(taskId))
            throw new AccessDeniedException(this.getClass(), "getNextStatus", sc, "!sc.allowedByACL(taskId)", taskId);
        String stateId = KernelManager.getStep().getNextStatusId(taskId, mstatusId);
        if (stateId != null)
            return new SecuredStatusBean(KernelManager.getFind().findStatus(stateId), sc);
        return null;

    }
}