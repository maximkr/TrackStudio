<%@ page buffer="128kb" errorPage="/jsp/Error.jsp" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/tiles-el" prefix="tiles" %>
<%@ taglib uri="http://trackstudio.com" prefix="ts" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value="${sc.timezone}"/>
<I18n:setBundle basename="language"/>
<tiles:insert page="/jsp/layout/ListLayout.jsp" flush="true">
    <tiles:put name="header" value="/jsp/task/TaskHeader.jsp"/>
    <tiles:put name="customHeader" type="string"/>
    <tiles:put name="tabs" type="string"/>
    <tiles:put name="main" type="string">
        <c:if test="${canView}">
            <script type="text/javascript">
                function updateDescription(id) {
                    $.ajax("${contextPath}/MessageDocumentAction.do?method=page&id=${tci.id}&go=" + id, {
                        success: function(data) {
                            $('#descriptionDiv').html(data);
                        }
                    });
                }
            </script>
            <div>
                <div class="caption" style="border-color:#FFFFFF; padding-left: 20px; font-size: 20px; background-color:#FFFFFF">
                    <c:out value="${tci.name}"/>
                </div>
                <div id="descriptionDiv">
                    <c:import url="/jsp/task/viewtask/messages/MessagesDocument.jsp"/>
                </div>
            </div>
        </c:if>
    </tiles:put>
</tiles:insert>