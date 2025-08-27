package com.trackstudio.action.task;

import java.util.ArrayList;
import java.util.Set;
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
import com.trackstudio.containers.PrstatusListItem;
import com.trackstudio.exception.GranException;
import com.trackstudio.form.CategoryForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.manager.CategoryManager;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.secured.SecuredCategoryBean;
import com.trackstudio.secured.SecuredPrstatusBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.securedkernel.SecuredCategoryAdapterManager;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.Tab;


public class CategoryPermissionAction extends TSDispatchAction {
    private static Log log = LogFactory.getLog(CategoryPermissionAction.class);

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.trace("##########");
        CategoryForm tf = (CategoryForm) form;
        SessionContext sc = (SessionContext) request.getAttribute("sc");
        String id = GeneralAction.getInstance().taskHeader(tf, sc, request, true);
        if (!sc.canAction(Action.manageCategories, id))
            return null;

        SecuredTaskBean tci = new SecuredTaskBean(id, sc);
        String catId = tf.getCategoryId() == null ? tci.getCategoryId() : tf.getCategoryId();
        SecuredCategoryBean category = AdapterManager.getInstance().getSecuredFindAdapterManager().findCategoryById(sc, catId);

//        ArrayList<SecuredPrstatusBean> prstatusSet = AdapterManager.getInstance().getSecuredPrstatusAdapterManager().getAvailablePrstatusList(sc, sc.getUserId());
        Set<SecuredPrstatusBean> prstatusSet = new TreeSet(AdapterManager.getInstance().getSecuredPrstatusAdapterManager().getAvailablePrstatusList(sc, sc.getUserId()));
        ArrayList<PrstatusListItem> cannotviewStatuses = new ArrayList<PrstatusListItem>();
        ArrayList<PrstatusListItem> canviewStatuses = new ArrayList<PrstatusListItem>();
        ArrayList<PrstatusListItem> cannoteditStatuses = new ArrayList<PrstatusListItem>();
        ArrayList<PrstatusListItem> caneditStatuses = new ArrayList<PrstatusListItem>();
        ArrayList<PrstatusListItem> cannotcreateStatuses = new ArrayList<PrstatusListItem>();
        ArrayList<PrstatusListItem> cancreateStatuses = new ArrayList<PrstatusListItem>();
        ArrayList<PrstatusListItem> cannotdeleteStatuses = new ArrayList<PrstatusListItem>();
        ArrayList<PrstatusListItem> candeleteStatuses = new ArrayList<PrstatusListItem>();
        ArrayList<PrstatusListItem> cannothandlerStatuses = new ArrayList<PrstatusListItem>();
        ArrayList<PrstatusListItem> canhandlerStatuses = new ArrayList<PrstatusListItem>();
        StringBuffer canview = new StringBuffer(), canedit = new StringBuffer(), cancreate = new StringBuffer(), candelete = new StringBuffer(), canhandler = new StringBuffer();

