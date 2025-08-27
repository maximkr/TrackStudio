package com.trackstudio.action.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.trackstudio.action.CustomEditAction;
import com.trackstudio.action.GeneralAction;
import com.trackstudio.action.TSDispatchAction;
import com.trackstudio.app.PageNaming;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.constants.WorkflowConstants;
import com.trackstudio.form.WorkflowForm;
import com.trackstudio.kernel.cache.AbstractPluginCacheItem;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.cache.PluginCacheManager;
import com.trackstudio.kernel.cache.PluginType;
import com.trackstudio.secured.SecuredMstatusBean;
import com.trackstudio.secured.SecuredPriorityBean;
import com.trackstudio.secured.SecuredPrstatusBean;
import com.trackstudio.secured.SecuredWorkflowBean;
import com.trackstudio.secured.SecuredWorkflowUDFBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.EggBasket;
import com.trackstudio.tools.ExternalAdapterManagerUtil;
import com.trackstudio.tools.Tab;
import com.trackstudio.view.TaskViewHTMLShort;

public class WorkflowViewAction extends TSDispatchAction {

    private static Log log = LogFactory.getLog(WorkflowEditAction.class);

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");

            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().taskHeader(null, sc, request, true);
            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_TASK_WORKFLOW_OVERVIEW);
            if (!sc.canAction(Action.manageWorkflows, id) && !sc.canAction(Action.manageCategories, id))
                return null;
            WorkflowForm wf = (WorkflowForm) form;

            String wfId = wf.getWorkflowId() != null ? wf.getWorkflowId() : request.getParameter("workflowId");

            SecuredWorkflowBean flow = AdapterManager.getInstance().getSecuredFindAdapterManager().findWorkflowById(sc, wfId);
            boolean canView = sc.canAction(Action.manageWorkflows, flow.getTaskId());
            if (canView) {
                ArrayList<SecuredPriorityBean> priorityList = AdapterManager.getInstance().getSecuredWorkflowAdapterManager().getPriorityList(sc, flow.getId());


                Collections.sort(priorityList);
                sc.setRequestAttribute(request, "priorities", priorityList);

                ArrayList statusSet = AdapterManager.getInstance().getSecuredWorkflowAdapterManager().getStateList(sc, flow.getId());
                Collections.sort(statusSet);

                sc.setRequestAttribute(request, "states", statusSet);

                ArrayList mstatusList = AdapterManager.getInstance().getSecuredWorkflowAdapterManager().getMstatusList(sc, flow.getId());
                Collections.sort(mstatusList);

                sc.setRequestAttribute(request, "mstatuses", mstatusList);

                List<SecuredWorkflowUDFBean> udfs = flow.getWorkflowUDFs();

                sc.setRequestAttribute(request, "action", "/WorkflowUDFSaveAction.do");
                sc.setRequestAttribute(request, "createObjectAction", "/WorkflowUDFSaveAction.do");
                sc.setRequestAttribute(request, "_can_view", canView);
                boolean _can_modify = false;
                sc.setRequestAttribute(request, "_can_modify", _can_modify);
                boolean _can_delete = false;
                sc.setRequestAttribute(request, "_can_delete", _can_delete);
                boolean _can_create = false;
                sc.setRequestAttribute(request, "_can_create", _can_create);
                Map<PluginType, List<AbstractPluginCacheItem>> scripts = PluginCacheManager.getInstance().list(
                        PluginType.TASK_CUSTOM_FIELD_VALUE, PluginType.TASK_CUSTOM_FIELD_LOOKUP
                        );
                List<AbstractPluginCacheItem> scriptCollection = scripts.get(PluginType.TASK_CUSTOM_FIELD_VALUE);
                sc.setRequestAttribute(request, "scriptCollection", scriptCollection);

                List<AbstractPluginCacheItem> lookupscriptCollection = scripts.get(PluginType.TASK_CUSTOM_FIELD_LOOKUP);
                sc.setRequestAttribute(request, "lookupscriptCollection", lookupscriptCollection);

                new CustomEditAction().fillForm(wfId, sc, udfs, request);
            }

            boolean hasStart = flow.hasStart();
            sc.setRequestAttribute(request, "hasStart", hasStart);
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
            boolean isValid = hasStart && isValidPermissions;
            sc.setRequestAttribute(request, "isValid", isValid);

            sc.setRequestAttribute(request, "hideUDFIcon", Boolean.TRUE);
            sc.setRequestAttribute(request, "canEdit", flow.canManage());
            sc.setRequestAttribute(request, "connected", new TaskViewHTMLShort(flow.getTask(), request.getContextPath()).getView(flow.getTask()).getName());
            sc.setRequestAttribute(request, "workflowId", wfId);
            sc.setRequestAttribute(request, "flow", flow);
            sc.setRequestAttribute(request, "isViewWF", Boolean.TRUE);
            sc.setRequestAttribute(request, "viewPriority", canView);
            sc.setRequestAttribute(request, "viewMstatus", canView);
            sc.setRequestAttribute(request, "viewState", canView);
            sc.setRequestAttribute(request, "viewUdfAction", "/WorkflowUdfViewAction.do");
            sc.setRequestAttribute(request, "editUdfAction", "/WorkflowUdfEditAction.do");

            sc.setRequestAttribute(request, "tabView", new Tab(flow.canManage() || sc.canAction(Action.manageCategories, id), true));

            sc.setRequestAttribute(request, "tabEdit", new Tab(flow.canManage(), false));
            sc.setRequestAttribute(request, "tabPriorities", new Tab(flow.canManage(), false));
            sc.setRequestAttribute(request, "tabStates", new Tab(flow.canManage(), false));
            sc.setRequestAttribute(request, "tabMessageTypes", new Tab(flow.canManage(), false));
            sc.setRequestAttribute(request, "tabCustomize", new Tab(flow.canManage(), false));

            selectTaskTab(sc, id, "tabWorkflows", request);

            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_TASK_WORKFLOW_OVERVIEW);
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_TASK_WORKFLOW_OVERVIEW));

            return mapping.findForward("workflowViewJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }
}
