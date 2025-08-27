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
    <tiles:put name="title" value="${title}"/>
    <tiles:put name="header" value="/jsp/task/TaskHeader.jsp"/>
    <tiles:put name="customHeader" value="/jsp/task/mailimport/MailImportHeader.jsp"/>
    <tiles:put name="tabs" type="string"/>
    <tiles:put name="main" type="string">

        <div class="blueborder">
        <div class="caption"><I18n:message key="EMAIL_IMPORT_LIST"/></div>

        <div class="controlPanel">
            <c:if test="${mailImport.canManage}">
                <html:link
                        href="${contextPath}/MailImportEditAction.do?method=page&amp;id=${id}&amp;mailImportId=${mailImport.id}">
                    <html:img src="${contextPath}${ImageServlet}/cssimages/ico.edit.gif" border="0" altKey="EDIT"/>
                    <I18n:message key="MAIL_IMPORT_EDIT"/>
                </html:link>
            </c:if>
        </div>

        <c:if test="${canCreateObject}">
            <div class="controlPanel">
                <html:link href="${contextPath}/MailImportAction.do?method=create&amp;id=${id}"><html:img
                        src="${contextPath}${ImageServlet}/cssimages/ico.edit.gif" border="0" altKey="EDIT"/>
                    <I18n:message key="MAIL_IMPORT_RULE_ADD"/>
                </html:link>
            </div>
        </c:if>

        <div class="indent">
        <div class="general">
            <table class="general" cellpadding="0" cellspacing="0">
                <colgroup>
                    <col class="col_1">
                    <col class="col_2">
                </colgroup>
                <caption><I18n:message key="GENERAL_SETTINGS"/></caption>
                <tr>
                    <th><I18n:message key="NAME"/></th>
                    <td colspan="2"><c:out value="${mailImport.name}"/></td>
                </tr>
                <tr>
                    <th><I18n:message key="ACTIVE"/></th>
                    <td colspan="2">
                        <c:choose>
                            <c:when test="${mailImport.active}">
                                <html:img src="${contextPath}${ImageServlet}/cssimages/ico.checked.gif"/>
                            </c:when>
                            <c:otherwise>
                                <html:img src="${contextPath}${ImageServlet}/cssimages/ico.unchecked.gif"/>
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
                <tr>
                    <th><I18n:message key="CONTAINS_KEYWORD"/></th>
                    <td colspan="2"><c:out value="${mailImport.keywords}"/></td>
                </tr>
                <tr>
                    <th><I18n:message key="SEARCH_IN"/></th>
                    <td colspan="2">
                        <c:choose>
                            <c:when test="${mailImport ne null && mailImport.searchIn eq 0}">
                                <I18n:message key="BODY"/>
                            </c:when>
                            <c:when test="${mailImport ne null && mailImport.searchIn eq 1}">
                                <I18n:message key="EMAIL_SUBJECT"/>
                            </c:when>
                            <c:when test="${mailImport ne null && mailImport.searchIn eq 3}">
                                <I18n:message key="EMAIL_SUBJECT_BODY"/>
                            </c:when>
                            <c:when test="${mailImport ne null && mailImport.searchIn eq 4}">
                                <I18n:message key="TO_EMAIL"/>
                            </c:when>
                            <c:otherwise>
                                <I18n:message key="EMAIL_HEADER"/>
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
                <tr>
                    <th><I18n:message key="ORDER"/></th>
                    <td colspan="2"><c:out value="${mailImport.order}"/></td>
                </tr>
                <tr>
                    <th><I18n:message key="ALLOWED_DOMAINS"/></th>
                    <td colspan="2"><c:out value="${mailImport.domain}"/></td>
                </tr>
                <c:if test="${mailImport.owner != null}">
                    <tr>
                        <th><I18n:message key="OWNER"/></th>
                        <td colspan="2">
                            <span class="user" ${mailImport.owner.id eq sc.userId ? "id='loggedUser'" : ""}>
                                <html:img styleClass="icon" border="0"
                                          src="${contextPath}${ImageServlet}/cssimages/${mailImport.owner.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
                                <ts:htmluser user="${mailImport.owner}">&nbsp;</ts:htmluser>
			                </span>
                        </td>
                    </tr>
                </c:if>
            </table>
        </div>

        <div class="general">
            <table class="general" cellpadding="0" cellspacing="0">
                <colgroup>
                    <col class="col_1">
                    <col class="col_2">
                </colgroup>
                <caption><I18n:message key="TASK_IMPORT_PROPERTIES"/></caption>
                <tr>
                    <th><I18n:message key="PARENT_TASK"/></th>
                    <td colspan="2"><span style="white-space: nowrap;">
                <html:img styleClass="icon" border="0"
                          src="${contextPath}${ImageServlet}/icons/categories/${mailImport.task.category.icon}"/>&nbsp;<c:out
                            value="${mailImport.task.name}" escapeXml="true"/>
                </span>
                    </td>
                </tr>
                <tr>
                    <th><I18n:message key="CATEGORY"/></th>
                    <td colspan="2"><html:img styleClass="icon" border="0"
                                              src="${contextPath}${ImageServlet}/icons/categories/${mailImport.category.icon}"/>&nbsp;<c:out
                            value="${mailImport.category.name}" escapeXml="true"/></td>
                </tr>
                <tr>
                    <th><I18n:message key="IMPORT_EMAIL_FROM_UNKNOWN_USERS"/></th>
                    <td colspan="2">
                        <c:choose>
                            <c:when test="${mailImport.importUnknown}">
                                <html:img src="${contextPath}${ImageServlet}/cssimages/ico.checked.gif"/>
                            </c:when>
                            <c:otherwise>
                                <html:img src="${contextPath}${ImageServlet}/cssimages/ico.unchecked.gif"/>
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
            </table>
        </div>
        <div class="general">
            <table class="general" cellpadding="0" cellspacing="0">
                <colgroup>
                    <col class="col_1">
                    <col class="col_2">
                </colgroup>
                <caption><I18n:message key="MESSAGE_HISTORY_IMPORT_PROPERTIES"/></caption>
                <tr>
                    <th><I18n:message key="IMPORT_COMMENTS_AS"/></th>
                    <td colspan="2">
                        <c:choose>
                            <c:when test="${mailImport.mstatus ne null}">
                                <c:out value="${mailImport.mstatus.name}"/>
                            </c:when>
                            <c:otherwise>
                                <I18n:message key="ALWAYS_CREATE_NEW_TASK"/>
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
            </table>
        </div>

    </tiles:put>
</tiles:insert>
