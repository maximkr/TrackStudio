package com.trackstudio.action.task;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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
import com.trackstudio.app.Preferences;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.constants.CategoryConstants;
import com.trackstudio.form.CategoryForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.cache.PluginType;
import com.trackstudio.secured.SecuredCategoryBean;
import com.trackstudio.startup.Config;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.Tab;

public class CategoryEditAction extends TSDispatchAction {
    private static Log log = LogFactory.getLog(CategoryEditAction.class);

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            CategoryForm tf = (CategoryForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().taskHeader(tf, sc, request, true);
            if (!sc.canAction(Action.manageCategories, id))
                return null;

            String catId = tf.getCategoryId();
            boolean createNewCategory = catId == null || catId.length() == 0;
            boolean canChangeWF = true;
            String path = getServlet().getServletContext().getRealPath("/icons");
            String realPath2 = Config.getInstance().getPluginsDir() + PluginType.ICON;
            File file = new File(realPath2);
            String[] imageNames2 = null;
            String[] imageNames1 = null;
            if (file.exists())
                imageNames2 = file.list();
            file = new File(path);
            if (file.exists())
                imageNames1 = file.list();
            ArrayList<String> imageNames = new ArrayList<String>();
            if (imageNames2 != null)
                imageNames.addAll(Arrays.asList(imageNames2));
            if (imageNames1 != null)
                imageNames.addAll(Arrays.asList(imageNames1));
            List<String> icos = new ArrayList<String>();
            for (String fileName : imageNames) {
                if (!fileName.startsWith(".")) {
                    icos.add(URLEncoder.encode(fileName, Config.getEncoding()));
                }
            }
            sc.setRequestAttribute(request, "icons", icos);
            if (createNewCategory) {
                tf.setHours(true);

                //TODO: may be should not be hardcoded, but to be got from some properties
                tf.setIcon("blank.png");

                sc.setRequestAttribute(request, "createNewCategory", Boolean.TRUE);
                sc.setRequestAttribute(request, "tabPermissions", new Tab(false, false));
                sc.setRequestAttribute(request, "tabView", new Tab(false, false));
                sc.setRequestAttribute(request, "tabEdit", new Tab(true, true));
                sc.setRequestAttribute(request, "tabRelations", new Tab(false, false));
                sc.setRequestAttribute(request, "tabTemplate", new Tab(false, false));
                sc.setRequestAttribute(request, "tabTriggers", new Tab(false, false));
                sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_TASK_CATEGORY_PROPERTIES);
                tf.setHiddenInTree(Preferences.HIDE_CLOSE_IN_TREE);
            } else {
                SecuredCategoryBean category = AdapterManager.getInstance().getSecuredFindAdapterManager().findCategoryById(sc, catId);
                tf.setWorkflow(category.getWorkflowId());
                tf.setName(category.getName());
                tf.setAction(category.getAction());
                tf.setShowInToolbar(Preferences.showInToolbar(category.getPreferences()));
                tf.setDefaultLink(Preferences.getDefaultLink(category.getPreferences()));
                tf.setSortOrderInTree(Preferences.isSortOrgerInTree(category.getPreferences()));
                tf.setHiddenInTree(Preferences.getHiddenInTree(category.getPreferences()));
                tf.setViewCategory(Preferences.getViewCategory(category.getPreferences()));
                tf.setHanderlOnlyRole(Preferences.isHandlerOnlyRole(category.getPreferences()));

                if (category.getBudget() != null) {
                    tf.setYears(category.getBudget().indexOf(CategoryConstants.Y) > -1);
                    tf.setMonths(category.getBudget().indexOf(CategoryConstants.M) > -1);
                    tf.setWeeks(category.getBudget().indexOf(CategoryConstants.W) > -1);
                    tf.setDays(category.getBudget().indexOf(CategoryConstants.D) > -1);
                    tf.setHours(category.getBudget().indexOf(CategoryConstants.h) > -1);
                    tf.setMinutes(category.getBudget().indexOf(CategoryConstants.m) > -1);
                    tf.setSeconds(category.getBudget().indexOf(CategoryConstants.s) > -1);
                    tf.setIcon(category.getIcon());
                }
                tf.setDescription(category.getDescription());
                tf.setHandlerRequired(category.isHandlerRequired());
                tf.setGroupHandlerAllowed(category.isGroupHandlerAllowed());
                canChangeWF = AdapterManager.getInstance().getSecuredCategoryAdapterManager().canChangeWorkflow(sc, catId);
                sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_TASK_CATEGORY_PROPERTIES);
                if (!canChangeWF)
                    sc.setRequestAttribute(request, "workflowName", AdapterManager.getInstance().getSecuredFindAdapterManager().findWorkflowById(sc, category.getWorkflowId()).getName());
                sc.setRequestAttribute(request, "canChangeWF", canChangeWF);
                sc.setRequestAttribute(request, "canEdit", category.canManage());
                sc.setRequestAttribute(request, "categoryId", catId);
                sc.setRequestAttribute(request, "currentCategory", category);
                boolean canManage = category.canManage();
                sc.setRequestAttribute(request, "tabView", new Tab(canManage, false));
                sc.setRequestAttribute(request, "tabEdit", new Tab(canManage, true));
                sc.setRequestAttribute(request, "tabRelations", new Tab(canManage, false));
                sc.setRequestAttribute(request, "tabPermissions", new Tab(canManage, false));
                sc.setRequestAttribute(request, "tabTemplate", new Tab(canManage, false));
                sc.setRequestAttribute(request, "tabTriggers", new Tab(sc.canAction(Action.manageCategories, category.getTaskId()) && category.isAllowedByACL(), false));
            }
            if (canChangeWF) {
                Set workflowSet = new TreeSet(AdapterManager.getInstance().getSecuredWorkflowAdapterManager().getAvailableWorkflowList(sc, id));
                sc.setRequestAttribute(request, "workflowSet", new ArrayList(workflowSet));
            }

            selectTaskTab(sc, id, "tabCategories", request);
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_TASK_CATEGORY_PROPERTIES));

            return mapping.findForward("categoryEditJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            CategoryForm tf = (CategoryForm) form;

            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().taskHeader(tf, sc, request, false);
            String categoryId = tf.getCategoryId();
            if (categoryId == null || categoryId.equals("null") || categoryId.length() == 0) {
                if (tf.getName() != null && tf.getName().trim().length() != 0)
                    categoryId = AdapterManager.getInstance().getSecuredCategoryAdapterManager().createCategory(sc, id, tf.getName(), tf.getWorkflow(), tf.isHandlerRequired(), tf.isGroupHandlerAllowed());
                else
                    return mapping.findForward("categoryListPage");
            }
            if (tf.getName() != null && tf.getName().trim().length() != 0) {
                SecuredCategoryBean cat = AdapterManager.getInstance().getSecuredFindAdapterManager().findCategoryById(sc, categoryId);
                StringBuffer budget = new StringBuffer();


                if (tf.getYears()) {
                    budget.append(CategoryConstants.Y);
                }
                if (tf.getMonths()) {
                    budget.append(CategoryConstants.M);
                }
                if (tf.getWeeks()) {
                    budget.append(CategoryConstants.W);
                }
                if (tf.getDays()) {
                    budget.append(CategoryConstants.D);
                }
                if (tf.getHours()) {
                    budget.append(CategoryConstants.h);
                }
                if (tf.getMinutes()) {
                    budget.append(CategoryConstants.m);
                }
                if (tf.getSeconds()) {
                    budget.append(CategoryConstants.s);
                }

                String icon = tf.getIcon();
                Preferences preferences = new Preferences(cat.getPreferences());
                preferences.setShowInToolbar(tf.getShowInToolbar());
                preferences.setDefaultLink(tf.getDefaultLink());
                preferences.setSortOrgerInTree(tf.isSortOrderInTree());
                preferences.setHiddenInTree(tf.getHiddenInTree());
                preferences.setViewCategory(tf.getViewCategory());
                preferences.setHandlerOnlyRole(tf.isHanderlOnlyRole());
                StringBuffer budgetResult = new StringBuffer();
                budgetResult.append(budget.toString());

                AdapterManager.getInstance().getSecuredCategoryAdapterManager().editCategory(sc, categoryId,
                        tf.getName(), tf.getAction(), tf.getDescription(), tf.isHandlerRequired(), tf.isGroupHandlerAllowed(), tf.getWorkflow(), budgetResult.toString(), preferences.getPreferences(), icon);
            }
            tf.setCategoryId(categoryId);
            tf.setMutable(false);

            return mapping.findForward("categoryViewPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }
}
