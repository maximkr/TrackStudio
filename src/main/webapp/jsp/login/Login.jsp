<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="struts/tiles-el" prefix="tiles" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<I18n:setLocale value="${defaultLocale}"/>
<I18n:setTimeZone value="${sc.timezone}"/>
<I18n:setBundle basename="language"/>
<tiles:insert page="/jsp/layout/LoginLayout.jsp" flush="true">
    <tiles:put name="form" type="string">
        <html:form method="POST" focus="login" action="/LoginAction">
            <c:if test="${!validateDataBasePrimaryKey}">
                <span style="color: red;"><I18n:message key="MISSED_FK"/></span>
            </c:if>
            <div class="sampleLogin">
                <html:hidden property="method" value="login"/>
                <html:hidden property="lastPath"/>
                <c:if test="${activeRootLogin}">
                    <br><a class="internal"
                           href="javascript:document.forms['userForm'].login.value='root';document.forms['userForm'].password.value='root';document.forms['userForm'].submit();">
                    <html:img src="${contextPath}${ImageServlet}/cssimages/ico.root.gif" border="0" align="left"/>
                    <I18n:message key="DEFAULT_LOGIN_AS_ROOT"/>
                </a>
                </c:if>
            </div>
            <table class="login">
                <colgroup>
                    <col class="col_1">
                    <col class="col_2">
                </colgroup>
                <tr>
                    <th><label for="login">
                        <I18n:message key="LOGIN"/>
                    </label></th>
                    <td>
                        <html:text maxlength="100" size="21" property="login" styleClass="reallyBig" styleId="login"
                                   alt=">0"/>
                    </td>
                </tr>
                <tr>
                    <th><label for="password">
                        <I18n:message key="PASSWORD"/>
                    </label></th>
                    <td>
                        <html:password maxlength="1000" size="21" value="" styleClass="reallyBig" property="password"
                                       styleId="password" alt=">0"/>
                    </td>
                </tr>

                <tr>
                    <th>
                        &nbsp;
                    </th>
                    <td>
                        <label class="secondary"><html:checkbox property="rememberMe"/><I18n:message key="REMEMBER_ME"/></label>
                    </td>
                </tr>
                <tr>
                    <th></th>
                    <td class="controls"><input type="submit" class="iconized" value="<I18n:message key="LOG_IN"/>"/>
                        <c:if test="${showRegister}">
                            <input type="button" name="register"
                                   onclick="document.location='${contextPath}/LoginAction.do?method=registerPage'; return true;"
                                   class="iconized" value="<I18n:message key="REGISTER"/>">
                        </c:if>
                        <c:if test="${anonymous}">
                            <input type="button" name="register"
                                   onclick="document.location='${contextPath}/app-shell.html'; return true;"
                                   class="iconized" value="<I18n:message key="ENTER_ANONYMOUS"/>">
                        </c:if>
                        <c:if test="${useX509}">
                            <input type="button" name="register"
                                   onclick="document.location='${contextPath}/LoginAction.do?method=sslcerf'; return true;"
                                   class="iconized" value="<I18n:message key="X_509"/>">
                        </c:if>
                    </td>
                </tr>
                <c:if test="${showForgotPassword}">
                    <tr>
                        <th></th>
                        <td>
                            <html:link styleClass="internal" forward="forgotPasswordPage" style="text-decoration: underline">
                                <I18n:message key="PASSWORD_FORGOT"/>
                            </html:link>
                        </td>
                    </tr>
                </c:if>
            </table>
        </html:form>
    </tiles:put>
</tiles:insert>
