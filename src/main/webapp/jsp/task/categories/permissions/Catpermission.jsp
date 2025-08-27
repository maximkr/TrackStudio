<%@ page buffer="128kb" errorPage="/jsp/Error.jsp" %>
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
<tiles:put name="customHeader" value="/jsp/task/categories/CategoryHeader.jsp"/>
<tiles:put name="tabs" value="/jsp/task/categories/CategorySubMenu.jsp"/>
<tiles:put name="main" type="string">

<html:form action="/CategoryPermissionAction" method="post" styleId="checkunload" onsubmit="return validate(this);">
<html:hidden property="method" value="save" styleId="ruleId"/>
<html:hidden property="id" value="${id}"/>
<html:hidden property="session" value="${session}"/>
<html:hidden property="categoryId" value="${categoryId}"/>
<c:set var="urlHtml" value="html"/>
 <ts:js request="${request}" response="${response}">
    <ts:jsLink link="${urlHtml}/filtersort.js"/>
</ts:js>
<c:if test="${!isValid}">
    <div class="indent">
    <c:set var="counter" value="${0}"/>
    <div class="general">
        <table class="error" cellpadding="0" cellspacing="0">
            <caption>
                <I18n:message key="CATEGORY_INVALID_PERMISSION_OVERVIEW"/>
            </caption>
            <c:if test="${!isValidEdit}">
                <c:forEach var="prstatus" items="${invalideEditList}">
                    <tr class="line<c:out value="${counter mod 2}"/>">
                        <td>
                            <I18n:message key="CATEGORY_INVALID_EDIT_PERMISSION_CONFLICT">
                                <I18n:param value="${prstatus.name}"/>
                            </I18n:message>
                        </td>
                    </tr>
                    <c:set var="counter" value="${counter + 1}"/>
                </c:forEach>
            </c:if>
            <c:if test="${!isValidCreate}">
                <c:forEach var="prstatus" items="${invalideCreateList}">
                    <tr class="line<c:out value="${counter mod 2}"/>">
                        <td>
                            <I18n:message key="CATEGORY_INVALID_CREATE_PERMISSION_CONFLICT">
                                <I18n:param value="${prstatus.name}"/>
                            </I18n:message>
                        </td>
                    </tr>
                    <c:set var="counter" value="${counter + 1}"/>
                </c:forEach>
            </c:if>
            <c:if test="${!isValidDelete}">
                <c:forEach var="prstatus" items="${invalideDeleteList}">
                    <tr class="line<c:out value="${counter mod 2}"/>">
                        <td>
                            <I18n:message key="CATEGORY_INVALID_DELETE_PERMISSION_CONFLICT">
                                <I18n:param value="${prstatus.name}"/>
                            </I18n:message>
                        </td>
                    </tr>
                    <c:set var="counter" value="${counter + 1}"/>
                </c:forEach>
            </c:if>
            <c:if test="${!isValidBeHandler}">
                <c:forEach var="prstatus" items="${invalideBeHandlerList}">
                    <tr class="line<c:out value="${counter mod 2}"/>">
                        <td>
                            <I18n:message key="CATEGORY_INVALID_BE_HANDLER_PERMISSION_CONFLICT">
                                <I18n:param value="${prstatus.name}"/>
                            </I18n:message>
                        </td>
                    </tr>
                    <c:set var="counter" value="${counter + 1}"/>
                </c:forEach>
            </c:if>
        </table>
    </div>
</div>
</c:if>

<div class="nblueborder">
<div class="ncaption"></div>
<div class="indent">
<div class="blueborder">
                    <div class="caption"><I18n:message key="RULE_VIEW_ADD"/></div>
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
                        <c:forEach items="${cannotviewStatuses}" var="s">
                            <option value="${s.id}">
                                <c:out value="${s.name}"/>
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
                                 >
                        <c:forEach items="${canviewStatuses}" var="s">
                            <option value="${s.id}">
                                <c:out value="${s.name}"/>
                            </option>
                        </c:forEach>
                    </html:select>
                </td>
            </tr>

            <tr>
                <td colspan="2"></td>
                <td>
                    <input type="button" class="iconized" name="submitter" id="canviewsubmitter"
                           onclick="setOptions(this.form, 'cannotview', 'canview', '<I18n:message key="SUBMITTER"/>'); return false;"
                           value="<I18n:message key="SUBMITTER"/>"> <label for="canviewsubmitter">
                    <I18n:message key="TASK_SUBMITTER_ONLY"/>
                </label><br>
                    <input type="button" class="iconized" name="all" id="canviewall"
                           onclick="resetOptions(this.form, 'cannotview', 'canview'); return false;"
                           value="<I18n:message key="ALL"/>"> <label for="canviewall">
                    <I18n:message key="ALL"/>
                </label><br>
                </td>
            </tr>
        </table>
        <input type="hidden" name="hiddencanview" value="${categoryForm.hiddencanview}">
    </div>
</div>

<br>

