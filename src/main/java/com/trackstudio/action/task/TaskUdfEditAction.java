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
import com.trackstudio.form.CustomForm;
import com.trackstudio.kernel.cache.AbstractPluginCacheItem;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.cache.PluginCacheManager;
import com.trackstudio.kernel.cache.PluginType;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.secured.SecuredTaskUDFBean;
import com.trackstudio.secured.SecuredUDFBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.Tab;

public class TaskUdfEditAction extends TSDispatchAction {
    public ActionForward page(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            CustomForm form = (CustomForm) actionForm;
            SessionContext sc = GeneralAction.getInstance().imports(request, response);
            if (sc == null)
                return null;
            String id = GeneralAction.getInstance().taskHeader(form, sc, request, true);
            Map<PluginType, List<AbstractPluginCacheItem>> scripts = PluginCacheManager.getInstance().list(
                    PluginType.TASK_CUSTOM_FIELD_VALUE, PluginType.TASK_CUSTOM_FIELD_LOOKUP
                    );
            List<AbstractPluginCacheItem> scriptCollection = scripts.get(PluginType.TASK_CUSTOM_FIELD_VALUE);
            sc.setRequestAttribute(request, "scriptCollection", scriptCollection);

            List<AbstractPluginCacheItem> lookupscriptCollection = scripts.get(PluginType.TASK_CUSTOM_FIELD_LOOKUP);
            sc.setRequestAttribute(request, "lookupscriptCollection", lookupscriptCollection);

            CustomEditAction.fillEditForm(sc, form, request);
            sc.setRequestAttribute(request, "editUdfAction", "/TaskUdfEditAction.do");

            sc.setRequestAttribute(request, "_can_view", sc.canAction(Action.manageTaskUDFs, id));
            SecuredTaskUDFBean udf = (SecuredTaskUDFBean) request.getAttribute("udf");

            if (udf != null) {
                sc.setRequestAttribute(request, "cancelAction", "/TaskUdfViewAction.do");
                sc.setRequestAttribute(request, "udfId", udf.getId());
                sc.setRequestAttribute(request, "connectedTo", udf.getTask());
                sc.setRequestAttribute(request, "_can_modify", udf.canManage() && sc.canAction(Action.manageTaskUDFs, id) && sc.canAction(Action.manageTaskUDFs, KernelManager.getFind().findUdfsource(udf.getUdfSourceId()).getTask().getId()));
            } else {
                sc.setRequestAttribute(request, "_can_modify", true);
                sc.setRequestAttribute(request, "cancelAction", "/TaskCustomizeAction.do");
            }
            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_TASK_CUSTOM_FIELD_PROPERTIES);
            sc.setRequestAttribute(request, "tabView", new Tab(udf != null && (sc.canAction(Action.manageTaskUDFs, id)) && sc.allowedByACL(id), false));
            sc.setRequestAttribute(request, "tabListValues", new Tab(udf != null && sc.canAction(Action.manageTaskUDFs, id) && sc.allowedByACL(id) && (udf.getType() == UdfValue.LIST || udf.getType() == UdfValue.MULTILIST), false));
            sc.setRequestAttribute(request, "tabEdit", new Tab(sc.canAction(Action.manageTaskUDFs, id) && sc.allowedByACL(id), true));
            sc.setRequestAttribute(request, "tabPermission", new Tab(udf != null && sc.canAction(Action.manageTaskUDFs, id) && sc.allowedByACL(id), false));
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_TASK_CUSTOM_FIELD_PROPERTIES));

            return actionMapping.findForward("taskUdfEditJSP");
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
            if (sc == null)
                return null;
            String id = form.getId();
            boolean newUdf = form.getCreateNewUdf() != null;
            String initial;
            int type = form.getType();
            if (newUdf) {
                if (form.getCaption() != null && form.getCaption().length() > 0) {
                    String order = form.getOrder();
                    String intitStr = form.getInitial();
                    initial = CustomEditAction.getInitial(type, intitStr, sc);
                    String newUdfId = AdapterManager.getInstance().getSecuredUDFAdapterManager().createTaskUdf(sc, id, form.getCaption(), form.getReferencedbycaption(),
                            Integer.parseInt(order.length() == 0 ? "0" : order), (type != 3 && type != 6) ? form.getDef() : null, form.getList(),
                            form.getRequire() != null, form.getHtmlview() != null,
                            form.getType(), form.getCalculen() != null ? form.getScript() : null, form.getLookupen() != null ? form.getLscript() : null, form.getLookuponly().equals("on"), !form.getCachevalues().equals("on") && form.getCalculen() == null || form.getCachevalues().equals("on"), initial);
                    form.setUdfId(newUdfId);
                    form.setCreateNewUdf(null);
                    form.setMutable(false);
                    if (type == UdfValue.LIST || type == UdfValue.MULTILIST) {
                        String def = form.getDef();
                        if (form.getAddlist() != null && form.getAddlist().length() > 0) {
                            StringTokenizer tk = new StringTokenizer(form.getAddlist(), "\r\n");
                            int j = 0;
                            while (tk.hasMoreTokens()) {
                                String token = tk.nextToken().trim();
                                String test = token + j;
                                j++;
                                if (token.length() > 0) {
                                    String udflistid = AdapterManager.getInstance().getSecuredUDFAdapterManager().addTaskUdflist(sc, id, newUdfId, token);
                                    if (test.equals(def)) {
                                        SecuredUDFBean udf = AdapterManager.getInstance().getSecuredFindAdapterManager().findUDFById(sc, form.getUdfId());
                                        AdapterManager.getInstance().getSecuredUDFAdapterManager().updateTaskUdf(sc, udf.getId(), udf.getCaption(), udf.getReferencedbycaption(),
                                                udf.getOrder(), udflistid,
                                                udf.isRequired(), udf.isHtmlview(), udf.getScript(), udf.getLookupscript(), udf.isLookuponly(), udf.isCachevalues(), udf.getInitial());
                                    }
                                }
                            }
                        }

                    }
                }
            } else {
                SecuredUDFBean udf = AdapterManager.getInstance().getSecuredFindAdapterManager().findUDFById(sc, form.getUdfId());
                initial = CustomEditAction.getInitial(udf.getType(), form.getInitial(), sc);
                String udfid = udf.getId();
                String def = form.getDef();
                if (udf.getType() == UdfValue.LIST || udf.getType() == UdfValue.MULTILIST) {
                    if (udf.getUL() != null)
                        for (String key : udf.getUL().keySet()) {
                            String listValue = form.getLists(key);
                            if (listValue != null)
                                AdapterManager.getInstance().getSecuredUDFAdapterManager().updateTaskUdflist(sc, id, key, listValue.trim());
                            else
                                AdapterManager.getInstance().getSecuredUDFAdapterManager().deleteTaskUdflist(sc, form.getId(), key);
                        }

                    if (form.getAddlist() != null && form.getAddlist().length() > 0) {

                        StringTokenizer tk = new StringTokenizer(form.getAddlist(), "\r\n");
                        int j = 0;
                        while (tk.hasMoreTokens()) {
                            String token = tk.nextToken().trim();
                            String test = token + j;
                            j++;
                            if (token.length() > 0) {
                                String udflistid = AdapterManager.getInstance().getSecuredUDFAdapterManager().addTaskUdflist(sc, id, udf.getId(), token);
                                if (test.equals(def)) {
                                    def = udflistid;
                                }
                            }
                        }
                    }

                }
                boolean required = form.getRequire() != null;
                AdapterManager.getInstance().getSecuredUDFAdapterManager().updateTaskUdf(sc, udfid, form.getCaption(), form.getReferencedbycaption(),
                        Integer.parseInt(form.getOrder()), def,
                        required, form.getHtmlview() != null, form.getCalculen() != null ? form.getScript() : null, form.getLookupen() != null ? form.getLscript() : null, form.getLookuponly().equals("on"), !form.getCachevalues().equals("on") && form.getCalculen() == null || form.getCachevalues().equals("on"), initial);

            }
            return actionMapping.findForward("taskUdfViewPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

}
