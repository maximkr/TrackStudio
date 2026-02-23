<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "${contextPath}/strict.dtd">
<%@ page buffer="128kb" errorPage="/jsp/Error.jsp" import="com.trackstudio.action.GeneralAction" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/tiles-el" prefix="tiles" %>
<%@ taglib uri="http://trackstudio.com" prefix="ts" %>
<jsp:useBean id="nowDate" class="java.util.Date"/>
<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value="${sc.timezone}"/>
<I18n:setBundle basename="language"/>
<c:set var="STRING" value="0"/>
<c:set var="FLOAT" value="1"/>
<c:set var="DATE" value="2"/>
<c:set var="LIST" value="3"/>
<c:set var="MULTILIST" value="6"/>
<c:set var="TASK" value="7"/>
<c:set var="USER" value="8"/>
<c:set var="INTEGER" value="4"/>
<c:set var="MEMO" value="5"/>
<c:set var="URL" value="9"/>
<html debug="true">
<head>
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<meta http-equiv="content-type" content="text/html; charset=<c:out value="${charSet}"/>">
<!-- L9: Removed Google Fonts CDN for GDPR and offline support. Inter font will use system fallback -->
<title>${originTitle}</title>

<style type="text/css">
    @media print {
        .controlPanel {display: none;}
        .link {display: none;}
        .navigation {display: none;}
    }
</style>

<script type="text/javascript">
    function stopRKey(evt) {
        var evt = (evt) ? evt : ((event) ? event : null);
        var node = (evt.target) ? evt.target : ((evt.srcElement) ? evt.srcElement : null);
        if ((evt.keyCode == 13) && (node.type=="text"))  {return false;}
    }

    document.onkeypress = stopRKey;

    if ('${useAnchor}' == 'true') {
        window.parent.location.hash = '#${tci.number}';
    }

    //���� ���� ��� ������ ���� ������, �.�. �� ���������� ������� ������
    function readCookie(name) {
        var nameEQ = name + "=";
        var ca = document.cookie.split(';');
        for (var i = 0; i < ca.length; i++) {
            var c = ca[i];
            while (c.charAt(0) == ' ')
                c = c.substring(1, c.length);
            if (c.indexOf(nameEQ) == 0)
                return c.substring(nameEQ.length, c.length);
        }
        return null;
    }
    if (parent === self || !window.top.TS) {
        // Page opened directly (not inside app-shell) — redirect into shell
        var currentUrl = encodeURIComponent(self.location.pathname + self.location.search);
        self.location.replace('${contextPath}/app-shell.html?url=' + currentUrl);
    }
</script>
<c:set var="urlHtml" value="html"/>
<ts:css request="${request}">
    <ts:cssLink link="style_tokens.css"/>
    <ts:cssLink link="style_sort.css"/>
    <ts:cssLink link="style_src.css"/>
    <ts:cssLink link="${urlHtml}/calendar-blue.css"/>
    <ts:cssLink link="${urlHtml}/colorpicker/css/fonts-min.css"/>
    <ts:cssLink link="${urlHtml}/colorpicker/css/container.css"/>
    <ts:cssLink link="${urlHtml}/colorpicker/css/colorpicker.css"/>
    <ts:cssLink link="${urlHtml}/color/css/colorpicker.css"/>
    <ts:cssLink link="${urlHtml}/SyntaxHighlighter/shCore.css"/>
    <ts:cssLink link="${urlHtml}/SyntaxHighlighter/shThemeDefault.css"/>
	<ts:cssLink link="${urlHtml}/jquery/jquery-ui.min.css"/>
	<ts:cssLink link="${urlHtml}/jquery/jquery-ui.structure.min.css"/>
	<ts:cssLink link="${urlHtml}/jquery/jquery-ui.theme.min.css"/>
	<ts:cssLink link="${urlHtml}/lightbox/css/lightbox.css"/>
    <ts:cssLink link="style_components.css"/>
</ts:css>

<link rel="shortcut icon" href="favicon.ico" type="image/x-icon"/>
<link rel="icon" href="${contextPath}${ImageServlet}/TrackStudio/favicon.png" type="image/png"/>
<!--<link rel="stylesheet" href="${contextPath}/style.css"/>-->


