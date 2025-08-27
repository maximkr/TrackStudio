<%@ page buffer="128kb" errorPage="/jsp/Error.jsp" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/tiles-el" prefix="tiles" %>


<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value="${sc.timezone}"/>
<I18n:setBundle basename="language"/>
<tiles:insert page="/jsp/layout/ListLayout.jsp" flush="true">
<tiles:put name="header" value="/jsp/task/TaskHeader.jsp"/>
<tiles:put name="customHeader" type="string"/>
<tiles:put name="tabs" type="string"/>
<tiles:put name="main" type="string">
<c:if test="${canEdit}">
    <c:import url="/jsp/TinyMCE.jsp"/>
</c:if>
<c:set var="valid" value="false"/>
<div class="blueborder">
<html:form method="POST" action="/TaskEditAction" enctype="multipart/form-data"  styleId="checkunload"
           onsubmit="if (valid) {$(window).unbind('beforeunload'); showButton(true); return true;} else {return false}">
<div class="caption" style="text-align: right"><span class="fleft"><c:choose>
    <c:when test="${newTask eq true}">
        <c:choose>
            <c:when test="${newTaskCategory.action ne null && !empty newTaskCategory.action}">
                <c:out value="${newTaskCategory.action}"/>
            </c:when>
            <c:otherwise>
                <I18n:message key="ADD"/>
                <c:out value="${newTaskCategory.name}"/>
            </c:otherwise>
        </c:choose>
    </c:when>
    <c:otherwise>
        <I18n:message key="EDIT"/>
    </c:otherwise>
</c:choose></span> <span class="fright"><input type="submit" id="saveup" class="iconized"
                                               value="<I18n:message key="SAVE" />"
                                               onClick="set('saveTask');"
                                               name="SAVE" disabled="true"/>
        <c:if test="${(tci.parent ne null && tci.id ne '1') || newTask eq true}">
            <input type="submit" class="iconized secondary"
                   value="<I18n:message key="GO_PARENT"/>"
                   onClick="set('saveGoToParent');"
                   name="GOPARENT" id="goparentup" disabled="true"/>
        </c:if>
    <c:if test="${next ne null}">
        <input type="submit" class="iconized secondary"
               value="<I18n:message key="GO_NEXT"/>"
               title="<c:out value="${next.name}"/>"
               onClick="set('saveGoToNext');"
               name="GONEXT" id="gonextup" disabled="true"/>
    </c:if>
        <script type="text/javascript">
            var dontValid = false;
        </script>
        <html:button styleClass="iconized secondary" property="cancelButton" disabled="true"
                     onclick="$(window).unbind('beforeunload'); showButton(true); document.location='${referer}';" styleId="cancelUp">
            <I18n:message key="CANCEL"/>
        </html:button></span></div>
<div class="indent">
<html:hidden property="method" value="saveTask" styleId="editTaskId"/>
<html:hidden property="id"/>
<html:hidden property="session"/>
<html:hidden property="parentForCancel"/>
<html:hidden property="workflowId"/>
<c:if test="${newTask eq true}">
    <html:hidden property="newTask" value="true"/>
    <html:hidden property="category"/>
</c:if>

<table class="general" cellpadding="0" cellspacing="0">
<caption>
    <c:out value="${tableTitle}"/>
</caption>
<colgroup>
    <col class="col_1">
    <col class="col_2">
</colgroup>
<tr>
    <th>
        <form:label path="name"><I18n:message key="NAME"/>*</form:label>
    </th>
    <td>
        <input type="text" name="name" value='${newTask ? "" : tci.name}' styleId="name" property="name" spellcheck="true" size="80" maxlength="200" alt=">0"/>
    </td>
</tr>
<tr>
    <th>
        <label for="alias"><I18n:message key="ALIAS"/></label>
    </th>
    <td>
        <c:choose>
            <c:when test="${canEditTaskAlias}">
                <html:text styleId="alias" property="shortname" size="14" maxlength='200'/>
            </c:when>
            <c:otherwise>
                <html:hidden property="shortname"/>
                <c:out value="${tci.shortname}" escapeXml="true"/>
            </c:otherwise>
        </c:choose>
    </td>
</tr>
<tr>
    <th>
        <I18n:message key="CATEGORY"/>
    </th>
    <td>
        <c:if test="${tci.categoryId ne null}">
            <html:img styleClass="icon" border="0"
                      src="${contextPath}${ImageServlet}/icons/categories/${newTask ? newTaskCategory.icon : tci.category.icon}"/>
            <c:out value="${newTask ? newTaskCategory.name : tci.category.name}" escapeXml="true"/>
        </c:if>
    </td>
