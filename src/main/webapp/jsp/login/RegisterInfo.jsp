<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="struts/tiles-el" prefix="tiles" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<I18n:setLocale value="${defaultLocale}"/>
<I18n:setTimeZone value='${sc.timezone}'/>
<I18n:setBundle basename="language"/>
<tiles:insert page="/jsp/layout/LoginLayout.jsp" flush="true">
    <tiles:put name="form" type="string">
        <I18n:message key="REGISTER_SUCCESSFULLY"/>
        <div class="controls">

            <input type="button" name="LGN"
                   onclick="document.location='<c:out value="${contextPath}"/>/LoginAction.do?method=loginPage'"
                   class="iconized" value="<I18n:message key="LOG_IN"/>"/>
        </div>
    </tiles:put>
</tiles:insert>
