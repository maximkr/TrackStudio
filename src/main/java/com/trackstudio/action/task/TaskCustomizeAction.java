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
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredTaskUDFBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.EggBasket;
import com.trackstudio.tools.Tab;

public class TaskCustomizeAction extends CustomEditAction {
    private static Log log = LogFactory.getLog(TaskCustomizeAction.class);
    private static final LockManager lockManager = LockManager.getInstance();

    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            CustomForm form = (CustomForm) actionForm;
            form.reset(actionMapping, request);
            SessionContext sc = GeneralAction.getInstance().imports(request, response);

            if (sc == null) return null;

            String id = GeneralAction.getInstance().taskHeader(form, sc, request, true);
            form.setId(id);
            SecuredTaskBean uc = new SecuredTaskBean(id, sc);
            String idUDF = uc.getId();
            List<SecuredTaskUDFBean> udfs = AdapterManager.getInstance().getSecuredUDFAdapterManager().getAllAvailableTaskUdfList(sc, id);
            boolean _can_view = sc.canAction(Action.manageTaskUDFs, idUDF) && uc.canView();
            boolean _can_modify = sc.canAction(Action.manageTaskUDFs, idUDF) && uc.canManage();
            boolean _can_delete = sc.canAction(Action.manageTaskUDFs, idUDF) && uc.canManage();
            boolean _can_create = sc.canAction(Action.manageTaskUDFs, idUDF) && uc.canManage();

            sc.setRequestAttribute(request, "_can_view", _can_view);
            sc.setRequestAttribute(request, "_can_modify", _can_modify);
            sc.setRequestAttribute(request, "_can_delete", _can_delete);
            sc.setRequestAttribute(request, "_can_create", _can_create);
            Map<PluginType, List<AbstractPluginCacheItem>> scripts = PluginCacheManager.getInstance().list(
                    PluginType.TASK_CUSTOM_FIELD_VALUE, PluginType.TASK_CUSTOM_FIELD_LOOKUP
            );
                    List < AbstractPluginCacheItem > scriptCollection = scripts.get(PluginType.TASK_CUSTOM_FIELD_VALUE);
            sc.setRequestAttribute(request, "scriptCollection", scriptCollection);

            sc.setRequestAttribute(request, "lookupscriptCollection", scripts.get(PluginType.TASK_CUSTOM_FIELD_LOOKUP));

            sc.setRequestAttribute(request, "idUdf", idUDF);
            sc.setRequestAttribute(request, "pattern", sc.getUser().getDateFormatter().getPattern2());
            Collections.sort(udfs);
            boolean canCreate = (Boolean) request.getAttribute("_can_create");
            sc.setRequestAttribute(request, "canCreateObject", canCreate);
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
            EggBasket<SecuredTaskBean, UdfBeanListItem> parentUdfSet = new EggBasket<SecuredTaskBean, UdfBeanListItem>();
            EggBasket<SecuredTaskBean, UdfBeanListItem> childrenUdfSet = new EggBasket<SecuredTaskBean, UdfBeanListItem>();
            ArrayList<EggBasket> seeAlso = new ArrayList<EggBasket>();
            ArrayList<SecuredTaskBean> parentTasks = AdapterManager.getInstance().getSecuredTaskAdapterManager().getTaskChain(sc, null, id);

            for (SecuredTaskUDFBean ri : udfs) {
                UdfBeanListItem fli = new UdfBeanListItem(ri.getId(), ri.getCaption(), type.get(ri.getType()), ri.getOrder(), ri.canManage());
                SecuredTaskBean task = ri.getTask();
                if (task.canView() && sc.canAction(Action.manageTaskUDFs, task.getId())){
                if (task.getId().equals(id)) {
                    udfList.add(fli);
                } else if (parentTasks.contains(task)) {
                    parentUdfSet.putItem(task, fli);
                } else childrenUdfSet.putItem(task, fli);
                }
            }

            sc.setRequestAttribute(request, "action", "/TaskUDFSaveAction.do");
            sc.setRequestAttribute(request, "editUdfAction", "/TaskUdfEditAction.do");
            sc.setRequestAttribute(request, "viewUdfAction", "/TaskUdfViewAction.do");
            sc.setRequestAttribute(request, "listUdfAction", "/TaskCustomizeAction.do");
            sc.setRequestAttribute(request, "udfList", udfList);
            if (!parentUdfSet.isEmpty()) seeAlso.add(parentUdfSet);
            if (!childrenUdfSet.isEmpty()) seeAlso.add(childrenUdfSet);
            sc.setRequestAttribute(request, "seeAlso", seeAlso);

            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_TASK_CUSTOM_FIELD_LIST);
            sc.setRequestAttribute(request, "helpTile", "HELP_TILE_ADD_NEW_UDF");
            sc.setRequestAttribute(request, "tabCustomize", new Tab(sc.canAction(Action.manageTaskUDFs, id), true));

            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_TASK_CUSTOM_FIELD_LIST);
            sc.setRequestAttribute(request, "helpTile", "HELP_TILE_ADD_NEW_UDF");
            sc.setRequestAttribute(request, "tabCustomize", new Tab(sc.canAction(Action.manageTaskUDFs, id), true));

            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_TASK_CUSTOM_FIELD_LIST));
            return actionMapping.findForward("taskCustomEditJSP");
        } catch (GranException ge) {
            request.setAttribute("javax.servlet.jsp.jspException", ge);
            return actionMapping.findForward("error");
        } finally {
            if (w) lockManager.releaseConnection();
        }
    }
}
