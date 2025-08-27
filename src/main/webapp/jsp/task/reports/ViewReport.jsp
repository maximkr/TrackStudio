<%@ page buffer="128kb" errorPage="/jsp/Error.jsp" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/tiles-el" prefix="tiles" %>
<%@ taglib uri="http://ajaxtags.org/tags/ajax" prefix="ajax" %>

<I18n:setLocale value='${sc.locale}'/>
<I18n:setTimeZone value='${sc.timezone}'/>
<I18n:setBundle basename='language'/>
<tiles:insert page="/jsp/layout/ListLayout.jsp" flush="true">
<tiles:put name="customHeader" value="/jsp/task/reports/TaskReportHeader.jsp"/>
<tiles:put name="header" value="/jsp/task/TaskHeader.jsp"/>

<tiles:put name="tabs" type="string"/>
<tiles:put name="main" type="string">
<c:if test="${handler_error != null}">
    <table cellspacing="0" cellpadding="0" class="error">
        <caption>
            <I18n:message key="ERROR_OCCURRED"/>
        </caption>
        <tbody>
        <tr class="line0">
            <td>
                <div class="error"><c:out value="${handler_error}"/></div>
            </td>
        </tr>
        </tbody>
    </table>
</c:if>
<div class="blueborder">
<div class="caption"><I18n:message key="REPORT_VIEW"/></div>
<c:if test="${reportId ne null && canEdit}">
    <div class="controlPanel">
        <html:link
                href="${contextPath}/ReportEditAction.do?method=page&amp;reportId=${reportId}&amp;id=${id}&amp;type=List"><html:img
                src="${contextPath}${ImageServlet}/cssimages/ico.edit.gif" styleClass="icon" border="0"/><I18n:message
                key="REPORT_EDIT"/></html:link>
    </div>
</c:if>
<div class="indent">
    <script type="text/javascript">
        function rebuildReportUrl() {
            var rtype = '${report.type}';
            var loginPassword = "&autologin=${sc.user.login}</br>&autopassword=PASSWORD";
            if ("${sc.user.login}" == "anonymous") {
                loginPassword = "";
            }

            var url = '${siteUrl}';
            if (url.substr(url.length-1, url.length) != '/') {
                url += '/';
            }
            url += 'ReportViewAction.do?method=browse&reportId=${reportId}&id=${id}' + loginPassword;
            var format = document.getElementsByName("format")[0].value;
            url += '&format=' + format;
            if (rtype == 'Tree') {
                    var charset = document.getElementsByName("charset")[0].value;
                    url += "&charset=" + charset;
                    var zipped = document.getElementsByName("zipped")[0].checked;
                    url += "&zipped=" + (zipped ? "true" : "false");
            }
            if (rtype == 'List') {
                    var charset = document.getElementsByName("charset")[0].value;
                    url += "&charset=" + charset;
                    var delimiter = document.getElementsByName("delimiter")[0].value;
                    url += "&delimiter=" + delimiter;
                    var zipped = document.getElementsByName("zipped")[0].checked;
                    url += "&zipped=" + (zipped ? "true" : "false");
            }
            document.getElementById("reportUrlId").href = url;
            document.getElementById("viewURL").innerHTML = url;
        }
    </script>
    <table class="general" cellpadding="0" cellspacing="0">
        <caption><I18n:message key="REPORT_PROPERTIES"/></caption>
        <COLGROUP>
            <COL class="col_1">
            <COL class="col_2">
        </COLGROUP>
        <tr>
            <th><label for="name"><I18n:message key="NAME"/></label></th>
            <td><c:out value="${report.name}"/></td>
        </tr>
        <tr>
            <th>
                <I18n:message key="TYPE"/>
            </th>
            <td>
                <c:out value="${reportType}" escapeXml="true"/>
            </td>
        </tr>
        <tr>
            <th>
                <label for="priv"><pre style="white-space: nowrap;"><I18n:message key="REPORT_SHARE"/></pre></label>
            </th>
            <td>
                <c:choose>
                    <c:when test="${report.priv ne true}">
                        <html:img src="${contextPath}${ImageServlet}/cssimages/ico.checked.gif"/>
                    </c:when>
                    <c:otherwise>
                        <html:img src="${contextPath}${ImageServlet}/cssimages/ico.unchecked.gif"/>
                    </c:otherwise>
                </c:choose>
            </td>
        </tr>
        <tr>
            <th><I18n:message key="OWNER"/></th>
            <td><span class="user" ${report.owner.id eq sc.userId ? "id='loggedUser'" : ""}>
            <html:img styleClass="icon" border="0"
                      src="${contextPath}${ImageServlet}/cssimages/${report.owner.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
            <c:out value="${report.owner.name}" escapeXml="true"/>
			</span>
            </td>
        </tr>
    </table>
