package com.trackstudio.action.user;

import java.util.ArrayList;
import java.util.Collections;
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
import com.trackstudio.action.task.items.WorkflowListItem;
import com.trackstudio.app.PageNaming;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.constants.WorkflowConstants;
import com.trackstudio.form.PrstatusForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.cache.ActionCacheManager;
import com.trackstudio.secured.SecuredCategoryBean;
import com.trackstudio.secured.SecuredMstatusBean;
import com.trackstudio.secured.SecuredPrstatusBean;
import com.trackstudio.secured.SecuredWorkflowBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.EggBasket;
import com.trackstudio.tools.ExternalAdapterManagerUtil;
import com.trackstudio.tools.Tab;
import com.trackstudio.view.UserViewHTMLLinked;

public class UserWorkflowSecurityAction extends TSDispatchAction {
    private static Log log = LogFactory.getLog(UserWorkflowSecurityAction.class);

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            PrstatusForm pf = (PrstatusForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().userHeader(pf, sc, request);
            if (!sc.canAction(Action.manageRoles, id))
                return null;
            String prstatusId = pf.getPrstatusId();
            SecuredPrstatusBean prstatus = AdapterManager.getInstance().getSecuredFindAdapterManager().findPrstatusById(sc, prstatusId);
            List<Action> choosedCurrent = ActionCacheManager.getInstance().getActions(sc.getPrstatusId());
            ArrayList<SecuredWorkflowBean> availableWorflowList = AdapterManager.getInstance().getSecuredWorkflowAdapterManager().getAllWorkflowListForUser(sc, prstatusId);
            List<WorkflowListItem> workflows = new ArrayList<WorkflowListItem>();
            for (SecuredWorkflowBean pr : availableWorflowList) {
                ArrayList<SecuredMstatusBean> processAll = new ArrayList<SecuredMstatusBean>();
                ArrayList<SecuredMstatusBean> processSubmitter = new ArrayList<SecuredMstatusBean>();
                ArrayList<SecuredMstatusBean> processHandler = new ArrayList<SecuredMstatusBean>();
                ArrayList<SecuredMstatusBean> processSAH = new ArrayList<SecuredMstatusBean>();

                WorkflowListItem li = new WorkflowListItem(pr.getId(), pr.getName());
                li.setCanManage(pr.canManage());
                ArrayList<SecuredCategoryBean> categories = AdapterManager.getInstance().getSecuredWorkflowAdapterManager().getCategoryList(sc, pr.getId());
                li.setCategories(categories);

                ArrayList<SecuredMstatusBean> mstatusList = AdapterManager.getInstance().getSecuredWorkflowAdapterManager().getMstatusList(sc, pr.getId());
                for (SecuredMstatusBean mstatus : mstatusList) {

                    EggBasket<String, String> rules = ExternalAdapterManagerUtil.getMprstatusMap(mstatus.getId());
                    List<String> types = rules.get(prstatusId);
                    if (types != null) {
                        if (types.contains(WorkflowConstants.PROCESS_ALL))
                            processAll.add(mstatus);
                        else if (types.contains(WorkflowConstants.PROCESS_SUBMITTER))
                            processSubmitter.add(mstatus);
                        else if (types.contains(WorkflowConstants.PROCESS_HANDLER))
                            processHandler.add(mstatus);
                        else if (types.contains(WorkflowConstants.PROCESS_SUBMITTER_AND_HANDLER))
                            processSAH.add(mstatus);
                    }
                }

                li.setProcessAll(processAll);
                li.setProcessHandler(processHandler);
                li.setProcessSubmitter(processSubmitter);
                li.setProcessSAH(processSAH);
                workflows.add(li);
            }
            Collections.sort(workflows);
            boolean editable = sc.canAction(Action.manageRoles, prstatus.getUserId()) && prstatus.canManage();


            sc.setRequestAttribute(request, "workflowList", workflows);
            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_USER_STATUS_WORKFLOW_LIST);

            sc.setRequestAttribute(request, "tabStatuses", new Tab(true, true));
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_USER_STATUS_WORKFLOW_LIST));
            sc.setRequestAttribute(request, "helpTile", "HELP_TILE_USER_STATUS_WORKFLOW_LIST");

            sc.setRequestAttribute(request, "owner", new UserViewHTMLLinked(prstatus.getUser(), request.getContextPath()).getPath());
            sc.setRequestAttribute(request, "currentPrstatus", prstatus);
            sc.setRequestAttribute(request, "name", prstatus.getName());
            sc.setRequestAttribute(request, "prstatusId", prstatusId);

            sc.setRequestAttribute(request, "canView", editable);
            sc.setRequestAttribute(request, "tabEdit", new Tab(editable, false));
            sc.setRequestAttribute(request, "tabTaskFieldSecurity", new Tab(true, false));
            sc.setRequestAttribute(request, "tabUserFieldSecurity", new Tab(true, false));
            sc.setRequestAttribute(request, "tabUserSecurity", new Tab(editable, false));
            sc.setRequestAttribute(request, "tabTaskSecurity", new Tab(editable, false));
            sc.setRequestAttribute(request, "tabCategorySecurity", new Tab(choosedCurrent.contains(Action.manageCategories), false));
            sc.setRequestAttribute(request, "tabWorkflowSecurity", new Tab(choosedCurrent.contains(Action.manageWorkflows), true));
            sc.setRequestAttribute(request, "tabView", new Tab(editable, false));

            return mapping.findForward("userWorkflowSecurityJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }
}
