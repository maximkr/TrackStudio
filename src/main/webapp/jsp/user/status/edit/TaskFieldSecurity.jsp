<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/tiles-el" prefix="tiles" %>
<%@ taglib prefix="ts" uri="http://trackstudio.com" %>
<c:set var="taskMenu" value="false"/>
<c:set var="userMenu" value="true"/>
<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value="${sc.timezone}"/>
<I18n:setBundle basename="language"/>
<tiles:insert page="/jsp/layout/ListLayout.jsp" flush="true">
<tiles:put name="customHeader" value="/jsp/user/status/StatusHeader.jsp"/>
<tiles:put name="header" value="/jsp/user/UserHeader.jsp"/>
<tiles:put name="tabs" value="/jsp/user/status/StatusSubMenu.jsp"/>
<tiles:put name="main" type="string">
<c:if test="${canView}">
<div class="nblueborder">
<div class="ncaption"></div>
<div class="indent">
<html:form method="POST" action="/TaskFieldSecurityAction" styleId="checkunload" onsubmit="return validate(this);">
<html:hidden property="method" value="save"/>
<html:hidden property="session" value="${session}"/>
<html:hidden property="prstatusId" value="${prstatusId}"/>
<html:hidden property="id" value="${id}"/>
<table class="general" cellpadding="0" cellspacing="0">
<colgroup>
    <col width="40%">
    <col width="30%">
    <col width="30%">
</colgroup>
<tr class="wide">
    <th>
        <I18n:message key="TASK_PROPERTIES"/>
    </th>
    <th>
        <I18n:message key="CAN_VIEW"/>
    </th>
    <th>
        <I18n:message key="CAN_EDIT"/>
    </th>
</tr>
<tr>
    <th>
        <I18n:message key="ALIAS"/>
    </th>
    <td style="text-align: center;">

        <input name="dis" type="checkbox" checked disabled>

    </td>
    <td style="text-align: center;">

        <html:checkbox property="editTaskAlias" value="on" disabled="${!canEdit}"/>

    </td>
</tr>
<tr>
    <th>
        <I18n:message key="RESOLUTION"/>
    </th>
    <td style="text-align: center;">

        <html:checkbox property="viewTaskResolution" value="on" disabled="${!canEdit}"/>

    </td>
    <td style="text-align: center;">

        <input name="dis" type="checkbox" disabled>

    </td>
</tr>
<tr>
    <th>
        <I18n:message key="PRIORITY"/>
    </th>
    <td style="text-align: center;">

        <html:checkbox disabled="${!canEdit}" property="viewTaskPriority" value="on"
                       onchange="if (!this.checked) this.form.editTaskPriority.checked=false;"/>

    </td>
    <td style="text-align: center;">

        <html:checkbox disabled="${!canEdit}" property="editTaskPriority" value="on"
                       onchange="if (this.checked) this.form.viewTaskPriority.checked=true;"/>

    </td>
</tr>
<tr>
    <th>
        <I18n:message key="HANDLER"/>
    </th>

    <td style="text-align: center;">

        <input name="dis" type="checkbox" checked disabled>

    </td>
    <td style="text-align: center;">

        <html:checkbox property="editTaskHandler" value="on" disabled="${!canEdit}"/>

    </td>
</tr>
<tr>
    <th>
        <I18n:message key="SUBMIT_DATE"/>
    </th>
    <td style="text-align: center;">

        <html:checkbox property="viewTaskSubmitDate" value="on" disabled="${!canEdit}"/>

    </td>
    <td style="text-align: center;">

        <input name="dis" type="checkbox" disabled>

    </td>
</tr>
<tr>
    <th>
        <I18n:message key="UPDATE_DATE"/>
    </th>
    <td style="text-align: center;">

        <html:checkbox property="viewTaskLastUpdated" value="on"  disabled="${!canEdit}"/>

    </td>
    <td style="text-align: center;">

        <input name="dis" type="checkbox" disabled>

    </td>
</tr>

<tr>
    <th>
        <I18n:message key="CLOSE_DATE"/>
    </th>
    <td style="text-align: center;">

        <html:checkbox property="viewTaskCloseDate" value="on" disabled="${!canEdit}"/>

    </td>
    <td style="text-align: center;">

        <input name="dis" type="checkbox" disabled>

    </td>
