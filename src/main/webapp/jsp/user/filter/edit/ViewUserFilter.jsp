<%@ page buffer="128kb" errorPage="/jsp/Error.jsp"%>
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
    <tiles:put name="customHeader" value="/jsp/user/filter/UserFilterHeader.jsp"/>
        <tiles:put name="tabs" type="string"/>
    
    <tiles:put name="main" type="string">
    <div class="blueborder">
    <div class="caption"><I18n:message key="USER_FILTER_OVERVIEW"/></div>
        <c:if test="${canEdit}">
    <div class="controlPanel">

        <html:link href="${contextPath}/UserFilterEditAction.do?method=page&filterId=${filterId}&id=${id}">
        <html:img src="${contextPath}${ImageServlet}/cssimages/ico.edit.gif"/><I18n:message key="USER_FILTER_EDIT"/>
            </html:link>

        <html:link href="${contextPath}/UserFilterSettingsAction.do?method=page&filterId=${filterId}&id=${id}">
        <html:img src="${contextPath}${ImageServlet}/cssimages/ico.edit.gif"/><I18n:message key="USER_FILTER_USER_PARAMETERS"/>
         </html:link>

    </div>
    </c:if>
    <div class="indent">

<table class="general" cellpadding="0" cellspacing="0">
<caption>
<I18n:message key="USER_FILTER_PROPERTIES"/>
</caption>
<COLGROUP>
<COL class="col_1">
<COL class="col_2">
</COLGROUP>
<tr>
<th>
<I18n:message key="NAME"/>
</th>
<td><c:out value="${currentFilter.name}"/></td>
</tr>
<tr>
<th>
<I18n:message key="DESCRIPTION"/>
</th>
<td><c:out value="${currentFilter.description}"/></td>
</tr>
<tr>
<th>
<I18n:message key="SHARE"/>
</th>
<td>
<c:choose>
<c:when test="${currentFilter.priv ne true}">
<html:img alt=""  src="${contextPath}${ImageServlet}/cssimages/ico.checked.gif"/>
</c:when>
<c:otherwise>
    <html:img alt=""  src="${contextPath}${ImageServlet}/cssimages/ico.unchecked.gif"/>
</c:otherwise>
</c:choose>
</td>
</tr>
<tr>
    <th>
        <I18n:message key="SHOW_IN_TOOLBAR"/>
    </th>
    <td>
        <c:choose>
            <c:when test="${showInToolbar ne true}">
                <html:img src="${contextPath}${ImageServlet}/cssimages/ico.unchecked.gif"/>
            </c:when>
            <c:otherwise>
                <html:img src="${contextPath}${ImageServlet}/cssimages/ico.checked.gif"/>
            </c:otherwise>
        </c:choose>
    </td>
</tr>

<tr>
    <th><I18n:message key="OWNER"/></th>
    <td>
        <span class="user" ${currentFilter.owner.id eq sc.userId ? "id='loggedUser'" : ""}>
            <html:img  styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/${currentFilter.owner.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
            <c:out value="${currentFilter.owner.name}" escapeXml="true"/>
        </span>
    </td>
</tr>
<c:if test="${!empty onpage}">
<tr>
<th>
<I18n:message key="USERS_ON_PAGE"/>
</th>
<td><c:out value="${onpage}" escapeXml='false'/>
</td>
</tr>
</c:if>
<tr>
<th>
<I18n:message key="DEEP_SEARCH"/>
</th>
<td>
    <c:choose>
    <c:when test="${search ne true}">
    <html:img alt=""  src="${contextPath}${ImageServlet}/cssimages/ico.unchecked.gif"/>
    </c:when>
    <c:otherwise>
    <html:img alt=""  src="${contextPath}${ImageServlet}/cssimages/ico.checked.gif"/>
    </c:otherwise>
    </c:choose>
</td>
</tr>

</table>
<br>

<table class="general" cellpadding="0" cellspacing="0">
<caption>
<I18n:message key="USER_FILTER_USER_PARAMETERS"/>
</caption>
<c:choose>
<c:when test="${(!empty display) || (!empty filter)}">
<COLGROUP>
<COL class="col_1">
<COL class="col_2">
</COLGROUP>
 <tr>
    <th><I18n:message key="USE_FIELDS"/></th>
    <td><ul>
        <c:forEach items="${display}" var="item">
        <li><c:out value="${item}" escapeXml="false"/></li>
        </c:forEach>
        </ul>
</td>
</tr>
 <c:forEach items="${filter}" var="item" varStatus="varCounter">
<tr class="line<c:out value="${varCounter.index mod 2}"/>">
    <th><c:out value="${item.key}"/></th>
    <td><c:out value="${item.value}" escapeXml="false" /></td>
</tr>
</c:forEach>
<c:if test="${!empty sort}">
<tr>
    <th><I18n:message key="SORT_FIELDS"/></th>
    <td><ul>
        <c:forEach items="${sort}" var="item">
        <li><c:out value="${item}" escapeXml="false"/></li>
        </c:forEach>
        </ul>
</td>
</tr>
</c:if>
</c:when>
<c:otherwise>
<tr class="wide">
<td><span style="text-align: center;"><I18n:message key="EMPTY_USER_FILTER_PARAMETERS_LIST"/></span></td>
</tr>
</c:otherwise>
</c:choose>
</table>

    </div>
    </div>
</tiles:put>
</tiles:insert>
