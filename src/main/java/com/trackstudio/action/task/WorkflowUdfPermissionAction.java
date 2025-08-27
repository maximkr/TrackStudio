package com.trackstudio.action.task;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.trackstudio.action.GeneralAction;
import com.trackstudio.action.TSDispatchAction;
import com.trackstudio.app.PageNaming;
import com.trackstudio.app.UdfValue;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.filter.FValue;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.constants.UdfConstants;
import com.trackstudio.containers.PrstatusListItem;
import com.trackstudio.form.CustomForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.secured.SecuredPrstatusBean;
import com.trackstudio.secured.SecuredUDFBean;
import com.trackstudio.secured.SecuredWorkflowBean;
import com.trackstudio.securedkernel.SecuredUDFAdapterManager;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.Tab;

public class WorkflowUdfPermissionAction extends TSDispatchAction {

    public ActionForward page(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            CustomForm form = (CustomForm) actionForm;
            SessionContext sc = GeneralAction.getInstance().imports(request, response);
            if (sc == null)
                return null;
            String id = GeneralAction.getInstance().taskHeader(form, sc, request, true);
            boolean canManageRole = sc.canAction(Action.manageRoles, sc.getUserId());
            if (!sc.canAction(Action.manageWorkflows, form.getId()) || !canManageRole)
                return null;
            String udfId = form.getUdfId();

            sc.setRequestAttribute(request, "udfId", udfId);
            SecuredUDFBean udf = AdapterManager.getInstance().getSecuredFindAdapterManager().findUDFById(sc, form.getUdfId());
            sc.setRequestAttribute(request, "udf", udf);

            ArrayList<PrstatusListItem> cannotviewStatuses = new ArrayList<PrstatusListItem>();
            ArrayList<PrstatusListItem> canviewStatuses = new ArrayList<PrstatusListItem>();
            ArrayList<PrstatusListItem> cannoteditStatuses = new ArrayList<PrstatusListItem>();
            ArrayList<PrstatusListItem> caneditStatuses = new ArrayList<PrstatusListItem>();
            StringBuffer canview = new StringBuffer(), canedit = new StringBuffer();

            Set<SecuredPrstatusBean> prstatusSet = new TreeSet<SecuredPrstatusBean>(AdapterManager.getInstance().getSecuredPrstatusAdapterManager().getAvailablePrstatusList(sc, sc.getUserId()));
            for (SecuredPrstatusBean spb : prstatusSet) {
                if (spb.isAllowedByACL() || spb.getUser().getSecure().allowedByACL(id)) {
                    List types = AdapterManager.getInstance().getSecuredUDFAdapterManager().getUDFRuleList(sc, spb.getId(), udfId);
                    if (!(types.contains(UdfConstants.VIEW_ALL) || types.contains(UdfConstants.VIEW_SUBMITTER) || types.contains(UdfConstants.VIEW_HANDLER) || types.contains(UdfConstants.VIEW_SUBMITTER_AND_HANDLER))) {
                        cannotviewStatuses.add(new PrstatusListItem(spb.getId(), spb.getName()));
                    }
                    if (types.contains(UdfConstants.VIEW_ALL)) {
                        canviewStatuses.add(new PrstatusListItem(spb.getId(), spb.getName()));
                        canview.append(spb.getId()).append(FValue.DELIM);
                    }
                    if (types.contains(UdfConstants.VIEW_SUBMITTER)) {
                        String id1 = spb.getId() + " (* " + I18n.getString(sc, "SUBMITTER") + ")";
                        canviewStatuses.add(new PrstatusListItem(id1, spb.getName() + " (* " + I18n.getString(sc, "SUBMITTER") + ")"));
                        canview.append(id1).append(FValue.DELIM);
                    }
                    if (types.contains(UdfConstants.VIEW_HANDLER)) {
                        String id1 = spb.getId() + " (* " + I18n.getString(sc, "HANDLER") + ")";
                        canviewStatuses.add(new PrstatusListItem(id1, spb.getName() + " (* " + I18n.getString(sc, "HANDLER") + ")"));
                        canview.append(id1).append(FValue.DELIM);
                    }
                    if (types.contains(UdfConstants.VIEW_SUBMITTER_AND_HANDLER)) {
                        String id1 = spb.getId() + " (* " + I18n.getString(sc, "HANDLER") + ", " + I18n.getString(sc, "SUBMITTER") + ")";
                        canviewStatuses.add(new PrstatusListItem(id1, spb.getName() + " (* " + I18n.getString(sc, "HANDLER") + ", " + I18n.getString(sc, "SUBMITTER") + ")"));
                        canview.append(id1).append(FValue.DELIM);
                    }

                    if (!(types.contains(UdfConstants.EDIT_ALL) || types.contains(UdfConstants.EDIT_HANDLER) || types.contains(UdfConstants.EDIT_SUBMITTER) || types.contains(UdfConstants.EDIT_SUBMITTER_AND_HANDLER)))
                        cannoteditStatuses.add(new PrstatusListItem(spb.getId(), spb.getName()));
                    if (types.contains(UdfConstants.EDIT_ALL)) {
                        caneditStatuses.add(new PrstatusListItem(spb.getId(), spb.getName()));
                        canedit.append(spb.getId()).append(FValue.DELIM);
                    }
                    if (types.contains(UdfConstants.EDIT_SUBMITTER)) {
                        String id1 = spb.getId() + " (* " + I18n.getString(sc, "SUBMITTER") + ")";
                        caneditStatuses.add(new PrstatusListItem(id1, spb.getName() + " (* " + I18n.getString(sc, "SUBMITTER") + ")"));
                        canedit.append(id1).append(FValue.DELIM);
                    }
                    if (types.contains(UdfConstants.EDIT_HANDLER)) {
                        String id1 = spb.getId() + " (* " + I18n.getString(sc, "HANDLER") + ")";
                        caneditStatuses.add(new PrstatusListItem(id1, spb.getName() + " (* " + I18n.getString(sc, "HANDLER") + ")"));
                        canedit.append(id1).append(FValue.DELIM);
                    }
                    if (types.contains(UdfConstants.EDIT_SUBMITTER_AND_HANDLER)) {
                        String s = spb.getId() + " (* " + I18n.getString(sc, "HANDLER") + ", " + I18n.getString(sc, "SUBMITTER") + ")";
                        caneditStatuses.add(new PrstatusListItem(s, spb.getName() + " (* " + I18n.getString(sc, "HANDLER") + ", " + I18n.getString(sc, "SUBMITTER") + ")"));
                        canedit.append(s).append(FValue.DELIM);
                    }
                }
            }
            form.setHiddencanedit(canedit.toString());
            form.setHiddencanview(canview.toString());
            sc.setRequestAttribute(request, "cannotviewStatuses", cannotviewStatuses);
            sc.setRequestAttribute(request, "canviewStatuses", canviewStatuses);
            sc.setRequestAttribute(request, "caneditStatuses", caneditStatuses);
            sc.setRequestAttribute(request, "cannoteditStatuses", cannoteditStatuses);
            sc.setRequestAttribute(request, "action", "/WorkflowUdfPermissionAction.do");

            SecuredWorkflowBean flow = AdapterManager.getInstance().getSecuredFindAdapterManager().findWorkflowById(sc, form.getWorkflowId());
            boolean canEdit = flow.canManage() || canManageRole;
            sc.setRequestAttribute(request, "canEdit", sc.canAction(Action.manageWorkflows, id));
            selectTaskTab(sc, id, "tabWorkflows", request);
            sc.setRequestAttribute(request, "workflowId", form.getWorkflowId());
            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_TASK_WORKFLOW_CUSTOM_FIELD_PERMISSIONS);
            sc.setRequestAttribute(request, "canViewPermission", canEdit);
            sc.setRequestAttribute(request, "tabView", new Tab(canEdit, false));
            sc.setRequestAttribute(request, "tabListValues", new Tab(udf != null && udf.canManage() && sc.canAction(Action.manageWorkflows, id) && sc.allowedByACL(id) && (udf.getType() == UdfValue.LIST || udf.getType() == UdfValue.MULTILIST), false));
            sc.setRequestAttribute(request, "tabEdit", new Tab(canEdit, false));
            boolean permission = (sc.canAction(Action.manageWorkflows, id)) || canManageRole && request.getAttribute("udf") != null && sc.allowedByACL(id);
            sc.setRequestAttribute(request, "tabPermission", new Tab(permission, true));
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_TASK_WORKFLOW_CUSTOM_FIELD_PERMISSIONS));
            sc.setRequestAttribute(request, "cancelAction", "/WorkflowUdfViewAction.do");
            sc.setRequestAttribute(request, "flow", flow);
            sc.setRequestAttribute(request, "canManage", canEdit);
            return actionMapping.findForward("workflowUdfPermissionJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            CustomForm tf = (CustomForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().taskHeader(tf, sc, request, false);
            SecuredUDFAdapterManager cam = AdapterManager.getInstance().getSecuredUDFAdapterManager();
            String udfId = tf.getUdfId();
            ArrayList<SecuredPrstatusBean> prstatuses = AdapterManager.getInstance().getSecuredPrstatusAdapterManager().getAvailablePrstatusList(sc, sc.getUserId());
            String canedit = tf.getHiddencanedit();
            String canview = tf.getHiddencanview();
            HashSet<String> canViewPrstatuses = new HashSet<String>();
            HashSet<String> canViewPrstatusesHandler = new HashSet<String>();
            HashSet<String> canViewPrstatusesSubmitter = new HashSet<String>();
            HashSet<String> canViewPrstatusesSubmitterAndHandler = new HashSet<String>();
            HashSet<String> canEditPrstatuses = new HashSet<String>();
            HashSet<String> canEditPrstatusesHandler = new HashSet<String>();
            HashSet<String> canEditPrstatusesSubmitter = new HashSet<String>();
            HashSet<String> canEditPrstatusesSubmitterAndHandler = new HashSet<String>();

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
                        canViewPrstatuses.add(token);
                    }
                }
            }
            tk = new StringTokenizer(canedit, FValue.DELIM);
            while (tk.hasMoreElements()) {
                String token = tk.nextToken();
                if (token.length() > 0) {
                    int posSpec = token.indexOf("(*");
                    if (posSpec > 0) {
                        int posHanlder = token.indexOf(I18n.getString(sc, "HANDLER"));
                        int posSubmitter = token.indexOf(I18n.getString(sc, "SUBMITTER"));
                        String prstatusId = token.substring(0, posSpec - 1).trim();
                        if (posHanlder > -1 && posSubmitter > -1) {
                            canEditPrstatusesSubmitterAndHandler.add(prstatusId);
                        } else if (posHanlder > -1 && posSubmitter == -1) {
                            canEditPrstatusesHandler.add(prstatusId);
                        } else if (posHanlder == -1 && posSubmitter > -1) {
                            canEditPrstatusesSubmitter.add(prstatusId);
                        }
                    } else {
                        canEditPrstatuses.add(token);
                    }
                }
            }
            for (SecuredPrstatusBean bean : prstatuses) {
                if (bean.isAllowedByACL() || bean.getUser().getSecure().allowedByACL(id)) {
                    String prstatusId = bean.getId();
                    String view = null, edit = null;
                    if (canViewPrstatuses.contains(prstatusId)) {
                        view = UdfConstants.VIEW_ALL;
                    } else if (canViewPrstatusesHandler.contains(prstatusId)) {
                        view = UdfConstants.VIEW_HANDLER;
                    } else if (canViewPrstatusesSubmitter.contains(prstatusId)) {
                        view = UdfConstants.VIEW_SUBMITTER;
                    } else if (canViewPrstatusesSubmitterAndHandler.contains(prstatusId)) {
                        view = UdfConstants.VIEW_SUBMITTER_AND_HANDLER;
                    }

                    if (canEditPrstatuses.contains(prstatusId)) {
                        edit = UdfConstants.EDIT_ALL;
                    } else if (canEditPrstatusesHandler.contains(prstatusId)) {
                        edit = UdfConstants.EDIT_HANDLER;
                    } else if (canEditPrstatusesSubmitter.contains(prstatusId)) {
                        edit = UdfConstants.EDIT_SUBMITTER;
                    } else if (canEditPrstatusesSubmitterAndHandler.contains(prstatusId)) {
                        edit = UdfConstants.EDIT_SUBMITTER_AND_HANDLER;
                    }

                    cam.setWorkflowUDFRule(sc, udfId, prstatusId, view, edit);
                }
            }
            return mapping.findForward("workflowUdfPermissionPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }
}
