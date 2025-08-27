<%@ page buffer="128kb" errorPage="/jsp/Error.jsp" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/tiles-el" prefix="tiles" %>
<%@ taglib uri="http://trackstudio.com" prefix="ts" %>
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
<script type="text/javascript">
    function showBookmarkDialog(bookmarkName, userId) {
        document.getElementById('bookmark_name').value = bookmarkName;
        document.getElementById('user_id').value = userId;
        YAHOO.trackstudio.bookmark.bookmark_dialog.show();
    }

    function showBookmarkDialogSimple() {
        showBookmarkDialog('<c:out value="${title}" escapeXml="false"/>', '<c:out value="${userId}"/>');
    }
</script>
<script type="text/javascript">
    var resourceId = "${user.id}";
    var number = "${user.login}";
    var taskUpload = false;
</script>
<c:set var="urlHtml" value="html"/>
<ts:js request="${request}" response="${response}">
    <ts:jsLink link="${urlHtml}/dnd/dnd.js"/>
</ts:js>
<div id="servicePanel" class="${selectedIds!=null && !empty selectedIds ? "norm" : "closed"}">
    <span>
    <img id="windowhideicon" src="${contextPath}${ImageServlet}/cssimages/ico.hidewin.gif" class="icon" onclick="hideServicePanel();" title="<I18n:message key="HIDE"/>">
    <img id="windowopenicon" src="${contextPath}${ImageServlet}/cssimages/ico.openwin.gif" class="icon" onclick="hideServicePanel();" title="<I18n:message key="OPEN"/>">
    <img id="windowcloseicon" src="${contextPath}${ImageServlet}/cssimages/ico.closewin.gif" class="icon" onclick="closeServicePanel(document.forms['userListForm'].elements['SELUSER']);" title="<I18n:message key="CLOSE"/>">
        </span>
    <c:forEach items="${selectedIds}" var="st">
        <label id="_spi_<c:out value="${st.id}"/>" style="padding-top: 1px; padding-bottom: 1px; font-family: Verdana; font-size: 11px; font-weight: bold;" title="[${st.login}] <c:out value="${st.name}"/>" onclick="placeOnServicePanel('<c:out value="${st.id}"/>','<c:out value="${st.login}"/>');" for="<c:out value="${st.login}"/>"><c:out value="${st.login}"/> </label>
    </c:forEach>

</div>
<script type="text/javascript">
    var servicePanelSrc = null;
</script>
<div class="blueborder">
<div class="caption">
    <html:link href="javascript:showBookmarkDialogSimple();" styleClass="floatlink">
        <html:img src="${contextPath}${ImageServlet}/cssimages/ico.star.gif" border="0" />
    </html:link>
    <I18n:message key="USER_OVERVIEW"/>
    <c:if test="${showClipboardButton}">
        <html:link style="float: right; text-decoration: none; font-weight: normal;padding-right: 20px;" onclick="placeOnServicePanel('${user.id}','${user.login}');" href="${contextPath}/UserListAction.do?method=cut&amp;id=${uci.parent.id}&amp;collector=${id}&amp;operation=CUT"><html:img src="${contextPath}${ImageServlet}/cssimages/ico.cut.gif" border="0"/><I18n:message key="CUT"/></html:link>
    </c:if>
</div>