</tr>
<tr>
    <th>
        <I18n:message key="TASK_STATE"/>
    </th>
    <td>
        <c:choose>
            <c:when test="${newTask}">
                <c:choose>
                    <c:when test="${isOne}">
                        <html:hidden property="status" value="${startStatus.id}"/>
                         <span class="nowrap">
                             <c:set var="currentState" value="${newTask ? startStatus : tci.status}"/>
                               <html:img styleClass="state" border="0" style="background-color: ${currentState.color}"
                                         src="${contextPath}${ImageServlet}${currentState.image}"/>
                            <c:out value="${currentState.name}" escapeXml="true"/>
                        </span>
                    </c:when>
                    <c:otherwise>
                        <html:select property="status" value="${startStatus.id}">
                            <c:forEach var="item" items="${starts}">
                                <html:option value="${item.id}">
                                    <c:out value="${item.name}" escapeXml="true"/>
                                </html:option>
                            </c:forEach>
                        </html:select>
                    </c:otherwise>
                </c:choose>
            </c:when>
            <c:otherwise>
                <c:if test="${tci.statusId ne null}">
                    <span class="nowrap">
                         <c:set var="currentState" value="${newTask ? startStatus : tci.status}"/>
                          <html:img styleClass="state" border="0" style="background-color: ${currentState.color}"
                                    src="${contextPath}${ImageServlet}${currentState.image}"/>
                          <c:out value="${currentState.name}" escapeXml="true"/>
                    </span>
                </c:if>
            </c:otherwise>
        </c:choose>
    </td>
</tr>
<c:if test="${canViewTaskPriority && (canEditTaskPriority && !(empty priorityList) || priorityName ne null) }">
    <tr>
        <th>
            <I18n:message key="PRIORITY"/>
            *
        </th>
        <td>
            <c:choose>
                <c:when test="${canEditTaskPriority}">
                    <c:if test="${!(empty priorityList)}">
                        <c:choose>
                            <c:when test="${onePriorityName ne ''}">
                                <c:out value="${onePriorityName}" escapeXml="true"/>
                            </c:when>
                            <c:otherwise>
                                <html:select property="priority">
                                    <html:options collection="priorityList" property="id" labelProperty="name"/>
                                </html:select>
                            </c:otherwise>
                        </c:choose>
                    </c:if>
                </c:when>
                <c:otherwise>
                    <c:if test="${tci.priorityId ne null}">
                        <c:out value="${tci.priority.name}" escapeXml="true"/>
                    </c:if>
                </c:otherwise>
            </c:choose>
        </td>
    </tr>
</c:if>
<tr>
    <th class="nowrap statetracking">
        <I18n:message key="SUBMITTER"/>
    </th>
    <td class="nowrap statetracking">
        <c:choose>
            <c:when test="${newTask eq true}">


            <span class="user" id="loggedUser">
                <html:img styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/arw.usr.a.gif"/>
                <c:out value="${sc.user.name}" escapeXml="true"/>
            </span>

            </c:when>
            <c:otherwise>
                <c:if test="${tci.submitter ne null}">
            <span class="user" ${tci.submitterId eq sc.userId ? "id='loggedUser'" : ""}>
                <html:img styleClass="icon" border="0"
                          src="${contextPath}${ImageServlet}/cssimages/${tci.submitter.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
                <c:out value="${tci.submitter.name}" escapeXml="true"/>
            </span>
                </c:if>
            </c:otherwise>
        </c:choose>
    </td>
