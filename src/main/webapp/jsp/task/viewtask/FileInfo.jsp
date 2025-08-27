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
<script type="text/javascript">
    var resourceId = "${tci.id}";
    var number = "${tci.number}";
    var taskUpload = true;

    function deleteAttachs(attachId) {
        var result = confirm('<I18n:message key="DELETE_ATTACHMENTS_REQ"/>');
        if (result) {
            document.location.href = '${contextPath}/AttachmentViewAction.do?taskId=${tci.id}&method=deleteTaskUpload&delete=' + attachId;
        }
    }

    function showBookmarkDialog(bookmarkName, taskId) {
        document.getElementById('bookmark_name').value = bookmarkName;

        document.getElementById('task_id').value = taskId;
        YAHOO.trackstudio.bookmark.bookmark_dialog.show();
    }

    function showViewDialog() {
        YAHOO.trackstudio.bookmark.view_dialog.show();
    }

    function showBookmarkDialogSimple() {
        showBookmarkDialog('<c:out value="${tci.name}" escapeXml="false"/>', '<c:out value="${tci.id}"/>');
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
<div>
    <div class="caption" style="border-color:#FFFFFF; padding-left:20px; font-size: 20px; background-color:#FFFFFF">
        <html:link href="javascript:showBookmarkDialogSimple();" style="float:right;padding-top:6px;padding-right:5px;">
            <html:img src="${contextPath}${ImageServlet}/cssimages/ico.star.gif" border="0"/>
        </html:link>
        <c:out value="${tci.name}"/>
                    <span style="float:right; padding-right:10px;">
                         <c:if test="${showView}"><a href="#" style=" text-decoration: none;" onclick="javascript:showViewDialog();"><I18n:message key="VIEWS"/></a></c:if>

                         <c:if test="${canCreateTaskAttachments && canEditTask}">
                             <html:link href="${contextPath}/AttachmentEditAction.do?method=attachToTask&id=${id}">
                                 <html:img src="${contextPath}${ImageServlet}/cssimages/ico.attachment.png" border="0"/>
                                 <I18n:message key="FILE_ADD"/>
                             </html:link>
                             <%--<html:link styleClass="internal" href="javascript:{}" onclick="window.open('${contextPath}/UploadAppletAction.do?method=page&amp;id=${id}','uplWin','dependent=yes,menubar=no,toolbar=no,status=no,scrollbars=no,titlebar=no,left=0,top=20,width=845,height=445,resizable=no');">--%>
                                 <%--<html:img src="${contextPath}${ImageServlet}/cssimages/ico.attachment.png" border="0"/>--%>
                                 <%--<I18n:message key="UPLOAD_MANAGER"/>--%>
                             <%--</html:link>--%>
                         </c:if>
                        <c:if test="${canEditTask}">
                            <input type="button" onclick="document.location = '${contextPath}/TaskEditAction.do?method=page&id=${id}';" value="<I18n:message key="EDIT"/>">
                        </c:if>
                    </span>
    </div>
                <span class="user" style="padding-left:20px;" ${tci.submitterId eq sc.userId ? "id='loggedUser'" : ""}>
                                    <html:img styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/${tci.submitter.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
                                    <c:out value="${tci.submitter.name}" escapeXml="true"/>,&nbsp;
                                </span><I18n:formatDate value="${tci.updatedate.time}" type="both" dateStyle="short" timeStyle="short"/>
    &nbsp;<html:link href="${contextPath}/task/${tci.number}?thisframe=true" title="#${tci.number}">[#${tci.number}]</html:link><br>
    <c:if test="${!empty containers}">
        <div style="padding-left:30px;padding-top:10px;padding-bottom:10px;">
            <c:forEach items="${containers}" var="container">
                <div>
                    <html:link styleClass="internal" href="${contextPath}/task/${container.number}?thisframe=true" title="#${container.number}">
                        <html:img border="0" style="vertical-align:bottom;" src="${contextPath}${ImageServlet}/cssimages/box.png"/>
                        <span style="font-size:16px;vertical-align:middle;"><c:out value="${container.name}" escapeXml="true"/>&nbsp;<em class="number">[<c:if test="${!empty container.shortname}"><c:out value="${container.shortname}" escapeXml="true"/> </c:if>#<c:out value="${container.number}" escapeXml="true"/>]</em></span>
                    </html:link>
                </div><br>
            </c:forEach>
        </div>
    </c:if>
    <c:if test="${!empty attachments}">
        <style type="text/css">
            .content-in-center {
                display: table-cell;
                vertical-align: middle;
                text-align:center;
                width:50px;
                height:50px;
                /*border:1px #a9a9a9 solid;*/
            }

            .container-internal {
                color: #000000;
                font-size: 10px;
                text-decoration: none;
                font-family: Verdana, Sans, sans-serif;
            }
        </style>
        <table style="border:1px black; margin-left:10px;">
            <c:forEach items="${attachmentMatrix}" var="attach">
                <tr>
                    <c:forEach items="${attach}" var="ata">
                        <td style="padding-left:5px;padding-right:5px;">
                            <table class="general" style="background-color:#FFFFFF;border-color:#FFFFFF;">
                                <tr>
                                    <td colspan="3">
                                        <div class="content-in-center">
                                            <c:choose>
                                                <c:when test="${ata.movie}">
                                                    <div style="border: 1px #a9a9a9 solid; width:200px;height:200px;">
                                                        <embed src="/download/task/${ata.task.number}/${ata.id}/${ata.name}"
                                                               width="50"
                                                               height="50"
                                                               allowscriptaccess="always"
                                                               allowfullscreen="false"/>
                                                    </div>
                                                </c:when>
                                                <c:otherwise>
                                                    <c:choose>
                                                        <c:when test="${ata.thumbnailed}">
	                                                        <a class="internal" href="/download/user/${ata.task.number}/${ata.id}?type=image" data-lightbox="example-1">
		                                                        <img alt="" src="${contextPath}/TSImageServlet?attId=${ata.id}&width=100&height=75" hspace="0" vspace="0">
	                                                        </a>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <c:choose>
                                                                <c:when test="${ata.txt}">
                                                                    <img style="border: 0px #a9a9a9 solid;" src="${contextPath}${ImageServlet}/cssimages/txt.png" height="50" width="50" hspace="0" vspace="0">
                                                                </c:when>
                                                                <c:when test="${ata.word}">
                                                                    <img style="border: 0px #a9a9a9 solid;" src="${contextPath}${ImageServlet}/cssimages/word.png" height="50" width="50" hspace="0" vspace="0">
                                                                </c:when>
                                                                <c:when test="${ata.excel}">
                                                                    <img style="border: 0px #a9a9a9 solid;" src="${contextPath}${ImageServlet}/cssimages/excel.png" height="50" width="50" hspace="0" vspace="0">
                                                                </c:when>
                                                                <c:when test="${ata.pdf}">
                                                                    <img style="border: 0px #a9a9a9 solid;" src="${contextPath}${ImageServlet}/cssimages/pdf.png" height="50" width="50" hspace="0" vspace="0">
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <img style="border: 0px #a9a9a9 solid;" src="${contextPath}${ImageServlet}/cssimages/no_image.jpg" height="50" width="50" hspace="0" vspace="0">
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </td>
                                    <td style="text-align:left;">
                                                    <span class="internal" title="<c:out value="${ata.name}" escapeXml="true"/>">
                                                        <html:img src="${contextPath}${ImageServlet}/cssimages/ico.attachment2.gif" hspace="4" vspace="0" border="0" align="middle" altKey="ATTACHMENT"/><c:out value="${ata.shortname}"/>&nbsp;[<c:out value="${ata.size}"/>b]
                                                    </span><br/>
                                        <c:if test="${!ata.deleted}">
                                            <a class="container-internal" target="_blank" href="<c:url value="/download/task/${tci.number}/${ata.id}"/>" title="<c:out value="${ata.name}" escapeXml="true"/>">
                                                <I18n:message key="DOWNLOAD_FILE"/>
                                            </a>
                                        </c:if>
                                        <c:if test="${deleteAttach[ata.id]}">
                                            <html:link styleClass="container-internal" href="#" onclick="deleteAttachs('${ata.id}');">
                                                <I18n:message key="DELETE"/>
                                            </html:link>
                                        </c:if>
                                        <c:if test="${ata.deleted}">

                                            <html:img src="${contextPath}${ImageServlet}/cssimages/ico.attachment2.gif" hspace="4" vspace="0" border="0" altKey="ATTACHMENT"/><img border="0" hspace="0" vspace="0" title="<I18n:message key="ATTACHMENT_DELETED"/>" src="${contextPath}${ImageServlet}/cssimages/warning.gif"/><c:out value="${ata.name}"/>
                                        </c:if>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </c:forEach>
                </tr>
            </c:forEach>
        </table>
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
            <input type="hidden" value="'${taskId}'" id="task_id"/>
        </form>
    </div>
</div>

<div id="view_dialog" style="visibility:hidden;">
    <div class="hd"><I18n:message key="VIEW"/></div>
    <div class="bd">
        <a href="${contextPath}/task/${tci.number}?thisframe=true&asView=task"><I18n:message key="TASK"/></a>
        <a href="${contextPath}/task/${tci.number}?thisframe=true&asView=document"><I18n:message key="DOCUMENT"/></a>
        <a href="#" style=" text-decoration: none;"><I18n:message key="CONTAINER"/></a>
    </div>
</div>
</c:if>
</tiles:put>
</tiles:insert>