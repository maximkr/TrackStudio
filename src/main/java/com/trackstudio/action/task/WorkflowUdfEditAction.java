package com.trackstudio.action.task;

import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.trackstudio.action.CustomEditAction;
import com.trackstudio.action.GeneralAction;
import com.trackstudio.action.TSDispatchAction;
import com.trackstudio.app.PageNaming;
import com.trackstudio.app.UdfValue;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.constants.CategoryConstants;
import com.trackstudio.form.CustomForm;
import com.trackstudio.kernel.cache.AbstractPluginCacheItem;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.cache.PluginCacheManager;
import com.trackstudio.kernel.cache.PluginType;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.secured.SecuredMstatusBean;
import com.trackstudio.secured.SecuredUDFBean;
import com.trackstudio.secured.SecuredWorkflowBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.Tab;

public class WorkflowUdfEditAction extends TSDispatchAction {
    public ActionForward page(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            CustomForm form = (CustomForm) actionForm;
            SessionContext sc = GeneralAction.getInstance().imports(request, response);

            if (sc == null) return null;

            String id = GeneralAction.getInstance().taskHeader(form, sc, request, true);
            Map<PluginType, List<AbstractPluginCacheItem>> scripts = PluginCacheManager.getInstance().list(
                    PluginType.TASK_CUSTOM_FIELD_VALUE, PluginType.TASK_CUSTOM_FIELD_LOOKUP
            );
            List<AbstractPluginCacheItem> scriptCollection = scripts.get(PluginType.TASK_CUSTOM_FIELD_VALUE);
            sc.setRequestAttribute(request, "scriptCollection", scriptCollection);

            List<AbstractPluginCacheItem> lookupscriptCollection = scripts.get(PluginType.TASK_CUSTOM_FIELD_LOOKUP);
            sc.setRequestAttribute(request, "lookupscriptCollection", lookupscriptCollection);

            CustomEditAction.fillEditForm(sc, form, request);
            sc.setRequestAttribute(request, "editUdfAction", "/WorkflowUdfEditAction.do");
            sc.setRequestAttribute(request, "cancelAction", "/WorkflowCustomizeAction.do");
            sc.setRequestAttribute(request, "_can_view", sc.canAction(Action.manageWorkflows, id));
            SecuredUDFBean udf = (SecuredUDFBean) request.getAttribute("udf");
            String workflowTaskId = null;

            if (udf != null) {
                String workflowId = KernelManager.getFind().findUdfsource(udf.getUdfSourceId()).getWorkflow().getId();
                workflowTaskId = KernelManager.getFind().findWorkflow(workflowId).getTask().getId();
            }
            boolean canEdit = (udf == null || udf.canManage()) && sc.canAction(Action.manageWorkflows, id) && (udf == null || sc.canAction(Action.manageWorkflows, workflowTaskId));
            if (udf != null)
                sc.setRequestAttribute(request, "_can_modify", canEdit);
            else
                sc.setRequestAttribute(request, "_can_modify", Boolean.TRUE);


            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_TASK_WORKFLOW_CUSTOM_FIELD_PROPERTIES);
            selectTaskTab(sc, id, "tabWorkflows", request);
            sc.setRequestAttribute(request, "tabView", new Tab(udf != null && sc.canAction(Action.manageWorkflows, id) && sc.allowedByACL(id), false));
            sc.setRequestAttribute(request, "tabListValues", new Tab(udf != null && sc.canAction(Action.manageWorkflows, id) && sc.allowedByACL(id) && (udf.getType() == UdfValue.LIST || udf.getType() == UdfValue.MULTILIST), false));
            sc.setRequestAttribute(request, "tabEdit", new Tab(canEdit, true));
            sc.setRequestAttribute(request, "tabPermission", new Tab(sc.canAction(Action.manageWorkflows, id) && request.getAttribute("udf") != null && sc.allowedByACL(id), false));
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_TASK_WORKFLOW_CUSTOM_FIELD_PROPERTIES));
            sc.setRequestAttribute(request, "workflowId", form.getWorkflowId());
            SecuredWorkflowBean flow = AdapterManager.getInstance().getSecuredFindAdapterManager().findWorkflowById(sc, form.getWorkflowId());
            sc.setRequestAttribute(request, "flow", flow);
            sc.setRequestAttribute(request, "canManage", flow.canManage());
            return actionMapping.findForward("workflowUdfEditJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward save(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            CustomForm form = (CustomForm) actionForm;
            SessionContext sc = GeneralAction.getInstance().imports(request, response);

            if (sc == null) return null;

            boolean newUdf = form.getCreateNewUdf() != null;
            String initial;
            int type = form.getType();
            if (newUdf) {
                if (form.getCaption() != null && form.getCaption().length() > 0) {
                    String order = form.getOrder();
                    String intitStr = form.getInitial();
                    initial = CustomEditAction.getInitial(type, intitStr, sc);
                    String def = form.getDef();
                    def = def != null ? def.replaceAll("\r\n", " ") : def;
                    String newUdfId = AdapterManager.getInstance().getSecuredUDFAdapterManager().createWorkflowUdf(sc, form.getWorkflowId(), form.getCaption(), form.getReferencedbycaption(),
                            Integer.parseInt(order.length() == 0 ? "0" : order), (type != 3 && type != 6) ? def : null, form.getList(),
                            form.getRequire() != null, form.getHtmlview() != null,
                            form.getType(), form.getCalculen() != null ? form.getScript() : null, form.getLookupen() != null ? form.getLscript() : null, form.getLookuponly().equals("on"), !form.getCachevalues().equals("on") && form.getCalculen() == null || form.getCachevalues().equals("on"), initial);
                    form.setUdfId(newUdfId);
                    form.setCreateNewUdf(null);
                    form.setMutable(false);
                    if (type == UdfValue.LIST || type == UdfValue.MULTILIST) {
                        if (form.getAddlist() != null && form.getAddlist().length() > 0) {
                            StringTokenizer tk = new StringTokenizer(form.getAddlist(), "\r\n");
                            int j = 0;
                            while (tk.hasMoreTokens()) {
                                String token = tk.nextToken().trim();
                                String test = token + j;
                                j++;
                                if (token.length() > 0) {
                                    String udflistid = AdapterManager.getInstance().getSecuredUDFAdapterManager().addWorkflowUdflist(sc, form.getWorkflowId(), newUdfId, token);
                                    if (test.equals(def)) {
                                        SecuredUDFBean udf = AdapterManager.getInstance().getSecuredFindAdapterManager().findUDFById(sc, form.getUdfId());
                                        AdapterManager.getInstance().getSecuredUDFAdapterManager().updateWorkflowUdf(sc, udf.getId(), udf.getCaption(), udf.getReferencedbycaption(),
                                                udf.getOrder(), udflistid,
                                                udf.isRequired(), udf.isHtmlview(), udf.getScript(), udf.getLookupscript(), udf.isLookuponly(), udf.isCachevalues(), udf.getInitial());
                                    }
                                }
                            }
                        }
                    }
                    List<SecuredMstatusBean> operationsList = AdapterManager.getInstance().getSecuredWorkflowAdapterManager().getMstatusList(sc, form.getWorkflowId());

                    for (SecuredMstatusBean operation : operationsList)
                        AdapterManager.getInstance().getSecuredUDFAdapterManager().setMstatusUDFRule(sc, newUdfId, operation.getId(), CategoryConstants.VIEW_ALL, CategoryConstants.EDIT_ALL);
                }
            } else {
                SecuredUDFBean udf = AdapterManager.getInstance().getSecuredFindAdapterManager().findUDFById(sc, form.getUdfId());
                initial = CustomEditAction.getInitial(udf.getType(), form.getInitial(), sc);
                String udfid = udf.getId();
                String def = form.getDef();
                def = def != null ? def.replaceAll("\r\n", " ") : def;
                if (udf.getType() == UdfValue.LIST || udf.getType() == UdfValue.MULTILIST) {
                    if (udf.getUL() != null)
                        for (Map.Entry<String, String> entry : udf.getUL().entrySet()) {
                            String listValue = form.getLists(entry.getKey());
                            if (listValue != null) {
                                if (!listValue.equals(entry.getValue())) AdapterManager.getInstance().getSecuredUDFAdapterManager().updateWorkflowUdflist(sc, entry.getKey(), listValue);
                            } else {
                                AdapterManager.getInstance().getSecuredUDFAdapterManager().deleteWorkflowUdflist(sc, form.getWorkflowId(), entry.getKey());
                            }
                        }

                    if (form.getAddlist() != null && form.getAddlist().length() > 0) {

                        StringTokenizer tk = new StringTokenizer(form.getAddlist(), "\r\n");
                        int j = 0;
                        while (tk.hasMoreTokens()) {
                            String token = tk.nextToken().trim();
                            String test = token + j;
                            j++;
                            if (token.length() > 0) {
                                String udflistid = AdapterManager.getInstance().getSecuredUDFAdapterManager().addWorkflowUdflist(sc, form.getWorkflowId(), udf.getId(), token);
                                if (test.equals(def)) {
                                    def = udflistid;
                                }
                            }
                        }
                    }
                }
                boolean required = form.getRequire() != null;
                AdapterManager.getInstance().getSecuredUDFAdapterManager().updateWorkflowUdf(sc, udfid, form.getCaption(), form.getReferencedbycaption(),
                        Integer.parseInt(form.getOrder()), def,
                        required, form.getHtmlview() != null, form.getCalculen() != null ? form.getScript() : null, form.getLookupen() != null ? form.getLscript() : null, form.getLookuponly().equals("on"), !form.getCachevalues().equals("on") && form.getCalculen() == null || form.getCachevalues().equals("on"), initial);
            }

            return actionMapping.findForward("workflowUdfViewPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

}
