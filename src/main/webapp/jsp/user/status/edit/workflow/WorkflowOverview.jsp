<%@ page buffer="128kb" errorPage="/jsp/Error.jsp" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/tiles-el" prefix="tiles" %>
<c:set var="taskMenu" value="false"/>
<c:set var="userMenu" value="true"/>
<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value="${sc.timezone}"/>
<I18n:setBundle basename="language"/>
<tiles:insert page="/jsp/layout/ListLayout.jsp" flush="true">

<tiles:put name="header" value="/jsp/user/UserHeader.jsp"/>
<tiles:put name="customHeader" value="/jsp/user/status/edit/workflow/WorkflowHeader.jsp"/>
<tiles:put name="tabs" value="/jsp/user/status/edit/workflow/WorkflowSubMenu.jsp"/>
<tiles:put name="main" type="string">
<div class="nblueborder">
    <div class="ncaption"></div>
    <div class="indent">
        <html:form method="POST" action="/UserWorkflowOverviewAction" styleId="checkunload"
                   onsubmit="return allow(this);">
            <html:hidden property="method" value="save" styleId="ruleId"/>
            <html:hidden property="id" value="${id}"/>
            <html:hidden property="session" value="${session}"/>
            <html:hidden property="prstatusId" value="${prstatusId}"/>
            <html:hidden property="workflowId" value="${workflowId}"/>
            <div class="general">
                <table class="general" cellpadding="0" cellspacing="0">
                    <caption>
                        <I18n:message key="PROPERTIES"/>
                    </caption>
                    <colgroup>
                        <col class="col_1">
                        <col class="col_2">
                    </colgroup>
                    <tr>
                        <th>
                            <I18n:message key="PRSTATUS"/>
                        </th>
                        <td>
                            <c:out value="${currentPrstatus.name}" escapeXml="true"/>
                        </td>
                    </tr>
                    <tr>
                        <th>
                            <I18n:message key="WORKFLOW"/>
                        </th>
                        <td>
                            <c:out value="${workflow.name}" escapeXml="true"/>
                        </td>
                    </tr>
                    <tr>
                        <th>
                            <I18n:message key="CONNECTED_TO"/>
                        </th>
                        <td>

                            <html:link styleClass="internal"
                                       href="${contextPath}/task/${connected.number}?thisframe=true">
                                <html:img styleClass="icon" border="0"
                                          src="${contextPath}${ImageServlet}/icons/categories/${connected.category.icon}"/>
                                <c:out value="${connected.name}" escapeXml="true"/>
                            </html:link>
                        </td>
                    </tr>
                </table>

                <table class="general" cellpadding="0" cellspacing="0">
                    <caption>
                        <I18n:message key="USER_MESSAGE_PERMISSIONS"/>
                    </caption>
                    <colgroup>
                        <col class="col_1">
                        <col class="col_2">
                    </colgroup>

                    <tr>
                        <th>
                            <I18n:message key="CAN_VIEW"/>
                        </th>
                        <td>
                            <c:forEach var="mstatus" items="${ruleViewAll}" varStatus="varC">
                                <c:if test="${varC.index > 0}">,</c:if>
                                <html:img src="${contextPath}${ImageServlet}/cssimages/ico.messagetypes.gif" hspace="0"
                                          vspace="0" border="0" align="middle"/>
                                <c:out value="${mstatus.name}" escapeXml="true"/>
                            </c:forEach>

                            <c:forEach var="mstatus" items="${ruleViewSubmitter}" varStatus="varC">
                                <c:if test="${varC.index > 0}">,</c:if>
                                <html:img src="${contextPath}${ImageServlet}/cssimages/ico.messagetypes.gif" hspace="0"
                                          vspace="0" border="0" align="middle"/>
                                <c:out value="${mstatus.name}" escapeXml="true"/> (* <I18n:message key="SUBMITTER"/>)
                            </c:forEach>

                            <c:forEach var="mstatus" items="${ruleViewHandler}" varStatus="varC">
                                <c:if test="${varC.index > 0}">,</c:if>
                                <html:img src="${contextPath}${ImageServlet}/cssimages/ico.messagetypes.gif" hspace="0"
                                          vspace="0" border="0" align="middle"/>
                                <c:out value="${mstatus.name}" escapeXml="true"/> (* <I18n:message key="HANDLER"/>)
                            </c:forEach>

                            <c:forEach var="mstatus" items="${ruleViewSAH}" varStatus="varC">
                                <c:if test="${varC.index > 0}">,</c:if>
                                <html:img src="${contextPath}${ImageServlet}/cssimages/ico.messagetypes.gif" hspace="0"
                                          vspace="0" border="0" align="middle"/>
                                <c:out value="${mstatus.name}" escapeXml="true"/> (* <I18n:message key="SUBMITTER"/>,
                                <I18n:message key="HANDLER"/>)
                            </c:forEach>
                        </td>
                    </tr>


                    <tr>
                        <th>
                            <I18n:message key="CAN_PROCESS"/>
                        </th>
                        <td>
                            <c:forEach var="mstatus" items="${ruleProcessAll}" varStatus="varC">
                                <c:if test="${varC.index > 0}">,</c:if>
                                <html:img src="${contextPath}${ImageServlet}/cssimages/ico.messagetypes.gif" hspace="0"
                                          vspace="0" border="0" align="middle"/>
                                <c:out value="${mstatus.name}" escapeXml="true"/>
                            </c:forEach>

                            <c:forEach var="mstatus" items="${ruleProcessSubmitter}" varStatus="varC">
                                <c:if test="${varC.index > 0}">,</c:if>
                                <html:img src="${contextPath}${ImageServlet}/cssimages/ico.messagetypes.gif" hspace="0"
                                          vspace="0" border="0" align="middle"/>
                                <c:out value="${mstatus.name}" escapeXml="true"/> (* <I18n:message key="SUBMITTER"/>)
                            </c:forEach>

                            <c:forEach var="mstatus" items="${ruleProcessHandler}" varStatus="varC">
                                <c:if test="${varC.index > 0}">,</c:if>
                                <html:img src="${contextPath}${ImageServlet}/cssimages/ico.messagetypes.gif" hspace="0"
                                          vspace="0" border="0" align="middle"/>
                                <c:out value="${mstatus.name}" escapeXml="true"/> (* <I18n:message key="HANDLER"/>)
                            </c:forEach>

                            <c:forEach var="mstatus" items="${ruleProcessSAH}" varStatus="varC">
                                <c:if test="${varC.index > 0}">,</c:if>
                                <html:img src="${contextPath}${ImageServlet}/cssimages/ico.messagetypes.gif" hspace="0"
                                          vspace="0" border="0" align="middle"/>
                                <c:out value="${mstatus.name}" escapeXml="true"/> (* <I18n:message key="SUBMITTER"/>,
                                <I18n:message key="HANDLER"/>)
                            </c:forEach>
                        </td>
                    </tr>


                    <tr>
                        <th>
                            <I18n:message key="CAN_BE_HANDLER"/>
                        </th>
                        <td>
                            <c:forEach var="mstatus" items="${ruleBeHandlerAll}" varStatus="varC">
                                <c:if test="${varC.index > 0}">,</c:if>
                                <html:img src="${contextPath}${ImageServlet}/cssimages/ico.messagetypes.gif" hspace="0"
                                          vspace="0" border="0" align="middle"/>
                                <c:out value="${mstatus.name}" escapeXml="true"/>
                            </c:forEach>

                            <c:forEach var="mstatus" items="${ruleBeHandlerSubmitter}" varStatus="varC">
                                <c:if test="${varC.index > 0}">,</c:if>
                                <html:img src="${contextPath}${ImageServlet}/cssimages/ico.messagetypes.gif" hspace="0"
                                          vspace="0" border="0" align="middle"/>
                                <c:out value="${mstatus.name}" escapeXml="true"/> (* <I18n:message key="SUBMITTER"/>)
                            </c:forEach>

                            <c:forEach var="mstatus" items="${ruleBeHandlerHandler}" varStatus="varC">
                                <c:if test="${varC.index > 0}">,</c:if>
                                <html:img src="${contextPath}${ImageServlet}/cssimages/ico.messagetypes.gif" hspace="0"
                                          vspace="0" border="0" align="middle"/>
                                <c:out value="${mstatus.name}" escapeXml="true"/> (* <I18n:message key="HANDLER"/>)
                            </c:forEach>

                            <c:forEach var="mstatus" items="${ruleBeHandlerSAH}" varStatus="varC">
                                <c:if test="${varC.index > 0}">,</c:if>
                                <html:img src="${contextPath}${ImageServlet}/cssimages/ico.messagetypes.gif" hspace="0"
                                          vspace="0" border="0" align="middle"/>
                                <c:out value="${mstatus.name}" escapeXml="true"/> (* <I18n:message key="SUBMITTER"/>,
                                <I18n:message key="HANDLER"/>)
                            </c:forEach>
                        </td>
                    </tr>

                </table>


                <table class="general" cellpadding="0" cellspacing="0">
                    <caption>
                        <I18n:message key="USER_CUSTOM_FIELD_PERMISSIONS"/>
                    </caption>
                    <colgroup>
                        <col class="col_1">
                        <col class="col_2">
                    </colgroup>

                    <tr>
                        <th>
                            <I18n:message key="CAN_VIEW"/>
                        </th>
                        <td>
                            <c:forEach var="udf" items="${viewUdfs}" varStatus="varC">
                                <c:out value="${udf.value}" escapeXml="true"/><c:if test="${!varC.last}">,</c:if>
                            </c:forEach>
                        </td>
                    </tr>


                    <tr>
                        <th>
                            <I18n:message key="CAN_EDIT"/>
                        </th>
                        <td>
                            <c:forEach var="udf" items="${editUdfs}" varStatus="varC">
                                <c:out value="${udf.value}" escapeXml="true"/><c:if test="${!varC.last}">,</c:if>
                            </c:forEach>
                        </td>
                    </tr>


                </table>


            </div>
        </html:form>
    </div>
</div>
</tiles:put>
</tiles:insert>