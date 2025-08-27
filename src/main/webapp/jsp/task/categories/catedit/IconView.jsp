<%@ page buffer="128kb" errorPage="/jsp/Error.jsp"%>
<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="http://trackstudio.com" prefix="ts" %>
<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value="${sc.timezone}"/>
<I18n:setBundle basename="language"/>
<html>
    <head>
        <meta http-equiv="content-type" content="text/html; charset=<c:out value="${charSet}"/>">

        <ts:css request="${request}">
            <ts:cssLink link="style_src.css"/>
        </ts:css>
        <%--<link rel="stylesheet" type="text/css" href="<c:out value="${contextPath}${versionPath}"/>/style-print_src.css" media="print">--%>
    </head>
    <body>
    <c:forEach var="icon" items="${icons}">
        <div class="icon" onclick="parent.parent.opener.icon.value='<c:out value="${icon.name}"/>'; parent.window.close();">
        <html:img src="${contextPath}${ImageServlet}/icons/categories/${icon.name}"/><c:out value="${icon.name}"/>
            <ul>
                <c:forEach var="u" items="${icon.uses}">
                    <li><c:out value="${u}"/></li>
                </c:forEach>
            </ul>
        </div>
    </c:forEach>
    </body>
</html>
