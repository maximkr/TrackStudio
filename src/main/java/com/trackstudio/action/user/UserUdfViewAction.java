package com.trackstudio.action.user;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.trackstudio.action.CustomEditAction;
import com.trackstudio.action.GeneralAction;
import com.trackstudio.action.TSDispatchAction;
import com.trackstudio.app.PageNaming;
import com.trackstudio.app.UdfValue;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.form.CustomForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.secured.SecuredUserUDFBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.Tab;

public class UserUdfViewAction extends TSDispatchAction {
    public ActionForward page(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            CustomForm form = (CustomForm) actionForm;
            SessionContext sc = GeneralAction.getInstance().imports(request, response);
            if (sc == null)
                return null;
            String id = GeneralAction.getInstance().userHeader(form, sc, request);

            CustomEditAction.fillViewForm(sc, form, request);
            sc.setRequestAttribute(request, "_can_view", sc.canAction(Action.manageUserUDFs, id));
            sc.setRequestAttribute(request, "viewPermission", sc.canAction(Action.manageUserUDFs, id));
            SecuredUserUDFBean udf = (SecuredUserUDFBean) request.getAttribute("udf");

            if (udf.getType() == UdfValue.TASK && udf.getInitial() != null) {

                SecuredTaskBean t = new SecuredTaskBean(udf.getInitial(), sc);

                sc.setRequestAttribute(request, "tuval", t);
            } else if (udf.getType() == UdfValue.USER && udf.getInitial() != null) {

                SecuredUserBean t = new SecuredUserBean(udf.getInitial(), sc);

                sc.setRequestAttribute(request, "tuval", t);
            }


            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_USER_CUSTOM_FIELD_OVERVIEW);
            TSDispatchAction.selectUserTab(sc, id, "tabUser", request);
            sc.setRequestAttribute(request, "tabView", new Tab(sc.canAction(Action.manageUserUDFs, id) && sc.allowedByUser(id), true));
            sc.setRequestAttribute(request, "tabListValues", new Tab(udf.canManage() && sc.canAction(Action.manageUserUDFs, id) && sc.allowedByUser(id) && (udf.getType() == UdfValue.LIST || udf.getType() == UdfValue.MULTILIST), false));
            sc.setRequestAttribute(request, "tabEdit", new Tab(sc.canAction(Action.manageUserUDFs, id) && sc.allowedByUser(id) && udf.canManage(), false));
            sc.setRequestAttribute(request, "tabPermissions", new Tab(request.getAttribute("udf") != null && sc.canAction(Action.manageUserUDFs, id) && sc.allowedByUser(id), false));
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_USER_CUSTOM_FIELD_PROPERTIES));

            return actionMapping.findForward("userUdfViewJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }
}
