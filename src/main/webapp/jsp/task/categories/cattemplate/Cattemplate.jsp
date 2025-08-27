<%@ page buffer="128kb" errorPage="/jsp/Error.jsp" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/tiles-el" prefix="tiles" %>


<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value="${sc.timezone}"/>
<I18n:setBundle basename="language"/>
<tiles:insert page="/jsp/layout/ListLayout.jsp" flush="true">
        <tiles:put name="header" value="/jsp/task/TaskHeader.jsp"/>
    <tiles:put name="customHeader" value="/jsp/task/categories/CategoryHeader.jsp"/>
    <tiles:put name="tabs" value="/jsp/task/categories/CategorySubMenu.jsp"/>
<tiles:put name="main" type="string">    
    <c:import url="/jsp/TinyMCE.jsp"/>
        <html:form method="post" styleId="checkunload" action="/CategoryTemplateAction"
                   onsubmit="return validate(this); ">
            <html:hidden property="method" value="save"/>
            <html:hidden property="id" value="${id}"/>
            <html:hidden property="categoryId" value="${categoryId}"/>
            <html:hidden property="session" value="${session}"/>
            <c:if test="${canEdit}">
                <div class="nblueborder">
                    <div class="ncaption"></div>
                    <div class="indent">
                        <div class="general">
                            <textarea class="mceEditor" rows="22" name="template" cols="70" id="template">
                                <c:out value="${template}" escapeXml="true"/>
                            </textarea>
                        </div>

                        <div class="controls">
                            <input type="submit" class="iconized"
                                   value="<I18n:message key="SAVE"/>"
                                   name="SETCATEGORY">
                        </div>
                    </div>
                </div>
            </c:if>
        </html:form>
    </tiles:put>
</tiles:insert>
