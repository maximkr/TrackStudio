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
	
<c:if test="${_can_view}">
    <div class="blueborder">
    <div class="caption">
       <I18n:message key="CUSTOM_FIELD_PROPERTIES_TASK"/>
    </div>
        <div class="indent">
<c:import url="/jsp/custom/CustomEditTile.jsp"/>
        </div>
        </div>
</c:if>

</tiles:put>
</tiles:insert>
