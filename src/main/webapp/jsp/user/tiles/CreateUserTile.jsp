<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/tiles-el" prefix="tiles" %>
<I18n:setLocale value='${sc.locale}'/>
<I18n:setTimeZone value='${sc.timezone}'/>
<I18n:setBundle basename='language'/>
<c:if test="${canCreateUser}">
    <div class="general">
        <c:choose>
            <c:when test="${empty prstatusSet}">
                <I18n:message key="NO_AVAIL_PRSTATUS_FOR_USER_CREATION"/>
            </c:when>
            <c:otherwise>
                
                <html:form method="POST" action="/UserCreateAction.do" onsubmit="return validate(this);">
                    <html:hidden property="method" value="create"/>
                    <html:hidden property="session" value="${sc.session}"/>
                    <html:hidden property="newUser" value="true"/>
                    <div class="general">
<table class="general" cellpadding="0" cellspacing="0">
                            <caption><I18n:message key="USER_CREATION"/></caption>
                            <COLGROUP>
                                <COL class="col_1">
                                <COL class="col_2">
                            </COLGROUP>
                            <tr>
                                <th>
                                    <I18n:message key="PRSTATUS"/>
                                </th>
                                <td>
                                    <select name="prstatus" alt="mustChoose(<I18n:message key="MSG_CHOOSE_PRSTATUS"/>)">
                                        <option selected value="<I18n:message key="NOT_CHOOSEN"/>"><I18n:message
                                                key="CHOOSE_ONE"/></option>
                                        <c:forEach var="prstatus" items="${prstatusSet}">
                                            <option value=<c:out value="${prstatus.id}"/>><c:out
                                                    value="${prstatus.name}" escapeXml="true"/></option>
                                        </c:forEach>
                                    </select>
                                </td>
                            </tr>
                            <tr>
                                <th><I18n:message key="LOGIN"/></th>
                                <td>
                                    <html:text property="login" value="" size="50" maxlength="100" alt=">0, login"/>
                                </td>
                            </tr>
                            <tr>
                                <th><I18n:message key="USER_NAME"/></th>
                                <td>
                                    <html:text property="name" value="" size="50" alt=">0" maxlength='200'/>
                                    <html:hidden property="id" value="${id}"/>
                                </td>
                            </tr>
                        </table>
                    </div>

                    <div class="controls">
                        <input type="submit" class="iconized"
                               value="<I18n:message key="USER_ADD"/>"
                               name="NEW">
                    </div>
                </html:form>

            </c:otherwise>
        </c:choose>
    </div>
</c:if>
