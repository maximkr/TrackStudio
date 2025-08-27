<%@ page import="com.trackstudio.app.session.SessionContext,
                 com.trackstudio.startup.I18n" %>
<%@ page buffer="128kb" errorPage="/jsp/Error.jsp" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="http://trackstudio.com" prefix="ts" %>
<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value="${sc.timezone}"/>
<I18n:setBundle basename="language"/>
<%
    if (request.getAttribute("firstParamName") == null)
        request.setAttribute("firstParamName", "name");
    if (request.getAttribute("firstParamMsg") == null)
        request.setAttribute("firstParamMsg", I18n.getString(((SessionContext) request.getAttribute("sc")).getLocale(), "NAME"));

%>
<div class="yellowbox" id="<c:out value="${param.tileId}"/>" style="display: none">
    <div class="general">
        <html:form method="POST" action="${createObjectAction}" onsubmit="return validate(this);">
            <html:hidden property="session" value="${session}"/>
            <html:hidden property="method" value="page"/>
            <html:hidden property="id" value="${id}"/>
            <html:hidden property="workflowId" value="${workflowId}"/>
            <table class="general" cellpadding="0" cellspacing="0">
                <caption>
                    <c:out value="${msgHowCreateObject}" escapeXml='false'/>
                </caption>
                <c:if test="${isTwoParams ne null}">
                    <colgroup>
                        <col class="col_1">
                        <col class="col_2">
                    </colgroup>
                </c:if>
                <tr>
                    <th>
                        <c:out value="${firstParamMsg}" escapeXml='false'/>
                    </th>
                    <td><input type="text" name="<c:out value="${firstParamName}"/>" size='50' alt=">0"></td>
                </tr>
                <tr>
                    <th>
                        <c:out value="${secondParamMsg}" escapeXml='false'/>
                    </th>
                    <td>
                        <select name="<c:out value="${secondParamName}"/>"
                                alt="mustChoose(<I18n:message key="NOT_CHOOSEN"/>)">
                            <option value="<I18n:message key="NOT_CHOOSEN"/>" selected>
                                <I18n:message
                                        key="CHOOSE_ONE"/>
                            </option>
                            <c:forEach var="var" items="${secondParamCollection}">
                                <option value="<c:out value="${var.id}"/>">
                                    <c:out value="${var.name}"
                                           escapeXml="false"/>
                                </option>
                            </c:forEach>
                        </select>
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
