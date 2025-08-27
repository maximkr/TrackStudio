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
<tiles:put name="tabs" type="string"/>
<tiles:put name="main" type="string">
<html:form method="post" styleId="checkunload" action="/CategoryEditAction" onsubmit="return validate(this); ">
<html:hidden property="method" value="save"/>
<html:hidden property="id" value="${id}"/>
<html:hidden property="categoryId" value="${categoryId}"/>
<html:hidden property="session" value="${session}"/>

<div class="blueborder">
<div class="caption">
    <I18n:message key="PROPERTIES_EDIT"/>
</div>
<div class="indent">
<table class="general" cellpadding="0" cellspacing="0">
<colgroup>
    <col class="col_1">
    <col class="col_2">
</colgroup>
<tr>
    <th>
        <label for="name"><I18n:message key="NAME"/>*</label>
    </th>
    <td>
        <html:text alt=">0" size="50" maxlength="200" property="name" styleId="name"/>
        <span class="sample"><I18n:message key="CATEGORY_NAME_SAMPLE"/></span>
    </td>
</tr>
<tr>
    <th>
        <label for="description"><I18n:message key="DESCRIPTION"/></label>
    </th>
    <td>
        <html:text size="50" maxlength="200" property="description" styleId="description"/>
        <span class="sample"><I18n:message key="CATEGORY_DESCRIPTION_SAMPLE"/></span>
    </td>
</tr>
<tr>
    <th>
        <label for="action"><I18n:message key="ACTION"/></label>
    </th>
    <td>
        <html:text size="50" maxlength="200" property="action" styleId="action"/>
        <span class="sample"><I18n:message key="CATEGORY_ACTION_SAMPLE"/></span>
    </td>
</tr>

<tr>
    <th>
        <I18n:message key="WORKFLOW"/>
    </th>
    <td>
        <c:choose>
            <c:when test="${canChangeWF || createNewCategory}">
                <html:select property="workflow">
                    <html:options collection="workflowSet" property="id" labelProperty="name"/>
                </html:select>
            </c:when>
            <c:otherwise>
                <html:hidden property="workflow"/>
                <html:img src="${contextPath}${ImageServlet}/cssimages/ico.workflow.gif"/><c:out value="${workflowName}"/>
            </c:otherwise>
        </c:choose>
    </td>
</tr>
<tr>
    <th>
        <I18n:message key="ICON"/>
    </th>
    <td>
        <table border="0" cellspacing="0" width="100%">
            <tr>
                <c:forEach var="icon" items="${icons}" varStatus="status">
                <c:if test="${(status.index mod 3) eq 0}"></tr>
            <tr></c:if>
                <td style="white-space: nowrap;">
                    <html:radio property="icon" value="${icon}" styleId="ic_${status.index}"/>
                    <label for="ic_${status.index}">
                        <html:img src="${contextPath}${ImageServlet}/icons/categories/${icon}"/>
                        <c:out value="${icon}"/>
                    </label></td>
                </c:forEach>
            </tr>
        </table>
    </td>
</tr>

<tr>
    <th>
        <I18n:message key="HANDLER_REQUIRED"/>
    </th>
    <td>
        <html:checkbox property="handlerRequired"/>
    </td>
</tr>
<tr>
    <th>
        <I18n:message key="GROUP_ASSIGMENT_ALOWED"/>
    </th>
    <td>
        <html:checkbox property="groupHandlerAllowed" value="true"/>
    </td>
</tr>
<tr>
    <th>
        <I18n:message key="HANDLER_ONLY_ROLE"/>
    </th>
    <td>
        <html:checkbox property="handerlOnlyRole" value="true"/>
    </td>
</tr>
<tr>
    <th>
        <I18n:message key="CATEGORY_BUDGET_FORMAT"/>
    </th>
    <td>
        <div class="budget">
            <html:checkbox styleId="years" property="years"/>
            <label for="years">
                <I18n:message key="BUDGET_YEARS"/>
            </label>
            <html:checkbox styleId="months" property="months"/>
            <label for="months">
                <I18n:message key="BUDGET_MONTHS"/>
            </label>
            <html:checkbox styleId="weeks" property="weeks"/>
            <label for="weeks">
                <I18n:message key="BUDGET_WEEKS"/>
            </label>
            <html:checkbox styleId="days" property="days"/>
            <label for="days">
                <I18n:message key="BUDGET_DAYS"/>
            </label>
            <html:checkbox styleId="hours" property="hours"/>
            <label for="hours">
                <I18n:message key="BUDGET_HOURS"/>
            </label>
            <html:checkbox styleId="munites" property="minutes"/>
            <label for="munites">
                <I18n:message key="BUDGET_MINUTES"/>
            </label>
            <html:checkbox styleId="seconds" property="seconds"/>
            <label for="seconds">
                <I18n:message key="BUDGET_SECONDS"/>
            </label>
        </div>
    </td>
