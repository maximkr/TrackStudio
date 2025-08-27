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
import com.trackstudio.secured.SecuredCategoryBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.Tab;

public class CategoryTriggerAction extends TSDispatchAction {
    private static Log log = LogFactory.getLog(TransitionAction.class);

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            TriggerForm tf = (TriggerForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().taskHeader(tf, sc, request, true);
            if (!sc.canAction(Action.manageCategories, id))
                return null;
            SecuredTaskBean tci = new SecuredTaskBean(id, sc);
            String catId = tf.getCategoryId() == null ? tci.getCategoryId() : tf.getCategoryId();
            SecuredCategoryBean category = AdapterManager.getInstance().getSecuredFindAdapterManager().findCategoryById(sc, catId);

            tf.setAfter(category.getCreateAfter() != null ? category.getCreateAfter() : "");
            tf.setBefore(category.getCreateBefore() != null ? category.getCreateBefore() : "");
            tf.setInsteadOf(category.getCreateInsteadOf() != null ? category.getCreateInsteadOf() : "");

            tf.setUpdAfter(category.getUpdateAfter() != null ? category.getUpdateAfter() : "");
            tf.setUpdBefore(category.getUpdateBefore() != null ? category.getUpdateBefore() : "");
            tf.setUpdInsteadOf(category.getUpdateInsteadOf() != null ? category.getUpdateInsteadOf() : "");

            Map<PluginType, List<AbstractPluginCacheItem>> scripts = PluginCacheManager.getInstance().list(PluginType.AFTER_CREATE_TASK ,
                    PluginType.BEFORE_CREATE_TASK, PluginType.BEFORE_CREATE_TASK, PluginType.INSTEAD_OF_CREATE_TASK,
                    PluginType.AFTER_EDIT_TASK, PluginType.BEFORE_EDIT_TASK, PluginType.INSTEAD_OF_EDIT_TASK);
            List<AbstractPluginCacheItem> afterScripts = scripts.get(PluginType.AFTER_CREATE_TASK);
            List<AbstractPluginCacheItem> beforeScripts = scripts.get(PluginType.BEFORE_CREATE_TASK);
            List<AbstractPluginCacheItem> insteadOfScripts = scripts.get(PluginType.INSTEAD_OF_CREATE_TASK);
            List<AbstractPluginCacheItem> updAfterScripts = scripts.get(PluginType.AFTER_EDIT_TASK);
            List<AbstractPluginCacheItem> updBeforeScripts = scripts.get(PluginType.BEFORE_EDIT_TASK);
            List<AbstractPluginCacheItem> updInsteadOfScripts = scripts.get(PluginType.INSTEAD_OF_EDIT_TASK);

            sc.setRequestAttribute(request, "afterScriptCollection", afterScripts);

            sc.setRequestAttribute(request, "beforeScriptCollection", beforeScripts);

            sc.setRequestAttribute(request, "insteadOfScriptCollection", insteadOfScripts);

            sc.setRequestAttribute(request, "updAfterScriptCollection", updAfterScripts);

            sc.setRequestAttribute(request, "updBeforeScriptCollection", updBeforeScripts);

            sc.setRequestAttribute(request, "updInsteadOfScriptCollection", updInsteadOfScripts);

            sc.setRequestAttribute(request, "categoryId", catId);
            sc.setRequestAttribute(request, "currentCategory", category);
            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_TASK_CATEGORY_TRIGGERS);
            selectTaskTab(sc, id, "tabWorkflows", request);
            sc.setRequestAttribute(request, "tabView", new Tab(sc.canAction(Action.manageCategories, category.getTaskId()), false));
            sc.setRequestAttribute(request, "tabEdit", new Tab(category.canManage(), false));
            sc.setRequestAttribute(request, "tabRelations", new Tab(category.canManage(), false));
            sc.setRequestAttribute(request, "tabPermissions", new Tab(sc.canAction(Action.manageCategories, category.getTaskId()), false));
            sc.setRequestAttribute(request, "tabTriggers", new Tab(sc.canAction(Action.manageCategories, category.getTaskId()) && category.canManage(), true));
            sc.setRequestAttribute(request, "tabTemplate", new Tab(sc.canAction(Action.manageCategories, category.getTaskId()) && category.canManage(), false));
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_TASK_CATEGORY_TRIGGERS));

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
            SecuredCategoryBean category = AdapterManager.getInstance().getSecuredFindAdapterManager().findCategoryById(sc, tf.getCategoryId());
            if (sc.canAction(Action.manageCategories, category.getTaskId()))
                AdapterManager.getInstance().getSecuredCategoryAdapterManager().setCategoryTrigger(sc, category.getId(), tf.getBefore(), tf.getInsteadOf(), tf.getAfter(), tf.getUpdBefore(), tf.getUpdInsteadOf(), tf.getUpdAfter());
            return mapping.findForward("categoryViewPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }
}
