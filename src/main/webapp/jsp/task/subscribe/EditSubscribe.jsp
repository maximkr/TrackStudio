<%@ page buffer="128kb" errorPage="/jsp/Error.jsp" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/tiles-el" prefix="tiles" %>


<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value="${sc.timezone}"/>
<I18n:setBundle basename="language"/>
<tiles:insert page="/jsp/layout/ListLayout.jsp" flush="true">
    <tiles:put name="title" value="${title}"/>

    <tiles:put name="header" value="/jsp/task/TaskHeader.jsp"/>
    <tiles:put name="customHeader" value="/jsp/task/subscribe/SubscribeHeader.jsp"/>

    <tiles:put name="tabs" type="string"/>
    <tiles:put name="main" type="string">
        <script type="text/javascript">
            function updateFilterDiv() {
                var params = {method : 'updateFilter', user: $('#user').val(), filterId: '${filterId}', taskId : ${id}};
                $.ajax('${contextPath}/TaskSubscribeEditAction.do', {
                    data : params,
                    success: function(data) {
                        $('#filterDiv').html(data);
                    }
                });
            }
        </script>
        <div class="blueborder">
            <div class="caption">
                <c:choose>
                    <c:when test="${subscription eq null}">
                        <I18n:message key="SUBSCRIPTION_ADD"/>
                    </c:when>
                    <c:otherwise>
                        <I18n:message key="SUBSCRIPTION_EDIT"/> <c:out value="${subscription.name}"/>
                    </c:otherwise>
                </c:choose>
            </div>
            <div class="indent">
                <html:form method="POST" action="/TaskSubscribeEditAction" onsubmit="return validate(this);">
                    <html:hidden property="method" value="addSubscribe" styleId="subscribeListId"/>
                    <html:hidden property="id" value="${id}"/>
                    <html:hidden property="session" value="${session}"/>
                    <html:hidden property="subscriptionId" value="${subscriptionId}"/>
                    <html:hidden property="oldFilterId" value="${filterId}"/>

                    <div class="general">
                        <table class="general" cellpadding="0" cellspacing="0">
                            <COLGROUP>
                                <COL class="col_1">
                                <COL class="col_2">
                            </COLGROUP>
                            <tr>
                                <th><I18n:message key="NAME"/></th>
                                <td>
                                    <html:text styleId="name" property="name" size="40" maxlength="200" alt=">0"/>
                                </td>
                            </tr>
                            <tr>
                                <th><I18n:message key="PRSTATUS_OR_USER"/></th>
                                <td>
                                    <c:choose>
                                        <c:when test="${connectedToUser ne null}">
                                            <html:hidden property="user"/>
            <span class="user" ${connectedToUser.id eq sc.userId ? "id=\"loggedUser\"" : ""}>
            <html:img styleClass="icon" border="0"
                      src="${contextPath}${ImageServlet}/cssimages/${connectedToUser.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
            <c:out value="${connectedToUser.name}" escapeXml="true"/>
			</span>
                                        </c:when>
                                        <c:when test="${connectedToGroup ne null}">
                                            <html:hidden property="user"/>
            <span class="user">
                <html:img styleClass="icon" border="0"
                          src="${contextPath}${ImageServlet}/cssimages/ico.status.gif"/><c:out
                    value="${connectedToGroup.name}" escapeXml="true"/>
		    </span>
                                        </c:when>
                                        <c:otherwise>
                                            <html:select property="user" styleId="user" onchange="updateFilterDiv();">
                                                <c:if test="${!(empty groupCollection)}">
                                                    <optgroup label="<I18n:message key="PRSTATUSES"/>">
                                                        <c:forEach var="handlerI" items="${groupCollection}">
                                                            <html:option value="PR_${handlerI.id}"><c:out
                                                                    value="${handlerI.name}"
                                                                    escapeXml="true"/></html:option>
                                                        </c:forEach>
                                                    </optgroup>
                                                </c:if>
                                                <c:if test="${!(empty userCollection)}">
                                                    <optgroup label="<I18n:message key="USERS_LIST"/>">
                                                        <c:forEach var="handlerI" items="${userCollection}">
                                                            <html:option value="${handlerI.id}"><c:out
                                                                    value="${handlerI.name}"
                                                                    escapeXml="true"/></html:option>
                                                        </c:forEach>
                                                    </optgroup>
                                                </c:if>
                                            </html:select>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>

                            <tr>
                                <th><I18n:message key="FILTER"/></th>
                                <td>
                                    <div id="filterDiv">
                                        <c:import url="EditSubscribeFilter.jsp"/>
                                    </div>
                                </td>
                            </tr>

                            <tr>
                                <th><I18n:message key="EMAIL_TYPE"/></th>
                                <td>
                                    <html:select property="template">
                                        <html:option value="USERDEFINED"><I18n:message
                                                key="USER_DEFINED"/></html:option>
                                        <c:forEach items="${templates}" var="t">
                                            <html:option value="${t.name}"><c:out value="${t.name}"/></html:option>
                                        </c:forEach>

                                    </html:select>
                                </td>
                            </tr>


                            <tr>
                                <th><I18n:message key="VALID_TIME"/> <I18n:message key="FROM"/></th>
                                <td>
                                    <html:text styleId="from" alt=">0,date(${sc.user.dateFormatter.pattern2})"
                                               property="startDate" value="${startDate}" size="18" maxlength="40"/>&nbsp;<html:img
                                        src="${contextPath}${ImageServlet}/cssimages/ico.calendar.gif" border="0"
                                        altKey="SELECT_DATE" styleClass="calendaricon"
                                        onclick="return showCalendar('from', '${sc.user.dateFormatter.pattern2}', '24', true);"/>
                                </td>
                            </tr>
                            <tr>
                                <th><I18n:message key="VALID_TIME"/> <I18n:message key="TO"/></th>
                                <td>
                                    <html:text styleId="to" alt=">0,date(${sc.user.dateFormatter.pattern2})"
                                               property="stopDate" value="${stopDate}" size="18"
                                               maxlength="40"/>&nbsp;<html:img
                                        src="${contextPath}${ImageServlet}/cssimages/ico.calendar.gif" border="0"
                                        altKey="SELECT_DATE" styleClass="calendaricon"
                                        onclick="return showCalendar('to', '${sc.user.dateFormatter.pattern2}', '24', true);"/>
                                </td>
                            </tr>
                            <tr>
                                <th><I18n:message key="NEXT_RUN"/></th>
                                <td>
                                    <html:text styleId="next" alt=">0,date(${sc.user.dateFormatter.pattern2})"
                                               property="nextRun" value="${nextRun}" size="18"
                                               maxlength="40"/>&nbsp;<html:img
                                        src="${contextPath}${ImageServlet}/cssimages/ico.calendar.gif" border="0"
                                        altKey="SELECT_DATE" styleClass="calendaricon"
                                        onclick="return showCalendar('next', '${sc.user.dateFormatter.pattern2}', '24', true);"/>
                                </td>
                            </tr>
                            <tr>
                                <th><I18n:message key="INTERVAL"/></th>
                                <td>
                                    <c:out value="${intervalList}" escapeXml="false"/>
                                </td>
                            </tr>
                        </table>
                    </div>

                    <div class="controls">
                        <input type="submit" class="iconized"
                               value="<I18n:message key="SAVE"/>"
                               name="SAVE" onClick="set('addSubscribe');">
                        <c:if test="${subscriptionId eq null}">
                            <html:button styleClass="iconized secondary" property="cancelButton"
                                         onclick="document.location='${contextPath}/TaskSubscribeAction.do?method=page&id=${id}';">
                                <I18n:message key="CANCEL"/>
                            </html:button>
                        </c:if>
                        <script type="text/javascript">
                            function set(target) {
                                document.getElementById('subscribeListId').value = target;
                            }
                            ;
                        </script>
                    </div>
                </html:form>
            </div>
        </div>
    </tiles:put>
</tiles:insert>
