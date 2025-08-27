package com.trackstudio.action.task;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.trackstudio.action.GeneralAction;
import com.trackstudio.action.UDFSaveAction;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.UserException;
import com.trackstudio.form.CustomForm;
import com.trackstudio.kernel.lock.LockManager;

public class WorkflowUDFSaveAction extends UDFSaveAction {
    private static final LockManager lockManager = LockManager.getInstance();

    public ActionForward delete(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            CustomForm form = (CustomForm) actionForm;
            SessionContext sc = GeneralAction.getInstance().imports(request, response);
            if (sc == null)
                return null;
            Map<String, String> m = form.dellists;
            for (String key : m.keySet()) {
                AdapterManager.getInstance().getSecuredUDFAdapterManager().deleteWorkflowUdflist(sc, form.getIdUdf(), key);
            }
            String[] arr2 = form.getDelete();
            if (arr2 != null)
                for (String anArr2 : arr2)
                    AdapterManager.getInstance().getSecuredUDFAdapterManager().deleteWorkflowUdf(sc, anArr2);

        } catch (UserException ue) {
            if (ue.getActionMessages() != null) {
                saveMessages(request, ue.getActionMessages());
                setLocale(request, new Locale("en", "US"));
            } else
                throw ue;
        } finally {
            if (w) lockManager.releaseConnection();
        }
        return actionMapping.findForward("workflowCustomEditPage");
    }

    public ActionForward create(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return actionMapping.findForward("workflowUdfEditPage");
    }

    public ActionForward clone(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            CustomForm form = (CustomForm) actionForm;
            SessionContext sc = GeneralAction.getInstance().imports(request, response);
            if (sc == null) {
                return null;
            }

            String[] workflowUdfId = form.getDelete();
            if (workflowUdfId != null) {
                for (String udfId : workflowUdfId) {
                    AdapterManager.getInstance().getSecuredUDFAdapterManager().cloneWorkflowUdf(sc, udfId);
                }
            }

        } catch (UserException ue) {
            if (ue.getActionMessages() != null) {
                saveMessages(request, ue.getActionMessages());
                setLocale(request, new Locale("en", "US"));
            } else
                throw ue;
        } finally {
            if (w) lockManager.releaseConnection();
        }
        return actionMapping.findForward("workflowCustomEditPage");
    }
}
