<%@ page buffer="128kb" errorPage="/jsp/Error.jsp" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/tiles-el" prefix="tiles" %>


<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value="${sc.timezone}"/>
<I18n:setBundle basename="language"/>
<tiles:insert page="/jsp/layout/ListLayout.jsp" flush="true">
<tiles:put name="customHeader" value="/jsp/task/workflow/mstatus/MstatusHeader.jsp"/>
<tiles:put name="header" value="/jsp/task/TaskHeader.jsp"/>
<tiles:put name="tabs" value="/jsp/task/workflow/mstatus/MstatusSubMenu.jsp"/>
<tiles:put name="main" type="string">
<div class="nblueborder">
<div class="ncaption"></div>

<c:if test="${tabEdit.allowed}">
    <div class="controlPanel">
        <html:link
                href="${contextPath}/MstatusEditAction.do?method=page&id=${id}&mstatusId=${mstatus.id}&workflowId=${flow.id}">
            <img src="${contextPath}${ImageServlet}/cssimages/ico.edit.gif" alt="" border="0">
            <I18n:message key="MESSAGE_TYPE_EDIT"/>
        </html:link>

    </div>
</c:if>

<div class="indent">
<c:if test="${!isValidPermissions}">
    <div class="general">
        <table class="error" cellpadding="0" cellspacing="0">
            <caption>
                <I18n:message key="WORKFLOW_INVALID_OPERATION_OVERVIEW"/>
            </caption>
            <c:forEach var="prstatus" items="${canViewCanEditList}" varStatus="counter">
                <tr class="line<c:out value="${counter.index mod 2}"/>">
                    <td>
                        <I18n:message key="WORKFLOW_INVALID_OPERATION_PERMISSION_CONFLICT">
                            <I18n:param value="${prstatus.name}"/>
                        </I18n:message>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </div>
</c:if>
<div class="general">
    <table class="general" cellpadding="0" cellspacing="0">
        <caption>
            <I18n:message key="MESSAGE_TYPE_PROPERTIES"/>
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
                <c:out value="${mstatus.name}" escapeXml="true"/>
            </td>
        </tr>
        <tr>
            <th>
                <I18n:message key="DESCRIPTION"/>
            </th>
            <td>
                <c:out value="${mstatus.description}" escapeXml="true"/>
            </td>
        </tr>
        <tr>
            <th>
                <I18n:message key="ACTION"/>
            </th>
            <td>
                <c:out value="${mstatus.action}" escapeXml="true"/>
            </td>
        </tr>

        <tr>
            <th>
                <I18n:message key="SHOW_IN_TOOLBAR"/>
            </th>
            <td>
                <c:choose>
                    <c:when test="${showInToolbar ne true}">
                        <html:img src="${contextPath}${ImageServlet}/cssimages/ico.unchecked.gif"/>
                    </c:when>
                    <c:otherwise>
                        <html:img src="${contextPath}${ImageServlet}/cssimages/ico.checked.gif"/>
                    </c:otherwise>
                </c:choose>
            </td>
        </tr>
    </table>
</div>

<c:if test="${viewResolution}">
    <div class="general">
        <table class="general" cellpadding="0" cellspacing="0">
            <caption>
                <I18n:message key="RESOLUTIONS_LIST"/>
            </caption>
            <c:choose>
                <c:when test="${!empty resolutions}">
                    <colgroup>
                        <col class="col_1">
                        <col class="col_2">
                    </colgroup>
                    <tr>
                        <th>
                            <I18n:message key="NAME"/>
                        </th>
                        <td>
                            <c:forEach var="resolution" items="${resolutions}">
                                <c:out value="${resolution.name}" escapeXml="true"/>
                                <br/>
                            </c:forEach>
                        </td>
                    </tr>
                </c:when>
                <c:otherwise>
                    <tr class="wide">
                        <td>
                            <span style="text-align: center;">
                                <I18n:message key="EMPTY_RESOLUTIONS_LIST"/>
                            </span>
                        </td>
                    </tr>
                </c:otherwise>
            </c:choose>
        </table>
    </div>
</c:if>
<c:if test="${viewTransition}">
    <div class="general">
        <table class="general" cellpadding="0" cellspacing="0">
            <caption>
                <I18n:message key="TRANSITIONS_LIST"/>
            </caption>
            <c:choose>
                <c:when test="${!empty transitions}">
                    <tr class="wide">
                        <th>
                            <I18n:message key="START"/>
                        </th>
                        <th>
                            <I18n:message key="FINAL"/>
                        </th>
                    </tr>
                    <c:forEach var="transition" items="${transitions}" varStatus="varCounter">
                        <tr class="line<c:out value="${varCounter.index mod 2}"/>">
                            <td><span class="nowrap">
                                <html:img styleClass="state" border="0" style="background-color: ${transition.start.color}" src="${contextPath}${ImageServlet}${transition.start.image}"/>
                                <c:out value="${transition.start.name}"/></span>
                            </td>
                            <td><span class="nowrap">
                                <html:img styleClass="state" border="0" style="background-color: ${transition.finish.color}" src="${contextPath}${ImageServlet}${transition.finish.image}"/>
                                <c:out value="${transition.finish.name}"/>
                                </span>
                            </td>
                        </tr>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <tr class="wide">
                        <td>
                            <span style="text-align: center;">
                                <I18n:message key="EMPTY_TRANSITIONS_LIST"/>
                            </span>
                        </td>
                    </tr>
                </c:otherwise>
            </c:choose>
        </table>
    </div>
