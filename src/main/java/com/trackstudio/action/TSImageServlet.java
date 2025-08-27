package com.trackstudio.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.secured.SecuredAttachmentBean;

public class TSImageServlet extends HttpServlet {
    private static final LockManager lockManager = LockManager.getInstance();
    private static Log log = LogFactory.getLog(TSImageServlet.class);
    private Calendar calendar;

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        InputStream sImage = null;
        boolean w = lockManager.acquireConnection();
        try {
            byte[] bytearray = new byte[4096];
            int size = 0;
            GeneralAction.getInstance().imports(request, response);
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            if (sc == null)
                return;
            String attId = request.getParameter("attId");
            SecuredAttachmentBean att = AdapterManager.getInstance().getSecuredFindAdapterManager().findAttachmentById(sc, attId);
            if (att == null)
                return;
            byte[] image = null;
            String widthValue = request.getParameter("width");
            String heightValue = request.getParameter("height");
            if (widthValue != null && widthValue.length() != 0 && heightValue != null && heightValue.length() > 0) {
                try {
                    int width = Integer.parseInt(widthValue);
                    int height = Integer.parseInt(heightValue);
                    image = att.getThumbData(width, height);
                } catch (NumberFormatException nfe) {
                    return;
                }
            }
            if (image == null)
                return;
            sImage = new ByteArrayInputStream(image);
            response.reset();
            response.setHeader("Cache-Control", "public");
            response.setHeader("Pragma", "public");
            response.setContentLength(image.length);
            if (calendar == null) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(System.currentTimeMillis());
                cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + 10);
                calendar = cal;
            }
            response.setDateHeader("Expires", calendar.getTimeInMillis());
            response.setContentType("image/png");
            response.addHeader("Content-Disposition", "filename=getimage.png");
            while ((size = sImage.read(bytearray)) != -1) {
                response.getOutputStream().write(bytearray, 0, size);
            }
            response.flushBuffer();
        } catch (Exception ex) {
            log.error("Error",ex);
        } finally {
            if (w) lockManager.releaseConnection();
            if (sImage != null) {
                try {
                sImage.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("Error", e);
                }
            }
        }
    }

    public void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException {
        doGet(httpServletRequest, httpServletResponse);
    }
}
