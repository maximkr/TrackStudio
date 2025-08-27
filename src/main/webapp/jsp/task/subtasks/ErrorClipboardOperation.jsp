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
    <tiles:put name="customHeader" type="string"/>
        <tiles:put name="tabs" type="string"/>
<tiles:put name="main" type="string">

<div class="caption">
    <I18n:message key="MSG_OPERATION_CANNOT_BE_COMPLETE"/>
</div>
<table class="general" cellpadding="0" cellspacing="0">
<tr class="wide">
    <th><I18n:message key="TASK_NUMBER"/></th>
    <th><I18n:message key="NAME"/></th>
    <th><I18n:message key="REASON"/></th>
</tr>
<c:forEach var="task" items="${errorCategoryTaskList}" varStatus="status">
    <tr class="line<c:out value="${status.index mod 2}"/>">
        <td><c:out value="${task.number}" escapeXml="true" /></td>
        <td><c:out value="${task.name}" escapeXml="true" /></td>
        <td><I18n:message key="MSG_OPERATION_CATEGORY_NOT_FOUND"/> (<c:out value="${task.category.name}" escapeXml="true" />)</td>
    </tr>
</c:forEach>
<c:forEach var="task" items="${errorParentTaskList}" varStatus="status">
    <tr class="line<c:out value="${status.index mod 2}"/>">
        <td>#<c:out value="${task.number}" escapeXml="true" /></td>
        <td><c:out value="${task.name}" escapeXml="true" /></td>
        <td><I18n:message key="MSG_OPERATION_INCORRECT_PARENT"/></td>
    </tr>
</c:forEach>
</table>
</div>
</div>

</tiles:put>
</tiles:insert>
