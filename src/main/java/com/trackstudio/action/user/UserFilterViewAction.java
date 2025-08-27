package com.trackstudio.action.user;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.trackstudio.action.GeneralAction;
import com.trackstudio.action.TSDispatchAction;
import com.trackstudio.app.Preferences;
import com.trackstudio.app.UdfValue;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.filter.FValue;
import com.trackstudio.app.filter.UserFValue;
import com.trackstudio.app.filter.customizer.BudgetCustomizer;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.common.FieldMap;
import com.trackstudio.exception.GranException;
import com.trackstudio.form.FilterForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.secured.SecuredFilterBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUDFValueBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.Pair;
import com.trackstudio.tools.formatter.DateFormatter;
import com.trackstudio.tools.formatter.HourFormatter;
import com.trackstudio.tools.textfilter.HTMLEncoder;
import com.trackstudio.view.UserViewHTMLLinked;

public class UserFilterViewAction extends TSDispatchAction {

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            SessionContext sc = GeneralAction.getInstance().imports(request, response);
            String userId = GeneralAction.getInstance().userHeader(null, sc, request);
            FilterForm ff = (FilterForm) form;

            String filterId = ff.getFilterId() != null ? ff.getFilterId() : request.getParameter("filterId");

            SecuredFilterBean currentFilter = AdapterManager.getInstance().getSecuredFindAdapterManager().findFilterById(sc, filterId);
            if (!sc.canAction(Action.viewUserFilters, userId))
                return null;

            UserFValue flthm = AdapterManager.getInstance().getSecuredFilterAdapterManager().getUserFValue(sc, filterId).getFValue();
            sc.setRequestAttribute(request, "owner", new UserViewHTMLLinked(currentFilter.getOwner(), request.getContextPath()).getPath());
            sc.setRequestAttribute(request, "onpage", flthm.getAsString(UserFValue.ONPAGE));
            sc.setRequestAttribute(request, "search", flthm.getAsString(FValue.DEEPSEARCH) != null);

