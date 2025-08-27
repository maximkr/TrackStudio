package com.trackstudio.action.user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.trackstudio.app.UdfValue;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.filter.FValue;
import com.trackstudio.app.filter.FilterSettings;
import com.trackstudio.app.filter.UserFValue;
import com.trackstudio.app.filter.customizer.Customizer;
import com.trackstudio.app.filter.customizer.DateCustomizer;
import com.trackstudio.app.filter.customizer.ListCustomizer;
import com.trackstudio.app.filter.customizer.TextCustomizer;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.common.FieldMap;
import com.trackstudio.form.FilterForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.secured.SecuredFilterBean;
import com.trackstudio.secured.SecuredPrstatusBean;
import com.trackstudio.secured.SecuredUDFValueBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.secured.SecuredUserFValueBean;
import com.trackstudio.startup.Config;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.ExternalAdapterManagerUtil;
import com.trackstudio.tools.Tab;
import com.trackstudio.tools.formatter.DateFormatter;
import com.trackstudio.tools.textfilter.HTMLEncoder;

public class UserFilterSettingsAction extends TSDispatchAction {
    private static Log log = LogFactory.getLog(UserFilterSettingsAction.class);

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                              HttpServletResponse response) throws Exception {

        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            FilterForm bf = (FilterForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().userHeader(bf, sc, request);
            String filterId = bf.getFilterId() == null ? request.getParameter("filterId") : bf.getFilterId();
            bf.setFilterId(filterId);
            bf.setId(id);
            SecuredFilterBean currentFilter = AdapterManager.getInstance().getSecuredFindAdapterManager().findFilterById(sc, filterId);

            if (!sc.canAction(Action.manageUserPrivateFilters, currentFilter.getOwnerId()) && !currentFilter.canManage())
                return null;

            UserFValue flthm = AdapterManager.getInstance().getSecuredFilterAdapterManager().getUserFValue(sc, filterId).getFValue();
            sc.setRequestAttribute(request, "flthm", flthm);
            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_USER_FILTER_USER_PARAMETERS);
            ArrayList customs = new ArrayList();
            ArrayList<SecuredPrstatusBean> col = AdapterManager.getInstance().getSecuredPrstatusAdapterManager().getAllViewablePrstatuses(sc);
            HashMap userStatuses = new HashMap();

            for (SecuredPrstatusBean prstatus : col) {
                userStatuses.put(prstatus.getId(), HTMLEncoder.encode(prstatus.getName()));
            }

            ArrayList udfIds = new ArrayList();
            SecuredUserBean filterUser = new SecuredUserBean(currentFilter.getOwnerId(), sc);
            ArrayList<SecuredUDFValueBean> list = new ArrayList(filterUser.getUDFValuesList());

            for (SecuredUDFValueBean udfvf : list) {
                udfIds.add(udfvf.getId());
            }
            String javaScript = "<script language='JavaScript'>\n" +
                    "var levelArray = new Array();\n";

            List<String> tn = flthm.getSortOrder();
            for (String token : tn) {
                if (token != null && token.length() != 0 && (token.indexOf(FValue.UDF_SORT) == -1 || udfIds.contains(token.substring(token.startsWith("_") ? FValue.UDF_SORT.length() + 1 : FValue.UDF_SORT.length())))) {
                    javaScript += "levelArray[levelArray.length] = \"" + (token.startsWith("_") ? token.substring(1) : token) + "\";\n";
                }
            }

            javaScript += "</script>";
            sc.setRequestAttribute(request, "javaScript", javaScript);

            String contextPath = request.getContextPath();

            Customizer forLogin = new TextCustomizer(TextCustomizer.CHARS, FieldMap.USER_LOGIN);
            sc.setRequestAttribute(request, "forLogin", forLogin.draw(sc, flthm, contextPath));
            customs.add(forLogin);

            Customizer forName = new TextCustomizer(TextCustomizer.CHARS, FieldMap.USER_NAME);
            sc.setRequestAttribute(request, "forName", forName.draw(sc, flthm, contextPath));
            customs.add(forName);


            Customizer forCompany = new TextCustomizer(TextCustomizer.CHARS, FieldMap.USER_COMPANY);
            sc.setRequestAttribute(request, "forCompany", forCompany.draw(sc, flthm, contextPath));
            customs.add(forCompany);

            Customizer forSubmitterStatus = new ListCustomizer(userStatuses, ListCustomizer.LIST_EQUAL, FieldMap.USER_STATUS, true);
            sc.setRequestAttribute(request, "forSubmitterStatus", forSubmitterStatus.draw(sc, flthm, contextPath));
            customs.add(forSubmitterStatus);

            Customizer forEmail = new TextCustomizer(TextCustomizer.CHARS, FieldMap.USER_EMAIL);
            sc.setRequestAttribute(request, "forEmail", forEmail.draw(sc, flthm, contextPath));
            customs.add(forEmail);

            Customizer forTel = new TextCustomizer(TextCustomizer.CHARS, FieldMap.USER_TEL);
            sc.setRequestAttribute(request, "forTel", forTel.draw(sc, flthm, contextPath));
            customs.add(forTel);

            Map locales = Config.getInstance().getAvailableLocalesMap(DateFormatter.toLocale(sc.getLocale()));


            Customizer forLocale = new ListCustomizer(locales, ListCustomizer.LIST_EQUAL, FieldMap.USER_LOCALE, true);
            sc.setRequestAttribute(request, "forLocale", forLocale.draw(sc, flthm, contextPath));
            customs.add(forLocale);

            Map tZones = Config.getInstance().getAvailableTimeZonesMap(DateFormatter.toLocale(sc.getLocale()));


            Customizer forTimezone = new ListCustomizer(tZones, ListCustomizer.LIST_EQUAL, FieldMap.USER_TIMEZONE, true);
            sc.setRequestAttribute(request, "forTimezone", forTimezone.draw(sc, flthm, contextPath));
            customs.add(forTimezone);

            Customizer forExpireDate = new DateCustomizer(FieldMap.USER_EXPIREDATE, sc.getUser().getDateFormatter());
            sc.setRequestAttribute(request, "forExpireDate", forExpireDate.draw(sc, flthm, contextPath));
            customs.add(forExpireDate);

            HashMap actives = new HashMap();
            actives.put("True", "True");
            actives.put("False", "False");
            Customizer forActive = new ListCustomizer(actives, ListCustomizer.LIST_EQUAL, FieldMap.USER_ACTIVE, true);
            sc.setRequestAttribute(request, "forActive", forActive.draw(sc, flthm, contextPath));
            customs.add(forActive);

            Customizer forChild = new TextCustomizer(TextCustomizer.NATURAL, FieldMap.USER_CHILDCOUNT);
            sc.setRequestAttribute(request, "forChild", forChild.draw(sc, flthm, contextPath));
            customs.add(forChild);

            Customizer forChildAllowed = new TextCustomizer(TextCustomizer.NATURAL, FieldMap.USER_CHILDALLOWED);
            sc.setRequestAttribute(request, "forChildAllowed", forChildAllowed.draw(sc, flthm, contextPath));
            customs.add(forChildAllowed);

            boolean disabled = false;

            ArrayList udfCustomizers = new ArrayList();
            Collections.sort(list);

            for (SecuredUDFValueBean udfvf : (TreeSet<SecuredUDFValueBean>) new TreeSet(list)) {
                Customizer cust = null;
                if (udfvf.getUdfType() != UdfValue.USER)
                    cust = udfvf.getCustomizer(udfvf.getId(), disabled);
                else {
                    HashMap usersMap = ExternalAdapterManagerUtil.makeUserMap(sc, flthm, (String) sc.getAttribute("taskId"));
                    FieldMap fm = FieldMap.createUDF(udfvf.getCaption(), FValue.UDF + udfvf.getId(), FValue.UDF_SORT + udfvf.getId());
                    cust = new ListCustomizer(usersMap, ListCustomizer.LIST_EQUAL, fm, true);
                }
                if (udfvf.isLookup() && cust instanceof TextCustomizer)
                    ((TextCustomizer) cust).setSecured(currentFilter.getOwner());
                customs.add(cust);
                udfCustomizers.add(cust.draw(sc, flthm, contextPath));
            }

            sc.setRequestAttribute(request, "udfCustomizers", udfCustomizers);
            sc.removeAttribute("customs");
            sc.setAttribute("customs", customs);
            sc.setRequestAttribute(request, "currentFilter", currentFilter);
            sc.setRequestAttribute(request, "filterId", filterId);

            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_USER_FILTER_USER_PARAMETERS));
            sc.setRequestAttribute(request, "tabView", new Tab(sc.canAction(Action.manageUserPrivateFilters, id), false));
            sc.setRequestAttribute(request, "tabEdit", new Tab(sc.canAction(Action.manageUserPrivateFilters, id) && (sc.canAction(Action.manageUserPublicFilters, id) || currentFilter.isPrivate()) && currentFilter.canManage(), false));
            sc.setRequestAttribute(request, "tabUserSettings", new Tab(sc.canAction(Action.manageUserPrivateFilters, id) && (sc.canAction(Action.manageUserPublicFilters, id) || currentFilter.isPrivate()) && currentFilter.canManage(), true));
            selectUserTab(sc, id, "tabUserFilter", request);
            return mapping.findForward("editUserFilterSettingsJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward edit(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            FilterForm ff = (FilterForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String filterId = ff.getFilterId();
            if (sc.getAttribute("customs") != null) {
                ArrayList<Customizer> customs = (ArrayList) sc.getAttribute("customs");
                UserFValue flthm = AdapterManager.getInstance().getSecuredFilterAdapterManager().getUserFValue(sc, filterId).getFValue();

                for (Customizer ab : customs)
                    ab.setFilter(sc, request, flthm);

                sc.removeAttribute("customs");
                AdapterManager.getInstance().getSecuredFilterAdapterManager().setFValue(sc, filterId, new SecuredUserFValueBean(flthm, sc));
            }

            Object oldFilter = sc.getAttribute("userfilter");
            if (oldFilter != null) {
                FilterSettings settings = (FilterSettings) oldFilter;
                if (settings.getFilterId().equals(filterId))
                    sc.removeAttribute("userfilter"); //winzard: это не атавизм, ТАК НАДО
            }

            return mapping.findForward("viewUserFilterPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

}
