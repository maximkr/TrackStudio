<%@ page buffer="128kb" errorPage="/jsp/Error.jsp" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/tiles-el" prefix="tiles" %>
<%@ taglib uri="http://ajaxtags.org/tags/ajax" prefix="ajax" %>
<%@ taglib uri="http://trackstudio.com" prefix="ts" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value="${sc.timezone}"/>
<I18n:setBundle basename="language"/>
<tiles:insert page="/jsp/layout/ListLayout.jsp" flush="true">
<tiles:put name="header" value="/jsp/task/TaskHeader.jsp"/>
<tiles:put name="customHeader" type="string"/>
<tiles:put name="tabs" type="string"/>
<tiles:put name="main" type="string">
<script type="text/javascript">
    function deleteNote(url, param, id, filter) {
        $.ajax(url, {
            data: {method : 'changeTaskFilter', session: '${session}', id : id, filter : filter,  deleteElement : param},
            success: function(data) {
                $('#bookmarkPanel').html(data);
            }
        });
    }
</script>
<div id="servicePanel" class="${selectedIds!=null && !empty selectedIds ? "norm" : "closed"}">
    <span>
    <img id="windowhideicon" src="${contextPath}${ImageServlet}/cssimages/ico.hidewin.gif" class="icon"
         onclick="hideServicePanel();" title="<I18n:message key="HIDE"/>">
    <img id="windowopenicon" src="${contextPath}${ImageServlet}/cssimages/ico.openwin.gif" class="icon"
         onclick="hideServicePanel();" title="<I18n:message key="OPEN"/>">
    <img id="windowcloseicon" src="${contextPath}${ImageServlet}/cssimages/ico.closewin.gif" class="icon"
         onclick="closeServicePanel(document.forms['taskListForm'].elements['SELTASK']);"
         title="<I18n:message key="CLOSE"/>">
        </span>
    <c:forEach items="${selectedIds}" var="st">
        <label id="_spi_<c:out value="${st.id}"/>"
               style="padding-top: 1px; padding-bottom: 1px; font-family: Verdana; font-size: 11px; font-weight: bold;"
               title="[#${st.number}] <c:out value="${st.name}"/>"
               onclick="placeOnServicePanel('<c:out value="${st.id}"/>','#<c:out value="${st.number}"/>');"
               for="#<c:out value="${st.number}"/>">#<c:out value="${st.number}"/> </label>
    </c:forEach>

</div>
<script type="text/javascript">
    var servicePanelSrc = null;
</script>

<script type="text/javascript">
    var submitDelete = true;

    function deleteTask() {
        submitDelete = deleteConfirm("<I18n:message key="DELETE_TASK_REQ"/>", "taskListForm");
    }

    function archiveTask(btn) {
        submitDelete = deleteConfirm("<I18n:message key="ARCHIVE_TASK_REQ"/>", "taskListForm");
        if (submitDelete) {
            btn.disabled = true;
        }
    }

    function onSubmitFunction(frm) {
        return validate(frm) && allow(frm) && submitDelete;
    }

    function showBookmarkDialog(bookmarkName, taskId, filterId) {
        document.getElementById('bookmark_name').value = bookmarkName;

        document.getElementById('task_id').value = taskId;
        document.getElementById('filter_id').value = filterId;
        YAHOO.trackstudio.bookmark.bookmark_dialog.show();
    }

    function showPostFilterSaveAs(filterId, action) {
        document.getElementById('filter_save_as_id').value = filterId;
        document.getElementById('post_filter_save_as_form').value = '${contextPath}' + action;
        YAHOO.trackstudio.bookmark.post_filter_save_as.show();
    }

    function showBookmarkDialogSimple() {
        showBookmarkDialog('<c:out value="${title}"  escapeXml="true"/>', '<c:out value="${tci.id}"/>', '<c:out value="${filter.id}"/>');
    }
</script>
<div class="blueborder">
<div class="caption">
    <c:out value="${headerSlider}" escapeXml="false"/>
    <c:if test="${canViewRSS}">
        <html:link href="${RSSLink}" target="_blank" styleClass="floatlink">
            <html:img src="${contextPath}${ImageServlet}/cssimages/rssicon.png" border="0"/>
        </html:link>
    </c:if>
    <html:link href="javascript:showBookmarkDialogSimple();" styleClass="floatlink">
        <html:img src="${contextPath}${ImageServlet}/cssimages/ico.star.gif" border="0"/>
    </html:link>
    <span><I18n:message key="TASKS"/>:</span>
    <span title="<c:out value="${filter.description}"/>"><c:out value="${filter.name}"/></span>
