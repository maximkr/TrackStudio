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
<tiles:put name="customHeader" value="/jsp/task/categories/CategoryHeader.jsp"/>
<tiles:put name="tabs" value="/jsp/task/categories/CategorySubMenu.jsp"/>
<tiles:put name="main" type="string">

<div class="nblueborder">
<div class="ncaption"></div>
<c:if test="${canManage}">
    <div class="controlPanel">
        <html:link
                href="${contextPath}/CategoryEditAction.do?method=page&amp;categoryId=${currentCategory.id}&amp;id=${id}">
            <html:img src="${contextPath}${ImageServlet}/cssimages/ico.edit.gif" hspace="0" vspace="0" border="0" align="middle"/>
            <I18n:message key="EDIT"/>
        </html:link>
        <c:choose>
            <c:when test="${isCategoryHidden}">
                <html:link  titleKey="ACTIVATE_CATEGORY_COMMENT" href="${contextPath}/CategoryAction.do?method=changeHideCategory&categoryId=${currentCategory.id}&id=${id}&session=${session}"><html:img border="0" src="${contextPath}${ImageServlet}/cssimages/ico.edit.gif"/><I18n:message key="ACTIVATE"/></html:link>
            </c:when>
            <c:otherwise>
                <html:link titleKey="DEACTIVATE_CATEGORY_COMMENT" href="${contextPath}/CategoryAction.do?method=changeHideCategory&categoryId=${currentCategory.id}&id=${id}&session=${session}"><html:img border="0" src="${contextPath}${ImageServlet}/cssimages/ico.edit.gif"/><I18n:message key="DEACTIVATE"/></html:link>
            </c:otherwise>
        </c:choose>

    </div>
</c:if>
<div class="indent">
<c:if test="${!isValid}">
    <c:set var="counter" value="${0}"/>
    <div class="general">
        <table class="error" cellpadding="0" cellspacing="0">
            <caption>
                <I18n:message key="CATEGORY_INVALID_OVERVIEW"/>
            </caption>
            <c:if test="${!isValideWorkflow}">
                <tr class="line<c:out value="${counter mod 2}"/>">
                    <td>
                        <I18n:message key="CATEGORY_INVALID_WORKFLOW">
                            <I18n:param value="${workflow}"/>
                        </I18n:message>
                    </td>
                </tr>
                <c:set var="counter" value="${counter + 1}"/>
            </c:if>
            <c:if test="${!isValidParentCategory}">
                <tr class="line<c:out value="${counter mod 2}"/>">
                    <td>
                        <I18n:message key="CATEGORY_INVALID_HASNT_PARENT"/>
                    </td>
                </tr>
                <c:set var="counter" value="${counter + 1}"/>
            </c:if>
            <c:if test="${!isValidEdit}">
                <c:forEach var="prstatus" items="${invalideEditList}">
                    <tr class="line<c:out value="${counter mod 2}"/>">
                        <td>
                            <I18n:message key="CATEGORY_INVALID_EDIT_PERMISSION_CONFLICT">
                                <I18n:param value="${prstatus.name}"/>
                            </I18n:message>
                        </td>
                    </tr>
                    <c:set var="counter" value="${counter + 1}"/>
                </c:forEach>
            </c:if>
            <c:if test="${!isValidCreate}">
                <c:forEach var="prstatus" items="${invalideCreateList}">
                    <tr class="line<c:out value="${counter mod 2}"/>">
                        <td>
                            <I18n:message key="CATEGORY_INVALID_CREATE_PERMISSION_CONFLICT">
                                <I18n:param value="${prstatus.name}"/>
                            </I18n:message>
                        </td>
                    </tr>
                    <c:set var="counter" value="${counter + 1}"/>
                </c:forEach>
            </c:if>
            <c:if test="${!isValidDelete}">
                <c:forEach var="prstatus" items="${invalideDeleteList}">
                    <tr class="line<c:out value="${counter mod 2}"/>">
                        <td>
                            <I18n:message key="CATEGORY_INVALID_DELETE_PERMISSION_CONFLICT">
                                <I18n:param value="${prstatus.name}"/>
                            </I18n:message>
                        </td>
                    </tr>
                    <c:set var="counter" value="${counter + 1}"/>
                </c:forEach>
            </c:if>
            <c:if test="${!isValidBeHandler}">
                <c:forEach var="prstatus" items="${invalideBeHandlerList}">
                    <tr class="line<c:out value="${counter mod 2}"/>">
                        <td>
                            <I18n:message key="CATEGORY_INVALID_BE_HANDLER_PERMISSION_CONFLICT">
                                <I18n:param value="${prstatus.name}"/>
                            </I18n:message>
                        </td>
                    </tr>
                    <c:set var="counter" value="${counter + 1}"/>
                </c:forEach>
            </c:if>
        </table>
    </div>
