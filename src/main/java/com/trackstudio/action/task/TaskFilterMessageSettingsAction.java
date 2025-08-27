package com.trackstudio.action.task;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.trackstudio.action.GeneralAction;
import com.trackstudio.action.TSDispatchAction;
import com.trackstudio.app.PageNaming;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.filter.FilterSettings;
import com.trackstudio.app.filter.TaskFValue;
import com.trackstudio.app.filter.customizer.BudgetCustomizer;
import com.trackstudio.app.filter.customizer.Customizer;
import com.trackstudio.app.filter.customizer.DateCustomizer;
import com.trackstudio.app.filter.customizer.ListCustomizer;
import com.trackstudio.app.filter.customizer.TextCustomizer;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.common.FieldMap;
import com.trackstudio.form.FilterForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.secured.SecuredFilterBean;
import com.trackstudio.secured.SecuredTaskFValueBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.ExternalAdapterManagerUtil;
import com.trackstudio.tools.Tab;

public class TaskFilterMessageSettingsAction extends TSDispatchAction {

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            FilterForm bf = (FilterForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");

            String id = GeneralAction.getInstance().taskHeader(bf, sc, request, true);
            String filterId = bf.getFilterId() == null ? request.getParameter("filterId") : bf.getFilterId();
            bf.setFilterId(filterId);
            SecuredFilterBean currentFilter = AdapterManager.getInstance().getSecuredFindAdapterManager().findFilterById(sc, filterId);
            if (!(sc.canAction(Action.manageTaskPrivateFilters, currentFilter.getTaskId()) && currentFilter.canManage()))
                return null;
            HashMap<String, String> workflowMap = ExternalAdapterManagerUtil.getAvailableWorkflowMap(sc, currentFilter.getTaskId());
            TaskFValue flthm = AdapterManager.getInstance().getSecuredFilterAdapterManager().getTaskFValue(sc, filterId).getFValue();

            ArrayList<Customizer> customs = new ArrayList<Customizer>();
            HashMap<String, String> handlerUsersMap = ExternalAdapterManagerUtil.makeStatusAndUserMap(sc, flthm, currentFilter.getTaskId());
            HashMap<String, String> usersMap = ExternalAdapterManagerUtil.makeUserMap(sc, flthm, currentFilter.getTaskId());


            usersMap.put("null", "-------" + I18n.getString(sc.getLocale(), "NONE") + "-------");

            HashMap<String, String> resolutionMap = ExternalAdapterManagerUtil.getAvailableResolutionMap(sc, workflowMap);
            resolutionMap.put("null", "-------" + I18n.getString(sc.getLocale(), "NONE") + "-------");

            HashMap mstatusMap = ExternalAdapterManagerUtil.getAvailableMstatusMap(sc, workflowMap);
            //mstatusMap.put("null", "-------" + I18n.getString(sc.getLocale(), "TERM_NONE") + "-------");

              /*
              ArrayList col = AdapterManager.getInstance().getSecuredPrstatusAdapterManager().getAllViewablePrstatuses(sc);

              HashMap userStatuses = new HashMap();

              for (Iterator iw = col.iterator(); iw.hasNext();) {
                  SecuredPrstatusBean prstatus = (SecuredPrstatusBean) iw.next();
                  userStatuses.put(prstatus.getId(), HTMLEncoder.encode(prstatus.getName()));
              }
              */
            String javaScript = "";

            sc.setRequestAttribute(request, "javaScript", javaScript);

            String contextPath = request.getContextPath();


              /*
              Customizer forMsgView = new TextCustomizer(TextCustomizer.QUANTITIES, FieldMap.MESSAGEVIEW);
              sc.setRequestAttribute(request,"forMsgView", forMsgView.draw(sc, flthm, contextPath,  false, false));
              customs.add(forMsgView);
              Customizer forMsgFilter = new TextCustomizer(TextCustomizer.QUANTITIES, FieldMap.MESSAGEFILTER);
              sc.setRequestAttribute(request,"forMsgFilter", forMsgFilter.draw(sc, flthm, contextPath,  false, false));
              customs.add(forMsgFilter);
              */

            Customizer forMsgSubmitter = new ListCustomizer(usersMap, ListCustomizer.LIST_EQUAL, FieldMap.MSG_SUSER_NAME, true);
            sc.setRequestAttribute(request, "forMsgSubmitter", forMsgSubmitter.draw(sc, flthm, contextPath));
            customs.add(forMsgSubmitter);

            Customizer forMsgDate = new DateCustomizer(FieldMap.MSG_SUBMITDATE, sc.getUser().getDateFormatter());
            sc.setRequestAttribute(request, "forMsgDate", forMsgDate.draw(sc, flthm, contextPath));
            customs.add(forMsgDate);

            Customizer forLastMsgDate = new DateCustomizer(FieldMap.LAST_MSG_SUBMITDATE, sc.getUser().getDateFormatter());
            sc.setRequestAttribute(request, "forLastMsgDate", forLastMsgDate.draw(sc, flthm, contextPath));
            customs.add(forLastMsgDate);

            Customizer forMsgMstatus = new ListCustomizer(mstatusMap, ListCustomizer.LIST_EQUAL, FieldMap.MSG_MSTATUS, true);
            sc.setRequestAttribute(request, "forMsgMstatus", forMsgMstatus.draw(sc, flthm, contextPath));
            customs.add(forMsgMstatus);

            Customizer forMsgHandler = new ListCustomizer(handlerUsersMap, ListCustomizer.LIST_EQUAL, FieldMap.MSG_HUSER_NAME, true);
            sc.setRequestAttribute(request, "forMsgHandler", forMsgHandler.draw(sc, flthm, contextPath));
            customs.add(forMsgHandler);

            Customizer forMsgResolution = new ListCustomizer(resolutionMap, ListCustomizer.LIST_EQUAL, FieldMap.MSG_RESOLUTION, true);
            sc.setRequestAttribute(request, "forMsgResolution", forMsgResolution.draw(sc, flthm, contextPath));
            customs.add(forMsgResolution);

            Customizer forMsgBudget = new BudgetCustomizer(BudgetCustomizer.ALL, FieldMap.MSG_ABUDGET);
            sc.setRequestAttribute(request, "forMsgBudget", forMsgBudget.draw(sc, flthm, contextPath));
            customs.add(forMsgBudget);

            Customizer forMsgText = new TextCustomizer(TextCustomizer.CHARS, FieldMap.TEXT_MSG);
            sc.setRequestAttribute(request, "forMsgText", forMsgText.draw(sc, flthm, contextPath));
            customs.add(forMsgText);

            sc.removeAttribute("customs");
            sc.setAttribute("customs", customs);

            sc.setRequestAttribute(request, "currentFilter", currentFilter);
            sc.setRequestAttribute(request, "filterId", filterId);

            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_TASK_FILTER_MESSAGE_PARAMETERS);
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_TASK_FILTER_MESSAGE_PARAMETERS));
            sc.setRequestAttribute(request, "tabView", new Tab(sc.canAction(Action.manageTaskPrivateFilters, currentFilter.getTaskId()), false));
            sc.setRequestAttribute(request, "tabEdit", new Tab(sc.canAction(Action.manageTaskPrivateFilters, currentFilter.getTaskId()) && currentFilter.canManage() && (sc.canAction(Action.manageTaskPublicFilters, id) || currentFilter.isPrivate()), false));
            sc.setRequestAttribute(request, "tabTaskSettings", new Tab(sc.canAction(Action.manageTaskPrivateFilters, currentFilter.getTaskId()) && currentFilter.canManage() && (sc.canAction(Action.manageTaskPublicFilters, id) || currentFilter.isPrivate()), false));
            sc.setRequestAttribute(request, "tabMessageSettings", new Tab(sc.canAction(Action.manageTaskPrivateFilters, currentFilter.getTaskId()) && currentFilter.canManage() && (sc.canAction(Action.manageTaskPublicFilters, id) || currentFilter.isPrivate()), true));

            selectTaskTab(sc, id, "tabFilters", request);
            return mapping.findForward("editTaskFilterMessageSettingsJSP");
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
            String filterId = ff.getFilterId();

            if (sc.getAttribute("customs") != null) {
                ArrayList<Customizer> customs = (ArrayList) sc.getAttribute("customs");
                TaskFValue flt = AdapterManager.getInstance().getSecuredFilterAdapterManager().getTaskFValue(sc, filterId).getFValue();
                for (Customizer ab : customs) {
                    ab.setFilter(sc, request, flt);
                }
                sc.removeAttribute("customs");
                AdapterManager.getInstance().getSecuredFilterAdapterManager().setFValue(sc, filterId, new SecuredTaskFValueBean(flt, sc));
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