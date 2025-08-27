package com.trackstudio.action.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
import com.trackstudio.containers.RuleListItem;
import com.trackstudio.form.CategoryForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.manager.CategoryManager;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.secured.SecuredCategoryBean;
import com.trackstudio.secured.SecuredPrstatusBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.Tab;
import com.trackstudio.tools.formatter.HourFormatter;
import com.trackstudio.tools.textfilter.MacrosUtil;

public class CategoryViewAction extends TSDispatchAction {
    private static Log log = LogFactory.getLog(CategoryEditAction.class);


    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");

            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().taskHeader(null, sc, request, true);
            boolean canManageRoles = sc.canAction(Action.manageRoles, sc.getUserId());
            if (!sc.canAction(Action.manageCategories, id) && !canManageRoles)
                return null;
            CategoryForm cf = (CategoryForm) form;
            String catId = cf.getCategoryId() != null ? cf.getCategoryId() : request.getParameter("categoryId");
            SecuredCategoryBean category = AdapterManager.getInstance().getSecuredFindAdapterManager().findCategoryById(sc, catId);
            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_TASK_CATEGORY_OVERVIEW);
            sc.setRequestAttribute(request, "categoryId", catId);
            sc.setRequestAttribute(request, "workflow", category.getWorkflow().getName());
            sc.setRequestAttribute(request, "category", category.getName());
            sc.setRequestAttribute(request, "icon", category.getIcon());
            sc.setRequestAttribute(request, "currentCategory", category);
            sc.setRequestAttribute(request, "showInToolbar", Preferences.showInToolbar(category.getPreferences()));
            sc.setRequestAttribute(request, "defaultLink", Preferences.getDefaultLink(category.getPreferences()).equals(Preferences.VIEW_TASK_PROPERTIES));
            sc.setRequestAttribute(request, "isCategoryHidden", Preferences.isCategoryHidden(category.getPreferences()));
            sc.setRequestAttribute(request, "sortOrderInTree", Preferences.isSortOrgerInTree(category.getPreferences()));
            sc.setRequestAttribute(request, "hiddenInTree", Preferences.getHiddenInTree(category.getPreferences()));
            sc.setRequestAttribute(request, "useGoogleCalendar", Preferences.isUseGoogleCalendar(category.getPreferences()));
            sc.setRequestAttribute(request, "useYandexCalendar", Preferences.isUseYandexCalendar(category.getPreferences()));
            sc.setRequestAttribute(request, "viewCategory", Preferences.getViewCategory(category.getPreferences()));
            sc.setRequestAttribute(request, "handlerOnlyRole", Preferences.isHandlerOnlyRole(category.getPreferences()));
            String budget = category.getBudget() == null ? "" : category.getBudget();
            Long time = HourFormatter.parseInput(budget.indexOf(CategoryConstants.Y) > -1 ? Long.valueOf(Math.round(Math.random() * 10) + 1).toString() : null, budget.indexOf(CategoryConstants.M) > -1 ? Long.valueOf(Math.round(Math.random() * 10) + 1).toString() : null, budget.indexOf(CategoryConstants.W) > -1 ? Long.valueOf(Math.round(Math.random() * 10) + 1).toString() : null, budget.indexOf(CategoryConstants.D) > -1 ? Long.valueOf(Math.round(Math.random() * 10) + 1).toString() : null, Long.valueOf(Math.round(Math.random() * 10) + 1).toString(), Long.valueOf(Math.round(Math.random() * 10) + 1).toString(), Long.valueOf(Math.round(Math.random() * 10) + 1).toString());
            HourFormatter hf = new HourFormatter(time, category.getBudget(), sc.getLocale());
            sc.setRequestAttribute(request, "budgetSample", hf.getString());
            ArrayList<SecuredCategoryBean> list = AdapterManager.getInstance().getSecuredCategoryAdapterManager().getParentCategoryList(sc, catId, id);

            Collections.sort(list);
            boolean canManageCategory = category.canManage();
            boolean canViewPermissions = canManageCategory || canManageRoles;
            if (canViewPermissions) {
                ArrayList<SecuredPrstatusBean> prstatusSet = AdapterManager.getInstance().getSecuredPrstatusAdapterManager().getAvailablePrstatusList(sc, sc.getUserId());
                ArrayList<RuleListItem> rulesC = new ArrayList<RuleListItem>();
                ArrayList<RuleListItem> rulesV = new ArrayList<RuleListItem>();
                ArrayList<RuleListItem> rulesE = new ArrayList<RuleListItem>();
                ArrayList<RuleListItem> rulesD = new ArrayList<RuleListItem>();
                ArrayList<RuleListItem> rulesH = new ArrayList<RuleListItem>();
                for (SecuredPrstatusBean prstatus : prstatusSet) {
                    List types = AdapterManager.getInstance().getSecuredCategoryAdapterManager().getCategoryRuleList(sc, prstatus.getId(), category.getId());
                    if (types != null) {
                        String ret = null;
                        if (types.contains(CategoryConstants.CREATE_ALL))
                            ret = "";
                        else if (types.contains(CategoryConstants.CREATE_SUBMITTER))
                            ret = I18n.getString(sc.getLocale(), "SUBMITTER");
                        else if (types.contains(CategoryConstants.CREATE_HANDLER))
                            ret = I18n.getString(sc.getLocale(), "HANDLER");
                        else if (types.contains(CategoryConstants.CREATE_SUBMITTER_AND_HANDLER))
                            ret = I18n.getString(sc.getLocale(), "SUBMITTER") + ", " + I18n.getString(sc.getLocale(), "HANDLER");
                        if (ret != null && ret.length() > 0)
                            ret = " (* " + ret + ")";
                        if (ret != null) {
                            String ret1 = null;
                            if (types.contains(CategoryConstants.CREATE_ALL))
                                ret1 = "";
                            else if (types.contains(CategoryConstants.CREATE_SUBMITTER))
                                ret1 = I18n.getString(sc.getLocale(), "SUBMITTER");
                            else if (types.contains(CategoryConstants.CREATE_HANDLER))
                                ret1 = I18n.getString(sc.getLocale(), "HANDLER");
                            else if (types.contains(CategoryConstants.CREATE_SUBMITTER_AND_HANDLER))
                                ret1 = I18n.getString(sc.getLocale(), "SUBMITTER") + ", " + I18n.getString(sc.getLocale(), "HANDLER");
                            if (ret1 != null && ret1.length() > 0)
                                ret1 = " (* " + ret1 + ")";
                            rulesC.add(new RuleListItem(prstatus, ret1));
                            Collections.sort(rulesC);
                        }
                        String ret4 = null;
                        if (types.contains(CategoryConstants.EDIT_ALL))
                            ret4 = "";
                        else if (types.contains(CategoryConstants.EDIT_SUBMITTER))
                            ret4 = I18n.getString(sc.getLocale(), "SUBMITTER");
                        else if (types.contains(CategoryConstants.EDIT_HANDLER))
                            ret4 = I18n.getString(sc.getLocale(), "HANDLER");
                        else if (types.contains(CategoryConstants.EDIT_SUBMITTER_AND_HANDLER))
                            ret4 = I18n.getString(sc.getLocale(), "SUBMITTER") + ", " + I18n.getString(sc.getLocale(), "HANDLER");
                        if (ret4 != null && ret4.length() > 0)
                            ret4 = " (* " + ret4 + ")";
                        if (ret4 != null) {
                            String ret1 = null;
                            if (types.contains(CategoryConstants.EDIT_ALL))
                                ret1 = "";
                            else if (types.contains(CategoryConstants.EDIT_SUBMITTER))
                                ret1 = I18n.getString(sc.getLocale(), "SUBMITTER");
                            else if (types.contains(CategoryConstants.EDIT_HANDLER))
                                ret1 = I18n.getString(sc.getLocale(), "HANDLER");
                            else if (types.contains(CategoryConstants.EDIT_SUBMITTER_AND_HANDLER))
                                ret1 = I18n.getString(sc.getLocale(), "SUBMITTER") + ", " + I18n.getString(sc.getLocale(), "HANDLER");
                            if (ret1 != null && ret1.length() > 0)
                                ret1 = " (* " + ret1 + ")";
                            rulesE.add(new RuleListItem(prstatus, ret1));
                            Collections.sort(rulesE);
                        }
                        String ret5 = null;
                        if (types.contains(CategoryConstants.DELETE_ALL))
                            ret5 = "";
                        else if (types.contains(CategoryConstants.DELETE_SUBMITTER))
                            ret5 = I18n.getString(sc.getLocale(), "SUBMITTER");
                        else if (types.contains(CategoryConstants.DELETE_HANDLER))
                            ret5 = I18n.getString(sc.getLocale(), "HANDLER");
                        else if (types.contains(CategoryConstants.DELETE_SUBMITTER_AND_HANDLER))
                            ret5 = I18n.getString(sc.getLocale(), "SUBMITTER") + ", " + I18n.getString(sc.getLocale(), "HANDLER");
                        if (ret5 != null && ret5.length() > 0)
                            ret5 = " (* " + ret5 + ")";
                        if (ret5 != null) {
                            String ret1 = null;
                            if (types.contains(CategoryConstants.DELETE_ALL))
                                ret1 = "";
                            else if (types.contains(CategoryConstants.DELETE_SUBMITTER))
                                ret1 = I18n.getString(sc.getLocale(), "SUBMITTER");
                            else if (types.contains(CategoryConstants.DELETE_HANDLER))
                                ret1 = I18n.getString(sc.getLocale(), "HANDLER");
                            else if (types.contains(CategoryConstants.DELETE_SUBMITTER_AND_HANDLER))
                                ret1 = I18n.getString(sc.getLocale(), "SUBMITTER") + ", " + I18n.getString(sc.getLocale(), "HANDLER");
                            if (ret1 != null && ret1.length() > 0)
                                ret1 = " (* " + ret1 + ")";
                            rulesD.add(new RuleListItem(prstatus, ret1));
                            Collections.sort(rulesD);
                        }
                        String ret2 = null;
                        if (types.contains(CategoryConstants.BE_HANDLER_ALL))
                            ret2 = "";
                        else if (types.contains(CategoryConstants.BE_HANDLER_SUBMITTER))
                            ret2 = I18n.getString(sc.getLocale(), "SUBMITTER");
                        else if (types.contains(CategoryConstants.BE_HANDLER_HANDLER))
                            ret2 = I18n.getString(sc.getLocale(), "HANDLER");
                        else if (types.contains(CategoryConstants.BE_HANDLER_SUBMITTER_AND_HANDLER))
                            ret2 = I18n.getString(sc.getLocale(), "SUBMITTER") + ", " + I18n.getString(sc.getLocale(), "HANDLER");
                        if (ret2 != null && ret2.length() > 0)
                            ret2 = " (* " + ret2 + ")";
                        if (ret2 != null) {
                            String ret1 = null;
                            if (types.contains(CategoryConstants.BE_HANDLER_ALL))
                                ret1 = "";
                            else if (types.contains(CategoryConstants.BE_HANDLER_SUBMITTER))
                                ret1 = I18n.getString(sc.getLocale(), "SUBMITTER");
                            else if (types.contains(CategoryConstants.BE_HANDLER_HANDLER))
                                ret1 = I18n.getString(sc.getLocale(), "HANDLER");
                            else if (types.contains(CategoryConstants.BE_HANDLER_SUBMITTER_AND_HANDLER))
                                ret1 = I18n.getString(sc.getLocale(), "SUBMITTER") + ", " + I18n.getString(sc.getLocale(), "HANDLER");
                            if (ret1 != null && ret1.length() > 0)
                                ret1 = " (* " + ret1 + ")";
                            rulesH.add(new RuleListItem(prstatus, ret1));
                            Collections.sort(rulesH);
                        }
                        String ret3 = null;
                        if (types.contains(CategoryConstants.VIEW_ALL))
                            ret3 = "";
                        else if (types.contains(CategoryConstants.VIEW_SUBMITTER))
                            ret3 = I18n.getString(sc.getLocale(), "SUBMITTER");
                        if (ret3 != null && ret3.length() > 0)
                            ret3 = " (*" + ret3 + ")";
                        if (ret3 != null) {
                            String ret1 = null;
                            if (types.contains(CategoryConstants.VIEW_ALL))
                                ret1 = "";
                            else if (types.contains(CategoryConstants.VIEW_SUBMITTER))
                                ret1 = I18n.getString(sc.getLocale(), "SUBMITTER");
                            if (ret1 != null && ret1.length() > 0)
                                ret1 = " (*" + ret1 + ")";
                            rulesV.add(new RuleListItem(prstatus, ret1));
                            Collections.sort(rulesV);
                        }
                    }
                }
                sc.setRequestAttribute(request, "rulesC", MacrosUtil.getMatrixAttachment(rulesC, 4));
                sc.setRequestAttribute(request, "rulesE", MacrosUtil.getMatrixAttachment(rulesE, 4));
                sc.setRequestAttribute(request, "rulesD", MacrosUtil.getMatrixAttachment(rulesD, 4));
                sc.setRequestAttribute(request, "rulesV", MacrosUtil.getMatrixAttachment(rulesV, 4));
                sc.setRequestAttribute(request, "rulesH", MacrosUtil.getMatrixAttachment(rulesH, 4));
            }

            boolean canViewTriggers = sc.canAction(Action.manageCategories, category.getTaskId());
            if (canViewTriggers) {
                sc.setRequestAttribute(request, "crBefore", category.getCreateBefore() != null ? category.getCreateBefore() : I18n.getString(sc.getLocale(), "NONE"));
                sc.setRequestAttribute(request, "crInsteadOf", category.getCreateInsteadOf() != null ? category.getCreateInsteadOf() : I18n.getString(sc.getLocale(), "NONE"));
                sc.setRequestAttribute(request, "crAfter", category.getCreateAfter() != null ? category.getCreateAfter() : I18n.getString(sc.getLocale(), "NONE"));
                sc.setRequestAttribute(request, "updBefore", category.getUpdateBefore() != null ? category.getUpdateBefore() : I18n.getString(sc.getLocale(), "NONE"));
                sc.setRequestAttribute(request, "updInsteadOf", category.getUpdateInsteadOf() != null ? category.getUpdateInsteadOf() : I18n.getString(sc.getLocale(), "NONE"));
                sc.setRequestAttribute(request, "updAfter", category.getUpdateAfter() != null ? category.getUpdateAfter() : I18n.getString(sc.getLocale(), "NONE"));
            }

            if (canManageCategory) {
                String template = category.getTemplate();
                if (template != null && template.length() > 0)
                    sc.setRequestAttribute(request, "template", template);
            }

            Boolean isValidParentCategory = category.isValidParentCategory();
            sc.setRequestAttribute(request, "isValidParentCategory", isValidParentCategory);

            Boolean isValideWorkflow = category.getWorkflow().isValid();
            sc.setRequestAttribute(request, "isValideWorkflow", isValideWorkflow);

            TreeSet<SecuredPrstatusBean> invalideCreateList = new TreeSet<SecuredPrstatusBean>();
            TreeSet<SecuredPrstatusBean> invalideEditList = new TreeSet<SecuredPrstatusBean>();
            TreeSet<SecuredPrstatusBean> invalideDeleteList = new TreeSet<SecuredPrstatusBean>();
            TreeSet<SecuredPrstatusBean> invalideBeHandlerList = new TreeSet<SecuredPrstatusBean>();
            ArrayList<SecuredPrstatusBean> prstatusSet = AdapterManager.getInstance().getSecuredPrstatusAdapterManager().getAvailablePrstatusList(sc, sc.getUserId());
            for (SecuredPrstatusBean prstatus : prstatusSet) {
                int correctness = KernelManager.getCategory().checkCategoryForPrstatus(category.getId(), prstatus.getId());
                if ((correctness & CategoryManager.EDIT_INCORRECT) != 0) invalideEditList.add(prstatus);
                if ((correctness & CategoryManager.CREATE_INCORRECT) != 0) invalideCreateList.add(prstatus);
                if ((correctness & CategoryManager.DELETE_INCORRECT) != 0) invalideDeleteList.add(prstatus);
                if ((correctness & CategoryManager.BEHANDLER_INCORRECT) != 0) invalideBeHandlerList.add(prstatus);

            }

            sc.setRequestAttribute(request, "invalideEditList", invalideEditList);
            sc.setRequestAttribute(request, "invalideCreateList", invalideCreateList);
            sc.setRequestAttribute(request, "invalideDeleteList", invalideDeleteList);
            sc.setRequestAttribute(request, "invalideBeHandlerList", invalideBeHandlerList);

            boolean isValidEdit = invalideEditList.isEmpty();
            boolean isValidCreate = invalideCreateList.isEmpty();
            boolean isValidDelete = invalideDeleteList.isEmpty();
            boolean isValidBeHandler = invalideBeHandlerList.isEmpty();
            boolean isValid = isValidParentCategory && isValideWorkflow && isValidCreate && isValidBeHandler && isValidEdit && isValidDelete;

            sc.setRequestAttribute(request, "isValidEdit", isValidEdit);
            sc.setRequestAttribute(request, "isValidCreate", isValidCreate);
            sc.setRequestAttribute(request, "isValidDelete", isValidDelete);
            sc.setRequestAttribute(request, "isValidBeHandler", isValidBeHandler);
            sc.setRequestAttribute(request, "isValid", isValid);

            sc.setRequestAttribute(request, "canViewTriggers", canViewTriggers);
            sc.setRequestAttribute(request, "canManage", canManageCategory);
            sc.setRequestAttribute(request, "canViewPermissions", canViewPermissions);
            sc.setRequestAttribute(request, "tabView", new Tab(canManageCategory || canManageRoles, true));
            sc.setRequestAttribute(request, "tabEdit", new Tab(canManageCategory, false));
            sc.setRequestAttribute(request, "tabRelations", new Tab(canManageCategory, false));
            sc.setRequestAttribute(request, "tabPermissions", new Tab(canManageCategory || canManageRoles, false));
            sc.setRequestAttribute(request, "tabTemplate", new Tab(canManageCategory, false));
            sc.setRequestAttribute(request, "tabTriggers", new Tab(canManageCategory, false));
            sc.setRequestAttribute(request, "childCategories", list);
            selectTaskTab(sc, id, "tabCategories", request);
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_TASK_CATEGORY_OVERVIEW));
            return mapping.findForward("categoryViewJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }
}