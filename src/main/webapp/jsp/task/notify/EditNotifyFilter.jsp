<%@ page buffer="128kb" errorPage="/jsp/Error.jsp" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/tiles-el" prefix="tiles" %>
<select name="filterId">
    <c:forEach var="f" items="${filterList}">
        <option ${filterId == f.id ? 'selected' : ''} value="${f.id}"><c:out value="${f.name}"/></option>
    </c:forEach>
</select>
