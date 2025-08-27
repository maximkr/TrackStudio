<%@ page buffer="128kb" errorPage="/jsp/Error.jsp" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/tiles-el" prefix="tiles" %>
<c:set var="taskMenu" value="false"/>
<c:set var="userMenu" value="true"/>
<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value="${sc.timezone}"/>
<I18n:setBundle basename="language"/>
<tiles:insert page="/jsp/layout/ListLayout.jsp" flush="true">
<tiles:put name="header" value="/jsp/user/UserHeader.jsp"/>
<tiles:put name="tabs" type="string"/>
    <tiles:put name="customHeader" type="string"/>
<tiles:put name="main" type="string">
<c:if test="${canViewList}">

<div class="blueborder">
    <div class="caption">
        <I18n:message key="PRSTATUSES_LIST"/>
    </div>

    <c:if test="${canCreateObject}">

        <div class="controlPanel">
            <html:link  href="${contextPath}/UserStatusEditAction.do?method=page&id=${id}">
                <html:img alt="" src="${contextPath}${ImageServlet}/cssimages/ico.edit.gif" border="0"/>
                <I18n:message key="PRSTATUS_ADD"/>
            </html:link>
        </div>

    </c:if>
    <div class="indent">
        <c:choose>
            <c:when test="${!empty rolesSet}">
        <html:form action="/UserStatusAction" styleId="checkunload" method="post" onsubmit="return onSubmitForm();">
            <table class="general" cellpadding="0" cellspacing="0">
                <html:hidden property="method" value="save" styleId="prstatusListId"/>
                <html:hidden property="id" value="${id}"/>
                <html:hidden property="session" value="${session}"/>
                <html:hidden property="prstatusId" value="${currentStatusId}"/>
                <tr class="wide">
                    <th width="1%" nowrap style="white-space:nowrap">
                        <input type="checkbox" onClick="selectAllCheckboxes(this, 'delete1')">
                    </th>
                    <th>
                        <I18n:message key="PRSTATUS"/>
                    </th>
                </tr>
                <c:forEach var="prstatus" items="${rolesSet}" varStatus="varCounter">
                    <tr class="line<c:out value="${varCounter.index mod 2}"/>">
                        <td>
                            <c:choose>
                                <c:when test="${prstatus.canManage}">
                                    <span style="text-align: center;"><c:if test="${roleAdmin!=prstatus.id}"><input type="checkbox" name="delete" alt="delete1"
                                                   value="<c:out value="${prstatus.id}"/>"> </c:if></span>
                                </c:when>
                            <c:otherwise>
                                &nbsp;
                            </c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${!prstatus.canManage}">
                                    <span class="link" ${prstatus.id eq currentUser.prstatusId ? "id='current'" : ""}>
                                        <img title="<I18n:message key="OBJECT_PROPERTIES_VIEW"/>" border="0"
                                             hspace="0" vspace="0" src="${contextPath}${ImageServlet}/cssimages/ico.closed.gif"/>
                                <c:out value="${prstatus.name}" escapeXml='true'/>
                                    </span>
                                </c:when>
                                <c:otherwise>
                                    <html:link styleClass="internal"
                                            href="${contextPath}/UserStatusEditAction.do?method=page&prstatusId=${prstatus.id}&id=${id}">
                                        <img title="<I18n:message key="OBJECT_PROPERTIES_EDIT"/>" border="0"
                                             hspace="0" vspace="0" src="${contextPath}${ImageServlet}/cssimages/ico.edit.gif"/>
                                    </html:link>
                                    <html:link styleClass="internal"
                                    href="${contextPath}/UserStatusViewAction.do?method=page&prstatusId=${prstatus.id}&id=${id}">
                                <c:if test="${prstatus.id eq currentUser.prstatusId}">
                                    <img border="0" hspace="0" vspace="0"
                                         src="${contextPath}${ImageServlet}/cssimages/ico.current.gif"/>
                                </c:if>
                                <c:out value="${prstatus.name}" escapeXml='true'/>
                            </html:link>
                                </c:otherwise>
                            </c:choose>

                        </td>
                    </tr>
                </c:forEach>
            </table>

            <div class="controls">
                <c:if test="${canCopy}">
                    <input type="SUBMIT" class="iconized secondary"
                           value="<I18n:message key="CLONE"/>"
                           name="copyButton" onClick="if (cloneRole()) set('clone');">
                </c:if>
                <c:if test="${canDelete}">
                    <input type="SUBMIT" class="iconized"
                           value="<I18n:message key="DELETE"/>"
                           name="deleteButton" onClick="if (deleteRole()) set('delete');">
                </c:if>
                <script type="text/javascript">
                    var action = false;
                    function set(target) {
                        document.getElementById('prstatusListId').value = target;
                    }

                    function deleteRole() {
                        action = deleteConfirm("<I18n:message key="DELETE_PRSTATUSES_REQ"/>", "prstatusForm");
                        return action;
                    }

                    function cloneRole() {
                        action = deleteConfirm("<I18n:message key="CLONE_PRSTATUSES_REQ"/>", "prstatusForm");
                        return action;
                    }

                    function onSubmitForm(){
                        return action;
                    }
                </script>
            </div>
        </html:form>
        </c:when>
    <c:otherwise>
        <div class="empty"><I18n:message key="EMPTY_PRSTATUS_LIST"/></div>
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
                            <dt>
                                <span class="itemname">
                                    <html:link styleClass="internal"
                                            href="${contextPath}/UserStatusAction.do?method=page&amp;id=${task.key.id}">
                                        <html:img styleClass="icon" border="0"
                                                  src="${contextPath}${ImageServlet}/cssimages/${task.key.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
                                        <c:out value="${task.key.name}"/>
                                    </html:link>
                                </span>
                                <span class="itempath">
                                    <html:link styleClass="internal"
                                            href="${contextPath}/UserStatusAction.do?method=page&amp;id=${task.key.id}">
                                        <c:forEach var="path" items="${task.key.ancestors}">
                                            <span class="separated"><c:out value="${path.name}"/></span>&nbsp;/

                                        </c:forEach>
                                    </html:link>
                                    <c:if test="${task.key.parentId ne null}">
                                        <html:link styleClass="internal"
                                                href="${contextPath}/UserStatusAction.do?method=page&amp;id=${task.key.id}">
                                            <c:out value="${task.key.name}"/>
                                        </html:link>
                                    </c:if>
                                </span>
                            </dt>
                            <dd>
                                <c:forEach var="cat" items="${task.value}" varStatus="varC">
                                    <c:if test="${varC.index > 0}">,</c:if>
                                    <span style="white-space: nowrap;">
                                        <c:choose>
                                            <c:when test="${cat.canManage}">
                                        <html:link styleClass="internal"
                                                   href="${contextPath}/UserStatusViewAction.do?method=page&amp;prstatusId=${cat.id}&amp;id=${task.key.id}"
                                                   title="${cat.name}"
                                                   styleId="${cat.id eq currentUserStatusId ? \"current\" : \"\"}">
                                            <html:img styleClass="icon" border="0"
                                                      src="${contextPath}${ImageServlet}/cssimages/ico.status.gif"/>
                                            <c:out value="${cat.name}"/>
                                        </html:link>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="link" id="${cat.id eq currentUserStatusId ? "current" : ""}"><html:img styleClass="icon" border="0"
                                                      src="${contextPath}${ImageServlet}/cssimages/ico.status.gif"/><c:out value="${cat.name}"/></span>
                                            </c:otherwise>
                                        </c:choose>
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
</c:if>
</tiles:put>
</tiles:insert>

