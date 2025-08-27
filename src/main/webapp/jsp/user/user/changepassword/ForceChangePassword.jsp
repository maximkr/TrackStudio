<%@ page buffer="128kb" errorPage="/jsp/Error.jsp"%>
<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/tiles-el" prefix="tiles" %>
<I18n:setLocale value='${sc.locale}'/>
<I18n:setTimeZone value='${sc.timezone}'/>
<I18n:setBundle basename='language'/>
<tiles:insert page="/jsp/layout/LoginLayout.jsp" flush="true">
    <tiles:put name="topic" value="${helpTopic}"/>
    <tiles:put name="content" value="${helpContent}"/>
    <tiles:put name="form" type="string">
<html:form method="post" action="/ForceChangePasswordAction" styleId="checkunload" onsubmit="return validate(this);">
	<div class="general">
<table class="general" cellpadding="0" cellspacing="0">
	<caption><c:out value="${tableTitle}"/></caption>
<COLGROUP>
                <COL class="col_1">
                <COL class="col_2">
            </COLGROUP>
<html:hidden property="session" value="${session}"/>
<html:hidden property="method" value="changePassword"/>
<html:hidden property="lastPath"/>
<html:hidden property="id"/>
        <tr>
                <th><I18n:message key="PASSWORD"/></th>
                <td><html:password property="password" size="15" alt=">0, pwd1" maxlength="127"/> </td>
        </tr>
        <tr>
                <th><I18n:message key="PASSWORD_CONFIRM"/></th>
                <td><html:password property="confirmation" size="15" alt=">0, pwd2" maxlength="127" /></td>
        </tr>
        </table>
	</div>
        <div class="controls">
	<html:submit styleClass="iconized" property="setpassword">
				<I18n:message key="PASSWORD_SET"/>
    </html:submit>
        </div>
</html:form>
</tiles:put>
</tiles:insert>
