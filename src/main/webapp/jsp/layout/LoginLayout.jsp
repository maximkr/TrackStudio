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
    <c:set var="urlHtml" value="html"/>
    <ts:css request="${request}">
        <ts:cssLink link="style_src.css"/>
    </ts:css>

    <link rel="shortcut icon" href="favicon.ico" type="image/x-icon" />
    <link rel="icon" href="${contextPath}${ImageServlet}/TrackStudio/favicon.png" type="image/png" />

    <ts:js request="${request}" response="${response}">
        <ts:jsLink link="${urlHtml}/validate.js"/>
    </ts:js>
    <script type="text/javascript">
        if (top.location.pathname.indexOf('staticframeset.html') != -1)
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
        body {
            margin: 0px 0px 0px 0px;
            padding: 0px 0px 0px 0px;
            text-align: center;

        /* part 1 of 2 centering hack */

        }
        #content {
            width: 400px;
            padding: 10px;
            margin-top: 20px;
            margin-bottom: 20px;
            margin-right: auto;
            margin-left: auto;
        /* opera does not like 'margin:20px auto' */
            text-align:left;
        /* part 2 of 2 centering hack */
            width: 400px; /* ie5win fudge begins */
            voice-family: "\"}\"";
            voice-family:inherit;
            width: 410px;
        }
        html>body #content {
            width: 410px; /* ie5win fudge ends */
        }



    </style>

</head>

<body>
<div id="content">
    <table class="centered">
        <tr>
            <td>
                <div class="box">

                    <html:img hspace="0" vspace="0" src="${contextPath}${ImageServlet}/cssimages/TrackStudio-Logo.png"/>

                    <div class="blueborder">
                        <div class="indent">
                            <html:messages id="error" message="true" property="msg">
                            <span style="color: red;">
                                <c:out value="${error}" escapeXml="false"/>
                            </span>
                            </html:messages>
                            <tiles:get name="form"/>
                        </div>
                    </div>
                </div>
            </td>
        </tr>
    </table>
</div>
<table class="allrights" style="bottom: 0">
    <tr>
        <td>
            <c:out value="${tsVersionDescription}" escapeXml="false"/>&nbsp;<c:out value="${tsBuildDate}" escapeXml="false"/>&nbsp;<I18n:message key="COPYRIGHT"/>
            <br/>
            <c:if test="${!empty tsSupportExpired}">
                    <span class="expired">
                        <c:out value="${tsSupportExpired}" escapeXml="false"/>
                    </span>
            </c:if>
            <c:if test="${!empty tsSupportExpires}">
                    <span class="expired">
                        <I18n:message key="SUPPORT_EXPIRATION_DATE">
                            <I18n:param value="${tsSupportExpires}"/>
                        </I18n:message>
                    </span>
            </c:if>
        </td>

    </tr>
</table>
</body>
</html>
