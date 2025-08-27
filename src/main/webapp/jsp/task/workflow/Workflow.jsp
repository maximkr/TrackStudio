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
    <I18n:message key="WORKFLOWS_LIST"/>
</div>
<c:if test="${canManage}">
    <div class="controlPanel">
        <html:link  href="${contextPath}/WorkflowEditAction.do?method=page&id=${id}">
            <html:img src="${contextPath}${ImageServlet}/cssimages/ico.workflow.gif" hspace="0" vspace="0" border="0"
                      align="middle"/>
            <I18n:message key="WORKFLOW_ADD"/>
        </html:link>
    </div>
</c:if>

<script type="text/javascript">
    var cancelDelete = false;

    function deleteWorkflow() {
        return deleteConfirm("<I18n:message key="DELETE_WORKFLOWS_REQ"/>", "workflowForm");
    }

    function clonWorkflow() {
       return clonConfirm("<I18n:message key="CLONE_WORKFLOWS"/>", "workflowForm");
    }

    function onSubmitFunction(frm) {
        return !cancelDelete;
    }

    function set(target) {
        document.getElementById('workflowListId').value = target;
    }
    ;
</script>

<div class="indent">
<c:choose>

<c:when test="${!empty workflowSet}">
    <html:form action="/WorkflowAction" method="post" styleId="checkunload">
        <html:hidden property="method" value="delete" styleId="workflowListId"/>
        <html:hidden property="id" value="${id}"/>
        <html:hidden property="session" value="${session}"/>
        <html:hidden property="workflowId" value="${flow.id}"/>
        <table class="general" cellpadding="0" cellspacing="0">
            <tr class="wide">
                <th width='1%' nowrap style="white-space:nowrap"><input type="checkbox"
                                                                        onClick="selectAllCheckboxes(this, 'delete1')">
                </th>

                <th>
                    <I18n:message key="WORKFLOW"/>
                </th>
                <th>
                    <I18n:message key="CONNECTED_TO_CATEGORY"/>
                </th>
            </tr>
            <c:if test="${flow ne null}">
                <c:forEach var="workflow" items="${workflowSet}" varStatus="varCounter">
                    <tr class="line<c:out value="${varCounter.index mod 2}"/>">

                        <td>
                            <c:choose>
                                <c:when test="${workflow.canManage}">
                                        <span style="text-align: center;">
                                            <input type="checkbox" name="delete" alt="delete1"
                                                   quickCheckboxSelectGroup="delete1"
                                                   value="<c:out value="${workflow.id}"/>">
                                        </span>
                                </c:when>
                                <c:otherwise>
                                    &nbsp;
                                </c:otherwise>
                            </c:choose>
                        </td>

                        <td>
                            <c:choose>
                                <c:when test="${!workflow.canManage}">
                                    <html:link  styleClass="internal"
                                               href="${contextPath}/WorkflowViewAction.do?method=page&workflowId=${workflow.id}&id=${id}">
                                        <img title="<I18n:message key="OBJECT_PROPERTIES_VIEW"/>" border="0"
                                             hspace="0" vspace="0"
                                             src="<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.closed.gif"/>
                                    </html:link>
                                </c:when>
                                <c:otherwise>
                                    <html:link  styleClass="internal"
                                               href="${contextPath}/WorkflowEditAction.do?method=page&workflowId=${workflow.id}&id=${id}">
                                        <img title="<I18n:message key="OBJECT_PROPERTIES_EDIT"/>" border="0"
                                             hspace="0" vspace="0"
                                             src="<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.edit.gif"/>
                                    </html:link>
                                </c:otherwise>
                            </c:choose>

                            <html:link  styleClass="internal" styleId="${workflow.id eq tci.workflowId ? 'current' : ''}"
                                       href="${contextPath}/WorkflowViewAction.do?method=page&workflowId=${workflow.id}&id=${id}">
                                <c:if test="${!workflow.isValid}">
                                    <img border="0" hspace="0" vspace="0" title="<I18n:message key="WORKFLOW_INVALID"/>"
                                         src="<c:out value="${contextPath}"/>${ImageServlet}/cssimages/warning.gif"/>
                                </c:if>
                                <c:out value="${workflow.name}" escapeXml="true"/>
                            </html:link>

                        </td>
                        <td>
                            <c:forEach var="cat" items="${workflow.categories}" varStatus="varC">
                                <c:if test="${varC.index > 0}">,</c:if>
                                <c:choose>
                                    <c:when test="${cat.canManage}">
                                            <html:link styleClass="internal"
                                        href="${contextPath}/CategoryViewAction.do?method=page&amp;categoryId=${cat.id}&amp;id=${id}">
                                                <html:img
                                                        styleClass="icon" titleKey="OBJECT_PROPERTIES_VIEW" border="0"
                                                        hspace="0"
                                                        vspace="0" src="${contextPath}${ImageServlet}/cssimages/ico.categories.gif"/>
                                                <c:if test="${!cat.isValid}">
                                                    <img border="0" hspace="0" vspace="0" title="<I18n:message key="CATEGORY_INVALID"/>"
                                                         src="<c:out value="${contextPath}"/>${ImageServlet}/cssimages/warning.gif"/>
                                                </c:if>
                                    <c:out value="${cat.name}"/>
                                </html:link>
                                    </c:when>
                                    <c:otherwise>
                                <span class="link">
                                    <html:img
                                            styleClass="icon" titleKey="OBJECT_PROPERTIES_VIEW" border="0"
                                            hspace="0"
                                            vspace="0" src="${contextPath}${ImageServlet}/cssimages/ico.categories.gif"/>
                                    <html:img titleKey="OBJECT_PROPERTIES_VIEW" border="0"
                                             hspace="0" vspace="0"
                                             src="${contextPath}${ImageServlet}/cssimages/ico.closed.gif"/>
                                    <c:out value="${cat.name}"/>
                                </span>        
                                    </c:otherwise>
                                </c:choose>

                            </c:forEach>
                        </td>
                    </tr>
                </c:forEach>
            </c:if>
        </table>
        <c:if test="${canManage}">
            <div class="controls">
                <c:if test="${canCopy eq true}">
                    <input type="SUBMIT" class="iconized secondary"
                           value="<I18n:message key="CLONE"/>"
                           name="SETFILTER" onClick="clonWorkflow(); set('clone');">
                </c:if>
                <c:if test="${canManage}">
                    <input type="submit" class="iconized" value="<I18n:message key="DELETE"/>"
                           name="DELETE" onClick="deleteWorkflow(); set('delete');">
                </c:if>
            </div>
        </c:if>
    </html:form>
