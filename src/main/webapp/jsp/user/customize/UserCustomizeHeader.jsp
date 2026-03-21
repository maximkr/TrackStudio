<%@ page buffer="128kb" errorPage="/jsp/Error.jsp"%>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<I18n:setLocale value='${sc.locale}'/>
<I18n:setTimeZone value='${sc.timezone}'/>
<I18n:setBundle basename='language'/>

<div class="ts-admin-header ts-admin-header--user-customize" id="customHeader">
    <div class="ts-admin-header__inner">
        <html:link styleClass="ts-admin-header__crumb" href="${contextPath}/UserCustomizeAction.do?id=${id}">
            <html:img src="${contextPath}${ImageServlet}/cssimages/ico.customfields.gif" hspace="0" vspace="0" border="0" />
            <I18n:message key="CUSTOM_FIELDS"/>
        </html:link>
        <span class="ts-admin-header__separator">/</span>
        <c:choose>
            <c:when test="${udf ne null}">
                <html:link styleClass="ts-admin-header__current" href="${contextPath}/UserUdfViewAction.do?method=page&amp;udfId=${udf.id}&amp;id=${id}">
                    <c:out value="${udf.caption}" escapeXml="true"/>
                </html:link>
            </c:when>
            <c:otherwise>
                <span class="ts-admin-header__current ts-admin-header__current--new"><I18n:message key="CUSTOM_FIELD_ADD"/></span>
            </c:otherwise>
        </c:choose>
    </div>
</div>
