package com.trackstudio.action.task;

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
import com.trackstudio.app.Defaults;
import com.trackstudio.app.PageNaming;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.form.PriorityForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.secured.SecuredPriorityBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredWorkflowBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.Tab;

public class PriorityAction extends TSDispatchAction {
    private static Log log = LogFactory.getLog(PriorityAction.class);

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            PriorityForm pf = (PriorityForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().taskHeader(pf, sc, request, true);
            if (!sc.canAction(Action.manageWorkflows, id))
                return null;
            SecuredTaskBean tci = new SecuredTaskBean(id, sc);
            String workflowId = pf.getWorkflowId() != null ? pf.getWorkflowId() : tci.getWorkflowId();
            SecuredWorkflowBean workflow = AdapterManager.getInstance().getSecuredFindAdapterManager().findWorkflowById(sc, workflowId);
            boolean canManage = workflow.canManage();
            List<SecuredPriorityBean> priorityList = AdapterManager.getInstance().getSecuredWorkflowAdapterManager().getPriorityList(sc, workflow.getId());
            SecuredPriorityBean defaultPriority = Defaults.getPriority(sc, workflow.getId());


            for (SecuredPriorityBean spb : priorityList) {
                if (spb.isDef()) pf.setDefaultForRadioButton(spb.getId());

            }
            Collections.sort(priorityList);

            boolean showOrder = priorityList.size() > 1;

            sc.setRequestAttribute(request, "priorityList", priorityList);
            sc.setRequestAttribute(request, "defaultPriority", defaultPriority);
            sc.setRequestAttribute(request, "flow", workflow);
            boolean canCreate = canManage;
            boolean canDelete = canManage;

            sc.setRequestAttribute(request, "createObjectAction", "/PriorityAction.do");
            sc.setRequestAttribute(request, "showOrder", showOrder);
            sc.setRequestAttribute(request, "canCreate", canCreate);
            sc.setRequestAttribute(request, "canDelete", canDelete);
            sc.setRequestAttribute(request, "canManage", canManage);
            sc.setRequestAttribute(request, "tabView", new Tab(canManage, false));
            sc.setRequestAttribute(request, "tabEdit", new Tab(canManage, false));
            sc.setRequestAttribute(request, "tabPriorities", new Tab(canManage, true));
            sc.setRequestAttribute(request, "tabStates", new Tab(canManage, false));
            sc.setRequestAttribute(request, "tabMessageTypes", new Tab(canManage, false));
            sc.setRequestAttribute(request, "tabCustomize", new Tab(canManage, false));
            selectTaskTab(sc, id, "tabWorkflows", request);
            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_TASK_WORKFLOW_PRIORITY_LIST);
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_TASK_WORKFLOW_PRIORITY_LIST));
            return mapping.findForward("priorityJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                HttpServletResponse response) throws Exception {
        log.trace("##########");
        return mapping.findForward("priorityEditPage");
    }

    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            PriorityForm pf = (PriorityForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");

            String defaultPriorityId = pf.getDefaultForRadioButton();

            List<SecuredPriorityBean> priorityList = AdapterManager.getInstance().getSecuredWorkflowAdapterManager().getPriorityList(sc, pf.getWorkflowId());

            for (SecuredPriorityBean spb : priorityList) {
                boolean def = false;
                if (defaultPriorityId != null && defaultPriorityId.equals(spb.getId())) {
                    def = true;
                }
                String priorityOrder = (String) pf.getValue("priority-" + spb.getId());
                AdapterManager.getInstance().getSecuredWorkflowAdapterManager().updatePriority(sc, spb.getId(), spb.getName(), spb.getDescription(), Integer.parseInt(priorityOrder), def);
            }

            return mapping.findForward("priorityPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward delete(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            PriorityForm pf = (PriorityForm) form;
            SessionContext sc = GeneralAction.getInstance().imports(request, response);

            if (sc == null) return null;

            String[] values = pf.getDelete();
            if (values != null) {
                for (String value : values)
                    AdapterManager.getInstance().getSecuredWorkflowAdapterManager().deletePriority(sc, value);
            }
            return mapping.findForward("priorityPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward clone(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            PriorityForm pf = (PriorityForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            if (sc == null) {
                return null;
            }
            String[] values = pf.getDelete();
            if (values != null) {
                for (String priorityId : values) {
                    SecuredPriorityBean priority = AdapterManager.getInstance().getSecuredFindAdapterManager().findPriorityById(sc, priorityId);
                    AdapterManager.getInstance().getSecuredWorkflowAdapterManager().createPriority(sc, priority.getName() + "_clone", priority.getDescription(), priority.getOrder(), false, pf.getWorkflowId());
                }
            }
            log.debug("CLOSE SESSION PriorityAction");
            //        HibernateSession.closeSession();
            return mapping.findForward("priorityPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }
}