<div class="controlPanel">
    <c:if test="${canEditUser}">
        <html:link href="${contextPath}/UserEditAction.do?method=page&amp;id=${id}">
            <html:img styleClass="icon" src="${contextPath}${ImageServlet}/cssimages/ico.edit.gif" border="0"/>
            <I18n:message key="EDIT"/>
        </html:link>
    </c:if>
    <c:if test="${canEditUser && canEditActive}">
        <c:choose>
            <c:when test="${enabled}">
                <html:link  href="${contextPath}/UserEditAction.do?method=activate&amp;id=${id}">
                    <html:img styleClass="icon" src="${contextPath}${ImageServlet}${ImageServlet}/cssimages/ico.deactivate.gif" border="0"/>
                    <I18n:message key="DEACTIVATE"/>
                </html:link>
            </c:when>
            <c:otherwise>
                <html:link href="${contextPath}/UserEditAction.do?method=activate&amp;id=${id}">
                    <html:img styleClass="icon" src="${contextPath}${ImageServlet}/cssimages/ico.activate.gif" border="0"/>
                    <I18n:message key="ACTIVATE"/>
                </html:link>
            </c:otherwise>
        </c:choose>
    </c:if>
    <c:if test="${canChangePassword}">
        <html:link href="${contextPath}/ChangePasswordAction.do?method=page&amp;id=${id}">
            <html:img styleClass="icon" src="${contextPath}${ImageServlet}/cssimages/ico.key.gif" border="0"/>
            <I18n:message key="CHANGE_PASSWORD"/>
        </html:link>
    </c:if>

    <c:if test="${canCreateUserAttachments}">
        <html:link styleClass="internal" href="${contextPath}/AttachmentEditAction.do?method=attachToUser&id=${id}">
            <html:img src="${contextPath}${ImageServlet}/cssimages/ico.attachment.png" border="0"/>
            <I18n:message key="FILE_ADD"/>
        </html:link>
        <%--<html:link styleClass="internal" href="javascript:{}" onclick="window.open('${contextPath}/UploadAppletAction.do?method=page&amp;user=true&amp;id=${id}',--%>
                 <%--'uplWin','dependent=yes,menubar=no,toolbar=no,status=no,scrollbars=no,titlebar=no,left=0,top=20,width=845,height=445,resizable=no');">--%>
            <%--<html:img src="${contextPath}${ImageServlet}/cssimages/ico.attachment.png" border="0" alt="Attachment"/>--%>
            <%--<I18n:message key="UPLOAD_MANAGER"/>--%>
        <%--</html:link>--%>
    </c:if>
</div>
<div class="indent">

<table class="general" cellpadding="0" cellspacing="0">
    <caption>
        <I18n:message key="USER_PROPERTIES"/>
    </caption>
    <colgroup>
        <col class="col_1">
        <col class="col_2">
    </colgroup>

    <c:if test="${!empty user.login}">
        <tr>
            <th>
                <I18n:message key="LOGIN"/>
            </th>
            <td>
                <c:out value="${user.login}" escapeXml="true"/>
            </td>
        </tr>
    </c:if>
    <c:if test="${!empty user.name}">
        <tr>
            <th>
                <I18n:message key="USER_NAME"/>
            </th>
            <td>
                <c:out value="${user.name}" escapeXml="true"/>
            </td>
        </tr>
    </c:if>
    <c:if test="${!empty user.prstatus}">
        <tr>
            <th>
                <I18n:message key="PRSTATUS"/>
            </th>
            <td><span style="white-space: nowrap;">
                                <html:img styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/ico.status.gif"/><c:out
                    value="${user.prstatus.name}" escapeXml="true"/></span></td>
        </tr>
    </c:if>
    <c:if test="${!empty user.company}">
        <tr>
            <th>
                <I18n:message key="COMPANY"/>
            </th>
            <td>
                <c:out value="${user.company}" escapeXml="true"/>
            </td>
        </tr>
    </c:if>
    <c:if test="${!empty user.email}">
        <tr>
            <th>
                <I18n:message key="EMAIL"/>
            </th>
            <td>
                <c:out value="${user.email}" escapeXml="true"/>
            </td>
        </tr>
    </c:if>
    <c:if test="${!empty user.tel}">
        <tr>
            <th>
                <I18n:message key="PHONE"/>
            </th>
            <td>
                <c:out value="${user.tel}" escapeXml="true"/>
            </td>
        </tr>
    </c:if>


    <tr>
        <th>
            <I18n:message key="ACTIVE"/>
        </th>
        <td>
            <c:choose>
                <c:when test="${user.active}">
                    <I18n:message key="YES"/>
                </c:when>
                <c:otherwise>
                    <I18n:message key="NO"/>
                </c:otherwise>
            </c:choose>
        </td>
    </tr>


    <c:if test="${user.expireDate ne null}">
        <tr>
            <th>
                <I18n:message key="EXPIRE_DATE"/>
            </th>
            <td>
                <I18n:formatDate value="${user.expireDate.time}" type="both" dateStyle="short" timeStyle="short"/>
            </td>
        </tr>
    </c:if>
    <c:if test="${!empty childallowed}">
        <tr>
            <th>
                <I18n:message key="LICENSED_USERS"/>
            </th>
            <td>
                <c:out value="${childallowed}" escapeXml="true"/>
            </td>
        </tr>
    </c:if>
