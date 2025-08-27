package com.trackstudio.action.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.trackstudio.action.GeneralAction;
import com.trackstudio.action.TSDispatchAction;
import com.trackstudio.app.PageNaming;
import com.trackstudio.app.Preferences;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.constants.UdfConstants;
import com.trackstudio.constants.WorkflowConstants;
import com.trackstudio.form.MstatusForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.secured.SecuredMstatusBean;
import com.trackstudio.secured.SecuredPrstatusBean;
import com.trackstudio.secured.SecuredResolutionBean;
import com.trackstudio.secured.SecuredTransitionBean;
import com.trackstudio.secured.SecuredUDFBean;
import com.trackstudio.secured.SecuredWorkflowBean;
import com.trackstudio.secured.SecuredWorkflowUDFBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.EggBasket;
import com.trackstudio.tools.ExternalAdapterManagerUtil;
import com.trackstudio.tools.Tab;

public class MstatusViewAction extends TSDispatchAction {

    public static class PermissionsUDFListItem {
        String name;
        boolean canView;
        boolean canEdit;

        public PermissionsUDFListItem(String name) {
            this.name = name;
            this.canView = false;
            this.canEdit = false;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isCanEdit() {
            return canEdit;
        }

        public void setCanEdit(boolean canEdit) {
            this.canEdit = canEdit;
        }

        public boolean isCanView() {
            return canView;
        }

        public void setCanView(boolean canView) {
            this.canView = canView;
        }
    }

    // todo тут надо все переделать #62686
    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().taskHeader(null, sc, request, true);
            MstatusForm mf = (MstatusForm) form;
            String mstatusId = mf.getMstatusId() != null ? mf.getMstatusId() : request.getParameter("mstatusId");
            SecuredMstatusBean mstatus = AdapterManager.getInstance().getSecuredFindAdapterManager().findMstatusById(sc, mstatusId);
            String wfId = mstatus.getWorkflowId();
            SecuredWorkflowBean flow = AdapterManager.getInstance().getSecuredFindAdapterManager().findWorkflowById(sc, wfId);
            boolean canManage = flow.canManage();
            sc.setRequestAttribute(request, "workflowId", wfId);
            sc.setRequestAttribute(request, "flow", flow);
            if (canManage) {
                List<SecuredResolutionBean> res = AdapterManager.getInstance().getSecuredWorkflowAdapterManager().getResolutionList(sc, mstatusId);
                Collections.sort(res);
                sc.setRequestAttribute(request, "resolutions", res);
            }
            if (canManage) {
                List<SecuredTransitionBean> transitionList = AdapterManager.getInstance().getSecuredWorkflowAdapterManager().getTransitionList(sc, mstatus.getId());
                Collections.sort(transitionList);
                sc.setRequestAttribute(request, "transitions", transitionList);
            }
            //todo это может быть неправильным. Вообще, тут нужно узнать, есть ли у залогиненого пользователя права управлять ролями вообще
            boolean canManageRoles = sc.canAction(Action.manageRoles, sc.getUserId());
            boolean viewMessageTypePermission = canManage || canManageRoles;
            if (viewMessageTypePermission) {

                ArrayList<SecuredPrstatusBean> prstatusSet = AdapterManager.getInstance().getSecuredPrstatusAdapterManager().getAvailablePrstatusList(sc, sc.getUserId());
                ArrayList<SecuredPrstatusBean> processAll = new ArrayList<SecuredPrstatusBean>();
                ArrayList<SecuredPrstatusBean> processSubmitter = new ArrayList<SecuredPrstatusBean>();
                ArrayList<SecuredPrstatusBean> processHandler = new ArrayList<SecuredPrstatusBean>();
                ArrayList<SecuredPrstatusBean> processSAH = new ArrayList<SecuredPrstatusBean>();

                ArrayList<SecuredPrstatusBean> viewAll = new ArrayList<SecuredPrstatusBean>();
                ArrayList<SecuredPrstatusBean> viewSubmitter = new ArrayList<SecuredPrstatusBean>();
                ArrayList<SecuredPrstatusBean> viewHandler = new ArrayList<SecuredPrstatusBean>();
                ArrayList<SecuredPrstatusBean> viewSAH = new ArrayList<SecuredPrstatusBean>();

                ArrayList<SecuredPrstatusBean> beHandlerAll = new ArrayList<SecuredPrstatusBean>();
                ArrayList<SecuredPrstatusBean> beHandlerSubmitter = new ArrayList<SecuredPrstatusBean>();
                ArrayList<SecuredPrstatusBean> beHandlerHandler = new ArrayList<SecuredPrstatusBean>();
                ArrayList<SecuredPrstatusBean> beHandlerSAH = new ArrayList<SecuredPrstatusBean>();

                EggBasket<String, String> rules = ExternalAdapterManagerUtil.getMprstatusMap(mstatusId);
                for (SecuredPrstatusBean prstatus : prstatusSet) {
                    String prstatusId = prstatus.getId();
                    List<String> types = rules.get(prstatusId);
                    if (types != null) {
                        if (types.contains(WorkflowConstants.VIEW_ALL))
                            viewAll.add(prstatus);
                        else if (types.contains(WorkflowConstants.VIEW_SUBMITTER))
                            viewSubmitter.add(prstatus);
                        else if (types.contains(WorkflowConstants.VIEW_HANDLER))
                            viewHandler.add(prstatus);
                        else if (types.contains(WorkflowConstants.VIEW_SUBMITTER_AND_HANDLER))
                            viewSAH.add(prstatus);

                        if (types.contains(WorkflowConstants.PROCESS_ALL))
                            processAll.add(prstatus);
                        else if (types.contains(WorkflowConstants.PROCESS_SUBMITTER))
                            processSubmitter.add(prstatus);
                        else if (types.contains(WorkflowConstants.PROCESS_HANDLER))
                            processHandler.add(prstatus);
                        else if (types.contains(WorkflowConstants.PROCESS_SUBMITTER_AND_HANDLER))
                            processSAH.add(prstatus);

                        if (types.contains(WorkflowConstants.BE_HANDLER_ALL))
                            beHandlerAll.add(prstatus);
                        else if (types.contains(WorkflowConstants.BE_HANDLER_SUBMITTER))
                            beHandlerSubmitter.add(prstatus);
                        else if (types.contains(WorkflowConstants.BE_HANDLER_HANDLER))
                            beHandlerHandler.add(prstatus);
                        else if (types.contains(WorkflowConstants.BE_HANDLER_SUBMITTER_AND_HANDLER))
                            beHandlerSAH.add(prstatus);
                    }
                }
                Collections.sort(processAll, new SortSecuredPrstatusBean());
                Collections.sort(processSubmitter, new SortSecuredPrstatusBean());
                Collections.sort(processHandler, new SortSecuredPrstatusBean());
                Collections.sort(processSAH, new SortSecuredPrstatusBean());
                Collections.sort(viewAll, new SortSecuredPrstatusBean());
                Collections.sort(viewSubmitter, new SortSecuredPrstatusBean());
                Collections.sort(viewHandler, new SortSecuredPrstatusBean());
                Collections.sort(viewSAH, new SortSecuredPrstatusBean());
                Collections.sort(beHandlerAll, new SortSecuredPrstatusBean());
                Collections.sort(beHandlerSubmitter, new SortSecuredPrstatusBean());
                Collections.sort(beHandlerHandler, new SortSecuredPrstatusBean());
                Collections.sort(beHandlerSAH, new SortSecuredPrstatusBean());
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
            }

