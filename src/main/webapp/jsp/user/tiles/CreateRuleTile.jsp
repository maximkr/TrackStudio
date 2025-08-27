<%@ page import="com.trackstudio.startup.I18n" %>
<%@ page buffer="128kb" errorPage="/jsp/Error.jsp" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="http://trackstudio.com" prefix="ts" %>
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
<html:hidden property="categoryId" value="${categoryId}"/>

<table class="general" cellpadding="0" cellspacing="0">
    <caption>
        <c:if test="${param.tileId == 'createRuleV'}">
            <c:out value="${msgHowCreateObject1}" escapeXml='false'/>
        </c:if>
        <c:if test="${param.tileId == 'createRuleE'}">
            <c:out value="${msgHowCreateObject2}" escapeXml='false'/>
        </c:if>
        <c:if test="${param.tileId == 'createRuleD'}">
            <c:out value="${msgHowCreateObject3}" escapeXml='false'/>
        </c:if>
        <c:if test="${param.tileId == 'createRuleH'}">
            <c:out value="${msgHowCreateObject4}" escapeXml='false'/>
        </c:if>
        <c:if test="${param.tileId == 'createRuleC'}">
            <c:out value="${msgHowCreateObject5}" escapeXml='false'/>
        </c:if>
    </caption>
    <tr>
    <th width="20%">
        <c:out value="${firstParamMsg}" escapeXml='false'/>
    </th>
    <td>
        <select name="<c:out value="${firstParamName}"/>" multiple size=5>
            <c:choose>
                <c:when test="${param.tileId == 'createRuleC'}">
                    <c:forEach var="var" items="${catsC}">
                        <option value="<c:out value="${var.id}"/>">
                            <c:out value="${var.name}" escapeXml="false"/>
                        </option>
                    </c:forEach>
                </c:when>
                <c:when test="${param.tileId == 'createRuleE'}">
                    <c:forEach var="var" items="${catsE}">
                        <option value="<c:out value="${var.id}"/>">
                            <c:out value="${var.name}" escapeXml="false"/>
                        </option>
                    </c:forEach>
                </c:when>
                <c:when test="${param.tileId == 'createRuleV'}">
                    <c:forEach var="var" items="${catsV}">
                        <option value="<c:out value="${var.id}"/>">
                            <c:out value="${var.name}" escapeXml="false"/>
                        </option>
                    </c:forEach>
                </c:when>
                <c:when test="${param.tileId == 'createRuleD'}">
                    <c:forEach var="var" items="${catsD}">
                        <option value="<c:out value="${var.id}"/>">
                            <c:out value="${var.name}" escapeXml="false"/>
                        </option>
                    </c:forEach>
                </c:when>
                <c:when test="${param.tileId == 'createRuleH'}">
                    <c:forEach var="var" items="${catsH}">
                        <option value="<c:out value="${var.id}"/>">
                            <c:out value="${var.name}" escapeXml="false"/>
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
            <c:choose>
                <c:when test="${param.tileId == 'createRuleC'}">
                    <input type="checkbox" name="submitterOnly"
                           value="<I18n:message key="TASK_SUBMITTER_ONLY"/>">&nbsp;
                    <I18n:message key="PARENT_TASK_SUBMITTER_ONLY"/>
                    <br>
                    <input type="checkbox" name="handlerOnly"
                           value="<I18n:message key="TASK_HANDLER_ONLY"/>">&nbsp;
                    <I18n:message key="PARENT_TASK_HANDLER_ONLY"/>
                </c:when>
                <c:otherwise>
                    <input type="checkbox" name="submitterOnly"
                           value="<I18n:message key="TASK_SUBMITTER_ONLY"/>">&nbsp;
                    <I18n:message key="TASK_SUBMITTER_ONLY"/>
                    <br>
                    <input type="checkbox" name="handlerOnly"
                           value="<I18n:message key="TASK_HANDLER_ONLY"/>">&nbsp;
                    <I18n:message key="TASK_HANDLER_ONLY"/>
                </c:otherwise>
            </c:choose>
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
