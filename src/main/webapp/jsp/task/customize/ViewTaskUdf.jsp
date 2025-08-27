<%@ page buffer="128kb" errorPage="/jsp/Error.jsp"%>

<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/tiles-el" prefix="tiles" %>

<I18n:setLocale value='${sc.locale}'/>
<I18n:setTimeZone value='${sc.timezone}'/>
<I18n:setBundle basename='language'/>
<tiles:insert page="/jsp/layout/ListLayout.jsp" flush="true">
    <tiles:put name="header" value="/jsp/task/TaskHeader.jsp"/>
    <tiles:put name="customHeader" value="/jsp/task/customize/TaskCustomizeHeader.jsp"/>
    <tiles:put name="tabs" type="string"/>

    <tiles:put name="main" type="string">
        <c:if test="${_can_view}">
            <c:if test="${!isValidePermission}">
                <c:set var="counter" value="${0}"/>
                <div class="general">
                    <table class="error" cellpadding="0" cellspacing="0">
                        <caption>
                            <I18n:message key="CATEGORY_INVALID_PERMISSION_OVERVIEW"/>
                        </caption>
                        <c:forEach var="prstatus" items="${exceptionPermission}">
                            <tr class="line<c:out value="${counter mod 2}"/>">
                                <td>
                                    <I18n:message key="CATEGORY_INVALID_EDIT_PERMISSION_CONFLICT">
                                        <I18n:param value="${prstatus.name}"/>
                                    </I18n:message>
                                </td>
                            </tr>
                            <c:set var="counter" value="${counter + 1}"/>
                        </c:forEach>
                    </table>
                </div>
            </c:if>
            <div class="blueborder">
                <div class="caption">
                    <I18n:message key="CUSTOM_FIELD_OVERVIEW_TASK"/>
                </div>

                <div class="controlPanel">
                    <c:if test="${tabEdit.allowed}">
                        <html:link href="${contextPath}/TaskUdfEditAction.do?method=page&amp;id=${id}&amp;udfId=${udfId}&amp;type=${type}">
                            <html:img  styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/ico.edit.gif"/><I18n:message key="CUSTOM_FIELD_PROPERTIES_TASK"/>
                        </html:link>
                    </c:if>
                    <c:if test="${tabPermission.allowed}">
                        <html:link  href="${contextPath}/TaskUdfPermissionAction.do?method=page&amp;id=${id}&amp;udfId=${udfId}">
                            <html:img  styleClass="icon" border="0" src="${contextPath}${ImageServlet}/cssimages/ico.effective.gif"/><I18n:message key="CUSTOM_FIELD_PERMISSIONS_TASK"/>
                        </html:link>
                    </c:if>
                </div>

                <div class="indent">
                    <c:import url="/jsp/custom/CustomViewTile.jsp"/>
                </div>
            </div>
        </c:if>

    </tiles:put>
</tiles:insert>
