package com.trackstudio.action.user;

import java.util.ArrayList;
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
import com.trackstudio.constants.WorkflowConstants;
import com.trackstudio.form.PrstatusForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.cache.ActionCacheManager;
import com.trackstudio.kernel.cache.TaskAction;
import com.trackstudio.kernel.cache.UserAction;
import com.trackstudio.secured.SecuredCategoryBean;
import com.trackstudio.secured.SecuredNotificationBean;
import com.trackstudio.secured.SecuredPrstatusBean;
import com.trackstudio.secured.SecuredSubscriptionBean;
import com.trackstudio.secured.SecuredUDFBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.Tab;
import com.trackstudio.view.UserViewHTMLLinked;

public class UserStatusViewAction extends TSDispatchAction {
    private static Log log = LogFactory.getLog(UserStatusEditAction.class);

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            PrstatusForm pf = (PrstatusForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().userHeader(pf, sc, request);

            boolean editable = false;
            String prstatusId = pf.getPrstatusId();
            List<Action> choosedCurrent = ActionCacheManager.getInstance().getActions(sc.getPrstatusId());
            if (prstatusId != null && prstatusId.length() != 0) {
                SecuredPrstatusBean prstatus = AdapterManager.getInstance().getSecuredFindAdapterManager().findPrstatusById(sc, prstatusId);
                pf.setName(prstatus.getName());
                editable = prstatus.canManage();
                if (!editable)
                    return null;
                List<Action> choosed = ActionCacheManager.getInstance().getActions(prstatus.getId());
                List<TaskAction> taskSecurity = ActionCacheManager.getInstance().getTaskSecurity();
                List<UserAction> userSecurity = ActionCacheManager.getInstance().getUserSecurity();
                List<UserAction> userFieldSecurity = ActionCacheManager.getInstance().getUserFieldSecurity();
                List<TaskAction> taskFieldSecurity = ActionCacheManager.getInstance().getTaskFieldSecurity();
                for (Action tAction : taskSecurity) {
                    sc.setRequestAttribute(request, tAction.toString(), choosed.contains(tAction));
                }
                for (Action tAction : userSecurity) {
                    sc.setRequestAttribute(request, tAction.toString(), choosed.contains(tAction));
                }
                for (Action tAction : userFieldSecurity) {
                    sc.setRequestAttribute(request, tAction.toString(), choosed.contains(tAction));
                }
                for (Action tAction : taskFieldSecurity) {
                    sc.setRequestAttribute(request, tAction.toString(), choosed.contains(tAction));
                }
                sc.setRequestAttribute(request, "currentPrstatus", prstatus);
                sc.setRequestAttribute(request, "name", prstatus.getName());
                sc.setRequestAttribute(request, "prstatusId", prstatusId);
                sc.setRequestAttribute(request, "owner", new UserViewHTMLLinked(prstatus.getUser(), request.getContextPath()).getPath());
                sc.setRequestAttribute(request, "showInToolbar", Preferences.showInToolbar(prstatus.getPreferences()));
            } else {
                editable = false;

                //sc.setRequestAttribute(request,"prstatusParent", pf.getPrstatusParent());
                sc.setRequestAttribute(request, "name", pf.getName());
                sc.setRequestAttribute(request, "createNewStatus", Boolean.TRUE);
            }

            sc.setRequestAttribute(request, "canView", editable);

            sc.setRequestAttribute(request, "tabEdit", new Tab(editable, false));
            sc.setRequestAttribute(request, "tabTaskFieldSecurity", new Tab(editable, false));
            sc.setRequestAttribute(request, "tabUserFieldSecurity", new Tab(editable, false));
            sc.setRequestAttribute(request, "tabUserSecurity", new Tab(editable, false));
            sc.setRequestAttribute(request, "tabTaskSecurity", new Tab(editable, false));
            sc.setRequestAttribute(request, "tabCategorySecurity", new Tab(choosedCurrent.contains(Action.manageCategories), false));
            sc.setRequestAttribute(request, "tabWorkflowSecurity", new Tab(choosedCurrent.contains(Action.manageWorkflows), false));
            sc.setRequestAttribute(request, "tabView", new Tab(editable, true));

            new StatusViewACLAction().viewACL(prstatusId, request);

            TreeSet<SecuredNotificationBean> retList = new TreeSet<SecuredNotificationBean>();
            List<SecuredNotificationBean> notifications = AdapterManager.getInstance().getSecuredFilterAdapterManager().getPrstatusNotificationList(sc, prstatusId);
            for (SecuredNotificationBean snb : notifications) {
                if (snb.getTaskId() != null && sc.taskOnSight(snb.getTaskId())) {
                    retList.add(snb);
                }
            }
            sc.setRequestAttribute(request, "notifications", retList);

            TreeSet<SecuredSubscriptionBean> retList2 = new TreeSet<SecuredSubscriptionBean>();
            List<SecuredSubscriptionBean> subscriptions = AdapterManager.getInstance().getSecuredFilterAdapterManager().getPrstatusSubscriptionList(sc, prstatusId);
            if (subscriptions != null) {
                for (SecuredSubscriptionBean ssb : subscriptions) {
                    if (ssb.getTaskId() != null && sc.taskOnSight(ssb.getTaskId())) {
                        retList2.add(ssb);
                    }
                }
            }
            sc.setRequestAttribute(request, "subscriptions", new ArrayList<SecuredSubscriptionBean>(retList2));

            ArrayList<SecuredUDFBean> taskUdfViewAll = new ArrayList<SecuredUDFBean>();
            ArrayList<SecuredUDFBean> taskUdfViewHandler = new ArrayList<SecuredUDFBean>();
            ArrayList<SecuredUDFBean> taskUdfViewSubmitter = new ArrayList<SecuredUDFBean>();
            ArrayList<SecuredUDFBean> taskUdfViewSAH = new ArrayList<SecuredUDFBean>();
            ArrayList<SecuredUDFBean> taskUdfEditAll = new ArrayList<SecuredUDFBean>();
            ArrayList<SecuredUDFBean> taskUdfEditHandler = new ArrayList<SecuredUDFBean>();
            ArrayList<SecuredUDFBean> taskUdfEditSubmitter = new ArrayList<SecuredUDFBean>();
            ArrayList<SecuredUDFBean> taskUdfEditSAH = new ArrayList<SecuredUDFBean>();

            ArrayList<SecuredUDFBean> userUdfViewAll = new ArrayList<SecuredUDFBean>();
            ArrayList<SecuredUDFBean> userUdfEditAll = new ArrayList<SecuredUDFBean>();

            Set<SecuredUDFBean> udfs = new TreeSet<SecuredUDFBean>(AdapterManager.getInstance().getSecuredUDFAdapterManager().getAllAvailableTaskUdfListForStatus(sc, prstatusId));
            for (SecuredUDFBean udf : udfs) {

                List localtypes = AdapterManager.getInstance().getSecuredUDFAdapterManager().getUDFRuleList(sc, prstatusId, udf.getId());
                if (localtypes.contains(CategoryConstants.VIEW_ALL))
                    taskUdfViewAll.add(udf);
                else if (localtypes.contains(CategoryConstants.VIEW_SUBMITTER))
                    taskUdfViewSubmitter.add(udf);
                else if (localtypes.contains(WorkflowConstants.VIEW_HANDLER))
                    taskUdfViewHandler.add(udf);
                else if (localtypes.contains(WorkflowConstants.VIEW_SUBMITTER_AND_HANDLER))
                    taskUdfViewSAH.add(udf);

                if (localtypes.contains(CategoryConstants.EDIT_ALL))
                    taskUdfEditAll.add(udf);
                else if (localtypes.contains(CategoryConstants.EDIT_SUBMITTER))
                    taskUdfEditSubmitter.add(udf);
                else if (localtypes.contains(CategoryConstants.EDIT_HANDLER))
                    taskUdfEditHandler.add(udf);
                else if (localtypes.contains(CategoryConstants.EDIT_SUBMITTER_AND_HANDLER))
                    taskUdfEditSAH.add(udf);
            }

            udfs = new TreeSet(AdapterManager.getInstance().getSecuredUDFAdapterManager().getAllAvailableUserUdfListForStatus(sc, prstatusId));

            for (SecuredUDFBean udf : udfs) {
                List localtypes = AdapterManager.getInstance().getSecuredUDFAdapterManager().getUDFRuleList(sc, prstatusId, udf.getId());
                if (localtypes.contains(CategoryConstants.VIEW_ALL))
                    userUdfViewAll.add(udf);


                if (localtypes.contains(CategoryConstants.EDIT_ALL))
                    userUdfEditAll.add(udf);

            }

            // Category permissions

            ArrayList<SecuredCategoryBean> categories = AdapterManager.getInstance().getSecuredCategoryAdapterManager().getAllCategoryList(sc, prstatusId);

            ArrayList<SecuredCategoryBean> categoryViewAll = new ArrayList<SecuredCategoryBean>();
            ArrayList<SecuredCategoryBean> categoryViewSubmitter = new ArrayList<SecuredCategoryBean>();

            ArrayList<SecuredCategoryBean> categoryCreateAll = new ArrayList<SecuredCategoryBean>();
            ArrayList<SecuredCategoryBean> categoryCreateSubmitter = new ArrayList<SecuredCategoryBean>();
            ArrayList<SecuredCategoryBean> categoryCreateHandler = new ArrayList<SecuredCategoryBean>();
            ArrayList<SecuredCategoryBean> categoryCreateSAH = new ArrayList<SecuredCategoryBean>();

            ArrayList<SecuredCategoryBean> categoryEditAll = new ArrayList<SecuredCategoryBean>();
            ArrayList<SecuredCategoryBean> categoryEditSubmitter = new ArrayList<SecuredCategoryBean>();
            ArrayList<SecuredCategoryBean> categoryEditHandler = new ArrayList<SecuredCategoryBean>();
            ArrayList<SecuredCategoryBean> categoryEditSAH = new ArrayList<SecuredCategoryBean>();

            ArrayList<SecuredCategoryBean> categoryBeHandlerAll = new ArrayList<SecuredCategoryBean>();
            ArrayList<SecuredCategoryBean> categoryBeHandlerSubmitter = new ArrayList<SecuredCategoryBean>();
            ArrayList<SecuredCategoryBean> categoryBeHandlerHandler = new ArrayList<SecuredCategoryBean>();
            ArrayList<SecuredCategoryBean> categoryBeHandlerSAH = new ArrayList<SecuredCategoryBean>();

            ArrayList<SecuredCategoryBean> categoryDeleteAll = new ArrayList<SecuredCategoryBean>();
            ArrayList<SecuredCategoryBean> categoryDeleteSubmitter = new ArrayList<SecuredCategoryBean>();
            ArrayList<SecuredCategoryBean> categoryDeleteHandler = new ArrayList<SecuredCategoryBean>();
            ArrayList<SecuredCategoryBean> categoryDeleteSAH = new ArrayList<SecuredCategoryBean>();

            for (SecuredCategoryBean category : categories) {
                List localtypes = AdapterManager.getInstance().getSecuredCategoryAdapterManager().getCategoryRuleList(sc, prstatusId, category.getId());

                if (localtypes.contains(CategoryConstants.VIEW_ALL))
                    categoryViewAll.add(category);
                else if (localtypes.contains(CategoryConstants.VIEW_SUBMITTER))
                    categoryViewSubmitter.add(category);


                if (localtypes.contains(CategoryConstants.CREATE_ALL))
                    categoryCreateAll.add(category);
                else if (localtypes.contains(CategoryConstants.CREATE_SUBMITTER))
                    categoryCreateSubmitter.add(category);
                else if (localtypes.contains(CategoryConstants.CREATE_HANDLER))
                    categoryCreateHandler.add(category);
                else if (localtypes.contains(CategoryConstants.CREATE_SUBMITTER_AND_HANDLER))
                    categoryCreateSAH.add(category);

                if (localtypes.contains(CategoryConstants.EDIT_ALL))
                    categoryEditAll.add(category);
                else if (localtypes.contains(CategoryConstants.EDIT_SUBMITTER))
                    categoryEditSubmitter.add(category);
                else if (localtypes.contains(CategoryConstants.EDIT_HANDLER))
                    categoryEditHandler.add(category);
                else if (localtypes.contains(CategoryConstants.EDIT_SUBMITTER_AND_HANDLER))
                    categoryEditSAH.add(category);

                if (localtypes.contains(CategoryConstants.DELETE_ALL))
                    categoryDeleteAll.add(category);
                else if (localtypes.contains(CategoryConstants.DELETE_SUBMITTER))
                    categoryDeleteSubmitter.add(category);
                else if (localtypes.contains(CategoryConstants.DELETE_HANDLER))
                    categoryDeleteHandler.add(category);
                else if (localtypes.contains(CategoryConstants.DELETE_SUBMITTER_AND_HANDLER))
                    categoryDeleteSAH.add(category);

                if (localtypes.contains(CategoryConstants.BE_HANDLER_ALL))
                    categoryBeHandlerAll.add(category);
                else if (localtypes.contains(CategoryConstants.BE_HANDLER_SUBMITTER))
                    categoryBeHandlerSubmitter.add(category);
                else if (localtypes.contains(CategoryConstants.BE_HANDLER_HANDLER))
                    categoryBeHandlerHandler.add(category);
                else if (localtypes.contains(CategoryConstants.BE_HANDLER_SUBMITTER_AND_HANDLER))
                    categoryBeHandlerSAH.add(category);

            }
            sc.setRequestAttribute(request, "taskUdfViewAll", taskUdfViewAll);
            sc.setRequestAttribute(request, "taskUdfViewHandler", taskUdfViewHandler);
            sc.setRequestAttribute(request, "taskUdfViewSubmitter", taskUdfViewSubmitter);
            sc.setRequestAttribute(request, "taskUdfViewSAH", taskUdfViewSAH);
            sc.setRequestAttribute(request, "taskUdfEditAll", taskUdfEditAll);
            sc.setRequestAttribute(request, "taskUdfEditHandler", taskUdfEditHandler);
            sc.setRequestAttribute(request, "taskUdfEditSubmitter", taskUdfEditSubmitter);
            sc.setRequestAttribute(request, "taskUdfEditSAH", taskUdfEditSAH);

            sc.setRequestAttribute(request, "userUdfViewAll", userUdfViewAll);
            sc.setRequestAttribute(request, "userUdfEditAll", userUdfEditAll);

            sc.setRequestAttribute(request, "categoryViewAll", categoryViewAll);
            sc.setRequestAttribute(request, "categoryViewSubmitter", categoryViewSubmitter);


            sc.setRequestAttribute(request, "categoryCreateAll", categoryCreateAll);
            sc.setRequestAttribute(request, "categoryCreateSubmitter", categoryCreateSubmitter);
            sc.setRequestAttribute(request, "categoryCreateHandler", categoryCreateHandler);
            sc.setRequestAttribute(request, "categoryCreateSAH", categoryCreateSAH);

            sc.setRequestAttribute(request, "categoryEditAll", categoryEditAll);
            sc.setRequestAttribute(request, "categoryEditSubmitter", categoryEditSubmitter);
            sc.setRequestAttribute(request, "categoryEditHandler", categoryEditHandler);
            sc.setRequestAttribute(request, "categoryEditSAH", categoryEditSAH);

            sc.setRequestAttribute(request, "categoryBeHandlerAll", categoryBeHandlerAll);
            sc.setRequestAttribute(request, "categoryBeHandlerSubmitter", categoryBeHandlerSubmitter);
            sc.setRequestAttribute(request, "categoryBeHandlerHandler", categoryBeHandlerHandler);
            sc.setRequestAttribute(request, "categoryBeHandlerSAH", categoryBeHandlerSAH);

            sc.setRequestAttribute(request, "categoryDeleteAll", categoryDeleteAll);
            sc.setRequestAttribute(request, "categoryDeleteSubmitter", categoryDeleteSubmitter);
            sc.setRequestAttribute(request, "categoryDeleteHandler", categoryDeleteHandler);
            sc.setRequestAttribute(request, "categoryDeleteSAH", categoryDeleteSAH);

            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_USER_STATUS_OVERVIEW);
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_USER_STATUS_OVERVIEW));
            return mapping.findForward("userStatusViewJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }


}