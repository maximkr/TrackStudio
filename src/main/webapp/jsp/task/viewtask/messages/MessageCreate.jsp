<%@ page buffer="128kb" errorPage="/jsp/Error.jsp" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/tiles-el" prefix="tiles" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://trackstudio.com" prefix="ts" %>
<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value='${sc.timezone}'/>
<I18n:setBundle basename="language"/>
<tiles:insert page="/jsp/layout/ListLayout.jsp" flush="true">
<tiles:put name="header" value="/jsp/task/TaskHeader.jsp"/>
<tiles:put name="customHeader" type="string"/>
<tiles:put name="tabs" type="string"/>
<tiles:put name="main" type="string">
<c:import url="/jsp/TinyMCE.jsp"/>
<div id="servicePanel" class="${selectedIds!=null && !empty selectedIds ? "norm" : "closed"}">
        <span>
        <img id="windowhideicon" src="${contextPath}${ImageServlet}/cssimages/ico.hidewin.gif" class="icon"
             onclick="hideServicePanel();" title="<I18n:message key="HIDE"/>">
        <img id="windowopenicon" src="${contextPath}${ImageServlet}/cssimages/ico.openwin.gif" class="icon"
             onclick="hideServicePanel();" title="<I18n:message key="OPEN"/>">
        <img id="windowcloseicon" src="${contextPath}${ImageServlet}/cssimages/ico.closewin.gif" class="icon"
             onclick="closeServicePanel();" title="<I18n:message key="CLOSE"/>">
            </span>
    <c:forEach items="${selectedIds}" var="st">
        <label id="_spi_<c:out value="${st.id}"/>"
               style="padding-top: 1px; padding-bottom: 1px; font-family: Verdana; font-size: 11px; font-weight: bold;"
               title="[#${st.number}] <c:out value="${st.name}"/>"
               onclick="placeOnServicePanel('<c:out value="${st.id}"/>','#<c:out value="${st.number}"/>');"
               for="#<c:out value="${st.number}"/>">#<c:out value="${st.number}"/> </label>
    </c:forEach>

</div>
<script type="text/javascript">
    function openDescription(Sender) {
        Sender.parentNode.className = "taskDescription descopened";
    }

    function closeDescription(Sender) {
        Sender.parentNode.className = "taskDescription descclosed";
    }

    function setParamSubmit(value) {
        document.getElementById("paramsubmit").value = value;
    }
</script>
<c:if test="${!empty tci.description && fn:length(tci.description)>0}">
    <div class="taskDescription descclosed">
        <label class="labelclose" onclick="closeDescription(this);"><img
                src="${contextPath}${ImageServlet}/cssimages/compact.png" class="icon"
                title="<I18n:message key="LESS"/>"><I18n:message key="LESS"/></label>

        <div class="content" id="taskDescription">
            <div class="indent">
                <ts:htmlfilter session="${sc.id}" macros="true" audit="false" request="<%=request%>"><c:out
                        value="${tci.description}" escapeXml="false"/></ts:htmlfilter>
            </div>
        </div>
        <label class="labelopen" onclick="openDescription(this);"><img
                src="${contextPath}${ImageServlet}/cssimages/scrollToObject.png" class="icon"
                title="<I18n:message key="MORE"/>"><I18n:message key="MORE"/></label>
    </div>
    <script language="JavaScript">
        var para = document.getElementById("taskDescription");
        if (para.scrollHeight <= para.clientHeight) {
            para.parentNode.className = "taskDescription";
        }
    </script>
</c:if>
<div class="blueborder">
<html:form method="post" enctype="multipart/form-data" focus="bugnote" action="/MessageCreateAction"
           onsubmit="if (validate(this)) {showButton(true); return true;} else {return false}">
<input type="hidden" id="paramsubmit" name="paramsubmit">

<div class="caption" style="text-align: right"><span class="fleft"><c:out value="${mstatus.name}"/></span> <span
        class="fright">
    <input type="submit" class="iconized" value="<I18n:message key="SAVE" />" name="ADDMESSAGE" disabled="true"
           onclick="setParamSubmit(this.name);"/>
<c:if test="${taskId ne '1'}">
    <input type="submit" class="iconized secondary" value="<I18n:message key="GO_PARENT"/>" name="GOPARENT"
           disabled="true" onclick="setParamSubmit(this.name);"/>