            HashMap<String, ArrayList> map = makeFilterView(currentFilter, flthm);
            for (Map.Entry e : map.entrySet()) {
                sc.setRequestAttribute(request, e.getKey().toString(), e.getValue());
            }
            sc.setRequestAttribute(request, "currentFilter", currentFilter);
            sc.setRequestAttribute(request, "filterId", filterId);
            sc.setRequestAttribute(request, "canEdit", currentFilter.getUserId().equals(sc.getUserId()) || sc.canAction(Action.manageUserPrivateFilters, userId) && (sc.canAction(Action.manageUserPublicFilters, userId) || currentFilter.isPrivate()) && currentFilter.canManage());
            sc.setRequestAttribute(request, "showInToolbar", Preferences.showInToolbar(currentFilter.getPreferences()));
            return mapping.findForward("viewUserFilterJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public static HashMap<String, ArrayList> makeFilterView(SecuredFilterBean currentFilter, FValue flthm) throws GranException {
        SessionContext sc = currentFilter.getSecure();
        ArrayList<Pair> fd = new ArrayList<Pair>();
        ArrayList<String> display = new ArrayList<String>();
        ArrayList<FieldMap> sort = new ArrayList<FieldMap>();
        printString(sc, FieldMap.USER_LOGIN, flthm, fd, display, sort);
        printString(sc, FieldMap.USER_NAME, flthm, fd, display, sort);
        printString(sc, FieldMap.FULLPATH, flthm, fd, display, sort);

        List<String> values = new ArrayList<String>();
        List<String> propertyValuesCollection;
        propertyValuesCollection = flthm.toList(FieldMap.USER_STATUS.getFilterKey());
        if (propertyValuesCollection != null)
            for (String aPropertyValuesCollection3 : propertyValuesCollection)
                values.add(AdapterManager.getInstance().getSecuredFindAdapterManager().findPrstatusById(sc, aPropertyValuesCollection3).getName());

        printList(sc, FieldMap.USER_STATUS, flthm, values, fd, display, sort);

        printString(sc, FieldMap.USER_COMPANY, flthm, fd, display, sort);

        printString(sc, FieldMap.USER_EMAIL, flthm, fd, display, sort);
        printString(sc, FieldMap.USER_TEL, flthm, fd, display, sort);
        printString(sc, FieldMap.USER_PARENT, flthm, fd, display, sort);
        printString(sc, FieldMap.USER_TEMPLATE, flthm, fd, display, sort);

        propertyValuesCollection = flthm.toList(FieldMap.USER_LOCALE.getFilterKey());
        values = new ArrayList<String>();
        if (propertyValuesCollection != null)
            for (String aPropertyValuesCollection4 : propertyValuesCollection)
                values.add(DateFormatter.toLocale(aPropertyValuesCollection4).getDisplayName(DateFormatter.toLocale(sc.getLocale())));

        printList(sc, FieldMap.USER_LOCALE, flthm, values, fd, display, sort);

        values = new ArrayList<String>();
        propertyValuesCollection = flthm.toList(FieldMap.USER_TIMEZONE.getFilterKey());
        if (propertyValuesCollection != null)
            for (String aPropertyValuesCollection2 : propertyValuesCollection)
                values.add(DateFormatter.getTimeZoneFromString(aPropertyValuesCollection2).getDisplayName());

        printList(sc, FieldMap.USER_TIMEZONE, flthm, values, fd, display, sort);

        values = new ArrayList<String>();
        propertyValuesCollection = flthm.toList(FieldMap.USER_ACTIVE.getFilterKey());
        if (propertyValuesCollection != null)
            for (String aPropertyValuesCollection1 : propertyValuesCollection)
                values.add(aPropertyValuesCollection1);

        printList(sc, FieldMap.USER_ACTIVE, flthm, values, fd, display, sort);
        printDate(sc, FieldMap.USER_EXPIREDATE, flthm, fd, display, sort);

        printNumber(sc, FieldMap.USER_CHILDCOUNT, flthm, fd, display, sort);
        printNumber(sc, FieldMap.USER_CHILDALLOWED, flthm, fd, display, sort);

        SecuredUserBean filterUser = new SecuredUserBean(currentFilter.getOwnerId(), sc);
        List<SecuredUDFValueBean> list = new ArrayList<SecuredUDFValueBean>(filterUser.getUDFValuesList());

        for (SecuredUDFValueBean udf : list) {
            int type = udf.getUdfType();
            if (type == UdfValue.STRING || type == UdfValue.MEMO || type == UdfValue.URL) { // name or memo
                printString(sc, FieldMap.createUDF(udf.getCaption(), FValue.UDF + udf.getId(), FValue.UDF_SORT + udf.getId()), flthm, fd, display, sort);
            }
            if (type == UdfValue.INTEGER || type == UdfValue.FLOAT) { // numeric value
                printNumber(sc, FieldMap.createUDF(udf.getCaption(), FValue.UDF + udf.getId(), FValue.UDF_SORT + udf.getId()), flthm, fd, display, sort);
            }
            if (type == UdfValue.DATE) { // Date
                printDate(sc, FieldMap.createUDF(udf.getCaption(), FValue.UDF + udf.getId(), FValue.UDF_SORT + udf.getId()), flthm, fd, display, sort);
            }
            if (type == UdfValue.LIST || type == UdfValue.MULTILIST || type == UdfValue.TASK || type == UdfValue.USER) { // List

                values = new ArrayList<String>();
                propertyValuesCollection = flthm.toList(FValue.UDF + udf.getId());
                if (type == UdfValue.LIST || type == UdfValue.MULTILIST)
                    if (propertyValuesCollection != null) {
                        for (String aPropertyValuesCollection : propertyValuesCollection)
                            values.add(AdapterManager.getInstance().getSecuredFindAdapterManager().findUdflistById(sc, aPropertyValuesCollection).getVal());

                        printList(sc, FieldMap.createUDF(udf.getCaption(), FValue.UDF + udf.getId(), FValue.UDF_SORT + udf.getId()), flthm, values, fd, display, sort);
                    }
                if (type == UdfValue.TASK)
                    if (propertyValuesCollection != null) {
                        for (String aPropertyValuesCollection1 : propertyValuesCollection)
                            values.add(new SecuredTaskBean(aPropertyValuesCollection1, sc).getName());

                        printList(sc, FieldMap.createUDF(udf.getCaption(), FValue.UDF + udf.getId(), FValue.UDF_SORT + udf.getId()), flthm, values, fd, display, sort);
                    }
                if (type == UdfValue.USER) {
                    if (propertyValuesCollection != null) {
                        for (String aPropertyValuesCollection : propertyValuesCollection)
                            values.add(new SecuredUserBean(aPropertyValuesCollection, sc).getName());

                        printList(sc, FieldMap.createUDF(udf.getCaption(), FValue.UDF + udf.getId(), FValue.UDF_SORT + udf.getId()), flthm, values, fd, display, sort);
                    }
                }
            }
        }
        List<String> sortList = flthm.getSortOrder();
        ArrayList<String> sortion = new ArrayList<String>();

        for (String str : sortList) {
            String method;
            if (str.startsWith(FValue.SUB)) {
                str = str.substring(1);
                method = I18n.getString(sc, "ASC");
            } else {
                method = I18n.getString(sc, "SORT_DESC");
            }
            int k = sort.indexOf(new FieldMap(0, null, null, str));
            if (k > -1) {
                sortion.add(method + " <b>" + HTMLEncoder.encode(I18n.getString(sc, (sort.get(k)).getAltKey())) + "</b>");
            }

        }
        HashMap<String, ArrayList> ret = new HashMap<String, ArrayList>();
        ret.put("filter", fd);
        ret.put("display", display);
        ret.put("sort", sortion);

        return ret;

    }

