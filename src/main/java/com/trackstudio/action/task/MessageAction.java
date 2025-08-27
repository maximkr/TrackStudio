package com.trackstudio.action.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.trackstudio.action.GeneralAction;
import com.trackstudio.action.TSDispatchAction;
import com.trackstudio.app.PageNaming;
import com.trackstudio.app.Preferences;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.csv.CSVImport;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.form.MessageForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.cache.AttachmentCacheItem;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.secured.SecuredMessageBean;
import com.trackstudio.secured.SecuredTaskBean;


public class MessageAction extends TSDispatchAction {
    private static Log log = LogFactory.getLog(MessageAction.class);


    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            MessageForm tf = (MessageForm) form;
            SessionContext sc = GeneralAction.getInstance().imports(request, response);

            if (sc == null) return null;

            String id = tf.getReturnToTask();
            if (id == null) {
                id = GeneralAction.getInstance().getId(request, tf);
            }
            SecuredTaskBean tci = new SecuredTaskBean(id, sc);
            ArrayList<SecuredMessageBean> listMes = AdapterManager.getInstance().getSecuredMessageAdapterManager().getMessageList(sc, tci.getId());
            boolean value = Preferences.isAscMessageSortOrder(sc.getUser().getPreferences());
            sc.setRequestAttribute(request, "sortMessageAsc", value);
            if (value) {
                Collections.reverse(listMes);
            }
            boolean showAudit = Preferences.isViewAutidTrail(sc.getUser().getPreferences());
            boolean canAudit = false;
            List<SecuredMessageBean> messages = new ArrayList<SecuredMessageBean>();
            List<String> tasksId = new ArrayList<String>();
            for (SecuredMessageBean message : listMes) {
                if (CSVImport.LOG_MESSAGE.equals(message.getMstatus().getName())) {
                    if (showAudit) {
                        messages.add(message);
                    }
                    canAudit = true;
                } else {
                    messages.add(message);
                    tasksId.add(message.getTaskId());
                }
            }
            if (sc.canAction(Action.viewTaskAttachments, tci.getId())) {
                Map<String, List<AttachmentCacheItem>> attachmentMaps = AdapterManager.getInstance().getSecuredMessageAdapterManager().getCollectAttachmentsForMessage(sc, tci.getId(), tasksId);
                sc.setRequestAttribute(request, "attachmentsMsg", attachmentMaps);
            }
            sc.setRequestAttribute(request, "canAudit", canAudit);
            sc.setRequestAttribute(request, "listMessages", messages);
            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_TASK_OVERVIEW);
            sc.setRequestAttribute(request, "canDeleteMessages", sc.allowedByACL(tci.getId()) && sc.canAction(Action.deleteOperations, tci.getId()));
            sc.setRequestAttribute(request, "canEditTaskActualBudget", sc.canAction(Action.editTaskActualBudget, id));
            sc.setRequestAttribute(request, "viewMessageCheckbox", true);

            return mapping.findForward("messageJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward delete(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            MessageForm ff = (MessageForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String[] val = ff.getDeleteMessage();
            if (val != null)
                for (String aVal : val)
                    AdapterManager.getInstance().getSecuredMessageAdapterManager().deleteMessage(sc, aVal);
            return mapping.findForward("messagePage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward changesort(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            SessionContext sc = (SessionContext) request.getAttribute("sc");

            Preferences p = new Preferences(sc.getUser().getPreferences());
            p.setAscMessageSortOrder(!Preferences.isAscMessageSortOrder(sc.getUser().getPreferences()));
            KernelManager.getUser().setPreferences(sc.getUserId(), p.getPreferences());
            return mapping.findForward("messagePage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward auditTrail(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            Preferences p = new Preferences(sc.getUser().getPreferences());
            p.setViewAutidTrail(!Preferences.isViewAutidTrail(sc.getUser().getPreferences()));
            KernelManager.getUser().setPreferences(sc.getUserId(), p.getPreferences());
            return mapping.findForward("messagePage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }
}
