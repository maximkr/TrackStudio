package com.trackstudio.action.user;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
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
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.filter.FValue;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.constants.CategoryConstants;
import com.trackstudio.exception.GranException;
import com.trackstudio.form.PrstatusForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.cache.ActionCacheManager;
import com.trackstudio.secured.SecuredCategoryBean;
import com.trackstudio.secured.SecuredPrstatusBean;
import com.trackstudio.securedkernel.SecuredCategoryAdapterManager;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.Tab;
import com.trackstudio.view.UserViewHTMLLinked;

public class UserCategorySecurityAction extends TSDispatchAction {
    private static Log log = LogFactory.getLog(UserStatusEditAction.class);

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            PrstatusForm pf = (PrstatusForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().userHeader(pf, sc, request);
            if (!sc.canAction(Action.manageRoles, id))
                return null;
            String prstatusId = pf.getPrstatusId();
            SecuredPrstatusBean prstatus = AdapterManager.getInstance().getSecuredFindAdapterManager().findPrstatusById(sc, prstatusId);
            pf.setName(prstatus.getName());
            boolean statusEditable = prstatus.canManage();

            List<Action> choosedCurrent = ActionCacheManager.getInstance().getActions(sc.getPrstatusId());
            // Tab attributes
            sc.setRequestAttribute(request, "owner", new UserViewHTMLLinked(prstatus.getUser(), request.getContextPath()).getPath());
            sc.setRequestAttribute(request, "currentPrstatus", prstatus);
            sc.setRequestAttribute(request, "name", prstatus.getName());
            sc.setRequestAttribute(request, "prstatusId", prstatusId);
            sc.setRequestAttribute(request, "canView", sc.canAction(Action.manageRoles, id));
            boolean canView = prstatus.canManage();
            sc.setRequestAttribute(request, "tabEdit", new Tab(statusEditable, false));
            sc.setRequestAttribute(request, "tabTaskFieldSecurity", new Tab(true, false));
            sc.setRequestAttribute(request, "tabUserFieldSecurity", new Tab(true, false));
            sc.setRequestAttribute(request, "tabUserSecurity", new Tab(canView, false));
            sc.setRequestAttribute(request, "tabTaskSecurity", new Tab(canView, false));
            sc.setRequestAttribute(request, "tabCategorySecurity", new Tab(choosedCurrent.contains(Action.manageCategories), true));
            sc.setRequestAttribute(request, "tabWorkflowSecurity", new Tab(choosedCurrent.contains(Action.manageWorkflows), false));
            sc.setRequestAttribute(request, "tabView", new Tab(sc.canAction(Action.manageRoles, id), false));
            sc.setRequestAttribute(request, "canView", sc.canAction(Action.manageRoles, id));
            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_USER_STATUS_CATEGORY_SECURITY);
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_USER_STATUS_CATEGORY_SECURITY));

            TreeSet<SecuredCategoryBean> categories = new TreeSet<SecuredCategoryBean>(AdapterManager.getInstance().getSecuredCategoryAdapterManager().getAllCategoryList(sc, prstatus.getId()));

            ArrayList<SecuredCategoryBean> categoryViewAll = new ArrayList<SecuredCategoryBean>();
            ArrayList<SecuredCategoryBean> categoryViewNone = new ArrayList<SecuredCategoryBean>();
            ArrayList<SecuredCategoryBean> categoryViewSubmitter = new ArrayList<SecuredCategoryBean>();

            ArrayList<SecuredCategoryBean> categoryCreateAll = new ArrayList<SecuredCategoryBean>();
            ArrayList<SecuredCategoryBean> categoryCreateNone = new ArrayList<SecuredCategoryBean>();
            ArrayList<SecuredCategoryBean> categoryCreateSubmitter = new ArrayList<SecuredCategoryBean>();
            ArrayList<SecuredCategoryBean> categoryCreateHandler = new ArrayList<SecuredCategoryBean>();
            ArrayList<SecuredCategoryBean> categoryCreateSAH = new ArrayList<SecuredCategoryBean>();

            ArrayList<SecuredCategoryBean> categoryEditAll = new ArrayList<SecuredCategoryBean>();
            ArrayList<SecuredCategoryBean> categoryEditNone = new ArrayList<SecuredCategoryBean>();
            ArrayList<SecuredCategoryBean> categoryEditSubmitter = new ArrayList<SecuredCategoryBean>();
            ArrayList<SecuredCategoryBean> categoryEditHandler = new ArrayList<SecuredCategoryBean>();
            ArrayList<SecuredCategoryBean> categoryEditSAH = new ArrayList<SecuredCategoryBean>();

            ArrayList<SecuredCategoryBean> categoryBeHandlerAll = new ArrayList<SecuredCategoryBean>();
            ArrayList<SecuredCategoryBean> categoryBeHandlerNone = new ArrayList<SecuredCategoryBean>();
            ArrayList<SecuredCategoryBean> categoryBeHandlerSubmitter = new ArrayList<SecuredCategoryBean>();
            ArrayList<SecuredCategoryBean> categoryBeHandlerHandler = new ArrayList<SecuredCategoryBean>();
            ArrayList<SecuredCategoryBean> categoryBeHandlerSAH = new ArrayList<SecuredCategoryBean>();

            ArrayList<SecuredCategoryBean> categoryDeleteAll = new ArrayList<SecuredCategoryBean>();
            ArrayList<SecuredCategoryBean> categoryDeleteNone = new ArrayList<SecuredCategoryBean>();
            ArrayList<SecuredCategoryBean> categoryDeleteSubmitter = new ArrayList<SecuredCategoryBean>();
            ArrayList<SecuredCategoryBean> categoryDeleteHandler = new ArrayList<SecuredCategoryBean>();
            ArrayList<SecuredCategoryBean> categoryDeleteSAH = new ArrayList<SecuredCategoryBean>();


            for (SecuredCategoryBean category : categories) {
                List types = AdapterManager.getInstance().getSecuredCategoryAdapterManager().getCategoryRuleList(sc, prstatusId, category.getId());
                if (types.contains(CategoryConstants.CREATE_ALL))
                    categoryCreateAll.add(category);
                else if (types.contains(CategoryConstants.CREATE_SUBMITTER))
                    categoryCreateSubmitter.add(category);
                else if (types.contains(CategoryConstants.CREATE_HANDLER))
                    categoryCreateHandler.add(category);
                else if (types.contains(CategoryConstants.CREATE_SUBMITTER_AND_HANDLER))
                    categoryCreateSAH.add(category);
                else categoryCreateNone.add(category);


                if (types.contains(CategoryConstants.EDIT_ALL))
                    categoryEditAll.add(category);
                else if (types.contains(CategoryConstants.EDIT_SUBMITTER))
                    categoryEditSubmitter.add(category);
                else if (types.contains(CategoryConstants.EDIT_HANDLER))
                    categoryEditHandler.add(category);
                else if (types.contains(CategoryConstants.EDIT_SUBMITTER_AND_HANDLER))
                    categoryEditSAH.add(category);
                else
                    categoryEditNone.add(category);

                if (types.contains(CategoryConstants.DELETE_ALL))
                    categoryDeleteAll.add(category);
                else if (types.contains(CategoryConstants.DELETE_SUBMITTER))
                    categoryDeleteSubmitter.add(category);
                else if (types.contains(CategoryConstants.DELETE_HANDLER))
                    categoryDeleteHandler.add(category);
                else if (types.contains(CategoryConstants.DELETE_SUBMITTER_AND_HANDLER))
                    categoryDeleteSAH.add(category);
                else categoryDeleteNone.add(category);

                if (types.contains(CategoryConstants.BE_HANDLER_ALL))
                    categoryBeHandlerAll.add(category);
                else if (types.contains(CategoryConstants.BE_HANDLER_SUBMITTER))
                    categoryBeHandlerSubmitter.add(category);
                else if (types.contains(CategoryConstants.BE_HANDLER_HANDLER))
                    categoryBeHandlerHandler.add(category);
                else if (types.contains(CategoryConstants.BE_HANDLER_SUBMITTER_AND_HANDLER))
                    categoryBeHandlerSAH.add(category);
                else categoryBeHandlerNone.add(category);

                if (types.contains(CategoryConstants.VIEW_ALL))
                    categoryViewAll.add(category);
                else if (types.contains(CategoryConstants.VIEW_SUBMITTER))
                    categoryViewSubmitter.add(category);
                else categoryViewNone.add(category);

            }


            sc.setRequestAttribute(request, "categoryViewAll", categoryViewAll);
            sc.setRequestAttribute(request, "categoryViewNone", categoryViewNone);
            sc.setRequestAttribute(request, "categoryViewSubmitter", categoryViewSubmitter);

            sc.setRequestAttribute(request, "categoryCreateAll", categoryCreateAll);
            sc.setRequestAttribute(request, "categoryCreateNone", categoryCreateNone);
            sc.setRequestAttribute(request, "categoryCreateSubmitter", categoryCreateSubmitter);
            sc.setRequestAttribute(request, "categoryCreateHandler", categoryCreateHandler);
            sc.setRequestAttribute(request, "categoryCreateSAH", categoryCreateSAH);

            sc.setRequestAttribute(request, "categoryEditAll", categoryEditAll);
            sc.setRequestAttribute(request, "categoryEditNone", categoryEditNone);
            sc.setRequestAttribute(request, "categoryEditSubmitter", categoryEditSubmitter);
            sc.setRequestAttribute(request, "categoryEditHandler", categoryEditHandler);
            sc.setRequestAttribute(request, "categoryEditSAH", categoryEditSAH);

            sc.setRequestAttribute(request, "categoryBeHandlerAll", categoryBeHandlerAll);
            sc.setRequestAttribute(request, "categoryBeHandlerNone", categoryBeHandlerNone);
            sc.setRequestAttribute(request, "categoryBeHandlerSubmitter", categoryBeHandlerSubmitter);
            sc.setRequestAttribute(request, "categoryBeHandlerHandler", categoryBeHandlerHandler);
            sc.setRequestAttribute(request, "categoryBeHandlerSAH", categoryBeHandlerSAH);

            sc.setRequestAttribute(request, "categoryDeleteAll", categoryDeleteAll);
            sc.setRequestAttribute(request, "categoryDeleteNone", categoryDeleteNone);
            sc.setRequestAttribute(request, "categoryDeleteSubmitter", categoryDeleteSubmitter);
            sc.setRequestAttribute(request, "categoryDeleteHandler", categoryDeleteHandler);
            sc.setRequestAttribute(request, "categoryDeleteSAH", categoryDeleteSAH);


            return mapping.findForward("userCategorySecurityJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public static String getCategoryURL(SecuredCategoryBean scb, String contextPath, String session) {
        return "<a href=\"" + contextPath + "/CategoryViewAction.do?method=page&amp;categoryId=" + scb.getId() + "&amp;id=" + scb.getTaskId() + "\">&nbsp;" + scb.getName() + "</a>";

    }

    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            PrstatusForm tf = (PrstatusForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String prstatusId = tf.getPrstatusId();
            SecuredCategoryAdapterManager cam = AdapterManager.getInstance().getSecuredCategoryAdapterManager();
            TreeSet<SecuredCategoryBean> categories = new TreeSet<SecuredCategoryBean>(AdapterManager.getInstance().getSecuredCategoryAdapterManager().getAllCategoryList(sc, prstatusId));

            String canedit = tf.getHiddencanedit();
            String canview = tf.getHiddencanview();
            String cancreate = tf.getHiddencancreate();
            String candelete = tf.getHiddencandelete();
            String canhandler = tf.getHiddencanhandler();
            ArrayList<String> canViewCategories = new ArrayList<String>();

            ArrayList<String> canViewCategoriesSubmitter = new ArrayList<String>();

            ArrayList<String> canEditCategories = new ArrayList<String>();
            ArrayList<String> canEditCategoriesHandler = new ArrayList<String>();
            ArrayList<String> canEditCategoriesSubmitter = new ArrayList<String>();
            ArrayList<String> canEditCategoriesSubmitterAndHandler = new ArrayList<String>();

            ArrayList<String> canCreateCategories = new ArrayList<String>();
            ArrayList<String> canCreateCategoriesHandler = new ArrayList<String>();
            ArrayList<String> canCreateCategoriesSubmitter = new ArrayList<String>();
            ArrayList<String> canCreateCategoriesSubmitterAndHandler = new ArrayList<String>();

            ArrayList<String> canDeleteCategories = new ArrayList<String>();
            ArrayList<String> canDeleteCategoriesHandler = new ArrayList<String>();
            ArrayList<String> canDeleteCategoriesSubmitter = new ArrayList<String>();
            ArrayList<String> canDeleteCategoriesSubmitterAndHandler = new ArrayList<String>();


            ArrayList<String> canBeHandlerCategories = new ArrayList<String>();
            ArrayList<String> canBeHandlerCategoriesHandler = new ArrayList<String>();
            ArrayList<String> canBeHandlerCategoriesSubmitter = new ArrayList<String>();
            ArrayList<String> canBeHandlerCategoriesSubmitterAndHandler = new ArrayList<String>();

            parseForm(sc, canview, null, null, canViewCategoriesSubmitter, canViewCategories);
            parseForm(sc, canedit, canEditCategoriesSubmitterAndHandler, canEditCategoriesHandler, canEditCategoriesSubmitter, canEditCategories);
            parseForm(sc, cancreate, canCreateCategoriesSubmitterAndHandler, canCreateCategoriesHandler, canCreateCategoriesSubmitter, canCreateCategories);
            parseForm(sc, candelete, canDeleteCategoriesSubmitterAndHandler, canDeleteCategoriesHandler, canDeleteCategoriesSubmitter, canDeleteCategories);
            parseForm(sc, canhandler, canBeHandlerCategoriesSubmitterAndHandler, canBeHandlerCategoriesHandler, canBeHandlerCategoriesSubmitter, canBeHandlerCategories);

            for (SecuredCategoryBean scb : categories) {
                String catId = scb.getId();
                String view = null, edit = null, create = null, delete = null, handler = null;
                if (canViewCategories.contains(scb.getId())) {
                    view = CategoryConstants.VIEW_ALL;

                } else if (canViewCategoriesSubmitter.contains(catId)) {
                    view = CategoryConstants.VIEW_SUBMITTER;
                }

                if (canEditCategories.contains(catId)) {
                    edit = CategoryConstants.EDIT_ALL;
                } else if (canEditCategoriesHandler.contains(catId)) {
                    edit = CategoryConstants.EDIT_HANDLER;
                } else if (canEditCategoriesSubmitter.contains(catId)) {
                    edit = CategoryConstants.EDIT_SUBMITTER;
                } else if (canEditCategoriesSubmitterAndHandler.contains(catId)) {
                    edit = CategoryConstants.EDIT_SUBMITTER_AND_HANDLER;
                }

                if (canCreateCategories.contains(catId)) {
                    create = CategoryConstants.CREATE_ALL;
                } else if (canCreateCategoriesHandler.contains(catId)) {
                    create = CategoryConstants.CREATE_HANDLER;
                } else if (canCreateCategoriesSubmitter.contains(catId)) {
                    create = CategoryConstants.CREATE_SUBMITTER;
                } else if (canCreateCategoriesSubmitterAndHandler.contains(catId)) {
                    create = CategoryConstants.CREATE_SUBMITTER_AND_HANDLER;
                }

                if (canDeleteCategories.contains(catId)) {
                    delete = CategoryConstants.DELETE_ALL;
                } else if (canDeleteCategoriesHandler.contains(catId)) {
                    delete = CategoryConstants.DELETE_HANDLER;
                } else if (canDeleteCategoriesSubmitter.contains(catId)) {
                    delete = CategoryConstants.DELETE_SUBMITTER;
                } else if (canDeleteCategoriesSubmitterAndHandler.contains(catId)) {
                    delete = CategoryConstants.DELETE_SUBMITTER_AND_HANDLER;
                }

                if (canBeHandlerCategories.contains(catId)) {
                    handler = CategoryConstants.BE_HANDLER_ALL;
                } else if (canBeHandlerCategoriesHandler.contains(catId)) {
                    handler = CategoryConstants.BE_HANDLER_HANDLER;
                } else if (canBeHandlerCategoriesSubmitter.contains(catId)) {
                    handler = CategoryConstants.BE_HANDLER_SUBMITTER;
                } else if (canBeHandlerCategoriesSubmitterAndHandler.contains(catId)) {
                    handler = CategoryConstants.BE_HANDLER_SUBMITTER_AND_HANDLER;
                }
                cam.setCategoryRule(sc, catId, prstatusId, create, view, edit, handler, delete);

            }
            return mapping.findForward("userCategorySecurityPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    private void parseForm(SessionContext sc, String canview, ArrayList<String> canViewPrstatusesSubmitterAndHandler, ArrayList<String> canViewPrstatusesHandler, ArrayList<String> canViewPrstatusesSubmitter, ArrayList<String> canViewPrstatuses) throws GranException {
        StringTokenizer tk = new StringTokenizer(canview, FValue.DELIM);
        while (tk.hasMoreElements()) {
            String token = tk.nextToken();
            if (token.length() > 0) {
                int posSpec = token.indexOf("(*");
                if (posSpec > 0) {
                    int posHanlder = token.indexOf(I18n.getString(sc, "HANDLER"));
                    int posSubmitter = token.indexOf(I18n.getString(sc, "SUBMITTER"));
                    String prstatusId = token.substring(0, posSpec - 1);
                    prstatusId = prstatusId.replace('\r', ' ');
                    prstatusId = prstatusId.replace('\n', ' ');
                    prstatusId = prstatusId.trim();
                    if (canViewPrstatusesSubmitterAndHandler != null && posHanlder > -1 && posSubmitter > -1) {
                        canViewPrstatusesSubmitterAndHandler.add(prstatusId);
                    } else if (canViewPrstatusesHandler != null && posHanlder > -1 && posSubmitter == -1) {
                        canViewPrstatusesHandler.add(prstatusId);
                    } else if (posHanlder == -1 && posSubmitter > -1) {
                        canViewPrstatusesSubmitter.add(prstatusId);
                    }
                } else {
                    String prstatusId = token.replace('\r', ' ');
                    prstatusId = prstatusId.replace('\n', ' ');
                    prstatusId = prstatusId.trim();
                    canViewPrstatuses.add(prstatusId);
                }
            }
        }
    }
}