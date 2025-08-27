<%@ page buffer="128kb" errorPage="/jsp/Error.jsp" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value="${sc.timezone}"/>
<I18n:setBundle basename="language"/>

<div class="yellowbox" id="<c:out value="${param.tileId}"/>" style="display: none;">
    <div class="general">
        <html:form method="POST" action="/TransitionAction" onsubmit="return validate(this);">
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
                    <I18n:message key="TRANSITION_ADD"/>
                </caption>
                <tr>
                    <th>
                        <I18n:message key="START"/>
                        *
                    </th>
                    <td>
                        <select name="value(START-<c:out value="${mstatus.id}"/>)"
                                alt="mustChoose(<I18n:message key="CHOOSE_CATEGORY"/>)">
                            <option value="<I18n:message key="NOT_CHOOSEN"/>">
                                <I18n:message key="CHOOSE_ONE"/>
                            </option>
                            <c:forEach var="status" items="${starts}">
                                <option value="<c:out value="${status.id}"/>">
                                    <c:out value="${status.name}" escapeXml="true"/>
                                </option>
                            </c:forEach>
                        </select>
                    </td>
                </tr>
                <tr>
                    <th>
                        <I18n:message key="FINAL"/>
                        *
                    </th>
                    <td>
                        <select name="value(FINISH-<c:out value="${mstatus.id}"/>)"
                                alt="mustChoose(<I18n:message key="CHOOSE_CATEGORY"/>)">
                            <option value="<I18n:message key="NOT_CHOOSEN"/>">
                                <I18n:message key="CHOOSE_ONE"/>
                            </option>
                            <c:forEach var="status" items="${statesX}">
                                <option value="<c:out value="${status.id}"/>">
                                    <c:out value="${status.name}" escapeXml="true"/>
                                </option>
                            </c:forEach>
                        </select>
                    </td>
                </tr>
            </table>

            <div class="controls">
                <input type="submit" class="iconized" value="<I18n:message key="TRANSITION_ADD"/>" name="NEW">
            </div>
        </html:form>
    </div>
</div>
