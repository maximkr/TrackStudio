<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "${contextPath}/strict.dtd">
<%@ page buffer="128kb" errorPage="/jsp/Error.jsp" %>
<%@ page session="false" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://trackstudio.com" prefix="ts" %>
<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value="${sc.timezone}"/>
<I18n:setBundle basename="language"/>
<html>

<head>
    <ts:css request="${request}">
                <ts:cssLink link="style_src.css"/>
                <c:set var="urlHtml" value="html"/>
    </ts:css>
    <%--<link rel="stylesheet" type="text/css" href="<c:out value="${contextPath}${versionPath}"/>/style-print_src.css" media="print">--%>
    <ts:js request="${request}" response="${response}">
        <ts:jsLink link="${urlHtml}/validate.js"/>
        <ts:jsLink link="${urlHtml}/quickSelect.js"/>
        <ts:jsLink link="${urlHtml}/tsmenu/tsmenu.js"/>
	    <ts:jsLink link="${urlHtml}/jquery/jquery-4.0.0.min.js"/>
	    <ts:jsLink link="${urlHtml}/jquery/jquery-ui.min.js"/>
    </ts:js>

    <script type="text/javascript">
        self.top.currentURL = '<c:out value='${currentURL}' escapeXml='false'/>';
        var selectedId = getTabCookie("selectedId");
        var udffield = getTabCookie("udffield");

        window.onload = function() {}

        var FuncContainer = function() {}

        var tsMenuDefImagePath = '<c:out value="${contextPath}"/>/cssimages/';
        var tsMenuImagePath = '<c:out value="${contextPath}"/>/cssimages/';
        var frameLoaded = "loaded";
    </script>
</head>
<body>
<div class="controls">
    <span style="float: left"><img src="${contextPath}${ImageServlet}/cssimages/ico.customfields.gif" alt="" class="icon"/><c:out value="${currentUDF}"/></span>
            <input type="submit" class="iconized"
                   value="<I18n:message key="SUBMIT"/>"
                   onclick="window.opener.document.getElementById(udffield).value=forhuman(); closeServicePanel(document.forms['userListForm'].elements['SELUSER']); window.close();"
                   name="SUBMIT">
            <input type="submit" class="iconized secondary"
                   value="<I18n:message key="CANCEL"/>"
                   onclick="closeServicePanel(document.forms['userListForm'].elements['SELUSER']); window.close();"
                   name="deleteButton">

        </div>
