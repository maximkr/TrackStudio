package com.trackstudio.action.task;

import java.io.BufferedOutputStream;
import java.net.URLEncoder;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.trackstudio.action.GeneralAction;
import com.trackstudio.action.TSDispatchAction;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.UserException;
import com.trackstudio.form.BaseForm;
import com.trackstudio.form.UploadForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.secured.SecuredTaskAttachmentBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.startup.Config;

public class AttachmentBrowserAction extends TSDispatchAction {

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");

            UploadForm tf = (UploadForm) form;

            final SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().taskHeader(tf, sc, request, true);

            final SecuredTaskBean tci = new SecuredTaskBean(id, sc);
            final List<SecuredTaskAttachmentBean> container = new ArrayList<SecuredTaskAttachmentBean>();

            List<String> tasks = new ArrayList<String>();

            tasks.add(tci.getId());

            for (Map.Entry<String, Boolean> child : tci.getAllowedChildrenWithSubtasksMap().entrySet()) {
                tasks.add(child.getKey());
            }
            for (String chicdId : tasks) {
                SecuredTaskBean task = new SecuredTaskBean(chicdId, sc);
                if (task.canView() && sc.canAction(Action.viewTaskAttachments, task.getId())) {
                    if (task.getAttachments() != null && task.getAttachments().size() != 0) {
                        container.addAll(task.getAttachments());
                    }
                }
            }
            Collections.sort(container);

            sc.setRequestAttribute(request, "attachments", container);
            return mapping.findForward("attachmentBrowserJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward createArchive(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            SessionContext sc = null;
            BaseForm tf = (BaseForm) form;
            response.setCharacterEncoding(Config.getEncoding());
            BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
            try {
                sc = GeneralAction.getInstance().imports(request, response);
                if (sc == null) {
                    throw new InvalidParameterException("Sc are not set");
                }
                String[] download = tf.getDelete();
                if (download != null) {
                    response.setContentType("'application/zip'; charset='UTF-8'");
                    response.setContentType("name=Files");
                    response.setHeader("Content-disposition", "attachment; filename*=utf-8" + "''" + URLEncoder.encode("Files.zip", "UTF-8") + ";");
                    if (request.getHeader("user-agent") != null) {
                        if (request.getHeader("user-agent").indexOf("Safari") != -1) {
                            response.setHeader("Content-Disposition", "attachment");
                            response.setContentType("application/octet-stream");
                        } else if (request.getHeader("user-agent").indexOf("MSIE") != -1 || request.getHeader("user-agent").indexOf("Chrome") != -1) {
                            response.setHeader("Content-disposition", "attachment; filename=" + URLEncoder.encode("Files.zip", "UTF-8"));
                        }
                    }
                    List<String> files = Arrays.asList(download);
                    AdapterManager.getInstance().getSecuredAttachmentAdapterManager().createZip(sc, out, files);
                }
            } catch (UserException ue) {
                if (ue.getActionMessages() != null) {
                    if (sc != null)
                        sc.setAttribute("uploadError", ue.getActionMessages());
                } else {
                    throw ue;
                }
            } catch (Exception ex) {
                log.error("Error",ex);
                request.setAttribute("javax.servlet.jsp.jspException", ex);
                RequestDispatcher requestDispatcher = request.getRequestDispatcher("/error.jsp");
                requestDispatcher.forward(request, response);
            } finally {
                out.flush();
                out.close();
            }
            return null;
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

}
