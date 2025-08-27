package com.trackstudio.action.task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.trackstudio.action.TSDispatchAction;
import com.trackstudio.app.UdfValue;
import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.filter.FValue;
import com.trackstudio.app.filter.FilterSettings;
import com.trackstudio.app.filter.TaskFValue;
import com.trackstudio.app.filter.customizer.BudgetCustomizer;
import com.trackstudio.app.filter.customizer.CheckboxCustomizer;
import com.trackstudio.app.filter.customizer.Customizer;
import com.trackstudio.app.filter.customizer.DateCustomizer;
import com.trackstudio.app.filter.customizer.ListCustomizer;
import com.trackstudio.app.filter.customizer.TextCustomizer;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.common.FieldMap;
import com.trackstudio.exception.GranException;
import com.trackstudio.form.PreFilterForm;
import com.trackstudio.kernel.cache.Action;
import com.trackstudio.secured.SecuredFilterBean;
import com.trackstudio.secured.SecuredTaskBean;
import com.trackstudio.secured.SecuredTaskFValueBean;
import com.trackstudio.secured.SecuredUDFValueBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.ExternalAdapterManagerUtil;
import com.trackstudio.tools.Null;
import com.trackstudio.tools.Pair;
import com.trackstudio.tools.ParameterValidator;
import com.trackstudio.tools.compare.FieldSort;
import com.trackstudio.tools.compare.SortUdf;

import static com.trackstudio.tools.Null.isNotNull;
import static com.trackstudio.tools.ParameterValidator.badSmallDesc;

public abstract class TaskFilterParametersAbstractAction extends TSDispatchAction {
    protected String filterId;
    protected String id;
    protected String filterParameter;
    protected String forward;
    protected String action;
    private static ParameterValidator pv = new ParameterValidator();
    private static boolean delete;

