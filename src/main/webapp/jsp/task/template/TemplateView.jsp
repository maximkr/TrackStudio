<%@ page buffer="128kb" errorPage="/jsp/Error.jsp" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/tiles-el" prefix="tiles" %>


<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value="${sc.timezone}"/>
<I18n:setBundle basename="language"/>

<tiles:insert page="/jsp/layout/ListLayout.jsp" flush="true">
    <tiles:put name="header" value="/jsp/task/TaskHeader.jsp"/>
    <tiles:put name="customHeader" value="/jsp/task/template/TemplateHeader.jsp"/>
    <tiles:put name="tabs" type="string"/>
    <tiles:put name="main" type="string">
        <div class="blueborder">
            <div class="caption">
                <I18n:message key="TEMPLATE_OVERVIEW"/>
            </div>

            <div class="controlPanel">
                <html:link
                        href="${contextPath}/TemplateEditAction.do?method=page&amp;id=${id}&amp;templateId=${template.id}&amp;user=${sc.userId}">
                    <html:img src="${contextPath}${ImageServlet}/cssimages/ico.edit.gif" styleClass="icon" border="0"/>
                    <I18n:message key="TEMPLATE_EDIT"/>
                </html:link>
            </div>


            <div class="indent">
                <table class="general" cellpadding="0" cellspacing="0">
                    <colgroup>
                        <col class="col_1">
                        <col class="col_2">
                    </colgroup>
                    <tr>
                        <th>
                            <I18n:message key="NAME"/>
                        </th>
                        <td>
                            <c:out value="${template.name}"/>
                        </td>
                    </tr>
                    <tr>
                        <th>
                            <I18n:message key="DESCRIPTION"/>
                        </th>
                        <td>
                            <c:out value="${template.description}"/>
                        </td>
                    </tr>
                    <tr>
                        <th>
                            <I18n:message key="USER"/>
                        </th>
                        <td>
                            <c:if test="${template.user ne null}">
                    <span class="user" ${template.user.id eq sc.userId ? "id='loggedUser'" : ""}>
            <html:img styleClass="icon" border="0"
                      src="${contextPath}${ImageServlet}/cssimages/${template.user.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
            <c:out value="${template.user.name}" escapeXml="true"/>
			</span></c:if>
                        </td>
                    </tr>
                    <tr>
                        <th>
                            <I18n:message key="OWNER"/>
                        </th>
                        <td>
                        <span class="user" ${template.owner.id eq sc.userId ? "id='loggedUser'" : ""}>
            <html:img styleClass="icon" border="0"
                      src="${contextPath}${ImageServlet}/cssimages/${template.owner.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
            <c:out value="${template.owner.name}" escapeXml="true"/>
			</span>
                        </td>
                    </tr>


                    <tr>
                        <th>
                            <I18n:message key="ACTIVE"/>
                        </th>
                        <td>
                            <c:choose>
                                <c:when test="${template.active}">
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
                            <I18n:message key="TEMPLATE_FOLDER"/>
                        </th>
                        <td>
                            <c:out value="${template.folder}"/>
                        </td>
                    </tr>
                    <c:if test="${template.active}">
                        <tr>
                            <th>
                                <I18n:message key="PERMLINK"/>
                            </th>
                            <td>
                                <a class="internal" target="_blank" href="${contextPath}/template/<c:out value="${name}"/>/task/${tci.number}">${contextPath}/template/<c:out value="${name}"/>/task/${tci.number}</a>
                            </td>
                        </tr>
                    </c:if>
                </table>
            </div>
        </div>
    </tiles:put>
</tiles:insert>
