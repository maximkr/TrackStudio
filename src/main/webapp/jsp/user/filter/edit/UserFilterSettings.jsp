<%@ page buffer="128kb" errorPage="/jsp/Error.jsp"%>
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
    <tiles:put name="customHeader" value="/jsp/user/filter/UserFilterHeader.jsp"/>
    <tiles:put name="header" value="/jsp/user/UserHeader.jsp"/>
    <tiles:put name="tabs" type="string"/>
    
    <tiles:put name="main" type="string">
<c:out value="${javaScript}" escapeXml="false"/>
<div class="blueborder">
        <div class="caption"><I18n:message key="USER_FILTER_USER_PARAMETERS"/></div>
    <div class="indent">
<html:form  method="post" styleId="checkunload" action="/UserFilterSettingsAction" onsubmit="return validate(this); " >
<html:hidden property="method" value="edit"/>
<html:hidden property="id"/>
<html:hidden property="filterId"/>
<html:hidden property="session" value="${session}"/>


<table class="general" cellpadding="0" cellspacing="0">
    <caption><I18n:message key="BASE_PROPERTIES"/></caption>
<tr>
<c:out value="${forLogin}" escapeXml="false"/>
</tr>
<tr>
<c:out value="${forName}" escapeXml="false"/>
</tr>
<tr>
<c:out value="${forSubmitterStatus}" escapeXml="false"/>
</tr>
<tr>
<c:out value="${forActive}" escapeXml="false"/>
</tr>
<tr>
<c:out value="${forExpireDate}" escapeXml="false"/>
</tr>
        </table>

    <br>
<table class="general" cellpadding="0" cellspacing="0">
    <caption><I18n:message key="PERSONAL_DATA"/></caption>


<tr>
<c:out value="${forCompany}" escapeXml="false"/>
</tr>
<tr>
<c:out value="${forEmail}" escapeXml="false"/>
</tr>
<tr>
<c:out value="${forTel}" escapeXml="false"/>
</tr>
<tr>
<c:out value="${forLocale}" escapeXml="false"/>
</tr>
<tr>
<c:out value="${forTimezone}" escapeXml="false"/>
</tr>
    </table>

    <br>
<table class="general" cellpadding="0" cellspacing="0">
    <caption><I18n:message key="OTHER_FILEDS"/></caption>


<tr>
<c:out value="${forChild}" escapeXml="false"/>
</tr>
<tr>
<c:out value="${forChildAllowed}" escapeXml="false"/>
</tr>

<c:forEach var="forUdf" items="${udfCustomizers}" varStatus="varCounter">
<tr class="line<c:out value="${varCounter.index mod 2}"/>">
        <c:out value="${forUdf}" escapeXml="false"/>
    </tr>
</c:forEach>
</table>

<div class="controls">
	<input type="submit"  class="iconized" 
				value="<I18n:message key="SAVE"/>"
                name="SETFILTER">
    <html:button styleClass="iconized secondary" property="cancelButton"
    onclick="document.location='${contextPath}/UserFilterViewAction.do?method=page&id=${id}&filterId=${currentFilter.id}';">
    <I18n:message key="CANCEL"/>
    </html:button>
 </div>
</html:form>
    </div>
            </div>
        
</tiles:put>
</tiles:insert>
