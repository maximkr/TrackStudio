<%@ page buffer="128kb" errorPage="/jsp/Error.jsp" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/tiles-el" prefix="tiles" %>


<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value="${sc.timezone}"/>
<I18n:setBundle basename="language"/>
<tiles:insert page="/jsp/layout/ListLayout.jsp" flush="true">
<tiles:put name="customHeader" type="string"/>
<tiles:put name="tabs" type="string"/>
<tiles:put name="header" value="/jsp/task/TaskHeader.jsp"/>
<tiles:put name="main" type="string">
<div class="blueborder">
    <div class="caption">
        <I18n:message key="CATEGORIES_LIST"/>
    </div>
    <c:if test="${canManage}">
        <div class="controlPanel">
            <html:link styleClass="internal" href="${contextPath}/CategoryEditAction.do?method=page&amp;id=${id}">
                <html:img src="${contextPath}${ImageServlet}/cssimages/ico.edit.gif" styleClass="icon" border="0"/>
                <I18n:message key="CATEGORY_ADD"/>
            </html:link>
        </div>
    </c:if>
    <script type="text/javascript">
        var cancelDelete = false;

        function deleteCategory() {
            if (document.getElementById('method').value == 'delete') {
                return deleteConfirm("<I18n:message key="DELETE_CATEGORY_REQ"/>", "categoryForm");
            }
            return true;
        }

        function onSubmitFunction(frm) {
            return !cancelDelete;
        }

        function setMethodElement(target) {
            document.getElementById('method').value = target;
        }
    </script>

    <div class="indent">
        <c:choose>
            <c:when test="${!empty categorySet}">
                <html:form action="/CategoryAction" method="post" styleId="checkunload"
                           onsubmit="return deleteCategory();">
                    <html:hidden styleId="method" property="method" value="delete"/>
                    <html:hidden property="id" value="${id}"/>
                    <html:hidden property="session" value="${session}"/>
                    <html:hidden property="categoryId" value="${categoryId}"/>
                    <div class="general">
                        <table class="general" cellpadding="0" cellspacing="0">
                            <tr class="wide">
                                <c:if test="${canManage}">
                                    <th width='1%' nowrap style="white-space:nowrap"><input type="checkbox"
                                                                                            onClick="selectAllCheckboxes(this, 'delete1')">
                                    </th>
                                </c:if>
                                <th width="30%">
                                    <I18n:message key="CATEGORY"/>
                                </th>
                                <th>
                                    <I18n:message key="WORKFLOW"/>
                                </th>
                                <th>
                                    <I18n:message key="ACTIVATE"/>
                                </th>
                            </tr>
                            <c:forEach var="category" items="${categorySet}" varStatus="varCounter">
                                <tr class="line<c:out value="${varCounter.index mod 2}"/>">
                                    <c:if test="${canManage}">

                                        <td>
                                            <c:if test="${category.canManage}">
                <span style="text-align: center">
                    <input type="checkbox" name="delete" alt="delete1" quickCheckboxSelectGroup="delete1"
                           value="<c:out value='${category.id}'/>">
	            </span>
                                            </c:if>
                                        </td>
                                    </c:if>
                                    <td style="white-space: nowrap;">
                                        <c:choose>
                                            <c:when test="${!category.canManage}">
                                                <html:link styleClass="internal"
                                                           href="${contextPath}/CategoryViewAction.do?method=page&amp;categoryId=${category.id}&amp;id=${id}">
                                                    <img title="<I18n:message key="OBJECT_PROPERTIES_VIEW"/>" border="0"
                                                         hspace="0" vspace="0"
                                                         src="<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.closed.gif"/>
                                                    <c:out value="${category.name}" escapeXml="true"/>
                                                </html:link>
                                            </c:when>
                                            <c:otherwise>
                                                <html:link styleClass="internal"
                                                           href="${contextPath}/CategoryEditAction.do?method=page&amp;categoryId=${category.id}&amp;id=${id}">
                                                    <img title="<I18n:message key="OBJECT_PROPERTIES_EDIT"/>" border="0"
                                                         hspace="0" vspace="0"
                                                         src="<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.edit.gif"/>
                                                </html:link>
                                                <html:link styleClass="internal"
                                                           styleId="${category.id eq tci.categoryId ? 'current' : ''}"
                                                           href="${contextPath}/CategoryViewAction.do?method=page&amp;categoryId=${category.id}&amp;id=${id}">
                                                    <c:if test="${!category.isValid}">
                                                        <img border="0" hspace="0" vspace="0"
                                                             title="<I18n:message key="CATEGORY_INVALID"/>"
                                                             src="<c:out value="${contextPath}"/>${ImageServlet}/cssimages/warning.gif"/>
                                                    </c:if>
                                                    <c:out value="${category.name}" escapeXml="true"/>
                                                </html:link>
                                            </c:otherwise>
                                        </c:choose>

                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${canManage || category.workflow.canManage}">
                                                <html:link styleClass="internal"
                                                           href="${contextPath}/WorkflowViewAction.do?method=page&amp;workflowId=${category.workflowId}&amp;id=${id}">
                                                    <html:img
                                                            src="${contextPath}${ImageServlet}/cssimages/ico.workflow.gif"
                                                            styleClass="icon" border="0"/>
                                                    <c:if test="${!category.workflow.isValid}">
                                                        <img border="0" hspace="0" vspace="0"
                                                             title="<I18n:message key="WORKFLOW_INVALID"/>"
                                                             src="<c:out value="${contextPath}"/>${ImageServlet}/cssimages/warning.gif"/>
                                                    </c:if>
                                                    <c:out value="${category.workflow.name}" escapeXml="true"/>
                                                </html:link>
                                            </c:when>
                                            <c:otherwise>
                                    <span class="link">
                                <html:img src="${contextPath}${ImageServlet}/cssimages/ico.workflow.gif"
                                          styleClass="icon"
                                          border="0"/>
                                <img title="<I18n:message key="OBJECT_PROPERTIES_VIEW"/>" border="0"
                                     hspace="0" vspace="0"
                                     src="<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.closed.gif"/>
                                <c:out value="${category.workflow.name}" escapeXml="true"/>
                            </span>
                                            </c:otherwise>
                                        </c:choose>

                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${category.active}">
                                                <I18n:message key="NO"/>
                                            </c:when>
                                            <c:otherwise>
                                                <I18n:message key="YES"/>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:forEach>
                        </table>
                    </div>

                    <div class="controls">
                        <c:if test="${canManage}">
                            <input type="submit" class="iconized secondary" value="<I18n:message key="CLONE"/>"
                                   onclick="setMethodElement('clone');">
                            <input type="submit" class="iconized secondary" value="<I18n:message key="DELETE"/>"
                                   name="DELETE" onclick="setMethodElement('delete');">
                        </c:if>
                    </div>
                </html:form>
            </c:when>
            <c:otherwise>
                <div class="empty"><I18n:message key="EMPTY_CATEGORIES_LIST"/></div>
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
                                               href="${contextPath}/CategoryAction.do?method=page&amp;id=${task.key.id}">
                                        <html:img styleClass="icon" border="0"
                                                  src="${contextPath}${ImageServlet}/icons/categories/${task.key.category.icon}"/>
                                        <c:out value="${task.key.name}"/>
                                    </html:link>
                                </span>
                                <span class="itempath">
                                <html:link styleClass="internal"
                                           href="${contextPath}/CategoryAction.do?method=page&amp;id=${task.key.id}">
                                    <c:forEach var="path" items="${task.key.ancestors}">
                                        <span class="separated"><c:out value="${path.name}"/></span>&nbsp;/

                                    </c:forEach>
                                </html:link><c:if test="${task.key.parentId ne null}">
                                    <html:link styleClass="internal"
                                               href="${contextPath}/CategoryAction.do?method=page&amp;id=${task.key.id}">
                                        <c:out value="${task.key.name}"/>
                                    </html:link>
                                </c:if>
                                </span>
                            </dt>
                            <dd>
                                <c:forEach var="cat" items="${task.value}" varStatus="varC">
                                    <c:choose>
                                        <c:when test="${canManage}">
                                            <html:link style="${cat.active ? 'color:gray' : ''}" styleClass="internal"
                                                       href="${contextPath}/CategoryViewAction.do?method=page&categoryId=${cat.id}&id=${task.key.id}"
                                                       title="${cat.name}"
                                                       styleId="${cat.id eq currentCategoryId ? \"current\" : \"\"}"
                                                    ><html:img styleClass="icon" border="0"
                                                               src="${contextPath}${ImageServlet}/cssimages/ico.categories.gif"
                                                    /><c:if test="${!cat.isValid}"
                                                    ><img border="0" hspace="0" vspace="0"
                                                          title="<I18n:message key="CATEGORY_INVALID"/>"
                                                          src="<c:out value="${contextPath}"/>${ImageServlet}/cssimages/warning.gif"
                                                          class="icon"
                                                    /></c:if><c:out value="${cat.name}"/>
                                            </html:link>
                                        </c:when>
                                        <c:otherwise>
                                                <span class="link"
                                                      id="${cat.id eq currentCategoryId ? "current" : ""}"
                                                        ><html:img styleClass="icon" border="0"
                                                                   src="${contextPath}${ImageServlet}/cssimages/ico.categories.gif"
                                                        /><c:out value="${cat.name}"/>
                                                    </span>
                                        </c:otherwise>
                                    </c:choose><c:if test="${!varC.last}">,</c:if>
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