</c:if>
    <c:if test="${next ne null}">
        <input type="submit" class="iconized secondary" value="<I18n:message key="GO_NEXT"/>"
               title="<c:out value="${next.name}"/>" name="GONEXT" disabled="true"
               onclick="setParamSubmit(this.name);"/>
    </c:if>
                    <html:button styleClass="iconized secondary" property="cancelButton" disabled="true"
                                 onclick="showButton(true); document.location='${referer}';">
                        <I18n:message key="CANCEL"/>
                    </html:button></span></div>
<div class="indent">


<html:hidden property="method" value="save" styleId="messageId"/>
<html:hidden property="id"/>
<html:hidden property="session"/>
<html:hidden property="mstatus"/>
<html:hidden property="returnToTask"/>
<html:textarea styleClass="mceEditor" style="margin:0px; padding:0px; width:100%;" rows="10" styleId="bugnote"
               property="bugnote" cols="70"></html:textarea>
<br>
<table class="general" cellpadding="0" cellspacing="0">
<caption><I18n:message key="TASK_PROPERTIES_EDIT"/></caption>
<COLGROUP>
    <COL width="25%">
    <COL width="75%">
</COLGROUP>
<c:if test="${canEditHandler && (!empty handlers || !empty handlerGroups || !empty participantsInSortOrder)}">
    <tr>
        <th><label for="handler">
            <I18n:message key="MESSAGE_HANDLER"/>
        </label>
        </th>
        <td>
            <div class="optgroup"><I18n:message key="CURRENT_HANDLER"/>

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
            <br>
            <input type="text" name="searchhandler" style="padding:0;margin-left:-1px;height:15px;"
                   class="form-autocomplete" size="30" onkeyup="__localsearch(this);">

            <div class="selectbox" id="_handler">
                <c:if test="${canShowNobody}">
                    <label class="sel0" for="handler_nobody"><html:radio property="handler" value=""
                                                                         styleId="handler_nobody"/><I18n:message
                            key="NOBODY"/></label>
                </c:if>
                <div id="participants" class="optgroup"><I18n:message key="PARTICIPANTS"/>
                    <c:if test="${!(empty participantsInSortOrder)}">
                        <c:forEach items="${participantsInSortOrder}" var="participant" varStatus="c">
                            <label style="height:100%;" for="handler_${participant.id}" class="sel${c.index mod 2}">
                                <html:radio style="vertical-align:top;" property="handler"
                                            alt="${canShowNobody ? '' : '>0'}" value="${participant.id}"
                                            styleId="handler_${participant.id}"/>
                                                                    <span class="user" ${participant.id eq sc.userId ? "id='loggedUser'" : ""}><c:choose>
                                                                        <c:when test="${participant.id eq tci.submitterId && participant.id eq tci.handlerUserId}">
                                                                            <html:img border="0"
                                                                                      src="${contextPath}${ImageServlet}/cssimages/ico.submitter.png"/>
                                                                            <html:img border="0"
                                                                                      src="${contextPath}${ImageServlet}/cssimages/ico.handler.png"/>
                                                                        </c:when>
                                                                        <c:when test="${participant.id eq tci.submitterId}">
                                                                            <html:img border="0"
                                                                                      src="${contextPath}${ImageServlet}/cssimages/ico.submitter.png"/>
                                                                        </c:when>
                                                                        <c:when test="${participant.id eq tci.handlerUserId}">
                                                                            <html:img border="0"
                                                                                      src="${contextPath}${ImageServlet}/cssimages/ico.handler.png"/>
                                                                        </c:when>
                                                                    </c:choose>
                                                                        <c:out value="${participant.name}"
                                                                               escapeXml="true"/>&nbsp;
                                                                        [<c:out value="${handlerMap[participant.id]}"
                                                                                escapeXml="true"/>]
                                                                    </span>
                            </label>
                        </c:forEach>
                    </c:if>
                </div>
                <c:if test="${!(empty handlerGroups)}">
                    <div id="roles" class="optgroup"><I18n:message key="PRSTATUSES"/>
                        <c:forEach items="${handlerGroups}" var="group" varStatus="c">
                            <label for="handlergroup_${group.id}" class="sel${c.index mod 2}"><span class="user">
                                <html:radio style="vertical-align:top;" property="handler"
                                            alt="${canShowNobody ? '' : '>0'}" value="PR_${group.id}"
                                            styleId="handlergroup_${group.id}"/>
                        <c:out value="${group.name}" escapeXml="true"/>
						</span></label>
                        </c:forEach>
                    </div>
                </c:if>
                <c:if test="${!(empty handlers)}">
                    <div id="userlist" class="optgroup"><I18n:message key="USERS_LIST"/>
                        <c:forEach items="${handlers}" var="huser" varStatus="c">
                            <label for="handler_${huser.id}" class="sel${c.index mod 2}">
                                <html:radio style="vertical-align:top;" property="handler"
                                            alt="${canShowNobody ? '' : '>0'}" value="${huser.id}"
                                            styleId="handler_${huser.id}"/>
                            <span class="user" ${huser.id eq sc.userId ? "id='loggedUser'" : ""}>
                                <input type="hidden" value="${huser.login}"/>
                                <c:out value="${huser.name}" escapeXml="true"/>&nbsp;
                        [<c:out value="${handlerMap[huser.id]}" escapeXml="true"/>]
			</span></label>
                        </c:forEach>
                    </div>
                </c:if>

            </div>
        </td>
    </tr>