</c:if>
<c:if test="${canViewTriggers}">
    <div class="general">
        <table class="general" cellpadding="0" cellspacing="0">
            <caption>
                <I18n:message key="TRIGGERS"/>
            </caption>
            <colgroup>
                <col class="col_1">
                <col class="col_2">
            </colgroup>
            <tr>
                <th>
                    <I18n:message key="TRIGGER_BEFORE"/>
                </th>
                <td>
                    <c:out value="${before}"/>
                </td>
            </tr>
            <tr>
                <th>
                    <I18n:message key="TRIGGER_INSTEADOF"/>
                </th>
                <td>
                    <c:out value="${insteadOf}"/>
                </td>
            </tr>
            <tr>
                <th>
                    <I18n:message key="TRIGGER_AFTER"/>
                </th>
                <td>
                    <c:out value="${after}"/>
                </td>
            </tr>
        </table>
    </div>
</c:if>
<c:if test="${viewMessageTypePermission}">
    <div class="general">
        <table class="general" cellpadding="0" cellspacing="0">
            <caption>
                <I18n:message key="PERMISSIONS_VIEW"/>
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
                    <c:forEach var="prstatus" items="${ruleViewAll}" varStatus="varC">
                        <c:if test="${varC.index > 0}">,</c:if>
                            <span style="white-space: nowrap;">
                                <html:img styleClass="icon" border="0"
                                          src="${contextPath}${ImageServlet}/cssimages/ico.status.gif"/>
                                                <c:out value="${prstatus.name}" escapeXml="true"/>
                                </span>
                    </c:forEach>

                    <c:forEach var="prstatus" items="${ruleViewSubmitter}" varStatus="varC">
                        <c:if test="${varC.index > 0}">,</c:if>
                            <span style="white-space: nowrap;">
                                <html:img styleClass="icon" border="0"
                                          src="${contextPath}${ImageServlet}/cssimages/ico.status.gif"/>
                                                <c:out value="${prstatus.name}" escapeXml="true"/> (* <I18n:message key="SUBMITTER"/>)
                                </span>
                    </c:forEach>

                    <c:forEach var="prstatus" items="${ruleViewHandler}" varStatus="varC">
                        <c:if test="${varC.index > 0}">,</c:if>
                            <span style="white-space: nowrap;">
                                <html:img styleClass="icon" border="0"
                                          src="${contextPath}${ImageServlet}/cssimages/ico.status.gif"/>
                                                <c:out value="${prstatus.name}" escapeXml="true"/> (* <I18n:message key="HANDLER"/>)
                                </span>
                    </c:forEach>

                    <c:forEach var="prstates" items="${ruleViewSAH}" varStatus="varC">
                        <c:if test="${varC.index > 0}">,</c:if>
                            <span style="white-space: nowrap;">
                                <html:img styleClass="icon" border="0"
                                          src="${contextPath}${ImageServlet}/cssimages/ico.status.gif"/>
                                                <c:out value="${prstatus.name}" escapeXml="true"/> (* <I18n:message key="SUBMITTER"/>, <I18n:message key="HANDLER"/>)
                                </span>
                    </c:forEach>

                </td>
            </tr>

            <tr>
                <th>
                    <I18n:message key="CAN_PROCESS"/>
                </th>
                <td>
                    <c:forEach var="prstatus" items="${ruleProcessAll}" varStatus="varC">
                        <c:if test="${varC.index > 0}">,</c:if>
                            <span style="white-space: nowrap;">
                                <html:img styleClass="icon" border="0"
                                          src="${contextPath}${ImageServlet}/cssimages/ico.status.gif"/>
                                                <c:out value="${prstatus.name}" escapeXml="true"/>
                                </span>
                    </c:forEach>

                    <c:forEach var="prstatus" items="${ruleProcessSubmitter}" varStatus="varC">
                        <c:if test="${varC.index > 0}">,</c:if>
                            <span style="white-space: nowrap;">
                                <html:img styleClass="icon" border="0"
                                          src="${contextPath}${ImageServlet}/cssimages/ico.status.gif"/>
                                                <c:out value="${prstatus.name}" escapeXml="true"/> (* <I18n:message key="SUBMITTER"/>)
                                </span>
                    </c:forEach>

                    <c:forEach var="prstatus" items="${ruleProcessHandler}" varStatus="varC">
                        <c:if test="${varC.index > 0}">,</c:if>
                            <span style="white-space: nowrap;">
                                <html:img styleClass="icon" border="0"
                                          src="${contextPath}${ImageServlet}/cssimages/ico.status.gif"/>
                                                <c:out value="${prstatus.name}" escapeXml="true"/> (* <I18n:message key="HANDLER"/>)
                                </span>
                    </c:forEach>

                    <c:forEach var="prstatus" items="${ruleProcessSAH}" varStatus="varC">
                        <c:if test="${varC.index > 0}">,</c:if>
                            <span style="white-space: nowrap;">
                                <html:img styleClass="icon" border="0"
                                          src="${contextPath}${ImageServlet}/cssimages/ico.status.gif"/>
                                                <c:out value="${prstatus.name}" escapeXml="true"/> (* <I18n:message key="SUBMITTER"/>, <I18n:message key="HANDLER"/>)
                                </span>
                    </c:forEach>

                </td>
            </tr>

            <tr>
                <th>
                    <I18n:message key="CAN_BE_HANDLER"/>
                </th>
                <td>
                    <c:forEach var="prstatus" items="${ruleBeHandlerAll}" varStatus="varC">
                        <c:if test="${varC.index > 0}">,</c:if>
                            <span style="white-space: nowrap;">
                                <html:img styleClass="icon" border="0"
                                          src="${contextPath}${ImageServlet}/cssimages/ico.status.gif"/>
                                                <c:out value="${prstatus.name}" escapeXml="true"/>
                                </span>
                    </c:forEach>

                    <c:forEach var="prstatus" items="${ruleBeHandlerSubmitter}" varStatus="varC">
                        <c:if test="${varC.index > 0}">,</c:if>
                            <span style="white-space: nowrap;">
                                <html:img styleClass="icon" border="0"
                                          src="${contextPath}${ImageServlet}/cssimages/ico.status.gif"/>
                                                <c:out value="${prstatus.name}" escapeXml="true"/> (* <I18n:message key="SUBMITTER"/>)
                                </span>
                    </c:forEach>

                    <c:forEach var="prstatus" items="${ruleBeHandlerHandler}" varStatus="varC">
                        <c:if test="${varC.index > 0}">,</c:if>
                            <span style="white-space: nowrap;">
                                <html:img styleClass="icon" border="0"
                                          src="${contextPath}${ImageServlet}/cssimages/ico.status.gif"/>
                                                <c:out value="${prstatus.name}" escapeXml="true"/> (* <I18n:message key="HANDLER"/>)
                                </span>
                    </c:forEach>

                    <c:forEach var="prstatus" items="${ruleBeHandlerSAH}" varStatus="varC">
                        <c:if test="${varC.index > 0}">,</c:if>
                            <span style="white-space: nowrap;">
                                <html:img styleClass="icon" border="0"
                                          src="${contextPath}${ImageServlet}/cssimages/ico.status.gif"/>
                                                <c:out value="${prstatus.name}" escapeXml="true"/> (* <I18n:message key="SUBMITTER"/>, <I18n:message key="HANDLER"/>)
                                </span>
                    </c:forEach>

                </td>
            </tr>

        </table>
    </div>
