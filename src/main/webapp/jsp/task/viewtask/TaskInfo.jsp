<%@ page buffer="128kb" errorPage="/jsp/Error.jsp" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/tiles-el" prefix="tiles" %>
<%@ taglib uri="http://trackstudio.com" prefix="ts" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value="${sc.timezone}"/>
<I18n:setBundle basename="language"/>
<tiles:insert page="/jsp/layout/ListLayout.jsp" flush="true">
<tiles:put name="header" value="/jsp/task/TaskHeader.jsp"/>
<tiles:put name="customHeader" type="string"/>
<tiles:put name="tabs" type="string"/>
<tiles:put name="main" type="string">
<c:if test="${canView}">
<script language="JavaScript">
    function bulkAction() {
        var params = {method : 'bulk', id : '${tci.id}', collector : '${tci.id};' , bulk : $("#bulk").val(), taskView : true};
        $.ajax('${contextPath}/SubtaskAction.do', {
            data : params,
            success: function(data) {
                document.location.href = '${contextPath}/task/${tci.number}?thisframe=true'
            }
        });
    }

    function deleteMessages() {
        return deleteConfirm("<I18n:message key="DELETE_MESSAGE_HISTORY_REQ"/>", "messageForm");
    }

    function openDescription(Sender) {
        Sender.parentNode.className = "taskDescription descopened";
    }
    function closeDescription(Sender) {
        Sender.parentNode.className = "taskDescription descclosed";
    }
</script>
<script type="text/javascript">
    var resourceId = "${tci.id}";
    var number = "${tci.number}";
    var taskUpload = true;
</script>
        <c:set var="urlHtml" value="html"/>
<ts:js request="${request}" response="${response}">
    <ts:jsLink link="${urlHtml}/dnd/dnd.js"/>
</ts:js>
<c:if test="${isDescription}">
    <div class="taskDescription descclosed">
        <label class="labelclose" onclick="closeDescription(this);"><img
                src="${contextPath}${ImageServlet}/cssimages/compact.png" class="icon"
                title="<I18n:message key="LESS"/>"><I18n:message key="LESS"/></label>

        <div class="content" id="taskDescription">
            <div class="indent">
                <ts:htmlfilter session="${sc.id}" macros="true" audit="false" request="<%=request%>"><c:out value="${tci.description}" escapeXml="false"/></ts:htmlfilter>
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


<script type="text/javascript">

    function showBookmarkDialog(bookmarkName, taskId) {
        document.getElementById('bookmark_name').value = bookmarkName;

        document.getElementById('task_id').value = taskId;
        YAHOO.trackstudio.bookmark.bookmark_dialog.show();
    }

    function showViewDialog() {
        YAHOO.trackstudio.bookmark.view_dialog.show();
    }


    function showBookmarkDialogSimple() {
        showBookmarkDialog('<c:out value="${title}" escapeXml="false"/>', '<c:out value="${tci.id}"/>');
    }
