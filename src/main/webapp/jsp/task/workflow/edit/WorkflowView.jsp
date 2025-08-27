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
<tiles:put name="customHeader" value="/jsp/task/workflow/WorkflowHeader.jsp"/>
<tiles:put name="tabs" value="/jsp/task/workflow/WorkflowSubMenu.jsp"/>
<tiles:put name="main" type="string">

<div class="nblueborder">
<div class="ncaption"></div>
<c:if test="${flow.canManage}">
    <div class="controlPanel">
        <html:link  href="${contextPath}/WorkflowEditAction.do?method=page&amp;id=${id}&amp;workflowId=${workflowId}">
            <html:img src="${contextPath}${ImageServlet}/cssimages/ico.edit.gif" hspace="0" vspace="0" border="0" align="middle"/>
            <I18n:message key="EDIT"/>
        </html:link>
    </div>
</c:if>

<div class="indent">
<c:if test="${!isValid}">
    <div class="general">
        <table class="error" cellpadding="0" cellspacing="0">
            <caption>
                <I18n:message key="WORKFLOW_INVALID_OVERVIEW"/>
            </caption>
            <c:if test="${!hasStart}">
                <tr class="line0">
                    <td>
                        <I18n:message key="WORKFLOW_INVALID_HASNT_START_STATE"/>
                    </td>
                </tr>
            </c:if>
            <c:if test="${!isValidPermissions}">
                <c:set var="counter" value="${hasStart ? 0 : 1}"/>
                <c:forEach var="prstatus" items="${canViewCanEditPrstatusList}">
                    <c:forEach var="mstatus" items="${canViewCanEditMap[prstatus.id]}">
                        <tr class="line<c:out value="${counter mod 2}"/>">
                            <td>
                                <I18n:message key="WORKFLOW_INVALID_PERMISSION_CONFLICT">
                                    <I18n:param value="${prstatus.name}"/>
                                    <I18n:param value="${mstatus.name}"/>
                                </I18n:message>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:set var="counter" value="${counter + 1}"/>
                </c:forEach>
            </c:if>
        </table>
    </div>
</c:if>

<table class="general" cellpadding="0" cellspacing="0">
    <caption>
        <I18n:message key="PROPERTIES"/>
    </caption>
    <colgroup>
        <col class="col_1">
        <col class="col_2">
    </colgroup>
    <tr>
        <th>
            <I18n:message key="NAME"/>
        </th>
        <td>
            <c:out value="${flow.name}" escapeXml="true"/>
        </td>
    </tr>
</table>

<c:if test="${viewPriority}">
    <div class="general">
        <table class="general" cellpadding="0" cellspacing="0">
            <caption>
                <I18n:message key="PRIORITIES_LIST"/>
            </caption>
            <c:choose>
                <c:when test="${!empty priorities}">
                    <tr class="wide">
                        <th>
                            <I18n:message key="NAME"/>
                        </th>
                        <th>
                            <I18n:message key="DESCRIPTION"/>
                        </th>
                        <th>
                            <I18n:message key="ORDER"/>
                        </th>
                        <th>
                            <I18n:message key="DEFAULT"/>
                        </th>
                    </tr>
                    <c:forEach var="priority" items="${priorities}" varStatus="varCounter">
                        <tr class="line<c:out value="${varCounter.index mod 2}"/>">
                            <td>
                                <c:out value="${priority.name}"/>
                            </td>
                            <td>
                                <c:out value="${priority.description}"/>
                            </td>
                            <td>
                                <c:out value="${priority.order}"/>
                            </td>
                            <td>
                                <c:if test="${priority.def}">
                                    <html:img src="${contextPath}${ImageServlet}/cssimages/ico.checked.gif"/>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <tr class="wide">
                        <td colspan="2">
                            <span style="text-align: center;">
                                <I18n:message key="EMPTY_PRIORITIES_LIST_WORKFLOW"/>
                            </span>
                        </td>
                    </tr>
                </c:otherwise>
            </c:choose>
        </table>
    </div>
</c:if>
<c:if test="${viewState}">
    <div class="general">
        <table class="general" cellpadding="0" cellspacing="0">
            <caption>
                <I18n:message key="STATES_LIST"/>
            </caption>
            <c:choose>
                <c:when test="${!empty states}">
                    <colgroup>
                        <col class="col_1">
                        <col class="col_2">
                    </colgroup>
                    <tr>
                        <th>
                            <I18n:message key="STATES_LIST"/>
                        </th>
                        <td>
                            <c:forEach var="state" items="${states}">
                                <span class="nowrap">
                                    <html:img styleClass="state" border="0" style="background-color: ${state.color}" src="${contextPath}${ImageServlet}${state.image}"/>
                                    <c:out value="${state.name}"/>
                                </span>
                                <br/>
                            </c:forEach>
                        </td>
                    </tr>
                </c:when>
                <c:otherwise>
                    <tr class="wide">
                        <td>
                            <span style="text-align: center;">
                                <I18n:message key="EMPTY_STATES_LIST_WORKFLOW"/>
                            </span>
                        </td>
                    </tr>
                </c:otherwise>
            </c:choose>
        </table>
    </div>
