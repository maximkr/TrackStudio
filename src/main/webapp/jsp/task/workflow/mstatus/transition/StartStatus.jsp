<%@ page buffer="128kb" errorPage="/jsp/Error.jsp" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/tiles-el" prefix="tiles" %>
<div id="ajaxStartSelect">
    <select id="start" size="6" multiple="true" class="monospaced fixedwidth" style="width:auto;">
        <c:forEach var="stateSelect" items="${startStateList}">
            <option value="${stateSelect.id}">
                <c:out value="${stateSelect.name}" escapeXml="true"/>
            </option>
        </c:forEach>
    </select>
</div>