</tr>
<tr>
    <th>
        <span style="white-space: nowrap;"><I18n:message key="SHOW_IN_TOOLBAR"/></span>
    </th>
    <td>
        <html:checkbox property="showInToolbar" styleClass="checkbox"/>
    </td>
</tr>
<tr>
    <th>
        <span style="white-space: nowrap;"><I18n:message key="DEFAULT_LINK"/></span>
    </th>
    <td>
        <html:radio property="defaultLink" styleClass="checkbox" value="V" styleId="default_task"/>
        <label for="default_task">
            <I18n:message key="MANAGE_TASK_PAGE_FIRST"/>
        </label><br>
        <html:radio property="defaultLink" styleClass="checkbox" value="" styleId="default_subtasks"/>
        <label for="default_subtasks">
            <I18n:message key="VIEW_SUBTASKS_PAGE"/>
        </label>
    </td>
</tr>
<tr>
    <th>
        <span style="white-space: nowrap;"><I18n:message key="SORT_ORDER_IN_TREE"/></span>
    </th>
    <td>
        <html:radio property="sortOrderInTree" styleClass="checkbox" value="false" styleId="sort_update"/>
        <label for="sort_update">
            <I18n:message key="SORT_UPDATE"/>
        </label><br>
        <html:radio property="sortOrderInTree" styleClass="checkbox" value="true" styleId="sort_asc"/>
        <label for="sort_asc">
            <I18n:message key="SORT_ASC"/>
        </label>
    </td>
</tr>
<tr>
    <th>
        <span style="white-space: nowrap;"><I18n:message key="HIDE_CATEGORY_IN_TREE"/></span>
    </th>
    <td>
        <html:radio property="hiddenInTree" styleClass="checkbox" styleId="checkbox_n" value="N"/>
        <label for="checkbox_n" >
            <I18n:message key="HIDE_CATEGORY_IN_TREE"/>
        </label><br>
        <html:radio property="hiddenInTree" styleClass="checkbox" styleId="checkbox_e" value="E"/>
        <label for="checkbox_e">
            <I18n:message key="ALWAYS_SHOW_CATEGORY_IN_TREE"/>
        </label><br>
        <html:radio property="hiddenInTree" styleClass="checkbox" styleId="checkbox_f" value="F"/>
        <label for="checkbox_f">
            <I18n:message key="HIDE_CLOSED_CATEGORY_IN_TREE"/>
        </label>
        <span class="sample"><I18n:message key="REFRESH_TREE"/></span>
    </td>
</tr>
<tr>
    <th>
        <span style="white-space: nowrap;"><I18n:message key="VIEW_CATEGORY_AS"/></span>
    </th>
    <td>
        <html:radio property="viewCategory" styleClass="checkbox" styleId="view_task" value=""/>
        <label for="view_task" >
            <I18n:message key="VIEW_CATEGORY_AS_TASK"/>
        </label><br>
        <html:radio property="viewCategory" styleClass="checkbox" styleId="view_document" value="D"/>
        <label for="view_document" >
            <I18n:message key="VIEW_CATEGORY_AS_DOCUMENT"/>
        </label><br>
        <html:radio property="viewCategory" styleClass="checkbox" styleId="view_file" value="B"/>
        <label for="view_file" >
            <I18n:message key="VIEW_CATEGORY_AS_FILE_CONTAINER"/>
        </label>
    </td>
</tr>
</table>
<div class="controls">
    <input type="submit" class="iconized"
           value="<I18n:message key="SAVE"/>"
           name="SETCATEGORY">

    <html:button styleClass="iconized secondary" property="cancelButton"
                 onclick="document.location='${contextPath}/CategoryAction.do?method=page&amp;id=${id}';">
        <I18n:message key="CANCEL"/>
    </html:button>

</div>
</div>
</div>
</html:form>
</tiles:put>
</tiles:insert>
