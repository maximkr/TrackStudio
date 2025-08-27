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
    <tiles:put name="customHeader" value="/jsp/task/workflow/customize/WorkflowCustomizeHeader.jsp"/>
    <tiles:put name="tabs" type="string"/>
    <tiles:put name="main" type="string">
<div class="blueborder">
    <div class="caption">
        <I18n:message key="WORKFLOW_CUSTOM_FIELD_PERMISSIONS"/>
    </div>
<c:if test="${canViewPermission}">
<c:import url="/jsp/custom/CustomPermissionTile.jsp" />
</c:if>
</tiles:put>
</tiles:insert>
