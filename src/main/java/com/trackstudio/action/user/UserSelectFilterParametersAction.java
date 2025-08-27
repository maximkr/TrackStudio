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

public class UserSelectFilterParametersAction extends UserFilterParametersAbstractAction {
    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            PreFilterForm bf = (PreFilterForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            response.addCookie(createCookie(sc.getCurrentSpace(), "UserSelectFilterParametersAction"));
            this.id = GeneralAction.getInstance().userHeader(bf, sc, request);
            //this.filterId = bf.getFilter();

            filterId = AdapterManager.getInstance().getSecuredFilterAdapterManager().getCurrentUserFilterId(sc, id);
            this.filterParameter = "userselectfilter";
            sc.setRequestAttribute(request, "hidePopups", Boolean.TRUE);
            sc.setRequestAttribute(request, "action", "/UserSelectFilterParametersAction.do");
            return super.page(mapping, form, request, response);    //To change body of overridden methods use File | Settings | File Templates.
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward changeFilter(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            this.filterParameter = "userselectfilter";
            this.forward = "userSelectPage";
            PreFilterForm bf = (PreFilterForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            AdapterManager.getInstance().getSecuredFilterAdapterManager().setCurrentUserFilter(sc, bf.getId(), bf.getFilter());
            return super.changeFilter(mapping, form, request, response);    //To change body of overridden methods use File | Settings | File Templates.
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }
}
