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
    <tiles:put name="customHeader" value="/jsp/task/workflow/mstatus/MstatusHeader.jsp"/>
    <tiles:put name="tabs" type="string"/>
    <tiles:put name="main" type="string">
        <div class="blueborder">
            <div class="caption">
                <c:out value="${tableTitle}"/>
            </div>
            <div class="indent">
                <html:form method="POST" action="/ResolutionEditAction" onsubmit="return validate(this);">
                    <html:hidden property="session" value="${session}"/>
                    <html:hidden property="method" value="save"/>
                    <html:hidden property="id" value="${id}"/>
                    <html:hidden property="workflowId" value="${flow.id}"/>
                    <html:hidden property="mstatusId" value="${mstatus.id}"/>
                    <html:hidden property="resolutionId"/>
                    <div class="general">
                        <table class="general" cellpadding="0" cellspacing="0">
                            <colgroup>
                                <col class="col_1">
                                <col class="col_2">
                            </colgroup>
                            <tr>
                                <th>
                                    <label for="name"><I18n:message key="NAME"/>*</label>
                                </th>
                                <td>
                                    <html:text styleId="name" size="20" maxlength="200" property="name" alt=">0"/><span class="sample"><I18n:message key="RESOLUTION_NAME_SAMPLE"/></span>
                                </td>
                            </tr>
                            <tr>
                                <th>
                                    <I18n:message key="DEFAULT"/>
                                </th>
                                <td>
                                    <c:choose>
                                        <c:when test="${checked}">
                                            <html:img src="${contextPath}${ImageServlet}/cssimages/ico.checked.gif"/>
                                            <html:hidden property="def" value="on"/>
                                        </c:when>
                                        <c:otherwise>
                                            <html:checkbox property="def" styleClass="checkbox"/>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>

                        </table>
                    </div>

                    <c:if test="${canManage}">
                        <div class="controls">
                            <input type="submit" class="iconized"
                                   value="<I18n:message key='SAVE'/>"
                                   name="NEW">
                            <html:button styleClass="iconized secondary" property="cancelButton"
                                         onclick="document.location='${contextPath}/ResolutionAction.do?method=page&id=${id}&workflowId=${flow.id}&mstatusId=${mstatus.id}';">
                                <I18n:message key="CANCEL"/>
                            </html:button>
                        </div>
                    </c:if>

                </html:form>
            </div>
        </div>
    </tiles:put>
</tiles:insert>