</c:if>
<c:if test="${!empty udfPerm}">
    <div class="general">
        <table class="general" cellpadding="0" cellspacing="0">
            <caption>
                <I18n:message key="WORKFLOW_CUSTOM_FIELD_PERMISSIONS"/>
            </caption>
            <colgroup>
                <col width="30%">
                <col width="35%">
                <col width="35%">
            </colgroup>
            <tr class="wide">
                <th>
                    <I18n:message key="CUSTOM_FIELDS_NAMES"/>
                </th>
                <th>
                    <I18n:message key="CAN_VIEW"/>
                </th>
                <th>
                    <I18n:message key="CAN_EDIT"/>
                </th>
            </tr>
            <c:forEach items="${udfList}" var="udf">
                <tr>
                    <th>
                        <c:out value="${udf.name}"/>
                    </th>
                    <td>
                        <span style="text-align: center">
                            <c:choose>
                                <c:when test="${udf.canView}">
                                    <html:img src="${contextPath}${ImageServlet}/cssimages/ico.checked.gif"/>
                                </c:when>
                                <c:otherwise>
                                    <html:img src="${contextPath}${ImageServlet}/cssimages/ico.unchecked.gif"/>
                                </c:otherwise>
                            </c:choose>
                        </span>
                    </td>
                    <td>
                        <span style="text-align: center">
                            <c:choose>
                                <c:when test="${udf.canEdit}">
                                    <html:img src="${contextPath}${ImageServlet}/cssimages/ico.checked.gif"/>
                                </c:when>
                                <c:otherwise>
                                    <html:img src="${contextPath}${ImageServlet}/cssimages/ico.unchecked.gif"/>
                                </c:otherwise>
                            </c:choose>
                        </span>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </div>
</c:if>
</div>
</div>
</tiles:put>
</tiles:insert>
