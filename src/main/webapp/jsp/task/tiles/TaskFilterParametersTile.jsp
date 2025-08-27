<%@ page buffer="128kb" errorPage="/jsp/Error.jsp" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value="${sc.timezone}"/>
<I18n:setBundle basename="language"/>
<div id="postFiltering">
    <html:form styleId="filterForm" method="POST" action="${action}" onsubmit="return validate(this);">
        <html:hidden property="method" value="changeTaskFilter"/>
        <html:hidden property="session" value="${session}"/>
        <input type="hidden" id="go" name="go">
        <html:hidden property="filter"/>
        <html:hidden property="reportId"/>
        <html:hidden property="id" value="${id}"/>
        <table class="general" cellpadding="0" cellspacing="0">
            <caption>
                <I18n:message key="FILTER_PARAMETERS"/>
            </caption>
            <colgroup>
                <col class="col_1">
                <col class="col_2">
            </colgroup>
            <c:set var="styleCSS" value="true"/>
            <c:forEach items="${v_filter}" var="item">
                <c:choose>
                    <c:when test="${styleCSS}">
                        <c:set var="styleClass" value="#ffffff"/>
                    </c:when>
                    <c:otherwise>
                        <c:set var="styleClass" value="#f2f2f2"/>
                    </c:otherwise>
                </c:choose>
                <tr style="background-color:${styleClass};">
                    <th>
                        <c:out value="${item.key}"/>
                    </th>
                    <td>
                        <c:out value="${item.value}" escapeXml="false"/>
                        <a class="internal" style="display:inline;" href="#" onclick="editParam('${item.key}');">
                            <img alt="<I18n:message key="EDIT"/>" title="<I18n:message key="EDIT"/>" border="0" hspace="0" vspace="0" src="${contextPath}${ImageServlet}/cssimages/ico.edit.gif"/>
                        </a>
                    </td>
                    <td>
                        <div class="internal" style="display:inline;" onclick="this.parentNode.parentNode.remove(); deleteNote('${undoUrl}', '${item.valueSort}', '${id}', '${filterId}');">
                            <img alt="<I18n:message key="DELETE"/>" title="<I18n:message key="DELETE"/>" border="0" hspace="0" vspace="0" src="${contextPath}${ImageServlet}/cssimages/ico.stop.gif"/>
                        </div>
                    </td>
                </tr>
                <c:set var="styleCSS" value="${!styleCSS}"/>
            </c:forEach>

            <c:forEach items="${v_message}" var="item">
                <c:choose>
                    <c:when test="${styleCSS}">
                        <c:set var="styleClass" value="#ffffff"/>
                    </c:when>
                    <c:otherwise>
                        <c:set var="styleClass" value="#f2f2f2"/>
                    </c:otherwise>
                </c:choose>
                <tr style="background-color:${styleClass};">
                    <th><c:out value="${item.key}"/></th>
                    <td>
                        <c:out value="${item.value}" escapeXml="false"/>
                        <a class="internal" style="display:inline;" href="#" onclick="editParam('${item.key}');">
                            <img alt="<I18n:message key="EDIT"/>" title="<I18n:message key="EDIT"/>" border="0" hspace="0" vspace="0" src="${contextPath}${ImageServlet}/cssimages/ico.edit.gif"/>
                        </a>
                    </td>
                    <td>
                    <span class="internal" onclick="this.parentNode.parentNode.remove(); new AjaxForm().submit(document.getElementById('filterForm', 'deleteElement=${item.key}'));">
                        <img alt="<I18n:message key="DELETE"/>" title="<I18n:message key="DELETE"/>" border="0" hspace="0" vspace="0" src="${contextPath}${ImageServlet}/cssimages/ico.stop.gif"/>
                    </span>
                    </td>
                </tr>
                <c:set var="styleCSS" value="${!styleCSS}"/>
            </c:forEach>
            <tr>
                <th>
                    <html:hidden property="oldfield"/>
                    <html:select property="field" styleId="cfield" onchange="loadUdfForm(this.form);">
                        <html:option value="default" key="DEFAULT"/>
                        <optgroup id="generalset" label="<I18n:message key="GENERAL_SETTINGS"/>">
                            <html:option value="search"  key="KEYWORD"/>
                            <html:option value="subtask"  key="SUBTASK"/>
                        </optgroup>

                        <optgroup id="taskset" label="<I18n:message key="TASK_SETTINGS"/>">
                            <html:options collection="taskSet" property="key" labelProperty="value"/>
                        </optgroup>
                        <optgroup id="messageset" label="<I18n:message key="MESSAGE_SETTINGS"/>">
                            <html:options collection="messageSet" property="key" labelProperty="value"/>
                        </optgroup>
                    </html:select>
                </th>
                <td>
                    <table class="noborder" width="100%" id="customizer">
                        <tr>
                            <c:if test="${preFilterForm.field eq 'search'}">
                                <td>
                                    <html:hidden property="_search" value=""/>
                                    <html:text property="search" maxlength="200"/>
                                </td>
                            </c:if>
                            <c:out value="${customizer}" escapeXml="false"/>
                            <c:if test="${preFilterForm.field ne 'default'}">
                                <td style="vertical-align: bottom;text-align: right" width="80%"><input type="button" class="iconized" value="<I18n:message key="SET"/>" name="ADD" onclick="submitPostFilter(false);"></td>
                            </c:if>
                        </tr>
                    </table>
                </td>
            </tr>
        </table>
        <div class="controls">
            <input type="button" class="iconized" value="<I18n:message key="APPLY"/>" onclick="submitPostFilter(true, this.form); return true;">
            <c:if test="${saveAsFilter}">
                <input type="button" class="iconized" value="<I18n:message key="SAVE_FILTER"/>" name="saveButton" onclick="showPostFilterSaveAs('${filterId}', '${action}'); return false;">
            </c:if>
            <input type="submit" class="iconized secondary" value="<I18n:message key="RESET"/>" name="reset">
        </div>
    </html:form>
</div>
