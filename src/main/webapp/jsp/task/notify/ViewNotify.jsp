<%@ page buffer="128kb" errorPage="/jsp/Error.jsp" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/tiles-el" prefix="tiles" %>


<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value="${sc.timezone}"/>
<I18n:setBundle basename="language"/>
<tiles:insert page="/jsp/layout/ListLayout.jsp" flush="true">
<tiles:put name="title" value="${title}"/>
<tiles:put name="header" value="/jsp/task/TaskHeader.jsp"/>
<tiles:put name="customHeader" value="/jsp/task/notify/NotifyHeader.jsp"/>

        <tiles:put name="tabs" type="string"/>
<tiles:put name="main" type="string">

<div class="blueborder">
<div class="caption">
    <I18n:message key="NOTIFICATION_VIEW"/>
</div>

<div class="controlPanel">
    <c:if test="${canEdit}">
        <html:link
                href="${contextPath}/TaskNotifyEditAction.do?method=page&amp;id=${id}&amp;notificationId=${notification.id}">
            <html:img src="${contextPath}${ImageServlet}/cssimages/ico.edit.gif" border="0" altKey="EDIT"/>
            <I18n:message key="NOTIFICATION_EDIT"/>
        </html:link>
    </c:if>
    <c:if test="${canCreate}">
        <html:link  href="${contextPath}/TaskNotifyEditAction.do?method=page&amp;id=${id}&amp;user=${sc.userId}">
            <html:img src="${contextPath}${ImageServlet}/cssimages/ico.edit.gif" border="0" altKey="EDIT"/>
            <I18n:message key="NOTIFICATION_RULE_SELF_ADD"/>
        </html:link>
        <html:link  href="${contextPath}/TaskNotifyEditAction.do?method=page&amp;id=${id}">
            <html:img src="${contextPath}${ImageServlet}/cssimages/ico.edit.gif" border="0" altKey="EDIT"/>
            <I18n:message key="NOTIFICATION_RULE_ADD"/>
        </html:link>
    </c:if>
</div>

<div class="indent">
<div class="general">
<table class="general" cellpadding="0" cellspacing="0">
    <colgroup>
        <col class="col_1">
        <col class="col_2">
    </colgroup>
    <caption>
        <I18n:message key="NOTIFICATION_PROPERTIES"/>
    </caption>
    <c:if test="${testResult ne null}">
        <script language="javascript">
            alert('<c:out value="${testResult}" escapeXml="true"/>');
	    </script>
    </c:if>
    <tr>
        <th>
            <I18n:message key="NAME"/>
        </th>
        <td>
            <c:out value="${notification.name}" escapeXml="true"/>
        </td>
    </tr>

    <tr>
        <th>
            <I18n:message key="PRSTATUS_OR_USER"/>
        </th>
        <td>
            <c:choose>
                <c:when test="${notification.user ne null}">
			        <span class="user" ${notification.user.id eq sc.userId ? "id=\"loggedUser\"" : ""}>
                        <html:img  styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/${notification.user.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/><c:out value="${notification.user.name}" escapeXml="true"/>
			        </span>
                </c:when>
                <c:when test="${notification.group ne null}">
					<span class="user">
                        <html:img  styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/ico.status.gif"/><c:out value="${notification.group.name}" escapeXml="true"/>
				    </span>
                </c:when>
            </c:choose>
        </td>
    </tr>

    <tr>
        <th>
            <I18n:message key="FILTER"/>
        </th>
        <td>
            <c:out value="${notification.filter.name}" escapeXml="true"/>
        </td>
    </tr>

    <tr>
        <th>
            <I18n:message key="EMAIL_TYPE"/>
        </th>
        <td>
            <c:out value="${template}"/>
        </td>
    </tr>

    <tr>
        <th>
            <I18n:message key="EXECUTE_ON_NEW_TASK"/>
        </th>
        <td>
            <c:choose>
                <c:when test="${notification.fireNewTask eq true}">
                    <html:img src="${contextPath}${ImageServlet}/cssimages/ico.checked.gif"/>
                </c:when>
                <c:otherwise>
                    <html:img src="${contextPath}${ImageServlet}/cssimages/ico.unchecked.gif"/>
                </c:otherwise>
            </c:choose>
        </td>
    </tr>

    <tr>
        <th>
            <I18n:message key="EXECUTE_ON_UPDATED_TASK"/>
        </th>
        <td>
            <c:choose>
                <c:when test="${notification.fireUpdatedTask eq true}">
                    <html:img src="${contextPath}${ImageServlet}/cssimages/ico.checked.gif"/>
                </c:when>
                <c:otherwise>
                    <html:img src="${contextPath}${ImageServlet}/cssimages/ico.unchecked.gif"/>
                </c:otherwise>
            </c:choose>
        </td>
    </tr>

    <tr>
        <th>
            <I18n:message key="EXECUTE_ON_NEW_ATTACHMENT"/>
        </th>
        <td>
            <c:choose>
                <c:when test="${notification.fireNewAttachment eq true}">
                    <html:img src="${contextPath}${ImageServlet}/cssimages/ico.checked.gif"/>
                </c:when>
                <c:otherwise>
                    <html:img src="${contextPath}${ImageServlet}/cssimages/ico.unchecked.gif"/>
                </c:otherwise>
            </c:choose>
        </td>
    </tr>

    <tr>
        <th>
            <I18n:message key="EXECUTE_ON_OPERATION"/>
        </th>
        <td>
            <c:choose>
                <c:when test="${notification.fireNewMessage eq true}">
                    <html:img src="${contextPath}${ImageServlet}/cssimages/ico.checked.gif"/>
                </c:when>
                <c:otherwise>
                    <html:img src="${contextPath}${ImageServlet}/cssimages/ico.unchecked.gif"/>
                </c:otherwise>
            </c:choose>
        </td>
    </tr>

    <tr>
        <th>
            <I18n:message key="EXECUTE_ON_NOT_I"/>
        </th>
        <td>
            <c:choose>
                <c:when test="${notification.fireNotI eq true}">
                    <html:img src="${contextPath}${ImageServlet}/cssimages/ico.checked.gif"/>
                </c:when>
                <c:otherwise>
                    <html:img src="${contextPath}${ImageServlet}/cssimages/ico.unchecked.gif"/>
                </c:otherwise>
            </c:choose>
        </td>
    </tr>

    <tr>
        <th>
            <I18n:message key="CONNECTED_TO"/>
        </th>
        <td>
            <html:img styleClass="icon" border="0"
                              src="${contextPath}${ImageServlet}/icons/categories/${notification.task.category.icon}"/>
                    <c:out value="${notification.task.name}" escapeXml="true"/>&nbsp;[#<c:out value="${notification.task.number}" escapeXml="true"/>]
        </td>
    </tr>
</table>
</div>
<html:form method="POST" action="/TaskNotifyViewAction" onsubmit="return validate(this);">
    <html:hidden property="method" value="test"/>
    <html:hidden property="id" value="${id}"/>
    <html:hidden property="session" value="${session}"/>
    <html:hidden property="notificationId" value="${notificationId}"/>
    <html:hidden property="filterId" value="${filter.id}"/>
    <div class="controls">
        <input type="submit" class="iconized"
               value="<I18n:message key="TEST"/>"
               name="TEST">
    </div>

</html:form>
</div>
</div>
</tiles:put>
</tiles:insert>
