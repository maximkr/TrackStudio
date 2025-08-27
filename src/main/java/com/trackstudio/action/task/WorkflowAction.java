package com.trackstudio.action.task;

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
import com.trackstudio.app.PageNaming;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.UserException;
import com.trackstudio.form.WorkflowForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.cache.TaskRelatedInfo;
import com.trackstudio.secured.SecuredMstatusBean;
import com.trackstudio.secured.SecuredStatusBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredWorkflowBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.EggBasket;
import com.trackstudio.tools.Tab;

public class WorkflowAction extends TSDispatchAction {
    private static Log log = LogFactory.getLog(WorkflowAction.class);

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            WorkflowForm tf = (WorkflowForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().taskHeader(tf, sc, request, true);
            if (!sc.canAction(Action.manageWorkflows, id) && !sc.canAction(Action.manageCategories, id))
                return null;
            SecuredTaskBean tci = new SecuredTaskBean(id, sc);

            ArrayList<SecuredWorkflowBean> workflowSet = AdapterManager.getInstance().getSecuredWorkflowAdapterManager().getAllAvailableWorkflowList(sc, id);
            ArrayList<SecuredWorkflowBean> workflowList = new ArrayList<SecuredWorkflowBean>();
            EggBasket<SecuredTaskBean, SecuredWorkflowBean> parentWorkflowSet = new EggBasket<SecuredTaskBean, SecuredWorkflowBean>();
            EggBasket<SecuredTaskBean, SecuredWorkflowBean> childrenWorkflowSet = new EggBasket<SecuredTaskBean, SecuredWorkflowBean>();
            String currentWorkflowId = tf.getWorkflowId() == null ? tci.getWorkflowId() : tf.getWorkflowId();
            ArrayList<EggBasket> seeAlso = new ArrayList<EggBasket>();
            ArrayList<SecuredTaskBean> parentTasks = AdapterManager.getInstance().getSecuredTaskAdapterManager().getTaskChain(sc, null, id);
            for (SecuredWorkflowBean swb : workflowSet) {
                SecuredTaskBean task = swb.getTask();
                if (task.canView() && sc.canAction(Action.manageWorkflows, task.getId())) {
                    if (swb.getTaskId().equals(id)) {
                        workflowList.add(swb);
                    } else {

                        if (parentTasks.contains(task)) {
                            parentWorkflowSet.putItem(task, swb);
                        } else {
                            childrenWorkflowSet.putItem(task, swb);
                        }
                    }
                }
            }
            Collections.sort(workflowList);
            String workflowId = tf.getWorkflowId() != null ? tf.getWorkflowId() : tci.getWorkflowId();
            SecuredWorkflowBean flow = AdapterManager.getInstance().getSecuredFindAdapterManager().findWorkflowById(sc, workflowId);

            sc.setRequestAttribute(request, "tabWorkflows", new Tab(true, true));
            sc.setRequestAttribute(request, "workflowSet", workflowList);

            if (!parentWorkflowSet.isEmpty())
                seeAlso.add(parentWorkflowSet);
            if (!childrenWorkflowSet.isEmpty())
                seeAlso.add(childrenWorkflowSet);

            sc.setRequestAttribute(request, "currentWorkflowId", currentWorkflowId);
            sc.setRequestAttribute(request, "seeAlso", seeAlso);
            sc.setRequestAttribute(request, "flow", flow);
            sc.setRequestAttribute(request, "canCopy", sc.canAction(Action.manageWorkflows, id) && sc.allowedByACL(id));
            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_TASK_WORKFLOW_LIST);
            selectTaskTab(sc, id, "tabWorkflows", request);
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_TASK_WORKFLOW_PROPERTIES));


            boolean canCreate = sc.canAction(Action.manageWorkflows, id) && sc.allowedByACL(id);
            sc.setRequestAttribute(request, "canManage", canCreate);

            if (canCreate) {
                sc.setRequestAttribute(request, "msgHowCreateObject", I18n.getString(sc.getLocale(), "WORKFLOW_ADD"));
                sc.setRequestAttribute(request, "msgAddObject", I18n.getString(sc.getLocale(), "WORKFLOW_ADD"));
                sc.setRequestAttribute(request, "createObjectAction", "/WorkflowAction.do");
            }
            return mapping.findForward("workflowJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward clone(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                               HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            WorkflowForm bf = (WorkflowForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String[] selected = bf.getDelete();
            if (selected != null)
                for (String aSelected : selected)
                    AdapterManager.getInstance().getSecuredWorkflowAdapterManager().cloneWorkflow(sc, aSelected, bf.getId());
            return mapping.findForward("workflowPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward delete(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            WorkflowForm wf = (WorkflowForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String workflowId = wf.getWorkflowId();
            String[] values = wf.getDelete();
            if (values != null) {
                for (String value : values) {
                    if (value.equals(workflowId)) {
                        workflowId = null;
                        break;
                    }
                }
            }
            if (values != null) {
                List<String> exceptions = new ArrayList<String>();
                for (String wId : values) {
                    if (AdapterManager.getInstance().getSecuredFindAdapterManager().findWorkflowById(sc, wId).canManage()) {
                        boolean canDelete = true;
                        List<SecuredStatusBean> statusList = AdapterManager.getInstance().getSecuredWorkflowAdapterManager().getStateList(sc, wId);
                        for (SecuredStatusBean status : statusList) {
                            List<TaskRelatedInfo> taskRelatedInfos = AdapterManager.getInstance().getSecuredWorkflowAdapterManager().getStateTask(sc, status.getId());
                            if (taskRelatedInfos.size() > 0) {
                                SecuredStatusBean securedStatusBean = AdapterManager.getInstance().getSecuredFindAdapterManager().findStatusById(sc, status.getId());
                                String exception = "";
                                for (TaskRelatedInfo info : taskRelatedInfos) {
                                    exception += "#" + info.getNumber() + "; ";
                                }
                                exceptions.add(I18n.getString("STATUS_EXCEPTION_CONSTRAINT", new Object[]{securedStatusBean.getName() + "(" + securedStatusBean.getWorkflow().getName() + ")", exception}));
                                canDelete = false;
                            }
                        }
                        List<SecuredMstatusBean> mstatusList = AdapterManager.getInstance().getSecuredWorkflowAdapterManager().getMstatusList(sc, wId);
                        for (SecuredMstatusBean mstatus : mstatusList) {
                            List<TaskRelatedInfo> taskRelatedInfos = AdapterManager.getInstance().getSecuredWorkflowAdapterManager().getMstatusTask(sc, mstatus.getId());
                            if (taskRelatedInfos.size() > 0) {
                                SecuredMstatusBean mstatusBean = AdapterManager.getInstance().getSecuredFindAdapterManager().findMstatusById(sc, mstatus.getId());
                                String exception = "";
                                for (TaskRelatedInfo info : taskRelatedInfos) {
                                    exception += "#" + info.getNumber() + "; ";
                                }
                                exceptions.add(I18n.getString("MSTATUS_EXCEPTION_CONSTRAINT", new Object[]{mstatusBean.getName() + "(" + mstatusBean.getWorkflow().getName() + ")", exception}));
                                canDelete = false;
                            }
                        }
                        if (canDelete) {
                            AdapterManager.getInstance().getSecuredWorkflowAdapterManager().deleteWorkflow(sc, wId);
                        }
                    }
                }
                if (exceptions.size() > 0) {
                    UserException workflowException = new UserException(I18n.getString("ERROR_CAN_NOT_DELETE_WORKFLOW"), false);
                    for (String message : exceptions) {
                        UserException ue = new UserException(message, false);
                        workflowException.addActionMessages(ue.getActionMessages());
                    }
                    throw workflowException;
                }
            }
            wf.setWorkflowId(workflowId);
            wf.setMutable(false);
            return mapping.findForward("workflowPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                HttpServletResponse response) throws Exception {
        log.trace("##########");
        return mapping.findForward("workflowEditPage");
    }
}
