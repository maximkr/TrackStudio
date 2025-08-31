<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "${contextPath}/strict.dtd">
<%@ page buffer="128kb" errorPage="/jsp/Error.jsp" %>
<%@ page session="false" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://trackstudio.com" prefix="ts" %>
<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value="${sc.timezone}"/>
<I18n:setBundle basename="language"/>
<html>
<head>
	<link rel="shortcut icon" href="favicon.ico" type="image/x-icon"/>
	<link rel="icon" href="${contextPath}${ImageServlet}/TrackStudio/favicon.png" type="image/png"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <ts:css request="${request}">
        <ts:cssLink link="style_src.css"/>
        <c:set var="urlHtml" value="html"/>
    </ts:css>
    <%--<link rel="stylesheet" type="text/css" href="<c:out value="${contextPath}${versionPath}"/>/style-print_src.css" media="print">--%>
    <!--   -->
    <ts:js request="${request}" response="${response}">
        <ts:jsLink link="${urlHtml}/validate.js"/>
        <ts:jsLink link="${urlHtml}/quickSelect.js"/>
        <ts:jsLink link="${urlHtml}/tsmenu/tsmenu.js"/>
	    <ts:jsLink link="${urlHtml}/jquery/jquery-1.11.2.min.js"/>
	    <ts:jsLink link="${urlHtml}/jquery/jquery-ui.min.js"/>
    </ts:js>
    <!-- -->
    <!--
    <script type="text/javascript" src="<c:out value="${contextPath}${versionPath}"/>/html/validate.js"></script>
    <script type="text/javascript" src="<c:out value="${contextPath}${versionPath}"/>/html/validate.js"></script>
    <script type="text/javascript" src="<c:out value="${contextPath}${versionPath}"/>/html/tsmenu/tsmenu.js"></script>
    <script type="text/javascript" src="<c:out value="${contextPath}${versionPath}"/>/html/prototype.js"></script>
    
    <script type="text/javascript" src="<c:out value="${contextPath}${versionPath}"/>/html/ajaxtags-1.1-beta1.js"></script>
     -->
    <script type="text/javascript">
        self.top.currentURL = '<c:out value='${currentURL}' escapeXml='false'/>';
        var selectedId = getTabCookie("_selectedId");
        var udffield = getTabCookie("udffield");

        window.onload = function() {
        }

        var FuncContainer = function() {
        }

        var tsMenuDefImagePath = '<c:out value="${contextPath}"/>/cssimages/';
        var tsMenuImagePath = '<c:out value="${contextPath}"/>/cssimages/';
        var frameLoaded = "loaded";
	    var VERSION_TS = "1";
	    var jsAlert = false;
    </script>
</head>

<body>
<div class="controls">
    <span style="float: left"><img src="${contextPath}${ImageServlet}/cssimages/ico.customfields.gif" class="icon"
                                   alt=""/><c:out value="${currentUDF}"/></span>
    <input type="submit" class="iconized"
           value="<I18n:message key="CHOOSE_TASK"/>"
           onclick="return chooseTask();"
           name="SUBMIT">
    <input type="submit" class="iconized secondary"
           value="<I18n:message key="CANCEL"/>"
           onclick="closeServicePanel(document.forms['taskListForm'].elements['SELTASK']); window.close();"
           name="deleteButton">&nbsp;

</div>
<div class="logopath">
    <c:forEach var="task" items="${tci.ancestors}" varStatus="varCounter">
        <html:link styleClass="internal" href="${contextPath}/TaskSelectAction.do?method=page&amp;id=${task.id}&amp;udffield=${udffield}" title="#${task.number}">
            <c:out value="${task.name}" escapeXml="true"/>&nbsp;/
        </html:link>
    </c:forEach>
</div>
<div class="taskTitle"><html:link styleClass="internal"
                                  href="${contextPath}/TaskSelectAction.do?method=page&amp;id=${tci.id}&amp;udffield=${udffield}"
                                  title="#${tci.number}">
    <html:img styleClass="icon" border="0" src="${contextPath}${ImageServlet}/icons/categories/${tci.category.icon}"/>
    <html:img styleClass="state" border="0" style="background-color: ${tci.status.color}"
              src="${contextPath}${ImageServlet}${tci.status.image}"/>
    <c:out value="${tci.name}" escapeXml="true"/>&nbsp;<em class="number">[<c:out value="${tci.shortname}"
                                                                                  escapeXml="true"/> #<c:out
        value="${tci.number}" escapeXml="true"/>]</em>
