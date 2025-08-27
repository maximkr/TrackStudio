<%@ page buffer="128kb" errorPage="/jsp/Error.jsp"%>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<I18n:setLocale value='${sc.locale}'/>
<I18n:setTimeZone value='${sc.timezone}'/>
<I18n:setBundle basename='language'/>
<html:form method="post" action="/TaskDispatchAction.do">
<html:hidden property="method" value="go"/>
<html:hidden property="session" value="${session}"/>
<input type="text" name="key" size="4" onfocus="if (this.value=='#') this.value='';" value="#"/>
<input type="submit" class="iconized" value="<I18n:message key="GO"/>"/>
</html:form>