</div>
<ajax:tabPanel
        panelStyleId="${sc.currentSpace}"
        panelStyleClass="controlPanel"
        contentStyleId="yellowbox"
        currentStyleId="selected"
        baseUrl="${contextPath}/SubtaskAction.do?method=page&id=${id}">
    <html:link href="${contextPath}/task/${homePageNumber}?thisframe=true">
        <html:img src="${contextPath}${ImageServlet}/cssimages/home.png" border="0"/>
    </html:link>
    <c:if test="${canUsePostFiltration}">
        <ajax:tab baseUrl="${contextPath}/TaskFilterParametersAction.do?method=page&id=${id}"
                  defaultTab="${sc.defaultTab eq 'TaskFilterParametersAction'}">
            <html:img src="${contextPath}${ImageServlet}/cssimages/ico.filterproperties.gif" border="0"/><I18n:message
                key="FILTER_PARAMETERS"/>
        </ajax:tab>
    </c:if>
    <script type="text/javascript">
        var filterMenu = {};
        var otherMenu = false;
    </script>
    <c:if test="${!empty filters}">
        <c:forEach items="${filters}" var="f">
            <c:choose>
                <c:when test="${fn:indexOf(f.correctName,'/')>0}">
                    <c:set var="menuGroup" value="${fn:substringBefore(f.correctName,'/')}"/>
                    <script type="text/javascript">
                        if (!filterMenu['${menuGroup}']) {
                            filterMenu['${menuGroup}'] = new TSMenu();
                            filterMenu['${menuGroup}'].width = 320;
                        }
                        filterMenu['${menuGroup}'].add(new TSMenuItem(
                                "${fn:substringAfter(f.correctName,'/')}",
                                "<c:out value="${contextPath}"/>/TaskFilterParametersAction.do?method=changeTaskFilter&id=${id}&filterId=${f.id}&go=true",
                                false,
                                false,
                                "${contextPath}${ImageServlet}/cssimages/ico.filter.gif",
                                null,
                                "<c:out value="${f.correctDesc}" escapeXml="true"/>"));
                        <c:if test="${f.id eq filter.id}">
                        filterMenu['${menuGroup}'].selected = true;
                        </c:if>
                    </script>
                </c:when>

                <c:otherwise>
                    <c:choose>
                        <c:when test="${fn:indexOf(f.preferences,'T')>-1}">
                            <c:choose>
                                <c:when test="${f.id eq filter.id}">
                                    <html:link title="${f.correctDesc}" styleClass="selected"
                                               href="${contextPath}/TaskFilterParametersAction.do?method=changeTaskFilter&id=${id}&filterId=${f.id}&go=true"
                                            ><html:img src="${contextPath}${ImageServlet}/cssimages/ico.filter.gif"
                                                       border="0"/><c:out value="${f.correctName}"/>
                                    </html:link>
                                </c:when>
                                <c:otherwise>
                                    <html:link styleClass="internal" title="${f.correctDesc}"
                                               href="${contextPath}/TaskFilterParametersAction.do?method=changeTaskFilter&id=${id}&filterId=${f.id}&go=true"
                                            ><html:img src="${contextPath}${ImageServlet}/cssimages/ico.filter.gif"
                                                       border="0"
                                            /><c:out value="${f.correctName}"/>
                                    </html:link>
                                </c:otherwise>
                            </c:choose>
                        </c:when>
                        <c:otherwise>
                            <script type="text/javascript">
                                if (!otherMenu) {
                                    otherMenu = new TSMenu();
                                    otherMenu.width = 320;
                                }
                                otherMenu.add(new TSMenuItem(
                                        "<c:out value="${f.correctName}"/>",
                                        "<c:out value="${contextPath}"/>/TaskFilterParametersAction.do?method=changeTaskFilter&id=${id}&filterId=${f.id}&go=true",
                                        false,
                                        false,
                                        "${contextPath}${ImageServlet}/cssimages/ico.filter.gif",
                                        null,
                                        "<c:out value="${f.correctDesc}" escapeXml="true"/>"));
                                <c:if test="${f.id eq filter.id}">
                                otherMenu.selected = true;
                                </c:if>
                            </script>
                        </c:otherwise>
                    </c:choose>
                </c:otherwise>
            </c:choose>
        </c:forEach>
        <c:if test="${!empty filters}">
   <span class="additional">
    <script type="text/javascript">
        var filterMenuBar = new TSMenuBar();
        for (var h in filterMenu) {
            if (filterMenu[h].selected) {
                var but = new TSMenuBut(
                        '${filter.correctName}',
                        null,
                        filterMenu[h],
                        "<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.filters.gif",
                        null,
                        "<c:out value="${filter.correctDesc}" escapeXml="true"/>");
                but.selected = true;
                filterMenuBar.add(but);
            }
            else {
                filterMenuBar.add(new TSMenuBut(
                        h,
                        null,
                        filterMenu[h],
                        "<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.filters.gif",
                        null,
                        "<c:out value="${filter.correctDesc}" escapeXml="true"/>"));
            }
        }
        if (otherMenu && '${showOtherFilterTab}' == 'true') {
            if (otherMenu.selected) {
                var but = new TSMenuBut(
                        '${filter.correctName}',
                        null,
                        otherMenu,
                        "<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.filters.gif",
                        null,
                        "<c:out value="${filter.correctDesc}" escapeXml="true"/>");
                but.selected = true;
                filterMenuBar.add(but);
            }
            else {
                filterMenuBar.add(new TSMenuBut(
                        '<I18n:message key="OTHER_FILTERS"/>',
                        null,
                        otherMenu,
                        "<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.filters.gif",
                        null,
                        "<c:out value="${filter.correctDesc}" escapeXml="true"/>"));
            }
        }
        document.write(filterMenuBar);
    </script>
    </span>
        </c:if>
    </c:if>
</ajax:tabPanel>
<div class="indent">
<html:form method="post" action="/SubtaskAction" onsubmit="return onSubmitFunction(this);">
<html:hidden property="method" value="page" styleId="subtaskId"/>
<html:hidden property="operation" value="SINGLE_COPY" styleId="operationId"/>
<html:hidden property="collector"/>
<html:hidden property="id" value="${id}"/>
<html:hidden property="session" value="${session}"/>
<table class="general" cellpadding="0" cellspacing="0">
<tr class="wide">
<c:set var="columns" value="1"/>
<th width="4%" style="white-space:nowrap;text-align: center;">
    <input type="checkbox" id="headerChecker" onClick="_selectAll(this, this.form.elements['SELTASK']);">
    <c:if test="${showOpenAll}">
        <a id="messageHistoryHeader" class="history-closed" href="javascript://nop/" onclick="openSubtasksMessage();">&nbsp;</a>
    </c:if>
</th>
<c:if test="${headerNumber.canView}">
    <c:set var="columns" value="${columns+1}"/>
    <th nowrap width="<c:out value="${sizeOfPart*headerNumber.parts}"/>%">
        <html:link styleClass="underline"
                   href="${contextPath}/SubtaskAction.do?method=page&id=${id}&sliderOrder=${headerNumber.sortBy}&filterId=${filter.id}">
            <I18n:message key="TASK_NUMBER"/>
            <c:if test="${headerNumber.currentSort != null && headerNumber.currentSort == 'abs'}"><html:image
                    src="${contextPath}${ImageServlet}/cssimages/up.gif"/></c:if>
            <c:if test="${headerNumber.currentSort != null && headerNumber.currentSort == 'desc'}"><html:image
                    src="${contextPath}${ImageServlet}/cssimages/down.gif"/></c:if>
        </html:link>
    </th>
</c:if>
<c:if test="${headerFullPath.canView}">
    <c:set var="columns" value="${columns+1}"/>
    <th width="<c:out value="${headerFullPath.parts}"/>%">
        <html:link styleClass="underline"
                   href="${contextPath}/SubtaskAction.do?method=page&id=${id}&sliderOrder=${headerFullPath.sortBy}&filterId=${filter.id}">
            <I18n:message key="RELATIVE_PATH"/>
            <c:if test="${headerNumber.currentSort != null && headerNumber.currentSort == 'abs'}"><html:image
                    src="${contextPath}${ImageServlet}/cssimages/up.gif"/></c:if>
            <c:if test="${headerNumber.currentSort != null && headerNumber.currentSort == 'desc'}"><html:image
                    src="${contextPath}${ImageServlet}/cssimages/down.gif"/></c:if>
        </html:link>
    </th>
</c:if>
<c:if test="${headerName.canView}">
    <c:set var="columns" value="${columns+1}"/>
    <th width="<c:out value="${headerName.parts}"/>%">
        <html:link styleClass="underline"
                   href="${contextPath}/SubtaskAction.do?method=page&id=${id}&sliderOrder=${headerName.sortBy}&filterId=${filter.id}">
            <I18n:message key="NAME"/>
            <c:if test="${headerName.currentSort != null && headerName.currentSort == 'abs'}"><html:image
                    src="${contextPath}${ImageServlet}/cssimages/up.gif"/></c:if>
            <c:if test="${headerName.currentSort != null && headerName.currentSort == 'desc'}"><html:image
                    src="${contextPath}${ImageServlet}/cssimages/down.gif"/></c:if>
        </html:link>
    </th>
</c:if>
<c:if test="${headerTaskParent.canView}">
    <c:set var="columns" value="${columns+1}"/>
    <th width="<c:out value="${headerTaskParent.parts}"/>%">
        <html:link styleClass="underline"
                   href="${contextPath}/SubtaskAction.do?method=page&id=${id}&sliderOrder=${headerTaskParent.sortBy}&filterId=${filter.id}">
            <I18n:message key="TASK_PARENT"/>
            <c:if test="${headerTaskParent.currentSort != null && headerTaskParent.currentSort == 'abs'}"><html:image
                    src="${contextPath}${ImageServlet}/cssimages/up.gif"/></c:if>
            <c:if test="${headerTaskParent.currentSort != null && headerTaskParent.currentSort == 'desc'}"><html:image
                    src="${contextPath}${ImageServlet}/cssimages/down.gif"/></c:if>
        </html:link>
    </th>