</tr>
<tr id="handlerTr">
    <th>
        <label for="handler"><I18n:message key="HANDLER"/><c:if
                test="${newTaskCategory.handlerRequired}">*</c:if></label>
    </th>
    <td>
        <c:choose>
            <c:when test="${canEditTaskHandler}">
                <c:if test="${!newTaskCategory.handlerRequired}">
                    <div class="optgroup">
                        <I18n:message key="CURRENT_HANDLER"/>
                        <c:choose>
                            <c:when test="${tci.handlerUserId ne null}">
					            <span class="user" ${tci.handlerUserId eq sc.userId ? "id='loggedUser'" : ""}>
                                    <html:img styleClass="icon" border="0"
                                              src="${contextPath}${ImageServlet}/cssimages/${tci.handlerUser.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
                                    <c:out value="${tci.handlerUser.name}" escapeXml="true"/>
			                    </span>
                            </c:when>
                            <c:when test="${tci.handlerGroupId ne null}">
						    <span class="user">
                                <html:img styleClass="icon" border="0"
                                          src="${contextPath}${ImageServlet}/cssimages/ico.status.gif"/><c:out
                                    value="${tci.handlerGroup.name}" escapeXml="true"/>
						    </span>
                            </c:when>
                            <c:otherwise>
                                <I18n:message key="UNASSIGNED"/>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </c:if>
                <br>
                <input type="text" name="searchhandler" style="padding:0;margin-left:-1px;height:15px;"
                       class="form-autocomplete" size="30" onkeyup="__localsearch(this);"><br>

                <div class="selectbox" id="_handler">
                    <c:if test="${!newTaskCategory.handlerRequired}">
                        <label class="sel0" for="handler_nobody"><html:radio property="handler" value=""
                                                                             styleId="handler_nobody"/><I18n:message
                                key="NOBODY"/></label>
                    </c:if>
                    <c:if test="${!(empty handlerGroups)}">
                        <div id="roles" class="optgroup"><I18n:message key="PRSTATUSES"/>
                            <c:forEach items="${handlerGroups}" var="group" varStatus="c">
                                <label for="handlergroup_${group.id}" class="sel${c.index mod 2}">
                                    <span class="user">
                                        <html:radio style="vertical-align:top;"
                                                    alt="${newTaskCategory.handlerRequired ? '>0' : ''}"
                                                    property="handler" value="PR_${group.id}"
                                                    styleId="handlergroup_${group.id}"/>
                                        <c:out value="${group.name}" escapeXml="true"/>
						            </span>
                                </label>
                            </c:forEach>
                        </div>
                    </c:if>
                    <c:if test="${!(empty handlers)}">
                        <div id="userlist" class="optgroup"><I18n:message key="USERS_LIST"/>
                            <c:forEach items="${handlers}" var="huser" varStatus="c">
                                <label for="handler_${huser.id}" class="sel${c.index mod 2}">
                                    <html:radio style="vertical-align:top;"
                                                alt="${newTaskCategory.handlerRequired ? '>0' : ''}" property="handler"
                                                value="${huser.id}" styleId="handler_${huser.id}"/>
                                        <span class="user" ${huser.id eq sc.userId ? "id='loggedUser'" : ""}>
                                            <input type="hidden" value="${huser.login}"/>
                                            <c:out value="${huser.name}" escapeXml="true"/>&nbsp;
                                            [<c:out value="${handlerMap[huser.id]}" escapeXml="true"/>]
			                            </span>
                                </label>
                            </c:forEach>
                        </div>
                    </c:if>

                </div>
            </c:when>
            <c:otherwise>
                <c:choose>
                    <c:when test="${handler ne null}">
			            <span class="user" ${handler.id eq sc.userId ? "id='loggedUser'" : ""}>
                            <c:out value="${handler.name}" escapeXml="true"/>
			            </span>
                    </c:when>
                    <c:when test="${handlerGroup ne null}">
					    <span class="user">
                            <c:out value="${handlerGroup.name}" escapeXml="true"/>
				        </span>
                    </c:when>
                    <c:otherwise>
                        <I18n:message key="UNASSIGNED"/>
                    </c:otherwise>
                </c:choose>
            </c:otherwise>
        </c:choose>
    </td>
</tr>
<c:if test="${canViewTaskDeadline}">
    <tr>
        <th>
            <I18n:message key="DEADLINE"/>
        </th>
        <td>
            <c:choose>
                <c:when test="${canEditTaskDeadline}">
                    <html:text styleId="sel1" alt="date(${sc.user.dateFormatter.pattern2})" property="deadline"
                               size="18" maxlength="40"/>
                    &nbsp;
                    <html:img src="${contextPath}${ImageServlet}/cssimages/ico.calendar.gif" border="0"
                              altKey="SELECT_DATE"
                              styleClass="calendaricon"
                              onclick="return showCalendar('sel1', '${sc.user.dateFormatter.pattern2}', '24', true);"/>
                </c:when>
                <c:otherwise>
                    <html:hidden property="deadline"/>
                    <span style="white-space: nowrap;"><c:if test="${tci.deadline ne null}">
                        <I18n:formatDate value="${tci.deadline.time}" type="both" dateStyle="short" timeStyle="short"/>
                    </c:if></span>
                </c:otherwise>
            </c:choose>
        </td>
    </tr>