<script type="text/javascript">
    var subtask = false;
    var jsAlert = '${jsAlert}';
    var contextPath = "<c:out value="${contextPath}"/>";
    var versionPath = "<c:out value="${versionPath}"/>";
    var decSep = "<c:out value="${decimalSepatator}"/>";

    var NOT_CHOOSEN = "<I18n:message key="NOT_CHOOSEN"/>";

    parent.document.title = "<c:out value="${originTitle}" escapeXml="false"/>";
    document.title = "<c:out value="${originTitle}" escapeXml="false"/>";

    function resetTabPanel(panel) {
        deleteTabCookie(panel, '<c:out value="${contextPath}"/>', null);
    }

    function setTabPanel(panel) {
        setTabCookie(panel, '<c:out value="${contextPath}"/>', null);
    }

    window.onload = function() {
    }

    var FuncContainer = function() {
    }
    var ERROR_PROBLEM_WITH_REQUEST = "<I18n:message key="ERROR_PROBLEM_WITH_REQUEST"/>";
    var ERROR_CANT_CREATE_XMLHTTP = "<I18n:message key="ERROR_CANT_CREATE_XMLHTTP"/>";
    var MOZILLA_PRINCIPAL = "<I18n:message key="MOZILLA_PRINCIPAL"/>";
    var ERROR_INVALID_LINE_LENGTH = "<I18n:message key="ERROR_INVALID_LINE_LENGTH"/>";
    var CHOOSE_PROJECT = "<I18n:message key="CHOOSE_PROJECT"/>";
    var ERROR_EMAIL_INCORRECT = "<I18n:message key="ERROR_EMAIL_INCORRECT"/>";
    var ERROR_INCORRECT_URL = "<I18n:message key="ERROR_INCORRECT_URL"/>";
    var ERROR_FLOAT = "<I18n:message key="ERROR_FLOAT"/>";
    var ERROR_CHOOSE_RADIO = "<I18n:message key="ERROR_CHOOSE_RADIO"/>";
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
    var ERROR_COOKIE_IS_TOO_BIG = "<I18n:message key="ERROR_COOKIE_IS_TOO_BIG"/>";
    var SIMPLE_PASSWORD = "<I18n:message key="SIMPLE_PASSWORD"/>";
    var REBIALE_PASSWORD = "<I18n:message key="REBIALE_PASSWORD"/>";
    var PASSWORD_CORRECT = "<I18n:message key="PASSWORD_CORRECT"/>";
    var ERROR_TOO_MUCH_TEXT = "<I18n:message key="ERROR_TOO_MUCH_TEXT"/>";
    var ERROR_SIMPLE_PASSWORD = "<I18n:message key="ERROR_SIMPLE_PASSWORD"/>";
    var URL_HTML = '${contextPath}${ImageServlet}/${urlHtml}';
    var VERSION_TS = '${versionTS}';
    var runClockWhenOpenTask = '${runClockWhenOpenTask}';
    var locale = 'en';
    if ('${sc.locale}'.indexOf("ru") != -1) locale = "ru";
    var IMAGE_VIEW = '${imageView}';
    var SEARCH_DELAY = '${searchDelay}';
