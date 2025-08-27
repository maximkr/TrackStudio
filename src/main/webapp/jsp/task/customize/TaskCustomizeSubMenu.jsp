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
		<html:link styleClass="internal" href="${contextPath}/TaskUdfViewAction.do?method=page&amp;id=${id}&amp;udfId=${udfId}">

 	<I18n:message key="CUSTOM_FIELD_OVERVIEW_TASK"/>

    </html:link>
</td>
</c:if>
<c:if test="${tabEdit.allowed}">
<td <c:if test="${tabEdit.selected}">class="selectedtab"</c:if>>
		<html:link styleClass="internal" href="${contextPath}/TaskUdfEditAction.do?method=page&amp;id=${id}&amp;udfId=${udfId}&amp;type=${type}">
 	<I18n:message key="CUSTOM_FIELD_PROPERTIES_TASK"/>

    </html:link>
</td>
</c:if>
<c:if test="${tabPermission.allowed}">
<td <c:if test="${tabPermission.selected}">class="selectedtab"</c:if>>
		<html:link styleClass="internal" href="${contextPath}/TaskUdfPermissionAction.do?method=page&amp;id=${id}&amp;udfId=${udfId}">

 	<I18n:message key="CUSTOM_FIELD_PERMISSIONS_TASK"/>

    </html:link>
</td>
</c:if>
<th>&nbsp;</th>
		</tr>
		</table>

