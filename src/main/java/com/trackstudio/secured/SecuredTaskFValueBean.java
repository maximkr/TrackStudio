package com.trackstudio.secured;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.trackstudio.app.filter.FValue;
import com.trackstudio.app.filter.TaskFValue;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.app.session.SessionManager;
import com.trackstudio.common.FieldMap;
import com.trackstudio.exception.GranException;
import com.trackstudio.soap.bean.TaskFvalueBean;
import com.trackstudio.tools.Null;

import net.jcip.annotations.Immutable;

/**
 * Bean which represents task custom field value
 */
@Immutable
public class SecuredTaskFValueBean extends SecuredFValueBean {
    private final ConcurrentMap<String, List<String>> fvalue = new ConcurrentHashMap<String, List<String>>();

    public SecuredTaskFValueBean(FValue f, SessionContext sc) {
        super(f, sc);
    }


    public SecuredTaskFValueBean(TaskFvalueBean f, String sessionId) throws GranException {
        this(new TaskFValue(), SessionManager.getInstance().getSessionContext(sessionId));
        addValue(FieldMap.TASK_ABUDGET.getFilterKey(), f.getABudget());
        addValue(FieldMap.TASK_BUDGET.getFilterKey(), f.getBudget());
        addValue(FieldMap.MSG_ABUDGET.getFilterKey(), f.getBudgetMsg());
        addValue(FieldMap.TASK_CATEGORY.getFilterKey(), f.getCategories());
        addValue(FieldMap.TASK_CHILDCOUNT.getFilterKey(), f.getChildCount());
        addValue(FieldMap.TASK_CLOSEDATE.getFilterKey(), f.getCloseDate());
        addValue(FieldMap.MSG_SUBMITDATE.getFilterKey(), f.getDateMsg());
        addValue(FieldMap.TASK_DEADLINE.getFilterKey(), f.getDeadline());
        addValue(FieldMap.TASK_DESCRIPTION.getFilterKey(), f.getDescription());
        addValue(FieldMap.HUSER_NAME.getFilterKey(), f.getHandler());
        addValue(FieldMap.HUSER_STATUS.getFilterKey(), f.getHandlerStatus());
        addValue(FValue.DISPLAY, f.getDisplay());
        addValue(FieldMap.TASK_MESSAGECOUNT.getFilterKey(), f.getMessageCount());
        addValue(FieldMap.MESSAGEVIEW.getFilterKey(), f.getMessageView());
        addValue(FieldMap.MSG_HUSER_NAME.getFilterKey(), f.getMsgHandler());
        addValue(FieldMap.MSG_SUSER_NAME.getFilterKey(), f.getMsgSubmitter());
        addValue(FieldMap.TASK_NAME.getFilterKey(), f.getName());
        addValue(FieldMap.TASK_SHORTNAME.getFilterKey(), f.getShortName());
        addValue(FValue.ONPAGE, f.getOnPage());
        addValue(FieldMap.TASK_PRIORITY.getFilterKey(), f.getPriority());
        addValue(FieldMap.TASK_RESOLUTION.getFilterKey(), f.getResolution());
        addValue(FieldMap.MSG_RESOLUTION.getFilterKey(), f.getResolutionMsg());
        addValue(FieldMap.SEARCH.getFilterKey(), f.getSearch());
        addValue(FValue.SORTORDER, f.getSortOrder());
        addValue(FieldMap.TASK_STATUS.getFilterKey(), f.getStatuses());
        addValue(FieldMap.TASK_SUBMITDATE.getFilterKey(), f.getSubmitDate());
        addValue(FieldMap.SUSER_NAME.getFilterKey(), f.getSubmitter());
        addValue(FieldMap.SUSER_STATUS.getFilterKey(), f.getSubmitterStatus());
        addValue(FValue.SUBTASK, f.getSubtask());
        addValue(FieldMap.TASK_NUMBER.getFilterKey(), f.getTaskNumber());
        addValue(TaskFValue.MSG_TEXT, f.getTextMsg());
        addValue(TaskFValue.MSG_TYPE, f.getTypeMsg());
        addValue("_submit_date", f.getSubmitDateFrom());
        addValue("_updated_date", f.getUpdateDateFrom());
        addValue("_close_date", f.getCloseDateFrom());
        addValue("_deadline", f.getDeadlineFrom());
        if (f.getUdfs() != null) {
            for (String str : f.getUdfs()) {
                int idx = str.indexOf("->");
                if (idx == -1)
                    continue;
                addValue(str.substring(0, idx), str.substring(idx + 2));
            }
        }
        addValue(FieldMap.TASK_UPDATEDATE.getFilterKey(), f.getUpdateDate());
    }

    public TaskFValue getFValue() {
        TaskFValue fv = new TaskFValue();
        fv.putAll(this.map);
        return fv;
    }

    public TaskFValue getFValueForSoap() {
        TaskFValue fv = new TaskFValue();
        fv.putAll(fvalue);
        return fv;
    }

    private void addValue(String key, String value) {
        if (Null.isNotNull(value)) {
            this.addValue(key, new String[]{value});
        }
    }