            sc.setRequestAttribute(request, "before", mstatus.getBefore() != null ? mstatus.getBefore() : I18n.getString(sc.getLocale(), "NONE"));
            sc.setRequestAttribute(request, "insteadOf", mstatus.getInsteadOf() != null ? mstatus.getInsteadOf() : I18n.getString(sc.getLocale(), "NONE"));
            sc.setRequestAttribute(request, "after", mstatus.getAfter() != null ? mstatus.getAfter() : I18n.getString(sc.getLocale(), "NONE"));

            HashMap udfPerm = new HashMap();
            List<SecuredWorkflowUDFBean> udfs = flow.getWorkflowUDFs();
            for (SecuredUDFBean udf : udfs) {
                Set prstatusSet = new TreeSet(AdapterManager.getInstance().getSecuredPrstatusAdapterManager().getAvailablePrstatusList(sc, sc.getUserId()));
                String viewRules = "", editRules = "";
                for (Object aPrstatusSet : prstatusSet) {
                    SecuredPrstatusBean spb = (SecuredPrstatusBean) aPrstatusSet;
                    List types = AdapterManager.getInstance().getSecuredUDFAdapterManager().getUDFRuleList(sc, spb.getId(), udf.getId());
                    boolean viewMstatusValues = false;
                    boolean editMstatusValues = false;

                    if (types.contains(UdfConstants.MSTATUS_VIEW_ALL))
                        viewMstatusValues = true;
                    if (types.contains(UdfConstants.MSTATUS_EDIT_ALL))
                        editMstatusValues = true;

                    if (types.contains(WorkflowConstants.MSTATUS_VIEW_PREFIX + mstatus.getId()))
                        viewMstatusValues = true;
                    if (types.contains(WorkflowConstants.MSTATUS_EDIT_PREFIX + mstatus.getId()))
                        editMstatusValues = true;
                    if (viewMstatusValues) {
                        if (viewRules.length() > 0)
                            viewRules += ", ";
                        viewRules += spb.getName();
                    }
                    if (editMstatusValues) {
                        if (editRules.length() > 0)
                            editRules += ", ";
                        editRules += spb.getName();
                    }
                }
                if (viewRules.length() > 0) {
                    String add = "<b>" + I18n.getString(sc.getLocale(), "CAN_VIEW") + ":</b> " + viewRules;
                    if (editRules.length() > 0)
                        add += "<br><b>" + I18n.getString(sc.getLocale(), "CAN_EDIT") + ":</b> " + editRules;
                    udfPerm.put(udf.getCaptionEx(), add);
                }
            }

