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
</script>
<script type="text/javascript">
    var resourceId = "${tci.id}";
    var number = "${tci.number}";
    var taskUpload = true;
    // Small tolerance for browser rounding noise in scroll metrics.
    var TASK_LAYOUT_SCROLL_TOLERANCE_PX = 1;
    // Width delta required before switching back to two columns.
    var TASK_LAYOUT_HYSTERESIS_PX = 72;
    var taskLayoutForcedSingleAtWidth = null;
    var taskLayoutSyncTimer = null;

    function syncTaskLayoutMode() {
        var layout = document.querySelector('.ts-task-layout');
        if (!layout) return;
        var docEl = document.documentElement;
        var viewportWidth = window.innerWidth || docEl.clientWidth;
        var overflowPx = docEl.scrollWidth - docEl.clientWidth;
        var hasHorizontalScroll = overflowPx > TASK_LAYOUT_SCROLL_TOLERANCE_PX;
        if (!layout.classList.contains('ts-task-layout--single') && hasHorizontalScroll) {
            layout.classList.add('ts-task-layout--single');
            taskLayoutForcedSingleAtWidth = viewportWidth;
        } else if (layout.classList.contains('ts-task-layout--single') && taskLayoutForcedSingleAtWidth !== null
            && viewportWidth >= taskLayoutForcedSingleAtWidth + TASK_LAYOUT_HYSTERESIS_PX && !hasHorizontalScroll) {
            layout.classList.remove('ts-task-layout--single');
            taskLayoutForcedSingleAtWidth = null;
        }
    }

    function scheduleTaskLayoutSync() {
        clearTimeout(taskLayoutSyncTimer);
        taskLayoutSyncTimer = setTimeout(syncTaskLayoutMode, 80);
    }

    window.addEventListener('load', scheduleTaskLayoutSync);
    window.addEventListener('resize', scheduleTaskLayoutSync);
</script>
        <c:set var="urlHtml" value="html"/>
<ts:js request="${request}" response="${response}">
    <ts:jsLink link="${urlHtml}/dnd/dnd.js"/>
</ts:js>
<div class="ts-task-layout">
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
<div id="servicePanel" class="${selectedIds!=null && !empty selectedIds ? "norm" : "closed"} ts-task-layout__full">
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
               class="ts-service-panel-item"
               title="[#${st.number}] <c:out value="${st.name}"/>"
               onclick="placeOnServicePanel('<c:out value="${st.id}"/>','#<c:out value="${st.number}"/>');"
               for="#<c:out value="${st.number}"/>">#<c:out value="${st.number}"/> </label>
    </c:forEach>

