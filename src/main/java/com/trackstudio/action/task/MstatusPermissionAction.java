package com.trackstudio.action.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

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
import com.trackstudio.app.filter.FValue;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.constants.WorkflowConstants;
import com.trackstudio.exception.GranException;
import com.trackstudio.form.MstatusPermissionForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.secured.SecuredMstatusBean;
import com.trackstudio.secured.SecuredPrstatusBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredWorkflowBean;
import com.trackstudio.secured.SecuredWorkflowUDFBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.EggBasket;
import com.trackstudio.tools.ExternalAdapterManagerUtil;
import com.trackstudio.tools.Tab;

public class MstatusPermissionAction extends TSDispatchAction {

    private static Log log = LogFactory.getLog(MstatusPermissionAction.class);

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            MstatusPermissionForm mf = (MstatusPermissionForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().taskHeader(mf, sc, request, true);
            boolean canManageRoles = sc.canAction(Action.manageRoles, sc.getUserId());
            if (!sc.canAction(Action.manageWorkflows, id) && !canManageRoles)
                return null;

            SecuredTaskBean tci = new SecuredTaskBean(id, sc);
            String workflowId = mf.getWorkflowId() != null ? mf.getWorkflowId() : tci.getWorkflowId();
            SecuredWorkflowBean flow = AdapterManager.getInstance().getSecuredFindAdapterManager().findWorkflowById(sc, workflowId);

            Map<String, TreeSet<SecuredMstatusBean>> canViewCanEditMap = new HashMap<String, TreeSet<SecuredMstatusBean>>();
            ArrayList<SecuredMstatusBean> mstatusList = AdapterManager.getInstance().getSecuredWorkflowAdapterManager().getMstatusList(sc, flow.getId());
            for (SecuredMstatusBean smsb : mstatusList) {
                EggBasket<String, String> rules = ExternalAdapterManagerUtil.getMprstatusMap(smsb.getId());
                Set<String> prstatusList = rules.keySet();
                for (String prstatus : prstatusList) {
                    List<String> types = rules.get(prstatus);
                    if (types != null) {
                        boolean viewAll = false;
                        boolean viewS = false;
                        boolean viewH = false;
                        boolean viewSAH = false;
                        if (types.contains(WorkflowConstants.VIEW_ALL)) {
                            viewAll = true;
                        } else if (types.contains(WorkflowConstants.VIEW_SUBMITTER)) {
                            viewS = true;
                        } else if (types.contains(WorkflowConstants.VIEW_HANDLER)) {
                            viewH = true;
                        } else if (types.contains(WorkflowConstants.VIEW_SUBMITTER_AND_HANDLER)) {
                            viewSAH = true;
                        }

                        if ((types.contains(WorkflowConstants.PROCESS_ALL) && !viewAll) ||
                                (types.contains(WorkflowConstants.PROCESS_SUBMITTER) && !viewAll && !viewS && !viewSAH) ||
                                (types.contains(WorkflowConstants.PROCESS_HANDLER) && !viewAll && !viewH && !viewSAH) ||
                                (types.contains(WorkflowConstants.PROCESS_SUBMITTER_AND_HANDLER) && !viewAll && !viewSAH)) {

                            TreeSet<SecuredMstatusBean> statuses = canViewCanEditMap.get(prstatus);
                            if (statuses == null) {
                                statuses = new TreeSet<SecuredMstatusBean>();
                            }
                            statuses.add(smsb);
                            canViewCanEditMap.put(prstatus, statuses);
                        }
                    }
                }
            }
            boolean isValidPermissions = canViewCanEditMap.isEmpty();
            sc.setRequestAttribute(request, "isValidPermissions", isValidPermissions);
            if (!isValidPermissions) {
                ArrayList<SecuredPrstatusBean> canViewCanEditPrstatusList = new ArrayList<SecuredPrstatusBean>();
                for (String prstatusId : canViewCanEditMap.keySet()) {
                    canViewCanEditPrstatusList.add(AdapterManager.getInstance().getSecuredFindAdapterManager().findPrstatusById(sc, prstatusId));
                }
                sc.setRequestAttribute(request, "canViewCanEditMap", canViewCanEditMap);
                sc.setRequestAttribute(request, "canViewCanEditPrstatusList", canViewCanEditPrstatusList);
            }

            Set<SecuredPrstatusBean> prstatusSet = new TreeSet(AdapterManager.getInstance().getSecuredPrstatusAdapterManager().getAvailablePrstatusList(sc, sc.getUserId()));
            String mstatusId = mf.getMstatusId();
            SecuredMstatusBean mstatus = AdapterManager.getInstance().getSecuredFindAdapterManager().findMstatusById(sc, mstatusId);

            ArrayList<SecuredPrstatusBean> processAll = new ArrayList<SecuredPrstatusBean>();
            ArrayList<SecuredPrstatusBean> processNone = new ArrayList<SecuredPrstatusBean>();
            ArrayList<SecuredPrstatusBean> processSubmitter = new ArrayList<SecuredPrstatusBean>();
            ArrayList<SecuredPrstatusBean> processHandler = new ArrayList<SecuredPrstatusBean>();
            ArrayList<SecuredPrstatusBean> processSAH = new ArrayList<SecuredPrstatusBean>();

            ArrayList<SecuredPrstatusBean> viewAll = new ArrayList<SecuredPrstatusBean>();
            ArrayList<SecuredPrstatusBean> viewNone = new ArrayList<SecuredPrstatusBean>();
            ArrayList<SecuredPrstatusBean> viewSubmitter = new ArrayList<SecuredPrstatusBean>();
            ArrayList<SecuredPrstatusBean> viewHandler = new ArrayList<SecuredPrstatusBean>();
            ArrayList<SecuredPrstatusBean> viewSAH = new ArrayList<SecuredPrstatusBean>();

            ArrayList<SecuredPrstatusBean> beHandlerAll = new ArrayList<SecuredPrstatusBean>();
            ArrayList<SecuredPrstatusBean> beHandlerNone = new ArrayList<SecuredPrstatusBean>();
            ArrayList<SecuredPrstatusBean> beHandlerSubmitter = new ArrayList<SecuredPrstatusBean>();
            ArrayList<SecuredPrstatusBean> beHandlerHandler = new ArrayList<SecuredPrstatusBean>();
            ArrayList<SecuredPrstatusBean> beHandlerSAH = new ArrayList<SecuredPrstatusBean>();

            EggBasket<String, String> rules = ExternalAdapterManagerUtil.getMprstatusMap(mstatusId);
            for (SecuredPrstatusBean prstatus : prstatusSet) {
                if (flow.canManage() && prstatus.canView() || !flow.canManage() && prstatus.isAllowedByACL()) {
                    List<String> types = rules.get(prstatus.getId());
                    if (types != null) {
                        if (types.contains(WorkflowConstants.VIEW_ALL))
                            viewAll.add(prstatus);
                        else if (types.contains(WorkflowConstants.VIEW_SUBMITTER))
                            viewSubmitter.add(prstatus);
                        else if (types.contains(WorkflowConstants.VIEW_HANDLER))
                            viewHandler.add(prstatus);
                        else if (types.contains(WorkflowConstants.VIEW_SUBMITTER_AND_HANDLER))
                            viewSAH.add(prstatus);
                        else
                            viewNone.add(prstatus);

                        if (types.contains(WorkflowConstants.PROCESS_ALL))
                            processAll.add(prstatus);
                        else if (types.contains(WorkflowConstants.PROCESS_SUBMITTER))
                            processSubmitter.add(prstatus);
                        else if (types.contains(WorkflowConstants.PROCESS_HANDLER))
                            processHandler.add(prstatus);
                        else if (types.contains(WorkflowConstants.PROCESS_SUBMITTER_AND_HANDLER))
                            processSAH.add(prstatus);
                        else
                            processNone.add(prstatus);

                        if (types.contains(WorkflowConstants.BE_HANDLER_ALL))
                            beHandlerAll.add(prstatus);
                        else if (types.contains(WorkflowConstants.BE_HANDLER_SUBMITTER))
                            beHandlerSubmitter.add(prstatus);
                        else if (types.contains(WorkflowConstants.BE_HANDLER_HANDLER))
                            beHandlerHandler.add(prstatus);
                        else if (types.contains(WorkflowConstants.BE_HANDLER_SUBMITTER_AND_HANDLER))
                            beHandlerSAH.add(prstatus);
                        else
                            beHandlerNone.add(prstatus);
                    } else {
                        viewNone.add(prstatus);
                        processNone.add(prstatus);
                        beHandlerNone.add(prstatus);
                    }
                }
            }
            sc.setRequestAttribute(request, "ruleProcessAll", processAll);
            sc.setRequestAttribute(request, "ruleProcessNone", processNone);
            sc.setRequestAttribute(request, "ruleProcessSubmitter", processSubmitter);
            sc.setRequestAttribute(request, "ruleProcessHandler", processHandler);
            sc.setRequestAttribute(request, "ruleProcessSAH", processSAH);
            sc.setRequestAttribute(request, "ruleViewAll", viewAll);
            sc.setRequestAttribute(request, "ruleViewNone", viewNone);
            sc.setRequestAttribute(request, "ruleViewSubmitter", viewSubmitter);
            sc.setRequestAttribute(request, "ruleViewHandler", viewHandler);
            sc.setRequestAttribute(request, "ruleViewSAH", viewSAH);
            sc.setRequestAttribute(request, "ruleBeHandlerAll", beHandlerAll);
            sc.setRequestAttribute(request, "ruleBeHandlerNone", beHandlerNone);
            sc.setRequestAttribute(request, "ruleBeHandlerSubmitter", beHandlerSubmitter);
            sc.setRequestAttribute(request, "ruleBeHandlerHandler", beHandlerHandler);
            sc.setRequestAttribute(request, "ruleBeHandlerSAH", beHandlerSAH);

            sc.setRequestAttribute(request, "mstatus", mstatus);
            sc.setRequestAttribute(request, "mstatusId", mstatusId);
            sc.setRequestAttribute(request, "flow", flow);
            sc.setRequestAttribute(request, "workflowId", workflowId);

            boolean canManageOperation = flow.canManage();

            sc.setRequestAttribute(request, "canView", canManageOperation || canManageRoles);
            sc.setRequestAttribute(request, "canEdit", canManageOperation || canManageRoles);
            List<SecuredWorkflowUDFBean> udfs = flow.getWorkflowUDFs();
            sc.setRequestAttribute(request, "canViewMstatusList", canManageOperation);
            selectTaskTab(sc, id, "tabWorkflows", request);
            sc.setRequestAttribute(request, "tabView", new Tab(canManageOperation || canManageRoles, false));
            sc.setRequestAttribute(request, "tabEdit", new Tab(canManageOperation, false));
            sc.setRequestAttribute(request, "tabResolutions", new Tab(canManageOperation, false));
            sc.setRequestAttribute(request, "tabTransitions", new Tab(canManageOperation, false));
            sc.setRequestAttribute(request, "tabPermissions", new Tab(canManageOperation || canManageRoles, true));
            sc.setRequestAttribute(request, "tabTriggers", new Tab(flow.canManage() && sc.canAction(Action.manageWorkflows, mstatus.getWorkflow().getTaskId()), false));
            sc.setRequestAttribute(request, "tabUdfPermissions", new Tab(!udfs.isEmpty() && (canManageOperation), false));
            sc.setRequestAttribute(request, "tabScheduler", new Tab(canManageOperation || canManageRoles, false));
            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_TASK_WORKFLOW_MSTATUS_PERMISSIONS);
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_TASK_WORKFLOW_MSTATUS_PERMISSIONS));
            return mapping.findForward("mstatusPermissionJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            MstatusPermissionForm tf = (MstatusPermissionForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String mstatusId = tf.getMstatusId();
            Set<SecuredPrstatusBean> prstatuses = new TreeSet<SecuredPrstatusBean>(AdapterManager.getInstance().getSecuredPrstatusAdapterManager().getAvailablePrstatusList(sc, sc.getUserId()));

            String canprocess = tf.getHiddencanprocess();
            String canview = tf.getHiddencanview();
            String canhandler = tf.getHiddencanhandler();
            ArrayList<String> canViewPrstatuses = new ArrayList<String>();
            ArrayList<String> canViewPrstatusesHandler = new ArrayList<String>();
            ArrayList<String> canViewPrstatusesSubmitter = new ArrayList<String>();
            ArrayList<String> canViewPrstatusesSubmitterAndHandler = new ArrayList<String>();

            ArrayList<String> canProcessPrstatuses = new ArrayList<String>();
            ArrayList<String> canProcessPrstatusesHandler = new ArrayList<String>();
            ArrayList<String> canProcessPrstatusesSubmitter = new ArrayList<String>();
            ArrayList<String> canProcessPrstatusesSubmitterAndHandler = new ArrayList<String>();

            ArrayList<String> canBeHandlerPrstatuses = new ArrayList<String>();
            ArrayList<String> canBeHandlerPrstatusesHandler = new ArrayList<String>();
            ArrayList<String> canBeHandlerPrstatusesSubmitter = new ArrayList<String>();
            ArrayList<String> canBeHandlerPrstatusesSubmitterAndHandler = new ArrayList<String>();

            parseForm(sc, canview, canViewPrstatusesSubmitterAndHandler, canViewPrstatusesHandler, canViewPrstatusesSubmitter, canViewPrstatuses);
            parseForm(sc, canprocess, canProcessPrstatusesSubmitterAndHandler, canProcessPrstatusesHandler, canProcessPrstatusesSubmitter, canProcessPrstatuses);
            parseForm(sc, canhandler, canBeHandlerPrstatusesSubmitterAndHandler, canBeHandlerPrstatusesHandler, canBeHandlerPrstatusesSubmitter, canBeHandlerPrstatuses);

            SecuredWorkflowBean flow = AdapterManager.getInstance().getSecuredFindAdapterManager().findWorkflowById(sc, tf.getWorkflowId());
            for (SecuredPrstatusBean prstatus : prstatuses) {
                if (flow.canManage() && prstatus.canView() || !flow.canManage() && prstatus.isAllowedByACL()) {
                    String prstatusId = prstatus.getId();
                    String view = null, process = null, handler = null;
                    if (canViewPrstatuses.contains(prstatusId)) {
                        view = WorkflowConstants.VIEW_ALL;
                    } else if (canViewPrstatusesHandler.contains(prstatusId)) {
                        view = WorkflowConstants.VIEW_HANDLER;
                    } else if (canViewPrstatusesSubmitter.contains(prstatusId)) {
                        view = WorkflowConstants.VIEW_SUBMITTER;
                    } else if (canViewPrstatusesSubmitterAndHandler.contains(prstatusId)) {
                        view = WorkflowConstants.VIEW_SUBMITTER_AND_HANDLER;
                    }

                    if (canProcessPrstatuses.contains(prstatusId)) {
                        process = WorkflowConstants.PROCESS_ALL;
                    } else if (canProcessPrstatusesHandler.contains(prstatusId)) {
                        process = WorkflowConstants.PROCESS_HANDLER;
                    } else if (canProcessPrstatusesSubmitter.contains(prstatusId)) {
                        process = WorkflowConstants.PROCESS_SUBMITTER;
                    } else if (canProcessPrstatusesSubmitterAndHandler.contains(prstatusId)) {
                        process = WorkflowConstants.PROCESS_SUBMITTER_AND_HANDLER;
                    }

                    if (canBeHandlerPrstatuses.contains(prstatusId)) {
                        handler = WorkflowConstants.BE_HANDLER_ALL;
                    } else if (canBeHandlerPrstatusesHandler.contains(prstatusId)) {
                        handler = WorkflowConstants.BE_HANDLER_HANDLER;
                    } else if (canBeHandlerPrstatusesSubmitter.contains(prstatusId)) {
                        handler = WorkflowConstants.BE_HANDLER_SUBMITTER;
                    } else if (canBeHandlerPrstatusesSubmitterAndHandler.contains(prstatusId)) {
                        handler = WorkflowConstants.BE_HANDLER_SUBMITTER_AND_HANDLER;
                    }

                    AdapterManager.getInstance().getSecuredWorkflowAdapterManager().grantView(sc, view, prstatusId, mstatusId);
                    AdapterManager.getInstance().getSecuredWorkflowAdapterManager().grantProcess(sc, process, prstatusId, mstatusId);
                    AdapterManager.getInstance().getSecuredWorkflowAdapterManager().grantBeHandler(sc, handler, prstatusId, mstatusId);
                }
            }
            return mapping.findForward("mstatusPermissionPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    private void parseForm(SessionContext sc, String canview, ArrayList<String> canViewPrstatusesSubmitterAndHandler, ArrayList<String> canViewPrstatusesHandler, ArrayList<String> canViewPrstatusesSubmitter, ArrayList<String> canViewPrstatuses) throws GranException {
        StringTokenizer tk = new StringTokenizer(canview, FValue.DELIM);
        while (tk.hasMoreElements()) {
            String token = tk.nextToken();
            if (token.length() > 0) {
                int posSpec = token.indexOf("(*");
                if (posSpec > 0) {
                    int posHanlder = token.indexOf(I18n.getString(sc, "HANDLER"));
                    int posSubmitter = token.indexOf(I18n.getString(sc, "SUBMITTER"));
                    String prstatusId = token.substring(0, posSpec - 1).trim();
                    if (posHanlder > -1 && posSubmitter > -1) {
                        canViewPrstatusesSubmitterAndHandler.add(prstatusId);
                    } else if (posHanlder > -1 && posSubmitter == -1) {
                        canViewPrstatusesHandler.add(prstatusId);
                    } else if (posHanlder == -1 && posSubmitter > -1) {
                        canViewPrstatusesSubmitter.add(prstatusId);
                    }
                } else {
                    canViewPrstatuses.add(token.trim());
                }
            }
        }
    }
}
