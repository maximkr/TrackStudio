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
    
        <tiles:put name="tabs" type="string"/>
    <tiles:put name="customHeader" value="/jsp/task/template/TemplateHeader.jsp"/>
    <tiles:put name="main" type="string">
        <c:if test="${canEditTemplate}">
            <div class="blueborder">
                <div class="caption">
                    <I18n:message key="TEMPLATE_EDIT_PAGE"/>
                </div>
                <div class="indent">
                    <html:form styleId="checkunload" method="POST" action="/TemplateEditAction"
                               onsubmit="return validate(this);">
                        <html:hidden property="method" value="save"/>
                        <html:hidden property="id" value="${id}"/>
                        <html:hidden property="templateId"/>
                        <html:hidden property="session" value="${session}"/>

                        <div class="general">
                            <table class="general" cellpadding="0" cellspacing="0">
                                <colgroup>
                                    <col class="col_1">
                                    <col class="col_2">
                                </colgroup>
                                <tr>
                                    <th><label for="name">
                                        <I18n:message key="NAME"/>
                                        *</label></th>
                                    <td>
                                        <html:text styleId="name" property="name" size="50" maxlength="200" alt=">0"/><span class="sample"><I18n:message key="TEMPLATE_NAME_SAMPLE"/></span>
                                    </td>
                                </tr>
                                <tr>
                                    <th><label for="description">
                                        <I18n:message key="DESCRIPTION"/>
                                    </label></th>
                                    <td>
                                        <html:text styleId="description" property="description" size="50"
                                                   maxlength="200"/>
                                    </td>
                                </tr>
                                <tr>
                                    <th><label for="user">
                                        <I18n:message key="USER"/>
                                        </label>
                                    </th>
                                    <td>
                                        <html:select property="user">
                                            <option value="">
                                                <I18n:message key="NONE_SPECIFIED"/>
                                            </option>
                                            <c:forEach var="user" items="${users}">
                                                <html:option value="${user.id}">
                                                    <c:out value="${user.name}" escapeXml="true"/>
                                                </html:option>
                                            </c:forEach>
                                        </html:select><span class="sample"><I18n:message key="TEMPLATE_USER_SAMPLE"/></span>
                                    </td>
                                </tr>
                                <tr>
                                    <th>
                                        <I18n:message key="OWNER"/>
                                        
                                    </th>
                                    <td>
                                        <c:if test="${template ne null}">
                                            <span class="user" ${template.owner.id eq sc.userId ? "id='loggedUser'" : ""}>
                                                <html:img styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/${template.owner.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
                                                <c:out value="${template.owner.name}" escapeXml="true"/>
			                                </span>
                                        </c:if>
                                        <c:if test="${template eq null}">
                                            <span class="user" ${currentUser.id eq sc.userId ? "id='loggedUser'" : ""}>
                                                <html:img styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/${currentUser.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
                                                <c:out value="${currentUser.name}" escapeXml="true"/>
			                                </span>
                                        </c:if>
                                    </td>
                                </tr>

                                <tr>
                                    <th><label for="active">
                                        <I18n:message key="ACTIVE"/>
                                        </label>
                                    </th>
                                    <td>
                                        <html:checkbox property="active" styleId="active"/>
                                    </td>
                                </tr>
                                <tr>
                                    <th><label for="folder">
                                        <I18n:message key="TEMPLATE_FOLDER"/>
                                        </label>
                                    </th>
                                    <td>
                                        <html:select property="folder" styleId="folder">
                                            <c:forEach items="${templates}" var="t">
                                                <html:option value="${t.name}">
                                                    <c:out value="${t.name}"/>
                                                </html:option>
                                            </c:forEach>
                                        </html:select>

                                    </td>
                                </tr>

                            </table>
                        </div>
                        <div class="controls">
                            <input type="submit" class="iconized"
                                   value="<I18n:message key="SAVE"/>"
                                   name="SAVE">

                                <html:button styleClass="iconized secondary" property="cancelButton"
                                             onclick="document.location='${contextPath}/TemplateAction.do?method=page&id=${id}';">
                                    <I18n:message key="CANCEL"/>
                                </html:button>
                            

                        </div>

                    </html:form>
                </div>
            </div>
        </c:if>
    </tiles:put>
</tiles:insert>
