<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/tiles-el" prefix="tiles" %>
<c:set var="taskMenu" value="false"/>
<c:set var="userMenu" value="true"/>
<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value="${sc.timezone}"/>
<I18n:setBundle basename="language"/>
<tiles:insert page="/jsp/layout/ListLayout.jsp" flush="true">
<tiles:put name="header" value="/jsp/user/UserHeader.jsp"/>
<tiles:put name="customHeader" value="/jsp/user/status/StatusHeader.jsp"/>
<tiles:put name="tabs" value="/jsp/user/status/StatusSubMenu.jsp"/>
<tiles:put name="main" type="string">
<c:if test="${canView}">

<div class="nblueborder">
<div class="ncaption"></div>
<c:if test="${tabEdit.allowed}">
    <div class="controlPanel">
        <html:link href="${contextPath}/UserStatusEditAction.do?method=page&id=${id}&prstatusId=${currentPrstatus.id}">
            <html:img src="${contextPath}${ImageServlet}/cssimages/ico.edit.gif"/>
            <I18n:message key="PRSTATUS_EDIT"/>
        </html:link>

    </div>
</c:if>

<div class="indent">
<html:form method="POST" action="/UserStatusViewAction" styleId="checkunload"
           onsubmit="return validate(this);">
<html:hidden property="method" value="editUserStatus"/>
<html:hidden property="session" value="${session}"/>
<html:hidden property="prstatusId" value="${prstatusId}"/>
<html:hidden property="id" value="${id}"/>
<div class="general">
    <table class="general" cellpadding="0" cellspacing="0">
        <COLGROUP>
            <COL class="col_1">
            <COL class="col_2">
        </COLGROUP>
        <caption>
            <I18n:message key="PRSTATUS_PROPERTIES"/>
        </caption>
        <tr>
            <th>
                <I18n:message key="NAME"/>
            </th>
            <td>
                <c:out value="${currentPrstatus.name}" escapeXml="true"/>
            </td>
        </tr>
        <tr>
            <th>
                <I18n:message key="OWNER"/>
            </th>
            <td>
        <span class="user" ${currentPrstatus.owner.id eq sc.userId ? "id='loggedUser'" : ""}>
            <html:img styleClass="icon" border="0"
                      src="${contextPath}${ImageServlet}/cssimages/${currentPrstatus.owner.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
            <c:out value="${currentPrstatus.owner.name}" escapeXml="true"/>
        </span>
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