</script>
<!--    -->
<ts:js request="${request}" response="${response}">
	<ts:jsLink link="${urlHtml}/jquery/jquery-4.0.0.min.js"/>
	<ts:jsLink link="${urlHtml}/jquery/jquery-ui.min.js"/>
    <ts:jsLink link="${urlHtml}/jquery/jquery.form.js"/>
    <ts:jsLink link="${urlHtml}/tsmenu/tsmenu.js"/>
    <ts:jsLink link="${urlHtml}/hint-textbox.js"/>
    <ts:jsLink link="${urlHtml}/tsmenu/tsmenu.js"/>
    <ts:jsLink link="${urlHtml}/calendar.js"/>
    <ts:jsLink link="${urlHtml}/calendar-en.js"/>
    <ts:jsLink link="${urlHtml}/calendar-helper.js"/>
    <ts:jsLink link="${urlHtml}/validate.js"/>
    <ts:jsLink link="${urlHtml}/quickSelect.js"/>
    <ts:jsLink link="${urlHtml}/ts-avatar.js"/>
    <ts:jsLink link="${urlHtml}/ts-user-avatar-enhancer.js"/>
    <ts:jsLink link="${urlHtml}/ts-predictor.js"/>
    <ts:jsLink link="${urlHtml}/slidingframe.js"/>
    <ts:jsLink link="${urlHtml}/ts-dialog.js"/>
    <ts:jsLink link="${urlHtml}/colorpicker/js/utilities.js"/>
    <ts:jsLink link="${urlHtml}/colorpicker/js/slider-min.js"/>
    <ts:jsLink link="${urlHtml}/colorpicker/js/colorpicker-min.js"/>
    <ts:jsLink link="${urlHtml}/color/js/colorpicker.js"/>
    <ts:jsLink link="${urlHtml}/passwordCorrect.js"/>
    <ts:jsLink link="${urlHtml}/validatePressKey.js"/>
    <ts:jsLink link="${urlHtml}/search_attachment.js"/>
    <ts:jsLink link="${urlHtml}/setDropdowns.js"/>
    <ts:jsLink link="${urlHtml}/SyntaxHighlighter/shCore.js"/>
    <ts:jsLink link="${urlHtml}/SyntaxHighlighter/shBrushJScript.js"/>
    <ts:jsLink link="${urlHtml}/SyntaxHighlighter/shBrushBash.js"/>
    <ts:jsLink link="${urlHtml}/SyntaxHighlighter/shBrushCpp.js"/>
    <ts:jsLink link="${urlHtml}/SyntaxHighlighter/shBrushCSharp.js"/>
    <ts:jsLink link="${urlHtml}/SyntaxHighlighter/shBrushCss.js"/>
    <ts:jsLink link="${urlHtml}/SyntaxHighlighter/shBrushDelphi.js"/>
    <ts:jsLink link="${urlHtml}/SyntaxHighlighter/shBrushDiff.js"/>
    <ts:jsLink link="${urlHtml}/SyntaxHighlighter/shBrushGroovy.js"/>
    <ts:jsLink link="${urlHtml}/SyntaxHighlighter/shBrushJava.js"/>
    <ts:jsLink link="${urlHtml}/SyntaxHighlighter/shBrushJScript.js"/>
    <ts:jsLink link="${urlHtml}/SyntaxHighlighter/shBrushPhp.js"/>
    <ts:jsLink link="${urlHtml}/SyntaxHighlighter/shBrushPython.js"/>
    <ts:jsLink link="${urlHtml}/SyntaxHighlighter/shBrushRuby.js"/>
    <ts:jsLink link="${urlHtml}/SyntaxHighlighter/shBrushSql.js"/>
    <ts:jsLink link="${urlHtml}/SyntaxHighlighter/shBrushVb.js"/>
    <ts:jsLink link="${urlHtml}/SyntaxHighlighter/shBrushXml.js"/>
	<ts:jsLink link="${urlHtml}/lightbox/js/lightbox.js"/>
</ts:js>


<%--<script type="text/javascript" src="${urlHtml}/lightbox/scriptaculous.js?load=effects"></script>--%>
<%--<!-- -->--%>
<%----%>
<%--<script type="text/javascript" src="/html/validate.js"></script>--%>
<%----%>
<%--<script type="text/javascript" src="<c:out value="${contextPath}${versionPath}"/>/html/tsmenu/tsmenu.js"></script>--%>
<%--<script type="text/javascript" src="<c:out value="${contextPath}${versionPath}"/>/html/calendar.js"></script>--%>
<%--<script type="text/javascript" src="<c:out value="${contextPath}${versionPath}"/>/html/calendar-en.js"></script>--%>
<%--<script type="text/javascript" src="<c:out value="${contextPath}${versionPath}"/>/html/calendar-helper.js"></script>--%>
<%----%>
<%--<script type="text/javascript" src="<c:out value="${contextPath}${versionPath}"/>/html/quickSelect.js"></script>--%>
<%--<script type="text/javascript" src="<c:out value="${contextPath}${versionPath}"/>/html/slidingframe.js"></script>--%>
<%--<script type="text/javascript" src="<c:out value="${contextPath}${versionPath}"/>/html/colorpicker/js/utilities.js"></script>--%>
<%--<script type="text/javascript" src="<c:out value="${contextPath}${versionPath}"/>/html/colorpicker/js/container-min.js"></script>--%>
<%--<script type="text/javascript" src="<c:out value="${contextPath}${versionPath}"/>/html/colorpicker/js/slider-min.js"></script>--%>
<%--<script type="text/javascript" src="<c:out value="${contextPath}${versionPath}"/>/html/colorpicker/js/colorpicker-min.js"></script>--%>
<%----%>
<%--<script type="text/javascript" src="<c:out value="${contextPath}${versionPath}"/>/html/ajaxtags-1.1-beta1.js"></script>--%>
<%--<script type="text/javascript" src="<c:out value="${contextPath}${versionPath}"/>/html/validate.js"></script>--%>
<%--<script type="text/javascript" src="<c:out value="${contextPath}${versionPath}"/>/html/passwordCorrect.js"></script>--%>
<%----%>
<c:if test="${empty noGoogle}">
    <script type="text/javascript" src="https://www.google.com/jsapi"></script>
