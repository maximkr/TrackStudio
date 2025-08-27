package com.trackstudio.action.task;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.trackstudio.action.GeneralAction;
import com.trackstudio.action.TSDispatchAction;
import com.trackstudio.action.task.items.ScriptItem;
import com.trackstudio.app.TriggerManager;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.adapter.scheduler.SchedulerManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.external.IGeneralScheduler;
import com.trackstudio.form.BaseForm;
import com.trackstudio.kernel.cache.AbstractPluginCacheItem;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.cache.PluginCacheManager;
import com.trackstudio.kernel.cache.PluginType;
import com.trackstudio.kernel.lock.LockManager;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.model.Category;
import com.trackstudio.model.Mstatus;
import com.trackstudio.model.Trigger;
import com.trackstudio.model.Udf;
import com.trackstudio.model.Udfsource;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUDFBean;
import com.trackstudio.secured.SecuredWorkflowBean;
import com.trackstudio.startup.I18n;

public class ScriptAction extends TSDispatchAction {
    private static final LockManager lockManager = LockManager.getInstance();
    private String host;

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            host = request.getContextPath();
            BaseForm bs = (BaseForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String taskId = GeneralAction.getInstance().taskHeader(bs, sc, request, true);
            SecuredTaskBean tci = new SecuredTaskBean(taskId, sc);
            List<Trigger> triggers = TriggerManager.getInstance().getAllTriggers();
            List<ScriptItem> mstatusList = new ArrayList<ScriptItem>();
            List<ScriptItem> categoryCreate = new ArrayList<ScriptItem>();
            List<ScriptItem> categoryUpdate = new ArrayList<ScriptItem>();
            List<ScriptItem> udfList = new ArrayList<ScriptItem>();
            for (Trigger bean : triggers) {
                List<Category> categoryListCr = AdapterManager.getInstance().getSecuredCategoryAdapterManager().getListCreateTrigger(sc, taskId, bean.getId());
                for (Category cat : categoryListCr) {
                    String link = buildLinkCategory(cat.getId(), cat.getTask().getId(), cat.getName());
                    checkValue(cat.getCrTrigger().getBefore(), "Before", categoryCreate, link, PluginType.BEFORE_CREATE_TASK);
                    checkValue(cat.getCrTrigger().getInsteadOf(), "Insted of", categoryCreate, link, PluginType.INSTEAD_OF_CREATE_TASK);
                    checkValue(cat.getCrTrigger().getAfter(), "After", categoryCreate, link, PluginType.AFTER_CREATE_TASK);
                }
                List<Category> categoryListUp = AdapterManager.getInstance().getSecuredCategoryAdapterManager().getListCreateTrigger(sc, taskId, bean.getId());
                for (Category cat : categoryListUp) {
                    String link = buildLinkCategory(cat.getId(), cat.getTask().getId(), cat.getName());
                    checkValue(cat.getUpdTrigger().getBefore(), "Before", categoryUpdate, link, PluginType.BEFORE_EDIT_TASK);
                    checkValue(cat.getUpdTrigger().getInsteadOf(), "Insted of", categoryUpdate, link, PluginType.INSTEAD_OF_EDIT_TASK);
                    checkValue(cat.getUpdTrigger().getAfter(), "After", categoryUpdate, link, PluginType.AFTER_EDIT_TASK);
                }

                List<Mstatus> mstatuses = AdapterManager.getInstance().getSecuredMessageAdapterManager().getListTrigger(sc, bean.getId());
                for (Mstatus mstatus : mstatuses) {
                    if (tci.canView() && sc.canAction(Action.manageWorkflows, mstatus.getWorkflow().getTask().getId())) {
                        SecuredWorkflowBean flow = AdapterManager.getInstance().getSecuredFindAdapterManager().findWorkflowById(sc, mstatus.getWorkflow().getId());
                        if (flow.canManage()) {
                            String link = buildLinkMstatus(mstatus.getId(), mstatus.getWorkflow().getTask().getId(), mstatus.getWorkflow().getId(), mstatus.getName(), mstatus.getWorkflow().getName());
                            checkValue(mstatus.getTrigger().getBefore(), "Before", mstatusList, link, PluginType.BEFORE_ADD_MESSAGE);
                            checkValue(mstatus.getTrigger().getInsteadOf(), "Insted of", mstatusList, link, PluginType.INSTEAD_OF_ADD_MESSAGE);
                            checkValue(mstatus.getTrigger().getAfter(), "After", mstatusList, link, PluginType.AFTER_ADD_MESSAGE);
                        }
                    }
                }
            }
            List<Udf> udfScript = AdapterManager.getInstance().getSecuredUDFAdapterManager().getListTriggerScript(sc);
            for (Udf udf : udfScript) {
                if (checkAccessForUdf(udf, tci, sc)) {
                    checkValue(udf.getScript(), "Calculated", udfList, buildLinkUdf(udf, taskId), getPluginTypeforUdf(udf, true));
                }
            }
            List<Udf> udfLookup = AdapterManager.getInstance().getSecuredUDFAdapterManager().getListTriggerLookup(sc);
            for (Udf udf : udfLookup) {
                if (checkAccessForUdf(udf, tci, sc)) {
                    checkValue(udf.getLookupscript(), "Lookup", udfList, buildLinkUdf(udf, taskId), getPluginTypeforUdf(udf, false));
                }
            }
            Map<String, List<ScriptItem>> map = new LinkedHashMap<String, List<ScriptItem>>();
            Collections.sort(categoryCreate);
            Collections.sort(categoryUpdate);
            Collections.sort(mstatusList);
            Collections.sort(udfList);
            map.put(I18n.getString(sc.getLocale(), "CREATE_TASK"), categoryCreate);
            map.put(I18n.getString(sc.getLocale(), "EDIT_TASK"), categoryUpdate);
            map.put(I18n.getString(sc.getLocale(), "ADD_MESSAGE"), mstatusList);
            map.put(I18n.getString(sc.getLocale(), "CUSTOM_FIELD"), udfList);
            sc.setRequestAttribute(request, "map", map);
            sc.setRequestAttribute(request, "schedulers", SchedulerManager.getInstance().getGeneralJob());
            sc.setRequestAttribute(request, "scriptLoadLog", PluginCacheManager.getInstance().getScriptLoadLog());
            return mapping.findForward("scriptJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    private String buildLinkUdf(Udf udf, String taskId) {
        if (udf.getUdfsource().getTask() != null) {
            return "<a style='color:#000000;font-weight:bold;text-decoration:none;' href='" + host + "/TaskUdfViewAction.do?method=page&udfId=" + udf.getId() + "&id=" + udf.getUdfsource().getTask().getId() + "'>" + udf.getCaption() + " ( " + udf.getUdfsource().getTask().getName() + " [#" + udf.getUdfsource().getTask().getNumber() + "] )</a>";
        } else if (udf.getUdfsource().getUser() != null) {
            return "<a style='color:#000000;font-weight:bold;text-decoration:none;' href='" + host + "/UserUdfViewAction.do?method=page&udfId=" + udf.getId() + "&id=" + udf.getUdfsource().getUser().getId() + "'>" + udf.getCaption() + " ( " + udf.getUdfsource().getUser().getLogin() + " )</a>";
        } else {
            return "<a style='color:#000000;font-weight:bold;text-decoration:none;' href='" + host + "/WorkflowUdfViewAction.do?method=page&udfId=" + udf.getId() + "&id=" + taskId + "&workflowId=" + udf.getUdfsource().getWorkflow().getId() + "'>" + udf.getCaption() + " ( " + udf.getUdfsource().getWorkflow().getName() + " )</a>";
        }
    }

    private boolean checkAccessForUdf(Udf udf, SecuredTaskBean tci, SessionContext sc) throws GranException {
        if (udf.getUdfsource().getTask() != null) {
            if (tci.canView() && sc.canAction(Action.manageTaskUDFs, udf.getUdfsource().getTask().getId())) {
                SecuredUDFBean sudf = AdapterManager.getInstance().getSecuredFindAdapterManager().findUDFById(sc, udf.getId());
                Udfsource udfs = KernelManager.getFind().findUdfsource(sudf.getUdfSourceId());
                return udfs != null;
            } else {
                return false;
            }
        } else if (udf.getUdfsource().getUser() != null) {
            if (sc.getUser().canView() && sc.canAction(Action.manageUserUDFs, udf.getUdfsource().getUser().getId())) {
                SecuredUDFBean sudf = AdapterManager.getInstance().getSecuredFindAdapterManager().findUDFById(sc, udf.getId());
                Udfsource udfs = KernelManager.getFind().findUdfsource(sudf.getUdfSourceId());
                return udfs != null;
            } else {
                return false;
            }
        } else {
            SecuredUDFBean udfBean = AdapterManager.getInstance().getSecuredFindAdapterManager().findUDFById(sc, udf.getId());
            boolean canEdit = udfBean != null && sc.canAction(Action.manageWorkflows, udf.getUdfsource().getWorkflow().getId());
            boolean canManageRole = sc.canAction(Action.manageRoles, sc.getUserId());
            SecuredWorkflowBean flow = AdapterManager.getInstance().getSecuredFindAdapterManager().findWorkflowById(sc, udf.getUdfsource().getWorkflow().getId());
            return flow.canManage() && (canEdit || canManageRole);
        }
    }

    private PluginType getPluginTypeforUdf(Udf udf, boolean script) {
        if (udf.getUdfsource().getTask() != null || udf.getUdfsource().getWorkflow() != null) {
            if (script) {
                return PluginType.TASK_CUSTOM_FIELD_VALUE;
            } else {
                return PluginType.TASK_CUSTOM_FIELD_LOOKUP;
            }
        }
        if (udf.getUdfsource().getUser() != null) {
            if (script) {
                return PluginType.USER_CUSTOM_FIELD_VALUE;
            } else {
                return PluginType.USER_CUSTOM_FIELD_LOOKUP;
            }
        }
        return null;
    }

    private void checkValue(String value, String name, final List<ScriptItem> list, String connecTo, PluginType type) throws GranException {
        if (value != null && !value.isEmpty()) {
            boolean isExist = checkScriptExist(type, value);
            list.add(new ScriptItem(value, name, connecTo, isExist));
        }
    }

    private boolean checkScriptExist(PluginType type, String nameScript) throws GranException {
        List<AbstractPluginCacheItem> list = PluginCacheManager.getInstance().list(type).get(type);
        if (list != null) {
            for (AbstractPluginCacheItem pluginCacheItem : list) {
                if (pluginCacheItem.getName().equals(nameScript)) {
                    return true;
                }
            }
        }
        return false;
    }

    private String buildLinkCategory(String catId, String taskId, String name) throws GranException {
        return "<a style='color:#000000;font-weight: bold;text-decoration: none;' href='" + host + "/CategoryViewAction.do?method=page&categoryId=" + catId + "&id=" + taskId + "'>" + name + "</a>";
    }

    private String buildLinkMstatus(String mstatusId, String taskId, String workId, String name, String workName) throws GranException {
        return "<a style='color:#000000;font-weight: bold;text-decoration: none;' href='" + host + "/MstatusViewAction.do?method=page&mstatusId=" + mstatusId + "&id=" + taskId + "&workflowId=" + workId + "'> " + name + " ( " + workName + " )" + "</a>";
    }

    public ActionForward testScheduler(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            String scheduler = request.getParameter("className");
            IGeneralScheduler clScheduler = null;
            for (IGeneralScheduler generalScheduler : SchedulerManager.getInstance().getGeneralJob()) {
                if (generalScheduler.getClass().getName().equals(scheduler)) {
                    clScheduler = generalScheduler;
                    break;
                }
            }
            PrintWriter writer = response.getWriter();
            if (clScheduler != null) {
                try {
                    writer.append(clScheduler.execute());
                } catch (Exception e) {
                    writer.append(GranException.printStackTrace(e));
                    log.error("Error occurred in execution general job", e);
                }
            }
            return null;
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }
}