<%@ page buffer="128kb" errorPage="/jsp/Error.jsp"%>
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
<tiles:put name="title" value="${title}"/>

<tiles:put name="header" value="/jsp/user/UserHeader.jsp"/>
<tiles:put name="tabs" type="string"/>
<tiles:put name="customHeader" type="string"/>
<tiles:put name="main" type="string">
<c:if test="${canViewAccessControl}">
<div class="blueborder">
<div class="caption">
    <I18n:message key="ACCESS_CONTROL_LIST"/>
</div>
<div class="controlPanel">
    <html:link  href="${contextPath}/UserEffectivePermissionAction.do?method=page&amp;id=${id}"><html:img src="${contextPath}${ImageServlet}/cssimages/ico.effective.gif" styleClass="icon" border="0"/><I18n:message key="EFFECTIVE_PRSTATUSES"/></html:link>
    <c:if test="${!(empty paramCollection) || !(empty statusCollection)}">
        <c:if test="${canCreateAccessControl}">
            <label class="expandable" for="createUserACL" onclick="showHide(this);"><a class="internal" href="javascript://nop/"><html:img src="${contextPath}${ImageServlet}/cssimages/ico.adduser.gif" styleClass="icon" border="0"/><I18n:message key="ACL_ADD"/></a></label>
        </c:if>
    </c:if>
</div>
<div class="indent">
<c:if test="${canCreateAccessControl}">
    <c:set var="tileId" value="createUserACL"/>
    <c:import url="/jsp/task/tiles/CreateAclTile.jsp">
        <c:param name="tileId" value="createUserACL"/>
    </c:import>