    public static void printString(SessionContext sc, FieldMap map, FValue flthm, ArrayList<Pair> prop, ArrayList<String> display, ArrayList<FieldMap> sort) throws GranException {
        StringBuffer fd = new StringBuffer(200);
        if (flthm.hasListValue(FValue.DISPLAY, map.getFilterKey())) {
            display.add(I18n.getString(sc, map.getAltKey()));
            if (flthm.getSortOrder().toString().indexOf(map.getFieldKey()) > -1)
                sort.add(map);
        }
        String str = flthm.getAsString(map.getFilterKey());
        String prefix = flthm.getPrefix(map.getFilterKey());

        if ((str != null && str.length() > 0) || prefix.equals(FValue.EMPTY)) {
            if (prefix.equals(FValue.EQ)) {
                fd.append(I18n.getString(sc, "EQUALS"));
            } else if (prefix.equals(FValue.NE)) {
                fd.append(I18n.getString(sc, "UNEQUALS"));
            } else if (prefix.equals(FValue.RE)) {
                fd.append(I18n.getString(sc, "REGEXP"));
            } else if (prefix.equals(FValue.EMPTY)) {
                fd.append(I18n.getString(sc, "EMPTY"));
            } else if (prefix.equals(FValue.SUB)) {
                fd.append(I18n.getString(sc, "STARTS_WITH"));
            } else {
                fd.append(I18n.getString(sc, "CONTAINS"));
            }
            fd.append(" <b>");
            fd.append(HTMLEncoder.encode(str == null ? "" : str));
            fd.append("</b>");
            prop.add(new Pair(I18n.getString(sc, map.getAltKey()), fd.toString(), map.getFilterKey()));
        }
    }

