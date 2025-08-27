<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="http://trackstudio.com" prefix="ts" %>
<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value="${sc.timezone}"/>
<I18n:setBundle basename="language"/>

<c:forEach items="${viewUdfList}" var="udf">
    <c:if test="${udf ne null}">
        <c:set var="val" value="${udf.value}"/>
        <c:if test="${val ne null}">
            <tr id="udf${udf.id}">
                <th>
                    <c:out value="${udf.caption}"/>
                </th>
                <td>
                    <c:choose>
                        <c:when test="${udf.type eq 'date'}">
                            <I18n:formatDate value="${val.time}" type="both" dateStyle="short" timeStyle="short"/>
                        </c:when>
                        <c:when test="${udf.type eq 'float'}">
                            <I18n:formatNumber value="${val}" groupingUsed="true" maxFractionDigits="${decimalFormatUdfFloat}"/>
                        </c:when>
                        <c:when test="${udf.type eq 'integer'}">
                            <I18n:formatNumber value="${val}" groupingUsed="false" maxFractionDigits="0"/>
                        </c:when>
                        <c:when test="${udf.type eq 'list'}">
                            <c:out value="${val.value}" escapeXml="true"/>
                        </c:when>
                        <c:when test="${udf.type eq 'multilist'}">
                            <c:forEach items="${val}" var="item" varStatus="c">
                                <c:out value="${item.value}" escapeXml="true"/><c:if test="${!c.last}">,</c:if>
                            </c:forEach>
                        </c:when>
                        <c:when test="${udf.type eq 'task'}">
                            <c:forEach var="t" items="${val}" varStatus="status">
                                <c:choose>
                                    <c:when test="${t.canManage}">
                                        <c:set var="ancestor"/>
                                        <c:forEach var="task" items="${t.ancestors}" varStatus="varCounter">
                                            <c:set var="ancestor" value="${ancestor}${task.encodeName} [${task.number}]/"/>
                                        </c:forEach>
                                        <c:set var="ancestor" value="${ancestor}${t.encodeName} [${t.number}]"/>
                                        <html:link styleClass="internal" title="${ancestor}"
                                                   href="${contextPath}/TaskViewAction.do?method=page&amp;id=${t.id}">
                                            <html:img styleClass="icon" border="0"
                                                      src="${contextPath}${ImageServlet}/icons/categories/${t.category.icon}"/>
                                            <html:img styleClass="state" border="0"
                                                      style="background-color: ${t.status.color}"
                                                      src="${contextPath}${ImageServlet}${t.status.image}"/>
                                            <c:out value="${t.name}"/>
                                        </html:link>&nbsp;<em class="number">[<c:out value="${t.shortname}"/>&nbsp;#<c:out value="${t.number}"/>]</em>
                                    </c:when>
                                    <c:otherwise>
                                        #<c:out value="${t.number}"/>
                                    </c:otherwise>
                                </c:choose>
                                <br>
                            </c:forEach>
                        </c:when>
                        <c:when test="${udf.type eq 'user'}">
                            <c:forEach var="user" items="${val}" varStatus="status">
                                    <span class="user">
                                        <html:img styleClass="icon" border="0"
                                                  src="${contextPath}${ImageServlet}/cssimages/${user.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
                                        <c:out value="${user.name}"/>
                                    </span>
                                <br>
                            </c:forEach>
                        </c:when>
                        <c:when test="${udf.type eq 'memo'}">
                            <c:choose>
                                <c:when test="${udf.htmlview}">
                                    <ts:htmlfilter session="${sc.id}" macros="true" audit="false" request="<%=request%>">
                                        <c:out value="${val}" escapeXml="false"/>
                                    </ts:htmlfilter>
                                </c:when>
                                <c:otherwise>
                                    <span><pre><c:out value="${val}"/></pre></span>
                                </c:otherwise>
                            </c:choose>
                        </c:when>
                        <c:when test="${udf.type eq 'url'}">
                            <html:link styleClass="internal" href="${val.link}" target="_blank">
                                <c:out value="${val.description ne null ? val.description : val.link}"
                                       escapeXml="true"/>
                            </html:link>
                        </c:when>
                        <c:otherwise>
                            <ts:htmlfilter session="${sc.id}" macros="true" audit="false" request="<%=request%>"><c:out value="${val}" escapeXml="${!udf.htmlview}"/></ts:htmlfilter>
                        </c:otherwise>
                    </c:choose>
                </td>
            </tr>
        </c:if>
    </c:if>
</c:forEach>