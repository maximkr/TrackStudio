<%@ page import="com.trackstudio.startup.I18n"%>
<%@ page buffer="128kb" errorPage="/jsp/Error.jsp"%>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="http://trackstudio.com" prefix="ts" %>
<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value="${sc.timezone}"/>
<I18n:setBundle basename="language"/>
<%
    if(request.getAttribute("firstParamName") == null)
        request.setAttribute("firstParamName", "name");
    if(request.getAttribute("firstParamMsg") == null)
        request.setAttribute("firstParamMsg", I18n.getString(((com.trackstudio.app.session.SessionContext)request.getAttribute("sc")).getLocale(), "NAME"));
%>
<div class="yellowbox" id="<c:out value="${param.tileId}"/>" style="display: none">
       <div class="general">
           <html:form method="POST" action="${createObjectAction}" onsubmit="return validate(this);">
               <html:hidden property="session" value="${session}"/>
               <html:hidden property="method" value="create"/>
               <html:hidden property="id" value="${id}"/>
    <div class="caption">
    <c:out value="${msgHowCreateObject}" escapeXml="false"/></div>
                   <div class="indent">
                           <input type="text" name="search${firstParamName}" style="padding:0;margin-left:-1px;height:15px;" class="form-autocomplete" size="30" onkeyup="__localsearch(this);">
                          <div class="selectbox">
                      <c:if test="${!(empty statusCollection)}">
                         <div class="optgroup"><I18n:message key="SUBORDINATED"/> <I18n:message key="PRSTATUS"/>
                            <c:forEach var="var" items="${statusCollection}" varStatus="c">
                              <label for="acl_${var.key.id}"  class="sel${c.index mod 2}">
                                <html:multibox property="${firstParamName}" value="PR_${var.key.id}" styleId="acl_${var.key.id}"/>
                                 <html:img styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/ico.status.gif"/><c:out
                                value="${var.key.name}" escapeXml="true"/> <c:if test="${!empty var.value}">(<c:forEach var="usr" varStatus="usrIndex" end="2" items="${var.value}">
                                <span class="user" ${usr.id eq sc.userId ? "id='loggedUser'" : ""}>
            <html:img styleClass="icon" border="0"
                      src="${contextPath}${ImageServlet}/cssimages/${usr.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
            <c:out value="${usr.name}" escapeXml="true"/>
			</span><c:if test="${!usrIndex.last}">,</c:if>
                            </c:forEach>)</c:if>
                            </label>
                            </c:forEach>
                             </div>
                        </c:if>
                    <c:if test="${!(empty userCollection)}">
                            <div class="optgroup"><I18n:message key="USER"/>
                            <c:forEach var="var" items="${userCollection}" varStatus="c">
                                <label for="acl_${var.id}"  class="sel${c.index mod 2}">
                                <html:multibox property="${firstParamName}" value="${var.id}" styleId="acl_${var.id}"/>
                                <span class="user" ${var.id eq sc.userId ? "id='loggedUser'" : ""}>
            <html:img styleClass="icon" border="0"
                      src="${contextPath}${ImageServlet}/cssimages/${var.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
            <c:out value="${var.name}" escapeXml="true"/>&nbsp;[<c:out value="${var.prstatus.name}" escapeXml="true"/>]
			</span>
                                    </label>
                                    </c:forEach>
                            </div>
                        </c:if>
                       </div>
             </div>
               <div class="controls">
                   <input type="submit"  class="iconized" value="<c:out value="${msgAddObject}"/>" name="createButton">
               </div>
           </html:form>
       </div>
       </div>
        <c:set var="urlHtml" value="html"/>
<ts:js request="${request}" response="${response}">
    <ts:jsLink link="${urlHtml}/setDropdowns.js"/>
</ts:js>
