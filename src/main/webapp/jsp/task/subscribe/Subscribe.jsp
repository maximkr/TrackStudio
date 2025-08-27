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
            <div class="caption"><I18n:message key="SUBSCRIPTIONS_LIST"/></div>
            <c:if test="${canSubscribeSelf || canSubscribeOthers}">
                <div class="controlPanel">
                    <c:if test="${canSubscribeSelf}">
                        <html:link titleKey="SUBSCRIPTION_SELF_ADD_COMMENT"
                                   href="${contextPath}/TaskSubscribeEditAction.do?method=create&amp;id=${id}&amp;user=${sc.userId}"><html:img
                                src="${contextPath}${ImageServlet}/cssimages/ico.edit.gif" border="0" altKey="EDIT"/>
                            <I18n:message key="SUBSCRIPTION_SELF_ADD"/>
                        </html:link>
                    </c:if>
                    <c:if test="${canSubscribeOthers}">
                        <html:link titleKey="SUBSCRIPTION_ADD_COMMENT"
                                   href="${contextPath}/TaskSubscribeEditAction.do?method=create&amp;id=${id}"><html:img
                                src="${contextPath}${ImageServlet}/cssimages/ico.edit.gif" border="0" altKey="EDIT"/>
                            <I18n:message key="SUBSCRIPTION_ADD"/>
                        </html:link>
                    </c:if>
                </div>
            </c:if>

            <script type="text/javascript">
                var cancelDelete = false;


                function deleteSubscription() {
                    if (document.getElementById('subscribeListId').value == 'deleteSubscribe') {
                        return deleteConfirm("<I18n:message key="DELETE_SUBSCRIPTION_REQ"/>", "notifySubscribeForm");
                    }
                    return true;
                }

                function onSubmitFunction(frm) {
                    return !cancelDelete;
                }

                function set(target) {
                    document.getElementById('subscribeListId').value = target;
                }
            </script>

            <div class="indent">
                <c:choose>
                    <c:when test="${!empty subscriptions}">
                        <html:form method="POST" action="/TaskSubscribeAction" onsubmit="return deleteSubscription();">
                            <html:hidden property="method" value="addSubscribe" styleId="subscribeListId"/>
                            <html:hidden property="id" value="${id}"/>
                            <html:hidden property="session" value="${session}"/>
                            <div class="general">
                                <table class="general" cellpadding="0" cellspacing="0">

                                    <tr class="wide">
                                        <c:if test="${canSubscribeSelf || canSubscribeOthers}">
                                            <th width="1%" nowrap style="white-space:nowrap"><input type="checkbox"
                                                                                                    onClick="selectAllCheckboxes(this, 'delete1')">
                                            </th>
                                        </c:if>
                                        <th><I18n:message key="NAME"/></th>
                                        <th><I18n:message key="PRSTATUS_OR_USER"/></th>
                                        <th><I18n:message key="FILTER"/></th>
                                        <th><I18n:message key="EMAIL_TYPE"/></th>
                                    </tr>
                                    <c:forEach var="subscribe" items="${subscriptions}" varStatus="varCounter">
                                        <tr class="line<c:out value="${varCounter.index mod 2}"/>">
                                            <c:if test="${canSubscribeSelf || canSubscribeOthers}">
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${subscribe.canUpdate}">
                <span style="text-align: center">
                    <input type="checkbox" class=checkbox name="delete" alt="delete1" quickCheckboxSelectGroup="delete1"
                           value="<c:out value="${subscribe.id}"/>"/>
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
                <c:when test="${!subscribe.canUpdate}">
                    <html:link styleClass="internal"
                               href="${contextPath}/TaskSubscribeViewAction.do?method=page&id=${id}&subscriptionId=${subscribe.id}">
                        <img title="<I18n:message key="OBJECT_PROPERTIES_VIEW"/>" border="0" hspace="0" vspace="0"
                             src="<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.closed.gif"/>
                        <c:out value="${subscribe.name}"/>
                    </html:link>
                </c:when>
                <c:otherwise>
                    <html:link styleClass="internal"
                               href="${contextPath}/TaskSubscribeEditAction.do?method=page&id=${id}&subscriptionId=${subscribe.id}">
                        <img title="<I18n:message key="OBJECT_PROPERTIES_EDIT"/>" border="0" hspace="0" vspace="0"
                             src="<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.edit.gif"/>
                    </html:link>
                    <html:link styleClass="internal"
                               href="${contextPath}/TaskSubscribeViewAction.do?method=page&id=${id}&subscriptionId=${subscribe.id}">
                        <c:out value="${subscribe.name}"/>
                    </html:link>
                </c:otherwise>
            </c:choose>
            </span>
                                            </td>
                                            <td>
        <span style="white-space: nowrap;">
            <c:choose>
                <c:when test="${subscribe.user ne null}">
			        <span class="user" ${subscribe.user.id eq sc.userId ? "id=\"loggedUser\"" : ""}>
                    <html:img styleClass="icon" border="0"
                              src="${contextPath}${ImageServlet}/cssimages/${subscribe.user.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/><c:out
                            value="${subscribe.user.name}" escapeXml="true"/>
			        </span>
                </c:when>
                <c:when test="${subscribe.group ne null}">
					<span class="user">
                        <html:img styleClass="icon" border="0"
                                  src="${contextPath}${ImageServlet}/cssimages/ico.status.gif"/><c:out
                            value="${subscribe.group.name}" escapeXml="true"/>
				    </span>
                </c:when>
            </c:choose>
        </span>
                                            </td>
                                            <td><span style="white-space: nowrap;"><c:out
                                                    value="${subscribe.filter}"/></span></td>
                                            <td><span style="white-space: nowrap;"><c:out
                                                    value="${subscribe.template}"/></span></td>
                                        </tr>
                                    </c:forEach>
                                </table>
                            </div>
                            <c:if test="${canSubscribeSelf || canSubscribeOthers}">
                                <div class="controls">
                                    <input type="submit" class="iconized secondary"
                                           value="<I18n:message key="CLONE"/>"
                                           name="CLONE" onClick="set('clone');">
                                    <input type="submit" class="iconized"
                                           value="<I18n:message key="DELETE"/>"
                                           name="DELETE" onClick="set('deleteSubscribe');">
                                </div>
                            </c:if>
                        </html:form>
                    </c:when>

                    <c:otherwise>
                        <div class="empty"><I18n:message key="EMPTY_SUBSCRIPTION_LIST"/></div>
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
                                                                          href="${contextPath}/TaskSubscribeAction.do?method=page&amp;id=${task.key.id}"><html:img
                                            styleClass="icon" border="0"
                                            src="${contextPath}${ImageServlet}/icons/categories/${task.key.category.icon}"/><c:out
                                            value="${task.key.name}"/></html:link></span><span
                                            class="itempath"><html:link styleClass="internal"
                                                                        href="${contextPath}/TaskSubscribeAction.do?method=page&amp;id=${task.key.id}"><c:forEach
                                            var="path" items="${task.key.ancestors}">
                                        <span class="separated"><c:out value="${path.name}"/></span>&nbsp;/
                                    </c:forEach></html:link><c:if test="${task.key.parentId ne null}"><html:link
                                            styleClass="internal"
                                            href="${contextPath}/TaskSubscribeAction.do?method=page&amp;id=${task.key.id}"><c:out
                                            value="${task.key.name}"/></html:link></c:if>
        </span>
                                    </dt>
                                    <dd>
                                        <c:forEach var="cat" items="${task.value}" varStatus="varC"><c:if
                                                test="${varC.index > 0}">, </c:if><span
                                                style="white-space: nowrap;"><html:link styleClass="internal"
                                                                                        href="${contextPath}/TaskSubscribeViewAction.do?method=page&subscriptionId=${cat.id}&id=${task.key.id}"
                                                                                        title="${cat.name}"><html:img
                                                styleClass="icon" border="0"
                                                src="${contextPath}${ImageServlet}/cssimages/ico.subscription.gif"/><c:out
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
