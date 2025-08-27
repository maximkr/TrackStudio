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

<div class="nblueborder">
<div class="ncaption"></div>
<c:if test="${canManage}">
    <div class="controlPanel">
        <html:link styleClass="external"
                   href="${contextPath}${createObjectAction}?method=create&id=${id}&workflowId=${flow.id}">
            <html:img src="${contextPath}${ImageServlet}/cssimages/ico.edit.gif" hspace="0" vspace="0" border="0" align="middle"/>
            <I18n:message key="STATE_ADD"/>
        </html:link>
    </div>
</c:if>

<div class="indent">
    <c:if test="${!flow.hasStart}">
        <div class="general">
            <table class="error" cellpadding="0" cellspacing="0">
                <caption>
                    <I18n:message key="WORKFLOW_INVALID_STATUS_OVERVIEW"/>
                </caption>
                <tr class="line0">
                    <td>
                        <I18n:message key="WORKFLOW_INVALID_HASNT_START_STATE"/>
                    </td>
                </tr>
            </table>
        </div>
    </c:if>
<c:choose>
<c:when test="${!empty statusList}">
<html:form action="/TaskStatusAction" method="post" styleId="checkunload" onsubmit="return onSubmitFunction();">
<html:hidden property="method" value="save" styleId="taskStatusListId"/>
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
                <I18n:message key="TASK_STATE"/>
            </th>
            <th id="taskColorHeader">
                <I18n:message key="COLOR"/>
            </th>
            <th>
                <I18n:message key="START"/>
            </th>
            <th>
                <I18n:message key="FINAL"/>
            </th>
        </tr>
        <c:forEach var="status" items="${statusList}" varStatus="varCounter">
            <tr class="line<c:out value="${varCounter.index mod 2}"/>">
                <c:if test="${canManage}">
                    <td>
                        <span style="text-align: center">
                            <input type="checkbox" name="delete" alt="delete1" value="<c:out value="${status.id}"/>">
                        </span>
                    </td>
                </c:if>
                <td>
                    <c:choose>
                        <c:when test="${canManage}">
                            <html:link styleClass="internal" href="${contextPath}/TaskStatusEditAction.do?method=page&state=${status.id}&workflowId=${flow.id}&id=${id}">
                                <img title="<I18n:message key="OBJECT_PROPERTIES_EDIT"/>" border="0"
                                     hspace="0" vspace="0"
                                     src="<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.edit.gif"/>
                                <c:out value="${status.name}" escapeXml="true"/>
                            </html:link>
                        </c:when>
                        <c:otherwise>
                            <c:out value="${status.name}" escapeXml="true"/>
                        </c:otherwise>
                    </c:choose>
                </td>
                <td>
                    <c:choose>
                        <c:when test="${canManage}">
                            <span style="float: left; background-color: <c:out value="${status.encodeColor}"/>; width: 20px; height: 20px; border: #5e5e4c 1px solid; margin-left: 12px; margin-right: 12px" id="colorbox-<c:out value="${status.id}"/>" onclick="startColorPicker('${status.id}');"></span>
                            <input alt="color" type="text" value="<c:out value="${status.color}"/>" name="value(color-<c:out value="${status.id}"/>)" id="value(color-<c:out value="${status.id}"/>)">&nbsp;
                        </c:when>
                        <c:otherwise>
                            <span style="float: left; background-color: <c:out value="${status.encodeColor}"/>; width: 20px; height: 20px; border: #5e5e4c 1px solid; margin-left: 12px; margin-right: 12px"></span>
                        </c:otherwise>
                    </c:choose>
                </td>
                <td>
                    <span style="text-align: center">
                        <html:checkbox property="value(start-${status.id})"/>
                        <c:if test="${status.secondaryStart}">
                            <img title="<I18n:message key="SECONDARY_STATUS_START"/>" src="${contextPath}${ImageServlet}/cssimages/icon-info.gif" border="0" width="15" height="15"/>
                        </c:if>
                    </span>
                </td>
                <td>
                    <span style="text-align: center">
                        <html:checkbox property="value(finish-${status.id})"/>
                    </span>
                </td>
            </tr>
        </c:forEach>
    </table>
</div>

<div class="controls">
    <c:if test="${canManage}">
        <input type="submit" class="iconized secondary"
               value="<I18n:message key="CLONE"/>"
               name="CLONE"
               onclick="if (validate(this.form)) setSubmitForm(true); else setSubmitForm(false); setMethod('clone');">
    </c:if>
    <input type="submit" class="iconized"
           value="<I18n:message key="SAVE"/>"
           name="SAVE"
           onclick="if (validate(this.form)) setSubmitForm(true); else setSubmitForm(false); setMethod('save');">
    <c:if test="${canManage}">
        <input type="submit" class="iconized secondary"
               value="<I18n:message key="DELETE"/>"
               name="DELETE"
               onclick="checkDeleteSelectedAndStart(); if (onSubmitFunction()) setMethod('delete');">
    </c:if>
    <script type="text/javascript">
        var submitForm = false;

        function setSubmitForm(bool) {
            submitForm = bool;
        }

        function onSubmitFunction() {
            return submitForm;
        }

        function checkDeleteSelectedAndStart() {
            if (!startStateAlert("<I18n:message key="DELETE_STATES_REQ"/>", "taskStatusForm", "${startState}")) {
                submitForm = deleteConfirm("<I18n:message key="DELETE_STATES_REQ"/>", "taskStatusForm");
            }
            return submitForm;
        }

        function checkDeleteSelected() {
            submitForm = deleteConfirm("<I18n:message key="DELETE_STATES_REQ"/>", "taskStatusForm");
            return submitForm;
        }

        function setMethod(target) {
            document.getElementById('taskStatusListId').value = target;
        }
    </script>
</div>
</html:form>
</c:when>

    <c:otherwise>
        <div class="empty"><I18n:message key="EMPTY_STATES_LIST_WORKFLOW"/></div>
    </c:otherwise>
    </c:choose>
</div>
</div>
</tiles:put>
</tiles:insert>
