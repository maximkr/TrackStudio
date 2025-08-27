package com.trackstudio.action.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import com.trackstudio.exception.UserException;
import com.trackstudio.form.CategoryForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.cache.TaskRelatedInfo;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.model.Category;
import com.trackstudio.secured.SecuredCategoryBean;
import com.trackstudio.secured.SecuredPrstatusBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.EggBasket;
import com.trackstudio.tools.Tab;

public class CategoryAction extends TSDispatchAction {
    private static Log log = LogFactory.getLog(CategoryAction.class);

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            CategoryForm tf = (CategoryForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().taskHeader(tf, sc, request, true);
            if (!sc.canAction(Action.manageCategories, id))
                return null;
            SecuredTaskBean tci = new SecuredTaskBean(id, sc);

            String catId = tf.getCategoryId() == null ? tci.getCategoryId() : tf.getCategoryId();
            SecuredCategoryBean currentCategory = AdapterManager.getInstance().getSecuredFindAdapterManager().findCategoryById(sc, catId);
            ArrayList<SecuredCategoryBean> categorySet = AdapterManager.getInstance().getSecuredCategoryAdapterManager().getAllAvailableCategoryList(sc, id);
            ArrayList<SecuredCategoryBean> categories = new ArrayList<SecuredCategoryBean>();
            EggBasket<SecuredTaskBean, SecuredCategoryBean> parentCategories = new EggBasket<SecuredTaskBean, SecuredCategoryBean>();
            EggBasket<SecuredTaskBean, SecuredCategoryBean> childrenCategories = new EggBasket<SecuredTaskBean, SecuredCategoryBean>();
            ArrayList<EggBasket> seeAlso = new ArrayList<EggBasket>();
            ArrayList<SecuredTaskBean> parentTasks = AdapterManager.getInstance().getSecuredTaskAdapterManager().getTaskChain(sc, null, id);

            for (SecuredCategoryBean scb : categorySet) {
                SecuredTaskBean task = scb.getTask();
                if (task.canView() && sc.canAction(Action.manageCategories, task.getId())) {
                    if (scb.getTaskId().equals(id)) {
                        categories.add(scb);
                        tf.setValue("workflow-" + scb.getId(), scb.getWorkflowId());
                    } else if (parentTasks.contains(task)) {
                        parentCategories.putItem(task, scb);
                    } else {
                        childrenCategories.putItem(task, scb);
                    }
                }
            }
            Collections.sort(categories);
            sc.setRequestAttribute(request, "categoryId", catId);
            sc.setRequestAttribute(request, "currentCategory", currentCategory);
            sc.setRequestAttribute(request, "currentCategoryId", currentCategory.getId());

            sc.setRequestAttribute(request, "categorySet", categories);

            if (!parentCategories.isEmpty())
                seeAlso.add(parentCategories);
            if (!childrenCategories.isEmpty())
                seeAlso.add(childrenCategories);
            boolean canManage = sc.canAction(Action.manageCategories, id) && sc.allowedByACL(id);

            sc.setRequestAttribute(request, "seeAlso", seeAlso);

            sc.setRequestAttribute(request, "canManage", canManage);
            if (canManage) {
                sc.setRequestAttribute(request, "msgHowCreateObject", I18n.getString(sc.getLocale(), "CATEGORY_ADD"));
                sc.setRequestAttribute(request, "msgAddObject", I18n.getString(sc.getLocale(), "CATEGORY_ADD"));
                sc.setRequestAttribute(request, "createObjectAction", "/CategoryAction.do");
            }
            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_TASK_CATEGORY_LIST);
            sc.setRequestAttribute(request, "tabCategories", new Tab(true, true));
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_TASK_CATEGORY_LIST));
            return mapping.findForward("categoryListJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward delete(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            CategoryForm tf = (CategoryForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String[] categories = tf.getDelete();
            if (categories != null) {
                List<String[]> exceptions = new ArrayList<String[]>();
                for (String categoryId : categories) {
                    List<TaskRelatedInfo> tasks = KernelManager.getTask().getTaskCategoryList(categoryId);
                    Category category = KernelManager.getFind().findCategory(categoryId);
                    if (tasks.size() > 0) {
                        String exception = "";
                        for (TaskRelatedInfo task : tasks) {
                            exception += "#" + task.getNumber() + "; ";
                        }
                        exceptions.add(new String[]{category.getName(), exception});
                        continue;
                    }
                    AdapterManager.getInstance().getSecuredCategoryAdapterManager().deleteCategory(sc, categoryId);
                }
                if (exceptions.size() > 0) {
                    UserException categoryException = new UserException(I18n.getString("ERROR_CAN_NOT_DELETE_CATEGORY"), false);
                    for (String[] message : exceptions) {
                        UserException ue = new UserException(I18n.getString("CATEGORY_EXCEPTION_CONSTRAINT", new Object[]{message[0], message[1]}), false);
                        categoryException.addActionMessages(ue.getActionMessages());
                    }
                    throw categoryException;
                }
            }
            tf.setCategoryId(null);
            tf.setMutable(false);
            return mapping.findForward("categoryListPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                HttpServletResponse response) throws Exception {
        log.trace("##########");
        return mapping.findForward("categoryEditPage");
    }

    public ActionForward changeHideCategory(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            CategoryForm f = (CategoryForm) form;
            Preferences p = new Preferences(KernelManager.getFind().findCategory(f.getCategoryId()).getPreferences());
            p.setHideCategory(!Preferences.isCategoryHidden(p.getPreferences()));
            KernelManager.getCategory().setPreferences(f.getCategoryId(), p.getPreferences());
            return mapping.findForward("categoryViewPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward clone(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            CategoryForm tf = (CategoryForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String[] categories = tf.getDelete();
            if (categories != null) {
                for (String cloneCategoryId : categories) {
                    String id = GeneralAction.getInstance().taskHeader(tf, sc, request, false);
                    SecuredCategoryBean oldCategory = AdapterManager.getInstance().getSecuredFindAdapterManager().findCategoryById(sc, cloneCategoryId);

                    String categoryId = AdapterManager.getInstance().getSecuredCategoryAdapterManager().createCategory(sc, id, oldCategory.getName() + "_clone", oldCategory.getWorkflow().getId(), oldCategory.isHandlerRequired(), oldCategory.isGroupHandlerAllowed());
                    SecuredCategoryBean category = AdapterManager.getInstance().getSecuredFindAdapterManager().findCategoryById(sc, categoryId);
                    AdapterManager.getInstance().getSecuredCategoryAdapterManager().editCategory(sc, categoryId, category.getName(), oldCategory.getAction(), oldCategory.getDescription(), oldCategory.isHandlerRequired(), oldCategory.isGroupHandlerAllowed(), oldCategory.getWorkflow().getId(), oldCategory.getBudget(), oldCategory.getPreferences(), oldCategory.getIcon());

                    ArrayList<SecuredCategoryBean> categoryList = AdapterManager.getInstance().getSecuredCategoryAdapterManager().getParentCategoryList(sc, oldCategory.getId(), id);
                    for (SecuredCategoryBean c : categoryList) {
                        AdapterManager.getInstance().getSecuredCategoryAdapterManager().addRelatedCategory(sc, c.getId(), categoryId);
                    }

                    AdapterManager.getInstance().getSecuredCategoryAdapterManager().setCategoryTrigger(sc, categoryId, oldCategory.getCreateBefore(), oldCategory.getCreateInsteadOf(), oldCategory.getCreateAfter(), oldCategory.getUpdateBefore(), oldCategory.getUpdateInsteadOf(), oldCategory.getUpdateAfter());

                    AdapterManager.getInstance().getSecuredCategoryAdapterManager().setTemplate(sc, categoryId, oldCategory.getTemplate());

                    ArrayList<SecuredPrstatusBean> prstatuses = AdapterManager.getInstance().getSecuredPrstatusAdapterManager().getAvailablePrstatusList(sc, sc.getUserId());
                    for (SecuredPrstatusBean prstatus : prstatuses) {
                        ArrayList<String> localtypes = AdapterManager.getInstance().getSecuredCategoryAdapterManager().getCategoryRuleList(sc, prstatus.getId(), oldCategory.getId());

                        String create = localtypes.contains(CategoryConstants.CREATE_ALL) ? CategoryConstants.CREATE_ALL : "";
                        if (localtypes.contains(CategoryConstants.CREATE_ALL)) {
                            create = CategoryConstants.CREATE_ALL;
                        } else if (localtypes.contains(CategoryConstants.CREATE_HANDLER)) {
                            create = CategoryConstants.CREATE_HANDLER;
                        } else if (localtypes.contains(CategoryConstants.CREATE_SUBMITTER)) {
                            create = CategoryConstants.CREATE_SUBMITTER;
                        } else if (localtypes.contains(CategoryConstants.CREATE_SUBMITTER_AND_HANDLER)) {
                            create = CategoryConstants.CREATE_SUBMITTER_AND_HANDLER;
                        }

                        String view = localtypes.contains(CategoryConstants.VIEW_ALL) ? CategoryConstants.VIEW_ALL : "";
                        if (localtypes.contains(CategoryConstants.VIEW_ALL)) {
                            view = CategoryConstants.VIEW_ALL;
                        } else if (localtypes.contains(CategoryConstants.VIEW_SUBMITTER)) {
                            view = CategoryConstants.VIEW_SUBMITTER;
                        }

                        String edit = "";
                        if (localtypes.contains(CategoryConstants.EDIT_ALL)) {
                            edit = CategoryConstants.EDIT_ALL;
                        } else if (localtypes.contains(CategoryConstants.EDIT_HANDLER)) {
                            edit = CategoryConstants.EDIT_HANDLER;
                        } else if (localtypes.contains(CategoryConstants.EDIT_SUBMITTER)) {
                            edit = CategoryConstants.EDIT_SUBMITTER;
                        } else if (localtypes.contains(CategoryConstants.EDIT_SUBMITTER_AND_HANDLER)) {
                            edit = CategoryConstants.EDIT_SUBMITTER_AND_HANDLER;
                        }
                        String handler = "";
                        if (localtypes.contains(CategoryConstants.BE_HANDLER_ALL)) {
                            handler = CategoryConstants.BE_HANDLER_ALL;
                        } else if (localtypes.contains(CategoryConstants.BE_HANDLER_HANDLER)) {
                            handler = CategoryConstants.BE_HANDLER_HANDLER;
                        } else if (localtypes.contains(CategoryConstants.BE_HANDLER_SUBMITTER)) {
                            handler = CategoryConstants.BE_HANDLER_SUBMITTER;
                        } else if (localtypes.contains(CategoryConstants.BE_HANDLER_SUBMITTER_AND_HANDLER)) {
                            handler = CategoryConstants.BE_HANDLER_SUBMITTER_AND_HANDLER;
                        }
                        String delete = "";
                        if (localtypes.contains(CategoryConstants.DELETE_ALL)) {
                            delete = CategoryConstants.DELETE_ALL;
                        } else if (localtypes.contains(CategoryConstants.DELETE_HANDLER)) {
                            delete = CategoryConstants.DELETE_HANDLER;
                        } else if (localtypes.contains(CategoryConstants.DELETE_SUBMITTER)) {
                            delete = CategoryConstants.DELETE_SUBMITTER;
                        } else if (localtypes.contains(CategoryConstants.DELETE_SUBMITTER_AND_HANDLER)) {
                            delete = CategoryConstants.DELETE_SUBMITTER_AND_HANDLER;
                        }

                        AdapterManager.getInstance().getSecuredCategoryAdapterManager().setCategoryRule(sc, categoryId, prstatus.getId(), create, view, edit, handler, delete);
                    }
                }
            }
            tf.setCategoryId(null);
            tf.setMutable(false);
            return mapping.findForward("categoryListPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }
}
