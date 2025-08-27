package com.trackstudio.action.task;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
import com.trackstudio.app.adapter.store.MailImportTaskOrMessage;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.form.MailImportForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.secured.SecuredCategoryBean;
import com.trackstudio.secured.SecuredMailImportBean;
import com.trackstudio.secured.SecuredMstatusBean;
import com.trackstudio.secured.SecuredWorkflowBean;
import com.trackstudio.startup.I18n;

public class MailImportEditAction extends TSDispatchAction {

    private static Log log = LogFactory.getLog(MailImportEditAction.class);

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            boolean canEdit = true;
            MailImportForm mif = (MailImportForm) form;
            mif.setActive(true);
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().taskHeader(mif, sc, request, true);
            String mailImportId = mif.getMailImportId();
            boolean createNewMailImport = mailImportId == null || mailImportId.length() == 0;

            ArrayList<SecuredCategoryBean> categoryColl = AdapterManager.getInstance().getSecuredCategoryAdapterManager().getCreatableCategoryList(sc, id);
            List<SecuredWorkflowBean> workflowColl = new ArrayList<SecuredWorkflowBean>();
            for (SecuredCategoryBean c : categoryColl) {
                if (!workflowColl.contains(c.getWorkflow())) workflowColl.add(c.getWorkflow());
            }
            List<SecuredMstatusBean> messageTypeColl = new ArrayList<SecuredMstatusBean>();
            if (workflowColl.size() > 0)
                messageTypeColl = AdapterManager.getInstance().getSecuredWorkflowAdapterManager().getMstatusList(sc, workflowColl.get(0).getId());
            String searchIn = "1";
            SecuredMailImportBean record;

            if (createNewMailImport) {
                sc.setRequestAttribute(request, "userName", sc.getUser());
            } else {
                record = AdapterManager.getInstance().getSecuredFindAdapterManager().findMailImportById(sc, mailImportId);
                canEdit = record.canManage();
                searchIn = record.getSearchIn() == null ? "1" : record.getSearchIn().toString();
                if (record.getCategoryId() != null) mif.setCategory(record.getCategoryId());
                mif.setKeywords(record.getKeywords());
                mif.setOrder(record.getOrder() == null ? "0" : record.getOrder().toString());
                mif.setName(record.getName());
                mif.setDomain(record.getDomain());
                mif.setActive(record.isActive());
                mif.setCategory(record.getCategoryId());
                mif.setMessageType(record.getMStatusId());
                mif.setImportUnknown(record.getImportUnknown());
                sc.setRequestAttribute(request, "userName", record.getOwnerId() != null ? record.getOwner() : null);
                sc.setRequestAttribute(request, "mailImport", record);
                sc.setRequestAttribute(request, "importMsgId", record.getMStatusId());
                messageTypeColl = AdapterManager.getInstance().getSecuredWorkflowAdapterManager().getMstatusList(sc, record.getCategory().getWorkflowId());
            }

            mif.setSearchIn(searchIn);
            sc.setRequestAttribute(request, "categoryColl", categoryColl);
            sc.setRequestAttribute(request, "messageTypeColl", messageTypeColl);
            sc.setRequestAttribute(request, "canImport", sc.canAction(Action.manageEmailImportRules, id));
            sc.setRequestAttribute(request, "mailImportId", mailImportId);
            request.setAttribute("alwaysCreateNewTask", I18n.getString(sc.getLocale(), "ALWAYS_CREATE_NEW_TASK"));
            if (canEdit) {
                return mapping.findForward("mailImportEditJSP");
            } else {
                return mapping.findForward("mailImportViewPage");
            }
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward checkRegExp(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            boolean result = MailImportTaskOrMessage.checkRegExp(request.getParameter("expression"), request.getParameter("text").toLowerCase(Locale.ENGLISH));
            PrintWriter writer = response.getWriter();
            writer.append("<span style=\"background:").append(result ? "green" : "red").append("\">").append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;").append("</span>");
            return null;
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            MailImportForm mailImportForm = (MailImportForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = mailImportForm.getId();
            String mailImportId = mailImportForm.getMailImportId();
        int order = 10;
            int searchIn = 1;
            try {
                if (mailImportForm.getOrder() != null && mailImportForm.getOrder().length() != 0)
                    order = Integer.parseInt(mailImportForm.getOrder());
                if (mailImportForm.getSearchIn() != null && mailImportForm.getSearchIn().length() != 0)
                    searchIn = Integer.parseInt(mailImportForm.getSearchIn());
            } catch (NumberFormatException nfe) {
            }
            if (mailImportId == null || mailImportId.length() == 0) {
                mailImportId = AdapterManager.getInstance().getSecuredMailImportAdapterManager().createMailImport(sc, mailImportForm.getName(), id, mailImportForm.getKeywords(),
                        searchIn, order, mailImportForm.getCategory(),
                        mailImportForm.getMessageType(), mailImportForm.getDomain(),
                        mailImportForm.getActive(), mailImportForm.getImportUnknown());
            } else {
                AdapterManager.getInstance().getSecuredMailImportAdapterManager().updateMailImport(sc, mailImportId, mailImportForm.getName(), mailImportForm.getKeywords(),
                        searchIn, order, mailImportForm.getCategory(),
                        mailImportForm.getMessageType(), mailImportForm.getDomain(),
                        mailImportForm.getActive(), mailImportForm.getImportUnknown());
            }
            mailImportForm.setMailImportId(mailImportId);
            mailImportForm.setMutable(false);
            return mapping.findForward("mailImportViewPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }
}
