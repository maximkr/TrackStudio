<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/tiles-el" prefix="tiles" %>
<c:set var="taskMenu" value="false"/>
<c:set var="userMenu" value="true"/>
<I18n:setLocale value='${sc.locale}'/>
<I18n:setTimeZone value='${sc.timezone}'/>
<I18n:setBundle basename='language'/>
<tiles:insert page="/jsp/layout/ListLayout.jsp" flush="true">
<tiles:put name="customHeader" value="/jsp/user/status/StatusHeader.jsp"/>
<tiles:put name="header" value="/jsp/user/UserHeader.jsp"/>
<tiles:put name="tabs" value="/jsp/user/status/StatusSubMenu.jsp"/>
<tiles:put name="main" type="string">
<c:if test="${canView}">
<div class="nblueborder">
<div class="ncaption"></div>
<html:form method="POST" action="/TaskSecurityAction" styleId="checkunload" onsubmit="return validate(this);">
<html:hidden property="method" value="save"/>
<html:hidden property="session" value="${session}"/>
<html:hidden property="prstatusId" value="${prstatusId}"/>
<html:hidden property="id" value="${id}"/>
<div class="indent">
<div class="general">
<table class="general" cellpadding="0" cellspacing="0">
<COLGROUP>
    <COL class="col_1">
    <COL class="col_2">
</COLGROUP>
<caption>
    <I18n:message key="PRSTATUS_TASK_ACTIONS_PERMISSIONS"/>
</caption>
<tr class="security-tr-border">
    <th><I18n:message key="TASKS"/></th>
    <td>
        <div>
            <html:checkbox property="cutCopyPasteTask" styleId="cutCopyPasteTask"/><label
                for="cutCopyPasteTask"><I18n:message key="Action.cutCopyPasteTask"/></label>
        </div>
        <div>
            <html:checkbox property="bulkProcessingTask" styleId="bulkProcessingTask"/><label
                for="bulkProcessingTask"><I18n:message key="Action.bulkProcessingTask"/></label>
        </div>
        <div>
            <html:checkbox property="deleteOperations" styleId="deleteOperations"/><label
                for="deleteOperations"><I18n:message key="Action.deleteOperations"/></label>
        </div>
    </td>
</tr>
<tr class="security-tr-border">
    <th><I18n:message key="FILTERS"/></th>
    <td>
        <div>
            <div class="security-tree-item" parent="0" nochilds="false" id="parentDivId5">
                <html:checkbox property="viewFilters" styleId="5" onclick="treeset(this);"/><label
                    for="5"><I18n:message key="Action.viewFilters"/></label>
            </div>
            <div class="security-tree-item" parent="5" nochilds="false" id="parentDivId6">
                <img src="${contextPath}${ImageServlet}/cssimages/L.png">
                <html:checkbox property="manageTaskPrivateFilters" styleId="6" onclick="treeset(this);"
                               styleClass="child"/><label for="6"><I18n:message
                    key="Action.manageTaskPrivateFilters"/></label>
            </div>
            <div class="security-tree-item" parent="6" nochilds="true" id="parentDivId7">
                <img src="${contextPath}${ImageServlet}/cssimages/blank.png">
                <img src="${contextPath}${ImageServlet}/cssimages/L.png">
                <html:checkbox property="manageTaskPublicFilters" styleId="7" onclick="treeset(this);"
                               styleClass="child"/><label for="7"><I18n:message
                    key="Action.manageTaskPublicFilters"/></label>
            </div>
        </div>
    </td>
</tr>
<tr class="security-tr-border">
    <th><I18n:message key="REPORTS_LIST"/></th>
    <td>
        <div>
            <div class="security-tree-item" parent="0" nochilds="false" id="parentDivId8">
                <html:checkbox property="viewReports" styleId="8" onclick="treeset(this);"/><label
                    for="8"><I18n:message key="Action.viewReports"/></label>
            </div>
            <div class="security-tree-item" parent="8" nochilds="false" id="parentDivId9">
                <img src="${contextPath}${ImageServlet}/cssimages/L.png">
                <html:checkbox property="managePrivateReports" styleId="9" onclick="treeset(this);"
                               styleClass="child"/><label for="9"><I18n:message
                    key="Action.managePrivateReports"/></label>
            </div>
            <div class="security-tree-item" parent="9" nochilds="true" id="parentDivId10">
                <img src="${contextPath}${ImageServlet}/cssimages/blank.png">
                <img src="${contextPath}${ImageServlet}/cssimages/L.png">
                <html:checkbox property="managePublicReports" styleId="10" onclick="treeset(this);"
                               styleClass="child"/><label for="10"><I18n:message
                    key="Action.managePublicReports"/></label>
            </div>
        </div>
    </td>
