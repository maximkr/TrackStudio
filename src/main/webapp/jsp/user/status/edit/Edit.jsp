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
<tiles:put name="customHeader" value="/jsp/user/status/StatusHeader.jsp"/>
<tiles:put name="header" value="/jsp/user/UserHeader.jsp" />
    <tiles:put name="tabs" type="string"/>
    <tiles:put name="main" type="string">
<c:if test="${canManage}">
    <div class="blueborder">
        <div class="caption"><I18n:message key="PRSTATUS_PROPERTIES"/></div>
        <div class="indent">
<html:form method="POST" action="/UserStatusEditAction" styleId="checkunload" onsubmit="return validate(this);">
<html:hidden property="method" value="editUserStatus"/>
<html:hidden property="session" value="${session}"/>
<html:hidden property="prstatusId" value="${prstatusId}"/>
<html:hidden property="id" value="${id}"/>
<div class="general">
<table class="general" cellpadding="0" cellspacing="0">
<COLGROUP>
<COL class="col_1">
<COL class="col_2">
</COLGROUP>
<caption><I18n:message key="PRSTATUS_PROPERTIES"/></caption>
<tr>
<th><label for="name"><I18n:message key="NAME"/>*</label></th>
<td>
    <html:text maxlength="200" size="50" property="name" styleId="name"/>
</td>
</tr>
    <tr>
    <th><I18n:message key="OWNER"/></th>
    <td>
        <span class="user" id="${owner.id eq sc.userId ? 'loggedUser' : ''}">
                                            <html:img  styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/${owner.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
                                            <c:out value="${owner.name}"/>
                                        </span>
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

</table>
</div>
        <div class="controls">
	<input type="SUBMIT"  class="iconized"
				value="<I18n:message key="SAVE"/>"
                name="SAVE">
            <c:choose>
                <c:when test="${prstatusId eq null}">
            <html:button styleClass="iconized secondary" property="cancelButton"
            onclick="document.location='${contextPath}/UserStatusAction.do?method=page&id=${id}';">
            <I18n:message key="CANCEL"/>
            </html:button>
                </c:when>
                <c:otherwise>
            <html:button styleClass="iconized secondary" property="cancelButton"
            onclick="document.location='${contextPath}/UserStatusViewAction.do?method=page&id=${id}&prstatusId=${prstatusId}';">
            <I18n:message key="CANCEL"/>
            </html:button>
                </c:otherwise>
            </c:choose>
            </div>
</html:form>
    </div>
    </div>
</c:if>
</tiles:put>
</tiles:insert>
