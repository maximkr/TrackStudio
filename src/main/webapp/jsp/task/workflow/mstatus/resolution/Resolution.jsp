<%@ page buffer="128kb" errorPage="/jsp/Error.jsp" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/tiles-el" prefix="tiles" %>


<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value="${sc.timezone}"/>
<I18n:setBundle basename="language"/>
<tiles:insert page="/jsp/layout/ListLayout.jsp" flush="true">
<tiles:put name="customHeader" value="/jsp/task/workflow/mstatus/MstatusHeader.jsp"/>
<tiles:put name="header" value="/jsp/task/TaskHeader.jsp"/>
<tiles:put name="tabs" value="/jsp/task/workflow/mstatus/MstatusSubMenu.jsp"/>
<tiles:put name="main" type="string">
    <div class="nblueborder">
    <div class="ncaption"></div>
        <c:if test="${canManage}">
            <div class="controlPanel">
                <html:link styleClass="external"
                           href="${contextPath}/ResolutionAction.do?method=create&id=${id}&mstatusId=${mstatus.id}&workflowId=${flow.id}">
                    <html:img src="${contextPath}${ImageServlet}/cssimages/ico.edit.gif"
                              hspace="0"
                              vspace="0"
                              border="0"
                              align="middle"/>
                    <I18n:message key="RESOLUTION_ADD"/>
                </html:link>
            </div>
        </c:if>
        <div class="indent">
            <c:choose>
            <c:when test="${!empty resolutionList}">
                <html:form action="/ResolutionAction"
                           method="post"
                           styleId="checkunload"
                           onsubmit="return onSubmitFunction();">
                    <html:hidden property="method" value="save" styleId="resolutionListId"/>
                    <html:hidden property="id" value="${id}"/>
                    <html:hidden property="session" value="${session}"/>
                    <html:hidden property="workflowId" value="${flow.id}"/>
                    <html:hidden property="mstatusId" value="${mstatus.id}"/>
                    <table class="general" cellpadding="0" cellspacing="0">
                        <tr class="wide">
                            <c:if test="${canDelete}">
                                <th width="1%" nowrap style="white-space:nowrap">
                                    <input type="checkbox" onClick="selectAllCheckboxes(this, 'delete1')">
                                </th>
                            </c:if>
                            <th>
                                <I18n:message key="NAME"/>
                            </th>
                            <th>
                                <I18n:message key="DEFAULT"/>
                            </th>
                        </tr>
                        <c:forEach var="resolution" items="${resolutionList}" varStatus="varCounter">
                            <tr class="line<c:out value="${varCounter.index mod 2}"/>">
                                <c:if test="${canDelete}">
                                    <td id="row<c:out value="${varCounter.index}"/>_checkbox">
                                        <span style="text-align: center">
                                            <input type="checkbox"
                                                   name="delete"
                                                   alt="delete1"
                                                   value="<c:out value="${resolution.id}"/>">
                                        </span>
                                    </td>
                                </c:if>
                                <td id="row<c:out value="${varCounter.index}"/>_name">
                                    <c:choose>
                                        <c:when test="${canManage}">
                                            <html:link styleClass="internal"
                                                       href="${contextPath}/ResolutionEditAction.do?method=page&resolution=${resolution.id}&mstatusId=${mstatus.id}&workflowId=${flow.id}&id=${id}">
                                                <img title="<I18n:message key="OBJECT_PROPERTIES_EDIT"/>"
                                                    border="0"
                                                    hspace="0"
                                                    vspace="0"
                                                    src="<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.edit.gif"/>
                                                <c:out value="${resolution.name}" escapeXml="true"/>
                                            </html:link>
                                        </c:when>
                                        <c:otherwise>
                                            <c:out value="${resolution.name}" escapeXml="true"/>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td id="row<c:out value="${varCounter.index}"/>_radio">
                                    <span style="text-align: center">
                                        <html:radio styleId="radio" property="defaultForRadioButton" value="${resolution.id}"/>
                                    </span>                                   
                                </td>
                            </tr>
                        </c:forEach>
                    </table>
                    <div class="controls">
                        <c:if test="${canManage}">
                            <input type="submit"
                                   class="iconized secondary"
                                   value="<I18n:message key="CLONE"/>"
                                   name="CLONE"
                                   onclick="setSubmitForm(true); setMethod('clone');">
                        </c:if>
                        <input type="submit"
                               class="iconized"
                               value="<I18n:message key="SAVE"/>"
                               name="SAVE"
                               onclick="setSubmitForm(true); setMethod('save');">
                        <c:if test="${canDelete}">
                            <input type="submit"
                                   class="iconized secondary"
                                   value="<I18n:message key="DELETE"/>"
                                   name="DELETE"
                                   onclick="checkDeleteSelectedAndDefault();">
                        </c:if>
                        <script type="text/javascript">
                            var submitForm = false;

                            function setSubmitForm(bool) {
                                submitForm = bool;
                            }

                            function onSubmitFunction() {
                                return submitForm;
                            }

                            function checkDeleteSelectedAndDefault() {
                                if (deleteConfirm("<I18n:message key="DELETE_RESOLUTION_REQ"/>", "resolutionForm")) {
                                    setSubmitForm(true);
                                    setMethod('delete');
                                }
                            }

                            function setMethod(target) {
                                document.getElementById('resolutionListId').value = target;
                            }
                        </script>
                    </div>
                </html:form>
            </c:when>

    <c:otherwise>
        <div class="empty"><I18n:message key="EMPTY_RESOLUTIONS_LIST"/></div>
    </c:otherwise>
    </c:choose>
        </div>
    </div>
</tiles:put>
</tiles:insert>


