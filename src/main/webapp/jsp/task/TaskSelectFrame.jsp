<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "${contextPath}/strict.dtd">
<%@ page buffer="128kb" errorPage="/jsp/Error.jsp" %>
<%@ page session="false" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="http://trackstudio.com" prefix="ts" %>
<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value="${sc.timezone}"/>
<I18n:setBundle basename="language"/>
<html>
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<script type="text/javascript">
    self.top.currentURL = '<c:out value='${currentURL}' escapeXml='false'/>';

    function resetTabPanel(panel) {
        deleteTabCookie(panel, '<c:out value="${contextPath}"/>', null);
    }
    function setTabPanel(panel) {
        setTabCookie(panel, '<c:out value="${contextPath}"/>', null);
    }

    var taskPath = new Array();
    var taskTab = true;
    <c:out value="${jsTaskPath}" escapeXml="false"/>
</script>
<head>
    <ts:css request="${request}">
        <ts:cssLink link="style_src.css"/>
        <c:set var="urlHtml" value="html"/>
    </ts:css>

    <ts:js request="${request}" response="${response}">
        <ts:jsLink link="${urlHtml}/validate.js"/>
        <ts:jsLink link="${urlHtml}/quickSelect.js"/>
	    <ts:jsLink link="${urlHtml}/jquery/jquery-4.0.0.min.js"/>
	    <ts:jsLink link="${urlHtml}/jquery/jquery-ui.min.js"/>
    </ts:js>
</head>
<body>

<div class="udfCaption">
    <html:img src="${contextPath}${ImageServlet}/cssimages/ico.customfields.gif" hspace="0" vspace="0" border="0"/>
    <c:out value="${currentUDF}" escapeXml="true"/>
</div>

<html:form styleId="selectForm" method="POST" action="/TaskSelectAction" onsubmit="return allow(this);">
    <html:hidden property="session" value="${session}"/>
    <html:hidden property="id" value="${id}"/>
    <html:hidden property="udffield" value="${udffield}"/>
    <html:hidden property="udfvalue" value="${udfvalue}"/>
    <div class="bordered">
        <div class="general">

            <table class="general" cellpadding="0" cellspacing="0">
                <caption>
                    <I18n:message key="SELECTED_TASKS"/>
                </caption>
                <tr class="wide">
                    <th width="1%">
                        <input type="checkbox" onClick="selectAllCheckboxes(this, 'delete')">
                    </th>
                    <th width="99%">
                        <I18n:message key="FULL_PATH"/>
                    </th>
                </tr>
                <c:choose>
                    <c:when test="${!empty selectColl}">

                        <c:forEach var="task" items="${selectColl}" varStatus="varCounter">
                            <tr class="line<c:out value="${varCounter.index mod 2}"/>">
                                <c:choose>
                                    <c:when test="${task.canView}">
                                        <td>
                        <span style="text-align: center">
                        <input id="r<c:out value="${task.id}"/>" type="checkbox" name="delete"
                               value="<c:out value="${task.id}"/>" quickCheckboxSelectGroup="delete">
                        </span>
                                        </td>
                                        <td>
                                            <label for="r<c:out value="${task.id}"/>">
                                                <c:out value="${task.name}" escapeXml="true"/>
                                            </label>
                                        </td>
                                    </c:when>
                                    <c:otherwise>
                                        <td>&nbsp;</td>
                                        <td>
                                            <c:out value="${task.name}" escapeXml="true"/>
                                        </td>
                                    </c:otherwise>
                                </c:choose>
                            </tr>
                        </c:forEach>

                    </c:when>
                    <c:otherwise>
                        <tr>
                            <td colspan=2><span style="text-align: center"><I18n:message
                                    key="NOTHING_SELECTED"/></span></td>
                        </tr>
                    </c:otherwise>
                </c:choose>
            </table>
        </div>
        <div class="controls">
            <input type="submit" class="iconized"
                   value="<I18n:message key="SUBMIT"/>"
                   onclick="parent.parent.opener.<c:out value="${udffield}" escapeXml="true"/>.value='<c:out value="${pack}"/>'; parent.window.close();"
                   name="SUBMIT">
            <input type="submit" class="iconized secondary"
                   value="<I18n:message key="DELETE_FROM_LIST"/>"
                   name="deleteButton">

        </div>
    </div>

</html:form>
<br>

