package com.trackstudio.action.task;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
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
import com.trackstudio.exception.GranException;
import com.trackstudio.form.ReportForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.kernel.cache.UserRelatedInfo;
import com.trackstudio.secured.SecuredFilterBean;
import com.trackstudio.secured.SecuredReportBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredUDFBean;
import com.trackstudio.secured.SecuredUDFValueBean;
import com.trackstudio.secured.SecuredUserBean;
import com.trackstudio.securedkernel.SecuredReportAdapterManager;
import com.trackstudio.startup.Config;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.ExternalAdapterManagerUtil;
import com.trackstudio.tools.Null;
import com.trackstudio.tools.Pair;
import com.trackstudio.view.TaskViewHTMLShort;

import static com.trackstudio.tools.Null.isNull;

public class ReportViewAction extends TSDispatchAction {

    public static class ReportItem {
        private String id;
        private String type;

        private String name;
        private String filter;
        private String connected;
        private SecuredUserBean owner;
        private String xfield;
        private String yfield;
        private String value;
        private String function;
        private String hformat;
        private boolean priv;
        private String linkXsl;
        private boolean hideHandlers;
        private String contentType;
        private String period;
        private boolean workloadProject;
        private String timeType;
        private String split;
        private String reportHandler;

        public String getReportHandler() {
            return reportHandler;
        }

        public void setReportHandler(String reportHandler) {
            this.reportHandler = reportHandler;
        }

        public String getSplit() {
            return split;
        }

        public void setSplit(String split) {
            this.split = split;
        }

        public String getTimeType() {
            return timeType;
        }

        public String getTimeTypeValue() {
            String msg = "";
            if ("submitTime".equals(timeType)) {
                msg = "SUBMIT_DATE";
            } else if ("updateTime".equals(timeType)) {
                msg = "UPDATE_DATE";
            } else if ("closeTime".equals(timeType)) {
                msg = "CLOSE_DATE";
            }
            return msg;
        }

        public void setTimeType(String timeType) {
            this.timeType = timeType;
        }

        public boolean isWorkloadProject() {
            return workloadProject;
        }

        public void setWorkloadProject(boolean workloadProject) {
            this.workloadProject = workloadProject;
        }

        public String getPeriod() {
            return period;
        }

