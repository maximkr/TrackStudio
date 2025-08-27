<%@ page buffer="128kb" errorPage="/jsp/Error.jsp" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value="${sc.timezone}"/>
<I18n:setBundle basename="language"/>
<div class="elev" id="customHeader">
    <div>
        <html:img src="${contextPath}${ImageServlet}/cssimages/L.png"/>
        <html:link  styleClass="ul" href="${contextPath}/ReportAction.do?method=page&id=${id}">
            <html:img src="${contextPath}${ImageServlet}/cssimages/ico.reports.gif" hspace="0" vspace="0" border="0"/>
            <I18n:message key="REPORTS_LIST"/>
        </html:link>
        <span>:</span>
        <c:choose>
            <c:when test="${currentReport ne null}">
                <html:link styleClass="internal" href="${contextPath}/ReportViewAction.do?method=page&reportId=${currentReport.id}&id=${id}">
                    <c:out value="${currentReport.name}" escapeXml="true"/>
                </html:link>
            </c:when>
            <c:otherwise>
                <span class="createnew"><I18n:message key="REPORT_ADD"/></span>
            </c:otherwise>
        </c:choose>
    </div>
</div>