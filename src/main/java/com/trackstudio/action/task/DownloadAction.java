package com.trackstudio.action.task;

import java.util.Locale;

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
import com.trackstudio.app.report.handmade.HandMadeReportManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.form.ExportForm;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.tools.Zip;

public class DownloadAction extends DispatchAction {
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

    public ActionForward export(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            ExportForm bf = (ExportForm) form;
            SessionContext sc = GeneralAction.getInstance().imports(request, response);
            if (sc == null)
                return null;
            boolean vABACL = sc.allowedByACL(bf.getId());
            if (vABACL) {
                TaskFValue fval = AdapterManager.getInstance().getSecuredFilterAdapterManager().getTaskFValue(sc, bf.getFilter()).getFValue();
                String export = new HandMadeReportManager().generate(sc, bf.getId(), bf.getFilter(), fval, bf.getExportFormat(), null, bf.getCharset());
                    response.setContentType("application/zip; name=\"generate.zip\"");
                    response.setHeader("Content-disposition", "filename=\"generate.zip\"");
                    String ext = bf.getExportFormat().toLowerCase(Locale.ENGLISH);

                    if (bf.getExportFormat().equals("TreeXML"))
                        ext = bf.getExportFormat().toLowerCase(Locale.ENGLISH).substring(4);
                    byte[] bytes;
                    bytes = Zip.compress("generate." + ext, export, bf.getCharset());
                    response.setContentLength(bytes.length);
                    ServletOutputStream ouputStream = response.getOutputStream();
                    ouputStream.write(bytes, 0, bytes.length);
                    ouputStream.flush();
                    ouputStream.close();
            }
            sc.setRequestAttribute(request, "startTime", null);
            return null;
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

}
