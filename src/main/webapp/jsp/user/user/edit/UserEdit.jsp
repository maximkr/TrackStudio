<%@ page buffer="128kb" errorPage="/jsp/Error.jsp" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/tiles-el" prefix="tiles" %>
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
<c:import url="/jsp/TinyMCE.jsp"/>
<div class="blueborder">
<div class="caption">
    <c:choose>
        <c:when test="${newUser eq true}">
            <I18n:message key="ADD_USER_WITH_PRSTATUS"/>
            <c:out value="${userPrstatus.name}"/>
        </c:when>
        <c:otherwise>
            <I18n:message key="EDIT"/>
            <c:out value="${userName}"/>
        </c:otherwise>
    </c:choose>
</div>
<div class="indent">
<html:form action="/UserEditAction" method="post" enctype="multipart/form-data" styleId="checkunload"
           onsubmit="return validate(this);">
<div class="general">
<table class="general" cellpadding="0" cellspacing="0">
<caption>
    <c:out value="${tableTitle}"/>
</caption>
<html:hidden property="id"/>
<html:hidden property="session"/>
<html:hidden property="oldManager"/>
<html:hidden property="method"/>
<c:if test="${newUser eq true}">
    <html:hidden property="newUser" value="true"/>
</c:if>
<colgroup>
    <col class="col_1">
</colgroup>
<tr>
    <th><label for="login">
        <I18n:message key="LOGIN"/>
        *</label></th>
    <td>
        <html:text styleId="login" property="login" size="80" alt=">0, login" maxlength="100"/>
    </td>
</tr>
<tr>
    <th><label>
        <I18n:message key="USER_NAME"/>
        *</label></th>
    <td>
        <html:text styleId="name" property="name" size="80" alt=">0" maxlength="200"/>
        <script type="text/javascript">document.getElementById("name").setAttribute("autocomplete", "off");</script>
    </td>
</tr>
<c:if test="${newUser eq true}">
    <tr>
        <th><label for="password">
            <I18n:message key="PASSWORD"/>${!useLdap ? '*' : ''}
        </label></th>
        <td>
            <table border="0" cellpadding="0" cellspacing="0">
                <tr>
                    <td style="padding-left:0;">
                        <c:if test="${!useLdap}" >
                            <html:password styleId="password" style="margin-left:0; float:left;" property="password"
                                           size="80" alt=">pwd1, ${correctPassword}"
                                           onkeyup="correctPassword(this.id, '${correctPassword}');" maxlength="200"/>
                            <script type="text/javascript">document.getElementById("password").setAttribute("autocomplete", "off");</script>
                        </c:if>
                        <c:if test="${useLdap}" >
                            <html:password styleId="password" style="margin-left:0; float:left;" property="password" size="80" maxlength="200"/>
                        </c:if>
                    </td>
                    <c:if test="${correctPassword}">
                        <td width="150px;">
                            <span id="warn_password_label" style="color:#e0e0e0"><I18n:message
                                    key="PASSWORD_CORRECT"/></span>

                            <div><span style="float: left; background-color:#e0e0e0; width: 50px; height: 4px;"
                                       id="warn_password_simple"></span></div>
                            <div><span style="float: left; background-color:#e0e0e0; width: 50px; height: 4px;"
                                       id="warn_password_normal"></span></div>
                        </td>
                    </c:if>
                </tr>
            </table>
        </td>
    </tr>
    <tr>
        <th><label for="password_confirm">
            <I18n:message key="PASSWORD_CONFIRM"/>${!useLdap ? '*' : ''}
        </label></th>
        <td>
            <c:if test="${!useLdap}" >
                <html:password styleId="password_confirm" property="confirmation" size="80" alt="pwd2" maxlength="200"/>
            </c:if>
            <c:if test="${useLdap}" >
                <html:password styleId="password_confirm" property="confirmation" size="80" maxlength="200"/>
            </c:if>
        </td>
    </tr>
</c:if>
<tr>
    <th>
        <I18n:message key="PRSTATUS"/>
    </th>
    <c:choose>
        <c:when test="${canEditUserStatus}">
            <td>
                <html:select property="prstatus" styleId="statusComboId">
                    <html:options collection="allowedPrstatuses" property="id" labelProperty="name"/>
                </html:select>
            </td>
        </c:when>
        <c:otherwise>
            <td>
                <html:hidden property="prstatus"/>
                <html:img styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/ico.status.gif"/>
                <c:out value="${userPrstatus.name}" escapeXml="true"/>
            </td>
        </c:otherwise>
    </c:choose>
</tr>
<c:if test="${canViewUserCompany}">
    <tr>
        <th>
            <I18n:message key="COMPANY"/>
        </th>
        <td>
            <c:choose>
                <c:when test="${canEditUserCompany}">
                    <html:text property="company" size="80" maxlength="200"/>
                </c:when>
                <c:otherwise>
                    <html:hidden property="company"/>
                    <c:out value="${userCompany}"/>
                </c:otherwise>
            </c:choose>
        </td>
    </tr>
</c:if>
<tr>
    <th><label for="email">
        <I18n:message key="EMAIL"/>
    </label></th>
    <td>
        <c:choose>
            <c:when test="${canEditUserEmail}">
                <!--<html:text styleId="email" alt="email" property="email" size="80" maxlength="200"/>-->
                <html:text styleId="email" alt="elist" property="email" size="80" maxlength="200"/>
            </c:when>
            <c:otherwise>
                <html:hidden property="email"/>
                <c:out value="${userEmail}" escapeXml="false"/>
            </c:otherwise>
        </c:choose>
    </td>
</tr>