</c:if>
<c:if test="${canViewTaskBudget && (budgetY || budgetM || budgetW ||budgetD ||budgeth || budgetm || budgets)}">
    <tr>
        <th>
            <I18n:message key="BUDGET"/>
        </th>
        <td>
            <c:choose>
                <c:when test="${canEditTaskBudget}">
                    <div class="budget">
                        <c:if test="${budgetY}">
                            <c:choose>
                                <c:when test="${!budgetM && !budgetW && !budgetD && !budgeth && !budgetm && !budgets}">
                                    <html:text styleId="budgetYears" alt="float" property="budgetDoubleYears" size="6"
                                               maxlength="6"/>
                                </c:when>
                                <c:otherwise>
                                    <html:text styleId="budgetYears" alt="natural" property="budgetIntegerYears"
                                               size="6"
                                               maxlength="6"/>
                                </c:otherwise>
                            </c:choose>
                            <label for="budgetYears">
                                <I18n:message key="BUDGET_YEARS"/>
                            </label>
                        </c:if>
                        <c:if test="${budgetM}">
                            <c:choose>
                                <c:when test="${!budgetW && !budgetD && !budgeth && !budgetm && !budgets}">
                                    <html:text styleId="budgetMonths" alt="float" property="budgetDoubleMonths" size="6"
                                               maxlength="6"/>
                                </c:when>
                                <c:otherwise>
                                    <html:text styleId="budgetMonths" alt="natural" property="budgetIntegerMonths"
                                               size="6"
                                               maxlength="6"/>
                                </c:otherwise>
                            </c:choose>
                            <label for="budgetMonths">
                                <I18n:message key="BUDGET_MONTHS"/>
                            </label>
                        </c:if>
                        <c:if test="${budgetW}">
                            <c:choose>
                                <c:when test="${!budgetD && !budgeth && !budgetm && !budgets}">
                                    <html:text styleId="budgetWeeks" alt="float" property="budgetDoubleWeeks" size="6"
                                               maxlength="6"/>
                                </c:when>
                                <c:otherwise>
                                    <html:text styleId="budgetWeeks" alt="natural" property="budgetIntegerWeeks"
                                               size="6"
                                               maxlength="6"/>
                                </c:otherwise>
                            </c:choose>
                            <label for="budgetWeeks">
                                <I18n:message key="BUDGET_WEEKS"/>
                            </label>
                        </c:if>
                        <c:if test="${budgetD}">
                            <c:choose>
                                <c:when test="${!budgeth && !budgetm && !budgets}">
                                    <html:text styleId="budgetDays" alt="float" property="budgetDoubleDays" size="6"
                                               maxlength="12"/>
                                </c:when>
                                <c:otherwise>
                                    <html:text styleId="budgetDays" alt="natural" property="budgetIntegerDays" size="6"
                                               maxlength="12"/>
                                </c:otherwise>
                            </c:choose>
                            <label for="budgetDays">
                                <I18n:message key="BUDGET_DAYS"/>
                            </label>
                        </c:if>
                        <c:if test="${budgeth}">

                            <c:choose>
                                <c:when test="${!budgetm && !budgets}">
                                    <html:text styleId="budgetHours" alt="float" property="budgetDoubleHours" size="6"
                                               maxlength="12"/>
                                </c:when>
                                <c:otherwise>
                                    <html:text styleId="budgetHours" alt="natural" property="budgetIntegerHours"
                                               size="6"
                                               maxlength="12"/>
                                </c:otherwise>
                            </c:choose>
                            <label for="budgetHours">
                                <I18n:message key="BUDGET_HOURS"/>
                            </label>
                        </c:if>
                        <c:if test="${budgetm}">
                            <c:choose>
                                <c:when test="${!budgets}">
                                    <html:text styleId="budgetMinutes" alt="float" property="budgetDoubleMinutes"
                                               size="6"
                                               maxlength="12"/>
                                </c:when>
                                <c:otherwise>
                                    <html:text styleId="budgetMinutes" alt="natural" property="budgetIntegerMinutes"
                                               size="6"
                                               maxlength="12"/>
                                </c:otherwise>
                            </c:choose>
                            <label for="budgetMinutes">
                                <I18n:message key="BUDGET_MINUTES"/>
                            </label>
                        </c:if>
                        <c:if test="${budgets}">
                            <html:text styleId="budgetSeconds" alt="natural" property="budgetIntegerSeconds" size="6"
                                       maxlength="12"/>
                            <label for="budgetSeconds">
                                <I18n:message key="BUDGET_SECONDS"/>
                            </label>
                        </c:if>
                    </div>
                </c:when>
                <c:otherwise>
                    <c:out value="${tci.budgetAsString}" escapeXml="true"/>
                </c:otherwise>
            </c:choose>
        </td>
    </tr>
