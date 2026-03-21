<%@ page buffer="128kb" errorPage="/jsp/Error.jsp" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value="${sc.timezone}"/>
<I18n:setBundle basename="language"/>
<div class="ts-category-header" id="customHeader">
    <div class="ts-category-header__inner">
        <html:link styleClass="ts-category-header__crumb" href="${contextPath}/CategoryAction.do?method=page&amp;id=${id}">
            <html:img src="${contextPath}${ImageServlet}/cssimages/ico.categories.gif" hspace="0" vspace="0" border="0"/>
            <I18n:message key="CATEGORIES"/>
        </html:link>
        <span class="ts-category-header__separator">/</span>
        <c:choose>
            <c:when test="${currentCategory ne null}">
                <html:link styleClass="ts-category-header__current"
                           href="${contextPath}/CategoryViewAction.do?method=page&amp;categoryId=${currentCategory.id}&amp;id=${id}">
                    <html:img src="${contextPath}${ImageServlet}/icons/categories/${currentCategory.icon}" hspace="0" vspace="0" border="0"/>
                    <c:out value="${currentCategory.name}" escapeXml="true"/>
                </html:link>
            </c:when>
            <c:otherwise>
                <span class="ts-category-header__current ts-category-header__current--new">
                    <I18n:message key="CATEGORY_ADD"/>
                </span>
            </c:otherwise>
        </c:choose>
    </div>
</div>
