<%@ page buffer="128kb" errorPage="/jsp/Error.jsp"%>
<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/tiles-el" prefix="tiles" %>
<%@ taglib prefix="ts" uri="http://trackstudio.com" %>


<I18n:setLocale value='${sc.locale}'/>
<I18n:setTimeZone value='${sc.timezone}'/>
<I18n:setBundle basename='language'/>
<tiles:insert page="/jsp/layout/ListLayout.jsp" flush="true">
<tiles:put name="customHeader" value="/jsp/task/workflow/customize/WorkflowCustomizeHeader.jsp"/>
<tiles:put name="header" value="/jsp/task/TaskHeader.jsp"/>
<tiles:put name="tabs" type="string"/>
<tiles:put name="main" type="string">
    <div class="blueborder">
        <div class="caption"><c:out value="${tableTitle}"/></div>
        <div class="indent">
<html:form action="/WorkflowUdfOperationPermissionAction" method="post" styleId="checkunload" onsubmit="return validate(this);">
<html:hidden property="method" value="save"/>
<html:hidden property="id" value="${id}"/>
<html:hidden property="session" value="${session}"/>
<html:hidden property="udfId" value="${udfId}"/>
<html:hidden property="workflowId" value="${flow.id}"/>
<c:set var="urlHtml" value="html"/>
    <ts:js request="${request}" response="${response}">
        <ts:jsLink link="${urlHtml}/filtersort.js"/>
    </ts:js>

    <div class="blueborder">
    <div class="caption"><I18n:message key="CAN_VIEW_MSTATUS_UDF_PERMISSION"/></div>
        <div class="indent">
    <table class="allowdeny">
	          <tr>
                <th class="denied"><I18n:message key="CAN_NOT_VIEW"/></th>
                <th></th>
                <th class="allowed"><I18n:message key="CAN_VIEW"/></th>
                </tr>
                <tr>
                <td>
                <html:select property="cannotview" multiple="true" size="10"  styleClass="monospaced fixedwidth">
                    <c:forEach items="${notviewable}" var="s">
                        <option value="${s.id}"><c:out value="${s.name}"/></option>
                    </c:forEach>
                </html:select>
                </td>
                <td>
                <input type="button" name="add" class="iconized" onclick="addSelectedItems(this.form, 'cannotview', 'canview'); return false;" value="&gt;"><br>
                <input type="button" name="remove" class="iconized" onclick="removeSelectedItems(this.form, 'cannotview', 'canview'); return false;" value="&lt;">
                </td>
                <td>
                <html:select property="canview" multiple="true" size="10" styleClass="monospaced fixedwidth">
                    <c:forEach items="${viewable}" var="s">
                        <option value="${s.id}"><c:out value="${s.name}"/></option>
                    </c:forEach>
                </html:select>
                </td>
                </tr>
        
                </table>
                <input type="hidden" name="hiddencanview" value="${mstatusUdfPermForm.hiddencanview}">
</div>
    </div>
<br>
<div class="blueborder">
    <div class="caption"><I18n:message key="CAN_EDIT_MSTATUS_UDF_PERMISSION"/></div>
        <div class="indent">
            <table class="allowdeny">
	                    <tr>
                <th class="denied"><I18n:message key="CAN_NOT_EDIT"/></th>
                <th></th>
                <th class="allowed"><I18n:message key="CAN_EDIT"/></th>
                </tr>
                <tr>
                <td>
                <html:select property="cannotedit" multiple="true" size="10"  styleClass="monospaced fixedwidth">
                    <c:forEach items="${noteditable}" var="s">
                        <option value="${s.id}"><c:out value="${s.name}"/></option>
                    </c:forEach>
                </html:select>
                </td>
                <td>
                <input type="button" class="iconized" name="add" onclick="addSelectedItems(this.form, 'cannotedit', 'canedit'); return false;" value="&gt;"><br>
                <input type="button" class="iconized" name="remove" onclick="removeSelectedItems(this.form, 'cannotedit', 'canedit'); return false;" value="&lt;">
                </td>
                <td>
                <html:select property="canedit" multiple="true" size="10" styleClass="monospaced fixedwidth">
                    <c:forEach items="${editable}" var="s">
                        <option value="${s.id}"><c:out value="${s.name}"/></option>
                    </c:forEach>
                </html:select>
                </td>
                    </tr>
        
                </table>
                <input type="hidden" name="hiddencanedit" value="${mstatusUdfPermForm.hiddencanedit}">
</div>
    </div>


                <div class="controls">

				<input type="submit"  class="iconized"
				value="<I18n:message key="SAVE"/>"
                name="SAVE">
				
<html:button styleClass="iconized secondary" property="cancelButton"
        onclick="document.location='${contextPath}${cancelAction}?method=page&amp;id=${id}&amp;workflowId=${flow.id}&amp;udfId=${udfId}';">
        <I18n:message key="CANCEL"/>
        </html:button>
                </div>
</html:form>
      </div>
    </div>
</tiles:put>
</tiles:insert>
