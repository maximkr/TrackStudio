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

    <tiles:put name="customHeader" type="string"/>
    <tiles:put name="tabs" type="string"/>
    <tiles:put name="main" type="string">

        <div class="blueborder">
            <div class="caption">
                <I18n:message key="REGISTRATIONS"/>
            </div>
            <c:if test="${canCreateObject}">
                <div class="controlPanel">
                    <html:link href="${contextPath}/RegistrationEditGeneralAction.do?method=page&amp;id=${id}"><html:img src="${contextPath}${ImageServlet}/cssimages/ico.edit.gif" styleClass="icon" border="0"/><I18n:message key="REGISTRATION_ADD"/></html:link>
                </div>
            </c:if>
            <div class="indent">
                <c:choose>
                    <c:when test="${!empty registrations}">
                        <html:form method="POST" styleId="checkunload" action="/RegistrationAction" onsubmit="${form_onsubmit}">
                            <div class="general">
                                <table class="general" cellpadding="0" cellspacing="0">
                                    <html:hidden property="id" value="${id}"/>
                                    <html:hidden property="session" value="${session}"/>
                                    <html:hidden property="method" value="save" styleId="registrationListId"/>
                                    <tr class="wide">
                                        <th width='1%' nowrap style="white-space:nowrap"><input type="checkbox" onClick="selectAllCheckboxes(this, 'select1')"></th>
                                        <th><I18n:message key="NAME"/></th>
                                        <th><I18n:message key="OWNER"/></th>
                                        <th><I18n:message key="PRSTATUS"/></th>
                                    </tr>

                                    <c:forEach var="reg" items="${registrations}" varStatus="varCounter">
                                        <tr class="line<c:out value="${varCounter.index mod 2}"/>">
                                            <td>
                                                <c:if test="${reg.canUpdate}">
                                                    <span style="text-align: center"><html:checkbox property="select" value="${reg.id}" alt="select1"/></span>
                                                </c:if>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${!reg.canUpdate}">
                                                        <html:link styleClass="internal" href="${contextPath}/RegistrationViewAction.do?method=page&amp;id=${id}&amp;registration=${reg.id}&id=${id}">
                                                            <img title="<I18n:message key="OBJECT_PROPERTIES_VIEW"/>" border="0" hspace="0" vspace="0"  src="<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.closed.gif"/>
                                                        </html:link>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <html:link styleClass="internal" href="${contextPath}/RegistrationEditGeneralAction.do?method=page&amp;id=${id}&amp;registration=${reg.id}&id=${id}">
                                                            <img title="<I18n:message key="OBJECT_PROPERTIES_EDIT"/>" border="0" hspace="0" vspace="0"  src="<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.edit.gif"/>
                                                        </html:link>
                                                    </c:otherwise>
                                                </c:choose>
                                                <html:link styleClass="internal" href="${contextPath}/RegistrationViewAction.do?method=page&amp;id=${id}&amp;registration=${reg.id}">
                                                    <c:out value="${reg.name}" escapeXml="true"/>
                                                </html:link>
                                            </td>
                                            <td><span class="user" ${reg.user.id eq sc.userId ? "id='loggedUser'" : ""}>
            <html:img  styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/${reg.user.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
            <c:out value="${reg.user.name}" escapeXml="true"/>
			</span></td>
                                            <td><img src="${contextPath}${ImageServlet}/cssimages/ico.status.gif"/><c:out value="${reg.prstatus.name}" escapeXml="true"/></td>

                                        </tr>
                                    </c:forEach>
                                </table>
                            </div>

                            <div class="controls">
                                 <c:if test="${canCreateObject}">
                                    <input type="submit"  class="iconized secondary"
                                           value="<I18n:message key="CLONE"/>"
                                           onClick="set('clone');"
                                           name="CLONE">
                                </c:if>
                                <c:if test="${canDelete}">
                                    <input type="submit"  class="iconized"
                                           value="<I18n:message key="DELETE"/>"
                                           onClick="set('delete');"
                                           name="DELETE">
                                </c:if>
                                <SCRIPT type="text/javascript">
                                    function set(target) {
                                        document.getElementById('registrationListId').value=target;
                                    }
                                </SCRIPT>
                            </div>
                        </html:form>
                    </c:when>
                    <c:otherwise>
                        <div class="empty"><I18n:message key="EMPTY_REGISTRATION_LIST"/></div>
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
                                    <dt><span class="itemname"><html:link styleClass="internal" href="${contextPath}/RegistrationAction.do?method=page&amp;id=${task.key.id}"><html:img  styleClass="icon" border="0" src="${contextPath}${ImageServlet}/icons/categories/${task.key.category.icon}"/><c:out value="${task.key.name}"/></html:link></span><span class="itempath"><html:link styleClass="internal" href="${contextPath}/RegistrationAction.do?method=page&amp;id=${task.key.id}"><c:forEach var="path" items="${task.key.ancestors}">
                                        <span class="separated"><c:out value="${path.name}"/></span>&nbsp;/
                                    </c:forEach></html:link><c:if test="${task.key.parentId ne null}"><html:link styleClass="internal" href="${contextPath}/RegistrationAction.do?method=page&amp;id=${task.key.id}"><c:out value="${task.key.name}"/></html:link></c:if>
        </span>
                                    </dt>
                                    <dd>
                                        <c:forEach var="cat" items="${task.value}" varStatus="varC"><c:if test="${varC.index > 0}">, </c:if><span style="white-space: nowrap;"><html:link styleClass="internal"
                                                                                                                                                                                          href="${contextPath}/RegistrationViewAction.do?method=page&amp;registration=${cat.id}&amp;id=${task.key.id}"
                                                                                                                                                                                          title="${cat.name}"><html:img  styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/ico.registration.gif"/><c:out value="${cat.name}"/></html:link></span></c:forEach>
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
