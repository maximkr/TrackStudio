<%@ page buffer="128kb" errorPage="/jsp/Error.jsp"%>
<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://trackstudio.com" prefix="ts" %>
<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value="${sc.timezone}"/>
<I18n:setBundle basename="language"/>
<c:set value="${messages}" var="listMes"/>
<c:forEach var="msg" items="${listMes}" varStatus="current">
    <c:choose>
        <c:when test="${msg.time.time.time > sc.prevLogonDate.time.time && msg.time.time.time > sc.lastLogonDate.time.time}">
            <div class="ts-activity-item line<c:out value="${current.index mod 2}"/> grayborder hotmessage" title="<I18n:message key="HOT_MESSAGE"/>">
        </c:when>
        <c:when test="${msg.time.time.time > sc.prevLogonDate.time.time}">
            <div class="ts-activity-item line<c:out value="${current.index mod 2}"/> grayborder newmessage" title="<I18n:message key="NEW_MESSAGE"/>">
        </c:when>
        <c:when test="${msg.time.time.time > sc.lastLogonDate.time.time}">
            <div class="ts-activity-item line<c:out value="${current.index mod 2}"/> grayborder hotmessage" title="<I18n:message key="HOT_MESSAGE"/>">
        </c:when>
        <c:otherwise>
            <div class="ts-activity-item line<c:out value="${current.index mod 2}"/> grayborder">
        </c:otherwise>
    </c:choose>
    <div class="indent ts-activity-item__content">
        <div class="msgbox-opened" id="${msg.id}">
            <c:set var="submitterNameTrimmed" value="${fn:trim(msg.submitter.name)}"/>
            <span class="ts-activity-item__avatar" title="<c:out value="${msg.submitter.name}" escapeXml="true"/>">
                <c:choose>
                    <c:when test="${!empty submitterNameTrimmed}">
                        <c:out value="${fn:toUpperCase(fn:substring(submitterNameTrimmed, 0, 1))}" escapeXml="true"/>
                    </c:when>
                    <c:otherwise>?</c:otherwise>
                </c:choose>
            </span>
            <div class="ts-activity-item__header${viewMessageCheckbox ? " ts-activity-item__header--with-checkbox" : ""}">
                <c:if test="${viewMessageCheckbox}">
                    <input type="checkbox" alt="delete1" class="checkbox link ts-activity-item__checkbox" name="deleteMessage" value="<c:out value="${msg.id}"/>">
                </c:if>
                <div class="messagelabel ts-activity-item__meta" id="label${msg.id}">
                    <c:choose>
                        <c:when test="${!empty msg.attachments}">
                            <html:img src="${contextPath}${ImageServlet}/cssimages/ico.attachment2.gif" hspace="0" vspace="0" border="0" align="middle" altKey="ATTACHMENT" />
                        </c:when>
                    </c:choose>
                    <c:out value="${msg.mstatus.name}" escapeXml="true"/>
                    <c:if test="${msg.resolution ne null}">(<c:out value="${msg.resolution.name}" escapeXml="true"/>)</c:if>
                    <c:set var="hintUser" value="${msg.submitter.login} email:${msg.submitter.email} ${msg.submitter.tel}"/>
                    <span class="user ts-activity-item__user" ${msg.submitterId eq sc.userId ? "id='loggedUser'" : ""} title="<c:out value="${hintUser}" escapeXml="true"/>">
                    <c:out value="${msg.submitter.name}" escapeXml="true"/>
                    </span>
                    <c:choose>
                        <c:when test="${msg.handlerUserId ne null}">
                            <I18n:message key="FOR"/>
                            <c:set var="hintUser" value="${msg.handlerUser.login} email:${msg.handlerUser.email} ${msg.handlerUser.tel}"/>
                                <span class="user ts-activity-item__user" ${msg.handlerUserId eq sc.userId ? "id='loggedUser'" : ""} title="<c:out value="${hintUser}" escapeXml="true"/>">
                                     <c:out value="${msg.handlerUser.name}" escapeXml="true"/>
                                </span>
                        </c:when>
                        <c:when test="${msg.handlerGroupId ne null}">
                            <I18n:message key="FOR"/>
                            <span class="user"><html:img  styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/ico.status.gif"/><c:out value="${msg.handlerGroup.name}" escapeXml="true"/></span>
                        </c:when>
                    </c:choose>
                </div>
                <span class="msgtime ts-activity-item__time">
                    <a class="internal" href="${contextPath}/task/${tci.number}?thisframe=true#${msg.id}" id="${msg.id}">
                        <span class="ts-activity-item__time-text">
                            <I18n:formatDate value="${msg.time.time}" type="both" dateStyle="short" timeStyle="short"/>
                        </span>
                    </a>
                </span>
            </div>
            <c:if test="${msg.priorityId ne null || msg.deadline ne null || !empty msg.budgetAsString || !empty msg.actualBudgetAsString || !empty msg.attachments}">
                <table class="general" cellpadding="0" cellspacing="0">

                    <c:if test="${msg.priorityId ne null}">
                        <tr>
                            <th width="20%"><I18n:message key="MESSAGE_PRIORITY"/></th>
                            <td width="80%"><c:out value="${msg.priority.name}" escapeXml="true" /></td>
                        </tr>
                    </c:if>
                    <c:if test="${msg.deadline ne null}">
                        <tr>
                            <th width="20%"><I18n:message key="MESSAGE_DEADLINE"/></th>
                            <td width="80%"><I18n:formatDate value="${msg.deadline.time}" type="both" dateStyle="short" timeStyle="short"/></td>
                        </tr>
                    </c:if>

                    <c:if test="${!empty msg.budgetAsString}">
                        <tr>
                            <th width="20%"><I18n:message key="BUDGET"/></th>
                            <td width="80%"><c:out value="${msg.budgetAsString}" escapeXml="true" /></td>
                        </tr>
                    </c:if>

                    <c:if test="${!empty msg.actualBudgetAsString}">
                        <tr>
                            <th width="20%"><I18n:message key="MESSAGE_ABUDGET"/></th>
                            <td width="80%"><c:out value="${msg.actualBudgetAsString}" escapeXml="true" /></td>
                        </tr>
                    </c:if>

                    <c:if test="${!empty msg.attachments}">
                        <tr class="attach-msg">
                            <th width="20%"><I18n:message key="ATTACHMENT"/></th>
                            <td width="80%">
                                <c:if test="${attachmentsMsg[msg.id] != null}">
                                    <c:forEach items="${attachmentsMsg[msg.id]}" var="ata">
                                        <c:if test="${!ata.deleted}">
                                            <c:choose>
                                                <c:when test="${ata.thumbnailed}">
                                                    <a class="internal" href="<c:url value="/download/task/${msg.task.number}/${ata.id}"/>?type=image" data-lightbox="example-1">
                                                        <img alt="" src="${contextPath}/TSImageServlet?attId=${ata.id}&width=100&height=75" hspace="0" vspace="0">
                                                    </a>
                                                    <a class="internal" target="_blank" href="${contextPath}/download/task/${msg.task.number}/${ata.id}" title="<c:out value="${ata.description}"/>">
                                                        <html:img src="${contextPath}${ImageServlet}/cssimages/ico.attachment2.gif" hspace="4" vspace="0" border="0" align="middle" altKey="ATTACHMENT"/><c:out value="${ata.name}"/>
                                                    </a>
                                                </c:when>
                                                <c:otherwise>
                                                    <a class="internal" target="_blank" href="${contextPath}/download/task/${msg.task.number}/${ata.id}" title="<c:out value="${ata.description}"/>">
                                                        <html:img src="${contextPath}${ImageServlet}/cssimages/ico.attachment2.gif" hspace="4" vspace="0" border="0" align="middle" altKey="ATTACHMENT"/><c:out value="${ata.name}"/>
                                                    </a>
                                                </c:otherwise>
                                            </c:choose>
                                            <br>
                                        </c:if>
                                        <c:if test="${ata.deleted}">
                                            <html:img src="${contextPath}${ImageServlet}/cssimages/ico.attachment2.gif" hspace="4" vspace="0" border="0" align="middle" altKey="ATTACHMENT"/><img border="0" hspace="0" vspace="0" title="<I18n:message key="ATTACHMENT_DELETED"/>" src="${contextPath}${ImageServlet}/cssimages/warning.gif"/><c:out value="${ata.name}"/>
                                        </c:if>
                                    </c:forEach>
                                </c:if>
                            </td>
                        </tr>
                    </c:if>
                </table>
            </c:if>
            <div class="description ts-activity-item__body">
                <ts:htmlfilter session="${sc.id}" macros="true" audit="${msg.mstatus.name == '*'}" request="<%=request%>"><c:out value="${msg.description}" escapeXml="false"/></ts:htmlfilter>
            </div>
        </div>
    </div>
    </div>

</c:forEach>
<script type="text/javascript">
    var url = location.href;
    if (url.lastIndexOf("#") != -1) {
        var id = url.substring(url.lastIndexOf("#") + "#".length, url.length);
        var message = document.getElementById(id);
        if (message) {
            message.classList.add("ts-message-highlighted");
        }
    }
</script>