</c:if>
<div class="general">
    <table class="general" cellpadding="0" cellspacing="0">
        <caption>
            <I18n:message key="PROPERTIES"/>
        </caption>
        <colgroup>
            <col class="col_1">
            <col class="col_2">
        </colgroup>
        <tr>
            <th>
                <I18n:message key="NAME"/>
            </th>
            <td>
                <c:out value="${currentCategory.name}"/>
            </td>
        </tr>

        <tr>
            <th>
                <I18n:message key="DESCRIPTION"/>
            </th>
            <td>
                <c:out value="${currentCategory.description}"/>
            </td>
        </tr>
        <tr>
            <th>
                <I18n:message key="ACTION"/>
            </th>
            <td>
                <c:out value="${currentCategory.action}"/>
            </td>
        </tr>
        <tr>
            <th>
                <I18n:message key="WORKFLOW"/>
            </th>
            <td>
                <html:img src="${contextPath}${ImageServlet}/cssimages/ico.workflow.gif"/><c:out value="${currentCategory.workflow.name}"/>
            </td>
        </tr>
        <tr>
            <th>
                <I18n:message key="ICON"/>
            </th>
            <td>
                <html:img src="${contextPath}${ImageServlet}/icons/categories/${currentCategory.icon}"/>
                <c:out value="${currentCategory.icon}"/>
            </td>
        </tr>

        <tr>
            <th>
                <I18n:message key="HANDLER_REQUIRED"/>
            </th>
            <td>
                <c:choose>
                    <c:when test="${currentCategory.handlerRequired}">
                        <html:img src="${contextPath}${ImageServlet}/cssimages/ico.checked.gif"/>
                    </c:when>
                    <c:otherwise>
                        <html:img src="${contextPath}${ImageServlet}/cssimages/ico.unchecked.gif"/>
                    </c:otherwise>
                </c:choose>
            </td>
        </tr>
        <tr>
            <th>
                <I18n:message key="GROUP_ASSIGMENT_ALOWED"/>
            </th>
            <td>
                <c:choose>
                    <c:when test="${currentCategory.groupHandlerAllowed}">
                        <html:img src="${contextPath}${ImageServlet}/cssimages/ico.checked.gif"/>
                    </c:when>
                    <c:otherwise>
                        <html:img src="${contextPath}${ImageServlet}/cssimages/ico.unchecked.gif"/>
                    </c:otherwise>
                </c:choose>
            </td>
        </tr>
        <tr>
            <th>
                <I18n:message key="HANDLER_ONLY_ROLE"/>
            </th>
            <td>
                <c:choose>
                    <c:when test="${handlerOnlyRole}">
                        <html:img src="${contextPath}${ImageServlet}/cssimages/ico.checked.gif"/>
                    </c:when>
                    <c:otherwise>
                        <html:img src="${contextPath}${ImageServlet}/cssimages/ico.unchecked.gif"/>
                    </c:otherwise>
                </c:choose>
            </td>
        </tr>
        <tr>
            <th>
                <I18n:message key="CATEGORY_BUDGET_FORMAT"/>
            </th>
            <td>
                <c:out value="${currentCategory.budget}"/> (<c:out value="${budgetSample}"/>)
            </td>
        </tr>
        <tr>
            <th>
                <I18n:message key="SHOW_IN_TOOLBAR"/>
            </th>
            <td>
                <c:choose>
                    <c:when test="${showInToolbar ne true}">
                        <html:img src="${contextPath}${ImageServlet}/cssimages/ico.unchecked.gif"/>
                    </c:when>
                    <c:otherwise>
                        <html:img src="${contextPath}${ImageServlet}/cssimages/ico.checked.gif"/>
                    </c:otherwise>
                </c:choose>
            </td>
        </tr>
        <tr>
            <th>
                <span style="white-space: nowrap;"><I18n:message key="DEFAULT_LINK"/></span>
            </th>
            <td>
                <c:choose>
                    <c:when test="${defaultLink eq true}">
                        <I18n:message key="MANAGE_TASK_PAGE_FIRST"/>
                    </c:when>
                    <c:otherwise>
                        <I18n:message key="VIEW_SUBTASKS_PAGE"/>
                    </c:otherwise>
                </c:choose>
            </td>
        </tr>
        <tr>
            <th>
                <span style="white-space: nowrap;"><I18n:message key="SORT_ORDER_IN_TREE"/></span>
            </th>
            <td>
                <c:choose>
                    <c:when test="${sortOrderInTree eq false}">
                        <I18n:message key="SORT_UPDATE"/>
                    </c:when>
                    <c:otherwise>
                        <I18n:message key="SORT_ASC"/>
                    </c:otherwise>
                </c:choose>
            </td>
        </tr>
        <tr>
            <th>
                <span style="white-space: nowrap;"><I18n:message key="DISPLAY_CATEGORY_IN_TREE"/></span>
            </th>
            <td>
                <c:if test="${hiddenInTree eq 'F'}">
                    <I18n:message key="HIDE_CLOSED_CATEGORY_IN_TREE"/>
                </c:if>
                <c:if test="${hiddenInTree eq 'N'}">
                    <I18n:message key="HIDE_CATEGORY_IN_TREE"/>
                </c:if>
                <c:if test="${hiddenInTree eq 'E'}">
                    <I18n:message key="ALWAYS_SHOW_CATEGORY_IN_TREE"/>
                </c:if>
            </td>
        </tr>
        <tr>
            <th>
                <span style="white-space: nowrap;"><I18n:message key="VIEW_CATEGORY_AS"/></span>
            </th>
            <td>
                <c:if test="${viewCategory eq ''}">
                    <I18n:message key="VIEW_CATEGORY_AS_TASK"/>
                </c:if>
                <c:if test="${viewCategory eq 'D'}">
                    <I18n:message key="VIEW_CATEGORY_AS_DOCUMENT"/>
                </c:if>
                <c:if test="${viewCategory eq 'B'}">
                    <I18n:message key="VIEW_CATEGORY_AS_FILE_CONTAINER"/>
                </c:if>
            </td>
        </tr>
    </table>
