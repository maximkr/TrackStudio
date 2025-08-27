<%@ page buffer="128kb" errorPage="/jsp/Error.jsp"%>
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

<tiles:insert page="/jsp/layout/ListLayout.jsp" flush="true">
    <tiles:put name="header" value="/jsp/task/TaskHeader.jsp"/>
    <tiles:put name="customHeader" type="string"/>
    <tiles:put name="tabs" type="string"/>
    <tiles:put name="main" type="string">
        <script type="text/javascript">
            function attachment(element) {
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
            }
            function setMethod(target) {
                document.getElementById('method').value = target;
            }
        </script>
        <c:if test="${canViewAttachment}">
            <html:form method="POST" action="/AttachmentBrowserAction" onsubmit="return attachment(this);">
                <html:hidden property="method" styleId="method" value="createArchive"/>
                <html:hidden property="session" value="${session}"/>
                <html:hidden property="id" value="${id}"/>
                <div class="blueborder">
                    <div class="caption">
                        <I18n:message key="ATTACHMENTS"/>
                        <div style="float:right;">
                            <input style="height:11px;vertical-align:middle; background-color:#FFFFFF; border:1px solid #B2C9D9; cursor:text;" class="form-autocomplete" name="filter" size="30" onkeyup="search_attachment(this, 'table_attachment', [1])" type="text">
                        </div>
                    </div>
                    <div class="indent">
                        <table class="general sortable" id="table_attachment" cellpadding="0" cellspacing="0">
                            <tr class="wide">
                                <th width="1%" nowrap style="white-space:nowrap" class="nosort"> <input type="checkbox" onClick="selectAllCheckboxes(this, 'delete1');"></th>
                                <th width="24%"><I18n:message key="FILE"/></th>
                                <th width="24%"><I18n:message key="TASK_NAME"/></th>
                                <th width="5%"><I18n:message key="WEBDAV"/></th>
                                <th><I18n:message key="FILE_SIZE"/></th>
                                <th width="15%"><I18n:message key="LAST_MODIFIED"/></th>
                                <th width="15%"><I18n:message key="OWNER"/></th>
                                <th width="25%"><I18n:message key="DESCRIPTION"/></th>
                                <th width="20%"><I18n:message key="THUMBNAIL"/></th>
                            </tr>
                            <c:forEach var="attachment" items="${attachments}" varStatus="varCounter">
                                <tr class="line<c:out value="${varCounter.index mod 2}"/>">
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
                                                            <a class="internal" target="blank" href="<c:url value="/download/task/${attachment.task.number}/${attachment.id}"/>"><c:out value="${attachment.shortName}" escapeXml="true"/></a></th></tr>
                                                    <c:forEach var="zipped" items="${attachment.zippedFiles}" varStatus="nfile">
                                                        <tr>
                                                            <td  style="white-space: nowrap;">
                                                                <a class="internal" target="blank" href="<c:url value="/download/task/${attachment.task.number}/zipped/${attachment.id}/${zipped.nameMD5}"/>"><c:out value="${zipped.name}" escapeXml="true"/></a>
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
                                            <a class="internal" target="blank" href="<c:url value="/download/task/${attachment.task.number}/${attachment.id}"/>"><c:out value="${attachment.shortName}" escapeXml="true"/></a>
                                    </span>
                                                    <c:if test="${attachment.part}">
                                                        <img border="0" hspace="0" vspace="0" title="<I18n:message key="ATTACHMENT_UPLOADED_UNCORRECTLY"/>" src="<c:out value="${contextPath}"/>${ImageServlet}/cssimages/warning.gif"/>
                                                    </c:if>
                                                </c:if>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <html:link styleClass="internal" styleId="${attachment.task.id}-name" href="${contextPath}/task/${attachment.task.number}?thisframe=true">
                                             <html:img styleClass="icon" border="0" src="${contextPath}${ImageServlet}/icons/categories/${attachment.task.category.icon}" title="${attachment.task.category.name}"/>
                                            <html:img styleClass="state" border="0" style="background-color: ${attachment.task.status.color}" src="${contextPath}${ImageServlet}${attachment.task.status.image}"/>
                                            <c:out value="${attachment.task.name}" escapeXml="true"/>
                                        </html:link>
                                    </td>
                                    <td style="white-space: nowrap;text-align:center;"><c:if test="${!attachment.deleted}"><a class="internal" target="blank" href="<c:url value="/webdav/task/${attachment.task.number}/${attachment.id}/${attachment.name}"/>">link</a></c:if></td>
                                    <td style="white-space: nowrap;" <c:if test="${!attachment.deleted}">title="<I18n:message key="SIZE_ORIGIN"><I18n:param value="${attachment.size}"/></I18n:message>"</c:if>><c:if test="${!attachment.deleted}"><I18n:message key="SIZE_FORMAT"><I18n:param value="${attachment.sizeDoubleValue}"/><I18n:param value="${attachment.sizeDoubleValue/1024}"/><I18n:param value="${attachment.sizeDoubleValue/1048576}"/></I18n:message></c:if></td>
                                    <td style="white-space: nowrap;"><c:if test="${!attachment.deleted}"><I18n:formatDate value="${attachment.lastModified.time}" type="both" dateStyle="short" timeStyle="short"/></c:if></td>
                                    <td style="white-space: nowrap;">
                                        <c:if test="${attachment.user ne null}"><span class="user" ${attachment.user.id eq sc.userId ? "id='loggedUser'" : ""}><html:img  styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/${attachment.user.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
                            <c:out value="${attachment.user.name}" escapeXml="true"/></span></c:if>
                                    </td>
                                    <td>
                                        <c:if test="${attachment.description ne null}">
                                            <span title="${attachment.description}"><c:out value="${attachment.shortDescription}" escapeXml="true"/></span>
                                        </c:if>
                                    </td>
                                    <td class="thumbnail">
                                        <c:choose>
                                            <c:when test="${attachment.thumbnailed}">
	                                            <a class="internal" href="/download/user/${attachment.task.number}/${attachment.id}?type=image" data-lightbox="example-1">
		                                            <img alt="" src="${contextPath}/TSImageServlet?attId=${attachment.id}&width=100&height=75" hspace="0" vspace="0">
	                                            </a>
                                            </c:when>
                                            <c:otherwise>
                                                &nbsp;
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:forEach>
                        </table>
                        <div class="controls" style="text-align:left;">
                            <input type="submit" onclick="setMethod('createArchive');" class="iconized" value="<I18n:message key="DOWNLOAD"/>" name="DOWNLOAD">
                        </div>
                    </div>
                </div>
            </html:form>
        </c:if>
    </tiles:put>
</tiles:insert>
