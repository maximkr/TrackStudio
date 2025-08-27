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
    <tiles:put name="customHeader" type="string"/>
    <tiles:put name="tabs" type="string"/>
    <tiles:put name="main" type="string">

        <div class="blueborder">
            <div class="caption">
                <I18n:message key="TEMPLATES_LIST"/>
            </div>

            <c:if test="${canCreateObject}">
                <div class="controlPanel">
                    <html:link href="${contextPath}/TemplateAction.do?method=create&id=${id}&amp;user=${sc.userId}">
                        <html:img src="${contextPath}${ImageServlet}/cssimages/ico.edit.gif" styleClass="icon" border="0"/>
                        <I18n:message key="TEMPLATE_ADD"/>
                    </html:link>
                </div>
            </c:if>
            <script type="text/javascript">
                var cancelDelete = false;

                function deleteTemplate() {
                    if (document.getElementById('templateMethod').value == 'delete') {
                        return deleteConfirm("<I18n:message key="DELETE_TEMPLATES_REQ"/>", "templateForm");
                    }
                    return true;
                }

                function onSubmitFunction(frm) {
                    return !cancelDelete;
                }

                function set(target) {
                    document.getElementById('templateMethod').value = target;
                }
            </script>

            <div class="indent">
                <c:choose>
                    <c:when test="${!empty templates}">
                        <html:form method="POST" action="/TemplateAction" onsubmit="return deleteTemplate();">
                            <html:hidden property="method" value="delete" styleId="templateMethod"/>
                            <html:hidden property="id" value="${id}"/>
                            <html:hidden property="session" value="${session}"/>
                            <table class="general" cellpadding="0" cellspacing="0">
                                <tr class="wide">
                                    <th width='1%' nowrap style="white-space:nowrap"><input type="checkbox"
                                                                                            onClick="selectAllCheckboxes(this, 'delete1')">
                                    </th>
                                    <th>
                                        <I18n:message key="NAME"/>
                                    </th>
                                    <th>
                                        <I18n:message key="USER"/>
                                    </th>
                                    <th>
                                        <I18n:message key="OWNER"/>
                                    </th>
                                    <th>
                                        <I18n:message key="ACTIVE"/>
                                    </th>
                                </tr>
                                <c:forEach var="template" items="${templates}" varStatus="varCounter">
                                    <tr class="line<c:out value="${varCounter.index mod 2}"/>">
                                        <td>
                                            <center><input type="checkbox" class=checkbox name="delete" alt="delete1"
                                                           quickCheckboxSelectGroup="delete1" value="<c:out value="${template.id}"/>">
                                            </center>
                                        </td>
                                        <td>
            <span style="white-space: nowrap;">
            <c:choose>
                <c:when test="${!template.canUpdate}">
                    <html:link styleClass="internal"
                               href="${contextPath}/TemplateViewAction.do?method=page&id=${id}&templateId=${template.id}">
                        <img title="<I18n:message key="OBJECT_PROPERTIES_VIEW"/>" border="0" hspace="0" vspace="0"
                             src="<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.closed.gif"/>
                        <c:out value="${template.name}"/>
                    </html:link>
                </c:when>
                <c:otherwise>
                    <html:link styleClass="internal"
                               href="${contextPath}/TemplateEditAction.do?method=page&id=${id}&templateId=${template.id}">
                        <img title="<I18n:message key="OBJECT_PROPERTIES_EDIT"/>" border="0" hspace="0" vspace="0"
                             src="<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.edit.gif"/>
                    </html:link>
                    <html:link styleClass="internal"
                               href="${contextPath}/TemplateViewAction.do?method=page&id=${id}&templateId=${template.id}">
                        <c:out value="${template.name}"/>
                    </html:link>
                </c:otherwise>
            </c:choose>
            </span>
                                        </td>
                                        <td>
                                            <c:if test="${template.user ne null}"><span style="white-space: nowrap;"><span
                                                    class="user" ${template.user.id eq sc.userId ? "id='loggedUser'" : ""}>
            <html:img styleClass="icon" border="0"
                      src="${contextPath}${ImageServlet}/cssimages/${template.user.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
            <c:out value="${template.user.name}" escapeXml="true"/>
			</span></span></c:if>
                                        </td>
                                        <td><span style="white-space: nowrap;"><span
                                                class="user" ${template.owner.id eq sc.userId ? "id='loggedUser'" : ""}>
            <html:img styleClass="icon" border="0"
                      src="${contextPath}${ImageServlet}/cssimages/${template.owner.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
            <c:out value="${template.owner.name}" escapeXml="true"/>
			</span></span></td>
                                        <td><span style="white-space: nowrap;"><c:choose>
                                            <c:when test="${template.active}">
                                                <html:img src="${contextPath}${ImageServlet}/cssimages/ico.checked.gif"/>
                                            </c:when>
                                            <c:otherwise>
                                                <html:img src="${contextPath}${ImageServlet}/cssimages/ico.unchecked.gif"/>
                                            </c:otherwise>
                                        </c:choose></span></td>
                                    </tr>
                                </c:forEach>
                            </table>
                            <div class="controls">
                                <c:if test="${canCreateObject}">
                                    <input type="submit" class="iconized secondary" value="<I18n:message key="CLONE"/>"
                                           name="CLONE" onclick="set('clone');">
                                </c:if>
                                <c:if test="${canDelete}">
                                    <input type="submit" class="iconized" value="<I18n:message key="DELETE"/>"
                                           name="DELETE" onclick="set('delete');">
                                </c:if>
                            </div>
                        </html:form>
                    </c:when>
                    <c:otherwise>
                        <div class="empty"><I18n:message key="EMPTY_TEMPLATE_LIST"/></div>
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
                                                                          href="${contextPath}/TemplateAction.do?method=page&amp;id=${task.key.id}">
                                        <html:img styleClass="icon" border="0"
                                                  src="${contextPath}${ImageServlet}/icons/categories/${task.key.category.icon}"/>
                                        <c:out value="${task.key.name}"/>
                                    </html:link></span><span class="itempath"><html:link styleClass="internal"
                                                                                         href="${contextPath}/TemplateAction.do?method=page&amp;id=${task.key.id}">
                                        <c:forEach var="path" items="${task.key.ancestors}">
                                            <span class="separated"><c:out value="${path.name}"/></span>&nbsp;/

                                        </c:forEach>
                                    </html:link><c:if test="${task.key.parentId ne null}">
                                        <html:link styleClass="internal" href="${contextPath}/TemplateAction.do?method=page&amp;id=${task.key.id}">
                                            <c:out value="${task.key.name}"/>
                                        </html:link>
                                    </c:if>
        </span>
                                    </dt>
                                    <dd>
                                        <c:forEach var="cat" items="${task.value}" varStatus="varC">
                                            <c:if test="${varC.index > 0}">,</c:if>
                                            <span style="white-space: nowrap;">
                                            <html:link styleClass="internal"
                                                       href="${contextPath}/TemplateViewAction.do?method=page&templateId=${cat.id}&id=${task.key.id}"
                                                       title="${cat.name}">
                                                <html:img styleClass="icon" border="0"
                                                          src="${contextPath}${ImageServlet}/cssimages/ico.template.gif"/>
                                                <c:out value="${cat.name}"/>
                                                </span>
                                            </html:link>
                                        </c:forEach>
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
