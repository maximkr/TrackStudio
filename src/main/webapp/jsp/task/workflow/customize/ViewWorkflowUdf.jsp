<%@ page buffer="128kb" errorPage="/jsp/Error.jsp" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/tiles-el" prefix="tiles" %>


<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value="${sc.timezone}"/>
<I18n:setBundle basename="language"/>
<tiles:insert page="/jsp/layout/ListLayout.jsp" flush="true">
    
    <tiles:put name="header" value="/jsp/task/TaskHeader.jsp"/>
    <tiles:put name="customHeader" value="/jsp/task/workflow/customize/WorkflowCustomizeHeader.jsp"/>
            <tiles:put name="tabs" type="string"/>
    <tiles:put name="main" type="string">
        <div class="blueborder">
            <div class="caption">
                <I18n:message key="CUSTOM_FIELD_OVERVIEW_TASK"/>
            </div>
            <div class="controlPanel">
                <c:if test="${tabEdit.allowed}">
                    <html:link href="${contextPath}/WorkflowUdfEditAction.do?method=page&id=${id}&udfId=${udfId}&type=${type}&workflowId=${workflowId}">
                        <html:img  styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/ico.edit.gif"/><I18n:message key="WORKFLOW_CUSTOM_FIELD_PROPERTIES"/>
                    </html:link>
                </c:if>
                <c:if test="${tabPermission.allowed}">
                    <html:link  href="${contextPath}/WorkflowUdfPermissionAction.do?method=page&id=${id}&udfId=${udfId}&workflowId=${workflowId}">
                        <html:img  styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/ico.effective.gif"/><I18n:message key="WORKFLOW_CUSTOM_FIELD_PERMISSIONS"/>
                    </html:link>
                    <html:link  href="${contextPath}/WorkflowUdfOperationPermissionAction.do?method=page&id=${id}&udfId=${udfId}&workflowId=${workflowId}">
                        <html:img  styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/ico.effective.gif"/><I18n:message key="MESSAGE_TYPE_CUSTOM_FIELDS_PERMISSIONS"/>
                    </html:link>
                </c:if>
            </div>
            <div class="indent">
                    <c:import url="/jsp/custom/CustomViewTile.jsp"/>
                <div class="general">
                    <table class="general" cellpadding="0" cellspacing="0">
                        <caption>
                            <I18n:message key="MESSAGE_TYPE_CUSTOM_FIELDS_PERMISSIONS"/>
                        </caption>
                        <colgroup>
                            <col class="col_1">
                            <col class="col_2">
                        </colgroup>
                        <tr>
                            <th>
                                <I18n:message key="CAN_VIEW"/>
                            </th>
                            <td>
                                <c:forEach items="${viewable}" var="oper" varStatus="c">
                                        <span style="white-space: nowrap;"><img src="${contextPath}${ImageServlet}/cssimages/ico.messagetypes.gif"
                                                                                alt=""><c:out value="${oper.name}"/><c:if test="${!c.last}">,
                                        </c:if></span>
                                </c:forEach>
                            </td>
                        </tr>
                        <tr>
                            <th>
                                <I18n:message key="CAN_EDIT"/>
                            </th>
                            <td>
                                <c:forEach items="${editable}" var="oper" varStatus="c">
                                        <span style="white-space: nowrap;"><img src="${contextPath}${ImageServlet}/cssimages/ico.messagetypes.gif"
                                                                                alt=""><c:out value="${oper.name}"/><c:if
                                                test="${!c.last}">,
                                        </c:if></span>
                                </c:forEach>
                            </td>
                        </tr>
                    </table>
                </div>
            </div>
        </div>
    </tiles:put>
</tiles:insert>