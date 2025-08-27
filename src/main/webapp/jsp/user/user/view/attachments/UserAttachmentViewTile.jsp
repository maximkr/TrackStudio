<%@ page buffer="128kb" errorPage="/jsp/Error.jsp" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/tiles-el" prefix="tiles" %>
<I18n:setLocale value='${sc.locale}'/>
<I18n:setTimeZone value='${sc.timezone}'/>
<I18n:setBundle basename='language'/>
<%
    String ua = request.getHeader( "User-Agent" );
    boolean isFirefox = ( ua != null && ua.indexOf( "Firefox/" ) != -1 );
    boolean isMSIE = ( ua != null && ua.indexOf( "MSIE" ) != -1 );
    boolean isSafari = (ua != null && ua.indexOf( "Safari" ) != -1 && ua.indexOf( "Chrome" ) == -1);
    response.setHeader( "Vary", "User-Agent" );
%>
<c:if test="${!empty attachments}">
<div class="blueborder">
<div class="caption">
    <I18n:message key="ATTACHMENTS"/>
</div>
<c:if test="${canCreateUserAttachments}">
    <div class="controlPanel">
        <html:link  href="${contextPath}/AttachmentEditAction.do?method=attachToUser&id=${id}">
            <html:img src="${contextPath}${ImageServlet}/cssimages/ico.attachment.png" border="0"/>
            <I18n:message key="FILE_ADD"/>
        </html:link>
        <html:link  href="javascript:{}" onclick="window.open('${contextPath}/UploadAppletAction.do?method=page&amp;user=true&amp;id=${id}',
                 'uplWin','dependent=yes,menubar=no,toolbar=no,status=no,scrollbars=no,titlebar=no,left=0,top=20,width=845,height=445,resizable=no');">
            <html:img src="${contextPath}${ImageServlet}/cssimages/ico.attachment.png" border="0" alt="Attachment"/>
            <I18n:message key="UPLOAD_MANAGER"/>
        </html:link>
        <div style="float:right;">
            <input style="height:11px;vertical-align:middle; background-color:#FFFFFF; border:1px solid #B2C9D9; cursor:text;" class="form-autocomplete" name="filter" size="30" onkeyup="search_attachment(this, 'table_attachment', [1])" type="text">
        </div>
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
    <html:form method="POST" action="/AttachmentViewAction.do?userId=${user.id}" onsubmit="return deleteAttachment(this);">
        <html:hidden property="method" styleId="method"/>
        <html:hidden property="session" value="${session}"/>
        <html:hidden property="id" value="${id}"/>
        <table class="general sortable" id="table_attachment" cellpadding="0" cellspacing="0">

            <tr class="wide">
                <c:if test="${canManageUserAttachments}">
                    <th width='1%' nowrap style="white-space:nowrap" class="nosort">
                        <input type="checkbox" onClick="selectAllCheckboxes(this, 'delete1')">
                    </th>
                </c:if>
                <th width='25%'>
                    <I18n:message key="FILE"/>
                </th>
                <th width="5%"><I18n:message key="WEBDAV"/></th>
                <th>
                    <I18n:message key="FILE_SIZE"/>
                </th>
                <th width='15%'>
                    <I18n:message key="LAST_MODIFIED"/>
                </th>
                <th width='45%'>
                    <c:if test="${isDescription}">
                        <I18n:message key="DESCRIPTION"/>
                    </c:if>
                </th>
                <th width='5%'>
                    <c:if test="${isPreview}">
                        <I18n:message key="THUMBNAIL"/>
                    </c:if>
                </th>
                <th width='5%'>
                    <I18n:message key="DELETE"/>
                </th>
            </tr>
            <c:forEach var="attachment" items="${attachments}" varStatus="varCounter">
                <tr class="line<c:out value="${varCounter.index mod 2}"/>" id="${attachment.id}">
                    <td>
                        <input type="checkbox" name="delete" alt="delete1" quickCheckboxSelectGroup="delete1" value="<c:out value="${attachment.id}"/>">
                    </td>
                    <td>
                        <c:choose>
                            <c:when test="${!empty attachment.zippedFiles}">
                                <table class="zip">
                                    <tr>
                                        <th colspan="2">
                                            <c:if test="${attachment.canUpdate}">
                                                <html:link styleClass="internal"
                                                           href="${contextPath}/AttachmentEditAction.do?method=page&amp;attachmentId=${attachment.id}&amp;userId=${id}">
                                                    <img alt="<I18n:message key="OBJECT_PROPERTIES_EDIT"/>"
                                                         title="<I18n:message key="OBJECT_PROPERTIES_EDIT"/>"
                                                         border="0" hspace="0" vspace="0"
                                                         src="${contextPath}${ImageServlet}/cssimages/ico.edit.gif"/>
                                                </html:link>
                                            </c:if>
                                            <html:img src="${contextPath}${ImageServlet}/cssimages/ico.zip.gif"/>

                                            <a class="internal" target="blank"
                                               href="<c:url value="/download/user/${attachment.user.login}/${attachment.id}"/>">
                                                <c:out value="${attachment.name}" escapeXml="true"/>
                                            </a>
                                        </th>
                                    </tr>

                                    <c:forEach var="zipped" items="${attachment.zippedFiles}" varStatus="nfile">
                                        <tr>
                                            <td style="white-space: nowrap;">
                                                <a class="internal" target="blank"
                                                   href="<c:url value="/download/user/${attachment.user.login}/zipped/${attachment.id}/${zipped.nameMD5}"/>">
                                                    <c:out value="${zipped.name}" escapeXml="true"/>
                                                </a>
                                            </td>
                                            <td style="white-space: nowrap;">
                                                <I18n:message key="SIZE_FORMAT">
                                                    <I18n:param value="${zipped.size}"/>
                                                    <I18n:param value="${zipped.size/1024}"/>
                                                    <I18n:param value="${zipped.size/1048576}"/>
                                                </I18n:message>
                                            </td>
                                        </tr>
                                    </c:forEach>

                                    <c:if test="${attachment.zipHasMore}">
                                        <tr>
                                            <td colspan='2'>
                                                <I18n:message key="MORE"/>
                                            </td>
                                        </tr>
                                    </c:if>
                                </table>
                            </c:when>
                            <c:otherwise>
                                <c:if test="${attachment.deleted}">
                <span style="white-space: nowrap;">
                    <img border="0" hspace="0" vspace="0" title="<I18n:message key="ATTACHMENT_DELETED"/>" src="<c:out value="${contextPath}"/>${ImageServlet}/cssimages/warning.gif"/>
                    <c:out value="${attachment.name}" escapeXml="true"/>
                </span>
                                </c:if>
                                <c:if test="${!attachment.deleted}">
                <span style="white-space: nowrap;">
                    <c:if test="${attachment.canUpdate}">
                        <html:link styleClass="internal" href="${contextPath}/AttachmentEditAction.do?method=page&amp;attachmentId=${attachment.id}&amp;userId=${id}">
                            <img alt="<I18n:message key="OBJECT_PROPERTIES_EDIT"/>"
                                 title="<I18n:message key="OBJECT_PROPERTIES_EDIT"/>" border="0" hspace="0" vspace="0"
                                 src="${contextPath}${ImageServlet}/cssimages/ico.edit.gif"/>
                        </html:link>
                        <c:if test="${attachment.part}">
                            <img border="0" hspace="0" vspace="0" title="<I18n:message key="CORRUPTED_FILE"/>" src="<c:out value="${contextPath}"/>${ImageServlet}/cssimages/warning.gif"/>
                        </c:if>
                    </c:if>
                    <a class="internal" target="blank" href="<c:url value="/download/user/${attachment.user.login}/${attachment.id}"/>">
                        <c:out value="${attachment.name}" escapeXml="true"/>
                    </a>
                </span>
                                </c:if>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td style="white-space: nowrap;text-align:center;"><c:if test="${!attachment.deleted}"><a class="internal" target="blank" href="<c:url value="/webdav/user/${attachment.user.login}/${attachment.id}/${attachment.name}"/>">link</a></c:if></td>
                    <td style="white-space: nowrap;" <c:if test="${!attachment.deleted}">
                        title="<I18n:message key="SIZE_ORIGIN"><I18n:param value="${attachment.size}"/></I18n:message>"</c:if>>
                        <c:if test="${!attachment.deleted}">
                            <I18n:message key="SIZE_FORMAT">
                                <I18n:param value="${attachment.size}"/>
                                <I18n:param value="${attachment.size/1024}"/>
                                <I18n:param value="${attachment.size/1048576}"/>
                            </I18n:message>
                        </c:if>
                    </td>
                    <td style="white-space: nowrap;">
                        <c:if test="${!attachment.deleted}">
                            <I18n:formatDate value="${attachment.lastModified.time}" type="both" dateStyle="short"
                                             timeStyle="short"/>
                        </c:if>
                    </td>
                    <td>
                        <c:if test="${isDescription}">
                            <span title="${attachment.description}"><c:out value="${attachment.shortDescription}" escapeXml="true"/></span>
                        </c:if>
                    </td>
                    <td class="thumbnail">
                        <c:if test="${!attachment.deleted && isPreview}">
                        <span style="white-space: nowrap; text-align: center">
                            <c:if test="${attachment.thumbnailed}">
	                            <a class="internal" href="/download/user/${attachment.user.login}/${attachment.id}?type=image" data-lightbox="example-1">
		                            <img alt="" src="${contextPath}/TSImageServlet?attId=${attachment.id}&width=100&height=75" hspace="0" vspace="0">
	                            </a>
                            </c:if>
                        </span>
                        </c:if>
                    </td>
                    <td style="text-align:center;">
                        <div style="cursor:pointer;" onclick="deleteCheck('${id}', '${attachment.id}', 'deleteUserUpload', '<I18n:message key='DELETE_ATTACHMENTS_REQ'/>', false);">
                            <img alt="<I18n:message key="DELETE"/>" title="<I18n:message key="DELETE"/>" border="0" hspace="0" vspace="0" src="${contextPath}${ImageServlet}/cssimages/delete.png"/>
                        </div>
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