</script>
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
<div class="blueborder">
<div class="caption">
    <html:link styleClass="link" href="javascript:showBookmarkDialogSimple();" style="float:right;padding-right:5px;">
        <html:img src="${contextPath}${ImageServlet}/cssimages/ico.star.gif" border="0"/>
    </html:link>
    <div class="navigation">
        <c:if test="${showView}"><a href="#" style=" text-decoration: none;" onclick="javascript:showViewDialog();"><I18n:message key="VIEWS"/></a></c:if>
        <c:if test="${prev ne null}">&lt; <html:link styleClass="internal link"
                                                     href="${contextPath}/TaskViewAction.do?method=page&amp;id=${prev.id}&amp;thisframe=true"
                                                     title="${prev.name}">
            <I18n:message key="PREV"/>
        </html:link></c:if>
        <c:if test="${position ne null}">${position+1} <I18n:message key="OF"/> ${listSize}</c:if>
        <c:if test="${next ne null}"><html:link styleClass="internal link"
                                                href="${contextPath}/TaskViewAction.do?method=page&amp;id=${next.id}&amp;thisframe=true"
                                                title="${next.name}">
            <I18n:message key="NEXT"/>
        </html:link> &gt;</c:if>
    </div>
    <a class="internal_properties" style="font-size:14px;"
       href="${contextPath}/task/${tci.number}?thisframe=true#taskProperties" id="taskProperties">
        <I18n:message key="TASK_PROPERTIES"/>
    </a>
    <html:link href="javascript:window.print();" styleClass="link" style="float: right; text-decoration: none; font-weight: normal;padding-right: 20px;">
        <html:img src="${contextPath}${ImageServlet}/cssimages/ico.print.png" border="0"/>
    </html:link>
    <c:if test="${isAudit}">
        <c:choose>
            <c:when test="${!showAudit}">
                <html:link styleClass="link" style="float: right; text-decoration: none; font-weight: normal;padding-right: 20px;"
                           titleKey="SHOW_AUDIT"
                           href="${contextPath}/MessageAction.do?method=auditTrail&id=${id}&session=${session}&asView=${asView}"><html:img
                        border="0" src="${contextPath}${ImageServlet}/cssimages/audit.png"/>&nbsp;<I18n:message
                        key="SHOW_AUDIT"/></html:link>
            </c:when>
            <c:otherwise>
                <html:link styleClass="link" style="float: right; text-decoration: none; font-weight: normal;padding-right: 20px;"
                           titleKey="HIDE_AUDIT"
                           href="${contextPath}/MessageAction.do?method=auditTrail&id=${id}&session=${session}&asView=${asView}"><html:img
                        border="0" src="${contextPath}${ImageServlet}/cssimages/audit.png"/>&nbsp;<I18n:message
                        key="HIDE_AUDIT"/></html:link>
            </c:otherwise>
        </c:choose>
    </c:if>
    <c:if test="${canPerformBulkProcessing}">
        <span style="float: right;">
        <c:if test="${!empty bulkProcessing}">
            <select id="bulk">
                <c:forEach items="${bulkProcessing}" var="t">
                <option value="${t.name}"><c:out value="${t.name}"/>
                    </c:forEach>
            </select>
            <input type="button" class="secondary" style="vertical-align: top; height: 18px; margin-right: 10px;"
                    onclick="bulkAction();"
                   value="<I18n:message key="APPLY"/>"
                   name="APPLY">
        </c:if>
        </span>
    </c:if>
    <c:if test="${showClipboardButton}">
        <c:if test="${canArchive}">
        <html:link styleClass="link"
                   style="float: right; text-decoration: none; font-weight: normal;padding-right: 5px;padding-left: 5px;"
                   onclick="placeOnServicePanel('${id}','#${tci.number}')"
                   href="${contextPath}/SubtaskAction.do?method=archive&amp;id=${tci.parent.id}&amp;SELTASK=${id}"><html:img
                src="${contextPath}${ImageServlet}/cssimages/ico.attachment.png" border="0"/>&nbsp;<I18n:message
                key="ARCHIVE"/></html:link>
        </c:if>
        <html:link styleClass="link" style="float: right; text-decoration: none; font-weight: normal;padding-right: 20px;"
                   onclick="placeOnServicePanel('${id}','#${tci.number}')"
                   href="${contextPath}/SubtaskAction.do?method=clipboardOperation&amp;id=${tci.parent.id}&amp;collector=${id}&amp;operation=SINGLE_COPY"><html:img
                src="${contextPath}${ImageServlet}/cssimages/ico.copy.gif" border="0"/>&nbsp;<I18n:message
                key="COPY"/></html:link>
        <html:link styleClass="link"
                style="float: right; text-decoration: none; font-weight: normal;padding-right: 5px;padding-left: 5px;"
                onclick="placeOnServicePanel('${id}','#${tci.number}')"
                href="${contextPath}/SubtaskAction.do?method=clipboardOperation&amp;id=${tci.parent.id}&amp;collector=${id}&amp;operation=CUT"><html:img
                src="${contextPath}${ImageServlet}/cssimages/ico.cut.gif" border="0"/><I18n:message
                key="CUT"/></html:link>
    </c:if>
