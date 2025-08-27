<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>

<%@ taglib uri="struts/tiles-el" prefix="tiles" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<I18n:setLocale value="${defaultLocale}"/>
<I18n:setTimeZone value='${sc.timezone}'/>
<I18n:setBundle basename="language"/>
<tiles:insert page="/jsp/layout/LoginLayout.jsp" flush="true">
    <tiles:put name="topic" value="${helpTopic}"/>
    <tiles:put name="content" value="${helpContent}"/>
    <tiles:put name="form" type="string">

        <html:form method="POST" action="/LoginAction" onsubmit="return validate(this);">
            <html:hidden property="method" value="forgotPassword"/>
            <table class="login">
                <COLGROUP>
                    <COL class="col_1">
                    <COL class="col_2">
                </COLGROUP>
                <tr>
                    <th>
                        <label for="login"><I18n:message key="LOGIN"/></label>
                    </th>
                    <td>
                         <html:text maxlength="100" size="21" property="login" styleClass="reallyBig" styleId="login" alt=">0"/>
                    </td>
                </tr>
                <tr>
                    <th>
                        <label for="email"><I18n:message key="EMAIL"/></label>
                    </th>
                    <td>
                        <html:text maxlength="100" size="21" property="email" styleClass="reallyBig" styleId="email" alt=">0, email"/>
                    </td>
                </tr>
                <tr>
                    <td></td>
                    <td><input type="submit" class="iconized" value="<I18n:message key="FORGOT_PASSWORD"/>"/></td>
                </tr>
            </table>
        </html:form>

    </tiles:put>
</tiles:insert>