</c:if>
<c:if test="${!empty resolutions && canViewResolution}">
    <tr>
        <th>
            <I18n:message key="MESSAGE_RESOLUTION"/>
        </th>
        <td>
            <html:select property="resolution" alt="${!defaultResolution ? 'mustChoose(resolution)' : ''}">
                <c:if test="${!defaultResolution}">
                    <option value=""><I18n:message key="CHOOSE_ONE"/></option>
                </c:if>
                <html:options collection="resolutions" property="key" labelProperty="value"/>
            </html:select>
        </td>
    </tr>
</c:if>
<c:if test="${canEditPriority && !empty priorities}">
    <tr>
        <th>
            <I18n:message key="MESSAGE_PRIORITY"/>
        </th>
        <td>
            <html:select property="priority">
                <html:options collection="priorities" property="key" labelProperty="value"/>
            </html:select>
        </td>
    </tr>
</c:if>
<c:if test="${canEditDeadline}">
    <tr>
        <th><label for="deadline"><I18n:message key="MESSAGE_DEADLINE"/></label></th>
        <td>
            <html:text property="deadline" styleId="deadline" alt="date(${pattern2})" size="18"
                       maxlength="40"/>&nbsp;<html:img src="${contextPath}${ImageServlet}/cssimages/ico.calendar.gif"
                                                       border="0" altKey="SELECT_DATE" titleKey="SELECT_DATE"
                                                       styleClass="calendaricon"
                                                       onclick="return showCalendar('deadline', '${pattern2}', '24', true);"/>
        </td>
    </tr>
</c:if>
<c:if test="${canEditBudget}">
    <tr>
        <th><I18n:message key="BUDGET"/></th>
        <td>
            <div class="budget">
                <c:if test="${budgetY}">
                    <c:choose>
                        <c:when test="${!budgetM && !budgetW && !budgetD && !budgeth && !budgetm && !budgets}">
                            <html:text styleId="budgetYears" alt="float" property="budgetDoubleYears" size="6"
                                       maxlength="6"/>
                        </c:when>
                        <c:otherwise>
                            <html:text styleId="budgetYears" alt="natural" property="budgetIntegerYears" size="6"
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
                            <html:text styleId="budgetMonths" alt="natural" property="budgetIntegerMonths" size="6"
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
                            <html:text styleId="budgetWeeks" alt="natural" property="budgetIntegerWeeks" size="6"
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
                            <html:text styleId="budgetHours" alt="natural" property="budgetIntegerHours" size="6"
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
                            <html:text styleId="budgetMinutes" alt="float" property="budgetDoubleMinutes" size="6"
                                       maxlength="12"/>
                        </c:when>
                        <c:otherwise>
                            <html:text styleId="budgetMinutes" alt="natural" property="budgetIntegerMinutes" size="6"
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

        </td>
    </tr>