    public static void printQuantity(SessionContext sc, FieldMap map, FValue flthm, ArrayList<Pair> prop, ArrayList<String> display, ArrayList<FieldMap> sort) throws GranException {
        StringBuffer fd = new StringBuffer(200);
        if (flthm.hasListValue(FValue.DISPLAY, map.getFilterKey())) {
            display.add(I18n.getString(sc, map.getAltKey()));
            if (flthm.getSortOrder().indexOf(map.getFieldKey()) > -1)
                sort.add(map);
        }
        String str = flthm.getAsString(map.getFilterKey());
        String prefix = flthm.getPrefix(map.getFilterKey());


        if (str != null && str.length() > 0) {
            if (prefix.equals(FValue.SUB)) {
                fd.append(I18n.getString(sc, "LAST"));
            } else {
                fd.append(I18n.getString(sc, "FIRST"));
            }
            fd.append(" <b>");
            fd.append(str);
            fd.append("</b>");
            prop.add(new Pair(I18n.getString(sc, map.getAltKey()), fd.toString(), map.getFilterKey()));
        }


    }

    private static String convertNumber(String str, FieldMap map, SessionContext sc) {
        if (str == null || str.length() == 0)
            return "";
        String ret;

        if (map.equals(FieldMap.TASK_ABUDGET) || map.equals(FieldMap.TASK_BUDGET) || map.equals(FieldMap.MSG_ABUDGET)) {
            try {

                HourFormatter hf = new HourFormatter(Long.parseLong(str), BudgetCustomizer.ALL, sc.getLocale());
                ret = hf.getString();
            } catch (Exception e) {
                ret = "";
            }
        } else {
            try {
                ret = new Integer(str).toString();
            } catch (NumberFormatException e) {
                try {
                    ret = Double.toString(Long.valueOf(Math.round(Double.parseDouble(str) * 100.0)).doubleValue() / 100.0);
                } catch (Exception ex) {
                    ret = "";
                }
            }
        }
        return ret;
    }

    public static void printNumber(SessionContext sc, FieldMap map, FValue flthm, ArrayList<Pair> prop, ArrayList<String> display, ArrayList<FieldMap> sort) throws GranException {
        StringBuffer fd = new StringBuffer(200);
        if (flthm.hasListValue(FValue.DISPLAY, map.getFilterKey())) {
            display.add(I18n.getString(sc, map.getAltKey()));
            if (flthm.getSortOrder().toString().indexOf(map.getFieldKey()) > -1)
                sort.add(map);
        }
        String str = flthm.getAsString(map.getFilterKey());
        String prefix = flthm.getPrefix(map.getFilterKey());
        if (str != null && str.length() > 0) {
            String str2;
            if (prefix.equals(FValue.EQ)) {
                str2 = convertNumber(str, map, sc);
                if (str2.length() == 0) return;
                fd.append(I18n.getString(sc, "EQUALS"));
            } else if (prefix.equals(FValue.NE)) {
                str2 = convertNumber(str, map, sc);
                if (str2.length() == 0) return;
                fd.append(I18n.getString(sc, "UNEQUALS"));
            } else if (prefix.equals(FValue.SUB)) {
                str2 = convertNumber(str, map, sc);
                if (str2.length() == 0) return;
                fd.append(I18n.getString(sc, "EQ_LESS"));
            } else if (prefix.equals(FValue.IN)) {
                str2 = str;
                fd.append(I18n.getString(sc, "IN_SET"));
            } else {
                str2 = convertNumber(str, map, sc);
                if (str2.length() == 0) return;
                fd.append(I18n.getString(sc, "EQ_MORE"));
            }
            fd.append(" <b>");
            fd.append(str2);
            fd.append("</b>");

            prop.add(new Pair(I18n.getString(sc, map.getAltKey()), fd.toString(), map.getFilterKey()));

        }
    }

