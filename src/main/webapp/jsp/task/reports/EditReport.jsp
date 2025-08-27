<%@ page buffer="128kb" errorPage="/jsp/Error.jsp" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/tiles-el" prefix="tiles" %>


<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value="${sc.timezone}"/>
<I18n:setBundle basename="language"/>
<tiles:insert page="/jsp/layout/ListLayout.jsp" flush="true">
<tiles:put name="customHeader" value="/jsp/task/reports/TaskReportHeader.jsp"/>
<tiles:put name="header" value="/jsp/task/TaskHeader.jsp"/>
<tiles:put name="tabs" type="string"/>
<tiles:put name="main" type="string">
<div class="blueborder">
<div class="caption">
    <I18n:message key="REPORT_EDIT"/>
</div>
<div class="indent">
<html:form method="post" action="/ReportEditAction" onsubmit="return validate(this)">
<html:hidden property="method"/>
<html:hidden property="id" value="${id}"/>
<html:hidden property="session" value="${session}"/>
<html:hidden property="type"/>
<html:hidden property="reportId"/>
<table class="general" cellpadding="0" cellspacing="0">
    <caption>
        <I18n:message key="REPORT_PROPERTIES"/>
    </caption>
    <COLGROUP>
        <COL class="col_1">
        <COL class="col_2">
    </COLGROUP>
    <tr>
        <th><label for="name">
            <I18n:message key="NAME"/>
            *</label></th>
        <td>
            <html:text styleId="name" property="name" size="40" maxlength="200" alt=">0"/>
        </td>
    </tr>
    <tr>
        <th>
            <I18n:message key="TYPE"/>
        </th>
        <td>
            <c:out value="${reportType}" escapeXml="true"/>
        </td>
    </tr>
        <tr>
            <th><label for="filter">
                <I18n:message key="FILTER"/>
            </label></th>
            <td>
                <html:select property="filter" styleId="filter" onchange="checkFilter(this);">
                    <c:forEach var="fli" items="${filters}" varStatus="counter">
                        <html:option value="${fli.id}" styleClass="${fli.priv ? 'private' : ''}">
                            <c:out value="${fli.name}"/>
                        </html:option>
                    </c:forEach>
                </html:select>
            </td>
        </tr>
    <tr>
        <th>
            <label for="priv"><span style="white-space: nowrap;"><I18n:message key="REPORT_SHARE"/></span></label>
        </th>
        <td>
            <c:choose>
                <c:when test="${!canCreatePublicReport}">
                    <html:checkbox property="shared" styleId="shared" styleClass="checkbox" disabled="true"/>
                    <html:hidden property="shared"/>
                </c:when>
                <c:otherwise>
                    <html:checkbox property="shared" styleId="shared" styleClass="checkbox" disabled="${currentFilter.priv}"/>
                </c:otherwise>
            </c:choose>
        </td>
    </tr>
    <c:if test="${currentReport ne null}">
        <tr>
            <th>
                <I18n:message key="OWNER"/>
            </th>
            <td><span class="user" ${currentReport.owner.id eq sc.userId ? "id='loggedUser'" : ""}>
            <html:img styleClass="icon" border="0"
                      src="${contextPath}${ImageServlet}/cssimages/${currentReport.owner.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
            <c:out value="${currentReport.owner.name}" escapeXml="true"/>
			</span></td>
        </tr>
    </c:if>
</table>
</div>

<div class="controls">
    <input type="submit" class="iconized"
           value="<I18n:message key="SAVE"/>"
           name="SUBMIT">
    <html:button styleClass="iconized secondary" property="cancelButton"
                 onclick="document.location='${contextPath}/ReportAction.do?method=page&id=${id}';">
        <I18n:message key="CANCEL"/>
    </html:button>
</div>
</html:form>
<script type="text/javascript">
    function checkFilter(A) {
        if (A.options[A.selectedIndex].className == 'private') {
            A.form.shared.checked = false;
            A.form.shared.disabled = true;
        } else {
            if (!A.form.shared.checked) A.form.shared.disabled = false;
        }
    }
</script>
</div>
</div>
</tiles:put>
</tiles:insert>
