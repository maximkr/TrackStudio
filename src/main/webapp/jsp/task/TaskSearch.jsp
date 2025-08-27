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
        <div class="searchtasks">
            <div class="blueborder">
                <div class="caption"><I18n:message key="FOUND_TASKS"><I18n:param><c:out
                        value="${size}"/></I18n:param><I18n:param><c:out
                        value="${key}"/></I18n:param></I18n:message></div>
                <div class="indent">
                    <c:if test="${attachments ne null && !empty attachments}">
                        <div class="searchother">

                            <div class="blueborder">
                                <div class="caption"><I18n:message key="FOUND_ATTACHMENTS"><I18n:param><c:out
                                        value="${attachments.size}"/></I18n:param><I18n:param><c:out
                                        value="${key}"/></I18n:param></I18n:message></div>
                                <div class="indent">
                                    <ol start="${(attachments.page-1)*20+1}">
                                        <c:forEach var="item" items="${attachments.col}" varStatus="c">
                                            <li class="searchitem">
                                                <c:choose>
                                                    <c:when test="${item.attachment.taskId eq null}">
                                                        <dt>
                              <span class="itempath">
                                <html:link styleClass="internal"
                                           href="${contextPath}/UserViewAction.do?method=page&amp;id=${item.attachment.user.id}">
                                    <c:forEach var="path" items="${item.attachment.user.ancestors}">
                                        /&nbsp;<span class="separated"><c:out value="${path.name}"/></span>
                                    </c:forEach>
                                </html:link>
                                </span><br>
                                <span class="itemname">
                                    <html:link styleClass="internal"
                                               href="${contextPath}/UserViewAction.do?method=page&amp;id=${item.attachment.user.id}">
                                        <html:img styleClass="icon" border="0"
                                                  src="${contextPath}${ImageServlet}/cssimages/ico.attachment2.gif"/>
                                        <c:out value="${item.highlightName}" escapeXml="false"/>&nbsp;&nbsp;(<c:out
                                            value="${item.attachment.user.name}" escapeXml="false"/>[<c:out
                                            value="${item.attachment.user.login}" escapeXml="false"/>])
                                    </html:link>
                                </span>

                                                        </dt>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <dt>
                                <span class="itempath">
                                <html:link styleClass="internal"
                                           href="${contextPath}/task/${item.attachment.task.number}?thisframe=true">
                                    <c:forEach var="path" items="${item.attachment.task.ancestors}">
                                        /&nbsp;<span class="separated"><c:out value="${path.name}"/></span>
                                    </c:forEach>
                                </html:link>
                                </span><br>
                                <span class="itemname">
                                    <html:link styleClass="internal"
                                               href="${contextPath}/task/${item.attachment.task.number}?thisframe=true">
                                        <html:img styleClass="icon" border="0"
                                                  src="${contextPath}${ImageServlet}/cssimages/ico.attachment2.gif"/>
                                        <c:out value="${item.name}" escapeXml="false"/>&nbsp;&nbsp;(<c:out
                                            value="${item.attachment.task.name}" escapeXml="false"/>[#<c:out
                                            value="${item.attachment.task.number}" escapeXml="false"/>])
                                    </html:link>
                                </span>

                                                        </dt>
                                                    </c:otherwise>
                                                </c:choose>

                                                <dd>
                                                    <c:out value="${item.surroundText}" escapeXml="false"/>
                                                </dd>
                                            </li>
                                        </c:forEach>
                                    </ol>
                                    <c:out value="${attachmentSlider}" escapeXml="false"/>
                                </div>
                            </div>
                        </div>
                    </c:if>
                    <c:if test="${founduser ne null}">
                        <div class="xfound"><I18n:message key="FOUND_USER"><I18n:param><html:link styleClass="internal"
                                                                                                  href="${contextPath}/UserViewAction.do?method=page&amp;id=${founduser.id}">
                            <html:img styleClass="icon" border="0"
                                      src="${contextPath}${ImageServlet}/cssimages/${founduser.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
                            <c:out value="${founduser.name}"/>
                            &nbsp;<em class="number">[@<c:out value="${founduser.login}"/>]
                            </em>
                        </html:link></I18n:param></I18n:message></div>
                    </c:if>
                    <c:if test="${foundtask ne null}">
                        <div class="xfound">
                            <I18n:message key="FOUND_TASK">
                                <I18n:param>
                                    <html:link styleClass="internal"
                                               href="${contextPath}/task/${foundtask.number}?thisframe=true">
                                        <html:img styleClass="icon" border="0"
                                                  src="${contextPath}${ImageServlet}/icons/categories/${foundtask.category.icon}"/>
                                        <html:img styleClass="state" border="0"
                                                  style="background-color: ${foundtask.status.color}"
                                                  src="${contextPath}${ImageServlet}${foundtask.status.image}"/>
                                        <c:out value="${foundtask.name}" escapeXml="true"/>[#<c:out
                                            value="${foundtask.number}" escapeXml="true"/>]
                                    </html:link>
                                </I18n:param>
                            </I18n:message>
                        </div>
                    </c:if>
                    <c:if test="${tasksByNumber ne null && !empty tasksByNumber  }">
                        <div class="foundtasks"><I18n:message key="FOUND_BY_NUMBER"/><br>
                            <ol start="1">
                                <c:forEach var="item" items="${tasksByNumber}" varStatus="c">
                                    <li class="searchitem main">
                <span class="itempath">
                <html:link styleClass="internal" href="${contextPath}/task/${item.number}?thisframe=true">
                    <c:forEach var="task" items="${item.ancestors}" varStatus="varCounter">
                <span class="separated">
                <c:out value="${task.name}" escapeXml="true"/>
                </span>&nbsp;/
                    </c:forEach>
                </html:link>
            </span><br>
        <span class="itemname">
        <html:link styleClass="internal" href="${contextPath}/task/${item.number}?thisframe=true">
            <html:img styleClass="icon" border="0"
                      src="${contextPath}${ImageServlet}/icons/categories/${item.category.icon}"/>
            <html:img styleClass="state" border="0" style="background-color: ${item.status.color}"
                      src="${contextPath}${ImageServlet}${item.status.image}"/>
            <c:out value="${item.name}" escapeXml="true"/>&nbsp;<em class="number">[#<c:out value="${item.number}"/>]</em>
        </html:link>
            </span>
                                        <br>
                                    </li>
                                </c:forEach>
                            </ol>
                        </div>
                        <br>
                        <c:if test="${tasks.size>0}">
                            <hr class="red">
                        </c:if>
                    </c:if>

                    <ol start="${(tasks.page-1)*20+1+size-tasks.size}">
                        <c:set var="mainResult" value="0"/>
                        <c:forEach var="item" items="${tasks.col}" varStatus="c">
                        <c:choose>
                        <c:when test="${item.pos==1}">
                        <li class="searchitem main">
                                <c:set var="mainResult" value="1"/>
                            </c:when>
                            <c:when test="${item.pos==0 && mainResult==1}">
                    </ol>
                    <hr class="red">
                    <ol start="${(tasks.page-1)*20+1+size-tasks.size+c.index}">

                        <li class="searchitem">
                                <c:set var="mainResult" value="0"/>
                            </c:when>
                            <c:otherwise>
                        <li class="searchitem">
                            </c:otherwise>
                            </c:choose>


                            <dt>
                                <span class="itempath">
                                <html:link styleClass="internal"
                                           href="${contextPath}/task/${item.task.number}?thisframe=true">
                                    <c:forEach var="path" items="${item.task.ancestors}">
                                        <span class="separated"><c:out value="${path.name}"/></span>&nbsp;/
                                    </c:forEach>
                                </html:link>
                                </span><br>
                                <span class="itemname">
                                    <html:link styleClass="internal"
                                               href="${contextPath}/task/${item.task.number}?thisframe=true">
                                        <html:img styleClass="icon" border="0"
                                                  src="${contextPath}${ImageServlet}/icons/categories/${item.task.category.icon}"/>
                                        <html:img styleClass="state" border="0"
                                                  style="background-color: ${item.task.status.color}"
                                                  src="${contextPath}${ImageServlet}${item.task.status.image}"/>
                                        <c:out value="${item.name}" escapeXml="false"/>&nbsp;[<c:out
                                            value="${item.task.shortname}" escapeXml="false"/>&nbsp;#<c:out
                                            value="${item.task.number}" escapeXml="false"/>]
                                    </html:link>
                                </span>

                            </dt>
                            <dd>
                                <c:out value="${item.surroundText}" escapeXml="false"/>
                            </dd>
                        </li>
                        </c:forEach>
                    </ol>
                    <c:out value="${taskSlider}" escapeXml="false"/>
                </div>
            </div>
        </div>
    </tiles:put>
</tiles:insert>
