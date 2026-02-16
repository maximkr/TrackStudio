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
        <script type="text/javascript">
            var resourceId = "${tci.id}";
            var number = "${tci.number}";
            var taskUpload = true;

            function deleteAttachs(attachId) {
                TSDialog.confirm('<I18n:message key="DELETE_ATTACHMENTS_REQ"/>', function(ok) {
                    if (ok) {
                        document.location.href = '${contextPath}/AttachmentViewAction.do?taskId=${tci.id}&method=deleteTaskUpload&delete=' + attachId;
                    }
                });
            }

            function showBookmarkDialog(bookmarkName, taskId) {
                document.getElementById('bookmark_name').value = bookmarkName;

                document.getElementById('task_id').value = taskId;
                YAHOO.trackstudio.bookmark.bookmark_dialog.show();
            }

            function showBookmarkDialogSimple() {
                showBookmarkDialog('<c:out value="${tci.name}" escapeXml="false"/>', '<c:out value="${tci.id}"/>');
            }

            function showViewDialog() {
                YAHOO.trackstudio.bookmark.view_dialog.show();
            }
            setTimer('${id}');
            getTimer();
            if (runClockWhenOpenTask == 'false') {
                pauseTimer('${id}');
            }
        </script>
        <c:set var="urlHtml" value="html"/>
        <ts:js request="${request}" response="${response}">
            <ts:jsLink link="${urlHtml}/dnd/dnd.js"/>
        </ts:js>
        <c:if test="${canView}">
            <div class="caption" style="border-color:#FFFFFF; padding-left: 20px; font-size: 20px; background-color:#FFFFFF">
                <html:link href="javascript:showBookmarkDialogSimple();" style="float:right;padding-top:6px;padding-right:5px;">
                    <html:img src="${contextPath}${ImageServlet}/cssimages/ico.star.gif" border="0"/>
                </html:link>
                <html:link href="javascript:window.print();" style="float:right;padding-top:6px;padding-right:5px;">
                    <html:img src="${contextPath}${ImageServlet}/cssimages/ico.print.png" border="0"/>
                </html:link>
                <c:out value="${tci.name}"/>&nbsp;<html:link href="${contextPath}/task/${tci.number}?thisframe=true" title="#${tci.number}">[#${tci.number}]</html:link>
                <div style="float:right; padding-right:10px; display: inline;">
                    <c:if test="${showView}"><a href="#" style=" text-decoration: none;" onclick="javascript:showViewDialog();"><I18n:message key="VIEWS"/></a></c:if>
                    <c:if test="${canEditTask}">
                        <input type="button" onclick="document.location = '${contextPath}/TaskEditAction.do?method=page&id=${id}&asView=document';" value="<I18n:message key="EDIT"/>">
                    </c:if>
                </div>
            </div>
            <div>
                <span class="user" style="padding-left:20px;" ${tci.submitterId eq sc.userId ? "id='loggedUser'" : ""}>
                    <html:img styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/${tci.submitter.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
                    <c:out value="${tci.submitter.name}" escapeXml="true"/>,&nbsp;
	            </span><I18n:formatDate value="${tci.updatedate.time}" type="both" dateStyle="short" timeStyle="short"/>
                <c:if test="${isHistory}">
                    &nbsp;<html:link href="${contextPath}/MessageDocumentAction.do?method=page&id=${tci.id}"><I18n:message key="HISTORY"/></html:link><br>
                </c:if>
                <div style="padding-left:20px;padding-top:20px;">
                    <ts:htmlfilter session="${sc.id}" macros="true" audit="false" request="<%=request%>">
                        <c:out value="${tci.description}" escapeXml="false"/>
                    </ts:htmlfilter><br>
                </div>
                <c:if test="${!empty attachments}">
                    <table class="general" style="background-color:#FFFFFF;border-color:#FFFFFF;">
                        <tr>
                            <td>
                                <c:forEach items="${attachments}" var="ata">
                                    <span style="border: 1px solid; padding: 8px 5px 8px 5px; margin: 5px; display:inline-block" title="<I18n:message key="DOWNLOAD"/>">
                                        <c:if test="${!ata.deleted}">
                                            <a class="internal" target="_blank" href="<c:url value="/download/task/${tci.number}/${ata.id}/${ata.name}"/>" title="<c:out value="${ata.description}"/>">
                                                <html:img src="${contextPath}${ImageServlet}/cssimages/download_file.png" width="18px" hspace="4" vspace="0" border="0" align="middle" altKey="ATTACHMENT"/><c:out value="${ata.name}"/>
                                            </a>
                                        </c:if>
                                        <c:if test="${deleteAttach[ata.id]}">
                                            <html:link href="#" onclick="deleteAttachs('${ata.id}');">
                                                <img alt="<I18n:message key="DELETE"/>" title="<I18n:message key="DELETE"/>" border="0" hspace="0" vspace="0" src="${contextPath}${ImageServlet}/cssimages/delete.png"/>
                                            </html:link>
                                        </c:if>
                                        <c:if test="${ata.deleted}">
                                            <html:img src="${contextPath}${ImageServlet}/cssimages/ico.attachment2.gif" hspace="4" vspace="0" border="0" altKey="ATTACHMENT"/><img border="0" hspace="0" vspace="0" title="<I18n:message key="ATTACHMENT_DELETED"/>" src="${contextPath}${ImageServlet}/cssimages/warning.gif"/><c:out value="${ata.name}"/>
                                        </c:if>
                                    </span>
                                </c:forEach>
                            </td>
                        </tr>
                    </table>
                </c:if>
                <c:if test="${!empty documents}">
                    <style type="text/css">
                        .book {
                            background-color: #DDD;
                            border-radius: 15px 15px;
                            margin-right: 20px;
                            margin-top: 10px;
                            padding: 20px;
                        }
                    </style>
                    <div class="book">
                        <c:forEach items="${documents}" var="doc">
                            <html:link styleClass="internal" href="${contextPath}/task/${doc.number}?thisframe=true" title="#${doc.number}">
                                <html:img border="0" style="vertical-align:bottom;" src="${contextPath}${ImageServlet}/icons/categories/${doc.category.icon}"/>
                                <c:out value="${doc.name}" escapeXml="true"/>&nbsp;<em class="number">[<c:if test="${!empty doc.shortname}"><c:out value="${tci.shortname}" escapeXml="true"/> </c:if>#<c:out value="${doc.number}" escapeXml="true"/>]</em>
                            </html:link><br>
                        </c:forEach>
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
                                <td><c:out value="${tci.name}"/></td>
                            </tr>
                        </table>
                        <input type="hidden" value="${taskId}" id="task_id"/>
                    </form>
                </div>
            </div>

            <div id="view_dialog" style="visibility:hidden;">
                <div class="hd"><I18n:message key="VIEW"/></div>
                <div class="bd">
                    <a href="${contextPath}/task/${tci.number}?thisframe=true&asView=task"><I18n:message key="TASK"/></a>
                    <a href="#" style=" text-decoration: none;"><I18n:message key="DOCUMENT"/></a>
                    <a href="${contextPath}/task/${tci.number}?thisframe=true&asView=container"><I18n:message key="CONTAINER"/></a>
                </div>
            </div>
        </c:if>
    </tiles:put>
</tiles:insert>