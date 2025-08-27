package com.trackstudio.action.task;

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
import com.trackstudio.app.UdfValue;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.filter.FValue;
import com.trackstudio.app.filter.FilterSettings;
import com.trackstudio.app.filter.TaskFValue;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.common.FieldMap;
import com.trackstudio.exception.GranException;
import com.trackstudio.form.FilterForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.cache.TaskAction;
import com.trackstudio.secured.SecuredFilterBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredTaskFValueBean;
import com.trackstudio.secured.SecuredUDFValueBean;
import com.trackstudio.view.TaskViewHTMLShort;


public class TaskFilterEditAction extends TSDispatchAction {

    private static Log log = LogFactory.getLog(ReportEditAction.class);

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws GranException {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            FilterForm bf = (FilterForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");

            String id = GeneralAction.getInstance().taskHeader(bf, sc, request, true);
            SecuredTaskBean task = new SecuredTaskBean(id, sc);
            String filterId = bf.getFilterId() == null ? request.getParameter("filterId") : bf.getFilterId();
            boolean canCreatePublicFilter = sc.canAction(TaskAction.manageTaskPublicFilters, id) && sc.allowedByACL(id);
            ArrayList<FieldListItem> fields = new ArrayList<FieldListItem>();
            ArrayList<FieldListItem> selectedFields = new ArrayList<FieldListItem>();
            List<String> sortorderList;
            SecuredTaskBean currentTask = task;
            String field = "";
            if (filterId == null || filterId.length() == 0) {//новый
                sc.setRequestAttribute(request, "types", types);
                bf.setUser(sc.getUserId());
                bf.setTask(id);
                bf.setMethod("create");
                bf.setShared(false);
                bf.setMsgnum("3");
                bf.setShowmsg(false);
                bf.setShowInToolbar(false);
                sc.setRequestAttribute(request, "disableMsgList", true);
                sc.setRequestAttribute(request, "owner", sc.getUser());
                sc.setRequestAttribute(request, "connected", (new TaskViewHTMLShort(task, request.getContextPath())).getView(task).getName());
                sc.setRequestAttribute(request, "createNewFilter", Boolean.TRUE);
                TaskFValue flthm = new TaskFValue();
                sortorderList = flthm.getSortOrder();

                for (FieldMap map : FieldMap.taskFields) {
                    field = flthm.setFields(sc, map, fields, sortorderList, selectedFields, field);
                }

                for (SecuredUDFValueBean udfvf : new TreeSet<SecuredUDFValueBean>(currentTask.getFilterUDFValues())) {
                    FieldMap map = new FieldMap(UdfValue.TOTAL_STANDESR_FIELDS + udfvf.getUdf().getOrder(), udfvf.getCaptionEx(), FValue.UDF + udfvf.getId(), FValue.UDF_SORT + udfvf.getId());
                    field = flthm.setFields(sc, map, fields, sortorderList, selectedFields, field);
                }
            } else {
                SecuredFilterBean currentFilter = AdapterManager.getInstance().getSecuredFindAdapterManager().findFilterById(sc, filterId);
                currentTask = currentFilter.getTask();
                if (!(sc.canAction(Action.manageTaskPrivateFilters, currentFilter.getTaskId()) && currentFilter.canManage()))
                    return null;
                canCreatePublicFilter = sc.canAction(TaskAction.manageTaskPublicFilters, currentFilter.getTaskId()) && sc.allowedByACL(currentFilter.getTaskId());
                bf.setName(currentFilter.getName());
                bf.setDescription(currentFilter.getDescription());

                bf.setShared(!currentFilter.isPrivate());
                bf.setShowInToolbar(Preferences.showInToolbar(currentFilter.getPreferences()));
                TaskFValue flthm = AdapterManager.getInstance().getSecuredFilterAdapterManager().getTaskFValue(sc, filterId).getFValue();
                bf.setOnpage(flthm.getAsString(TaskFValue.ONPAGE));
                bf.setSearch(flthm.getAsString(FieldMap.SEARCH.getFilterKey()));
                bf.setSubtask(flthm.getOriginValues(FValue.SUBTASK) != null);
                bf.setTask(currentFilter.getTaskId());
                bf.setUser(currentFilter.getOwnerId());
                String msgs = flthm.getAsString(FieldMap.MESSAGEVIEW.getFilterKey());
                bf.setMsgnum(msgs != null ? msgs : "1");
                boolean showMsgs = msgs != null && !msgs.equals("0");
                sc.setRequestAttribute(request, "disableMsgList", !showMsgs);
                bf.setShowmsg(showMsgs);
                sc.setRequestAttribute(request, "currentFilter", currentFilter);
                sc.setRequestAttribute(request, "filterId", filterId);
                bf.setMethod("edit");

                sortorderList = flthm.getSortOrder();

                for (FieldMap map : FieldMap.taskFields) {
                    field = flthm.setFields(sc, map, fields, sortorderList, selectedFields, field);
                }

                for (SecuredUDFValueBean udfvf : new TreeSet<SecuredUDFValueBean>(currentTask.getFilterUDFValues())) {
                    FieldMap map = new FieldMap(UdfValue.TOTAL_STANDESR_FIELDS + udfvf.getUdf().getOrder(), udfvf.getCaptionEx(), FValue.UDF + udfvf.getId(), FValue.UDF_SORT + udfvf.getId());
                    field = flthm.setFields(sc, map, fields, sortorderList, selectedFields, field);
                }
            }

            bf.setFields(field);
            int counter = sortorderList != null ? sortorderList.size() : 0;

            if (counter > 0) {
                String cn = "";
                for (int i = 0; i < counter; i++) cn += ".";
                sc.setRequestAttribute(request, "counter", cn);
            }

            Collections.sort(fields);
            sc.setRequestAttribute(request, "fields", fields);
            Collections.sort(selectedFields);
            sc.setRequestAttribute(request, "selectedFields", selectedFields);
            sc.setRequestAttribute(request, "canCreatePublicFilter", canCreatePublicFilter);
            return mapping.findForward("editTaskFilterJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }


    public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws GranException {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            FilterForm ff = (FilterForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String taskId = ff.getId();

            if (!sc.canAction(Action.manageTaskPrivateFilters, taskId)) return null;

            String filterId = AdapterManager.getInstance().getSecuredFilterAdapterManager().createTaskFilter(sc, ff.getName(), ff.getDescription(), !ff.isShared(), ff.getTask(), ff.getUser(), ff.isShowInToolbar() ? "T" : "");

            SecuredFilterBean currentFilter = AdapterManager.getInstance().getSecuredFindAdapterManager().findFilterById(sc, filterId);

            //считываем preferences у только что созданного фильтра, потому что могут быть
            //preferences, создаваемые по умолчанию
            String preferences = currentFilter.getPreferences();
            Preferences p = new Preferences(preferences);
            p.setShowInToolbar(ff.getShowInToolbar());
            AdapterManager.getInstance().getSecuredFilterAdapterManager().updateTaskFilter(sc, filterId, ff.getName(), ff.getDescription(), !ff.isShared(), p.getPreferences());

            ff.setFilterId(filterId);
            setFilterParameters(sc, filterId, ff);
            ff.setMutable(false);
            return mapping.findForward("viewTaskFilterPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward edit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws GranException {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            FilterForm ff = (FilterForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_TASK_FILTER_PROPERTIES);
            String filterId = ff.getFilterId();

            SecuredFilterBean currentFilter = AdapterManager.getInstance().getSecuredFindAdapterManager().findFilterById(sc, filterId);
            String preferences = currentFilter.getPreferences();
            Preferences p = new Preferences(preferences);
            p.setShowInToolbar(ff.getShowInToolbar());
            AdapterManager.getInstance().getSecuredFilterAdapterManager().updateTaskFilter(sc, ff.getFilterId(), ff.getName(), ff.getDescription(), !ff.isShared(), p.getPreferences());

            setFilterParameters(sc, filterId, ff);
            Object oldFilter = sc.getAttribute("taskfilter");
            if (oldFilter != null) {
                FilterSettings settings = (FilterSettings) oldFilter;
                if (settings.getFilterId().equals(filterId)) {
                    sc.removeAttribute("taskfilter");
                    sc.removeAttribute("statictask");
                    sc.removeAttribute("next");
                    sc.removeAttribute("statictasklist");
                }
            }
            sc.removeAttributes(sc.filter("_reportfilter_" + filterId));
            return mapping.findForward("viewTaskFilterPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    private void setFilterParameters(SessionContext sc, String filterId, FilterForm ff) throws GranException {
        TaskFValue flthm = AdapterManager.getInstance().getSecuredFilterAdapterManager().getTaskFValue(sc, filterId).getFValue();
        flthm.set(TaskFValue.ONPAGE, ff.getOnpage());
        flthm.set(FieldMap.SEARCH.getFilterKey(), ff.getSearch());
        flthm.set(FValue.SUBTASK, ff.getSubtask() ? "1" : null);
        if (!ff.isShowmsg())
            flthm.set(FieldMap.MESSAGEVIEW.getFilterKey(), "0");
        else
            flthm.set(FieldMap.MESSAGEVIEW.getFilterKey(), ff.getMsgnum());
        String fields = ff.getFields();
        StringTokenizer tk = new StringTokenizer(fields, FValue.DELIM);
        String[] sorted = new String[32];
        ArrayList<String> display = new ArrayList<String>();

        while (tk.hasMoreElements()) {
            String token = tk.nextToken();
            if (token.indexOf("+") > -1) {
                // asc
                int pos = Integer.parseInt(token.substring(2, 3));
                sorted[pos - 1] = "_" + token.substring(5);
            } else if (token.indexOf("-") > -1) {
                //desc
                int pos = Integer.parseInt(token.substring(2, 3));
                sorted[pos - 1] = token.substring(5);
            }

            display.add(FieldMap.getFilterKeyByFieldKey(token.substring(5)));

        }

        flthm.setList(FValue.DISPLAY, display);
        flthm.setSortOrder(sorted);
        Object oldFilter = sc.getAttribute("taskfilter");
        if (oldFilter != null) {
            FilterSettings settings = (FilterSettings) oldFilter;
            if (settings.getFilterId().equals(filterId))
                sc.removeAttribute("taskfilter"); //winzard: ??? ?? ???????, ??? ????
            sc.removeAttribute("statictask"); //winzard: ??? ?? ???????, ??? ????
            sc.removeAttribute("next"); //winzard: ??? ?? ???????, ??? ????
            sc.removeAttribute("statictasklist"); //winzard: ??? ?? ???????, ??? ????
        }
        AdapterManager.getInstance().getSecuredFilterAdapterManager().setFValue(sc, filterId, new SecuredTaskFValueBean(flthm, sc));
    }
}
