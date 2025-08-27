package com.trackstudio.action.task;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.trackstudio.action.GeneralAction;
import com.trackstudio.action.TSDispatchAction;
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

public class PriorityEditAction extends TSDispatchAction {

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            PriorityForm pf = (PriorityForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().taskHeader(pf, sc, request, true);

            if (!sc.canAction(Action.manageWorkflows, id)) return null;

            String priorityId = request.getParameter("priority");
            boolean createNewPriority = priorityId == null || priorityId.length() == 0;

            SecuredTaskBean tci = new SecuredTaskBean(id, sc);
            String workflowId = pf.getWorkflowId() != null ? pf.getWorkflowId() : tci.getWorkflowId();
            SecuredWorkflowBean flow = AdapterManager.getInstance().getSecuredFindAdapterManager().findWorkflowById(sc, workflowId);
            boolean canManage = flow.canManage();
            sc.setRequestAttribute(request, "flow", flow);

            List<SecuredPriorityBean> priorityList = AdapterManager.getInstance().getSecuredWorkflowAdapterManager().getPriorityList(sc, flow.getId());

            boolean def = false;
            if (!createNewPriority) {
                SecuredPriorityBean priority = AdapterManager.getInstance().getSecuredFindAdapterManager().findPriorityById(sc, priorityId);
                def = priority.isDef();
                pf.setPriorityId(priorityId);
                pf.setName(priority.getName());
                pf.setDescription(priority.getDescription());
                pf.setOrder(priority.getOrder().toString());
                pf.setDef(priority.isDef());
            }

            sc.setRequestAttribute(request, "newlist", !createNewPriority);
            sc.setRequestAttribute(request, "checked", priorityList.isEmpty() || def);


            boolean canView = canManage;
            boolean canCreate = canManage;

            sc.setRequestAttribute(request, "canView", canView);
            sc.setRequestAttribute(request, "canCreate", canCreate);
            sc.setRequestAttribute(request, "canManage", canManage);
            sc.setRequestAttribute(request, "tabView", new Tab(canManage, false));
            sc.setRequestAttribute(request, "tabEdit", new Tab(canManage, false));
            sc.setRequestAttribute(request, "tabPriorities", new Tab(canManage, true));
            sc.setRequestAttribute(request, "tabStates", new Tab(canManage, false));
            sc.setRequestAttribute(request, "tabMessageTypes", new Tab(canManage, false));
            sc.setRequestAttribute(request, "tabCustomize", new Tab(canManage, false));
            selectTaskTab(sc, id, "tabWorkflows", request);
            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_TASK_WORKFLOW_PRIORITY_LIST);
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_TASK_WORKFLOW_PRIORITY_PROPERTIES));
            return mapping.findForward("priorityEditJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            PriorityForm pf = (PriorityForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String priorityName = pf.getName();
            String priorityDescription = pf.getDescription();
            String priorityId = pf.getPriorityId();
            boolean priorityDefault = pf.getDef();
            String workflowId = pf.getWorkflowId();
            int priorityOrder = 1;
            try {
                priorityOrder = Integer.parseInt(pf.getOrder());
            } catch (Exception e) {
                log.debug("Can't parse order parameter");
            }
            for (String name : priorityName.split("\n")) {
                if (name != null && !name.isEmpty()) {
                    if (name.endsWith("\r")) {
                        name = name.substring(0, name.lastIndexOf("\r"));
                    }
                    if (priorityDefault) {
                        List<SecuredPriorityBean> priorityList = AdapterManager.getInstance().getSecuredWorkflowAdapterManager().getPriorityList(sc, workflowId);
                        for (SecuredPriorityBean spb : priorityList) {
                            AdapterManager.getInstance().getSecuredWorkflowAdapterManager().updatePriority(sc, spb.getId(), spb.getName(), spb.getDescription(), spb.getOrder(), false);
                        }
                    }
                    if (priorityId != null && priorityId.length() > 0) {
                        AdapterManager.getInstance().getSecuredWorkflowAdapterManager().updatePriority(sc, priorityId, name, priorityDescription, priorityOrder, priorityDefault);
                    } else {
                        AdapterManager.getInstance().getSecuredWorkflowAdapterManager().createPriority(sc, name, priorityDescription, priorityOrder, priorityDefault, pf.getWorkflowId());
                    }
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
