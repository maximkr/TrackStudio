<%@ page buffer="128kb" errorPage="/jsp/Error.jsp" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/tiles-el" prefix="tiles" %>
<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value="${sc.timezone}"/>
<I18n:setBundle basename="language"/>
<tiles:insert page="/jsp/layout/ListLayout.jsp" flush="true">
    <tiles:put name="title" value="${title}"/>
    <tiles:put name="header" value="/jsp/task/TaskHeader.jsp"/>
    <tiles:put name="customHeader" value="/jsp/task/mailimport/MailImportHeader.jsp"/>
    <tiles:put name="tabs" type="string"/>
    <tiles:put name="main" type="string">
        <script type="text/javascript">
            function checkRegExp() {
                var url = '${contextPath}/MailImportEditAction.do?method=checkRegExp';
                var params = {expression : $('#keywords').val(), text : $('#text').val()};
                $.ajax(url, {
                    data : params,
                    success: function(data) {
                        $('#resultRegExpText').html(data);
                    }
                });
            }

            function showTestRegExpDialog() {
                $('#regexp').val($("#keywords").val());
                YAHOO.trackstudio.bookmark.dialog_test_reg_exp.show();
            }
        </script>
        <div class="blueborder">
            <div class="caption">
                <I18n:message key="EMAIL_IMPORT_LIST"/>
            </div>
            <div class="indent">
                <html:form method="POST" action="/MailImportEditAction" onsubmit="return validate(this);">
                    <html:hidden property="method" value="save" styleId="mailImportId"/>
                    <html:hidden property="id" value="${id}"/>
                    <html:hidden property="mailImportId" value="${mailImportId}"/>
                    <html:hidden property="session" value="${session}"/>

                    <div class="general">
                        <table class="general" cellpadding="0" cellspacing="0">
                            <colgroup>
                                <col class="col_1">
                                <col class="col_2">
                            </colgroup>
                            <caption><I18n:message key="EMAIL_IMPORT_PROPERTIES"/></caption>
                            <tr>
                                <th><label for="name"><I18n:message key="NAME"/>*</label></th>
                                <td colspan="2">
                                    <html:text styleId="name" property="name" size="50" maxlength="200" alt=">0"/>
                                </td>
                            </tr>
                            <tr>
                                <th><I18n:message key="ACTIVE"/></th>
                                <td colspan="2"><html:checkbox property="active" /></td>
                            </tr>
                            <tr>
                                <th><label for="keywords"><I18n:message key="CONTAINS_KEYWORD"/></label></th>
                                <td colspan="2">
                                    <html:text styleId="keywords" maxlength="200" size="50" property="keywords"/>
                                    <input type="button" value="<I18n:message key="TEST"/>" onclick="showTestRegExpDialog(); return false;">
                                </td>
                            </tr>
                            <tr>
                                <th><I18n:message key="SEARCH_IN"/></th>
                                <td colspan="2">
                                    <html:select styleId="searchin" property="searchIn">
                                        <html:option value="1"><I18n:message key="EMAIL_SUBJECT"/></html:option>
                                        <html:option value="0"><I18n:message key="BODY"/></html:option>
                                        <html:option value="3"><I18n:message key="EMAIL_SUBJECT_BODY"/></html:option>
                                        <html:option value="2"><I18n:message key="EMAIL_HEADER"/></html:option>
                                        <html:option value="4"><I18n:message key="TO_EMAIL"/></html:option>
                                    </html:select>
                                </td>
                            </tr>
                            <tr>
                                <th><label for="order"><I18n:message key="ORDER"/></label></th>
                                <td colspan="2">
                                    <html:text styleId="order" property="order" size="2" maxlength="3" alt="natural"/>
                                </td>
                            </tr>
                            <tr>
                                <th><label for="domain"><I18n:message key="ALLOWED_DOMAINS"/></label></th>
                                <td colspan="2">
                                    <html:text styleId="domain" property="domain" size="50" maxlength="200"/>
                                </td>
                            </tr>
                        </table>
                    </div>

                    <div class="general">
                        <table class="general" cellpadding="0" cellspacing="0">
                            <colgroup>
                                <col class="col_1">
                                <col class="col_2">
                            </colgroup>
                            <caption><I18n:message key="TASK_IMPORT_PROPERTIES"/></caption>
                            <tr>
                                <th><I18n:message key="PARENT_TASK"/></th>
                                <td colspan="2"><span style="white-space: nowrap;">
                <html:img  styleClass="icon" border="0" src="${contextPath}${ImageServlet}/icons/categories/${tci.category.icon}"/>&nbsp;<c:out value="${tci.name}" escapeXml="true"/>
                </span></td>
                            </tr>
                            <tr>
                                <th><I18n:message key="WITH_CATEGORY"/></th>
                                <td colspan="2">
                                    <script type="text/javascript">
                                        function updateMesageType(url) {
                                            $.ajax(url, {
                                                success: function(data) {
                                                    $('#messageTypeDiv').html(data);
                                                }
                                            });
                                        }
                                    </script>
                                    <html:select property="category" styleId="category" onchange="updateMesageType('${contextPath}/GetMstatusByCategoryServlet.do?id=' + $('#category').val());  return false;">
                                        <html:options collection="categoryColl" property="id" labelProperty="name"/>
                                    </html:select>
                                </td>
                            </tr>
                            <tr>
                                <th><I18n:message key="IMPORT_EMAIL_FROM_UNKNOWN_USERS"/></th>
                                <td colspan="2"><html:checkbox property="importUnknown"/></td>
                            </tr>
                        </table>
                    </div>

                    <div class="general">
                        <table class="general" cellpadding="0" cellspacing="0">
                            <colgroup>
                                <col class="col_1">
                                <col class="col_2">
                            </colgroup>
                            <caption><I18n:message key="MESSAGE_HISTORY_IMPORT_PROPERTIES"/></caption>
                            <tr>
                                <th><I18n:message key="IMPORT_COMMENTS_AS"/></th>
                                <td colspan="2">
                                    <div id="messageTypeDiv">
                                        <c:import url="/jsp/task/mailimport/MailImportMessageType.jsp"/>
                                    </div>
                                </td>
                            </tr>
                        </table>
                    </div>

                    <div class="controls">
                        <input type="submit" class="iconized"
                               value="<I18n:message key="SAVE"/>"
                               name="SAVE">
                        <html:button styleClass="iconized secondary" property="cancelButton"
                                     onclick="document.location='${contextPath}/MailImportAction.do?method=page&id=${id}';">
                            <I18n:message key="CANCEL"/>
                        </html:button>
                    </div>

                </html:form>
            </div>
        </div>

        <div id="dialog_test_reg_exp" style="visibility:hidden;">
            <div class="hd"><I18n:message key="TEST"/></div>
            <div class="bd">
                <I18n:message key="CONTAINS_KEYWORD"/><input type="text" id="regexp" size="50">
                <I18n:message key="TESTED_TEXT"/><input type="text" id="text" size="50"><br/>
                <I18n:message key="RESULT"/><div id="resultRegExpText"></div>
            </div>
        </div>
    </tiles:put>
</tiles:insert>
