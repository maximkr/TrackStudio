<%@ page buffer="128kb" errorPage="/jsp/Error.jsp"%>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<I18n:setLocale value='${sc.locale}'/>
<I18n:setTimeZone value='${sc.timezone}'/>
<I18n:setBundle basename='language'/>
<div class="elev" id="customHeader">
<html:img alt="" src="${contextPath}${ImageServlet}/cssimages/L.png"/><html:link  styleClass="ul"  href="${contextPath}/TaskViewAction.do?method=page&amp;id=${id}"><html:img  src="${contextPath}${ImageServlet}/cssimages/ico.attachment.png" hspace="0" vspace="0" border="0"/>
<I18n:message key="ATTACHMENT"/></html:link>
 <span>:</span>
<c:out value="${attachment.name}" escapeXml="true"/>
</div>
<br>