</c:if>
<c:choose>
    <c:when test="${!empty acls}">
        <html:form method="POST" action="/UserACLAction" styleId="checkunload" onsubmit="return allow(this);">
            <html:hidden property="method" value="save" styleId="aclId"/>
            <html:hidden property="id" value="${id}"/>
            <html:hidden property="session" value="${session}"/>
            <div class="general">
                <table class="general" cellpadding="0" cellspacing="0">
                    <tr class="wide">
                        <c:if test="${canDeleteAccessControl}">
                            <th width='1%' nowrap style="white-space:nowrap"> <input type="checkbox" onClick="selectAllCheckboxes(this, 'delete1')">
                            </th>
                        </c:if>
                        <th><I18n:message key="USER"/></th>
                        <th width="20%"><I18n:message key="ASSIGNED_PRSTATUS"/></th>
                        <th width='5%'><I18n:message key="OVERRIDE"/></th>
                        <th width="20%"><I18n:message key="OWNER"/></th>

                    </tr>
                    <c:forEach var="acl" items="${acls}" varStatus="varCounter">
                        <tr class="line<c:out value="${varCounter.index mod 2}"/>">
                            <c:if test="${canDeleteAccessControl}">
                                <td>
    <span style="text-align: center;">
    <c:choose>
        <c:when test="${acl.canUpdate}">
            <input type="checkbox" name="delete" quickCheckboxSelectGroup="delete1" value="<c:out value="${acl.id}"/>">
        </c:when>
        <c:otherwise>
            &nbsp;
        </c:otherwise>
    </c:choose>

    </span>
                                </td>
                            </c:if>
                            <c:choose>
                                <c:when test="${acl.forUser ne null}">
                                    <td>
        <span class="user" ${acl.forUser.id eq sc.userId ? "id='loggedUser'" : ""}>
            <html:img  styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/${acl.forUser.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
            <c:out value="${acl.forUser.name}" escapeXml="true"/>
			</span>
                                    </td>

                                    <c:choose>
                                        <c:when test="${acl.canUpdate && acl.connectedTo.id eq id && sc.userId ne acl.forUser.id}">
                                            <td>
                                                <html:select property="value(prstatus-${acl.id})">
                                                    <c:forEach var="prstatus" items="${acl.prstatus}">
                                                        <html:option value="${prstatus.id}"><c:out value="${prstatus.name}" escapeXml="true"/></html:option>
                                                    </c:forEach>
                                                </html:select>
                                            </td>
                                            <td>
        <span style="text-align: center;">
            <html:checkbox styleClass="checkbox" property="value(override-${acl.id})" value="1"/>
        </span>
                                            </td>
                                        </c:when>
                                        <c:otherwise>
                                            <td>
        <span class="user">
                        <html:img  styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/ico.status.gif"/><c:out value="${acl.withRole.name}" escapeXml="true"/>
		</span>
                                            </td>
                                            <td>
        <span style="text-align: center;">
        <html:checkbox styleClass="checkbox" property="value(override-${acl.id})" value="1" disabled="true"/>
            <c:if test="${acl.override}">
                <html:hidden property="value(override-${acl.id})" value="1"/>
            </c:if>
            </span>
                                            </td>
                                        </c:otherwise>
                                    </c:choose>

                                </c:when>
                                <c:otherwise>
                                    <td>
         <span class="user">
                        <html:img  styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/ico.status.gif"/><c:out value="${acl.forRole.name}" escapeXml="true"/>
		</span>
                                    </td>
                                    <c:choose>
                                        <c:when test="${acl.canUpdate && acl.connectedTo.id eq id && (acl.withRole ne '5' || acl.connectedTo.id ne '1')}">
                                            <td>
                                                <html:select property="value(prstatus-${acl.id})">
                                                    <c:forEach var="prstatus" items="${acl.prstatus}">
                                                        <html:option value="${prstatus.id}"><c:out value="${prstatus.name}" escapeXml="true"/></html:option>
                                                    </c:forEach>
                                                </html:select>
                                            </td>
                                            <td>
        <span style="text-align: center;">
            <html:checkbox styleClass="checkbox" property="value(override-${acl.id})" value="1"/>
        </span>
                                            </td>
                                        </c:when>
                                        <c:otherwise>
                                            <td>
            <span class="user">
                            <html:img  styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/ico.status.gif"/><c:out value="${acl.withRole.name}" escapeXml="true"/>
            </span>
                                            </td>
                                            <td>
        <span style="text-align: center;">
        <html:checkbox styleClass="checkbox" property="value(override-${acl.id})" value="1" disabled="true"/>
            <c:if test="${acl.override}">
                <html:hidden property="value(override-${acl.id})" value="1"/>
            </c:if>
        </span>
                                            </td>
                                        </c:otherwise>
                                    </c:choose>
                                </c:otherwise>
                            </c:choose>
                            <td><span class="user" ${acl.owner.id eq sc.userId ? "id='loggedUser'" : ""}>
            <html:img  styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/${acl.owner.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
            <c:out value="${acl.owner.name}" escapeXml="true"/>
			</span>
                            </td>

                        </tr>
                    </c:forEach>

                </table>
            </div>

            <div class="controls">
                <c:if test="${canCreateAccessControl}">
                    <input type="submit"  class="iconized"
                           value="<I18n:message key="SAVE"/>"
                           name="ADDUSER" onclick="set('save');">
                </c:if>
                <c:if test="${canDeleteAccessControl}">
                    <input type="submit"  class="iconized"
                           value="<I18n:message key="DELETE"/>"
                           name="DELETE" onclick="set('delete');">
                </c:if>
                <SCRIPT type="text/javascript">
                    function set(target) {document.getElementById('aclId').value=target;};
                </SCRIPT>
            </div>
        </html:form>
    </c:when>
    <c:otherwise>
        <div class="empty"><I18n:message key="EMPTY_ACL_LIST"/></div>
    </c:otherwise>
</c:choose>
<c:if test="${!empty seeAlso}">
    <br>
    <div class="blueborder">
    <div class="caption">
        <I18n:message key="SEE_ALSO"/>
    </div>
    <div class="indent">
    <c:forEach items="${seeAlso}" var="also"  varStatus="affected">
        <c:if test="${!empty also}">
            <dl ${affected.first ? "class='affected'" : ""}>

                <c:forEach var="task" items="${also}" varStatus="varCounter">
                    <dt><span class="itemname"><html:link styleClass="internal" href="${contextPath}/UserACLAction.do?method=page&amp;id=${task.key.id}"><html:img  styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/${task.key.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/><c:out value="${task.key.name}"/></html:link></span><span class="itempath"><html:link styleClass="internal" href="${contextPath}/UserACLAction.do?method=page&amp;id=${task.key.id}"><c:forEach var="path" items="${task.key.ancestors}">
                        <span class="separated"><c:out value="${path.name}"/></span>&nbsp;/
                    </c:forEach></html:link><c:if test="${task.key.parentId ne null}"><html:link styleClass="internal" href="${contextPath}/UserACLAction.do?method=page&amp;id=${task.key.id}"><c:out value="${task.key.name}"/></html:link></c:if>
        </span>
                    </dt>
                    <dd>
                        <c:forEach var="cat" items="${task.value}" varStatus="varC"><c:if test="${varC.index > 0}">, </c:if><span style="white-space: nowrap;"><html:link styleClass="internal"
                                                                                                                                                                          href="${contextPath}/UserACLAction.do?method=page&amp;id=${task.key.id}"><c:choose><c:when test="${cat.forUser ne null}"><span class="user" ${cat.forUser.id eq sc.userId ? "id='loggedUser'" : ""}>
            <html:img  styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/${cat.forUser.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
            <c:out value="${cat.forUser.name}" escapeXml="true"/>
			</span></c:when><c:otherwise><html:img  styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/ico.status.gif"/><c:out value="${cat.forRole.name}" escapeXml="true"/></c:otherwise></c:choose></span></html:link></c:forEach>
                    </dd>
                </c:forEach>
            </dl>
        </c:if>
    </c:forEach>
</c:if>
</div>
</div>
</c:if>

</tiles:put>
</tiles:insert>