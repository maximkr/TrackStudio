package com.trackstudio.action.task;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import com.trackstudio.app.Slider;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.filter.FValue;
import com.trackstudio.app.filter.FilterSettings;
import com.trackstudio.app.filter.TaskFValue;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.common.FieldMap;
import com.trackstudio.constants.UdfConstants;
import com.trackstudio.exception.GranException;
import com.trackstudio.form.TaskListForm;
import com.trackstudio.secured.SecuredFilterBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUDFBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.Null;
import com.trackstudio.tools.formatter.SortedFactory;
import com.trackstudio.tools.formatter.SortedLink;
import com.trackstudio.tools.textfilter.HTMLEncoder;


public class TaskSelectAction extends TSDispatchAction {

    private static Log log = LogFactory.getLog(TaskSelectAction.class);

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            TaskListForm sf = (TaskListForm) form;
            SessionContext sc = GeneralAction.getInstance().imports(request, response);
            String id = GeneralAction.getInstance().taskHeader(sf, sc, request, true);
            String filterId = sf.getFilter();
            if (sf.getUdfvalue() != null && sf.getUdfvalue().length() != 0) {
                String s = sf.getUdfvalue();
                if (s.startsWith("UDF")) s = s.substring(3);
                SecuredUDFBean udfls = AdapterManager.getInstance().getSecuredFindAdapterManager().findUDFById(sc, s);
                id = udfls.getInitial() != null ? udfls.getInitial() : id;
                sc.setRequestAttribute(request, "tci", new SecuredTaskBean(id, sc));
                sc.setRequestAttribute(request, "id", id);
            }
            sc.setCurrentSpace("TaskSelectAction", request);

            String contextPath = request.getContextPath();
            ArrayList<SecuredFilterBean> filterSet = AdapterManager.getInstance().getSecuredFilterAdapterManager().getTaskFilterList(sc, id);
            boolean hasFilter = false;
            for (SecuredFilterBean filter : filterSet) {
                if (filter.getId().equals(filterId)) {
                    hasFilter = true;
                    break;
                }
            }
            filterId = hasFilter ? filterId : "1";

            sc.setRequestAttribute(request, "filters", filterSet);
            sc.setRequestAttribute(request, "viewMessageCheckbox", false);

            if (filterId == null) {
                Object filterObject = sc.getAttribute("taskfilter");
                if (filterObject != null) {
                    filterId = ((FilterSettings) filterObject).getFilterId();
                }
                if (filterId == null) {
                    filterId = AdapterManager.getInstance().getSecuredFilterAdapterManager().getCurrentTaskFilterId(sc, id);
                }
            }
            FilterSettings filterSettings = null;

            Object filterObject = sc.getAttribute("taskfilter");
            if (filterObject != null) {
                filterSettings = (FilterSettings) filterObject;
                if (!filterSettings.getFilterId().equals(filterId) || !filterSettings.getObjectId().equals(id)) {

                    filterSettings = null;
                }
            }

            TaskFValue val = AdapterManager.getInstance().getSecuredFilterAdapterManager().getTaskFValue(sc, filterId).getFValue();
            FilterSettings originFilterSettings;
            originFilterSettings = new FilterSettings(val, id, filterId);
            if (filterSettings == null) {
                filterSettings = originFilterSettings;
                sc.setAttribute("taskselectfilter", originFilterSettings);
            }
            SecuredFilterBean filter = AdapterManager.getInstance().getSecuredFindAdapterManager().findFilterById(sc, filterId);
            sc.setRequestAttribute(request, "filter", filter);

            sf.setFilter(filterId);
            String udffield = null;
            Object o_udffield = request.getParameter("udffield");
            if (o_udffield != null) udffield = o_udffield.toString();
            if (udffield != null && udffield.length() > 0) {
                String udfId = udffield;
                if (udffield.indexOf("(") != -1 && udffield.indexOf(")") != -1) {
                    udfId = udffield.substring(udffield.indexOf("(")+1, udffield.indexOf(")"));
                }
                if (udfId != null && udfId.length() > 0) {

                    SecuredUDFBean udfls = AdapterManager.getInstance().getSecuredFindAdapterManager().findUDFById(sc, udfId);
                    if (udfls != null)
                        sc.setRequestAttribute(request, "currentUDF", udfls.getCaptionEx());
                    response.addCookie(createCookie("udffield", udffield));
                    sc.setRequestAttribute(request, "udffield", udffield);
                }
            }

