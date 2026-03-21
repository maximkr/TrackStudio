<%@ page buffer="128kb" errorPage="/jsp/Error.jsp" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value="${sc.timezone}"/>
<I18n:setBundle basename="language"/>
<div class="ts-admin-header ts-admin-header--workflow" id="customHeader">
    <div class="ts-admin-header__inner">
        <html:link styleClass="ts-admin-header__crumb" href="${contextPath}/WorkflowAction.do?method=page&id=${id}">
            <html:img src="${contextPath}${ImageServlet}/cssimages/ico.workflow.gif" hspace="0" vspace="0" border="0"/>
            <I18n:message key="WORKFLOWS"/>
        </html:link>
        <span class="ts-admin-header__separator">/</span>
        <c:choose>
            <c:when test="${flow ne null}">
                <html:link styleClass="ts-admin-header__current" href="${contextPath}/WorkflowViewAction.do?method=page&workflowId=${flow.id}&id=${id}">
                    <c:out value="${flow.name}" escapeXml="true"/>
                </html:link>
            </c:when>
            <c:otherwise>
                <span class="ts-admin-header__current ts-admin-header__current--new">
                    <I18n:message key="WORKFLOW_ADD"/>
                </span>
            </c:otherwise>
        </c:choose>
    </div>
</div>
