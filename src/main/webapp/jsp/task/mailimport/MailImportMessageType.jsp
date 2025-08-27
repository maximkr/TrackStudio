<%@ page buffer="128kb" errorPage="/jsp/Error.jsp" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/tiles-el" prefix="tiles" %>
<div id="messageTypeDiv">
    <select id="messageType" name="messageType">
        <option value="" ${null == importMsgId ? "selected" : ""}>${alwaysCreateNewTask}</option>
        <c:forEach items="${messageTypeColl}" var="m">
            <option value="${m.id}" ${m.id == importMsgId ? "selected" : ""}>${m.name}</option>
        </c:forEach>
    </select>
</div>