</c:if>
<c:if test="${headerAlias.canView}">
    <c:set var="columns" value="${columns+1}"/>
    <th width="<c:out value="${sizeOfPart*headerAlias.parts}"/>%">
        <html:link styleClass="underline"
                   href="${contextPath}/SubtaskAction.do?method=page&id=${id}&sliderOrder=${headerAlias.sortBy}&filterId=${filter.id}">
            <I18n:message key="ALIAS"/>
            <c:if test="${headerAlias.currentSort != null && headerAlias.currentSort == 'abs'}"><html:image
                    src="${contextPath}${ImageServlet}/cssimages/up.gif"/></c:if>
            <c:if test="${headerAlias.currentSort != null && headerAlias.currentSort == 'desc'}"><html:image
                    src="${contextPath}${ImageServlet}/cssimages/down.gif"/></c:if>
        </html:link>
    </th>
</c:if>
<c:if test="${headerCategory.canView}">
    <c:set var="columns" value="${columns+1}"/>
    <th width="<c:out value="${sizeOfPart}"/>%">
        <html:link styleClass="underline"
                   href="${contextPath}/SubtaskAction.do?method=page&id=${id}&sliderOrder=${headerCategory.sortBy}&filterId=${filter.id}">
            <I18n:message key="CATEGORY"/>
            <c:if test="${headerCategory.currentSort != null && headerCategory.currentSort == 'abs'}"><html:image
                    src="${contextPath}${ImageServlet}/cssimages/up.gif"/></c:if>
            <c:if test="${headerCategory.currentSort != null && headerCategory.currentSort == 'desc'}"><html:image
                    src="${contextPath}${ImageServlet}/cssimages/down.gif"/></c:if>
        </html:link>
    </th>
</c:if>
<c:if test="${headerStatus.canView}">
    <c:set var="columns" value="${columns+1}"/>
    <th width="<c:out value="${sizeOfPart}"/>%">
        <html:link styleClass="underline"
                   href="${contextPath}/SubtaskAction.do?method=page&id=${id}&sliderOrder=${headerStatus.sortBy}&filterId=${filter.id}">
            <I18n:message key="TASK_STATE"/>
            <c:if test="${headerStatus.currentSort != null && headerStatus.currentSort == 'abs'}"><html:image
                    src="${contextPath}${ImageServlet}/cssimages/up.gif"/></c:if>
            <c:if test="${headerStatus.currentSort != null && headerStatus.currentSort == 'desc'}"><html:image
                    src="${contextPath}${ImageServlet}/cssimages/down.gif"/></c:if>
        </html:link>
    </th>
</c:if>
<c:if test="${headerResolution.canView}">
    <c:set var="columns" value="${columns+1}"/>
    <th width="<c:out value="${sizeOfPart}"/>%">
        <html:link styleClass="underline"
                   href="${contextPath}/SubtaskAction.do?method=page&id=${id}&sliderOrder=${headerResolution.sortBy}&filterId=${filter.id}">
            <I18n:message key="RESOLUTION"/>
            <c:if test="${headerResolution.currentSort != null && headerResolution.currentSort == 'abs'}"><html:image
                    src="${contextPath}${ImageServlet}/cssimages/up.gif"/></c:if>
            <c:if test="${headerResolution.currentSort != null && headerResolution.currentSort == 'desc'}"><html:image
                    src="${contextPath}${ImageServlet}/cssimages/down.gif"/></c:if>
        </html:link>
    </th>
</c:if>
<c:if test="${headerPriority.canView}">
    <c:set var="columns" value="${columns+1}"/>
    <th width="<c:out value="${sizeOfPart}"/>%">
        <html:link styleClass="underline"
                   href="${contextPath}/SubtaskAction.do?method=page&id=${id}&sliderOrder=${headerPriority.sortBy}&filterId=${filter.id}">
            <I18n:message key="PRIORITY"/>
            <c:if test="${headerPriority.currentSort != null && headerPriority.currentSort == 'abs'}"><html:image
                    src="${contextPath}${ImageServlet}/cssimages/up.gif"/></c:if>
            <c:if test="${headerPriority.currentSort != null && headerPriority.currentSort == 'desc'}"><html:image
                    src="${contextPath}${ImageServlet}/cssimages/down.gif"/></c:if>
        </html:link>
    </th>
</c:if>
<c:if test="${headerSubmitter.canView}">
    <c:set var="columns" value="${columns+1}"/>
    <th width="<c:out value="${sizeOfPart}"/>%">
        <html:link styleClass="underline"
                   href="${contextPath}/SubtaskAction.do?method=page&id=${id}&sliderOrder=${headerSubmitter.sortBy}&filterId=${filter.id}">
            <I18n:message key="SUBMITTER"/>
        </html:link>
        <c:if test="${headerSubmitter.currentSort != null && headerSubmitter.currentSort == 'abs'}"><html:image
                src="${contextPath}${ImageServlet}/cssimages/up.gif"/></c:if>
        <c:if test="${headerSubmitter.currentSort != null && headerSubmitter.currentSort == 'desc'}"><html:image
                src="${contextPath}${ImageServlet}/cssimages/down.gif"/></c:if>
    </th>
</c:if>
<c:if test="${headerSubmitterStatus.canView}">
    <c:set var="columns" value="${columns+1}"/>
    <th width="<c:out value="${sizeOfPart}"/>%">
        <html:link styleClass="underline"
                   href="${contextPath}/SubtaskAction.do?method=page&id=${id}&sliderOrder=${headerSubmitterStatus.sortBy}&filterId=${filter.id}">
            <I18n:message key="SUBMITTER_STATUS"/>
            <c:if test="${headerSubmitterStatus.currentSort != null && headerSubmitterStatus.currentSort == 'abs'}"><html:image
                    src="${contextPath}${ImageServlet}/cssimages/up.gif"/></c:if>
            <c:if test="${headerSubmitterStatus.currentSort != null && headerSubmitterStatus.currentSort == 'desc'}"><html:image
                    src="${contextPath}${ImageServlet}/cssimages/down.gif"/></c:if>
        </html:link>
    </th>
</c:if>
<c:if test="${headerHandler.canView}">
    <c:set var="columns" value="${columns+1}"/>
    <th width="<c:out value="${sizeOfPart}"/>%">
        <html:link styleClass="underline"
                   href="${contextPath}/SubtaskAction.do?method=page&id=${id}&sliderOrder=${headerHandler.sortBy}&filterId=${filter.id}">
            <I18n:message key="HANDLER"/>
            <c:if test="${headerHandler.currentSort != null && headerHandler.currentSort == 'abs'}"><html:image
                    src="${contextPath}${ImageServlet}/cssimages/up.gif"/></c:if>
            <c:if test="${headerHandler.currentSort != null && headerHandler.currentSort == 'desc'}"><html:image
                    src="${contextPath}${ImageServlet}/cssimages/down.gif"/></c:if>
        </html:link>
    </th>
