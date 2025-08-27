<%@ page buffer="128kb" errorPage="/jsp/Error.jsp" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/tiles-el" prefix="tiles" %>
<%@ taglib uri="http://trackstudio.com" prefix="ts" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value="${sc.timezone}"/>
<I18n:setBundle basename="language"/>
<div class="user" style="display:inline; padding-left:20px;" ${message.submitterId eq sc.userId ? "id='loggedUser'" : ""}>
    <html:img styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/${message.submitter.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
    <c:out value="${message.submitter.name}" escapeXml="true"/>,&nbsp;
</div><I18n:formatDate value="${message.time.time}" type="both" dateStyle="short" timeStyle="short"/>
<div style="float:right;padding-right:10px;">
    <c:if test="${previosMessageId != null}">
        <div style="display:inline;cursor:pointer;text-decoration:underline;" onclick="updateDescription('${previosMessageId}');"><I18n:message key="PREV"/></div>&nbsp;
    </c:if>
    <c:if test="${nextMessageId != null}">
        <div style="display:inline;cursor:pointer;text-decoration:underline;" onclick="updateDescription('${nextMessageId}');"><I18n:message key="NEXT"/></div>
    </c:if>
</div>
<div style="padding-left:20px;padding-top:20px;">
    <ts:htmlfilter session="${sc.id}" macros="true" audit="false" request="<%=request%>">
        <c:out value="${message.description}" escapeXml="false"/>
    </ts:htmlfilter>
</div>