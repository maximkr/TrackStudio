<%@ page buffer="128kb" errorPage="/jsp/Error.jsp" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value="${sc.timezone}"/>
<I18n:setBundle basename="language"/>
<div class="elev" id="customHeader">
    <div>
        <html:img src="${contextPath}${ImageServlet}/cssimages/L.png"/>
        <html:link  styleClass="ul" href="${contextPath}/UserStatusAction.do?method=page&id=${id}">
            <html:img src="${contextPath}${ImageServlet}/cssimages/ico.status.gif" hspace="0" vspace="0" border="0"/>
            <I18n:message key="PRSTATUSES"/>
        </html:link>
        <span>:</span>
        <c:if test="${currentPrstatus ne null}">
            <html:link styleClass="internal"
                    href="${contextPath}/UserStatusViewAction.do?method=page&prstatusId=${currentPrstatus.id}&id=${id}">
                <c:out value="${currentPrstatus.name}" escapeXml="true"/>
            </html:link>
        </c:if>
    </div>
    <div>
        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        <html:img alt="" src="${contextPath}${ImageServlet}/cssimages/L.png"/>
        <html:link  styleClass="ul"
                   href="${contextPath}/UserWorkflowSecurityAction.do?method=page&amp;id=${id}&amp;prstatusId=${currentPrstatus.id}&amp;workflowId=${workflowId}">
            <html:img src="${contextPath}${ImageServlet}/cssimages/ico.workflow.gif" hspace="0" vspace="0" border="0"/>
            <I18n:message key="WORKFLOW_PERMISSIONS"/>
        </html:link>
        <span>:</span>
        <c:choose>
            <c:when test="${workflow ne null}">
                <html:link styleClass="internal"
                        href="${contextPath}/UserWorkflowOverviewAction.do?method=page&amp;prstatusId=${currentPrstatus.id}&amp;workflowId=${workflow.id}&amp;id=${id}">
                    <c:out value="${workflow.name}" escapeXml="true"/>
                </html:link>
            </c:when>
            <c:otherwise>
                <span class="createnew"><I18n:message key="PRSTATUS_ADD"/></span>
            </c:otherwise>
        </c:choose>
    </div>
</div>