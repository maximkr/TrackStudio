package com.trackstudio.action.user;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.trackstudio.action.GeneralAction;
import com.trackstudio.action.TSDispatchAction;
import com.trackstudio.app.PageNaming;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.filter.FValue;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.constants.UdfConstants;
import com.trackstudio.exception.GranException;
import com.trackstudio.form.PrstatusForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.secured.SecuredPrstatusBean;
import com.trackstudio.secured.SecuredWorkflowBean;
import com.trackstudio.secured.SecuredWorkflowUDFBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.Pair;
import com.trackstudio.tools.Tab;
import com.trackstudio.view.UserViewHTMLLinked;

public class UserUdfMTypeSecurityAction extends TSDispatchAction {
    public ActionForward page(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            PrstatusForm form = (PrstatusForm) actionForm;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().userHeader(form, sc, request);
            if (!sc.canAction(Action.manageWorkflows, id))
                return null;
            String prstatusId = form.getPrstatusId();
            String workflowId = form.getWorkflowId();

            SecuredPrstatusBean prstatus = AdapterManager.getInstance().getSecuredFindAdapterManager().findPrstatusById(sc, prstatusId);
            SecuredWorkflowBean workflow = AdapterManager.getInstance().getSecuredFindAdapterManager().findWorkflowById(sc, workflowId);
            form.setName(prstatus.getName());
            boolean canEdit = sc.canAction(Action.manageWorkflows, prstatus.getUserId()) && prstatus.isAllowedByACL();
            // Tab attributes
            sc.setRequestAttribute(request, "owner", new UserViewHTMLLinked(prstatus.getUser(), request.getContextPath()).getPath());
            sc.setRequestAttribute(request, "workflow", workflow);
            sc.setRequestAttribute(request, "workflowId", workflow.getId());
            sc.setRequestAttribute(request, "name", prstatus.getName());
            sc.setRequestAttribute(request, "prstatusId", prstatusId);
            sc.setRequestAttribute(request, "currentPrstatus", prstatus);
            sc.setRequestAttribute(request, "canView", sc.canAction(Action.manageRoles, id));
            sc.setRequestAttribute(request, "tabView", new Tab(sc.canAction(Action.manageRoles, id), false));
            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_USER_MTYPE_USER_SECURITY);
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_USER_MTYPE_USER_SECURITY));

            Boolean editable = sc.canAction(Action.manageWorkflows, workflow.getTaskId()) && sc.canAction(Action.manageWorkflows, id) && (workflow.canManage() || prstatus.isAllowedByACL());

            List<SecuredWorkflowUDFBean> udfs = workflow.getWorkflowUDFs();

            StringBuilder canview = new StringBuilder(), canedit = new StringBuilder();

            List<Pair> canViewList = new ArrayList<Pair>();
            List<Pair> canEditList = new ArrayList<Pair>();
            List<Pair> canNotViewList = new ArrayList<Pair>();
            List<Pair> canNotEditList = new ArrayList<Pair>();
            for (SecuredWorkflowUDFBean udf : udfs) {
                List<String> rules = AdapterManager.getInstance().getSecuredUDFAdapterManager().getUDFRuleList(sc, prstatusId, udf.getId());
                if (rules.contains(UdfConstants.VIEW_ALL) || rules.contains(UdfConstants.STATUS_VIEW_ALL)) {
                    String specId = udf.getId();
                    canview.append(specId).append(FValue.DELIM);
                    canViewList.add(new Pair(specId, udf.getCaption()));
                } else if (rules.contains(UdfConstants.VIEW_HANDLER)) {
                    String specId = udf.getId() + " (* " + I18n.getString(sc, "HANDLER") + ")";
                    canview.append(specId).append(FValue.DELIM);
                    canViewList.add(new Pair(specId, udf.getCaption() + " (* " + I18n.getString(sc, "HANDLER") + ")"));
                } else if (rules.contains(UdfConstants.VIEW_SUBMITTER)) {
                    String specId = udf.getId() + " (* " + I18n.getString(sc, "SUBMITTER") + ")";
                    canview.append(specId).append(FValue.DELIM);
                    canViewList.add(new Pair(specId, udf.getCaption() + " (* " + I18n.getString(sc, "SUBMITTER") + ")"));
                } else if (rules.contains(UdfConstants.VIEW_SUBMITTER_AND_HANDLER)) {
                    String specId = udf.getId() + " (* " + I18n.getString(sc, "HANDLER") + ", " + I18n.getString(sc, "SUBMITTER") + ")";
                    canview.append(specId).append(FValue.DELIM);
                    canViewList.add(new Pair(specId, udf.getCaption() + " (* " + I18n.getString(sc, "HANDLER") + ", " + I18n.getString(sc, "SUBMITTER") + ")"));
                } else {
                    canNotViewList.add(new Pair(udf.getId(), udf.getCaption()));
                }

                if (rules.contains(UdfConstants.EDIT_ALL) || rules.contains(UdfConstants.STATUS_EDIT_ALL)) {
                    String specId = udf.getId();
                    canedit.append(specId).append(FValue.DELIM);
                    canEditList.add(new Pair(udf.getId(), udf.getCaption()));
                } else if (rules.contains(UdfConstants.EDIT_HANDLER)) {
                    String specId = udf.getId() + " (* " + I18n.getString(sc, "HANDLER") + ")";
                    canedit.append(specId).append(FValue.DELIM);
                    canEditList.add(new Pair(specId, udf.getCaption() + " (* " + I18n.getString(sc, "HANDLER") + ")"));
                } else if (rules.contains(UdfConstants.EDIT_SUBMITTER)) {
                    String specId = udf.getId() + " (* " + I18n.getString(sc, "SUBMITTER") + ")";
                    canedit.append(specId).append(FValue.DELIM);
                    canEditList.add(new Pair(specId, udf.getCaption() + " (* " + I18n.getString(sc, "SUBMITTER") + ")"));
                } else if (rules.contains(UdfConstants.EDIT_SUBMITTER_AND_HANDLER)) {
                    String specId = udf.getId() + " (* " + I18n.getString(sc, "HANDLER") + ", " + I18n.getString(sc, "SUBMITTER") + ")";
                    canedit.append(specId).append(FValue.DELIM);
                    canEditList.add(new Pair(specId, udf.getCaption() + " (* " + I18n.getString(sc, "HANDLER") + ", " + I18n.getString(sc, "SUBMITTER") + ")"));
                } else {
                    canNotEditList.add(new Pair(udf.getId(), udf.getCaption()));
                }
            }

            sc.setRequestAttribute(request, "canedit", canedit);
            sc.setRequestAttribute(request, "canview", canview);
            sc.setRequestAttribute(request, "canViewList", canViewList);
            sc.setRequestAttribute(request, "canEditList", canEditList);
            sc.setRequestAttribute(request, "canNotViewList", canNotViewList);
            sc.setRequestAttribute(request, "canNotEditList", canNotEditList);

            sc.setRequestAttribute(request, "udfs", udfs);
            sc.setRequestAttribute(request, "canEdit", canEdit);

            sc.setRequestAttribute(request, "tabMType", new Tab(editable, false));
            sc.setRequestAttribute(request, "tabUdfMType", new Tab(true, true));
            return actionMapping.findForward("userUdfMTypeSecurityJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            PrstatusForm prform = (PrstatusForm) form;
            SessionContext sc = GeneralAction.getInstance().imports(request, response);
            if (sc == null) return null;
            String id = GeneralAction.getInstance().userHeader(prform, sc, request);
            if (!sc.canAction(Action.manageWorkflows, id))
                return null;
            String prstatusId = prform.getPrstatusId();
            List<String> viewUdfs = new ArrayList<String>();
            List<String> viewH = new ArrayList<String>();
            List<String> viewS = new ArrayList<String>();
            List<String> viewSH = new ArrayList<String>();
            List<String> editUdfs = new ArrayList<String>();
            List<String> editH = new ArrayList<String>();
            List<String> editS = new ArrayList<String>();
            List<String> editSH = new ArrayList<String>();
            breakList(sc, prform.getHiddencanview(), viewUdfs, viewH, viewS, viewSH);
            breakList(sc, prform.getHiddencanedit(), editUdfs, editH, editS, editSH);
            SecuredWorkflowBean workflow = AdapterManager.getInstance().getSecuredFindAdapterManager().findWorkflowById(sc, prform.getWorkflowId());
            List<SecuredWorkflowUDFBean> udfs = workflow.getWorkflowUDFs();
            for (SecuredWorkflowUDFBean udf : udfs) {
                String view = null;
                String edit = null;
                if (viewUdfs.contains(udf.getId()))
                    view = UdfConstants.VIEW_ALL;
                if (viewH.contains(udf.getId()))
                    view = UdfConstants.VIEW_HANDLER;
                if (viewS.contains(udf.getId()))
                    view = UdfConstants.VIEW_SUBMITTER;
                if (viewSH.contains(udf.getId()))
                    view = UdfConstants.VIEW_SUBMITTER_AND_HANDLER;
                if (editUdfs.contains(udf.getId()))
                    edit = UdfConstants.EDIT_ALL;
                if (editH.contains(udf.getId()))
                    edit = UdfConstants.EDIT_HANDLER;
                if (editS.contains(udf.getId()))
                    edit = UdfConstants.EDIT_SUBMITTER;
                if (editSH.contains(udf.getId()))
                    edit = UdfConstants.EDIT_SUBMITTER_AND_HANDLER;
                AdapterManager.getInstance().getSecuredUDFAdapterManager().setWorkflowUDFRule(sc, udf.getId(), prstatusId, view, edit);
            }
            return mapping.findForward("userUdfMTypeSecurityPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    private void breakList(SessionContext sc, final String text, final List<String> all, final List<String> handler, final List<String> submitter, final List<String> sh) throws GranException {
        StringTokenizer tk = new StringTokenizer(text, FValue.DELIM);
        while (tk.hasMoreElements()) {
            String token = tk.nextToken();
            if (token.length() > 0) {
                int posSpec = token.indexOf("(*");
                if (posSpec > 0) {
                    int posHanlder = token.indexOf(I18n.getString(sc, "HANDLER"));
                    int posSubmitter = token.indexOf(I18n.getString(sc, "SUBMITTER"));
                    String udfId = token.substring(0, posSpec - 1).trim();
                    if (posHanlder > -1 && posSubmitter > -1) {
                        sh.add(udfId);
                    } else if (posHanlder > -1 && posSubmitter == -1) {
                        handler.add(udfId);
                    } else if (posHanlder == -1 && posSubmitter > -1) {
                        submitter.add(udfId);
                    }
                } else {
                    all.add(token);
                }
            }
        }
    }
}
