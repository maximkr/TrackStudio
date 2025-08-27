<%@ page buffer="128kb" errorPage="/jsp/Error.jsp" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/tiles-el" prefix="tiles" %>
<%@ taglib prefix="ts" uri="http://trackstudio.com" %>


<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value="${sc.timezone}"/>
<I18n:setBundle basename="language"/>
<tiles:insert page="/jsp/layout/ListLayout.jsp" flush="true">
<tiles:put name="header" value="/jsp/task/TaskHeader.jsp"/>
<tiles:put name="customHeader" value="/jsp/task/workflow/mstatus/MstatusHeader.jsp"/>
<tiles:put name="tabs" value="/jsp/task/workflow/mstatus/MstatusSubMenu.jsp"/>
<tiles:put name="main" type="string">
<div class="nblueborder">
<div class="ncaption"></div>
<div class="indent">
<html:form method="POST" action="/MstatusPermissionAction" styleId="checkunload" onsubmit="return allow(this);">
<html:hidden property="method" value="save" styleId="ruleId"/>
<html:hidden property="id" value="${id}"/>
<html:hidden property="session" value="${session}"/>
<html:hidden property="mstatusId" value="${mstatusId}"/>
<html:hidden property="workflowId" value="${workflowId}"/>
<c:set var="urlHtml" value="html"/>
<ts:js request="${request}" response="${response}">
    <ts:jsLink link="${urlHtml}/filtersort.js"/>
</ts:js>

<c:if test="${!isValidPermissions}">
<div class="indent">
    <div class="general">
        <table class="error" cellpadding="0" cellspacing="0">
            <caption>
                <I18n:message key="WORKFLOW_INVALID_PERMISSION_OVERVIEW"/>
            </caption>
            <c:forEach var="prstatus" items="${canViewCanEditPrstatusList}" varStatus="counter">
                <c:forEach var="mstatus" items="${canViewCanEditMap[prstatus.id]}">
                    <tr class="line<c:out value="${counter.index mod 2}"/>">
                        <td>
                            <I18n:message key="WORKFLOW_INVALID_PERMISSION_CONFLICT">
                                <I18n:param value="${prstatus.name}"/>
                                <I18n:param value="${mstatus.name}"/>
                            </I18n:message>
                        </td>
                    </tr>
                </c:forEach>
            </c:forEach>
        </table>
    </div>
</div>    
</c:if>
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
                        <c:forEach items="${ruleViewNone}" var="s">
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
                        <c:forEach items="${ruleViewAll}" var="s">
        <option value="<c:out value="${s.id}"/>"><c:out value="${s.name}"/></option>
        </c:forEach>
        <c:forEach items="${ruleViewSubmitter}" var="s">
        <option value="<c:out value="${s.id}"/> (* <I18n:message key="SUBMITTER"/>)"><c:out value="${s.name}"/> (* <I18n:message key="SUBMITTER"/>)</option>
        </c:forEach>
        <c:forEach items="${ruleViewHandler}" var="s">
        <option value="<c:out value="${s.id}"/> (* <I18n:message key="HANDLER"/>)"><c:out value="${s.name}"/> (* <I18n:message key="HANDLER"/>)</option>
        </c:forEach>
        <c:forEach items="${ruleViewSAH}" var="s">
        <option value="<c:out value="${s.id}"/> (* <I18n:message key="SUBMITTER"/>, <I18n:message key="HANDLER"/>)"><c:out value="${s.name}"/> (* <I18n:message key="SUBMITTER"/>, <I18n:message key="HANDLER"/>)</option>
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
                    <I18n:message key="TASK_HANDLER_ONLY"/>
                </label><br>
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
        <input type="hidden" name="hiddencanview" value="
        <c:forEach items="${ruleViewAll}" var="s">
        <c:out value="${s.id}"/>;
        </c:forEach>
        <c:forEach items="${ruleViewSubmitter}" var="s">
        <c:out value="${s.id}"/> (* <I18n:message key="SUBMITTER"/>);
        </c:forEach>
        <c:forEach items="${ruleViewHandler}" var="s">
        <c:out value="${s.id}"/> (* <I18n:message key="HANDLER"/>);
        </c:forEach>
        <c:forEach items="${ruleViewSAH}" var="s">
        <c:out value="${s.id}"/> (* <I18n:message key="SUBMITTER"/>,<I18n:message key="HANDLER"/>);
        </c:forEach>
        ">
    </div>
</div>

<br>

