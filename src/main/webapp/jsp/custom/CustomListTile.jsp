<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value="${sc.timezone}"/>
<I18n:setBundle basename="language"/>
<c:if test="${_can_create}">
    <div class="controlPanel">
        <c:if test="${!empty types}">
            <script type="text/javascript">
                var filterMenu = new TSMenu();
                filterMenu.width = 320;
                var filterMenuTitle='<I18n:message key="OTHER_CUSTOM_FIELDS"/>';
                <c:forEach items="${types}" var="udfType">
                <c:if test="${udfType.key==1 || udfType.key==5 || udfType.key==6 || udfType.key==7 || udfType.key==8 || udfType.key==9}">
                filterMenu.add(new TSMenuItem("<c:out value="${udfType.value}"/>", "${contextPath}${editUdfAction}?method=page&amp;id=${id}&amp;workflowId=${workflowId}&amp;type=${udfType.key}", false, false, "<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.customfields.gif"));
                </c:if>
                </c:forEach>
            </script>
        </c:if>
        <html:link
                href="${contextPath}${editUdfAction}?method=page&amp;id=${id}&amp;type=0&amp;workflowId=${workflowId}">
            <html:img src="${contextPath}${ImageServlet}/cssimages/ico.udf_string.gif" border="0"/>
            <I18n:message key="UDF_STRING"/>
        </html:link>
        <html:link
                href="${contextPath}${editUdfAction}?method=page&amp;id=${id}&amp;type=4&amp;workflowId=${workflowId}">
            <html:img src="${contextPath}${ImageServlet}/cssimages/ico.udf_integer.gif" border="0"/>
            <I18n:message key="UDF_INTEGER"/>
        </html:link>
        <html:link
                href="${contextPath}${editUdfAction}?method=page&amp;id=${id}&amp;type=2&amp;workflowId=${workflowId}">
            <html:img src="${contextPath}${ImageServlet}/cssimages/ico.udf_date.gif" border="0"/>
            <I18n:message key="UDF_DATE"/>
        </html:link>
        <html:link
                href="${contextPath}${editUdfAction}?method=page&amp;id=${id}&amp;type=3&amp;workflowId=${workflowId}">
            <html:img src="${contextPath}${ImageServlet}/cssimages/ico.udf_list.gif" border="0"/>
            <I18n:message key="UDF_LIST"/>
        </html:link>
        <c:if test="${!empty types}">
        <span class="additional">
    <script type="text/javascript">
        var filterBar =new TSMenuBar();
        filterBar.add(new TSMenuBut(filterMenuTitle, null, filterMenu, "<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.customfields.gif"));
        document.write(filterBar);
    </script>
    </span>
        </c:if>
    </div>

</c:if>

<script type="text/javascript">
    function del() {
        if (document.getElementById('method').value == "delete") {
            return deleteConfirm("<I18n:message key="DELETE_CUSTOM_REQ"/>", "customForm");
        }
        return true;
    }

    function setMethod(target) {
        document.getElementById('method').value = target;   
    }
</script>

<div class="indent">
    <c:choose>
        <c:when test="${!empty udfList}">
            <html:form method="post" styleId="checkunload" action="${action}" onsubmit="return del();">
                <html:hidden property="method" value="delete" styleId="method"/>
                <html:hidden property="session" value="${session}"/>
                <html:hidden property="idUdf" value="${idUdf}"/>
                <html:hidden property="workflowId" value="${workflowId}"/>
                <html:hidden property="id"/>
                <table class="general" cellpadding="0" cellspacing="0">
                    <tr class="wide">
                        <c:if test="${_can_delete eq true}">
                            <th nowrap style="white-space:nowrap" width="24px">
                                <input type="checkbox" onClick="selectAllCheckboxes(this, 'delete1')">
                            </th>
                        </c:if>
                        <th width="50%">
                            <I18n:message key="CAPTION"/>
                        </th>
                        <th width="30%">
                            <I18n:message key="TYPE"/>
                        </th>
                        <th width="20%">
                            <I18n:message key="ORDER"/>
                        </th>
                    </tr>
                    <c:forEach var="udf" items="${udfList}">
                        <tr>
                            <c:if test="${_can_delete eq true}">
                                <td class="top" style="text-align: left">
                                    <c:if test="${udf.canUpdate}">
                                    <span style="text-align: center">
                                        <input type="checkbox" name="delete" alt="delete1" value="${udf.id}">
                                    </span>
                                    </c:if>
                                </td>
                            </c:if>
                            <td>
                                <c:choose>
                                    <c:when test="${!udf.canUpdate}">
                                        <html:link styleClass="internal"
                                                   href="${contextPath}${viewUdfAction}?method=page&amp;udfId=${udf.id}&amp;id=${id}&amp;workflowId=${workflowId}">
                                            <img title="<I18n:message key="OBJECT_PROPERTIES_VIEW"/>" border="0"
                                                 hspace="0" vspace="0" src="${contextPath}${ImageServlet}/cssimages/ico.closed.gif"/>
                                        </html:link>
                                    </c:when>
                                    <c:otherwise>
                                        <html:link styleClass="internal"
                                                   href="${contextPath}${editUdfAction}?method=page&amp;udfId=${udf.id}&amp;id=${id}&amp;workflowId=${workflowId}">
                                            <img title="<I18n:message key="OBJECT_PROPERTIES_EDIT"/>" border="0"
                                                 hspace="0" vspace="0" src="${contextPath}${ImageServlet}/cssimages/ico.edit.gif"/>
                                        </html:link>
                                    </c:otherwise>
                                </c:choose>
                                <html:link styleClass="internal" href="${contextPath}${viewUdfAction}?method=page&amp;udfId=${udf.id}&amp;id=${id}&amp;workflowId=${workflowId}">
                                    <c:out value="${udf.name}"/>
                                </html:link>
                            </td>
                            <td>
                                <c:out value="${udf.type}"/>
                            </td>
                            <td>
                                <c:out value="${udf.order}"/>
                            </td>
                        </tr>
                    </c:forEach>
                </table>

                <div class="controls">
                     <c:if test="${_can_create}">
                        <input type="submit" name="cloneButton" value="<I18n:message key="CLONE"/>" class="iconized secondary" onclick="setMethod('clone');">
                    </c:if>
                    <c:if test="${_can_delete}">
                        <input type="submit" name="deleteButton" value="<I18n:message key="DELETE"/>" class="iconized secondary" onclick="setMethod('delete');">
                    </c:if>
                </div>
            </html:form>
        </c:when>
        <c:otherwise>
            <div class="empty"><I18n:message key="EMPTY_UDF_LIST_WORKFLOW"/></div>
        </c:otherwise>
    </c:choose>
