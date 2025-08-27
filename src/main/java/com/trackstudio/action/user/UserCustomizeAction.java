package com.trackstudio.action.user;

import java.util.ArrayList;
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
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.secured.SecuredUserUDFBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.EggBasket;
import com.trackstudio.tools.Tab;

public class UserCustomizeAction extends CustomEditAction {
    private static Log log = LogFactory.getLog(UserCustomizeAction.class);
    private static final LockManager lockManager = LockManager.getInstance();

    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            CustomForm form = (CustomForm) actionForm;
            form.reset(actionMapping, request);
            SessionContext sc = GeneralAction.getInstance().imports(request, response);
            if (sc == null)
                return null;
            String id = GeneralAction.getInstance().userHeader(form, sc, request);
            form.setId(id);
            ArrayList<SecuredUserUDFBean> udfs = AdapterManager.getInstance().getSecuredUDFAdapterManager().getAllAvailableUserUdfList(sc, id);
            boolean _can_view = sc.userOnSight(id) && sc.canAction(Action.manageUserUDFs, id);
            boolean _can_modify = sc.allowedByUser(id) && sc.canAction(Action.manageUserUDFs, id);
            boolean _can_delete = sc.allowedByUser(id) && sc.canAction(Action.manageUserUDFs, id);
            boolean _can_create = sc.allowedByUser(id) && sc.canAction(Action.manageUserUDFs, id);
            sc.setRequestAttribute(request, "_can_view", _can_view);
            sc.setRequestAttribute(request, "_can_modify", _can_modify);
            sc.setRequestAttribute(request, "_can_delete", _can_delete);
            sc.setRequestAttribute(request, "_can_create", _can_create);
            Map<PluginType, List<AbstractPluginCacheItem>> scripts = PluginCacheManager.getInstance().list(
                    PluginType.USER_CUSTOM_FIELD_VALUE, PluginType.USER_CUSTOM_FIELD_LOOKUP
            );
            List<AbstractPluginCacheItem> scriptCollection = scripts.get(PluginType.USER_CUSTOM_FIELD_VALUE);
            sc.setRequestAttribute(request, "scriptCollection", scriptCollection);

            List<AbstractPluginCacheItem> lookupscriptCollection = scripts.get(PluginType.USER_CUSTOM_FIELD_LOOKUP);
            sc.setRequestAttribute(request, "lookupscriptCollection", lookupscriptCollection);


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
            EggBasket<SecuredUserBean, UdfBeanListItem> parentUdfSet = new EggBasket<SecuredUserBean, UdfBeanListItem>();
            EggBasket<SecuredUserBean, UdfBeanListItem> childrenUdfSet = new EggBasket<SecuredUserBean, UdfBeanListItem>();
            ArrayList<SecuredUserBean> parentTasks = AdapterManager.getInstance().getSecuredUserAdapterManager().getUserChain(sc, null, id);
            ArrayList<EggBasket> seeAlso = new ArrayList<EggBasket>();
            for (SecuredUserUDFBean ri : udfs) {
                UdfBeanListItem fli = new UdfBeanListItem(ri.getId(), ri.getCaption(), type.get(ri.getType()), ri.getOrder(), sc.canAction(Action.manageUserUDFs, id) && ri.canManage());
                SecuredUserBean user = ri.getUser();
                if (user.canView() && sc.canAction(Action.manageUserUDFs, user.getId())){
                if (user.getId().equals(id)) {
                    udfList.add(fli);
                } else if (parentTasks.contains(user)) {
                    parentUdfSet.putItem(user, fli);
                } else {
                    childrenUdfSet.putItem(user, fli);
                }
                }
            }

            if (!parentUdfSet.isEmpty()) seeAlso.add(parentUdfSet);
            if (!childrenUdfSet.isEmpty()) seeAlso.add(childrenUdfSet);

            sc.setRequestAttribute(request, "udfList", udfList);
            sc.setRequestAttribute(request, "seeAlsoUsers", seeAlso);
            sc.setRequestAttribute(request, "action", "/UserUDFSaveAction.do");
            sc.setRequestAttribute(request, "createObjectAction", "/UserUDFSaveAction.do");
            sc.setRequestAttribute(request, "viewUdfAction", "/UserUdfViewAction.do");
            sc.setRequestAttribute(request, "editUdfAction", "/UserUdfEditAction.do");
            sc.setRequestAttribute(request, "listUdfAction", "/UserCustomizeAction.do");
            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_USER_CUSTOM_FIELD_LIST);
            sc.setRequestAttribute(request, "tabCustomize", new Tab(true, true));
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_USER_CUSTOM_FIELD_LIST));
            sc.setRequestAttribute(request, "helpTile", "HELP_TILE_ADD_NEW_UDF");
            return actionMapping.findForward("userCustomEditJSP");
        } catch (GranException ge) {
            request.setAttribute("javax.servlet.jsp.jspException", ge);
            return actionMapping.findForward("error");
        } finally {
            if (w) lockManager.releaseConnection();
        }
    }
}
