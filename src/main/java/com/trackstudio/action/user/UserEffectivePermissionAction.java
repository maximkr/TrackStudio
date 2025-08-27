package com.trackstudio.action.user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import com.trackstudio.app.Slider;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.form.ACLForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.secured.SecuredPrstatusBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.EggBasket;
import com.trackstudio.tools.Null;
import com.trackstudio.tools.Tab;

public class UserEffectivePermissionAction extends TSDispatchAction {
    private static Log log = LogFactory.getLog(UserEffectivePermissionAction.class);

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            ACLForm tf = (ACLForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().userHeader(tf, sc, request);
            String pageNumber = tf.getSliderPage();
            Set<SecuredUserBean> userSet = new HashSet<SecuredUserBean>(AdapterManager.getInstance().getSecuredAclAdapterManager().getUserEffectiveStatusesList(sc, id));
            List chain = AdapterManager.getInstance().getSecuredUserAdapterManager().getUserChain(sc, id, "1");

            if (chain != null)
                userSet.addAll(chain);
            userSet.add(sc.getUser());
            ArrayList<SecuredUserBean> userList = new ArrayList(userSet);
            Collections.sort(userList);
            int pn = 1;
            if (Null.isNotNull(pageNumber)) {
                try {
                    pn = Integer.parseInt(pageNumber);
                } catch (Exception exc) {
                    // just skip
                }
            }
            Slider<SecuredUserBean> slider = new Slider<SecuredUserBean>(userList, 50, null, pn);
            slider.setAll(tf.isAll(), sc.getLocale());
            EggBasket<SecuredUserBean, SecuredPrstatusBean> effective = new EggBasket<SecuredUserBean, SecuredPrstatusBean>();


            for (SecuredUserBean us2 : slider.getCol()) {
                for (SecuredPrstatusBean bean : AdapterManager.getInstance().getSecuredAclAdapterManager().getUserAllowedPrstatusList(sc, id, us2.getId()))
                    effective.putItem(us2, bean);
            }

            ArrayList userColl = AdapterManager.getInstance().getSecuredUserAdapterManager().getUserListForNewAcl(sc, sc.getUserId(id));
            Collections.sort(userColl);
            sc.setRequestAttribute(request, "handlerColl", userColl);

            sc.setRequestAttribute(request, "paramCollection", userColl);
            sc.setRequestAttribute(request, "msgHowCreateObject", I18n.getString(sc.getLocale(), "ACL_ADD"));
            sc.setRequestAttribute(request, "firstParamMsg", I18n.getString(sc.getLocale(), "ADD_NEW"));
            sc.setRequestAttribute(request, "firstParamName", "aclUser");
            sc.setRequestAttribute(request, "msgAddObject", I18n.getString(sc.getLocale(), "ACL_ADD"));
            sc.setRequestAttribute(request, "createObjectAction", "/UserACLAction.do");

            sc.setRequestAttribute(request, "slider", slider.drawSlider(request.getContextPath() + "/UserEffectivePermissionAction.do?method=page&amp;id=" + id, "div", "slider"));
            sc.setRequestAttribute(request, "effective", effective);
            sc.setRequestAttribute(request, "canView", Boolean.valueOf(sc.canAction(Action.manageUserACLs, id)));
            sc.setRequestAttribute(request, "canEdit", Boolean.valueOf(sc.canAction(Action.manageUserACLs, id)));
            sc.setRequestAttribute(request, "tabView", new Tab(sc.canAction(Action.manageUserACLs, id), true));
            sc.setRequestAttribute(request, "tabEdit", new Tab(sc.canAction(Action.manageUserACLs, id) && !id.equals(sc.getUserId()), false));
            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_USER_EFFECTIVE_STATUSES);
            sc.setRequestAttribute(request, "tabACL", new Tab(true, true));
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_USER_EFFECTIVE_STATUSES));
            return mapping.findForward("userEffectivePermissionsJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }
}
