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
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.cache.UserRelatedInfo;
import com.trackstudio.kernel.lock.LockManager;

public class TaskFilterParametersAction extends TaskFilterParametersAbstractAction {
    private final LockManager lockManager = LockManager.getInstance();

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            PreFilterForm bf = (PreFilterForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            response.addCookie(createCookie(sc.getCurrentSpace(), "TaskFilterParametersAction"));
            this.id = GeneralAction.getInstance().taskHeader(bf, sc, request, true);
            this.filterId = AdapterManager.getInstance().getSecuredFilterAdapterManager().getCurrentTaskFilterId(sc, id);
            sc.setRequestAttribute(request, "filterId", filterId);
            sc.setRequestAttribute(request, "action", "/TaskFilterParametersAction.do");
            sc.setRequestAttribute(request, "undoUrl", request.getContextPath() + "/TaskFilterParametersAction.do");
            boolean canCreate = sc.canAction(Action.manageTaskPrivateFilters, id) && sc.allowedByACL(id);
            sc.setRequestAttribute(request, "saveAsFilter", canCreate);
            this.action = "taskFilterParametersPage";
            this.filterParameter = "taskfilter";
            return super.page(mapping, form, request, response);
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
            this.id = GeneralAction.getInstance().taskHeader(bf, sc, request, false);
            if (request.getParameter("filterId") != null) {
                String tempFilterId = request.getParameter("filterId");
                AdapterManager.getInstance().getSecuredFilterAdapterManager().setCurrentFilter(sc, id, tempFilterId);
                bf.setFilter(tempFilterId);
                bf.setField("default");
                bf.setId(request.getParameter("id"));
                sc.removeAttribute("taskfilter");
                sc.removeAttribute("statictask");
                sc.removeAttribute("next");
                sc.removeAttribute("statictasklist");
            } else {
                AdapterManager.getInstance().getSecuredFilterAdapterManager().setCurrentFilter(sc, bf.getId(), bf.getFilter());
            }
            this.filterParameter = "taskfilter";
            this.forward = "subtaskPage";
            this.action = "taskFilterParametersPage";
            return super.changeField(mapping, bf, request, response);
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward changeTaskFilter(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            PreFilterForm bf = (PreFilterForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            this.id = GeneralAction.getInstance().taskHeader(bf, sc, request, false);
            if (request.getParameter("filterId") != null) {
                String tempFilterId = request.getParameter("filterId");
                if (!sc.getUser().getLogin().equals(UserRelatedInfo.ANONYMOUS_USER)) {
                    String filterId = AdapterManager.getInstance().getSecuredFilterAdapterManager().setCurrentFilter(sc, id, tempFilterId);
                    sc.setRequestAttribute(request, "filterId", filterId);
                }
                bf.setFilter(tempFilterId);
                bf.setField("default");
                bf.setId(request.getParameter("id"));
                sc.removeAttribute("taskfilter");
                sc.removeAttribute("statictask");
                sc.removeAttribute("next");
                sc.removeAttribute("statictasklist");
                sc.removeAttribute("permlinkfilter");
            } else {
                if (!sc.getUser().getLogin().equals(UserRelatedInfo.ANONYMOUS_USER)) {
                    AdapterManager.getInstance().getSecuredFilterAdapterManager().setCurrentFilter(sc, bf.getId(), bf.getFilter());
                }
            }

            this.filterParameter = "taskfilter";
            this.forward = "subtaskPage";
            this.action = "taskFilterParametersPage";
            return super.changeTaskFilter(mapping, bf, request, response);
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }
}
