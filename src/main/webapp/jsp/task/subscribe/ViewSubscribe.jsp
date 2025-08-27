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
        <br/>
        <div class="blueborder">
            <div class="caption">
                <I18n:message key="SUBSCRIPTION_VIEW"/>
            </div>

            <div class="controlPanel">
                <c:if test="${canEdit}">
                    <html:link  href="${contextPath}/TaskSubscribeEditAction.do?method=page&amp;id=${id}&amp;subscriptionId=${subscription.id}"><html:img src="${contextPath}${ImageServlet}/cssimages/ico.edit.gif" border="0" altKey="EDIT"/>
                        <I18n:message key="SUBSCRIPTION_EDIT"/>
                    </html:link>
                </c:if>
                <c:if test="${canCreate}">
                    <html:link href="${contextPath}/TaskSubscribeEditAction.do?method=create&amp;id=${id}&amp;user=${sc.userId}"><html:img src="${contextPath}${ImageServlet}/cssimages/ico.edit.gif" border="0" altKey="EDIT"/>
                        <I18n:message key="SUBSCRIPTION_SELF_ADD"/>
                    </html:link>
                    <html:link href="${contextPath}/TaskSubscribeEditAction.do?method=create&amp;id=${id}"><html:img src="${contextPath}${ImageServlet}/cssimages/ico.edit.gif" border="0" altKey="EDIT"/>
                        <I18n:message key="SUBSCRIPTION_ADD"/>
                    </html:link>
                </c:if>
            </div>

            <html:form method="POST" action="/TaskSubscribeViewAction" onsubmit="return validate(this);">
                <html:hidden property="method" value="test"/>
                <html:hidden property="id" value="${id}"/>
                <html:hidden property="session" value="${session}"/>
                <html:hidden property="subscriptionId" value="${subscriptionId}"/>
                <html:hidden property="oldFilterId" value="${filterId}"/>
                <div class="indent">
                    <div class="general">
                        <table class="general" cellpadding="0" cellspacing="0">
                            <COLGROUP>
                                <COL class="col_1">
                                <COL class="col_2">
                            </COLGROUP>
                            <caption><I18n:message key="SUBSCRIPTION_PROPERTIES"/></caption>
                            <c:if test="${testResult ne null}">
                                <tr>
                                    <th colspan=2><center>
                                        <c:out value="${testResult}"/>
                                    </center>
                                    </th>
                                </tr>
                            </c:if>
                            <tr>
                                <th><I18n:message key="NAME"/></th>
                                <td><c:out value="${subscription.name}"/>
                                </td>
                            </tr>

                            <tr>
                                <th><I18n:message key="PRSTATUS_OR_USER"/></th>
                                <td><c:choose>
                                    <c:when test="${subscription.user ne null}">
			        <span class="user" ${subscription.user.id eq sc.userId ? "id=\"loggedUser\"" : ""}>
                        <html:img  styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/${subscription.user.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/><c:out value="${subscription.user.name}" escapeXml="true"/>
			        </span>
                                    </c:when>
                                    <c:when test="${subscription.group ne null}">
					<span class="user">
                        <html:img  styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/ico.status.gif"/><c:out value="${subscription.group.name}" escapeXml="true"/>
				    </span>
                                    </c:when>
                                </c:choose>
                                </td>
                            </tr>

                            <tr>
                                <th><I18n:message key="FILTER"/></th>
                                <td><c:out value="${subscription.filter.name}" escapeXml="true"/></td>
                            </tr>

                            <tr>
                                <th><I18n:message key="EMAIL_TYPE"/></th>
                                <td><c:out value="${template}"/></td>
                            </tr>


                            <tr>
                                <th><I18n:message key="VALID_TIME"/> <I18n:message key="FROM"/></th>
                                <td><I18n:formatDate value="${subscription.startdate.time}" type="both" dateStyle="short" timeStyle="short"/></td>
                            </tr>

                            <tr>
                                <th><I18n:message key="VALID_TIME"/> <I18n:message key="TO"/></th>
                                <td><I18n:formatDate value="${subscription.stopdate.time}" type="both" dateStyle="short" timeStyle="short"/></td>
                            </tr>

                            <tr>
                                <th><I18n:message key="NEXT_RUN"/></th>
                                <td><I18n:formatDate value="${subscription.nextrun.time}" type="both" dateStyle="short" timeStyle="short"/></td>
                            </tr>

                            <tr>
                                <th><I18n:message key="INTERVAL"/></th>
                                <td><c:out value="${subscription.interval}"/></td>
                            </tr>
                        </table>
                    </div>

                    <div class="controls">
                        <input type="submit"  class="iconized"
                               value="<I18n:message key="TEST"/>"
                               name="TEST">
                    </div>
                </div>
            </html:form>
        </div>
    </tiles:put>
</tiles:insert>