<div class="blueborder">
<div class="caption"><I18n:message key="RULE_CREATE_ADD"/></div>
    <div class="indent">
                           <table class="allowdeny">
            <tr>
                <th class="denied">
                    <I18n:message key="CAN_NOT_CREATE"/>
                </th>
                <th></th>
                <th class="allowed">
                    <I18n:message key="CAN_CREATE"/>
                </th>
            </tr>
            <tr>
                <td>
                    <html:select property="cannotcreate" multiple="true" size="10" styleClass="monospaced fixedwidth">
                        <c:forEach items="${cannotcreateStatuses}" var="s">
                            <option value="${s.id}">
                                <c:out value="${s.name}"/>
                            </option>
                        </c:forEach>
                    </html:select>
                </td>
                <td>
                    <input type="button" class="iconized" name="add"
                           onclick="addSelectedItems(this.form, 'cannotcreate', 'cancreate'); return false;"
                           value="&gt;"><br>
                    <input type="button" class="iconized" name="remove"
                           onclick="removeSelectedItems(this.form, 'cannotcreate', 'cancreate'); return false;"
                           value="&lt;">
                </td>
                <td>
                    <html:select property="cancreate" multiple="true" size="10" styleClass="monospaced fixedwidth"
                                 >
                        <c:forEach items="${cancreateStatuses}" var="s">
                            <option value="${s.id}">
                                <c:out value="${s.name}"/>
                            </option>
                        </c:forEach>
                    </html:select>
                </td>
            </tr>

            <tr>
                <td colspan="2"></td>
                <td>
                    <input type="button" class="iconized" name="handler" id="cancreatehandler"
                           onclick="setOptions(this.form, 'cannotcreate', 'cancreate','<I18n:message key="HANDLER"/>'); return false;"
                           value="<I18n:message key="HANDLER"/>"> <label for=cancreatehandler>
                    <I18n:message key="PARENT_TASK_HANDLER_ONLY"/>
                </label><br>
                    <input type="button" class="iconized" name="submitter" id="cancreatesubmitter"
                           onclick="setOptions(this.form, 'cannotcreate', 'cancreate', '<I18n:message key="SUBMITTER"/>'); return false;"
                           value="<I18n:message key="SUBMITTER"/>"> <label for="cancreatesubmitter">
                    <I18n:message key="PARENT_TASK_SUBMITTER_ONLY"/>
                </label><br>
                    <input type="button" class="iconized" name="all" id="cancreateall"
                           onclick="resetOptions(this.form, 'cannotcreate', 'cancreate'); return false;"
                           value="<I18n:message key="ALL"/>"> <label for="cancreateall">
                    <I18n:message key="ALL"/>
                </label><br>
                </td>
            </tr>

        </table>
        <input type="hidden" name="hiddencancreate" value="${categoryForm.hiddencancreate}">
    </div>
</div>

<br>

<div class="blueborder">
<div class="caption"><I18n:message key="RULE_EDIT_ADD"/></div>
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
                        <c:forEach items="${cannoteditStatuses}" var="s">
                            <option value="${s.id}">
                                <c:out value="${s.name}"/>
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
                                 >
                        <c:forEach items="${caneditStatuses}" var="s">
                            <option value="${s.id}">
                                <c:out value="${s.name}"/>
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
                    <I18n:message key="TASK_HANDLER_ONLY"/>
                </label><br>
                    <input type="button" class="iconized" name="submitter" id="caneditsubmitter"
                           onclick="setOptions(this.form, 'cannotedit', 'canedit', '<I18n:message key="SUBMITTER"/>'); return false;"
                           value="<I18n:message key="SUBMITTER"/>"> <label for="caneditsubmitter">
                    <I18n:message key="TASK_SUBMITTER_ONLY"/>
                </label><br>
                    <input type="button" class="iconized" name="all" id="caneditall"
                           onclick="resetOptions(this.form, 'cannotedit', 'canedit'); return false;"
                           value="<I18n:message key="ALL"/>"> <label for="caneditall">
                    <I18n:message key="ALL"/>
                </label><br>
                </td>
            </tr>

        </table>
        <input type="hidden" name="hiddencanedit" value="${categoryForm.hiddencanedit}">
    </div>
</div>


