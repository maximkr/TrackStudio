<%@ page buffer="128kb" errorPage="/jsp/Error.jsp" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value="${sc.timezone}"/>
<I18n:setBundle basename="language"/>
<c:choose>
<c:when test="${tabView.allowed && !tabPriorities.allowed && !tabStates.allowed && !tabMessageTypes.allowed && !tabCustomize.allowed}">
<div class="caption"><I18n:message key="WORKFLOW_OVERVIEW"/></div>
    </c:when>
    <c:otherwise>
<table class="tabs">
    <tr>
        <c:if test="${tabView.allowed}">
            <td <c:if test="${tabView.selected}">class="selectedtab"</c:if>>
                <html:link styleClass="internal" href="${contextPath}/WorkflowViewAction.do?method=page&id=${id}&workflowId=${flow.id}">
                    <I18n:message key="WORKFLOW_OVERVIEW"/>
                </html:link>
            </td>
        </c:if>
        
        <c:if test="${tabPriorities.allowed}">
            <td <c:if test="${tabPriorities.selected}">class="selectedtab"</c:if>>
                <html:link styleClass="internal" href="${contextPath}/PriorityAction.do?method=page&id=${id}&workflowId=${flow.id}">
                    <I18n:message key="PRIORITIES_LIST"/>
                </html:link>
            </td>
        </c:if>

        <c:if test="${tabStates.allowed}">
            <td <c:if test="${tabStates.selected}">class="selectedtab"</c:if>>
                <html:link styleClass="internal" href="${contextPath}/TaskStatusAction.do?method=page&id=${id}&workflowId=${flow.id}">
                    <I18n:message key="STATES_LIST"/>
                </html:link>
            </td>
        </c:if>
        <c:if test="${tabMessageTypes.allowed}">
            <td <c:if test="${tabMessageTypes.selected}">class="selectedtab"</c:if>>
                <html:link styleClass="internal" href="${contextPath}/MstatusAction.do?method=page&id=${id}&workflowId=${flow.id}">
                    <I18n:message key="MESSAGE_TYPES"/>
                </html:link>
            </td>
        </c:if>

        <c:if test="${tabCustomize.allowed}">
            <td <c:if test="${tabCustomize.selected}">class="selectedtab"</c:if>>
                <html:link styleClass="internal" href="${contextPath}/WorkflowCustomizeAction.do?method=page&id=${id}&workflowId=${flow.id}">
                    <I18n:message key="WORKFLOW_CUSTOM_FIELDS_LIST"/>
                </html:link>
            </td>
        </c:if>
        <th>&nbsp;</th>
    </tr>
</table>
    </c:otherwise>
</c:choose>

