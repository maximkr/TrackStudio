package com.trackstudio.action;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.exception.UserException;
import com.trackstudio.form.TaskForm;
import com.trackstudio.form.UploadForm;
import com.trackstudio.form.UserForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.manager.AttachmentArray;
import com.trackstudio.kernel.manager.SafeString;
import com.trackstudio.secured.SecuredAttachmentBean;
import com.trackstudio.secured.SecuredTaskBean;

public class AttachmentEditAction extends TSDispatchAction {
    private static Log log = LogFactory.getLog(AttachmentEditAction.class);


    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");

            UploadForm tf = (UploadForm) form;

            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String attachmentId = tf.getAttachmentId();
            String taskId = tf.getTaskId();
            String userId = tf.getUserId();
            if (taskId == null) {
                taskId = request.getAttribute("taskId") != null ? request.getAttribute("taskId").toString() : null;
                if (taskId == null) {
                    taskId = request.getParameter("taskId");
                }
            }

            if (userId == null) {
                userId = request.getAttribute("userId") != null ? request.getAttribute("userId").toString() : null;
                if (userId == null) {
                    userId = request.getParameter("userId");
                }
            }

            if (taskId != null && sc.taskOnSight(taskId)) {
                tf.setId(taskId);
                GeneralAction.getInstance().taskHeader(tf, sc, request, false);
                SecuredAttachmentBean att = AdapterManager.getInstance().getSecuredFindAdapterManager().findAttachmentById(sc, attachmentId);
                if (att != null) {
                    tf.setName(att.getName());
                    tf.setDescription(att.getDescription());
                    tf.setAttachmentId(attachmentId);
                }
                sc.setRequestAttribute(request, "attachment", att);
                SecuredTaskBean tci = new SecuredTaskBean(taskId, sc);
                boolean canEditTask = tci.canManage() && AdapterManager.getInstance().getSecuredTaskAdapterManager().isTaskEditable(sc, taskId);
                sc.setRequestAttribute(request, "canManageTaskAttachments", canEditTask || sc.canAction(Action.manageTaskAttachments, taskId));
                sc.setRequestAttribute(request, "tci", tci);
                return mapping.findForward("taskAttachmentEditJSP");
            } else if (userId != null && sc.userOnSight(userId)) {
                tf.setId(userId);
                GeneralAction.getInstance().userHeader(tf, sc, request);
                SecuredAttachmentBean att = AdapterManager.getInstance().getSecuredFindAdapterManager().findAttachmentById(sc, attachmentId);
                if (att != null) {
                    tf.setName(att.getName());
                    tf.setDescription(att.getDescription());
                    tf.setAttachmentId(attachmentId);
                }
                sc.setRequestAttribute(request, "attachment", att);
                return mapping.findForward("userAttachmentEditJSP");
            } else {
                return null;
            }
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward attachToTask(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().taskHeader(null, sc, request, true);
            sc.setRequestAttribute(request, "hideHeader", true);
            return mapping.findForward("taskAttachmentCreateJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward attachToUser(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().userHeader(null, sc, request);
            sc.setRequestAttribute(request, "hideHeader", true);
            return mapping.findForward("userAttachmentCreateJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward createTaskAttachment(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            UploadForm tf = (UploadForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().taskHeader(tf, sc, request, false);
            ArrayList<AttachmentArray> atts = uploadForm(form);
            AdapterManager.getInstance().getSecuredAttachmentAdapterManager().createAttachment(sc, id, null, sc.getUserId(), atts);
            destroyForm(tf);
            ActionForward af = new ActionForward(mapping.findForward("taskViewPage").getPath() + "&id=" + tf.getId());
            af.setRedirect(true);
            return af;
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward createUserAttachment(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            UploadForm tf = (UploadForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().userHeader(tf, sc, request);
            ArrayList<AttachmentArray> atts = uploadForm(form);
            AdapterManager.getInstance().getSecuredAttachmentAdapterManager().createAttachment(sc, null, null, id, atts);
            destroyForm(tf);
            ActionForward af = new ActionForward(mapping.findForward("userViewPage").getPath() + "&id=" + tf.getId());
            af.setRedirect(true);
            return af;
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public static List<String> uploadTaskForm(TaskForm form, SessionContext sc, String id) throws GranException {
        List<AttachmentArray> atts = uploadForm(form);
        List<String> ret = AdapterManager.getInstance().getSecuredAttachmentAdapterManager().createAttachment(sc, id, null, sc.getUserId(), atts, false);
        destroyForm(form);
        return ret;
    }

    public static void uploadUserForm(UserForm form, SessionContext sc, String id) throws IOException, GranException {
        ArrayList<AttachmentArray> atts = uploadForm(form);
        if (!atts.isEmpty()) {
            AdapterManager.getInstance().getSecuredAttachmentAdapterManager().createAttachment(sc, null, null, id, atts);
            destroyForm(form);
        }
    }

    public static ArrayList<AttachmentArray> uploadForm(ActionForm form) throws GranException {
        try {
            UploadForm tf = (UploadForm) form;

            ArrayList<AttachmentArray> atts = new ArrayList<AttachmentArray>();
            ArrayList<FormFile> ff =  (ArrayList) tf.getFile();
            List<String> desc = Arrays.asList(tf.getFiledesc());
            int j=0;
            for (FormFile f: ff){
                getAttachmentFromForm(f, desc.size()>j ? desc.get(j): "", atts);
                j++;
            }
            return atts;

        } catch (IOException io) {
            throw new GranException(io);
        }
    }

    private static void getAttachmentFromForm(FormFile ff, String description, ArrayList<AttachmentArray> atts) throws UserException, IOException {
        if (ff != null && ff.getFileSize() > 0) {
            String originalName = ff.getFileName();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(ff.getInputStream());
            AttachmentArray arr = new AttachmentArray(SafeString.createSafeString(originalName), SafeString.createSafeString(description), bufferedInputStream);
            atts.add(arr);
        }
    }

    public static void destroyForm(UploadForm tf) {
        if (tf.getFile() != null){
            for(FormFile f:  ((List<FormFile>) tf.getFile())){
                f.destroy();
            }
        }
    }


    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");

            UploadForm tf = (UploadForm) form;

            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String taskId = tf.getTaskId();
            String userId = tf.getUserId();
            String attachmentId = tf.getAttachmentId();
            String attName = tf.getName();
            String attDesc = tf.getDescription();
            AdapterManager.getInstance().getSecuredAttachmentAdapterManager().updateAttachment(sc, attachmentId, attName, attDesc);
            if (taskId != null && sc.taskOnSight(taskId)) {
                tf.setId(taskId);
                GeneralAction.getInstance().taskHeader(tf, sc, request, false);

                tf.setMutable(false);
                return mapping.findForward("viewTaskPage");
            } else {
                tf.setId(userId);
                GeneralAction.getInstance().userHeader(tf, sc, request);
                tf.setMutable(false);
                return mapping.findForward("userViewPage");
            }
        } finally {
            if (w) lockManager.releaseConnection();
        }
    }
}
