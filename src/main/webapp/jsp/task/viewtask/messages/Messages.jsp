<%@ page buffer="128kb" errorPage="/jsp/Error.jsp" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value="${sc.timezone}"/>
<I18n:setBundle basename="language"/>
<script type="text/javascript">
    function getSelectionHtml() {
        var html = "";
        if (typeof window.getSelection != "undefined") {
            var sel = window.getSelection();
            if (sel.rangeCount) {
                var container = document.createElement("div");
                for (var i = 0, len = sel.rangeCount; i < len; ++i) {
                    container.appendChild(sel.getRangeAt(i).cloneContents());
                }
                html = container.innerHTML;
            }
        } else if (typeof document.selection != "undefined") {
            if (document.selection.type == "Text") {
                html = document.selection.createRange().htmlText;
            }
        }
        document.getElementById("selectedTextId").value = html;
        document.getElementById("formMethod").value = "createTaskByOperation";
    }
</script>

<c:if test="${!empty listMessages}">
    <c:set var="messages" value="${listMessages}" scope="session"/>

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

        <html:form method="post" action="/MessageCreateAction" styleId="messageCreateActionForm"
                   onsubmit="if (document.getElementById('formMstatus') == 'delete') return deleteMessages(); else return true;">
            <html:hidden property="method" value="delete" styleId="formMethod"/>
            <html:hidden property="selectedText" styleId="selectedTextId"/>
            <html:hidden property="mstatus" styleId="formMstatus"/>
            <html:hidden property="id" value="${id}"/>
            <html:hidden property="session" value="${session}"/>
            <c:if test="${isMstatuses}">
                <div class="controlPanel">
                    <script type="text/javascript">
                        var mstatusTopMenu = {};
                    </script>
                    <c:choose>
                        <c:when test="${sortMessageAsc}">
                            <html:link titleKey="SORT_MESSAGE_DESC"
                                       href="${contextPath}/MessageAction.do?method=changesort&id=${id}&session=${session}"><html:img
                                    border="0" src="${contextPath}${ImageServlet}/cssimages/ico.down.gif"/><I18n:message
                                    key="SORT_MESSAGE_DESC"/></html:link>
                        </c:when>
                        <c:otherwise>
                            <html:link titleKey="SORT_MESSAGE_ASC"
                                       href="${contextPath}/MessageAction.do?method=changesort&id=${id}&session=${session}"><html:img
                                    border="0" src="${contextPath}${ImageServlet}/cssimages/ico.up.gif"/><I18n:message
                                    key="SORT_MESSAGE_ASC"/></html:link>
                        </c:otherwise>
                    </c:choose>
                    <c:forEach items="${mstatuses}" var="mstatus">
                        <c:choose>
                            <c:when test="${fn:indexOf(mstatus.action,'/')>0}">
                                <c:set var="menuGroup" value="${fn:substringBefore(mstatus.action,'/')}"/>
                                <script type="text/javascript">
                                    if (!mstatusTopMenu['${menuGroup}']) {
                                        mstatusTopMenu['${menuGroup}'] = new TSMenu();
                                        mstatusTopMenu['${menuGroup}'].width = 320;
                                    }
                                    mstatusTopMenu['${menuGroup}'].add(new TSMenuItem("<c:out value="${fn:substringAfter(mstatus.action,'/')}"/>", "javascript:document.getElementById('formMethod').value='page'; document.getElementById('formMstatus').value='${mstatus.id}'; document.getElementById('messageCreateActionForm').submit();", false, false, "<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.messagetypes.gif", "", ""));
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
                                            if (!mstatusTopMenu['<I18n:message key="OTHER_ACTIONS"/>']) {
                                                mstatusTopMenu['<I18n:message key="OTHER_ACTIONS"/>'] = new TSMenu();
                                                mstatusTopMenu['<I18n:message key="OTHER_ACTIONS"/>'].width = 320;
                                            }
                                            mstatusTopMenu['<I18n:message key="OTHER_ACTIONS"/>'].add(new TSMenuItem(
                                                "<c:out value="${mstatus.action}"/>",
                                                "javascript:document.getElementById('formMethod').value='page'; document.getElementById('formMstatus').value='${mstatus.id}'; document.getElementById('messageCreateActionForm').submit();",
                                                false,
                                                false,
                                                "<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.messagetypes.gif",
                                                "",
                                                "<c:out value="${mstatus.description}"/>"));
                                        </script>
                                    </c:otherwise>
                                </c:choose>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                    <c:if test="${!empty mstatuses}">
    <span class="additional">
    <script type="text/javascript">
        var mstatusTopMenuBar = new TSMenuBar();
        for (var h in mstatusTopMenu) {
            mstatusTopMenuBar.add(new TSMenuBut(h, null, mstatusTopMenu[h], "${contextPath}${ImageServlet}/cssimages/ico.messagetypes.gif", "", ""));
        }
        document.write(mstatusTopMenuBar);
    </script>
    </span>
                    </c:if>
                </div>
            </c:if>
            <div class="indent" id="messageHistory">
                <c:import url="/jsp/task/viewtask/messages/MessagesTile.jsp"/>
                <c:remove var="messages" scope="session"/>
                <div class="controls">
                    <c:if test="${canCreateTaskByOperation}">
                        <input type="submit" class="iconized secondary link" onclick="getSelectionHtml();" value="<I18n:message key="CREATE_TASK_BY_OPERATION"/>" name="create_task_by_operation">
                    </c:if>
                    <c:if test="${canDeleteMessages}">
                        <input type="submit" class="iconized secondary link" value="<I18n:message key="DELETE_MESSAGES"/>"
                               name="DELETE">
                    </c:if>
                </div>
            </div>
            <c:if test="${isMstatuses}">
                <div class="controlPanel">
                    <script type="text/javascript">
                        var mstatusBottomMenu = {};
                    </script>
                    <c:forEach items="${mstatuses}" var="mstatus">
                        <c:choose>
                            <c:when test="${fn:indexOf(mstatus.action,'/')>0}">
                                <c:set var="menuGroup" value="${fn:substringBefore(mstatus.action,'/')}"/>
                                <script type="text/javascript">
                                    if (!mstatusBottomMenu['${menuGroup}']) {
                                        mstatusBottomMenu['${menuGroup}'] = new TSMenu();
                                        mstatusBottomMenu['${menuGroup}'].width = 320;
                                    }
                                    mstatusBottomMenu['${menuGroup}'].add(new TSMenuItem("<c:out value="${fn:substringAfter(mstatus.action,'/')}"/>", "javascript:document.getElementById('formMethod').value='page'; document.getElementById('formMstatus').value='${mstatus.id}'; document.getElementById('messageCreateActionForm').submit();", false, false, "<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.messagetypes.gif", "", ""));
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
                                            if (!mstatusBottomMenu['<I18n:message key="OTHER_ACTIONS"/>']) {
                                                mstatusBottomMenu['<I18n:message key="OTHER_ACTIONS"/>'] = new TSMenu();
                                                mstatusBottomMenu['<I18n:message key="OTHER_ACTIONS"/>'].width = 320;
                                            }
                                            mstatusBottomMenu['<I18n:message key="OTHER_ACTIONS"/>'].add(new TSMenuItem("<c:out value="${mstatus.action}"/>", "javascript:document.getElementById('formMethod').value='page'; document.getElementById('formMstatus').value='${mstatus.id}'; document.getElementById('messageCreateActionForm').submit();", false, false, "<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.messagetypes.gif", "", "${mstatus.description}"));
                                        </script>
                                    </c:otherwise>
                                </c:choose>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                    <c:if test="${!empty mstatuses}">
    <span class="additional">
    <script type="text/javascript">
        var mstatusBottomMenuBar = new TSMenuBar();
        for (var h in mstatusBottomMenu) {
            mstatusBottomMenuBar.add(new TSMenuBut(h, null, mstatusBottomMenu[h], "${contextPath}${ImageServlet}/cssimages/ico.messagetypes.gif", "", ""));
        }
        document.write(mstatusBottomMenuBar);
    </script>
    </span>
                    </c:if>
                </div>
            </c:if>
        </html:form>

        <script type="text/javascript">
            setParentClosedOpenedForMessageHistory();
//            openMessage($('messageHistoryHeader'));
            if (parseInt(${listMessages.size()}) === 1) {
                document.getElementById('label${listMessages.get(0).getId()}').className = 'msgbox-opened';
                document.getElementById('${listMessages.get(0).getId()}').className = 'msgbox-opened';;
            }
        </script>
    </div>
</c:if>