</div>

<div class=caption><I18n:message key="GENERATE_REPORT"/></div>

<c:if test="${canManageTaskFilters}">

    <c:if test="${report.type eq 'List'}">
    <ajax:tabPanel
            panelStyleId="${sc.currentSpace}"
            panelStyleClass="controlPanel"
            contentStyleId="yellowbox"
            currentStyleId="selected"
            baseUrl="${contextPath}/ReportViewAction.do?method=page&id=${id}&reportId=${report.id}"
            >
        <ajax:tab
                baseUrl="${contextPath}/ReportFilterParametersAction.do?method=page&id=${id}&reportId=${report.id}"
                defaultTab="${sc.defaultTab eq 'ReportFilterParametersAction'}"
        ><html:img src="${contextPath}${ImageServlet}/cssimages/ico.filterproperties.gif"
                           border="0"/><I18n:message key="FILTER_PARAMETERS"/></ajax:tab>
    </ajax:tabPanel>
    </c:if>

</c:if>
<div class="indent">
    <html:form method="post" styleId="generalReport" action="/ReportViewAction">
        <html:hidden property="id" value="${id}"/>
        <html:hidden property="session" value="${session}"/>
        <html:hidden property="reportId"/>
        <html:hidden property="method" styleId="method" value="changeFilter"/>

        <table class="general" cellpadding="0" cellspacing="0">

            <COLGROUP>
                <COL class="col_1">
                <COL class="col_2">
            </COLGROUP>
            <tr>
                <th><I18n:message key="FORMAT"/></th>
                <td>
                    <html:select styleId="format" property="format" onchange="rebuildReportUrl();"
                                 alt="mustChoose(format)">
                        <c:if test="${report.type eq 'Tree'}">
                            <html:option value="TreeXML">XML Tree</html:option>
                        </c:if>
                        <c:if test="${report.type eq 'List'}">
                            <html:option value="CSV">CSV</html:option>
                        </c:if>
                    </html:select>
                </td>
            </tr>
            <tr id="trForHideId3">
                <th>
                    <I18n:message key="ZIPPED"/>
                </th>
                <td>
                    <input type="checkbox" name="zipped" class="checkbox" onchange="rebuildReportUrl();">
                </td>
            </tr>
            <tr id="trForHideId4">
                <th>
                    <I18n:message key="CHARSET_ENCODING"/>
                </th>
                <td>
                    <html:select property="charset" value="${defCharSet}" onchange="rebuildReportUrl();">
                        <c:forEach var="charset" items="${charsetList}">
                           <html:option value="${charset}"><c:out value="${charset}" escapeXml="false"/></html:option>
                                </c:forEach>
                            </html:select>
                </td>
            </tr>

            <c:if test="${report.type eq 'List'}">
                <tr id="trForHideId5">
                    <th>
                        <I18n:message key="DELIMITER"/>
                    </th>
                    <td>
                        <html:select property="delimiter" value=";" onchange="rebuildReportUrl();">
                            <html:option value=","/>
                            <html:option value=";"/>
                        </html:select>
                    </td>
                </tr>
            </c:if>

            <span></span>
            <tr>
                <th><I18n:message key="LINK"/></th>
                <td>
                    <div id="showLink" style="padding: 5px; border: solid 1px black; display: none; width: 510px;">Link : <div id="viewURL" style="width: 510px">${urlReport}</div><br/><I18n:message key="REPLACE_PASSWORD"/></div>
                    <span style="cursor: pointer;text-decoration:underline; width: 500px;" id="reportUrlId"
                          onclick="if (document.getElementById('showLink').style.display == 'none') { document.getElementById('showLink').style.display = 'block'; } else { document.getElementById('showLink').style.display = 'none'; }  return false;" href="#">link</span>
                </td>
            </tr>
        </table>
        <script type="text/javascript">
            function fixBugChrome() {
                var formAction = document.getElementById('generalReport');
                var name = navigator.userAgent;
                if (name.toLowerCase().indexOf('chrome') != -1) {
                    formAction.action = formAction.action + '?bug_chrome';
                }
            }
            function submitForm() {
                var error = '${handler_error}';
                if (error != '') {
                    alert(error);
                } else {
                    fixBugChrome();
                    document.getElementById('method').value = 'browse';
                    document.getElementById('generalReport').target = '_blank';
                    document.getElementById('generalReport').submit();
                }
            }
            rebuildReportUrl();
        </script>
        <div class="controls">
            <input type="button" class="iconized" value="<I18n:message key="GENERATE_REPORT"/>" name="SUBMIT"
                   onclick="submitForm();"/>
        </div>
    </html:form>
</div>
</div>
</tiles:put>
</tiles:insert>