</div>
<div class="controlPanel">
    <script type="text/javascript">
        var mstatusMenu = {};
    </script>
    <c:if test="${canEditTask}">
        <html:link href="${contextPath}/TaskEditAction.do?method=page&id=${id}">
            <html:img src="${contextPath}${ImageServlet}/cssimages/ico.edit.gif" border="0" altKey="EDIT"/>
            <I18n:message key="EDIT"/>
        </html:link>
    </c:if>
    <c:if test="${canCreateTaskAttachments && canEditTask}">
        <html:link href="${contextPath}/AttachmentEditAction.do?method=attachToTask&id=${id}">
            <html:img src="${contextPath}${ImageServlet}/cssimages/ico.attachment.png" border="0"/>
            <I18n:message key="FILE_ADD"/>
        </html:link>
    </c:if>

    <c:forEach items="${mstatuses}" var="mstatus">
        <c:choose>
            <c:when test="${lastMessage == null}">
                <c:choose>
                    <c:when test="${fn:indexOf(mstatus.action,'/')>0}">
                        <c:set var="menuGroup" value="${fn:substringBefore(mstatus.action,'/')}"/>
                        <script type="text/javascript">
                            if (!mstatusMenu['${menuGroup}']) {
                                mstatusMenu['${menuGroup}'] = new TSMenu();
                                mstatusMenu['${menuGroup}'].width = 320;
                            }
                            mstatusMenu['${menuGroup}'].add(new TSMenuItem("<c:out value="${fn:substringAfter(mstatus.action,'/')}"/>", "${contextPath}/MessageCreateAction.do?method=page&id=${id}&mstatus=${mstatus.id}", false, false, "<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.messagetypes.gif", "", ""));
                        </script>
                    </c:when>

                    <c:otherwise>
                        <c:choose>
                            <c:when test="${fn:indexOf(mstatus.preferences,'T')>-1}">
                                <html:link title="${mstatus.description}"
                                           href="${contextPath}/MessageCreateAction.do?method=page&id=${id}&mstatus=${mstatus.id}">
                                    <html:img src="${contextPath}${ImageServlet}/cssimages/ico.messagetypes.gif"
                                              border="0"/>
                                    <c:out value="${mstatus.action}"/>
                                </html:link>
                            </c:when>
                            <c:otherwise>
                                <script type="text/javascript">
                                    if (!mstatusMenu['<I18n:message key="OTHER_ACTIONS"/>']) {
                                        mstatusMenu['<I18n:message key="OTHER_ACTIONS"/>'] = new TSMenu();
                                        mstatusMenu['<I18n:message key="OTHER_ACTIONS"/>'].width = 320;
                                    }
                                    mstatusMenu['<I18n:message key="OTHER_ACTIONS"/>'].add(new TSMenuItem(
                                            "<c:out value="${mstatus.action}"/>",
                                            "${contextPath}/MessageCreateAction.do?method=page&id=${id}&mstatus=${mstatus.id}",
                                            false,
                                            false,
                                            "<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.messagetypes.gif",
                                            "",
                                            "<c:out value="${mstatus.description}" escapeXml="true"/>"));
                                </script>
                            </c:otherwise>
                        </c:choose>
                    </c:otherwise>
                </c:choose>
            </c:when>
            <c:otherwise>
                <c:choose>
                    <c:when test="${fn:indexOf(mstatus.action,'/')>0}">
                        <c:set var="menuGroup" value="${fn:substringBefore(mstatus.action,'/')}"/>
                        <script type="text/javascript">
                            if (!mstatusMenu['${menuGroup}']) {
                                mstatusMenu['${menuGroup}'] = new TSMenu();
                                mstatusMenu['${menuGroup}'].width = 320;
                            }
                            mstatusMenu['${menuGroup}'].add(new TSMenuItem("<c:out value="${fn:substringAfter(mstatus.action,'/')}"/>", "javascript:document.getElementById('formMethod').value='page'; document.getElementById('formMstatus').value='${mstatus.id}'; document.getElementById('messageCreateActionForm').submit();", false, false, "<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.messagetypes.gif", "", ""));
                        </script>
                    </c:when>

                    <c:otherwise>
                        <c:choose>
                            <c:when test="${fn:indexOf(mstatus.preferences,'T')>-1}">
                                <html:link title="${mstatus.description}"
                                           href="javascript:document.getElementById('formMethod').value='page'; document.getElementById('formMstatus').value='${mstatus.id}'; document.getElementById('messageCreateActionForm').submit();">
                                    <html:img src="${contextPath}${ImageServlet}/cssimages/ico.messagetypes.gif"
                                              border="0"/>
                                    <c:out value="${mstatus.action}"/>
                                </html:link>
                            </c:when>
                            <c:otherwise>
                                <script type="text/javascript">
                                    if (!mstatusMenu['<I18n:message key="OTHER_ACTIONS"/>']) {
                                        mstatusMenu['<I18n:message key="OTHER_ACTIONS"/>'] = new TSMenu();
                                        mstatusMenu['<I18n:message key="OTHER_ACTIONS"/>'].width = 320;
                                    }
                                    mstatusMenu['<I18n:message key="OTHER_ACTIONS"/>'].add(new TSMenuItem(
                                            "<c:out value="${mstatus.action}"/>",
                                            "javascript:document.getElementById('formMethod').value='page'; document.getElementById('formMstatus').value='${mstatus.id}'; document.getElementById('messageCreateActionForm').submit();",
                                            false,
                                            false,
                                            "<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.messagetypes.gif",
                                            "",
                                            "${mstatus.description}"));
                                </script>
                            </c:otherwise>
                        </c:choose>
                    </c:otherwise>
                </c:choose>
            </c:otherwise>
        </c:choose>
    </c:forEach>
    <c:if test="${!empty mstatuses}">
    <span class="additional">
    <script type="text/javascript">
        var mstatusMenuBar = new TSMenuBar();
        for (var h in mstatusMenu) {
            mstatusMenuBar.add(new TSMenuBut(h, null, mstatusMenu[h], "${contextPath}${ImageServlet}/cssimages/ico.messagetypes.gif", "", ""));
        }
        document.write(mstatusMenuBar);
    </script>
    </span>
    </c:if>
</div>

<div class="indent" style="padding-bottom:0px;">
<table class="general">

<colgroup>
    <col width="15%">
    <col width="34%">
    <col width="2%">
    <col width="15%">
    <col width="34%">
</colgroup>

<tr>
    <th class="taskinfo-right statetracking" colspan=2>
        <a class="internal" href="${contextPath}/task/${tci.number}?thisframe=true#statuTracking" id="statuTracking">
            <div style="color:#000000"><I18n:message key="STATE_TRACKING"/></div>
        </a>
    </th>
    <td class="strut">&nbsp;</td>
    <th class="taskinfo-left timetracking" colspan=2>
        <a class="internal" href="${contextPath}/task/${tci.number}?thisframe=true#timeTracking" id="timeTracking">
            <div style="color:#000000"><I18n:message key="TIME_TRACKING"/></div>
        </a>
    </th>