<div class="general">
    <table class="general" cellpadding="0" cellspacing="0">
        <caption>
            <I18n:message key="PRSTATUS_USAGE"/>
        </caption>
        <tr class="wide">
            <th>
                <I18n:message key="CONNECTED_TO"/>
            </th>
            <th>
                <I18n:message key="OWNER"/>
            </th>
        </tr>
        <c:choose>
            <c:when test="${(!empty acls) || (!empty userAcls)}">
                <c:forEach var="acl" items="${acls}" varStatus="varCounter">
                    <tr class="line<c:out value="${varCounter.index mod 2}"/>">
                        <td>
                            <html:link styleClass="internal"
                                       href="${contextPath}/TaskViewAction.do?method=page&amp;id=${acl.task.id}">
                                <html:img styleClass="icon" border="0"
                                          src="${contextPath}${ImageServlet}/icons/categories/${acl.task.category.icon}"/>
                                <c:out value="${acl.task.name}"/>&nbsp;[#<c:out value="${acl.task.number}"/>]&nbsp;(<c:out value="${acl.user != null ? acl.user.name : acl.prstatus.name}"/>)
                            </html:link>
                        </td>
                        <td>
                            <span class="user" ${acl.owner.id eq sc.userId ? "id='loggedUser'" : ""}>
            <html:img styleClass="icon" border="0"
                      src="${contextPath}${ImageServlet}/cssimages/${acl.owner.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
            <c:out value="${acl.owner.name}" escapeXml="true"/>
            </span>
                        </td>
                    </tr>
                </c:forEach>
                <c:forEach var="acl" items="${userAcls}" varStatus="varCounter">
                    <tr class="line<c:out value="${varCounter.index mod 2}"/>">
                        <td>
                            <span class="user" ${acl.toUser.id eq sc.userId ? "id='loggedUser'" : ""}>
            <html:img styleClass="icon" border="0"
                      src="${contextPath}${ImageServlet}/cssimages/${acl.toUser.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
            <c:out value="${acl.toUser.name}" escapeXml="true"/>
            </span>
                        </td>
                        <td>
                            <span class="user" ${acl.owner.id eq sc.userId ? "id='loggedUser'" : ""}>
            <html:img styleClass="icon" border="0"
                      src="${contextPath}${ImageServlet}/cssimages/${acl.owner.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
            <c:out value="${acl.owner.name}" escapeXml="true"/>
            </span>
                        </td>
                    </tr>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <tr class="wide">
                    <td colspan="2">
                        <center>
                            <I18n:message key="EMPTY_ACL_LIST"/>
                        </center>
                    </td>
                </tr>
            </c:otherwise>
        </c:choose>
    </table>
</div>

<div class="general">
    <table class="general" cellpadding="0" cellspacing="0">
        <caption>
            <I18n:message key="NOTIFICATIONS_VIEW"/>
        </caption>
        <tr class="wide">
            <th>
                <I18n:message key="NAME"/>
            </th>
            <th>
                <I18n:message key="CONNECTED_TO"/>
            </th>
            <th>
                <I18n:message key="FILTER"/>
            </th>
            <th>
                <I18n:message key="EMAIL_TYPE"/>
            </th>
        </tr>
        <c:choose>
            <c:when test="${!empty notifications}">
                <c:forEach var="notification" items="${notifications}" varStatus="varCounter">
                    <tr class="line<c:out value="${varCounter.index mod 2}"/>">
                        <td>
                            <c:out value="${notification.name}"/>
                        </td>
                        <td>
                            <html:img styleClass="icon" border="0"
                                      src="${contextPath}${ImageServlet}/icons/categories/${notification.task.category.icon}"/>
                            <c:out value="${notification.task.name}" escapeXml="true"/>
                        </td>
                        <td>
                            <c:out value="${notification.filter.name}"/>
                        </td>
                        <td>
                            <c:out value="${notification.template}"/>
                        </td>

                    </tr>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <tr class="wide">
                    <td colspan="4">
                        <center>
                            <I18n:message key="EMPTY_PRSTATUS_NOTIFICATION_LIST"/>
                        </center>
                    </td>
                </tr>
            </c:otherwise>
        </c:choose>

    </table>
</div>


<div class="general">
    <table class="general" cellpadding="0" cellspacing="0">
        <caption>
            <I18n:message key="SUBSCRIPTIONS_LIST"/>
        </caption>
        <tr class="wide">
            <th>
                <I18n:message key="NAME"/>
            </th>
            <th>
                <I18n:message key="CONNECTED_TO"/>
            </th>
            <th>
                <I18n:message key="FILTER"/>
            </th>
            <th>
                <I18n:message key="EMAIL_TYPE"/>
            </th>
            <th>
                <I18n:message key="VALID_TIME"/>
            </th>
            <th>
                <I18n:message key="NEXT_RUN"/>
            </th>
            <th>
                <I18n:message key="INTERVAL"/>
            </th>
        </tr>
        <c:choose>
            <c:when test="${!empty subscriptions}">
                <c:forEach var="subscription" items="${subscriptions}" varStatus="varCounter">
                    <tr class="line<c:out value="${varCounter.index mod 2}"/>">
                        <td>
                            <c:out value="${subscription.name}" escapeXml="true"/>
                        </td>
                        <td>
                            <html:img styleClass="icon" border="0"
                                      src="${contextPath}${ImageServlet}/icons/categories/${subscription.task.category.icon}"/>
                            <c:out value="${subscription.task.name}" escapeXml="true"/>
                        </td>
                        <td>
                            <c:out value="${subscription.filter.name}"/>
                        </td>
                        <td>
                            <c:out value="${subscription.template}"/>
                        </td>
                        <td>
                            <c:if test="${subscription.startdate != null}">
                                <I18n:message key="FROM"/>
                                <I18n:formatDate value="${subscription.startdate.time}" type="both" dateStyle="short" timeStyle="short"/>
                            </c:if>
                            /
                            <c:if test="${subscription.stopdate != null}">
                                <I18n:message key="TO"/>
                                <I18n:formatDate value="${subscription.stopdate.time}" type="both" dateStyle="short" timeStyle="short"/>
                            </c:if>
                        </td>
                        <td>
                            <c:if test="${subscription.nextrun != null}">
                                <I18n:formatDate value="${subscription.nextrun.time}" type="both" dateStyle="short" timeStyle="short"/>
                            </c:if>
                        </td>
                        <td>
                            <c:out value="${subscription.interval}" escapeXml="true"/>
                        </td>
                    </tr>

                </c:forEach>
            </c:when>
            <c:otherwise>
                <tr class="wide">
                    <td colspan="7">
                        <center>
                            <I18n:message key="EMPTY_PRSTATUS_SUBSCRIPTION_LIST"/>
                        </center>
                    </td>
                </tr>
            </c:otherwise>
        </c:choose>

    </table>
</div>

<div class="general">
    <table class="general" cellpadding="0" cellspacing="0">
        <caption>
            <I18n:message key="PRSTATUS_CATEGORIES_PERMISSIONS"/>
        </caption>
        <COLGROUP>
            <COL class="col_1">
            <COL class="col_2">
        </COLGROUP>
        <tr>
            <th>
                <I18n:message key="CAN_VIEW"/>
            </th>
            <td>
                <c:forEach var="category" items="${categoryViewAll}" varStatus="varC">
                    <html:img src="${contextPath}${ImageServlet}/cssimages/ico.categories.gif" hspace="0" vspace="0" border="0" align="middle"/>
                    <c:if test="${category.canManage && !category.isValid}">
                        <img border="0" hspace="0" vspace="0" title="<I18n:message key="CATEGORY_INVALID"/>"
                             src="<c:out value="${contextPath}"/>${ImageServlet}/cssimages/warning.gif" class="icon"/>
                    </c:if>
                    <c:out value="${category.name}" escapeXml="true"/><c:if test="${!varC.last}">,</c:if>
                </c:forEach>
                <c:forEach var="category" items="${categoryViewSubmitter}" varStatus="varC">
                    <html:img src="${contextPath}${ImageServlet}/cssimages/ico.categories.gif" hspace="0" vspace="0" border="0" align="middle"/>
                    <c:if test="${category.canManage && !category.isValid}">
                        <img border="0" hspace="0" vspace="0" title="<I18n:message key="CATEGORY_INVALID"/>"
                             src="<c:out value="${contextPath}"/>${ImageServlet}/cssimages/warning.gif" class="icon"/>
                    </c:if>
                    <c:out value="${category.name}" escapeXml="true"/> (* <I18n:message key="SUBMITTER"/>)<c:if test="${!varC.last}">,</c:if>
                </c:forEach>
            </td>
        </tr>

        <tr>
            <th>
                <I18n:message key="CAN_CREATE"/>
            </th>
            <td>
                <c:forEach var="category" items="${categoryCreateAll}" varStatus="varC">
                    <html:img src="${contextPath}${ImageServlet}/cssimages/ico.categories.gif" hspace="0" vspace="0" border="0" align="middle"/>
                    <c:if test="${category.canManage && !category.isValid}">
                        <img border="0" hspace="0" vspace="0" title="<I18n:message key="CATEGORY_INVALID"/>"
                             src="<c:out value="${contextPath}"/>${ImageServlet}/cssimages/warning.gif" class="icon"/>
                    </c:if>
                    <c:out value="${category.name}" escapeXml="true"/><c:if test="${!varC.last}">,</c:if>
                </c:forEach>
                <c:forEach var="category" items="${categoryCreateHandler}" varStatus="varC">
                    <html:img src="${contextPath}${ImageServlet}/cssimages/ico.categories.gif" hspace="0" vspace="0" border="0" align="middle"/>
                    <c:if test="${category.canManage && !category.isValid}">
                        <img border="0" hspace="0" vspace="0" title="<I18n:message key="CATEGORY_INVALID"/>"
                             src="<c:out value="${contextPath}"/>${ImageServlet}/cssimages/warning.gif" class="icon"/>
                    </c:if>
                    <c:out value="${category.name}" escapeXml="true"/>  (* <I18n:message key="HANDLER"/>)<c:if test="${!varC.last}">,</c:if>
                </c:forEach>
                <c:forEach var="category" items="${categoryCreateSubmitter}" varStatus="varC">
                    <html:img src="${contextPath}${ImageServlet}/cssimages/ico.categories.gif" hspace="0" vspace="0" border="0" align="middle"/>
                    <c:if test="${category.canManage && !category.isValid}">
                        <img border="0" hspace="0" vspace="0" title="<I18n:message key="CATEGORY_INVALID"/>"
                             src="<c:out value="${contextPath}"/>${ImageServlet}/cssimages/warning.gif" class="icon"/>
                    </c:if>
                    <c:out value="${category.name}" escapeXml="true"/>  (* <I18n:message key="SUBMITTER"/>)<c:if test="${!varC.last}">,</c:if>
                </c:forEach>
                <c:forEach var="category" items="${categoryCreateSAH}" varStatus="varC">
                    <html:img src="${contextPath}${ImageServlet}/cssimages/ico.categories.gif" hspace="0" vspace="0" border="0" align="middle"/>
                    <c:if test="${category.canManage && !category.isValid}">
                        <img border="0" hspace="0" vspace="0" title="<I18n:message key="CATEGORY_INVALID"/>"
                             src="<c:out value="${contextPath}"/>${ImageServlet}/cssimages/warning.gif" class="icon"/>
                    </c:if>
                    <c:out value="${category.name}" escapeXml="true"/>  (* <I18n:message key="SUBMITTER"/>, <I18n:message key="HANDLER"/>)<c:if test="${!varC.last}">,</c:if>
                </c:forEach>
            </td>
        </tr>
        <tr>
            <th>
                <I18n:message key="CAN_EDIT"/>
            </th>
            <td>
                <c:forEach var="category" items="${categoryEditAll}" varStatus="varC">
                    <html:img src="${contextPath}${ImageServlet}/cssimages/ico.categories.gif" hspace="0" vspace="0" border="0" align="middle"/>
                    <c:if test="${category.canManage && !category.isValid}">
                        <img border="0" hspace="0" vspace="0" title="<I18n:message key="CATEGORY_INVALID"/>"
                             src="<c:out value="${contextPath}"/>${ImageServlet}/cssimages/warning.gif" class="icon"/>
                    </c:if>
                    <c:out value="${category.name}" escapeXml="true"/><c:if test="${!varC.last}">,</c:if>
                </c:forEach>
                <c:forEach var="category" items="${categoryEditHandler}" varStatus="varC">
                    <html:img src="${contextPath}${ImageServlet}/cssimages/ico.categories.gif" hspace="0" vspace="0" border="0" align="middle"/>
                    <c:if test="${category.canManage && !category.isValid}">
                        <img border="0" hspace="0" vspace="0" title="<I18n:message key="CATEGORY_INVALID"/>"
                             src="<c:out value="${contextPath}"/>${ImageServlet}/cssimages/warning.gif" class="icon"/>
                    </c:if>
                    <c:out value="${category.name}" escapeXml="true"/>  (* <I18n:message key="HANDLER"/>)<c:if test="${!varC.last}">,</c:if>
                </c:forEach>
                <c:forEach var="category" items="${categoryEditSubmitter}" varStatus="varC">
                    <html:img src="${contextPath}${ImageServlet}/cssimages/ico.categories.gif" hspace="0" vspace="0" border="0" align="middle"/>
                    <c:if test="${category.canManage && !category.isValid}">
                        <img border="0" hspace="0" vspace="0" title="<I18n:message key="CATEGORY_INVALID"/>"
                             src="<c:out value="${contextPath}"/>${ImageServlet}/cssimages/warning.gif" class="icon"/>
                    </c:if>
                    <c:out value="${category.name}" escapeXml="true"/>  (* <I18n:message key="SUBMITTER"/>)<c:if test="${!varC.last}">,</c:if>
                </c:forEach>
                <c:forEach var="category" items="${categoryEditSAH}" varStatus="varC">
                    <html:img src="${contextPath}${ImageServlet}/cssimages/ico.categories.gif" hspace="0" vspace="0" border="0" align="middle"/>
                    <c:if test="${category.canManage && !category.isValid}">
                        <img border="0" hspace="0" vspace="0" title="<I18n:message key="CATEGORY_INVALID"/>"
                             src="<c:out value="${contextPath}"/>${ImageServlet}/cssimages/warning.gif" class="icon"/>
                    </c:if>
                    <c:out value="${category.name}" escapeXml="true"/>  (* <I18n:message key="SUBMITTER"/>, <I18n:message key="HANDLER"/>)<c:if test="${!varC.last}">,</c:if>
                </c:forEach>
            </td>
        </tr>
        <tr>
            <th>
                <I18n:message key="CAN_DELETE"/>
            </th>
            <td>
                <c:forEach var="category" items="${categoryDeleteAll}" varStatus="varC">
                    <html:img src="${contextPath}${ImageServlet}/cssimages/ico.categories.gif" hspace="0" vspace="0" border="0" align="middle"/>
                    <c:if test="${category.canManage && !category.isValid}">
                        <img border="0" hspace="0" vspace="0" title="<I18n:message key="CATEGORY_INVALID"/>"
                             src="<c:out value="${contextPath}"/>${ImageServlet}/cssimages/warning.gif" class="icon"/>
                    </c:if>
                    <c:out value="${category.name}" escapeXml="true"/><c:if test="${!varC.last}">,</c:if>
                </c:forEach>
                <c:forEach var="category" items="${categoryDeleteHandler}" varStatus="varC">
                    <html:img src="${contextPath}${ImageServlet}/cssimages/ico.categories.gif" hspace="0" vspace="0" border="0" align="middle"/>
                    <c:if test="${category.canManage && !category.isValid}">
                        <img border="0" hspace="0" vspace="0" title="<I18n:message key="CATEGORY_INVALID"/>"
                             src="<c:out value="${contextPath}"/>${ImageServlet}/cssimages/warning.gif" class="icon"/>
                    </c:if>
                    <c:out value="${category.name}" escapeXml="true"/>  (* <I18n:message key="HANDLER"/>)<c:if test="${!varC.last}">,</c:if>
                </c:forEach>
                <c:forEach var="category" items="${categoryDeleteSubmitter}" varStatus="varC">
                    <html:img src="${contextPath}${ImageServlet}/cssimages/ico.categories.gif" hspace="0" vspace="0" border="0" align="middle"/>
                    <c:if test="${category.canManage && !category.isValid}">
                        <img border="0" hspace="0" vspace="0" title="<I18n:message key="CATEGORY_INVALID"/>"
                             src="<c:out value="${contextPath}"/>${ImageServlet}/cssimages/warning.gif" class="icon"/>
                    </c:if>
                    <c:out value="${category.name}" escapeXml="true"/>  (* <I18n:message key="SUBMITTER"/>)<c:if test="${!varC.last}">,</c:if>
                </c:forEach>
                <c:forEach var="category" items="${categoryDeleteSAH}" varStatus="varC">
                    <html:img src="${contextPath}${ImageServlet}/cssimages/ico.categories.gif" hspace="0" vspace="0" border="0" align="middle"/>
                    <c:if test="${category.canManage && !category.isValid}">
                        <img border="0" hspace="0" vspace="0" title="<I18n:message key="CATEGORY_INVALID"/>"
                             src="<c:out value="${contextPath}"/>${ImageServlet}/cssimages/warning.gif" class="icon"/>
                    </c:if>
                    <c:out value="${category.name}" escapeXml="true"/>  (* <I18n:message key="SUBMITTER"/>, <I18n:message key="HANDLER"/>)<c:if test="${!varC.last}">,</c:if>
                </c:forEach>
            </td>
        </tr>
        <tr>
            <th>
                <I18n:message key="CAN_BE_HANDLER"/>
            </th>
            <td>
                <c:forEach var="category" items="${categoryBeHandlerAll}" varStatus="varC">
                    <html:img src="${contextPath}${ImageServlet}/cssimages/ico.categories.gif" hspace="0" vspace="0" border="0" align="middle"/>
                    <c:if test="${category.canManage && !category.isValid}">
                        <img border="0" hspace="0" vspace="0" title="<I18n:message key="CATEGORY_INVALID"/>"
                             src="<c:out value="${contextPath}"/>${ImageServlet}/cssimages/warning.gif" class="icon"/>
                    </c:if>
                    <c:out value="${category.name}" escapeXml="true"/><c:if test="${!varC.last}">,</c:if>
                </c:forEach>
                <c:forEach var="category" items="${categoryBeHandlerHandler}" varStatus="varC">
                    <html:img src="${contextPath}${ImageServlet}/cssimages/ico.categories.gif" hspace="0" vspace="0" border="0" align="middle"/>
                    <c:if test="${category.canManage && !category.isValid}">
                        <img border="0" hspace="0" vspace="0" title="<I18n:message key="CATEGORY_INVALID"/>"
                             src="<c:out value="${contextPath}"/>${ImageServlet}/cssimages/warning.gif" class="icon"/>
                    </c:if>
                    <c:out value="${category.name}" escapeXml="true"/>  (* <I18n:message key="HANDLER"/>)<c:if test="${!varC.last}">,</c:if>
                </c:forEach>
                <c:forEach var="category" items="${categoryBeHandlerSubmitter}" varStatus="varC">
                    <html:img src="${contextPath}${ImageServlet}/cssimages/ico.categories.gif" hspace="0" vspace="0" border="0" align="middle"/>
                    <c:if test="${category.canManage && !category.isValid}">
                        <img border="0" hspace="0" vspace="0" title="<I18n:message key="CATEGORY_INVALID"/>"
                             src="<c:out value="${contextPath}"/>${ImageServlet}/cssimages/warning.gif" class="icon"/>
                    </c:if>
                    <c:out value="${category.name}" escapeXml="true"/>  (* <I18n:message key="SUBMITTER"/>)<c:if test="${!varC.last}">,</c:if>
                </c:forEach>
                <c:forEach var="category" items="${categoryBeHandlerSAH}" varStatus="varC">
                    <html:img src="${contextPath}${ImageServlet}/cssimages/ico.categories.gif" hspace="0" vspace="0" border="0" align="middle"/>
                    <c:if test="${category.canManage && !category.isValid}">
                        <img border="0" hspace="0" vspace="0" title="<I18n:message key="CATEGORY_INVALID"/>"
                             src="<c:out value="${contextPath}"/>${ImageServlet}/cssimages/warning.gif" class="icon"/>
                    </c:if>
                    <c:out value="${category.name}" escapeXml="true"/>  (* <I18n:message key="SUBMITTER"/>, <I18n:message key="HANDLER"/>)<c:if test="${!varC.last}">,</c:if>
                </c:forEach>
            </td>
        </tr>

    </table>
</div>


<div class="blueborder">
    <div class="caption">
        <I18n:message key="PRSTATUS_TASK_FIELDS_PERMISSIONS"/>
    </div>
    <div class="indent">
        <ul>
            <li class="${editTaskAlias ? 'lichecked' : 'liunchecked'}">
                <I18n:message key="Action.editTaskAlias"/>
            </li>
            <li class="${viewTaskResolution ? 'lichecked' : 'liunchecked'}">
                <I18n:message key="Action.viewTaskResolution"/>
            </li>
            <li class="${viewTaskPriority ? 'lichecked' : 'liunchecked'}">
                <I18n:message key="Action.viewTaskPriority"/>
            </li>
            <li class="${editTaskPriority ? 'lichecked' : 'liunchecked'}">
                <I18n:message key="Action.editTaskPriority"/>
            </li>
            <li class="${editTaskHandler ? 'lichecked' : 'liunchecked'}">
                <I18n:message key="Action.editTaskHandler"/>
            </li>
            <li class="${viewTaskSubmitDate ? 'lichecked' : 'liunchecked'}">
                <I18n:message key="Action.viewTaskSubmitDate"/>
            </li>
            <li class="${viewTaskLastUpdated ? 'lichecked' : 'liunchecked'}">
                <I18n:message key="Action.viewTaskLastUpdated"/>
            </li>
            <li class="${viewTaskCloseDate ? 'lichecked' : 'liunchecked'}">
                <I18n:message key="Action.viewTaskCloseDate"/>
            </li>
            <li class="${viewTaskDeadline ? 'lichecked' : 'liunchecked'}">
                <I18n:message key="Action.viewTaskDeadline"/>
            </li>
            <li class="${editTaskDeadline ? 'lichecked' : 'liunchecked'}">
                <I18n:message key="Action.editTaskDeadline"/>
            </li>
            <li class="${viewTaskBudget ? 'lichecked' : 'liunchecked'}">
                <I18n:message key="Action.viewTaskBudget"/>
            </li>
            <li class="${editTaskBudget ? 'lichecked' : 'liunchecked'}">
                <I18n:message key="Action.editTaskBudget"/>
            </li>
            <li class="${viewTaskActualBudget ? 'lichecked' : 'liunchecked'}">
                <I18n:message key="Action.viewTaskActualBudget"/>
            </li>
            <li class="${editTaskActualBudget ? 'lichecked' : 'liunchecked'}">
                <I18n:message key="Action.editTaskActualBudget"/>
            </li>
            <li class="${viewTaskDescription ? 'lichecked' : 'liunchecked'}">
                <I18n:message key="Action.viewTaskDescription"/>
            </li>
            <li class="${editTaskDescription ? 'lichecked' : 'liunchecked'}">
                <I18n:message key="Action.editTaskDescription"/>
            </li>
        </ul>
    </div>



</div>
<br>
<div class="general">
    <table class="general" cellpadding="0" cellspacing="0">
        <caption>
            <I18n:message key="PRSTATUS_TASK_CUSTOM_FIELDS_PERMISSIONS"/>
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
                <c:forEach items="${taskUdfViewAll}" var="v" varStatus="c">
                            <span style="white-space: nowrap;"><html:img styleClass="icon" border="0"
                                                                         src="${contextPath}${ImageServlet}/cssimages/ico.customfields.gif"/>
                                <c:out value="${v.captionEx}"/><c:if
                                        test="${!c.last}">,
                                </c:if></span>
                </c:forEach>
                <c:forEach items="${taskUdfViewHandler}" var="v" varStatus="c">
                            <span style="white-space: nowrap;"><html:img styleClass="icon" border="0"
                                                                         src="${contextPath}${ImageServlet}/cssimages/ico.customfields.gif"/>
                                <c:out value="${v.captionEx}"/> (* <I18n:message key="HANDLER"/>)<c:if
                                        test="${!c.last}">,
                                </c:if></span>
                </c:forEach>
                <c:forEach items="${taskUdfViewSubmitter}" var="v" varStatus="c">
                            <span style="white-space: nowrap;">
                                <html:img styleClass="icon" border="0"
                                          src="${contextPath}${ImageServlet}/cssimages/ico.customfields.gif"/>
                                <c:out value="${v.captionEx}"/> (* <I18n:message
                                    key="SUBMITTER"/>)<c:if test="${!c.last}">,</c:if></span>
                </c:forEach>
                <c:forEach items="${taskUdfViewSAH}" var="v" varStatus="c">
                            <span style="white-space: nowrap;"><html:img styleClass="icon" border="0"
                                                                         src="${contextPath}${ImageServlet}/cssimages/ico.customfields.gif"/><c:out
                                    value="${v.captionEx}"/> (* <I18n:message
                                    key="SUBMITTER"/>, <I18n:message key="HANDLER"/>)<c:if test="${!c.last}">,
                            </c:if></span>
                </c:forEach>
            </td>
        </tr>
        <tr>
            <th>
                <I18n:message key="CAN_EDIT"/>
            </th>
            <td>
                <c:forEach items="${taskUdfEditAll}" var="v" varStatus="c">
                            <span style="white-space: nowrap;"><html:img styleClass="icon" border="0"
                                                                         src="${contextPath}${ImageServlet}/cssimages/ico.customfields.gif"/>
                                <c:out value="${v.captionEx}"/><c:if
                                        test="${!c.last}">,
                                </c:if></span>
                </c:forEach>
                <c:forEach items="${taskUdfEditHandler}" var="v" varStatus="c">
                            <span style="white-space: nowrap;"><html:img styleClass="icon" border="0"
                                                                         src="${contextPath}${ImageServlet}/cssimages/ico.customfields.gif"/>
                                <c:out value="${v.captionEx}"/> (* <I18n:message key="HANDLER"/>)<c:if
                                        test="${!c.last}">,
                                </c:if></span>
                </c:forEach>
                <c:forEach items="${taskUdfEditSubmitter}" var="v" varStatus="c">
                            <span style="white-space: nowrap;">
                                <html:img styleClass="icon" border="0"
                                          src="${contextPath}${ImageServlet}/cssimages/ico.customfields.gif"/>
                                <c:out value="${v.captionEx}"/> (* <I18n:message
                                    key="SUBMITTER"/>)<c:if test="${!c.last}">,</c:if></span>
                </c:forEach>
                <c:forEach items="${taskUdfEditSAH}" var="v" varStatus="c">
                            <span style="white-space: nowrap;"><html:img styleClass="icon" border="0"
                                                                         src="${contextPath}${ImageServlet}/cssimages/ico.customfields.gif"/><c:out
                                    value="${v.captionEx}"/> (* <I18n:message
                                    key="SUBMITTER"/>, <I18n:message key="HANDLER"/>)<c:if test="${!c.last}">,
                            </c:if></span>
                </c:forEach>
            </td>
        </tr>
    </table>
</div>
<br>
<div class="blueborder">
    <div class="caption">
        <I18n:message key="PRSTATUS_USER_FIELDS_PERMISSIONS"/>
    </div>
    <div class="indent">
        <ul>
            <li class="${viewUserCompany ? 'lichecked' : 'liunchecked'}">
                <I18n:message key="Action.viewUserCompany"/>
            </li>
            <li class="${editUserCompany ? 'lichecked' : 'liunchecked'}">
                <I18n:message key="Action.editUserCompany"/>
            </li>
            <li class="${editUserStatus ? 'lichecked' : 'liunchecked'}">
                <I18n:message key="Action.editUserStatus"/>
            </li>
            <li class="${editUserEmail ? 'lichecked' : 'liunchecked'}">
                <I18n:message key="Action.editUserEmail"/>
            </li>
            <li class="${viewUserPhone ? 'lichecked' : 'liunchecked'}">
                <I18n:message key="Action.viewUserPhone"/>
            </li>
            <li class="${editUserPhone ? 'lichecked' : 'liunchecked'}">
                <I18n:message key="Action.editUserPhone"/>
            </li>
            <li class="${editUserLocale ? 'lichecked' : 'liunchecked'}">
                <I18n:message key="Action.editUserLocale"/>
            </li>
            <li class="${editUserTimezone ? 'lichecked' : 'liunchecked'}">
                <I18n:message key="Action.editUserTimezone"/>
            </li>
            <li class="${editUserExpireDate ? 'lichecked' : 'liunchecked'}">
                <I18n:message key="Action.editUserExpireDate"/>
            </li>
            <li class="${editUserLicensed ? 'lichecked' : 'liunchecked'}">
                <I18n:message key="Action.editUserLicensed"/>
            </li>
            <li class="${editUserDefaultProject ? 'lichecked' : 'liunchecked'}">
                <I18n:message key="Action.editUserDefaultProject"/>
            </li>
            <li class="${editUserEmailType ? 'lichecked' : 'liunchecked'}">
                <I18n:message key="Action.editUserEmailType"/>
            </li>
            <li class="${editUserActive ? 'lichecked' : 'liunchecked'}">
                <I18n:message key="Action.editUserActive"/>
            </li>
        </ul>
    </div>

</div>
<br>
<div class="general">
    <table class="general" cellpadding="0" cellspacing="0">
        <caption>
            <I18n:message key="PRSTATUS_USER_CUSTOM_FIELDS_PERMISSIONS"/>
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
                <c:forEach items="${userUdfViewAll}" var="v" varStatus="c">
                            <span style="white-space: nowrap;"><html:img styleClass="icon" border="0"
                                                                         src="${contextPath}${ImageServlet}/cssimages/ico.customfields.gif"/>
                                <c:out value="${v.caption}"/><c:if
                                        test="${!c.last}">,
                                </c:if></span>
                </c:forEach>

            </td>
        </tr>
        <tr>
            <th>
                <I18n:message key="CAN_EDIT"/>
            </th>
            <td>
                <c:forEach items="${userUdfEditAll}" var="v" varStatus="c">
                            <span style="white-space: nowrap;"><html:img styleClass="icon" border="0"
                                                                         src="${contextPath}${ImageServlet}/cssimages/ico.customfields.gif"/>
                                <c:out value="${v.caption}"/><c:if
                                        test="${!c.last}">,
                                </c:if></span>
                </c:forEach>

            </td>
        </tr>
    </table>
</div>
<br>
<div class="blueborder">
    <div class="caption">
        <I18n:message key="PRSTATUS_TASK_ACTIONS_PERMISSIONS"/>
    </div>
    <div class="indent">
        <ul>

            <li class="${manageRegistrations ? 'lichecked': 'liunchecked'}">
                <I18n:message key="Action.manageRegistrations"/>
            </li>
            <li class="${cutCopyPasteTask ? 'lichecked': 'liunchecked'}">
                <I18n:message key="Action.cutCopyPasteTask"/>
            </li>
            <li class="${deleteOperations ? 'lichecked': 'liunchecked'}">
                <I18n:message key="Action.deleteOperations"/>
            </li>
            <li class="${bulkProcessingTask ? 'lichecked': 'liunchecked'}">
                <I18n:message key="Action.bulkProcessingTask"/>
            </li>
            <li class="${viewTaskAttachments ? 'lichecked': 'liunchecked'}">
                <I18n:message key="Action.viewTaskAttachments"/>
            </li>
            <li class="${createTaskAttachments ? 'lichecked': 'liunchecked'}">
                <I18n:message key="Action.createTaskAttachments"/>
            </li>
            <li class="${manageTaskAttachments ? 'lichecked': 'liunchecked'}">
                <I18n:message key="Action.manageTaskAttachments"/>
            </li>
            <li class="${createTaskMessageAttachments ? 'lichecked': 'liunchecked'}">
                <I18n:message key="Action.createTaskMessageAttachments"/>
            </li>
            <li class="${manageTaskMessageAttachments ? 'lichecked': 'liunchecked'}">
                <I18n:message key="Action.manageTaskMessageAttachments"/>
            </li>
            <li class="${viewFilters ? 'lichecked': 'liunchecked'}">
                <I18n:message key="Action.viewFilters"/>
            </li>
            <li class="${manageTaskPrivateFilters ? 'lichecked': 'liunchecked'}">
                <I18n:message key="Action.manageTaskPrivateFilters"/>
            </li>
            <li class="${manageTaskPublicFilters ? 'lichecked': 'liunchecked'}">
                <I18n:message key="Action.manageTaskPublicFilters"/>
            </li>
            <li class="${viewReports ? 'lichecked': 'liunchecked'}">
                <I18n:message key="Action.viewReports"/>
            </li>
            <li class="${managePrivateReports ? 'lichecked': 'liunchecked'}">
                <I18n:message key="Action.managePrivateReports"/>
            </li>
            <li class="${managePublicReports ? 'lichecked': 'liunchecked'}">
                <I18n:message key="Action.managePublicReports"/>
            </li>
            <li class="${manageEmailSchedules ? 'lichecked': 'liunchecked'}">
                <I18n:message key="Action.manageEmailSchedules"/>
            </li>
            <li class="${manageTaskACLs ? 'lichecked': 'liunchecked'}">
                <I18n:message key="Action.manageTaskACLs"/>
            </li>
            <li class="${manageTaskUDFs ? 'lichecked': 'liunchecked'}">
                <I18n:message key="Action.manageTaskUDFs"/>
            </li>
            <li class="${manageEmailImportRules ? 'lichecked': 'liunchecked'}">
                <I18n:message key="Action.manageEmailImportRules"/>
            </li>
            <li class="${manageTaskTemplates ? 'lichecked': 'liunchecked'}">
                <I18n:message key="Action.manageTaskTemplates"/>
            </li>
            <li class="${manageCategories ? 'lichecked': 'liunchecked'}">
                <I18n:message key="Action.manageCategories"/>
            </li>
            <li class="${manageWorkflows ? 'lichecked': 'liunchecked'}">
                <I18n:message key="Action.manageWorkflows"/>
            </li>
        </ul>
    </div>
</div>
<br>
<div class="blueborder">

    <div class="caption">
        <I18n:message key="PRSTATUS_USER_ACTIONS_PERMISSIONS"/>
    </div>
    <div class="indent">
        <ul>
            <li class="${editUserHimself ? 'lichecked' : 'liunchecked'}">
                <I18n:message key="Action.editUserHimself"/>
            </li>
            <li class="${editUserChildren ? 'lichecked' : 'liunchecked'}">
                <I18n:message key="Action.editUserChildren"/>
            </li>
            <li class="${createUser ? 'lichecked' : 'liunchecked'}">
                <I18n:message key="Action.createUser"/>
            </li>
            <li class="${deleteUser ? 'lichecked' : 'liunchecked'}">
                <I18n:message key="Action.deleteUser"/>
            </li>
            <li class="${cutPasteUser ? 'lichecked' : 'liunchecked'}">
                <I18n:message key="Action.cutPasteUser"/>
            </li>
            <li class="${editUserPasswordHimself ? 'lichecked' : 'liunchecked'}">
                <I18n:message key="Action.editUserPasswordHimself"/>
            </li>
            <li class="${editUserChildrenPassword ? 'lichecked' : 'liunchecked'}">
                <I18n:message key="Action.editUserChildrenPassword"/>
            </li>
            <li class="${viewUserFilters ? 'lichecked' : 'liunchecked'}">
                <I18n:message key="Action.viewUserFilters"/>
            </li>
            <li class="${manageUserPrivateFilters ? 'lichecked' : 'liunchecked'}">
                <I18n:message key="Action.manageUserPrivateFilters"/>
            </li>
            <li class="${manageUserPublicFilters ? 'lichecked' : 'liunchecked'}">
                <I18n:message key="Action.manageUserPublicFilters"/>
            </li>
            <li class="${manageUserACLs ? 'lichecked' : 'liunchecked'}">
                <I18n:message key="Action.manageUserACLs"/>
            </li>
            <li class="${manageUserUDFs ? 'lichecked' : 'liunchecked'}">
                <I18n:message key="Action.manageUserUDFs"/>
            </li>
            <li class="${manageRoles ? 'lichecked' : 'liunchecked'}">
                <I18n:message key="Action.manageRoles"/>
            </li>
            <li class="${viewUserAttachments ? 'lichecked' : 'liunchecked'}">
                <I18n:message key="Action.viewUserAttachments"/>
            </li>
            <li class="${createUserAttachments ? 'lichecked' : 'liunchecked'}">
                <I18n:message key="Action.createUserAttachments"/>
            </li>
            <li class="${manageUserAttachments ? 'lichecked' : 'liunchecked'}">
                <I18n:message key="Action.manageUserAttachments"/>
            </li>
        </ul>
    </div>
</div>
</html:form>
</div>
</div>
</c:if>
</tiles:put>
</tiles:insert>
