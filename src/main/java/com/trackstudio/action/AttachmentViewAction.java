package com.trackstudio.action;

import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.exception.UserException;
import com.trackstudio.form.BaseForm;
import com.trackstudio.form.UploadForm;
import com.trackstudio.kernel.cache.TaskRelatedInfo;
import com.trackstudio.kernel.cache.TaskRelatedManager;
import com.trackstudio.kernel.cache.UserRelatedInfo;
import com.trackstudio.kernel.cache.UserRelatedManager;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.secured.SecuredTaskAttachmentBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.startup.Config;

public class AttachmentViewAction extends Action {//не наследует из-за того, что используется redirect TSDispatchAction
    private static Log log = LogFactory.getLog(AttachmentViewAction.class);
    private static final LockManager lockManager = LockManager.getInstance();

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
        log.trace("##########");
        try {
            UploadForm tf = (UploadForm) form;
            if (tf.getMethod() != null) {
                if (tf.getMethod().equals("deleteTaskUpload")) {
                    return deleteTaskUpload(mapping, tf, request, response);

                } else if (tf.getMethod().equals("deleteUserUpload")) {
                    return deleteUserUpload(mapping, tf, request, response);
                } else  if (tf.getMethod().equals("creataArchiveTaskUpload")) {
                    return creataArchiveTaskUpload(mapping, tf, request, response);
                }
            } else {
                throw new UserException("Method not found!");
            }
        } catch (GranException ge) {
            request.setAttribute("javax.servlet.jsp.jspException", ge);
            return mapping.findForward("error");
        }
        return null;
    }

    public ArrayList<SecuredTaskAttachmentBean> getTaskAttachments(SessionContext sc, SecuredTaskBean stb) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            return stb.getAttachments();
        } finally {
            if (w) lockManager.releaseConnection();
        }
    }


    public ActionForward deleteTaskUpload(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                          HttpServletResponse response) throws Exception {
        log.trace("##########");

        SessionContext sc = null;
        BaseForm tf = (BaseForm) form;
        boolean w = lockManager.acquireConnection();
        try {
            sc = GeneralAction.getInstance().imports(request, response);
            if (sc == null)
                return null;
            String[] removed = tf.getDelete();
            if (removed != null)
                for (String aRemoved : removed) {
                    AdapterManager.getInstance().getSecuredAttachmentAdapterManager().deleteAttachment(sc, aRemoved);
                }
        } catch (UserException ue) {
            if (ue.getActionMessages() != null) {
                if (sc != null)
                    sc.setAttribute("uploadError", ue.getActionMessages());
            } else
                throw ue;
        } finally {
            if (w) lockManager.releaseConnection();
        }
        //dnikitin: Если использовать forward, то возникает какой-то непонятный exception java.lang.IllegalStateException: getReader() or getInputStream() called
        //at org.mortbay.jetty.servlet.ServletHttpRequest.setCharacterEncoding(ServletHttpRequest.java:603)

        ActionForward af = new ActionForward(mapping.findForward("taskViewPage").getPath() + "&id=" + ((UploadForm) tf).getTaskId());
        af.setRedirect(true);
        return af;
    }

    public ActionForward deleteUserUpload(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                          HttpServletResponse response) throws Exception {
        log.trace("##########");
        SessionContext sc = null;
        BaseForm tf = (BaseForm) form;
        boolean w = lockManager.acquireConnection();
        try {
            sc = GeneralAction.getInstance().imports(request, response);
            if (sc == null)
                return null;
            String[] removed = tf.getDelete();
            if (removed != null)
                for (String aRemoved : removed) {
                    AdapterManager.getInstance().getSecuredAttachmentAdapterManager().deleteAttachment(sc, aRemoved);
                }
        } catch (UserException ue) {
            if (ue.getActionMessages() != null) {
                if (sc != null)
                    sc.setAttribute("uploadError", ue.getActionMessages());
            } else
                throw ue;
        } finally {
            if (w) lockManager.releaseConnection();
        }
        //dnikitin: Если использовать forward, то возникает какой-то непонятный exception java.lang.IllegalStateException: getReader() or getInputStream() called
        //at org.mortbay.jetty.servlet.ServletHttpRequest.setCharacterEncoding(ServletHttpRequest.java:603)

        ActionForward af = new ActionForward(mapping.findForward("userViewPage").getPath() + "&id=" + ((UploadForm) tf).getUserId());
        af.setRedirect(true);
        return af;
    }

    public ActionForward creataArchiveTaskUpload(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.trace("##########");
        SessionContext sc = null;
        BaseForm tf = (BaseForm) form;
        response.setCharacterEncoding(Config.getEncoding());
        OutputStream out = response.getOutputStream();
        String name = "Files";
        boolean w = lockManager.acquireConnection();
        try {
            sc = GeneralAction.getInstance().imports(request, response);
            if (sc == null) {
                throw new InvalidParameterException("Sc are not set");
            }
            if (request.getParameter("taskId") != null) {
                String taskId = request.getParameter("taskId");
                TaskRelatedInfo task = TaskRelatedManager.getInstance().find(taskId);
                if (task != null) {
                    name += "-task-"+task.getName().replaceAll(" ", "_");
                }
            } else if (request.getParameter("userId") != null) {
                String userId = request.getParameter("userId");
                UserRelatedInfo user = UserRelatedManager.getInstance().find(userId);
                if (user!=null) {
                    name += "-user-"+user.getName().replaceAll(" ", "_");
                }
            }
            String[] download = tf.getDelete();
            if (download != null) {
                DownloadServlet.buildHeaderForBrowser(request, response, name+".zip");
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
        } catch (IOException ignore) {
        } catch (Exception ex) {
            log.error("Error",ex);
            request.setAttribute("javax.servlet.jsp.jspException", ex);
            RequestDispatcher requestDispatcher = request.getRequestDispatcher("/error.jsp");
            requestDispatcher.forward(request, response);
        } finally {
            out.flush();
            out.close();
            if (w) lockManager.releaseConnection();
    }
        return null;
    }
}
