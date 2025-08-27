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
import com.trackstudio.form.RegistrationForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.secured.SecuredRegistrationBean;
import com.trackstudio.startup.Config;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.Tab;

public class RegistrationViewAction extends TSDispatchAction {

    private static Log log = LogFactory.getLog(RegistrationViewAction.class);

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                              HttpServletResponse response) throws Exception {

        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            RegistrationForm rf = (RegistrationForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");

            String id = GeneralAction.getInstance().taskHeader(rf, sc, request, true);
            if (!sc.canAction(Action.manageRegistrations, id))
                return null;

            String currentRegId = null;
            if (request.getAttribute("registration") != null)
                currentRegId = ((SecuredRegistrationBean) request.getAttribute("registration")).getId();
            if (currentRegId == null)
                currentRegId = rf.getRegistration();

              /*String currentRegId =*/
            ;
            SecuredRegistrationBean reg = AdapterManager.getInstance().getSecuredFindAdapterManager().findRegistrationById(sc, currentRegId);

            sc.setRequestAttribute(request, "owner", reg.getUser());
            sc.setRequestAttribute(request, "prstatus", reg.getPrstatus());
            sc.setRequestAttribute(request, "expire", reg.getExpireDays() != null ? reg.getExpireDays().toString() : null);

            sc.setRequestAttribute(request, "child", reg.getChildAllowed() != null ? reg.getChildAllowed().toString() : null);
            sc.setRequestAttribute(request, "url", Config.getInstance().getSiteURL() + "/LoginAction.do?method=registerPage&amp;project=" + currentRegId);
            sc.setRequestAttribute(request, "prstatusId", reg.getPrstatusId());

            sc.setRequestAttribute(request, "canEdit", sc.canAction(Action.manageRegistrations, id) && reg.canUpdate());
            sc.setRequestAttribute(request, "registration", reg);
            sc.setRequestAttribute(request, "canView", sc.canAction(Action.manageRegistrations, id));

            sc.setRequestAttribute(request, "create", new Tab(false, false));
            sc.setRequestAttribute(request, "view", new Tab(true, true));
            boolean canEdit = sc.canAction(Action.manageRegistrations, id) && reg.canManage();
            sc.setRequestAttribute(request, "general", new Tab(canEdit, false));
            sc.setRequestAttribute(request, "registrationTab", new Tab(canEdit && sc.taskOnSight(reg.getTaskId()), false));
            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_USER_REGISTRATION_OVERVIEW);
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_USER_REGISTRATION_PROPERTIES));
            selectTaskTab(sc, id, "tabRegistration", request);
            return mapping.findForward("viewRegistrationJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

}
