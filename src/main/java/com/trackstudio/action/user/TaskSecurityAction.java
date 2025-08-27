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
import com.trackstudio.form.RoleTaskSecurityForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.cache.ActionCacheManager;
import com.trackstudio.secured.SecuredPrstatusBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.Tab;


public class TaskSecurityAction extends TSDispatchAction {
    private static Log log = LogFactory.getLog(TaskFieldSecurityAction.class);

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            RoleTaskSecurityForm pf = (RoleTaskSecurityForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().userHeader(pf, sc, request);
            if (!sc.canAction(Action.manageRoles, id))
                return null;
            String prstatusId = pf.getPrstatusId();

            SecuredPrstatusBean prstatus = AdapterManager.getInstance().getSecuredFindAdapterManager().findPrstatusById(sc, prstatusId);
            pf.setName(prstatus.getName());
            boolean canEdit = prstatus.canManage();

            List<Action> choosed = ActionCacheManager.getInstance().getActions(prstatus.getId());
            List<Action> choosedCurrent = ActionCacheManager.getInstance().getActions(sc.getPrstatusId());
            pf.setManageRegistrations(choosed.contains(Action.manageRegistrations));
            pf.setCutCopyPasteTask(choosed.contains(Action.cutCopyPasteTask));
            pf.setBulkProcessingTask(choosed.contains(Action.bulkProcessingTask));
            pf.setViewTaskAttachments(choosed.contains(Action.viewTaskAttachments));
            pf.setCreateTaskAttachments(choosed.contains(Action.createTaskAttachments));
            pf.setManageTaskAttachments(choosed.contains(Action.manageTaskAttachments));
            pf.setDeleteTheirTaskAttachment(choosed.contains(Action.manageTaskAttachments) || choosed.contains(Action.deleteTheirTaskAttachment));
            pf.setCreateTaskMessageAttachments(choosed.contains(Action.createTaskMessageAttachments));
            pf.setManageTaskMessageAttachments(choosed.contains(Action.manageTaskMessageAttachments));
            pf.setDeleteTheirMessageAttachment(choosed.contains(Action.manageTaskMessageAttachments) || choosed.contains(Action.deleteTheirMessageAttachment));
            pf.setViewFilters(choosed.contains(Action.viewFilters));
            pf.setManageTaskPrivateFilters(choosed.contains(Action.manageTaskPrivateFilters));
            pf.setManageTaskPublicFilters(choosed.contains(Action.manageTaskPublicFilters));
            pf.setViewReports(choosed.contains(Action.viewReports));
            pf.setManagePrivateReports(choosed.contains(Action.managePrivateReports));
            pf.setManagePublicReports(choosed.contains(Action.managePublicReports));
            pf.setManageEmailSchedules(choosed.contains(Action.manageEmailSchedules));
            pf.setManageTaskACLs(choosed.contains(Action.manageTaskACLs));
            pf.setManageTaskUDFs(choosed.contains(Action.manageTaskUDFs));
            pf.setManageEmailImportRules(choosed.contains(Action.manageEmailImportRules));
            pf.setManageTaskTemplates(choosed.contains(Action.manageTaskTemplates));
            pf.setManageCategories(choosed.contains(Action.manageCategories));
            pf.setManageWorkflows(choosed.contains(Action.manageWorkflows));
            pf.setDeleteOperations(choosed.contains(Action.deleteOperations));
            pf.setViewScriptsBrowser(choosed.contains(Action.viewScriptsBrowser));
            pf.setViewTemplatesBrowser(choosed.contains(Action.viewTemplatesBrowser));
            pf.setShowView(choosed.contains(Action.showView));
            pf.setShowOtherFilterTab(choosed.contains(Action.showOtherFilterTab));
            pf.setCanCreateTaskByOperation(choosed.contains(Action.canCreateTaskByOperation));
            pf.setCanUsePostFiltration(choosed.contains(Action.canUsePostFiltration));
            pf.setCanArchive(choosed.contains(Action.canArchive));
            pf.setCanDeleteArchive(choosed.contains(Action.canDeleteArchive));
            sc.setRequestAttribute(request, "currentPrstatus", prstatus);
            sc.setRequestAttribute(request, "prstatusId", prstatusId);
            sc.setRequestAttribute(request, "canView", canEdit);
            sc.setRequestAttribute(request, "canEdit", canEdit);

            sc.setRequestAttribute(request, "tabEdit", new Tab(canEdit, false));
            sc.setRequestAttribute(request, "tabTaskFieldSecurity", new Tab(canEdit, false));
            sc.setRequestAttribute(request, "tabUserFieldSecurity", new Tab(canEdit, false));
            sc.setRequestAttribute(request, "tabUserSecurity", new Tab(canEdit, false));
            sc.setRequestAttribute(request, "tabTaskSecurity", new Tab(canEdit, true));
            sc.setRequestAttribute(request, "tabCategorySecurity", new Tab(choosedCurrent.contains(Action.manageCategories), false));
            sc.setRequestAttribute(request, "tabWorkflowSecurity", new Tab(choosedCurrent.contains(Action.manageWorkflows), false));
            sc.setRequestAttribute(request, "tabView", new Tab(canEdit, false));
            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_USER_STATUS_TASK_SECURITY);
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_USER_STATUS_PROPERTIES));
            return mapping.findForward("taskSecurityJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            RoleTaskSecurityForm pf = (RoleTaskSecurityForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().userHeader(pf, sc, request);
            if (!sc.canAction(Action.manageRoles, id))
                return null;

            String prstatusId = pf.getPrstatusId();

            List<String> allowed = new ArrayList<String>();
            List<String> denied = new ArrayList<String>();
            if (pf.isManageRegistrations()) allowed.add(Action.manageRegistrations.toString());
            else denied.add(Action.manageRegistrations.toString());
            if (pf.isCutCopyPasteTask()) allowed.add(Action.cutCopyPasteTask.toString());
            else denied.add(Action.cutCopyPasteTask.toString());
            if (pf.isBulkProcessingTask()) allowed.add(Action.bulkProcessingTask.toString());
            else denied.add(Action.bulkProcessingTask.toString());

            if (pf.isViewTaskAttachments()) allowed.add(Action.viewTaskAttachments.toString());
            else denied.add(Action.viewTaskAttachments.toString());
            if (pf.isCreateTaskAttachments()) allowed.add(Action.createTaskAttachments.toString());
            else denied.add(Action.createTaskAttachments.toString());

            if (pf.isManageTaskAttachments()) {
                allowed.add(Action.manageTaskAttachments.toString());
            } else {
                denied.add(Action.manageTaskAttachments.toString());
            }
            if (pf.isDeleteTheirTaskAttachment() || pf.isManageTaskAttachments()) {
                allowed.add(Action.deleteTheirTaskAttachment.toString());
            } else {
                denied.add(Action.deleteTheirTaskAttachment.toString());
            }

            if (pf.isCreateTaskMessageAttachments()) allowed.add(Action.createTaskMessageAttachments.toString());
            else denied.add(Action.createTaskMessageAttachments.toString());

            if (pf.isManageTaskMessageAttachments()) allowed.add(Action.manageTaskMessageAttachments.toString());
            else denied.add(Action.manageTaskMessageAttachments.toString());

            if (pf.isDeleteTheirMessageAttachment() || pf.isManageTaskMessageAttachments()) {
                allowed.add(Action.deleteTheirMessageAttachment.toString());
            } else {
                denied.add(Action.deleteTheirMessageAttachment.toString());
            }

            if (pf.isViewFilters()) allowed.add(Action.viewFilters.toString());
            else denied.add(Action.viewFilters.toString());
            if (pf.isManageTaskPrivateFilters()) allowed.add(Action.manageTaskPrivateFilters.toString());
            else denied.add(Action.manageTaskPrivateFilters.toString());
            if (pf.isManageTaskPublicFilters()) allowed.add(Action.manageTaskPublicFilters.toString());
            else denied.add(Action.manageTaskPublicFilters.toString());

            if (pf.isViewReports()) allowed.add(Action.viewReports.toString());
            else denied.add(Action.viewReports.toString());
            if (pf.isManagePrivateReports()) allowed.add(Action.managePrivateReports.toString());
            else denied.add(Action.managePrivateReports.toString());
            if (pf.isManagePublicReports()) allowed.add(Action.managePublicReports.toString());
            else denied.add(Action.managePublicReports.toString());

            if (pf.isManageEmailSchedules()) allowed.add(Action.manageEmailSchedules.toString());
            else denied.add(Action.manageEmailSchedules.toString());

            if (pf.isManageTaskACLs()) allowed.add(Action.manageTaskACLs.toString());
            else denied.add(Action.manageTaskACLs.toString());
            if (pf.isManageTaskUDFs()) allowed.add(Action.manageTaskUDFs.toString());
            else denied.add(Action.manageTaskUDFs.toString());
            if (pf.isManageEmailImportRules()) allowed.add(Action.manageEmailImportRules.toString());
            else denied.add(Action.manageEmailImportRules.toString());
            if (pf.isManageTaskTemplates()) allowed.add(Action.manageTaskTemplates.toString());
            else denied.add(Action.manageTaskTemplates.toString());
            if (pf.isManageCategories()) allowed.add(Action.manageCategories.toString());
            else denied.add(Action.manageCategories.toString());
            if (pf.isManageWorkflows()) allowed.add(Action.manageWorkflows.toString());
            else denied.add(Action.manageWorkflows.toString());
            if (pf.isDeleteOperations()) allowed.add(Action.deleteOperations.toString());
            else denied.add(Action.deleteOperations.toString());

            if (pf.isViewScriptsBrowser()) {
                allowed.add(Action.viewScriptsBrowser.toString());
            } else {
                denied.add(Action.viewScriptsBrowser.toString());
            }

            if (pf.isViewTemplatesBrowser()) {
                allowed.add(Action.viewTemplatesBrowser.toString());
            } else {
                denied.add(Action.viewTemplatesBrowser.toString());
            }

            if (pf.isShowView()) {
                allowed.add(Action.showView.toString());
            } else {
                denied.add(Action.showView.toString());
            }

            if (pf.isShowOtherFilterTab()) {
                allowed.add(Action.showOtherFilterTab.toString());
            } else {
                denied.add(Action.showOtherFilterTab.toString());
            }

            if (pf.isCanCreateTaskByOperation()) {
                allowed.add(Action.canCreateTaskByOperation.toString());
            } else {
                denied.add(Action.canCreateTaskByOperation.toString());
            }

            if (pf.isCanUsePostFiltration()) {
                allowed.add(Action.canUsePostFiltration.toString());
            } else {
                denied.add(Action.canUsePostFiltration.toString());
            }

            if (pf.isCanArchive()) {
                allowed.add(Action.canArchive.toString());
            } else {
                denied.add(Action.canArchive.toString());
            }

            if (pf.isCanDeleteArchive()) {
                allowed.add(Action.canDeleteArchive.toString());
            } else {
                denied.add(Action.canDeleteArchive.toString());
            }

            AdapterManager.getInstance().getSecuredPrstatusAdapterManager().setRoles(sc, prstatusId, allowed, denied);

            return mapping.findForward("taskSecurityPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }


}
