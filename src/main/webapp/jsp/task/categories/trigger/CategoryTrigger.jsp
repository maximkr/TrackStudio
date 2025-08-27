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

<html:form action="/CategoryTriggerAction" method="post" styleId="checkunload" onsubmit="return validate(this);">
<html:hidden property="method" value="save"/>
<html:hidden property="id" value="${id}"/>
<html:hidden property="session" value="${session}"/>
<html:hidden property="categoryId" value="${categoryId}"/>
<div class="nblueborder">
<div class="ncaption"></div>
<div class="indent">
<div class="general">
    <table class="general" cellpadding="0" cellspacing="0">
        <caption>
            <I18n:message key="CREATE_TASK_TRIGGERS"/>
        </caption>
        <colgroup>
            <col class="col_1">
            <col class="col_2">
        </colgroup>
        <tr>
            <th>
                <I18n:message key="TRIGGER_BEFORE"/>
            </th>
            <td>
                <c:if test="${!empty beforeScriptCollection}">
                <div class="selectbox">
                      <label class="sel0" for="before_none"><html:radio property="before" value="" styleId="before_none"/><I18n:message key="NONE"/></label>
                          <c:forEach items="${beforeScriptCollection}" var="script" varStatus="c">
                             <label for="before_${c.index}"  class="sel${c.index mod 2}">
                             <html:radio property="before" value="${script.name}" styleId="before_${c.index}"/>
                             <c:out value="${script.name}" escapeXml="true"/>
                             <div class="sel${c.index mod 2}"><c:out value="${script.description}" escapeXml="true"/></div>
                             </label>
                          </c:forEach>
                </div>
                </c:if>
            </td>
        </tr>
        <tr>
            <th>
                <I18n:message key="TRIGGER_INSTEADOF"/>
            </th>
            <td>
                <c:if test="${!empty insteadOfScriptCollection}">
                <div class="selectbox">
                      <label class="sel0" for="insteadOf_none"><html:radio property="insteadOf" value="" styleId="insteadOf_none"/><I18n:message key="NONE"/></label>
                          <c:forEach items="${insteadOfScriptCollection}" var="script" varStatus="c">
                             <label for="insteadOf_${c.index}"  class="sel${c.index mod 2}">
                             <html:radio property="insteadOf" value="${script.name}" styleId="insteadOf_${c.index}"/>
                             <c:out value="${script.name}" escapeXml="true"/>
                             <div class="sel${c.index mod 2}"><c:out value="${script.description}" escapeXml="true"/></div>
                             </label>
                          </c:forEach>
                </div>
                </c:if>
            </td>
        </tr>
        <tr>
            <th>
                <I18n:message key="TRIGGER_AFTER"/>
            </th>
            <td>
                <c:if test="${!empty afterScriptCollection}">
                <div class="selectbox">
                      <label class="sel0" for="after_none"><html:radio property="after" value="" styleId="after_none"/><I18n:message key="NONE"/></label>
                          <c:forEach items="${afterScriptCollection}" var="script" varStatus="c">
                             <label for="after_${c.index}"  class="sel${c.index mod 2}">
                             <html:radio property="after" value="${script.name}" styleId="after_${c.index}"/>
                             <c:out value="${script.name}" escapeXml="true"/>
                             <div class="sel${c.index mod 2}"><c:out value="${script.description}" escapeXml="true"/></div>
                             </label>
                          </c:forEach>
                </div>
                </c:if>
            </td>
        </tr>
    </table>
</div>
<div class="general">
    <table class="general" cellpadding="0" cellspacing="0">
        <caption>
            <I18n:message key="TASK_EDIT_TRIGGERS"/>
        </caption>
        <colgroup>
            <col class="col_1">
            <col class="col_2">
        </colgroup>
        <tr>
            <th>
                <I18n:message key="TRIGGER_BEFORE"/>
            </th>
            <td>
                <c:if test="${!empty updBeforeScriptCollection}">
                <div class="selectbox">
                      <label class="sel0" for="updBefore_none"><html:radio property="updBefore" value="" styleId="updBefore_none"/><I18n:message key="NONE"/></label>
                          <c:forEach items="${updBeforeScriptCollection}" var="script" varStatus="c">
                             <label for="updBefore_${c.index}"  class="sel${c.index mod 2}">
                             <html:radio property="updBefore" value="${script.name}" styleId="updBefore_${c.index}"/>
                             <c:out value="${script.name}" escapeXml="true"/>
                             <div class="sel${c.index mod 2}"><c:out value="${script.description}" escapeXml="true"/></div>
                             </label>
                          </c:forEach>
                </div>
                </c:if>
            </td>
        </tr>
        <tr>
            <th>
                <I18n:message key="TRIGGER_INSTEADOF"/>
            </th>
            <td>
                <c:if test="${!empty updInsteadOfScriptCollection}">
                <div class="selectbox">
                      <label class="sel0" for="updInsteadOf_none"><html:radio property="updInsteadOf" value="" styleId="updInsteadOf_none"/><I18n:message key="NONE"/></label>
                          <c:forEach items="${updInsteadOfScriptCollection}" var="script" varStatus="c">
                             <label for="updInsteadOf_${c.index}"  class="sel${c.index mod 2}">
                             <html:radio property="updInsteadOf" value="${script.name}" styleId="updInsteadOf_${c.index}"/>
                             <c:out value="${script.name}" escapeXml="true"/>
                             <div class="sel${c.index mod 2}"><c:out value="${script.description}" escapeXml="true"/></div>
                             </label>
                          </c:forEach>
                </div>
                </c:if>
            </td>
        </tr>
        <tr>
            <th>
                <I18n:message key="TRIGGER_AFTER"/>
            </th>
            <td>
                <c:if test="${!empty updAfterScriptCollection}">
                <div class="selectbox">
                      <label class="sel0" for="updAfter_none"><html:radio property="updAfter" value="" styleId="updAfter_none"/><I18n:message key="NONE"/></label>
                          <c:forEach items="${updAfterScriptCollection}" var="script" varStatus="c">
                             <label for="updAfter_${c.index}"  class="sel${c.index mod 2}">
                             <html:radio property="updAfter" value="${script.name}" styleId="updAfter_${c.index}"/>
                             <c:out value="${script.name}" escapeXml="true"/>
                             <div class="sel${c.index mod 2}"><c:out value="${script.description}" escapeXml="true"/></div>
                             </label>
                          </c:forEach>
                </div>
               </c:if>
            </td>
        </tr>
    </table>
</div>
<div class="controls">
    <input type="submit" class="iconized"
           value="<I18n:message key="SAVE"/>"
           name="SAVE">
</div>
</div>
</div>
</html:form>
</tiles:put>
</tiles:insert>
