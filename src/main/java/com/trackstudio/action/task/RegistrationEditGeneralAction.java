package com.trackstudio.action.task;

import java.util.ArrayList;
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
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.form.RegistrationForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.secured.SecuredRegistrationBean;
import com.trackstudio.startup.Config;
import com.trackstudio.startup.I18n;

public class RegistrationEditGeneralAction extends TSDispatchAction {
    private static Log log = LogFactory.getLog(RegistrationEditGeneralAction.class);

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
            String registrationId = rf.getRegistration();
            if (registrationId != null) {
                SecuredRegistrationBean reg = AdapterManager.getInstance().getSecuredFindAdapterManager().findRegistrationById(sc, registrationId);
                rf.setName(reg.getName());
                rf.setShared(reg.isPriv());
                sc.setRequestAttribute(request, "owner", reg.getUser());
                sc.setRequestAttribute(request, "registration", reg);
                sc.setRequestAttribute(request, "checkbox_task", (reg.getCategory() != null ? "checked" : null) + ' ' + (sc.canAction(Action.manageRegistrations, id) && reg.canManage() ? "" : "disabled"));
                sc.setRequestAttribute(request, "registrationPrstatuses", new ArrayList(new TreeSet(AdapterManager.getInstance().getSecuredPrstatusAdapterManager().getCreatablePrstatusList(sc, sc.getUserId()))));
                rf.setStatusId(reg.getPrstatusId());
                rf.setCategoryId(reg.getCategoryId());
                sc.setRequestAttribute(request, "url", Config.getInstance().getSiteURL() + "/LoginAction.do?method=registerPage&amp;project=" + registrationId);
                sc.setRequestAttribute(request, "child", reg.getChildAllowed());
                sc.setRequestAttribute(request, "expire", reg.getExpireDays());
                sc.setRequestAttribute(request, "view", (reg.getCategory() != null ? "false" : "true"));
            } else {
                sc.setRequestAttribute(request, "owner", sc.getUser());
                sc.setRequestAttribute(request, "new", Boolean.TRUE);
                sc.setRequestAttribute(request, "registrationPrstatuses", new ArrayList(new TreeSet(AdapterManager.getInstance().getSecuredPrstatusAdapterManager().getCreatablePrstatusList(sc, sc.getUserId()))));
                sc.setRequestAttribute(request, "url", Config.getInstance().getSiteURL() + "/LoginAction.do?method=registerPage&amp;project=" + id);
                sc.setRequestAttribute(request, "view", "true");
            }

            ArrayList availColl = AdapterManager.getInstance().getSecuredCategoryAdapterManager().getAvailableCategoryList(sc, id);
            ArrayList categoryColl = AdapterManager.getInstance().getSecuredCategoryAdapterManager().getCreatableCategoryList(sc, id);
            //sc.setRequestAttribute(request,"registrationId", registrationId);
            sc.setRequestAttribute(request, "canView", sc.canAction(Action.manageRegistrations, id));
            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_USER_REGISTRATION_PROPERTIES);
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_USER_REGISTRATION_PROPERTIES));


            categoryColl.retainAll(availColl);
            sc.setRequestAttribute(request, "categories", new ArrayList(new TreeSet(categoryColl)));

            boolean isRegistrationExist = true;
            if (rf.getRegistration() == null || rf.getRegistration().length() == 0) {
                isRegistrationExist = false;
            }
            log.debug(rf.getRegistration());
            log.debug(rf.getId());
            sc.setRequestAttribute(request, "isRegistrationExist", isRegistrationExist);

            return mapping.findForward("editGeneralPageJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }


    }

    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            RegistrationForm rf = (RegistrationForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = rf.getId();
            String reg_id = null;
            reg_id = rf.getRegistration();
            boolean priv = rf.isShared();
            if (reg_id == null || reg_id.length() == 0) {
                if (rf.getName() != null && rf.getName().trim().length() > 0)
                    reg_id = AdapterManager.getInstance().getSecuredRegistrationAdapterManager().createRegistration(sc, rf.getId(), rf.getName(), rf.getStatusId());


            }
            SecuredRegistrationBean reg = null;
            reg = AdapterManager.getInstance().getSecuredFindAdapterManager().findRegistrationById(sc, reg_id);

            if (!sc.canAction(Action.manageRegistrations, id) && reg.canManage())
                throw new GranException("You have no permition to change this registration");

            Integer ch = null;
            String s_id = rf.getStatusId();
            String child = rf.getChild();
            String expire = rf.getExpire();
            String cat_id = rf.getCategoryId();


            if (child != null && child.length() != 0 && !child.equals("0")) ch = new Integer(child);
            Integer ex = null;
            if (expire != null && expire.length() != 0) ex = new Integer(expire);
            sc.setRequestAttribute(request, "registration", reg);
            AdapterManager.getInstance().getSecuredRegistrationAdapterManager().updateRegistration(sc, reg_id, rf.getName(), s_id, ch, ex, cat_id, priv);
            return mapping.findForward("registrationViewPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

}