    public static void printList(SessionContext sc, FieldMap map, FValue flthm, List values, List<Pair> prop, List<String> display, List<FieldMap> sort) throws GranException {

        String prefix = flthm.getPrefix(map.getFilterKey());
        StringBuffer fd = new StringBuffer(200);
        if (flthm.hasListValue(FValue.DISPLAY, map.getFilterKey())) {
            display.add(I18n.getString(sc, map.getAltKey()));
            if (flthm.getSortOrder().toString().indexOf(map.getFieldKey()) > -1)
                sort.add(map);
        }

        if (flthm.getSortOrder().indexOf(map.getFieldKey()) > -1)
            sort.add(map);
        if (values != null && !values.isEmpty()) {

            if (prefix.equals(FValue.SUB)) {
                fd.append(I18n.getString(sc, "NOT_IN"));
            } else {
                fd.append(I18n.getString(sc, "IN_SET"));
            }
            fd.append(" <b>");
            fd.append("[");
            for (Object value : values) {
                fd.append(HTMLEncoder.encode((String) value)).append(", ");
            }
            fd.setCharAt(fd.length() - 2, ']');
            fd.append("</b>");

            prop.add(new Pair(I18n.getString(sc, map.getAltKey()), fd.toString(), map.getFilterKey()));
        }

    }

