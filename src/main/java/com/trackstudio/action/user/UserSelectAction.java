package com.trackstudio.action.user;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import javax.servlet.http.Cookie;
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
import com.trackstudio.app.Preferences;
import com.trackstudio.app.Slider;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.filter.FValue;
import com.trackstudio.app.filter.FilterSettings;
import com.trackstudio.app.filter.UserFValue;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.constants.UdfConstants;
import com.trackstudio.exception.GranException;
import com.trackstudio.form.UserListForm;
import com.trackstudio.secured.SecuredFilterBean;
import com.trackstudio.secured.SecuredUDFBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.startup.I18n;

public class UserSelectAction extends TSDispatchAction {
    private static Log log = LogFactory.getLog(UserSelectAction.class);

    private Cookie getCookie(HttpServletRequest request, String name) {
        if (request.getCookies() != null) {
            for (ListIterator i1 = Arrays.asList(request.getCookies()).listIterator(); i1.hasNext();) {
                Cookie c = (Cookie) i1.next();
                if (c.getName().equals(name))
                    return c;
            }
        }
        return null;
    }

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            UserListForm sf = (UserListForm) form;
            SessionContext sc = GeneralAction.getInstance().imports(request, response);


            String id = null;


            if (sf.getUdfvalue() != null && sf.getUdfvalue().length() != 0) {
                SecuredUDFBean udfls = AdapterManager.getInstance().getSecuredFindAdapterManager().findUDFById(sc, sf.getUdfvalue());
                id = udfls.getInitial() != null ? udfls.getInitial() : id;
                sc.setRequestAttribute(request, "id", id);
            }
            sc.setCurrentSpace("UserSelectAction", request);

            String contextPath = request.getContextPath();

            id = GeneralAction.getInstance().userHeader(sf, sc, request);

            ArrayList<SecuredFilterBean> filterSet = new ArrayList<SecuredFilterBean>();
            ArrayList<SecuredFilterBean> additionalFiltersSet = new ArrayList<SecuredFilterBean>();
            for (SecuredFilterBean prs1 : AdapterManager.getInstance().getSecuredFilterAdapterManager().getUserFilterList(sc, id)) {
                if (Preferences.showInToolbar(prs1.getPreferences()))
                    filterSet.add(prs1);
                else
                    additionalFiltersSet.add(prs1);
            }
            sc.setRequestAttribute(request, "filters", filterSet);
            sc.setRequestAttribute(request, "additionalFilters", additionalFiltersSet);

            String filterId = sf.getFilter();
            if (filterId == null)
                filterId = AdapterManager.getInstance().getSecuredFilterAdapterManager().getCurrentUserFilterId(sc, id);

            FilterSettings filterSettings = null;

            Object filterObject = sc.getAttribute("userfilter");
            if (filterObject != null) {
                filterSettings = (FilterSettings) filterObject;
                if (!filterSettings.getFilterId().equals(filterId) || !filterSettings.getObjectId().equals(id)) {

                    filterSettings = null;
                }
            }

            UserFValue val = AdapterManager.getInstance().getSecuredFilterAdapterManager().getUserFValue(sc, filterId).getFValue();
            FilterSettings originFilterSettings;
            originFilterSettings = new FilterSettings(val, id, filterId);
            if (filterSettings == null) {
                filterSettings = originFilterSettings;
                sc.setAttribute("userselectfilter", originFilterSettings);
            }
            SecuredFilterBean filter = AdapterManager.getInstance().getSecuredFindAdapterManager().findFilterById(sc, filterId);
            sc.setRequestAttribute(request, "filter", filter);

            sf.setFilter(filterId);
            String udffield = null;
            Object o_udffield = request.getParameter("udffield");
            if (o_udffield != null) udffield = o_udffield.toString();
            if (udffield != null && udffield.length() > 0) {
                String udfId = udffield.substring(udffield.lastIndexOf("." + FValue.UDF) + 4);
                if (udfId != null && udfId.length() > 0) {
                    udfId = udfId.substring(udfId.lastIndexOf("_") + 1);
                    SecuredUDFBean udfls = AdapterManager.getInstance().getSecuredFindAdapterManager().findUDFById(sc, udfId);
                    if (udfls != null)
                        sc.setRequestAttribute(request, "currentUDF", udfls.getCaptionEx());
                    response.addCookie(createCookie("udffield", udffield));
                }
            }

