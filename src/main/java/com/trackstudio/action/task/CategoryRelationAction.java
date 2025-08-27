package com.trackstudio.action.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.StringTokenizer;

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
import com.trackstudio.app.filter.FValue;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.form.CategoryForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.secured.SecuredCategoryBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.Tab;

public class CategoryRelationAction extends TSDispatchAction {
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
            SecuredTaskBean tci = new SecuredTaskBean(id, sc);
            String catId = tf.getCategoryId() == null ? tci.getCategoryId() : tf.getCategoryId();
            SecuredCategoryBean category = AdapterManager.getInstance().getSecuredFindAdapterManager().findCategoryById(sc, catId);

            ArrayList<SecuredCategoryBean> categoryList = AdapterManager.getInstance().getSecuredCategoryAdapterManager().getAvailableCategoryList(sc, id);

            ArrayList<SecuredCategoryBean> selectedCategories = AdapterManager.getInstance().getSecuredCategoryAdapterManager().getParentCategoryList(sc, catId, id);
            boolean allowedCategory = category.canManage();
            categoryList.removeAll(selectedCategories);
            ArrayList<SecuredCategoryBean> tmpList = new ArrayList<SecuredCategoryBean>();
            for (SecuredCategoryBean ca : categoryList) {
                boolean flowAllows = ca.canManage() || allowedCategory;
                if (flowAllows) tmpList.add(ca);
            }
            StringBuffer hiddenRelated = new StringBuffer();
            for (SecuredCategoryBean cat : selectedCategories) {
                hiddenRelated.append(cat.getId()).append(FValue.DELIM);
            }

            tf.setHiddento(hiddenRelated.toString());
            categoryList = tmpList;
            Collections.sort(selectedCategories);
            Collections.sort(categoryList);

            Boolean isValidParentCategory = category.isValidParentCategory();
            sc.setRequestAttribute(request, "isValidParentCategory", isValidParentCategory);

            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_TASK_CATEGORY_RELATIONS);
            sc.setRequestAttribute(request, "categoryId", catId);
            sc.setRequestAttribute(request, "currentCategory", category);
            sc.setRequestAttribute(request, "selectedCategories", selectedCategories);
            sc.setRequestAttribute(request, "categoryList", categoryList);
            sc.setRequestAttribute(request, "msgHowCreateObject", I18n.getString(sc.getLocale(), "RELATED_CATEGORY_ADD"));
            sc.setRequestAttribute(request, "firstParamMsg", I18n.getString(sc.getLocale(), "RELATED_CATEGORY_ADD"));
            sc.setRequestAttribute(request, "firstParamName", "addRelation");
            sc.setRequestAttribute(request, "msgAddObject", I18n.getString(sc.getLocale(), "RELATED_CATEGORY_ADD"));
            sc.setRequestAttribute(request, "createObjectAction", "/CategoryRelationAction.do");
            boolean canManage = category.canManage();
            selectTaskTab(sc, id, "tabCategories", request);
            sc.setRequestAttribute(request, "tabView", new Tab(canManage, false));

            sc.setRequestAttribute(request, "tabEdit", new Tab(canManage, false));
            sc.setRequestAttribute(request, "tabRelations", new Tab(canManage, true));
            sc.setRequestAttribute(request, "tabPermissions", new Tab(canManage, false));
            sc.setRequestAttribute(request, "tabTriggers", new Tab(sc.canAction(Action.manageCategories, category.getTaskId()) && category.canManage(), false));
            sc.setRequestAttribute(request, "tabTemplate", new Tab(canManage, false));
            sc.setRequestAttribute(request, "canEdit", canManage);
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_TASK_CATEGORY_RELATIONS));
            return mapping.findForward("categoryRelationJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            CategoryForm tf = (CategoryForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");

            SecuredCategoryBean parentBean = AdapterManager.getInstance().getSecuredFindAdapterManager().findCategoryById(sc, tf.getCategoryId());

            if (sc.canAction(Action.manageCategories, parentBean.getTaskId())) {
                String fields = tf.getHiddento();
                StringTokenizer tk = new StringTokenizer(fields, FValue.DELIM);
                ArrayList<String> catIds = new ArrayList<String>();
                while (tk.hasMoreElements()) {
                    String token = tk.nextToken();
                    catIds.add(token);
                }
                ArrayList<SecuredCategoryBean> categories = AdapterManager.getInstance().getSecuredCategoryAdapterManager().getAvailableCategoryList(sc, parentBean.getTaskId());
                ArrayList<SecuredCategoryBean> selectedCategories = AdapterManager.getInstance().getSecuredCategoryAdapterManager().getParentCategoryList(sc, tf.getCategoryId(), parentBean.getTaskId());
                for (SecuredCategoryBean c : categories) {
                    if (!catIds.contains(c.getId()))
                        AdapterManager.getInstance().getSecuredCategoryAdapterManager().removeRelatedCategory(sc, c.getId(), tf.getCategoryId());
                    else if (!selectedCategories.contains(c))
                        AdapterManager.getInstance().getSecuredCategoryAdapterManager().addRelatedCategory(sc, c.getId(), tf.getCategoryId());
                }
            }
            return mapping.findForward("categoryRelationPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }
}
