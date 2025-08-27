package com.trackstudio.action;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.kernel.manager.AttachmentArray;
import com.trackstudio.kernel.manager.SafeString;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.startup.Config;

public class DragAndDropServlet extends HttpServlet {
    private static final LockManager lockManager = LockManager.getInstance();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        boolean w = lockManager.acquireConnection();
        try {
            SessionContext sc = GeneralAction.getInstance().imports(request, response);
            String resourceId = request.getParameter("resourceId");
            boolean taskUpload = "true".equals(request.getParameter("taskUpload"));
            String nameFile =  new String(request.getParameter("fileName").getBytes("ISO-8859-1"), Config.getEncoding());
            InputStream inputStream = new BufferedInputStream(request.getInputStream());
            AttachmentArray atta = new AttachmentArray(SafeString.createSafeString(nameFile), SafeString.createSafeString(""), inputStream, inputStream.available());
            if (taskUpload) {
                SecuredTaskBean tci = new SecuredTaskBean(resourceId, sc);
                boolean canEditTask = tci.canManage() && AdapterManager.getInstance().getSecuredTaskAdapterManager().isTaskEditable(sc, resourceId);
                boolean canAddAttachments = canEditTask || sc.canAction(Action.manageTaskAttachments, resourceId);
                if (canAddAttachments) {
                    AdapterManager.getInstance().getSecuredAttachmentAdapterManager().createAttachment(sc, resourceId, null, sc.getUserId(), new ArrayList<AttachmentArray>(Arrays.asList(atta)));
                }
            } else {
                boolean canAddAttachments = sc.canAction(Action.createUserAttachments, resourceId);
                if (canAddAttachments) {
                    AdapterManager.getInstance().getSecuredAttachmentAdapterManager().createAttachment(sc, null, null, resourceId, new ArrayList<AttachmentArray>(Arrays.asList(atta)));
                }
            }
        } catch (GranException gr) {
            throw new ServletException(gr);
        } finally {
            if (w) lockManager.releaseConnection();
        }
    }
}
