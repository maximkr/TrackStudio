<%@ page buffer="128kb" errorPage="/jsp/Error.jsp" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/tiles-el" prefix="tiles" %>
<c:set var="taskMenu" value="false"/>
<c:set var="userMenu" value="true"/>
<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value="${sc.timezone}"/>
<I18n:setBundle basename="language"/>
<tiles:insert page="/jsp/layout/ListLayout.jsp" flush="true">
<tiles:put name="header" value="/jsp/user/UserHeader.jsp"/>
<tiles:put name="tabs" type="string"/>
<tiles:put name="customHeader" type="string"/>
<tiles:put name="main" type="string">
<c:if test="${canView eq true}">
<div class="blueborder">
<div class="caption">
    <I18n:message key="USER_FILTERS_LIST"/>
</div>
<c:if test="${canCreateObject}">
    <div class="sideblueborder">
        <div class="controlPanel">
            <html:link href="${contextPath}/UserFilterEditAction.do?method=page&amp;id=${id}">
                <html:img alt="" src="${contextPath}${ImageServlet}/cssimages/ico.edit.gif" border="0"/>
                <I18n:message key="FILTER_ADD"/>
            </html:link>
        </div>
    </div>
</c:if>
<script type="text/javascript">
    var buttonIsDel = false;
    var cancelDelete = false;

    function deleteUserFilter() {
        cancelDelete = !deleteConfirm("<I18n:message key="DELETE_USER_FILTERS_REQ"/>", "filterForm");
    }

    function onSubmitFunction(frm) {
        return !cancelDelete;
    }

    function set(target) {
        document.getElementById('filterListId').value = target;
    }
</script>

<div class="indent">
<c:choose>
    <c:when test="${!empty filterList}">
<html:form method="POST" styleId="checkunload" action="/UserFilterAction"
           onsubmit="if (buttonIsDel) return onSubmitFunction(this); else return validate(this);">
<html:hidden property="method" value="save" styleId="filterListId"/>
<html:hidden property="id" value="${id}"/>
<html:hidden property="session" value="${session}"/>

<table class="general" cellpadding="0" cellspacing="0">

    <tr class="wide">
        <c:if test="${canDelete || canCopy}">
            <th width='1%'><input type="checkbox" onClick="selectAllCheckboxes(this, 'delete1')"></th>
        </c:if>
        <th>
            <I18n:message key="NAME"/>
        </th>
        <th>
            <I18n:message key="DESCRIPTION"/>
        </th>
        <th>
            <I18n:message key="SHARED"/>
        </th>
        <th>
            <I18n:message key="OWNER"/>
        </th>
    </tr>
    <c:forEach var="filter" items="${filterList}" varStatus="varCounter">
        <tr class="line<c:out value="${varCounter.index mod 2}"/>">
            <c:if test="${canDelete || canCopy}">
                <td>
                            <span style="text-align: center;">
                                <html:checkbox property="select" value="${filter.id}" alt="delete1"/>
                            </span>
                    
                </td>
            </c:if>

            <td>
                <c:choose>
                    <c:when test="${!filter.canManage}">
                        <html:link styleClass="internal"
                                href="${contextPath}/UserFilterViewAction.do?method=page&amp;filterId=${filter.id}&amp;id=${id}">
                            <img title="<I18n:message key="OBJECT_PROPERTIES_VIEW"/>" border="0" hspace="0"
                                 vspace="0" src="<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.closed.gif"/>
                        </html:link>
                    </c:when>
                    <c:otherwise>
                        <html:link styleClass="internal"
                                href="${contextPath}/UserFilterEditAction.do?method=page&amp;filterId=${filter.id}&amp;id=${id}">
                            <img title="<I18n:message key="OBJECT_PROPERTIES_EDIT"/>" border="0" hspace="0"
                                 vspace="0" src="<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.edit.gif"/>
                        </html:link>
                    </c:otherwise>
                </c:choose>
                <html:link styleClass="internal"
                        href="${contextPath}/UserFilterViewAction.do?method=page&amp;filterId=${filter.id}&amp;id=${id}">
                    <c:if test="${filter.id eq currentFilerId}">
                        <img border="0" hspace="0" vspace="0"
                             src="<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.current.gif"/>
                    </c:if>

                    <c:out value="${filter.name}"/>
                </html:link>
            </td>
            <td>
                <c:out value="${filter.description}"/>
            </td>
            <td>
                     <span style="text-align: center;">
                         <c:choose>
                             <c:when test="${filter.priv ne true}">
                                 <html:img alt="" src="${contextPath}${ImageServlet}/cssimages/ico.checked.gif"/>
                             </c:when>
                             <c:otherwise>
                                 <html:img alt="" src="${contextPath}${ImageServlet}/cssimages/ico.unchecked.gif"/>
                             </c:otherwise>
                         </c:choose>
                     </span>
            </td>
            <td>
            <span class="user" ${filter.owner.id eq sc.userId ? "id='loggedUser'" : ""}>
            <html:img styleClass="icon" border="0"
                      src="${contextPath}${ImageServlet}/cssimages/${filter.owner.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
            <c:out value="${filter.owner.name}" escapeXml="true"/>
			</span>
            </td>
        </tr>
    </c:forEach>
