<%@ page buffer="128kb" errorPage="/jsp/Error.jsp"%>
<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/tiles-el" prefix="tiles" %>
<c:set var="taskMenu" value="false"/>
<c:set var="userMenu" value="true"/>
<I18n:setLocale value='${sc.locale}'/>
<I18n:setTimeZone value='${sc.timezone}'/>
<I18n:setBundle basename='language'/>
<tiles:insert page="/jsp/layout/ListLayout.jsp" flush="true">
    <tiles:put name="header" value="/jsp/task/TaskHeader.jsp"/>
    
    <tiles:put name="customHeader" value="/jsp/task/registration/UserRegistrationHeader.jsp"/>
    <tiles:put name="tabs" type="string"/>
    <tiles:put name="main" type="string">
<c:if test="${canView}">
<div class="blueborder">
    <div class="caption"><I18n:message key="REGISTRATION_PROPERTIES"/></div>
    <div class="indent">
<html:form method="POST" action="/RegistrationEditGeneralAction" onsubmit="return validate(this);">    
<div class="general">
    <table class="general" cellpadding="0" cellspacing="0">
<COLGROUP>
<COL class="col_1">
<COL class="col_2">
</COLGROUP>
<html:hidden property="method" value="save" styleId="methodName"/>
<html:hidden property="id" value="${id}"/>
<html:hidden property="registration" value="${registration.id}"/>
<html:hidden property="session" value="${session}"/>

<tr>
<th>
 <I18n:message key="NAME"/>
 </th>
 <td>
    <html:text styleId="REG_NAME_ID" property="name" size="50" maxlength="200" alt=">0" />
 </td>
</tr>
<tr>
<th>
 <I18n:message key="OWNER"/>
 </th>
 <td>
    <span class="user" ${owner.id eq sc.userId ? "id='loggedUser'" : ""}>
            <html:img  styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/${owner.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
            <c:out value="${owner.name}" escapeXml="true"/>
			</span>
 </td>
</tr>
<tr>
<th>
 <I18n:message key="REGISTRATION_URL"/>
 </th>
 <td>
    <c:choose>
        <c:when test="${isRegistrationExist}">
            <html:link styleClass="internal" href="${url}"><c:out value="${url}" escapeXml="true"/></html:link>
        </c:when>
        <c:otherwise>
            <c:out value="${url}" escapeXml="true"/>
        </c:otherwise>
     </c:choose>
 </td>
</tr>
<tr>
<th><I18n:message key="PRSTATUS"/></th>
<td>
        <html:select property="statusId">
                    <html:options collection="registrationPrstatuses" property="id" labelProperty="name"/>
        </html:select>
</td>
</tr>
<tr>
<th><I18n:message key="USERS_ALLOWED"/></th>
<td>
    <html:text property="child" maxlength="7" size="7" alt="integer" value="${child}"/>
</td>
</tr>
<tr>
<th><I18n:message key="EXPIRE_IN"/></th>
<td>
    <html:text property="expire" maxlength="7" size="7" alt="integer" value="${expire}"/>
</td>
</tr>
<tr>
<th><I18n:message key="HOME_PROJECT_CREATE"/></th>
<td>
<input type="checkbox" name="task" <c:out value="${checkbox_task}" escapeXml="false"/> onClick="{var chb = document.getElementById('select_category'); chb.disabled = !chb.disabled;}">
</td>
</tr>

<tr>
<th><I18n:message key="CATEGORY"/></th>
<td>

        <html:select property="categoryId" disabled="${view}" styleId="select_category">
            <html:options collection="categories" property="id" labelProperty="name"/>
        </html:select>
</td>
</tr>
        <tr>
        <th><I18n:message key="SHARED"/></th>
        <td>
        <html:checkbox property="shared"/>
        </td>
        </tr>
</table>
</div>

    <div class="controls">
	<input type="submit"  class="iconized"
				value="<I18n:message key="SAVE"/>"
                onClick="set('save');"
                name="SAVE">
        <html:button styleClass="iconized secondary" property="cancelButton"
        onclick="document.location='${contextPath}/RegistrationAction.do?method=page&id=${id}';">
        <I18n:message key="CANCEL"/>
        </html:button>
        <SCRIPT type="text/javascript">
            function set(target) {document.getElementById('methodName').value=target;};
        </SCRIPT>
    </div>
</html:form>
 </div>
    </div>
</c:if>
</tiles:put>
</tiles:insert>