</div>
<div class="controlPanel ts-task-layout__full ts-task-main-actions">
    <span class="ts-task-toolbar-main">
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
    </span>
    <span class="separator ts-task-toolbar-divider" aria-hidden="true"></span>
    <span class="ts-task-toolbar-secondary">
        <html:link href="javascript:showBookmarkDialogSimple();">
            <html:img src="${contextPath}${ImageServlet}/cssimages/ico.star.gif" border="0"/>
            <I18n:message key="BOOKMARKS"/>
        </html:link>
        <html:link href="javascript:window.print();">
            <html:img src="${contextPath}${ImageServlet}/cssimages/ico.print.png" border="0"/>
            <I18n:message key="PRINT"/>
        </html:link>
        <c:if test="${isAudit}">
            <c:choose>
                <c:when test="${!showAudit}">
                    <html:link titleKey="SHOW_AUDIT"
                               href="${contextPath}/MessageAction.do?method=auditTrail&id=${id}&session=${session}&asView=${asView}">
                        <html:img border="0" src="${contextPath}${ImageServlet}/cssimages/audit.png"/>
                        <I18n:message key="SHOW_AUDIT"/>
                    </html:link>
                </c:when>
                <c:otherwise>
                    <html:link titleKey="HIDE_AUDIT"
                               href="${contextPath}/MessageAction.do?method=auditTrail&id=${id}&session=${session}&asView=${asView}">
                        <html:img border="0" src="${contextPath}${ImageServlet}/cssimages/audit.png"/>
                        <I18n:message key="HIDE_AUDIT"/>
                    </html:link>
                </c:otherwise>
            </c:choose>
        </c:if>
        <c:if test="${showClipboardButton}">
            <c:if test="${canArchive}">
                <html:link onclick="placeOnServicePanel('${id}','#${tci.number}')"
                           href="${contextPath}/SubtaskAction.do?method=archive&amp;id=${tci.parent.id}&amp;SELTASK=${id}">
                    <html:img src="${contextPath}${ImageServlet}/cssimages/ico.attachment.png" border="0"/>
                    <I18n:message key="ARCHIVE"/>
                </html:link>
            </c:if>
            <html:link onclick="placeOnServicePanel('${id}','#${tci.number}')"
                       href="${contextPath}/SubtaskAction.do?method=clipboardOperation&amp;id=${tci.parent.id}&amp;collector=${id}&amp;operation=SINGLE_COPY">
                <html:img src="${contextPath}${ImageServlet}/cssimages/ico.copy.gif" border="0"/>
                <I18n:message key="COPY"/>
            </html:link>
            <html:link onclick="placeOnServicePanel('${id}','#${tci.number}')"
                       href="${contextPath}/SubtaskAction.do?method=clipboardOperation&amp;id=${tci.parent.id}&amp;collector=${id}&amp;operation=CUT">
                <html:img src="${contextPath}${ImageServlet}/cssimages/ico.cut.gif" border="0"/>
                <I18n:message key="CUT"/>
            </html:link>
        </c:if>
    </span>
