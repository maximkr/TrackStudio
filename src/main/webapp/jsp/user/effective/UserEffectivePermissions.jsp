<%@ page buffer="128kb" errorPage="/jsp/Error.jsp"%>
<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/tiles-el" prefix="tiles" %>
<I18n:setLocale value='${sc.locale}'/>
<I18n:setTimeZone value='${sc.timezone}'/>
<I18n:setBundle basename='language'/>


<tiles:insert page="/jsp/layout/ListLayout.jsp" flush="true">
    <tiles:put name="header" value="/jsp/user/UserHeader.jsp"/>
	<tiles:put name="tabs" type="string"/>
    <tiles:put name="customHeader" type="string"/>
    <tiles:put name="main" type="string">

    <c:if test="${canView}">
        <div class="blueborder">
<div class="caption">
    <I18n:message key="USER_EFFECTIVE_PRSTATUSES"/>
</div>
<c:if test="${canEdit}">
    <div class="controlPanel">
        <html:link href="${contextPath}/UserACLAction.do?method=page&amp;id=${id}"><html:img src="${contextPath}${ImageServlet}/cssimages/ico.edit.gif" styleClass="icon" border="0"/><I18n:message key="ACL"/></html:link>
    </div>
</c:if>
        <div class="indent">
        <div class="general">
<table class="general" cellpadding="0" cellspacing="0">
<tr class="wide">
        <th width="20%"><I18n:message key="USER"/></th>
        <th width="40%"><I18n:message key="LOGIN"/></th>
        <th width="40%"><I18n:message key="EFFECTIVE_PRSTATUSES"/></th>
</tr>
        <c:forEach var="acl" items="${effective}" varStatus="varCounter">
<tr class="line<c:out value="${varCounter.index mod 2}"/>">
    <td>
        <span class="user" ${acl.key.id eq sc.userId ? "id='loggedUser'" : ""}>
            <html:img  styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/${acl.key.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
            <c:out value="${acl.key.name}" escapeXml="true"/>
	    </span>
    </td>
    <td>
        <a href="${contextPath}/UserViewAction.do?id=${acl.key.id}#acl" class="user" ${acl.key.id eq sc.userId ? "id='loggedUser'" : ""}>
            <html:img  styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/${acl.key.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
            <c:out value="${acl.key.login}" escapeXml="true"/>
	    </a>
    </td>
    <td>
        <c:forEach var="cat" items="${acl.value}" varStatus="varC"><c:if test="${varC.index > 0}">, </c:if><span style="white-space: nowrap;">
            <html:img  styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/ico.status.gif"/><c:out value="${cat.name}" escapeXml="true"/></span></c:forEach>
    </td>
</tr>
</c:forEach>


</table>
</div>
<c:out value="${slider}" escapeXml="false"/>
        </div>
        </div>
</c:if>
</tiles:put>
</tiles:insert>