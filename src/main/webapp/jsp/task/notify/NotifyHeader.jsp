<%@ page buffer="128kb" errorPage="/jsp/Error.jsp" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<I18n:setLocale value='${sc.locale}'/>
<I18n:setTimeZone value='${sc.timezone}'/>
<I18n:setBundle basename='language'/>
<div class="elev" id="customHeader">
    <div>
        <html:img src="${contextPath}${ImageServlet}/cssimages/L.png"/><html:link styleClass="ul"
                                                                                  href="${contextPath}/TaskNotifyAction.do?method=page&id=${id}"><html:img
            src="${contextPath}${ImageServlet}/cssimages/ico.notifications.gif" hspace="0" vspace="0" border="0"/>
        <I18n:message key="NOTIFICATIONS_LIST"/>
    </html:link> <span>:</span>
        <c:if test="${isUnsubscribe eq null}">
            <c:choose>
                <c:when test="${notification ne null}">
                    <html:link styleClass="internal"
                               href="${contextPath}/TaskNotifyViewAction.do?method=page&notificationId=${notification.id}&id=${id}">
                        <c:out value="${notification.name}" escapeXml="true"/></html:link>
                </c:when>
                <c:otherwise>
                    <span class="createnew"><I18n:message key="NOTIFICATION_RULE_ADD"/></span>
                </c:otherwise>
            </c:choose>
        </c:if>
        <c:if test="${isUnsubscribe ne null}">
            <span class="createnew"><I18n:message key="NOTIFICATION_UNSUBSCRIBE"/></span>
        </c:if>
    </div>
</div>