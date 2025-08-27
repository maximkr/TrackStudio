package com.trackstudio.action.task;

import java.net.URLEncoder;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import com.trackstudio.action.GeneralAction;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.filter.TaskFValue;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.form.ReportForm;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.securedkernel.SecuredReportAdapterManager;
import com.trackstudio.startup.Config;
import com.trackstudio.tools.Zip;

public class DownloadReportAction extends DispatchAction {
    private static Log log = LogFactory.getLog(DownloadAction.class);
    private static final LockManager lockManager = LockManager.getInstance();

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
        try {
            return super.execute(mapping, form, request, response);
        } catch (GranException ge) {
            request.setAttribute("javax.servlet.jsp.jspException", ge);
            return mapping.findForward("error");
        }
    }

    public ActionForward report(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            ReportForm tf = (ReportForm) form;
            SessionContext sc = GeneralAction.getInstance().imports(request, response);
            if (sc == null)
                return null;
            boolean vABACL = sc.allowedByACL(tf.getId());
            String fileName = tf.getName();
            String storedFN = (String) sc.getAttribute("repName");
            if (storedFN != null) fileName = storedFN;
            sc.removeAttribute("repName");
            boolean zipped = false;
            if (request.getParameter("zipped") != null) {
                zipped = request.getParameter("zipped").equals("true") || "on".equals(request.getParameter("zipped"));
            }
            String ext = "zip";
            if (zipped) {
                response.setContentType("application/zip");
            } else {
                response.setContentType("text/plain");
                ext = "xml";
                if (tf.getFormat().equalsIgnoreCase(SecuredReportAdapterManager.RT_CSV))
                    ext = "csv";
            }
            if (fileName.length() > 0) {
                if (request.getHeader("user-agent") != null && request.getHeader("user-agent").indexOf("MSIE") > -1 || request.getHeader("user-agent").indexOf("Chrome") != -1) {
                    response.setHeader("Content-disposition", "attachment; filename=" + URLEncoder.encode(fileName.replace(' ', '_') + "." + ext, "UTF-8"));
                } else {
                    response.setHeader("Content-disposition", "attachment; filename*=utf-8" + "''" + URLEncoder.encode(fileName.replace(' ', '_') + "." + ext, "UTF-8"));
                }
            }

            if (vABACL) {
                ext = "xml";
                if (tf.getFormat().equalsIgnoreCase(SecuredReportAdapterManager.RT_CSV))
                    ext = "csv";
                TaskFValue fv = AdapterManager.getInstance().getSecuredReportAdapterManager().getFValue(sc, tf.getReportId());
                byte[] bytes = AdapterManager.getInstance().getSecuredReportAdapterManager().generateReport(sc, tf.getReportId(), tf.getId(),  tf.getFormat(), tf.getDelimiter(), tf.getCharset(), fv, request);
                if (zipped) {
                    bytes = Zip.compress(fileName + "." + ext, new String(bytes, "UTF-8"), Config.getEncoding());
                }
                response.setContentLength(bytes.length);
                ServletOutputStream ouputStream = response.getOutputStream();
                ouputStream.write(bytes, 0, bytes.length);
                ouputStream.flush();
                ouputStream.close();
            }
            return null;
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }
}