</c:if>
<c:if test="${headerHandlerStatus.canView}">
    <c:set var="columns" value="${columns+1}"/>
    <th width="<c:out value="${sizeOfPart}"/>%">
        <html:link styleClass="underline"
                   href="${contextPath}/SubtaskAction.do?method=page&id=${id}&sliderOrder=${headerHanlderStatus.sortBy}&filterId=${filter.id}">
            <I18n:message key="HANDLER_STATUS"/>
            <c:if test="${headerHandlerStatus.currentSort != null && headerHandlerStatus.currentSort == 'abs'}"><html:image
                    src="${contextPath}${ImageServlet}/cssimages/up.gif"/></c:if>
            <c:if test="${headerHandlerStatus.currentSort != null && headerHandlerStatus.currentSort == 'desc'}"><html:image
                    src="${contextPath}${ImageServlet}/cssimages/down.gif"/></c:if>
        </html:link>
    </th>
</c:if>
<c:if test="${headerSubmitDate.canView}">
    <c:set var="columns" value="${columns+1}"/>
    <th width="<c:out value="${sizeOfPart}"/>%">
        <html:link styleClass="underline"
                   href="${contextPath}/SubtaskAction.do?method=page&id=${id}&sliderOrder=${headerSubmitDate.sortBy}&filterId=${filter.id}">
            <I18n:message key="SUBMIT_DATE"/>
            <c:if test="${headerSubmitDate.currentSort != null && headerSubmitDate.currentSort == 'abs'}"><html:image
                    src="${contextPath}${ImageServlet}/cssimages/up.gif"/></c:if>
            <c:if test="${headerSubmitDate.currentSort != null && headerSubmitDate.currentSort == 'desc'}"><html:image
                    src="${contextPath}${ImageServlet}/cssimages/down.gif"/></c:if>
        </html:link>
    </th>
</c:if>
<c:if test="${headerUpdateDate.canView}">
    <c:set var="columns" value="${columns+1}"/>
    <th width="<c:out value="${sizeOfPart}"/>%">
        <html:link styleClass="underline"
                   href="${contextPath}/SubtaskAction.do?method=page&id=${id}&sliderOrder=${headerUpdateDate.sortBy}&filterId=${filter.id}">
            <I18n:message key="UPDATE_DATE"/>
            <c:if test="${headerUpdateDate.currentSort != null && headerUpdateDate.currentSort == 'abs'}"><html:image
                    src="${contextPath}${ImageServlet}/cssimages/up.gif"/></c:if>
            <c:if test="${headerUpdateDate.currentSort != null && headerUpdateDate.currentSort == 'desc'}"><html:image
                    src="${contextPath}${ImageServlet}/cssimages/down.gif"/></c:if>
        </html:link>
    </th>
</c:if>
<c:if test="${headerCloseDate.canView}">
    <c:set var="columns" value="${columns+1}"/>
    <th width="<c:out value="${sizeOfPart}"/>%">
        <html:link styleClass="underline"
                   href="${contextPath}/SubtaskAction.do?method=page&id=${id}&sliderOrder=${headerCloseDate.sortBy}&filterId=${filter.id}">
            <I18n:message key="CLOSE_DATE"/>
            <c:if test="${headerCloseDate.currentSort != null && headerCloseDate.currentSort == 'abs'}"><html:image
                    src="${contextPath}${ImageServlet}/cssimages/up.gif"/></c:if>
            <c:if test="${headerCloseDate.currentSort != null && headerCloseDate.currentSort == 'desc'}"><html:image
                    src="${contextPath}${ImageServlet}/cssimages/down.gif"/></c:if>
        </html:link>
    </th>
</c:if>
<c:if test="${headerDeadline.canView}">
    <c:set var="columns" value="${columns+1}"/>
    <th width="<c:out value="${sizeOfPart}"/>%">
        <html:link styleClass="underline"
                   href="${contextPath}/SubtaskAction.do?method=page&id=${id}&sliderOrder=${headerDeadline.sortBy}&filterId=${filter.id}">
            <I18n:message key="DEADLINE"/>
            <c:if test="${headerDeadline.currentSort != null && headerDeadline.currentSort == 'abs'}"><html:image
                    src="${contextPath}${ImageServlet}/cssimages/up.gif"/></c:if>
            <c:if test="${headerDeadline.currentSort != null && headerDeadline.currentSort == 'desc'}"><html:image
                    src="${contextPath}${ImageServlet}/cssimages/down.gif"/></c:if>
        </html:link>
    </th>
</c:if>
<c:if test="${headerBudget.canView}">
    <c:set var="columns" value="${columns+1}"/>
    <th width="<c:out value="${sizeOfPart}"/>%">
        <html:link styleClass="underline"
                   href="${contextPath}/SubtaskAction.do?method=page&id=${id}&sliderOrder=${headerBudget.sortBy}&filterId=${filter.id}">
            <I18n:message key="BUDGET"/>
            <c:if test="${headerBudget.currentSort != null && headerBudget.currentSort == 'abs'}"><html:image
                    src="${contextPath}${ImageServlet}/cssimages/up.gif"/></c:if>
            <c:if test="${headerBudget.currentSort != null && headerBudget.currentSort == 'desc'}"><html:image
                    src="${contextPath}${ImageServlet}/cssimages/down.gif"/></c:if>
        </html:link>
    </th>
</c:if>
<c:if test="${headerActualBudget.canView}">
    <c:set var="columns" value="${columns+1}"/>
    <th width="<c:out value="${sizeOfPart}"/>%">
        <html:link styleClass="underline"
                   href="${contextPath}/SubtaskAction.do?method=page&id=${id}&sliderOrder=${headerActualBudget.sortBy}&filterId=${filter.id}">
            <I18n:message key="ABUDGET"/>
            <c:if test="${headerActualBudget.currentSort != null && headerActualBudget.currentSort == 'abs'}"><html:image
                    src="${contextPath}${ImageServlet}/cssimages/up.gif"/></c:if>
            <c:if test="${headerActualBudget.currentSort != null && headerActualBudget.currentSort == 'desc'}"><html:image
                    src="${contextPath}${ImageServlet}/cssimages/down.gif"/></c:if>
        </html:link>
    </th>
</c:if>
<c:if test="${headerChildrenCount.canView}">
    <c:set var="columns" value="${columns+1}"/>
    <th width="<c:out value="${sizeOfPart}"/>%">
        <html:link styleClass="underline"
                   href="${contextPath}/SubtaskAction.do?method=page&id=${id}&sliderOrder=${headerChildrenCount.sortBy}&filterId=${filter.id}">
            <I18n:message key="SUBTASKS"/>
            <c:if test="${headerChildrenCount.currentSort != null && headerChildrenCount.currentSort == 'abs'}"><html:image
                    src="${contextPath}${ImageServlet}/cssimages/up.gif"/></c:if>
            <c:if test="${headerChildrenCount.currentSort != null && headerChildrenCount.currentSort == 'desc'}"><html:image
                    src="${contextPath}${ImageServlet}/cssimages/down.gif"/></c:if>
        </html:link>
    </th>
</c:if>
<c:if test="${headerMessageCount.canView}">
    <c:set var="columns" value="${columns+1}"/>
    <th width="<c:out value="${sizeOfPart}"/>%">
        <html:link styleClass="underline"
                   href="${contextPath}/SubtaskAction.do?method=page&id=${id}&sliderOrder=${headerMessageCount.sortBy}&filterId=${filter.id}">
            <I18n:message key="MESSAGES_AMOUNT"/>
            <c:if test="${headerMessageCount.currentSort != null && headerMessageCount.currentSort == 'abs'}"><html:image
                    src="${contextPath}${ImageServlet}/cssimages/up.gif"/></c:if>
            <c:if test="${headerMessageCount.currentSort != null && headerMessageCount.currentSort == 'desc'}"><html:image
                    src="${contextPath}${ImageServlet}/cssimages/down.gif"/></c:if>
        </html:link>
    </th>