<div class="logopath">
<c:forEach var="user" items="${currentUser.ancestors}" varStatus="varCounter">
    <html:link styleClass="internal" href="${contextPath}/UserSelectAction.do?method=page&amp;id=${user.id}" title="${user.login}">
    <html:img  styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/${user.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
    <c:out value="${user.name}" escapeXml="true"/>
    </html:link>
</c:forEach>
    </div>
<div class="taskTitle">
    <html:link styleClass="internal" href="${contextPath}/UserSelectAction.do?method=page&amp;id=${currentUser.id}" title="${currentUser.login}">
    <html:img  styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/${currentUser.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
    <c:out value="${currentUser.name}" escapeXml="true"/>
    </html:link>
    </div>
<div id="servicePanel" class="${selectedIds!=null && !empty selectedIds ? "norm" : "closed"}">
    <span>
    <img id="windowhideicon" src="${contextPath}${ImageServlet}/cssimages/ico.hidewin.gif" class="icon" onclick="hideServicePanel();" title="<I18n:message key="HIDE"/>">
    <img id="windowopenicon" src="${contextPath}${ImageServlet}/cssimages/ico.openwin.gif" class="icon" onclick="hideServicePanel();" title="<I18n:message key="OPEN"/>">
    <img id="windowcloseicon" src="${contextPath}${ImageServlet}/cssimages/ico.closewin.gif" class="icon" onclick="closeServicePanel(document.forms['userListForm'].elements['SELUSER']);" title="<I18n:message key="CLOSE"/>">
        </span>
    <c:forEach items="${selectedIds}" var="st">
        <label id="_spi_<c:out value="${st.id}"/>" onclick="placeOnServicePanel('<c:out value="${st.id}"/>','<c:out value="${st.login}"/>');" style="padding-top: 1px; padding-bottom: 1px; font-family: Verdana; font-size: 11px; font-weight: bold;" title="[${st.login}] <c:out value="${st.name}"/>" for="<c:out value="${st.login}"/>">[<c:out value="${st.login}"/>] <c:choose><c:when test="${fn:length(st.name)>23}">${fn:substring(st.name,0 ,20)}...</c:when><c:otherwise><c:out value="${st.name}"/></c:otherwise></c:choose></label>
        </c:forEach>

</div>
<script type="text/javascript">
        var servicePanelSrc = null;
    </script>


    <div class="blueborder">
    <div class="caption">
        <span><I18n:message key="USERS"/>:</span>
        <span title="<c:out value="${filter.description}"/>"><c:out value="${filter.name}"/></span>
    </div>
    <div class="controlPanel">
        <c:if test="${!empty additionalFilters}">
            <c:set var="currentFlt" value="false"/>
            <script type="text/javascript">
                var filterMenu = new TSMenu();
                filterMenu.width = 320;
                var filterMenuTitle = '<I18n:message key="OTHER_FILTERS"/>';
                <c:forEach items="${additionalFilters}" var="filt">
                <c:choose>
                <c:when test="${filt.id eq filter.id}">
                filterMenuTitle = '<c:out value="${filt.name}"/>';
                <c:set var="currentFlt" value="true"/>
                </c:when>
                <c:otherwise>
                filterMenu.add(new TSMenuItem("<c:out value="${filt.name}"/>", "<c:out value="${contextPath}"/>/UserSelectAction.do?method=page&id=${id}&filter=${filt.id}&go=true", false, false, "<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.filter.gif"));
                </c:otherwise>
                </c:choose>
                </c:forEach>
            </script>
        </c:if>
        <c:forEach items="${filters}" var="filt">
            <c:choose>
                <c:when test="${filt.id eq filter.id}">
                    <html:link title="${filt.description}" styleClass="selected"
                               href="${contextPath}/UserSelectAction.do?method=page&id=${id}&filter=${filt.id}&go=true">
                        <html:img src="${contextPath}${ImageServlet}/cssimages/ico.filter.gif" border="0"/>
                        <c:out value="${filt.name}"/>
                    </html:link>
                </c:when>
                <c:otherwise>
                    <html:link styleClass="internal" title="${filt.description}"
                               href="${contextPath}/UserSelectAction.do?method=page&id=${id}&filter=${filt.id}&go=true">
                        <html:img src="${contextPath}${ImageServlet}/cssimages/ico.filter.gif" border="0"/>
                        <c:out value="${filt.name}"/>
                    </html:link>
                </c:otherwise>
            </c:choose>
        </c:forEach>
        <c:if test="${!empty additionalFilters}">
        <span class="${currentFlt eq true ? 'additional selected' : 'additional'}">
        <script type="text/javascript">
            var filterBar = new TSMenuBar();
            filterBar.add(new TSMenuBut(filterMenuTitle, null, filterMenu, "<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.filter.gif"));
            document.write(filterBar);
        </script>
        </span>
        </c:if>
    </div>
    <div class="indent">
    <html:form method="post" action="/UserSelectAction">
    <html:hidden property="method" value="page"/>
    <html:hidden property="collector"/>
    <html:hidden property="id" value="${id}"/>
    <html:hidden property="session" value="${session}"/>
    <table class="general" cellpadding="0" cellspacing="0">
    <tr class="wide">
    <th width="5%" style="white-space:nowrap">
        <span style="text-align: center;">
            <input type="checkbox" onClick="_selectAll(this,document.forms['userListForm'].elements['SELUSER']);">
        </span>
    </th>


        <th width="10%">
                <I18n:message key="LOGIN"/>
        </th>

        <th width="85%">
                <I18n:message key="USER_NAME"/>
        </th>
    </tr>
    <c:if test="${!(empty  userLines)}">
    <c:forEach var="userLine" items="${userLines}" varStatus="status">
           <tr class="line<c:out value="${status.index mod 2}"/>">
    <td class="top" style="text-align: center">
        <html:hidden property="USERIDS" value="${userLine.id}"/>
            <input type="checkbox" name="SELUSER" alt="delete1"
                   title="#${userLine.login}"
                   value="<c:out value="${userLine.id}"/>"
                   onclick="this.checked = placeOnServicePanel('<c:out value="${userLine.id}" escapeXml="true"/>','<c:out value="${userLine.login}" escapeXml="true"/>','<c:out value="${userLine.name}" escapeXml="true"/>')">
    </td>
        <td><a class="internal" id="${userLine.id}-number" href="${contextPath}/UserSelectAction.do?method=page&id=${userLine.id}">
            <c:out value="${userLine.login}" escapeXml="true"/>
        </a></td>
        <td>
            <html:link styleClass="internal" styleId="${userLine.id}-name"
                       href="${contextPath}/UserSelectAction.do?method=page&amp;id=${userLine.id}"
                    ><html:img  styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/${userLine.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/><c:out value="${userLine.name}" escapeXml="true"/>
            </html:link>
        </td>
    </tr>
        </c:forEach>
        </c:if>
    </table>
    <c:out value="${slider}" escapeXml="false"/>

<script type="text/javascript">
            servicePanelSrc = document.forms["userListForm"].elements["SELUSER"];
        fillFormServicePanel(document.forms["userListForm"].elements["SELUSER"]);
        </script>
        <div class="controls">
            <input type="submit" class="iconized"
                   value="<I18n:message key="SUBMIT"/>"
                   onclick="window.opener.document.getElementById(udffield).value=forhuman(); closeServicePanel(document.forms['userListForm'].elements['SELUSER']); window.close();"
                   name="SUBMIT">
            <input type="submit" class="iconized secondary"
                   value="<I18n:message key="CANCEL"/>"
                   onclick="closeServicePanel(document.forms['userListForm'].elements['SELUSER']); window.close();"
                   name="deleteButton">

        </div>
   </html:form>
    </div>
    </div>
</body>
</html>
