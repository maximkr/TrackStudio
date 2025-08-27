package com.trackstudio.action.user;

import java.util.ArrayList;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.trackstudio.action.GeneralAction;
import com.trackstudio.action.TSDispatchAction;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.form.FilterForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.secured.SecuredFilterBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.EggBasket;

public class UserFilterAction extends TSDispatchAction {
    private static Log log = LogFactory.getLog(UserFilterAction.class);

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            FilterForm bf = (FilterForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().userHeader(bf, sc, request);
            if (!sc.canAction(Action.viewUserFilters, id))
                return null;
            ArrayList<SecuredFilterBean> filterList = AdapterManager.getInstance().getSecuredFilterAdapterManager().getAllUserFilterList(sc, id);

            ArrayList<SecuredFilterBean> filters = new ArrayList<SecuredFilterBean>();
            EggBasket<SecuredUserBean, SecuredFilterBean> parentFilterSet = new EggBasket<SecuredUserBean, SecuredFilterBean>();
            EggBasket<SecuredUserBean, SecuredFilterBean> childrenFilterSet = new EggBasket<SecuredUserBean, SecuredFilterBean>();
            ArrayList<EggBasket> seeAlso = new ArrayList<EggBasket>();
            ArrayList<SecuredUserBean> parentUsers = AdapterManager.getInstance().getSecuredUserAdapterManager().getUserChain(sc, null, id);

            for (SecuredFilterBean fl : filterList) {
                SecuredUserBean toUser = fl.getUser();
                if (toUser.canView() && sc.canAction(Action.viewUserFilters, toUser.getId())) {
                    if (toUser.getId().equals(id)) {
                        filters.add(fl);
                    } else if (parentUsers.contains(toUser)) {
                        parentFilterSet.putItem(toUser, fl);
                    } else {
                        childrenFilterSet.putItem(toUser, fl);
                    }
                }
            }

            boolean canView = sc.canAction(Action.viewUserFilters, id) && sc.userOnSight(id);
            boolean canDelete = sc.canAction(Action.manageUserPrivateFilters, id) && sc.allowedByUser(id);
            boolean canCreate = sc.canAction(Action.manageUserPrivateFilters, id) && sc.allowedByUser(id);
            boolean canCopy = sc.canAction(Action.manageUserPrivateFilters, id) && sc.allowedByUser(id);

            if (canCreate) {
                sc.setRequestAttribute(request, "msgHowCreateObject", I18n.getString(sc.getLocale(), "FILTER_ADD"));
                sc.setRequestAttribute(request, "msgAddObject", I18n.getString(sc.getLocale(), "FILTER_ADD"));
                sc.setRequestAttribute(request, "createObjectAction", "/UserFilterAction.do");
            }

            if (!parentFilterSet.isEmpty())
                seeAlso.add(parentFilterSet);
            if (!childrenFilterSet.isEmpty())
                seeAlso.add(childrenFilterSet);
            String currentFilterId = AdapterManager.getInstance().getSecuredFilterAdapterManager().getCurrentUserFilterId(sc, id);
            Collections.sort(filters);
            sc.setRequestAttribute(request, "currentFilterId", currentFilterId);
            sc.setRequestAttribute(request, "canView", canView);
            sc.setRequestAttribute(request, "canCopy", canCopy);
            sc.setRequestAttribute(request, "canCreateObject", canCreate);
            sc.setRequestAttribute(request, "canDelete", canDelete);
            sc.setRequestAttribute(request, "filterList", filters);
            sc.setRequestAttribute(request, "seeAlso", seeAlso);

            return mapping.findForward("listUserFilterJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward clone(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            FilterForm bf = (FilterForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().userHeader(bf, sc, request);
            String[] selected = bf.getSelect();
            if (selected != null)
                for (String aSelected : selected)
                    AdapterManager.getInstance().getSecuredFilterAdapterManager().cloneUserFilter(sc, id, aSelected);
            return mapping.findForward("userFilterListPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward delete(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            FilterForm ff = (FilterForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String[] filters = ff.getSelect();
            if (filters != null) {
                for (String filter : filters)
                    if (!filter.equals("0") && AdapterManager.getInstance().getSecuredFindAdapterManager().findFilterById(sc, filter).canManage())
                        AdapterManager.getInstance().getSecuredFilterAdapterManager().deleteUserFilter(sc, filter);
            }

            ff.setMutable(false);
            return mapping.findForward("userFilterListPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            FilterForm bf = (FilterForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().userHeader(bf, sc, request);

            if (!sc.canAction(Action.manageUserPrivateFilters, id))
                return null;

            boolean canCreatePrivateFilter = sc.canAction(Action.manageUserPrivateFilters, id) && sc.getUserId(id).equals(sc.getUserId());
            bf.setUser(sc.getUserId(id));
            if (canCreatePrivateFilter)
                bf.setShared(false);
            bf.setMethod("create");
            sc.setRequestAttribute(request, "createNewFilter", Boolean.TRUE);
            return mapping.findForward("editUserFilterJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }
    }
}
