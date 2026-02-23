<%@ page buffer="128kb" errorPage="/jsp/Error.jsp"%>
<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/tiles-el" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value="${sc.timezone}"/>
<I18n:setBundle basename="language"/>

<%
    String ua = request.getHeader( "User-Agent" );
    boolean isFirefox = ( ua != null && ua.indexOf( "Firefox/" ) != -1 );
    boolean isMSIE = ( ua != null && ua.indexOf( "MSIE" ) != -1 );
    boolean isSafari = (ua != null && ua.indexOf( "Safari" ) != -1 && ua.indexOf( "Chrome" ) == -1);
    response.setHeader( "Vary", "User-Agent" );
%>
<c:if test="${!empty attachments}">
    <div class="blueborder ts-task-attachments" id="divAttachment">
        <c:if test="${canCreateTaskAttachments && canEditTask}">
            <div class="controlPanel ts-attachment-toolbar">
                <html:link  href="${contextPath}/AttachmentEditAction.do?method=attachToTask&amp;id=${id}"><html:img  src="${contextPath}${ImageServlet}/cssimages/ico.attachment.png" border="0" altKey="FILE_ADD"/><I18n:message key="FILE_ADD"/></html:link>
                <span class="ts-attachment-toolbar__separator" aria-hidden="true"></span>
                <span class="ts-attachment-toolbar__search">
                    <input class="form-autocomplete ts-attachment-filter"
                           name="filter"
                           size="30"
                           placeholder="<I18n:message key="SEARCH"/>"
                           onkeyup="search_attachment(this, 'table_attachment')"
                           type="text">
                </span>
            </div>
        </c:if>
        <script type="text/javascript">
            var countAtt = '${attaNum}';
            function deleteAttachment(element) {
                if (document.getElementById('method').value == "creataArchiveTaskUpload") {
                    var col = element.elements;
                    var count = 0;
                    for (var i = 0; i < col.length; i++) {
                        if (col[i].type == "checkbox" && col[i].checked) {
                            count++;
                        }
                    }
                    if (count > 0) {
                        return true;
                    } else {
                        alert("<I18n:message key="ERROR_SELECT_DOWNLOAD"/>");
                        return false;
                    }
                } else {

                    return deleteConfirm("<I18n:message key="DELETE_ATTACHMENTS_REQ"/>", "uploadForm");
                }
            }

            function setMethod(target) {
                document.getElementById('method').value = target;
            }
        </script>

        <div class="indent" id="attachDiv">
            <html:form method="POST" action="/AttachmentViewAction.do?taskId=${tci.id}" onsubmit="return deleteAttachment(this);">
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
                        <th width="5%"><I18n:message key="DELETE"/></th>
                    </tr>
                    <c:forEach var="attachment" items="${attachments}" varStatus="varCounter">
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
                                                    <c:if test="${attachment.canManage}">
                                                        <html:link styleClass="internal" href="${contextPath}/AttachmentEditAction.do?method=page&amp;attachmentId=${attachment.id}&amp;taskId=${id}">
                                                            <img alt="<I18n:message key="OBJECT_PROPERTIES_EDIT"/>" title="<I18n:message key="OBJECT_PROPERTIES_EDIT"/>" border="0" hspace="0" vspace="0" src="${contextPath}${ImageServlet}/cssimages/ico.edit.gif"/>
                                                        </html:link>
                                                    </c:if>
                                                    <html:img src="${contextPath}${ImageServlet}/cssimages/ico.zip.gif"/>
                                                    <a class="internal" target="blank" href="<c:url value="/download/task/${attachment.task.number}/${attachment.id}"/>"><c:out value="${attachment.shortName}" escapeXml="true"/></a></th></tr>
                                            <c:forEach var="zipped" items="${attachment.zippedFiles}" varStatus="nfile">
                                                <tr>
                                                    <td  style="white-space: nowrap;">
                                                        <a class="internal" target="blank" href="<c:url value="/download/task/${attachment.task.number}/zipped/${attachment.id}/${zipped.nameMD5}/${zipped.name}"/>"><c:out value="${zipped.name}" escapeXml="true"/></a>
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
                                                <c:if test="${attachment.canManage}">
                                                    <html:link styleClass="internal" href="${contextPath}/AttachmentEditAction.do?method=page&amp;attachmentId=${attachment.id}&amp;taskId=${id}">
                                                        <img alt="<I18n:message key="OBJECT_PROPERTIES_EDIT"/>" title="<I18n:message key="OBJECT_PROPERTIES_EDIT"/>" border="0" hspace="0" vspace="0" src="${contextPath}${ImageServlet}/cssimages/ico.edit.gif"/>
                                                    </html:link>
                                                </c:if>
                                                <a class="internal" target="blank" href="<c:url value="/download/task/${attachment.task.number}/${attachment.id}"/>"><c:out value="${attachment.shortName}" escapeXml="true"/></a>
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
                                <span title="${fn:escapeXml(attachment.description)}"><c:out value="${attachment.shortDescription}" escapeXml="true"/></span>
                            </td>
                            <td class="thumbnail">
                                <c:if test="${!attachment.deleted}">
                                    <c:if test="${attachment.thumbnailed}">
	                                    <a class="example-image-link" href="<c:url value="/download/task/${attachment.task.number}/${attachment.id}"/>" data-lightbox="example-1">
		                                    <img alt="" src="${contextPath}/TSImageServlet?attId=${attachment.id}&width=100&height=75" hspace="0" vspace="0">
	                                    </a>
                                    </c:if>
                                </c:if>
                            </td>
                            <td style="text-align:center;">
                                <c:if test="${deleteAttach[attachment.id]}">
                                    <div style="cursor:pointer;" onclick="deleteCheck('${tci.id}', '${attachment.id}', 'deleteTaskUpload', '<I18n:message key='DELETE_ATTACHMENTS_REQ'/>', true, 'attachDiv');">
                                        <img alt="<I18n:message key="DELETE"/>" title="<I18n:message key="DELETE"/>" border="0" hspace="0" vspace="0" src="${contextPath}${ImageServlet}/cssimages/delete.png"/>
                                    </div>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                </table>
                <div class="controls">
                    <table width="100%;">
                        <tr>
                            <td style="text-align:left;">
                                <input type="submit" id="downloadBn" onclick="setMethod('creataArchiveTaskUpload');" class="iconized" value="<I18n:message key="DOWNLOAD"/>" name="DOWNLOAD" disabled="true">
                            </td>
                        </tr>
                    </table>
                </div>
            </html:form>
        </div>
    </div>
</c:if>
<script type="text/javascript">
    showButton(false);
    function showButton(result) {
        var downloadButton = document.getElementById("downloadBn");
        if (downloadButton) {
            downloadButton.disabled = result;
        }
        var deleteButtom = document.getElementById("deleteBn");
        if (deleteButtom) {
            deleteButtom.disabled = result;
        }
    }
</script>