</div>
<c:if test="${!empty mstatuses}">
    <div class="controlPanel ts-task-layout__full ts-workflow-toolbar ts-task-workflow-toolbar">
        <script type="text/javascript">
            var taskWorkflowMenu = {};
        </script>
        <span class="ts-workflow-toolbar__group">
            <c:forEach items="${mstatuses}" var="mstatus">
                <c:choose>
                    <c:when test="${lastMessage == null}">
                        <c:choose>
                            <c:when test="${fn:indexOf(mstatus.action,'/')>0}">
                                <c:set var="menuGroup" value="${fn:substringBefore(mstatus.action,'/')}"/>
                                <script type="text/javascript">
                                    if (!taskWorkflowMenu['${fn:escapeXml(menuGroup)}']) {
                                        taskWorkflowMenu['${fn:escapeXml(menuGroup)}'] = new TSMenu();
                                        taskWorkflowMenu['${fn:escapeXml(menuGroup)}'].width = 320;
                                    }
                                    taskWorkflowMenu['${fn:escapeXml(menuGroup)}'].add(new TSMenuItem("<c:out value="${fn:substringAfter(mstatus.action,'/')}"/>", "${contextPath}/MessageCreateAction.do?method=page&id=${id}&mstatus=${mstatus.id}", false, false, "<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.messagetypes.gif", "", ""));
                                </script>
                            </c:when>
                            <c:otherwise>
                                <c:choose>
                                    <c:when test="${fn:indexOf(mstatus.preferences,'T')>-1}">
                                        <html:link title="${fn:escapeXml(mstatus.description)}"
                                                   href="${contextPath}/MessageCreateAction.do?method=page&id=${id}&mstatus=${mstatus.id}">
                                            <html:img src="${contextPath}${ImageServlet}/cssimages/ico.messagetypes.gif"
                                                      border="0"/>
                                            <c:out value="${mstatus.action}"/>
                                        </html:link>
                                    </c:when>
                                    <c:otherwise>
                                        <script type="text/javascript">
                                            if (!taskWorkflowMenu['<I18n:message key="OTHER_ACTIONS"/>']) {
                                                taskWorkflowMenu['<I18n:message key="OTHER_ACTIONS"/>'] = new TSMenu();
                                                taskWorkflowMenu['<I18n:message key="OTHER_ACTIONS"/>'].width = 320;
                                            }
                                            taskWorkflowMenu['<I18n:message key="OTHER_ACTIONS"/>'].add(new TSMenuItem(
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
                                    if (!taskWorkflowMenu['${fn:escapeXml(menuGroup)}']) {
                                        taskWorkflowMenu['${fn:escapeXml(menuGroup)}'] = new TSMenu();
                                        taskWorkflowMenu['${fn:escapeXml(menuGroup)}'].width = 320;
                                    }
                                    taskWorkflowMenu['${fn:escapeXml(menuGroup)}'].add(new TSMenuItem("<c:out value="${fn:substringAfter(mstatus.action,'/')}"/>", "javascript:document.getElementById('formMethod').value='page'; document.getElementById('formMstatus').value='${mstatus.id}'; document.getElementById('messageCreateActionForm').submit();", false, false, "<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.messagetypes.gif", "", ""));
                                </script>
                            </c:when>
                            <c:otherwise>
                                <c:choose>
                                    <c:when test="${fn:indexOf(mstatus.preferences,'T')>-1}">
                                        <html:link title="${fn:escapeXml(mstatus.description)}"
                                                   href="javascript:document.getElementById('formMethod').value='page'; document.getElementById('formMstatus').value='${mstatus.id}'; document.getElementById('messageCreateActionForm').submit();">
                                            <html:img src="${contextPath}${ImageServlet}/cssimages/ico.messagetypes.gif"
                                                      border="0"/>
                                            <c:out value="${mstatus.action}"/>
                                        </html:link>
                                    </c:when>
                                    <c:otherwise>
                                        <script type="text/javascript">
                                            if (!taskWorkflowMenu['<I18n:message key="OTHER_ACTIONS"/>']) {
                                                taskWorkflowMenu['<I18n:message key="OTHER_ACTIONS"/>'] = new TSMenu();
                                                taskWorkflowMenu['<I18n:message key="OTHER_ACTIONS"/>'].width = 320;
                                            }
                                            taskWorkflowMenu['<I18n:message key="OTHER_ACTIONS"/>'].add(new TSMenuItem(
                                                    "<c:out value="${mstatus.action}"/>",
                                                    "javascript:document.getElementById('formMethod').value='page'; document.getElementById('formMstatus').value='${mstatus.id}'; document.getElementById('messageCreateActionForm').submit();",
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
                    </c:otherwise>
                </c:choose>
            </c:forEach>
            <span class="additional">
                <script type="text/javascript">
                    var taskWorkflowMenuBar = new TSMenuBar();
                    for (var h in taskWorkflowMenu) {
                        taskWorkflowMenuBar.add(new TSMenuBut(h, null, taskWorkflowMenu[h], "${contextPath}${ImageServlet}/cssimages/ico.messagetypes.gif", "", ""));
                    }
                    document.write(taskWorkflowMenuBar);
                </script>
            </span>
        </span>
    </div>
</c:if>
<c:if test="${isDescription}">
    <div class="ts-task-main">
        <div class="taskDescription">
            <div class="content" id="taskDescription">
                <div class="indent">
                    <ts:htmlfilter session="${sc.id}" macros="true" audit="false" request="<%=request%>"><c:out value="${tci.description}" escapeXml="false"/></ts:htmlfilter>
                </div>
            </div>
        </div>
    </div>
</c:if>
<div class="blueborder ts-task-sidebar" id="taskProperties">
<div class="caption">
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
</div>
<div class="indent ts-task-properties-wrap" style="padding-bottom:0px;">
<table class="general ts-task-properties">
    <tr>
        <th class="taskinfo-right ts-task-property-group">
            <a class="internal" href="${contextPath}/task/${tci.number}?thisframe=true#statuTracking" id="statuTracking">
                <div class="ts-task-property-group-title"><I18n:message key="TASK_INFO_GROUP_STATE"/></div>
            </a>
        </th>
    </tr>
    <c:if test="${tci.categoryId ne null}">
        <tr class="ts-task-property-label-row">
            <th><I18n:message key="CATEGORY"/></th>
        </tr>
        <tr class="ts-task-property-value-row">
            <td>
                <html:img styleClass="icon" border="0" src="${contextPath}${ImageServlet}/icons/categories/${tci.category.icon}"/>
                <c:out value="${tci.category.name}" escapeXml="true"/>
            </td>
        </tr>
    </c:if>
    <c:if test="${tci.statusId ne null}">
        <tr class="ts-task-property-label-row">
            <th><I18n:message key="TASK_STATE"/></th>
        </tr>
        <tr class="ts-task-property-value-row">
            <td>
                <span class="nowrap">
                    <html:img styleClass="state" border="0" style="background-color: ${tci.status.color}" src="${contextPath}${ImageServlet}${tci.status.image}"/>
                    <c:out value="${tci.status.name}" escapeXml="true"/>
                </span>
            </td>
        </tr>
    </c:if>
    <c:if test="${tci.resolutionId ne null}">
        <tr class="ts-task-property-label-row">
            <th><I18n:message key="RESOLUTION"/></th>
        </tr>
        <tr class="ts-task-property-value-row">
            <td><c:out value="${tci.resolution.name}" escapeXml="true"/></td>
        </tr>
    </c:if>
    <c:if test="${tci.priorityId ne null}">
        <tr class="ts-task-property-label-row">
            <th><I18n:message key="PRIORITY"/></th>
        </tr>
        <tr class="ts-task-property-value-row">
            <td><c:out value="${tci.priority.name}" escapeXml="true"/></td>
        </tr>
    </c:if>
    <c:if test="${tci.submitter ne null}">
        <tr class="ts-task-property-label-row">
            <th><I18n:message key="SUBMITTER"/></th>
        </tr>
        <tr class="ts-task-property-value-row">
            <td>
                <c:set var="hintUser" value="${tci.submitter.login} email:${tci.submitter.email} ${tci.submitter.tel}"/>
                <span title="<c:out value="${hintUser}" escapeXml="true"/>" class="user" ${tci.submitterId eq sc.userId ? "id='loggedUser'" : ""}>
                    <html:img styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/${tci.submitter.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
                    <c:out value="${tci.submitter.name}" escapeXml="true"/>
                </span>
            </td>
        </tr>
    </c:if>
    <tr class="ts-task-property-label-row">
        <th><I18n:message key="HANDLER"/></th>
    </tr>
    <tr class="ts-task-property-value-row">
        <td>
            <c:choose>
                <c:when test="${tci.handlerUserId ne null}">
                    <c:set var="hintUser" value="${tci.handlerUser.login} email:${tci.handlerUser.email} ${tci.handlerUser.tel}"/>
                    <span title="<c:out value="${hintUser}" escapeXml="true"/>" class="user" ${tci.handlerUserId eq sc.userId ? "id='loggedUser'" : ""}>
                        <html:img styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/${tci.handlerUser.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
                        <c:out value="${tci.handlerUser.name}" escapeXml="true"/>
                    </span>
                </c:when>
                <c:when test="${tci.handlerGroupId ne null}">
                    <span class="user">
                        <html:img styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/ico.status.gif"/>
                        <c:out value="${tci.handlerGroup.name}" escapeXml="true"/>
                    </span>
                </c:when>
                <c:otherwise>
                    <I18n:message key="UNASSIGNED"/>
                </c:otherwise>
            </c:choose>
        </td>
    </tr>
    <tr>
        <th class="taskinfo-right ts-task-property-group">
            <a class="internal" href="${contextPath}/task/${tci.number}?thisframe=true#timeTracking" id="timeTracking">
                <div class="ts-task-property-group-title"><I18n:message key="TASK_INFO_GROUP_TIME"/></div>
            </a>
        </th>
    </tr>
    <c:if test="${tci.submitdate ne null}">
        <tr class="ts-task-property-label-row">
            <th><I18n:message key="SUBMIT_DATE"/></th>
        </tr>
        <tr class="ts-task-property-value-row">
            <td><I18n:formatDate value="${tci.submitdate.time}" type="both" dateStyle="short" timeStyle="short"/></td>
        </tr>
    </c:if>
    <c:if test="${tci.updatedate ne null}">
        <tr class="ts-task-property-label-row">
            <th><I18n:message key="UPDATE_DATE"/></th>
        </tr>
        <tr class="ts-task-property-value-row">
            <td><I18n:formatDate value="${tci.updatedate.time}" type="both" dateStyle="short" timeStyle="short"/></td>
        </tr>
    </c:if>
    <c:if test="${tci.closedate ne null}">
        <tr class="ts-task-property-label-row">
            <th><I18n:message key="CLOSE_DATE"/></th>
        </tr>
        <tr class="ts-task-property-value-row">
            <td><I18n:formatDate value="${tci.closedate.time}" type="both" dateStyle="short" timeStyle="short"/></td>
        </tr>
    </c:if>
    <c:if test="${tci.deadline ne null}">
        <tr class="ts-task-property-label-row">
            <th><I18n:message key="DEADLINE"/></th>
        </tr>
        <tr class="ts-task-property-value-row">
            <td><I18n:formatDate value="${tci.deadline.time}" type="both" dateStyle="short" timeStyle="short"/></td>
        </tr>
    </c:if>
    <c:if test="${!empty tci.budgetAsString}">
        <tr class="ts-task-property-label-row">
            <th><I18n:message key="BUDGET"/></th>
        </tr>
        <tr class="ts-task-property-value-row">
            <td><c:out value="${tci.budgetAsString}" escapeXml="true"/></td>
        </tr>
    </c:if>
    <c:if test="${!empty tci.actualBudgetAsString || canEditActualBudget}">
        <tr class="ts-task-property-label-row">
            <th><I18n:message key="ABUDGET"/></th>
        </tr>
        <tr class="ts-task-property-value-row">
            <td>
                <c:choose>
                    <c:when test="${!empty tci.actualBudgetAsString}">
                        <c:out value="${tci.abudgetToString}" escapeXml="true"/>&nbsp;/<c:out value="${tci.actualBudgetAsString}" escapeXml="true"/>
                        <c:if test="${canEditActualBudget}">
                            <span class="clock"><img src="${contextPath}${ImageServlet}/cssimages/ico.clock.gif"><span id="clockId"></span>
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
                                    onclick="stopTimer('${id}');"></span>
                        </c:if>
                        <script type="text/javascript">
                            if (runClockWhenOpenTask == 'false') {
                                pauseTimer('${id}');
                            }
                        </script>
                    </c:when>
                    <c:otherwise>
                        <c:if test="${canEditActualBudget}">
                            <span class="clock"><img src="${contextPath}${ImageServlet}/cssimages/ico.clock.gif"><span id="clockId"></span>
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
                    </c:otherwise>
                </c:choose>
            </td>
        </tr>
    </c:if>
</table>

<c:if test="${isViewUdf}">
    <table class="general customfields ts-task-customfields" cellpadding="0" cellspacing="0">
        <caption>
            <a class="internal" href="${contextPath}/task/${tci.number}?thisframe=true#customField" id="customField">
                <div class="ts-task-property-group-title"><I18n:message key="CUSTOM_FIELDS"/></div>
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
    <div class="blueborder ts-task-layout__full">
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
    <div class="blueborder ts-task-layout__full">
        <div class="caption">
            <a class="internal" href="${contextPath}/task/${tci.number}?thisframe=true#referenced" id="referenced">
                <div class="ts-task-property-group-title"><I18n:message key="REFERENCED_BY_USERS"/></div>
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

<c:if test="${canViewTaskAttachments && !empty attachments}">
    <div class="ts-task-layout__full">
        <div class="strut"><a class="internal" name="attachments"></a></div>
        <c:import url="/jsp/task/viewtask/attachments/TaskAttachmentViewTile.jsp"/>
    </div>
</c:if>


<c:if test="${lastMessage ne null}">
    <div class="ts-task-layout__full ts-task-main--activity">
        <div class="strut">&nbsp;</div>
        <c:import url="/MessageAction.do?method=page&id=${id}"/>
    </div>
</c:if>
</div>

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
