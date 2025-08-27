<%@ page buffer="128kb" errorPage="/jsp/Error.jsp"%>
<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/tiles-el" prefix="tiles" %>
<%@ taglib uri="http://trackstudio.com" prefix="ts" %>

<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value="${sc.timezone}"/>
<I18n:setBundle basename="language"/>
<tiles:insert page="/jsp/layout/ListLayout.jsp" flush="true">
    

    <tiles:put name="header" value="/jsp/task/TaskHeader.jsp"/>
    <tiles:put name="customHeader" value="/jsp/task/filter/TaskFilterHeader.jsp"/>
    <tiles:put name="tabs" type="string"/>

    <tiles:put name="main" type="string">
<div class="blueborder">
<div class="caption"><I18n:message key="FILTER_PROPERTIES_EDIT"/></div>
<div class="indent">
<c:set var="urlHtml" value="html"/>
    <ts:js request="${request}" response="${response}">
        <ts:jsLink link="${urlHtml}/filtersort.js"/>
    </ts:js>

<html:form method="post" styleId="checkunload" action="/TaskFilterEditAction" onsubmit="return validate(this); ">
<html:hidden property="method"/>
<html:hidden property="id" value="${id}"/>
<html:hidden property="filterId"/>
<html:hidden property="user"/>
<html:hidden property="task"/>
<html:hidden property="session" value="${session}"/>

    <table class="general" cellpadding="0" cellspacing="0">
    <caption><I18n:message key="FILTER_PROPERTIES"/></caption>
<COLGROUP>
<COL class="col_1">
<COL class="col_2">
</COLGROUP>
<tr>
<th>
<label for="tlc1">
<I18n:message key="NAME"/>*
</label>
</th>
<td><html:text styleId="tlc1" property="name" size="50" maxlength="200" alt=">0"/>
</td>
</tr>
<tr>
<th>
<label for="tlc2">
<I18n:message key="DESCRIPTION"/>
</label>
</th>
<td><html:text styleId="tlc2" property="description" size="50" maxlength="200"/>
</tr>
<tr>
<th>
<label for="tlc3"><span style="white-space: nowrap;"><I18n:message key="SHARE"/></span></label>
</th>
<td>
  <c:choose>
  <c:when test="${filterForm.shared eq true || !canCreatePublicFilter}">
  <html:checkbox property="shared" styleId="tlc3" styleClass="checkbox" disabled="true"/>
  <html:hidden property="shared"/>
  </c:when>
  <c:otherwise>
  <html:checkbox property="shared" styleId="tlc3" styleClass="checkbox"/>
  </c:otherwise>
</c:choose>

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
<c:if test="${currentFilter ne null}">
<tr>
        <th><I18n:message key="OWNER"/></th>
        <td>
            <span class="user" ${currentFilter.owner.id eq sc.userId ? "id='loggedUser'" : ""}>
            <html:img  styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/${currentFilter.owner.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
            <c:out value="${currentFilter.owner.name}" escapeXml="true"/>
			</span>
        </td>
</tr>
<tr>
</c:if>    
<th>
<label for="tlc4">
<I18n:message key="TASKS_ON_PAGE"/>
</label>
</th>
<td><html:text styleId="tlc4" property="onpage" size="3" maxlength="3" alt="natural"/>
</td>
</tr>
<tr>
<th>
<label for="tlc5"><span style="white-space: nowrap;"><I18n:message key="DEEP_SEARCH"/></span></label>
</th>
<td>
<html:checkbox property="subtask" styleId="tlc5" styleClass="checkbox"/>
</td>
</tr>
<tr>
<th>
<label for="searchtext">
 <I18n:message key="KEYWORD"/>
 </label>
</th>
<td>
<html:text styleId="searchtext"  maxlength="200" property="search" size="50" />
</td>
</tr>
<tr>
<th>
<label for="tlc6"><span style="white-space: nowrap;"><I18n:message key="DISPLAY_MSG"/></span></label>
</th>
<td>
<html:checkbox property="showmsg" styleId="tlc6" styleClass="checkbox"
               onclick="document.getElementById('msgnum').disabled=!this.checked;"/>
<html:select property="msgnum" styleId="msgnum" disabled="${disableMsgList}">
<html:option value="1"><I18n:message key="LAST_MESSAGE"/></html:option>
<html:option value="3"><I18n:message key="LAST_3_MESSAGES"/></html:option>
<html:option value="5"><I18n:message key="LAST_5_MESSAGES"/></html:option>
<html:option value="10"><I18n:message key="LAST_10_MESSAGES"/></html:option>
<html:option value="-1"><I18n:message key="ALL_HISTORY"/></html:option>
</html:select>
</td>
</tr>
</table>
<table class="general" cellpadding="0" cellspacing="0">
    <caption><I18n:message key="SORT_ORDER"/></caption>
<tr>
    <td>
        <table class="sortfilter">
        <tr>
        <td><I18n:message key="FIELDS"/><br>
        <html:select property="from" multiple="true" size="10"  styleClass="fieldsSelect">
            <c:forEach items="${fields}" var="field">
                <option value="<c:out value="${field.fieldKey}" escapeXml="false"/>"><c:out value="${field.display}" escapeXml="false"/></option>
            </c:forEach>
        </html:select>
        </td>
        <td>
        <input type="button" name="add" size="5" onclick="addSelected(this.form); return false;" value="&gt;"><br>
        <input type="button" name="remove" size="5" onclick="removeSelected(this.form); return false;" value="&lt;">
        </td>
        <td><I18n:message key="DISPLAY_FIELDS"/><br>
        <html:select property="to" multiple="true" size="10" styleClass="fieldsSelect" alt="filter">
            <c:forEach items="${selectedFields}" var="field">
                <option value="<c:out value="${field.fieldKey}" escapeXml="false"/>"><c:out value="${field.display}" escapeXml="false"/></option>
            </c:forEach>
        </html:select>
        </td>
        <td>
        <input type="button" name="asc" onclick="sortAsc(this.form); return false;" value="&#8593; <I18n:message key="ASC"/>"><br>
            <input type="button" name="desc" onclick="sortDesc(this.form); return false;" value="&#8595;  <I18n:message key="SORT_DESC"/>"><br>
            <input type="button"  name="unsort" onclick="sortNone(this.form); return false;" value=" <I18n:message key="UNSORTED"/>">
        </td>
        </tr>
        </table>
        <input type="hidden" name="fields" value="<c:out value="${filterForm.fields}" escapeXml="false"/>">
        <input type="hidden" name="counter" value="${counter}">

    </td>
</tr>
</table>

<div class="controls">
<input type="submit"  class="iconized"
                                value="<I18n:message key="SAVE"/>"
                name="SETFILTER">
<c:choose>
    <c:when test="${currentFilter ne null}">
        <html:button styleClass="iconized secondary" property="cancelButton"
        onclick="document.location='${contextPath}/TaskFilterViewAction.do?method=page&id=${id}&filterId=${currentFilter.id}';">
        <I18n:message key="CANCEL"/>
        </html:button>
    </c:when>
    <c:otherwise>
        <html:button styleClass="iconized secondary" property="cancelButton"
        onclick="document.location='${contextPath}/TaskFilterAction.do?method=page&id=${id}';">
        <I18n:message key="CANCEL"/>
        </html:button>
    </c:otherwise>
</c:choose>
</div>
</html:form>
    </div>
    </div>
</tiles:put>
</tiles:insert>
