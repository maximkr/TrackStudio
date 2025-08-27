package com.trackstudio.action.user;

import java.util.Iterator;
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

public class UserUDFSaveAction extends UDFSaveAction {

    public ActionForward delete(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.trace("##########");
        try {
            CustomForm form = (CustomForm) actionForm;
            SessionContext sc = GeneralAction.getInstance().imports(request, response);
            if (sc == null)
                return null;
            Map m = form.dellists;
            for (Iterator it = m.keySet().iterator(); it.hasNext();) {
                String key = it.next().toString();
                AdapterManager.getInstance().getSecuredUDFAdapterManager().deleteUserUdflist(sc, form.getIdUdf(), key);
            }
            String[] arr2 = form.getDelete();
            if (arr2 != null)
                for (int i = 0; i < arr2.length; i++)
                    AdapterManager.getInstance().getSecuredUDFAdapterManager().deleteUserUdf(sc, arr2[i]);
        } catch (UserException ue) {
            if (ue.getActionMessages() != null) {
                saveMessages(request, ue.getActionMessages());
                setLocale(request, new Locale("en", "US"));
            } else
                throw ue;
        }
        return actionMapping.findForward("userCustomEditPage");
    }

    public ActionForward create(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.trace("##########");
        return actionMapping.findForward("userUdfEditPage");
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
                    AdapterManager.getInstance().getSecuredUDFAdapterManager().cloneUserUdf(sc, udfId);
                }
            }
        } catch (UserException ue) {
            if (ue.getActionMessages() != null) {
                saveMessages(request, ue.getActionMessages());
                setLocale(request, new Locale("en", "US"));
            } else
                throw ue;
        }
        return actionMapping.findForward("userCustomEditPage");
    }

}