</tr>

<tr>
    <th>
        <I18n:message key="DEADLINE"/>
    </th>
    <td style="text-align: center;">

        <html:checkbox property="viewTaskDeadline" value="on" disabled="${!canEdit}"
                       onchange="if (!this.checked) this.form.editTaskDeadline.checked=false;"/>
    </td>
    <td style="text-align: center;">

        <html:checkbox property="editTaskDeadline" value="on" disabled="${!canEdit}"
                       onchange="if (this.checked) this.form.viewTaskDeadline.checked=true;"/>
    </td>
</tr>
<tr>
    <th>
        <I18n:message key="BUDGET"/>
    </th>
    <td style="text-align: center;">
        <html:checkbox property="viewTaskBudget" value="on" disabled="${!canEdit}"
                       onchange="if (!this.checked) this.form.editTaskBudget.checked=false;"/>
    </td>
    <td style="text-align: center;">
        <html:checkbox property="editTaskBudget" value="on" disabled="${!canEdit}"
                       onchange="if (this.checked) this.form.viewTaskBudget.checked=true;"/>

    </td>
</tr>
<tr>
    <th>
        <I18n:message key="ABUDGET"/>
    </th>
    <td style="text-align: center;">

        <html:checkbox property="viewTaskActualBudget" value="on" disabled="${!canEdit}"
                       onchange="if (!this.checked) this.form.editTaskActualBudget.checked=false;"/>


    </td>
    <td style="text-align: center;">

        <html:checkbox property="editTaskActualBudget" value="on" disabled="${!canEdit}"
                       onchange="if (this.checked) this.form.viewTaskActualBudget.checked=true;"/>

    </td>
</tr>
<tr>
    <th>
        <I18n:message key="DESCRIPTION"/>
    </th>
    <td style="text-align: center;">

        <html:checkbox property="viewTaskDescription" value="on" disabled="${!canEdit}"
                       onchange="if (!this.checked) this.form.editTaskDescription.checked=false;"/>

    </td>
    <td style="text-align: center;">

        <html:checkbox property="editTaskDescription" value="on" disabled="${!canEdit}"
                       onchange="if (this.checked) this.form.viewTaskDescription.checked=true;"/>
    </td>
</tr>
</table>
<br>
 <c:set var="urlHtml" value="html"/>
 <ts:js request="${request}" response="${response}">
    <ts:jsLink link="${urlHtml}/filtersort.js"/>
