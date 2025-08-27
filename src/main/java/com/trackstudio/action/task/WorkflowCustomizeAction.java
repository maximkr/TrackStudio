package com.trackstudio.action.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.trackstudio.action.CustomEditAction;
import com.trackstudio.action.GeneralAction;
import com.trackstudio.app.PageNaming;
import com.trackstudio.app.UdfValue;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.form.CustomForm;
import com.trackstudio.kernel.cache.AbstractPluginCacheItem;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.cache.PluginCacheManager;
import com.trackstudio.kernel.cache.PluginType;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.secured.SecuredWorkflowBean;
import com.trackstudio.secured.SecuredWorkflowUDFBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.Tab;

public class WorkflowCustomizeAction extends CustomEditAction {
    private static Log log = LogFactory.getLog(WorkflowCustomizeAction.class);
    private static final LockManager lockManager = LockManager.getInstance();

    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.trace("##########");
        boolean w = lockManager.acquireConnection();
        try {
            CustomForm form = (CustomForm) actionForm;
            form.reset(actionMapping, request);
            SessionContext sc = GeneralAction.getInstance().imports(request, response);

            if (sc == null) return null;

            String id = GeneralAction.getInstance().taskHeader(form, sc, request, true);
            form.setId(id);
            String idUDF = form.getWorkflowId() == null ? "1" : form.getWorkflowId();
            SecuredWorkflowBean flow = AdapterManager.getInstance().getSecuredFindAdapterManager().findWorkflowById(sc, idUDF);
            List<SecuredWorkflowUDFBean> udfs = flow.getWorkflowUDFs();
            boolean flowAllows = flow.canManage();
            boolean _can_view = sc.canAction(Action.manageWorkflows, flow.getTaskId()) && sc.canAction(Action.manageWorkflows, id);
            boolean _can_modify = sc.canAction(Action.manageWorkflows, flow.getTaskId()) && flowAllows && sc.canAction(Action.manageWorkflows, id);
            boolean _can_delete = sc.canAction(Action.manageWorkflows, flow.getTaskId()) && flowAllows && sc.canAction(Action.manageWorkflows, id);
            boolean _can_create = sc.canAction(Action.manageWorkflows, flow.getTaskId()) && flowAllows && sc.canAction(Action.manageWorkflows, id);
            sc.setRequestAttribute(request, "_can_view", _can_view);
            sc.setRequestAttribute(request, "_can_modify", _can_modify);
            sc.setRequestAttribute(request, "_can_delete", _can_delete);
            sc.setRequestAttribute(request, "_can_create", _can_create);
            sc.setRequestAttribute(request, "flow", flow);
            sc.setRequestAttribute(request, "workflowId", flow.getId());
            Map<PluginType, List<AbstractPluginCacheItem>> scripts = PluginCacheManager.getInstance().list(
                    PluginType.TASK_CUSTOM_FIELD_VALUE, PluginType.TASK_CUSTOM_FIELD_LOOKUP
                    );
            List<AbstractPluginCacheItem> scriptCollection = scripts.get(PluginType.TASK_CUSTOM_FIELD_VALUE);

            sc.setRequestAttribute(request, "scriptCollection", scriptCollection);

            List<AbstractPluginCacheItem> lookupscriptCollection = scripts.get(PluginType.TASK_CUSTOM_FIELD_LOOKUP);
            sc.setRequestAttribute(request, "lookupscriptCollection", lookupscriptCollection);

            HashMap<Integer, String> type = new HashMap<Integer, String>();

            type.put(UdfValue.STRING, I18n.getString(sc.getLocale(), "UDF_STRING"));
            type.put(UdfValue.MEMO, I18n.getString(sc.getLocale(), "UDF_MEMO"));
            type.put(UdfValue.FLOAT, I18n.getString(sc.getLocale(), "UDF_FLOAT"));
            type.put(UdfValue.INTEGER, I18n.getString(sc.getLocale(), "UDF_INTEGER"));
            type.put(UdfValue.DATE, I18n.getString(sc.getLocale(), "UDF_DATE"));
            type.put(UdfValue.LIST, I18n.getString(sc.getLocale(), "UDF_LIST"));
            type.put(UdfValue.MULTILIST, I18n.getString(sc.getLocale(), "UDF_MULTILIST"));
            type.put(UdfValue.TASK, I18n.getString(sc.getLocale(), "UDF_TASK"));
            type.put(UdfValue.USER, I18n.getString(sc.getLocale(), "UDF_USER"));
            type.put(UdfValue.URL, I18n.getString(sc.getLocale(), "UDF_URL"));

            sc.setRequestAttribute(request, "types", type);

            ArrayList<UdfBeanListItem> udfList = new ArrayList<UdfBeanListItem>();
            for (SecuredWorkflowUDFBean ri : udfs) {
                UdfBeanListItem fli = new UdfBeanListItem(ri.getId(), ri.getCaption(), type.get(ri.getType()), ri.getOrder(), ri.canManage());
                udfList.add(fli);
            }
            Collections.sort(udfList);
            sc.setRequestAttribute(request, "udfList", udfList);
            sc.setRequestAttribute(request, "action", "/WorkflowUDFSaveAction.do");
            sc.setRequestAttribute(request, "viewUdfAction", "/WorkflowUdfViewAction.do");
            sc.setRequestAttribute(request, "editUdfAction", "/WorkflowUdfEditAction.do");
            sc.setRequestAttribute(request, "listUdfAction", "/WorkflowUdfListValuesAction.do");

            sc.setRequestAttribute(request, "tabView", new Tab(flow.canView() && sc.canAction(Action.manageWorkflows, id), false));
            sc.setRequestAttribute(request, "tabEdit", new Tab(flow.canManage() && sc.canAction(Action.manageWorkflows, flow.getTaskId()), false));
            sc.setRequestAttribute(request, "tabPriorities", new Tab(flow.canManage() && sc.canAction(Action.manageWorkflows, flow.getTaskId()), false));
            sc.setRequestAttribute(request, "tabStates", new Tab(flow.canManage() && sc.canAction(Action.manageWorkflows, flow.getTaskId()), false));
            sc.setRequestAttribute(request, "tabMessageTypes", new Tab(flow.canView() && sc.canAction(Action.manageWorkflows, flow.getTaskId()), false));
            sc.setRequestAttribute(request, "tabCustomize", new Tab(flow.canView() && sc.canAction(Action.manageWorkflows, flow.getTaskId()), true));
            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_TASK_WORKFLOW_CUSTOM_FIELD_LIST);
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_TASK_WORKFLOW_CUSTOM_FIELD_LIST));
            sc.setRequestAttribute(request, "helpTile", "HELP_TILE_ADD_NEW_UDF");
            sc.setRequestAttribute(request, "canManage", flow.canManage());
            return actionMapping.findForward("workflowCustomEditJSP");
        } catch (GranException ge) {
            request.setAttribute("javax.servlet.jsp.jspException", ge);
            return actionMapping.findForward("error");
        } finally {
            if (w) lockManager.releaseConnection();
        }
    }

}
