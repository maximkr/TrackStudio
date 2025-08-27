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
<c:if test="${canManage}">
<div class="nblueborder">
<div class="ncaption"></div>
<c:if test="${canManage}">
    <div class="controlPanel">
        <html:link styleClass="external"
                   href="${contextPath}${createObjectAction}?method=create&id=${id}&workflowId=${workflowId}">
            <html:img src="${contextPath}${ImageServlet}/cssimages/ico.messagetypes.gif" hspace="0" vspace="0" border="0"
                      align="middle"/>
            <I18n:message key="MESSAGE_TYPE_ADD"/>
        </html:link>
    </div>
</c:if>

<div class="indent">
    <c:if test="${!isValidPermissions}">
        <div class="general">
            <table class="error" cellpadding="0" cellspacing="0">
                <caption>
                    <I18n:message key="WORKFLOW_INVALID_PERMISSION_OVERVIEW"/>
                </caption>
                <c:forEach var="prstatus" items="${canViewCanEditPrstatusList}" varStatus="counter">
                    <c:forEach var="mstatus" items="${canViewCanEditMap[prstatus.id]}">
                        <tr class="line<c:out value="${counter.index mod 2}"/>">
                            <td>
                                <I18n:message key="WORKFLOW_INVALID_PERMISSION_CONFLICT">
                                    <I18n:param value="${prstatus.name}"/>
                                    <I18n:param value="${mstatus.name}"/>
                                </I18n:message>
                            </td>
                        </tr>
                    </c:forEach>
                </c:forEach>
            </table>
        </div>
    </c:if>
<c:choose>
<c:when test="${!empty mstatusList}">
<html:form action="/MstatusAction" method="post" styleId="checkunload" onsubmit="return onSubmitFunction();">
    <html:hidden property="method" value="save" styleId="mstatusListId"/>
    <html:hidden property="id" value="${id}"/>
    <html:hidden property="session" value="${session}"/>
    <html:hidden property="workflowId" value="${flow.id}"/>
    <div class="general">
        <table class="general" cellpadding="0" cellspacing="0">
            <tr class="wide">
                <c:if test="${canManage}">
                    <th width="1%" nowrap style="white-space:nowrap">
                        <input type="checkbox" onClick="selectAllCheckboxes(this, 'delete1')">
                    </th>
                </c:if>
                <th>
                    <I18n:message key="MESSAGE_TYPE"/>
                </th>
                <th>
                    <I18n:message key="DESCRIPTION"/>
                </th>

            </tr>
            <c:forEach var="mstatus" items="${mstatusList}" varStatus="varCounter">
                <tr class="line<c:out value="${varCounter.index mod 2}"/>">
                    <c:if test="${canManage}">
                        <td>
                            <span style="text-align: center">
                                <input type="checkbox" name="delete" alt="delete1" value="<c:out value="${mstatus.id}"/>">
                            </span>
                        </td>
                    </c:if>
                    <td>
                        <c:choose>
                            <c:when test="${!flow.canManage}">
                                <html:link styleClass="internal"
                                        href="${contextPath}/MstatusViewAction.do?method=page&mstatusId=${mstatus.id}&workflowId=${flow.id}&id=${id}">
                                    <img title="<I18n:message key="OBJECT_PROPERTIES_VIEW"/>"
                                         border="0" hspace="0" vspace="0"
                                         src="<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.closed.gif"/>
                                </html:link>
                            </c:when>
                            <c:otherwise>
                                <html:link styleClass="internal"
                                        href="${contextPath}/MstatusEditAction.do?method=page&mstatusId=${mstatus.id}&workflowId=${flow.id}&id=${id}">
                                    <img title="<I18n:message key="OBJECT_PROPERTIES_EDIT"/>"
                                         border="0" hspace="0" vspace="0"
                                         src="<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.edit.gif"/>
                                </html:link>
                            </c:otherwise>
                        </c:choose>
                        <html:link styleClass="internal"
                                href="${contextPath}/MstatusViewAction.do?method=page&mstatusId=${mstatus.id}&id=${id}&workflowId=${flow.id}">
                            <c:out value="${mstatus.name}" escapeXml="true"/>
                        </html:link>
                    </td>
                    <td>
                        <c:out value="${mstatus.description}" escapeXml="true"/>
                    </td>
                    
                </tr>
            </c:forEach>
        </table>
    </div>
    <c:if test="${canManage eq true}">
        <div class="controls">
                <input type="submit" class="iconized"
                       value="<I18n:message key="CLONE"/>"
                       name="CLONE" onclick="onSubmit(); setMethod('clone');">
            <c:if test="${_can_delete eq true}">
                <input type="submit" class="iconized"
                       value="<I18n:message key="DELETE"/>"
                       name="DELETE" onclick="checkDeleteSelected(); if (onSubmitFunction()) setMethod('delete');">
            </c:if>
            <script type="text/javascript">
                var submitForm = false;

                function onSubmitFunction() {
                    return submitForm;
                }

                function onSubmit() {
                    submitForm = true;
                }

                function checkDeleteSelected() {
                    submitForm = deleteConfirm("<I18n:message key="DELETE_MESSAGES"/> ?", "mstatusForm");
                    return submitForm;
                }

                function setMethod(target) {
                    document.getElementById('mstatusListId').value = target;
                }
            </script>
        </div>
    </c:if>
</html:form>
</c:when>
<c:otherwise>
    <div class="empty"><I18n:message key="EMPTY_MESSAGES_TYPES_LIST_WORKFLOW"/></div>
</c:otherwise>
</c:choose>
</div>
</div>
</c:if>
</tiles:put>
</tiles:insert>