</ts:js>
<c:if test="${canManageTaskUDFs}">
    <div class="blueborder">
        <div class="caption"><I18n:message key="USER_CAN_VIEW_UDF_PERMISSION"/></div>
        <div class="indent">
            <table class="allowdeny">
                <tr>
                    <th class="denied">
                        <I18n:message key="CAN_NOT_VIEW"/>
                    </th>
                    <th></th>
                    <th class="allowed">
                        <I18n:message key="CAN_VIEW"/>
                    </th>
                </tr>
                <tr>
                    <td>
                        <html:select property="cannotview" multiple="true" size="10" styleClass="monospaced fixedwidth">
                            <c:forEach items="${cannotviewUdfs}" var="s">
                                <option value="${s.id}">
                                    <c:out value="${s.caption}"/>
                                </option>
                            </c:forEach>
                        </html:select>
                    </td>
                    <td>
                        <input type="button" name="add" class="iconized"
                               onclick="addSelectedItems(this.form, 'cannotview', 'canview'); return false;"
                               value="&gt;"><br>
                        <input type="button" name="remove" class="iconized"
                               onclick="removeSelectedItems(this.form, 'cannotview', 'canview'); return false;"
                               value="&lt;">
                    </td>
                    <td>
                        <html:select property="canview" multiple="true" size="10" styleClass="monospaced fixedwidth"
                                     style="width: 400px">
                            <c:forEach items="${canviewUdfs}" var="s">
                                <option value="${s.id}">
                                    <c:out value="${s.caption}"/>
                                </option>
                            </c:forEach>
                        </html:select>
                    </td>
                </tr>

                <tr>
                    <td colspan="2"></td>
                    <td>
                        <input type="button" class="iconized" name="handler" id="canviewhandler"
                               onclick="setOptions(this.form, 'cannotview', 'canview','<I18n:message key="HANDLER"/>'); return false;"
                               value="<I18n:message key="HANDLER"/>"> <label for="canviewhandler">
                        <I18n:message key="HANDLER_ONLY"/>
                    </label><br>
                        <input type="button" class="iconized" name="submitter" id="canviewsubmitter"
                               onclick="setOptions(this.form, 'cannotview', 'canview', '<I18n:message key="SUBMITTER"/>'); return false;"
                               value="<I18n:message key="SUBMITTER"/>"> <label for="canviewsubmitter">
                        <I18n:message key="SUBMITTER_ONLY"/>
                    </label><br>
                        <input type="button" class="iconized" name="all" id="canviewall"
                               onclick="resetOptions(this.form, 'cannotview', 'canview'); return false;"
                               value="<I18n:message key="ALL"/>"> <label for="canviewall">
                        <I18n:message key="ALL"/>
                    </label><br>
                    </td>
                </tr>
            </table>
            <input type="hidden" name="hiddencanview" value="${prstatusForm.hiddencanview}">
        </div>
    </div>


    <br>

    <div class="blueborder">
        <div class="caption">
            <I18n:message key="USER_CAN_EDIT_UDF_PERMISSION"/>
        </div>
        <div class="indent">
            <table class="allowdeny">

                <tr>
                    <th class="denied">
                        <I18n:message key="CAN_NOT_EDIT"/>
                    </th>
                    <th></th>
                    <th class="allowed">
                        <I18n:message key="CAN_EDIT"/>
                    </th>
                </tr>
                <tr>
                    <td>
                        <html:select property="cannotedit" multiple="true" size="10" styleClass="monospaced fixedwidth">
                            <c:forEach items="${cannoteditUdfs}" var="s">
                                <option value="${s.id}">
                                    <c:out value="${s.caption}"/>
                                </option>
                            </c:forEach>
                        </html:select>
                    </td>
                    <td>
                        <input type="button" class="iconized" name="add"
                               onclick="addSelectedItems(this.form, 'cannotedit', 'canedit'); return false;"
                               value="&gt;"><br>
                        <input type="button" class="iconized" name="remove"
                               onclick="removeSelectedItems(this.form, 'cannotedit', 'canedit'); return false;"
                               value="&lt;">
                    </td>
                    <td>
                        <html:select property="canedit" multiple="true" size="10" styleClass="monospaced fixedwidth"
                                     style="width: 400px">
                            <c:forEach items="${caneditUdfs}" var="s">
                                <option value="${s.id}">
                                    <c:out value="${s.caption}"/>
                                </option>
                            </c:forEach>
                        </html:select>
                    </td>
                </tr>
                <tr>
                    <td colspan="2"></td>
                    <td>
                        <input type="button" class="iconized" name="handler" id="canedithandler"
                               onclick="setOptions(this.form, 'cannotedit', 'canedit','<I18n:message key="HANDLER"/>'); return false;"
                               value="<I18n:message key="HANDLER"/>"> <label for=canedithandler>
                        <I18n:message key="HANDLER_ONLY"/>
                    </label><br>
                        <input type="button" class="iconized" name="submitter" id="caneditsubmitter"
                               onclick="setOptions(this.form, 'cannotedit', 'canedit', '<I18n:message key="SUBMITTER"/>'); return false;"
                               value="<I18n:message key="SUBMITTER"/>"> <label for="caneditsubmitter">
                        <I18n:message key="SUBMITTER_ONLY"/>
                    </label><br>
                        <input type="button" class="iconized" name="all" id="caneditall"
                               onclick="resetOptions(this.form, 'cannotedit', 'canedit'); return false;"
                               value="<I18n:message key="ALL"/>"> <label for="caneditall">
                        <I18n:message key="ALL"/>
                    </label><br>
                    </td>
                </tr>
            </table>
            <input type="hidden" name="hiddencanedit" value="${prstatusForm.hiddencanedit}">
        </div>
    </div>
</c:if>
<c:if test="${canEdit}">
    <div class="controls">
        <input type="SUBMIT" class="iconized" value="<I18n:message key="SAVE"/>" name="SAVE">
    </div>
</c:if>
</html:form>
</div>
</div>
</c:if>
</tiles:put>
</tiles:insert>
