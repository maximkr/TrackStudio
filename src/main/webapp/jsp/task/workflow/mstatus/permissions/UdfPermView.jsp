<%@ page buffer="128kb" errorPage="/jsp/Error.jsp"%>
<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/tiles-el" prefix="tiles" %>


<I18n:setLocale value='${sc.locale}'/>
<I18n:setTimeZone value='${sc.timezone}'/>
<I18n:setBundle basename='language'/>
<tiles:insert page="/jsp/layout/ListLayout.jsp" flush="true">
<tiles:put name="title" value="${title}"/>
<tiles:put name="customHeader" value="/jsp/task/workflow/mstatus/MstatusHeader.jsp"/>
<tiles:put name="header" value="/jsp/task/TaskHeader.jsp"/>
<tiles:put name="tabs" value="/jsp/task/workflow/mstatus/permissions/UdfPermSubMenu.jsp" />
<tiles:put name="main" type="string">
    <div class="nblueborder">
    <div class="ncaption"></div>
    <div class="indent">
<html:form action="/UdfPermissionViewAction" method="post" styleId="checkunload" onsubmit="return validate(this);">
    <html:hidden property="method" value="save"/>
    <html:hidden property="id" value="${id}"/>
    <html:hidden property="session" value="${session}"/>
    <html:hidden property="udfId" value="${udfId}"/>
    <html:hidden property="workflowId" value="${flow.id}"/>
    <html:hidden property="mstatusId" value="${mstatusId}"/>
    <div class="general">
<table class="general" cellpadding="0" cellspacing="0">
    <caption><c:out value="${tableTitle}"/></caption>
    <tr class="wide">
        <th><I18n:message key="CAN_VIEW"/></th>
        <td><c:out value="${viewRules}"/>
            <c:if test="${viewRules == ''}"><I18n:message key="NOBODY"/></c:if>
        </td>
    </tr>
    <c:if test="${editRules != ''}">
    <tr class="wide">
        <th><I18n:message key="CAN_EDIT"/></th>
        <td><c:out value="${editRules}"/></td>
    </tr>
    </c:if>
    </table>
    </div>
    </html:form>
    </div>
    </div>
</tiles:put>
</tiles:insert>