        boolean canManageCategory = category.canManage();
        for (SecuredPrstatusBean prstatus : prstatusSet) {
            if (category.canManage() && prstatus.canView() || !category.canManage() && prstatus.isAllowedByACL()) {
                ArrayList<String> localtypes = AdapterManager.getInstance().getSecuredCategoryAdapterManager().getCategoryRuleList(sc, prstatus.getId(), category.getId());
                String ret = null;
                if (localtypes.contains(CategoryConstants.CREATE_ALL))
                    ret = "";
                else if (localtypes.contains(CategoryConstants.CREATE_SUBMITTER))
                    ret = I18n.getString(sc.getLocale(), "SUBMITTER");
                else if (localtypes.contains(CategoryConstants.CREATE_HANDLER))
                    ret = I18n.getString(sc.getLocale(), "HANDLER");
                else if (localtypes.contains(CategoryConstants.CREATE_SUBMITTER_AND_HANDLER))
                    ret = I18n.getString(sc.getLocale(), "SUBMITTER") + ", " + I18n.getString(sc.getLocale(), "HANDLER");
                if (ret != null && ret.length() > 0)
                    ret = " (* " + ret + ")";
                String s1 = ret;
                if (s1 != null) {
                    cancreateStatuses.add(new PrstatusListItem(prstatus.getId() + s1, prstatus.getName() + s1));
                    cancreate.append(prstatus.getId()).append(s1).append(FValue.DELIM);
                } else {
                    cannotcreateStatuses.add(new PrstatusListItem(prstatus.getId(), prstatus.getName()));
                }
                ret = null;
                if (localtypes.contains(CategoryConstants.VIEW_ALL))
                    ret = "";
                else if (localtypes.contains(CategoryConstants.VIEW_SUBMITTER))
                    ret = I18n.getString(sc.getLocale(), "SUBMITTER");
                if (ret != null && ret.length() > 0)
                    ret = " (*" + ret + ")";
                String s2 = ret;
                if (s2 != null) {
                    canviewStatuses.add(new PrstatusListItem(prstatus.getId() + s2, prstatus.getName() + s2));
                    canview.append(prstatus.getId()).append(s2).append(FValue.DELIM);
                } else {
                    cannotviewStatuses.add(new PrstatusListItem(prstatus.getId(), prstatus.getName()));
                }
                ret = null;
                if (localtypes.contains(CategoryConstants.DELETE_ALL))
                    ret = "";
                else if (localtypes.contains(CategoryConstants.DELETE_SUBMITTER))
                    ret = I18n.getString(sc.getLocale(), "SUBMITTER");
                else if (localtypes.contains(CategoryConstants.DELETE_HANDLER))
                    ret = I18n.getString(sc.getLocale(), "HANDLER");
                else if (localtypes.contains(CategoryConstants.DELETE_SUBMITTER_AND_HANDLER))
                    ret = I18n.getString(sc.getLocale(), "SUBMITTER") + ", " + I18n.getString(sc.getLocale(), "HANDLER");
                if (ret != null && ret.length() > 0)
                    ret = " (* " + ret + ")";
                String s3 = ret;
                if (s3 != null) {
                    candeleteStatuses.add(new PrstatusListItem(prstatus.getId() + s3, prstatus.getName() + s3));
                    candelete.append(prstatus.getId()).append(s3).append(FValue.DELIM);
                } else {
                    cannotdeleteStatuses.add(new PrstatusListItem(prstatus.getId(), prstatus.getName()));
                }
                ret = null;
                if (localtypes.contains(CategoryConstants.EDIT_ALL))
                    ret = "";
                else if (localtypes.contains(CategoryConstants.EDIT_SUBMITTER))
                    ret = I18n.getString(sc.getLocale(), "SUBMITTER");
                else if (localtypes.contains(CategoryConstants.EDIT_HANDLER))
                    ret = I18n.getString(sc.getLocale(), "HANDLER");
                else if (localtypes.contains(CategoryConstants.EDIT_SUBMITTER_AND_HANDLER))
                    ret = I18n.getString(sc.getLocale(), "SUBMITTER") + ", " + I18n.getString(sc.getLocale(), "HANDLER");
                if (ret != null && ret.length() > 0)
                    ret = " (* " + ret + ")";
                String s4 = ret;
                if (s4 != null) {
                    caneditStatuses.add(new PrstatusListItem(prstatus.getId() + s4, prstatus.getName() + s4));
                    canedit.append(prstatus.getId()).append(s4).append(FValue.DELIM);
                } else {
                    cannoteditStatuses.add(new PrstatusListItem(prstatus.getId(), prstatus.getName()));
                }
                ret = null;
                if (localtypes.contains(CategoryConstants.BE_HANDLER_ALL))
                    ret = "";
                else if (localtypes.contains(CategoryConstants.BE_HANDLER_SUBMITTER))
                    ret = I18n.getString(sc.getLocale(), "SUBMITTER");
                else if (localtypes.contains(CategoryConstants.BE_HANDLER_HANDLER))
                    ret = I18n.getString(sc.getLocale(), "HANDLER");
                else if (localtypes.contains(CategoryConstants.BE_HANDLER_SUBMITTER_AND_HANDLER))
                    ret = I18n.getString(sc.getLocale(), "SUBMITTER") + ", " + I18n.getString(sc.getLocale(), "HANDLER");
                if (ret != null && ret.length() > 0)
                    ret = " (* " + ret + ")";
                String s5 = ret;
                if (s5 != null) {
                    canhandlerStatuses.add(new PrstatusListItem(prstatus.getId() + s5, prstatus.getName() + s5));
                    canhandler.append(prstatus.getId()).append(s5).append(FValue.DELIM);
                } else {
                    cannothandlerStatuses.add(new PrstatusListItem(prstatus.getId(), prstatus.getName()));
                }
            }
        }
        tf.setHiddencanedit(canedit.toString());
        tf.setHiddencanview(canview.toString());
        tf.setHiddencancreate(cancreate.toString());
        tf.setHiddencandelete(candelete.toString());
        tf.setHiddencanhandler(canhandler.toString());

