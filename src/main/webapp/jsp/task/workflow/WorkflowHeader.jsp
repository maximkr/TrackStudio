<%@ page buffer="128kb" errorPage="/jsp/Error.jsp" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value="${sc.timezone}"/>
<I18n:setBundle basename="language"/>
<div class="elev" id="customHeader">
        <html:img src="${contextPath}${ImageServlet}/cssimages/L.png"/>
        <html:link styleClass="ul" href="${contextPath}/WorkflowAction.do?method=page&id=${id}">
            <html:img src="${contextPath}${ImageServlet}/cssimages/ico.workflow.gif" hspace="0" vspace="0" border="0"/>
            <I18n:message key="WORKFLOWS"/>
        </html:link>
        <span>:</span>
        <c:choose>
            <c:when test="${flow ne null}">
                <html:link styleClass="internal" href="${contextPath}/WorkflowViewAction.do?method=page&workflowId=${flow.id}&id=${id}">
                    <c:out value="${flow.name}" escapeXml="true"/>
                </html:link>
            </c:when>
            <c:otherwise>
                <em class="createnew">
                    <I18n:message key="WORKFLOW_ADD"/>
                </em>
            </c:otherwise>
        </c:choose>
</div>