</c:if>
<script type="text/javascript">
    var tsMenu = new TSMenu();
    tsMenu.width = 320;
</script>
</head>

<body class="yui-skin-sam ts-app" id="dropbox">
<!-- class "yui-skin-sam" set for Yahoo! Color Picker. It determines the scope. -->
<div class="ts-app-shell">
    <tiles:get name="header"/>
    <tiles:get name="customHeader"/>

    <html:messages id="error" message="true" property="msg">
        <table class="error ts-message-error" cellpadding="0" cellspacing="0">
            <caption>
                <I18n:message key="ERROR_OCCURRED"/>
            </caption>
            <tr class="line0">
                <td>
                    <c:out value="${error}"/>
                </td>
            </tr>
        </table>
    </html:messages>

    <div class="ts-main-tabs">
        <tiles:get name="tabs"/>
    </div>
    <div class="ts-main-content">
        <tiles:get name="main"/>
    </div>
</div>
<table class="allrights ts-footer">
    <tr>
        <td>
            <c:out value="${tsVersionDescription}" escapeXml="false"/>&nbsp;<c:out value="${tsBuildDate}" escapeXml="false"/>&nbsp;<I18n:message key="COPYRIGHT"/>
            <br/>
            <c:if test="${!empty willExpire}">
                    <span class="expiration">
                        <I18n:message key="EXPIRATION_DATE"/>
                        <c:out value="${willExpire}" escapeXml="false"/>
                    </span>
            </c:if>
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
            <c:if test="${!empty tsSupportOK}">
                    <span>
                        <c:out value="${tsSupportOK}" escapeXml="false"/>
                    </span>
            </c:if>
        </td>
    </tr>
</table>

<script type="text/javascript">
    <c:if test="${farManagerNotification ne null}">
    TSDialog.confirm("<I18n:message key="MOVE_TO_FAR_MANAGER"/>", function(ok) {
        if (ok) document.location = "<c:out value="${contextPath}"/>/UserListAction.do?method=paste&amp;farManagerAgree=true&amp;id=${currentUser.id}";
    });
    </c:if>
    var frameLoaded = "loaded";
</script>