</c:when>

    <c:otherwise>
        <div class="empty"><I18n:message key="EMPTY_WORKFLOW_LIST"/></div>
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
                                        href="${contextPath}/WorkflowAction.do?method=page&amp;id=${task.key.id}">
                                    <html:img styleClass="icon" border="0"
                                              src="${contextPath}${ImageServlet}/icons/categories/${task.key.category.icon}"/>
                                    <c:out value="${task.key.name}"/>
                                </html:link>
                                </span>
                                <span class="itempath">
                                    <html:link styleClass="internal"
                                            href="${contextPath}/WorkflowAction.do?method=page&amp;id=${task.key.id}">
                                        <c:forEach var="path" items="${task.key.ancestors}">
                                            <span class="separated"><c:out value="${path.name}"/></span>&nbsp;/

                                        </c:forEach>
                                    </html:link>
                                    <c:if test="${task.key.parentId ne null}">
                                        <html:link styleClass="internal"
                                                href="${contextPath}/WorkflowAction.do?method=page&amp;id=${task.key.id}">
                                            <c:out value="${task.key.name}"/>
                                        </html:link>
                                    </c:if>
                                </span>
                            </dt>
                            <dd>
                                <c:forEach var="cat" items="${task.value}" varStatus="varC">
                                    <span style="white-space: nowrap;">
                                        <html:link styleClass="internal"
                                                   href="${contextPath}/WorkflowViewAction.do?method=page&workflowId=${cat.id}&id=${task.key.id}"
                                                   title="${cat.name}"
                                                   styleId="${cat.id eq currentWorkflowId ? \"current\" : \"\"}">
                                            <html:img styleClass="icon" border="0"
                                                      src="${contextPath}${ImageServlet}/cssimages/ico.workflow.gif"/>
                                            <c:out value="${cat.name}"/>
                                        </html:link>
                                    </span><c:if test="${!varC.last}">,</c:if>
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