</html:link></div>
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
               onclick="placeOnServicePanel('<c:out value="${st.id}"/>','#<c:out value="${st.number}"/>');"
               style="padding-top: 1px; padding-bottom: 1px; font-family: Verdana; font-size: 11px; font-weight: bold;"
               title="[#${st.number}] <c:out value="${st.name}"/>" for="#<c:out value="${st.number}"/>">
            <wm class="number">#<c:out value="${st.number}"/></em>
                <c:choose>
                <c:when test="${fn:length(st.name)>23}">${fn:substring(st.name,0 ,20)}...
                </c:when>
                <c:otherwise>
                    <c:out value="${st.name}"/>
                </c:otherwise>
            </c:choose></label>
    </c:forEach>

</div>
<script type="text/javascript">
    var servicePanelSrc = null;

    function selected(id) {
        var el = document.getElementById(id);
        var value = !el.checked;
        el.checked = value;
    }
</script>


<div class="blueborder">
<div class="caption">
    <span><I18n:message key="TASKS"/>:</span>
    <span title="<c:out value="${filter.description}"/>"><c:out value="${filter.name}"/></span>
</div>
<div class="controlPanel">
    <script type="text/javascript">
        var filterMenu = {};
        var otherMenu = false;
    </script>

    <c:if test="${!empty filters}">
        <c:forEach items="${filters}" var="f">
            <c:choose>
                <c:when test="${fn:indexOf(f.name,'/')>0}">
                    <c:set var="menuGroup" value="${fn:substringBefore(f.name,'/')}"/>
                    <script type="text/javascript">
                        if (!filterMenu['${menuGroup}']) {
                            filterMenu['${menuGroup}'] = new TSMenu();
                            filterMenu['${menuGroup}'].width = 320;
                        }
                        filterMenu['${menuGroup}'].add(new TSMenuItem(
                                "${fn:substringAfter(f.name,'/')}",
                                "<c:out value="${contextPath}"/>/TaskSelectAction.do?method=page&amp;id=${id}&amp;filter=${f.id}&amp;udffield=${udffield}&amp;go=true",
                                false,
                                false,
                                "${contextPath}${ImageServlet}/cssimages/ico.filter.gif",
                                null,
                                '<c:out value="${f.htmlDesc}" escapeXml="true"/>'));
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
                                    <html:link title="${f.description}" styleClass="selected"
                                               href="${contextPath}/TaskSelectAction.do?method=page&amp;id=${id}&amp;filter=${f.id}&amp;udffield=${udffield}&amp;go=true"
                                            ><html:img src="${contextPath}${ImageServlet}/cssimages/ico.filter.gif"
                                                       border="0"/><c:out value="${f.name}"/>
                                    </html:link>
                                </c:when>
                                <c:otherwise>
                                    <html:link styleClass="internal" title="${f.description}"
                                               href="${contextPath}/TaskSelectAction.do?method=page&amp;id=${id}&amp;filter=${f.id}&amp;udffield=${udffield}&amp;go=true"
                                            ><html:img src="${contextPath}${ImageServlet}/cssimages/ico.filter.gif"
                                                       border="0"
                                            /><c:out value="${f.name}"/>
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
                                        "<c:out value="${f.name}"/>",
                                        "<c:out value="${contextPath}"/>/TaskSelectAction.do?method=page&amp;id=${id}&amp;filter=${f.id}&amp;udffield=${udffield}&amp;go=true",
                                        false,
                                        false,
                                        "${contextPath}${ImageServlet}/cssimages/ico.filter.gif",
                                        null,
                                        '<c:out value="${f.htmlDesc}" escapeXml="true"/>'));

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
                var but = new TSMenuBut('${filter.name}', null, filterMenu[h], "<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.filters.gif");
                but.selected = true;
                filterMenuBar.add(but);
            }
            else {
                filterMenuBar.add(new TSMenuBut(h, null, filterMenu[h], "<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.filters.gif"));
            }
        }
        if (otherMenu) {
            if (otherMenu.selected) {
                var but = new TSMenuBut('${filter.name}', null, otherMenu, "<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.filters.gif");
                but.selected = true;
                filterMenuBar.add(but);
            }
            else {
                filterMenuBar.add(new TSMenuBut('<I18n:message key="OTHER_FILTERS"/>', null, otherMenu, "<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.filters.gif"));
            }
        }
        document.write(filterMenuBar);
    </script>
    </span>
        </c:if>
    </c:if>