<script type="text/javascript">
    SyntaxHighlighter.all();

    // Backward-compat namespace (other JSPs reference YAHOO.trackstudio.bookmark.*)
    if (typeof YAHOO === "undefined") { window.YAHOO = {}; }
    if (!YAHOO.trackstudio) { YAHOO.trackstudio = {}; }
    if (!YAHOO.trackstudio.bookmark) { YAHOO.trackstudio.bookmark = {}; }

    // Set localized labels for TSDialog.confirm / TSDialog.alert
    TSDialog.labels.cancel = "<I18n:message key="CANCEL"/>";

    function tsInitUserAvatars() {
        if (window.TSAvatarEnhancer && typeof window.TSAvatarEnhancer.init === 'function') {
            window.TSAvatarEnhancer.init();
        }
    }

    $(function () {
        tsInitUserAvatars();

        var handleSubmit = function() {
            if ($('#bookmark_name').val() != null) {
                this.hide();
                var url = "${contextPath}/BookmarkAction.do";
                var pars = '';
                if ($('#task_id').val() != null) {
                    if ($('#filter_id').val() != null)
                        pars = {method : 'save', name : $('#bookmark_name').val(), taskId: $('#task_id').val(), filterId: $('#filter_id').val()};
                    else
                        pars = {method : 'save', name : $('#bookmark_name').val(), taskId: $('#task_id').val()};
                }
                if ($('#user_id').val() != null) {
                    if ($('#filter_id').val() != null)
                        pars = {method : 'save', name : $('#bookmark_name').val(), userId: $('#user_id').val(), filterId: $('#filter_id').val()};
                    else
                        pars = {method : 'save', name : $('#bookmark_name').val(), userId: $('#user_id').val()};
                }
	            $.ajax(url, {
		            data : pars,
		            method: "post",
		            success: function(data) {
			            self.top.frames[0].updateBookmarks("${contextPath}/bookmark");
		            }
	            });
            } else {
                TSDialog.alert("<I18n:message key="ENTER_BOOKMARK_NAME"/>");
            }
        };

        function checkRegExp() {
            var url = '${contextPath}/MailImportEditAction.do?method=checkRegExp';
            var params = {expression : $('#regexp').val(), text : $('#text').val()};
            $.ajax(url, {
                data : params,
                success: function(data) {
                    $('#resultRegExpText').html(data);
                }
            });
        }

        function submitPostFilterForm() {
            var name = document.getElementById('post_filter_name').value;
            var filterId = document.getElementById('filter_save_as_id').value;
            var url = document.getElementById('post_filter_save_as_form').value;
            var taskId = '${tci.id}';
            var params = {filter : filterId, method : 'changeTaskFilter', name : name, id : taskId, oldfield : '', saveButton : 'true'};

	        $.ajax(url, {
		        data : params,
		        method: "post",
		        success: function(data) {
			        document.location.href = contextPath + "/task/${tci.number}?thisframe=true";
		        }
	        });
            this.hide();
        }

        var handleCancel = function() {
            this.hide();
        };

        var mailRegExpClose = function() {
            document.getElementById("keywords").value = $('#regexp').val();
            this.hide();
        };

        // Instantiate the Dialogs (TSDialog — drop-in for YAHOO.widget.Dialog)
        YAHOO.trackstudio.bookmark.bookmark_dialog = new TSDialog("bookmark_dialog",
                { width : "600px",
                    visible : false,
                    buttons : [
                        { text:"OK", handler:handleSubmit, isDefault:true },
                        { text:"<I18n:message key="CANCEL"/>", handler:handleCancel }
                    ]
                });

        YAHOO.trackstudio.bookmark.view_dialog = new TSDialog("view_dialog",
                { width : "400px",
                    visible : false,
                    buttons : [
                        { text:"OK", handler:handleSubmit, isDefault:true },
                        { text:"<I18n:message key="CANCEL"/>", handler:handleCancel }
                    ]
                });

        YAHOO.trackstudio.bookmark.dialog_test_reg_exp = new TSDialog("dialog_test_reg_exp",
                { width : "400px",
                    visible : false,
                    buttons : [
                        { text:"<I18n:message key="TEST"/>", handler:checkRegExp, isDefault:true },
                        { text:"<I18n:message key="CLOSE"/>", handler:mailRegExpClose }
                    ]
                });

        YAHOO.trackstudio.bookmark.post_filter_save_as = new TSDialog("post_filter_save_as",
                { width : "400px",
                    visible : false,
                    buttons : [
                        { text:"OK", handler:submitPostFilterForm, isDefault:true },
                        { text:"<I18n:message key="CANCEL"/>", handler:handleCancel }
                    ]
                });

        // Render all dialogs
        YAHOO.trackstudio.bookmark.bookmark_dialog.render();
        YAHOO.trackstudio.bookmark.view_dialog.render();
        YAHOO.trackstudio.bookmark.dialog_test_reg_exp.render();
        YAHOO.trackstudio.bookmark.post_filter_save_as.render();
    });
</script>

</body>
</html>
<% request.setAttribute("startTime", null); %>
