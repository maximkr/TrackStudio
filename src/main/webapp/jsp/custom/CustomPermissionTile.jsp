<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/tiles-el" prefix="tiles" %>
<%@ taglib uri="http://trackstudio.com" prefix="ts" %>
<I18n:setLocale value='${sc.locale}'/>
<I18n:setTimeZone value='${sc.timezone}'/>
<I18n:setBundle basename='language'/>
<html:form action="${action}" method="post" styleId="checkunload" onsubmit="return validate(this);">
<html:hidden property="method" value="save"/>
<html:hidden property="id" value="${id}"/>
<html:hidden property="session" value="${session}"/>
<html:hidden property="udfId" value="${udfId}"/>
<html:hidden property="workflowId" value="${workflowId}"/>
    <c:set var="urlHtml" value="html"/>
    <ts:js request="${request}" response="${response}">
        <ts:jsLink link="${urlHtml}/filtersort.js"/>
    </ts:js>
<div class="blueborder">
<div class="caption"><I18n:message key="CAN_VIEW_UDF_PERMISSION"/></div>
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
                    <c:forEach items="${cannotviewStatuses}" var="s">
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
                    <c:forEach items="${canviewStatuses}" var="s">
                        <option value="${s.id}"><c:out value="${s.name}"/></option>
                    </c:forEach>
                </html:select>
                </td>
                </tr>
        <c:if test="${!isUser}"> 
        <tr>
        <td colspan="2"></td>
                <td>
                <input type="button" class="iconized" name="hanlder" id="canviewhandler" onclick="setOptions(this.form, 'cannotview', 'canview','<I18n:message key="HANDLER"/>'); return false;" value="<I18n:message key="HANDLER"/>"> <label for="canviewhandler"><I18n:message key="HANDLER_ONLY"/></label><br>
                <input type="button" class="iconized" name="submitter" id="canviewsubmitter" onclick="setOptions(this.form, 'cannotview', 'canview', '<I18n:message key="SUBMITTER"/>'); return false;" value="<I18n:message key="SUBMITTER"/>"> <label for="canviewsubmitter"><I18n:message key="SUBMITTER_ONLY"/></label><br>
                <input type="button" class="iconized" name="all" id="canviewall" onclick="resetOptions(this.form, 'cannotview', 'canview'); return false;" value="<I18n:message key="ALL"/>"> <label for="canviewall"><I18n:message key="ALL"/></label><br>
                </td>
                </tr>
        </c:if>
                </table>
                <input type="hidden" name="hiddencanview" value="${customForm.hiddencanview}">
</div>
    </div>
<br>
<div class="blueborder">
<div class="caption"><I18n:message key="CAN_EDIT_UDF_PERMISSION"/></div>
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
                    <c:forEach items="${cannoteditStatuses}" var="s">
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
                    <c:forEach items="${caneditStatuses}" var="s">
                        <option value="${s.id}"><c:out value="${s.name}"/></option>
                    </c:forEach>
                </html:select>
                </td>
                    </tr>
        <c:if test="${!isUser}">
        <tr>
        <td colspan="2"></td>
                <td>
                <input type="button" class="iconized" name="handler" id="canedithandler" onclick="setOptions(this.form, 'cannotedit', 'canedit','<I18n:message key="HANDLER"/>'); return false;" value="<I18n:message key="HANDLER"/>"> <label for=canedithandler><I18n:message key="HANDLER_ONLY"/></label><br>
                <input type="button" class="iconized" name="submitter" id="caneditsubmitter" onclick="setOptions(this.form, 'cannotedit', 'canedit', '<I18n:message key="SUBMITTER"/>'); return false;" value="<I18n:message key="SUBMITTER"/>"> <label for="caneditsubmitter"><I18n:message key="SUBMITTER_ONLY"/></label><br>
                <input type="button" class="iconized" name="all" id="caneditall" onclick="resetOptions(this.form, 'cannotedit', 'canedit'); return false;" value="<I18n:message key="ALL"/>"> <label for="caneditall"><I18n:message key="ALL"/></label><br>
                </td>
                </tr>
        </c:if>
                </table>
                <input type="hidden" name="hiddencanedit" value="${customForm.hiddencanedit}">
</div>
    </div>


                <div class="controls">
				                <c:if test="${canEdit}">
				<input type="submit"  class="iconized"
				value="<I18n:message key="SAVE"/>"
                name="SAVE">
				</c:if>
<html:button styleClass="iconized secondary" property="cancelButton"
        onclick="document.location='${contextPath}${cancelAction}?method=page&amp;id=${id}&amp;workflowId=${workflowId}&amp;udfId=${udfId}';">
        <I18n:message key="CANCEL"/>
        </html:button>
                </div>

</html:form>