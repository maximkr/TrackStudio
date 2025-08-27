package com.trackstudio.action.task;

import java.util.List;

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
import com.trackstudio.form.MessageForm;
import com.trackstudio.secured.SecuredMessageBean;
import com.trackstudio.secured.SecuredTaskBean;

public class MessageDocumentAction  extends TSDispatchAction {
    private static Log log = LogFactory.getLog(MessageDocumentAction.class);

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("####");
            MessageForm tf = (MessageForm) form;
            SessionContext sc = GeneralAction.getInstance().imports(request, response);
            GeneralAction.getInstance().taskHeader(tf, sc, request, true);
            if (sc == null) return null;
            String id = tf.getId();
            if (id == null) {
                id = GeneralAction.getInstance().getId(request, tf);
            }
            SecuredTaskBean tci = new SecuredTaskBean(id, sc);
            sc.setRequestAttribute(request, "tci", tci);
            sc.setRequestAttribute(request, "canView", tci.canManage());
            String messageId = tf.getGo();
            List<SecuredMessageBean> listMes = AdapterManager.getInstance().getSecuredMessageAdapterManager().getMessageList(sc, tci.getId());
            for (int i = 0; i != listMes.size(); ++i) {
                if (messageId == null) {
                    sc.setRequestAttribute(request, "message", listMes.get(i));
                    if (listMes.size() > 1) {
                        sc.setRequestAttribute(request, "nextMessageId", listMes.get(++i).getId());
                    }
                    break;
                } else if (messageId.equals(listMes.get(i).getId())) {
                    if (i > 0) {
                        sc.setRequestAttribute(request, "previosMessageId", listMes.get(i - 1).getId());
                    }
                    if (i + 1 < listMes.size()) {
                        sc.setRequestAttribute(request, "nextMessageId", listMes.get(i + 1).getId());
                    }
                    sc.setRequestAttribute(request, "message", listMes.get(i));
                    break;
                }
            }
            if (messageId != null) {
                return mapping.findForward("messageDocumentJSP");
            } else {
                return mapping.findForward("messageDocumentTileJSP");
            }
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }
}
