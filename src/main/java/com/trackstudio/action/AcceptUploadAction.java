package com.trackstudio.action;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

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
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.kernel.manager.AttachmentArray;
import com.trackstudio.kernel.manager.SafeString;
import com.trackstudio.secured.SecuredAttachmentBean;

public class AcceptUploadAction extends Action {

    private static Log log = LogFactory.getLog(AcceptUploadAction.class);
    private static final LockManager lockManager = LockManager.getInstance();
    protected static final String END_SIGN = "<(The ee";

    private String ReadString(InputStream in) throws IOException {
        byte[] bt = new byte[1];
        String ret = "";
        for (int i = 0; i < 50; i++) {
            in.read(bt, 0, 1);
            if (bt[0] == 0xd) {
                in.read(bt, 0, 1);
                break;
            }
            ret += new String(bt);
        }
        return ret;
    }

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
        InputStream in = request.getInputStream();
        boolean w = lockManager.acquireConnection();
        try {
//			String contentType = request.getContentType();
//			byte dataBytes[] = null;
//			if (contentType != null && contentType.indexOf("multipart/form-data") != -1){
//				int formDataLength = request.getContentLength();
//				dataBytes = new byte[formDataLength];
//				in = request.getInputStream();
//				int bytesRead ;
//				int totalBytesRead = 0;
//				while (totalBytesRead < formDataLength){
//					bytesRead = in.read(dataBytes, totalBytesRead, formDataLength);
//					totalBytesRead += bytesRead;
//				}
//				System.out.println("Debug. Total Bytes Count [" + totalBytesRead + "]");
//			} else{
//				System.out.println("Request not multipart/form-data.");
//			}
//            if (dataBytes != null)
//			    in = new ByteArrayInputStream(dataBytes);
            log.debug("in");
            // read tasc number
            String number = ReadString(in);
            log.debug("number is " + number);
            boolean user = false;
            if (number.startsWith("user")) {
                number = number.substring(4);
                user = true;
            }
            ArrayList<AttachmentArray> attachments = new ArrayList<AttachmentArray>();
            SessionContext sc = GeneralAction.getInstance().imports(request, response);
            // It's delete getTaskAttachments command
            if (number.compareTo("Delete_atts") == 0) {
                // Read bound
                ReadString(in);
                // Read attachment's ids
                int read = 0;
                byte[] bt = new byte[1];
                String id = "";
                while (read != -1) {
                    read = in.read(bt, 0, 1);
                    if (bt[0] == 0xd) {
                        in.read(bt, 0, 1);
                        break;
                    }
                    if (bt[0] == ';') {
                        AdapterManager.getInstance().getSecuredAttachmentAdapterManager().deleteAttachment(sc, id);
                        id = "";
                    } else
                        id += new String(bt);
                }
                return mapping.findForward("writeOutJSP");
            }
            String bound = readLine(in);
            int startBound = bound.indexOf('*');
            log.debug("bound is " + bound);
            if (startBound != -1) {
                bound = bound.substring(startBound);
                int bt;
                while (true) {
                    String name;
                    String description = "";
                    if (bound == null || bound.length() == 0)
                        break;
                    // Read file info
                    name = readLine(in);

                    log.debug("name is " + name);

                    if (name.compareTo(END_SIGN) == 0)
                        break;
                    int len;
                    try {
                        len = Integer.parseInt(readLine(in));
                    } catch (NumberFormatException ex) {
                        break;
                    }

                    log.debug("len " + len);

                    String d = readLine(in);
                    while (d.compareTo(bound) != 0) {
                        description += d;
                        d = readLine(in);
                    }
                    if (number.equals("Edit_att_descr")) {
                        SecuredAttachmentBean stb = AdapterManager.getInstance().getSecuredFindAdapterManager().findAttachmentById(sc, name);
                        if (stb != null) {
                            AdapterManager.getInstance().getSecuredAttachmentAdapterManager().updateAttachment(sc, stb.getId(), stb.getName(), description);
                        }
                    } else {
                        AttachmentArray atta = new AttachmentArray(SafeString.createSafeString(name), SafeString.createSafeString(description), in, len);
                        atta.setUploadFromApplet(true);
                        attachments.add(atta);
                        log.debug("creating getTaskAttachments...");
                        if (user)
                            AdapterManager.getInstance().getSecuredAttachmentAdapterManager().createAttachment(sc, null, null, number, attachments);
                        else
                            AdapterManager.getInstance().getSecuredAttachmentAdapterManager().createAttachment(sc, number, null, sc.getUserId(), attachments);
                        // Read bound
                        readLine(in);      // empty string
                        readLine(in);
                        attachments.clear();
                    }
                }
                log.debug("done");
                response.getWriter().println("SUCCESSFUL : Files were uploaded");
            }
        } catch (Exception e) {
            response.getWriter().println("ERROR: " + e.getMessage());
            log.error("Error", e);
        } finally {
            in.close();
            if (w) lockManager.releaseConnection();
        }
        return mapping.findForward("writeOutJSP");
    }

    private static String readLine(InputStream in) throws IOException {
        String ret;
        byte[] bt = new byte[1];
        int readed = 1;
        byte[] bytes = new byte[0x1000];
        int pos = 0;
        try {
            while (readed > 0 && pos < bytes.length) {
                readed = in.read(bt, 0, 1);
                if (bt[0] == 0xd) {
                    in.read(bt, 0, 1);
                    break;
                }
                bytes[pos++] = bt[0];
            }
        } catch (IOException ex) {
            log.error("Error",ex);
            return END_SIGN;
        }
        ret = new String(bytes, "UTF-8").substring(0, pos);
        int idx = ret.indexOf('\u0000');
        if (idx > 0)
            ret = ret.substring(0, idx);
        return ret;
    }
}
