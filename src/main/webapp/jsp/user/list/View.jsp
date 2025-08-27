<%@ page buffer="128kb" errorPage="/jsp/Error.jsp" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/tiles-el" prefix="tiles" %>
<%@ taglib uri="http://ajaxtags.org/tags/ajax" prefix="ajax" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="taskMenu" value="false"/>
<c:set var="userMenu" value="true"/>
<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value="${sc.timezone}"/>
<I18n:setBundle basename="language"/>
<tiles:insert page="/jsp/layout/ListLayout.jsp" flush="true">
<tiles:put name="header" value="/jsp/user/UserHeader.jsp"/>
<tiles:put name="tabs" type="string"/>
<tiles:put name="customHeader" type="string"/>
<tiles:put name="main" type="string">
<div id="servicePanel" class="${selectedIds!=null && !empty selectedIds ? "norm" : "closed"}">
    <span>
    <img id="windowhideicon" src="${contextPath}${ImageServlet}/cssimages/ico.hidewin.gif" class="icon"
         onclick="hideServicePanel();" title="<I18n:message key="HIDE"/>">
    <img id="windowopenicon" src="${contextPath}${ImageServlet}/cssimages/ico.openwin.gif" class="icon"
         onclick="hideServicePanel();" title="<I18n:message key="OPEN"/>">
    <img id="windowcloseicon" src="${contextPath}${ImageServlet}/cssimages/ico.closewin.gif" class="icon"
         onclick="closeServicePanel(document.forms['userListForm'].elements['SELUSER']);"
         title="<I18n:message key="CLOSE"/>">
        </span>
    <c:forEach items="${selectedIds}" var="st">
        <label id="_spi_<c:out value="${st.id}"/>"
               style="padding-top: 1px; padding-bottom: 1px; font-family: Verdana; font-size: 11px; font-weight: bold;"
               title="[${st.login}] <c:out value="${st.name}"/>"
               onclick="placeOnServicePanel('<c:out value="${st.id}"/>','<c:out value="${st.login}"/>');"
               for="<c:out value="${st.login}"/>"><c:out value="${st.login}"/> </label>
    </c:forEach>

</div>
<script type="text/javascript">
    var servicePanelSrc = null;
</script>
<div class="blueborder">
<div class="caption">
    <c:out value="${headerSlider}" escapeXml="fasle"/>
    <html:link href="javascript:showBookmarkDialogSimple();" styleClass="floatlink">
        <html:img src="${contextPath}${ImageServlet}/cssimages/ico.star.gif" border="0"/>
    </html:link>
    <span><I18n:message key="USERS"/>:</span>
    <c:out value="${filterName}"/>
