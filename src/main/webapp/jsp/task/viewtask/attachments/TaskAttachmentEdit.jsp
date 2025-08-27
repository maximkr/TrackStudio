<%@ page buffer="128kb" errorPage="/jsp/Error.jsp" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/tiles-el" prefix="tiles" %>


<I18n:setLocale value='${sc.locale}'/>
<I18n:setTimeZone value='${sc.timezone}'/>
<I18n:setBundle basename='language'/>
<tiles:insert page="/jsp/layout/ListLayout.jsp" flush="true">
    <tiles:put name="header" value="/jsp/task/TaskHeader.jsp"/>
    <tiles:put name="customHeader" value="/jsp/task/viewtask/attachments/TaskAttachmentHeader.jsp"/>

    <tiles:put name="tabs" type="string"/>
    <tiles:put name="main" type="string">
        <c:if test="${attachment.canManage || canManageTaskAttachments}">
            <div class="blueborder">
                <div class="caption"><I18n:message key="ATTACHMENT_EDIT"/></div>
                <div class="indent">
                    <html:form styleId="checkunload" method="POST" action="/AttachmentEditAction" onsubmit="return validate(this);">
                        <html:hidden property="method" value="save"/>
                        <html:hidden property="id" value="${id}"/>
                        <html:hidden property="taskId"/>
                        <html:hidden property="userId"/>
                        <html:hidden property="attachmentId"/>
                        <html:hidden property="session" value="${session}"/>

                        <table class="general" cellpadding="0" cellspacing="0">
                            <COLGROUP>
                                <COL class="col_1">
                                <COL class="col_2">
                            </COLGROUP>

                            <tr>
                                <th><label for="name"><I18n:message key="NAME"/></label></th>
                                <td>
                                    <html:text styleId="name" property="name" size="50" maxlength="200" alt=">0"/>
                                </td>
                            </tr>
                            <tr>
                                <th><label for="description"><I18n:message key="DESCRIPTION"/></label></th>
                                <td>
                                    <html:text styleId="description" property="description" size="50" maxlength="200"/>
                                </td>
                            </tr>
                            <tr>
                                <th><I18n:message key="OWNER"/></th>
                                <td>
                        <span class="user" ${attachment.userId eq sc.userId ? "id='loggedUser'" : ""}>
            <html:img  styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/${attachment.user.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
    <c:out value="${attachment.user.name}" escapeXml="true"/>
	</span>
                                </td>
                            </tr>
                        </table>

                        <div class="controls">
                            <input type="submit" class="iconized"
                                   value="<I18n:message key="SAVE"/>"
                                   name="SAVE">
                            <html:button styleClass="iconized secondary" property="cancelButton"
                                         onclick="document.location='${contextPath}/TaskViewAction.do?method=page&amp;id=${id}';">
                                <I18n:message key="CANCEL"/>
                            </html:button>
                        </div>

                    </html:form>
                </div>
            </div>
        </c:if>
    </tiles:put>
</tiles:insert>