</tr>
<tr>
    <c:choose>
        <c:when test="${tci.categoryId ne null}">
            <th class="nowrap statetracking">
                <I18n:message key="CATEGORY"/>
            </th>
            <td class="nowrap statetracking">
                <html:img styleClass="icon" border="0"
                          src="${contextPath}${ImageServlet}/icons/categories/${tci.category.icon}"/>
                <c:out value="${tci.category.name}" escapeXml="true"/>
            </td>
        </c:when>
        <c:otherwise>
            <th class="hden statetracking">
                <I18n:message key="CATEGORY"/>
            </th>
            <td class="nowrap statetracking"></td>
        </c:otherwise>
    </c:choose>
    <td class="strut">&nbsp;</td>
    <c:choose>
        <c:when test="${tci.submitdate ne null}">
            <th class="nowrap timetracking">
                <I18n:message key="SUBMIT_DATE"/>
            </th>
            <td class="nowrap timetracking">
                <I18n:formatDate value="${tci.submitdate.time}" type="both" dateStyle="short" timeStyle="short"/>
            </td>
        </c:when>
        <c:otherwise>
            <th class="hden timetracking"><I18n:message key="SUBMIT_DATE"/></th>
            <td class="nowrap statetracking"></td>
        </c:otherwise>
    </c:choose>
</tr>
<tr>
    <c:choose>
        <c:when test="${tci.statusId ne null}">

            <th class="nowrap statetracking">
                <I18n:message key="TASK_STATE"/>
            </th>
            <td class="nowrap statetracking">
                <span class="nowrap">
                     <html:img styleClass="state" border="0" style="background-color: ${tci.status.color}"
                               src="${contextPath}${ImageServlet}${tci.status.image}"/>
                    <c:out value="${tci.status.name}" escapeXml="true"/>
                </span>
            </td>
        </c:when>
        <c:otherwise>
            <th class="hden statetracking"><I18n:message key="TASK_STATE"/></th>
            <td class="nowrap statetracking"></td>
        </c:otherwise>
    </c:choose>
    <td class="strut">&nbsp;</td>
    <c:choose>
        <c:when test="${tci.updatedate ne null}">

            <th class="nowrap timetracking">
                <I18n:message key="UPDATE_DATE"/>
            </th>
            <td class="nowrap timetracking">
                <I18n:formatDate value="${tci.updatedate.time}" type="both" dateStyle="short" timeStyle="short"/>
            </td>

        </c:when>
        <c:otherwise>
            <th class="hden timetracking"><I18n:message key="UPDATE_DATE"/></th>
            <td class="nowrap statetracking"></td>
        </c:otherwise>
    </c:choose>
</tr>
<tr>
    <c:choose>
        <c:when test="${tci.resolutionId ne null}">

            <th class="nowrap statetracking">
                <I18n:message key="RESOLUTION"/>
            </th>
            <td class="nowrap statetracking">
                <c:out value="${tci.resolution.name}" escapeXml="true"/>
            </td>

        </c:when>
        <c:otherwise>
            <th class="hden statetracking"><I18n:message key="RESOLUTION"/></th>
            <td class="nowrap statetracking"></td>
        </c:otherwise>
    </c:choose>
    <td class="strut">&nbsp;</td>
    <c:choose>
        <c:when test="${tci.closedate ne null}">
            <th class="nowrap timetracking">
                <I18n:message key="CLOSE_DATE"/>
            </th>
            <td class="nowrap timetracking">
                <I18n:formatDate value="${tci.closedate.time}" type="both" dateStyle="short" timeStyle="short"/>
            </td>
        </c:when>
        <c:otherwise>
            <th class="hden timetracking"><I18n:message key="CLOSE_DATE"/></th>
            <td class="nowrap statetracking"></td>
        </c:otherwise>
    </c:choose>
</tr>
<tr>
    <c:choose>
        <c:when test="${tci.priorityId ne null}">
            <th class="nowrap statetracking">
                <I18n:message key="PRIORITY"/>
            </th>
            <td class="nowrap statetracking">
                <c:out value="${tci.priority.name}" escapeXml="true"/>
            </td>
        </c:when>
        <c:otherwise>
            <th class="hden statetracking"><I18n:message key="PRIORITY"/></th>
            <td class="nowrap statetracking"></td>
        </c:otherwise>
    </c:choose>
    <td class="strut">&nbsp;</td>
    <c:choose>
        <c:when test="${tci.deadline ne null}">
            <th class="nowrap timetracking">
                <I18n:message key="DEADLINE"/>
            </th>
            <td class="nowrap timetracking">
                <I18n:formatDate value="${tci.deadline.time}" type="both" dateStyle="short" timeStyle="short"/>
            </td>
        </c:when>
        <c:otherwise>
            <th class="hden timetracking"><I18n:message key="DEADLINE"/></th>
            <td class="nowrap statetracking"></td>
        </c:otherwise>
    </c:choose>