</div>
<div class="indent">
<html:form method="post" action="/TaskSelectAction">
<html:hidden property="method" value="page" styleId="subtaskId"/>
<html:hidden property="collector"/>

<html:hidden property="id" value="${id}"/>
<html:hidden property="session" value="${session}"/>
<table class="general" cellpadding="0" cellspacing="0">
<tr class="wide">
<th width="5%" style="white-space:nowrap">
        <span style="text-align: center;">
            <input type="checkbox" onClick="_selectAll(this, document.forms['taskListForm'].elements['SELTASK']);">
        </span>
</th>
<c:if test="${headerNumber.canView}">
    <c:set var="columns" value="${columns+1}"/>
    <th nowrap width="<c:out value="${sizeOfPart*headerNumber.parts}"/>%">
        <html:link styleClass="underline"
                   href="${contextPath}/TaskSelectAction.do?method=page&id=${id}&sliderOrder=${headerNumber.sortBy}&filterId=${filter.id}">
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
                   href="${contextPath}/TaskSelectAction.do?method=page&id=${id}&sliderOrder=${headerFullPath.sortBy}&filterId=${filter.id}">
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
                   href="${contextPath}/TaskSelectAction.do?method=page&id=${id}&sliderOrder=${headerName.sortBy}&filterId=${filter.id}">
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
                   href="${contextPath}/TaskSelectAction.do?method=page&id=${id}&sliderOrder=${headerTaskParent.sortBy}&filterId=${filter.id}">
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
                   href="${contextPath}/TaskSelectAction.do?method=page&id=${id}&sliderOrder=${headerAlias.sortBy}&filterId=${filter.id}">
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
                   href="${contextPath}/TaskSelectAction.do?method=page&id=${id}&sliderOrder=${headerCategory.sortBy}&filterId=${filter.id}">
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
                   href="${contextPath}/TaskSelectAction.do?method=page&id=${id}&sliderOrder=${headerStatus.sortBy}&filterId=${filter.id}">
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
                   href="${contextPath}/TaskSelectAction.do?method=page&id=${id}&sliderOrder=${headerResolution.sortBy}&filterId=${filter.id}">
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
                   href="${contextPath}/TaskSelectAction.do?method=page&id=${id}&sliderOrder=${headerPriority.sortBy}&filterId=${filter.id}">
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
                   href="${contextPath}/TaskSelectAction.do?method=page&id=${id}&sliderOrder=${headerSubmitter.sortBy}&filterId=${filter.id}">
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
                   href="${contextPath}/TaskSelectAction.do?method=page&id=${id}&sliderOrder=${headerSubmitterStatus.sortBy}&filterId=${filter.id}">
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
                   href="${contextPath}/TaskSelectAction.do?method=page&id=${id}&sliderOrder=${headerHandler.sortBy}&filterId=${filter.id}">
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
                   href="${contextPath}/TaskSelectAction.do?method=page&id=${id}&sliderOrder=${headerHanlderStatus.sortBy}&filterId=${filter.id}">
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
                   href="${contextPath}/TaskSelectAction.do?method=page&id=${id}&sliderOrder=${headerSubmitDate.sortBy}&filterId=${filter.id}">
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
                   href="${contextPath}/TaskSelectAction.do?method=page&id=${id}&sliderOrder=${headerUpdateDate.sortBy}&filterId=${filter.id}">
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
                   href="${contextPath}/TaskSelectAction.do?method=page&id=${id}&sliderOrder=${headerCloseDate.sortBy}&filterId=${filter.id}">
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
                   href="${contextPath}/TaskSelectAction.do?method=page&id=${id}&sliderOrder=${headerDeadline.sortBy}&filterId=${filter.id}">
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
                   href="${contextPath}/TaskSelectAction.do?method=page&id=${id}&sliderOrder=${headerBudget.sortBy}&filterId=${filter.id}">
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
                   href="${contextPath}/TaskSelectAction.do?method=page&id=${id}&sliderOrder=${headerActualBudget.sortBy}&filterId=${filter.id}">
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
                   href="${contextPath}/TaskSelectAction.do?method=page&id=${id}&sliderOrder=${headerChildrenCount.sortBy}&filterId=${filter.id}">
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
                   href="${contextPath}/TaskSelectAction.do?method=page&id=${id}&sliderOrder=${headerMessageCount.sortBy}&filterId=${filter.id}">
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
                       href="${contextPath}/TaskSelectAction.do?method=page&id=${id}&sliderOrder=${udflink.sortBy}&filterId=${filter.id}">
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
<c:forEach var="taskLine" items="${taskLines}" varStatus="status">
<tr class="line<c:out value="${status.index mod 2}"/>">
<td class="top" style="text-align: center">
    <html:hidden property="TASKIDS" value="${taskLine.id}"/>
    <input type="checkbox" name="SELTASK" alt="delete1"
           title="#${taskLine.number}"
           value="<c:out value="${taskLine.id}"/>"
           id='${taskLine.id}'
           onclick="this.checked = placeOnServicePanel('<c:out value="${taskLine.id}" escapeXml="true"/>','#<c:out value="${taskLine.number}" escapeXml="true"/>','<c:out value="${taskLine.name}" escapeXml="true"/>');">
