package com.trackstudio.action.task;

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
import com.trackstudio.form.CategoryTemplateForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.secured.SecuredCategoryBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.Tab;
import com.trackstudio.view.TaskViewHTMLShort;

public class CategoryTemplateAction extends TSDispatchAction {
    private static Log log = LogFactory.getLog(CategoryTemplateAction.class);

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            CategoryTemplateForm tf = (CategoryTemplateForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().taskHeader(tf, sc, request, true);
            if (!sc.canAction(Action.manageCategories, id))
                return null;
            SecuredTaskBean tci = new SecuredTaskBean(id, sc);
            String catId = request.getParameter("categoryId");
            SecuredCategoryBean category = AdapterManager.getInstance().getSecuredFindAdapterManager().findCategoryById(sc, catId);
            String template = category.getTemplate();
            if (template != null && template.length() > 0) {
                tf.setTemplate(template);
                sc.setRequestAttribute(request, "template", template);
            }
            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_TASK_CATEGORY_TEMPLATE);
            sc.setRequestAttribute(request, "canEdit", sc.canAction(Action.manageCategories, category.getTaskId()) && sc.canAction(Action.manageCategories, id));
            sc.setRequestAttribute(request, "connected", new TaskViewHTMLShort(tci, request.getContextPath()).getView(category.getTask()).getName());
            sc.setRequestAttribute(request, "categoryId", catId);
            sc.setRequestAttribute(request, "currentCategory", category);
            sc.setRequestAttribute(request, "tabView", new Tab(sc.canAction(Action.manageCategories, category.getTaskId()), false));
            sc.setRequestAttribute(request, "tabEdit", new Tab(category.canManage(), false));
            sc.setRequestAttribute(request, "tabRelations", new Tab(category.canManage(), false));
            sc.setRequestAttribute(request, "tabTemplate", new Tab(sc.canAction(Action.manageCategories, category.getTaskId()) && category.canView(), true));
            sc.setRequestAttribute(request, "tabPermissions", new Tab(sc.canAction(Action.manageCategories, category.getTaskId()), false));
            sc.setRequestAttribute(request, "tabTriggers", new Tab(sc.canAction(Action.manageCategories, category.getTaskId()) && category.canManage(), false));
            selectTaskTab(sc, id, "tabCategories", request);
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_TASK_CATEGORY_TEMPLATE));
            return mapping.findForward("categoryTemplateJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            CategoryTemplateForm tf = (CategoryTemplateForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String categoryId = tf.getCategoryId();

            AdapterManager.getInstance().getSecuredCategoryAdapterManager().setTemplate(sc, categoryId, tf.getTemplate());

            return mapping.findForward("categoryViewPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }
    }
}