</c:if>
<c:forEach items="${udfs}" var="udf">
    <c:set var="udflink" value="${udfHeaderLink[udf]}"/>
    <c:if test="${udflink.canView}">
        <th width="<c:out value="${sizeOfPart*udflink.parts}"/>%">
            <c:set var="columns" value="${columns+1}"/>
            <html:link styleClass="underline"
                       href="${contextPath}/SubtaskAction.do?method=page&id=${id}&sliderOrder=${udflink.sortBy}&filterId=${filter.id}">
                <c:out value="${udfHeaderCaption[udf]}" escapeXml="false"/>
                <c:if test="${udflink.currentSort != null && udflink.currentSort == 'abs'}"><html:image
                        src="${contextPath}${ImageServlet}/cssimages/up.gif"/></c:if>
                <c:if test="${udflink.currentSort != null && udflink.currentSort == 'desc'}"><html:image
                        src="${contextPath}${ImageServlet}/cssimages/down.gif"/></c:if>
            </html:link>
        </th>
    </c:if>
</c:forEach>
</tr>
<c:set var="rowspan" value="1"/>
<c:if test="${canViewDescription}">
    <c:set var="rowspan" value="2"/>
</c:if>

<c:if test="${!(empty taskLines)}">
<c:forEach var="taskLineEntity" items="${taskLines}" varStatus="status">

<c:set var="listMes" value="${taskLineEntity.value}"/>
<c:set var="taskLine" value="${taskLineEntity.key}"/>
<tr class="line<c:out value="${status.index mod 2}"/>">
<c:choose>
    <c:when test="${(!empty listMes)}">
        <c:set var="tmprowspan" value="${rowspan+1}"/>
    </c:when>
    <c:otherwise>
        <c:set var="tmprowspan" value="${rowspan}"/>
    </c:otherwise>
</c:choose>
<c:choose>
<c:when test="${taskLine.updatedate.time.time > sc.prevLogonDate.time.time && taskLine.updatedate.time.time > sc.lastLogonDate.time.time}">
<td rowspan="<c:out value="${tmprowspan}"/>" style="text-align: center" class="top<c:if
                test="${taskLine.overdue}"> overdue</c:if> hottask" title="<c:if test="${taskLine.overdue}"><I18n:message
                key="OVERDUE_TASK"/>, </c:if> <I18n:message key="HOT_TASK"/>">
    </c:when>
    <c:when test="${taskLine.updatedate.time.time > sc.prevLogonDate.time.time}">
<td rowspan="<c:out value="${tmprowspan}"/>" style="text-align: center" class="top<c:if
                test="${taskLine.overdue}"> overdue</c:if> newtask" title="<c:if test="${taskLine.overdue}"><I18n:message
                key="OVERDUE_TASK"/>, </c:if><I18n:message key="NEW_TASK"/>">
    </c:when>
    <c:when test="${taskLine.updatedate.time.time > sc.lastLogonDate.time.time}">
<td rowspan="<c:out value="${tmprowspan}"/>" style="text-align: center" class="top<c:if
                test="${taskLine.overdue}"> overdue</c:if> hottask" title="<c:if test="${taskLine.overdue}"><I18n:message
                key="OVERDUE_TASK"/>, </c:if><I18n:message key="HOT_TASK"/>">
    </c:when>
    <c:otherwise>
<td rowspan="<c:out value="${tmprowspan}"/>" style="text-align: center" class="top<c:if
                test="${taskLine.overdue}"> overdue</c:if>" title="<c:if test="${taskLine.overdue}"><I18n:message
                key="OVERDUE_TASK"/></c:if>">
    </c:otherwise>
    </c:choose>
    <html:hidden property="TASKIDS" value="${taskLine.id}"/>
    <c:if test="${taskLine.canUpdate}">
        <input type="checkbox" name="SELTASK" alt="delete1"
               title="#${taskLine.number}"
               value="<c:out value="${taskLine.id}"/>"
               onclick="this.checked=placeOnServicePanel('<c:out value="${taskLine.id}" escapeXml="true"/>','#<c:out value="${taskLine.number}" escapeXml="true"/>');">
    </c:if>
</td>

<c:if test="${headerNumber.canView}">
    <td>
        <span class="internal">#<c:out value="${taskLine.number}" escapeXml="true"/></span>
    </td>
</c:if>
<c:if test="${headerFullPath.canView}">
    <td>
        <c:set var="ancestor" value="false"/>
        <c:forEach var="task" items="${taskLine.ancestors}" varStatus="varCounter">
            <c:if test="${ancestor}">
                <html:link styleClass="internal" href="${contextPath}/task/${task.number}?thisframe=true">
                    <html:img styleClass="icon" border="0"
                              src="${contextPath}${ImageServlet}${ImageServlet}/icons/categories/${task.categorySubtaskView.icon}"
                              title="${task.categorySubtaskView.name}"/>
                    <c:out value="${task.name}" escapeXml="true"/>&nbsp;/
                </html:link>
            </c:if>
            <c:if test="${!ancestor && task.id==tci.id}"><c:set var="ancestor" value="true"/></c:if>
        </c:forEach>

        <html:link styleClass="internal" href="${contextPath}/task/${taskLine.number}?thisframe=true">
            <html:img styleClass="icon" border="0" title="${taskLine.categorySubtaskView.name}"
                      src="${contextPath}${ImageServlet}/icons/categories/${taskLine.categorySubtaskView.icon}"/>
            <html:img styleClass="state" border="0" style="background-color: ${taskLine.status.color}"
                      src="${contextPath}${ImageServlet}${taskLine.status.image}"/>
            <c:out value="${taskLine.name}" escapeXml="true"/>
        </html:link>
    </td>
</c:if>

<c:if test="${headerName.canView}">
    <td>
        <html:link styleClass="internal" styleId="${taskLine.id}-name"
                   href="${contextPath}/task/${taskLine.number}?thisframe=true&asView=taskInfo">
            <html:img styleClass="icon" border="0"
                      src="${contextPath}${ImageServlet}/icons/categories/${taskLine.categorySubtaskView.icon}"
                      title="${taskLine.categorySubtaskView.name}"/>
        </html:link>
        <html:link styleClass="internal" styleId="${taskLine.id}-name"
                   href="${contextPath}/task/${taskLine.number}?thisframe=true" title="${taskLine.status.name}">
            <html:img styleClass="state" border="0" style="background-color: ${taskLine.status.color}"
                      src="${contextPath}${ImageServlet}${taskLine.status.image}"/>
            <c:choose>
                <c:when test="${taskLine.maskName eq null}">
                    <c:out value="${taskLine.name}" escapeXml="true"/>
                </c:when>
                <c:otherwise>
                    <c:out value='${taskLine.maskName}' escapeXml="false"/>
                </c:otherwise>
            </c:choose>
        </html:link>
    </td>
</c:if>

