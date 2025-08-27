<%@ page buffer="128kb" errorPage="/jsp/Error.jsp"%>
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
    <tiles:put name="tabs" value="/jsp/task/workflow/mstatus/MstatusSubMenu.jsp" />
    <tiles:put name="main" type="string">
        <div class="nblueborder">
            <div class="ncaption"></div>
            <div class="indent">
                <html:form action="/MstatusSchedulerAction" method="post" styleId="checkunload" onsubmit="return validate(this);">
                    <html:hidden property="method" value="save"/>
                    <html:hidden property="id" value="${id}"/>
                    <html:hidden property="session" value="${session}"/>
                    <html:hidden property="workflowId" value="${flow.id}"/>
                    <html:hidden property="mstatusId" value="${mstatusId}"/>
                    <div class="general">
                        <table class="general" cellpadding="0" cellspacing="0">
                            <COLGROUP>
                                <COL class="col_1">
                                <COL class="col_2">
                            </COLGROUP>
                            <tr>
                                <th>
                                    <I18n:message key="OBSERVER_BY_SCHEDULER"/>
                                </th>
                                <td>
                                    <html:radio property="scheduler" styleClass="checkbox" value="" styleId="not_use"/>
                                    <label for="not_use">
                                        <I18n:message key="NOT_OBSERVED"/>
                                    </label><br>
                                    <html:radio property="scheduler" styleClass="checkbox" value="C" styleId="observed"/>
                                    <label for="observed">
                                        <I18n:message key="OBSERVER_BY_SCHEDULER"/>
                                    </label><br>
                                    <html:radio property="scheduler" styleClass="checkbox" value="I" styleId="unobserved"/>
                                    <label for="unobserved">
                                        <I18n:message key="UNOBSERVER_BY_SCHEDULER"/>
                                    </label><br>
                                </td>
                            </tr>
                        </table>
                        <div>
                            <div class="blueborder">
                                <div class="caption">
                                    <I18n:message key="CONNECTED_TO"/>
                                </div>
                                <div class="indent">
                                    <c:forEach var="entry" items="${categorySchedulers}">
                                        <html:link styleClass="internal" href="${contextPath}/CategoryViewAction.do?method=page&categoryId=${entry.key.id}&id=${id}">
                                            <html:img styleClass="icon" border="0" src="${contextPath}${ImageServlet}/icons/categories/${entry.key.icon}"/><c:out value="${entry.key.name}" escapeXml="true"/>&nbsp;:&nbsp;[${entry.value.name}&nbsp;(${entry.value.cronTime})]
                                        </html:link>
                                    </c:forEach>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="controls">
                        <input type="submit"  class="iconized"
                               value="<I18n:message key="SAVE"/>"
                               name="SAVE">
                    </div>
                </html:form>
            </div>
        </div>
    </tiles:put>
</tiles:insert>