    public ActionForward page(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            PreFilterForm bf = (PreFilterForm) form;
            String taskId = bf.getId();
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            FilterSettings flthm = null;
            Object filterObject = sc.getAttribute(filterParameter);
            if (filterObject != null) {
                flthm = (FilterSettings) filterObject;
            }
            if (flthm == null) {
                String filterid = AdapterManager.getInstance().getSecuredFilterAdapterManager().getCurrentTaskFilterId(sc, id);
                TaskFValue val = AdapterManager.getInstance().getSecuredFilterAdapterManager().getTaskFValue(sc, filterid).getFValue();
                flthm = new FilterSettings(val, id, filterid);
            }
            if (flthm.getFieldId() == null)
                flthm.setFieldId("default");
            String currentField = flthm.getFieldId();
            bf.setField(currentField);
            bf.setOldfield(currentField);
            bf.setFilter(filterId);

            SecuredFilterBean fltr = AdapterManager.getInstance().getSecuredFindAdapterManager().findFilterById(sc, filterId);

            sc.setRequestAttribute(request, "filterId", fltr.getId());
            sc.setRequestAttribute(request, "filterName", fltr.getName());
            String contextPath = request.getContextPath();


            TaskFValue val = (TaskFValue) flthm.getSettings();
            FilterSettings originFilterSettings = new FilterSettings(val, id, flthm.getFilterId());
            HashMap<String, ArrayList> map = TaskFilterViewAction.makeFilterView(fltr, flthm.getSettings(), id, request.getContextPath());
            for (String key : map.keySet()) {
                ArrayList value = map.get(key);
                sc.setRequestAttribute(request, key, value);
            }
            makeFilterForm(fltr, sc, currentField, request, flthm, contextPath, originFilterSettings, taskId);
            if (currentField.equals(FieldMap.SEARCH.getFieldKey())) {
                bf.setSearch(Null.stripNullText(flthm.getSettings().getAsString(FieldMap.SEARCH.getFilterKey())));
            }

            sc.setRequestAttribute(request, "canManageTaskFilters", fltr.canView() && sc.canAction(Action.manageTaskPrivateFilters, id));
            sc.setRequestAttribute(request, "tileId", "taskFilterParams");

            return mapping.findForward("taskFilterParametersTileJSP");
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public static void makeFilterForm(SecuredFilterBean fltr, SessionContext sc, String currentField, HttpServletRequest request, FilterSettings flthm, String contextPath, FilterSettings originFilterSettings, String taskId) throws GranException {
        List<SecuredUDFValueBean> list = new ArrayList<SecuredUDFValueBean>(new SecuredTaskBean(taskId, sc).getFilterUDFValues());
        if (currentField != null) {
            if (currentField.equals(FieldMap.DEEP_SEARCH.getFieldKey())) {
                Customizer forNumber = new CheckboxCustomizer(FieldMap.DEEP_SEARCH);
                sc.setRequestAttribute(request, "customizer", forNumber.drawInput(sc, flthm.getSettings(), contextPath));
            }
            if (currentField.equals(FieldMap.TASK_NUMBER.getFieldKey())) {
                Customizer forNumber = new TextCustomizer(TextCustomizer.NATURAL, FieldMap.TASK_NUMBER);
                sc.setRequestAttribute(request, "customizer", forNumber.drawInput(sc, flthm.getSettings(), contextPath));
            } // block
            else if (currentField.equals(FieldMap.TASK_CATEGORY.getFieldKey())) {
                HashMap<String, String> categoryMap = ExternalAdapterManagerUtil.getAvailableCategoryMap(sc, taskId);
                Customizer forCategory = new ListCustomizer(categoryMap, ListCustomizer.LIST_EQUAL, FieldMap.TASK_CATEGORY, true);
                sc.setRequestAttribute(request, "customizer", forCategory.drawInput(sc, flthm.getSettings(), contextPath));

            } // block
            else if (currentField.equals(FieldMap.TASK_STATUS.getFieldKey())) {
                HashMap<String, String> workflowMap = ExternalAdapterManagerUtil.getAvailableWorkflowMap(sc, taskId);
                Customizer forStatus = new ListCustomizer(ExternalAdapterManagerUtil.getAvailableStatusMap(sc, workflowMap), ListCustomizer.LIST_EQUAL, FieldMap.TASK_STATUS, true);
                sc.setRequestAttribute(request, "customizer", forStatus.drawInput(sc, flthm.getSettings(), contextPath));

            } // block
            else if (currentField.equals(FieldMap.TASK_RESOLUTION.getFieldKey())) {
                HashMap<String, String> workflowMap = ExternalAdapterManagerUtil.getAvailableWorkflowMap(sc, taskId);
                HashMap<String, String> resolutionMap = ExternalAdapterManagerUtil.getAvailableResolutionMap(sc, workflowMap);
                resolutionMap.put("null", "-------" + I18n.getString(sc.getLocale(), "NONE") + "-------");
                Customizer forResolution = new ListCustomizer(resolutionMap, ListCustomizer.LIST_EQUAL, FieldMap.TASK_RESOLUTION, true);
                sc.setRequestAttribute(request, "customizer", forResolution.drawInput(sc, flthm.getSettings(), contextPath));

            } // block
            else if (currentField.equals(FieldMap.SUSER_NAME.getFieldKey())) {
                HashMap usersMap = ExternalAdapterManagerUtil.makeUserMap(sc, flthm.getSettings(), taskId);
                Customizer forSubmitter = new ListCustomizer(usersMap, ListCustomizer.LIST_EQUAL, FieldMap.SUSER_NAME, true);
                ((ListCustomizer) forSubmitter).setOriginFilter(originFilterSettings.getSettings());
                sc.setRequestAttribute(request, "customizer", forSubmitter.drawInput(sc, flthm.getSettings(), contextPath));

            } // block
            else if (currentField.equals(FieldMap.SUSER_STATUS.getFieldKey())) {
                HashMap userStatuses = ExternalAdapterManagerUtil.makeStatusMap(sc, taskId);
                Customizer forSubmitterStatus = new ListCustomizer(userStatuses, ListCustomizer.LIST_EQUAL, FieldMap.SUSER_STATUS, true);
                sc.setRequestAttribute(request, "customizer", forSubmitterStatus.drawInput(sc, flthm.getSettings(), contextPath));

            } // block
            else if (currentField.equals(FieldMap.HUSER_NAME.getFieldKey())) {
                HashMap<String, String> handlerUserMap = ExternalAdapterManagerUtil.makeStatusAndUserMap(sc, flthm.getSettings(), taskId);
                handlerUserMap.put("null", "-------" + I18n.getString(sc.getLocale(), "NONE") + "-------");
                Customizer forHandler = new ListCustomizer(handlerUserMap, ListCustomizer.LIST_EQUAL, FieldMap.HUSER_NAME, true);
                ((ListCustomizer) forHandler).setOriginFilter(originFilterSettings.getSettings());
                sc.setRequestAttribute(request, "customizer", forHandler.drawInput(sc, flthm.getSettings(), contextPath));

            } // block
            else if (currentField.equals(FieldMap.HUSER_STATUS.getFieldKey())) {

                HashMap<String, String> userStatuses = ExternalAdapterManagerUtil.makeStatusMap(sc, taskId);

                userStatuses.put("null", "-------" + I18n.getString(sc.getLocale(), "NONE") + "-------");
                Customizer forHandlerStatus = new ListCustomizer(userStatuses, ListCustomizer.LIST_EQUAL, FieldMap.HUSER_STATUS, true);
                sc.setRequestAttribute(request, "customizer", forHandlerStatus.drawInput(sc, flthm.getSettings(), contextPath));

            } // block
            else if (currentField.equals(FieldMap.TASK_DEADLINE.getFieldKey())) {
                Customizer forDeadline = new DateCustomizer(FieldMap.TASK_DEADLINE, sc.getUser().getDateFormatter());
                sc.setRequestAttribute(request, "customizer", forDeadline.drawInput(sc, flthm.getSettings(), contextPath));

            } // block
            else if (currentField.equals(FieldMap.TASK_SUBMITDATE.getFieldKey())) {
                Customizer forSubmitDate = new DateCustomizer(FieldMap.TASK_SUBMITDATE, sc.getUser().getDateFormatter());
                sc.setRequestAttribute(request, "customizer", forSubmitDate.drawInput(sc, flthm.getSettings(), contextPath));

            } // block
            else if (currentField.equals(FieldMap.TASK_UPDATEDATE.getFieldKey())) {
                Customizer forUpdatedDate = new DateCustomizer(FieldMap.TASK_UPDATEDATE, sc.getUser().getDateFormatter());
                sc.setRequestAttribute(request, "customizer", forUpdatedDate.drawInput(sc, flthm.getSettings(), contextPath));
            } // block
            else if (currentField.equals(FieldMap.TASK_CLOSEDATE.getFieldKey())) {
                Customizer forCloseDate = new DateCustomizer(FieldMap.TASK_CLOSEDATE, sc.getUser().getDateFormatter());
                sc.setRequestAttribute(request, "customizer", forCloseDate.drawInput(sc, flthm.getSettings(), contextPath));
            } // block
            else if (currentField.equals(FieldMap.TASK_BUDGET.getFieldKey())) {
                Customizer forBudget = new BudgetCustomizer(BudgetCustomizer.ALL, FieldMap.TASK_BUDGET);
                sc.setRequestAttribute(request, "customizer", forBudget.drawInput(sc, flthm.getSettings(), contextPath));
            } // block
            else if (currentField.equals(FieldMap.TASK_ABUDGET.getFieldKey())) {
                Customizer forABudget = new BudgetCustomizer(BudgetCustomizer.ALL, FieldMap.TASK_ABUDGET);
                sc.setRequestAttribute(request, "customizer", forABudget.drawInput(sc, flthm.getSettings(), contextPath));
            } // block
            else if (currentField.equals(FieldMap.TASK_CHILDCOUNT.getFieldKey())) {
                Customizer forChild = new TextCustomizer(TextCustomizer.NATURAL, FieldMap.TASK_CHILDCOUNT);
                sc.setRequestAttribute(request, "customizer", forChild.drawInput(sc, flthm.getSettings(), contextPath));
            } // block
            else if (currentField.equals(FieldMap.TASK_MESSAGECOUNT.getFieldKey())) {
                Customizer forMessage = new TextCustomizer(TextCustomizer.NATURAL, FieldMap.TASK_MESSAGECOUNT);
                sc.setRequestAttribute(request, "customizer", forMessage.drawInput(sc, flthm.getSettings(), contextPath));
            } // block
            else if (currentField.equals(FieldMap.TASK_PRIORITY.getFieldKey())) {
                HashMap<String, String> workflowMap = ExternalAdapterManagerUtil.getAvailableWorkflowMap(sc, taskId);
                Customizer forPriority = new ListCustomizer(ExternalAdapterManagerUtil.getAvailablePriorityMap(sc, workflowMap), ListCustomizer.LIST_EQUAL, FieldMap.TASK_PRIORITY, true);
                sc.setRequestAttribute(request, "customizer", forPriority.drawInput(sc, flthm.getSettings(), contextPath));
            } // block
            Collections.sort(list, new SortUdf(FieldSort.NAME));
            for (Object o : new TreeSet<SecuredUDFValueBean>(list)) {
                SecuredUDFValueBean udfvf = (SecuredUDFValueBean) o;
                //cnt++;
                if (currentField.equals(udfvf.getId())) {
                    Customizer cust;
                    if (udfvf.getUdfType() != UdfValue.USER)
                        cust = udfvf.getCustomizer(udfvf.getId(), request.getAttribute("hidePopups") != null);
                    else {
                        HashMap usersMap = ExternalAdapterManagerUtil.makeUserMap(sc, flthm.getSettings(), taskId);
                        FieldMap fm = FieldMap.createUDF(udfvf.getCaption(), FValue.UDF + udfvf.getId(), FValue.UDF_SORT + udfvf.getId());
                        cust = new ListCustomizer(usersMap, ListCustomizer.LIST_EQUAL, fm, true);
                    }

                    if (udfvf.isLookup() && cust instanceof TextCustomizer)
                        ((TextCustomizer) cust).setSecured(fltr.getTask());
                    sc.setRequestAttribute(request, "customizer", cust.drawInput(sc, flthm.getSettings(), contextPath));
                    break;
                }
            } // for

            // if
            if (currentField.equals(FieldMap.TASK_SHORTNAME.getFieldKey())) {
                Customizer forShortName = new TextCustomizer(TextCustomizer.CHARS, FieldMap.TASK_SHORTNAME);
                sc.setRequestAttribute(request, "customizer", forShortName.drawInput(sc, flthm.getSettings(), contextPath));
            } // block
            if (currentField.equals(FieldMap.TASK_NAME.getFieldKey())) {
                Customizer forName = new TextCustomizer(TextCustomizer.CHARS, FieldMap.TASK_NAME);
                sc.setRequestAttribute(request, "customizer", forName.drawInput(sc, flthm.getSettings(), contextPath));
            } // block
	        if (currentField.equals(FieldMap.TASK_PARENT.getFieldKey())) {
		        Customizer forTaskParent = new TextCustomizer(TextCustomizer.CHARS, FieldMap.TASK_PARENT);
		        sc.setRequestAttribute(request, "customizer", forTaskParent.drawInput(sc, flthm.getSettings(), contextPath));
	        } // block
            else if (currentField.equals(FieldMap.TASK_DESCRIPTION.getFieldKey())) {
                Customizer forDescription = new TextCustomizer(TextCustomizer.CHARS, FieldMap.TASK_DESCRIPTION);
                sc.setRequestAttribute(request, "customizer", forDescription.drawInput(sc, flthm.getSettings(), contextPath));
            } // block
            else if (currentField.equals(FieldMap.MSG_SUSER_NAME.getFieldKey())) {
                HashMap usersMap = ExternalAdapterManagerUtil.makeUserMap(sc, flthm.getSettings(), taskId);

                Customizer forMsgSubmitter = new ListCustomizer(usersMap, ListCustomizer.LIST_EQUAL, FieldMap.MSG_SUSER_NAME, true);
                ((ListCustomizer) forMsgSubmitter).setOriginFilter(originFilterSettings.getSettings());
                sc.setRequestAttribute(request, "customizer", forMsgSubmitter.drawInput(sc, flthm.getSettings(), contextPath));
            } // block
            else if (currentField.equals(FieldMap.MSG_SUBMITDATE.getFieldKey())) {
                Customizer forMsgDate = new DateCustomizer(FieldMap.MSG_SUBMITDATE, sc.getUser().getDateFormatter());
                sc.setRequestAttribute(request, "customizer", forMsgDate.drawInput(sc, flthm.getSettings(), contextPath));
            } // block
            else if (currentField.equals(FieldMap.LAST_MSG_SUBMITDATE.getFieldKey())) {
                Customizer forMsgDate = new DateCustomizer(FieldMap.LAST_MSG_SUBMITDATE, sc.getUser().getDateFormatter());
                sc.setRequestAttribute(request, "customizer", forMsgDate.drawInput(sc, flthm.getSettings(), contextPath));
            } // block
            else if (currentField.equals(FieldMap.MSG_MSTATUS.getFieldKey())) {
                HashMap<String, String> workflowMap = ExternalAdapterManagerUtil.getAvailableWorkflowMap(sc, taskId);
                Customizer forMsgMstatus = new ListCustomizer(ExternalAdapterManagerUtil.getAvailableMstatusMap(sc, workflowMap), ListCustomizer.LIST_EQUAL, FieldMap.MSG_MSTATUS, true);
                sc.setRequestAttribute(request, "customizer", forMsgMstatus.drawInput(sc, flthm.getSettings(), contextPath));
            } // block
            else if (currentField.equals(FieldMap.MSG_HUSER_NAME.getFieldKey())) {
                HashMap<String, String> handlerUserMap = ExternalAdapterManagerUtil.makeStatusAndUserMap(sc, flthm.getSettings(), taskId);
                handlerUserMap.put("null", "-------" + I18n.getString(sc.getLocale(), "NONE") + "-------");
                Customizer forMsgHandler = new ListCustomizer(handlerUserMap, ListCustomizer.LIST_EQUAL, FieldMap.MSG_HUSER_NAME, true);
                ((ListCustomizer) forMsgHandler).setOriginFilter(originFilterSettings.getSettings());
                sc.setRequestAttribute(request, "customizer", forMsgHandler.drawInput(sc, flthm.getSettings(), contextPath));

            } // block
            else if (currentField.equals(FieldMap.MSG_RESOLUTION.getFieldKey())) {
                HashMap<String, String> workflowMap = ExternalAdapterManagerUtil.getAvailableWorkflowMap(sc, taskId);
                HashMap<String, String> resolutionMap = ExternalAdapterManagerUtil.getAvailableResolutionMap(sc, workflowMap);
                resolutionMap.put("null", "-------" + I18n.getString(sc.getLocale(), "NONE") + "-------");

                Customizer forMsgResolution = new ListCustomizer(resolutionMap, ListCustomizer.LIST_EQUAL, FieldMap.MSG_RESOLUTION, true);
                sc.setRequestAttribute(request, "customizer", forMsgResolution.drawInput(sc, flthm.getSettings(), contextPath));
            } // block
            else if (currentField.equals(FieldMap.MSG_ABUDGET.getFieldKey())) {
                Customizer forMsgBudget = new BudgetCustomizer(BudgetCustomizer.ALL, FieldMap.MSG_ABUDGET);
                sc.setRequestAttribute(request, "customizer", forMsgBudget.drawInput(sc, flthm.getSettings(), contextPath));
            } // block

            else if (currentField.equals(FieldMap.TEXT_MSG.getFieldKey())) {
                Customizer forMsgText = new TextCustomizer(TextCustomizer.CHARS, FieldMap.TEXT_MSG);
                sc.setRequestAttribute(request, "customizer", forMsgText.drawInput(sc, flthm.getSettings(), contextPath));
            }
        }
        ArrayList<Pair> taskFieldSet = new ArrayList<Pair>();
        taskFieldSet.add(new Pair(FieldMap.TASK_NUMBER.getFieldKey(), I18n.getString(sc, "TASK_NUMBER")));
        taskFieldSet.add(new Pair(FieldMap.TASK_NAME.getFieldKey(), I18n.getString(sc, "NAME")));
	    taskFieldSet.add(new Pair(FieldMap.TASK_PARENT.getFieldKey(), I18n.getString(sc, "TASK_PARENT")));
        taskFieldSet.add(new Pair(FieldMap.TASK_SHORTNAME.getFieldKey(), I18n.getString(sc, "ALIAS")));
        taskFieldSet.add(new Pair(FieldMap.TASK_CATEGORY.getFieldKey(), I18n.getString(sc, "CATEGORY")));
        taskFieldSet.add(new Pair(FieldMap.TASK_STATUS.getFieldKey(), I18n.getString(sc, "TASK_STATE")));
        taskFieldSet.add(new Pair(FieldMap.TASK_RESOLUTION.getFieldKey(), I18n.getString(sc, "RESOLUTION")));
        taskFieldSet.add(new Pair(FieldMap.TASK_PRIORITY.getFieldKey(), I18n.getString(sc, "PRIORITY")));
        taskFieldSet.add(new Pair(FieldMap.SUSER_NAME.getFieldKey(), I18n.getString(sc, "SUBMITTER")));
        taskFieldSet.add(new Pair(FieldMap.SUSER_STATUS.getFieldKey(), I18n.getString(sc, "SUBMITTER_STATUS")));
        taskFieldSet.add(new Pair(FieldMap.HUSER_NAME.getFieldKey(), I18n.getString(sc, "HANDLER")));
        taskFieldSet.add(new Pair(FieldMap.HUSER_STATUS.getFieldKey(), I18n.getString(sc, "HANDLER_STATUS")));
        taskFieldSet.add(new Pair(FieldMap.TASK_SUBMITDATE.getFieldKey(), I18n.getString(sc, "SUBMIT_DATE")));
        taskFieldSet.add(new Pair(FieldMap.TASK_UPDATEDATE.getFieldKey(), I18n.getString(sc, "UPDATE_DATE")));
        taskFieldSet.add(new Pair(FieldMap.TASK_CLOSEDATE.getFieldKey(), I18n.getString(sc, "CLOSE_DATE")));
        taskFieldSet.add(new Pair(FieldMap.TASK_DEADLINE.getFieldKey(), I18n.getString(sc, "DEADLINE")));
        taskFieldSet.add(new Pair(FieldMap.TASK_BUDGET.getFieldKey(), I18n.getString(sc, "BUDGET")));
        taskFieldSet.add(new Pair(FieldMap.TASK_ABUDGET.getFieldKey(), I18n.getString(sc, "ABUDGET")));
        taskFieldSet.add(new Pair(FieldMap.TASK_CHILDCOUNT.getFieldKey(), I18n.getString(sc, "SUBTASKS_AMOUNT")));
        taskFieldSet.add(new Pair(FieldMap.TASK_MESSAGECOUNT.getFieldKey(), I18n.getString(sc, "MESSAGES_AMOUNT")));
        // Collections.sort(list);
        ArrayList<Pair> udfs = new ArrayList<Pair>();
        for (SecuredUDFValueBean bean : list) {
            udfs.add(new Pair(bean.getId(), bean.getCaptionEx()));
        }
        Collections.sort(udfs);
        taskFieldSet.addAll(udfs);
        taskFieldSet.add(new Pair(FieldMap.TASK_DESCRIPTION.getFieldKey(), I18n.getString(sc, "DESCRIPTION")));
        ArrayList<Pair> messageFieldSet = new ArrayList<Pair>();
        messageFieldSet.add(new Pair(FieldMap.MSG_SUSER_NAME.getFieldKey(), I18n.getString(sc, "MESSAGE_SUBMITTER")));
        messageFieldSet.add(new Pair(FieldMap.MSG_SUBMITDATE.getFieldKey(), I18n.getString(sc, "MESSAGE_DATE")));
        messageFieldSet.add(new Pair(FieldMap.LAST_MSG_SUBMITDATE.getFieldKey(), I18n.getString(sc, "LAST_MESSAGE_DATE")));
        messageFieldSet.add(new Pair(FieldMap.MSG_MSTATUS.getFieldKey(), I18n.getString(sc, "MESSAGE_TYPE")));
        messageFieldSet.add(new Pair(FieldMap.MSG_HUSER_NAME.getFieldKey(), I18n.getString(sc, "MESSAGE_HANDLER")));
        messageFieldSet.add(new Pair(FieldMap.MSG_RESOLUTION.getFieldKey(), I18n.getString(sc, "MESSAGE_RESOLUTION")));
        messageFieldSet.add(new Pair(FieldMap.MSG_ABUDGET.getFieldKey(), I18n.getString(sc, "MESSAGE_ABUDGET")));
        messageFieldSet.add(new Pair(FieldMap.TEXT_MSG.getFieldKey(), I18n.getString(sc, "MESSAGE_DESCRIPTION")));
        Collections.sort(messageFieldSet);
        sc.setRequestAttribute(request, "taskSet", taskFieldSet);
        sc.setRequestAttribute(request, "messageSet", messageFieldSet);
    }

    private void changePreFilter(SessionContext sc, HttpServletRequest request, FilterSettings flthm, FilterSettings originalFilterSettings, String field, String oldField, String taskId, boolean fill) throws GranException {
        SecuredFilterBean fltr = AdapterManager.getInstance().getSecuredFindAdapterManager().findFilterById(sc, flthm.getFilterId());


        Collection list = new SecuredTaskBean(taskId, sc).getFilterUDFValues();
        combineFilterSettings(field, originalFilterSettings.getSettings(), flthm.getSettings());
        if (oldField.equals(FieldMap.TASK_NUMBER.getFieldKey()) || field.equals(FieldMap.TASK_NUMBER.getFieldKey())) {
            Customizer forNumber = new TextCustomizer(TextCustomizer.NATURAL, FieldMap.TASK_NUMBER);
            if (fill) forNumber.setFilter(sc, request, flthm.getSettings());


        } // block
        else if (oldField.equals(FieldMap.TASK_PARENT.getFieldKey()) || field.equals(FieldMap.TASK_PARENT.getFieldKey())) {

	        Customizer forTaskParent = new TextCustomizer(TextCustomizer.NATURAL, FieldMap.TASK_PARENT);
	        if (fill) forTaskParent.setFilter(sc, request, flthm.getSettings());


        } // block
        else if (oldField.equals(FieldMap.TASK_CATEGORY.getFieldKey()) || field.equals(FieldMap.TASK_CATEGORY.getFieldKey())) {

            HashMap<String, String> categoryMap = ExternalAdapterManagerUtil.getAvailableCategoryMap(sc, taskId);
	        Customizer forCategory = new ListCustomizer(categoryMap, ListCustomizer.LIST_EQUAL, FieldMap.TASK_CATEGORY, true);
            if (fill) forCategory.setFilter(sc, request, flthm.getSettings());


        } // block
        else if (oldField.equals(FieldMap.TASK_STATUS.getFieldKey()) || field.equals(FieldMap.TASK_STATUS.getFieldKey())) {
            HashMap<String, String> workflowMap = ExternalAdapterManagerUtil.getAvailableWorkflowMap(sc, taskId);
	        Customizer forStatus = new ListCustomizer(ExternalAdapterManagerUtil.getAvailableStatusMap(sc, workflowMap), ListCustomizer.LIST_EQUAL, FieldMap.TASK_STATUS, true);
            if (fill) forStatus.setFilter(sc, request, flthm.getSettings());


        } // block
        else if (oldField.equals(FieldMap.TASK_RESOLUTION.getFieldKey()) || field.equals(FieldMap.TASK_RESOLUTION.getFieldKey())) {
            HashMap<String, String> workflowMap = ExternalAdapterManagerUtil.getAvailableWorkflowMap(sc, taskId);
            HashMap<String, String> resolutionMap = ExternalAdapterManagerUtil.getAvailableResolutionMap(sc, workflowMap);
	        resolutionMap.put("null", "-------" + I18n.getString(sc.getLocale(), "NONE") + "-------");
            Customizer forResolution = new ListCustomizer(resolutionMap, ListCustomizer.LIST_EQUAL, FieldMap.TASK_RESOLUTION, true);
            if (fill) forResolution.setFilter(sc, request, flthm.getSettings());


        } // block
        else if (oldField.equals(FieldMap.SUSER_NAME.getFieldKey()) || field.equals(FieldMap.SUSER_NAME.getFieldKey())) {
            HashMap usersMap = ExternalAdapterManagerUtil.makeUserMap(sc, flthm.getSettings(), taskId);


	        Customizer forSubmitter = new ListCustomizer(usersMap, ListCustomizer.LIST_EQUAL, FieldMap.SUSER_NAME, true);
            if (fill) forSubmitter.setFilter(sc, request, flthm.getSettings());

        } // block
        else if (oldField.equals(FieldMap.SUSER_STATUS.getFieldKey()) || field.equals(FieldMap.SUSER_STATUS.getFieldKey())) {
            HashMap userStatuses = ExternalAdapterManagerUtil.makeStatusMap(sc, taskId);
            Customizer forSubmitterStatus = new ListCustomizer(userStatuses, ListCustomizer.LIST_EQUAL, FieldMap.SUSER_STATUS, true);

            if (fill) forSubmitterStatus.setFilter(sc, request, flthm.getSettings());


        } // block
        else if (oldField.equals(FieldMap.HUSER_NAME.getFieldKey()) || field.equals(FieldMap.HUSER_NAME.getFieldKey())) {
            HashMap<String, String> handlerUserMap = ExternalAdapterManagerUtil.makeStatusAndUserMap(sc, flthm.getSettings(), taskId);
            handlerUserMap.put("null", "-------" + I18n.getString(sc.getLocale(), "NONE") + "-------");
            Customizer forHandler = new ListCustomizer(handlerUserMap, ListCustomizer.LIST_EQUAL, FieldMap.HUSER_NAME, true);
            if (fill) forHandler.setFilter(sc, request, flthm.getSettings());

        } // block
        else if (oldField.equals(FieldMap.HUSER_STATUS.getFieldKey()) || field.equals(FieldMap.HUSER_STATUS.getFieldKey())) {

            HashMap<String, String> userStatuses = ExternalAdapterManagerUtil.makeStatusMap(sc, taskId);

            userStatuses.put("null", "-------" + I18n.getString(sc.getLocale(), "NONE") + "-------");
            Customizer forHandlerStatus = new ListCustomizer(userStatuses, ListCustomizer.LIST_EQUAL, FieldMap.HUSER_STATUS, true);
            if (fill) forHandlerStatus.setFilter(sc, request, flthm.getSettings());


        } // block
        else if (oldField.equals(FieldMap.TASK_DEADLINE.getFieldKey()) || field.equals(FieldMap.TASK_DEADLINE.getFieldKey())) {
            Customizer forDeadline = new DateCustomizer(FieldMap.TASK_DEADLINE, sc.getUser().getDateFormatter());
            if (fill) forDeadline.setFilter(sc, request, flthm.getSettings());

        } // block
        else if (oldField.equals(FieldMap.TASK_SUBMITDATE.getFieldKey()) || field.equals(FieldMap.TASK_SUBMITDATE.getFieldKey())) {
            Customizer forSubmitDate = new DateCustomizer(FieldMap.TASK_SUBMITDATE, sc.getUser().getDateFormatter());
            if (fill) forSubmitDate.setFilter(sc, request, flthm.getSettings());


        } // block
        else if (oldField.equals(FieldMap.TASK_UPDATEDATE.getFieldKey()) || field.equals(FieldMap.TASK_UPDATEDATE.getFieldKey())) {
            Customizer forUpdatedDate = new DateCustomizer(FieldMap.TASK_UPDATEDATE, sc.getUser().getDateFormatter());
            if (fill) forUpdatedDate.setFilter(sc, request, flthm.getSettings());


        } // block
        else if (oldField.equals(FieldMap.TASK_CLOSEDATE.getFieldKey()) || field.equals(FieldMap.TASK_CLOSEDATE.getFieldKey())) {
            Customizer forCloseDate = new DateCustomizer(FieldMap.TASK_CLOSEDATE, sc.getUser().getDateFormatter());
            if (fill) forCloseDate.setFilter(sc, request, flthm.getSettings());

        } // block
        else if (oldField.equals(FieldMap.TASK_BUDGET.getFieldKey()) || field.equals(FieldMap.TASK_BUDGET.getFieldKey())) {
            Customizer forBudget = new BudgetCustomizer(BudgetCustomizer.ALL, FieldMap.TASK_BUDGET);
            if (fill) forBudget.setFilter(sc, request, flthm.getSettings());

        } // block
        else if (oldField.equals(FieldMap.TASK_ABUDGET.getFieldKey()) || field.equals(FieldMap.TASK_ABUDGET.getFieldKey())) {
            Customizer forABudget = new BudgetCustomizer(BudgetCustomizer.ALL, FieldMap.TASK_ABUDGET);
            if (fill) forABudget.setFilter(sc, request, flthm.getSettings());

        } // block
        else if (oldField.equals(FieldMap.TASK_CHILDCOUNT.getFieldKey()) || field.equals(FieldMap.TASK_CHILDCOUNT.getFieldKey())) {
            Customizer forChild = new TextCustomizer(TextCustomizer.NATURAL, FieldMap.TASK_CHILDCOUNT);
            if (fill) forChild.setFilter(sc, request, flthm.getSettings());

        } // block
        else if (oldField.equals(FieldMap.TASK_MESSAGECOUNT.getFieldKey()) || field.equals(FieldMap.TASK_MESSAGECOUNT.getFieldKey())) {
            Customizer forMessage = new TextCustomizer(TextCustomizer.NATURAL, FieldMap.TASK_MESSAGECOUNT);
            if (fill) forMessage.setFilter(sc, request, flthm.getSettings());

        } // block
        else if (oldField.equals(FieldMap.TASK_PRIORITY.getFieldKey()) || field.equals(FieldMap.TASK_PRIORITY.getFieldKey())) {
            HashMap<String, String> workflowMap = ExternalAdapterManagerUtil.getAvailableWorkflowMap(sc, taskId);
            Customizer forPriority = new ListCustomizer(ExternalAdapterManagerUtil.getAvailablePriorityMap(sc, workflowMap), ListCustomizer.LIST_EQUAL, FieldMap.TASK_PRIORITY, true);
            if (fill) forPriority.setFilter(sc, request, flthm.getSettings());

        } // block

        for (Object aList : list) {
            SecuredUDFValueBean udfvf = (SecuredUDFValueBean) aList;
            //cnt++;
            if (oldField.equals(udfvf.getId()) || field.equals(udfvf.getId())) {
                // Customizer cust = udfvf.getCustomizer(udfvf.getId(), false);
                Customizer cust;
                if (udfvf.getUdfType() != UdfValue.USER)
                    cust = udfvf.getCustomizer(udfvf.getId(), false);
                else {
                    HashMap usersMap = ExternalAdapterManagerUtil.makeUserMap(sc, flthm.getSettings(), taskId);
                    FieldMap fm = FieldMap.createUDF(udfvf.getCaption(), FValue.UDF + udfvf.getId(), FValue.UDF_SORT + udfvf.getId());
                    cust = new ListCustomizer(usersMap, ListCustomizer.LIST_EQUAL, fm, true);
                }
                if (fill) cust.setFilter(sc, request, flthm.getSettings());

            }
        } // for

        // if
        if (oldField.equals(FieldMap.TASK_SHORTNAME.getFieldKey()) || field.equals(FieldMap.TASK_SHORTNAME.getFieldKey())) {
            Customizer forShortName = new TextCustomizer(TextCustomizer.CHARS, FieldMap.TASK_SHORTNAME);
            if (fill) forShortName.setFilter(sc, request, flthm.getSettings());


        } // block
        if (oldField.equals(FieldMap.TASK_NAME.getFieldKey()) || field.equals(FieldMap.TASK_NAME.getFieldKey())) {
            Customizer forName = new TextCustomizer(TextCustomizer.CHARS, FieldMap.TASK_NAME);
            if (fill) forName.setFilter(sc, request, flthm.getSettings());

        } // block
        else if (oldField.equals(FieldMap.TASK_DESCRIPTION.getFieldKey()) || field.equals(FieldMap.TASK_DESCRIPTION.getFieldKey())) {
            Customizer forDescription = new TextCustomizer(TextCustomizer.CHARS, FieldMap.TASK_DESCRIPTION);
            if (fill) forDescription.setFilter(sc, request, flthm.getSettings());

        } // block
        else if (oldField.equals(FieldMap.MSG_SUSER_NAME.getFieldKey()) || field.equals(FieldMap.MSG_SUSER_NAME.getFieldKey())) {
            HashMap usersMap = ExternalAdapterManagerUtil.makeUserMap(sc, flthm.getSettings(), taskId);
            Customizer forMsgSubmitter = new ListCustomizer(usersMap, ListCustomizer.LIST_EQUAL, FieldMap.MSG_SUSER_NAME, true);
            if (fill) forMsgSubmitter.setFilter(sc, request, flthm.getSettings());

        } // block
        else if (oldField.equals(FieldMap.MSG_SUBMITDATE.getFieldKey()) || field.equals(FieldMap.MSG_SUBMITDATE.getFieldKey())) {
            Customizer forMsgDate = new DateCustomizer(FieldMap.MSG_SUBMITDATE, sc.getUser().getDateFormatter());
            if (fill) forMsgDate.setFilter(sc, request, flthm.getSettings());

        } // block
        else if (oldField.equals(FieldMap.LAST_MSG_SUBMITDATE.getFieldKey()) || field.equals(FieldMap.LAST_MSG_SUBMITDATE.getFieldKey())) {
            Customizer forLastMsgDate = new DateCustomizer(FieldMap.LAST_MSG_SUBMITDATE, sc.getUser().getDateFormatter());
            if (fill) forLastMsgDate.setFilter(sc, request, flthm.getSettings());

        } // block
        else if (oldField.equals(FieldMap.MSG_MSTATUS.getFieldKey()) || field.equals(FieldMap.MSG_MSTATUS.getFieldKey())) {
            HashMap<String, String> workflowMap = ExternalAdapterManagerUtil.getAvailableWorkflowMap(sc, taskId);
            Customizer forMsgMstatus = new ListCustomizer(ExternalAdapterManagerUtil.getAvailableMstatusMap(sc, workflowMap), ListCustomizer.LIST_EQUAL, FieldMap.MSG_MSTATUS, true);
            if (fill) forMsgMstatus.setFilter(sc, request, flthm.getSettings());

        } // block
        else if (oldField.equals(FieldMap.MSG_HUSER_NAME.getFieldKey()) || field.equals(FieldMap.MSG_HUSER_NAME.getFieldKey())) {
            HashMap<String, String> handlerUserMap = ExternalAdapterManagerUtil.makeStatusAndUserMap(sc, flthm.getSettings(), taskId);
            handlerUserMap.put("null", "-------" + I18n.getString(sc.getLocale(), "NONE") + "-------");
            Customizer forMsgHandler = new ListCustomizer(handlerUserMap, ListCustomizer.LIST_EQUAL, FieldMap.MSG_HUSER_NAME, true);
            if (fill) forMsgHandler.setFilter(sc, request, flthm.getSettings());

        } // block
        else if (oldField.equals(FieldMap.MSG_RESOLUTION.getFieldKey()) || field.equals(FieldMap.MSG_RESOLUTION.getFieldKey())) {
            HashMap<String, String> workflowMap = ExternalAdapterManagerUtil.getAvailableWorkflowMap(sc, taskId);
            HashMap<String, String> resolutionMap = ExternalAdapterManagerUtil.getAvailableResolutionMap(sc, workflowMap);
            resolutionMap.put("null", "-------" + I18n.getString(sc.getLocale(), "NONE") + "-------");
            Customizer forMsgResolution = new ListCustomizer(resolutionMap, ListCustomizer.LIST_EQUAL, FieldMap.MSG_RESOLUTION, true);
            if (fill) forMsgResolution.setFilter(sc, request, flthm.getSettings());
        } // block
        else if (oldField.equals(FieldMap.MSG_ABUDGET.getFieldKey()) || field.equals(FieldMap.MSG_ABUDGET.getFieldKey())) {
            Customizer forMsgBudget = new BudgetCustomizer(BudgetCustomizer.ALL, FieldMap.MSG_ABUDGET);
            if (fill) forMsgBudget.setFilter(sc, request, flthm.getSettings());

        } // block
        else if (oldField.equals(FieldMap.TEXT_MSG.getFieldKey()) || field.equals(FieldMap.TEXT_MSG.getFieldKey())) {
            Customizer forMsgText = new TextCustomizer(TextCustomizer.CHARS, FieldMap.TEXT_MSG);
            if (fill) forMsgText.setFilter(sc, request, flthm.getSettings());

        }
        if (oldField.equals(FieldMap.SEARCH.getFieldKey()) || field.equals(FieldMap.SEARCH.getFieldKey())) {
            Customizer forKeyword = new TextCustomizer(TextCustomizer.CHARS, FieldMap.SEARCH);
            if (fill) forKeyword.setFilter(sc, request, flthm.getSettings());
        }
        if (oldField.equals(FieldMap.DEEP_SEARCH.getFieldKey()) || field.equals(FieldMap.DEEP_SEARCH.getFieldKey())) {
            Customizer forKeyword = new CheckboxCustomizer(FieldMap.DEEP_SEARCH);
            if (fill) forKeyword.setFilter(sc, request, flthm.getSettings());
        }
    }

    public ActionForward changeTaskFilter(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            PreFilterForm bf = (PreFilterForm) form;
            String taskId = bf.getId();
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            TaskFValue val = AdapterManager.getInstance().getSecuredFilterAdapterManager().getTaskFValue(sc, bf.getFilter()).getFValue();
            FilterSettings flthm = null;
            String currentField = bf.getField();
            String filterId = bf.getFilter();

            flthm = generalChangeFilter(bf, request, currentField, sc, val, filterParameter);
            boolean isSave = bf.getSaveButton() != null;
            if (!delete && !isSave) {
                currentField = flthm.getFieldId();
                changePreFilter(sc, request, flthm, new FilterSettings(val, bf.getId(), bf.getFilter()), currentField, bf.getOldfield(), taskId, true);
            }
            sc.setAttribute(filterParameter, flthm);
            bf.setOldfield(bf.getField());

            if (isSave) {
                SecuredFilterBean curSfb = AdapterManager.getInstance().getSecuredFindAdapterManager().findFilterById(sc, filterId);
                if (curSfb != null) {
                    Calendar time = sc.getUser().getDateFormatter().getCalendar();

                    String name = isNotNull(bf.getName()) ? bf.getName() : curSfb.getName() + " (" + sc.getUser().getDateFormatter().parse(time) + ")";
                    if (badSmallDesc(name)) name = curSfb.getName();
                    filterId = AdapterManager.getInstance().getSecuredFilterAdapterManager().createTaskFilter(sc, name, curSfb.getDescription(), true, id, sc.getUserId(), curSfb.getPreferences());
                    flthm.setFilterId(filterId);
                    AdapterManager.getInstance().getSecuredFilterAdapterManager().setFValue(sc, filterId, new SecuredTaskFValueBean(flthm.getSettings(), sc));
                    AdapterManager.getInstance().getSecuredFilterAdapterManager().setCurrentFilter(sc, bf.getId(), filterId);
                    bf.setFilter(filterId);
                    sc.setAttribute(filterParameter, flthm);
                }
            }

            if (bf.getGo() != null || bf.getReset() != null || bf.getSaveButton() != null)
                return mapping.findForward(this.forward);
            else return mapping.findForward(this.action);
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public ActionForward changeField(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {
        boolean w = lockManager.acquireConnection();
        try {
            log.trace("##########");
            PreFilterForm bf = (PreFilterForm) form;
            String taskId = bf.getId();
            SessionContext sc = (SessionContext) request.getAttribute("sc");
            TaskFValue val = AdapterManager.getInstance().getSecuredFilterAdapterManager().getTaskFValue(sc, bf.getFilter()).getFValue();
            String currentField = bf.getField();
            FilterSettings flthm = generalChangeFilter(bf, request, currentField, sc, val, filterParameter);
            if (flthm != null) {

                if (sc.canAction(Action.manageTaskPrivateFilters, id)) {
                    changePreFilter(sc, request, flthm, new FilterSettings(val, bf.getId(), bf.getFilter()), currentField, bf.getOldfield(), taskId, false);
                }
                sc.setAttribute(filterParameter, flthm);
                bf.setOldfield(bf.getField());
            }
            return mapping.findForward(this.action);
        } finally {
            if (w) lockManager.releaseConnection();
        }

    }

    public static FilterSettings generalChangeFilter(PreFilterForm bf, HttpServletRequest request, String currentField, SessionContext sc, FValue val, String filterParameter) {
        FilterSettings flthm = null;
        String id = bf.getId();
        String filterid = bf.getFilter();

        boolean isReset = request.getParameter("reset") != null;
        if (isReset || currentField != null && isNotNull(currentField) && currentField.equals("default")) {
            sc.removeAttribute(filterParameter);
            currentField = "default";
            bf.setField(currentField);
            bf.setOldfield("default");
        }

        Object filterObject = sc.getAttribute(filterParameter);
        if (filterObject != null) {
            flthm = (FilterSettings) filterObject;
            FValue setting = flthm.getSettings();
            if (request.getParameter("deleteElement") != null) {
                String deleteElement = request.getParameter("deleteElement");
                setting.remove(deleteElement);
                String _deleteElement = deleteElement.startsWith("_") ? deleteElement.substring(1) : "_" + deleteElement;
                setting.remove(_deleteElement);
                delete = true;
                flthm.setSettings(setting);
            } else {
                delete = false;
            }
            if (!flthm.getFilterId().equals(filterid) || !flthm.getObjectId().equals(id)) {
                flthm = null;
                currentField = "default";
                bf.setField(currentField);
                bf.setOldfield("default");
            }
        }
        FilterSettings originFilterSettings = new FilterSettings(val, id, filterid);
        if (flthm == null /*|| (bf.getGo() == null && bf.getReset() == null && bf.getSaveButton() == null && request.getParameter("add") == null && sc.getAttribute("changedTaskFilterParameter") == null)*/) {
            flthm = originFilterSettings;
        }

        if (Null.isNull(currentField) && flthm.getFieldId() == null) {
            flthm.setFieldId("default");
        } else if (isNotNull(currentField)) {
            flthm.setFieldId(currentField);
        }

        if (bf.goPressed())
            flthm.setCurrentPage(1);
        if (bf.getSliderPage() != null && bf.getSliderPage().length() != 0) {
            flthm.setCurrentPage(Integer.parseInt(bf.getSliderPage()));
        }

        if (bf.getSliderOrder() != null && bf.getSliderOrder().length() != 0) {
            flthm.setSortedBy(FValue.parseFilterValue(bf.getSliderOrder()));
        }
        return flthm;
    }

    private void combineFilterSettings(String field, FValue original, FValue changed) throws GranException {
        for (FieldMap s : FieldMap.taskFields) {
            checkValue(field, original, changed, s.getFilterKey());
        }
        for (FieldMap s : FieldMap.messageFields) {
            checkValue(field, original, changed, s.getFilterKey());
        }
    }

    // Какая-то полная лажа. Критерием, какой параметр оставлять, не может быть размер
    private void checkValue(String field, FValue original, FValue changed, String constant) {
        if (FieldMap.getFilterKeyByFieldKey(field) != null && FieldMap.getFilterKeyByFieldKey(field).equals(constant) && !changed.containsKey(constant) && original.containsKey(constant)) {

            if (original.get(constant).size() == 1) {

                changed.set(constant, original.get(constant).get(0));
            } else if (original.get(constant).size() > 1) {
                changed.setList(constant, original.get(constant));
            }
        }
    }

}