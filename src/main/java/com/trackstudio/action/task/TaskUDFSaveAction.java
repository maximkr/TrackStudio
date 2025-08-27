package com.trackstudio.action.task;

import java.util.Locale;

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

public class TaskUDFSaveAction extends UDFSaveAction {
    private static final LockManager lockManager = LockManager.getInstance();

    public ActionForward delete(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        log.trace("##########");
        try {
            CustomForm form = (CustomForm) actionForm;
            SessionContext sc = GeneralAction.getInstance().imports(request, response);
            if (sc == null)
                return null;
            String[] arr2 = form.getDelete();
            if (arr2 != null)
                for (int i = 0; i < arr2.length; i++)
                    AdapterManager.getInstance().getSecuredUDFAdapterManager().deleteTaskUdf(sc, arr2[i]);
        } catch (UserException ue) {
            if (ue.getActionMessages() != null) {
                saveMessages(request, ue.getActionMessages());
                setLocale(request, new Locale("en", "US"));
            } else
                throw ue;
        } finally {
            if (w) lockManager.releaseConnection();
        }
        return actionMapping.findForward("taskCustomEditPage");
    }

    public ActionForward create(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.trace("##########");
        CustomForm af = (CustomForm) actionForm;
        af.setMutable(false);
        return actionMapping.findForward("taskUdfEditPage");
    }

    @Override
    public ActionForward clone(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.trace("##########");
        try {
            CustomForm form = (CustomForm) actionForm;
            SessionContext sc = GeneralAction.getInstance().imports(request, response);
            if (sc == null) {
                return null;
            }
            String[] udfs = form.getDelete();
            if (udfs != null) {
                for (String udfId : udfs) {
                    AdapterManager.getInstance().getSecuredUDFAdapterManager().cloneTaskUdf(sc, udfId);
                }
            }
        } catch (UserException ue) {
            if (ue.getActionMessages() != null) {
                saveMessages(request, ue.getActionMessages());
                setLocale(request, new Locale("en", "US"));
            } else
                throw ue;
        }
        return actionMapping.findForward("taskCustomEditPage");
    }

}