            Object opack = request.getParameter("pack");
            String pack = null;
            String userIds = "";
            if (opack != null) {
                pack = URLDecoder.decode(opack.toString());

                for (String p : pack.split(";")) {
                    SecuredUserBean i = AdapterManager.getInstance().getSecuredUserAdapterManager().findByLogin(sc, p.trim());
                    if (i != null) userIds += i.getId() + ":";
                }
                response.addCookie(createCookie("_selectedId", userIds));

            } else {
                Cookie[] cook = request.getCookies();
                if (userIds.length() == 0 && cook != null) {

                    for (Cookie c : cook) {
                        log.debug(c.getName() + c.getValue());
                        if (c.getName().equals("_selectedId") && c.getValue() != null && c.getValue().length() > 0) {
                            userIds = c.getValue();
                        }
                    }
                }
            }
            ArrayList<SecuredUserBean> selected = new ArrayList<SecuredUserBean>();
            for (String s : userIds.split(UdfConstants.SPLIT_SEPARATOR)) {
                SecuredUserBean t = AdapterManager.getInstance().getSecuredFindAdapterManager().findUserById(sc, s);
                if (t != null) selected.add(t);
                sc.setRequestAttribute(request, "selectedIds", selected);
            }

            if (sf.getSliderPage() != null && sf.getSliderPage().length() != 0) {
                filterSettings.setCurrentPage(Integer.parseInt(sf.getSliderPage()));
            }

            if (sf.getSliderOrder() != null && sf.getSliderOrder().length() != 0) {
                ArrayList<String> sliderOrderList = new ArrayList<String>();
                sliderOrderList.add(sf.getSliderOrder());
                filterSettings.setSortedBy(sliderOrderList);
                originFilterSettings.setSortedBy(sliderOrderList);
            }


            Integer totalChildrenCount;
            Slider<SecuredUserBean> userSlider = AdapterManager.getInstance().getSecuredUserAdapterManager().getUserList(sc, id, (UserFValue) filterSettings.getSettings(), filterSettings.getCurrentPage(), false, filterSettings.getSortedBy());
            userSlider.setAll(sf.isAll(), sc.getLocale());
            totalChildrenCount = userSlider.getTotalChildrenCount();

            sc.setRequestAttribute(request, "slider", userSlider.drawSlider(contextPath + "/UserSelectAction.do?method=page&amp;id=" + id, "div", "slider"));
            sc.setRequestAttribute(request, "sliderSize", userSlider.getColSize() != null ? userSlider.getColSize() : I18n.getString(sc.getLocale(), "HUNDREDS"));

            UserListAction.SortedFactory fact = new UserListAction.SortedFactory(originFilterSettings);

            boolean useCookies = true;
            sc.setRequestAttribute(request, "useCookies", useCookies);
            ArrayList<String> defaultSortString = new ArrayList<String>();
            defaultSortString.add("user_login");

            List<SecuredUserBean> userLines = userSlider.getCol();

            sc.setRequestAttribute(request, "userLines", userLines);
            sc.setAttribute("userfilter", filterSettings);
            sc.setRequestAttribute(request, "isList", "true");

            String currentURL = contextPath + "/UserSelectAction.do";

            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_USER_LIST);
            sc.setRequestAttribute(request, "currentURL", currentURL);
            sc.setRequestAttribute(request, "totalChildrenCount", totalChildrenCount);

            return mapping.findForward("userSelectJSP");
        } catch (GranException ge) {
            request.setAttribute("javax.servlet.jsp.jspException", ge);
            return mapping.findForward("error");
        } finally {
            if (w) lockManager.releaseConnection();
        }
    }
}