</tr>
<tr>
    <c:choose>
        <c:when test="${tci.submitter ne null}">

            <th class="nowrap statetracking">
                <I18n:message key="SUBMITTER"/>
            </th>
            <td class="nowrap statetracking">
                <c:set var="hintUser" value="${tci.submitter.login} email:${tci.submitter.email} ${tci.submitter.tel}"/>
                <span title="${hintUser}" class="user" ${tci.submitterId eq sc.userId ? "id='loggedUser'" : ""}>
                    <html:img styleClass="icon" border="0"
                              src="${contextPath}${ImageServlet}/cssimages/${tci.submitter.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
                    <c:out value="${tci.submitter.name}" escapeXml="true"/>
	            </span>
            </td>
        </c:when>
        <c:otherwise>
            <th class="hden statetracking"><I18n:message key="SUBMITTER"/></th>
            <td class="nowrap statetracking"></td>
        </c:otherwise>
    </c:choose>
    <td class="strut">&nbsp;</td>
    <c:choose>
        <c:when test="${!empty tci.budgetAsString}">

            <th class="nowrap timetracking">
                <I18n:message key="BUDGET"/>
            </th>
            <td class="nowrap timetracking">
                <c:out value="${tci.budgetAsString}" escapeXml="true"/>
            </td>

        </c:when>
        <c:otherwise>

            <th class="hden timetracking"><I18n:message key="BUDGET"/></th>
            <td class="nowrap statetracking"></td>
        </c:otherwise>
    </c:choose>
</tr>
<tr>
    <c:choose>
        <c:when test="${tci.handlerUserId ne null || tci.handlerGroupId ne null}">

            <th class="nowrap statetracking">
                <I18n:message key="HANDLER"/>
            </th>
            <td class="nowrap statetracking">
                <c:choose>
                    <c:when test="${tci.handlerUserId ne null}">
                        <c:set var="hintUser"
                               value="${tci.handlerUser.login} email:${tci.handlerUser.email} ${tci.handlerUser.tel}"/>
					<span title="${hintUser}" class="user" ${tci.handlerUserId eq sc.userId ? "id='loggedUser'" : ""}>
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

            </td>

        </c:when>
        <c:otherwise>

            <th class="hden statetracking"><I18n:message key="HANDLER"/></th>
            <td class="nowrap statetracking"></td>

        </c:otherwise>
    </c:choose>
    <td class="strut">&nbsp;</td>
    <c:choose>
        <c:when test="${!empty tci.actualBudgetAsString}">

            <th class="nowrap timetracking">
                <I18n:message key="ABUDGET"/>
            </th>
            <td class="nowrap timetracking">
                <c:out value="${tci.abudgetToString}" escapeXml="true"/>&nbsp;/<c:out
                    value="${tci.actualBudgetAsString}" escapeXml="true"/><c:if test="${canEditActualBudget}"><span
                    class="clock"><img src="${contextPath}${ImageServlet}/cssimages/ico.clock.gif"><span
                    id="clockId"></span>
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
                        title="<I18n:message key="CLICK_TO_RESET"/>" id="clockStop"
                        onclick="stopTimer('${id}');"></span></c:if>
            </td>
            <script type="text/javascript">
                if (runClockWhenOpenTask == 'false') {
                    pauseTimer('${id}');
                }
            </script>

        </c:when>
        <c:otherwise>
            <th class="hden timetracking"><I18n:message key="ABUDGET"/></th>
            <td class="nowrap timetracking"><c:if test="${canEditActualBudget}"><span class="clock"><img
                    src="${contextPath}${ImageServlet}/cssimages/ico.clock.gif"><span id="clockId"></span>
                <script type="text/javascript">
                    setTimer('${id}');
                    getTimer();
                </script>
                <img src="${contextPath}${ImageServlet}/cssimages/ico.pause.gif"
                     title="<I18n:message key="CLICK_TO_PAUSE"/>" id="clockPause"
                     onclick="pauseTimer('${id}'); document.getElementById('clockPlay').style.display='inline'; document.getElementById('clockPause').style.display='none';">
                <img id="clockPlay" src="${contextPath}${ImageServlet}/cssimages/ico.play.gif"
                     title="<I18n:message key="CLICK_TO_START"/>" style="display:none;"
                     onclick="setTimer('${id}'); document.getElementById('clockPlay').style.display='none'; document.getElementById('clockPause').style.display='inline';getTimer();">
                <img src="${contextPath}${ImageServlet}/cssimages/ico.stop.gif"
                     title="<I18n:message key="CLICK_TO_RESET"/>" id="clockStop"
                     onclick="stopTimer('${id}'); document.getElementById('clockPlay').style.display='inline'; document.getElementById('clockPause').style.display='none';"></span>
                <script type="text/javascript">
                    if (runClockWhenOpenTask == 'false') {
                        pauseTimer('${id}');
                    }
                </script>
            </c:if>
            </td>
        </c:otherwise>
    </c:choose>
</tr>
</table>

