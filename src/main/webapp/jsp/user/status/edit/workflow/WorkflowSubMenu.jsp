<%@ page buffer="128kb" errorPage="/jsp/Error.jsp" %>
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
                            <html:link styleClass="internal"
                                    href="${contextPath}/UserWorkflowOverviewAction.do?method=page&amp;id=${id}&amp;workflowId=${workflow.id}&amp;prstatusId=${prstatusId}">
                                       <I18n:message key="USER_WORKFLOW_OVERVIEW"/>
                            </html:link>
                        </td>
                    </c:if>
                    <c:if test="${tabMType.allowed}">
                        <td <c:if test="${tabMType.selected}">class="selectedtab"</c:if>>
                            <html:link styleClass="internal"
                                    href="${contextPath}/UserMTypeSecurityAction.do?method=page&amp;id=${id}&amp;workflowId=${workflow.id}&amp;prstatusId=${prstatusId}">
                                        <I18n:message key="USER_MESSAGE_PERMISSIONS"/>
                            </html:link>
                        </td>
                    </c:if>
                    <c:if test="${tabUdfMType.allowed}">
                        <td <c:if test="${tabUdfMType.selected}">class="selectedtab"</c:if>>
                            <html:link styleClass="internal"
                                    href="${contextPath}/UserUdfMTypeSecurityAction.do?method=page&amp;id=${id}&amp;workflowId=${workflow.id}&amp;prstatusId=${prstatusId}">
                                        <I18n:message key="USER_CUSTOM_FIELD_PERMISSIONS"/>
                            </html:link>
                        </td>
                    </c:if>
            <th>&nbsp;</th>
        </tr>
    </table>

