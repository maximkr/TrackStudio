<%@ page buffer="128kb" errorPage="/jsp/Error.jsp" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value="${sc.timezone}"/>
<I18n:setBundle basename="language"/>
<table class="tabs">
    <tr>
        <c:if test="${tabView.allowed}">
            <td <c:if test="${tabView.selected}">class="selectedtab"</c:if>>
                <html:link styleClass="internal" href="${contextPath}/MstatusViewAction.do?method=page&amp;id=${id}&amp;mstatusId=${mstatus.id}&amp;workflowId=${flow.id}">
                    <I18n:message key="MESSAGE_TYPE_OVERVIEW"/>
                </html:link>
            </td>
        </c:if>

        <c:if test="${tabResolutions.allowed}">
            <td <c:if test="${tabResolutions.selected}">class="selectedtab"</c:if>>
                <html:link styleClass="internal" href="${contextPath}/ResolutionAction.do?method=page&amp;id=${id}&amp;mstatusId=${mstatus.id}&amp;workflowId=${flow.id}">
                    <I18n:message key="RESOLUTIONS_LIST"/>
                </html:link>
            </td>
        </c:if>
        <c:if test="${tabTransitions.allowed}">
            <td <c:if test="${tabTransitions.selected}">class="selectedtab"</c:if>>
                <html:link styleClass="internal" href="${contextPath}/TransitionAction.do?method=page&amp;id=${id}&amp;mstatusId=${mstatus.id}&amp;workflowId=${flow.id}">
                    <I18n:message key="TRANSITIONS_LIST"/>
                </html:link>
            </td>
        </c:if>

        <c:if test="${tabTriggers.allowed}">
            <td <c:if test="${tabTriggers.selected}">class="selectedtab"</c:if>>
                <html:link styleClass="internal" href="${contextPath}/MstatusTriggerAction.do?method=page&amp;id=${id}&amp;mstatusId=${mstatus.id}&amp;workflowId=${flow.id}">
                    <I18n:message key="MESSAGE_TYPE_TRIGGERS"/>
                </html:link>
            </td>
        </c:if>

        <c:if test="${tabPermissions.allowed}">
            <td <c:if test="${tabPermissions.selected}">class="selectedtab"</c:if>>
                <html:link styleClass="internal" href="${contextPath}/MstatusPermissionAction.do?method=page&amp;id=${id}&amp;mstatusId=${mstatus.id}&amp;workflowId=${flow.id}">
                    <I18n:message key="MESSAGE_TYPE_PERMISSIONS"/>
                </html:link>
            </td>
        </c:if>

        <c:if test="${tabUdfPermissions.allowed}">
            <td <c:if test="${tabUdfPermissions.selected}">class="selectedtab"</c:if>>
                <html:link styleClass="internal" href="${contextPath}/UdfPermissionAction.do?method=page&amp;id=${id}&amp;mstatusId=${mstatus.id}&amp;workflowId=${flow.id}">
                    <I18n:message key="CUSTOM_FIELDS_PERMISSIONS"/>
                </html:link>
            </td>
        </c:if>

        <c:if test="${tabScheduler.allowed}">
            <td <c:if test="${tabScheduler.selected}">class="selectedtab"</c:if>>
                <html:link styleClass="internal" href="${contextPath}/MstatusSchedulerAction.do?method=page&amp;id=${id}&amp;mstatusId=${mstatus.id}&amp;workflowId=${flow.id}">
                    <I18n:message key="SCHEDULER_SETTING"/>
                </html:link>
            </td>
        </c:if>
        <th>&nbsp;</th>
    </tr>
</table>



