package com.trackstudio.secured;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.trackstudio.exception.GranException;

/**
 * Copyright (c) TrackStudio Ltd., 2002-2005. All rights reserved.
 * Date: 10.04.2006
 * Time: 16:00:35
 */
public interface SecuredTaskBeanInterface {
    SecuredCategoryBean getCategory() throws GranException;

    SecuredStatusBean getStatus() throws GranException;

    SecuredResolutionBean getResolution() throws GranException;

    SecuredPriorityBean getPriority() throws GranException;

    SecuredUserBean getSubmitter() throws GranException;

    SecuredUserBean getHandlerUser() throws GranException;

    SecuredPrstatusBean getHandlerGroup() throws GranException;

    SecuredTaskBean getParent() throws GranException;

    SecuredWorkflowBean getWorkflow() throws GranException;

    String getId();

    String getName();


    String getShortname();

    Calendar getSubmitdate() throws GranException;

    Calendar getUpdatedate() throws GranException;

    Calendar getClosedate() throws GranException;

    String getDescription() throws GranException;

    String getTextDescription() throws GranException;

    Long getBudget() throws GranException;

    Long getActualBudget() throws GranException;

    Calendar getDeadline() throws GranException;

    String getNumber();

    Integer getMessageCount() throws GranException;

    Integer getChildrenCount() throws GranException;

    ArrayList getChildren() throws GranException;

    ArrayList getMessages() throws GranException;

    Integer getTotalChildrenCount() throws GranException;

    Integer getAllowedChildrenCount() throws GranException;

    String getTaskNumber();

    ArrayList getUDFValuesForNewTask(String workflowId) throws GranException;

    HashMap getUDFValues() throws GranException;

    ArrayList getUDFValuesList() throws GranException;

    //используется в SecuredTaskBean-ах, полученных от TaskFilter. Цель - определять isTaskUdfViewableFast только один раз для группы одинаковых тасков.
    ArrayList getFilteredUDFValues() throws GranException;

    ArrayList getWorkflowUDFValues() throws GranException;


    ArrayList getUDFs() throws GranException;

    ArrayList getUDFs(String workflowId) throws GranException;

    ArrayList getFilterUDFValues() throws GranException;

    String getTaskNameCutted();

    String getProjectAlias() throws GranException;

    ArrayList getAttachments() throws GranException;

    boolean hasAttachments() throws GranException;

    List getHandlerPrstatuses() throws GranException;

    List getSubmitterPrstatuses() throws GranException;


    String getCategoryId();

    String getStatusId();

    String getResolutionId()
            throws GranException;

    String getPriorityId()
            throws GranException;

    String getSubmitterId();

    String getHandlerUserId();

    String getHandlerGroupId();

    Collection getHandlerPrstatusesId() throws GranException;

    Collection getSubmitterPrstatusesId() throws GranException;

    String getParentId();

    String getWorkflowId();

    boolean isOnSight();

    //dnikitin: Map возвращаем с целью, чтобы при создании SecuredTaskBean-a в TaskFilter
//не делать проверку на onSight и hasAccess. Т.к. это довольно тормозные методы.

    //

    Map getAllowedChildrenMap() throws GranException;

    Map getAllowedChildrenWithSubtasksMap() throws GranException;

    SecuredUserBean getHandler() throws GranException;

    @Deprecated
    String getHandlerId() throws GranException;
}
