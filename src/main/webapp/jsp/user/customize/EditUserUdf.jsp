<%@ page buffer="128kb" errorPage="/jsp/Error.jsp"%>
<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/tiles-el" prefix="tiles" %>
<c:set var="taskMenu" value="false"/>
<c:set var="userMenu" value="true"/>
<I18n:setLocale value='${sc.locale}'/>
<I18n:setTimeZone value='${sc.timezone}'/>
<I18n:setBundle basename='language'/>
<tiles:insert page="/jsp/layout/ListLayout.jsp" flush="true">
    <tiles:put name="customHeader" value="/jsp/user/customize/UserCustomizeHeader.jsp"/>
    <tiles:put name="header" value="/jsp/user/UserHeader.jsp"/>
    <tiles:put name="tabs" type="string"/>
    
    <tiles:put name="main" type="string">
        <c:if test="${_can_view}">
            <div class="blueborder">
                <div class="caption">
                    <I18n:message key="USER_CUSTOM_FIELD_PROPERTIES"/>
                </div>
                <div class="indent">
                    <c:import url="/jsp/custom/CustomEditTile.jsp"/>
                </div>
            </div>
        </c:if>
    </tiles:put>
</tiles:insert>
