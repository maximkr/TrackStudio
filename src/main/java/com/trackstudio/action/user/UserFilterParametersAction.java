package com.trackstudio.action.user;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.trackstudio.action.GeneralAction;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.form.PreFilterForm;

public class UserFilterParametersAction extends UserFilterParametersAbstractAction {

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            this.filterParameter = "userfilter";
            PreFilterForm bf = (PreFilterForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            response.addCookie(createCookie(sc.getCurrentSpace(), "UserFilterParametersAction"));
            this.id = GeneralAction.getInstance().userHeader(bf, sc, request);
            this.filterId = AdapterManager.getInstance().getSecuredFilterAdapterManager().getCurrentUserFilterId(sc, id);
            sc.setRequestAttribute(request, "action", "/UserFilterParametersAction.do");
            this.action = "userFilterParametersPage";
            this.filterParameter = "userfilter";
            return super.page(mapping, form, request, response);    //To change body of overridden methods use File | Settings | File Templates.
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward changeFilter(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            PreFilterForm bf = (PreFilterForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            this.id = GeneralAction.getInstance().userHeader(bf, sc, request);
            if (request.getParameter("filterId") != null) {
                String tempFilterId = request.getParameter("filterId");
                AdapterManager.getInstance().getSecuredFilterAdapterManager().setCurrentUserFilter(sc, id, tempFilterId);
                bf.setFilter(tempFilterId);
                bf.setField("default");
                bf.setId(request.getParameter("id"));
                sc.removeAttribute("userfilter");
            } else
                AdapterManager.getInstance().getSecuredFilterAdapterManager().setCurrentUserFilter(sc, bf.getId(), bf.getFilter());

            this.filterParameter = "userfilter";
            this.forward = "userListPage";
            this.action = "userFilterParametersPage";
            return super.changeFilter(mapping, form, request, response);
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward changeField(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            PreFilterForm bf = (PreFilterForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            this.id = GeneralAction.getInstance().userHeader(bf, sc, request);
            this.filterParameter = "userfilter";
            if (request.getParameter("filterId") != null) {
                String tempFilterId = request.getParameter("filterId");
                AdapterManager.getInstance().getSecuredFilterAdapterManager().setCurrentUserFilter(sc, id, tempFilterId);
                bf.setFilter(tempFilterId);
                bf.setField("default");
                bf.setId(request.getParameter("id"));
                sc.removeAttribute(this.filterParameter);
            } else {
                AdapterManager.getInstance().getSecuredFilterAdapterManager().setCurrentUserFilter(sc, bf.getId(), bf.getFilter());
            }

            this.forward = "userListPage";
            this.action = "userFilterParametersPage";
            return super.changeField(mapping, bf, request, response);
        } finally {
            if (w) lockManager.releaseConnection();
        }
    }
}
