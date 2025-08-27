<%@ page buffer="128kb" errorPage="/jsp/Error.jsp"%>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<I18n:setLocale value='${sc.locale}'/>
<I18n:setTimeZone value='${sc.timezone}'/>
<I18n:setBundle basename='language'/>
<div class="elev" id="customHeader">

<div>
<html:img src="${contextPath}${ImageServlet}/cssimages/L.png"/><html:link  styleClass="ul"  href="${contextPath}/TaskSubscribeAction.do?method=page&id=${id}"><html:img  src="${contextPath}${ImageServlet}/cssimages/ico.subscription.gif" hspace="0" vspace="0" border="0" />
<I18n:message key="SUBSCRIPTION"/>
</html:link> <span>:</span> 
<c:choose>
<c:when test="${subscription ne null}">
<html:link styleClass="internal" href="${contextPath}/TaskSubscribeViewAction.do?method=page&subscriptionId=${subscription.id}&id=${id}">
<c:out value="${subscription.name}"/></html:link>
</c:when>
<c:otherwise>
<span class="createnew">
        <I18n:message key="SUBSCRIPTION_ADD"/>
</span>
</c:otherwise>
</c:choose>
</div>
</div>