package com.trackstudio.action.task;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.trackstudio.action.GeneralAction;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.form.PreFilterForm;

public class TaskSelectFilterParametersAction extends TaskFilterParametersAbstractAction {
    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            PreFilterForm bf = (PreFilterForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            response.addCookie(createCookie(sc.getCurrentSpace(), "TaskSelectFilterParametersAction"));
            this.id = GeneralAction.getInstance().taskHeader(bf, sc, request, true);
            //this.filterId = bf.getFilter();

            filterId = AdapterManager.getInstance().getSecuredFilterAdapterManager().getCurrentTaskFilterId(sc, id);
            sc.setRequestAttribute(request, "action", "/TaskSelectFilterParametersAction.do");
            sc.setRequestAttribute(request, "hidePopups", Boolean.TRUE);
            sc.setRequestAttribute(request, "saveAsFilter", false);
            this.filterParameter = "taskselectfilter";
            return super.page(mapping, form, request, response);    //To change body of overridden methods use File | Settings | File Templates.
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward changeTaskFilter(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            PreFilterForm bf = (PreFilterForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            this.filterParameter = "taskselectfilter";
            this.forward = "taskSelectPage";
            AdapterManager.getInstance().getSecuredFilterAdapterManager().setCurrentFilter(sc, bf.getId(), bf.getFilter());
            return super.changeTaskFilter(mapping, form, request, response);
        } finally {
            if (w) lockManager.releaseConnection();
        }
    }
}