        public void setPeriod(String period) {
            this.period = period;
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public boolean isHideHandlers() {
            return hideHandlers;
        }

        public void setHideHandlers(boolean hideHandlers) {
            this.hideHandlers = hideHandlers;
        }

        public String getLinkXsl() {
            return linkXsl;
        }

        public void setLinkXsl(String linkXsl) {
            this.linkXsl = linkXsl;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String typeName) {
            this.type = typeName;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getConnected() {
            return connected;
        }

        public void setConnected(String connected) {
            this.connected = connected;
        }

        public SecuredUserBean getOwner() {
            return owner;
        }

        public void setOwner(SecuredUserBean owner) {
            this.owner = owner;
        }

        public String getFilter() {
            return filter;
        }

        public void setFilter(String filter) {
            this.filter = filter;
        }

        public boolean isPriv() {
            return priv;
        }

        public void setPriv(boolean priv) {
            this.priv = priv;
        }


        public String getXfield() {
            return xfield;
        }

        public void setXfield(String xfield) {
            this.xfield = xfield;
        }

        public String getYfield() {
            return yfield;
        }

        public void setYfield(String yfield) {
            this.yfield = yfield;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getFunction() {
            return function;
        }

        public void setFunction(String function) {
            this.function = function;
        }

        public String getHformat() {
            return hformat;
        }

        public void setHformat(String hformat) {
            this.hformat = hformat;
        }

        public ReportItem(String id, String type, String name, boolean priv) {
            this.id = id;
            this.type = type;

            this.name = name;
            this.priv = priv;
        }
    }

    private static Log log = LogFactory.getLog(ReportAction.class);
//    private final String htmlPrefix = "<html xmlns=\"http://www.w3.org/1999/xhtml\">";

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            ReportForm bf = (ReportForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String taskId = GeneralAction.getInstance().taskHeader(bf, sc, request, true);
            if (!sc.canAction(Action.viewReports, taskId))
                return null;

            String reportId = bf.getReportId() == null ? request.getParameter("reportId") : bf.getReportId();
            SecuredReportBean bean = AdapterManager.getInstance().getSecuredFindAdapterManager().findReportById(sc, reportId);
            if (bean == null || !bean.canView())
                return null;
            ReportItem report = new ReportItem(bean.getId(), bean.getRtype(), bean.getName(), bean.getPriv());
            report.setOwner(bean.getOwner());
            report.setConnected(new TaskViewHTMLShort(bean.getTask(), request.getContextPath()).getName());
            SecuredFilterBean fltr = bean.getFilter();
            report.setFilter(fltr.getName());

            sc.setRequestAttribute(request, "reportType", bean.getRtypeText());
            ArrayList<SecuredUDFBean> udfHash = new SecuredTaskBean(taskId, sc).getFilterUDFs();

            boolean canView = sc.canAction(Action.viewReports, taskId);
            boolean canDelete = sc.canAction(Action.managePrivateReports, taskId);
            boolean canCreate = sc.canAction(Action.managePrivateReports, taskId);

            FilterSettings flthm = null;
            Object filterObject = sc.getAttribute(reportId + "_reportfilter_" + bf.getFilter());
            String currentField = null;

            if (filterObject != null) {
                flthm = (FilterSettings) filterObject;
                if (!flthm.getFilterId().equals(bean.getFilterId()) || !flthm.getObjectId().equals(taskId)) {
                    flthm = null;
                    currentField = "default";
                    bf.setField(currentField);
                    bf.setOldfield(currentField);
                }
            }
            TaskFValue val = AdapterManager.getInstance().getSecuredReportAdapterManager().getFValue(sc, reportId);
            //TaskFValue val = AdapterManager.getInstance().getSecuredFilterAdapterManager().getTaskFValue(sc, bean.getFilterId()).getFValue();
            FilterSettings originFilterSettings = new FilterSettings(val, taskId, bean.getFilterId());
            if (flthm == null) {
                flthm = originFilterSettings;
            }
            if (isNull(currentField) && flthm.getFieldId() == null) {
                flthm.setFieldId("default");
                currentField = "default";
            } else {
                currentField = flthm.getFieldId();
            }

            bf.setField(currentField);
            //        String useField = currentField;
            String contextPath = request.getContextPath();

            TaskFilterParametersAbstractAction.makeFilterForm(fltr, sc, currentField, request, flthm, contextPath, originFilterSettings, taskId);
            if (currentField != null && currentField.equals(FieldMap.SEARCH.getFilterKey())) {
                bf.setSearch(Null.stripNullText(flthm.getSettings().getAsString(FieldMap.SEARCH.getFilterKey())));
            }
            sc.setAttribute(reportId + "_reportfilter_" + bf.getFilter(), flthm);

            sc.setRequestAttribute(request, "canManageTaskFilters", fltr.canView() && sc.canAction(Action.manageTaskPrivateFilters, taskId));
            sc.setRequestAttribute(request, "defCharSet", "windows-1251");
            sc.setRequestAttribute(request, "currentTab", PageNaming.TAB_TASK_REPORT_OVERVIEW);
            sc.setRequestAttribute(request, "canView", canView);
            sc.setRequestAttribute(request, "canCreate", canCreate);
            sc.setRequestAttribute(request, "canDelete", canDelete);

            sc.setRequestAttribute(request, "charsetList", Config.getInstance().getAllowedEncodings());
            sc.setRequestAttribute(request, "report", report);
            sc.setRequestAttribute(request, "currentReport", bean);
            sc.setRequestAttribute(request, "reportId", reportId);
            sc.setRequestAttribute(request, "pageBreak", true);
            String format = report.getType().equals("Tree") ? "MSProject" : "html";
            String diagramm = report.getType().equals("UserWorkload") || report.getType().equals("Distribution") ? "&diagram=false" : "";
            String encode = report.getType().equals("Tree") ? "&charset=" + Config.getEncoding() : "";
            String hideHandler = report.getType().equals("UserWorkload") ? "&hide_handlers=" + report.isHideHandlers() : "";
            String siteUrl = Config.getInstance().getSiteURL();
            siteUrl = siteUrl.endsWith("/") ? siteUrl : siteUrl + "/";
            String urlReport = siteUrl + "ReportViewAction.do?method=browse&reportId=" + reportId + "&id=" + taskId + "&format=" + format + diagramm + encode + hideHandler;
            if (!UserRelatedInfo.ANONYMOUS_USER.equals(sc.getUser().getLogin())) {
                urlReport += "&autologin=" + sc.getUser().getLogin() + "&autopassword=PASSWORD";
            }
            sc.setRequestAttribute(request, "urlReport", urlReport);
            sc.setRequestAttribute(request, "siteUrl", siteUrl);
            bf.setMethod("browse");
            bf.setFormat("html");
            bf.setOldfield(bf.getField());
            sc.setRequestAttribute(request, "canEdit", sc.canAction(Action.managePrivateReports, bean.getTask().getId()) && bean.canManage());
            selectTaskTab(sc, taskId, "tabReports", request);
            sc.setRequestAttribute(request, "tableTitle", I18n.getString(sc.getLocale(), PageNaming.TAB_TASK_REPORT_OVERVIEW));
            sc.setRequestAttribute(request, "canCreateReport", sc.canAction(Action.managePrivateReports, taskId));
            return mapping.findForward("viewReportJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward changeFilter(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            ReportForm tf = (ReportForm) form;
            change(tf, sc, request);
            return mapping.findForward("viewReportPage");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    private void change(ReportForm tf, SessionContext sc, HttpServletRequest request) throws GranException {
        String currentField = tf.getField();
        String oldField = tf.getOldfield();
        String reportId = tf.getReportId();
        String taskId = tf.getId();
        SecuredReportBean bean = AdapterManager.getInstance().getSecuredFindAdapterManager().findReportById(sc, reportId);
        boolean isReset = request.getParameter("reset") != null;
//        System.out.println(isReset? "reset": "not reset");
        if (isReset || currentField != null && Null.isNotNull(currentField) && currentField.equals("default")) {
            sc.removeAttribute(reportId + "_reportfilter_" + tf.getFilter());
            currentField = "default";
            oldField = "default";
            tf.setField(currentField);
            tf.setOldfield(currentField);

        }
        FilterSettings flthm = null;
        Object filterObject = sc.getAttribute(reportId + "_reportfilter_" + tf.getFilter());
        if (filterObject != null) {
            flthm = (FilterSettings) filterObject;
            if (!flthm.getFilterId().equals(bean.getFilterId())) {
                flthm = null;
                currentField = "default";
                tf.setField(currentField);
                oldField = "default";
                tf.setOldfield(currentField);

            }
        }
        TaskFValue val = AdapterManager.getInstance().getSecuredFilterAdapterManager().getTaskFValue(sc, bean.getFilterId()).getFValue();
        FilterSettings originFilterSettings = new FilterSettings(val, taskId, bean.getFilterId());
        if (flthm == null) {
            flthm = originFilterSettings;
        }
        if (isNull(currentField) && flthm.getFieldId() == null) {
            flthm.setFieldId("default");
        } else if (Null.isNotNull(currentField)) {
            flthm.setFieldId(currentField);
        }
        currentField = flthm.getFieldId();

        SecuredFilterBean fltr = AdapterManager.getInstance().getSecuredFindAdapterManager().findFilterById(sc, flthm.getFilterId());
        changePrefilter(sc, fltr, oldField, request, flthm, taskId);

        if (oldField == null || !oldField.equals(currentField))
            changePrefilter(sc, fltr, currentField, request, flthm, taskId);
//        System.out.println("Current "+currentField+ " "+tf.getField());
//        System.out.println("Old  "+oldField+ " "+tf.getOldfield());

        sc.setAttribute(reportId + "_reportfilter_" + bean.getFilterId(), flthm);
    }

    private void changePrefilter(SessionContext sc, SecuredFilterBean fltr, String currentField, HttpServletRequest request, FilterSettings flthm, String taskId) throws GranException {
        //HashMap<String, String> categoryMap = new HashMap<String, String>();
        //HashMap<String, String> workflowMap = new HashMap<String, String>();
        //ExternalAdapterManagerUtil.fillAvailableCategoryAndWorkflowMaps(sc, taskId, categoryMap, workflowMap);

        if (currentField == null) currentField = "";
        if (currentField.equals(FieldMap.TASK_NUMBER.getFieldKey())) {
            Customizer forNumber = new TextCustomizer(TextCustomizer.NATURAL, FieldMap.TASK_NUMBER);
            forNumber.setFilter(sc, request, flthm.getSettings());
        } // block
        else if (currentField.equals(FieldMap.TASK_PARENT.getFieldKey())) {
	        Customizer forTaskParent = new TextCustomizer(TextCustomizer.NATURAL, FieldMap.TASK_PARENT);
	        forTaskParent.setFilter(sc, request, flthm.getSettings());
        } // block
        else if (currentField.equals(FieldMap.TASK_CATEGORY.getFieldKey())) {
            HashMap<String, String> categoryMap = ExternalAdapterManagerUtil.getAvailableCategoryMap(sc, taskId);
            Customizer forCategory = new ListCustomizer(categoryMap, ListCustomizer.LIST_EQUAL, FieldMap.TASK_CATEGORY, true);
            forCategory.setFilter(sc, request, flthm.getSettings());


        } // block
        else if (currentField.equals(FieldMap.TASK_STATUS.getFieldKey())) {
            HashMap<String, String> workflowMap = ExternalAdapterManagerUtil.getAvailableWorkflowMap(sc, taskId);
            Customizer forStatus = new ListCustomizer(ExternalAdapterManagerUtil.getAvailableStatusMap(sc, workflowMap), ListCustomizer.LIST_EQUAL, FieldMap.TASK_STATUS, true);
            forStatus.setFilter(sc, request, flthm.getSettings());


        } // block
        else if (currentField.equals(FieldMap.TASK_RESOLUTION.getFieldKey())) {
            HashMap<String, String> workflowMap = ExternalAdapterManagerUtil.getAvailableWorkflowMap(sc, taskId);
            HashMap<String, String> resolutionMap = ExternalAdapterManagerUtil.getAvailableResolutionMap(sc, workflowMap);
            resolutionMap.put("null", "-------" + I18n.getString(sc.getLocale(), "NONE") + "-------");
            Customizer forResolution = new ListCustomizer(resolutionMap, ListCustomizer.LIST_EQUAL, FieldMap.TASK_RESOLUTION, true);
            forResolution.setFilter(sc, request, flthm.getSettings());


        } // block
        else if (currentField.equals(FieldMap.SUSER_NAME.getFieldKey())) {
            HashMap handlerUserMap = ExternalAdapterManagerUtil.makeUserMap(sc, flthm.getSettings(), taskId);
            Customizer forSubmitter = new ListCustomizer(handlerUserMap, ListCustomizer.LIST_EQUAL, FieldMap.SUSER_NAME, true);

            forSubmitter.setFilter(sc, request, flthm.getSettings());

        } // block
        else if (currentField.equals(FieldMap.SUSER_STATUS.getFieldKey())) {

            HashMap userStatuses = ExternalAdapterManagerUtil.makeStatusMap(sc, taskId);
            Customizer forSubmitterStatus = new ListCustomizer(userStatuses, ListCustomizer.LIST_EQUAL, FieldMap.SUSER_STATUS, true);
            forSubmitterStatus.setFilter(sc, request, flthm.getSettings());


        } // block
        else if (currentField.equals(FieldMap.HUSER_NAME.getFieldKey())) {
            HashMap<String, String> userStatuses = ExternalAdapterManagerUtil.makeStatusAndUserMap(sc, flthm.getSettings(), taskId);
            userStatuses.put("null", "-------" + I18n.getString(sc.getLocale(), "NONE") + "-------");
            Customizer forHandler = new ListCustomizer(userStatuses, ListCustomizer.LIST_EQUAL, FieldMap.HUSER_NAME, true);
            forHandler.setFilter(sc, request, flthm.getSettings());

        } // block
        else if (currentField.equals(FieldMap.HUSER_STATUS.getFieldKey())) {
            //ArrayList col = AdapterManager.getInstance().getSecuredPrstatusAdapterManager().getAllViewablePrstatuses(sc);
            HashMap<String, String> userStatuses = ExternalAdapterManagerUtil.makeStatusMap(sc, taskId);

            userStatuses.put("null", "-------" + I18n.getString(sc.getLocale(), "NONE") + "-------");
            Customizer forHandlerStatus = new ListCustomizer(userStatuses, ListCustomizer.LIST_EQUAL, FieldMap.HUSER_STATUS, true);
            forHandlerStatus.setFilter(sc, request, flthm.getSettings());


        } // block
        else if (currentField.equals(FieldMap.TASK_DEADLINE.getFieldKey())) {
            Customizer forDeadline = new DateCustomizer(FieldMap.TASK_DEADLINE, sc.getUser().getDateFormatter());
            forDeadline.setFilter(sc, request, flthm.getSettings());

        } // block
        else if (currentField.equals(FieldMap.TASK_SUBMITDATE.getFieldKey())) {
            Customizer forSubmitDate = new DateCustomizer(FieldMap.TASK_SUBMITDATE, sc.getUser().getDateFormatter());
            forSubmitDate.setFilter(sc, request, flthm.getSettings());


        } // block
        else if (currentField.equals(FieldMap.TASK_UPDATEDATE.getFieldKey())) {
            Customizer forUpdatedDate = new DateCustomizer(FieldMap.TASK_UPDATEDATE, sc.getUser().getDateFormatter());

            forUpdatedDate.setFilter(sc, request, flthm.getSettings());


        } // block
        else if (currentField.equals(FieldMap.TASK_CLOSEDATE.getFieldKey())) {
            Customizer forCloseDate = new DateCustomizer(FieldMap.TASK_CLOSEDATE, sc.getUser().getDateFormatter());
            forCloseDate.setFilter(sc, request, flthm.getSettings());

        } // block
        else if (currentField.equals(FieldMap.TASK_BUDGET.getFieldKey())) {
            Customizer forBudget = new BudgetCustomizer(BudgetCustomizer.ALL, FieldMap.TASK_BUDGET);
            forBudget.setFilter(sc, request, flthm.getSettings());

        } // block
        else if (currentField.equals(FieldMap.TASK_ABUDGET.getFieldKey())) {
            Customizer forABudget = new BudgetCustomizer(BudgetCustomizer.ALL, FieldMap.TASK_ABUDGET);
            forABudget.setFilter(sc, request, flthm.getSettings());

        } // block
        else if (currentField.equals(FieldMap.TASK_CHILDCOUNT.getFieldKey())) {
            Customizer forChild = new TextCustomizer(TextCustomizer.NATURAL, FieldMap.TASK_CHILDCOUNT);
            forChild.setFilter(sc, request, flthm.getSettings());

        } // block
        else if (currentField.equals(FieldMap.TASK_MESSAGECOUNT.getFieldKey())) {
            Customizer forMessage = new TextCustomizer(TextCustomizer.NATURAL, FieldMap.TASK_MESSAGECOUNT);
            forMessage.setFilter(sc, request, flthm.getSettings());

        } // block
        else if (currentField.equals(FieldMap.TASK_PRIORITY.getFieldKey())) {
            HashMap<String, String> workflowMap = ExternalAdapterManagerUtil.getAvailableWorkflowMap(sc, taskId);
            Customizer forPriority = new ListCustomizer(ExternalAdapterManagerUtil.getAvailablePriorityMap(sc, workflowMap), ListCustomizer.LIST_EQUAL, FieldMap.TASK_PRIORITY, true);
            forPriority.setFilter(sc, request, flthm.getSettings());

        } // block

        Collection<SecuredUDFValueBean> list = fltr.getTask().getFilterUDFValues();
//        for (Iterator ci = new TreeSet(list).iterator(); ci.hasNext();) {
        for (SecuredUDFValueBean udfvf : new TreeSet<SecuredUDFValueBean>(list)) {
            //cnt++;
            if (currentField.equals(udfvf.getId())) {
                Customizer cust;
                if (udfvf.getUdfType() != UdfValue.USER)
                    cust = udfvf.getCustomizer(udfvf.getId(), false);
                else {
                    HashMap usersMap = ExternalAdapterManagerUtil.makeUserMap(sc, flthm.getSettings(), taskId);
                    FieldMap fm = FieldMap.createUDF(udfvf.getCaption(), FValue.UDF + udfvf.getId(), FValue.UDF_SORT + udfvf.getId());
                    cust = new ListCustomizer(usersMap, ListCustomizer.LIST_EQUAL, fm, true);
                }
                cust.setFilter(sc, request, flthm.getSettings());

            }
        } // for

        // if
        if (currentField.equals(FieldMap.TASK_SHORTNAME.getFieldKey())) {
            Customizer forShortName = new TextCustomizer(TextCustomizer.CHARS, FieldMap.TASK_SHORTNAME);
            forShortName.setFilter(sc, request, flthm.getSettings());


        } // block
        if (currentField.equals(FieldMap.TASK_NAME.getFieldKey())) {
            Customizer forName = new TextCustomizer(TextCustomizer.CHARS, FieldMap.TASK_NAME);
            forName.setFilter(sc, request, flthm.getSettings());

        } // block
        else if (currentField.equals(FieldMap.TASK_DESCRIPTION.getFieldKey())) {
            Customizer forDescription = new TextCustomizer(TextCustomizer.CHARS, FieldMap.TASK_DESCRIPTION);
            forDescription.setFilter(sc, request, flthm.getSettings());

        } // block
        else if (currentField.equals(FieldMap.MSG_SUSER_NAME.getFieldKey())) {
            HashMap usersMap = ExternalAdapterManagerUtil.makeUserMap(sc, flthm.getSettings(), taskId);
            Customizer forMsgSubmitter = new ListCustomizer(usersMap, ListCustomizer.LIST_EQUAL, FieldMap.MSG_SUSER_NAME, true);
            forMsgSubmitter.setFilter(sc, request, flthm.getSettings());

        } // block
        else if (currentField.equals(FieldMap.MSG_SUBMITDATE.getFieldKey())) {
            Customizer forMsgDate = new DateCustomizer(FieldMap.MSG_SUBMITDATE, sc.getUser().getDateFormatter());
            forMsgDate.setFilter(sc, request, flthm.getSettings());

        } // block
        else if (currentField.equals(FieldMap.LAST_MSG_SUBMITDATE.getFieldKey())) {
            Customizer forLastMsgDate = new DateCustomizer(FieldMap.LAST_MSG_SUBMITDATE, sc.getUser().getDateFormatter());
            forLastMsgDate.setFilter(sc, request, flthm.getSettings());

        }
        else if (currentField.equals(FieldMap.MSG_MSTATUS.getFieldKey())) {
            HashMap<String, String> workflowMap = ExternalAdapterManagerUtil.getAvailableWorkflowMap(sc, taskId);
            Customizer forMsgMstatus = new ListCustomizer(ExternalAdapterManagerUtil.getAvailableMstatusMap(sc, workflowMap), ListCustomizer.LIST_EQUAL, FieldMap.MSG_MSTATUS, true);
            forMsgMstatus.setFilter(sc, request, flthm.getSettings());

        } // block
        else if (currentField.equals(FieldMap.MSG_HUSER_NAME.getFieldKey())) {
            HashMap<String, String> handlerUserMap = ExternalAdapterManagerUtil.makeStatusAndUserMap(sc, flthm.getSettings(), taskId);
            handlerUserMap.put("null", "-------" + I18n.getString(sc.getLocale(), "NONE") + "-------");
            Customizer forMsgHandler = new ListCustomizer(handlerUserMap, ListCustomizer.LIST_EQUAL, FieldMap.MSG_HUSER_NAME, true);
            forMsgHandler.setFilter(sc, request, flthm.getSettings());

        } // block
        else if (currentField.equals(FieldMap.MSG_RESOLUTION.getFieldKey())) {
            HashMap<String, String> workflowMap = ExternalAdapterManagerUtil.getAvailableWorkflowMap(sc, taskId);
            HashMap<String, String> resolutionMap = ExternalAdapterManagerUtil.getAvailableResolutionMap(sc, workflowMap);
            resolutionMap.put("null", "-------" + I18n.getString(sc.getLocale(), "NONE") + "-------");
            Customizer forMsgResolution = new ListCustomizer(resolutionMap, ListCustomizer.LIST_EQUAL, FieldMap.MSG_RESOLUTION, true);
            forMsgResolution.setFilter(sc, request, flthm.getSettings());

        } // block
        else if (currentField.equals(FieldMap.MSG_ABUDGET.getFieldKey())) {
            Customizer forMsgBudget = new BudgetCustomizer(BudgetCustomizer.ALL, FieldMap.MSG_ABUDGET);

            forMsgBudget.setFilter(sc, request, flthm.getSettings());

        } // block
        else if (currentField.equals(FieldMap.TEXT_MSG.getFieldKey())) {
            Customizer forMsgText = new TextCustomizer(TextCustomizer.CHARS, FieldMap.TEXT_MSG);

            forMsgText.setFilter(sc, request, flthm.getSettings());

        }
        if (currentField.equals(FieldMap.SEARCH.getFieldKey())) {
            Customizer forKeyword = new TextCustomizer(TextCustomizer.CHARS, FieldMap.SEARCH);
            forKeyword.setFilter(sc, request, flthm.getSettings());

        }
    }

    public ActionForward browse(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            String contextPath = request.getContextPath();
            ReportForm tf = (ReportForm) form;
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            String id = tf.getId();
            String reportId = tf.getReportId();
            SecuredReportBean bean = AdapterManager.getInstance().getSecuredFindAdapterManager().findReportById(sc, reportId);
            // do not need?
            //change(tf, sc, request);

            String fileName = URLEncoder.encode(bean.getName().replace(' ', '_'), "UTF-8");
            if (tf.getZipped() != null) {
                response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
                String filterId;
                if (sc.getAttribute(tf.getReportId() + "_reportfilter_" + tf.getFilter()) != null) {
                    filterId = ((FilterSettings) sc.getAttribute(tf.getReportId() + "_reportfilter_" + tf.getFilter())).getFilterId();
                } else if (request.getParameter("reportId") != null) {
                    filterId = request.getParameter("reportId");
                } else {
                    return null;
                }
                String url = contextPath + "/DownloadReportAction.do?method=report&id=" + id + "&reportId=" + tf.getReportId() + "&format=" + tf.getFormat() + "&delimiter=" + tf.getDelimiter() +
                        "&name=" + fileName + "&type=" + tf.getType() + "&filter=" + filterId + "&oldfield=" + tf.getOldfield() + "&charset=" + tf.getCharset() + "&zipped=" + tf.getZipped();
                response.sendRedirect(url);
                sc.setRequestAttribute(request, "startTime", null);
                sc.setAttribute("repName", bean.getName());
                return null;
            }

            response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
            String url = contextPath + "/birt?repType=" +
                    tf.getFormat() + "&taskId=" + id + "&repId=" + tf.getReportId() + "&charset=" + tf.getCharset() + "&delimiter=" + tf.getDelimiter();
            response.sendRedirect(url);
            sc.setAttribute("repName", bean.getName());
            request.setAttribute("sc", sc);
            sc.setRequestAttribute(request, "startTime", null);
            return null;
        } finally {
            if (w) lockManager.releaseConnection();
        }


    }
}
