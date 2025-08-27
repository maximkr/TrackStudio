package com.trackstudio.action.user;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.trackstudio.action.TSDispatchAction;
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
import com.trackstudio.exception.GranException;
import com.trackstudio.form.PreFilterForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.secured.SecuredFilterBean;
import com.trackstudio.secured.SecuredPrstatusBean;
import com.trackstudio.secured.SecuredUDFValueBean;
import com.trackstudio.secured.SecuredUserFValueBean;
import com.trackstudio.startup.Config;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.ExternalAdapterManagerUtil;
import com.trackstudio.tools.Null;
import com.trackstudio.tools.Pair;
import com.trackstudio.tools.formatter.DateFormatter;
import com.trackstudio.tools.textfilter.HTMLEncoder;

public abstract class UserFilterParametersAbstractAction extends TSDispatchAction {
    protected String filterParameter;
    protected String action;
    protected String forward;
    protected String filterId;
    protected String id;

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            PreFilterForm bf = (PreFilterForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            FilterSettings flthm = null;
            Object filterObject = sc.getAttribute(filterParameter);
            if (filterObject != null) {
                flthm = (FilterSettings) filterObject;
            }
            if (flthm == null) {
                String filterid = AdapterManager.getInstance().getSecuredFilterAdapterManager().getCurrentUserFilterId(sc, id);
                UserFValue val = AdapterManager.getInstance().getSecuredFilterAdapterManager().getUserFValue(sc, filterid).getFValue();
                flthm = new FilterSettings(val, id, filterid);
            }
            if (flthm.getFieldId() == null)
                flthm.setFieldId("default");
            String currentField = flthm.getFieldId();
            bf.setField(currentField);
            bf.setOldfield(currentField);
            bf.setFilter(filterId);

            SecuredFilterBean fltr = AdapterManager.getInstance().getSecuredFindAdapterManager().findFilterById(sc, filterId);

            sc.setRequestAttribute(request, "filterId", fltr.getId());
            sc.setRequestAttribute(request, "filterName", fltr.getName());
            String contextPath = request.getContextPath();
            UserFValue val = AdapterManager.getInstance().getSecuredFilterAdapterManager().getUserFValue(sc, flthm.getFilterId()).getFValue();
            FilterSettings originFilterSettings = new FilterSettings(val, id, flthm.getFilterId());

            HashMap<String, ArrayList> map = UserFilterViewAction.makeFilterView(fltr, flthm.getSettings());
            for (String key : map.keySet()) {
                ArrayList value = map.get(key);
                sc.setRequestAttribute(request, key, value);
            }

            makeFilterForm(fltr, sc, currentField, request, flthm, contextPath, originFilterSettings);
            sc.setRequestAttribute(request, "canManageUserPrivateFilters", fltr.canView() && sc.canAction(Action.manageUserPrivateFilters, id));
            sc.setRequestAttribute(request, "tileId", "userFilterParams");

            return mapping.findForward("userFilterParametersTileJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public static void makeFilterForm(SecuredFilterBean fltr, SessionContext sc, String currentField, HttpServletRequest request, FilterSettings flthm, String contextPath, FilterSettings originFilterSettings) throws GranException {
        List<SecuredUDFValueBean> list = fltr.getOwner().getUDFValuesList();


        if (currentField.equals(FieldMap.USER_NAME.getFieldKey())) {
            Customizer forName = new TextCustomizer(TextCustomizer.CHARS, FieldMap.USER_NAME);
            sc.setRequestAttribute(request, "customizer", forName.drawInput(sc, flthm.getSettings(), contextPath));
        } // block
        else if (currentField.equals(FieldMap.USER_EMAIL.getFieldKey())) {
            Customizer forEmail = new TextCustomizer(TextCustomizer.CHARS, FieldMap.USER_EMAIL);
            sc.setRequestAttribute(request, "customizer", forEmail.drawInput(sc, flthm.getSettings(), contextPath));
        } // block
        else if (currentField.equals(FieldMap.USER_STATUS.getFieldKey())) {
            ArrayList<SecuredPrstatusBean> col = AdapterManager.getInstance().getSecuredPrstatusAdapterManager().getAllViewablePrstatuses(sc);
            HashMap<String, String> userStatuses = new HashMap<String, String>();
            for (SecuredPrstatusBean prstatus : col) {
                userStatuses.put(prstatus.getId(), HTMLEncoder.encode(prstatus.getName()));
            }

            Customizer forSubmitterStatus = new ListCustomizer(userStatuses, ListCustomizer.LIST_EQUAL, FieldMap.USER_STATUS, true);
            ((ListCustomizer) forSubmitterStatus).setOriginFilter(originFilterSettings.getSettings());
            sc.setRequestAttribute(request, "customizer", forSubmitterStatus.drawInput(sc, flthm.getSettings(), contextPath));

        } else if (currentField.equals(FieldMap.USER_COMPANY.getFieldKey())) {
            Customizer forCompany = new TextCustomizer(TextCustomizer.CHARS, FieldMap.USER_COMPANY);
            sc.setRequestAttribute(request, "customizer", forCompany.drawInput(sc, flthm.getSettings(), contextPath));

        } // block
        else if (currentField.equals(FieldMap.USER_CHILDCOUNT.getFieldKey())) {
            Customizer forChild = new TextCustomizer(TextCustomizer.NATURAL, FieldMap.USER_CHILDCOUNT);
            sc.setRequestAttribute(request, "customizer", forChild.drawInput(sc, flthm.getSettings(), contextPath));

        } // block
        else if (currentField.equals(FieldMap.USER_CHILDALLOWED.getFieldKey())) {
            Customizer forChildAllowed = new TextCustomizer(TextCustomizer.NATURAL, FieldMap.USER_CHILDALLOWED);
            sc.setRequestAttribute(request, "customizer", forChildAllowed.drawInput(sc, flthm.getSettings(), contextPath));

        } // block
        else if (currentField.equals(FieldMap.USER_TEL.getFieldKey())) {
            Customizer forTel = new TextCustomizer(TextCustomizer.CHARS, FieldMap.USER_TEL);
            sc.setRequestAttribute(request, "customizer", forTel.drawInput(sc, flthm.getSettings(), contextPath));

        } // block
        else if (currentField.equals(FieldMap.USER_EXPIREDATE.getFieldKey())) {
            Customizer forExpireDate = new DateCustomizer(FieldMap.USER_EXPIREDATE, sc.getUser().getDateFormatter());
            sc.setRequestAttribute(request, "customizer", forExpireDate.drawInput(sc, flthm.getSettings(), contextPath));

        } // block
        else if (currentField.equals(FieldMap.USER_TIMEZONE.getFieldKey())) {
            Map tZones = Config.getInstance().getAvailableTimeZonesMap(DateFormatter.toLocale(sc.getLocale()));
            Customizer forTimezone = new ListCustomizer(tZones, ListCustomizer.LIST_EQUAL, FieldMap.USER_TIMEZONE, true);
            sc.setRequestAttribute(request, "customizer", forTimezone.drawInput(sc, flthm.getSettings(), contextPath));
            ((ListCustomizer) forTimezone).setOriginFilter(originFilterSettings.getSettings());
        } // block
        else if (currentField.equals(FieldMap.USER_LOCALE.getFieldKey())) {
            Map locales = Config.getInstance().getAvailableLocalesMap(DateFormatter.toLocale(sc.getLocale()));


            Customizer forLocale = new ListCustomizer(locales, ListCustomizer.LIST_EQUAL, FieldMap.USER_LOCALE, true);
            sc.setRequestAttribute(request, "customizer", forLocale.drawInput(sc, flthm.getSettings(), contextPath));
            ((ListCustomizer) forLocale).setOriginFilter(originFilterSettings.getSettings());

        } // block
        else if (currentField.equals(FieldMap.USER_LOGIN.getFieldKey())) {
            Customizer forLogin = new TextCustomizer(TextCustomizer.CHARS, FieldMap.USER_LOGIN);
            sc.setRequestAttribute(request, "customizer", forLogin.drawInput(sc, flthm.getSettings(), contextPath));


        } else if (currentField.equals(FieldMap.USER_ACTIVE.getFieldKey())) {
            HashMap<String, String> actives = new HashMap<String, String>();
            actives.put("True", "True");
            actives.put("False", "False");
            Customizer forActive = new ListCustomizer(actives, ListCustomizer.LIST_EQUAL, FieldMap.USER_ACTIVE, true);
            ((ListCustomizer) forActive).setOriginFilter(originFilterSettings.getSettings());
            sc.setRequestAttribute(request, "customizer", forActive.drawInput(sc, flthm.getSettings(), contextPath));

        } else {
// UDF
            for (SecuredUDFValueBean udfvf : new TreeSet<SecuredUDFValueBean>(list)) {
                //cnt++;
                if (currentField.equals(udfvf.getId())) {
                    Customizer cust;
                    if (udfvf.getUdfType() != UdfValue.USER)
                        cust = udfvf.getCustomizer(udfvf.getId(), request.getAttribute("hidePopups") != null);
                    else {
                        HashMap usersMap = ExternalAdapterManagerUtil.makeUserMap(sc, flthm.getSettings(), (String) sc.getAttribute("taskId"));
                        FieldMap fm = FieldMap.createUDF(udfvf.getCaption(), FValue.UDF + udfvf.getId(), FValue.UDF_SORT + udfvf.getId());
                        cust = new ListCustomizer(usersMap, ListCustomizer.LIST_EQUAL, fm, true);
                        ((ListCustomizer) cust).setOriginFilter(originFilterSettings.getSettings());
                    }
                    if (udfvf.isLookup() && cust instanceof TextCustomizer)
                        ((TextCustomizer) cust).setSecured(fltr.getOwner());
                    sc.setRequestAttribute(request, "customizer", cust.drawInput(sc, flthm.getSettings(), contextPath));
                    break;
                }
            } // for
        }
        ArrayList<Pair> userFieldSet = new ArrayList<Pair>();
        userFieldSet.add(new Pair(FieldMap.USER_LOGIN.getFieldKey(), I18n.getString(sc, "LOGIN")));
        userFieldSet.add(new Pair(FieldMap.USER_NAME.getFieldKey(), I18n.getString(sc, "USER_NAME")));
        userFieldSet.add(new Pair(FieldMap.USER_STATUS.getFieldKey(), I18n.getString(sc, "PRSTATUS")));
        userFieldSet.add(new Pair(FieldMap.USER_COMPANY.getFieldKey(), I18n.getString(sc, "COMPANY")));
        userFieldSet.add(new Pair(FieldMap.USER_EMAIL.getFieldKey(), I18n.getString(sc, "EMAIL")));
        userFieldSet.add(new Pair(FieldMap.USER_TEL.getFieldKey(), I18n.getString(sc, "PHONE")));
        userFieldSet.add(new Pair(FieldMap.USER_LOCALE.getFieldKey(), I18n.getString(sc, "LOCALE")));
        userFieldSet.add(new Pair(FieldMap.USER_TIMEZONE.getFieldKey(), I18n.getString(sc, "TIME_ZONE")));
        userFieldSet.add(new Pair(FieldMap.USER_ACTIVE.getFieldKey(), I18n.getString(sc, "ACTIVE")));
        userFieldSet.add(new Pair(FieldMap.USER_EXPIREDATE.getFieldKey(), I18n.getString(sc, "EXPIRE_DATE")));
        userFieldSet.add(new Pair(FieldMap.USER_CHILDCOUNT.getFieldKey(), I18n.getString(sc, "SUBORDINATED_USERS_AMOUNT")));
        userFieldSet.add(new Pair(FieldMap.USER_CHILDALLOWED.getFieldKey(), I18n.getString(sc, "USERS_ALLOWED")));

        for (SecuredUDFValueBean bean : list) {
            userFieldSet.add(new Pair(bean.getId(), bean.getCaption()));
        }
        //Collections.sort(userFieldSet);
        sc.setRequestAttribute(request, "userSet", userFieldSet);
    }

    private void changePreFilter(SessionContext sc, HttpServletRequest request, FilterSettings flthm, FilterSettings originalFilterSettings, String currentField, String oldField, boolean set) throws GranException {
        SecuredFilterBean fltr = AdapterManager.getInstance().getSecuredFindAdapterManager().findFilterById(sc, flthm.getFilterId());
        combineFilterSettings(currentField, originalFilterSettings.getSettings(), flthm.getSettings());
        if (oldField.equals(FieldMap.USER_NAME.getFieldKey()) && currentField.equals(FieldMap.USER_NAME.getFieldKey())) {
            Customizer forName = new TextCustomizer(TextCustomizer.CHARS, FieldMap.USER_NAME);
            if (set) forName.setFilter(sc, request, flthm.getSettings());


        } else if (oldField.equals(FieldMap.USER_EMAIL.getFieldKey()) && currentField.equals(FieldMap.USER_EMAIL.getFieldKey())) {
            Customizer forEmail = new TextCustomizer(TextCustomizer.CHARS, FieldMap.USER_EMAIL);
            if (set) forEmail.setFilter(sc, request, flthm.getSettings());


        } // block
        else if (oldField.equals(FieldMap.USER_STATUS.getFieldKey()) && currentField.equals(FieldMap.USER_STATUS.getFieldKey())) {
            ArrayList<SecuredPrstatusBean> col = AdapterManager.getInstance().getSecuredPrstatusAdapterManager().getAllViewablePrstatuses(sc);
            HashMap<String, String> userStatuses = new HashMap<String, String>();
            for (SecuredPrstatusBean prstatus : col) {
                userStatuses.put(prstatus.getId(), HTMLEncoder.encode(prstatus.getName()));
            }
            Customizer forSubmitterStatus = new ListCustomizer(userStatuses, ListCustomizer.LIST_EQUAL, FieldMap.USER_STATUS, true);
            if (set) forSubmitterStatus.setFilter(sc, request, flthm.getSettings());
        } else if (oldField.equals(FieldMap.USER_COMPANY.getFieldKey()) && currentField.equals(FieldMap.USER_COMPANY.getFieldKey())) {
            Customizer forCompany = new TextCustomizer(TextCustomizer.CHARS, FieldMap.USER_COMPANY);
            if (set) forCompany.setFilter(sc, request, flthm.getSettings());
        } // block
        else if (oldField.equals(FieldMap.USER_CHILDCOUNT.getFieldKey()) && currentField.equals(FieldMap.USER_CHILDCOUNT.getFieldKey())) {
            Customizer forChild = new TextCustomizer(TextCustomizer.NATURAL, FieldMap.USER_CHILDCOUNT);
            if (set) forChild.setFilter(sc, request, flthm.getSettings());
        } // block
        else if (oldField.equals(FieldMap.USER_CHILDALLOWED.getFieldKey()) && currentField.equals(FieldMap.USER_CHILDALLOWED.getFieldKey())) {
            Customizer forChildAllowed = new TextCustomizer(TextCustomizer.NATURAL, FieldMap.USER_CHILDALLOWED);
            if (set) forChildAllowed.setFilter(sc, request, flthm.getSettings());
        } // block
        else if (oldField.equals(FieldMap.USER_TEL.getFieldKey()) && currentField.equals(FieldMap.USER_TEL.getFieldKey())) {
            Customizer forTel = new TextCustomizer(TextCustomizer.CHARS, FieldMap.USER_TEL);
            if (set) forTel.setFilter(sc, request, flthm.getSettings());
        } // block
        else if (oldField.equals(FieldMap.USER_EXPIREDATE.getFieldKey()) && currentField.equals(FieldMap.USER_EXPIREDATE.getFieldKey())) {
            Customizer forExpireDate = new DateCustomizer(FieldMap.USER_EXPIREDATE, sc.getUser().getDateFormatter());
            if (set) forExpireDate.setFilter(sc, request, flthm.getSettings());
        } // block
        else if (oldField.equals(FieldMap.USER_TIMEZONE.getFieldKey()) && currentField.equals(FieldMap.USER_TIMEZONE.getFieldKey())) {
            TreeMap tZones = Config.getInstance().getAvailableTimeZonesMap(DateFormatter.toLocale(sc.getLocale()));
            Customizer forTimezone = new ListCustomizer(tZones, ListCustomizer.LIST_EQUAL, FieldMap.USER_TIMEZONE, true);
            forTimezone.setFilter(sc, request, flthm.getSettings());
        } // block
        else if (oldField.equals(FieldMap.USER_LOCALE.getFieldKey()) && currentField.equals(FieldMap.USER_LOCALE.getFieldKey())) {
            TreeMap locales = Config.getInstance().getAvailableLocalesMap(DateFormatter.toLocale(sc.getLocale()));
            Customizer forLocale = new ListCustomizer(locales, ListCustomizer.LIST_EQUAL, FieldMap.USER_LOCALE, true);
            if (set) forLocale.setFilter(sc, request, flthm.getSettings());
        } // block
        else if (oldField.equals(FieldMap.USER_LOGIN.getFieldKey()) && currentField.equals(FieldMap.USER_LOGIN.getFieldKey())) {
            Customizer forLogin = new TextCustomizer(TextCustomizer.CHARS, FieldMap.USER_LOGIN);
            if (set) forLogin.setFilter(sc, request, flthm.getSettings());
        } else if (oldField.equals(FieldMap.USER_ACTIVE.getFieldKey()) && currentField.equals(FieldMap.USER_ACTIVE.getFieldKey())) {
            HashMap<String, String> actives = new HashMap<String, String>();
            actives.put("True", "True");
            actives.put("False", "False");
            Customizer forActive = new ListCustomizer(actives, ListCustomizer.LIST_EQUAL, FieldMap.USER_ACTIVE, true);
            if (set) forActive.setFilter(sc, request, flthm.getSettings());
        } else {
// UDF
            List<SecuredUDFValueBean> list = fltr.getOwner().getUDFValuesList();

            for (SecuredUDFValueBean udfvf : list) {
                //cnt++;
                if (oldField.equals(udfvf.getId()) && currentField.equals(udfvf.getId())) {
                    Customizer cust;
                    if (udfvf.getUdfType() != UdfValue.USER)
                        cust = udfvf.getCustomizer(udfvf.getId(), request.getAttribute("hidePopups") != null);
                    else {
                        HashMap usersMap = ExternalAdapterManagerUtil.makeUserMap(sc, flthm.getSettings(), (String) sc.getAttribute("taskId"));
                        FieldMap fm = FieldMap.createUDF(udfvf.getCaption(), FValue.UDF + udfvf.getId(), FValue.UDF_SORT + udfvf.getId());
                        cust = new ListCustomizer(usersMap, ListCustomizer.LIST_EQUAL, fm, true);
                    }
                    if (set) cust.setFilter(sc, request, flthm.getSettings());
                }
            } // for
            // if
        }
    }

    public ActionForward changeField(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            PreFilterForm bf = (PreFilterForm) form;
            String userId = bf.getId();
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            UserFValue val = AdapterManager.getInstance().getSecuredFilterAdapterManager().getUserFValue(sc, bf.getFilter()).getFValue();
            String currentField = bf.getField();
            FilterSettings flthm = generalChangeFilter(bf, request, currentField, sc, val, filterParameter);
            if (flthm != null) {

                if (sc.canAction(Action.manageUserPrivateFilters, id)) {
                    changePreFilter(sc, request, flthm, new FilterSettings(val, bf.getId(), bf.getFilter()), currentField, bf.getOldfield(), false);
                }
                sc.setAttribute(filterParameter, flthm);
                bf.setOldfield(bf.getField());
            }
            return mapping.findForward(this.action);
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    private void fillCustomizers(String currentField, SessionContext sc, HttpServletRequest request, FilterSettings flthm, SecuredFilterBean fltr) throws GranException {
        if (currentField.equals(FieldMap.USER_NAME.getFieldKey())) {
            Customizer forName = new TextCustomizer(TextCustomizer.CHARS, FieldMap.USER_NAME);
            forName.setFilter(sc, request, flthm.getSettings());


        } else if (currentField.equals(FieldMap.USER_EMAIL.getFieldKey())) {
            Customizer forEmail = new TextCustomizer(TextCustomizer.CHARS, FieldMap.USER_EMAIL);
            forEmail.setFilter(sc, request, flthm.getSettings());


        } // block
        else if (currentField.equals(FieldMap.USER_STATUS.getFieldKey())) {
            ArrayList<SecuredPrstatusBean> col = AdapterManager.getInstance().getSecuredPrstatusAdapterManager().getAllViewablePrstatuses(sc);
            HashMap<String, String> userStatuses = new HashMap<String, String>();
            for (SecuredPrstatusBean prstatus : col) {
                userStatuses.put(prstatus.getId(), HTMLEncoder.encode(prstatus.getName()));
            }
            Customizer forSubmitterStatus = new ListCustomizer(userStatuses, ListCustomizer.LIST_EQUAL, FieldMap.USER_STATUS, true);
            forSubmitterStatus.setFilter(sc, request, flthm.getSettings());
        } else if (currentField.equals(FieldMap.USER_COMPANY.getFieldKey())) {
            Customizer forCompany = new TextCustomizer(TextCustomizer.CHARS, FieldMap.USER_COMPANY);
            forCompany.setFilter(sc, request, flthm.getSettings());
        } // block
        else if (currentField.equals(FieldMap.USER_CHILDCOUNT.getFieldKey())) {
            Customizer forChild = new TextCustomizer(TextCustomizer.NATURAL, FieldMap.USER_CHILDCOUNT);
            forChild.setFilter(sc, request, flthm.getSettings());
        } // block
        else if (currentField.equals(FieldMap.USER_CHILDALLOWED.getFieldKey())) {
            Customizer forChildAllowed = new TextCustomizer(TextCustomizer.NATURAL, FieldMap.USER_CHILDALLOWED);
            forChildAllowed.setFilter(sc, request, flthm.getSettings());
        } // block
        else if (currentField.equals(FieldMap.USER_TEL.getFieldKey())) {
            Customizer forTel = new TextCustomizer(TextCustomizer.CHARS, FieldMap.USER_TEL);
            forTel.setFilter(sc, request, flthm.getSettings());
        } // block
        else if (currentField.equals(FieldMap.USER_EXPIREDATE.getFieldKey())) {
            Customizer forExpireDate = new DateCustomizer(FieldMap.USER_EXPIREDATE, sc.getUser().getDateFormatter());
            forExpireDate.setFilter(sc, request, flthm.getSettings());
        } // block
        else if (currentField.equals(FieldMap.USER_TIMEZONE.getFieldKey())) {
            TreeMap tZones = Config.getInstance().getAvailableTimeZonesMap(DateFormatter.toLocale(sc.getLocale()));
            Customizer forTimezone = new ListCustomizer(tZones, ListCustomizer.LIST_EQUAL, FieldMap.USER_TIMEZONE, true);
            forTimezone.setFilter(sc, request, flthm.getSettings());
        } // block
        else if (currentField.equals(FieldMap.USER_LOCALE.getFieldKey())) {
            TreeMap locales = Config.getInstance().getAvailableLocalesMap(DateFormatter.toLocale(sc.getLocale()));
            Customizer forLocale = new ListCustomizer(locales, ListCustomizer.LIST_EQUAL, FieldMap.USER_LOCALE, true);
            forLocale.setFilter(sc, request, flthm.getSettings());
        } // block
        else if (currentField.equals(FieldMap.USER_LOGIN.getFieldKey())) {
            Customizer forLogin = new TextCustomizer(TextCustomizer.CHARS, FieldMap.USER_LOGIN);
            forLogin.setFilter(sc, request, flthm.getSettings());
        } else if (currentField.equals(FieldMap.USER_ACTIVE.getFieldKey())) {
            HashMap<String, String> actives = new HashMap<String, String>();
            actives.put("True", "True");
            actives.put("False", "False");
            Customizer forActive = new ListCustomizer(actives, ListCustomizer.LIST_EQUAL, FieldMap.USER_ACTIVE, true);
            forActive.setFilter(sc, request, flthm.getSettings());
        } else {
// UDF
            List<SecuredUDFValueBean> list = fltr.getOwner().getUDFValuesList();

            for (SecuredUDFValueBean udfvf : list) {
                //cnt++;
                if (currentField.equals(udfvf.getId())) {
                    Customizer cust;
                    if (udfvf.getUdfType() != UdfValue.USER)
                        cust = udfvf.getCustomizer(udfvf.getId(), request.getAttribute("hidePopups") != null);
                    else {
                        HashMap usersMap = ExternalAdapterManagerUtil.makeUserMap(sc, flthm.getSettings(), (String) sc.getAttribute("taskId"));
                        FieldMap fm = FieldMap.createUDF(udfvf.getCaption(), FValue.UDF + udfvf.getId(), FValue.UDF_SORT + udfvf.getId());
                        cust = new ListCustomizer(usersMap, ListCustomizer.LIST_EQUAL, fm, true);
                    }
                    cust.setFilter(sc, request, flthm.getSettings());
                }
            } // for
            // if
        }
    }


    public ActionForward changeFilter(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            PreFilterForm bf = (PreFilterForm) form;

            SessionContext sc = (SessionContext) request.getAttribute("sc");

            UserFValue val = AdapterManager.getInstance().getSecuredFilterAdapterManager().getUserFValue(sc, bf.getFilter()).getFValue();
            FilterSettings flthm = null;
            String currentField = bf.getField();
            //String filterId = bf.getFilter();

            flthm = generalChangeFilter(bf, request, currentField, sc, val, filterParameter);
            if (sc.canAction(Action.manageUserPrivateFilters, id)) {
                currentField = flthm.getFieldId();
                changePreFilter(sc, request, flthm, new FilterSettings(val, bf.getId(), bf.getFilter()), currentField, bf.getOldfield(), true);
            }
            sc.setAttribute(filterParameter, flthm);
            bf.setOldfield(bf.getField());

            if (bf.getSaveButton() != null) {
                SecuredFilterBean curSfb = AdapterManager.getInstance().getSecuredFindAdapterManager().findFilterById(sc, filterId);
                if (curSfb != null) {
                    Calendar time = sc.getUser().getDateFormatter().getCalendar();
                    filterId = AdapterManager.getInstance().getSecuredFilterAdapterManager().createUserFilter(sc, id, curSfb.getName() + " (" + sc.getUser().getDateFormatter().parse(time) + ")", curSfb.getDescription(), true, curSfb.getPreferences());
                    flthm.setFilterId(filterId);
                    AdapterManager.getInstance().getSecuredFilterAdapterManager().setFValue(sc, filterId, new SecuredUserFValueBean(flthm.getSettings(), sc));
                    AdapterManager.getInstance().getSecuredFilterAdapterManager().setCurrentUserFilter(sc, bf.getId(), filterId);
                    bf.setFilter(filterId);
                    sc.setAttribute(filterParameter, flthm);
                }
            }

            if (bf.getGo() != null || bf.getReset() != null || bf.getSaveButton() != null)
                return mapping.findForward(this.forward);
            else
                return mapping.findForward(this.action);
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public static FilterSettings generalChangeFilter(PreFilterForm bf, HttpServletRequest request, String currentField, SessionContext sc, FValue val, String filterParameter) {
        FilterSettings flthm = null;
        String id = bf.getId();
        String filterid = bf.getFilter();

        boolean isReset = request.getParameter("reset") != null;
        if (isReset || currentField != null && Null.isNotNull(currentField) && currentField.equals("default")) {
            sc.removeAttribute(filterParameter);
            currentField = "default";
            bf.setField(currentField);
            bf.setOldfield("default");

        }


        Object filterObject = sc.getAttribute(filterParameter);
        if (filterObject != null) {
            flthm = (FilterSettings) filterObject;
            if (!flthm.getFilterId().equals(filterid) || !flthm.getObjectId().equals(id)) {
                flthm = null;
                currentField = "default";
                bf.setField(currentField);
                bf.setOldfield("default");
            }
        }
        FilterSettings originFilterSettings = new FilterSettings(val, id, filterid);
        if (flthm == null /*|| (bf.getGo() == null && bf.getReset() == null && bf.getSaveButton() == null && request.getParameter("add") == null && sc.getAttribute("changedTaskFilterParameter") == null)*/) {
            flthm = originFilterSettings;
        }

        if (Null.isNull(currentField) && flthm.getFieldId() == null) {
            flthm.setFieldId("default");
        } else if (Null.isNotNull(currentField)) {
            flthm.setFieldId(currentField);
        }

        if (bf.goPressed())
            flthm.setCurrentPage(1);
        if (bf.getSliderPage() != null && bf.getSliderPage().length() != 0) {
            flthm.setCurrentPage(Integer.parseInt(bf.getSliderPage()));
        }

        if (bf.getSliderOrder() != null && bf.getSliderOrder().length() != 0) {
            flthm.setSortedBy(FValue.parseFilterValue(bf.getSliderOrder()));
        }
        return flthm;
    }


    private void combineFilterSettings(String field, FValue original, FValue changed) throws GranException {
        for (FieldMap s : FieldMap.userFields) {
            checkValue(field, original, changed, s.getFilterKey());
        }

    }

    // Какая-то полная лажа. Критерием, какой параметр оставлять, не может быть размер
    private void checkValue(String field, FValue original, FValue changed, String constant) {
        if (FieldMap.getFilterKeyByFieldKey(field) != null && FieldMap.getFilterKeyByFieldKey(field).equals(constant) && !changed.containsKey(constant) && original.containsKey(constant)) {

            if (original.get(constant).size() == 1) {

                changed.set(constant, original.get(constant).get(0));
            } else if (original.get(constant).size() > 1) {
                changed.setList(constant, original.get(constant));
            }
        }
    }


}
