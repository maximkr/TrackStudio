<%@ page buffer="128kb" errorPage="/jsp/Error.jsp"%>
<%@ page buffer="128kb" errorPage="/jsp/Error.jsp"%>
<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/tiles-el" prefix="tiles" %>
<I18n:setLocale value='${sc.locale}'/>
<I18n:setTimeZone value='${sc.timezone}'/>
<I18n:setBundle basename='language'/>


<tiles:insert page="/jsp/layout/ListLayout.jsp" flush="true">
<tiles:put name="customHeader" type="string"/>
<tiles:put name="header" value="/jsp/task/TaskHeader.jsp"/>

<tiles:put name="tabs" type="string"/>
<tiles:put name="main" type="string">

<div class="blueborder">
    <div class="caption"><I18n:message key="REPORTS_LIST"/></div>
    <c:if test="${canCreateReport}">

        <div class="controlPanel">
            <html:link href="${contextPath}/ReportEditAction.do?method=page&amp;id=${id}&amp;type=List"><html:img src="${contextPath}${ImageServlet}/cssimages/ico.listreport.gif" styleClass="icon" border="0"/><I18n:message key="REPORT_LIST_CREATE"/></html:link>
            <html:link href="${contextPath}/ReportEditAction.do?method=page&amp;id=${id}&amp;type=Tree"><html:img src="${contextPath}${ImageServlet}/cssimages/ico.treereport.gif" styleClass="icon" border="0"/><I18n:message key="REPORT_TREE_CREATE"/></html:link>
        </div>

    </c:if>
    <script type="text/javascript">
        var buttonIsDel = false;

        var cancelDelete = false;

        function deleteReport() {
            if (document.getElementById('reportListId').value == 'delete') {
                return deleteConfirm("<I18n:message key="DELETE_REPORT_REQ"/>", "reportForm");
            }
            return true;
        }

        function onSubmitFunction(frm) {
            return !cancelDelete;
        }

        function set(target) {
            document.getElementById('reportListId').value=target;
        }
    </script>

    <div class="indent">
        <c:choose>
            <c:when test="${!empty reportList}">
                <html:form method="POST" action="/ReportAction" styleId="checkunload" onsubmit="return deleteReport();">
                    <html:hidden property="method" value="create" styleId="reportListId"/>
                    <html:hidden property="id" value="${id}"/>
                    <html:hidden property="session" value="${session}"/>
                    <table class="general" cellpadding="0" cellspacing="0">
                        <tr class="wide">
                            <c:if test="${canDelete}">
                                <th width='1%' nowrap style="white-space:nowrap"> <input type="checkbox" onClick="selectAllCheckboxes(this, 'delete1')"></th>
                            </c:if>
                            <th><I18n:message key="NAME"/></th>
                            <th><I18n:message key="TYPE"/></th>
                            <th><I18n:message key="FILTER"/></th>
                            <th><I18n:message key="REPORT_SHARE"/></th>
                            <th><I18n:message key="OWNER"/></th>
                            <th><I18n:message key="CONNECTED_TO"/></th>
                        </tr>
                        <c:forEach var="report" items="${reportList}" varStatus="varCounter">
                            <tr class="line<c:out value="${varCounter.index mod 2}"/>">
                                <c:if test="${canDelete}">
                                    <td>
                                        <c:choose>
                                            <c:when test="${report.canManage}">
                                                <center>
                                                    <html:checkbox property="select" value="${report.id}" alt="delete1"/>
                                                </center>
                                            </c:when>
                                            <c:otherwise>
                                                &nbsp;
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </c:if>
                                <td>
                                    <c:choose>
                                        <c:when test="${!report.canManage}">
                                            <html:link styleClass="internal" href="${contextPath}/ReportViewAction.do?method=page&reportId=${report.id}&id=${id}">
                                                <img title="<I18n:message key="OBJECT_PROPERTIES_VIEW"/>" border="0" hspace="0" vspace="0"  src="<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.closed.gif"/>
                                            </html:link>
                                        </c:when>
                                        <c:otherwise>
                                            <html:link styleClass="internal" href="${contextPath}/ReportEditAction.do?method=page&reportId=${report.id}&id=${id}">
                                                <img title="<I18n:message key="OBJECT_PROPERTIES_EDIT"/>" border="0" hspace="0" vspace="0"  src="<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.edit.gif"/>
                                            </html:link>
                                        </c:otherwise>
                                    </c:choose>
                                    <html:link styleClass="internal" href="${contextPath}/ReportViewAction.do?method=page&id=${id}&reportId=${report.id}">
                                        <c:out value="${report.name}" escapeXml="true"/>
                                    </html:link>

                                </td>
                                <td><c:out value="${report.rtypeText}" escapeXml="true"/></td>
                                <td>
            <span style="white-space: nowrap;">
                <c:choose>
                    <c:when test="${canViewFilters}">
                        <html:link styleClass="internal" href="${contextPath}/TaskFilterViewAction.do?method=page&amp;filterId=${report.filter.id}&amp;id=${id}"
                                   title="${report.filter.name}"><html:img  styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/ico.filter.gif"/><c:out value="${report.filter.name}" escapeXml="true"/></html:link>
                    </c:when>
                    <c:otherwise>
                        <c:out value="${report.filter.name}" escapeXml="true"/>
                    </c:otherwise>
                </c:choose>
            </span>

                                </td>
                                <td>
             <span style="text-align: center;">
                 <c:choose>
                     <c:when test="${report.priv ne true}">
                         <html:img alt="" src="${contextPath}${ImageServlet}/cssimages/ico.checked.gif"/>
                     </c:when>
                     <c:otherwise>
                         <html:img alt="" src="${contextPath}${ImageServlet}/cssimages/ico.unchecked.gif"/>
                     </c:otherwise>
                 </c:choose>
             </span>
                                </td>

                                <td>
      <span class="user" ${report.owner.id eq sc.userId ? "id='loggedUser'" : ""}>
            <html:img  styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/${report.owner.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
            <c:out value="${report.owner.name}" escapeXml="true"/>
			</span>
                                </td>
                                <td>
                                    <html:link styleClass="internal" styleId="${report.task.id}-name" href="${contextPath}/task/${report.task.number}?thisframe=true">
                                        <html:img styleClass="icon" border="0" src="${contextPath}${ImageServlet}/icons/categories/${report.task.category.icon}" title="${report.task.category.name}"/>
                                        <html:img styleClass="state" border="0" style="background-color: ${report.task.status.color}" src="${contextPath}${ImageServlet}${report.task.status.image}"/>
                                        <c:out value="${report.task.name}" escapeXml="true"/>
                                    </html:link>
                                </td>
                            </tr>
                        </c:forEach>
                    </table>
                    <div class="controls">
                        <c:if test="${canCreateReport}" >
                            <input type="SUBMIT"  class="iconized secondary"
                                   value="<I18n:message key="CLONE"/>"
                                   name="CLONE" onClick="set('clone');">
                        </c:if>
                        <c:if test="${canCopy eq true}" >
                            <input type="SUBMIT"  class="iconized secondary"
                                   value="<I18n:message key="COPY"/>"
                                   name="SETFILTER" onClick="set('copy');">
                        </c:if>
                        <c:if test="${canDelete eq true}" >
                            <input type="SUBMIT"  class="iconized" value="<I18n:message key="DELETE"/>"
                                   onClick="buttonIsDel = true; set('delete'); " name="DELETE">
                        </c:if>
                    </div>

                </html:form>
            </c:when>
            <c:otherwise>
                <div class="empty"><I18n:message key="EMPTY_REPORT_LIST"/></div>
            </c:otherwise>
        </c:choose>
    </div>
</div>
<c:if test="${!empty seeAlso}">
    <br>
    <div class="blueborder">
        <div class="caption">
            <I18n:message key="SEE_ALSO"/>
        </div>
        <div class="indent">
            <c:forEach items="${seeAlso}" var="also" varStatus="affected">
                <c:if test="${!empty also}">
                    <dl ${affected.first ? "class='affected'" : ""}>

                        <c:forEach var="task" items="${also}" varStatus="varCounter">
                            <dt>
                                    <span class="itemname">
                                        <html:link styleClass="internal" href="${contextPath}/ReportAction.do?method=page&amp;id=${task.key.id}"><html:img  styleClass="icon" border="0" src="${contextPath}${ImageServlet}/icons/categories/${task.key.category.icon}"/><c:out value="${task.key.name}"/></html:link></span><span class="itempath"><html:link styleClass="internal" href="${contextPath}/ReportAction.do?method=page&amp;id=${task.key.id}">
                                <c:forEach var="path" items="${task.key.ancestors}">
                                    <span class="separated"><c:out value="${path.name}"/></span>&nbsp;/
                                </c:forEach>
                            </html:link>
                                        <c:if test="${task.key.parentId ne null}">
                                            <html:link styleClass="internal" href="${contextPath}/ReportAction.do?method=page&amp;id=${task.key.id}">
                                                <c:out value="${task.key.name}"/>
                                            </html:link>
                                        </c:if>
                </span>
                            </dt>
                            <dd>
                                <c:forEach var="cat" items="${task.value}" varStatus="varC">
                                    <c:if test="${varC.index > 0}">, </c:if><span style="white-space: nowrap;">
                                            <html:link styleClass="internal" href="${contextPath}/ReportViewAction.do?method=page&amp;reportId=${cat.id}&amp;id=${task.key.id}" title="${cat.name}">
                                                <c:if test="${cat.rtype eq 'List'}"><html:img src="${contextPath}${ImageServlet}/cssimages/ico.listreport.gif" styleClass="icon" border="0"/></c:if>
                                                <c:if test="${cat.rtype eq 'Tree'}"><html:img src="${contextPath}${ImageServlet}/cssimages/ico.treereport.gif" styleClass="icon" border="0"/></c:if>
                                                <c:out value="${cat.name}"/></html:link></span></c:forEach>
                            </dd>
                        </c:forEach>
                    </dl>
                </c:if>
            </c:forEach>
        </div>
    </div>
</c:if>

</tiles:put>
</tiles:insert>