<c:if test="${headerTaskParent.canView}">
    <td>
        <c:if test="${taskLine.parent != null}">
            <html:link styleClass="internal" styleId="${taskLine.parent.id}-name"
                       href="${contextPath}/task/${taskLine.parent.number}?thisframe=true&asView=taskInfo">
                <html:img styleClass="icon" border="0"
                          src="${contextPath}${ImageServlet}/icons/categories/${taskLine.parent.categorySubtaskView.icon}"
                          title="${taskLine.parent.categorySubtaskView.name}"/>
            </html:link>
            <html:link styleClass="internal" styleId="${taskLine.parent.id}-name"
                       href="${contextPath}/task/${taskLine.parent.number}?thisframe=true">
                <html:img styleClass="state" border="0" style="background-color: ${taskLine.parent.status.color}"
                          src="${contextPath}${ImageServlet}${taskLine.parent.status.image}"/>
                <c:out value="${taskLine.parent.name}" escapeXml="true"/>
            </html:link>
        </c:if>
    </td>
</c:if>

<c:if test="${headerAlias.canView}">
    <td>
        <span style="white-space: nowrap;">
            <c:if test="${!empty taskLine.shortname}">
                <c:out value="${taskLine.shortname}" escapeXml="true"/>
            </c:if>
        </span>
    </td>
</c:if>
<c:if test="${headerCategory.canView}">
    <td><span>
             <c:out value="${taskLine.categorySubtaskView.name}" escapeXml="true"/>
        </span>
    </td>
</c:if>
<c:if test="${headerStatus.canView}">
    <td>
        <span>
            <c:if test="${taskLine.status ne null}">
                <c:out value="${taskLine.status.name}" escapeXml="true"/>
            </c:if>
        </span>
    </td>
</c:if>
<c:if test="${headerResolution.canView}">
    <td>
        <c:if test="${taskLine.resolution ne null}">
            <c:out value="${taskLine.resolution.name}" escapeXml="true"/>
        </c:if>
    </td>
</c:if>
<c:if test="${headerPriority.canView}">
    <td>
        <c:if test="${taskLine.priority ne null}">
            <c:out value="${taskLine.priority.name}" escapeXml="true"/>
        </c:if>
    </td>
</c:if>
<c:if test="${headerSubmitter.canView}">
    <td style="white-space: nowrap;">
        <c:if test="${taskLine.submitter ne null}"><span
                class="user" ${taskLine.submitter.id eq sc.userId ? "id='loggedUser'" : ""}>
            <html:img styleClass="icon" border="0"
                      src="${contextPath}${ImageServlet}/cssimages/${taskLine.submitter.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
    <c:out value="${taskLine.submitter.name}" escapeXml="true"/>
    </span></c:if>
    </td>
</c:if>
<c:if test="${headerSubmitterStatus.canView}">
    <td>
        <c:out value="${taskLine.submitter.prstatus.name}" escapeXml="true"/>
    </td>
</c:if>
<c:if test="${headerHandler.canView}">
    <td style="white-space: nowrap;">
        <c:choose>
            <c:when test="${taskLine.handlerUserId ne null}">
			<span class="user" ${taskLine.handlerUserId eq sc.userId ? "id='loggedUser'" : ""}>
            <html:img styleClass="icon" border="0"
                      src="${contextPath}${ImageServlet}/cssimages/${taskLine.handlerUser.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
            <c:out value="${taskLine.handlerUser.name}" escapeXml="true"/>
			</span>
            </c:when>
            <c:when test="${taskLine.handlerGroupId ne null}">
					<span class="user">
                        <html:img styleClass="icon" border="0"
                                  src="${contextPath}${ImageServlet}/cssimages/ico.status.gif"/><c:out
                            value="${taskLine.handlerGroup.name}" escapeXml="true"/>
						</span>
            </c:when>
            <c:otherwise>

            </c:otherwise>
        </c:choose>
    </td>
</c:if>
<c:if test="${headerHandlerStatus.canView}">
    <td>
        <c:if test="${taskLine.handlerUserId ne null}">
            <c:out value="${taskLine.handlerUser.prstatus.name}" escapeXml="true"/>
        </c:if>
    </td>
</c:if>
<c:if test="${headerSubmitDate.canView}">
    <td><span style="white-space: nowrap;"><c:if test="${taskLine.submitdate ne null}">
        <I18n:formatDate value="${taskLine.submitdate.time}" type="both" dateStyle="short" timeStyle="short"/>
    </c:if></span></td>
</c:if>
<c:if test="${headerUpdateDate.canView}">
    <td><span style="white-space: nowrap;"><c:if test="${taskLine.updatedate ne null}">
        <I18n:formatDate value="${taskLine.updatedate.time}" type="both" dateStyle="short" timeStyle="short"/>
    </c:if></span></td>
</c:if>
<c:if test="${headerCloseDate.canView}">
    <td><span style="white-space: nowrap;"><c:if test="${taskLine.closedate ne null}">
        <I18n:formatDate value="${taskLine.closedate.time}" type="both" dateStyle="short" timeStyle="short"/>
    </c:if></span></td>
</c:if>
<c:if test="${headerDeadline.canView}">
    <td><span style="white-space: nowrap;"><c:if test="${taskLine.deadline ne null}">
        <I18n:formatDate value="${taskLine.deadline.time}" type="both" dateStyle="short" timeStyle="short"/>
    </c:if></span></td>
</c:if>
<c:if test="${headerBudget.canView}">
    <td>
        <c:out value="${taskLine.budgetAsString}" escapeXml="true"/>
    </td>
</c:if>
<c:if test="${headerActualBudget.canView}">
    <td>
        <c:out value="${taskLine.actualBudgetAsString}" escapeXml="true"/>
    </td>
</c:if>
<c:if test="${headerChildrenCount.canView}">
    <td>
        <c:out value="${taskLine.childrenCount}" escapeXml="true"/>
    </td>
</c:if>
<c:if test="${headerMessageCount.canView}">
    <td>
        <c:out value="${taskLine.messageCount}" escapeXml="true"/>
    </td>