<c:if test="${isViewUdf}">
    <table class="general customfields" cellpadding="0" cellspacing="0">
        <caption>
            <a class="internal" href="${contextPath}/task/${tci.number}?thisframe=true#customField" id="customField">
                <div style="color:#000000"><I18n:message key="CUSTOM_FIELDS"/></div>
            </a>
        </caption>
        <colgroup>
            <col class="col_1">
            <col class="col_2">
        </colgroup>
        <c:import url="/jsp/custom/UDFViewTemplate.jsp"/>
    </table>
</c:if>


</div>
</div>

<c:if test="${!empty refTasks}">
    <div class="strut">&nbsp;</div>
    <div class="blueborder">
        <div class="caption">
            <I18n:message key="REFERENCED_BY_TASKS"/>
        </div>
        <table class="general customfields" style="border: 0;margin-bottom:0;" cellpadding="0" cellspacing="0">
            <colgroup>
                <col class="col_1">
                <col class="col_2">
            </colgroup>
            <c:forEach var="udf" items="${refTasks}">
                <c:if test="${udf.key.referencedByCaption ne null && udf.key.referencedByCaption ne ''}">
                    <tr>
                        <th>
                            <c:out value="${udf.key.referencedByCaption}" escapeXml="true"/>
                        </th>
                        <td>
                            <c:forEach var="t" items="${udf.value}" varStatus="status">
                                <c:choose>
                                    <c:when test="${t.allowedByACL}">
                                        <html:link styleClass="internal"
                                                   href="${contextPath}/TaskViewAction.do?method=page&amp;id=${t.id}">
                                            <html:img styleClass="icon" border="0"
                                                      src="${contextPath}${ImageServlet}/icons/categories/${t.category.icon}"/>
                                            <html:img styleClass="state" border="0"
                                                      style="background-color: ${t.status.color}"
                                                      src="${contextPath}${ImageServlet}${t.status.image}"/>
                                            <c:out value="${t.name}"/></html:link>&nbsp;<em class="number">[#<c:out
                                            value="${t.number}"/>]
                                        </em>
                                    </c:when>
                                    <c:otherwise>
                                        #<c:out value="${t.number}"/>
                                    </c:otherwise>
                                </c:choose>
                                <br>
                            </c:forEach>
                        </td>
                    </tr>
                </c:if>
            </c:forEach>
        </table>
    </div>
</c:if>

<c:if test="${!empty refsUser}">
    <div class="strut">&nbsp;</div>
    <div class="blueborder">
        <div class="caption">
            <a class="internal" href="${contextPath}/task/${tci.number}?thisframe=true#referenced" id="referenced">
                <div style="font-size:14px;"><I18n:message key="REFERENCED_BY_USERS"/></div>
            </a>
        </div>
        <div class="indent">
            <table class="general customfields" cellpadding="0" cellspacing="0">
                <colgroup>
                    <col class="col_1">
                    <col class="col_2">
                </colgroup>

                <c:forEach var="udf" items="${refsUser}">

                    <tr>
                        <th>
                            <c:out value="${udf.key.referencedByCaption}" escapeXml="true"/>
                        </th>
                        <td>
                            <c:forEach var="user" items="${udf.value}" varStatus="status">
                                <html:link styleClass="internal"
                                           href="${contextPath}/UserViewAction.do?method=page&amp;id=${user.id}">
                                    <c:out value="${user.name}"/></html:link>&nbsp;<em class="number">[@<c:out
                                    value="${user.login}"/>]</em>

                                <br>
                            </c:forEach>
                        </td>
                    </tr>

                </c:forEach>
            </table>
        </div>
    </div>
</c:if>