<div class="caption">
    <I18n:message key="TASKS_LIST"/>
</div>
<html:form styleId="selectForm" method="POST" action="/TaskSelectAction">
    <html:hidden property="session" value="${session}"/>
    <html:hidden property="id" value="${id}"/>
    <html:hidden property="udffield" value="${udffield}"/>
    <html:hidden property="udfvalue" value="${udfvalue}"/>

    <span style="float: right"><input type="text" name="goKey" size="20" maxlength='200' style="font-weight: bolder"
                                      value="<I18n:message key="SEARCH_BY_TASK"/>"
                                      onfocus="if (this.value=='<I18n:message key="SEARCH_BY_TASK"/>') this.value='';">
        <input type="submit" class="iconized" value="<I18n:message key="GO"/>"
               name="go"></span>
</html:form>
<br>

<div class="logo">
    <div class="logopath">
        <c:out value="${fullPath4Select}" escapeXml="false"/>
    </div>
    <div class="taskTitle">
       <c:out value="${taskPath4Select}"/>
    </div>
    <ajax:tabPanel
            panelStyleId="${sc.currentSpace}"
            panelStyleClass="labels"
            contentStyleId="yellowbox"
            currentStyleId="activeDropdown"
            baseUrl="${contextPath}/TaskSelectFramesetAction.do?method=page&amp;id=${id}"
            >

    <ajax:tab
            baseUrl="${contextPath}/TaskSelectFilterParametersAction.do?method=page&amp;id=${id}"
            defaultTab="${sc.defaultTab eq 'TaskSelectFilterParametersAction'}"
            >
        <I18n:message key="FILTER_PARAMETERS"/>
    </ajax:tab>
    </ajax:tabPanel>

    <html:form styleId="selectForm" method="POST" action="/TaskSelectAction">
        <html:hidden property="session" value="${session}"/>
        <html:hidden property="id" value="${id}"/>
        <html:hidden property="udffield" value="${udffield}"/>
        <html:hidden property="udfvalue" value="${udfvalue}"/>
    <div class="bordered">
        <div class="general">
            <table class="general" cellpadding="0" cellspacing="0">
                <caption>
                    <I18n:message key="TASKS_LIST"/>
                    <c:out value="${filterName}" escapeXml="true"/>
                    &nbsp;&nbsp;|&nbsp;&nbsp;
                    <I18n:message key="SUBTASKS"/>
                    :&nbsp;
                    <c:out value="${totalChildrenCount}"/>
                    &nbsp;&nbsp;|&nbsp;&nbsp;
                    <I18n:message key="FILTERED_TASKS"/>
                    :&nbsp;
                    <c:out value="${sliderSize}" escapeXml="false"/>
                </caption>
                <tr class="wide">
                    <th width="1%">
                        <input type="checkbox" onClick="selectAllCheckboxes(this, 'adds')">
                    </th>
                    <th width="99%">
                        <I18n:message key="TASK_NAME"/>
                    </th>
                </tr>
                <c:choose>
                    <c:when test="${!empty taskColl}">

                        <c:forEach var="task" items="${taskColl}" varStatus="varCounter">
                            <tr class="line<c:out value="${varCounter.index mod 2}"/>">
                                <td>
                                    <span style="text-align: center;">
                                        <input type="checkbox" name="adds" value="<c:out value="${task.id}"/>"
                                               quickCheckboxSelectGroup="adds">
                                    </span>
                                </td>
                                <td>                                     Ubyp
                                    <c:out value="${task.name}" escapeXml="true"/>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <tr>
                            <td colspan="2"><span style="text-align: center"><I18n:message
                                    key="EMPTY_TASK_LIST"/></span></td>
                        </tr>
                    </c:otherwise>
                </c:choose>

            </table>
        </div>
        <c:out value="${slider}" escapeXml="false"/>
        <div class="controls">
            <input type="submit" class="iconized"
                   value="<I18n:message key="ADD_SELECTED_TASKS"/>"
                   name="addButton">
            <input type="submit" class="iconized"
                   value="<I18n:message key="CURRENT_TASK_ADD"/>"
                   name="addCurrentButton">
        </div>


    </div>
    </html:form>

    <ts:js request="${request}" response="${response}">
        <ts:jsLink link="${urlHtml}/setDropdowns.js"/>
    </ts:js>
</body>
</html>