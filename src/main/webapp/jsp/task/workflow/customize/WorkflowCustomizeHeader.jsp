<%@ page buffer="128kb" errorPage="/jsp/Error.jsp" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value="${sc.timezone}"/>
<I18n:setBundle basename="language"/>
<c:set var="udfUrl" value="${contextPath}/WorkflowCustomizeAction.do?id=${id}&workflowId=${flow.id}"/>
<c:if test="${!tabEdit.allowed}">
    <c:set var="udfUrl" value="${contextPath}/WorkflowViewAction.do?method=page&workflowId=${flow.id}&id=${id}"/>
</c:if>
<div class="elev" id="customHeader">
    <div>
        <html:img src="${contextPath}${ImageServlet}/cssimages/L.png"/>
        <html:link  styleClass="ul" href="${contextPath}/WorkflowAction.do?method=page&id=${id}">
            <html:img src="${contextPath}${ImageServlet}/cssimages/ico.workflow.gif" hspace="0" vspace="0" border="0"/>
            <I18n:message key="WORKFLOWS_LIST"/>
        </html:link>
        <span>:</span>
        <html:link styleClass="internal" href="${contextPath}/WorkflowViewAction.do?method=page&workflowId=${flow.id}&id=${id}">
            <c:out value="${flow.name}" escapeXml="true"/>
        </html:link>
    </div>
    <div>
        <html:img src="${contextPath}${ImageServlet}/cssimages/blank.png"/>
        <html:img src="${contextPath}${ImageServlet}/cssimages/L.png"/>
        <html:link  styleClass="ul" href="${udfUrl}">
            <html:img src="${contextPath}${ImageServlet}/cssimages/ico.customfields.gif" hspace="0" vspace="0" border="0"/>
            <I18n:message key="CUSTOM_FIELDS"/>
        </html:link>
        <span>:</span>
        <c:choose>
            <c:when test="${udf ne null}">
                <html:link styleClass="internal"
                        href="${contextPath}/WorkflowUdfViewAction.do?method=page&udfId=${udf.id}&id=${id}&workflowId=${flow.id}">
                    <c:out value="${udf.caption}" escapeXml="true"/>
                </html:link>
            </c:when>
            <c:otherwise>
                <span class="createnew"><I18n:message key="CUSTOM_FIELD_ADD"/></span>
            </c:otherwise>
        </c:choose>
    </div>
</div>