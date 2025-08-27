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
            <div class="caption">
                <c:out value="${tableTitle}"/>
            </div>
            <div class="indent">
                <html:form method="POST" action="/TaskStatusEditAction" onsubmit="return validate(this);">
                    <html:hidden property="session" value="${session}"/>
                    <html:hidden property="method" value="save"/>
                    <html:hidden property="id" value="${id}"/>
                    <html:hidden property="workflowId" value="${flow.id}"/>
                    <html:hidden property="stateId"/>
                    <div class="general">
                        <table class="general" cellpadding="0" cellspacing="0">
                            <colgroup>
                                <col class="col_1">
                                <col class="col_2">
                            </colgroup>
                            <tr>
                                <c:choose>
                                    <c:when test="${newlist}">
                                        <th>
                                            <label for="name"><I18n:message key="NAME"/>*</label>
                                        </th>
                                        <td>
                                            <html:text styleId="name" property="name" alt=">0"/><span class="sample"><I18n:message key="TASK_STATE_NAME_SAMPLE"/></span>
                                        </td>
                                    </c:when>
                                    <c:otherwise>
                                        <th>
                                            <label for="name"><I18n:message key="LIST_NAMES"/>*</label>
                                        </th>
                                        <td>
                                            <html:textarea styleId="name" property="name" cols="80" rows="5" alt=">0"/><span class="sample"><I18n:message key="TASK_STATE_NAMES_SAMPLE"/></span><br>
                                            <span class="sample"><I18n:message key="USE_CTRL_V"/></span>
                                        </td>
                                    </c:otherwise>
                                </c:choose>
                            </tr>
                            <c:if test="${newlist}">
                                <tr>
                                    <th>
                                        <label for="color"><I18n:message key="COLOR"/></label>
                                    </th>
                                    <td>
                                        <span onclick="startColorPicker('new-state-color')" style="float: left; background-color: ${color}; width: 20px; height: 20px; border: #5e5e4c 1px solid; margin-left: 12px; margin-right: 12px" id="colorbox-new-state-color"></span>
                                        <input alt="color" type="text" value="${color}" name="color" id="value(color-new-state-color)">&nbsp;
                                    </td>
                                </tr>
                                <tr>
                                    <th>
                                        <I18n:message key="START"/>
                                    </th>
                                    <td>
                                        <c:choose>
                                            <c:when test="${checked}">
                                                <html:img src="${contextPath}${ImageServlet}/cssimages/ico.checked.gif"/>
                                                <html:hidden property="start" value="on"/>
                                            </c:when>
                                            <c:otherwise>
                                                <html:checkbox property="start" styleClass="checkbox"/>
                                            </c:otherwise>
                                        </c:choose><span class="sample"><I18n:message key="TASK_STATE_START_SAMPLE"/></span>
                                    </td>
                                </tr>
                                <tr>
                                    <th>
                                        <I18n:message key="FINAL"/>
                                    </th>
                                    <td>
                                        <html:checkbox property="finish" styleClass="checkbox"/><span class="sample"><I18n:message key="TASK_STATE_FINAL_SAMPLE"/></span>
                                    </td>
                                </tr>
                            </c:if>
                        </table>
                    </div>

                    <c:if test="${canManage}">
                        <div class="controls">
                            <input type="submit" class="iconized"
                                   value="<I18n:message key='SAVE'/>"
                                   name="NEW">
                            <html:button styleClass="iconized secondary" property="cancelButton"
                                         onclick="document.location='${contextPath}/TaskStatusAction.do?method=page&id=${id}&workflowId=${flow.id}';">
                                <I18n:message key="CANCEL"/>
                            </html:button>
                        </div>
                    </c:if>
                </html:form>
            </div>
        </div>
    </tiles:put>
</tiles:insert>