</div>
</div>
<c:if test="${!empty seeAlso || !empty seeAlsoUsers}">
    <br/>

    <div class="blueborder">
        <div class="caption">
            <I18n:message key="SEE_ALSO"/>
        </div>
        <div class="indent">
            <c:choose>
                <c:when test="${!empty seeAlsoUsers}">
                    <c:forEach items="${seeAlsoUsers}" var="also" varStatus="affected">
                        <c:if test="${!empty also}">
                            <dl ${affected.first ? "class='affected'" : ""}>
                                <c:forEach var="task" items="${also}" varStatus="varCounter">
                                    <dt>
                                <span class="itemname">
                                    <html:link styleClass="internal" href="${contextPath}${listUdfAction}?method=page&amp;id=${task.key.id}">
                                        <span class="user" ${task.key.id eq sc.userId ? "id='loggedUser'" : ""}>
                                            <html:img styleClass="icon" border="0"
                                                      src="${contextPath}${ImageServlet}/cssimages/${task.key.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
                                                <c:out value="${task.key.name}" escapeXml="true"/>
			                            </span>
                                    </html:link>
                                    </span>
                                <span class="itempath">
                                    <html:link styleClass="internal"
                                               href="${contextPath}${listUdfAction}?method=page&amp;id=${task.key.id}">
                                        <c:forEach var="path" items="${task.key.ancestors}">
                                            <span class="separated"><c:out value="${path.name}"/></span>&nbsp;/
                                        </c:forEach>
                                    </html:link>
                                    <c:if test="${task.key.parentId ne null}">
                                        <html:link styleClass="internal"
                                                   href="${contextPath}${listUdfAction}?method=page&amp;id=${task.key.id}">
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
                                                       href="${contextPath}${viewUdfAction}?method=page&udfId=${cat.id}&id=${task.key.id}"
                                                       title="${cat.name}">
                                                <html:img styleClass="icon" border="0"
                                                          src="${contextPath}${ImageServlet}/cssimages/ico.customfields.gif"/>
                                                <c:out value="${cat.name}"/>
                                                </span>
                                            </html:link>
                                        </c:forEach>
                                    </dd>
                                </c:forEach>
                            </dl>
                        </c:if>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <c:forEach items="${seeAlso}" var="also" varStatus="affected">
                        <c:if test="${!empty also}">
                            <dl ${affected.first ? "class='affected'" : ""}>
                                <c:forEach var="task" items="${also}" varStatus="varCounter">
                                    <dt>
                                <span class="itemname">
                                    <html:link styleClass="internal"  href="${contextPath}${listUdfAction}?method=page&amp;id=${task.key.id}">
                                        <html:img styleClass="icon" border="0"
                                                  src="${contextPath}${ImageServlet}/icons/categories/${task.key.category.icon}"/>
                                        <c:out value="${task.key.name}"/>
                                    </html:link>
                                </span>
                                <span class="itempath">
                                    <html:link styleClass="internal" href="${contextPath}${listUdfAction}?method=page&amp;id=${task.key.id}">
                                        <c:forEach var="path" items="${task.key.ancestors}">
                                            <span class="separated"><c:out value="${path.name}"/></span>&nbsp;/
                                        </c:forEach>
                                    </html:link>
                                    <c:if test="${task.key.parentId ne null}">
                                        <html:link styleClass="internal"
                                                   href="${contextPath}${listUdfAction}?method=page&amp;id=${task.key.id}">
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
                                                   href="${contextPath}${viewUdfAction}?method=page&udfId=${cat.id}&id=${task.key.id}"
                                                   title="${cat.name}">
                                            <html:img styleClass="icon" border="0"
                                                      src="${contextPath}${ImageServlet}/cssimages/ico.customfields.gif"/>
                                            <c:out value="${cat.name}"/>
                                        </html:link>
                                    </span>
                                        </c:forEach>
                                    </dd>
                                </c:forEach>
                            </dl>
                        </c:if>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

</c:if>
