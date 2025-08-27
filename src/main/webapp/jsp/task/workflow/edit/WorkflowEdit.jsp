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
    <tiles:put name="customHeader" value="/jsp/task/workflow/WorkflowHeader.jsp"/>
    <tiles:put name="tabs" type="string"/>
    <tiles:put name="main" type="string">
        <div class="blueborder">
            <html:form method="post" styleId="checkunload" action="/WorkflowEditAction" onsubmit="return validate(this); ">
            <html:hidden property="method" value="save"/>
            <html:hidden property="id" value="${id}"/>
            <html:hidden property="workflowId" value="${workflowId}"/>
            <html:hidden property="session" value="${session}"/>

            <div class="caption">
                <I18n:message key="PROPERTIES_EDIT"/>
            </div>
            <div class="indent">
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
                            <html:text size="50" maxlength="200" property="name" styleId="name" alt=">0"/>
                        </td>
                    </tr>
                </table>

                <div class="controls">
                    <input type="submit" class="iconized"
                           value="<I18n:message key="SAVE"/>"
                           name="SETCATEGORY">
                    <c:choose>
                        <c:when test="${createNewWorkflow}">
                            <html:button styleClass="iconized secondary" property="cancelButton"
                                         onclick="document.location='${contextPath}/WorkflowAction.do?method=page&id=${id}';">
                                <I18n:message key="CANCEL"/>
                            </html:button>
                        </c:when>
                        <c:otherwise>
                            <html:button styleClass="iconized secondary" property="cancelButton"
                                         onclick="document.location='${contextPath}/WorkflowViewAction.do?method=page&amp;id=${id}&amp;workflowId=${workflowId}';">
                                <I18n:message key="CANCEL"/>
                            </html:button>
                        </c:otherwise>
                    </c:choose>
                </div>
                </html:form>
            </div>
        </div>
    </tiles:put>
</tiles:insert>
