<%@ page buffer="128kb" errorPage="/jsp/Error.jsp"%>
<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/tiles-el" prefix="tiles" %>


<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value="${sc.timezone}"/>
<I18n:setBundle basename="language"/>
<tiles:insert page="/jsp/layout/ListLayout.jsp" flush="true">
<tiles:put name="header" value="/jsp/task/TaskHeader.jsp"/>
<tiles:put name="customHeader" value="/jsp/task/filter/TaskFilterHeader.jsp"/>
<tiles:put name="tabs" type="string"/>
<tiles:put name="main" type="string">
    <div class="blueborder">
        <div class="caption"><I18n:message key="TASK_FILTER_PROPERTIES"/></div>
        <c:if test="${canEdit}">
            <div class="controlPanel">
                <c:if test="${canEdit}">
                    <html:link  href="${contextPath}/TaskFilterEditAction.do?method=page&amp;filterId=${filterId}&amp;id=${id}">
                        <html:img src="${contextPath}${ImageServlet}/cssimages/ico.edit.gif"/><I18n:message key="FILTER_PROPERTIES_EDIT"/>
                    </html:link>
                </c:if>
                <c:if test="${canEdit}">
                    <html:link  href="${contextPath}/TaskFilterTaskSettingsAction.do?method=page&amp;filterId=${filterId}&amp;id=${id}">
                        <html:img src="${contextPath}${ImageServlet}/cssimages/ico.edit.gif"/><I18n:message key="FILTER_TASK_PARAMETERS_EDIT"/>
                    </html:link>
                </c:if>
                <c:if test="${canEdit}">
                    <html:link  href="${contextPath}/TaskFilterMessageSettingsAction.do?method=page&amp;filterId=${filterId}&amp;id=${id}">
                        <html:img src="${contextPath}${ImageServlet}/cssimages/ico.edit.gif"/><I18n:message key="MESSAGE_PARAMETERS_EDIT"/>
                    </html:link>
                </c:if>
            </div>
        </c:if>
        <div class="indent">
            <table class="general" cellpadding="0" cellspacing="0">
                <caption><I18n:message key="TASK_FILTER_PROPERTIES"/></caption>
                <COLGROUP>
                    <COL class="col_1">
                    <COL class="col_2">
                </COLGROUP>
                <tr>
                    <th>
                        <I18n:message key="NAME"/>
                    </th>
                    <td><c:out value="${currentFilter.name}"/></td>
                </tr>
                <tr>
                    <th>
                        <I18n:message key="DESCRIPTION"/>
                    </th>
                    <td><c:out value="${currentFilter.description}"/></td>
                </tr>
                <tr>
                    <th>
                        <I18n:message key="SHARE"/>
                    </th>
                    <td>
                        <c:choose>
                            <c:when test="${currentFilter.priv ne true}">
                                <html:img src="${contextPath}${ImageServlet}/cssimages/ico.checked.gif"/>
                            </c:when>
                            <c:otherwise>
                                <html:img src="${contextPath}${ImageServlet}/cssimages/ico.unchecked.gif"/>
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
                <tr>
                    <th>
                        <I18n:message key="SHOW_IN_TOOLBAR"/>
                    </th>
                    <td>
                        <c:choose>
                            <c:when test="${showInToolbar ne true}">
                                <html:img src="${contextPath}${ImageServlet}/cssimages/ico.unchecked.gif"/>
                            </c:when>
                            <c:otherwise>
                                <html:img src="${contextPath}${ImageServlet}/cssimages/ico.checked.gif"/>
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
                <tr>
                    <th><I18n:message key="OWNER"/></th>
                    <td>
                  <span class="user" ${currentFilter.owner.id eq sc.userId ? "id='loggedUser'" : ""}>
            <html:img  styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/${currentFilter.owner.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
            <c:out value="${currentFilter.owner.name}" escapeXml="true"/>
			</span>
                    </td>
                </tr>
                <c:if test="${!empty onpage}">
                    <tr>
                        <th>
                            <I18n:message key="TASKS_ON_PAGE"/>
                        </th>
                        <td><c:out value="${onpage}" escapeXml='true'/>
                        </td>
                    </tr>
                </c:if>
                <tr>
                    <th>
                        <I18n:message key="DEEP_SEARCH"/>
                    </th>
                    <td>
                        <c:choose>
                            <c:when test="${subtask ne true}">
                                <html:img src="${contextPath}${ImageServlet}/cssimages/ico.unchecked.gif"/>
                            </c:when>
                            <c:otherwise>
                                <html:img src="${contextPath}${ImageServlet}/cssimages/ico.checked.gif"/>
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>

                <c:if test="${!empty search}">
                    <tr>
                        <th>
                            <I18n:message key="KEYWORD"/>
                        </th>
                        <td>
                            <c:out value="${search}"/>
                        </td>
                    </tr>
                </c:if>
                <c:if test="${!empty messages}">
                    <tr>
                        <th>
                            <I18n:message key="DISPLAY_MSG"/>
                        </th>
                        <td>
                            <c:out value="${messages}"/>
                        </td>
                    </tr>
                </c:if>
            </table>
            <br>

            <table class="general" cellpadding="0" cellspacing="0">
                <caption><I18n:message key="TASK_FILTER_TASK_PARAMETERS"/></caption>
                <c:choose>
                    <c:when test="${(!empty v_display) || (!empty v_filter)}">
                        <COLGROUP>
                            <COL class="col_1">
                            <COL class="col_2">
                        </COLGROUP>
                        <tr>
                            <th><I18n:message key="USE_FIELDS"/></th>
                            <td><ul>
                                <c:forEach items="${v_display}" var="item">
                                    <li><c:out value="${item}" escapeXml="false"/></li>
                                </c:forEach>
                            </ul>
                            </td>
                        </tr>
                        <c:forEach items="${v_filter}" var="item">
                            <tr>
                                <th><c:out value="${item.key}"/></th>
                                <td><c:out value="${item.value}" escapeXml="false"/></td>
                            </tr>
                        </c:forEach>
                        <c:if test="${!empty v_sort}">
                            <tr>
                                <th><I18n:message key="SORT_FIELDS"/></th>
                                <td><ul>
                                    <c:forEach items="${v_sort}" var="item">
                                        <li><c:out value="${item}"  escapeXml='false'/></li>
                                    </c:forEach>
                                </ul>
                                </td>
                            </tr>
                        </c:if>
                    </c:when>
                    <c:otherwise>
                        <tr class="wide">
                            <td><span style="text-align: center;"><I18n:message key="EMPTY_TASK_FILTER_PARAMETERS_LIST"/></span></td>
                        </tr>
                    </c:otherwise>
                </c:choose>
            </table>
            <br>


            <table class="general" cellpadding="0" cellspacing="0">
                <caption><I18n:message key="FILTER_MESSAGE_PARAMETERS"/></caption>
                <c:choose>
                    <c:when test="${!empty v_message}">
                        <COLGROUP>
                            <COL class="col_1">
                            <COL class="col_2">
                        </COLGROUP>
                        <c:forEach items="${v_message}" var="item">
                            <tr>
                                <th><c:out value="${item.key}"/></th>
                                <td><c:out value="${item.value}" escapeXml="false"/></td>
                            </tr>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <tr class="wide">
                            <td><span style="text-align: center;"><I18n:message key="EMPTY_MESSAGE_FILTER_PARAMETERS_LIST"/></span></td>
                        </tr>
                    </c:otherwise>
                </c:choose>
            </table>
        </div>
    </div>
</tiles:put>
</tiles:insert>
