<%@ page buffer="128kb" errorPage="/jsp/Error.jsp" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value="${sc.timezone}"/>
<I18n:setBundle basename="language"/>
<div class="elev">
    <div>
        <html:img src="${contextPath}${ImageServlet}/cssimages/L.png"/>
        <html:link styleClass="ul" href="${contextPath}/MailImportAction.do?method=page&id=${id}">
            <html:img src="${contextPath}${ImageServlet}/cssimages/ico.emailimport.gif" hspace="0" vspace="0" border="0"/>
            <I18n:message key="EMAIL_IMPORT_LIST"/>
        </html:link>
        <span>:</span>
        <c:choose>
            <c:when test="${mailImport ne null}">
                <html:link styleClass="internal"
                        href="${contextPath}/MailImportViewAction.do?method=page&mailImportId=${mailImport.id}&id=${id}">
                    <c:out value="${mailImport.name}" escapeXml="true"/>
                </html:link>
            </c:when>
            <c:otherwise>
                <span class="createnew"><I18n:message key="MAIL_IMPORT_RULE_ADD"/></span>
            </c:otherwise>
        </c:choose>
    </div>
</div>