<c:if test="${isMessagesList && lastMessage ne null}">
    <div class="strut">&nbsp;</div>
    <div class="blueborder" id="divLastMessage">
        <div class="caption">
            <a class="internal" href="${contextPath}/task/${tci.number}?thisframe=true#lastMessage" id="lastMessage">
                <div style="font-size:14px;"><I18n:message key="LAST_MESSAGE"/></div>
            </a>
        </div>
        <div class="indent line0">
            <div class="msgbox-opened" id="lastMessage${lastMessage.id}">
            <span class="msgtime">
                <a class="internal"
                   href="${contextPath}/task/${tci.number}?thisframe=true#lastMessage${lastMessage.id}">
                    <div style="color:#000000">
                        <I18n:formatDate value="${lastMessage.time.time}" type="both" dateStyle="short"
                                         timeStyle="short"/>
                    </div>
                </a>
            </span>
                <label class="messagelabel" for="lastMessage${lastMessage.id}">
                    <c:choose>
                        <c:when test="${lastMessage.testEmpty}">
                            <html:img src="${contextPath}${ImageServlet}/cssimages/ico.nocollapse.png" hspace="0"
                                      vspace="0" border="0" altKey="SHOW" styleClass="imgExpand"/>
                            <html:img src="${contextPath}${ImageServlet}/cssimages/ico.nocollapse.png" hspace="0"
                                      vspace="0" border="0" altKey="HIDE" styleClass="imgCollapse"/>
                        </c:when>
                        <c:otherwise>
                            <label class="messagelabel" for="lastMessage${lastMessage.id}" onclick="msgSwitch(this);">
                                <html:img src="${contextPath}${ImageServlet}/cssimages/ico.expand.png" hspace="0"
                                          vspace="0" border="0" altKey="SHOW" styleClass="imgExpand"/>
                                <html:img src="${contextPath}${ImageServlet}/cssimages/ico.collapse.png" hspace="0"
                                          vspace="0" border="0" altKey="HIDE" styleClass="imgCollapse"/>
                            </label>
                        </c:otherwise>
                    </c:choose>
                    <c:choose>
                        <c:when test="${!empty lastMessage.attachments}">
                            <html:img src="${contextPath}${ImageServlet}/cssimages/ico.attachment2.gif" hspace="0"
                                      vspace="0" border="0" align="middle" altKey="ATTACHMENT"/>
                        </c:when>
                        <c:otherwise>
                            <html:img src="${contextPath}${ImageServlet}/cssimages/dot.gif" width="10px" hspace="0"
                                      vspace="0" border="0" align="middle"/>
                        </c:otherwise>
                    </c:choose>
                    <c:out value="${lastMessage.mstatus.name}" escapeXml="true"/>
                    <c:if test="${lastMessage.resolution ne null}">(<c:out value="${lastMessage.resolution.name}"
                                                                           escapeXml="true"/>)</c:if>
                    <c:set var="hintUser" value="${lastMessage.submitter.login} email:${lastMessage.submitter.email} ${lastMessage.submitter.tel}"/>
            <span style="vertical-align:top;"
                  class="user" ${lastMessage.submitterId eq sc.userId ? "id='loggedUser'" : ""} title="<c:out value="${hintUser}" escapeXml="true"/>">
                <c:out value="${lastMessage.submitter.name}" escapeXml="true"/>
                </span>
                    <c:choose>
                        <c:when test="${lastMessage.handlerUserId ne null}">
                            <I18n:message key="FOR"/>
                            <c:set var="hintUser" value="${lastMessage.handlerUser.login} email:${lastMessage.handlerUser.email} ${lastMessage.handlerUser.tel}"/>
            <span style="vertical-align:top;"
                  class="user" ${lastMessage.handlerUserId eq sc.userId ? "id='loggedUser'" : ""} title="<c:out value="${hintUser}" escapeXml="true"/>">
                <c:out value="${lastMessage.handlerUser.name}" escapeXml="true"/>
            </span>
                        </c:when>
                        <c:when test="${lastMessage.handlerGroupId ne null}">
                            <I18n:message key="FOR"/>
                            <span class="user"><html:img styleClass="icon" border="0"
                                                         src="${contextPath}${ImageServlet}/cssimages/ico.status.gif"/><c:out
                                    value="${lastMessage.handlerGroup.name}" escapeXml="true"/></span>
                        </c:when>
                    </c:choose>
                </label>

                <c:if test="${lastMessage.priorityId ne null || lastMessage.deadline ne null || !empty lastMessage.budgetAsString || !empty lastMessage.actualBudgetAsString || !empty lastMessage.attachments}">
                    <table class="general" cellpadding="0" cellspacing="0">

                        <c:if test="${lastMessage.priorityId ne null}">
                            <tr>
                                <th width="20%"><I18n:message key="MESSAGE_PRIORITY"/></th>
                                <td width="80%"><c:out value="${lastMessage.priority.name}" escapeXml="true"/></td>
                            </tr>
                        </c:if>
                        <c:if test="${lastMessage.deadline ne null}">
                            <tr>
                                <th width="20%"><I18n:message key="MESSAGE_DEADLINE"/></th>
                                <td width="80%"><I18n:formatDate value="${lastMessage.deadline.time}" type="both"
                                                                 dateStyle="short" timeStyle="short"/></td>
                            </tr>
                        </c:if>

                        <c:if test="${!empty lastMessage.budgetAsString}">
                            <tr>
                                <th width="20%"><I18n:message key="BUDGET"/></th>
                                <td width="80%"><c:out value="${lastMessage.budgetAsString}" escapeXml="true"/></td>
                            </tr>
                        </c:if>

                        <c:if test="${!empty lastMessage.actualBudgetAsString}">
                            <tr>
                                <th width="20%"><I18n:message key="MESSAGE_ABUDGET"/></th>
                                <td width="80%"><c:out value="${lastMessage.actualBudgetAsString}"
                                                       escapeXml="true"/></td>
                            </tr>
                        </c:if>

                        <c:if test="${!empty lastMessage.attachments}">
                            <tr class="attach-msg">
                                <th width="20%"><I18n:message key="ATTACHMENT"/></th>
                                <td width="80%">
                                    <c:forEach items="${lastMessage.attachments}" var="ata">
                                        <c:if test="${!ata.deleted}">
                                            <c:choose>
                                                <c:when test="${ata.thumbnailed}">
                                                    <a class="internal" href="<c:url value="/download/task/${lastMessage.task.number}/${ata.id}"/>?type=image" data-lightbox="example-1">
                                                        <img alt="" src="${contextPath}/TSImageServlet?attId=${ata.id}&width=100&height=75" hspace="0" vspace="0">
                                                    </a>
                                                </c:when>
                                                <c:otherwise>
                                                    <a class="internal" target="_blank" href="<c:url value="/download/task/${lastMessage.task.number}/${ata.id}/${ata.name}"/>" title="<c:out value="${ata.description}"/>">
                                                        <html:img src="${contextPath}${ImageServlet}/cssimages/ico.attachment2.gif" hspace="4" vspace="0" border="0" align="middle" altKey="ATTACHMENT"/><c:out value="${ata.name}"/>
                                                    </a>
                                                </c:otherwise>
                                            </c:choose>
                                            <br>
                                        </c:if>
                                        <c:if test="${ata.deleted}">
                                            <html:img src="${contextPath}${ImageServlet}/cssimages/ico.attachment2.gif"
                                                      hspace="4" vspace="0" border="0" align="middle"
                                                      altKey="ATTACHMENT"/><img border="0" hspace="0" vspace="0"
                                                                                title="<I18n:message key="ATTACHMENT_DELETED"/>"
                                                                                src="${contextPath}${ImageServlet}/cssimages/warning.gif"/><c:out
                                                value="${ata.name}"/>
                                        </c:if>
                                    </c:forEach>
                                </td>
                            </tr>
                        </c:if>
                    </table>
                </c:if>
                <div class="description">
                    <ts:htmlfilter session="${sc.id}" macros="true" audit="true" request="<%=request%>"><c:out
                            value="${lastMessage.description}" escapeXml="false"/></ts:htmlfilter>
                </div>
            </div>
        </div>
    </div>
