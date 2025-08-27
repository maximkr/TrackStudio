<%@ page buffer="128kb" errorPage="/jsp/Error.jsp"%>
<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/tiles-el" prefix="tiles" %>


<I18n:setLocale value='${sc.locale}'/>
<I18n:setTimeZone value='${sc.timezone}'/>
<I18n:setBundle basename='language'/>
<tiles:insert page="/jsp/layout/ListLayout.jsp" flush="true">
    <tiles:put name="header" value="/jsp/task/TaskHeader.jsp"/>
    <tiles:put name="customHeader" type="string"/>
    <tiles:put name="tabs" type="string"/>

    <tiles:put name="main" type="string">

<c:if test="${canView}">
<div class="blueborder">
    <div class="caption">
        <I18n:message key="TASK_EFFECTIVE_PRSTATUSES"/>
        </div>
         <c:if test="${canEditTaskACL}">
<div class="controlPanel">
<html:link href="${contextPath}/ACLAction.do?method=page&amp;id=${id}"><html:img src="${contextPath}${ImageServlet}/cssimages/ico.edit.gif" styleClass="icon" border="0"/><I18n:message key="ACL"/></html:link>
</div>

 </c:if>

    <div class="indent">
    <table class="general" cellpadding="0" cellspacing="0">
<tr class="wide">
        <th width="20%"><I18n:message key="USER"/></th>
        <th width="20%"><I18n:message key="LOGIN"/></th>
        <th width="40%"><I18n:message key="EFFECTIVE_PRSTATUSES"/></th>
</tr>
<c:forEach var="acl" items="${effective}" varStatus="varCounter">
<tr class="line<c:out value="${varCounter.index mod 2}"/>">
    <td>
        <span class="user" ${acl.key.id eq sc.userId ? "id='loggedUser'" : ""}>
            <html:img  styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/${acl.key.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
            <c:if test="${!canViewException[acl.key]}">
                <img border="0" hspace="0" vspace="0" title="<I18n:message key="CAN_NOT_VIEW_CATEGORY"/>" src="<c:out value="${contextPath}"/>${ImageServlet}/cssimages/warning.gif"/>
            </c:if>            
            <c:out value="${acl.key.name}" escapeXml="true"/>
	    </span>
    </td>
    <td>
        <a  href="${contextPath}/UserViewAction.do?id=${acl.key.id}#acl" class="user" ${acl.key.id eq sc.userId ? "id='loggedUser'" : ""}>
            <html:img  styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/${acl.key.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
            <c:out value="${acl.key.login}" escapeXml="true"/>
	    </a>
    </td>
    <td>
        <c:forEach var="cat" items="${acl.value}" varStatus="varC"><c:if test="${varC.index > 0}">, </c:if><span style="white-space: nowrap;">
            <html:img  styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/ico.status.gif"/><c:out value="${cat.name}" escapeXml="true"/></span></c:forEach>
    </td>
</tr>
</c:forEach>
</table>

<c:out value="${slider}" escapeXml="false"/>
    </div>
    </div>
</c:if>

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
                            <dt><span class="itemname"><html:link styleClass="internal"
                                    href="${contextPath}/ACLAction.do?method=page&amp;id=${task.key.id}">
                                <html:img styleClass="icon" border="0"
                                          src="${contextPath}${ImageServlet}/icons/categories/${task.key.category.icon}"/>
                                <c:out value="${task.key.name}"/>
                            </html:link></span><span class="itempath"><html:link styleClass="internal"
                                    href="${contextPath}/ACLAction.do?method=page&amp;id=${task.key.id}">
                                <c:forEach var="path" items="${task.key.ancestors}">
                                    <span class="separated"><c:out value="${path.name}"/></span>&nbsp;/
                                    
                                </c:forEach>
                            </html:link><c:if test="${task.key.parentId ne null}">
                                <html:link styleClass="internal" href="${contextPath}/ACLAction.do?method=page&amp;id=${task.key.id}">
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
                                               href="${contextPath}/ACLAction.do?method=page&amp;id=${task.key.id}">
                                        <c:choose>
                                            <c:when test="${cat.forUser ne null}"><span
                                                    class="user" ${cat.forUser.id eq sc.userId ? "id='loggedUser'" : ""}>
            <html:img styleClass="icon" border="0"
                      src="${contextPath}${ImageServlet}/cssimages/${cat.forUser.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
            <c:out value="${cat.forUser.name}" escapeXml="true"/>
			</span></c:when>
                                            <c:otherwise>
                                                <html:img styleClass="icon" border="0"
                                                          src="${contextPath}${ImageServlet}/cssimages/ico.status.gif"/>
                                                <c:out value="${cat.forPrstatus.name}" escapeXml="true"/>
                                            </c:otherwise>
                                        </c:choose>
                                        </span>
                                    </html:link>
                                </c:forEach>
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