package com.trackstudio.securedkernel;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.exception.AccessDeniedException;
import com.trackstudio.exception.GranException;
import com.trackstudio.exception.InvalidParameterException;
import com.trackstudio.kernel.cache.TaskRelatedInfo;
import com.trackstudio.kernel.cache.TaskRelatedManager;
import com.trackstudio.kernel.cache.UserRelatedManager;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUDFValueBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.tools.EggBasket;

import net.jcip.annotations.Immutable;

/**
 * Класс SecuredIndexAdapterManager содержит методы для работы с индексами Lucene
 */
@Immutable
public class SecuredIndexAdapterManager {

    private static final Log log = LogFactory.getLog(SecuredIndexAdapterManager.class);

    /**
     * Возвращает карту задач, которые ссылаются на указанную задачу
     *
     * @param task задача
     * @return карта задач
     * @throws GranException при необходимости
     * @see com.trackstudio.tools.EggBasket
     */
    public EggBasket<SecuredUDFValueBean, SecuredTaskBean> getReferencedTasksForTask(SecuredTaskBean task) throws GranException {
        log.trace("getReferencedTasksForTask");
        if (task == null)
            throw new InvalidParameterException(this.getClass(), "getReferencedTasksForTask", "task", null);

        if (!task.canView())
            throw new AccessDeniedException(this.getClass(), "getReferencedTasksForTask", task.getSecure(), "!sc.taskOnSight(taskId)", task.getId());

        EggBasket<String, String> rtlist = KernelManager.getIndex().getReferencedTasksForTask(task.getId());
        EggBasket<SecuredUDFValueBean, SecuredTaskBean> map = new EggBasket<SecuredUDFValueBean, SecuredTaskBean>();

        for (String udfId : rtlist.keySet()) {
            // Search for task. Check that referenced task exists. Index may to contain wrong data/
            List<String> refs = rtlist.get(udfId);
            for (String taskId : refs) {
                TaskRelatedInfo taskRef = TaskRelatedManager.getInstance().find(taskId);
                if (taskRef != null) {
                    SecuredTaskBean stb = new SecuredTaskBean(taskRef, task.getSecure());
                    SecuredUDFValueBean udf = stb.getUDFValues().get(udfId);
                    if (udf != null && udf.getReferencedByCaption() != null && !udf.getReferencedByCaption().isEmpty()) {
                        map.putItem(udf, stb);
                    }
                }
            }
        }
        return map;
    }

    /**
     * Возвращает карта пользователей, которые ссылаются на указанную задачу
     *
     * @param task задачa
     * @return карта задач
     * @throws GranException при необходимости
     * @see com.trackstudio.tools.EggBasket
     */
    public EggBasket<SecuredUDFValueBean, SecuredUserBean> getReferencedUsersForTask(SecuredTaskBean task) throws GranException {
        log.trace("getReferencedUsersForTask");

        if (task == null)
            throw new InvalidParameterException(this.getClass(), "getReferencedUsersForTask", "taskId", null);
        if (!task.canView())
            throw new AccessDeniedException(this.getClass(), "getReferencedUsersForTask", task.getSecure(), "!sc.taskOnSight(taskId)", task.getId());

        EggBasket<String, String> rtlist = KernelManager.getIndex().getReferencedUsersForTask(task.getId());
        EggBasket<SecuredUDFValueBean, SecuredUserBean> ret = new EggBasket<SecuredUDFValueBean, SecuredUserBean>();
        for (String udfId : rtlist.keySet()) {
            for (String userId : rtlist.get(udfId))
                if (UserRelatedManager.getInstance().isUserExists(userId)) {
                    SecuredUserBean stb = new SecuredUserBean(userId, task.getSecure());
                    SecuredUDFValueBean udf = stb.getUDFValues().get(udfId);
                    if (udf != null) {
                        for (String id : rtlist.get(udfId)) {
                            if (UserRelatedManager.getInstance().isUserExists(id))
                                ret.putItem(udf, new SecuredUserBean(id, task.getSecure()));
                        }
                    }
                }
        }
        return ret;
    }

    /**
     * Возвращает карту задач, которые ссылаются на указанного пользователя
     *
     * @param user пользователь
     * @return карта задач
     * @throws GranException при необходимости
     * @see com.trackstudio.tools.EggBasket
     */
    public EggBasket<SecuredUDFValueBean, SecuredTaskBean> getReferencedTasksForUser(SecuredUserBean user) throws GranException {
        log.trace("getReferencedTasksForUser");
        if (user == null)
            throw new InvalidParameterException(this.getClass(), "getReferencedTasksForUser", "user", null);

        if (!user.getSecure().userOnSight(user.getId()))
            throw new AccessDeniedException(this.getClass(), "getReferencedTasksForUser", user.getSecure(), "!sc.userOnSight(userId)", user.getId());

        EggBasket<String, String> rtlist = KernelManager.getIndex().getReferencedTasksForUser(user.getId());
        EggBasket<SecuredUDFValueBean, SecuredTaskBean> map = new EggBasket<SecuredUDFValueBean, SecuredTaskBean>();
        for (String udfId : rtlist.keySet()) {
            for (String taskId : rtlist.get(udfId))
                if (TaskRelatedManager.getInstance().isTaskExists(taskId)) {
                    SecuredTaskBean stb = new SecuredTaskBean(taskId, user.getSecure());
                    SecuredUDFValueBean udf = stb.getUDFValues().get(udfId);
                    if (udf != null) {
                        for (String id : rtlist.get(udfId)) {
                            if (TaskRelatedManager.getInstance().isTaskExists(id))
                                map.putItem(udf, new SecuredTaskBean(id, user.getSecure()));
                        }
                    }
                }
        }
        return map;
    }

    /**
     * Возвращает карту пользователей, которые ссылаются на указанного пользователя
     *
     * @param user пользователь
     * @return карта задач
     * @throws GranException при необходимости
     * @see com.trackstudio.tools.EggBasket
     */
    public EggBasket<SecuredUDFValueBean, SecuredUserBean> getReferencedUsersForUser(SecuredUserBean user) throws GranException {
        log.trace("getReferencedUsersForUser");
        if (user == null)
            throw new InvalidParameterException(this.getClass(), "getReferencedUsersForUser", "user", null);

        if (!user.getSecure().userOnSight(user.getId()))
            throw new AccessDeniedException(this.getClass(), "getReferencedUsersForUser", user.getSecure(), "!sc.userOnSight(userId)", user.getId());

        EggBasket<String, String> rtlist = KernelManager.getIndex().getReferencedUsersForUser(user.getId());
        EggBasket<SecuredUDFValueBean, SecuredUserBean> ret = new EggBasket<SecuredUDFValueBean, SecuredUserBean>();
        for (String udfId : rtlist.keySet()) {
            for (String userId : rtlist.get(udfId))
                if (UserRelatedManager.getInstance().isUserExists(userId)) {
                    SecuredUserBean stb = new SecuredUserBean(userId, user.getSecure());
                    SecuredUDFValueBean udf = stb.getUDFValues().get(udfId);
                    if (udf != null) {
                        for (String id : rtlist.get(udfId)) {
                            if (UserRelatedManager.getInstance().isUserExists(id))
                                ret.putItem(udf, new SecuredUserBean(id, user.getSecure()));
                        }
                    }
                }
        }
        return ret;
    }
}