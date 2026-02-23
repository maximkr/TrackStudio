<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "${contextPath}/strict.dtd">
<%@ page buffer="128kb" errorPage="/jsp/Error.jsp" %>
<%@ page session="false" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="struts/tiles-el" prefix="tiles" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="http://trackstudio.com" prefix="ts" %>
<I18n:setLocale value="${locale}"/>
<I18n:setBundle basename="language"/>

<html>
<head>
    <title>
        <I18n:message key="POWERED_BY_TRACKSTUDIO"/>
    </title>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta http-equiv="content-type" content="text/html; charset=<c:out value="${charSet}"/>">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <!-- L9: Removed Google Fonts CDN for GDPR and offline support. Inter font will use system fallback -->
    <c:set var="urlHtml" value="html"/>
    <ts:css request="${request}">
        <ts:cssLink link="style_tokens.css"/>
        <ts:cssLink link="style_src.css"/>
        <ts:cssLink link="style_components.css"/>
    </ts:css>

    <link rel="shortcut icon" href="favicon.ico" type="image/x-icon" />
    <link rel="icon" href="${contextPath}${ImageServlet}/TrackStudio/favicon.png" type="image/png" />

    <ts:js request="${request}" response="${response}">
        <ts:jsLink link="${urlHtml}/validate.js"/>
    </ts:js>
    <script type="text/javascript">
        if (top.location.pathname.indexOf('app-shell.html') != -1 || top.location.pathname.indexOf('staticframeset.html') != -1)
            top.location.href = "${contextPath}/LoginAction.do?method=loginPage";

        var ERROR_PROBLEM_WITH_REQUEST = "<I18n:message key="ERROR_PROBLEM_WITH_REQUEST"/>";
        var ERROR_CANT_CREATE_XMLHTTP = "<I18n:message key="ERROR_CANT_CREATE_XMLHTTP"/>";
        var MOZILLA_PRINCIPAL = "<I18n:message key="MOZILLA_PRINCIPAL"/>";
        var ERROR_INVALID_LINE_LENGTH = "<I18n:message key="ERROR_INVALID_LINE_LENGTH"/>";
        var CHOOSE_PROJECT = "<I18n:message key="CHOOSE_PROJECT"/>";
        var ERROR_EMAIL_INCORRECT = "<I18n:message key="ERROR_EMAIL_INCORRECT"/>";
        var ERROR_INCORRECT_URL = "<I18n:message key="ERROR_INCORRECT_URL"/>";
        var ERROR_FLOAT = "<I18n:message key="ERROR_FLOAT"/>";
        var ERROR_INTEGER = "<I18n:message key="ERROR_INTEGER"/>";
        var ERROR_SECONDS = "<I18n:message key="ERROR_SECONDS"/>";
        var ERROR_NATURAL = "<I18n:message key="ERROR_NATURAL"/>";
        var ERROR_LIST_NATURAL = "<I18n:message key="ERROR_LIST_NATURAL"/>";
        var ERROR_INCORRECT_LOGIN = "<I18n:message key="ERROR_INCORRECT_LOGIN"/>";
        var ERROR_INCORRECT_TASK_NUMBER = "<I18n:message key="ERROR_INCORRECT_TASK_NUMBER"/>";
        var ERROR_INCORRECT_TASK = "<I18n:message key="ERROR_INCORRECT_TASK"/>";
        var ERROR_INCORRECT_COLOR = "<I18n:message key="ERROR_INCORRECT_COLOR"/>";
        var ERROR_INCORRECT_DATE = "<I18n:message key="ERROR_INCORRECT_DATE"/>";
        var ERROR_FIELD_LENGTH_1 = "<I18n:message key="ERROR_FIELD_LENGTH_1"/> ";
        var ERROR_FIELD_LENGTH_2 = " <I18n:message key="ERROR_FIELD_LENGTH_2"/> ";
        var ERROR_FIELD_LENGTH_3 = " <I18n:message key="ERROR_FIELD_LENGTH_3"/> ";
        var ERROR_INCORRECT_PASSWORD = "<I18n:message key="ERROR_INCORRECT_PASSWORD"/>";
        var ERRROR_CORRECT_FIELDS = "<I18n:message key="ERRROR_CORRECT_FIELDS"/>";
    </script>
    <style>
        #content {
            width: 100%;
            max-width: 480px;
            margin: 0 auto;
            padding: 24px;
            text-align: left;
        }
    </style>

</head>

<body class="ts-login-page">
<div class="ts-login-shell">
    <div id="content">
        <div class="box ts-login-card">
            <div class="ts-login-brand">
                <div class="ts-brand-logo">
                    <span class="ts-brand-mark">TS</span>
                    <span class="ts-brand-name">TrackStudio</span>
                </div>
                <div class="ts-brand-subtitle">Enterprise</div>
            </div>
            <div class="blueborder">
                <div class="indent">
                    <html:messages id="error" message="true" property="msg">
                        <span class="ts-login-error">
                            <c:out value="${error}"/>
                        </span>
                    </html:messages>
                    <tiles:get name="form"/>
                </div>
            </div>
        </div>
        <div class="ts-login-footer">
            <c:out value="${tsVersionDescription}" escapeXml="false"/>&nbsp;<c:out value="${tsBuildDate}" escapeXml="false"/>&nbsp;<I18n:message key="COPYRIGHT"/>
            <c:if test="${!empty tsSupportExpired}">
                <br/><span class="expired">
                    <c:out value="${tsSupportExpired}" escapeXml="false"/>
                </span>
            </c:if>
            <c:if test="${!empty tsSupportExpires}">
                <br/><span class="expired">
                    <I18n:message key="SUPPORT_EXPIRATION_DATE">
                        <I18n:param value="${tsSupportExpires}"/>
                    </I18n:message>
                </span>
            </c:if>
        </div>
    </div>
</div>
</body>
</html>
