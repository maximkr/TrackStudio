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
<tiles:put name="header" value="/jsp/task/TaskHeader.jsp"/>
<tiles:put name="customHeader" type="string"/>
<tiles:put name="tabs" type="string"/>

<tiles:put name="main" type="string">
<c:if test="${canManage}">
<div class="blueborder">
<div class="caption">
    <I18n:message key="ACCESS_CONTROL_LIST"/>
</div>
<c:if test="${canEditTaskACL}">
    <div class="controlPanel">   &nbsp;
        <html:link href="${contextPath}/EffectivePermissionAction.do?method=page&amp;id=${id}"><html:img src="${contextPath}${ImageServlet}/cssimages/ico.effective.gif" styleClass="icon" border="0"/><I18n:message key="TASK_EFFECTIVE_PRSTATUSES"/></html:link>
        <c:if test="${!(empty handlerColl) || !(empty statusCollection)}">
        <label class="expandable" for="createTaskACL" onclick="showHide(this);"><a  href="javascript://nop/">
            <html:img src="${contextPath}${ImageServlet}/cssimages/ico.adduser.gif" styleClass="icon" border="0"/><I18n:message key="ACL_ADD"/>
        </a></label>
    </c:if>
    </div>
</c:if>
<div class="indent">
<div class="labels">
        <c:set var="tileId" value="createTaskACL"/>
        <c:import url="/jsp/task/tiles/CreateAclTile.jsp">
            <c:param name="tileId" value="createTaskACL"/>
        </c:import>
</div>
<c:choose>
<c:when test="${!empty aclList}">
<html:form method="POST" action="/ACLAction" styleId="checkunload" onsubmit="return allow(this);">
<html:hidden property="method" value="save" styleId="aclId"/>
<html:hidden property="id" value="${id}"/>
<html:hidden property="session" value="${session}"/>
<div class="general">
<table class="general" cellpadding="0" cellspacing="0">
<tr class="wide">
    <th width="1%" nowrap style="white-space: nowrap;"><input type="checkbox"
                                                            onClick="selectAllCheckboxes(this, 'delete1')">
    </th>
    <th>
        <I18n:message key="USER"/>
    </th>
    <th width="30%">
        <I18n:message key="ASSIGNED_PRSTATUS"/>
    </th>
    <th width='5%'>
        <I18n:message key="OVERRIDE"/>
    </th>
    <th width="30%">
        <I18n:message key="OWNER"/>
    </th>
</tr>
<c:forEach var="acl" items="${aclList}" varStatus="varCounter">
<tr class="line<c:out value="${varCounter.index mod 2}"/>">
<td>
    <span style="text-align: center;">
        <c:choose>
            <c:when test="${acl.canManage && acl.toTask.id eq id && (acl.forUser eq null || sc.userId ne acl.forUser.id)}">
                <input type="checkbox" name="delete" quickCheckboxSelectGroup="delete1"
                       value="<c:out value="${acl.id}"/>">
            </c:when>
            <c:otherwise>
                &nbsp;
            </c:otherwise>
        </c:choose>
    </span>
</td>

<c:choose>
    <c:when test="${acl.forUser ne null}">
        <td>
        <span class="user" ${acl.forUser.id eq sc.userId ? "id='loggedUser'" : ""}>
            <html:img styleClass="icon" border="0"
                      src="${contextPath}${ImageServlet}/cssimages/${acl.forUser.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
            <c:out value="${acl.forUser.name}" escapeXml="true"/>
			</span>
        </td>

        <c:choose>
            <c:when test="${acl.canManage && acl.toTask.id eq id && sc.userId ne acl.forUser.id}">
                <td>
                    <html:select property="value(prstatus-${acl.id})">
                        <c:forEach var="prstatus" items="${acl.prstatus}">
                            <html:option value="${prstatus.id}">
                                <c:out value="${prstatus.name}" escapeXml="true"/>
                            </html:option>
                        </c:forEach>
                    </html:select>
                </td>
                <td>
                    <center>
                        <html:checkbox styleClass="checkbox" property="value(override-${acl.id})" value="1"/>
                    </center>
                </td>
            </c:when>
            <c:otherwise>
                <td>
        <span class="user">
                        <html:img styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/ico.status.gif"/><c:out
                value="${acl.withPrstatus.name}" escapeXml="true"/>
		</span>
                </td>
                <td>
                    <center>
                        <html:checkbox styleClass="checkbox" property="value(override-${acl.id})" value="1"
                                       disabled="true"/>
                        <c:if test="${acl.override}">
                            <html:hidden property="value(override-${acl.id})" value="1"/>
                        </c:if>
                    </center>
                </td>
            </c:otherwise>
        </c:choose>

    </c:when>
    <c:otherwise>
        <td>
         <span class="user">
                        <html:img styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/ico.status.gif"/><c:out
                 value="${acl.forPrstatus.name}" escapeXml="true"/>
		</span>
        </td>
        <c:choose>
            <c:when test="${acl.canManage && acl.toTask.id eq id && (acl.withPrstatus ne '5' || acl.toTask.id ne '1')}">
                <td>
                    <html:select property="value(prstatus-${acl.id})">
                        <c:forEach var="prstatus" items="${acl.prstatus}">
                            <html:option value="${prstatus.id}">
                                <c:out value="${prstatus.name}" escapeXml="true"/>
                            </html:option>
                        </c:forEach>
                    </html:select>
                </td>
                <td>
                    <center>
                        <html:checkbox styleClass="checkbox" property="value(override-${acl.id})" value="1"/>
                    </center>
                </td>
            </c:when>
            <c:otherwise>
                <td>
            <span class="user">
                            <html:img styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/ico.status.gif"/><c:out
                    value="${acl.withPrstatus.name}" escapeXml="true"/>
            </span>
                </td>
                <td>
                    <center>
                        <html:checkbox styleClass="checkbox" property="value(override-${acl.id})" value="1"
                                       disabled="true"/>
                        <c:if test="${acl.override}">
                            <html:hidden property="value(override-${acl.id})" value="1"/>
                        </c:if>
                    </center>
                </td>
            </c:otherwise>
        </c:choose>
    </c:otherwise>
