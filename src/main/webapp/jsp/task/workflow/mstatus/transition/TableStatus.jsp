<%@ page buffer="128kb" errorPage="/jsp/Error.jsp" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/tiles-el" prefix="tiles" %>
<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value="${sc.timezone}"/>
<I18n:setBundle basename="language"/>
<div id="ajaxTable">
<c:if test="${!empty transitionList}">
    <table class="general" cellpadding="0" cellspacing="0">
        <caption>
            <c:out value="${tableTitle}"/>
        </caption>
        <tr class="wide">
            <c:if test="${canManage}">
                <th width="1%" nowrap style="white-space:nowrap">
                    <input type="checkbox" onClick="selectAllCheckboxes(this, 'delete1')">
                </th>
            </c:if>
            <th>
                <I18n:message key="START"/>
            </th>
            <th>
                <I18n:message key="FINAL"/>
            </th>
        </tr>
        <c:forEach var="transition" items="${transitionList}" varStatus="varCounter">
            <tr class="line<c:out value="${varCounter.index mod 2}"/>">
                <c:if test="${canManage eq true}">
                    <td>
                                                <span style="text-align: center;">
                                                    <input type="checkbox" name="delete" alt="delete1" value="<c:out value="${transition.id}"/>">
                                                </span>
                    </td>
                </c:if>
                <td>
                                            <span class="nowrap">
                                                <html:img styleClass="state" border="0" style="background-color: ${transition.start.color}" src="${contextPath}${ImageServlet}${transition.start.image}"/>
                                                <c:out value="${transition.start.name}" escapeXml="true"/>
                                            </span>
                </td>
                <td>
                                            <span class="nowrap">
                                               <html:img styleClass="state" border="0" style="background-color: ${transition.finish.color}" src="${contextPath}${ImageServlet}${transition.finish.image}"/>
                                                <c:out value="${transition.finish.name}" escapeXml="true"/>
                                            </span>
                </td>
            </tr>
        </c:forEach>
    </table>
    </div>
    <div class="controls">
    <c:if test="${canManage}">
        <input type="submit" class="iconized"
               value="<I18n:message key="DELETE"/>"
               name="DELETE" onclick="checkDeleteSelected();">
    </c:if>
</c:if>
</div>