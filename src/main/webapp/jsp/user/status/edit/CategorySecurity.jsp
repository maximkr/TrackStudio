<%@ page buffer="128kb" errorPage="/jsp/Error.jsp" %>
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

<div class="nblueborder">
<div class="ncaption"></div>
<div class="indent">
<html:form method="POST" action="/UserCategorySecurityAction" styleId="checkunload" onsubmit="return allow(this);">
<html:hidden property="method" value="save" styleId="ruleId"/>
<html:hidden property="id" value="${id}"/>
<html:hidden property="session" value="${session}"/>
<html:hidden property="prstatusId" value="${prstatusId}"/>
<c:set var="urlHtml" value="html"/>
<ts:js request="${request}" response="${response}">
    <ts:jsLink link="${urlHtml}/filtersort.js"/>
</ts:js>
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
                        <c:forEach items="${categoryViewNone}" var="s">
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
                    <html:select property="canview" multiple="true" size="10" styleClass="monospaced fixedwidth">
                        <c:forEach items="${categoryViewAll}" var="s">
                            <option value="${s.id}">
                                <c:out value="${s.name}"/>
                            </option>
                        </c:forEach>
                         <c:forEach items="${categoryViewSubmitter}" var="s">
        <option value="<c:out value="${s.id}"/> (* <I18n:message key="SUBMITTER"/>)"><c:out value="${s.name}"/> (* <I18n:message key="SUBMITTER"/>)</option>
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
        <input type="hidden" name="hiddencanview" value="<c:forEach items="${categoryViewAll}" var="s">
        <c:out value="${s.id}"/>;
        </c:forEach>
        <c:forEach items="${categoryViewSubmitter}" var="s">
        <c:out value="${s.id}"/> (* <I18n:message key="SUBMITTER"/>);
        </c:forEach>
        ">
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
                        <c:forEach items="${categoryCreateNone}" var="s">
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
                    <html:select property="cancreate" multiple="true" size="10" styleClass="monospaced fixedwidth">
                                 
                        <c:forEach items="${categoryCreateAll}" var="s">
        <option value="<c:out value="${s.id}"/>"><c:out value="${s.name}"/></option>
        </c:forEach>
        <c:forEach items="${categoryCreateSubmitter}" var="s">
        <option value="<c:out value="${s.id}"/> (* <I18n:message key="SUBMITTER"/>)"><c:out value="${s.name}"/> (* <I18n:message key="SUBMITTER"/>)</option>
        </c:forEach>
        <c:forEach items="${categoryCreateHandler}" var="s">
        <option value="<c:out value="${s.id}"/> (* <I18n:message key="HANDLER"/>)"><c:out value="${s.name}"/> (* <I18n:message key="HANDLER"/>)</option>
        </c:forEach>
        <c:forEach items="${categoryCreateSAH}" var="s">
        <option value="<c:out value="${s.id}"/> (* <I18n:message key="SUBMITTER"/>, <I18n:message key="HANDLER"/>)"><c:out value="${s.name}"/> (* <I18n:message key="SUBMITTER"/>, <I18n:message key="HANDLER"/>)</option>
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
        <input type="hidden" name="hiddencancreate" value="<c:forEach items="${categoryCreateAll}" var="s">
        <c:out value="${s.id}"/>;
        </c:forEach>
        <c:forEach items="${categoryCreateSubmitter}" var="s">
        <c:out value="${s.id}"/> (* <I18n:message key="SUBMITTER"/>);
        </c:forEach>
        <c:forEach items="${categoryCreateHandler}" var="s">
        <c:out value="${s.id}"/> (* <I18n:message key="HANDLER"/>);
        </c:forEach>
        <c:forEach items="${categoryCreateSAH}" var="s">
        <c:out value="${s.id}"/> (* <I18n:message key="SUBMITTER"/>,<I18n:message key="HANDLER"/>);
        </c:forEach>">
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
                        <c:forEach items="${categoryEditNone}" var="s">
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
                        <c:forEach items="${categoryEditAll}" var="s">
        <option value="<c:out value="${s.id}"/>"><c:out value="${s.name}"/></option>
        </c:forEach>
        <c:forEach items="${categoryEditSubmitter}" var="s">
        <option value="<c:out value="${s.id}"/> (* <I18n:message key="SUBMITTER"/>)"><c:out value="${s.name}"/> (* <I18n:message key="SUBMITTER"/>)</option>
        </c:forEach>
        <c:forEach items="${categoryEditHandler}" var="s">
        <option value="<c:out value="${s.id}"/> (* <I18n:message key="HANDLER"/>)"><c:out value="${s.name}"/> (* <I18n:message key="HANDLER"/>)</option>
        </c:forEach>
        <c:forEach items="${categoryEditSAH}" var="s">
        <option value="<c:out value="${s.id}"/> (* <I18n:message key="SUBMITTER"/>, <I18n:message key="HANDLER"/>)"><c:out value="${s.name}"/> (* <I18n:message key="SUBMITTER"/>, <I18n:message key="HANDLER"/>)</option>
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
        <input type="hidden" name="hiddencanedit" value="<c:forEach items="${categoryEditAll}" var="s">
        <c:out value="${s.id}"/>;
        </c:forEach>
        <c:forEach items="${categoryEditSubmitter}" var="s">
        <c:out value="${s.id}"/> (* <I18n:message key="SUBMITTER"/>);
        </c:forEach>
        <c:forEach items="${categoryEditHandler}" var="s">
        <c:out value="${s.id}"/> (* <I18n:message key="HANDLER"/>);
        </c:forEach>
        <c:forEach items="${categoryEditSAH}" var="s">
        <c:out value="${s.id}"/> (* <I18n:message key="SUBMITTER"/>,<I18n:message key="HANDLER"/>);
        </c:forEach>">
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
                         <c:forEach items="${categoryDeleteNone}" var="s">
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
                    <html:select property="candelete" multiple="true" size="10" styleClass="monospaced fixedwidth"
                                 >
                        <c:forEach items="${categoryDeleteAll}" var="s">
        <option value="<c:out value="${s.id}"/>"><c:out value="${s.name}"/></option>
        </c:forEach>
        <c:forEach items="${categoryDeleteSubmitter}" var="s">
        <option value="<c:out value="${s.id}"/> (* <I18n:message key="SUBMITTER"/>)"><c:out value="${s.name}"/> (* <I18n:message key="SUBMITTER"/>)</option>
        </c:forEach>
        <c:forEach items="${categoryDeleteHandler}" var="s">
        <option value="<c:out value="${s.id}"/> (* <I18n:message key="HANDLER"/>)"><c:out value="${s.name}"/> (* <I18n:message key="HANDLER"/>)</option>
        </c:forEach>
        <c:forEach items="${categoryDeleteSAH}" var="s">
        <option value="<c:out value="${s.id}"/> (* <I18n:message key="SUBMITTER"/>, <I18n:message key="HANDLER"/>)"><c:out value="${s.name}"/> (* <I18n:message key="SUBMITTER"/>, <I18n:message key="HANDLER"/>)</option>
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
        <input type="hidden" name="hiddencandelete" value="<c:forEach items="${categoryDeleteAll}" var="s">
        <c:out value="${s.id}"/>;
        </c:forEach>
        <c:forEach items="${categoryDeleteSubmitter}" var="s">
        <c:out value="${s.id}"/> (* <I18n:message key="SUBMITTER"/>);
        </c:forEach>
        <c:forEach items="${categoryDeleteHandler}" var="s">
        <c:out value="${s.id}"/> (* <I18n:message key="HANDLER"/>);
        </c:forEach>
        <c:forEach items="${categoryDeleteSAH}" var="s">
        <c:out value="${s.id}"/> (* <I18n:message key="SUBMITTER"/>,<I18n:message key="HANDLER"/>);
        </c:forEach>">
    </div>