        sc.setRequestAttribute(request, "cannotviewStatuses", cannotviewStatuses);
        sc.setRequestAttribute(request, "canviewStatuses", canviewStatuses);

        sc.setRequestAttribute(request, "caneditStatuses", caneditStatuses);
        sc.setRequestAttribute(request, "cannoteditStatuses", cannoteditStatuses);

        sc.setRequestAttribute(request, "canhandlerStatuses", canhandlerStatuses);
        sc.setRequestAttribute(request, "cannothandlerStatuses", cannothandlerStatuses);

        sc.setRequestAttribute(request, "cancreateStatuses", cancreateStatuses);
        sc.setRequestAttribute(request, "cannotcreateStatuses", cannotcreateStatuses);

        sc.setRequestAttribute(request, "candeleteStatuses", candeleteStatuses);
        sc.setRequestAttribute(request, "cannotdeleteStatuses", cannotdeleteStatuses);
        sc.setRequestAttribute(request, "createObjectAction", "/CategoryPermissionAction.do");

        TreeSet<SecuredPrstatusBean> invalideCreateList = new TreeSet<SecuredPrstatusBean>();
        TreeSet<SecuredPrstatusBean> invalideEditList = new TreeSet<SecuredPrstatusBean>();
        TreeSet<SecuredPrstatusBean> invalideDeleteList = new TreeSet<SecuredPrstatusBean>();
        TreeSet<SecuredPrstatusBean> invalideBeHandlerList = new TreeSet<SecuredPrstatusBean>();
        for (SecuredPrstatusBean prstatus : prstatusSet) {
            int correctness = KernelManager.getCategory().checkCategoryForPrstatus(category.getId(), prstatus.getId());
            if ((correctness & CategoryManager.EDIT_INCORRECT)!=0) invalideEditList.add(prstatus);
            if ((correctness & CategoryManager.CREATE_INCORRECT)!=0) invalideCreateList.add(prstatus);
            if ((correctness & CategoryManager.DELETE_INCORRECT)!=0) invalideDeleteList.add(prstatus);
            if ((correctness & CategoryManager.BEHANDLER_INCORRECT)!=0) invalideBeHandlerList.add(prstatus);
        }

        sc.setRequestAttribute(request, "invalideEditList", invalideEditList);
        sc.setRequestAttribute(request, "invalideCreateList", invalideCreateList);
        sc.setRequestAttribute(request, "invalideDeleteList", invalideDeleteList);
        sc.setRequestAttribute(request, "invalideBeHandlerList", invalideBeHandlerList);

        boolean isValidEdit = invalideEditList.isEmpty();
        boolean isValidCreate = invalideCreateList.isEmpty();
        boolean isValidDelete = invalideDeleteList.isEmpty();
        boolean isValidBeHandler = invalideBeHandlerList.isEmpty();

        sc.setRequestAttribute(request, "isValidEdit", isValidEdit);
        sc.setRequestAttribute(request, "isValidCreate", isValidCreate);
        sc.setRequestAttribute(request, "isValidDelete", isValidDelete);
        sc.setRequestAttribute(request, "isValidBeHandler", isValidBeHandler);
        sc.setRequestAttribute(request, "isValid", isValidEdit && isValidCreate && isValidDelete && isValidBeHandler);

