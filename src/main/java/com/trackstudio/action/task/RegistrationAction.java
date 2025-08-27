package com.trackstudio.action.task;

import java.util.ArrayList;
import java.util.Collections;
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
import com.trackstudio.form.RegistrationForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.secured.SecuredRegistrationBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.EggBasket;
import com.trackstudio.tools.Tab;

public class RegistrationAction extends TSDispatchAction {
    private static Log log = LogFactory.getLog(RegistrationAction.class);

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
            ArrayList<SecuredRegistrationBean> registrations = new ArrayList<SecuredRegistrationBean>();
            EggBasket<SecuredTaskBean, SecuredRegistrationBean> parentRegistrations = new EggBasket<SecuredTaskBean, SecuredRegistrationBean>();
            EggBasket<SecuredTaskBean, SecuredRegistrationBean> childrenRegistrations = new EggBasket<SecuredTaskBean, SecuredRegistrationBean>();
            ArrayList<EggBasket> seeAlso = new ArrayList<EggBasket>();
            ArrayList<SecuredTaskBean> parentTasks = AdapterManager.getInstance().getSecuredTaskAdapterManager().getTaskChain(sc, null, id);
            ArrayList<SecuredRegistrationBean> col = AdapterManager.getInstance().getSecuredRegistrationAdapterManager().getRegistrationSharesList(sc, id);
            ArrayList retList = new ArrayList();

            for (SecuredRegistrationBean srb : col) {
                SecuredTaskBean stb = srb.getTask();
                if (stb.canManage() && sc.canAction(Action.manageRegistrations, stb.getId())) {
                    if (srb.getTaskId().equals(id)) {
                        registrations.add(srb);
                    } else {

                        if (parentTasks.contains(stb)) {
                            parentRegistrations.putItem(stb, srb);
                        } else childrenRegistrations.putItem(stb, srb);
                    }
                }
            }
            Collections.sort(retList);

            sc.setRequestAttribute(request, "form_onsubmit", sc.canAction(Action.manageRegistrations, id) ? "return validate(this);" : "return false;");

            sc.setRequestAttribute(request, "canDelete", sc.canAction(Action.manageRegistrations, id));

            sc.setRequestAttribute(request, "canEdit", sc.canAction(Action.manageRegistrations, id));
            boolean canCreate = sc.canAction(Action.manageRegistrations, id);
            sc.setRequestAttribute(request, "canCreateObject", canCreate);
            if (canCreate) {
                sc.setRequestAttribute(request, "msgHowCreateObject", I18n.getString(sc.getLocale(), "REGISTRATION_ADD"));
                sc.setRequestAttribute(request, "msgAddObject", I18n.getString(sc.getLocale(), "REGISTRATION_ADD"));
                sc.setRequestAttribute(request, "createObjectAction", "/RegistrationEditGeneralAction.do");
            }
            sc.setRequestAttribute(request, "secondParamMsg", I18n.getString(sc.getLocale(), "ROLE"));
            sc.setRequestAttribute(request, "secondParamName", "statusId");
            sc.setRequestAttribute(request, "secondParamCollection", new ArrayList(new TreeSet(AdapterManager.getInstance().getSecuredPrstatusAdapterManager().getCreatablePrstatusList(sc, sc.getUserId()))));
            sc.setRequestAttribute(request, "firstParamMsg", I18n.getString(sc.getLocale(), "NAME"));
            sc.setRequestAttribute(request, "firstParamName", "name");

            Collections.sort(registrations);
            sc.setRequestAttribute(request, "registrations", registrations);

            if (!parentRegistrations.isEmpty()) seeAlso.add(parentRegistrations);
            if (!childrenRegistrations.isEmpty()) seeAlso.add(childrenRegistrations);

            sc.setRequestAttribute(request, "seeAlso", seeAlso);
            sc.setRequestAttribute(request, "tabReg", new Tab(true, true));
            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_USER_REGISTRATION_LIST);
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_USER_REGISTRATION_LIST));
            return mapping.findForward("registrationJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }


    public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                HttpServletResponse response) throws Exception {
        log.trace("##########");
        return mapping.findForward("registrationViewPage");
    }

    public ActionForward delete(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            RegistrationForm rf = (RegistrationForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String[] registrations = rf.getSelect();
            if (registrations != null)
                for (String registration : registrations) {
                    SecuredRegistrationBean srb = AdapterManager.getInstance().getSecuredFindAdapterManager().findRegistrationById(sc, registration);
                    if (sc.canAction(Action.manageRegistrations, srb.getUserId()) && srb.canManage())
                        AdapterManager.getInstance().getSecuredRegistrationAdapterManager().deleteRegistration(sc, registration);
                }
            rf.setMutable(false);
            return mapping.findForward("registrationListPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

     public ActionForward clone(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
         boolean w = lockManager.acquireConnection();
         try {
             log.trace("##########");
             RegistrationForm rf = (RegistrationForm) form;
             SessionContext sc = (SessionContext) request.getAttribute("sc");
             String[] registrations = rf.getSelect();
             if (registrations != null)
                 for (String registration : registrations) {
                     SecuredRegistrationBean srb = AdapterManager.getInstance().getSecuredFindAdapterManager().findRegistrationById(sc, registration);
                     String regId = AdapterManager.getInstance().getSecuredRegistrationAdapterManager().createRegistration(sc, srb.getTaskId(), srb.getName() + "_clone", srb.getPrstatusId());
                     AdapterManager.getInstance().getSecuredRegistrationAdapterManager().updateRegistration(sc, regId, srb.getName() + "_clone", srb.getPrstatusId(), srb.getChildAllowed(), srb.getExpireDays(), srb.getCategoryId(), srb.isPriv());
                 }
             rf.setMutable(false);
             return mapping.findForward("registrationListPage");
         } finally {
             if (w) lockManager.releaseConnection();
         }

     }
}