</c:choose>

<td>
    <c:if test="${acl.owner ne null}">
            <span class="user" ${acl.owner.id eq sc.userId ? "id='loggedUser'" : ""}>
            <html:img styleClass="icon" border="0"
                      src="${contextPath}${ImageServlet}/cssimages/${acl.owner.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
            <c:out value="${acl.owner.name}" escapeXml="true"/>
			</span>
    </c:if>
</td>
</tr>
</c:forEach>

</table>
</div>
<c:out value="${slider}" escapeXml="false"/>
<c:if test="${canManage}">
    <div class="controls">
	        <input type="submit" class="iconized"
               value="<I18n:message key="SAVE"/>"
               name="ADDUSER" onclick="set('save');">
        <input type="submit" class="iconized secondary"
               value="<I18n:message key="CUT"/>"
               name="CUT" onclick="set('clipboardOperation');">
        <input type="submit" class="iconized secondary"
               value="<I18n:message key="COPY"/>"
               name="SINGLE_COPY" onclick="set('clipboardOperation');">
        <input type="submit" class="iconized secondary"
               value="<I18n:message key="COPY_ALL"/>"
               name="ALL_COPY" onclick="set('clipboardOperation');">
        <c:if test="${!clipBoardEmpty}">
            <input type="submit" class="iconized secondary"
                   value="<I18n:message key="PASTE"/>"
                   name="PASTE" onclick="set('paste');">
        </c:if>


            <input type="submit" class="iconized secondary"
                   value="<I18n:message key="DELETE"/>"
                   name="DELETE" onclick="set('delete');">

        <SCRIPT type="text/javascript">
            function set(target) {
                document.getElementById('aclId').value = target;
            }
            ;
        </SCRIPT>
    </div>
</c:if>

</html:form>
</c:when>
<c:otherwise>
    <div class="empty"><I18n:message key="EMPTY_ACL_LIST"/></div>
    <html:form method="POST" action="/ACLAction" styleId="checkunload" onsubmit="return allow(this);">
    <html:hidden property="method" value="save" styleId="aclId"/>
    <html:hidden property="id" value="${id}"/>
    <html:hidden property="session" value="${session}"/>
    <div class="controls">
        <c:if test="${!clipBoardEmpty}">
            <input type="submit" class="iconized secondary"
                   value="<I18n:message key="PASTE"/>"
                   name="PASTE" onclick="set('paste');">
        </c:if>
        <SCRIPT type="text/javascript">
            function set(target) {
                document.getElementById('aclId').value = target;
            };
        </SCRIPT>
    </div>
    </html:form>
</c:otherwise>
</c:choose>
</div>
</div>

</c:if>
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
                            <dt><span class="itemname"><html:link styleClass="internal" href="${contextPath}/ACLAction.do?method=page&amp;id=${task.key.id}">
                                <html:img styleClass="icon" border="0" src="${contextPath}${ImageServlet}/icons/categories/${task.key.category.icon}"/>
                                <c:out value="${task.key.name}"/>
                            </html:link></span><span class="itempath"><html:link styleClass="internal"
                                    href="${contextPath}/ACLAction.do?method=page&amp;id=${task.key.id}">
                                <c:forEach var="path" items="${task.key.ancestors}">
                                    <span class="separated"><c:out value="${path.name}"/></span>&nbsp;/
                                    
                                </c:forEach>
                            </html:link><c:if test="${task.key.parentId ne null}">
                                <html:link styleClass="internal" href="${contextPath}/ACLAction.do?method=page&amp;id=${task.key.id}">
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
                                               href="${contextPath}/ACLAction.do?method=page&amp;id=${task.key.id}">
                                        <c:choose>
                                            <c:when test="${cat.forUser ne null}">
                                                <span class="user" ${cat.forUser.id eq sc.userId ? "id='loggedUser'" : ""}>
                                                    <html:img styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/${cat.forUser.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
                                                    <ts:htmluser user="${cat.forUser}">&nbsp;</ts:htmluser>
			                                    </span>
                                            </c:when>
                                            <c:otherwise>
                                                <html:img styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/ico.status.gif"/>
                                                <c:out value="${cat.forPrstatus.name}" escapeXml="true"/>
                                            </c:otherwise>
                                        </c:choose>
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