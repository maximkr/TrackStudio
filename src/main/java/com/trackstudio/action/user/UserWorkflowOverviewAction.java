package com.trackstudio.action.user;

import java.util.ArrayList;
import java.util.List;

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
import com.trackstudio.constants.UdfConstants;
import com.trackstudio.constants.WorkflowConstants;
import com.trackstudio.form.PrstatusForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.secured.SecuredMstatusBean;
import com.trackstudio.secured.SecuredPrstatusBean;
import com.trackstudio.secured.SecuredWorkflowBean;
import com.trackstudio.secured.SecuredWorkflowUDFBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.ExternalAdapterManagerUtil;
import com.trackstudio.tools.Pair;
import com.trackstudio.tools.Tab;
import com.trackstudio.view.UserViewHTMLLinked;

public class UserWorkflowOverviewAction extends TSDispatchAction {
    private static Log log = LogFactory.getLog(UserWorkflowOverviewAction.class);

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            PrstatusForm pf = (PrstatusForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().userHeader(pf, sc, request);
            if (!sc.canAction(Action.manageRoles, id))
                return null;
            String prstatusId = pf.getPrstatusId();
            String workflowId = pf.getWorkflowId();
            boolean canEdit;
            SecuredPrstatusBean prstatus = AdapterManager.getInstance().getSecuredFindAdapterManager().findPrstatusById(sc, prstatusId);
            SecuredWorkflowBean workflow = AdapterManager.getInstance().getSecuredFindAdapterManager().findWorkflowById(sc, workflowId);
            pf.setName(prstatus.getName());
            canEdit = prstatus.canManage() && !sc.getUser().getPrstatus().equals(prstatus);

            // Tab attributes
            sc.setRequestAttribute(request, "owner", new UserViewHTMLLinked(prstatus.getUser(), request.getContextPath()).getPath());
            sc.setRequestAttribute(request, "workflow", workflow);
            sc.setRequestAttribute(request, "workflowId", workflow.getId());
            sc.setRequestAttribute(request, "name", prstatus.getName());
            sc.setRequestAttribute(request, "prstatusId", prstatusId);
            sc.setRequestAttribute(request, "currentPrstatus", prstatus);

            sc.setRequestAttribute(request, "tabMType", new Tab(true, false));
            sc.setRequestAttribute(request, "tabView", new Tab(true, true));
            sc.setRequestAttribute(request, "tabUdfMType", new Tab(true, false));
            sc.setRequestAttribute(request, "canView", canEdit);
            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_USER_WORKFLOW_OVERVIEW);
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_USER_WORKFLOW_OVERVIEW));
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_USER_WORKFLOW_OVERVIEW));
            sc.setRequestAttribute(request, "connected", workflow.getTask());


            ArrayList<SecuredMstatusBean> mstatusList = AdapterManager.getInstance().getSecuredWorkflowAdapterManager().getMstatusList(sc, workflow.getId());
            ArrayList<SecuredMstatusBean> processAll = new ArrayList<SecuredMstatusBean>();

            ArrayList<SecuredMstatusBean> processSubmitter = new ArrayList<SecuredMstatusBean>();
            ArrayList<SecuredMstatusBean> processHandler = new ArrayList<SecuredMstatusBean>();
            ArrayList<SecuredMstatusBean> processSAH = new ArrayList<SecuredMstatusBean>();

            ArrayList<SecuredMstatusBean> viewAll = new ArrayList<SecuredMstatusBean>();

            ArrayList<SecuredMstatusBean> viewSubmitter = new ArrayList<SecuredMstatusBean>();
            ArrayList<SecuredMstatusBean> viewHandler = new ArrayList<SecuredMstatusBean>();
            ArrayList<SecuredMstatusBean> viewSAH = new ArrayList<SecuredMstatusBean>();

            ArrayList<SecuredMstatusBean> beHandlerAll = new ArrayList<SecuredMstatusBean>();

            ArrayList<SecuredMstatusBean> beHandlerSubmitter = new ArrayList<SecuredMstatusBean>();
            ArrayList<SecuredMstatusBean> beHandlerHandler = new ArrayList<SecuredMstatusBean>();
            ArrayList<SecuredMstatusBean> beHandlerSAH = new ArrayList<SecuredMstatusBean>();

            for (SecuredMstatusBean mstatus : mstatusList) {
                List<String> types = ExternalAdapterManagerUtil.getMprstatusMap(mstatus.getId()).get(prstatusId);
                if (types != null) {
                    if (types.contains(WorkflowConstants.VIEW_ALL))
                        viewAll.add(mstatus);
                    else if (types.contains(WorkflowConstants.VIEW_SUBMITTER))
                        viewSubmitter.add(mstatus);
                    else if (types.contains(WorkflowConstants.VIEW_HANDLER))
                        viewHandler.add(mstatus);
                    else if (types.contains(WorkflowConstants.VIEW_SUBMITTER_AND_HANDLER))
                        viewSAH.add(mstatus);

                    if (types.contains(WorkflowConstants.PROCESS_ALL))
                        processAll.add(mstatus);
                    else if (types.contains(WorkflowConstants.PROCESS_SUBMITTER))
                        processSubmitter.add(mstatus);
                    else if (types.contains(WorkflowConstants.PROCESS_HANDLER))
                        processHandler.add(mstatus);
                    else if (types.contains(WorkflowConstants.PROCESS_SUBMITTER_AND_HANDLER))
                        processSAH.add(mstatus);

                    if (types.contains(WorkflowConstants.BE_HANDLER_ALL))
                        beHandlerAll.add(mstatus);
                    else if (types.contains(WorkflowConstants.BE_HANDLER_SUBMITTER))
                        beHandlerSubmitter.add(mstatus);
                    else if (types.contains(WorkflowConstants.BE_HANDLER_HANDLER))
                        beHandlerHandler.add(mstatus);
                    else if (types.contains(WorkflowConstants.BE_HANDLER_SUBMITTER_AND_HANDLER))
                        beHandlerSAH.add(mstatus);
                }
            }

            List<SecuredWorkflowUDFBean> udfs = workflow.getWorkflowUDFs();

            List<Pair> viewUdfs = new ArrayList<Pair>();
            List<Pair> editUdfs = new ArrayList<Pair>();

            for (SecuredWorkflowUDFBean udf : udfs) {
                List<String> rules = AdapterManager.getInstance().getSecuredUDFAdapterManager().getUDFRuleList(sc, prstatusId, udf.getId());
                if (rules.contains(UdfConstants.VIEW_ALL) || rules.contains(UdfConstants.STATUS_VIEW_ALL)) {
                    viewUdfs.add(new Pair(udf.getId(), udf.getCaption()));
                } else if (rules.contains(UdfConstants.VIEW_HANDLER)) {
                    viewUdfs.add(new Pair(udf.getId(), udf.getCaption() + " (* " + I18n.getString(sc, "HANDLER") + ")"));
                } else if (rules.contains(UdfConstants.VIEW_SUBMITTER)) {
                    viewUdfs.add(new Pair(udf.getId(), udf.getCaption() + " (* " + I18n.getString(sc, "SUBMITTER") + ")"));
                } else if (rules.contains(UdfConstants.VIEW_SUBMITTER_AND_HANDLER)) {
                    viewUdfs.add(new Pair(udf.getId(), udf.getCaption() + " (* " + I18n.getString(sc, "HANDLER") + ", " + I18n.getString(sc, "SUBMITTER") + ")"));
                }

                if (rules.contains(UdfConstants.EDIT_ALL) || rules.contains(UdfConstants.STATUS_EDIT_ALL)) {
                    editUdfs.add(new Pair(udf.getId(), udf.getCaption()));
                } else if (rules.contains(UdfConstants.EDIT_HANDLER)) {
                    editUdfs.add(new Pair(udf.getId(), udf.getCaption() + " (* " + I18n.getString(sc, "HANDLER") + ")"));
                } else if (rules.contains(UdfConstants.EDIT_SUBMITTER)) {
                    editUdfs.add(new Pair(udf.getId(), udf.getCaption() + " (* " + I18n.getString(sc, "SUBMITTER") + ")"));
                } else if (rules.contains(UdfConstants.EDIT_SUBMITTER_AND_HANDLER)) {
                    editUdfs.add(new Pair(udf.getId(), udf.getCaption() + " (* " + I18n.getString(sc, "HANDLER") + ", " + I18n.getString(sc, "SUBMITTER") + ")"));
                }
            }
            sc.setRequestAttribute(request, "viewUdfs", viewUdfs);
            sc.setRequestAttribute(request, "editUdfs", editUdfs);

            sc.setRequestAttribute(request, "ruleProcessAll", processAll);

            sc.setRequestAttribute(request, "ruleProcessSubmitter", processSubmitter);
            sc.setRequestAttribute(request, "ruleProcessHandler", processHandler);
            sc.setRequestAttribute(request, "ruleProcessSAH", processSAH);
            sc.setRequestAttribute(request, "ruleViewAll", viewAll);

            sc.setRequestAttribute(request, "ruleViewSubmitter", viewSubmitter);
            sc.setRequestAttribute(request, "ruleViewHandler", viewHandler);
            sc.setRequestAttribute(request, "ruleViewSAH", viewSAH);
            sc.setRequestAttribute(request, "ruleBeHandlerAll", beHandlerAll);

            sc.setRequestAttribute(request, "ruleBeHandlerSubmitter", beHandlerSubmitter);
            sc.setRequestAttribute(request, "ruleBeHandlerHandler", beHandlerHandler);
            sc.setRequestAttribute(request, "ruleBeHandlerSAH", beHandlerSAH);
            return mapping.findForward("userWorkflowOverviewJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }


}