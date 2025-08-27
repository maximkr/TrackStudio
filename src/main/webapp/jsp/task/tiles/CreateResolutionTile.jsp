<%@ page buffer="128kb" errorPage="/jsp/Error.jsp" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value="${sc.timezone}"/>
<I18n:setBundle basename="language"/>

<c:if test="${canView}">
    <div class="yellowbox" id="<c:out value="${param.tileId}"/>" style="display: none">
        <div class="general">
            <html:form method="POST" action="/ResolutionAction" onsubmit="return validate(this);">
                <html:hidden property="session" value="${session}"/>
                <html:hidden property="method" value="create"/>
                <html:hidden property="id" value="${id}"/>
                <html:hidden property="workflowId" value="${flow.id}"/>
                <html:hidden property="mstatusId" value="${mstatus.id}"/>
                <table class="general" cellpadding="0" cellspacing="0">
                    <colgroup>
                        <col class="col_1">
                        <col class="col_2">
                    </colgroup>
                    <caption>
                        <I18n:message key="RESOLUTION_ADD"/>
                    </caption>
                    <tr>
                        <th>
                            <label for="name"><I18n:message key="NAME"/>*</label>
                        </th>
                        <td>
                            <html:text styleId="name" property="value(addresolution-${mstatus.id})" value="" size="40" maxlength="200" alt=">0"/>
                        </td>
                    </tr>
                    <tr>
                        <th>
                            <I18n:message key="DEFAULT"/>
                        </th>
                        <td>
                            <c:choose>
                                <c:when test="${checked}">
                                    <html:img src="${contextPath}${ImageServlet}/cssimages/ico.checked.gif"/>
                                    <input type="hidden" id='radio<c:out value="${mstatus.id}"/>' class="checkbox"
                                           name="value(defaultresolution-<c:out value="${mstatus.id}"/>)" value="new" checked>
                                </c:when>
                                <c:otherwise>
                                    <input type="checkbox" id='radio<c:out value="${mstatus.id}"/>' class="checkbox"
                                           name="value(defaultresolution-<c:out value="${mstatus.id}"/>)" value="new">
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>

                </table>

                <div class="controls">
                    <input type="submit" class="iconized" value="<I18n:message key="RESOLUTION_ADD"/>" name="NEW">
                </div>
            </html:form>
        </div>
    </div>
</c:if>