</div>
<ajax:tabPanel
        panelStyleId="${sc.currentSpace}"
        panelStyleClass="controlPanel"
        contentStyleId="yellowbox"
        currentStyleId="selected"
        baseUrl="${contextPath}/UserListAction.do?method=page&id=${id}"
        >
    <c:if test="${canManageUserPrivateFilters}">
        <ajax:tab
                baseUrl="${contextPath}/UserFilterParametersAction.do?method=page&id=${id}"
                defaultTab="${sc.defaultTab eq 'UserFilterParametersAction'}"
                >
            <html:img src="${contextPath}${ImageServlet}/cssimages/ico.filterproperties.gif" border="0"/><I18n:message
                key="FILTER_PARAMETERS"/>
        </ajax:tab>
    </c:if>

    <c:if test="${!empty additionalFilters}">
        <c:set var="currentFlt" value="false"/>
        <script type="text/javascript">
            var filterMenu = new TSMenu();
            filterMenu.width = 320;
            var filterMenuTitle = '<I18n:message key="OTHER_FILTERS"/>';
            <c:forEach items="${additionalFilters}" var="filt">
            <c:choose>
            <c:when test="${filt.id eq filter.id}">
            filterMenuTitle = '<c:out value="${filt.name}"/>';
            <c:set var="currentFlt" value="true"/>
            </c:when>
            <c:otherwise>
            filterMenu.add(new TSMenuItem("<c:out value="${filt.name}"/>", "<c:out value="${contextPath}"/>/UserFilterParametersAction.do?method=changeFilter&id=${id}&filterId=${filt.id}&go=true", false, false, "<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.filter.gif"));
            </c:otherwise>
            </c:choose>
            </c:forEach>
        </script>
    </c:if>
    <%-- <div class="filterPanel"> --%>
    <c:forEach items="${filters}" var="filt">
        <c:choose>
            <c:when test="${filt.id eq filterId}">
                <html:link title="${filt.description}" styleClass="selected"
                           href="${contextPath}/UserFilterParametersAction.do?method=changeFilter&id=${id}&filterId=${filt.id}&go=true">
                    <html:img alt="" src="${contextPath}${ImageServlet}/cssimages/ico.filter.gif" border="0"/>
                    <c:out value="${filt.name}"/>
                </html:link>
            </c:when>
            <c:otherwise>
                <html:link styleClass="internal" title="${filt.description}"
                           href="${contextPath}/UserFilterParametersAction.do?method=changeFilter&id=${id}&filterId=${filt.id}&go=true">
                    <html:img alt="" src="${contextPath}${ImageServlet}/cssimages/ico.filter.gif" border="0"/>
                    <c:out value="${filt.name}"/>
                </html:link>
            </c:otherwise>
        </c:choose>
    </c:forEach>
    <c:if test="${!empty additionalFilters}">
        <span class="${currentFlt eq true ? 'additional selected' : 'selected'}">
    	<script type="text/javascript">
            var filterBar = new TSMenuBar();
            filterBar.add(new TSMenuBut(filterMenuTitle, null, filterMenu, "<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.filter.gif"));
            document.write(filterBar);
        </script>
    	</span>
    </c:if>

</ajax:tabPanel>

<script type="text/javascript">
    var submitDelete = true;

    function deleteUser() {
        submitDelete = deleteConfirm("<I18n:message key="DELETE_USERS_REQ"/>", "userListForm");
    }

    function onSubmitFunction(frm) {
        return validate(frm) && allow(frm) && submitDelete;
    }

    function showBookmarkDialog(bookmarkName, userId, filterId) {
        document.getElementById('bookmark_name').value = bookmarkName;

        document.getElementById('user_id').value = userId;
        document.getElementById('filter_id').value = filterId;
        YAHOO.trackstudio.bookmark.bookmark_dialog.show();
    }

    function showBookmarkDialogSimple() {
        showBookmarkDialog('<c:out value="${title}" escapeXml="false"/>', '<c:out value="${userId}"/>', '<c:out value="${filterId}"/>');
    }
</script>

<div class="indent">