</div>
<div class="general">
    <table class="general" cellpadding="0" cellspacing="0">
        <caption>
            <I18n:message key="RELATED_CATEGORIES"/>
        </caption>
        <tr class="wide">
            <th>
                <I18n:message key="NAME"/>
            </th>
            <th>
                <I18n:message key="WORKFLOW"/>
            </th>
        </tr>
        <c:forEach var="cat" items="${childCategories}" varStatus="varCounter">
            <tr class="line<c:out value="${varCounter.index mod 2}"/>">
                <td>
                    <c:choose>
                        <c:when test="${canManage}">
                            <html:link styleClass="internal"
                                       href="${contextPath}/CategoryViewAction.do?method=page&amp;categoryId=${cat.id}&amp;id=${cat.taskId}">
                                <html:img styleClass="icon" border="0" src="${contextPath}${ImageServlet}/icons/categories/${cat.icon}"/>
                                <c:out value="${cat.name}"/>
                            </html:link>
                        </c:when>
                        <c:otherwise>
                            <html:img styleClass="icon" border="0" src="${contextPath}${ImageServlet}/icons/categories/${cat.icon}"/>
                            <c:out value="${cat.name}"/>
                        </c:otherwise>
                    </c:choose>
                </td>
                <td>
                    <c:out value="${cat.workflow.name}"/>
                </td>
            </tr>
        </c:forEach>
    </table>
</div>
<c:if test="${canViewTriggers}">
    <div class="general">
        <table class="general" cellpadding="0" cellspacing="0">
            <caption>
                <I18n:message key="CREATE_TASK_TRIGGERS"/>
            </caption>
            <colgroup>
                <col class="col_1">
                <col class="col_2">
            </colgroup>
            <tr>
                <th>
                    <I18n:message key="TRIGGER_BEFORE"/>
                </th>
                <td>
                    <c:out value="${crBefore}"/>
                </td>
            </tr>
            <tr>
                <th>
                    <I18n:message key="TRIGGER_INSTEADOF"/>
                </th>
                <td>
                    <c:out value="${crInsteadOf}"/>
                </td>
            </tr>
            <tr>
                <th>
                    <I18n:message key="TRIGGER_AFTER"/>
                </th>
                <td>
                    <c:out value="${crAfter}"/>
                </td>
            </tr>
        </table>
    </div>
    <div class="general">
        <table class="general" cellpadding="0" cellspacing="0">
            <caption>
                <I18n:message key="TASK_EDIT_TRIGGERS"/>
            </caption>
            <colgroup>
                <col class="col_1">
                <col class="col_2">
            </colgroup>
            <tr>
                <th>
                    <I18n:message key="TRIGGER_BEFORE"/>
                </th>
                <td>
                    <c:out value="${updBefore}"/>
                </td>
            </tr>
            <tr>
                <th>
                    <I18n:message key="TRIGGER_INSTEADOF"/>
                </th>
                <td>
                    <c:out value="${updInsteadOf}"/>
                </td>
            </tr>
            <tr>
                <th>
                    <I18n:message key="TRIGGER_AFTER"/>
                </th>
                <td>
                    <c:out value="${updAfter}"/>
                </td>
            </tr>
        </table>
    </div>