    public static void printDate(SessionContext sc, FieldMap map, FValue flthm, ArrayList<Pair> prop, ArrayList<String> display, ArrayList<FieldMap> sort) throws GranException {
        StringBuffer fd = new StringBuffer(200);
        if (flthm.hasListValue(FValue.DISPLAY, map.getFilterKey())) {
            display.add(I18n.getString(sc, map.getAltKey()));
            if (flthm.getSortOrder().toString().indexOf(map.getFieldKey()) > -1)
                sort.add(map);
        }
        // amount of days, hours, minutes, etc
        String amountValue = flthm.getAsString(FValue.AMNT + map.getFilterKey());
        String periodValue = flthm.getAsString(FValue.PERIOD + map.getFilterKey());
        // заполнено количество дней для before/after - это имеет приоритет

        if (periodValue != null) {
            fd.append("<b>");
            fd.append(I18n.getString(sc,"PERIOD"));
            fd.append("</b>");
            fd.append(' ');
            if ("1".equals(periodValue)) {
                fd.append(I18n.getString(sc,"FOR_CURRENT_WEEK"));
            }
            if ("2".equals(periodValue)) {
                fd.append(I18n.getString(sc,"FOR_CURRENT_MONTH"));
            }
            if ("3".equals(periodValue)) {
                fd.append(I18n.getString(sc,"FOR_CURRENT_YEAR"));
            }
            if ("4".equals(periodValue)) {
                fd.append(I18n.getString(sc,"FOR_CURRENT_QUARTER"));
            }
            if ("5".equals(periodValue)) {
                fd.append(I18n.getString(sc,"FOR_PREVIOUS_WEEK"));
            }
            if ("6".equals(periodValue)) {
                fd.append(I18n.getString(sc,"FOR_PREVIOUS_MONTH"));
            }
            if ("7".equals(periodValue)) {
                fd.append(I18n.getString(sc,"FOR_PREVIOUS_YEAR"));
            }
            if ("8".equals(periodValue)) {
                fd.append(I18n.getString(sc,"FOR_PREVIOUS_QUARTER"));
            }
            if  ("9".equals(periodValue)) {
                fd.append(I18n.getString(sc, "FOR_NEXT_WEEK"));
            }
            if  ("10".equals(periodValue)) {
                fd.append(I18n.getString(sc, "FOR_NEXT_MONTH"));
            }
            if  ("11".equals(periodValue)) {
                fd.append(I18n.getString(sc, "FOR_NEXT_YEAR"));
            }
            if  ("12".equals(periodValue)) {
                fd.append(I18n.getString(sc, "FOR_NEXT_QUARTER"));
            }
            prop.add(new Pair(I18n.getString(sc, map.getAltKey()), fd.toString(), map.getFilterKey()));


        } else {
            if (amountValue != null) {

            // 0 - hours, 1 - days, 2 - months
            String intervalValue = flthm.getAsString(FValue.INTERVAL + map.getFilterKey());

            // 0 - before
            String beforeAfterValue = flthm.getAsString(FValue.BA + map.getFilterKey());

            // 0 - early, 1 - later
            String earlyLaterValue = flthm.getAsString(FValue.EL + map.getFilterKey());

            // количество в before/after заполнено
            if (intervalValue != null && beforeAfterValue != null && earlyLaterValue != null) {
                int amount = Integer.parseInt(amountValue);
                fd.append("<b>");
                fd.append(amount);
                fd.append("</b>");
                fd.append(' ');
                if ("0".equals(intervalValue)) // hours
                {
                    fd.append(I18n.getString(sc, "MINUTES"));
                }

                if ("1".equals(intervalValue)) // days
                {
                    fd.append(I18n.getString(sc, "HOURS"));
                }

                if ("2".equals(intervalValue)) // month
                {
                    fd.append(I18n.getString(sc, "DAY"));
                }

                if ("3".equals(intervalValue)) // month
                {
                    fd.append(I18n.getString(sc, "MONTH"));
                }
                if ("0".equals(beforeAfterValue)) // before
                {
                    fd.append(' ');
                    fd.append(I18n.getString(sc, "BEFORE"));
                } else {
                    fd.append(' ');
                    fd.append(I18n.getString(sc, "AFTER"));
                }

                if ("0".equals(earlyLaterValue)) // or earlier
                {
                    fd.append(' ');
                    fd.append(I18n.getString(sc, "EARLIER"));
                }

                if ("1".equals(earlyLaterValue)) // or later
                {
                    fd.append(' ');
                    fd.append(I18n.getString(sc, "LATER"));
                }


                prop.add(new Pair(I18n.getString(sc, map.getAltKey()), fd.toString(), map.getFilterKey()));
            }

        } else {    // from/to заполнены

            // if propertyValue start with with _ "from date", else "to date";
            String propertyFromValue = flthm.getAsString(FValue.SUB + map.getFilterKey());
            String propertyToValue = flthm.getAsString(map.getFilterKey());
            Calendar to = null;
            if (propertyToValue != null && !"".equals(propertyToValue)) { // to date
                to = new GregorianCalendar();
                to.setTimeInMillis(Long.parseLong(propertyToValue));
            }
            Calendar from = null;

            if (propertyFromValue != null && !"".equals(propertyFromValue)) {  // from date
                from = new GregorianCalendar();
                from.setTimeInMillis(Long.parseLong(propertyFromValue));
            }
            if (from != null || to != null) {
                if (from != null && to != null) {
                    fd.append(I18n.getString(sc, "BETWEEN"));
                    fd.append(" <b>");
                    fd.append(sc.getUser().getDateFormatter().parse(from));
                    fd.append("</b>");
                    fd.append(' ');
                    fd.append(I18n.getString(sc, "AND"));
                    fd.append(" <b>");
                    fd.append(sc.getUser().getDateFormatter().parse(to));
                    fd.append("</b>");
                } else if (from != null) {
                    fd.append(I18n.getString(sc, "FROM"));
                    fd.append(" <b>");
                    fd.append(sc.getUser().getDateFormatter().parse(from));
                    fd.append("</b>");
                } else {
                    fd.append(I18n.getString(sc, "TO"));
                    fd.append(" <b>");
                    fd.append(sc.getUser().getDateFormatter().parse(to));
                    fd.append("</b>");
                }
                prop.add(new Pair(I18n.getString(sc, map.getAltKey()), fd.toString(), FValue.SUB + map.getFilterKey()));
            }
            }
        }
    }

    public static void checkbox(SessionContext sc, FieldMap map, FValue flthm, ArrayList<Pair> prop, String context) throws GranException {
        if ("1".equals(flthm.getAsString(map.getFilterKey()))) {
            StringBuffer fd = new StringBuffer(200);
            fd.append("<img src=\"").append(context).append("/ImageServlet/").append(GeneralAction.SERVLET_KEY).append("/cssimages/ico.checked.gif\"/>");
            prop.add(new Pair(I18n.getString(sc, map.getAltKey()), fd.toString(), map.getFilterKey()));
        }
    }
}
