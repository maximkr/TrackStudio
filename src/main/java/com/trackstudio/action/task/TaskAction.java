package com.trackstudio.action.task;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.trackstudio.action.GeneralAction;
import com.trackstudio.app.Preferences;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.kernel.cache.TaskRelatedManager;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.tools.Null;


public class TaskAction extends Action {
    private static Log log = LogFactory.getLog(TaskAction.class);
    private static final LockManager lockManager = LockManager.getInstance();

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            SessionContext sc = GeneralAction.getInstance().imports(request, response);
            if (sc == null)
                return null;

            String taskId = GeneralAction.getInstance().getTaskIdByProperties(sc, request);
            SecuredTaskBean targetTask = AdapterManager.getInstance().getSecuredFindAdapterManager().findTaskById(sc, taskId);

            if (targetTask == null || !targetTask.isOnSight()) {
                taskId = "1";
                targetTask = new SecuredTaskBean("1", sc);
            }
            sc.setRequestAttribute(request, "id", taskId);
            if (sc.allowedByACL(taskId) && (Null.isNotNull(request.getParameter("asView")) || TaskRelatedManager.getInstance().getChildrenId(taskId).isEmpty() || Preferences.getDefaultLink(targetTask.getCategory().getPreferences()).equals(Preferences.VIEW_TASK_PROPERTIES))) {
                return mapping.findForward("viewTaskPage");
            }
            if (sc.taskOnSight(taskId)) {
                return mapping.findForward("subtaskPage");
            }

            return mapping.findForward("taskFilterListPage");
        } catch (Exception ge) {
            request.setAttribute("javax.servlet.jsp.jspException", ge);
            return mapping.findForward("error");
        } finally {
            if (w) lockManager.releaseConnection();
        }
    }
}

