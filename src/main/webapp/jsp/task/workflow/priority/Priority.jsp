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
            <I18n:message key="PRIORITY_ADD"/>
        </html:link>
    </div>
</c:if>

<div class="indent">
<c:choose>
<c:when test="${!empty priorityList}">
<html:form action="/PriorityAction" method="post" styleId="checkunload" onsubmit="return onSubmitFunction();">
<html:hidden property="method" value="save" styleId="priorityListId"/>
<html:hidden property="id" value="${id}"/>
<html:hidden property="session" value="${session}"/>
<html:hidden property="workflowId" value="${flow.id}"/>

<table class="general" cellpadding="0" cellspacing="0">
    <tr class="wide">
        <c:if test="${canDelete}">
            <th width="1%" nowrap style="white-space:nowrap">
                <input type="checkbox" onClick="selectAllCheckboxes(this, 'delete1')">
            </th>
        </c:if>
        <th>
            <I18n:message key="NAME"/>
        </th>
        <th>
            <I18n:message key="DESCRIPTION"/>
        </th>
        <c:if test="${showOrder}">
            <th>
                <I18n:message key="ORDER"/>
            </th>
        </c:if>
        <th>
            <I18n:message key="DEFAULT"/>
        </th>
    </tr>
    <c:forEach var="priority" items="${priorityList}" varStatus="varCounter">
        <tr class="line<c:out value="${varCounter.index mod 2}"/>">
            <c:if test="${canDelete}">
                <td id="row<c:out value="${varCounter.index}"/>_checkbox">
                    <span style="text-align: center">
                        <input type="checkbox" name="delete" alt="delete1" value="<c:out value="${priority.id}"/>">
                    </span>
                </td>
            </c:if>
            <td id="row<c:out value="${varCounter.index}"/>_name">
                <c:choose>
                    <c:when test="${canManage}">
                        <html:link styleClass="internal" href="${contextPath}/PriorityEditAction.do?method=page&priority=${priority.id}&workflowId=${flow.id}&id=${id}">
                            <img title="<I18n:message key="OBJECT_PROPERTIES_EDIT"/>" border="0"
                                 hspace="0" vspace="0"
                                 src="<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.edit.gif"/>
                            <c:out value="${priority.name}" escapeXml="true"/>
                        </html:link>
                    </c:when>
                    <c:otherwise>
                        <c:out value="${priority.name}" escapeXml="true"/>
                    </c:otherwise>
                </c:choose>
            </td>
            <td id="row<c:out value="${varCounter.index}"/>_description">
                <c:out value="${priority.description}" escapeXml="true"/>
            </td>
            <c:if test="${showOrder}">
                <td>
                    <table cellpadding="0" cellspacing="0">
                    <tr>
                        <td style="width: 50%;">
                        <c:if test="${varCounter.index ne 0}">
                            <input type="button" value="&#8593;" class="iconized" style="color: green;"
                                   onclick="swapCells(<c:out value="${varCounter.index}"/>,<c:out value="${varCounter.index-1}"/>);">
                        </c:if>
                        </td>
                        <td style="width: 50%;">
                        <c:if test="${!varCounter.last}">
                            <input type="button" value="&#8595;" class="iconized" style="color: red;"
                                   onclick="swapCells(<c:out value="${varCounter.index}"/>,<c:out value="${varCounter.index+1}"/>);">
                        </c:if>
                        </td>
                    </tr>
                    </table>
                </td>
            </c:if>
            <td id="row<c:out value="${varCounter.index}"/>_radio">
                <span style="text-align: center">
                    <html:radio styleId="radio" property="defaultForRadioButton" value="${priority.id}"/>
               </span>
                <input type="hidden" id="order<c:out value="${varCounter.index}"/>" value="${priority.order}"
                       name="value(priority-<c:out value="${priority.id}"/>)"/>
            </td>
        </tr>
    </c:forEach>
</table>
<div class="controls">
      <c:if test="${canManage}">
        <input type="submit" class="iconized secondary"
               value="<I18n:message key="CLONE"/>"
               name="CLONE" onclick="setSubmitForm(true); setMethod('clone');">
    </c:if>
    <input type="submit" class="iconized"
           value="<I18n:message key="SAVE"/>"
           name="SAVE" onclick="setSubmitForm(true); setMethod('save');">
    <c:if test="${canDelete}">
        <input type="submit" class="iconized secondary"
               value="<I18n:message key="DELETE"/>"
               name="DELETE" onclick="checkDeleteSelectedAndDefault();">
    </c:if>
    <script type="text/javascript">
        var submitForm = false;

        function setSubmitForm(bool) {
            submitForm = bool;
        }

        function onSubmitFunction() {
            return submitForm;
        }

        function checkDeleteSelectedAndDefault() {
            if (deleteConfirm("<I18n:message key="DELETE_PRIORITIES_REQ"/>", "priorityForm")) {
                setSubmitForm(true);
                setMethod('delete');
            }
        }

        function setMethod(target) {
            document.getElementById('priorityListId').value = target;
        }

        function swapCells(idA, idB) {
            var cellACheckbox = document.getElementById("row" + idA + "_checkbox");
            var cellBCheckbox = document.getElementById("row" + idB + "_checkbox");

            var cellAName = document.getElementById("row" + idA + "_name");
            var cellBName = document.getElementById("row" + idB + "_name");

            var cellADescription = document.getElementById("row" + idA + "_description");
            var cellBDescription = document.getElementById("row" + idB + "_description");

            var cellARadio = document.getElementById("row" + idA + "_radio");
            var cellBRadio = document.getElementById("row" + idB + "_radio");

            var hiddenAOrder = document.getElementById("order" + idA);
            var hiddenBOrder = document.getElementById("order" + idB);

            var priorityForm = document.getElementsByName("priorityForm")[0];

            if (cellACheckbox && cellBCheckbox && cellAName && cellBName && cellADescription && cellBDescription && cellARadio && cellBRadio) {
                var temp = cellACheckbox.innerHTML;
                cellACheckbox.innerHTML = cellBCheckbox.innerHTML;
                cellBCheckbox.innerHTML = temp;

                temp = cellAName.innerHTML;
                cellAName.innerHTML = cellBName.innerHTML;
                cellBName.innerHTML = temp;

                temp = cellADescription.innerHTML;
                cellADescription.innerHTML = cellBDescription.innerHTML;
                cellBDescription.innerHTML = temp;

                temp = cellARadio.innerHTML;
                cellARadio.innerHTML = cellBRadio.innerHTML;
                cellBRadio.innerHTML = temp;

                var j = 1;
                for (var i = priorityForm.elements.length-1; i >=0; i--) {
                    if ((priorityForm.elements[i].type == "hidden") && (priorityForm.elements[i].id.indexOf("order") != -1)) {
                        priorityForm.elements[i].value = j;
                        j++;
                    }
                }
            }
        }
    </script>
</div>
</html:form>
</c:when>
    <c:otherwise>
        <div class="empty"><I18n:message key="EMPTY_PRIORITIES_LIST_WORKFLOW"/></div>
    </c:otherwise>
    </c:choose>
</div>
</div>
</tiles:put>
</tiles:insert>


