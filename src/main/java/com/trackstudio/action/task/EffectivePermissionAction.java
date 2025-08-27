package com.trackstudio.action.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
import com.trackstudio.app.Slider;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.form.ACLForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.secured.SecuredPrstatusBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.EggBasket;
import com.trackstudio.tools.Null;
import com.trackstudio.tools.Tab;

public class EffectivePermissionAction extends TSDispatchAction {
    private static Log log = LogFactory.getLog(CategoryPermissionAction.class);

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            ACLForm tf = (ACLForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().taskHeader(tf, sc, request, true);
            SecuredTaskBean tci = new SecuredTaskBean(id, sc);
            String pageNumber = tf.getSliderPage();

            HashMap<SecuredUserBean, Boolean> userMap = AdapterManager.getInstance().getSecuredAclAdapterManager().getEffectiveList(sc, tci.getId());
            List<SecuredUserBean> userList = new ArrayList<SecuredUserBean>(userMap.keySet());
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
                for (SecuredPrstatusBean bean : AdapterManager.getInstance().getSecuredAclAdapterManager().getAllowedPrstatusList(sc, tci.getId(), us2.getId()))
                    effective.putItem(us2, bean);

            }

            sc.setRequestAttribute(request, "slider", slider.drawSlider(request.getContextPath() + "/EffectivePermissionAction.do?method=page&amp;id=" + id, "div", "slider"));

            sc.setRequestAttribute(request, "effective", effective);
            sc.setRequestAttribute(request, "canViewException", userMap);
            sc.setRequestAttribute(request, "canView", Boolean.valueOf(sc.canAction(Action.manageTaskACLs, id)));

            sc.setRequestAttribute(request, "canEdit", Boolean.valueOf(sc.canAction(Action.manageTaskACLs, id)));

            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_TASK_EFFECTIVE_STATUSES);
            sc.setRequestAttribute(request, "tabView", new Tab(sc.canAction(Action.manageTaskACLs, id), true));
            sc.setRequestAttribute(request, "tabEdit", new Tab(false, false));
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_TASK_EFFECTIVE_STATUSES));
            return mapping.findForward("effectivePermissionsJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }
}
