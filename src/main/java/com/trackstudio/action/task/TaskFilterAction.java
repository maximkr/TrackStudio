package com.trackstudio.action.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.form.FilterForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.secured.SecuredFilterBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.EggBasket;

public class TaskFilterAction extends TSDispatchAction {

    private static Log log = LogFactory.getLog(ReportAction.class);

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            FilterForm bf = (FilterForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = GeneralAction.getInstance().taskHeader(bf, sc, request, true);

            if (!sc.canAction(Action.viewFilters, id))
                return null;
            List<SecuredFilterBean> filterList = AdapterManager.getInstance().getSecuredFilterAdapterManager().getAllTaskFilterList(sc, id);

            ArrayList<SecuredFilterBean> filters = new ArrayList<SecuredFilterBean>();
            EggBasket<SecuredTaskBean, SecuredFilterBean> parentFilterSet = new EggBasket<SecuredTaskBean, SecuredFilterBean>();
            EggBasket<SecuredTaskBean, SecuredFilterBean> childrenFilterSet = new EggBasket<SecuredTaskBean, SecuredFilterBean>();
            ArrayList<EggBasket> seeAlso = new ArrayList<EggBasket>();
            ArrayList<SecuredTaskBean> parentTasks = AdapterManager.getInstance().getSecuredTaskAdapterManager().getTaskChain(sc, null, id);
            Boolean canManage = false;
            for (SecuredFilterBean fl : filterList) {
                if (!canManage && fl.canManage()) {
                    canManage = true;
                }
                SecuredTaskBean task = fl.getTask();
                if (sc.canAction(Action.viewFilters, task.getId())) {
                    if (task.getId().equals(id)) {
                        filters.add(fl);
                    } else if (parentTasks.contains(task)) {
                        parentFilterSet.putItem(task, fl);
                    } else {
                        childrenFilterSet.putItem(task, fl);
                    }
                }
            }

            sc.setRequestAttribute(request, "msgHowCreateObject", I18n.getString(sc.getLocale(), "FILTER_ADD"));
            sc.setRequestAttribute(request, "msgAddObject", I18n.getString(sc.getLocale(), "FILTER_ADD"));
            sc.setRequestAttribute(request, "createObjectAction", "/TaskFilterAction.do");

            String currentFilterId = AdapterManager.getInstance().getSecuredFilterAdapterManager().getCurrentTaskFilterId(sc, id);
            sc.setRequestAttribute(request, "currentFilterId", currentFilterId);

            boolean canView = sc.canAction(Action.viewFilters, id) && sc.taskOnSight(id);
            boolean canDelete = sc.canAction(Action.manageTaskPrivateFilters, id) && sc.allowedByACL(id);
            boolean canCreate = sc.canAction(Action.manageTaskPrivateFilters, id);

            boolean canCopy = sc.canAction(Action.manageTaskPrivateFilters, id) && sc.allowedByACL(id);
            sc.setRequestAttribute(request, "canCreatePublicFilter", canCreate && sc.allowedByACL(id));
            sc.setRequestAttribute(request, "canView", canView);
            sc.setRequestAttribute(request, "canCopy", canCopy);
            sc.setRequestAttribute(request, "canCreateObject", canCreate);
            sc.setRequestAttribute(request, "canDelete", canDelete);
            sc.setRequestAttribute(request, "canManage", canManage);
            Collections.sort(filters);
            sc.setRequestAttribute(request, "filterList", filters);

            if (!parentFilterSet.isEmpty())
                seeAlso.add(parentFilterSet);
            if (!childrenFilterSet.isEmpty())
                seeAlso.add(childrenFilterSet);

            sc.setRequestAttribute(request, "seeAlso", seeAlso);
            sc.setRequestAttribute(request, "parentTasks", parentTasks);
            selectTaskTab(sc, id, "tabFilters", request);
            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_TASK_FILTER_LIST);
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_TASK_FILTER_LIST));
            return mapping.findForward("taskFilterListJSP");
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
            String[] selected = bf.getSelect();
            if (selected != null)
                for (String aSelected : selected)
                    AdapterManager.getInstance().getSecuredFilterAdapterManager().cloneTaskFilter(sc, aSelected, bf.getId());
            return mapping.findForward("taskFilterListPage");
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
                for (String filter : filters) {
                    if (AdapterManager.getInstance().getSecuredFindAdapterManager().findFilterById(sc, filter).canManage() && !filter.equals("1"))
                        AdapterManager.getInstance().getSecuredFilterAdapterManager().deleteTaskFilter(sc, filter);
                }
            }
            ff.setMutable(false);
            return mapping.findForward("taskFilterListPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.trace("##########");
        SessionContext sc = (SessionContext) request.getAttribute("sc");
        sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_TASK_FILTER_PROPERTIES);
        return mapping.findForward("editTaskFilterPage");
    }
}
