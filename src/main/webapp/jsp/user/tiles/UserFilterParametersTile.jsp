<%@ page buffer="128kb" errorPage="/jsp/Error.jsp" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value="${sc.timezone}"/>
<I18n:setBundle basename="language"/>
<div>
    <html:form styleId="filterForm" method="POST" action="${action}" onsubmit="return validate(this);">
        <html:hidden property="method" value="changeFilter"/>
        <html:hidden property="session" value="${session}"/>
        <html:hidden property="filter"/>
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
            <c:forEach items="${filter}" var="item">
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
                    </td>
                    <td>
                    <span class="internal" onclick="this.parentNode.parentNode.remove(); new AjaxForm().submit(document.getElementById('filterForm'), 'deleteElement=${item.valueSort}');">
                        <img alt="<I18n:message key="DELETE"/>" title="<I18n:message key="DELETE"/>" border="0" hspace="0" vspace="0" src="${contextPath}${ImageServlet}/cssimages/ico.stop.gif"/>
                    </span>
                    </td>
                </tr>
                <c:set var="styleCSS" value="${!styleCSS}"/>
            </c:forEach>
            <c:if test="${canManageUserPrivateFilters}">
                <tr>
                    <th>
                        <html:hidden property="oldfield"/>
                        <html:select property="field" styleId="cfield" onchange="document.getElementById('customizer').style.visibility = 'hidden';this.form['method'].value = 'changeField';new AjaxForm().submit(this.form);">
                            <html:option value="default" key="TERM_DEFAULT_SETTINGS"/>
                            <optgroup id="taskset" label="<I18n:message key="USER_PROPERTIES"/>">
                                <html:options collection="userSet" property="key" labelProperty="value"/>
                            </optgroup>
                        </html:select>
                    </th>
                    <td>
                        <table class="noborder" width="100%"  id="customizer">
                            <tr>
                                <c:out value="${customizer}" escapeXml="false"/>
                                <c:if test="${preFilterForm.field ne 'default'}">
                                    <td style="vertical-align: bottom;text-align: right" width="80%"><input type="button" class="iconized" value="<I18n:message key="SET"/>" name="ADD" onclick="if (validate(document.getElementById('filterForm'))){new AjaxForm().submit(document.getElementById('filterForm'));}"></td>
                                </c:if>
                            </tr>
                        </table>
                    </td>
                </tr>
            </c:if>
        </table>
        <div class="controls">
            <input type="submit" class="iconized" value="<I18n:message key="APPLY"/>" name="go">
            <input type="submit" class="iconized secondary" value="<I18n:message key="RESET"/>" name="reset">
            <input type="submit" class="iconized" value="<I18n:message key="SAVE_FILTER"/>" name="saveButton">
        </div>
    </html:form>
</div>
