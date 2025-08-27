<%@ page buffer="128kb" errorPage="/jsp/Error.jsp" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/tiles-el" prefix="tiles" %>


<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value="${sc.timezone}"/>
<I18n:setBundle basename="language"/>
<tiles:insert page="/jsp/layout/ListLayout.jsp" flush="true">
    
    <tiles:put name="header" value="/jsp/task/TaskHeader.jsp"/>
    <tiles:put name="customHeader" value="/jsp/task/workflow/WorkflowHeader.jsp"/>
    <tiles:put name="tabs" value="/jsp/task/workflow/WorkflowSubMenu.jsp"/>
    <tiles:put name="main" type="string">
        <div class="nblueborder">
            <div class="ncaption">    </div>
            <c:if test="${canManage eq true}">
                <c:import url="/jsp/custom/CustomListTile.jsp">
                    <c:param name="tileId" value="createWorkflowUDF"/>
                </c:import>
            </c:if>
        </div>
    </tiles:put>
</tiles:insert>