</c:if>
</table>
<c:if test="${!empty udfMap}">
    <table class="general" cellpadding="0" cellspacing="0">
        <caption>
            <I18n:message key="CUSTOM_FIELDS"/>
        </caption>
        <colgroup>
            <col class="col_1">
            <col class="col_2">
        </colgroup>
        <c:import url="/jsp/custom/UDFEditTemplateTile.jsp"/>
    </table>
</c:if>
<c:if test="${canViewTaskDescription}">

    <table class="general" cellpadding="0" cellspacing="0">
        <caption>
            <I18n:message key="DESCRIPTION"/>
        </caption>
        <tr>
            <td>
                <c:choose>
                    <c:when test="${canEditTaskDescription}">
                        <html:textarea rows="10" property="description" styleClass="mceEditor" cols="70"
                                       style="margin:0px; padding:0px; width: 100%;" styleId="description"></html:textarea>
                    </c:when>
                    <c:otherwise>
                        <html:hidden property="description"/>
                        <c:out value="${wikiDescription}" escapeXml='false'/>
                    </c:otherwise>
                </c:choose>
            </td>
        </tr>
    </table>
</c:if>
<c:if test="${newTask eq true && canCreateTaskAttachments}">
    <c:import url="/jsp/attachments/AttachmentCreateTile.jsp"/>
</c:if>
<c:if test="${canViewTaskAttachments && !empty attachments}">
    <div class="strut"><a class="internal" class="internal" name="attachments"></a></div>
    <c:import url="/jsp/task/viewtask/attachments/TaskAttachmentViewTile.jsp"/>
</c:if>
<c:if test="${canSubmit}">
    <div class="controls">
        <input type="button" class="iconized"
               value="<I18n:message key="SAVE"/>"
               onClick="set('saveTask');"
               name="SAVE" id="savedown" disabled="true"/>
        <c:if test="${(tci.parent ne null && tci.id ne '1') || newTask eq true}">
            <input type="button" class="iconized secondary"
                   value="<I18n:message key="GO_PARENT"/>"
                   onClick="set('saveGoToParent');"
                   name="GOPARENT" id="goparentdown" disabled="true"/>
        </c:if>
        <c:if test="${next ne null}">
            <input type="button" class="iconized secondary"
                   value="<I18n:message key="GO_NEXT"/>"
                   onClick="set('saveGoToNext');"
                   title="<c:out value="${next.name}"/>"
                   name="GONEXT" id="gonextdown" disabled="true"/>
        </c:if>
        <script type="text/javascript">
            var dontValid = false;
        </script>
        <html:button styleClass="iconized secondary" property="cancelButton" disabled="true"
                     onclick="$(window).unbind('beforeunload'); showButton(true); document.location='${referer}';" styleId="cancelDown">
            <I18n:message key="CANCEL"/>
        </html:button>
        <script type="text/javascript">
            function set(target) {
                valid = validate(document.forms["checkunload"]);
                if (valid) {
                    $(window).unbind('beforeunload');
                    showButton(true);
                    document.getElementById('editTaskId').value = target;
                    document.forms["checkunload"].submit();
                }
            }
        </script>
    </div>
</c:if>

</div>
</html:form>
</div>
</tiles:put>
</tiles:insert>
<script type="text/javascript">
    function visibilityButton(id, enable) {
        var el = document.getElementById(id);
        if (el) {
            el.disabled = enable;
        }
    }

    function showButton(result) {
        try {
            visibilityButton("savedown", result);
            visibilityButton("goparentdown", result);
            visibilityButton("gonextdown", result);
            visibilityButton("cancelDown", result);
            visibilityButton("saveup", result);
            visibilityButton("goparentup", result);
            visibilityButton("gonextup", result);
            visibilityButton("cancelUp", result);
        } catch (err) {
            showError("showButton", err);
        }
    }

    showButton(false);

    $(window).bind('beforeunload', function() {
        return true;
    });
</script>
