package com.trackstudio.action.user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
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
import com.trackstudio.action.task.items.FieldListItem;
import com.trackstudio.app.PageNaming;
import com.trackstudio.app.Preferences;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.filter.FValue;
import com.trackstudio.app.filter.FilterSettings;
import com.trackstudio.app.filter.UserFValue;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.common.FieldMap;
import com.trackstudio.exception.GranException;
import com.trackstudio.form.FilterForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.cache.UserAction;
import com.trackstudio.secured.SecuredFilterBean;
import com.trackstudio.secured.SecuredUDFValueBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.secured.SecuredUserFValueBean;
import com.trackstudio.view.UserViewHTMLLinked;

public class UserFilterEditAction extends TSDispatchAction {
    private static Log log = LogFactory.getLog(UserFilterEditAction.class);

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            FilterForm bf = (FilterForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String userId = GeneralAction.getInstance().userHeader(bf, sc, request);
            String filterId = bf.getFilterId() == null ? request.getParameter("filterId") : bf.getFilterId();
            String field = "";
            ArrayList<FieldListItem> fields = new ArrayList<FieldListItem>();
            ArrayList<FieldListItem> selectedFields = new ArrayList<FieldListItem>();
            List<String> sortorderList;
            SecuredUserBean currentUser = new SecuredUserBean(userId, sc);
            SecuredFilterBean currentFilter = AdapterManager.getInstance().getSecuredFindAdapterManager().findFilterById(sc, filterId);
            if (filterId == null || filterId.length() == 0) {
                UserFValue flthm = new UserFValue();
                sortorderList = flthm.getSortOrder();
                for (FieldMap map : FieldMap.userFields) {
                    field = flthm.setFields(sc, map, fields, sortorderList, selectedFields, field);
                }

                for (SecuredUDFValueBean udfvf : new TreeSet<SecuredUDFValueBean>(currentUser.getFilterUDFValues())) {
                    FieldMap map = FieldMap.createUDF(udfvf.getCaption(), FValue.UDF + udfvf.getId(), FValue.UDF_SORT + udfvf.getId());
                    field = flthm.setFields(sc, map, fields, sortorderList, selectedFields, field);
                }
                bf.setMethod("create");
            } else {
                if (!sc.canAction(Action.manageUserPrivateFilters, currentFilter.getOwnerId()) && !currentFilter.canManage())
                    return null;
                bf.setName(currentFilter.getName());
                bf.setDescription(currentFilter.getDescription());
                bf.setShared(!currentFilter.isPrivate());
                UserFValue flthm = AdapterManager.getInstance().getSecuredFilterAdapterManager().getUserFValue(sc, filterId).getFValue();
                bf.setOnpage(flthm.getAsString(UserFValue.ONPAGE));
                bf.setSubtask(flthm.getOriginValues(FValue.DEEPSEARCH) != null);
                bf.setShowInToolbar(Preferences.showInToolbar(currentFilter.getPreferences()));
                bf.setUser(currentFilter.getOwnerId());
                sc.setRequestAttribute(request, "owner", new UserViewHTMLLinked(currentFilter.getOwner(), request.getContextPath()).getPath());
                sc.setRequestAttribute(request, "currentFilter", currentFilter);
                sc.setRequestAttribute(request, "filterId", filterId);
                bf.setMethod("edit");
                sortorderList = flthm.getSortOrder();

                for (FieldMap map : FieldMap.userFields) {
                    field = flthm.setFields(sc, map, fields, sortorderList, selectedFields, field);
                }

                for (SecuredUDFValueBean udfvf : new TreeSet<SecuredUDFValueBean>(currentUser.getFilterUDFValues())) {
                    FieldMap map = FieldMap.createUDF(udfvf.getCaption(), FValue.UDF + udfvf.getId(), FValue.UDF_SORT + udfvf.getId());
                    field = flthm.setFields(sc, map, fields, sortorderList, selectedFields, field);
                }
            }

            bf.setFields(field);

            int counter = sortorderList.size();

            if (counter > 0) {
                String cn = "";
                for (int i = 0; i < counter; i++) cn += ".";
                sc.setRequestAttribute(request, "counter", cn);
            }

            Collections.sort(fields);
            sc.setRequestAttribute(request, "fields", fields);
            Collections.sort(selectedFields);
            sc.setRequestAttribute(request, "selectedFields", selectedFields);
            sc.setRequestAttribute(request, "canCreatePublicFilter", sc.canAction(UserAction.manageUserPublicFilters, userId) && sc.allowedByUser(userId));
            if (filterId == null) return mapping.findForward("createUserFilterPage");
            else return mapping.findForward("editUserFilterJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            FilterForm ff = (FilterForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String userId = GeneralAction.getInstance().userHeader(ff, sc, request);

            if (!sc.canAction(Action.manageUserPrivateFilters, userId)) return null;

            String filterId = AdapterManager.getInstance().getSecuredFilterAdapterManager().createUserFilter(sc, ff.getUser(), ff.getName(), ff.getDescription(), !ff.isShared(), ff.isShowInToolbar() ? "T" : "");

            SecuredFilterBean currentFilter = AdapterManager.getInstance().getSecuredFindAdapterManager().findFilterById(sc, filterId);

            //считываем preferences у только что созданного фильтра, потому что могут быть
            //preferences, создаваемые по умолчанию
            String preferences = currentFilter.getPreferences();
            Preferences p = new Preferences(preferences);
            p.setShowInToolbar(ff.getShowInToolbar());
            AdapterManager.getInstance().getSecuredFilterAdapterManager().updateUserFilter(sc, filterId, ff.getName(), ff.getDescription(), !ff.isShared(), p.getPreferences());

            ff.setFilterId(filterId);
            setFilterParameters(sc, filterId, ff);
            ff.setMutable(false);
            return mapping.findForward("viewUserFilterPage");
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
            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_USER_FILTER_PROPERTIES);
            String filterId = ff.getFilterId();
            SecuredFilterBean currentFilter = AdapterManager.getInstance().getSecuredFindAdapterManager().findFilterById(sc, filterId);
            Preferences p = new Preferences(currentFilter.getPreferences());
            p.setShowInToolbar(ff.getShowInToolbar());
            AdapterManager.getInstance().getSecuredFilterAdapterManager().updateUserFilter(sc, ff.getFilterId(), ff.getName(), ff.getDescription(), !ff.isShared(), p.getPreferences());
            setFilterParameters(sc, filterId, ff);
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

    private void setFilterParameters(SessionContext sc, String filterId, FilterForm ff) throws GranException {
        boolean w = lockManager.acquireConnection();
        try {
            UserFValue flthm = AdapterManager.getInstance().getSecuredFilterAdapterManager().getUserFValue(sc, filterId).getFValue();
            flthm.set(UserFValue.ONPAGE, ff.getOnpage());
            flthm.set(FValue.DEEPSEARCH, ff.getSubtask() ? "1" : null);
            String fields = ff.getFields();
            StringTokenizer tk = new StringTokenizer(fields, FValue.DELIM);
            String[] sorted = new String[32];
            ArrayList<String> display = new ArrayList<String>();

            while (tk.hasMoreElements()) {
                String token = tk.nextToken();
                if (token.startsWith("+")) {
                    // asc
                    int pos = Integer.parseInt(token.substring(2, 3));
                    sorted[pos - 1] = "_" + token.substring(5);
                } else if (token.startsWith("-")) {
                    //desc
                    int pos = Integer.parseInt(token.substring(2, 3));
                    sorted[pos - 1] = token.substring(5);
                }
                display.add(FieldMap.getFilterKeyByFieldKey(token.substring(5)));
            }

            flthm.setList(FValue.DISPLAY, display);
            flthm.setSortOrder(sorted);
            AdapterManager.getInstance().getSecuredFilterAdapterManager().setFValue(sc, filterId, new SecuredUserFValueBean(flthm, sc));
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

}
