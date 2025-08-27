<%@ page buffer="128kb" errorPage="/jsp/Error.jsp"%>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<I18n:setLocale value='${sc.locale}'/>
<I18n:setTimeZone value='${sc.timezone}'/>
<I18n:setBundle basename='language'/>

    <div class="controlPanel">
        <c:if test="${tabView.allowed}">
            <c:if test="${!tabView.selected}">
                <html:link  href="${contextPath}/UserUdfViewAction.do?method=page&amp;id=${id}&amp;udfId=${udfId}">
                    <html:img  styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/ico.overview.gif"/>
                    <I18n:message key="USER_CUSTOM_FIELD_OVERVIEW"/>
                </html:link>
            </c:if>
        </c:if>
        <c:if test="${tabEdit.allowed}">
            <c:if test="${!tabEdit.selected}">
                <html:link  href="${contextPath}/UserUdfEditAction.do?method=page&amp;id=${id}&amp;udfId=${udfId}&amp;type=${type}">
                    <html:img  styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/ico.edit.gif"/>
                    <I18n:message key="USER_CUSTOM_FIELD_PROPERTIES"/>
                </html:link>
            </c:if>
        </c:if>
        <c:if test="${tabPermissions.allowed}">
            <c:if test="${!tabPermissions.selected}">
                <html:link href="${contextPath}/UserUdfPermissionAction.do?method=page&amp;id=${id}&amp;udfId=${udfId}">
                    <html:img  styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/ico.effective.gif"/>
                    <I18n:message key="USER_CUSTOM_FIELD_PERMISSIONS"/>
                </html:link>
            </c:if>
        </c:if>
    </div>
