<%@ page buffer="128kb" errorPage="/jsp/Error.jsp"%>
<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/tiles-el" prefix="tiles" %>


<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value="${sc.timezone}"/>
<I18n:setBundle basename="language"/>
<tiles:insert page="/jsp/layout/ListLayout.jsp" flush="true">
    <tiles:put name="header" value="/jsp/task/TaskHeader.jsp"/>
    <tiles:put name="customHeader" value="/jsp/task/customize/TaskCustomizeHeader.jsp"/>

    <tiles:put name="tabs" type="string"/>

    <tiles:put name="main" type="string">

<c:if test="${canViewTaskCustomization}">
    <div class="blueborder">
    <div class="caption">
       <I18n:message key="CUSTOM_FIELD_PERMISSIONS_TASK"/>
    </div>

    <div class="controlPanel">
        <c:if test="${tabView.allowed}">
        <html:link  href="${contextPath}/TaskUdfViewAction.do?method=page&amp;id=${id}&amp;udfId=${udfId}&amp;type=${type}">
     	<html:img  styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/ico.overview.gif"/><I18n:message key="CUSTOM_FIELD_OVERVIEW_TASK"/>
        </html:link>
        </c:if>
        <c:if test="${tabEdit.allowed}">

        <html:link href="${contextPath}/TaskUdfEditAction.do?method=page&amp;id=${id}&amp;udfId=${udfId}&amp;type=${type}">

        <html:img  styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/ico.edit.gif"/> <I18n:message key="CUSTOM_FIELD_PROPERTIES_TASK"/>
        </html:link>
        </c:if>
    </div>
    
        <div class="indent">
<c:import url="/jsp/custom/CustomPermissionTile.jsp" />
    </div>
    </div>
</c:if>
</tiles:put>
</tiles:insert>
