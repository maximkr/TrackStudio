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
    <tiles:put name="customHeader" type="string"/>
        <tiles:put name="tabs" type="string"/>
<tiles:put name="main" type="string">
<div class="blueborder">
<div class="caption">
            <html:img src="${contextPath}${ImageServlet}/cssimages/ico.similar.gif" hspace="0" vspace="0" border="0"
                      align="middle"/>
            <I18n:message key="SEARCH"/>
            :
            <I18n:message key="SIMILAR"/>

</div>
<div class="indent">
    <table class="general" cellpadding=4 cellspacing=1 border=0>
    
    <tr class="wide">

        <th><I18n:message key="TASK"/></th>
    </tr>
    <c:forEach var="item" items="${tasks}">
    <tr>

        <td>
        <dt>
                                <span class="itempath">
                                <html:link styleClass="internal"
                                        href="${contextPath}/TaskViewAction.do?method=page&amp;id=${item.task.id}">
                                    <c:forEach var="path" items="${item.task.ancestors}">
                                        <span class="separated"><c:out value="${path.name}"/></span>&nbsp;/
                                    </c:forEach>
                                </html:link>
                                </span><br>
                                <span class="itemname">
                                    <html:link styleClass="internal"
                                            href="${contextPath}/TaskViewAction.do?method=page&amp;id=${item.task.id}">
                                        <html:img styleClass="icon" border="0"
                                                  src="${contextPath}${ImageServlet}/icons/categories/${item.task.category.icon}"/>
                                        <c:out value="${item.task.name}"/>&nbsp;[#${item.task.number}]
                                    </html:link>
                                </span>

                            </dt>
                            <dd>
                                <c:out value="${item.surroundText}" escapeXml="false"/>
                            </dd>
        </td>
    </tr>
    </c:forEach>


</table>
<c:out value="${slider}" escapeXml="false"/>
</div>
</div>
</tiles:put>
</tiles:insert>