    /**
     * This method adds the value for filters
     * @param key key
     * @param value value
     */
    private void addValue(String key, String[] value) {
        if (Null.isNotNull(value)) {
            fvalue.put(key, Arrays.asList(value));
        } else {
            fvalue.remove(key);
        }
    }

    public TaskFvalueBean getSOAP() {
        TaskFvalueBean bean = new TaskFvalueBean();
        bean.setABudget(getStirngValueFromMap(FieldMap.TASK_ABUDGET.getFilterKey()));
        bean.setBudget(getStirngValueFromMap(FieldMap.TASK_BUDGET.getFilterKey()));
        bean.setBudgetMsg(getStirngValueFromMap(FieldMap.MSG_ABUDGET.getFilterKey()));
        bean.setCategories(getArrayValueFromMap(FieldMap.TASK_CATEGORY.getFilterKey()));
        bean.setChildCount(getStirngValueFromMap(FieldMap.TASK_CHILDCOUNT.getFilterKey()));
        bean.setCloseDate(getStirngValueFromMap("el_" + FieldMap.TASK_CLOSEDATE.getFilterKey()));
        bean.setDateMsg(getStirngValueFromMap(FieldMap.MSG_SUBMITDATE.getFilterKey()));
        bean.setDateMsg(getStirngValueFromMap(FieldMap.LAST_MSG_SUBMITDATE.getFilterKey()));
        bean.setDeadline(getStirngValueFromMap("el_" + FieldMap.TASK_DEADLINE.getFilterKey()));
        bean.setDescription(getStirngValueFromMap(FieldMap.TASK_DESCRIPTION.getFilterKey()));
        bean.setHandler(getStirngValueFromMap(FieldMap.HUSER_NAME.getFilterKey()));
        bean.setHandlerStatus(getStirngValueFromMap(FieldMap.HUSER_STATUS.getFilterKey()));
        bean.setDisplay(getStirngValueFromMap(FValue.DISPLAY));
        bean.setMessageCount(getStirngValueFromMap(FieldMap.TASK_MESSAGECOUNT.getFilterKey()));
        bean.setMessageView(getStirngValueFromMap(FieldMap.MESSAGEVIEW.getFilterKey()));
        bean.setMsgHandler(getStirngValueFromMap(FieldMap.MSG_HUSER_NAME.getFilterKey()));
        bean.setMsgSubmitter(getStirngValueFromMap(FieldMap.MSG_SUSER_NAME.getFilterKey()));
        bean.setName(getStirngValueFromMap(FieldMap.TASK_NAME.getFilterKey()));
        bean.setShortName(getStirngValueFromMap(FieldMap.TASK_SHORTNAME.getFilterKey()));
        bean.setOnPage(getStirngValueFromMap(FValue.ONPAGE));
        bean.setPriority(getStirngValueFromMap(FieldMap.TASK_PRIORITY.getFilterKey()));
        bean.setResolution(getStirngValueFromMap(FieldMap.TASK_RESOLUTION.getFilterKey()));
        bean.setResolutionMsg(getStirngValueFromMap(FieldMap.MSG_RESOLUTION.getFilterKey()));
        bean.setSearch(getStirngValueFromMap(FieldMap.SEARCH.getFilterKey()));
        bean.setSortOrder(getStirngValueFromMap(FValue.SORTORDER));
        bean.setStatuses(getArrayValueFromMap(FieldMap.TASK_STATUS.getFilterKey()));
        bean.setSubmitDate(getStirngValueFromMap("el_" + FieldMap.TASK_SUBMITDATE.getFilterKey()));
        bean.setSubmitter(getStirngValueFromMap(FieldMap.SUSER_NAME.getFilterKey()));
        bean.setSubmitterStatus(getStirngValueFromMap(FieldMap.SUSER_STATUS.getFilterKey()));
        bean.setSubtask(getStirngValueFromMap(FValue.SUBTASK));
        bean.setTaskNumber(getStirngValueFromMap(FieldMap.TASK_NUMBER.getFilterKey()));
        bean.setTextMsg(getStirngValueFromMap(TaskFValue.MSG_TEXT));
        bean.setTypeMsg(getStirngValueFromMap(TaskFValue.MSG_TYPE));
        bean.setUpdateDate(getStirngValueFromMap("el_" + FieldMap.TASK_UPDATEDATE.getFilterKey()));

        List<String> udfList = new ArrayList<String>(100);
        ArrayList<String> udfSortList = new ArrayList<String>();

        for (String key : (Set<String>) map.keySet()) {
            if (key.indexOf(FValue.UDF) != -1)
                if (key.indexOf(FValue.UDF_SORT) != -1)
                    udfSortList.add(key);
                else
                    udfList.add(key + "->" + getStirngValueFromMap(key));
        }
        bean.setUdfs(udfList.toArray(new String[udfList.size()]));
        bean.setUdfSort(udfSortList.toArray(new String[]{}));

        return bean;
    }

    public boolean canManage() throws GranException {
        return true;
    }
}
