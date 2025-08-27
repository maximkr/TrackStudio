package com.trackstudio.action.task;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.trackstudio.action.GeneralAction;
import com.trackstudio.action.TSDispatchAction;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.form.MailImportForm;
import com.trackstudio.secured.SecuredMailImportBean;

public class MailImportViewAction extends TSDispatchAction {

    private static Log log = LogFactory.getLog(MailImportViewAction.class);

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            MailImportForm tf = (MailImportForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String mailImportId = tf.getMailImportId();
            String id = GeneralAction.getInstance().taskHeader(tf, sc, request, true);
            SecuredMailImportBean mailImport = AdapterManager.getInstance().getSecuredFindAdapterManager().findMailImportById(sc, mailImportId);
            sc.setRequestAttribute(request, "mailImport", mailImport);
            return mapping.findForward("mailImportViewJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

}