</td>
<c:if test="${headerNumber.canView}">
    <td>
        <a class="internal" id="${taskLine.id}-number" href="#"
           onclick="document.getElementById('${taskLine.id}').checked = placeOnServicePanel('<c:out value="${taskLine.id}" escapeXml="true"/>','#<c:out value="${taskLine.number}" escapeXml="true"/>','<c:out value="${taskLine.name}" escapeXml="true"/>'); return false;">
            #<c:out value="${taskLine.number}" escapeXml="true"/>
        </a>
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
        <c:if test="${taskLine.totalChildrenCount > 0}">
            <html:link styleClass="internal" styleId="${taskLine.id}-name"
                       href="${contextPath}/TaskSelectAction.do?method=page&amp;id=${taskLine.id}&amp;udffield=${udffield}">
                <html:img styleClass="icon" border="0"
                          src="${contextPath}${ImageServlet}/icons/categories/${taskLine.category.icon}"/>
                <html:img styleClass="state" border="0"
                          style="background-color: ${taskLine.status.color}"
                          src="${contextPath}${ImageServlet}${taskLine.status.image}"/>
                &nbsp;<c:out value="${taskLine.name}" escapeXml="true"/>
            </html:link>
        </c:if>
        <c:if test="${taskLine.totalChildrenCount == 0}">
            <html:img styleClass="icon" border="0"
                      src="${contextPath}${ImageServlet}/icons/categories/${taskLine.category.icon}"/>
            <html:img styleClass="state" border="0"
                      style="background-color: ${taskLine.status.color}"
                      src="${contextPath}${ImageServlet}${taskLine.status.image}"/>
            &nbsp;<c:out value="${taskLine.name}" escapeXml="true"/>
        </c:if>
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
</c:forEach>
</c:if>
<c:if test="${empty taskLines}">
    <script type="text/javascript">
        placeOnServicePanel('<c:out value="${tci.id}" escapeXml="true"/>', '#<c:out value="${tci.number}" escapeXml="true"/>', '<c:out value="${tci.name}" escapeXml="true"/>');
    </script>
</c:if>
</table>
<c:out value="${slider}" escapeXml="false"/>

<script type="text/javascript">
    servicePanelSrc = document.forms["taskListForm"].elements["SELTASK"];
    fillFormServicePanel(document.forms["taskListForm"].elements["SELTASK"]);

    	function chooseTask() {
		try {
			__operate('${contextPath}/predictor', '${udffield}');
		} catch(err) {
			console.error("chooseTask: caught error =", err);
			showError("chooseTask", err);
		}
		return false;
	}
</script>

<div class="controls" style="margin-bottom: 20px;">
    <input type="submit" class="iconized"
           value="<I18n:message key="CHOOSE_TASK"/>"
           onclick="return chooseTask(); "
           name="SUBMIT">
    <input type="submit" class="iconized secondary"
           value="<I18n:message key="CANCEL"/>"
           onclick="closeServicePanel(document.forms['taskListForm'].elements['SELTASK']); window.close();"
           name="deleteButton">

</div>

</html:form>
<ts:set session="${sc.id}" key="taskfilter" value="${taskfilter}"/>
</div>
</div>
</body>
</html>
