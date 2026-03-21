<%@ page buffer="128kb" errorPage="/jsp/Error.jsp"%>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<I18n:setLocale value='${sc.locale}'/>
<I18n:setTimeZone value='${sc.timezone}'/>
<I18n:setBundle basename='language'/>
<div class="ts-admin-header ts-admin-header--user-status" id="customHeader">
    <div class="ts-admin-header__inner">
        <html:link styleClass="ts-admin-header__crumb" href="${contextPath}/UserStatusAction.do?method=page&id=${id}">
            <html:img styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/ico.status.gif"/>
            <I18n:message key="PRSTATUSES"/>
        </html:link>
        <span class="ts-admin-header__separator">/</span>
        <c:choose>
            <c:when test="${currentPrstatus ne null}">
                <html:link styleClass="ts-admin-header__current" href="${contextPath}/UserStatusViewAction.do?method=page&prstatusId=${currentPrstatus.id}&id=${id}" >
                    <c:out value="${currentPrstatus.name}" escapeXml="true"/>
                </html:link>
            </c:when>
            <c:otherwise>
                <span class="ts-admin-header__current ts-admin-header__current--new"><I18n:message key="PRSTATUS_ADD"/></span>
            </c:otherwise>
        </c:choose>
    </div>
</div>
