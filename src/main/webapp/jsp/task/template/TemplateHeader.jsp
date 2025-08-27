<%@ page buffer="128kb" errorPage="/jsp/Error.jsp"%>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<I18n:setLocale value='${sc.locale}'/>
<I18n:setTimeZone value='${sc.timezone}'/>
<I18n:setBundle basename='language'/>
<div class="elev" id="customHeader">
<div>
<html:img src="${contextPath}${ImageServlet}/cssimages/L.png"/><html:link  styleClass="ul"  href="${contextPath}/TemplateAction.do?method=page&id=${id}"><html:img  src="${contextPath}${ImageServlet}/cssimages/ico.template.gif" hspace="0" vspace="0" border="0" />
<I18n:message key="TEMPLATE"/>
</html:link> <span>:</span> 
<c:choose>
<c:when test="${template ne null}">
<html:link styleClass="internal" href="${contextPath}/TemplateViewAction.do?method=page&templateId=${template.id}&id=${id}">
<c:out value="${template.name}" escapeXml="true"/></html:link>
</c:when>
<c:otherwise>
<span class="createnew"><I18n:message key="TEMPLATE_ADD"/></span>
</c:otherwise>
</c:choose>
</div>
</div>