<div class="blueborder">
<div class="caption"><I18n:message key="RULE_PROCESS_ADD"/></div>
    <div class="indent">
        <table class="allowdeny">
            <tr>
                <th class="denied">
                    <I18n:message key="CAN_NOT_PROCESS"/>
                </th>
                <th></th>
                <th class="allowed">
                    <I18n:message key="CAN_PROCESS"/>
                </th>
            </tr>
            <tr>
                <td>
                    <html:select property="cannotprocess" multiple="true" size="10" styleClass="monospaced fixedwidth">
                        <c:forEach items="${ruleProcessNone}" var="s">
                            <option value="${s.id}">
                                <c:out value="${s.name}"/>
                            </option>
                        </c:forEach>
                    </html:select>
                </td>
                <td>
                    <input type="button" class="iconized" name="add"
                           onclick="addSelectedItems(this.form, 'cannotprocess', 'canprocess'); return false;"
                           value="&gt;"><br>
                    <input type="button" class="iconized" name="remove"
                           onclick="removeSelectedItems(this.form, 'cannotprocess', 'canprocess'); return false;"
                           value="&lt;">
                </td>
                <td>
                    <html:select property="canprocess" multiple="true" size="10" styleClass="monospaced fixedwidth"
                                >
                        <c:forEach items="${ruleProcessAll}" var="s">
        <option value="<c:out value="${s.id}"/>"><c:out value="${s.name}"/></option>
        </c:forEach>
        <c:forEach items="${ruleProcessSubmitter}" var="s">
        <option value="<c:out value="${s.id}"/> (* <I18n:message key="SUBMITTER"/>)"><c:out value="${s.name}"/> (* <I18n:message key="SUBMITTER"/>)</option>
        </c:forEach>
        <c:forEach items="${ruleProcessHandler}" var="s">
        <option value="<c:out value="${s.id}"/> (* <I18n:message key="HANDLER"/>)"><c:out value="${s.name}"/> (* <I18n:message key="HANDLER"/>)</option>
        </c:forEach>
        <c:forEach items="${ruleProcessSAH}" var="s">
        <option value="<c:out value="${s.id}"/> (* <I18n:message key="SUBMITTER"/>, <I18n:message key="HANDLER"/>)"><c:out value="${s.name}"/> (* <I18n:message key="SUBMITTER"/>, <I18n:message key="HANDLER"/>)</option>
        </c:forEach>

                    </html:select>
                </td>
            </tr>

            <tr>
                <td colspan="2"></td>
                <td>
                    <input type="button" class="iconized" name="handler" id="canprocesshandler"
                           onclick="setOptions(this.form, 'cannotprocess', 'canprocess','<I18n:message key="HANDLER"/>'); return false;"
                           value="<I18n:message key="HANDLER"/>"> <label for=canprocesshandler>
                    <I18n:message key="TASK_HANDLER_ONLY"/>
                </label><br>
                    <input type="button" class="iconized" name="submitter" id="canprocesssubmitter"
                           onclick="setOptions(this.form, 'cannotprocess', 'canprocess', '<I18n:message key="SUBMITTER"/>'); return false;"
                           value="<I18n:message key="SUBMITTER"/>"> <label for="canprocesssubmitter">
                    <I18n:message key="TASK_SUBMITTER_ONLY"/>
                </label><br>
                    <input type="button" class="iconized" name="all" id="canprocessall"
                           onclick="resetOptions(this.form, 'cannotprocess', 'canprocess'); return false;"
                           value="<I18n:message key="ALL"/>"> <label for="canprocessall">
                    <I18n:message key="ALL"/>
                </label><br>
                </td>
            </tr>

        </table>
        <input type="hidden" name="hiddencanprocess" value="
                <c:forEach items="${ruleProcessAll}" var="s">
        <c:out value="${s.id}"/>;
        </c:forEach>
        <c:forEach items="${ruleProcessSubmitter}" var="s">
        <c:out value="${s.id}"/> (* <I18n:message key="SUBMITTER"/>);
        </c:forEach>
        <c:forEach items="${ruleProcessHandler}" var="s">
        <c:out value="${s.id}"/> (* <I18n:message key="HANDLER"/>);
        </c:forEach>
        <c:forEach items="${ruleProcessSAH}" var="s">
        <c:out value="${s.id}"/> (* <I18n:message key="SUBMITTER"/>,<I18n:message key="HANDLER"/>);
        </c:forEach>
        ">
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
                        <c:forEach items="${ruleBeHandlerNone}" var="s">
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
                        <c:forEach items="${ruleBeHandlerAll}" var="s">
        <option value="<c:out value="${s.id}"/>"><c:out value="${s.name}"/></option>
        </c:forEach>
        <c:forEach items="${ruleBeHandlerSubmitter}" var="s">
        <option value="<c:out value="${s.id}"/> (* <I18n:message key="SUBMITTER"/>)"><c:out value="${s.name}"/> (* <I18n:message key="SUBMITTER"/>)</option>
        </c:forEach>
        <c:forEach items="${ruleBeHandlerHandler}" var="s">
        <option value="<c:out value="${s.id}"/> (* <I18n:message key="HANDLER"/>)"><c:out value="${s.name}"/> (* <I18n:message key="HANDLER"/>)</option>
        </c:forEach>
        <c:forEach items="${ruleBeHandlerSAH}" var="s">
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
        <input type="hidden" name="hiddencanhandler" value="
                <c:forEach items="${ruleBeHandlerAll}" var="s">
        <c:out value="${s.id}"/>;
        </c:forEach>
        <c:forEach items="${ruleBeHandlerSubmitter}" var="s">
        <c:out value="${s.id}"/> (* <I18n:message key="SUBMITTER"/>);
        </c:forEach>
        <c:forEach items="${ruleBeHandlerHandler}" var="s">
        <c:out value="${s.id}"/> (* <I18n:message key="HANDLER"/>);
        </c:forEach>
        <c:forEach items="${ruleBeHandlerSAH}" var="s">
        <c:out value="${s.id}"/> (* <I18n:message key="SUBMITTER"/>,<I18n:message key="HANDLER"/>);
        </c:forEach>
        ">
    </div>
</div>
<c:if test="${canEdit}">
    <div class="controls">
        <input type="submit" class="iconized"
               value="<I18n:message key="SAVE"/>"
               name="SAVE">
    </div>
</c:if>
</html:form>
</div>
</div>
</tiles:put>
</tiles:insert>