</c:if>
<c:if test="${canEditActualBudget}">
    <tr>
        <th><I18n:message key="MESSAGE_ABUDGET"/></th>
        <td>
            <div class="budget">
                <c:if test="${budgetY}">
                    <c:choose>
                        <c:when test="${!budgetM && !budgetW && !budgetD && !budgeth && !budgetm && !budgets}">
                            <html:text styleId="actualBudgetYears" alt="float" property="actualBudgetDoubleYears"
                                       size="6"
                                       maxlength="6"/>
                        </c:when>
                        <c:otherwise>
                            <html:text styleId="actualBudgetYears" alt="natural" property="actualBudgetIntegerYears"
                                       size="6"
                                       maxlength="6"/>
                        </c:otherwise>
                    </c:choose>
                    <label for="actualBudgetYears">
                        <I18n:message key="BUDGET_YEARS"/>
                    </label>
                </c:if>
                <c:if test="${budgetM}">
                    <c:choose>
                        <c:when test="${!budgetW && !budgetD && !budgeth && !budgetm && !budgets}">
                            <html:text styleId="actualBudgetMonths" alt="float" property="actualBudgetDoubleMonths"
                                       size="6"
                                       maxlength="6"/>
                        </c:when>
                        <c:otherwise>
                            <html:text styleId="actualBudgetMonths" alt="natural" property="actualBudgetIntegerMonths"
                                       size="6"
                                       maxlength="6"/>
                        </c:otherwise>
                    </c:choose>
                    <label for="actualBudgetMonths">
                        <I18n:message key="BUDGET_MONTHS"/>
                    </label>
                </c:if>
                <c:if test="${budgetW}">
                    <c:choose>
                        <c:when test="${!budgetD && !budgeth && !budgetm && !budgets}">
                            <html:text styleId="actualBudgetWeeks" alt="float" property="actualBudgetDoubleWeeks"
                                       size="6"
                                       maxlength="6"/>
                        </c:when>
                        <c:otherwise>
                            <html:text styleId="actualBudgetWeeks" alt="natural" property="actualBudgetIntegerWeeks"
                                       size="6"
                                       maxlength="6"/>
                        </c:otherwise>
                    </c:choose>
                    <label for="actualBudgetWeeks">
                        <I18n:message key="BUDGET_WEEKS"/>
                    </label>
                </c:if>
                <c:if test="${budgetD}">
                    <c:choose>
                        <c:when test="${!budgeth && !budgetm && !budgets}">
                            <html:text styleId="actualBudgetDays" alt="float" property="actualBudgetDoubleDays" size="6"
                                       maxlength="12"/>
                        </c:when>
                        <c:otherwise>
                            <html:text styleId="actualBudgetDays" alt="natural" property="actualBudgetIntegerDays"
                                       size="6"
                                       maxlength="12"/>
                        </c:otherwise>
                    </c:choose>
                    <label for="actualBudgetDays">
                        <I18n:message key="BUDGET_DAYS"/>
                    </label>
                </c:if>
                <c:if test="${budgeth}">

                    <c:choose>
                        <c:when test="${!budgetm && !budgets}">
                            <html:text styleId="actualBudgetHours" alt="float" property="actualBudgetDoubleHours"
                                       size="6"
                                       maxlength="12"/>
                        </c:when>
                        <c:otherwise>
                            <html:text styleId="actualBudgetHours" alt="natural" property="actualBudgetIntegerHours"
                                       size="6"
                                       maxlength="12"/>
                        </c:otherwise>
                    </c:choose>
                    <label for="actualBudgetHours">
                        <I18n:message key="BUDGET_HOURS"/>
                    </label>
                </c:if>
                <c:if test="${budgetm}">
                    <c:choose>
                        <c:when test="${!budgets}">
                            <html:text styleId="actualBudgetMinutes" alt="float" property="actualBudgetDoubleMinutes"
                                       size="6"
                                       maxlength="12"/>
                        </c:when>
                        <c:otherwise>
                            <html:text styleId="actualBudgetMinutes" alt="natural" property="actualBudgetIntegerMinutes"
                                       size="6"
                                       maxlength="12"/>
                        </c:otherwise>
                    </c:choose>
                    <label for="actualBudgetMinutes">
                        <I18n:message key="BUDGET_MINUTES"/>
                    </label>
                </c:if>
                <c:if test="${budgets}">
                    <html:text styleId="actualBudgetSeconds" alt="natural" property="actualBudgetIntegerSeconds"
                               size="6"
                               maxlength="12"/>
                    <label for="actualBudgetSeconds">
                        <I18n:message key="BUDGET_SECONDS"/>
                    </label>
                </c:if><span class="clock"><img src="${contextPath}${ImageServlet}/cssimages/ico.clock.gif"
                                                title="<I18n:message key="CLICK_TO_SET"/>"
                                                onclick="peekTimer('${id}');"><span id="clockId"></span>
                    <script type="text/javascript">
                        setTimer('${id}');
                        getTimer();
                    </script><img src="${contextPath}${ImageServlet}/cssimages/ico.pause.gif"
                                  title="<I18n:message key="CLICK_TO_PAUSE"/>" id="clockPause"
                                  onclick="pauseTimer('${id}');"><img id="clockPlay"
                                                                      src="${contextPath}${ImageServlet}/cssimages/ico.play.gif"
                                                                      title="<I18n:message key="CLICK_TO_START"/>"
                                                                      style="display:none;"
                                                                      onclick="setTimer('${id}');getTimer();"><img
                        src="${contextPath}${ImageServlet}/cssimages/ico.stop.gif"
                        title="<I18n:message key="CLICK_TO_RESET"/>" id="clockStop" onclick="stopTimer('${id}');"></span>
                <script type="text/javascript">
                    if (runClockWhenOpenTask == 'false') {
                        pauseTimer('${id}');
                    }
                </script>
            </div>
        </td>

    </tr>
