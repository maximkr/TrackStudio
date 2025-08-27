<%@ page buffer="128kb" errorPage="/jsp/Error.jsp"%>
<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/tiles-el" prefix="tiles" %>
<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value="${sc.timezone}"/>
<I18n:setBundle basename="language"/>
<c:set var="urlHtml" value="html"/>
<script type="text/javascript" src="<c:out value="${contextPath}${versionPath}"/>/${urlHtml}/tiny_mce/tinymce.min.js"></script>
<script type="text/javascript" src="<c:out value="${contextPath}${versionPath}"/>/${urlHtml}/tiny_mce/ts_init.js"></script>
