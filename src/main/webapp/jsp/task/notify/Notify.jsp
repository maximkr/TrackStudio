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
    <tiles:put name="customHeader" type="string"/>
    <tiles:put name="tabs" type="string"/>
    <tiles:put name="main" type="string">
        <div class="blueborder">
            <div class="caption"><I18n:message key="NOTIFICATIONS_LIST"/></div>
            <c:if test="${canManageEmailSchedules}">
                <div class="controlPanel">
                    <html:link
                            href="${contextPath}/TaskNotifyEditAction.do?method=page&amp;id=${id}&amp;user=${sc.userId}"><html:img
                            src="${contextPath}${ImageServlet}/cssimages/ico.edit.gif" border="0" altKey="EDIT"/>
                        <I18n:message key="NOTIFICATION_RULE_SELF_ADD"/>
                    </html:link>
                    <html:link href="${contextPath}/TaskNotifyEditAction.do?method=page&amp;id=${id}"><html:img
                            src="${contextPath}${ImageServlet}/cssimages/ico.edit.gif" border="0" altKey="EDIT"/>
                        <I18n:message key="NOTIFICATION_RULE_ADD"/>
                    </html:link>

                </div>
            </c:if>

            <script type="text/javascript">
                var cancelDelete = false;

                function deleteNotification() {
                    if (document.getElementById('notifyListId').value == 'deleteNotify') {
                        return deleteConfirm("<I18n:message key="DELETE_NOTIFICATION_REQ"/>", "notifySubscribeForm");
                    }
                    return true;
                }

                function onSubmitFunction(frm) {
                    return !cancelDelete;
                }

                function set(target) {
                    document.getElementById('notifyListId').value = target;
                }
            </script>

            <div class="indent">
                <c:choose>
                    <c:when test="${!empty notifications}">
                        <html:form method="POST" action="/TaskNotifyAction" onsubmit="return deleteNotification();">
                            <html:hidden property="method" value="addNotify" styleId="notifyListId"/>
                            <html:hidden property="id" value="${id}"/>
                            <html:hidden property="session" value="${session}"/>
                            <div class="general">
                                <table class="general" cellpadding="0" cellspacing="0">

                                    <tr class="wide">
                                        <c:if test="${canManageEmailSchedules}">
                                            <th width="1%" nowrap style="white-space:nowrap"><input type="checkbox"
                                                                                                    onClick="selectAllCheckboxes(this, 'delete1')">
                                            </th>
                                        </c:if>
                                        <th><I18n:message key="NAME"/></th>
                                        <th><I18n:message key="PRSTATUS_OR_USER"/></th>
                                        <th><I18n:message key="FILTER"/></th>
                                    </tr>
                                    <c:forEach var="notification" items="${notifications}" varStatus="varCounter">
                                        <tr class="line<c:out value="${varCounter.index mod 2}"/>">
                                            <c:if test="${canManageEmailSchedules}">
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${notification.canUpdate}">
		        <span style="text-align: center">
                    <input type="checkbox" class=checkbox name="delete" alt="delete1" quickCheckboxSelectGroup="delete1"
                           value="<c:out value="${notification.id}"/>"/>
                </span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            &nbsp;
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                            </c:if>
                                            <td>
            <span style="white-space: nowrap;">
            <c:choose>
                <c:when test="${!notification.canUpdate}">
                    <html:link styleClass="internal"
                               href="${contextPath}/TaskNotifyViewAction.do?method=page&id=${id}&notificationId=${notification.id}">
                        <img title="<I18n:message key="OBJECT_PROPERTIES_VIEW"/>" border="0" hspace="0" vspace="0"
                             src="<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.closed.gif"/>
                        <c:out value="${notification.name}"/>
                    </html:link>
                </c:when>
                <c:otherwise>
                    <html:link styleClass="internal"
                               href="${contextPath}/TaskNotifyEditAction.do?method=page&id=${id}&notificationId=${notification.id}&filterId=${notification.filterId}">
                        <img title="<I18n:message key="OBJECT_PROPERTIES_EDIT"/>" border="0" hspace="0" vspace="0"
                             src="<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.edit.gif"/>
                    </html:link>
                    <html:link styleClass="internal"
                               href="${contextPath}/TaskNotifyViewAction.do?method=page&id=${id}&notificationId=${notification.id}">
                        <c:out value="${notification.name}"/>
                    </html:link>
                </c:otherwise>
            </c:choose>
            </span>
                                            </td>
                                            <td>
        <span style="white-space: nowrap;">
            <c:choose>
                <c:when test="${notification.user ne null}">
			        <span class="user" ${notification.user.id eq sc.userId ? "id=\"loggedUser\"" : ""}>
                        <html:img styleClass="icon" border="0"
                                  src="${contextPath}${ImageServlet}/cssimages/${notification.user.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/><c:out
                            value="${notification.user.name}" escapeXml="true"/>
			        </span>
                </c:when>
                <c:when test="${notification.group ne null}">
					<span class="user">
                        <html:img styleClass="icon" border="0"
                                  src="${contextPath}${ImageServlet}/cssimages/ico.status.gif"/><c:out
                            value="${notification.group.name}" escapeXml="true"/>
				    </span>
                </c:when>
            </c:choose>
        </span>
                                            </td>
                                            <td><span style="white-space: nowrap;"><c:out
                                                    value="${notification.filter}"/></span></td>
                                        </tr>
                                    </c:forEach>
                                </table>
                            </div>
                            <div class="controls">
                                <c:if test="${canManageEmailSchedules}">
                                    <input type="submit" class="iconized secondary"
                                           value="<I18n:message key="CLONE"/>"
                                           name="CLONE" onClick="set('clone');">
                                </c:if>
                                <c:if test="${canManageEmailSchedules}">
                                    <input type="submit" class="iconized"
                                           value="<I18n:message key="DELETE"/>"
                                           name="DELETE" onClick="set('deleteNotify');">
                                </c:if>
                            </div>
                        </html:form>
                    </c:when>
                    <c:otherwise>
                        <div class="empty"><I18n:message key="EMPTY_NOTIFICATION_LIST"/></div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <c:if test="${!empty seeAlso}">
            <br>

            <div class="blueborder">
                <div class="caption">
                    <I18n:message key="SEE_ALSO"/>
                </div>
                <div class="indent">
                    <c:forEach items="${seeAlso}" var="also" varStatus="affected">
                        <c:if test="${!empty also}">
                            <dl ${affected.first ? "class='affected'" : ""}>

                                <c:forEach var="task" items="${also}" varStatus="varCounter">
                                    <dt><span class="itemname"><html:link styleClass="internal"
                                                                          href="${contextPath}/TaskNotifyAction.do?method=page&amp;id=${task.key.id}"><html:img
                                            styleClass="icon" border="0"
                                            src="${contextPath}${ImageServlet}/icons/categories/${task.key.category.icon}"/><c:out
                                            value="${task.key.name}"/></html:link></span><span
                                            class="itempath"><html:link styleClass="internal"
                                                                        href="${contextPath}/TaskNotifyAction.do?method=page&amp;id=${task.key.id}"><c:forEach
                                            var="path" items="${task.key.ancestors}">
                                        <span class="separated"><c:out value="${path.name}"/>&nbsp;/</span>
                                    </c:forEach></html:link><c:if test="${task.key.parentId ne null}"><html:link
                                            styleClass="internal"
                                            href="${contextPath}/TaskNotifyAction.do?method=page&amp;id=${task.key.id}"><c:out
                                            value="${task.key.name}"/></html:link></c:if>
        </span>
                                    </dt>
                                    <dd>
                                        <c:forEach var="cat" items="${task.value}" varStatus="varC"><c:if
                                                test="${varC.index > 0}">, </c:if><span
                                                style="white-space: nowrap;"><html:link styleClass="internal"
                                                                                        href="${contextPath}/TaskNotifyViewAction.do?method=page&notificationId=${cat.id}&id=${task.key.id}"
                                                                                        title="${cat.name}"><html:img
                                                styleClass="icon" border="0"
                                                src="${contextPath}${ImageServlet}/cssimages/ico.notifications.gif"/><c:out
                                                value="${cat.name}"/></html:link> (<c:choose>
                                            <c:when test="${cat.user ne null}">
			<span class="user" ${cat.user.id eq sc.userId ? "id='loggedUser'" : ""}>
            <html:img styleClass="icon" border="0"
                      src="${contextPath}${ImageServlet}/cssimages/${cat.user.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/><c:out
                    value="${cat.user.name}" escapeXml="true"/>
			</span>
                                            </c:when>
                                            <c:when test="${cat.group ne null}">
					<span class="user">
                        <html:img styleClass="icon" border="0"
                                  src="${contextPath}${ImageServlet}/cssimages/ico.status.gif"/><c:out
                            value="${cat.group.name}" escapeXml="true"/>
						</span>
                                            </c:when>
                                        </c:choose>)</span></c:forEach>
                                    </dd>
                                </c:forEach>
                            </dl>
                        </c:if>
                    </c:forEach>
                </div>
            </div>
        </c:if>

    </tiles:put>
</tiles:insert>
