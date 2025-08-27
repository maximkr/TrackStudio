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
                <html:link styleClass="internal" href="${contextPath}/CategoryViewAction.do?method=page&amp;id=${id}&amp;categoryId=${categoryId}">
                    <I18n:message key="CATEGORY_OVERVIEW"/>
                </html:link>
            </td>
        </c:if>

        <c:if test="${tabRelations.allowed}">
            <td <c:if test="${tabRelations.selected}">class="selectedtab"</c:if>>
                <html:link styleClass="internal" href="${contextPath}/CategoryRelationAction.do?method=page&amp;id=${id}&amp;categoryId=${categoryId}">
                    <I18n:message key="CATEGORY_RELATIONS"/>
                </html:link>
            </td>
        </c:if>

        <c:if test="${tabTriggers.allowed}">
            <td <c:if test="${tabTriggers.selected}">class="selectedtab"</c:if>>
                <html:link styleClass="internal" href="${contextPath}/CategoryTriggerAction.do?method=page&amp;id=${id}&amp;categoryId=${categoryId}">
                    <I18n:message key="CATEGORY_TRIGGERS"/>
                </html:link>
            </td>
        </c:if>

        <c:if test="${tabTemplate.allowed}">
            <td <c:if test="${tabTemplate.selected}">class="selectedtab"</c:if>>
                <html:link styleClass="internal" href="${contextPath}/CategoryTemplateAction.do?method=page&amp;id=${id}&amp;categoryId=${categoryId}">
                    <I18n:message key="CATEGORY_TEMPLATE"/>
                </html:link>
            </td>
        </c:if>

        <c:if test="${tabPermissions.allowed}">
            <td <c:if test="${tabPermissions.selected}">class="selectedtab"</c:if>>
                <html:link styleClass="internal" href="${contextPath}/CategoryPermissionAction.do?method=page&amp;id=${id}&amp;categoryId=${categoryId}">
                    <I18n:message key="CATEGORY_PERMISSIONS"/>
                </html:link>
            </td>
        </c:if>
        <th>&nbsp;</th>
    </tr>
</table>

