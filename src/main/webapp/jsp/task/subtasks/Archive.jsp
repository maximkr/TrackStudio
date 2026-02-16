<%@ page buffer="128kb" errorPage="/jsp/Error.jsp" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/tiles-el" prefix="tiles" %>
<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value="${sc.timezone}"/>
<I18n:setBundle basename="language"/>
<tiles:insert page="/jsp/layout/ListLayout.jsp" flush="true">
    <tiles:put name="customHeader" type="string"/>
    <tiles:put name="tabs" type="string"/>

    <tiles:put name="header" value="/jsp/task/TaskHeader.jsp"/>
    <tiles:put name="main" type="string">
        <script>
            function deleteArchive(id, name, number) {
                TSDialog.confirm('<I18n:message key="DELETE_ARCHIVE"/> ' + name + " [#" + number + "]", function(ok) {
                    if (ok) {
                        document.location.href = '<c:out value="${contextPath}"/>/SubtaskAction.do?method=deleteArchive&id=${tci.id}&archiveId=' + id;
                    }
                });
            }
        </script>
        <div class="searchtasks">
        <div class="caption">
            <I18n:message key="ARCHIVED_TASKS"/>
            <form method="post" id="searchArchive" style="display:inline;float:right;" action="<c:out value="${contextPath}"/>/SubtaskAction.do">
                <input type="hidden" name="method" value="archiveView"/>
                <input type="hidden" name="id" value="<c:out value="${tci.id}"/>"/>
                <input type="hidden" name="session" value="<c:out value="${session}"/>"/>
                <div class="link" style="vertical-align:top;padding-right:10px;">
                    <input type="text" style="font-weight: bold; background-color: #FFFFFF;"  one
                           class="form-autocomplete" name="archive_search" id="archive_search" size="46" value="${archive_search}">
                </div>
                <script type="text/javascript">
                    var input = document.getElementById("archive_search");
                    input.addEventListener("keyup", function(event) {
                        if (event.keyCode === 13) {
                            document.forms['searchArchive'].submit();
                        }
                    });
                </script>
            </form>
        </div>
        <div class="indent">

        <table class="general" cellpadding="0" cellspacing="0">
            <tr class="wide">
                <c:set var="columns" value="1"/>
                <c:set var="columns" value="${columns+1}"/>
                <th nowrap width="<c:out value="${sizeOfPart*headerNumber.parts}"/>%">
                    <html:link styleClass="underline"
                               href="#">
                        <I18n:message key="TASK_NUMBER"/>
                        <c:if test="${headerNumber.currentSort != null && headerNumber.currentSort == 'abs'}"><html:image
                                src="${contextPath}${ImageServlet}/cssimages/up.gif"/></c:if>
                        <c:if test="${headerNumber.currentSort != null && headerNumber.currentSort == 'desc'}"><html:image
                                src="${contextPath}${ImageServlet}/cssimages/down.gif"/></c:if>
                    </html:link>
                </th>
                <c:set var="columns" value="${columns+1}"/>
                <th width="<c:out value="${headerName.parts}"/>%">
                    <html:link styleClass="underline"
                               href="#">
                        <I18n:message key="NAME"/>
                        <c:if test="${headerName.currentSort != null && headerName.currentSort == 'abs'}"><html:image
                                src="${contextPath}${ImageServlet}/cssimages/up.gif"/></c:if>
                        <c:if test="${headerName.currentSort != null && headerName.currentSort == 'desc'}"><html:image
                                src="${contextPath}${ImageServlet}/cssimages/down.gif"/></c:if>
                    </html:link>
                </th>
                <c:set var="columns" value="${columns+1}"/>
                <c:set var="columns" value="${columns+1}"/>
                <th width="<c:out value="${sizeOfPart}"/>%">
                    <html:link styleClass="underline"
                               href="${contextPath}/SubtaskAction.do?method=page&id=${id}&sliderOrder=${headerSubmitter.sortBy}&filterId=${filter.id}">
                        <I18n:message key="SUBMITTER"/>
                    </html:link>
                    <c:if test="${headerSubmitter.currentSort != null && headerSubmitter.currentSort == 'abs'}"><html:image
                            src="${contextPath}${ImageServlet}/cssimages/up.gif"/></c:if>
                    <c:if test="${headerSubmitter.currentSort != null && headerSubmitter.currentSort == 'desc'}"><html:image
                            src="${contextPath}${ImageServlet}/cssimages/down.gif"/></c:if>
                </th>
                <c:if test="${canDeleteArchive}">
                    <th>&nbsp;</th>
                </c:if>
            </tr>

            <c:set var="rowspan" value="1"/>
            <c:if test="${canViewDescription}">
                <c:set var="rowspan" value="2"/>
            </c:if>

            <c:forEach var="taskLineEntity" items="${tasks}" varStatus="status">

            <c:set var="taskLine" value="${taskLineEntity}"/>
            <tr class="line<c:out value="${status.index mod 2}"/>">
                <c:choose>
                <c:when test="${(!empty listMes)}">
                    <c:set var="tmprowspan" value="${rowspan+1}"/>
                </c:when>
                <c:otherwise>
                    <c:set var="tmprowspan" value="${rowspan}"/>
                </c:otherwise>
                </c:choose>

                <td>
                    <span class="internal">#<c:out value="${taskLine.number}" escapeXml="true"/></span>
                </td>
                <td>
                    <span class="internal">
                        <html:link styleClass="internal"
                                   href="${contextPath}/TaskViewAction.do?method=archive&id=${tci.id}&archiveId=${taskLine.id}&thisframe=true">
                            <html:img styleClass="icon" border="0" title="${taskLine.categoryName}"
                                      src="${contextPath}${ImageServlet}/icons/categories/${taskLine.categoryIcon}"/>
                            <html:img styleClass="state" border="0" style="background-color: ${taskLine.statusColor}"
                                      src="${contextPath}${ImageServlet}${taskLine.statusImage}"/>
                            <c:out value="${taskLine.path} > " escapeXml="true"/>
                            <c:out value="${taskLine.name}" escapeXml="true"/>&nbsp;[#<c:out value="${taskLine.number}" escapeXml="true"/>]
                        </html:link>
                    </span>
                </td>
                <td style="white-space: nowrap;">
                    <c:if test="${taskLine.submitter ne null}"><span
                            class="user" ${taskLine.submitter.id eq sc.userId ? "id='loggedUser'" : ""}>
                                    <html:img styleClass="icon" border="0"
                                              src="${contextPath}${ImageServlet}/cssimages/${taskLine.submitter.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
                                    <c:out value="${taskLine.submitter.name}" escapeXml="true"/>
                    </span></c:if>
                </td>
                <c:if test="${canDeleteArchive}">
                    <td style="text-align:center;">
                        <div style="cursor:pointer;" onclick="deleteArchive('${taskLine.id}', '${taskLine.name}', '${taskLine.number}');">
                            <img alt="<I18n:message key="DELETE"/>" title="<I18n:message key="DELETE"/>" border="0" hspace="0" vspace="0" src="${contextPath}${ImageServlet}/cssimages/delete.png"/>
                        </div>
                    </td>
                </c:if>
                </c:forEach>
        </table>
        <c:out value="${slider}" escapeXml="false"/>
    </tiles:put>
</tiles:insert>