</div>

<br>

<div class="blueborder">
    <div class="caption"><I18n:message key="RULE_BE_HANDLER_ADD"/></div>
    <div class="indent">
        <table class="allowdeny">
            <tr>
                <th class="denied">
                    <I18n:message key="CAN_NOT_BE_HANDLER"/>
                </th>
                <th></th>
                <th class="allowed">
                    <I18n:message key="CAN_BE_HANDLER"/>
                </th>
            </tr>
            <tr>
                <td>
                    <html:select property="cannothandler" multiple="true" size="10" styleClass="monospaced fixedwidth">
                         <c:forEach items="${categoryBeHandlerNone}" var="s">
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
                        <c:forEach items="${categoryBeHandlerAll}" var="s">
        <option value="<c:out value="${s.id}"/>"><c:out value="${s.name}"/></option>
        </c:forEach>
        <c:forEach items="${categoryBeHandlerSubmitter}" var="s">
        <option value="<c:out value="${s.id}"/> (* <I18n:message key="SUBMITTER"/>)"><c:out value="${s.name}"/> (* <I18n:message key="SUBMITTER"/>)</option>
        </c:forEach>
        <c:forEach items="${categoryCreateHandler}" var="s">
        <option value="<c:out value="${s.id}"/> (* <I18n:message key="HANDLER"/>)"><c:out value="${s.name}"/> (* <I18n:message key="HANDLER"/>)</option>
        </c:forEach>
        <c:forEach items="${categoryBeHandlerHandler}" var="s">
            <option value="<c:out value="${s.id}"/> (* <I18n:message key="HANDLER"/>)"><c:out value="${s.name}"/> (* <I18n:message key="HANDLER"/>)</option>
        </c:forEach>
        <c:forEach items="${categoryCreateSAH}" var="s">
        <option value="<c:out value="${s.id}"/> (* <I18n:message key="SUBMITTER"/>, <I18n:message key="HANDLER"/>)"><c:out value="${s.name}"/> (* <I18n:message key="SUBMITTER"/>, <I18n:message key="HANDLER"/>)</option>
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
        <input type="hidden" name="hiddencanhandler" value="<c:forEach items="${categoryBeHandlerAll}" var="s">
        <c:out value="${s.id}"/>;
        </c:forEach>
        <c:forEach items="${categoryBeHandlerSubmitter}" var="s">
        <c:out value="${s.id}"/> (* <I18n:message key="SUBMITTER"/>);
        </c:forEach>
        <c:forEach items="${categoryBeHandlerHandler}" var="s">
        <c:out value="${s.id}"/> (* <I18n:message key="HANDLER"/>);
        </c:forEach>
        <c:forEach items="${categoryBeHandlerSAH}" var="s">
        <c:out value="${s.id}"/> (* <I18n:message key="SUBMITTER"/>,<I18n:message key="HANDLER"/>);
        </c:forEach>">
    </div>
</div>

<div class="controls">
    <input type="submit" class="iconized"
           value="<I18n:message key="SAVE"/>"
           name="SAVE">
</div>
</html:form>
</div>
</div>
</tiles:put>
</tiles:insert>
