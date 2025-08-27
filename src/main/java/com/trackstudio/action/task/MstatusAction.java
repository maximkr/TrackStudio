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

import com.trackstudio.action.GeneralAction;
import com.trackstudio.action.TSDispatchAction;
import com.trackstudio.app.PageNaming;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.constants.CategoryConstants;
import com.trackstudio.constants.WorkflowConstants;
import com.trackstudio.exception.UserException;
import com.trackstudio.form.MstatusForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.cache.TaskRelatedInfo;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.model.Prstatus;
import com.trackstudio.secured.SecuredMstatusBean;
import com.trackstudio.secured.SecuredPrstatusBean;
import com.trackstudio.secured.SecuredResolutionBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredTransitionBean;
import com.trackstudio.secured.SecuredWorkflowBean;
import com.trackstudio.secured.SecuredWorkflowUDFBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.EggBasket;
import com.trackstudio.tools.ExternalAdapterManagerUtil;
import com.trackstudio.tools.Tab;

public class MstatusAction extends TSDispatchAction {
    private static Log log = LogFactory.getLog(MstatusAction.class);

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            MstatusForm mf = (MstatusForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().taskHeader(mf, sc, request, true);
            SecuredTaskBean tci = new SecuredTaskBean(id, sc);
            String workflowId = mf.getWorkflowId() != null ? mf.getWorkflowId() : tci.getWorkflowId();
            SecuredWorkflowBean flow = AdapterManager.getInstance().getSecuredFindAdapterManager().findWorkflowById(sc, workflowId);

            boolean canManage = flow.canManage();

            ArrayList<SecuredMstatusBean> mstatusList = AdapterManager.getInstance().getSecuredWorkflowAdapterManager().getMstatusList(sc, flow.getId());
            Collections.sort(mstatusList);
            sc.setRequestAttribute(request, "mstatusList", mstatusList);
            sc.setRequestAttribute(request, "_can_modify", canManage);
            sc.setRequestAttribute(request, "canManage", canManage);
            sc.setRequestAttribute(request, "canCreateObject", canManage);
            if (canManage) {
                sc.setRequestAttribute(request, "msgHowCreateObject", I18n.getString(sc.getLocale(), "MESSAGE_ADD"));
                sc.setRequestAttribute(request, "msgAddObject", I18n.getString(sc.getLocale(), "MESSAGE_ADD"));
                sc.setRequestAttribute(request, "createObjectAction", "/MstatusAction.do");
            }

            Map<String, TreeSet<SecuredMstatusBean>> canViewCanEditMap = new HashMap<String, TreeSet<SecuredMstatusBean>>();
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

            sc.setRequestAttribute(request, "_can_delete", canManage);
            sc.setRequestAttribute(request, "canView", canManage);
            sc.setRequestAttribute(request, "flow", flow);
            sc.setRequestAttribute(request, "workflowId", flow.getId());
            sc.setRequestAttribute(request, "tabView", new Tab(canManage, false));
            sc.setRequestAttribute(request, "tabEdit", new Tab(canManage, false));
            sc.setRequestAttribute(request, "tabPriorities", new Tab(canManage, false));
            sc.setRequestAttribute(request, "tabStates", new Tab(canManage, false));
            sc.setRequestAttribute(request, "tabMessageTypes", new Tab(flow.canView() && sc.canAction(Action.manageWorkflows, flow.getTaskId()), true));
            sc.setRequestAttribute(request, "tabCustomize", new Tab(canManage, false));
            selectTaskTab(sc, id, "tabWorkflows", request);
            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_TASK_WORKFLOW_MSTATUS_LIST);
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_TASK_WORKFLOW_MSTATUS_LIST));
            return mapping.findForward("mstatusJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }


    public ActionForward delete(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");

            MstatusForm mf = (MstatusForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String[] values = mf.getDelete();
            if (values != null) {
                List<String[]> exceptions = new ArrayList<String[]>();
                for (String mstatusId : values) {
                    List<TaskRelatedInfo> taskRelatedInfos = AdapterManager.getInstance().getSecuredWorkflowAdapterManager().getMstatusTask(sc, mstatusId);
                    if (taskRelatedInfos.size() > 0) {
                        SecuredMstatusBean mstatusBean = AdapterManager.getInstance().getSecuredFindAdapterManager().findMstatusById(sc, mstatusId);
                        String exception = "";
                        for (TaskRelatedInfo info : taskRelatedInfos) {
                            exception += "#" + info.getNumber() + "; ";
                        }
                        exceptions.add(new String[]{mstatusBean.getName() + "(" + mstatusBean.getWorkflow().getName() + ")", exception});
                        continue;
                    }
                    AdapterManager.getInstance().getSecuredWorkflowAdapterManager().deleteMstatus(sc, mstatusId);
                }
                if (exceptions.size() > 0) {
                    UserException mstatusException = new UserException(I18n.getString("ERROR_CAN_NOT_DELETE_MTYPE"), false);
                    for (String[] message : exceptions) {
                        UserException ue = new UserException(I18n.getString("MSTATUS_EXCEPTION_CONSTRAINT", new Object[]{message[0], message[1]}), false);
                        mstatusException.addActionMessages(ue.getActionMessages());
                    }
                    throw mstatusException;
                }
            }
            return mapping.findForward("mstatusPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                HttpServletResponse response) throws Exception {
        log.trace("##########");
        return mapping.findForward("mstatusEditPage");
    }

    public ActionForward clone(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");

            MstatusForm mf = (MstatusForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String[] values = mf.getDelete();
            if (values != null) {
                for (String id : values) {
                    SecuredMstatusBean oldMstatus = AdapterManager.getInstance().getSecuredFindAdapterManager().findMstatusById(sc, id);
                    String mstatusId = AdapterManager.getInstance().getSecuredWorkflowAdapterManager().createMstatus(sc, oldMstatus.getWorkflowId(), oldMstatus.getName() + "_clone", oldMstatus.getDescription(), oldMstatus.getPreferences());

                    List<SecuredResolutionBean> res = AdapterManager.getInstance().getSecuredWorkflowAdapterManager().getResolutionList(sc, oldMstatus.getId());
                    for (SecuredResolutionBean srb : res) {
                        AdapterManager.getInstance().getSecuredWorkflowAdapterManager().createResolution(sc, mstatusId, srb.getName(), srb.getIsDefault());
                    }

                    List<SecuredTransitionBean> transitionList = AdapterManager.getInstance().getSecuredWorkflowAdapterManager().getTransitionList(sc, oldMstatus.getId());
                    for (SecuredTransitionBean stb : transitionList) {
                        AdapterManager.getInstance().getSecuredWorkflowAdapterManager().updateTransition(sc, mstatusId, stb.getStartId(), stb.getFinishId());
                    }

                    AdapterManager.getInstance().getSecuredWorkflowAdapterManager().setMstatusTrigger(sc, mstatusId, oldMstatus.getBefore(), oldMstatus.getInsteadOf(), oldMstatus.getAfter());
                    EggBasket<String, String> rules = ExternalAdapterManagerUtil.getMprstatusMap(oldMstatus.getId());
                    for (Prstatus prstatus : KernelManager.getPrstatus().getPrstatusList()) {
                        List<String> types = rules.get(prstatus.getId());
                        String view = null;
                        String process = null;
                        String handler = null;
                        if (types != null) {
                            if (types.contains(WorkflowConstants.VIEW_ALL)) {
                                view = WorkflowConstants.VIEW_ALL;
                            } else if (types.contains(WorkflowConstants.VIEW_SUBMITTER)) {
                                view = WorkflowConstants.VIEW_SUBMITTER;
                            } else if (types.contains(WorkflowConstants.VIEW_HANDLER)) {
                                view = WorkflowConstants.VIEW_HANDLER;
                            } else if (types.contains(WorkflowConstants.VIEW_SUBMITTER_AND_HANDLER)) {
                                view = WorkflowConstants.VIEW_SUBMITTER_AND_HANDLER;
                            }


                            if (types.contains(WorkflowConstants.PROCESS_ALL)) {
                                process = WorkflowConstants.PROCESS_ALL;
                            } else if (types.contains(WorkflowConstants.PROCESS_SUBMITTER)) {
                                process = WorkflowConstants.PROCESS_SUBMITTER;
                            } else if (types.contains(WorkflowConstants.PROCESS_HANDLER)) {
                                process = WorkflowConstants.PROCESS_HANDLER;
                            } else if (types.contains(WorkflowConstants.PROCESS_SUBMITTER_AND_HANDLER)) {
                                process = WorkflowConstants.PROCESS_SUBMITTER_AND_HANDLER;
                            }

                            if (types.contains(WorkflowConstants.BE_HANDLER_ALL)) {
                                handler = WorkflowConstants.BE_HANDLER_ALL;
                            } else if (types.contains(WorkflowConstants.BE_HANDLER_SUBMITTER)) {
                                handler = WorkflowConstants.BE_HANDLER_SUBMITTER;
                            } else if (types.contains(WorkflowConstants.BE_HANDLER_HANDLER)) {
                                handler = WorkflowConstants.BE_HANDLER_HANDLER;
                            } else if (types.contains(WorkflowConstants.BE_HANDLER_SUBMITTER_AND_HANDLER)) {
                                handler = WorkflowConstants.BE_HANDLER_SUBMITTER_AND_HANDLER;
                            }

                        }
                        KernelManager.getWorkflow().removeBeMstatusByPrstatus(prstatus.getId(), mstatusId);
                        if (view != null) {
                            AdapterManager.getInstance().getSecuredWorkflowAdapterManager().grantView(sc, view, prstatus.getId(), mstatusId);
                        }
                        if (process != null) {
                            AdapterManager.getInstance().getSecuredWorkflowAdapterManager().grantProcess(sc, process, prstatus.getId(), mstatusId);
                        }
                        if (handler != null) {
                            AdapterManager.getInstance().getSecuredWorkflowAdapterManager().grantBeHandler(sc, handler, prstatus.getId(), mstatusId);
                        }
                    }
                    List<SecuredWorkflowUDFBean> udfs = oldMstatus.getWorkflow().getWorkflowUDFs();
                    List<String> editable = KernelManager.getUdf().getEditableUDFId(oldMstatus.getId());
                    List<String> viewable = KernelManager.getUdf().getViewableUDFId(oldMstatus.getId());
                    for (SecuredWorkflowUDFBean bean : udfs) {
                        String view = null;
                        if (viewable.contains(bean.getId())) {
                            view = CategoryConstants.VIEW_ALL;
                        }
                        String edit = null;
                        if (editable.contains(bean.getId())) {
                            edit = CategoryConstants.EDIT_ALL;
                        }
                        AdapterManager.getInstance().getSecuredUDFAdapterManager().setMstatusUDFRule(sc, bean.getId(), mstatusId, view, edit);
                    }
                }
            }
            return mapping.findForward("mstatusPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

}
