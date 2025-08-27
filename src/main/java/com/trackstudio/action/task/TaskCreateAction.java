package com.trackstudio.action.task;

import java.util.ArrayList;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.trackstudio.action.TSDispatchAction;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.UserException;
import com.trackstudio.form.TaskForm;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.secured.SecuredCategoryBean;
import com.trackstudio.secured.SecuredStatusBean;

public class TaskCreateAction extends TSDispatchAction {
    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            //TaskForm bf = (TaskForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");

            response.addCookie(createCookie(sc.getCurrentSpace(), "TaskCreateAction"));
            String id = request.getParameter("id");
            sc.setRequestAttribute(request, "id", id);
            if (sc.allowedByACL(id)) {
                ArrayList availColl = AdapterManager.getInstance().getSecuredCategoryAdapterManager().getAvailableCategoryList(sc, id);
                ArrayList categoryColl = AdapterManager.getInstance().getSecuredCategoryAdapterManager().getCreatableCategoryList(sc, id);
                categoryColl.retainAll(availColl);
                sc.setRequestAttribute(request, "categories", new ArrayList(new TreeSet(categoryColl)));
            }
            return mapping.findForward("createTaskTileJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }


    public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            TaskForm tf = (TaskForm) form;
            String id = tf.getId();
            ArrayList<SecuredCategoryBean> availColl = AdapterManager.getInstance().getSecuredCategoryAdapterManager().getAvailableCategoryList(sc, id);
            ArrayList<SecuredCategoryBean> categoryColl = AdapterManager.getInstance().getSecuredCategoryAdapterManager().getCreatableCategoryList(sc, id);
            categoryColl.retainAll(availColl);

            for (SecuredCategoryBean c : categoryColl) {
                if (request.getParameter(c.getId()) != null) tf.setCategory(c.getId());
            }
            SecuredCategoryBean categoryBean = AdapterManager.getInstance().getSecuredFindAdapterManager().findCategoryById(sc, tf.getCategory());
            String workflowId = categoryBean.getWorkflowId();
            SecuredStatusBean startStatus = AdapterManager.getInstance().getSecuredFindAdapterManager().findStatusById(sc, KernelManager.getWorkflow().getStartStateId(workflowId));
            if (startStatus == null)
                throw new UserException("ERROR_WORKFLOW_HAS_NOT_START_STATE", new Object[]{categoryBean.getWorkflow().getName()});
            return mapping.findForward("editTaskPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }
}
