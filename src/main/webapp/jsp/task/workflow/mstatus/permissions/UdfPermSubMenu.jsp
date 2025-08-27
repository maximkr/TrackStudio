<%@ page buffer="128kb" errorPage="/jsp/Error.jsp"%>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<I18n:setLocale value='${sc.locale}'/>
<I18n:setTimeZone value='${sc.timezone}'/>
<I18n:setBundle basename='language'/>

<table class="tabs">
	<tr>


<c:if test="${tabView.allowed}">
<td <c:if test="${tabView.selected}">class="selectedtab"</c:if>>
		<html:link styleClass="internal" href="${contextPath}/UdfPermissionViewAction.do?method=page&id=${id}&mstatusId=${mstatus.id}&workflowId=${flow.id}&udfId=${udfId}">

  <I18n:message key="MESSAGE_TYPE_CUSTOM_FIELD_OVERVIEW"/>

      </html:link>
</td>
</c:if>
<c:if test="${tabEdit.allowed}">
<td <c:if test="${tabEdit.selected}">class="selectedtab"</c:if>>
		<html:link styleClass="internal" href="${contextPath}/WorkflowUdfOperationPermissionAction.do?method=page&id=${id}&mstatusId=${mstatus.id}&workflowId=${flow.id}&udfId=${udfId}">

  <I18n:message key="CUSTOM_FIELDS_PERMISSIONS"/>

      </html:link>
</td>
</c:if>

<th>&nbsp;
</th>
</tr>
</table>
</div>


