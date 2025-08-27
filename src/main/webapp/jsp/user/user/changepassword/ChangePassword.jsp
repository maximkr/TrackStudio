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
        <c:if test="${changed ne null and changed eq true}">
        <table cellspacing="0" cellpadding="0" class="general">
            <caption>
                <I18n:message key="PASSWORD_WAS_CHANGED"/>
            </caption>
        </table>
        </c:if>
        <div class="blueborder">
            <div class="caption">
                <I18n:message key="CHANGE_PASSWORD"/>
            </div>
            <div class="indent">
                <html:form method="post" action="/ChangePasswordAction" styleId="checkunload" onsubmit="return validate(this);">
                    <table class="general" cellpadding="0" cellspacing="0">
                        <colgroup>
                            <col class="col_1">
                        </colgroup>
                        <html:hidden property="session" value="${session}"/>
                        <html:hidden property="method" value="changePassword"/>
                        <html:hidden property="id"/>
                        <c:if test="${oldPassword}">
                            <c:if test="${changed ne null and changed eq false}">
                                <script type="text/javascript">
                                    alert('<I18n:message key="ERROR_INCORRECT_PASSWORD"/>');
                                </script>
                            </c:if>
                        </c:if>
                        <tr>
                            <th>
                                <label for="password"><I18n:message key="PASSWORD"/><c:if test="${correctPassword}">*</c:if></label>
                            </th>
                            <td width="100px;">
                                <html:password styleId="password" property="password" size="15" alt=">0, pwd1, ${correctPassword}" onkeyup="correctPassword(this.id, '${correctPassword}');" maxlength="127"/>
                            </td>
                            <td>
                                <span id="warn_password_label" style="color:#e0e0e0"><I18n:message key="PASSWORD_CORRECT"/></span>
                                <div>
                                    <span style="float: left; background-color:#e0e0e0; width: 50px; height: 4px;" id="warn_password_simple"></span>
                                </div>
                                <div>
                                    <span style="float: left; background-color:#e0e0e0; width: 50px; height: 4px;" id="warn_password_normal"></span>
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <th>
                                <label for="password_confirm"><I18n:message key="PASSWORD_CONFIRM"/></label>
                            </th>
                            <td colspan="2">
                                <html:password styleId="password_confirm" property="confirmation" size="15" alt=">0, pwd2" maxlength="127"/>
                            </td>
                        </tr>
                    </table>
                    <div class="controls">
                        <html:submit styleClass="iconized" property="setpassword">
                            <I18n:message key="PASSWORD_SET"/>
                        </html:submit>
                        <html:button styleClass="iconized secondary" property="cancelButton"
                                     onclick="document.location='${referer}';">
                            <I18n:message key="CANCEL"/>
                        </html:button>
                    </div>
                </html:form>
            </div>
        </div>
    </tiles:put>
</tiles:insert>
