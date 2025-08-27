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
<tiles:put name="customHeader" value="/jsp/task/workflow/mstatus/MstatusHeader.jsp"/>
<tiles:put name="tabs" type="string"/>
<tiles:put name="main" type="string">
<div class="blueborder">
<div class="caption">
    <c:out value="${tableTitle}"/>
</div>
<div class="indent">
<html:form method="post" styleId="checkunload" action="/MstatusEditAction" onsubmit="return validate(this); ">
<html:hidden property="method" value="save"/>
<html:hidden property="id" value="${id}"/>
<html:hidden property="mstatusId" value="${mstatusId}"/>
<html:hidden property="workflowId" value="${workflowId}"/>
<html:hidden property="session" value="${session}"/>
<div class="general">
    <table class="general" cellpadding="0" cellspacing="0">
        <colgroup>
            <col class="col_1">
            <col class="col_2">
        </colgroup>
        <c:choose>
            <c:when test="${!canManage}">
                <tr>
                    <th>
                        <I18n:message key="NAME"/>
                    </th>
                    <td>
                        <c:out value="${mstatus.name}" escapeXml="true"/>
                    </td>
                </tr>
                <tr>
                    <th>
                        <I18n:message key="DESCRIPTION"/>
                    </th>
                    <td><c:out value="${mstatus.description}" escapeXml="true"/>
                </tr>
                <tr>
                    <th>
                        <I18n:message key="ACTION"/>
                    </th>
                    <td>
                        <c:out value="${mstatus.action}" escapeXml="true"/>
                    </td>
                </tr>
            </c:when>
            <c:otherwise>
                <tr>
                    <th>
                        <label for="name"><I18n:message key="NAME"/>*</label>
                    </th>
                    <td>
                        <html:text styleId="name" size="20" maxlength="200" property="name" alt=">0"/><span class="sample"><I18n:message key="MSTATUS_NAME_SAMPLE"/></span>
                    </td>
                </tr>
                <tr>
                    <th>
                        <label for="description"><I18n:message key="DESCRIPTION"/></label>
                    </th>

                    <td>
                        <html:text styleId="description" property="description" size="40" maxlength="200"/><span class="sample"><I18n:message key="MSTATUS_DESCRIPTION_SAMPLE"/></span>
                    </td>
                </tr>
                <tr>
                    <th>
                        <label for="action"><I18n:message key="ACTION"/>*</label>
                    </th>

                    <td>
                        <html:text styleId="action" property="action" size="40" maxlength="200" alt=">0"/><span class="sample"><I18n:message key="MSTATUS_ACTION_SAMPLE"/></span>
                    </td>
                </tr>
            </c:otherwise>
        </c:choose>
 
        <tr>
            <th>
                <I18n:message key="SHOW_IN_TOOLBAR"/>
            </th>
            <td>
                <html:checkbox property="showInToolbar" styleClass="checkbox"/>
            </td>
        </tr>
    </table>
</div>
<c:if test="${canManage}">
    <div class="controls">
        <input type="submit" class="iconized"
               value="<I18n:message key="SAVE"/>"
               name="SETCATEGORY">
        <c:choose>
            <c:when test="${canManage}">
                <html:button styleClass="iconized secondary" property="cancelButton"
                             onclick="document.location='${contextPath}/MstatusAction.do?method=page&id=${id}&workflowId=${workflowId}';">
                    <I18n:message key="CANCEL"/>
                </html:button>
            </c:when>
            <c:otherwise>
                <html:button styleClass="iconized secondary" property="cancelButton"
                             onclick="document.location='${contextPath}/MstatusViewAction.do?method=page&id=${id}&workflowId=${workflowId}&mstatusId=${mstatusId}';">
                    <I18n:message key="CANCEL"/>
                </html:button>
            </c:otherwise>
        </c:choose>
    </div>
</c:if>

</html:form>
</div>
</div>
</tiles:put>
</tiles:insert>
