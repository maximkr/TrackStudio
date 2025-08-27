package com.trackstudio.action.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.trackstudio.action.GeneralAction;
import com.trackstudio.action.TSDispatchAction;
import com.trackstudio.action.user.UserFilterViewAction;
import com.trackstudio.app.Preferences;
import com.trackstudio.app.UdfValue;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.filter.FValue;
import com.trackstudio.app.filter.TaskFValue;
import com.trackstudio.app.filter.list.TaskFilter;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.common.FieldMap;
import com.trackstudio.exception.CantFindObjectException;
import com.trackstudio.exception.GranException;
import com.trackstudio.form.FilterForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.cache.TaskRelatedManager;
import com.trackstudio.kernel.cache.UserRelatedManager;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.model.Category;
import com.trackstudio.model.Priority;
import com.trackstudio.model.Resolution;
import com.trackstudio.model.Status;
import com.trackstudio.secured.SecuredFilterBean;
import com.trackstudio.secured.SecuredMstatusBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUDFBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.Pair;
import com.trackstudio.tools.compare.FieldSort;
import com.trackstudio.tools.compare.SortUdf;
import com.trackstudio.tools.textfilter.HTMLEncoder;

public class TaskFilterViewAction extends TSDispatchAction {
    private static final String VIEW_FILTER = "v_filter";
    private static final String VIEW_DISPLAY = "v_display";
    private static final String VIEW_SORT = "v_sort";
    private static final String VIEW_MESSAGE = "v_message";

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws GranException {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            SessionContext sc = GeneralAction.getInstance().imports(request, response);
            String id = GeneralAction.getInstance().taskHeader(null, sc, request, true);

            if (!sc.canAction(Action.viewFilters, id))
                return null;

            FilterForm ff = (FilterForm) form;
            String filterId = ff.getFilterId() != null ? ff.getFilterId() : request.getParameter("filterId");

            SecuredFilterBean currentFilter = AdapterManager.getInstance().getSecuredFindAdapterManager().findFilterById(sc, filterId);
            TaskFValue flthm = AdapterManager.getInstance().getSecuredFilterAdapterManager().getTaskFValue(sc, filterId).getFValue();

            sc.setRequestAttribute(request, "onpage", flthm.getAsString(TaskFValue.ONPAGE));
            sc.setRequestAttribute(request, "subtask", flthm.getAsString(FValue.SUBTASK) != null);
            sc.setRequestAttribute(request, "search", flthm.getAsString(FieldMap.SEARCH.getFilterKey()));
            String msgs = flthm.getAsString(FieldMap.MESSAGEVIEW.getFilterKey());
            String messages = "";
            if (msgs != null) {
                if (msgs.equals("1"))
                    messages = I18n.getString(sc.getLocale(), "LAST_MESSAGE");
                if (msgs.equals("3"))
                    messages = I18n.getString(sc.getLocale(), "LAST_3_MESSAGES");
                if (msgs.equals("5"))
                    messages = I18n.getString(sc.getLocale(), "LAST_5_MESSAGES");
                if (msgs.equals("10"))
                    messages = I18n.getString(sc.getLocale(), "LAST_10_MESSAGES");
                if (msgs.equals("-1"))
                    messages = I18n.getString(sc.getLocale(), "ALL_HISTORY");
            }
            sc.setRequestAttribute(request, "messages", messages);

            HashMap<String, ArrayList> map = makeFilterView(currentFilter, flthm, id, request.getContextPath());
            for (String key : map.keySet()) {
                ArrayList value = map.get(key);
                sc.setRequestAttribute(request, key, value);
            }

            sc.setRequestAttribute(request, "currentFilter", currentFilter);
            sc.setRequestAttribute(request, "showInToolbar", Preferences.showInToolbar(currentFilter.getPreferences()));
            sc.setRequestAttribute(request, "filterId", filterId);
            boolean canEditTaskProperties = sc.canAction(Action.manageTaskPrivateFilters, currentFilter.getTaskId()) && currentFilter.canManage() && (sc.canAction(Action.manageTaskPublicFilters, id) || currentFilter.isPrivate());

            sc.setRequestAttribute(request, "canEdit", canEditTaskProperties);
            return mapping.findForward("viewTaskFilterJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public static HashMap<String, ArrayList> makeFilterView(SecuredFilterBean currentFilter, FValue flthm, String taskId, String context) throws GranException {
        SessionContext sc = currentFilter.getSecure();
        ArrayList<Pair> fd = new ArrayList<Pair>();
        ArrayList<String> use = new ArrayList<String>();
        ArrayList<FieldMap> sort = new ArrayList<FieldMap>();
        UserFilterViewAction.checkbox(sc, FieldMap.DEEP_SEARCH, flthm, fd, context);
        UserFilterViewAction.printNumber(sc, FieldMap.TASK_NUMBER, flthm, fd, use, sort);
        UserFilterViewAction.printString(sc, FieldMap.FULLPATH, flthm, fd, use, sort);
        UserFilterViewAction.printString(sc, FieldMap.TASK_NAME, flthm, fd, use, sort);
	    UserFilterViewAction.printString(sc, FieldMap.TASK_PARENT, flthm, fd, use, sort);
        UserFilterViewAction.printString(sc, FieldMap.TASK_SHORTNAME, flthm, fd, use, sort);

        List<String> values = new ArrayList<String>();
        List<String> propertyValuesCollection;
        propertyValuesCollection = flthm.toList(FieldMap.TASK_CATEGORY.getFilterKey());
        if (propertyValuesCollection != null)
            for (String categoryId : propertyValuesCollection) {
                try {
                Category category = KernelManager.getFind().findCategory(categoryId);
                if (category != null) {
                    String name = category.getName();
                    values.add(name);
                }
                } catch (CantFindObjectException e) { /*Empty*/}
            }
        UserFilterViewAction.printList(sc, FieldMap.TASK_CATEGORY, flthm, values, fd, use, sort);

        values = new ArrayList<String>();
        propertyValuesCollection = flthm.toList(FieldMap.TASK_STATUS.getFilterKey());
        if (propertyValuesCollection != null)
            for (Object aPropertyValuesCollection5 : propertyValuesCollection) {
                try {
                    Status statusById = KernelManager.getFind().findStatus(aPropertyValuesCollection5.toString());
                    values.add(statusById.getWorkflow().getName() + " / " + statusById.getName());
                } catch (CantFindObjectException ex) {
                    // object not found
                }
            }

        Collections.sort(values);
        UserFilterViewAction.printList(sc, FieldMap.TASK_STATUS, flthm, values, fd, use, sort);

        values = new ArrayList<String>();
        propertyValuesCollection = flthm.toList(FieldMap.TASK_RESOLUTION.getFilterKey());
        if (propertyValuesCollection != null)
            for (Object aPropertyValuesCollection4 : propertyValuesCollection) {
                String resolution = aPropertyValuesCollection4.toString();
                if (resolution.equals("0")) continue;
                if (resolution.equals("null"))
                    values.add("--" + I18n.getString(sc.getLocale(), "NONE") + "--");
                else {
                    try {
                        Resolution resolutionById = KernelManager.getFind().findResolution(resolution);
                        values.add(resolutionById.getMstatus().getWorkflow().getName() + " / " + resolutionById.getMstatus().getName() + " / " + resolutionById.getName());
                    } catch (CantFindObjectException ex) {
                        // object not found
                    }
                }
            }

        Collections.sort(values);
        UserFilterViewAction.printList(sc, FieldMap.TASK_RESOLUTION, flthm, values, fd, use, sort);

        values = new ArrayList<String>();
        propertyValuesCollection = flthm.toList(FieldMap.TASK_PRIORITY.getFilterKey());
        if (propertyValuesCollection != null)
            for (Object aPropertyValuesCollection3 : propertyValuesCollection) {
                try {
                    // only for existing values
                    Priority priorityById = KernelManager.getFind().findPriority(aPropertyValuesCollection3.toString());
                    values.add(priorityById.getWorkflow().getName() + " / " + priorityById.getName());
                } catch (CantFindObjectException ex) {
                    // object not found
                }
            }
        Collections.sort(values);
        UserFilterViewAction.printList(sc, FieldMap.TASK_PRIORITY, flthm, values, fd, use, sort);

        propertyValuesCollection = flthm.toList(FieldMap.SUSER_NAME.getFilterKey());

        values = getSubmitterList(propertyValuesCollection, sc);
        UserFilterViewAction.printList(sc, FieldMap.SUSER_NAME, flthm, values, fd, use, sort);

        values = new ArrayList<String>();
        propertyValuesCollection = flthm.toList(FieldMap.SUSER_STATUS.getFilterKey());
        if (propertyValuesCollection != null) {
            for (String pvString : propertyValuesCollection) {
                try {
                    if (!pvString.equals("0"))
                        values.add(KernelManager.getFind().findPrstatus(pvString).getName());
                } catch (CantFindObjectException ex) {
                    // object not found
                }
            }
        }
        UserFilterViewAction.printList(sc, FieldMap.SUSER_STATUS, flthm, values, fd, use, sort);


        propertyValuesCollection = flthm.toList(FieldMap.HUSER_NAME.getFilterKey());
        values = TaskFilter.getHandlerValues(propertyValuesCollection, sc);
        UserFilterViewAction.printList(sc, FieldMap.HUSER_NAME, flthm, values, fd, use, sort);

        values = new ArrayList<String>();
        propertyValuesCollection = flthm.toList(FieldMap.HUSER_STATUS.getFilterKey());
        if (propertyValuesCollection != null)
            for (Object aPropertyValuesCollection2 : propertyValuesCollection) {
                String string = aPropertyValuesCollection2.toString();
                try {
                    if (!string.equals("0"))
                        values.add(KernelManager.getFind().findPrstatus(string).getName());
                } catch (CantFindObjectException ex) {
                    // object not found
                }
            }

        UserFilterViewAction.printList(sc, FieldMap.HUSER_STATUS, flthm, values, fd, use, sort);

        UserFilterViewAction.printDate(sc, FieldMap.TASK_SUBMITDATE, flthm, fd, use, sort);
        UserFilterViewAction.printDate(sc, FieldMap.TASK_UPDATEDATE, flthm, fd, use, sort);
        UserFilterViewAction.printDate(sc, FieldMap.TASK_CLOSEDATE, flthm, fd, use, sort);
        UserFilterViewAction.printDate(sc, FieldMap.TASK_DEADLINE, flthm, fd, use, sort);

        UserFilterViewAction.printNumber(sc, FieldMap.TASK_BUDGET, flthm, fd, use, sort);
        UserFilterViewAction.printNumber(sc, FieldMap.TASK_ABUDGET, flthm, fd, use, sort);
        UserFilterViewAction.printNumber(sc, FieldMap.TASK_CHILDCOUNT, flthm, fd, use, sort);
        UserFilterViewAction.printNumber(sc, FieldMap.TASK_MESSAGECOUNT, flthm, fd, use, sort);


        List<SecuredUDFBean> list = new ArrayList<SecuredUDFBean>(new SecuredTaskBean(taskId, sc).getFilterUDFs());
        Collections.sort(list, new SortUdf(FieldSort.ORDER));
        for (SecuredUDFBean udf : list) {
            int type = udf.getType();
            if (type == UdfValue.STRING || type == UdfValue.MEMO || type == UdfValue.URL) { // name or memo
                UserFilterViewAction.printString(sc, FieldMap.createUDF(udf.getCaptionEx(), FValue.UDF + udf.getId(), FValue.UDF_SORT + udf.getId()), flthm, fd, use, sort);
            }
            if (type == UdfValue.INTEGER || type == UdfValue.FLOAT) { // name or memo
                UserFilterViewAction.printNumber(sc, FieldMap.createUDF(udf.getCaptionEx(), FValue.UDF + udf.getId(), FValue.UDF_SORT + udf.getId()), flthm, fd, use, sort);
            }
            if (type == UdfValue.DATE) { // Date
                UserFilterViewAction.printDate(sc, FieldMap.createUDF(udf.getCaptionEx(), FValue.UDF + udf.getId(), FValue.UDF_SORT + udf.getId()), flthm, fd, use, sort);
            }
            if (type == UdfValue.LIST || type == UdfValue.MULTILIST || type == UdfValue.TASK || type == UdfValue.USER) { // List

                values = new ArrayList<String>();
                propertyValuesCollection = flthm.toList(FValue.UDF + udf.getId());
                if (type == UdfValue.LIST || type == UdfValue.MULTILIST) {
                    if (propertyValuesCollection != null)
                        for (String aPropertyValuesCollection : propertyValuesCollection) {
                            try {
                                values.add(AdapterManager.getInstance().getSecuredFindAdapterManager().findUdflistById(sc, aPropertyValuesCollection).getVal());
                            } catch (CantFindObjectException ex) {
                                // object not found
                            }
                        }
                    UserFilterViewAction.printList(sc, FieldMap.createUDF(udf.getCaptionEx(), FValue.UDF + udf.getId(), FValue.UDF_SORT + udf.getId()), flthm, values, fd, use, sort);
                }
                if (type == UdfValue.TASK)
                    if (propertyValuesCollection != null) {
                        for (String tid : propertyValuesCollection) {
                            if (TaskRelatedManager.getInstance().isTaskExists(tid))
                                values.add(new SecuredTaskBean(tid, sc).getName());
                        }
                        UserFilterViewAction.printList(sc, FieldMap.createUDF(udf.getCaptionEx(), FValue.UDF + udf.getId(), FValue.UDF_SORT + udf.getId()), flthm, values, fd, use, sort);
                    }
                if (type == UdfValue.USER)

                    if (propertyValuesCollection != null) {
                        values = getSubmitterList(propertyValuesCollection, sc);
                        UserFilterViewAction.printList(sc, FieldMap.createUDF(udf.getCaptionEx(), FValue.UDF + udf.getId(), FValue.UDF_SORT + udf.getId()), flthm, values, fd, use, sort);
                    }
            }
        }
        UserFilterViewAction.printString(sc, FieldMap.TASK_DESCRIPTION, flthm, fd, use, sort);
        UserFilterViewAction.printString(sc, FieldMap.SEARCH, flthm, fd, use, sort);

        ArrayList<Pair> messages = new ArrayList<Pair>();
        ArrayList<String> usemsg = new ArrayList<String>();
        String msgs = flthm.getAsString(FieldMap.MESSAGEVIEW.getFilterKey());
        String mess = "";
        if (msgs != null) {
            if (msgs.equals("1"))
                mess = I18n.getString(sc.getLocale(), "LAST_MESSAGE");
            if (msgs.equals("3"))
                mess = I18n.getString(sc.getLocale(), "LAST_3_MESSAGES");
            if (msgs.equals("5"))
                mess = I18n.getString(sc.getLocale(), "LAST_5_MESSAGES");
            if (msgs.equals("10"))
                mess = I18n.getString(sc.getLocale(), "LAST_10_MESSAGES");
            if (msgs.equals("-1"))
                mess = I18n.getString(sc.getLocale(), "ALL_HISTORY");
        }
        if (mess.length() > 0)
            messages.add(new Pair(I18n.getString(sc, FieldMap.MESSAGEVIEW.getAltKey()), mess));

        propertyValuesCollection = flthm.toList(FieldMap.MSG_SUSER_NAME.getFilterKey());
        values = getSubmitterList(propertyValuesCollection, sc);
        UserFilterViewAction.printList(sc, FieldMap.MSG_SUSER_NAME, flthm, values, messages, usemsg, sort);


        UserFilterViewAction.printDate(sc, FieldMap.MSG_SUBMITDATE, flthm, messages, usemsg, sort);
        UserFilterViewAction.printDate(sc, FieldMap.LAST_MSG_SUBMITDATE, flthm, messages, usemsg, sort);

        values = new ArrayList<String>();
        propertyValuesCollection = flthm.toList(FieldMap.MSG_MSTATUS.getFilterKey());
        if (propertyValuesCollection != null)
            for (Object aPropertyValuesCollection1 : propertyValuesCollection) {
                String string = aPropertyValuesCollection1.toString();
                if (string.equals("0")) continue;
                try {
                    SecuredMstatusBean mstatusById = AdapterManager.getInstance().getSecuredFindAdapterManager().findMstatusById(sc, string);
                    values.add(mstatusById.getWorkflow().getName() + " / " + mstatusById.getName());
                } catch (CantFindObjectException ex) {
                    // object not found
                }
            }
        Collections.sort(values);
        UserFilterViewAction.printList(sc, FieldMap.MSG_MSTATUS, flthm, values, messages, usemsg, sort);


        propertyValuesCollection = flthm.toList(FieldMap.MSG_HUSER_NAME.getFilterKey());
        values = TaskFilter.getHandlerValues(propertyValuesCollection, sc);
        UserFilterViewAction.printList(sc, FieldMap.MSG_HUSER_NAME, flthm, values, messages, usemsg, sort);


        values = new ArrayList<String>();
        propertyValuesCollection = flthm.toList(FieldMap.MSG_RESOLUTION.getFilterKey());
        if (propertyValuesCollection != null)
            for (String pvString : propertyValuesCollection) {
                if (pvString.equals("0")) continue;
                if (pvString.equals("null"))
                    values.add("--" + I18n.getString(sc.getLocale(), "NONE") + "--");
                else {
                    try {
                        Resolution resolutionById = KernelManager.getFind().findResolution(pvString);
                        values.add(resolutionById.getMstatus().getWorkflow().getName() + " / " + resolutionById.getMstatus().getName() + " / " + resolutionById.getName());
                    } catch (CantFindObjectException ex) {
                        // object not found
                    }
                }
            }
        Collections.sort(values);
        UserFilterViewAction.printList(sc, FieldMap.MSG_RESOLUTION, flthm, values, messages, usemsg, sort);

        UserFilterViewAction.printNumber(sc, FieldMap.MSG_ABUDGET, flthm, messages, usemsg, sort);
        UserFilterViewAction.printString(sc, FieldMap.TEXT_MSG, flthm, messages, usemsg, sort);

        List<String> sortList = flthm.getSortOrder();
        ArrayList<String> sortion = new ArrayList<String>();

        if (sortList != null) for (String str : sortList) {

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
        ret.put(VIEW_FILTER, fd);
        ret.put(VIEW_DISPLAY, use);
        ret.put(VIEW_SORT, sortion);
        ret.put(VIEW_MESSAGE, messages);
        return ret;
    }

    private static List<String> getSubmitterList(List<String> propertyValuesCollection, SessionContext sc) throws GranException {
        List<String> values = new ArrayList<String>();
        if (propertyValuesCollection != null)
            for (String pvString : propertyValuesCollection) {
                if (pvString.equals("0")) continue;
                if (pvString.equals("CurrentUserID"))
                    values.add("--" + I18n.getString(sc.getLocale(), "I_AM") + "--");
                else if (pvString.equals("IandSubUsers"))
                    values.add("--" + I18n.getString(sc.getLocale(), "ME_AND_SUBORDINATED") + "--");
                else if (pvString.equals("IandManager"))
                    values.add("--" + I18n.getString(sc.getLocale(), "ME_AND_MANAGER") + "--");
                else if (pvString.equals("IandManagers"))
                    values.add("--" + I18n.getString(sc.getLocale(), "ME_AND_MANAGERS") + "--");
                else if (UserRelatedManager.getInstance().isUserExists(pvString))
                    values.add(new SecuredUserBean(pvString, sc).getName());
            }
        return values;
    }
}