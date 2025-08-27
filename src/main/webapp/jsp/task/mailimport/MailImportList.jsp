<%@ page buffer="128kb" errorPage="/jsp/Error.jsp" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/tiles-el" prefix="tiles" %>
<%@ taglib uri="http://trackstudio.com" prefix="ts" %>
<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value="${sc.timezone}"/>
<I18n:setBundle basename="language"/>
<tiles:insert page="/jsp/layout/ListLayout.jsp" flush="true">
    <tiles:put name="customHeader" type="string"/>
    <tiles:put name="header" value="/jsp/task/TaskHeader.jsp"/>
    <tiles:put name="customHeader" type="string"/>
    <tiles:put name="tabs" type="string"/>
    <tiles:put name="main" type="string">
        <div class="blueborder">
            <div class="caption">
                <I18n:message key="EMAIL_IMPORT_LIST"/>
            </div>
            <c:if test="${canCreateObject}">
                <div class="controlPanel">
                    <html:link  href="${contextPath}/MailImportAction.do?method=create&id=${id}"><html:img src="${contextPath}${ImageServlet}/cssimages/ico.edit.gif" border="0" altKey="EDIT"/>
                        <I18n:message key="MAIL_IMPORT_RULE_ADD"/>
                    </html:link>
                </div>
            </c:if>

            <script type="text/javascript">
                var cancelDelete = false;

                function deleteMailImport() {
                    if (document.getElementById('mailMethod').value == 'delete') {
                        cancelDelete = !deleteConfirm("<I18n:message key="DELETE_EMAIL_IMPORT_RULE_REQ"/>", "mailImportForm");
                    } else {
                        cancelDelete = true;
                    }
                }

                function onSubmitFunction() {
                    return !cancelDelete;
                }

                function set(target) {
                    document.getElementById('mailMethod').value = target;
                }
            </script>

            <div class="indent">
                <c:choose>
                    <c:when test="${!empty mailImportList}">
                        <html:form method="POST" action="/MailImportAction" onsubmit="return onSubmitFunction();">
                            <html:hidden property="method" value="delete" styleId="mailMethod"/>
                            <html:hidden property="id" value="${id}"/>
                            <html:hidden property="session" value="${session}"/>
                            <div class="general">
                                <table class="general" cellpadding="0" cellspacing="0">
                                    <caption><I18n:message key="EMAIL_IMPORT_LIST"/></caption>
                                    <tr class="wide">
                                        <th width="1%" nowrap style="white-space:nowrap"><input type="checkbox" onClick="selectAllCheckboxes(this, 'delete1')"></th>
                                        <th><I18n:message key="NAME"/></th>
                                        <th><I18n:message key="ACTIVE"/></th>
                                        <th><I18n:message key="ORDER"/></th>
                                        <th><I18n:message key="CATEGORY"/></th>
                                        <th><I18n:message key="MESSAGE_TYPE"/></th>
                                        <th><I18n:message key="OWNER"/></th>
                                    </tr>
                                    <c:forEach var="mailImport" items="${mailImportList}" varStatus="varCounter">
                                        <tr class="line<c:out value="${varCounter.index mod 2}"/>">
                                            <td>
            <span style="text-align: center">
                <input type="checkbox" class=checkbox name="delete" alt="delete1" quickCheckboxSelectGroup="delete1" value="<c:out value="${mailImport.id}"/>">
            </span>
                                            </td>
                                            <td>
            <span style="white-space: nowrap;">
            <c:choose>
                <c:when test="${!mailImport.canUpdate}">
                    <html:link  styleClass="internal" href="${contextPath}/MailImportViewAction.do?method=page&id=${id}&mailImportId=${mailImport.id}">
                        <img title="<I18n:message key="OBJECT_PROPERTIES_VIEW"/>" border="0" hspace="0" vspace="0" src="<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.closed.gif"/>
                        <c:out value="${mailImport.name}"/>
                    </html:link>
                </c:when>
                <c:otherwise>
                    <html:link styleClass="internal"  href="${contextPath}/MailImportEditAction.do?method=page&id=${id}&mailImportId=${mailImport.id}">
                        <img title="<I18n:message key="OBJECT_PROPERTIES_EDIT"/>" border="0" hspace="0" vspace="0" src="<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.edit.gif"/>
                    </html:link>
                    <html:link styleClass="internal"  href="${contextPath}/MailImportViewAction.do?method=page&id=${id}&mailImportId=${mailImport.id}">
                        <c:out value="${mailImport.name}"/>
                    </html:link>
                </c:otherwise>
            </c:choose>
            </span>
                                            </td>
                                            <td>
            <span style="white-space: nowrap;">
                <c:choose>
                    <c:when test="${mailImport.active}">
                        <html:img src="${contextPath}${ImageServlet}/cssimages/ico.checked.gif"/>
                    </c:when>
                    <c:otherwise>
                        <html:img src="${contextPath}${ImageServlet}/cssimages/ico.unchecked.gif"/>
                    </c:otherwise>
                </c:choose>
            </span>
                                            </td>
                                            <td><span style="white-space: nowrap;"><c:out value="${mailImport.order}"/></span></td>
                                            <td><span style="white-space: nowrap;"><c:out value="${mailImport.category}" escapeXml="false"/></span></td>
                                            <td><span style="white-space: nowrap;"><c:out value="${mailImport.mstatus}" escapeXml="false"/></span></td>
                                            <td>
        <span style="white-space: nowrap;">
            <c:choose>
                <c:when test="${mailImport.owner ne null}">
			        <span class="user" ${mailImport.owner.user.id eq sc.userId ? "id=\"loggedUser\"" : ""}>
                        <html:img  styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/${mailImport.owner.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
                        <ts:htmluser user="${mailImport.owner}">&nbsp;</ts:htmluser>
			        </span>
                </c:when>
            </c:choose>
        </span>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </table>
                            </div>
                            <div class="controls">
                                <c:if test="${canCreateObject}">
                                    <input type="submit"  class="iconized secondary"
                                           value="<I18n:message key="CLONE"/>"
                                           name="CLONE" onClick="set('clone');">
                                </c:if>
                                <c:if test="${canDelete}">
                                    <input type="submit"  class="iconized"
                                           value="<I18n:message key="DELETE"/>"
                                           name="DELETE" onClick="set('delete'); deleteMailImport(); if(onSubmitFunction()) this.form.submit();">
                                </c:if>
                            </div>
                        </html:form>
                    </c:when>
                    <c:otherwise>
                        <div class="empty"><I18n:message key="EMPTY_MAILIMPORT_LIST"/></div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <c:if test="${!empty seeAlso}">
            <br>
            <div class="blueborder">
                <div class="caption"><I18n:message key="SEE_ALSO"/></div>
                <div class="indent">
                    <c:forEach items="${seeAlso}" var="also" varStatus="affected">
                        <c:if test="${!empty also}">
                            <dl ${affected.first ? "class='affected'" : ""}>
                                <c:forEach var="task" items="${also}" varStatus="varCounter">
                                    <dt>
                    <span class="itemname">
                        <html:link styleClass="internal" href="${contextPath}/MailImportAction.do?method=page&amp;id=${task.key.id}">
                            <html:img  styleClass="icon" border="0" src="${contextPath}${ImageServlet}/icons/categories/${task.key.category.icon}"/><c:out value="${task.key.name}"/></html:link>
                    </span>
                    <span class="itempath">
                        <html:link styleClass="internal" href="${contextPath}/MailImportAction.do?method=page&amp;id=${task.key.id}">
                            <c:forEach var="path" items="${task.key.ancestors}">
                                <span class="separated"><c:out value="${path.name}"/></span>&nbsp;/
                            </c:forEach>
                        </html:link>
                        <c:if test="${task.key.parentId ne null}">
                            <html:link styleClass="internal" href="${contextPath}/MailImportAction.do?method=page&amp;id=${task.key.id}">
                                <c:out value="${task.key.name}"/>
                            </html:link>
                        </c:if>
                    </span>
                                    </dt>
                                    <dd>
                                        <c:forEach var="cat" items="${task.value}" varStatus="varC"><c:if test="${varC.index > 0}">, </c:if>
                    <span style="white-space: nowrap;">
                        <html:link  styleClass="internal" href="${contextPath}/MailImportViewAction.do?method=page&id=${task.key.id}&mailImportId=${cat.id}" title="${cat.name}">
                            <html:img  styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/ico.notifications.gif"/><c:out value="${cat.name}"/>
                        </html:link>
                    </span>
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