<br>
<div class="blueborder">
<div class="caption"><I18n:message key="RULE_DELETE_ADD"/></div>
    <div class="indent">

        <table class="allowdeny">
            <tr>
                <th class="denied">
                    <I18n:message key="CAN_NOT_DELETE"/>
                </th>
                <th></th>
                <th class="allowed">
                    <I18n:message key="CAN_DELETE"/>
                </th>
            </tr>
            <tr>
                <td>
                    <html:select property="cannotdelete" multiple="true" size="10" styleClass="monospaced fixedwidth">
                        <c:forEach items="${cannotdeleteStatuses}" var="s">
                            <option value="${s.id}">
                                <c:out value="${s.name}"/>
                            </option>
                        </c:forEach>
                    </html:select>
                </td>
                <td>
                    <input type="button" class="iconized" name="add"
                           onclick="addSelectedItems(this.form, 'cannotdelete', 'candelete'); return false;"
                           value="&gt;"><br>
                    <input type="button" class="iconized" name="remove"
                           onclick="removeSelectedItems(this.form, 'cannotdelete', 'candelete'); return false;"
                           value="&lt;">
                </td>
                <td>
                    <html:select property="candelete" multiple="true" size="10" styleClass="monospaced fixedwidth">

                        <c:forEach items="${candeleteStatuses}" var="s">
                            <option value="${s.id}">
                                <c:out value="${s.name}"/>
                            </option>
                        </c:forEach>
                    </html:select>
                </td>
            </tr>

            <tr>
                <td colspan="2"></td>
                <td>
                    <input type="button" class="iconized" name="handler" id="candeletehandler"
                           onclick="setOptions(this.form, 'cannotdelete', 'candelete','<I18n:message key="HANDLER"/>'); return false;"
                           value="<I18n:message key="HANDLER"/>"> <label for=candeletehandler>
                    <I18n:message key="TASK_HANDLER_ONLY"/>
                </label><br>
                    <input type="button" class="iconized" name="submitter" id="candeletesubmitter"
                           onclick="setOptions(this.form, 'cannotdelete', 'candelete', '<I18n:message key="SUBMITTER"/>'); return false;"
                           value="<I18n:message key="SUBMITTER"/>"> <label for="candeletesubmitter">
                    <I18n:message key="TASK_SUBMITTER_ONLY"/>
                </label><br>
                    <input type="button" class="iconized" name="all" id="candeleteall"
                           onclick="resetOptions(this.form, 'cannotdelete', 'candelete'); return false;"
                           value="<I18n:message key="ALL"/>"> <label for="candeleteall">
                    <I18n:message key="ALL"/>
                </label><br>
                </td>
            </tr>

        </table>
        <input type="hidden" name="hiddencandelete" value="${categoryForm.hiddencandelete}">
    </div>
</div>

<br>

<div class="blueborder">
<div class="caption"><I18n:message key="RULE_BE_HANDLER_ADD"/></div>
    <div class="indent">

        <table class="allowdeny">
            <tr>
                <th class="denied">
                    <I18n:message key="CAN_NOT_BE_HANDLER_CREATE"/>
                </th>
                <th></th>
                <th class="allowed">
                    <I18n:message key="CAN_BE_HANDLER_CREATE"/>
                </th>
            </tr>
            <tr>
                <td>
                    <html:select property="cannothandler" multiple="true" size="10" styleClass="monospaced fixedwidth">
                        <c:forEach items="${cannothandlerStatuses}" var="s">
                            <option value="${s.id}">
                                <c:out value="${s.name}"/>
                            </option>
                        </c:forEach>
                    </html:select>
                </td>
                <td>
                    <input type="button" class="iconized" name="add"
                           onclick="addSelectedItems(this.form, 'cannothandler', 'canhandler'); return false;"
                           value="&gt;"><br>
                    <input type="button" class="iconized" name="remove"
                           onclick="removeSelectedItems(this.form, 'cannothandler', 'canhandler'); return false;"
                           value="&lt;">
                </td>
                <td>
                    <html:select property="canhandler" multiple="true" size="10" styleClass="monospaced fixedwidth"
                                 >
                        <c:forEach items="${canhandlerStatuses}" var="s">
                            <option value="${s.id}">
                                <c:out value="${s.name}"/>
                            </option>
                        </c:forEach>
                    </html:select>
                </td>
            </tr>

            <tr>
                <td colspan="2"></td>
                <td>
                    <input type="button" class="iconized" name="handler" id="canhandlerhandler"
                           onclick="setOptions(this.form, 'cannothandler', 'canhandler','<I18n:message key="HANDLER"/>'); return false;"
                           value="<I18n:message key="HANDLER"/>"> <label for=canhandlerhandler>
                    <I18n:message key="TASK_HANDLER_ONLY"/>
                </label><br>
                    <input type="button" class="iconized" name="submitter" id="canhandlersubmitter"
                           onclick="setOptions(this.form, 'cannothandler', 'canhandler', '<I18n:message key="SUBMITTER"/>'); return false;"
                           value="<I18n:message key="SUBMITTER"/>"> <label for="canhandlersubmitter">
                    <I18n:message key="TASK_SUBMITTER_ONLY"/>
                </label><br>
                    <input type="button" class="iconized" name="all" id="canhandlerall"
                           onclick="resetOptions(this.form, 'cannothandler', 'canhandler'); return false;"
                           value="<I18n:message key="ALL"/>"> <label for="canhandlerall">
                    <I18n:message key="ALL"/>
                </label><br>
                </td>
            </tr>

        </table>
        <input type="hidden" name="hiddencanhandler" value="${categoryForm.hiddencanhandler}">
    </div>
</div>
<c:if test="${canEdit}">
    <div class="controls">
        <input type="submit" class="iconized"
               value="<I18n:message key="SAVE"/>"
               name="SAVE">

    </div>
</c:if>
</div>
</div>
</html:form>

</tiles:put>
</tiles:insert>