</c:if>
<c:if test="${template ne null}">
    <div class="general">
        <table class="general" cellpadding="0" cellspacing="0">
            <caption>
                <I18n:message key="CATEGORY_TEMPLATE"/>
            </caption>
            <colgroup>
                <col class="col_1">
                <col class="col_2">
            </colgroup>
            <tr>
                <td colspan="2">
                    <c:out value="${template}" escapeXml="false"/>
                </td>
            </tr>
        </table>
    </div>
</c:if>
<c:if test="${canViewPermissions}">
    <div class="general">
        <table class="general" cellpadding="0" cellspacing="0">
            <caption>
                <I18n:message key="PERMISSIONS_EDIT"/>
            </caption>
            <colgroup>
                <col class="col_1">
                <col class="col_2">
            </colgroup>
            <tr>
                <th>
                    <I18n:message key="CAN_CREATE"/>
                </th>

                <td>
                    <c:forEach var="ruleList" items="${rulesC}">
                        <c:forEach var="rule" items="${ruleList}" varStatus="rc">
                        <span style="white-space: nowrap;">
                            <html:img styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/ico.status.gif"/>
                            <c:out value="${rule.status.name}" escapeXml="true"/>
                            <c:out value="${rule.restriction}" escapeXml="true"/>
                            ${!rc.last? ", ": ""}
                        </span>
                        </c:forEach><br>
                    </c:forEach>
                </td>

            </tr>
            <tr>
                <th>
                    <I18n:message key="CAN_VIEW"/>
                </th>
                <td>
                    <c:forEach var="ruleList" items="${rulesV}">
                        <c:forEach var="rule" items="${ruleList}" varStatus="rc">
                        <span style="white-space: nowrap;">
                            <html:img styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/ico.status.gif"/>
                            <c:out value="${rule.status.name}" escapeXml="true"/>
                            <c:out value="${rule.restriction}" escapeXml="true"/>
                            ${!rc.last? ", ": ""}
                        </span>
                        </c:forEach><br>
                    </c:forEach>
                </td>
            </tr>
            <tr>
                <th>
                    <I18n:message key="CAN_EDIT"/>
                </th>
                <td>
                    <c:forEach var="ruleList" items="${rulesE}">
                        <c:forEach var="rule" items="${ruleList}" varStatus="rc">
                        <span style="white-space: nowrap;">
                            <html:img styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/ico.status.gif"/>
                            <c:out value="${rule.status.name}" escapeXml="true"/>
                            <c:out value="${rule.restriction}" escapeXml="true"/>
                            ${!rc.last? ", ": ""}
                        </span>
                        </c:forEach><br>
                    </c:forEach>
                </td>
            </tr>
            <tr>
                <th>
                    <I18n:message key="CAN_BE_HANDLER"/>
                </th>
                <td>
                    <c:forEach var="ruleList" items="${rulesH}">
                        <c:forEach var="rule" items="${ruleList}" varStatus="rc">
                        <span style="white-space: nowrap;">
                            <html:img styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/ico.status.gif"/>
                            <c:out value="${rule.status.name}" escapeXml="true"/>
                            <c:out value="${rule.restriction}" escapeXml="true"/>
                            ${!rc.last? ", ": ""}
                        </span>
                        </c:forEach><br>
                    </c:forEach>
                </td>
            </tr>
            <tr>
                <th>
                    <I18n:message key="CAN_DELETE"/>
                </th>
                <td>
                    <c:forEach var="ruleList" items="${rulesD}">
                        <c:forEach var="rule" items="${ruleList}" varStatus="rc">
                        <span style="white-space: nowrap;">
                            <html:img styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/ico.status.gif"/>
                            <c:out value="${rule.status.name}" escapeXml="true"/>
                            <c:out value="${rule.restriction}" escapeXml="true"/>
                            ${!rc.last? ", ": ""}
                        </span>
                        </c:forEach><br>
                    </c:forEach>
                </td>
            </tr>
        </table>
    </div>
</c:if>
</div>
</div>

</tiles:put>
</tiles:insert>