</c:if>
<c:if test="${viewMstatus}">
    <div class="general">
        <table class="general" cellpadding="0" cellspacing="0">
            <caption>
                <I18n:message key="MESSAGE_TYPES_VIEW"/>
            </caption>
            <c:choose>
                <c:when test="${!empty mstatuses}">
                    <colgroup>
                        <col width="20%">
                        <col width="30%">
                        <col width="50%">
                    </colgroup>
                    <tr class="wide">
                        <th>
                            <I18n:message key="NAME"/>
                        </th>
                        <th>
                            <I18n:message key="DESCRIPTION"/>
                        </th>
                        <th>
                            <I18n:message key="TRANSITIONS_LIST"/>
                        </th>
                    </tr>
                    <c:forEach var="mstatus" items="${mstatuses}" varStatus="varCounter">
                        <tr class="line<c:out value="${varCounter.index mod 2}"/>">
                            <td>
                                <c:choose>
                                    <c:when test="${mstatus.canView}">
                                        <html:link styleClass="internal"
                                                   href="${contextPath}/MstatusViewAction.do?method=page&mstatusId=${mstatus.id}&id=${id}&workflowId=${flow.id}">
                                            <img border="0"
                                                 src="<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.messagetypes.gif"/>
                                            <c:out value="${mstatus.name}"/>
                                        </html:link>
                                    </c:when>
                                    <c:otherwise>
                                        <img border="0"
                                             src="<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.messagetypes.gif"/>
                                        <c:out value="${mstatus.name}"/>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <c:out value="${mstatus.description}"/>
                            </td>
                            <td>
                                <c:forEach var="cat" items="${mstatus.transitions}" varStatus="varC">
                                    <span class="nowrap">
                                         <html:img styleClass="state" border="0" style="background-color: ${cat.start.color}" src="${contextPath}${ImageServlet}${cat.start.image}"/>
                                        <c:out value="${cat.start.name}"/></span> &rarr; <span class="nowrap">
                                        <html:img styleClass="state" border="0" style="background-color: ${cat.finish.color}" src="${contextPath}${ImageServlet}${cat.finish.image}"/>
                                        <c:out value="${cat.finish.name}"/>
                                    </span><br>
                                </c:forEach>
                            </td>
                        </tr>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <tr class="wide">
                        <td>
                            <span style="text-align: center;">
                                <I18n:message key="EMPTY_MESSAGES_TYPES_LIST_WORKFLOW"/>
                            </span>
                        </td>
                    </tr>
                </c:otherwise>
            </c:choose>
        </table>
    </div>
</c:if>
<c:if test="${_can_view}">
    <div class="general">
        <table class="general" cellpadding="0" cellspacing="0">
            <caption>
                <I18n:message key="CUSTOM_FIELDS"/>
            </caption>
            <c:choose>
                <c:when test="${!empty udfList}">
                    <tr class="wide">
                        <th width="50%">
                            <I18n:message key="CAPTION"/>
                        </th>
                        <th width="30%">
                            <I18n:message key="TYPE"/>
                        </th>
                        <th width="20%">
                            <I18n:message key="ORDER"/>
                        </th>
                    </tr>
                    <c:forEach var="udf" items="${udfList}">
                        <tr>
                            <td>
                                <c:choose>
                                    <c:when test="${!udf.canUpdate}">
                                        <html:link styleClass="internal"
                                                   href="${contextPath}${viewUdfAction}?method=page&amp;udfId=${udf.id}&amp;id=${id}&amp;workflowId=${workflowId}">
                                            <img title="<I18n:message key="OBJECT_PROPERTIES_VIEW"/>" border="0"
                                                 hspace="0" vspace="0" src="${contextPath}${ImageServlet}/cssimages/ico.closed.gif"/>
                                        </html:link>
                                    </c:when>
                                    <c:otherwise>
                                        <html:link styleClass="internal"
                                                   href="${contextPath}${editUdfAction}?method=page&amp;udfId=${udf.id}&amp;id=${id}&amp;workflowId=${workflowId}">
                                            <img title="<I18n:message key="OBJECT_PROPERTIES_EDIT"/>" border="0"
                                                 hspace="0" vspace="0" src="${contextPath}${ImageServlet}/cssimages/ico.edit.gif"/>
                                        </html:link>
                                    </c:otherwise>
                                </c:choose>
                                <html:link styleClass="internal"
                                           href="${contextPath}${viewUdfAction}?method=page&amp;udfId=${udf.id}&amp;id=${id}&amp;workflowId=${workflowId}">
                                    <c:out value="${udf.name}"/>
                                </html:link>
                            </td>
                            <td>
                                <c:out value="${udf.type}"/>
                            </td>
                            <td>
                                <c:out value="${udf.order}"/>
                            </td>
                        </tr>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <tr class="wide">
                        <td>
                            <span style="text-align: center;">
                                <I18n:message key="EMPTY_UDF_LIST_WORKFLOW"/>
                            </span>
                        </td>
                    </tr>
                </c:otherwise>
            </c:choose>
        </table>
    </div>
</c:if>
</div>
</div>
</tiles:put>
</tiles:insert>