        // Tab attributes
        boolean canManageRoles = sc.canAction(Action.manageRoles, sc.getUserId());
        sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_TASK_CATEGORY_PERMISSIONS);
        sc.setRequestAttribute(request, "canEdit", canManageCategory || canManageRoles);
        sc.setRequestAttribute(request, "categoryId", catId);
        sc.setRequestAttribute(request, "currentCategory", category);
        sc.setRequestAttribute(request, "tabView", new Tab(canManageCategory || canManageRoles, false));
        sc.setRequestAttribute(request, "tabEdit", new Tab(canManageCategory, false));
        sc.setRequestAttribute(request, "tabRelations", new Tab(canManageCategory, false));
        sc.setRequestAttribute(request, "tabPermissions", new Tab(canManageCategory || canManageRoles, true));
        sc.setRequestAttribute(request, "tabTriggers", new Tab(sc.canAction(Action.manageCategories, category.getTaskId()) && category.canUpdate(), false));
        sc.setRequestAttribute(request, "tabTemplate", new Tab(canManageCategory, false));
        selectTaskTab(sc, id, "tabCategories", request);
        sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_TASK_CATEGORY_PERMISSIONS));
        return mapping.findForward("categoryPermissionJSP");
    }

    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.trace("##########");
        CategoryForm tf = (CategoryForm) form;
        SessionContext sc = (SessionContext) request.getAttribute("sc");
        SecuredCategoryAdapterManager cam = AdapterManager.getInstance().getSecuredCategoryAdapterManager();
        String catId = tf.getCategoryId();

        ArrayList<SecuredPrstatusBean> prstatuses = AdapterManager.getInstance().getSecuredPrstatusAdapterManager().getAvailablePrstatusList(sc, sc.getUserId());
        String canedit = tf.getHiddencanedit();
        String canview = tf.getHiddencanview();
        String cancreate = tf.getHiddencancreate();
        String candelete = tf.getHiddencandelete();
        String canhandler = tf.getHiddencanhandler();
        ArrayList<String> canViewPrstatuses = new ArrayList<String>();

        ArrayList<String> canViewPrstatusesSubmitter = new ArrayList<String>();

        ArrayList<String> canEditPrstatuses = new ArrayList<String>();
        ArrayList<String> canEditPrstatusesHandler = new ArrayList<String>();
        ArrayList<String> canEditPrstatusesSubmitter = new ArrayList<String>();
        ArrayList<String> canEditPrstatusesSubmitterAndHandler = new ArrayList<String>();

        ArrayList<String> canCreatePrstatuses = new ArrayList<String>();
        ArrayList<String> canCreatePrstatusesHandler = new ArrayList<String>();
        ArrayList<String> canCreatePrstatusesSubmitter = new ArrayList<String>();
        ArrayList<String> canCreatePrstatusesSubmitterAndHandler = new ArrayList<String>();

        ArrayList<String> canDeletePrstatuses = new ArrayList<String>();
        ArrayList<String> canDeletePrstatusesHandler = new ArrayList<String>();
        ArrayList<String> canDeletePrstatusesSubmitter = new ArrayList<String>();
        ArrayList<String> canDeletePrstatusesSubmitterAndHandler = new ArrayList<String>();


        ArrayList<String> canBeHandlerPrstatuses = new ArrayList<String>();
        ArrayList<String> canBeHandlerPrstatusesHandler = new ArrayList<String>();
        ArrayList<String> canBeHandlerPrstatusesSubmitter = new ArrayList<String>();
        ArrayList<String> canBeHandlerPrstatusesSubmitterAndHandler = new ArrayList<String>();

        parseForm(sc, canview, null, null, canViewPrstatusesSubmitter, canViewPrstatuses);
        parseForm(sc, canedit, canEditPrstatusesSubmitterAndHandler, canEditPrstatusesHandler, canEditPrstatusesSubmitter, canEditPrstatuses);
        parseForm(sc, cancreate, canCreatePrstatusesSubmitterAndHandler, canCreatePrstatusesHandler, canCreatePrstatusesSubmitter, canCreatePrstatuses);
        parseForm(sc, candelete, canDeletePrstatusesSubmitterAndHandler, canDeletePrstatusesHandler, canDeletePrstatusesSubmitter, canDeletePrstatuses);
        parseForm(sc, canhandler, canBeHandlerPrstatusesSubmitterAndHandler, canBeHandlerPrstatusesHandler, canBeHandlerPrstatusesSubmitter, canBeHandlerPrstatuses);

        SecuredCategoryBean category = AdapterManager.getInstance().getSecuredFindAdapterManager().findCategoryById(sc, catId);
        for (SecuredPrstatusBean prstatus : prstatuses) {
            if (category.canManage() && prstatus.canView() || !category.canManage() && prstatus.isAllowedByACL()) {
                String prstatusId = prstatus.getId();
                String view = null, edit = null, create = null, delete = null, handler = null;
                if (canViewPrstatuses.contains(prstatusId)) {
                    view = CategoryConstants.VIEW_ALL;

                } else if (canViewPrstatusesSubmitter.contains(prstatusId)) {
                    view = CategoryConstants.VIEW_SUBMITTER;
                }

                if (canEditPrstatuses.contains(prstatusId)) {
                    edit = CategoryConstants.EDIT_ALL;
                } else if (canEditPrstatusesHandler.contains(prstatusId)) {
                    edit = CategoryConstants.EDIT_HANDLER;
                } else if (canEditPrstatusesSubmitter.contains(prstatusId)) {
                    edit = CategoryConstants.EDIT_SUBMITTER;
                } else if (canEditPrstatusesSubmitterAndHandler.contains(prstatusId)) {
                    edit = CategoryConstants.EDIT_SUBMITTER_AND_HANDLER;
                }

                if (canCreatePrstatuses.contains(prstatusId)) {
                    create = CategoryConstants.CREATE_ALL;
                } else if (canCreatePrstatusesHandler.contains(prstatusId)) {
                    create = CategoryConstants.CREATE_HANDLER;
                } else if (canCreatePrstatusesSubmitter.contains(prstatusId)) {
                    create = CategoryConstants.CREATE_SUBMITTER;
                } else if (canCreatePrstatusesSubmitterAndHandler.contains(prstatusId)) {
                    create = CategoryConstants.CREATE_SUBMITTER_AND_HANDLER;
                }

                if (canDeletePrstatuses.contains(prstatusId)) {
                    delete = CategoryConstants.DELETE_ALL;
                } else if (canDeletePrstatusesHandler.contains(prstatusId)) {
                    delete = CategoryConstants.DELETE_HANDLER;
                } else if (canDeletePrstatusesSubmitter.contains(prstatusId)) {
                    delete = CategoryConstants.DELETE_SUBMITTER;
                } else if (canDeletePrstatusesSubmitterAndHandler.contains(prstatusId)) {
                    delete = CategoryConstants.DELETE_SUBMITTER_AND_HANDLER;
                }

                if (canBeHandlerPrstatuses.contains(prstatusId)) {
                    handler = CategoryConstants.BE_HANDLER_ALL;
                } else if (canBeHandlerPrstatusesHandler.contains(prstatusId)) {
                    handler = CategoryConstants.BE_HANDLER_HANDLER;
                } else if (canBeHandlerPrstatusesSubmitter.contains(prstatusId)) {
                    handler = CategoryConstants.BE_HANDLER_SUBMITTER;
                } else if (canBeHandlerPrstatusesSubmitterAndHandler.contains(prstatusId)) {
                    handler = CategoryConstants.BE_HANDLER_SUBMITTER_AND_HANDLER;
                }

                cam.setCategoryRule(sc, catId, prstatusId, create, view, edit, handler, delete);
            }
        }
        return mapping.findForward("categoryViewPage");
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
                    String prstatusId = token.substring(0, posSpec - 1).trim();
                    if (canViewPrstatusesSubmitterAndHandler != null && posHanlder > -1 && posSubmitter > -1) {
                        canViewPrstatusesSubmitterAndHandler.add(prstatusId);
                    } else if (canViewPrstatusesHandler != null && posHanlder > -1 && posSubmitter == -1) {
                        canViewPrstatusesHandler.add(prstatusId);
                    } else if (posHanlder == -1 && posSubmitter > -1) {
                        canViewPrstatusesSubmitter.add(prstatusId);
                    }
                } else {
                    canViewPrstatuses.add(token);
                }
            }
        }
    }


}
