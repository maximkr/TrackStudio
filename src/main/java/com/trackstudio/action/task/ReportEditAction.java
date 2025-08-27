package com.trackstudio.action.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.trackstudio.action.GeneralAction;
import com.trackstudio.action.TSDispatchAction;
import com.trackstudio.app.PageNaming;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.report.birt.Report;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.form.ReportForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.secured.SecuredFilterBean;
import com.trackstudio.secured.SecuredReportBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUDFBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.AllContentType;
import com.trackstudio.tools.Tab;
import com.trackstudio.view.TaskViewHTMLShort;

public class ReportEditAction extends TSDispatchAction {
    private static Log log = LogFactory.getLog(ReportEditAction.class);

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            ReportForm bf = (ReportForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");

            String id = GeneralAction.getInstance().taskHeader(bf, sc, request, true);
            String reportId = bf.getReportId() == null ? request.getParameter("reportId") : bf.getReportId();
            String type;
            bf.setId(id);
            sc.setRequestAttribute(request, "allContentType", AllContentType.getAllContentType());
            boolean priv = true;
            if (reportId == null || reportId.length() == 0) {
                sc.setRequestAttribute(request, "tabView", new Tab(false, false));
                sc.setRequestAttribute(request, "tabEdit", new Tab(sc.canAction(Action.managePrivateReports, id), true));
                bf.setType(bf.getType());
                type = bf.getType();
                bf.setOwner(sc.getUserId());
                bf.setFilter(AdapterManager.getInstance().getSecuredFilterAdapterManager().getCurrentTaskFilterId(sc, id));
                bf.setShared(false);
                sc.setRequestAttribute(request, "owner", sc.getUser());
            } else {
                SecuredReportBean currentReport = AdapterManager.getInstance().getSecuredFindAdapterManager().findReportById(sc, reportId);
                if (!currentReport.canManage())
                    return null;

                bf.setName(currentReport.getName());

                bf.setType(currentReport.getRtype());
                type = currentReport.getRtype();
                bf.setShared(!currentReport.isPriv());
                sc.setRequestAttribute(request, "canEditPrivate", currentReport.getOwnerId().equals(sc.getUserId()));

                bf.setOwner(currentReport.getOwnerId());
                bf.setFilter(currentReport.getFilterId());
                HashMap<String, String> param = currentReport.getParamsHashMap();
                bf.setParams(param);
                sc.setRequestAttribute(request, "owner", currentReport.getOwner());
                sc.setRequestAttribute(request, "currentFilter", currentReport.getFilter());
                sc.setRequestAttribute(request, "currentReport", currentReport);
                sc.setRequestAttribute(request, "reportId", reportId);
                sc.setRequestAttribute(request, "tabView", new Tab(sc.canAction(Action.viewReports, currentReport.getTask().getId()), false));
                sc.setRequestAttribute(request, "tabEdit", new Tab(sc.canAction(Action.managePrivateReports, currentReport.getTask().getId()) && currentReport.canManage(), true));
                priv = sc.getUserId().equals(currentReport.getOwnerId());
            }
            ArrayList<SecuredFilterBean> filterSet = AdapterManager.getInstance().getSecuredFilterAdapterManager().getTaskFilterList(sc, id);
            Collections.sort(filterSet);
            sc.setRequestAttribute(request, "filters", filterSet);
            sc.setRequestAttribute(request, "reportType", Report.getReportTypeFactory(sc, type));
            ArrayList<SecuredUDFBean> udfHash = new SecuredTaskBean(id, sc).getFilterUDFs();
            sc.setRequestAttribute(request, "connected", (new TaskViewHTMLShort(new SecuredTaskBean(id, sc), request.getContextPath())).getView(new SecuredTaskBean(id, sc)).getName());
            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_TASK_REPORT_PROPERTIES);
            sc.setRequestAttribute(request, "canCreatePublicReport", sc.canAction(Action.managePublicReports, id) && priv);
            sc.setRequestAttribute(request, "type", type);

            bf.setMethod("edit");
            sc.setRequestAttribute(request, "canCreateReport", sc.canAction(Action.managePrivateReports, id));
            selectTaskTab(sc, id, "tabReports", request);
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_TASK_REPORT_PROPERTIES));
            return mapping.findForward("editReportJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            ReportForm ff = (ReportForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = ff.getId();
            if (!sc.canAction(Action.managePrivateReports, id))
                return null;
            boolean priv = !ff.isShared();
            if (!sc.canAction(Action.managePublicReports, id))
                priv = true;
            String filterId = ff.getFilter();
            SecuredFilterBean filter = AdapterManager.getInstance().getSecuredFindAdapterManager().findFilterById(sc, filterId);
            if (filter.isPrivate()) priv = true;
            String reportId = AdapterManager.getInstance().getSecuredReportAdapterManager().createReport(sc, ff.getName(), ff.getType(), priv, filterId, id);
            AdapterManager.getInstance().getSecuredReportAdapterManager().updateReportParams(sc, reportId, getReportParam(ff.getParams()));

            ff.setReportId(reportId);

            ff.setMutable(false);
            return mapping.findForward("viewReportPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward edit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            ReportForm ff = (ReportForm) form;
            String id = ff.getId();
            boolean priv = !ff.isShared();
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String reportId = ff.getReportId();
            String filterId = ff.getFilter() != null ? ff.getFilter() : "1";
            SecuredFilterBean filter = AdapterManager.getInstance().getSecuredFindAdapterManager().findFilterById(sc, filterId);
            if (filter.isPrivate()) priv = true;
            if (reportId == null || reportId.length() == 0) {
                if (!sc.canAction(Action.managePrivateReports, id))
                    return null;
                if (!sc.canAction(Action.managePublicReports, id))
                    priv = true;
                reportId = AdapterManager.getInstance().getSecuredReportAdapterManager().createReport(sc, ff.getName(), ff.getType(), priv, filterId, id);
                ff.setReportId(reportId);
                ff.setMutable(false);
            } else {
                AdapterManager.getInstance().getSecuredReportAdapterManager().updateReport(sc, reportId, ff.getName(), priv, ff.getType(), filterId);
            }
            Map params = ff.getParams();
            AdapterManager.getInstance().getSecuredReportAdapterManager().updateReportParams(sc, reportId, getReportParam(params));
            ActionForward af = new ActionForward(mapping.findForward("viewReportPage").getPath() + "&reportId=" + reportId + "&id=" + id);
            af.setRedirect(true);
            return af;
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    private String getReportParam(Map<String, String> map) {
        if (map == null || map.isEmpty() || map.size() == 0)
            return "";
        String ret = "";
        for (Map.Entry e : map.entrySet()) {
            ret += ";" + e.getKey() + "=" + ((String[]) e.getValue())[0];
        }
        ret = ret.substring(1);

        return ret;
    }

}