</table>
<table class="general" cellpadding="0" cellspacing="0">
    <caption>
        <I18n:message key="USER_PREFERENCES"/>
    </caption>
    <colgroup>
        <col class="col_1">
        <col class="col_2">
    </colgroup>
    <c:if test="${!empty locale}">
        <tr>
            <th>
                <I18n:message key="LOCALE"/>
            </th>
            <td class="input">
                <html:img src="${contextPath}${ImageServlet}/style/flags/${country}.gif"/>
                <c:out value="${locale}" escapeXml="true"/>
            </td>
        </tr>
    </c:if>

    <tr>
        <th>
            <I18n:message key="TIME_ZONE"/>
        </th>
        <td>
            <c:out value="${user.timezoneAsString}" escapeXml="true"/>
        </td>
    </tr>

    <c:if test="${user.defaultProject != null}">
        <tr>
            <th>
                <I18n:message key="DEFAULT_PROJECT"/>
            </th>
            <td>
                <c:choose>
                    <c:when test="${user.defaultProject.canManage}">
                        <html:link styleClass="internal"
                                   href="${contextPath}/TaskViewAction.do?method=page&amp;id=${user.defaultProject.id}">
                            <html:img styleClass="icon" border="0"
                                      src="${contextPath}${ImageServlet}/icons/categories/${user.defaultProject.category.icon}"/>
                            <html:img styleClass="state" border="0"
                                      style="background-color: ${user.defaultProject.status.color}"
                                      src="${contextPath}${ImageServlet}${user.defaultProject.status.image}"/>
                            <c:out value="${user.defaultProject.name}"/>
                        </html:link>&nbsp;<em class="number">[#<c:out value="${user.defaultProject.number}"/>]</em>
                    </c:when>
                    <c:otherwise>
                        #<c:out value="${user.defaultProject.number}"/>
                    </c:otherwise>
                </c:choose>
            </td>
        </tr>
    </c:if>
    <c:if test="${!empty template}">
        <tr>
            <th>
                <I18n:message key="EMAIL_TYPE_DEFAULT"/>
            </th>
            <td>
                <c:out value="${template}" escapeXml="true"/>
            </td>
        </tr>
    </c:if>
    <c:if test="${!empty notice}">
        <tr>
            <th>
                <I18n:message key="EMERGENCY_NOTICE"/>
            </th>
            <td>
                <c:out value="${noticeDate}" escapeXml="true"/><br>
                <c:out value="${notice}" escapeXml="true"/>
            </td>
        </tr>
    </c:if>
</table>


<c:if test="${!empty viewUdfList}">

    <table class="general" cellpadding="0" cellspacing="0">
        <caption>
            <I18n:message key="CUSTOM_FIELDS"/>
        </caption>

        <colgroup>
            <col class="col_1">
            <col class="col_2">
        </colgroup>
        <c:import url="/jsp/custom/UDFViewTemplate.jsp"/>

    </table>

</c:if>


</div>
</div>

<c:if test="${!empty refTasks}">
    <div class="strut">&nbsp;</div>
    <a class="internal" name="reftask"></a>

    <div class="blueborder">

        <div class="caption">
            <I18n:message key="REFERENCED_BY_TASKS"/>
        </div>
        <div class="indent">
            <table class="general" cellpadding="0" cellspacing="0">
                <colgroup>
                    <col class="col_1">
                    <col class="col_2">
                </colgroup>

                <c:forEach var="udf" items="${refTasks}">
                    <tr>
                        <th>
                            <c:out value="${udf.key.referencedByCaption}" escapeXml="true"/>
                        </th>
                        <td>
                            <c:forEach var="t" items="${udf.value}" varStatus="status">
                                <div class="line<c:out value="${status.index mod 2}"/>">
                                    <c:if test="${t.parentId ne null}">
                                        <html:link styleClass="internal" href="${contextPath}/TaskViewAction.do?method=page&amp;id=${t.id}">
                                            <html:img styleClass="icon" border="0" src="${contextPath}${ImageServlet}/icons/categories/${t.category.icon}"/>
                                            <html:img styleClass="state" border="0" style="background-color: ${t.status.color}" src="${contextPath}${ImageServlet}${t.status.image}"/>
                                            <c:out value="${t.name}"/>
                                        </html:link>&nbsp;[<c:out value="${t.number}"/>]
                                    </c:if>
                                </div>
                            </c:forEach>
                        </td>
                    </tr>
                </c:forEach>
            </table>
        </div>
    </div>
</c:if>

<c:if test="${!empty refUsers}">
    <div class="strut">&nbsp;</div>
    <a class="internal" name="refuser"></a>

    <div class="blueborder">
        <div class="caption">
            <I18n:message key="REFERENCED_BY_USERS"/>
        </div>
        <div class="indent">
            <table class="general" cellpadding="0" cellspacing="0">

                <colgroup>
                    <col class="col_1">
                    <col class="col_2">
                </colgroup>

                <c:forEach var="udf" items="${refUsers}">

                    <tr>
                        <th>
                            <c:out value="${udf.key.referencedByCaption}" escapeXml="true"/>
                        </th>
                        <td>
                            <c:forEach var="t" items="${udf.value}" varStatus="status">
                                <div class="line<c:out value="${status.index mod 2}"/>">
                                    <c:if test="${t.parentId ne null}">
                                        <html:link styleClass="internal" href="${contextPath}/UserViewAction.do?method=page&amp;id=${t.id}" styleId="${t.id eq sc.userId ? 'loggedUser' : ''}">
                                            <html:img  styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/${t.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
                                            <c:out value="${t.name}"/>
                                        </html:link>
                                    </c:if>
                                    <c:if test="${t.parentId == null}">
                                        <html:img  styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/${t.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
                                        <c:out value="${t.name}"/>
                                    </c:if>
                                </div>
                            </c:forEach>
                        </td>
                    </tr>

                </c:forEach>
            </table>
        </div>
    </div>
</c:if>

<c:if test="${canViewUserACLs}">
    <c:if test="${!empty acls}">
        <div class="strut">&nbsp;</div>
        <div class="blueborder">
            <div class="caption">
                <I18n:message key="TASK_ACLS_VIEW"/>
            </div>
            <div class="indent">
                <table class="general" cellpadding="0" cellspacing="0">
                    <tr class="wide">
                        <th>
                            <I18n:message key="CONNECTED_TO"/>
                        </th>
                        <th>
                            <I18n:message key="EFFECTIVE_PRSTATUSES"/>
                        </th>
                    </tr>
                    <c:choose>
                        <c:when test="${(!empty acls) || (!empty userAcls)}">
                            <c:forEach var="acl" items="${acls}" varStatus="varCounter">
                                <tr class="line<c:out value="${varCounter.index mod 2}"/>">
                                    <td>
                                        <span class="task ${acl.key.status.finish ? 'finish' : ''} ${acl.key.status.start ? 'start' : ''}">
                                            <html:img styleClass="icon" border="0" src="${contextPath}${ImageServlet}/icons/categories/${acl.key.category.icon}"/>
                                            <c:out value="${acl.key.name}"/>&nbsp;[#<c:out value="${acl.key.number}"/>]
                                        </span>
                                    </td>
                                    <td>
                                        <c:forEach var="cat" items="${acl.value}" varStatus="varC">
                                            <c:if test="${varC.index > 0}">,</c:if>
                                            <span style="white-space: nowrap;">
                                                <html:img styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/ico.status.gif"/>
                                                <c:out value="${cat.name}" escapeXml="true"/>
                                            </span>
                                        </c:forEach>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr class="wide">
                                <td colspan="2"><span style="text-align: center;"><I18n:message
                                        key="EMPTY_ACL_LIST"/></span></td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                </table>
            </div>
        </div>
    </c:if>

    <c:if test="${!empty userAcls}">
        <div class="strut">&nbsp;</div>
        <div class="blueborder">
            <div class="caption">
                <I18n:message key="USER_ACLS_VIEW"/>
            </div>
            <div class="indent">
                <table class="general" cellpadding="0" cellspacing="0">
                    <tr class="wide">
                        <th>
                            <I18n:message key="CONNECTED_TO"/>
                        </th>
                        <th>
                            <I18n:message key="EFFECTIVE_PRSTATUSES"/>
                        </th>
                    </tr>
                    <c:choose>
                        <c:when test="${(!empty acls) || (!empty userAcls)}">
                            <c:forEach var="acl" items="${userAcls}" varStatus="varCounter">
                                <tr class="line<c:out value="${varCounter.index mod 2}"/>">
                                    <td>
                                    <span class="user" ${acl.key.id eq sc.userId ? "id='loggedUser'" : ""}>
                <html:img styleClass="icon" border="0"
                          src="${contextPath}${ImageServlet}/cssimages/${acl.key.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
                <c:out value="${acl.key.name}" escapeXml="true"/>
            </span>
                                    </td>
                                    <td>
                                        <c:forEach var="cat" items="${acl.value}" varStatus="varC">
                                            <c:if test="${varC.index > 0}">,</c:if><span style="white-space: nowrap;">
                <html:img styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/ico.status.gif"/><c:out
                                                value="${cat.name}" escapeXml="true"/></span></c:forEach>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr class="wide">
                                <td colspan="2"><span style="text-align: center;"><I18n:message
                                        key="EMPTY_ACL_LIST"/></span></td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                </table>
            </div>
        </div>
    </c:if>
</c:if>

<c:if test="${canViewNotification}">
    <div class="strut">&nbsp;</div>
    <a class="internal" name="notification"></a>

    <div class="blueborder">
        <div class="caption">
            <I18n:message key="NOTIFICATIONS_VIEW"/>
        </div>
        <div class="indent">
            <table class="general" cellpadding="0" cellspacing="0">

                <tr class="wide">
                    <th>
                        <I18n:message key="NAME"/>
                    </th>
                    <th>
                        <I18n:message key="CONNECTED_TO"/>
                    </th>
                    <th>
                        <I18n:message key="FILTER"/>
                    </th>
                    <th>
                        <I18n:message key="EMAIL_TYPE"/>
                    </th>
                </tr>
                <c:choose>
                    <c:when test="${!empty notifications}">
                        <c:forEach var="notification" items="${notifications}" varStatus="varCounter">
                            <tr class="line<c:out value="${varCounter.index mod 2}"/>">
                                <td>
                                    <c:choose>
                                        <c:when test="${notification.canManage}">
                                            <html:link styleClass="internal" href="${contextPath}/TaskNotifyViewAction.do?method=page&id=${notification.taskId}&notificationId=${notification.id}">
                                                <img title="<I18n:message key="OBJECT_PROPERTIES_VIEW"/>" border="0" hspace="0" vspace="0" src="<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.notifications.gif"/>
                                                <c:out value="${notification.name}"/>
                                            </html:link>
                                        </c:when>
                                        <c:otherwise>
                                            <c:out value="${notification.name}"/>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <html:img styleClass="icon" border="0"
                                              src="${contextPath}${ImageServlet}/icons/categories/${notification.task.category.icon}"/>
                                    <c:out value="${notification.task.name}" escapeXml="true"/>
                                </td>
                                <td>
                                    <c:out value="${notification.filter.name}"/>
                                </td>
                                <td>
                                    <c:out value="${notification.template}"/>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <tr class="wide">
                            <td colspan="4"><span style="text-align: center;"><I18n:message
                                    key="EMPTY_NOTIFICATION_LIST"/></span></td>
                        </tr>
                    </c:otherwise>
                </c:choose>
            </table>
        </div>
    </div>
</c:if>
<c:if test="${canViewSubscription}">
    <div class="strut">&nbsp;</div>
    <a class="internal" name="subscription"></a>

    <div class="blueborder">
        <div class="caption">
            <I18n:message key="SUBSCRIPTIONS_LIST"/>
        </div>
        <div class="indent">
            <table class="general" cellpadding="0" cellspacing="0">

                <tr class="wide">
                    <th>
                        <I18n:message key="NAME"/>
                    </th>
                    <th>
                        <I18n:message key="CONNECTED_TO"/>
                    </th>
                    <th>
                        <I18n:message key="FILTER"/>
                    </th>
                    <th>
                        <I18n:message key="EMAIL_TYPE"/>
                    </th>
                    <th>
                        <I18n:message key="VALID_TIME"/>
                    </th>
                    <th>
                        <I18n:message key="NEXT_RUN"/>
                    </th>
                    <th>
                        <I18n:message key="INTERVAL"/> (<I18n:message key="BUDGET_MINUTES"/>)
                    </th>
                </tr>
                <c:choose>
                    <c:when test="${!empty subscriptions}">
                        <c:forEach var="subscription" items="${subscriptions}" varStatus="varCounter">
                            <tr class="line<c:out value="${varCounter.index mod 2}"/>">
                                <td>
                                    <c:choose>
                                        <c:when test="${subscription.canManage}">
                                            <html:link styleClass="internal" href="${contextPath}/TaskSubscribeViewAction.do?method=page&id=${subscription.taskId}&subscriptionId=${subscription.id}">
                                                <img title="<I18n:message key="OBJECT_PROPERTIES_VIEW"/>" border="0" hspace="0" vspace="0" src="<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.subscription.gif"/>
                                                <c:out value="${subscription.name}"/>
                                            </html:link>
                                        </c:when>
                                        <c:otherwise>
                                            <c:out value="${subscription.name}"/>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <html:img styleClass="icon" border="0"
                                              src="${contextPath}${ImageServlet}/icons/categories/${subscription.task.category.icon}"/>
                                    <c:out value="${subscription.task.name}" escapeXml="true"/>
                                </td>
                                <td>
                                    <c:out value="${subscription.filter.name}"/>
                                </td>
                                <td>
                                    <c:out value="${subscription.template}"/>
                                </td>
                                <td>
                                    <I18n:message key="FROM"/>
                                    <I18n:formatDate value="${subscription.startdate.time}" type="both" dateStyle="short" timeStyle="short"/>
                                    /
                                    <I18n:message key="TO"/>
                                    <I18n:formatDate value="${subscription.stopdate.time}" type="both" dateStyle="short" timeStyle="short"/>
                                </td>
                                <td>
                                    <I18n:formatDate value="${subscription.nextrun.time}" type="both" dateStyle="short" timeStyle="short"/>
                                </td>
                                <td>
                                    <c:out value="${subscription.interval}"/>
                                </td>
                            </tr>

                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <tr class="wide">
                            <td colspan="7"><span style="text-align: center;"><I18n:message
                                    key="EMPTY_SUBSCRIPTION_LIST"/></span></td>
                        </tr>
                    </c:otherwise>
                </c:choose>
            </table>
        </div>
    </div>
</c:if>
<c:if test="${canViewUserAttachments}">
    <div class="strut"><a class="internal" name="attachments"></a></div>
    <c:import url="/jsp/user/user/view/attachments/UserAttachmentViewTile.jsp"/>
</c:if>

<script type="text/javascript">
    function onSubmitBookMark() {
        if ($('bookmark_name').value != '') {
            YAHOO.trackstudio.bookmark.bookmark_dialog.hide();
            var url = "${contextPath}/BookmarkAction.do";
            var pars = {method : 'save', name : $('#bookmark_name').val(), userId: $('#user_id').val()};
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
                    <td><input type="text" value="'${name}'" id="bookmark_name" size="35" /></td>
                </tr>
                <tr>
                    <td><label for="task" style="margin-right: 20px;"><I18n:message key="USER"/></label></td>
                    <td><c:out value="${uci.name}"/></td>
                </tr>
            </table>
            <input type="hidden" value="'${userId}'" id="user_id" />
        </form>
    </div>
</div>

</tiles:put>
</tiles:insert>