<html:form method="POST" action="/UserListAction" onsubmit="return onSubmitFunction(this);">
<html:hidden property="method" value="deleteUsers" styleId="userListId"/>
<html:hidden property="farManagerAgree" value="false" styleId="farManagerAgreeId"/>
<html:hidden property="id" value="${id}"/>
<html:hidden property="session" value="${session}"/>
<html:hidden property="collector"/>
<table class="general" cellpadding="0" cellspacing="0">
<tr class="wide">
    <c:set var="columns" value="1"/>
    <th width="4%" style="white-space:nowrap;text-align: center">
        <input type="checkbox" onClick="_selectAll(this,document.forms['userListForm'].elements['SELUSER']);"/>
    </th>
    <c:if test="${headerLogin.canView}">
        <c:set var="columns" value="${columns+1}"/>
        <th width="<c:out value="${sizeOfPart*headerLogin.parts}"/>%">
            <html:link styleClass="underline"
                       href="${contextPath}/UserListAction.do?method=page&id=${id}&sliderOrder=${headerLogin.sortBy}">
                <I18n:message key="LOGIN"/>
            </html:link>
        </th>
    </c:if>
    <c:if test="${headerName.canView}">
        <c:set var="columns" value="${columns+1}"/>
        <th width="<c:out value="${headerName.parts}"/>%">
            <html:link styleClass="underline"
                       href="${contextPath}/UserListAction.do?method=page&id=${id}&sliderOrder=${headerName.sortBy}">
                <I18n:message key="USER_NAME"/>
            </html:link>
        </th>
    </c:if>
    <c:if test="${headerFullPath.canView}">
        <c:set var="columns" value="${columns+1}"/>
        <th width="<c:out value="${headerFullPath.parts}"/>%">
            <html:link styleClass="underline"
                       href="${contextPath}/UserListAction.do?method=page&id=${id}&sliderOrder=${headerFullPath.sortBy}">
                <I18n:message key="RELATIVE_PATH"/>
            </html:link>
        </th>
    </c:if>
    <c:if test="${headerStatus.canView}">
        <c:set var="columns" value="${columns+1}"/>
        <th width="<c:out value="${sizeOfPart*headerStatus.parts}"/>%">
            <html:link styleClass="underline"
                       href="${contextPath}/UserListAction.do?method=page&id=${id}&sliderOrder=${headerStatus.sortBy}">
                <I18n:message key="PRSTATUS"/>
            </html:link>
        </th>
    </c:if>
    <c:if test="${headerCompany.canView}">
        <c:set var="columns" value="${columns+1}"/>
        <th width="<c:out value="${sizeOfPart*headerCompany.parts}"/>%">
            <html:link styleClass="underline"
                       href="${contextPath}/UserListAction.do?method=page&id=${id}&sliderOrder=${headerCompany.sortBy}">
                <I18n:message key="COMPANY"/>
            </html:link>
        </th>
    </c:if>
    <c:if test="${headerEmail.canView}">
        <c:set var="columns" value="${columns+1}"/>
        <th width="<c:out value="${sizeOfPart*headerEmail.parts}"/>%">
            <html:link styleClass="underline"
                       href="${contextPath}/UserListAction.do?method=page&id=${id}&sliderOrder=${headerEmail.sortBy}">
                <I18n:message key="EMAIL"/>
            </html:link>
        </th>
    </c:if>
    <c:if test="${headerTel.canView}">
        <c:set var="columns" value="${columns+1}"/>
        <th width="<c:out value="${sizeOfPart*headerTel.parts}"/>%">
            <html:link styleClass="underline"
                       href="${contextPath}/UserListAction.do?method=page&id=${id}&sliderOrder=${headerTel.sortBy}">
                <I18n:message key="PHONE"/>
            </html:link>
        </th>
    </c:if>
    <c:if test="${headerLocale.canView}">
        <c:set var="columns" value="${columns+1}"/>
        <th width="<c:out value="${sizeOfPart*headerLocale.parts}"/>%">
            <html:link styleClass="underline"
                       href="${contextPath}/UserListAction.do?method=page&id=${id}&sliderOrder=${headerLocale.sortBy}">
                <I18n:message key="LOCALE"/>
            </html:link>
        </th>
    </c:if>
    <c:if test="${headerTimezone.canView}">
        <c:set var="columns" value="${columns+1}"/>
        <th width="<c:out value="${sizeOfPart*headerTimezone.parts}"/>%">
            <html:link styleClass="underline"
                       href="${contextPath}/UserListAction.do?method=page&id=${id}&sliderOrder=${headerTimezone.sortBy}">
                <I18n:message key="TIME_ZONE"/>
            </html:link>
        </th>
    </c:if>
    <c:if test="${headerActive.canView}">
        <c:set var="columns" value="${columns+1}"/>
        <th width="<c:out value="${sizeOfPart*headerActive.parts}"/>%">
            <html:link styleClass="underline"
                       href="${contextPath}/UserListAction.do?method=page&id=${id}&sliderOrder=${headerActive.sortBy}">
                <I18n:message key="ACTIVE"/>
            </html:link>
        </th>
    </c:if>
    <c:if test="${headerExpireDate.canView}">
        <c:set var="columns" value="${columns+1}"/>
        <th width="<c:out value="${sizeOfPart*headerExpireDate.parts}"/>%">
            <html:link styleClass="underline"
                       href="${contextPath}/UserListAction.do?method=page&id=${id}&sliderOrder=${headerExpireDate.sortBy}">
                <I18n:message key="EXPIRE_DATE"/>
            </html:link>
        </th>
    </c:if>
    <c:if test="${headerChildrenAllowed.canView}">
        <c:set var="columns" value="${columns+1}"/>
        <th width="<c:out value="${sizeOfPart*headerChildrenAllowed.parts}"/>%">
            <html:link styleClass="underline"
                       href="${contextPath}/UserListAction.do?method=page&id=${id}&sliderOrder=${headerChildrenAllowed.sortBy}">
                <I18n:message key="USERS_ALLOWED"/>
            </html:link>
        </th>
    </c:if>
    <c:if test="${headerChildrenCount.canView}">
        <c:set var="columns" value="${columns+1}"/>
        <th width="<c:out value="${sizeOfPart*headerChildrenCount.parts}"/>%">
            <html:link accesskey="" styleClass="underline"
                       href="${contextPath}/UserListAction.do?method=page&id=${id}&sliderOrder=${headerChildrenCount.sortBy}">
                <I18n:message key="SUBORDINATED_USERS_AMOUNT"/>
            </html:link>
        </th>
    </c:if>
    <c:if test="${headerParent.canView}">
        <c:set var="columns" value="${columns+1}"/>
        <th width="<c:out value="${sizeOfPart*headerParent.parts}"/>%">
            <html:link styleClass="underline"
                       href="${contextPath}/UserListAction.do?method=page&id=${id}&sliderOrder=${headerParent.sortBy}">
                <I18n:message key="USER_PARENT"/>
            </html:link>
        </th>
    </c:if>
    <c:if test="${headerTemplate.canView}">
        <c:set var="columns" value="${columns+1}"/>
        <th width="<c:out value="${sizeOfPart*headerTemplate.parts}"/>%">
            <html:link styleClass="underline"
                       href="${contextPath}/UserListAction.do?method=page&id=${id}&sliderOrder=${headerTemplate.sortBy}">
                <I18n:message key="USER_TEMPLATE"/>
            </html:link>
        </th>
    </c:if>
    <c:forEach items="${udfs}" var="udf">
        <c:set var="udflink" value="${udfHeaderLink[udf]}"/>
        <c:if test="${udflink.canView}">
            <th width="<c:out value="${sizeOfPart*udflink.parts}"/>%">
                <c:set var="columns" value="${columns+1}"/>
                <html:link styleClass="underline"
                           href="${contextPath}/UserListAction.do?method=page&id=${id}&sliderOrder=${udflink.sortBy}">
                    <c:out value="${udfHeaderCaption[udf]}" escapeXml="false"/>
                </html:link>
            </th>
        </c:if>
    </c:forEach>
