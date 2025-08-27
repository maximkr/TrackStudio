<%@ page buffer="128kb" errorPage="/jsp/Error.jsp"%>
<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/tiles-el" prefix="tiles" %>
<I18n:setLocale value='${sc.locale}'/>
<I18n:setTimeZone value='${sc.timezone}'/>
<I18n:setBundle basename='language'/>
<tiles:insert page="/jsp/layout/ListLayout.jsp" flush="true">
    <tiles:put name="header" value="/jsp/task/TaskHeader.jsp"/>
    <tiles:put name="customHeader" type="string"/>
    <tiles:put name="tabs" type="string"/>
    <tiles:put name="main" type="string">
        <div class="blueborder">
            <div class="caption">
                <I18n:message key="LIST_TIRGGERS"/>
            </div>
            <div class="indent">
                <c:forEach var="entity" items="${map}" varStatus="varCounter">
                    <c:set var="scripts" value="${entity.value}"/>
                    <table class="general">
                        <caption>
                            <c:out value="${entity.key}"/>
                        </caption>
                        <tr class="wide">
                            <th width="30%"><I18n:message key="NAME"/></th>
                            <th width="30%"><I18n:message key="TYPE"/></th>
                            <th width="30%"><I18n:message key="CONNECTED_TO"/></th>
                        </tr>
                        <c:forEach var="script" items="${scripts}" varStatus="varCounter">
                            <tr class="line<c:out value="${varCounter.index mod 2}"/>">
                                <td>
                                    <c:if test="${!script.exist}">
                                        <img border="0" hspace="0" vspace="0" title="<I18n:message key="SCRIPT_NOT_IS_EXIST"/>" src="<c:out value="${contextPath}"/>${ImageServlet}/cssimages/warning.gif"/>
                                    </c:if>
                                        ${script.name}
                                </td>
                                <td>${script.type}</td>
                                <td>${script.connectedTo}</td>
                            </tr>
                        </c:forEach> &nbsp;
                    </table>
                </c:forEach>
            </div>
        </div>
        <br/>
        <div class="blueborder">
            <div class="caption">
                <I18n:message key="SCHEDULERS"/>
            </div>
            <div class="indent">
                <table class="general">
                    <tr class="wide">
                        <th width="30%"><I18n:message key="NAME"/></th>
                        <th width="30%"><I18n:message key="LOG"/></th>
                        <th width="30%"><I18n:message key="TEST"/></th>
                    </tr>
                    <c:forEach var="scheduler" items="${schedulers}">
                        <tr>
                            <td><c:out value="${scheduler.className}"/></td>
                            <td id="${scheduler.className}">&nbsp;</td>
                            <td><input type="button" onclick="testScheduler('${scheduler.className}'); return false;" value="<I18n:message key="TEST"/>"/></td>
                        </tr>
                    </c:forEach>
                </table>
            </div>
        </div>
        <br/>
        <div class="blueborder">
            <div class="caption">
                <I18n:message key="SCRIPT_LOAD_LOG"/>
            </div>
            <div class="indent">
                <div class="selectbox">
                    <c:forEach var="entry" items="${scriptLoadLog}">
                        <jsp:useBean id="dateValue" class="java.util.Date"/>
                        <jsp:setProperty name="dateValue" property="time" value="${entry.key}"/>
                        <div style="margin-left: 10px; margin-top: 10px; margin-bottom: 10px;">
                            <b><I18n:formatDate value="${dateValue}" type="both" dateStyle="short" timeStyle="short"/></b><br/>
                            <c:forEach var="file" items="${entry.value}">
                                ${file}<br/>
                            </c:forEach>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>
    </tiles:put>
</tiles:insert>

