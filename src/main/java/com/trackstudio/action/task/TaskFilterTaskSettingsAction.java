package com.trackstudio.action.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
import com.trackstudio.app.UdfValue;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.filter.FValue;
import com.trackstudio.app.filter.FilterSettings;
import com.trackstudio.app.filter.TaskFValue;
import com.trackstudio.app.filter.customizer.BudgetCustomizer;
import com.trackstudio.app.filter.customizer.Customizer;
import com.trackstudio.app.filter.customizer.DateCustomizer;
import com.trackstudio.app.filter.customizer.ListCustomizer;
import com.trackstudio.app.filter.customizer.TextCustomizer;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.common.FieldMap;
import com.trackstudio.exception.UserException;
import com.trackstudio.form.FilterForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.secured.SecuredFilterBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredTaskFValueBean;
import com.trackstudio.secured.SecuredUDFValueBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.ExternalAdapterManagerUtil;
import com.trackstudio.tools.Tab;
import com.trackstudio.tools.compare.FieldSort;
import com.trackstudio.tools.compare.SortUdf;

public class TaskFilterTaskSettingsAction extends TSDispatchAction {

    private static Log log = LogFactory.getLog(TaskFilterTaskSettingsAction.class);

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            FilterForm bf = (FilterForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");

            String id = GeneralAction.getInstance().taskHeader(bf, sc, request, true);
            String filterId = bf.getFilterId() == null ? request.getParameter("filterId") : bf.getFilterId();
            bf.setFilterId(filterId);
            bf.setId(id);
            SecuredFilterBean currentFilter = AdapterManager.getInstance().getSecuredFindAdapterManager().findFilterById(sc, filterId);
            if (currentFilter == null) {
                throw new UserException("Sorry, the filter is not found!");
            }
            if (!sc.canAction(Action.manageTaskPrivateFilters, currentFilter.getTaskId()) && !currentFilter.canManage())
                return null;

            HashMap<String, String> categoryMap = new HashMap<String, String>();
            HashMap<String, String> workflowMap = new HashMap<String, String>();
            ExternalAdapterManagerUtil.fillAvailableCategoryAndWorkflowMaps(sc, currentFilter.getTaskId(), categoryMap, workflowMap);


            SecuredTaskFValueBean settings = AdapterManager.getInstance().getSecuredFilterAdapterManager().getTaskFValue(sc, filterId);
            TaskFValue flthm = settings.getFValue();
            sc.setRequestAttribute(request, "flthm", flthm);
            ArrayList<Customizer> customs = new ArrayList<Customizer>();
            boolean disabled = !currentFilter.canManage() || !sc.canAction(Action.manageTaskPrivateFilters, currentFilter.getTaskId()) || !sc.canAction(Action.manageTaskPrivateFilters, id);
            sc.setRequestAttribute(request, "disabled", disabled);
            HashMap<String, String> usersMap = ExternalAdapterManagerUtil.makeUserMap(sc, flthm, currentFilter.getTaskId());

            HashMap<String, String> handlerUserMap = ExternalAdapterManagerUtil.makeStatusAndUserMap(sc, flthm, currentFilter.getTaskId());
            HashMap<String, String> userStatuses = ExternalAdapterManagerUtil.makeStatusMap(sc, currentFilter.getTaskId());


            handlerUserMap.putAll(usersMap);
            HashMap<String, String> resolutionMap = ExternalAdapterManagerUtil.getAvailableResolutionMap(sc, workflowMap);
            HashMap<String, String> mstatusMap = ExternalAdapterManagerUtil.getAvailableMstatusMap(sc, workflowMap);
            //HashMap userStatuses = (HashMap)AdapterManager.getInstance().getSecuredAclAdapterManager().getStatusesForTaskFilter(sc);
            ArrayList<String> udfIds = new ArrayList<String>();
            SecuredTaskBean filtertask = new SecuredTaskBean(currentFilter.getTaskId(), sc);
            ArrayList<SecuredUDFValueBean> list = filtertask.getFilterUDFValues();
            for (SecuredUDFValueBean udfvf : list)
                udfIds.add(udfvf.getId());

            String javaScript = "";

            List<String> tk = flthm.getSortOrder();
            for (String token : tk) {
                if (token != null && token.length() != 0 && (token.indexOf(FValue.UDF_SORT) == -1 || udfIds.contains(token.substring(token.startsWith("_") ? FValue.UDF_SORT.length() + 1 : FValue.UDF_SORT.length())))) {
                    javaScript += "levelArray[levelArray.length] = \"" + (token.startsWith("_") ? token.substring(1) : token) + "\";";
                }
            }

            sc.setRequestAttribute(request, "javaScript", javaScript);

            String contextPath = request.getContextPath();

            Customizer forNumber = new TextCustomizer(TextCustomizer.NATURAL, FieldMap.TASK_NUMBER);
            sc.setRequestAttribute(request, "forNumber", forNumber.draw(sc, flthm, contextPath));
            customs.add(forNumber);

            Customizer forFullPath = new TextCustomizer(TextCustomizer.NOT_FILTERED, FieldMap.FULLPATH);
            sc.setRequestAttribute(request, "forFullPath", forFullPath.draw(sc, flthm, contextPath));
            customs.add(forFullPath);

            Customizer forName = new TextCustomizer(TextCustomizer.CHARS, FieldMap.TASK_NAME);
            sc.setRequestAttribute(request, "forName", forName.draw(sc, flthm, contextPath));
            customs.add(forName);


            Customizer forCategory = new ListCustomizer(categoryMap, ListCustomizer.LIST_EQUAL, FieldMap.TASK_CATEGORY, true);
            sc.setRequestAttribute(request, "forCategory", forCategory.draw(sc, flthm, contextPath));
            customs.add(forCategory);

            Customizer forStatus = new ListCustomizer(ExternalAdapterManagerUtil.getAvailableStatusMap(sc, workflowMap), ListCustomizer.LIST_EQUAL, FieldMap.TASK_STATUS, true);
            sc.setRequestAttribute(request, "forStatus", forStatus.draw(sc, flthm, contextPath));
            customs.add(forStatus);

            Customizer forResolution = new ListCustomizer(resolutionMap, ListCustomizer.LIST_EQUAL, FieldMap.TASK_RESOLUTION, true);
            sc.setRequestAttribute(request, "forResolution", forResolution.draw(sc, flthm, contextPath));
            customs.add(forResolution);

            Customizer forPriority = new ListCustomizer(ExternalAdapterManagerUtil.getAvailablePriorityMap(sc, workflowMap), ListCustomizer.LIST_EQUAL, FieldMap.TASK_PRIORITY, true);
            sc.setRequestAttribute(request, "forPriority", forPriority.draw(sc, flthm, contextPath));
            customs.add(forPriority);

            Customizer forSubmitter = new ListCustomizer(usersMap, ListCustomizer.LIST_EQUAL, FieldMap.SUSER_NAME, true);
            sc.setRequestAttribute(request, "forSubmitter", forSubmitter.draw(sc, flthm, contextPath));
            customs.add(forSubmitter);

            Customizer forSubmitterStatus = new ListCustomizer(userStatuses, ListCustomizer.LIST_EQUAL, FieldMap.SUSER_STATUS, true);
            sc.setRequestAttribute(request, "forSubmitterStatus", forSubmitterStatus.draw(sc, flthm, contextPath));
            customs.add(forSubmitterStatus);

            Customizer forHandler = new ListCustomizer(handlerUserMap, ListCustomizer.LIST_EQUAL, FieldMap.HUSER_NAME, true);
            sc.setRequestAttribute(request, "forHandler", forHandler.draw(sc, flthm, contextPath));
            customs.add(forHandler);

            userStatuses.put("null", "-------" + I18n.getString(sc.getLocale(), "NONE") + "-------");
            Customizer forHandlerStatus = new ListCustomizer(userStatuses, ListCustomizer.LIST_EQUAL, FieldMap.HUSER_STATUS, true);
            sc.setRequestAttribute(request, "forHandlerStatus", forHandlerStatus.draw(sc, flthm, contextPath));
            customs.add(forHandlerStatus);

            Customizer forSubmitDate = new DateCustomizer(FieldMap.TASK_SUBMITDATE, sc.getUser().getDateFormatter());
            sc.setRequestAttribute(request, "forSubmitDate", forSubmitDate.draw(sc, flthm, contextPath));
            customs.add(forSubmitDate);

            Customizer forUpdatedDate = new DateCustomizer(FieldMap.TASK_UPDATEDATE, sc.getUser().getDateFormatter());
            sc.setRequestAttribute(request, "forUpdatedDate", forUpdatedDate.draw(sc, flthm, contextPath));
            customs.add(forUpdatedDate);

            Customizer forCloseDate = new DateCustomizer(FieldMap.TASK_CLOSEDATE, sc.getUser().getDateFormatter());
            sc.setRequestAttribute(request, "forCloseDate", forCloseDate.draw(sc, flthm, contextPath));
            customs.add(forCloseDate);

            Customizer forDeadline = new DateCustomizer(FieldMap.TASK_DEADLINE, sc.getUser().getDateFormatter());
            sc.setRequestAttribute(request, "forDeadline", forDeadline.draw(sc, flthm, contextPath));
            customs.add(forDeadline);

            Customizer forBudget = new BudgetCustomizer(BudgetCustomizer.ALL, FieldMap.TASK_BUDGET);
            sc.setRequestAttribute(request, "forBudget", forBudget.draw(sc, flthm, contextPath));
            customs.add(forBudget);

            Customizer forABudget = new BudgetCustomizer(BudgetCustomizer.ALL, FieldMap.TASK_ABUDGET);
            sc.setRequestAttribute(request, "forABudget", forABudget.draw(sc, flthm, contextPath));
            customs.add(forABudget);

            Customizer forChild = new TextCustomizer(TextCustomizer.NATURAL, FieldMap.TASK_CHILDCOUNT);
            sc.setRequestAttribute(request, "forChild", forChild.draw(sc, flthm, contextPath));
            customs.add(forChild);

            Customizer forMessage = new TextCustomizer(TextCustomizer.NATURAL, FieldMap.TASK_MESSAGECOUNT);
            sc.setRequestAttribute(request, "forMessage", forMessage.draw(sc, flthm, contextPath));
            customs.add(forMessage);

            Customizer forDesc = new TextCustomizer(TextCustomizer.CHARS, FieldMap.TASK_DESCRIPTION);
            sc.setRequestAttribute(request, "forDesc", forDesc.draw(sc, flthm, contextPath));
            customs.add(forDesc);

            Map<String, String> booleanList = new LinkedHashMap<String, String>();
            booleanList.put("yes", I18n.getString(sc, "YES"));
            booleanList.put("no", I18n.getString(sc, "NO"));

            ArrayList<String> udfCustomizers = new ArrayList<String>();
            Collections.sort(list, new SortUdf(FieldSort.NAME));
            for (SecuredUDFValueBean udfvf : list) {
                Customizer cust;
                if (udfvf.getUdfType() != UdfValue.USER)
                    cust = udfvf.getCustomizer(udfvf.getId(), false);
                else {
                    FieldMap fm = new FieldMap(UdfValue.TOTAL_STANDESR_FIELDS + udfvf.getUdf().getOrder(), udfvf.getCaptionEx(), FValue.UDF + udfvf.getId(), FValue.UDF_SORT + udfvf.getId());
                    cust = new ListCustomizer(usersMap, ListCustomizer.LIST_EQUAL, fm, true);
                }
                //cust.setDisableAltKey(true);
                if (udfvf.isLookup() && cust instanceof TextCustomizer)
                    ((TextCustomizer) cust).setSecured(currentFilter.getTask());
                customs.add(cust);
                udfCustomizers.add(cust.draw(sc, flthm, contextPath));
            }
            sc.setRequestAttribute(request, "udfCustomizers", udfCustomizers);

            String msgscnt = flthm.getAsString(FieldMap.MESSAGEVIEW.getFilterKey());
            boolean showMsgs = msgscnt != null && !msgscnt.equals("0");

            Collections.sort(customs);
            sc.removeAttribute("customs");
            sc.setAttribute("customs", customs);
            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_TASK_FILTER_TASK_PARAMETERS);
            sc.setRequestAttribute(request, "currentFilter", currentFilter);
            sc.setRequestAttribute(request, "filterId", filterId);
            sc.setRequestAttribute(request, "tileId", "taskFilterParams");
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_TASK_FILTER_TASK_PARAMETERS));
            sc.setRequestAttribute(request, "tabView", new Tab(sc.canAction(Action.manageTaskPrivateFilters, currentFilter.getTaskId()), false));
            sc.setRequestAttribute(request, "tabEdit", new Tab(sc.canAction(Action.manageTaskPrivateFilters, currentFilter.getTaskId()) && currentFilter.canManage() && (sc.canAction(Action.manageTaskPublicFilters, id) || currentFilter.isPrivate()), false));
            sc.setRequestAttribute(request, "tabTaskSettings", new Tab(sc.canAction(Action.manageTaskPrivateFilters, currentFilter.getTaskId()) && currentFilter.canManage() && (sc.canAction(Action.manageTaskPublicFilters, id) || currentFilter.isPrivate()), true));
            sc.setRequestAttribute(request, "tabMessageSettings", new Tab(showMsgs && sc.canAction(Action.manageTaskPrivateFilters, currentFilter.getTaskId()) && currentFilter.canManage() && (sc.canAction(Action.manageTaskPublicFilters, id) || currentFilter.isPrivate()), false));

            return mapping.findForward("editTaskFilterTaskSettingsJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward edit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            FilterForm ff = (FilterForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String filterId = ff.getFilterId();
            if (sc.getAttribute("customs") != null) {
                ArrayList<Customizer> customs = (ArrayList) sc.getAttribute("customs");
                TaskFValue flthm = AdapterManager.getInstance().getSecuredFilterAdapterManager().getTaskFValue(sc, filterId).getFValue();
                for (Customizer ab : customs) {
                    ab.setFilter(sc, request, flthm);
                }
                sc.removeAttribute("customs");
                AdapterManager.getInstance().getSecuredFilterAdapterManager().setFValue(sc, filterId, new SecuredTaskFValueBean(flthm, sc));
            }
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
}