<c:if test="${canViewUserPhone}">
    <tr>
        <th><label for="phone">
            <I18n:message key="PHONE"/>
        </label></th>
        <td>
            <c:choose>
                <c:when test="${canEditUserPhone}">
                    <html:text styleId="phone" property="tel" size="80" maxlength="200"/>
                </c:when>
                <c:otherwise>
                    <html:hidden property="tel"/>
                    <c:out value="${userTel}"/>
                </c:otherwise>
            </c:choose>
        </td>

    </tr>
</c:if>
<tr>
    <th>
        <I18n:message key="LOCALE"/>
    </th>
    <td>
        <c:choose>
            <c:when test="${canEditUserLocale}">
                <html:select property="locale">
                    <c:if test="${configDefaultLocale}"></c:if>
                    <html:option value=""><I18n:message key="DEFAULT"/> (${defaultLocale})</html:option>
                    <c:forEach items="${locales}" var="lc">
                        <html:option value="${lc.key}">
                            <c:out value="${lc.value}"/>
                        </html:option>
                    </c:forEach>
                </html:select>
            </c:when>
            <c:otherwise>
                <html:hidden property="locale"/>
                <c:out value="${currentUser.localeAsString}"/>
            </c:otherwise>
        </c:choose>
    </td>
</tr>

<tr>
    <th>
        <I18n:message key="TIME_ZONE"/>
    </th>
    <td>
        <c:choose>
            <c:when test="${canEditUserTimezone}">
                <html:select property="timezone">
                    <html:option value="${defaultTimezone}"><I18n:message key="DEFAULT"/> (${defaultTimezone})</html:option>
                    <c:forEach items="${timezones}" var="tz">

                        <html:option value="${tz.key}"><c:out value="${tz.value}"/></html:option>
                    </c:forEach>
                </html:select>

            </c:when>
            <c:otherwise>
                <html:hidden property="timezone"/>
                <c:out value="${currentUser.timezoneAsString}"/>
            </c:otherwise>
        </c:choose>
    </td>
</tr>
<tr>
    <th>
        <I18n:message key="ACTIVE"/>
    </th>
    <td>
        <c:choose>
            <c:when test="${canEditActive}">
                <html:checkbox property="enabled"/>
            </c:when>
            <c:otherwise>
                <html:checkbox property="enabled" disabled="true"/>
            </c:otherwise>
        </c:choose>
    </td>
</tr>

<c:if test="${canEditUserExpireDate}">
    <tr>
        <th><label for="expire">
            <I18n:message key="EXPIRE_DATE"/>
        </label></th>
        <td class="input">

            <html:text styleId="expire" property="expireDate" alt="date(${datepattern})" size="18" maxlength="40"/>
            &nbsp;
            <html:img src="${contextPath}${ImageServlet}/cssimages/ico.calendar.gif" border="0"
                      altKey="SELECT_DATE" titleKey="SELECT_DATE" styleClass="calendaricon"
                      onclick="return showCalendar('expire', '${pattern}', '24', true);"/>

        </td>
    </tr>
</c:if>
<c:if test="${canEditUserLicensed}">
    <tr>
        <th><label for="licensed">
            <I18n:message key="LICENSED_USERS"/>
        </label></th>
        <td>
            <html:text styleId="licensed" maxlength="7" property="licensedUsers" alt="natural"/>
        </td>
    </tr>
</c:if>
<tr>
    <th><label for="default">
        <I18n:message key="DEFAULT_PROJECT"/>
    </label></th>
    <td>
        <c:choose>
            <c:when test="${canEditDefaultProject}">
                <html:text styleId="default" property="project" size="5"/>
            </c:when>
            <c:otherwise>
                <html:hidden property="project"/>
                <c:out value="${userProject}" escapeXml="false"/>
            </c:otherwise>
        </c:choose>
    </td>
</tr>
<tr>
    <th>
        <I18n:message key="EMAIL_TYPE_DEFAULT"/>
    </th>
    <td>
        <c:choose>
            <c:when test="${canEditUserEmailType}">
                <html:select property="template">
                    <c:forEach items="${templates}" var="t">
                        <html:option value="${t.name}">
                            <c:out value="${t.name}"/>
                        </html:option>
                    </c:forEach>

                </html:select>
            </c:when>
            <c:otherwise>
                <html:hidden property="template"/>
                <c:out value="${currentUser.template}"/>
            </c:otherwise>
        </c:choose>
    </td>
</tr>
</table>

<c:if test="${!empty udfMap}">
    <table class="general" cellpadding="0" cellspacing="0">
        <caption>
            <I18n:message key="CUSTOM_FIELDS"/>
        </caption>
        <colgroup>
            <col class="col_1">
            <col class="col_2">
        </colgroup>
        <c:import url="/jsp/custom/UDFEditTemplateTile.jsp"/>
    </table>
</c:if>

<c:if test="${newUser eq true && canCreateUserAttachments}">
    <c:import url="/jsp/attachments/AttachmentCreateTile.jsp"/>
</c:if>

</div>

<c:if test="${canSaveChanges}">
    <div class="controls">
        <html:submit styleClass="iconized" property="saveButton">
            <I18n:message key="SAVE"/>
        </html:submit>
        <c:if test="${canViewList}">
            <html:submit styleClass="iconized secondary" property="parentButton">
                <I18n:message key="GO_PARENT"/>
            </html:submit>
        </c:if>
        <script type="text/javascript">
            var dontValid = false;
        </script>
        <html:button styleClass="iconized secondary" property="cancelButton"
                     onclick="document.location='${referer}';">
            <I18n:message key="CANCEL"/>
        </html:button>
    </div>
</c:if>

</html:form>
</div>
</div>
</tiles:put>
</tiles:insert>