            List<PermissionsUDFListItem> permissionsUDFList = new ArrayList<PermissionsUDFListItem>();
            List<String> editable = KernelManager.getUdf().getEditableUDFId(mstatus.getId());
            List<String> viewable = KernelManager.getUdf().getViewableUDFId(mstatus.getId());

            for (SecuredWorkflowUDFBean udf : udfs) {
                PermissionsUDFListItem permissionsUDFListItem = new PermissionsUDFListItem(udf.getCaption());
                if (editable.contains(udf.getId())) {
                    permissionsUDFListItem.setCanEdit(true);
                }
                if (viewable.contains(udf.getId())) {
                    permissionsUDFListItem.setCanView(true);
                }
                permissionsUDFList.add(permissionsUDFListItem);
            }

            TreeSet<SecuredPrstatusBean> canViewCanEditList = new TreeSet<SecuredPrstatusBean>();
            EggBasket<String, String> rules = ExternalAdapterManagerUtil.getMprstatusMap(mstatus.getId());
            Set<String> prstatusList = rules.keySet();
            for (String prstatusId : prstatusList) {
                List<String> types = rules.get(prstatusId);
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

                        canViewCanEditList.add(AdapterManager.getInstance().getSecuredFindAdapterManager().findPrstatusById(sc, prstatusId));
                    }
                }
            }
            boolean isValidPermissions = canViewCanEditList.isEmpty();
            sc.setRequestAttribute(request, "isValidPermissions", isValidPermissions);
            if (!isValidPermissions) {
                sc.setRequestAttribute(request, "canViewCanEditList", canViewCanEditList);
            }

            sc.setRequestAttribute(request, "udfList", permissionsUDFList);
            sc.setRequestAttribute(request, "udfPerm", udfPerm);
            sc.setRequestAttribute(request, "canViewTriggers", true);
            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_TASK_WORKFLOW_MSTATUS_OVERVIEW);
            sc.setRequestAttribute(request, "viewResolution", canManage);
            sc.setRequestAttribute(request, "viewTransition", canManage);
            sc.setRequestAttribute(request, "viewMessageTypePermission", viewMessageTypePermission);
            sc.setRequestAttribute(request, "mstatus", mstatus);
            sc.setRequestAttribute(request, "showInToolbar", Preferences.showInToolbar(mstatus.getPreferences()));
            sc.setRequestAttribute(request, "mstatusId", mstatusId);
            sc.setRequestAttribute(request, "canViewMstatusList", flow.canManage());
            sc.setRequestAttribute(request, "tabView", new Tab(canManage || canManageRoles, true));
            sc.setRequestAttribute(request, "tabEdit", new Tab(canManage, false));
            sc.setRequestAttribute(request, "tabResolutions", new Tab(canManage, false));
            sc.setRequestAttribute(request, "tabTransitions", new Tab(canManage, false));
            sc.setRequestAttribute(request, "tabPermissions", new Tab(canManage || canManageRoles, false));
            sc.setRequestAttribute(request, "tabTriggers", new Tab(flow.canManage() && sc.canAction(Action.manageWorkflows, mstatus.getWorkflow().getTaskId()), false));
            sc.setRequestAttribute(request, "tabUdfPermissions", new Tab(udfs.size() > 0 && (canManage), false));
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_TASK_WORKFLOW_MSTATUS_OVERVIEW));
            sc.setRequestAttribute(request, "tabScheduler", new Tab(canManage, false));
            return mapping.findForward("mstatusViewJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    private static class SortSecuredPrstatusBean implements Comparator<SecuredPrstatusBean> {
        @Override
        public int compare(SecuredPrstatusBean o1, SecuredPrstatusBean o2) {
            return o1.getName().compareTo(o2.getName());
        }
    }
}