</table>
<div class="controls">
    <c:if test="${canCopy eq true}">
        <input type="SUBMIT" class="iconized secondary"
               value="<I18n:message key="CLONE"/>"
               name="SETFILTER" onClick="set('clone');">
    </c:if>
    <c:if test="${canDelete eq true}">
        <input type="SUBMIT" class="iconized" value="<I18n:message key="DELETE"/>"
               onClick="buttonIsDel = true; set('delete'); deleteUserFilter(); if(onSubmitFunction(this.form))this.form.submit();"
               name="DELETE">
    </c:if>
    <SCRIPT type="text/javascript">

    </SCRIPT>
</div>
</html:form>
</c:when>
    <c:otherwise>
        <div class="empty"><I18n:message key="EMPTY_USER_FILTER_LIST"/></div>
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
                                    <html:link styleClass="internal"
                                            href="${contextPath}/UserFilterAction.do?method=page&amp;id=${task.key.id}">
                                        <html:img styleClass="icon" border="0"
                                                  src="${contextPath}${ImageServlet}/cssimages/${task.key.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
                                        <c:out value="${task.key.name}"/>
                                    </html:link>
                                </span>
                                <span class="itempath">
                                <html:link styleClass="internal"
                                        href="${contextPath}/UserFilterAction.do?method=page&amp;id=${task.key.id}">
                                    <c:forEach var="path" items="${task.key.ancestors}">
                                        <span class="separated"><c:out value="${path.name}"/></span>&nbsp;/

                                    </c:forEach>
                                </html:link><c:if test="${task.key.parentId ne null}">
                                    <html:link styleClass="internal"
                                            href="${contextPath}/UserFilterAction.do?method=page&amp;id=${task.key.id}">
                                        <c:out value="${task.key.name}"/>
                                    </html:link>
                                </c:if>
                                </span>
                            </dt>
                            <dd>
                                <c:forEach var="cat" items="${task.value}" varStatus="varC">
                                    <c:if test="${varC.index > 0}">,</c:if>
                                    <span style="white-space: nowrap;">
                                        <html:link styleClass="internal"
                                                   href="${contextPath}/UserFilterViewAction.do?method=page&amp;filterId=${cat.id}&amp;id=${task.key.id}"
                                                   title="${cat.name}"
                                                   styleId="${cat.id eq currentFilterId ? \"current\" : \"\"}">
                                            <html:img styleClass="icon" border="0"
                                                      src="${contextPath}${ImageServlet}/cssimages/ico.filter.gif"/>
                                            <c:out value="${cat.name}"/>

                                        </html:link>
                                    </span>
                                </c:forEach>
                            </dd>
                        </c:forEach>
                    </dl>
                </c:if>
            </c:forEach>
        </div>
    </div>
</c:if>

</c:if>
</tiles:put>
</tiles:insert>