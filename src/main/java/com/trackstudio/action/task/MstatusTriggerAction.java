package com.trackstudio.action.task;

import java.util.List;
import java.util.Map;

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
import com.trackstudio.form.TriggerForm;
import com.trackstudio.kernel.cache.AbstractPluginCacheItem;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.cache.PluginCacheManager;
import com.trackstudio.kernel.cache.PluginType;
import com.trackstudio.secured.SecuredMstatusBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredWorkflowBean;
import com.trackstudio.secured.SecuredWorkflowUDFBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.Tab;

public class MstatusTriggerAction extends TSDispatchAction {
    private static Log log = LogFactory.getLog(TransitionAction.class);

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            TriggerForm tf = (TriggerForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().taskHeader(tf, sc, request, true);
            if (!sc.canAction(Action.manageWorkflows, id))
                return null;
            SecuredTaskBean tci = new SecuredTaskBean(id, sc);
            String mstatusId = tf.getMstatusId();
            SecuredMstatusBean mstatus = AdapterManager.getInstance().getSecuredFindAdapterManager().findMstatusById(sc, mstatusId);
            String workflowId = tf.getWorkflowId() != null ? tf.getWorkflowId() : tci.getWorkflowId();
            SecuredWorkflowBean flow = AdapterManager.getInstance().getSecuredFindAdapterManager().findWorkflowById(sc, workflowId);
            boolean canManage = flow.canManage();
            List<SecuredWorkflowUDFBean> udfs = flow.getWorkflowUDFs();
            tf.setAfter(mstatus.getAfter() != null ? mstatus.getAfter() : "");
            tf.setBefore(mstatus.getBefore() != null ? mstatus.getBefore() : "");
            tf.setInsteadOf(mstatus.getInsteadOf() != null ? mstatus.getInsteadOf() : "");

            Map<PluginType, List<AbstractPluginCacheItem>> scripts = PluginCacheManager.getInstance().list(
                    PluginType.AFTER_ADD_MESSAGE, PluginType.BEFORE_ADD_MESSAGE,PluginType.INSTEAD_OF_ADD_MESSAGE
            );
            List<AbstractPluginCacheItem> afterScripts = scripts.get(PluginType.AFTER_ADD_MESSAGE);
            List<AbstractPluginCacheItem> beforeScripts = scripts.get(PluginType.BEFORE_ADD_MESSAGE);
            List<AbstractPluginCacheItem> insteadOfScripts = scripts.get(PluginType.INSTEAD_OF_ADD_MESSAGE);


            sc.setRequestAttribute(request, "afterScriptCollection", afterScripts);
            sc.setRequestAttribute(request, "beforeScriptCollection", beforeScripts);
            sc.setRequestAttribute(request, "insteadOfScriptCollection", insteadOfScripts);

            sc.setRequestAttribute(request, "mstatusId", mstatusId);
            sc.setRequestAttribute(request, "mstatus", mstatus);
            sc.setRequestAttribute(request, "flow", flow);
            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_TASK_WORKFLOW_MSTATUS_TRIGGERS);
            selectTaskTab(sc, id, "tabWorkflows", request);
            sc.setRequestAttribute(request, "tabView", new Tab(canManage, false));
            sc.setRequestAttribute(request, "tabEdit", new Tab(canManage, false));
            sc.setRequestAttribute(request, "tabResolutions", new Tab(canManage, false));
            sc.setRequestAttribute(request, "tabTransitions", new Tab(canManage, false));
            sc.setRequestAttribute(request, "tabPermissions", new Tab(canManage, false));
            sc.setRequestAttribute(request, "tabTriggers", new Tab(flow.canManage() && sc.canAction(Action.manageWorkflows, mstatus.getWorkflow().getTaskId()), true));
            sc.setRequestAttribute(request, "tabUdfPermissions", new Tab(udfs.size() > 0 && canManage, false));
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_TASK_WORKFLOW_MSTATUS_TRIGGERS));
            sc.setRequestAttribute(request, "tabScheduler", new Tab(canManage, false));

            return mapping.findForward("triggerJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            TriggerForm tf = (TriggerForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            SecuredWorkflowBean flow = AdapterManager.getInstance().getSecuredFindAdapterManager().findWorkflowById(sc, tf.getWorkflowId());
            if (sc.canAction(Action.manageWorkflows, flow.getTaskId())) {
                String mstatusId = tf.getMstatusId();
                AdapterManager.getInstance().getSecuredWorkflowAdapterManager().setMstatusTrigger(sc, mstatusId, tf.getBefore(), tf.getInsteadOf(), tf.getAfter());
            }
            return mapping.findForward("mstatusTriggerPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }
}
