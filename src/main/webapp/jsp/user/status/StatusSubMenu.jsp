<%@ page buffer="128kb" errorPage="/jsp/Error.jsp"%>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<I18n:setLocale value='${sc.locale}'/>
<I18n:setTimeZone value='${sc.timezone}'/>
<I18n:setBundle basename='language'/>
<table class="tabs">
    <tr>
        <c:if test="${tabView.allowed}">
            <td <c:if test="${tabView.selected}">class="selectedtab"</c:if>>
                <html:link styleClass="internal" href="${contextPath}/UserStatusViewAction.do?method=page&id=${id}&prstatusId=${currentPrstatus.id}">
                    <I18n:message key="PRSTATUS_OVERVIEW"/>
                </html:link>
            </td>
        </c:if>

        <c:if test="${tabTaskFieldSecurity.allowed}">
            <td <c:if test="${tabTaskFieldSecurity.selected}">class="selectedtab"</c:if>>
                <html:link styleClass="internal" href="${contextPath}/TaskFieldSecurityAction.do?method=page&id=${id}&prstatusId=${currentPrstatus.id}&name=${urlEncodedName}">
                    <I18n:message key="PRSTATUS_TASK_FIELDS_PERMISSIONS"/>
                </html:link>
            </td>
        </c:if>
        <c:if test="${tabUserFieldSecurity.allowed}">
            <td <c:if test="${tabUserFieldSecurity.selected}">class="selectedtab"</c:if>>
                <html:link styleClass="internal" href="${contextPath}/UserFieldSecurityAction.do?method=page&id=${id}&prstatusId=${currentPrstatus.id}&name=${urlEncodedName}">
                    <I18n:message key="PRSTATUS_USER_FIELDS_PERMISSIONS"/>
                </html:link>
            </td>
        </c:if>
        <c:if test="${tabTaskSecurity.allowed}">
            <td <c:if test="${tabTaskSecurity.selected}">class="selectedtab"</c:if>>
                <html:link styleClass="internal" href="${contextPath}/TaskSecurityAction.do?method=page&id=${id}&prstatusId=${currentPrstatus.id}&name=${urlEncodedName}">
                    <I18n:message key="PRSTATUS_TASK_ACTIONS_PERMISSIONS"/>
                </html:link>
            </td>
        </c:if>
        <c:if test="${tabUserSecurity.allowed}">
            <td <c:if test="${tabUserSecurity.selected}">class="selectedtab"</c:if>>
                <html:link styleClass="internal" href="${contextPath}/UserSecurityAction.do?method=page&id=${id}&prstatusId=${currentPrstatus.id}&name=${urlEncodedName}">
                    <I18n:message key="PRSTATUS_USER_ACTIONS_PERMISSIONS"/>
                </html:link>
            </td>
        </c:if>

        <c:if test="${tabCategorySecurity.allowed}">
            <td <c:if test="${tabCategorySecurity.selected}">class="selectedtab"</c:if>>
                <html:link styleClass="internal" href="${contextPath}/UserCategorySecurityAction.do?method=page&id=${id}&prstatusId=${currentPrstatus.id}&name=${urlEncodedName}">
                    <I18n:message key="PRSTATUS_CATEGORIES_PERMISSIONS"/>
                </html:link>
            </td>
        </c:if>

        <c:if test="${tabWorkflowSecurity.allowed}">
            <td <c:if test="${tabWorkflowSecurity.selected}">class="selectedtab"</c:if>>
                <html:link styleClass="internal" href="${contextPath}/UserWorkflowSecurityAction.do?method=page&id=${id}&prstatusId=${currentPrstatus.id}&name=${urlEncodedName}">
                    <I18n:message key="PRSTATUS_WORKFLOW_PERMISSIONS"/>
                </html:link>
            </td>
        </c:if>
        <th>&nbsp;</th>
    </tr>
</table>
