<%@ page buffer="128kb" errorPage="/jsp/Error.jsp"%>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<I18n:setLocale value='${sc.locale}'/>
<I18n:setTimeZone value='${sc.timezone}'/>
<I18n:setBundle basename='language'/>
<c:set var="mstatusUrl" value="${contextPath}/MstatusAction.do?method=page&id=${id}&workflowId=${flow.id}"/>
<c:if test="${!tabEdit.allowed}">
    <c:set var="mstatusUrl" value="${contextPath}/WorkflowViewAction.do?method=page&workflowId=${flow.id}&id=${id}"/>
</c:if>
<div class="elev" id="customHeader">

<div>
<html:img src="${contextPath}${ImageServlet}/cssimages/L.png"/><html:link  styleClass="ul"  href="${contextPath}/WorkflowAction.do?method=page&id=${id}"><html:img  src="${contextPath}${ImageServlet}/cssimages/ico.workflow.gif" hspace="0" vspace="0" border="0" />
<I18n:message key="WORKFLOWS"/>
</html:link> <span>:</span> 
<html:link styleClass="internal" href="${contextPath}/WorkflowViewAction.do?method=page&workflowId=${flow.id}&id=${id}" >
        <c:out value="${flow.name}" escapeXml="true"/></html:link>
</div>
<div>
<html:img src="${contextPath}${ImageServlet}/cssimages/blank.png"/><html:img src="${contextPath}${ImageServlet}/cssimages/L.png"/><html:link  styleClass="ul"  href="${mstatusUrl}"><html:img  src="${contextPath}${ImageServlet}/cssimages/ico.messagetypes.gif" hspace="0" vspace="0" border="0" />
<I18n:message key="MESSAGE_TYPES"/>
</html:link> <span>:</span> 

<c:choose>
<c:when test="${mstatus ne null}">
<html:link styleClass="internal" href="${contextPath}/MstatusViewAction.do?method=page&mstatusId=${mstatus.id}&id=${id}&workflowId=${flow.id}">
<c:out value="${mstatus.name}" escapeXml="true"/></html:link>
</c:when>
<c:otherwise>
<span class="createnew"><I18n:message key="MESSAGE_TYPE_ADD"/></span>
</c:otherwise>
</c:choose>
</div>
<c:if test="${udf ne null}">    
<div>
<html:img src="${contextPath}${ImageServlet}/cssimages/blank.png"/>
<html:img src="${contextPath}${ImageServlet}/cssimages/blank.png"/><html:img src="${contextPath}${ImageServlet}/cssimages/L.png"/>
<html:link  styleClass="ul"  href="${contextPath}/UdfPermissionAction.do?method=page&mstatusId=${mstatus.id}&id=${id}&workflowId=${flow.id}"><html:img  src="${contextPath}${ImageServlet}/cssimages/ico.customfields.gif" hspace="0" vspace="0" border="0" />
<I18n:message key="CUSTOM_FIELDS"/>
</html:link><span>:</span>
<html:link styleClass="internal" href="${contextPath}/WorkflowUdfViewAction.do?method=page&udfId=${udf.id}&id=${id}&workflowId=${flow.id}">
<c:out value="${udf.caption}" escapeXml="true"/></html:link>
</div>
</c:if>
</div>
