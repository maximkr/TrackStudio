package com.trackstudio.action.task;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.trackstudio.action.GeneralAction;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.UserException;
import com.trackstudio.form.PreFilterForm;
import com.trackstudio.secured.SecuredReportBean;

public class ReportFilterParametersAction extends TaskFilterParametersAbstractAction {

    private static Log log = LogFactory.getLog(ReportFilterParametersAction.class);
    
    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            PreFilterForm bf = (PreFilterForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            this.id = GeneralAction.getInstance().taskHeader(bf, sc, request, true);
            String report = bf.getReportId();
            SecuredReportBean n = AdapterManager.getInstance().getSecuredFindAdapterManager().findReportById(sc, report);
            if (n == null) {
                throw new UserException("Sorry. Report not found.");
            }
            this.filterId = n.getFilterId();
            sc.setRequestAttribute(request, "action", "/ReportFilterParametersAction.do");
            sc.setRequestAttribute(request, "saveAsFilter", false);
            this.filterParameter = bf.getReportId() + "_reportfilter_" + this.filterId;
            this.action = "reportFilterParametersPage";
            bf.setReportId(report);
            return super.page(mapping, form, request, response);
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward changeTaskFilter(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            PreFilterForm bf = (PreFilterForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            this.id = GeneralAction.getInstance().taskHeader(bf, sc, request, false);
            this.filterParameter = bf.getReportId() + "_reportfilter_" + bf.getFilter();
            this.forward = "viewReportPage";
            this.action = "reportFilterParametersPage";
            bf.setReportId(bf.getReportId());
            return super.changeTaskFilter(mapping, bf, request, response);
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }
}
