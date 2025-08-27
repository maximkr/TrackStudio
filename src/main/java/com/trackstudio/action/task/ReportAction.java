package com.trackstudio.action.task;

import java.util.ArrayList;
import java.util.Collections;
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
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.form.ReportForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.secured.SecuredFilterBean;
import com.trackstudio.secured.SecuredReportBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUDFBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.EggBasket;
import com.trackstudio.tools.Tab;
import com.trackstudio.view.TaskViewHTMLShort;

public class ReportAction extends TSDispatchAction {

    private static Log log = LogFactory.getLog(ReportAction.class);

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            ReportForm bf = (ReportForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().taskHeader(bf, sc, request, true);
            if (!sc.canAction(Action.viewReports, id))
                return null;
            ArrayList<SecuredReportBean> reports = new ArrayList<SecuredReportBean>();

            EggBasket<SecuredTaskBean, SecuredReportBean> childrenReportSet = new EggBasket<SecuredTaskBean, SecuredReportBean>();
            ArrayList<EggBasket> seeAlso = new ArrayList<EggBasket>();
            ArrayList<SecuredTaskBean> parentTasks = AdapterManager.getInstance().getSecuredTaskAdapterManager().getTaskChain(sc, null, id);
            List<SecuredReportBean> ris = AdapterManager.getInstance().getSecuredReportAdapterManager().getAllReportList(sc, id);
            for (SecuredReportBean ri : ris) {
                SecuredTaskBean task = ri.getTask();
                if (task.getId().equals(id)) {
                    reports.add(ri);
                } else if (parentTasks.contains(task)) {
                    reports.add(ri);
                }
                if (!id.equals(task.getId())) {
                    childrenReportSet.putItem(task, ri);
                }
            }

            boolean canView = sc.canAction(Action.viewReports, id);
            boolean canDelete = sc.canAction(Action.managePrivateReports, id);

            sc.setRequestAttribute(request, "canView", canView);
            sc.setRequestAttribute(request, "canCreateReport", sc.canAction(Action.managePrivateReports, id));
            sc.setRequestAttribute(request, "canDelete", canDelete);
            sc.setRequestAttribute(request, "canViewFilters", sc.canAction(Action.viewFilters, id));

            Collections.sort(reports);
            sc.setRequestAttribute(request, "reportList", reports);
            EggBasket<SecuredTaskBean, SecuredReportBean> tempSee = new EggBasket<SecuredTaskBean, SecuredReportBean>();
            if (!childrenReportSet.isEmpty()) {
                for (Map.Entry<SecuredTaskBean, List<SecuredReportBean>> entry : childrenReportSet.entrySet()) {
                    List<SecuredReportBean> temp = new ArrayList<SecuredReportBean>();
                    for (SecuredReportBean rL : entry.getValue()) {
                        if (!reports.contains(rL)) {
                            temp.add(rL);
                        }
                    }
                    if (!temp.isEmpty()) {
                        tempSee.put(entry.getKey(), temp);
                    }
                }
                if (!tempSee.isEmpty()) {
                    seeAlso.add(tempSee);
                }
            }
            sc.setRequestAttribute(request, "seeAlso", seeAlso);
            selectTaskTab(sc, id, "tabReports", request);
            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_TASK_REPORT_LIST);
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_TASK_REPORT_LIST));
            sc.setRequestAttribute(request, "helpTile", "HELP_TILE_ADD_NEW_REPORT");

            return mapping.findForward("reportListJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward delete(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            ReportForm ff = (ReportForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String[] reports = ff.getSelect();
            if (reports != null) {
                for (String report : reports)
                    AdapterManager.getInstance().getSecuredReportAdapterManager().deleteReport(sc, report);
            }
            return mapping.findForward("reportListPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");

            ReportForm bf = (ReportForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().taskHeader(bf, sc, request, false);
            SecuredTaskBean task = new SecuredTaskBean(id, sc);
            if (!sc.canAction(Action.managePrivateReports, id))
                return null;
            bf.setOwner(sc.getUserId());
            bf.setMethod("create");
            bf.setShared(false);
            String rtype = bf.getType();
            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_TASK_REPORT_PROPERTIES);

            ArrayList<SecuredFilterBean> filterSet = new ArrayList<SecuredFilterBean>();
            for (SecuredFilterBean fl : AdapterManager.getInstance().getSecuredFilterAdapterManager().getTaskFilterList(sc, id)) {
                if ((!fl.isPrivate() || sc.getUserId().equals(fl.getOwnerId()))) {
                    filterSet.add(fl);
                }
            }
            Collections.sort(filterSet);
            sc.setRequestAttribute(request, "filters", filterSet);
            sc.setRequestAttribute(request, "tabView", new Tab(false, false));
            sc.setRequestAttribute(request, "tabEdit", new Tab(sc.canAction(Action.managePrivateReports, id), true));
            sc.setRequestAttribute(request, "connected", (new TaskViewHTMLShort(task, request.getContextPath())).getView(task).getName());
            sc.setRequestAttribute(request, "owner", sc.getUser());
            sc.setRequestAttribute(request, "createNewReport", Boolean.TRUE);
            sc.setRequestAttribute(request, "canCreatePublicReport", sc.canAction(Action.managePublicReports, id));
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_TASK_REPORT_PROPERTIES));
            return mapping.findForward("editReportJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward clone(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            ReportForm ff = (ReportForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String[] reports = ff.getSelect();
            if (reports != null) {
                for (String reportId : reports) {
                    SecuredReportBean srb = AdapterManager.getInstance().getSecuredFindAdapterManager().findReportById(sc, reportId);
                    String newReportId = AdapterManager.getInstance().getSecuredReportAdapterManager().createReport(sc, srb.getName() + "_clone", srb.getRtype(), srb.getPriv(), srb.getFilterId(), srb.getTaskId());
                    AdapterManager.getInstance().getSecuredReportAdapterManager().updateReportParams(sc, newReportId, srb.getParams());
                }
            }
            return mapping.findForward("reportListPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }
}