            Object opack = request.getParameter("pack");
            String pack = null;
            String taskIds = "";
            if (opack != null) {
                pack = URLDecoder.decode(opack.toString());

                for (String p : pack.split(";")) {
                    SecuredTaskBean i = AdapterManager.getInstance().getSecuredTaskAdapterManager().findTaskByNumber(sc, p.trim());
                    if (i != null) taskIds += i.getId() + ":";
                }
                response.addCookie(createCookie("_selectedId", taskIds));

            } else {
                Cookie[] cook = request.getCookies();
                if (taskIds.length() == 0 && cook != null) {
                    for (Cookie c : cook) {
                        log.debug(c.getName() + c.getValue());
                        if (c.getName().equals("_selectedId") && c.getValue() != null && c.getValue().length() > 0) {
                            taskIds = c.getValue();
                        }
                    }
                }
            }
            ArrayList<SecuredTaskBean> selected = new ArrayList<SecuredTaskBean>();
            for (String s : taskIds.split(UdfConstants.SPLIT_SEPARATOR)) {
                SecuredTaskBean t = AdapterManager.getInstance().getSecuredFindAdapterManager().findTaskById(sc, s);
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
            Slider<SecuredTaskBean> taskSlider = AdapterManager.getInstance().getSecuredTaskAdapterManager().getTaskList(sc, id, (TaskFValue) filterSettings.getSettings(), true, filterSettings.getCurrentPage(), filterSettings.getSortedBy());
            taskSlider.setAll(sf.isAll(), sc.getLocale());
            totalChildrenCount = taskSlider.getTotalChildrenCount();

            sc.setRequestAttribute(request, "slider", taskSlider.drawSlider(contextPath + "/TaskSelectAction.do?method=page&amp;id=" + id + "&udffield=" + udffield, "div", "slider"));
            sc.setRequestAttribute(request, "sliderSize", taskSlider.getColSize() != null ? taskSlider.getColSize() : I18n.getString(sc.getLocale(), "HUNDREDS"));

            SortedFactory fact = new SortedFactory(originFilterSettings, filterSettings.getSortedBy());
            SortedLink fullPathLnk = fact.getLink(FieldMap.FULLPATH.getFieldKey(), FieldMap.FULLPATH.getFilterKey(), 12);
            SortedLink taskNameLnk = fact.getLink(FieldMap.TASK_NAME.getFieldKey(), FieldMap.TASK_NAME.getFilterKey(), 12);
            int sizeOfPart = 100 / fact.getParts();
            int freePercents = 100 - fact.getParts() * sizeOfPart;
            if (fullPathLnk.getCanView() && taskNameLnk.getCanView()) {
                int type = freePercents >> 1;
                fullPathLnk.setParts(12 * sizeOfPart + type);
                taskNameLnk.setParts(12 * sizeOfPart + (freePercents - type));
                sc.setRequestAttribute(request, "headerFullPath", fullPathLnk);
                sc.setRequestAttribute(request, "headerName", taskNameLnk);
            } else {
                fullPathLnk.setParts(12 * sizeOfPart + freePercents);
                taskNameLnk.setParts(12 * sizeOfPart + freePercents);
                sc.setRequestAttribute(request, "headerFullPath", fullPathLnk);
                sc.setRequestAttribute(request, "headerName", taskNameLnk);
            }

            sc.setRequestAttribute(request, "headerNumber", fact.getLink(FieldMap.TASK_NUMBER.getFieldKey(), FieldMap.TASK_NUMBER.getFilterKey(), 1));
            sc.setRequestAttribute(request, "headerAlias", fact.getLink(FieldMap.TASK_SHORTNAME.getFieldKey(), FieldMap.TASK_SHORTNAME.getFilterKey(), 2));
            sc.setRequestAttribute(request, "headerCategory", fact.getLink(FieldMap.TASK_CATEGORY.getFieldKey(), FieldMap.TASK_CATEGORY.getFilterKey(), 3));
            sc.setRequestAttribute(request, "headerStatus", fact.getLink(FieldMap.TASK_STATUS.getFieldKey(), FieldMap.TASK_STATUS.getFilterKey(), 3));
            sc.setRequestAttribute(request, "headerResolution", fact.getLink(FieldMap.TASK_RESOLUTION.getFieldKey(), FieldMap.TASK_RESOLUTION.getFilterKey(), 3));
            sc.setRequestAttribute(request, "headerPriority", fact.getLink(FieldMap.TASK_PRIORITY.getFieldKey(), FieldMap.TASK_PRIORITY.getFilterKey(), 3));
            sc.setRequestAttribute(request, "headerSubmitter", fact.getLink(FieldMap.SUSER_NAME.getFieldKey(), FieldMap.SUSER_NAME.getFilterKey(), 4));
            sc.setRequestAttribute(request, "headerSubmitterStatus", fact.getLink(FieldMap.SUSER_STATUS.getFieldKey(), FieldMap.SUSER_STATUS.getFilterKey(), 3));
            sc.setRequestAttribute(request, "headerHandler", fact.getLink(FieldMap.HUSER_NAME.getFieldKey(), FieldMap.HUSER_NAME.getFilterKey(), 4));
            sc.setRequestAttribute(request, "headerHandlerStatus", fact.getLink(FieldMap.HUSER_STATUS.getFieldKey(), FieldMap.HUSER_STATUS.getFilterKey(), 3));
            sc.setRequestAttribute(request, "headerSubmitDate", fact.getLink(FieldMap.TASK_SUBMITDATE.getFieldKey(), FieldMap.TASK_SUBMITDATE.getFilterKey(), 3));
            sc.setRequestAttribute(request, "headerUpdateDate", fact.getLink(FieldMap.TASK_UPDATEDATE.getFieldKey(), FieldMap.TASK_UPDATEDATE.getFilterKey(), 3));
            sc.setRequestAttribute(request, "headerCloseDate", fact.getLink(FieldMap.TASK_CLOSEDATE.getFieldKey(), FieldMap.TASK_CLOSEDATE.getFilterKey(), 3));
            sc.setRequestAttribute(request, "headerDeadline", fact.getLink(FieldMap.TASK_DEADLINE.getFieldKey(), FieldMap.TASK_DEADLINE.getFilterKey(), 3));
            sc.setRequestAttribute(request, "headerBudget", fact.getLink(FieldMap.TASK_BUDGET.getFieldKey(), FieldMap.TASK_BUDGET.getFilterKey(), 2));
            sc.setRequestAttribute(request, "headerActualBudget", fact.getLink(FieldMap.TASK_ABUDGET.getFieldKey(), FieldMap.TASK_ABUDGET.getFilterKey(), 2));
            sc.setRequestAttribute(request, "headerChildrenCount", fact.getLink(FieldMap.TASK_CHILDCOUNT.getFieldKey(), FieldMap.TASK_CHILDCOUNT.getFilterKey(), 1));
            sc.setRequestAttribute(request, "headerMessageCount", fact.getLink(FieldMap.TASK_MESSAGECOUNT.getFieldKey(), FieldMap.TASK_MESSAGECOUNT.getFilterKey(), 1));
            sc.setRequestAttribute(request, "headerTaskParent", fact.getLink(FieldMap.TASK_PARENT.getFieldKey(), FieldMap.TASK_PARENT.getFilterKey(), 2));

            List<String> udfs = new ArrayList<String>();
            HashMap<String, SortedLink> udfHeaderLink = new HashMap<String, SortedLink>();
            HashMap<String, String> udfHeaderCaption = new HashMap<String, String>();
            if ( filterSettings.getSettings().needFilterUDF()) {
                FValue settings = originFilterSettings.getSettings();
                List<String> view = settings.getView();
                for (String udfKey : view) {
                    if (udfKey.contains(FValue.UDF)) {
                        SecuredUDFBean udf = AdapterManager.getInstance().getSecuredFindAdapterManager().findUDFById(sc, udfKey.substring(FValue.UDF.length()));
                        if (udf != null) {
                            udfHeaderLink.put(udf.getId(), fact.getLink(FValue.UDF_SORT + udf.getId(), FValue.UDF + udf.getId(), 2));
                            udfHeaderCaption.put(udf.getId(), Null.stripNullHtml(HTMLEncoder.encode(udf.getCaption())));
                            udfs.add(udf.getId());
                        }
                    }
                }
            }
            sc.setRequestAttribute(request, "udfHeaderLink", udfHeaderLink);
            sc.setRequestAttribute(request, "udfHeaderCaption", udfHeaderCaption);
            sc.setRequestAttribute(request, "udfs", udfs);

            boolean useCookies = true;
            sc.setRequestAttribute(request, "useCookies", useCookies);
//                    SubtaskAction.highlightOperation(sc, request);
            ArrayList<String> defaultSortString = new ArrayList<String>();
            defaultSortString.add("task_updatedate");
//        ArrayList<String> sortstring = filterSettings.getSortedBy() != null && filterSettings.getSortedBy().size() != 0 ? filterSettings.getSortedBy() : defaultSortString;

            List<SecuredTaskBean> taskLines = taskSlider.getCol();

            sc.setRequestAttribute(request, "taskLines", taskLines);
            sc.setRequestAttribute(request, "taskfilter", filterSettings);

            sc.setRequestAttribute(request, "isList", "true");

            String currentURL = contextPath + "/TaskSelectAction.do";

            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_TASK_LIST);
            sc.setRequestAttribute(request, "currentURL", currentURL);
            sc.setRequestAttribute(request, "totalChildrenCount", totalChildrenCount);

            return mapping.findForward("taskSelectJSP");
        } catch (GranException ge) {
            request.setAttribute("javax.servlet.jsp.jspException", ge);
            return mapping.findForward("error");
        } finally {
            if (w) lockManager.releaseConnection();
        }
    }
}