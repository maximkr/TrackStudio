<%@ page buffer="128kb" errorPage="/jsp/Error.jsp" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/tiles-el" prefix="tiles" %>


<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value="${sc.timezone}"/>
<I18n:setBundle basename="language"/>
<tiles:insert page="/jsp/layout/ListLayout.jsp" flush="true">
    <tiles:put name="customHeader" value="/jsp/task/workflow/mstatus/MstatusHeader.jsp"/>
    <tiles:put name="header" value="/jsp/task/TaskHeader.jsp"/>
    <tiles:put name="tabs" value="/jsp/task/workflow/mstatus/MstatusSubMenu.jsp"/>
    <tiles:put name="main" type="string">
        <div class="nblueborder">
            <div class="ncaption"></div>
            <div class="indent">
                <html:form action="/UdfPermissionAction" method="post" styleId="checkunload"
                           onsubmit="return validate(this);">
                    <html:hidden property="method" value="save"/>
                    <html:hidden property="id" value="${id}"/>
                    <html:hidden property="session" value="${session}"/>
                    <html:hidden property="workflowId" value="${flow.id}"/>
                    <html:hidden property="mstatusId" value="${mstatusId}"/>
                    <div class="general">
                        <table class="general" cellpadding="0" cellspacing="0">
                            <colgroup>
                                <col width="40%">
                                <col width="30%">
                                <col width="30%">
                            </colgroup>
                            <tr class="wide">
                                <th>
                                    <I18n:message key="CUSTOM_FIELDS"/>
                                </th>
                                <th>
                                    <I18n:message key="CAN_VIEW"/>
                                </th>
                                <th>
                                    <I18n:message key="CAN_EDIT"/>
                                </th>
                            </tr>
                            <c:forEach items="${udfs}" var="s">
                                <tr>
                                    <th>
                                        <c:out value="${s.name}"/>
                                    </th>
                                    <td>
                                        <span style="text-align: center">
                                            <html:multibox property="view" value="${s.id}" styleId="view${s.id}"
                                                           disabled="${!canEdit}"
                                                           onchange="if (!this.checked) this.form.edit${s.id}.checked=false;"/>
                                        </span>
                                    </td>
                                    <td>
                                        <span style="text-align: center">
                                            <html:multibox property="edit" value="${s.id}" styleId="edit${s.id}"
                                                           disabled="${!canEdit}"
                                                           onchange="if (this.checked) this.form.view${s.id}.checked=true;"/>
                                        </span>
                                    </td>
                                </tr>
                            </c:forEach>
                        </table>
                    </div>
                    <c:if test="${canEdit}">
                        <div class="controls">
                            <input type="submit" class="iconized"
                                   value="<I18n:message key="SAVE"/>"
                                   name="SAVE">
                        </div>
                    </c:if>
                </html:form>
            </div>
        </div>
    </tiles:put>
</tiles:insert>
