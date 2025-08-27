<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<I18n:setLocale value='${sc.locale}'/>
<I18n:setTimeZone value='${sc.timezone}'/>
<I18n:setBundle basename='language'/>

<html:form method="post" styleId="checkunload" action="${editUdfAction}" onsubmit="return validate(this);">
<div class="general">
<html:hidden property="session" value="${session}"/>
<html:hidden property="method" value="save" styleId="udfListValuesId"/>
<html:hidden property="udfId" value="${udfId}"/>
<html:hidden property="id" value="${id}"/>
<html:hidden property="workflowId" value="${workflowId}"/>

</div>
<c:if test="${_can_modify eq true}">
    <div class="controls">
        <html:submit property="saveButton" styleClass="iconized"><I18n:message key="SAVE"/></html:submit>
        <html:submit property="deleteButton" styleClass="iconized secondary" onclick="set('delete');"><I18n:message key="DELETE"/></html:submit>
        <script type="text/javascript">
            function set(target) {document.getElementById('udfListValuesId').value=target;};
        </script>
    </div>
</c:if>
</html:form>