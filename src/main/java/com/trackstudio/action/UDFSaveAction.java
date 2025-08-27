package com.trackstudio.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.ForwardAction;

import com.trackstudio.exception.GranException;
import com.trackstudio.form.CustomForm;
import com.trackstudio.kernel.lock.LockManager;

public abstract class UDFSaveAction extends ForwardAction {
    private static final LockManager lockManager = LockManager.getInstance();
    protected static Log log = LogFactory.getLog(UDFSaveAction.class);
    private static final String NBSP = "&nbsp;";

    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            CustomForm form = (CustomForm) actionForm;
            if (form.deletePressed())
                return delete(actionMapping, actionForm, request, response);
            else if (form.createPressed())
                return create(actionMapping, actionForm, request, response);
            else if (form.clonePressed())
                return  clone(actionMapping, actionForm, request, response);
            else
                return actionMapping.getInputForward();
        } catch (GranException ge) {
            request.setAttribute("javax.servlet.jsp.jspException", ge);
            return actionMapping.findForward("error");
        } finally {
            if (w) lockManager.releaseConnection();
        }
    }

    public abstract ActionForward delete(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse response) throws Exception;

    public abstract ActionForward create(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse response) throws Exception;

    public abstract ActionForward clone(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse response) throws Exception;
}
