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
        <script language="JavaScript">
            function openDescription(Sender) {
                Sender.parentNode.className = "taskDescription descopened";
            }
            function closeDescription(Sender) {
                Sender.parentNode.className = "taskDescription descclosed";
            }
        </script>
        <script type="text/javascript">
            var resourceId = "${archive.id}";
            var number = "${archive.number}";
            var taskUpload = true;
        </script>
        <c:set var="urlHtml" value="html"/>
        <ts:js request="${request}" response="${response}">
            <ts:jsLink link="${urlHtml}/dnd/dnd.js"/>
        </ts:js>
        <c:if test="${archive.description != null}">
            <div class="taskDescription descclosed">
                <label class="labelclose" onclick="closeDescription(this);"><img
                        src="${contextPath}${ImageServlet}/cssimages/compact.png" class="icon"
                        title="<I18n:message key="LESS"/>"><I18n:message key="LESS"/></label>

                <div class="content" id="taskDescription">
                    <div class="indent">
                        <ts:htmlfilter session="${sc.id}" macros="true" audit="false" request="<%=request%>"><c:out value="${archive.description}" escapeXml="false"/></ts:htmlfilter>
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
                showBookmarkDialog('<c:out value="${title}" escapeXml="false"/>', '<c:out value="${archive.id}"/>');
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
                   href="${contextPath}/task/${archive.number}?thisframe=true#taskProperties" id="taskProperties">
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
                    <html:link styleClass="link" style="float: right; text-decoration: none; font-weight: normal;padding-right: 20px;"
                               onclick="placeOnServicePanel('${id}','#${archive.number}')"
                               href="${contextPath}/SubtaskAction.do?method=clipboardOperation&amp;id=${archive.parent.id}&amp;collector=${id}&amp;operation=SINGLE_COPY"><html:img
                            src="${contextPath}${ImageServlet}/cssimages/ico.copy.gif" border="0"/><I18n:message
                            key="COPY"/></html:link>
                    <html:link styleClass="link"
                               style="float: right; text-decoration: none; font-weight: normal;padding-right: 5px;padding-left: 5px;"
                               onclick="placeOnServicePanel('${id}','#${archive.number}')"
                               href="${contextPath}/SubtaskAction.do?method=clipboardOperation&amp;id=${archive.parent.id}&amp;collector=${id}&amp;operation=CUT"><html:img
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
                            <a class="internal" href="${contextPath}/task/${archive.number}?thisframe=true#statuTracking" id="statuTracking">
                                <div style="color:#000000"><I18n:message key="STATE_TRACKING"/></div>
                            </a>
                        </th>
                        <td class="strut">&nbsp;</td>
                        <th class="taskinfo-left timetracking" colspan=2>
                            <a class="internal" href="${contextPath}/task/${archive.number}?thisframe=true#timeTracking" id="timeTracking">
                                <div style="color:#000000"><I18n:message key="TIME_TRACKING"/></div>
                            </a>
                        </th>
                    </tr>
                    <tr>
                        <c:choose>
                            <c:when test="${archive.categoryId ne null}">
                                <th class="nowrap statetracking">
                                    <I18n:message key="CATEGORY"/>
                                </th>
                                <td class="nowrap statetracking">
                                    <html:img styleClass="icon" border="0"
                                              src="${contextPath}${ImageServlet}/icons/categories/${archive.categoryIcon}"/>
                                    <c:out value="${archive.categoryName}" escapeXml="true"/>
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
                            <c:when test="${archive.submitdate ne null}">
                                <th class="nowrap timetracking">
                                    <I18n:message key="SUBMIT_DATE"/>
                                </th>
                                <td class="nowrap timetracking">
                                    <I18n:formatDate value="${archive.submitdate.time}" type="both" dateStyle="short" timeStyle="short"/>
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
                            <c:when test="${archive.statusId ne null}">

                                <th class="nowrap statetracking">
                                    <I18n:message key="TASK_STATE"/>
                                </th>
                                <td class="nowrap statetracking">
                <span class="nowrap">
                     <html:img styleClass="state" border="0" style="background-color: ${archive.statusColor}"
                               src="${contextPath}${ImageServlet}${archive.statusImage}"/>
                    <c:out value="${archive.statusName}" escapeXml="true"/>
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
                            <c:when test="${archive.updatedate ne null}">

                                <th class="nowrap timetracking">
                                    <I18n:message key="UPDATE_DATE"/>
                                </th>
                                <td class="nowrap timetracking">
                                    <I18n:formatDate value="${archive.updatedate.time}" type="both" dateStyle="short" timeStyle="short"/>
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
                            <c:when test="${archive.resolutionId ne null}">

                                <th class="nowrap statetracking">
                                    <I18n:message key="RESOLUTION"/>
                                </th>
                                <td class="nowrap statetracking">
                                    <c:out value="${archive.resolutionName}" escapeXml="true"/>
                                </td>

                            </c:when>
                            <c:otherwise>
                                <th class="hden statetracking"><I18n:message key="RESOLUTION"/></th>
                                <td class="nowrap statetracking"></td>
                            </c:otherwise>
                        </c:choose>
                        <td class="strut">&nbsp;</td>
                        <c:choose>
                            <c:when test="${archive.closedate ne null}">
                                <th class="nowrap timetracking">
                                    <I18n:message key="CLOSE_DATE"/>
                                </th>
                                <td class="nowrap timetracking">
                                    <I18n:formatDate value="${archive.closedate.time}" type="both" dateStyle="short" timeStyle="short"/>
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
                            <c:when test="${archive.priorityId ne null}">
                                <th class="nowrap statetracking">
                                    <I18n:message key="PRIORITY"/>
                                </th>
                                <td class="nowrap statetracking">
                                    <c:out value="${archive.priorityName}" escapeXml="true"/>
                                </td>
                            </c:when>
                            <c:otherwise>
                                <th class="hden statetracking"><I18n:message key="PRIORITY"/></th>
                                <td class="nowrap statetracking"></td>
                            </c:otherwise>
                        </c:choose>
                        <td class="strut">&nbsp;</td>
                        <c:choose>
                            <c:when test="${archive.deadline ne null}">
                                <th class="nowrap timetracking">
                                    <I18n:message key="DEADLINE"/>
                                </th>
                                <td class="nowrap timetracking">
                                    <I18n:formatDate value="${archive.deadline.time}" type="both" dateStyle="short" timeStyle="short"/>
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
                            <c:when test="${archive.submitterId ne null}">

                                <th class="nowrap statetracking">
                                    <I18n:message key="SUBMITTER"/>
                                </th>
                                <td class="nowrap statetracking">
                                    <c:set var="hintUser" value="${archive.submitterLogin} email:${archive.submitterEmail} ${archive.submitterTel}"/>
                                    <span title="${hintUser}" class="user" ${archive.submitterId eq sc.userId ? "id='loggedUser'" : ""}>
                    <html:img styleClass="icon" border="0"
                              src="${contextPath}${ImageServlet}/cssimages/${archive.submitterActive ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
                    <c:out value="${archive.submitterName}" escapeXml="true"/>
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
                            <c:when test="${!empty archive.budgetAsString}">

                                <th class="nowrap timetracking">
                                    <I18n:message key="BUDGET"/>
                                </th>
                                <td class="nowrap timetracking">
                                    <c:out value="${archive.budgetAsString}" escapeXml="true"/>
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
                            <c:when test="${archive.handlerUserId ne null || archive.handlerGroupId ne null}">

                                <th class="nowrap statetracking">
                                    <I18n:message key="HANDLER"/>
                                </th>
                                <td class="nowrap statetracking">
                                    <c:choose>
                                        <c:when test="${archive.handlerUserId ne null}">
                                            <c:set var="hintUser"
                                                   value="${archive.handlerUserLogin} email:${archive.handlerUserEmail} ${archive.handlerUserTel}"/>
                                            <span title="${hintUser}" class="user" ${archive.handlerUserId eq sc.userId ? "id='loggedUser'" : ""}>
                        <html:img styleClass="icon" border="0"
                                  src="${contextPath}${ImageServlet}/cssimages/${archive.handlerUserActive ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
                        <c:out value="${archive.handlerUserName}" escapeXml="true"/>
			        </span>
                                        </c:when>
                                        <c:when test="${archive.handlerGroupId ne null}">
						<span class="user">
                        <html:img styleClass="icon" border="0"
                                  src="${contextPath}${ImageServlet}/cssimages/ico.status.gif"/><c:out
                                value="${archive.handlerGroupName}" escapeXml="true"/>
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
                            <c:when test="${!empty archive.actualBudgetAsString}">

                                <th class="nowrap timetracking">
                                    <I18n:message key="ABUDGET"/>
                                </th>
                                <td class="nowrap timetracking">
                                    <c:out value="${archive.abudgetToString}" escapeXml="true"/>&nbsp;/<c:out
                                        value="${archive.actualBudgetAsString}" escapeXml="true"/><c:if test="${canEditActualBudget}"><span
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

                <table class="general customfields" cellpadding="0" cellspacing="0">
                    <caption>
                        <a class="internal" href="${contextPath}/task/${archive.number}?thisframe=true#customField" id="customField">
                            <div style="color:#000000"><I18n:message key="CUSTOM_FIELDS"/></div>
                        </a>
                    </caption>
                    <colgroup>
                        <col class="col_1">
                        <col class="col_2">
                    </colgroup>
                    <c:forEach items="${archive.udfs}" var="udf">
                        <tr>
                            <th>
                                <c:out value="${udf.key}"/>
                            </th>
                            <td>
                                <c:out value="${udf.value}"/>
                            </td>
                        </tr>
                    </c:forEach>
                </table>


            </div>
        </div>

        <c:if test="${!empty archive.atts}">
            <div class="strut"><a class="internal" class="internal" name="attachments"></a></div>
            <div class="blueborder" id="divAttachment">
                <div class="caption">
                    <a class="internal" href="${contextPath}/task/${archive.number}?thisframe=true#attachments" id="attachments"><div style="font-size:14px;"><I18n:message key="ATTACHMENTS"/></div></a>
                </div>
                <script type="text/javascript">
                    function setMethod(target) {
                        document.getElementById('method').value = target;
                    }
                </script>

                <div class="indent" id="attachDiv">
                    <html:hidden property="method" styleId="method" value="creataArchiveTaskUpload"/>
                    <html:hidden property="session" value="${session}"/>
                    <html:hidden property="id" value="${id}"/>
                    <table class="general sortable" id="table_attachment" cellpadding="0" cellspacing="0">
                        <tr class="wide">
                            <th width="1%" nowrap style="white-space:nowrap;" class="nosort"> <input type="checkbox" onClick="selectAllCheckboxes(this, 'delete1');"></th>
                            <th width="24%"><I18n:message key="FILE"/></th>
                            <th><I18n:message key="FILE_SIZE"/></th>
                            <th width="15%" ${fn:contains(sc.locale, 'ru') ? 'class=date-ru' : ''}><I18n:message key="LAST_MODIFIED"/></th>
                            <th width="15%"><I18n:message key="OWNER"/></th>
                            <th width="25%"><I18n:message key="DESCRIPTION"/></th>
                            <th width="20%"><I18n:message key="THUMBNAIL"/></th>
                        </tr>
                        <c:forEach var="attachment" items="${archive.atts}" varStatus="varCounter">
                            <tr class="line<c:out value="${varCounter.index mod 2}"/>" id="${attachment.id}">
                                <td>
                                    <input type="checkbox" name="delete" alt="delete1" value="<c:out value="${attachment.id}"/>">
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${!empty attachment.zippedFiles}">
                                            <table class="zip">
                                                <tr>
                                                    <th colspan="2">
                                                        <html:img src="${contextPath}${ImageServlet}/cssimages/ico.zip.gif"/>
                                                        <a class="internal" target="blank" href="<c:url value="/download/task/${archive.number}/${attachment.id}?archive=true"/>"><c:out value="${attachment.shortName}" escapeXml="true"/></a></th></tr>
                                                <c:forEach var="zipped" items="${attachment.zippedFiles}" varStatus="nfile">
                                                    <tr>
                                                        <td  style="white-space: nowrap;">
                                                            <a class="internal" target="blank" href="<c:url value="/download/task/${archive.number}/zipped/${attachment.id}?archive=true"/>"><c:out value="${zipped.name}" escapeXml="true"/></a>
                                                        </td>
                                                        <td  style="white-space: nowrap;">
                                                            <I18n:message key="SIZE_FORMAT">
                                                                <I18n:param value="${zipped.size/1}"/>
                                                                <I18n:param value="${zipped.size/1024}"/>
                                                                <I18n:param value="${zipped.size/1048576}"/>
                                                            </I18n:message>
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                            </table>
                                        </c:when>
                                        <c:otherwise>
                                            <c:if test="${attachment.deleted}">
                                                <span  style="white-space: nowrap;">
                                                    <img border="0" hspace="0" vspace="0" title="<I18n:message key="ATTACHMENT_DELETED"/>" src="<c:out value="${contextPath}"/>${ImageServlet}/cssimages/warning.gif"/>
                                                    <c:out value="${attachment.shortName}" escapeXml="true"/>
                                                </span>
                                            </c:if>
                                            <c:if test="${!attachment.deleted}">
                                                <span  style="white-space: nowrap;">
                                                    <a class="internal" target="blank" href="<c:url value="/download/task/${archive.number}/${attachment.id}"/>?archive=true"><c:out value="${attachment.shortName}" escapeXml="true"/></a>
                                                     <c:if test="${attachment.part}">
                                                         <img border="0" hspace="0" vspace="0" title="<I18n:message key="CORRUPTED_FILE"/>" src="<c:out value="${contextPath}"/>${ImageServlet}/cssimages/warning.gif"/>
                                                     </c:if>
                                                </span>
                                            </c:if>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td style="white-space: nowrap;" <c:if test="${!attachment.deleted}">title="<I18n:message key="SIZE_ORIGIN"><I18n:param value="${attachment.size}"/></I18n:message>"</c:if>><c:if test="${!attachment.deleted}"><I18n:message key="SIZE_FORMAT"><I18n:param value="${attachment.sizeDoubleValue}"/><I18n:param value="${attachment.sizeDoubleValue/1024}"/><I18n:param value="${attachment.sizeDoubleValue/1048576}"/></I18n:message></c:if></td>
                                <td style="white-space: nowrap;"><c:if test="${!attachment.deleted}"><I18n:formatDate value="${attachment.lastModified.time}" type="both" dateStyle="short" timeStyle="short"/></c:if></td>
                                <td style="white-space: nowrap;">
                                    <c:if test="${attachment.user ne null}"><span class="user" ${attachment.user.id eq sc.userId ? "id='loggedUser'" : ""}><html:img  styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/${attachment.user.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
                                    <c:out value="${attachment.user.name}" escapeXml="true"/></span></c:if>
                                </td>
                                <td>
                                    <span title="${attachment.description}"><c:out value="${attachment.shortDescription}" escapeXml="true"/></span>
                                </td>
                                <td class="thumbnail">
                                    <c:if test="${!attachment.deleted}">
                                        <c:if test="${attachment.thumbnailed}">
                                            <a class="example-image-link" href="<c:url value="/download/task/${archive.number}/${attachment.id}?archive=true"/>" data-lightbox="example-1">
                                                <img alt="" src="${contextPath}/TSImageServlet?attId=${attachment.id}&width=100&height=75" hspace="0" vspace="0">
                                            </a>
                                        </c:if>
                                    </c:if>
                                </td>
                            </tr>
                        </c:forEach>
                    </table>
                </div>
            </div>
            <div class="strut">&nbsp;</div>
        </c:if>
        <c:if test="${!empty archive.msgs}">
            <div class="blueborder">
            <div class="caption">
                <a id="messageHistoryHeader" class="history-closed" href="javascript://nop/" onclick="openMessage(this);">
                    <c:choose>
                        <c:when test="${!sortMessageAsc}">
                            <I18n:message key="HISTORY_DESC"/>
                        </c:when>
                        <c:otherwise>
                            <I18n:message key="HISTORY_ASC"/>
                        </c:otherwise>
                    </c:choose>
                </a>
            </div>
            <div class="indent" id="messageHistory">
            <c:set value="${archive.msgs}" var="listMes"/>
            <c:forEach var="msg" items="${listMes}" varStatus="current">
                <c:choose>
                    <c:when test="${msg.time.time.time > sc.prevLogonDate.time.time && msg.time.time.time > sc.lastLogonDate.time.time}">
                        <div class="line<c:out value="${current.index mod 2}"/> grayborder hotmessage" title="<I18n:message key="HOT_MESSAGE"/>">
                    </c:when>
                    <c:when test="${msg.time.time.time > sc.prevLogonDate.time.time}">
                        <div class="line<c:out value="${current.index mod 2}"/> grayborder newmessage" title="<I18n:message key="NEW_MESSAGE"/>">
                    </c:when>
                    <c:when test="${msg.time.time.time > sc.lastLogonDate.time.time}">
                        <div class="line<c:out value="${current.index mod 2}"/> grayborder hotmessage" title="<I18n:message key="HOT_MESSAGE"/>">
                    </c:when>
                    <c:otherwise>
                        <div class="line<c:out value="${current.index mod 2}"/> grayborder">
                    </c:otherwise>
                </c:choose>
                <div class="indent">
                    <div class="msgbox-closed" id="${msg.id}">
                        <span class="msgtime">
                            <a class="internal" href="${contextPath}/task/${archive.number}?thisframe=true#${msg.id}" id="${msg.id}">
                                <div style="color:#000000">
                                    <I18n:formatDate value="${msg.time.time}" type="both" dateStyle="short" timeStyle="short"/>
                                </div>
                             </a>
                        </span>
                        <label class="messagelabel" id="label${msg.id}" style="color:#666;" for="${msg.id}">
                            <c:out value="${msg.mstatusName}" escapeXml="true"/>
                            <c:if test="${msg.resolutionId ne null}">(<c:out value="${msg.resolutionName}" escapeXml="true"/>)</c:if>
                            <c:set var="hintUser" value="${msg.submitterLogin} email:${msg.submitterEmail} ${msg.submitterTel}"/>
                            <span class="user" style="vertical-align:top;" ${msg.submitterId eq sc.userId ? "id='loggedUser'" : ""} title="<c:out value="${hintUser}" escapeXml="true"/>">
                            <c:out value="${msg.submitterName}" escapeXml="true"/>
                        </span>
                            <c:choose>
                                <c:when test="${msg.handlerUserId ne null}">
                                        <I18n:message key="FOR"/>
                                        <c:set var="hintUser" value="${msg.handlerUserLogin} email:${msg.handlerUserEmail} ${msg.handlerUserTel}"/>
                                        <span class="user" style="vertical-align:top;" ${msg.handlerUserId eq sc.userId ? "id='loggedUser'" : ""} title="<c:out value="${hintUser}" escapeXml="true"/>">
                                            <c:out value="${msg.handlerUserName}" escapeXml="true"/>
                                    </span>
                                </c:when>
                                <c:when test="${msg.handlerGroupId ne null}">
                                    <I18n:message key="FOR"/>
                                    <span class="user"><html:img  styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/ico.status.gif"/><c:out value="${msg.handlerGroupName}" escapeXml="true"/></span>
                                </c:when>
                            </c:choose>
                        </label>
                        <c:if test="${msg.priorityId ne null || msg.deadline ne null || !empty msg.budgetAsString || !empty msg.actualBudgetAsString || !empty msg.attachments}">
                            <table class="general" cellpadding="0" cellspacing="0">
                                <c:if test="${msg.priorityId ne null}">
                                    <tr>
                                        <th width="20%"><I18n:message key="MESSAGE_PRIORITY"/></th>
                                        <td width="80%"><c:out value="${msg.priorityName}" escapeXml="true" /></td>
                                    </tr>
                                </c:if>
                                <c:if test="${msg.deadline ne null}">
                                    <tr>
                                        <th width="20%"><I18n:message key="MESSAGE_DEADLINE"/></th>
                                        <td width="80%"><I18n:formatDate value="${msg.deadline.time}" type="both" dateStyle="short" timeStyle="short"/></td>
                                    </tr>
                                </c:if>

                                <c:if test="${!empty msg.budgetAsString}">
                                    <tr>
                                        <th width="20%"><I18n:message key="BUDGET"/></th>
                                        <td width="80%"><c:out value="${msg.budgetAsString}" escapeXml="true" /></td>
                                    </tr>
                                </c:if>

                                <c:if test="${!empty msg.actualBudgetAsString}">
                                    <tr>
                                        <th width="20%"><I18n:message key="MESSAGE_ABUDGET"/></th>
                                        <td width="80%"><c:out value="${msg.actualBudgetAsString}" escapeXml="true" /></td>
                                    </tr>
                                </c:if>

                                <c:if test="${!empty msg.attachments}">
                                    <c:if test="${attachmentsMsg[msg.id] != null}">
                                    <tr class="attach-msg">
                                        <th width="20%"><I18n:message key="ATTACHMENT"/></th>
                                        <td width="80%">
                                    <c:forEach items="${attachmentsMsg[msg.id]}" var="ata">
                                                    <c:if test="${!ata.deleted}">
                                                        <c:choose>
                                                            <c:when test="${ata.thumbnailed}">
                                                                <a class="internal" href="<c:url value="/download/task/${archive.number}/${ata.id}?archive=true"/>?type=image" data-lightbox="example-1">
                                                                    <img alt="" src="${contextPath}/TSImageServlet?attId=${ata.id}&width=100&height=75" hspace="0" vspace="0">
                                                                </a>
                                                                <a class="internal" target="_blank" href="${contextPath}/download/task/${archive.number}/${ata.id}?archive=true" title="<c:out value="${ata.description}"/>">
                                                                    <html:img src="${contextPath}${ImageServlet}/cssimages/ico.attachment2.gif" hspace="4" vspace="0" border="0" align="middle" altKey="ATTACHMENT"/><c:out value="${ata.name}"/>
                                                                </a>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <a class="internal" target="_blank" href="${contextPath}/download/task/${archive.number}/${ata.id}?archive=true" title="<c:out value="${ata.description}"/>">
                                                                    <html:img src="${contextPath}${ImageServlet}/cssimages/ico.attachment2.gif" hspace="4" vspace="0" border="0" align="middle" altKey="ATTACHMENT"/><c:out value="${ata.name}"/>
                                                                </a>
                                                            </c:otherwise>
                                                        </c:choose>
                                                        <br>
                                                    </c:if>
                                                    <c:if test="${ata.deleted}">
                                                        <html:img src="${contextPath}${ImageServlet}/cssimages/ico.attachment2.gif" hspace="4" vspace="0" border="0" align="middle" altKey="ATTACHMENT"/><img border="0" hspace="0" vspace="0" title="<I18n:message key="ATTACHMENT_DELETED"/>" src="${contextPath}${ImageServlet}/cssimages/warning.gif"/><c:out value="${ata.name}"/>
                                                    </c:if>
                                                </c:forEach>
                                            </c:if>
                                        </td>
                                    </tr>
                                </c:if>
                            </table>
                        </c:if>
                        <div class="description">
                            <ts:htmlfilter session="${sc.id}" macros="true" audit="${msg.mstatusName == '*'}" request="<%=request%>"><c:out value="${msg.description}" escapeXml="false"/></ts:htmlfilter>
                        </div>
                    </div>
                </div>
                </div>
            </c:forEach>
            </div>
            </div>
        </c:if>
    </tiles:put>
</tiles:insert>