</tr>
<tr class="security-tr-border">
    <th><I18n:message key="ATTACHMENTS"/></th>
    <td>
        <div>
            <div class="security-tree-item" parent="0" nochilds="false" id="parentDivId1">
                <html:checkbox property="viewTaskAttachments" styleId="1" onclick="treeset(this);"/><label
                    for="1"><I18n:message key="Action.viewTaskAttachments"/></label>
            </div>
            <div class="security-tree-item" parent="1" nochilds="false" id="parentDivId2">
                <img src="${contextPath}${ImageServlet}/cssimages/T.png">
                <html:checkbox property="createTaskAttachments" styleId="2" onclick="treeset(this);"
                               styleClass="child"/><label for="2"><I18n:message
                    key="Action.createTaskAttachments"/></label>
            </div>
            <div class="security-tree-item" parent="2" nochilds="false" id="parentDivId3">
                <img src="${contextPath}${ImageServlet}/cssimages/I.png">
                <img src="${contextPath}${ImageServlet}/cssimages/L.png">
                <html:checkbox property="deleteTheirTaskAttachment" styleId="3" onclick="treeset(this);"
                               styleClass="child"/><label for="3"><I18n:message
                    key="Action.deleteTheirTaskAttachment"/></label>
            </div>
            <div class="security-tree-item" parent="3" nochilds="true" id="parentDivId4">
                <img src="${contextPath}${ImageServlet}/cssimages/I.png">
                <img src="${contextPath}${ImageServlet}/cssimages/blank.png">
                <img src="${contextPath}${ImageServlet}/cssimages/L.png">
                <html:checkbox property="manageTaskAttachments" styleId="4" onclick="treeset(this);"
                               styleClass="child"/><label for="4"><I18n:message
                    key="Action.manageTaskAttachments"/></label>
            </div>
            <div class="security-tree-item" parent="1" nochilds="false" id="parentDivId11">
                <img src="${contextPath}${ImageServlet}/cssimages/L.png">
                <html:checkbox property="createTaskMessageAttachments" styleId="11" onclick="treeset(this);"
                               styleClass="child"/><label for="11"><I18n:message
                    key="Action.createTaskMessageAttachments"/></label>
            </div>
            <div class="security-tree-item" parent="11" nochilds="false" id="parentDivId12">
                <img src="${contextPath}${ImageServlet}/cssimages/blank.png">
                <img src="${contextPath}${ImageServlet}/cssimages/L.png">
                <html:checkbox property="deleteTheirMessageAttachment" styleId="12" onclick="treeset(this);"
                               styleClass="child"/><label for="12"><I18n:message
                    key="Action.deleteTheirMessageAttachment"/></label>
            </div>
            <div class="security-tree-item" parent="12" nochilds="true" id="parentDivId13">
                <img src="${contextPath}${ImageServlet}/cssimages/blank.png">
                <img src="${contextPath}${ImageServlet}/cssimages/blank.png">
                <img src="${contextPath}${ImageServlet}/cssimages/L.png">
                <html:checkbox property="manageTaskMessageAttachments" styleId="13" onclick="treeset(this);"
                               styleClass="child"/><label for="13"><I18n:message
                    key="Action.manageTaskMessageAttachments"/></label>
            </div>
        </div>
    </td>
</tr>
<tr class="security-tr-border">
    <th><I18n:message key="EMAIL"/></th>
    <td>
        <div>
            <html:checkbox property="manageEmailSchedules" styleId="manageEmailSchedules"/><label
                for="manageEmailSchedules"><I18n:message key="Action.manageEmailSchedules"/></label>
        </div>
    </td>
</tr>
<tr class="security-tr-border">
    <th><I18n:message key="ACL"/></th>
    <td>
        <div>
            <html:checkbox property="manageTaskACLs" styleId="manageTaskACLs"/><label
                for="manageTaskACLs"><I18n:message key="Action.manageTaskACLs"/></label>
        </div>
    </td>
</tr>
<tr class="security-tr-border">
    <th><I18n:message key="CUSTOM_FIELDS"/></th>
    <td>
        <div>
            <html:checkbox property="manageTaskUDFs" styleId="manageTaskUDFs"/><label
                for="manageTaskUDFs"><I18n:message key="Action.manageTaskUDFs"/></label>
        </div>
    </td>
</tr>
<tr class="security-tr-border">
    <th><I18n:message key="EMAIL_IMPORT_LIST"/></th>
    <td>
        <div>
            <html:checkbox property="manageEmailImportRules" styleId="manageEmailImportRules"/><label
                for="manageEmailImportRules"><I18n:message key="Action.manageEmailImportRules"/></label>
        </div>
    </td>
