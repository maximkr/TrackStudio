<%@ page buffer="128kb" errorPage="/jsp/Error.jsp"%>
<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/tiles-el" prefix="tiles" %>


<I18n:setLocale value='${sc.locale}'/>
<I18n:setTimeZone value='${sc.timezone}'/>
<I18n:setBundle basename='language'/>
<tiles:insert page="/jsp/layout/ListLayout.jsp" flush="true">

    <tiles:put name="header" value="/jsp/task/TaskHeader.jsp"/>
    <tiles:put name="customHeader" value="/jsp/task/filter/TaskFilterHeader.jsp"/>
    <tiles:put name="tabs" type="string"/>
    <tiles:put name="main" type="string">

        <div class="blueborder">
            <div class="caption">
                <I18n:message key="FILTER_MESSAGE_PARAMETERS"/>
            </div>
            <div class="indent">
                <script language="JavaScript">
                    var levelArray = new Array();
                    <c:out value="${javaScript}" escapeXml="false"/>
                </script>

                <html:form method="post" styleId="checkunload" action="/TaskFilterMessageSettingsAction" onsubmit="return validate(this); ">
                    <html:hidden property="method" value="edit"/>
                    <html:hidden property="id" value="${id}"/>
                    <html:hidden property="filterId"/>
                    <html:hidden property="session" value="${session}"/>
                    <table class="general" cellpadding="0" cellspacing="0">
                        <tr class="wide">
                            <th><I18n:message key="FILTER"/></th>
                            <th><I18n:message key="CONDITION"/></th>
                        </tr>
                        <tr>
                            <c:out value="${forMsgSubmitter}" escapeXml="false"/>
                        </tr>
                        <tr>
                            <c:out value="${forMsgDate}" escapeXml="false"/>
                        </tr>
                        <tr>
                            <c:out value="${forLastMsgDate}" escapeXml="false"/>
                        </tr>
                        <tr>
                            <c:out value="${forMsgMstatus}" escapeXml="false"/>
                        </tr>
                        <tr>
                            <c:out value="${forMsgHandler}" escapeXml="false"/>
                        </tr>
                        <tr>
                            <c:out value="${forMsgResolution}" escapeXml="false"/>
                        </tr>
                        <tr>
                            <c:out value="${forMsgBudget}" escapeXml="false"/>
                        </tr>
                        <tr>
                            <c:out value="${forMsgText}" escapeXml="false"/>
                        </tr>
                    </table>

                    <div class="controls">
                        <input type="submit"  class="iconized"
                               value="<I18n:message key="SAVE"/>"
                               name="SETFILTER">
                        <html:button styleClass="iconized secondary" property="cancelButton"
                                     onclick="document.location='${contextPath}/TaskFilterViewAction.do?method=page&id=${id}&filterId=${currentFilter.id}';">
                            <I18n:message key="CANCEL"/>
                        </html:button>

                    </div>

                </html:form>
            </div>
        </div>
    </tiles:put>
</tiles:insert>
