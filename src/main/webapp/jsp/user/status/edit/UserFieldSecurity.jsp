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

<tiles:put name="customHeader" value="/jsp/user/status/StatusHeader.jsp"/>
<tiles:put name="header" value="/jsp/user/UserHeader.jsp"/>
<tiles:put name="tabs" value="/jsp/user/status/StatusSubMenu.jsp"/>
<tiles:put name="main" type="string">
    <c:if test="${canView}">

        <div class="nblueborder">
            <div class="ncaption"></div>
            <div class="indent">
                <html:form method="POST" action="/UserFieldSecurityAction" styleId="checkunload" onsubmit="return validate(this);">
                    <html:hidden property="method" value="save"/>
                    <html:hidden property="session" value="${session}"/>
                    <html:hidden property="prstatusId" value="${prstatusId}"/>
                    <html:hidden property="id" value="${id}"/>
                    <div class="general">
                        <table class="general" cellpadding="0" cellspacing="0">
                            <COLGROUP>
                                <COL width="40%">
                                <COL width="30%">
                                <COL width="30%">
                            </COLGROUP>
                            <tr class="wide">
                                <th>
                                    <I18n:message key="USER_PROPERTIES"/>
                                </th>
                                <th>
                                    <I18n:message key="CAN_VIEW"/>
                                </th>
                                <th>
                                    <I18n:message key="CAN_EDIT"/>
                                </th>
                            </tr>
                            <tr>
                                <th><I18n:message key="PRSTATUS"/></th>
                                <td><center>
                                    <input nsme="dis" type="checkbox" checked disabled>
                                </center></td>
                                <td><center>
                                    <html:checkbox property="editUserStatus" value="on" disabled="${!canEdit}"/>
                                </center></td>
                            </tr>
                            <tr>
                                <th>
                                    <I18n:message key="COMPANY"/>
                                </th>
                                <td>
                                    <center>
                                        <html:checkbox property="viewUserCompany" value="on" disabled="${!canEdit}"
                                                       onchange="if (!this.checked) this.form.editUserCompany.checked=false;"/>
                                    </center>
                                </td>
                                <td>
                                    <center>
                                        <html:checkbox property="editUserCompany" value="on" disabled="${!canEdit}"
                                                       onchange="if (this.checked) this.form.viewUserCompany.checked=true;"/>
                                    </center>
                                </td>
                            </tr>

                            <tr>
                                <th><I18n:message key="EMAIL"/></th>
                                <td><center>
                                    <input nsme="dis" type="checkbox" checked disabled>
                                </center></td>
                                <td><center>
                                    <html:checkbox property="editUserEmail" value="on" disabled="${!canEdit}"/>
                                </center>
                                </td>
                            </tr>
                            <tr>
                                <th>
                                    <I18n:message key="PHONE"/>
                                </th>
                                <td>
                                    <center>
                                        <html:checkbox property="viewUserPhone" value="on"
                                                       onchange="if (!this.checked) this.form.editUserPhone.checked=false;" disabled="${!canEdit}"/>

                                    </center>
                                </td>
                                <td>
                                    <center>
                                        <html:checkbox property="editUserPhone" disabled="${!canEdit}" value="on"
                                                       onchange="if (this.checked) this.form.viewUserPhone.checked=true;"/>
                                    </center>
                                </td>

                            </tr>
                            <tr>
                                <th><I18n:message key="LOCALE"/></th>
                                <td><center>
                                    <input nsme="dis" type="checkbox" checked disabled>
                                </center></td>
                                <td><center>
                                    <html:checkbox property="editUserLocale" disabled="${!canEdit}" value="on"/>
                                </center></td>
                            </tr>
                            <tr>
                                <th><I18n:message key="TIME_ZONE"/></th>
                                <td><center>
                                    <input nsme="dis" type="checkbox" checked disabled>
                                </center></td>
                                <td><center>
                                    <html:checkbox property="editUserTimezone"  disabled="${!canEdit}" value="on"/>
                                </center></td>
                            </tr>
                            <tr>
                                <th><I18n:message key="ACTIVE"/></th>
                                <td><center>
                                    <input nsme="dis" type="checkbox" checked disabled>
                                </center></td>
                                <td><center>
                                    <html:checkbox property="editUserActive" disabled="${!canEdit}" value="on"/>
                                </center></td>
                            </tr>
                            <tr>
                                <th><I18n:message key="EXPIRE_DATE"/></th>
                                <td><center>
                                    <input nsme="dis" type="checkbox" checked disabled>
                                </center></td>
                                <td><center>
                                    <html:checkbox property="editUserExpireDate" disabled="${!canEdit}" value="on"/>
                                </center></td>
                            </tr>
                            <tr>
                                <th><I18n:message key="LICENSED_USERS"/></th>
                                <td><center>
                                    <input nsme="dis" type="checkbox" checked disabled>
                                </center></td>
                                <td><center>
                                    <html:checkbox property="editUserLicensed" disabled="${!canEdit}" value="on"/>
                                </center></td>
                            </tr>
                            <tr>
                                <th><I18n:message key="DEFAULT_PROJECT"/></th>
                                <td><center>
                                    <input nsme="dis" type="checkbox" checked disabled>
                                </center></td>
                                <td><center>
                                    <html:checkbox property="editUserDefaultProject" disabled="${!canEdit}"  value="on"/>
                                </center></td>
                            </tr>
                            <tr>
                                <th><I18n:message key="EMAIL_TYPE_DEFAULT"/></th>
                                <td><center>
                                    <input nsme="dis" type="checkbox" checked disabled>
                                </center></td>
                                <td><center>
                                    <html:checkbox property="editUserEmailType" disabled="${!canEdit}" value="on"/>
                                </center></td>
                            </tr>
                        </table>
                    </div>
                    <br>
                    <c:if test="${canManageUserUDFs}">
                        <div class="general">
                            <table class="general" cellpadding="0" cellspacing="0">
                                <colgroup>
                                    <col width="40%">
                                    <col width="30%">
                                    <col width="30%">
                                </colgroup>
                                <tr class="wide">
                                    <th>
                                        <I18n:message key="CUSTOM_FIELDS"/>
                                    </th>
                                    <th>
                                        <I18n:message key="CAN_VIEW"/>
                                    </th>
                                    <th>
                                        <I18n:message key="CAN_EDIT"/>
                                    </th>
                                </tr>
                                <c:forEach items="${udfs}" var="udf">
                                    <tr>
                                        <th>
                                            <c:out value="${udf.caption}"/>
                                        </th>
                                        <td>
                                            <center>
                                                <html:multibox property="view" value="${udf.id}" disabled="${!canEdit}" onchange="autoUnsetEdit(this);"/>
                                            </center>
                                        </td>
                                        <td>
                                            <center>
                                                <html:multibox property="edit" value="${udf.id}" disabled="${!canEdit}" onchange="autoSetView(this);"/>
                                            </center>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </table>
                        </div>
                    </c:if>
                    <c:if test="${canEdit}">
                        <div class="controls">
                            <input type="SUBMIT" class="iconized" value="<I18n:message key="SAVE"/>" name="SAVE">
                        </div>
                    </c:if>
                </html:form>
            </div>
        </div>
    </c:if>
</tiles:put>
</tiles:insert>
