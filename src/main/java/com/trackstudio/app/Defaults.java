package com.trackstudio.app;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.TaskRelatedManager;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.kernel.manager.FindManager;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.model.Priority;
import com.trackstudio.model.Resolution;
import com.trackstudio.secured.SecuredPriorityBean;
import com.trackstudio.secured.SecuredResolutionBean;
import com.trackstudio.tools.HibernateUtil;

import net.jcip.annotations.Immutable;

/**
 * Класс, описывающий значения по умолчанию
 */
@Immutable
public class Defaults {

    private static final Log log = LogFactory.getLog(Defaults.class);
    private static final HibernateUtil hu = new HibernateUtil();
    private static final LockManager lockManager = LockManager.getInstance();
    /**
     * Возвращает тип сообщения по умолчанию для задачи
     *
     * @param taskId ID задачи
     * @return ID типа сообщения
     * @throws GranException при необходимости
     */
    public static String getDefaultMstatusId(String taskId) throws GranException {
        List l = hu.getList("select m.id from com.trackstudio.model.Mstatus as m, com.trackstudio.model.Task as t where t.id=? and " +
                "m.workflow=t.category.workflow", taskId);
        log.warn("Can't find default message type for the workflow " + KernelManager.getFind().
                findWorkflow(TaskRelatedManager.getInstance().find(taskId).getWorkflowId()).getName() +
                ". Please choose default message type.");
        if (l.isEmpty())
            throw new GranException("there are no available status for task:" + taskId);
        return (String) l.get(0);
    }

    /**
     * Возвращает приоритет по умолчанию для процесса
     *
     * @param workflowId ID процесса
     * @return приоритет
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Priority
     */
    public static Priority getPriority(String workflowId) throws GranException {
        if (workflowId == null)
            return null;
        List col = hu.getList("from com.trackstudio.model.Priority p where p.workflow=? and p.def=1", workflowId);
        if (col != null && !col.isEmpty()) {
            return FindManager.getFind().findPriority(((Priority) col.get(0)).getId());
        } else {
            return null;
        }
    }

    /**
     * Возвращает приоритет по умолчанию для процесса
     *
     * @param sc         сессия
     * @param workflowId ID процесса
     * @return приоритет
     * @throws GranException при необходимости
     * @see com.trackstudio.model.Priority
     */
    public static SecuredPriorityBean getPriority(SessionContext sc, String workflowId) throws GranException {
        if (workflowId == null)
            return null;
        Priority priority = getPriority(workflowId);
        if (priority != null) {
            return new SecuredPriorityBean(priority, sc);
        } else {
            return null;
        }
    }

    /**
     * Возврашает резолюцию по умолчанию для типа сообщения
     *
     * @param sc      сессия
     * @param mstatus ID типа сообщения
     * @return резолюцию
     * @throws GranException при необходимости
     * @see com.trackstudio.secured.SecuredResolutionBean
     */
    public static SecuredResolutionBean getResolution(SessionContext sc, String mstatus) throws GranException {
        boolean w = lockManager.acquireConnection(Defaults.class.getSimpleName());
        try {
            log.trace("getMsgResolution");
            List<Resolution> col = hu.getList("select res from com.trackstudio.model.Resolution res where res.mstatus=? and res.isdefault=1", mstatus);
            if (col != null && !col.isEmpty()) {
                Resolution pr = col.get(0);
                return new SecuredResolutionBean(pr, sc);
            } else {
                return null;
            }
        } finally {
            if (w) lockManager.releaseConnection(Defaults.class.getSimpleName());
        }
    }
}