</c:if>
<c:import url="/jsp/custom/UDFEditTemplateTile.jsp"/>
</table>
<c:if test="${canCreateTaskMessageAttachments}">
    <c:import url="/jsp/attachments/AttachmentCreateTile.jsp"/>
</c:if>
<div class="controls">
    <input type="submit" class="iconized" value="<I18n:message key="SAVE"/>" name="ADDMESSAGE" disabled="true"
           onclick="setParamSubmit(this.name);"/>
    <c:if test="${taskId ne '1'}">
        <input type="submit" class="iconized secondary" value="<I18n:message key="GO_PARENT"/>" name="GOPARENT"
               disabled="true" onclick="setParamSubmit(this.name);"/>
    </c:if>
    <c:if test="${next ne null}">
        <input type="submit" class="iconized secondary" value="<I18n:message key="GO_NEXT"/>"
               title="<c:out value="${next.name}"/>" name="GONEXT" disabled="true"
               onclick="setParamSubmit(this.name);"/>
    </c:if>
    <html:button styleClass="iconized secondary" property="cancelButton" disabled="true"
                 onclick="showButton(true); document.location='${referer}';">
        <I18n:message key="CANCEL"/>
    </html:button>
</div>


</div>
</html:form>

<c:if test="${canViewTaskAttachments && !empty attachments}">
    <div class="strut"><a class="internal" name="attachments"></a></div>
    <c:import url="/jsp/task/viewtask/attachments/TaskAttachmentViewTile.jsp"/>
</c:if>

<c:if test="${!empty messages}">
    <div class="caption">
        <a id="messageHistoryHeader" class="history-closed internal" href="javascript://nop/"
           onclick="openMessage(this);"><I18n:message key="HISTORY"/></a>
    </div>
    <div class="indent" id="messageHistory">
        <c:import url="/jsp/task/viewtask/messages/MessagesTile.jsp"/>
    </div>
</c:if>
</div>
</tiles:put>
</tiles:insert>
<script type="text/javascript">
    showButton(false);
    function showButton(result) {
        var save = document.getElementsByName("ADDMESSAGE");
        for (var s = 0; s != save.length; s++) {
            save[s].disabled = result;
        }
        var goParent = document.getElementsByName("GOPARENT");
        for (var gp = 0; gp != goParent.length; gp++) {
            goParent[gp].disabled = result;
        }
        var goNext = document.getElementsByName("GONEXT");
        for (var gn = 0; gn != goNext.length; gn++) {
            goNext[gn].disabled = result;
        }
        var cancel = document.getElementsByName("cancelButton");
        for (var c = 0; c != cancel.length; c++) {
            cancel[c].disabled = result;
        }
    }
</script>