</c:if>


<c:if test="${canViewTaskAttachments && !empty attachments}">
    <div class="strut"><a class="internal" class="internal" name="attachments"></a></div>
    <c:import url="/jsp/task/viewtask/attachments/TaskAttachmentViewTile.jsp"/>
</c:if>


<div class="strut">&nbsp;</div>
<c:if test="${lastMessage ne null}">
    <c:import url="/MessageAction.do?method=page&id=${id}"/>
</c:if>

<script type="text/javascript">
    function onSubmitBookMark() {
        if (document.getElementById('bookmark_name').value != '') {
            YAHOO.trackstudio.bookmark.bookmark_dialog.hide();
            var url = "${contextPath}/BookmarkAction.do";
            var pars = {method : 'save', name : document.getElementById('bookmark_name').value, taskId: document.getElementById('task_id').value};
	        $.ajax(url, {
		        data : pars,
		        method: "post",
		        success: function(data) {
			        self.top.frames[0].updateBookmarks("${contextPath}/bookmark");
		        }
	        });
        } else {
            alert("<I18n:message key="ENTER_BOOKMARK_NAME"/>");
        }
        return false;
    }
</script>

<div id="bookmark_dialog" style="visibility:hidden;">
    <div class="hd"><I18n:message key="CREATE_BOOKMARK"/></div>
    <div class="bd">
        <form method="post" idd="bookmarkForm" action="" onsubmit="onSubmitBookMark()">
            <table>
                <tr>
                    <td><label for="name" style="margin-right: 20px;"><I18n:message key="NAME"/></label></td>
                    <td><input type="text" value="${name}" id="bookmark_name" size="35"/></td>
                </tr>
                <tr>
                    <td><label for="task" style="margin-right: 20px;"><I18n:message key="TASK"/></label></td>
                    <td>#<c:out value="${tci.number}"/>&nbsp;<c:out value="${tci.taskNameCutted}"/></td>
                </tr>
            </table>
            <input type="hidden" value="${taskId}" id="task_id"/>
        </form>
    </div>
</div>

<div id="view_dialog" style="visibility:hidden;">
    <div class="hd"><I18n:message key="VIEW"/></div>
    <div class="bd">
        <a href="#" style=" text-decoration: none;"><I18n:message key="TASK"/></a>
        <a href="${contextPath}/task/${tci.number}?thisframe=true&asView=document" ><I18n:message key="DOCUMENT"/></a>
        <a href="${contextPath}/task/${tci.number}?thisframe=true&asView=container"><I18n:message key="CONTAINER"/></a>
    </div>
</div>
</c:if>
    <script text="text/javascript">
        $(window).keydown(function(event) {
            if(event.ctrlKey && event.keyCode === 37 && '${prev}' !== '') {
                location.href='${contextPath}/TaskViewAction.do?method=page&id=${prev.id}&thisframe=true';
                event.preventDefault();
            }
            if(event.ctrlKey && event.keyCode === 39 && '${next}' !== '') {
                location.href='${contextPath}/TaskViewAction.do?method=page&id=${next.id}&thisframe=true';
                event.preventDefault();
            }
        });
    </script>
</tiles:put>
</tiles:insert>