</tr>
<tr class="security-tr-border">
    <th><I18n:message key="REGISTRATIONS"/></th>
    <td>
        <div>
            <html:checkbox property="manageRegistrations" styleId="manageRegistrations"/><label
                for="manageRegistrations"><I18n:message key="Action.manageRegistrations"/></label>
        </div>
    </td>
</tr>
<tr class="security-tr-border">
    <th><I18n:message key="TEMPLATES_LIST"/></th>
    <td>
        <div>
            <html:checkbox property="manageTaskTemplates" styleId="manageTaskTemplates"/><label
                for="manageTaskTemplates"><I18n:message key="Action.manageTaskTemplates"/></label>
        </div>
    </td>
</tr>
<tr class="security-tr-border">
    <th><I18n:message key="CATEGORIES"/></th>
    <td>
        <div>
            <html:checkbox property="manageCategories" styleId="manageCategories"/><label
                for="manageCategories"><I18n:message key="Action.manageCategories"/></label>
        </div>
    </td>
</tr>
<tr class="security-tr-border">
    <th><I18n:message key="WORKFLOWS"/></th>
    <td>
        <div>
            <html:checkbox property="manageWorkflows" styleId="manageWorkflows"/><label
                for="manageWorkflows"><I18n:message key="Action.manageWorkflows"/></label>
        </div>
    </td>
</tr>
<tr class="security-tr-border">
    <th><I18n:message key="TASK_SCRIPTS"/></th>
    <td>
        <div>
            <html:checkbox property="viewScriptsBrowser" styleId="viewScriptsBrowser"/><label
                for="viewScriptsBrowser"><I18n:message key="Action.viewScriptsBrowser"/></label>
        </div>
    </td>
</tr>
<tr class="security-tr-border">
    <th><I18n:message key="LIST_TEMPLATES"/></th>
    <td>
        <div>
            <html:checkbox property="viewTemplatesBrowser" styleId="viewTemplatesBrowser"/>
            <label for="viewTemplatesBrowser">
                <I18n:message key="Action.viewTemplatesBrowser"/>
            </label>
        </div>
    </td>
</tr>
<tr class="security-tr-border">
    <th><I18n:message key="SHOW_VIEWS_DIALOG"/></th>
    <td>
        <div>
            <html:checkbox property="showView" styleId="showView"/><label for="showView"><I18n:message key="SHOW_VIEWS_DIALOG"/></label>
        </div>
    </td>
</tr>
<tr class="security-tr-border">
    <th><I18n:message key="SHOW_OTHER_FILTER_TAB"/></th>
    <td>
        <div>
            <html:checkbox property="showOtherFilterTab" styleId="showOtherFilterTab"/><label for="showOtherFilterTab"><I18n:message key="SHOW_OTHER_FILTER_TAB"/></label>
        </div>
    </td>
</tr>
<tr class="security-tr-border">
    <th><I18n:message key="CAN_CREATE_TASK_BY_OPERATION"/></th>
    <td>
        <div>
            <html:checkbox property="canCreateTaskByOperation" styleId="canCreateTaskByOperation"/><label for="canCreateTaskByOperation"><I18n:message key="CAN_CREATE_TASK_BY_OPERATION"/></label>
        </div>
    </td>
</tr>
<tr class="security-tr-border">
    <th><I18n:message key="CAN_USE_POST_FILTRATION"/></th>
    <td>
        <div>
            <html:checkbox property="canUsePostFiltration" styleId="canUsePostFiltration"/><label for="canUsePostFiltration"><I18n:message key="CAN_USE_POST_FILTRATION"/></label>
        </div>
    </td>
</tr>
    <tr class="security-tr-border">
        <th><I18n:message key="CAN_ARCHIVE"/></th>
        <td>
            <div class="security-tree-item" parent="14" nochilds="false" id="parentDivId15">
                <html:checkbox property="canArchive" styleId="15" onclick="treeset(this);"/><label for="15"><I18n:message key="CAN_ARCHIVE"/></label>
            </div>
            <div class="security-tree-item" parent="15" nochilds="false" id="parentDivId16">
                <img src="${contextPath}${ImageServlet}/cssimages/L.png">
                <html:checkbox property="canDeleteArchive" styleId="16" onclick="treeset(this);" styleClass="child"/>
                <label for="16"><I18n:message key="CAN_DELETE_ARCHIVE"/></label>
            </div>
        </td>
    </tr>
</table>
</div>
<c:if test="${canEdit}">
    <div class="controls">
        <input type="SUBMIT" class="iconized" value="<I18n:message key="SAVE"/>" name="SAVE">
    </div>
</c:if>
</div>
</html:form>
</div>
</c:if>
</tiles:put>
</tiles:insert>