</tr>
<c:choose>
    <c:when test="${!(empty userLines)}">
        <c:forEach var="userLine" items="${userLines}" varStatus="varCounter">
            <tr class="line<c:out value="${varCounter.index mod 2}"/>">
                <td style="text-align: center">
                    <html:hidden property="USERIDS" value="${userLine.id}"/>
                    <c:if test="${userLine.canManage}">
                        <input type="checkbox" name="SELUSER"
                               title="<c:out value="${userLine.login}" escapeXml="true"/>"
                               alt="delete1" value="${userLine.id}"
                               onclick="this.checked =placeOnServicePanel('<c:out value="${userLine.id}" escapeXml="true"/>','<c:out value="${userLine.login}" escapeXml="true"/>')">
                    </c:if>
                </td>
                <c:if test="${headerLogin.canView}">
                    <td><a class="user" id="${userLine.id}-login"
                           href="${contextPath}/user/<c:out value="${userLine.login}"/>?thisframe=true">
                        <html:img styleClass="icon" border="0"
                                  src="${contextPath}${ImageServlet}/cssimages/${userLine.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
                        <c:out value="${userLine.login}" escapeXml="true"/>
                    </a>
                    </td>
                </c:if>

                <c:if test="${headerName.canView}">
                    <td><span style="white-space: nowrap;"><a class="internal" id="${userLine.id}-name"
                                                              href="${contextPath}/user/<c:out value="${userLine.login}"/>?thisframe=true">
                        <html:img styleClass="icon" border="0"
                                  src="${contextPath}${ImageServlet}/cssimages/${userLine.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
                        <c:out value="${userLine.name}" escapeXml="true"/>
                    </a></span></td>
                </c:if>

                <c:if test="${headerFullPath.canView}">
                    <td>
                        <c:forEach var="user" items="${userLine.ancestors}" varStatus="varCounter">
                            <a class="internal"
                               href="${contextPath}/user/<c:out value="${user.login}"/>?thisframe=true">
                                <html:img styleClass="icon" border="0"
                                          src="${contextPath}${ImageServlet}/cssimages/${user.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
                                <c:out value="${user.name}" escapeXml="true"/>&nbsp;/
                            </a>
                        </c:forEach>
                        <a class="internal" id="${userLine.id}-name"
                           href="${contextPath}/user/<c:out value="${userLine.login}"/>?thisframe=true">
                            <html:img styleClass="icon" border="0"
                                      src="${contextPath}${ImageServlet}/cssimages/${userLine.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
                            <c:out value="${userLine.name}" escapeXml="true"/>
                        </a>
                    </td>
                </c:if>

                <c:if test="${headerStatus.canView}">
                    <td>
                        <span style="white-space: nowrap;">
                            <html:img styleClass="icon" border="0"
                                      src="${contextPath}${ImageServlet}/cssimages/ico.status.gif"/>
                                <c:out value="${userLine.prstatus.name}" escapeXml="true"/>
                        </span>
                    </td>
                </c:if>

                <c:if test="${headerCompany.canView}">
                    <td>
                        <c:out value="${userLine.company}" escapeXml="true"/>
                    </td>
                </c:if>

                <c:if test="${headerEmail.canView}">
                    <td><span style="white-space: nowrap;"><c:out value="${userLine.email}" escapeXml="true"/></span>
                    </td>
                </c:if>

                <c:if test="${headerTel.canView}">
                    <td>
                        <c:out value="${userLine.tel}" escapeXml="true"/>
                    </td>
                </c:if>

                <c:if test="${headerLocale.canView}">
                    <td>
                        <c:out value="${userLine.localeAsString}" escapeXml="true"/>
                    </td>
                </c:if>

                <c:if test="${headerTimezone.canView}">
                    <td><span style="white-space: nowrap;"><c:out value="${userLine.timezoneAsString}"
                                                                  escapeXml="true"/></span>
                    </td>
                </c:if>

                <c:if test="${headerActive.canView}">
                    <td>
                        <span style="text-align: center">
                            <span style="white-space: nowrap;">
                                <c:out value="${userLine.active}" escapeXml="true"/>
                            </span>
                        </span>
                    </td>
                </c:if>

                <c:if test="${headerExpireDate.canView}">
                    <td>
                        <span style="white-space: nowrap;">
                            <c:out value="${userLine.expireDateAsString}" escapeXml="true"/>
                        </span>
                    </td>
                </c:if>

                <c:if test="${headerChildrenAllowed.canView}">
                    <td>
                        <c:out value="${userLine.childrenAllowed}" escapeXml="true"/>
                    </td>
                </c:if>
                <c:if test="${headerChildrenCount.canView}">
                    <td>
                        <c:out value="${userLine.totalChildrenCount}" escapeXml="true"/>
                    </td>
                </c:if>

                <c:if test="${headerParent.canView}">
                    <td><a class="user" id="${userLine.id}-parent"
                           href="${contextPath}/user/<c:out value="${userLine.parent}"/>?thisframe=true">
                        <html:img styleClass="icon" border="0"
                                  src="${contextPath}${ImageServlet}/cssimages/${userLine.parent.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
                        <c:out value="${userLine.parent.login}" escapeXml="true"/>
                    </a>
                    </td>
                </c:if>
                <c:if test="${headerTemplate.canView}">
                    <td>
                        <c:out value="${userLine.template}" escapeXml="true"/>
                    </td>
                </c:if>
                <c:forEach items="${udfs}" var="udf">
                    <td>
                        <c:if test="${userLine.UDFValues[udf] ne null}">
                            <c:set var="udfValue" value="${userLine.UDFValues[udf]}"/>
                            <c:set var="val" value="${udfValue.value}"/>
                            <c:if test="${val ne null}">
                                <c:choose>
                                    <c:when test="${udfValue.type eq 'date'}">
                                        <I18n:formatDate value="${val.time}" type="both" dateStyle="short"
                                                         timeStyle="short"/>
                                    </c:when>
                                    <c:when test="${udfValue.type eq 'float'}">
                                        <I18n:formatNumber value="${val}" groupingUsed="true" maxFractionDigits="${decimalFormatUdfFloat}"/>
                                    </c:when>
                                    <c:when test="${udfValue.type eq 'list'}">
                                        <c:out value="${val.value}" escapeXml="true"/>
                                    </c:when>
                                    <c:when test="${udfValue.type eq 'multilist'}">
                                        <c:forEach items="${val}" var="item">
                                            <c:out value="${item.value}" escapeXml="true"/>
                                        </c:forEach>
                                    </c:when>
                                    <c:when test="${udfValue.type eq 'task'}">
                                        <c:forEach var="t" items="${val}" varStatus="status">
                                            <div class="line<c:out value="${status.index mod 2}"/>">
                                                <html:link styleClass="internal"
                                                           href="${contextPath}/task/${t.number}?thisframe=true">
                                                    <html:img styleClass="icon" border="0"
                                                              src="${contextPath}${ImageServlet}/icons/categories/${t.category.icon}"/>
                                                    <html:img styleClass="state" border="0"
                                                              style="background-color: ${t.status.color}"
                                                              src="${contextPath}${ImageServlet}${t.status.image}"/>
                                                    <c:out value="${t.name}"/></html:link>&nbsp;<em
                                                    class="number">[#<c:out value="${t.number}"/>]</em>
                                            </div>
                                        </c:forEach>
                                    </c:when>
                                    <c:when test="${udfValue.type eq 'user'}">
                                        <c:forEach var="user" items="${val}" varStatus="status">
                                            <div class="line<c:out value="${status.index mod 2}"/>">
                                                <a class="user"
                                                   href="${contextPath}/user/<c:out value="${user.login}"/>?thisframe=true"
                                                        ><html:img styleClass="icon" border="0"
                                                                   src="${contextPath}${ImageServlet}/cssimages/${user.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"
                                                        /><c:out value="${user.name}"/></a>&nbsp;<em
                                                    class="number">[@<c:out value="${user.login}"/>]
                                            </em>
                                            </div>
                                        </c:forEach>
                                    </c:when>
                                    <c:when test="${udfValue.type eq 'memo'}">
                                        <c:out value="${val}" escapeXml="${!udfValue.htmlview}"/>
                                    </c:when>
                                    <c:when test="${udfValue.type eq 'url'}">
                                        <html:link styleClass="internal" href="${val.link}">
                                            <c:out value="${val.description ne null ? val.description : val.link}"
                                                   escapeXml="true"/>
                                        </html:link>
                                    </c:when>
                                    <c:otherwise>
                                        <c:out value="${val}" escapeXml="${!udfValue.htmlview}"/>
                                    </c:otherwise>
                                </c:choose>
                            </c:if>
                        </c:if>
                    </td>
                </c:forEach>
            </tr>
        </c:forEach>
    </c:when>
    <c:otherwise>
        <tr>
            <td colspan="<c:out value="${columns}"/>" width="100%">
                <span style="text-align: center">
                    <I18n:message key="EMPTY_USER_LIST"/>
                </span>
            </td>
        </tr>
    </c:otherwise>
</c:choose>
</table>
<c:out value="${slider}" escapeXml="false"/>
<div class="controls">
    <c:if test="${canCutUser eq true}">
        <input type="submit" class="iconized secondary"
               value="<I18n:message key="CUT"/>"
               name="CUT"
               onClick="submitDelete=true; this.form['collector'].value=forrobots(document.forms['userListForm'].elements['SELUSER']); set('cut'); if(onSubmitFunction(this.form) ) this.form.submit();">
    </c:if>
    <c:if test="${canPasteUser eq true}">
        <input type="submit" class="iconized secondary" value="<I18n:message key="PASTE"/>" name="PASTE"
               onClick="set('paste'); closeServicePanel(document.forms['userListForm'].elements['SELUSER']); if(onSubmitFunction(this.form)) this.form.submit();">
    </c:if>
    <c:if test="${canDeleteUsers}">
        <input type="submit" class="iconized" value="<I18n:message key="DELETE"/>" name="deleteButton"
               onClick="deleteUser(); this.form['collector'].value=forrobots(document.forms['userListForm'].elements['SELUSER']); closeServicePanel(document.forms['userListForm'].elements['SELUSER']); set('deleteUsers'); if(onSubmitFunction(this.form) ) this.form.submit();">
    </c:if>
</div>
<I18n:message key="TOTAL_USERS"/>&nbsp;:&nbsp;<c:out value="${totalChildrenCount}"/>

<script type="text/javascript">
    function set(target) {
        document.getElementById('userListId').value = target;
    }
    servicePanelSrc = document.forms["userListForm"].elements["SELUSER"];

    fillFormServicePanel(document.forms["userListForm"].elements["SELUSER"]);

    function copyAllToClipboard(Sender) {
        var addFor = "";
        if (Sender.elements['SELUSER'].length)
            for (var j = 0; j < Sender.elements['SELUSER'].length; j++) {
                if (Sender.elements['SELUSER'][j].checked) {
                    addFor += Sender.elements['SELUSER'][j].title + ' ';
                }
            }
        else
        if (Sender.elements['SELUSER'].checked)
            addFor += Sender.elements['SELUSER'].title;
        copyToClipboard(addFor);
        return true;
    }


    var selectedUsers = new Array(<c:out value="${selectedUsers}" escapeXml="false"/>);
    var operationType = 'cut_in_list';
    var tempNode;
    for (var i = 0; i < selectedUsers.length; i++) {
        tempNode = document.getElementById(selectedUsers[i] + '-login');
        if (tempNode != null && tempNode.tagName == 'A' && operationType != '') {
            tempNode.className = operationType;
        }
        tempNode = document.getElementById(selectedUsers[i] + '-name');
        if (tempNode != null && tempNode.tagName == 'A' && operationType != '') {
            tempNode.className = operationType;
        }
    }
</script>

</html:form>
</div>
</div>

<script type="text/javascript">
    function onSubmitBookMark() {
        if ($('bookmark_name').value != '') {
            YAHOO.trackstudio.bookmark.bookmark_dialog.hide();
            var url = "${contextPath}/BookmarkAction.do";
            var pars = {method : 'save', name : $('#bookmark_name').val(), userId: $('#user_id').val(), filterId: $('#filter_id').val()};
	        $.ajax(url, {
		        data : pars,
		        method: "post",
		        success: function(data) {
			        self.top.frames[0].updateBookmarks("${contextPath}/bookmark");
		        }
	        });
        } else {
            alert("<I18n:message key="ENTER_BOOKMARK_NAME"/>");
        }
        return false;
    }
</script>

<div id="bookmark_dialog" style="visibility:hidden;">
    <div class="hd"><I18n:message key="CREATE_BOOKMARK"/></div>
    <div class="bd">
        <form method="post" id="bookmarkForm" action="#" onsubmit="onSubmitBookMark()">
            <table>
                <tr>
                    <td><label for="name" style="margin-right: 20px;"><I18n:message key="NAME"/></label></td>
                    <td><input type="text" value="'${name}'" id="bookmark_name" size="35"/></td>
                </tr>
                <tr>
                    <td><label for="task" style="margin-right: 20px;"><I18n:message key="USER"/></label></td>
                    <td><c:out value="${user.name}"/></td>
                </tr>
                <tr>
                    <td><label for="filter" style="margin-right: 20px;"><I18n:message key="FILTER"/></label></td>
                    <td><c:out value="${filter.name}"/></td>
                </tr>
            </table>
            <input type="hidden" value="'${userId}'" id="user_id"/>
            <input type="hidden" value="'${filterId}'" id="filter_id"/>
        </form>
    </div>
</div>
</tiles:put>
</tiles:insert>



