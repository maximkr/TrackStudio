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
        <div class="blueborder">
            <div class="caption">
                <I18n:message key="LIST_TEMPLATES"/>
            </div>
            <div class="indent">
                <table class="general">
                    <caption>
                        <I18n:message key="NOTIFICATIONS_LIST"/>
                    </caption>
                    <tr class="wide">
                        <th width="30%"><I18n:message key="EMAIL_TYPE"/></th>
                        <th width="30%"><I18n:message key="USER"/></th>
                        <th width="30%"><I18n:message key="CONNECTED_TO"/></th>
                    </tr>
                    <c:forEach var="entity" items="${notifications}" varStatus="varCounter">
                        <tr class="line<c:out value="${varCounter.index mod 2}"/>">
                            <td>${entity.template}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${entity.user ne null}">
			        <span class="user" ${entity.user.id eq sc.userId ? "id=\"loggedUser\"" : ""}>
                        <html:img styleClass="icon" border="0"
                                  src="${contextPath}${ImageServlet}/cssimages/${entity.user.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/><c:out
                            value="${entity.user.name}" escapeXml="true"/>
			        </span>
                                    </c:when>
                                    <c:when test="${entity.group ne null}">
					<span class="user">
                        <html:img styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/ico.status.gif"/><c:out value="${entity.group.name}" escapeXml="true"/>
				    </span>
                                    </c:when>
                                </c:choose>
                            </td>
                            <td>
                                    ${entity.task.name}&nbsp;[#${entity.task.number}]
                            </td>
                        </tr>
                    </c:forEach> &nbsp;
                </table>
                <table class="general">
                    <caption>
                        <I18n:message key="SUBSCRIPTIONS_LIST"/>
                    </caption>
                    <tr class="wide">
                        <th width="30%"><I18n:message key="EMAIL_TYPE"/></th>
                        <th width="30%"><I18n:message key="USER"/></th>
                        <th width="30%"><I18n:message key="CONNECTED_TO"/></th>
                    </tr>
                    <c:forEach var="entity" items="${subscribes}" varStatus="varCounter">
                        <tr class="line<c:out value="${varCounter.index mod 2}"/>">
                            <td>${entity.template}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${entity.user ne null}">
			        <span class="user" ${entity.user.id eq sc.userId ? "id=\"loggedUser\"" : ""}>
                        <html:img styleClass="icon" border="0"
                                  src="${contextPath}${ImageServlet}/cssimages/${entity.user.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/><c:out
                            value="${entity.user.name}" escapeXml="true"/>
			        </span>
                                    </c:when>
                                    <c:when test="${entity.group ne null}">
					<span class="user">
                        <html:img styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/ico.status.gif"/><c:out value="${entity.group.name}" escapeXml="true"/>
				    </span>
                                    </c:when>
                                </c:choose>
                            </td>
                            <td>
                                    ${entity.task.name}&nbsp;[#${entity.task.number}]
                            </td>
                        </tr>
                    </c:forEach> &nbsp;
                </table>
                <table class="general">
                    <caption>
                        <I18n:message key="TEMPLATES_LIST"/>
                    </caption>
                    <tr class="wide">
                        <th width="30%"><I18n:message key="TEMPLATE"/></th>
                        <th width="30%"><I18n:message key="USER"/></th>
                        <th width="30%"><I18n:message key="CONNECTED_TO"/></th>
                    </tr>
                    <c:forEach var="entity" items="${templates}" varStatus="varCounter">
                        <tr class="line<c:out value="${varCounter.index mod 2}"/>">
                            <td>${entity.folder}</td>
                            <td>
			        <span class="user" ${entity.user.id eq sc.userId ? "id=\"loggedUser\"" : ""}>
                        <html:img styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/${entity.user.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/><c:out value="${entity.user.name}" escapeXml="true"/>
			        </span>
                            </td>
                            <td>
                                    ${entity.task.name}&nbsp;[#${entity.task.number}]
                            </td>
                        </tr>
                    </c:forEach> &nbsp;
                </table>
            </div>
        </div>
    </tiles:put>
</tiles:insert>