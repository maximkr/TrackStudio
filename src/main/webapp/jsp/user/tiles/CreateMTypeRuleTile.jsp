<%@ page import="com.trackstudio.startup.I18n" %>
<%@ page buffer="128kb" errorPage="/jsp/Error.jsp" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib prefix="ts" uri="http://trackstudio.com" %>
<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value='${sc.timezone}'/>
<I18n:setBundle basename="language"/>
<%
    if (request.getAttribute("firstParamName") == null)
        request.setAttribute("firstParamName", "name");
    if (request.getAttribute("firstParamMsg") == null)
        request.setAttribute("firstParamMsg", I18n.getString(((com.trackstudio.app.session.SessionContext) request.getAttribute("sc")).getLocale(), "NAME"));

%>
<div class="yellowbox" id="<c:out value="${param.tileId}"/>" style="display: none">
<div class="general">
<html:form method="POST" action="${createObjectAction}" onsubmit="return validate(this);">
<html:hidden property="session" value="${session}"/>
<html:hidden property="method" value="${param.tileId}"/>
<html:hidden property="id" value="${id}"/>
<html:hidden property="prstatusId" value="${prstatusId}"/>
<html:hidden property="workflowId" value="${workflowId}"/>
<html:hidden property="mstatusId" value="${mstatusId}"/>

<table class="general" cellpadding="0" cellspacing="0">
    <caption>
        <c:if test="${param.tileId == 'createRuleV'}">
            <c:out value="${msgHowCreateObject1}" escapeXml='false'/>
        </c:if>
        <c:if test="${param.tileId == 'createRuleP'}">
            <c:out value="${msgHowCreateObject2}" escapeXml='false'/>
        </c:if>
        <c:if test="${param.tileId == 'createRuleH'}">
            <c:out value="${msgHowCreateObject3}" escapeXml='false'/>
        </c:if>
    </caption>
    <tr>
    <th width="20%">
        <c:out value="${firstParamMsg}" escapeXml='false'/>
    </th>
    <td>
        <select name="<c:out value="${firstParamName}"/>" multiple size=5>
            <c:choose>
                <c:when test="${param.tileId == 'createRuleV'}">
                    <c:forEach var="var" items="${mtypesV}">
                        <option value="<c:out value="${var.id}"/>">
                            <c:out value="${var.mtype}" escapeXml="false"/>
                        </option>
                    </c:forEach>
                </c:when>
                <c:when test="${param.tileId == 'createRuleP'}">
                    <c:forEach var="var" items="${mtypesP}">
                        <option value="<c:out value="${var.id}"/>">
                            <c:out value="${var.mtype}" escapeXml="false"/>
                        </option>
                    </c:forEach>
                </c:when>
                <c:when test="${param.tileId == 'createRuleH'}">
                    <c:forEach var="var" items="${mtypesH}">
                        <option value="<c:out value="${var.id}"/>">
                            <c:out value="${var.mtype}" escapeXml="false"/>
                        </option>
                    </c:forEach>
                </c:when>
            </c:choose>
        </select>
    </td>
    <tr>
        <th>
            <I18n:message key="PRSTATUSES"/>
        </th>
        <td>

            <input type="checkbox" name="submitterOnly"
                   value="<I18n:message key="TASK_SUBMITTER_ONLY"/>">&nbsp;
            <I18n:message key="TASK_SUBMITTER_ONLY"/>
            <br>
            <input type="checkbox" name="handlerOnly"
                   value="<I18n:message key="TASK_HANDLER_ONLY"/>">&nbsp;
            <I18n:message key="TASK_HANDLER_ONLY"/>
        </td>
    </tr>
</table>

    <div class="controls">
    <input type="submit" class="iconized" value="<c:out value="${msgAddObject}"/>" name="createButton">
</div>
</html:form>
</div>
</div>
<c:set var="urlHtml" value="html"/>
<ts:js request="${request}" response="${response}">
    <ts:jsLink link="${urlHtml}/setDropdowns.js"/>
</ts:js>