</c:if>
<c:forEach items="${udfs}" var="udf">
    <td>
        <c:if test="${taskLine.aliasUdfValues[udf] ne null}">
            <c:set var="udfValue" value="${taskLine.aliasUdfValues[udf]}"/>
            <c:set var="val" value="${udfValue.value}"/>
            <c:if test="${val ne null}">
                <c:choose>
                    <c:when test="${udfValue.type eq 'date'}">
                        <I18n:formatDate value="${val.time}" type="both" dateStyle="short" timeStyle="short"/>
                    </c:when>
                    <c:when test="${udfValue.type eq 'float'}">
                        <I18n:formatNumber value="${val}" groupingUsed="true" maxFractionDigits="${decimalFormatUdfFloat}"/>
                    </c:when>
                    <c:when test="${udfValue.type eq 'list'}">
                        <c:out value="${val.value}" escapeXml="true"/>
                    </c:when>
                    <c:when test="${udfValue.type eq 'multilist'}">
                        <c:forEach items="${val}" var="item" varStatus="c">

                            <c:out value="${item.value}" escapeXml="true"/>
                            <c:if test="${!c.last}">,</c:if>
                        </c:forEach>
                    </c:when>
                    <c:when test="${udfValue.type eq 'task'}">
                        <c:forEach var="t" items="${val}" varStatus="status">
                            <div class="line<c:out value="${status.index mod 2}"/>">
                                <c:choose>
                                    <c:when test="${t.canManage}">
                                        <html:link styleClass="internal"
                                                   href="${contextPath}/TaskViewAction.do?method=page&amp;id=${t.id}">
                                            <html:img styleClass="icon" border="0"
                                                      src="${contextPath}${ImageServlet}/icons/categories/${t.categorySubtaskView.icon}"/>
                                            <html:img styleClass="state" border="0"
                                                      style="background-color: ${t.status.color}"
                                                      src="${contextPath}${ImageServlet}${t.status.image}"/>
                                            <c:out value="${t.name}"/>
                                        </html:link>&nbsp;<em class="number">[#<c:out value="${t.number}"/>]</em>
                                    </c:when>
                                    <c:otherwise>
                                        #<c:out value="${t.number}"/>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </c:forEach>
                    </c:when>
                    <c:when test="${udfValue.type eq 'user'}">
                        <c:forEach var="user" items="${val}" varStatus="status">
                            <div class="line<c:out value="${status.index mod 2}"/>">
                                <span class="user">
                                    <html:img styleClass="icon" border="0"
                                              src="${contextPath}${ImageServlet}/cssimages/${user.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
                                    <c:out value="${user.name}"/>
                                </span>
                            </div>
                        </c:forEach>
                    </c:when>
                    <c:when test="${udfValue.type eq 'memo'}">
                        <c:out value="${val}" escapeXml="${!udfValue.htmlview}"/>
                    </c:when>
                    <c:when test="${udfValue.type eq 'url'}">
                        <html:link styleClass="internal" href="${val.link}">
                            <c:out value="${val.description ne null ? val.description : val.link}" escapeXml="true"/>
                        </html:link>
                    </c:when>
                    <c:otherwise>
                        <c:out value="${val}" escapeXml="${!udfValue.htmlview}"/>
                    </c:otherwise>
                </c:choose>
            </c:if>
        </c:if>
    </td>
</c:forEach>
</tr>
<c:if test="${canViewDescription}">
    <c:choose>
        <c:when test="${taskLine.updatedate.time.time > sc.prevLogonDate.time.time && taskLine.updatedate.time.time > sc.lastLogonDate.time.time}">
            <tr class="line<c:out value="${status.index mod 2}"/><c:if
                test="${taskLine.overdue}"> overdue</c:if> hottask" title="<c:if
                test="${taskLine.overdue}"><I18n:message key="OVERDUE_TASK"/></c:if>, <I18n:message key="HOT_TASK"/>">
        </c:when>
        <c:when test="${taskLine.updatedate.time.time > sc.prevLogonDate.time.time}">
            <tr class="line<c:out value="${status.index mod 2}"/><c:if
                test="${taskLine.overdue}"> overdue</c:if> newtask" title="<c:if
                test="${taskLine.overdue}"><I18n:message key="OVERDUE_TASK"/>, </c:if><I18n:message key="NEW_TASK"/>">
        </c:when>
        <c:when test="${taskLine.updatedate.time.time > sc.lastLogonDate.time.time}">
            <tr class="line<c:out value="${status.index mod 2}"/><c:if
                test="${taskLine.overdue}"> overdue</c:if> hottask" title="<c:if
                test="${taskLine.overdue}"><I18n:message key="OVERDUE_TASK"/>, </c:if><I18n:message key="HOT_TASK"/>">
        </c:when>
        <c:otherwise>
            <tr class="line<c:out value="${status.index mod 2}"/><c:if
                test="${taskLine.overdue}"> overdue</c:if>" title="<c:if test="${taskLine.overdue}"><I18n:message
                key="OVERDUE_TASK"/></c:if>">
        </c:otherwise>
    </c:choose>
    <td colspan="<c:out value="${columns-1}"/>" width="100%">
        <ts:htmlfilter session="${sc.id}" macros="true" audit="false" request="<%=request%>"><c:out
                value="${taskLine.description}" escapeXml="false"/></ts:htmlfilter>
    </td>
    </tr>
</c:if>

<c:if test="${( !empty listMes)}">
    <c:choose>
        <c:when test="${taskLine.updatedate.time.time > sc.prevLogonDate.time.time && taskLine.updatedate.time.time > sc.lastLogonDate.time.time}">
            <tr class="line<c:out value="${status.index mod 2}"/><c:if
                test="${taskLine.overdue}"> overdue</c:if> hottask" title="<c:if
                test="${taskLine.overdue}"><I18n:message key="OVERDUE_TASK"/></c:if>, <I18n:message key="HOT_TASK"/>">
        </c:when>
        <c:when test="${taskLine.updatedate.time.time > sc.prevLogonDate.time.time}">
            <tr class="line<c:out value="${status.index mod 2}"/><c:if
                test="${taskLine.overdue}"> overdue</c:if> newtask" title="<c:if
                test="${taskLine.overdue}"><I18n:message key="OVERDUE_TASK"/>, </c:if><I18n:message key="NEW_TASK"/>">
        </c:when>
        <c:when test="${taskLine.updatedate.time.time > sc.lastLogonDate.time.time}">
            <tr class="line<c:out value="${status.index mod 2}"/><c:if
                test="${taskLine.overdue}"> overdue</c:if> hottask" title="<c:if
                test="${taskLine.overdue}"><I18n:message key="OVERDUE_TASK"/>, </c:if><I18n:message key="HOT_TASK"/>">
        </c:when>
        <c:otherwise>
            <tr class="line<c:out value="${status.index mod 2}"/><c:if
                test="${taskLine.overdue}"> overdue</c:if>" title="<c:if test="${taskLine.overdue}"><I18n:message
                key="OVERDUE_TASK"/></c:if>">
        </c:otherwise>
    </c:choose>
    <td colspan="<c:out value="${columns-1}"/>" width="100%" class="msgcontainer">
        <c:if test="${(!(empty listMes))}">
            <c:set var="messages" value="${listMes}" scope="session"/>
            <c:import url="/jsp/task/viewtask/messages/MessagesTile.jsp"/>
        </c:if>

    </td>
    </tr>
</c:if>
</c:forEach>
<c:remove var="messages" scope="session"/>
</c:if>
<c:if test="${empty taskLines}">
    <tr>
        <td colspan="<c:out value="${columns}"/>" width="100%">
            <span style="text-align: center;">
                <I18n:message key="EMPTY_TASK_LIST"/>
            </span>
        </td>
    </tr>
</c:if>
</table>


<c:out value="${slider}" escapeXml="false"/>
<div class="controls">
    <c:if test="${!(empty taskLines)}">

        <c:if test="${canPerformBulkProcessing}">
            <div style="float: left;">
                <c:if test="${!empty bulkProcessing}">
                    <label><I18n:message key="BULK_SCRIPT"/>:</label>
                    <html:select style="padding: 2px 12px; vertical-align:middle;" property="bulk" disabled="false">
                        <c:forEach items="${bulkProcessing}" var="t">
                            <html:option value="${t.name}">
                                <c:out value="${t.name}"/>
                            </html:option>
                        </c:forEach>
                    </html:select>
                    <input type="submit" class="iconized secondary"
                           onClick="this.form['collector'].value=forrobots(document.forms['taskListForm'].elements['SELTASK']); closeServicePanel(document.forms['taskListForm'].elements['SELTASK']); set('bulk')"
                           value="<I18n:message key="APPLY"/>"
                           name="APPLY">
                    <html:checkbox property="useByFilter"><I18n:message key="USE_BY_FILTER"/></html:checkbox>
                </c:if>
                <c:if test="${!empty multiBulk}">
                    <label><I18n:message key="MULTI_BULK_SCRIPT"/>:</label>
                    <html:select style="padding: 2px 12px; vertical-align:middle;" property="multiBulk"
                                 disabled="false">
                        <c:forEach items="${multiBulk}" var="t">
                            <html:option value="${t.name}">
                                <c:out value="${t.name}"/>
                            </html:option>
                        </c:forEach>
                    </html:select>
                    <input type="submit" class="iconized secondary"
                           onClick="this.form['collector'].value=forrobots(document.forms['taskListForm'].elements['SELTASK']); closeServicePanel(document.forms['taskListForm'].elements['SELTASK']); set('multibulk')"
                           value="<I18n:message key="APPLY"/>"
                           name="APPLY">
                    <html:checkbox property="useByFilter"><I18n:message key="USE_BY_FILTER"/></html:checkbox>
                </c:if>
            </div>
        </c:if>
        <c:if test="${showClipboardButton}">
            <c:if test="${canArchive}">
            <input type="submit" class="iconized secondary"
                   value="<I18n:message key="ARCHIVE"/>"
                   name="ARCHIVE"
                   onClick="archiveTask(this); this.form['collector'].value=forrobots(document.forms['taskListForm'].elements['SELTASK']); set('archive'); setOperation('ARCHIVE'); if(onSubmitFunction(this.form)) this.form.submit();">
            </c:if>
            <input type="submit" class="iconized secondary"
                   value="<I18n:message key="CUT"/>"
                   name="CUT"
                   onClick="submitDelete=true; this.form['collector'].value=forrobots(document.forms['taskListForm'].elements['SELTASK']); set('clipboardOperation'); setOperation('CUT'); if(onSubmitFunction(this.form)) this.form.submit();">
            <input type="submit" class="iconized secondary"
                   value="<I18n:message key="COPY"/>"
                   name="SINGLE_COPY"
                   onClick="submitDelete=true; this.form['collector'].value=forrobots(document.forms['taskListForm'].elements['SELTASK']); set('clipboardOperation'); setOperation('SINGLE_COPY'); if(onSubmitFunction(this.form) ) this.form.submit();">
            <input type="submit" class="iconized secondary"
                   value="<I18n:message key="COPY_RECURSIVELY"/>"
                   onClick="submitDelete=true; this.form['collector'].value=forrobots(document.forms['taskListForm'].elements['SELTASK']); set('clipboardOperation'); setOperation('RECURSIVELY_COPY'); if(onSubmitFunction(this.form) ) this.form.submit();"
                   name="RECURSIVELY_COPY">
        </c:if>
    </c:if>
    <c:if test="${canPaste eq true}">
        <input type="submit" class="iconized secondary" value="<I18n:message key="PASTE"/>" name="PASTE"
               onClick="submitDelete=true; set('paste');closeServicePanel(document.forms['taskListForm'].elements['SELTASK']); if(onSubmitFunction(this.form)) this.form.submit();">
    </c:if>
    <c:if test="${canDelete}">
        <input type="submit" class="iconized"
               onClick="deleteTask(); this.form['collector'].value=forrobots(document.forms['taskListForm'].elements['SELTASK']); set('delete'); if(onSubmitFunction(this.form)) this.form.submit();"
               value="<I18n:message key="DELETE"/>"
               name="DELETE">
    </c:if>

    <script type="text/javascript">
        function set(target) {
            document.getElementById('subtaskId').value = target;
        }

        function setOperation(target) {
            document.getElementById('operationId').value = target;
        }


        servicePanelSrc = document.forms["taskListForm"].elements["SELTASK"];

        fillFormServicePanel(document.forms["taskListForm"].elements["SELTASK"]);

    </script>
</div>
</html:form>

<script type="text/javascript">


    var selectedTasks = new Array(<c:out value="${selectedTasks}" escapeXml="false"/>);
    var operationType = <c:out value="${operationType}" escapeXml="false"/>;
    var tempNode;
    for (var i = 0; i < selectedTasks.length; i++) {
        tempNode = document.getElementById(selectedTasks[i] + '-number');
        if (tempNode != null && tempNode.tagName == 'A' && operationType != '') {
            tempNode.className = operationType;
        }
        tempNode = document.getElementById(selectedTasks[i] + '-name');
        if (tempNode != null && tempNode.tagName == 'A' && operationType != '') {
            tempNode.className = operationType;
        }
    }
</script>
<I18n:message key="TOTAL_TASKS"/>&nbsp;:&nbsp;<c:out value="${totalTasks}"/>
</div>
</div>
<script type="text/javascript">


    function onSubmitBookMark() {
        if ($('bookmark_name').value != '') {
            YAHOO.trackstudio.bookmark.bookmark_dialog.hide();
            var url = "${contextPath}/BookmarkAction.do";
            var pars = {method : 'save', name : $('#bookmark_name').val(), taskId: $('#task_id').val(), filterId: $('#filter_id').val()};
            $.ajax('${contextPath}/TaskSubscribeEditAction.do', {
                data : pars,
                success: function(data) {
                    $.ajax("${contextPath}/bookmark", {
                        success: function(data) {
                            $('#bookmarkPanel').html(data);
                        }
                    });
                }
            });
        } else {
            alert("<I18n:message key="ENTER_BOOKMARK_NAME"/>");
        }
        return false;
    }

</script>

<div id="bookmark_dialog" style="visibility:hidden;">
    <div class="hd"><I18n:message key="CREATE_BOOKMARK"/></div>
    <div class="bd">
        <form method="post" id="bookmarkForm" action="#" onsubmit="onSubmitBookMark();">
            <table>
                <tr>
                    <td><label for="name" style="margin-right: 20px;"><I18n:message key="NAME"/></label></td>
                    <td><input type="text" value="'${name}'" id="bookmark_name" size="25"/></td>
                </tr>
                <tr>
                    <td><label for="task" style="margin-right: 20px;"><I18n:message key="TASK"/></label></td>
                    <td><c:out value="${tci.name}"/></td>
                </tr>
                <tr>
                    <td><label for="filter" style="margin-right: 20px;"><I18n:message key="FILTER"/></label></td>
                    <td><c:out value="${filter.name}"/></td>
                </tr>
            </table>
            <input type="hidden" value="'${taskId}'" id="task_id"/>
            <input type="hidden" value="'${filterId}'" id="filter_id"/>
        </form>
    </div>
</div>

<div id="post_filter_save_as" style="visibility:hidden;">
    <div class="hd"><I18n:message key="SAVE_FILTER"/></div>
    <div class="bd">
        <table>
            <tr>
                <td><label for="post_filter_name" style="margin-right: 20px;"><I18n:message key="NAME"/></label></td>
                <td><input type="text" id="post_filter_name" size="25"/></td>
            </tr>
        </table>
        <input type="hidden" id="post_filter_save_as_form">
        <input type="hidden" id="filter_save_as_id">
        <input type="hidden" id="method" value="changeTaskFilter">
    </div>
</div>
<script type="text/javascript">
    openSubtasksMessage();
</script>
<ts:set session="${sc.id}" key="taskfilter" value="${taskfilter}"/>
</tiles:put>
</tiles:insert>
