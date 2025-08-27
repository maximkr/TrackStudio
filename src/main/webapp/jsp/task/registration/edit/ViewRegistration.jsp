<%@ page buffer="128kb" errorPage="/jsp/Error.jsp"%>
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
    <tiles:put name="header" value="/jsp/task/TaskHeader.jsp"/>

    <tiles:put name="customHeader" value="/jsp/task/registration/UserRegistrationHeader.jsp"/>
    <tiles:put name="tabs" type="string"/>
    <tiles:put name="main" type="string">
        <c:if test="${canView}">
            <div class="blueborder">
                <div class="caption">
                    <I18n:message key="REGISTRATION_PROPERTIES"/>
                </div>
                <div class="controlPanel">
                    <c:if test="${canEdit}">
                        <html:link href="${contextPath}/RegistrationEditGeneralAction.do?method=page&amp;id=${id}&amp;registration=${registration.id}"><html:img src="${contextPath}${ImageServlet}/cssimages/ico.edit.gif" border="0" altKey="EDIT"/>
                            <I18n:message key="EDIT"/>
                        </html:link>
                    </c:if>
                    <c:if test="${canCreate}">
                        <html:link  href="${contextPath}/RegistrationEditGeneralAction.do?method=page&amp;id=${id}"><html:img src="${contextPath}${ImageServlet}/cssimages/ico.edit.gif" border="0" altKey="EDIT"/>
                            <I18n:message key="SUBSCRIPTION_SELF_ADD"/>
                        </html:link>
                    </c:if>
                </div>
                <div class="indent">
                    <div class="general">
                        <table class="general" cellpadding="0" cellspacing="0">
                            <COLGROUP>
                                <COL class="col_1">
                                <COL class="col_2">
                            </COLGROUP>
                            <caption><I18n:message key="REGISTRATION_PROPERTIES"/></caption>
                            <tr>
                                <th>
                                    <I18n:message key="NAME"/>
                                </th>
                                <td>
                                    <c:out value="${registration.name}"/>
                                </td>
                            </tr>
                            <tr>
                                <th>
                                    <I18n:message key="OWNER"/>
                                </th>
                                <td>
    <span class="user" ${owner.id eq sc.userId ? "id='loggedUser'" : ""}>
            <html:img  styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/${owner.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
            <c:out value="${owner.name}" escapeXml="true"/>
			</span>
                                </td>
                            </tr>
                            <tr>
                                <th>
                                    <I18n:message key="REGISTRATION_URL"/>
                                </th>
                                <td>
                                    <html:link styleClass="internal" href="${url}" target="_blank"><c:out value="${url}" escapeXml="true"/></html:link>
                                </td>
                            </tr>
                            <tr>
                                <th><I18n:message key="PRSTATUS"/></th>
                                <td>
                                    <img src="${contextPath}${ImageServlet}/cssimages/ico.status.gif"/><c:out value="${prstatus.name}" escapeXml="true"/>
                                </td>
                            </tr>
                            <c:if test="${child ne null}">
                                <tr>
                                    <th><I18n:message key="USERS_ALLOWED"/></th>
                                    <td>
                                        <c:out value="${child}" escapeXml="true"/>
                                    </td>
                                </tr>
                            </c:if>
                            <c:if test="${expire ne null}">
                                <tr>
                                    <th><I18n:message key="EXPIRE_IN"/></th>
                                    <td>
                                        <c:out value="${expire}" escapeXml="true"/>
                                    </td>
                                </tr>
                            </c:if>
                            <tr>
                                <th><I18n:message key="HOME_PROJECT_CREATE"/></th>
                                <td>
                                    <c:if test="${registration.category ne null}">
                                        <html:img src="${contextPath}${ImageServlet}/cssimages/ico.checked.gif"/>
                                    </c:if>
                                    <c:if test="${registration.category eq null}">
                                        <html:img src="${contextPath}${ImageServlet}/cssimages/ico.unchecked.gif"/>
                                    </c:if>
                                </td>
                            </tr>
                            <c:if test="${registration.category ne null}">
                                <tr>
                                    <th><I18n:message key="CATEGORY"/></th>
                                    <td>
                                        <c:out value="${registration.category.name}" escapeXml="true"/>
                                    </td>
                                </tr>
                            </c:if>

                            <tr>
                                <th><I18n:message key="SHARED"/></th>
                                <td>
                                    <c:choose>
                                        <c:when test="${registration.priv}">
                                            <html:img src="${contextPath}${ImageServlet}/cssimages/ico.checked.gif"/>
                                        </c:when>
                                        <c:otherwise>
                                            <html:img src="${contextPath}${ImageServlet}/cssimages/ico.unchecked.gif"/>
                                        </c:otherwise>
                                    </c:choose>

                                </td>
                            </tr>


                        </table>
                    </div>
                </div>
            </div>
        </c:if>
    </tiles:put>
</tiles:insert>
