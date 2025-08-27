package com.trackstudio.action.user;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.trackstudio.action.GeneralAction;
import com.trackstudio.action.TSDispatchAction;
import com.trackstudio.app.PageNaming;
import com.trackstudio.app.UdfValue;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.filter.FValue;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.constants.CategoryConstants;
import com.trackstudio.containers.PrstatusListItem;
import com.trackstudio.form.CustomForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.secured.SecuredPrstatusBean;
import com.trackstudio.secured.SecuredUDFBean;
import com.trackstudio.securedkernel.SecuredUDFAdapterManager;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.Tab;

public class UserUdfPermissionAction extends TSDispatchAction {
    public ActionForward page(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            CustomForm form = (CustomForm) actionForm;
            SessionContext sc = GeneralAction.getInstance().imports(request, response);
            if (sc == null)
                return null;
            String id = GeneralAction.getInstance().userHeader(form, sc, request);
            sc.setRequestAttribute(request, "isUser", true);
            sc.setRequestAttribute(request, "action", "/UserUdfPermissionAction.do");

            SecuredUDFBean udf = AdapterManager.getInstance().getSecuredFindAdapterManager().findUDFById(sc, form.getUdfId());

            sc.setRequestAttribute(request, "udf", udf);
            form.setUdfId(udf.getId());
            sc.setRequestAttribute(request, "udfId", udf.getId());
            ArrayList<PrstatusListItem> cannotviewStatuses = new ArrayList<PrstatusListItem>();
            ArrayList<PrstatusListItem> canviewStatuses = new ArrayList<PrstatusListItem>();
            ArrayList<PrstatusListItem> cannoteditStatuses = new ArrayList<PrstatusListItem>();
            ArrayList<PrstatusListItem> caneditStatuses = new ArrayList<PrstatusListItem>();
            StringBuffer canview = new StringBuffer(), canedit = new StringBuffer();

            Set prstatusSet = new TreeSet(AdapterManager.getInstance().getSecuredPrstatusAdapterManager().getAvailablePrstatusList(sc, sc.getUserId(id)));
            for (Iterator it = prstatusSet.iterator(); it.hasNext(); ) {
                SecuredPrstatusBean spb = (SecuredPrstatusBean) it.next();
                List types = AdapterManager.getInstance().getSecuredUDFAdapterManager().getUDFRuleList(sc, spb.getId(), udf.getId());
                if (!types.contains(CategoryConstants.VIEW_ALL)) {
                    cannotviewStatuses.add(new PrstatusListItem(spb.getId(), spb.getName()));
                }
                if (types.contains(CategoryConstants.VIEW_ALL)) {
                    canviewStatuses.add(new PrstatusListItem(spb.getId(), spb.getName()));
                    canview.append(spb.getId()).append(FValue.DELIM);
                }
                if (!types.contains(CategoryConstants.EDIT_ALL))
                    cannoteditStatuses.add(new PrstatusListItem(spb.getId(), spb.getName()));
                if (types.contains(CategoryConstants.EDIT_ALL)) {
                    caneditStatuses.add(new PrstatusListItem(spb.getId(), spb.getName()));
                    canedit.append(spb.getId()).append(FValue.DELIM);
                }

            }
            form.setHiddencanedit(canedit.toString());
            form.setHiddencanview(canview.toString());
            sc.setRequestAttribute(request, "cannotviewStatuses", cannotviewStatuses);
            sc.setRequestAttribute(request, "canviewStatuses", canviewStatuses);
            sc.setRequestAttribute(request, "caneditStatuses", caneditStatuses);
            sc.setRequestAttribute(request, "cannoteditStatuses", cannoteditStatuses);
            sc.setRequestAttribute(request, "cancelAction", "/UserUdfViewAction.do");


            sc.setRequestAttribute(request, "canEdit", sc.canAction(Action.manageUserUDFs, id) && sc.canAction(Action.manageUserUDFs, KernelManager.getFind().findUdfsource(udf.getUdfSourceId()).getUser().getId()));
            sc.setRequestAttribute(request, "canView", sc.canAction(Action.manageUserUDFs, id));
            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_USER_CUSTOM_FIELD_PERMISSIONS);
            sc.setRequestAttribute(request, "tabView", new Tab(sc.canAction(Action.manageUserUDFs, id), false));
            sc.setRequestAttribute(request, "tabListValues", new Tab(udf != null && sc.canAction(Action.manageUserUDFs, id) && udf.canManage() && sc.allowedByUser(id) && (udf.getType() == UdfValue.LIST || udf.getType() == UdfValue.MULTILIST), false));
            sc.setRequestAttribute(request, "tabEdit", new Tab(sc.canAction(Action.manageUserUDFs, id) && udf.canManage(), false));
            sc.setRequestAttribute(request, "tabPermissions", new Tab(sc.canAction(Action.manageUserUDFs, id), true));
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_USER_CUSTOM_FIELD_PERMISSIONS));
            selectUserTab(sc, id, "tabCustomize", request);
            return actionMapping.findForward("userUdfPermissionJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            CustomForm tf = (CustomForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            SecuredUDFAdapterManager cam = AdapterManager.getInstance().getSecuredUDFAdapterManager();
            String udfId = tf.getUdfId();
            String id = GeneralAction.getInstance().userHeader(tf, sc, request);

            ArrayList<SecuredPrstatusBean> prstatuses = AdapterManager.getInstance().getSecuredPrstatusAdapterManager().getAvailablePrstatusList(sc, sc.getUserId(id));
            String canedit = tf.getHiddencanedit();
            String canview = tf.getHiddencanview();
            ArrayList<String> canViewPrstatuses = new ArrayList<String>();
            ArrayList<String> canEditPrstatuses = new ArrayList<String>();

            StringTokenizer tk = new StringTokenizer(canview, FValue.DELIM);
            while (tk.hasMoreElements()) {
                String token = tk.nextToken();
                if (token.length() > 0) {

                    canViewPrstatuses.add(token);

                }
            }

            tk = new StringTokenizer(canedit, FValue.DELIM);
            while (tk.hasMoreElements()) {
                String token = tk.nextToken();
                if (token.length() > 0) {

                    canEditPrstatuses.add(token);

                }
            }

            for (SecuredPrstatusBean it : prstatuses) {
                String prstatusId = it.getId();
                String view = null, edit = null;
                if (canViewPrstatuses.contains(prstatusId)) {
                    view = CategoryConstants.VIEW_ALL;
                }

                if (canEditPrstatuses.contains(prstatusId)) {
                    edit = CategoryConstants.EDIT_ALL;
                }

                cam.setUserUDFRule(sc, udfId, prstatusId, view, edit);

            }


            return mapping.findForward("userUdfViewPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }
}
