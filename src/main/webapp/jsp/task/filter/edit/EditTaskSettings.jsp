<%@ page buffer="128kb" errorPage="/jsp/Error.jsp" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/tiles-el" prefix="tiles" %>


<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value="${sc.timezone}"/>
<I18n:setBundle basename="language"/>
<tiles:insert page="/jsp/layout/ListLayout.jsp" flush="true">
    <tiles:put name="header" value="/jsp/task/TaskHeader.jsp"/>
    <tiles:put name="customHeader" value="/jsp/task/filter/TaskFilterHeader.jsp"/>
    <tiles:put name="tabs" type="string"/>
    <tiles:put name="main" type="string">

        <div class="blueborder">
        <div class="caption">
            <I18n:message key="TASK_FILTER_TASK_PARAMETERS"/>
        </div>
        <div class="indent">
        <script type="text/javascript">
            var levelArray = new Array();
            <c:out value="${javaScript}" escapeXml="false"/>
        </script>

        <html:form method="post" styleId="checkunload" action="/TaskFilterTaskSettingsAction"
                   onsubmit="return validate(this); ">
            <html:hidden property="method" value="edit"/>
            <html:hidden property="id"/>
            <html:hidden property="filterId"/>
            <html:hidden property="session" value="${session}"/>

            <table class="general" cellpadding="0" cellspacing="0">
                <caption>
                    <I18n:message key="BASE_PROPERTIES"/>
                </caption>
                <tr class="line0">
                    <c:out value="${forNumber}" escapeXml="false"/>
                </tr>
                <tr class="line1">
                    <c:out value="${forName}" escapeXml="false"/>
                </tr>
                <tr class="line0">
                    <c:out value="${forCategory}" escapeXml="false"/>
                </tr>
                <tr class="line1">
                    <c:out value="${forStatus}" escapeXml="false"/>
                </tr>
                <tr class="line0">
                    <c:out value="${forResolution}" escapeXml="false"/>
                </tr>
                <tr class="line1">
                    <c:out value="${forPriority}" escapeXml="false"/>
                </tr>
                <tr class="line1">
                    <c:out value="${forDesc}" escapeXml="false"/>
                </tr>
            </table>

            <br>
            <table class="general" cellpadding="0" cellspacing="0">
                <caption>
                    <I18n:message key="STATE_TRACKING"/>
                </caption>
                <tr class="line0">
                    <c:out value="${forSubmitter}" escapeXml="false"/>
                </tr>
                <tr class="line1">
                    <c:out value="${forSubmitterStatus}" escapeXml="false"/>
                </tr>
                <tr class="line0">
                    <c:out value="${forHandler}" escapeXml="false"/>
                </tr>
                <tr class="line1">
                    <c:out value="${forHandlerStatus}" escapeXml="false"/>
                </tr>
                <tr class="line0">
                    <c:out value="${forSubmitDate}" escapeXml="false"/>
                </tr>
                <tr class="line1">
                    <c:out value="${forUpdatedDate}" escapeXml="false"/>
                </tr>
                <tr class="line0">
                    <c:out value="${forCloseDate}" escapeXml="false"/>
                </tr>
            </table>

            <br>
            <table class="general" cellpadding="0" cellspacing="0">
                <caption>
                    <I18n:message key="TIME_TRACKING"/>
                </caption>
                <tr class="line1">
                    <c:out value="${forDeadline}" escapeXml="false"/>
                </tr>
                <tr class="line0">
                    <c:out value="${forBudget}" escapeXml="false"/>
                </tr>
                <tr class="line1">
                    <c:out value="${forABudget}" escapeXml="false"/>
                </tr>
            </table>
            <br>
            <table class="general" cellpadding="0" cellspacing="0">
                <caption>
                    <I18n:message key="OTHER_FILEDS"/>
                </caption>
                <tr class="line1">
                    <c:out value="${forChild}" escapeXml="false"/>
                </tr>
                <tr class="line0">
                    <c:out value="${forMessage}" escapeXml="false"/>
                </tr>

                <c:forEach var="forUdf" items="${udfCustomizers}" varStatus="status">
                    <tr class="line${status.index mod 2}">
                        <c:out value="${forUdf}" escapeXml="false"/>
                    </tr>
                </c:forEach>

            </table>

            <div class="controls">
                <input type="submit" class="iconized"
                       value="<I18n:message key="SAVE"/>"
                       name="SETFILTER">
                <html:button styleClass="iconized secondary" property="cancelButton"
                             onclick="document.location='${contextPath}/TaskFilterViewAction.do?method=page&id=${id}&filterId=${currentFilter.id}';">
                    <I18n:message key="CANCEL"/>
                </html:button>

            </div>
            </div>
            </div>
        </html:form>
    </tiles:put>
</tiles:insert>
