<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/tiles-el" prefix="tiles" %>
<c:set var="taskMenu" value="false"/>
<c:set var="userMenu" value="true"/>
<I18n:setLocale value='${sc.locale}'/>
<I18n:setTimeZone value='${sc.timezone}'/>
<I18n:setBundle basename='language'/>
<tiles:insert page="/jsp/layout/ListLayout.jsp" flush="true">

    <tiles:put name="customHeader" value="/jsp/user/status/StatusHeader.jsp"/>
    <tiles:put name="header" value="/jsp/user/UserHeader.jsp"/>
    <tiles:put name="tabs" value="/jsp/user/status/StatusSubMenu.jsp"/>
    <tiles:put name="main" type="string">
        <c:if test="${canView}">
            <div class="nblueborder">
                <div class="ncaption"></div>
                <div class="indent">
                    <html:form method="POST" action="/UserWorkflowSecurityAction" styleId="checkunload"
                               onsubmit="return validate(this);">
                        <html:hidden property="method" value="save"/>
                        <html:hidden property="session" value="${session}"/>
                        <html:hidden property="prstatusId" value="${prstatusId}"/>
                        <html:hidden property="id" value="${id}"/>
                        <div class="general">
                            <table class="general" cellpadding="0" cellspacing="0">
                                <tr class="wide">
                                    <th><I18n:message key="WORKFLOW"/></th>
                                    <th><I18n:message key="CATEGORIES"/></th>
                                    <th><I18n:message key="CAN_PROCESS"/></th>
                                </tr>
                                <c:forEach var="workflow" items="${workflowList}" varStatus="varCounter">
                                    <tr class="line<c:out value="${varCounter.index mod 2}"/>">
                                        <td width="10%">
                                            <html:link styleClass="internal"
                                                       href="${contextPath}/UserWorkflowOverviewAction.do?method=page&amp;workflowId=${workflow.id}&amp;prstatusId=${prstatusId}&amp;id=${id}">
                                                <img title="<I18n:message key="OBJECT_PROPERTIES_VIEW"/>" border="0"
                                                     hspace="0" vspace="0"
                                                     src="${contextPath}${ImageServlet}/cssimages/ico.workflow.gif"/><c:out
                                                    value="${workflow.name}" escapeXml='true'/>
                                            </html:link>
                                        </td>
                                        <td><c:forEach var="cat" items="${workflow.categories}" varStatus="varC"><c:if
                                                test="${varC.index > 0}">, </c:if><html:img
                                                src="${contextPath}${ImageServlet}/cssimages/ico.categories.gif"
                                                hspace="0" vspace="0" border="0"/><c:out
                                                value="${cat.name}"/></c:forEach>
                                        </td>
                                        <td>
                                            <c:forEach var="mstatus" items="${workflow.processAll}" varStatus="varC">
                                                <c:if test="${varC.index > 0}">,</c:if>
                                                <html:img
                                                        src="${contextPath}${ImageServlet}/cssimages/ico.messagetypes.gif"
                                                        hspace="0" vspace="0" border="0" align="middle"/><c:out
                                                    value="${mstatus.name}" escapeXml="true"/>
                                            </c:forEach>
                                            <c:forEach var="mstatus" items="${workflow.processSubmitter}"
                                                       varStatus="varC">
                                                <c:if test="${varC.index > 0}">,</c:if>
                                                <html:img
                                                        src="${contextPath}${ImageServlet}/cssimages/ico.messagetypes.gif"
                                                        hspace="0" vspace="0" border="0" align="middle"/><c:out
                                                    value="${mstatus.name}" escapeXml="true"/> (* <I18n:message
                                                    key="SUBMITTER"/>)
                                            </c:forEach>
                                            <c:forEach var="mstatus" items="${workflow.processHandler}"
                                                       varStatus="varC">
                                                <c:if test="${varC.index > 0}">,</c:if>
                                                <html:img
                                                        src="${contextPath}${ImageServlet}/cssimages/ico.messagetypes.gif"
                                                        hspace="0" vspace="0" border="0"
                                                        align="middle"/>
                                                <c:out value="${mstatus.name}" escapeXml="true"/> (* <I18n:message
                                                    key="HANDLER"/>)
                                            </c:forEach>
                                            <c:forEach var="mstatus" items="${workflow.processSAH}" varStatus="varC">
                                                <c:if test="${varC.index > 0}">,</c:if>
                                                <html:img
                                                        src="${contextPath}${ImageServlet}/cssimages/ico.messagetypes.gif"
                                                        hspace="0" vspace="0" border="0" align="middle"/><c:out
                                                    value="${mstatus.name}" escapeXml="true"/> (* <I18n:message
                                                    key="SUBMITTER"/>, <I18n:message key="HANDLER"/>)
                                            </c:forEach>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </table>
                        </div>
                    </html:form>
                </div>
            </div>
        </c:if>
    </tiles:put>
</tiles:insert>
