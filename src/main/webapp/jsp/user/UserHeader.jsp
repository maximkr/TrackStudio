<%@ page buffer="128kb" errorPage="/jsp/Error.jsp"%>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value="${sc.timezone}"/>
<I18n:setBundle basename="language"/>
<script type="text/javascript">
    function forbidEmptyLineSearch(form) {
        return form.key.value == "";
    }

    function deleteUser() {
        TSDialog.confirm("<I18n:message key="DELETE_USER_REQ"><I18n:param value="${currentUser.name}"/></I18n:message>", function(ok) {
            if (ok) location.replace("<c:out value="${contextPath}/UserListAction.do?deleteButton=true&amp;method=deleteUsers&amp;id=${currentUser.id}&amp;SELUSER=${currentUser.id}" escapeXml='false'/>");
        });
    }

    function deleteTaskFromMenu() {
        TSDialog.confirm("<c:if test="${tci.childrenCount != null &&  tci.childrenCount > 0}"><I18n:message key="CONTAINS_SUBTASKS"/></c:if> <I18n:message key="DELETE_TASK"><I18n:param value="\#${tci.number}"/></I18n:message>", function(ok) {
            if (ok) document.location = "<c:out value="${contextPath}/SubtaskAction.do?method=delete&amp;DELETE=TRUE&amp;SELTASK=${tci.id}&amp;id=${tci.parentId}" escapeXml='false'/>";
        });
    }

    var tsMenuDefImagePath = '<c:out value="${contextPath}"/>/cssimages/';
    var tsMenuImagePath = '<c:out value="${contextPath}"/>/cssimages/';

</script>
<script type="text/javascript">
    <c:if test="${jsEvent ne null}">
    <c:choose>
    <c:when test="${jsEvent.type eq 'Task Added'}">
    addTaskToTree(${jsEvent.hints}, ${jsEvent.names}, ${jsEvent.parentHint}, ${jsEvent.icons}, ${jsEvent.statusIcons}, ${jsEvent.actions}, ${jsEvent.ids});
    </c:when>
    <c:when test="${jsEvent.type eq 'Task Deleted'}">
    removeTasksFromTree(${jsEvent.hints});
    </c:when>
    <c:when test="${jsEvent.type eq 'Task Copied'}">
    copyTasksInTree(${jsEvent.hints}, ${jsEvent.copiedHints});
    </c:when>
    <c:when test="${jsEvent.type eq 'Task Copied Recursively'}">
    copyRecursivelyTasksInTree(${jsEvent.hints}, ${jsEvent.copiedHints});
    </c:when>
    <c:when test="${jsEvent.type eq 'Task Cut'}">
    cutTasksInTree(${jsEvent.hints}, ${jsEvent.copiedHints});
    </c:when>
    <c:when test="${jsEvent.type eq 'Task Pasted Cut'}">
    pasteCutTasksToTree(${jsEvent.hints}, ${jsEvent.names}, ${jsEvent.parentHint}, ${jsEvent.icons}, ${jsEvent.statusIcons}, ${jsEvent.actions}, ${jsEvent.ids});
    </c:when>
    <c:when test="${jsEvent.type eq 'Task Pasted Copied'}">
    pasteCopiedTasksToTree(${jsEvent.hints}, ${jsEvent.names}, ${jsEvent.parentHint}, ${jsEvent.icons}, ${jsEvent.statusIcons}, ${jsEvent.actions}, ${jsEvent.ids}, ${jsEvent.copiedHints});
    </c:when>
    <c:when test="${jsEvent.type eq 'Task Pasted Copied Recursively'}">
    pasteCopiedRecursivelyTasksToTree(${jsEvent.hints}, ${jsEvent.names}, ${jsEvent.parentHint}, ${jsEvent.icons}, ${jsEvent.statusIcons}, ${jsEvent.actions}, ${jsEvent.ids}, ${jsEvent.copiedHints});
    </c:when>
    <c:when test="${jsEvent.type eq 'Task Updated'}">
    updateTaskInTree(${jsEvent.hints}, ${jsEvent.names}, ${jsEvent.statusIcons});
    </c:when>
    <c:when test="${jsEvent.type eq 'User Added'}">
    self.top.frames[0].reloadTsUserTree(${jsEvent.parentHint});
    </c:when>
    <c:when test="${jsEvent.type eq 'User Deleted'}">
    self.top.frames[0].reloadTsUserTree(${jsEvent.parentHint});
    </c:when>
    <c:when test="${jsEvent.type eq 'User Renamed'}">
    self.top.frames[0].reloadTsUserTree(${jsEvent.parentHint});
    </c:when>
    <c:when test="${jsEvent.type eq 'User Cut'}">
    self.top.frames[0].selectUsersTsTree(${jsEvent.copiedHints});
    </c:when>
    <c:when test="${jsEvent.type eq 'User Pasted Cut'}">
    self.top.frames[0].reloadTsUserTree(${jsEvent.parentHint}, ${jsEvent.copiedHints});
    </c:when>
    </c:choose>
    </c:if>

    var taskPath = new Array();
    var taskTab = true;

    <c:if test="${jsTaskPath ne null}">
    <c:out value="${jsTaskPath}" escapeXml="false"/>
    </c:if>

    <c:if test="${jsUserPath ne null}">
    taskTab = false;
    <c:out value="${jsUserPath}" escapeXml="false"/>
    </c:if>

    scrollTree();
    $(function() {
	    $("#key").autocomplete({
		    source: function (request, response)
		    {
			    $.ajax(
					    {
						    url: '${contextPath}/predictor/',
						    dataType: "json",
						    html:true,
						    data:
						    {
							    key: request.term,
							    session: '${session}',
							    id: '${id}'
						    },
						    success: function (data)
						    {
							    response(data);
						    }
					    });
		    },
		    minLength: 1,
		    select: function( event, ui ) {
			    if (ui.item.value.toString().match("^u-")) {
				    document.location.href = '${contextPath}/UserAction.do?method=page&id=' + ui.item.value.toString().substr(2);
			    } else {
				    document.location.href = '${contextPath}/TaskAction.do?method=page&id=' + ui.item.value;
			    }
			    return false;
		    }
	    }).data("ui-autocomplete")._renderItem = function (ul, item) {
		    return $("<li></li>")
				    .data("item.autocomplete", item)
				    .append(item.label)
				    .appendTo(ul);
	    };
    });
</script>
<c:if test="${canCreateUser && !empty additionalPrstatuses}">
    <script type="text/javascript">
        var taskAddMenu = new TSMenu();
        taskAddMenu.width = 320;
        <c:forEach items="${additionalPrstatuses}" var="prstatus">
        taskAddMenu.add(new TSMenuItem("<I18n:message key="ADD_USER_WITH_PRSTATUS"/> <c:out value="${prstatus.name}"/>", "<c:out value="${contextPath}/UserEditAction.do?method=page&id=${id}&newUser=true&prstatus=${prstatus.id}"/>", false, false, "${contextPath}${ImageServlet}/cssimages/ico.adduser.gif"));
        </c:forEach>
        //tsMenu.add(new TSMenuItem("<I18n:message key="OTHER_ACTIONS"/>", "", taskAddMenu, false, "<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.customfields.gif"));
    </script>
</c:if>
<div class="login header">
    <table width="100%" style="height:25px;" cellpadding="0" cellspacing="0">
        <tr>
            <td>
                <div id="logoHeader" class="ts-brand-inline">
                    <span class="ts-brand-mark">TS</span>
                    <span class="ts-brand-name">TrackStudio</span>
                </div>
            </td>
            <td>
                <form method="post" style="display:inline;float:right;" action="<c:out value="${contextPath}"/>/TaskDispatchAction.do" id="searchForm" onsubmit="return !forbidEmptyLineSearch(this);">
                    <input type="hidden" name="method" value="go"/>
                    <input type="hidden" name="id" value="<c:out value="${userId}"/>"/>
                    <input type="hidden" name="searchIn" value="users"/>
                    <input type="hidden" name="session" value="<c:out value="${session}"/>"/>
                    <script type="text/javascript">
                        function submitSearchForm(){
                            return document.forms['searchForm'].submit();
                        }
                    </script>
                    <div style="vertical-align:top;padding-right:10px;">
                        <input type="text" style="font-weight: bold;" class="form-autocomplete" name="key" id="key" size="46" value="${key}">
                    </div>
                </form>
            </td>
        </tr>
        <tr style="height:5px;">
            <td colspan="2">
                <div style="float:right;">
                    <c:if test="${sc.user.login != 'anonymous'}">
                        <I18n:message key="LOGGED_INFO"/>
                        <html:link href="javascript:{self.top.frames[1].location = '${contextPath}/UserViewAction.do?id=${sc.userId}'};" style="vertical-align:bottom;color:#000000;">
                            <c:out value="${sc.user.name}"/>
                        </html:link>.&nbsp;<I18n:message key="YOUR_EFFECTIVE_PRSTATUSES"/>&nbsp;<div style="display:inline;vertical-align:bottom;color:#000000;">&nbsp;[&nbsp;${prstatuses}&nbsp;]</div>&nbsp;
                    </c:if>
                    <c:if test="${regRole && sc.user.login == 'anonymous'}">
                        <a style="color:#000000;" href="${contextPath}/LoginAction.do?method=registerPage"><I18n:message key="REGISTER"/></a>
                    </c:if>
                    <html:link style="padding-right:10px;" href="${contextPath}/LoginAction.do?method=logoutPage">
                        <c:choose>
                            <c:when test="${sc.user.login == 'anonymous'}">
                                <I18n:message key="LOG_IN"/>
                            </c:when>
                            <c:otherwise>
                                <I18n:message key="LOGOUT"/>
                            </c:otherwise>
                        </c:choose>&nbsp;
                    </html:link>&nbsp;
                </div>
            </td>
        </tr>
    </table>
</div>
<div>
    <div class="controlPanel">
        <span id="topleft" onclick="showTree();">
            <span id="closepanel" class="tree-toggle-button" aria-label="<I18n:message key="CLOSE"/>" title="<I18n:message key="CLOSE"/>">&#x276E;</span>
            <span id="openpanel" class="tree-toggle-button" aria-label="<I18n:message key="OPEN"/>" title="<I18n:message key="OPEN"/>"
                  style="display: none">&#x276F;</span>
            <script type="text/javascript">
                try {
                    if (parent.document.getElementsByTagName("frameset")[0] && typeof parent.document.getElementsByTagName("frameset")[0].attributes['border'] != "undefined" && parent.document.getElementsByTagName("frameset")[0].attributes['border'].value == '0') {
                        document.getElementById('closepanel').style.display='none';
                        document.getElementById('openpanel').style.display='inline';
                    } else {
                        document.getElementById('closepanel').style.display='inline';
                        document.getElementById('openpanel').style.display='none';
                    }
                } catch (e) {
                    document.getElementById('closepanel').style.display='none';
                    document.getElementById('openpanel').style.display='inline';
                }
            </script>
        </span>
        <script type="text/javascript">
//            var isShowTree = getTabCookie("isShowTree");
//            if (isShowTree == "false") {
//                showTree();
//            }
        </script>
        <span class="mainmenu">
        <script type="text/javascript">
            <c:set var="urlHtml" value="html"/>
            <c:if test="${user != null && currentTab=='TAB_USER_OVERVIEW'}">
            var userPermLink = '<c:out value="${user.permLink}" escapeXml="false"/>';
            </c:if>
            <c:if test="${canViewUserList}">
            tsMenu.add(new TSMenuItem("<I18n:message key="USERS_LIST"/>", "<c:out value="${contextPath}"/>/UserListAction.do?method=page&amp;id=<c:out value="${currentUser.id}"/> ", false, false, "<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.userlist.gif"));
            </c:if>
            <c:if test="${canViewUser && sc.user.login != 'anonymous'}">
            tsMenu.add(new TSMenuItem("<I18n:message key="VIEW_USER"/>", "<c:out value="${contextPath}"/>/UserViewAction.do?method=page&amp;id=<c:out value="${currentUser.id}"/> ", false, false, "<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.userinfo.gif"));
            </c:if>
            <c:if test="${canViewUserFilters}">
            tsMenu.add(new TSMenuItem("<I18n:message key="USER_FILTERS_LIST"/>", "<c:out value="${contextPath}"/>/UserFilterAction.do?method=page&amp;id=<c:out value="${currentUser.id}"/> ", false, false, "<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.userfilters.gif"));
            </c:if>
            <c:if test="${canViewUserACL || canViewUserCustomization || canViewStatuses || canManageRegistrations}">
            tsMenu.add(new TSMenuSeparator());
            </c:if>
            <c:if test="${canViewUserACL}">
            tsMenu.add(new TSMenuItem("<I18n:message key="ACCESS_CONTROL_LIST"/>", "<c:out value="${contextPath}"/>/UserACLAction.do?method=page&amp;id=<c:out value="${currentUser.id}"/> ", false, false, "<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.accesscontrol.gif"));
            tsMenu.add(new TSMenuItem("<I18n:message key="EFFECTIVE_PRSTATUSES"/>", "<c:out value="${contextPath}"/>/UserEffectivePermissionAction.do?method=page&amp;id=<c:out value="${currentUser.id}"/> ", false, false, "<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.effective.gif"));
            </c:if>
            <c:if test="${canViewUserCustomization}">
            tsMenu.add(new TSMenuItem("<I18n:message key="CUSTOM_FIELDS_LIST_USER"/>", "<c:out value="${contextPath}"/>/UserCustomizeAction.do?id=<c:out value="${currentUser.id}"/>", false, false, "<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.customfields.gif"));
            </c:if>
            <c:if test="${canViewStatuses}">
            tsMenu.add(new TSMenuItem("<I18n:message key="PRSTATUSES_LIST"/>", "<c:out value="${contextPath}"/>/UserStatusAction.do?method=page&amp;id=<c:out value="${currentUser.id}"/>", false, false, "<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.status.gif"));
            </c:if>
            <c:if test="${canAllowedByUser && sc.user.login != 'anonymous'}">
            tsMenu.add(new TSMenuItem("<I18n:message key="EMERGENCY_NOTICE"/>", "<c:out value="${contextPath}"/>/UserEmegencyNoticeAction.do?method=page&amp;id=<c:out value="${currentUser.id}"/>", false, false, "<c:out value="${contextPath}"/>${ImageServlet}/cssimages/exclamation.gif"));
            </c:if>
            tsMenu.add(new TSMenuSeparator());
            tsMenu.add(new TSMenuItem("<I18n:message key="TASK_MANAGEMENT"/>", "<c:out value="${contextPath}"/>/SubtaskAction.do?method=page&amp;id=<c:out value="${tci.id}"/>", false, false, "${contextPath}${ImageServlet}/${urlHtml}/xtree/images/taskMGM.gif"));
            var myBar = new TSMenuBar;
            myBar.add(new TSMenuBut("<I18n:message key="USER_MANAGEMENT"/>", null, tsMenu, "${contextPath}${ImageServlet}/${urlHtml}/xtree/images/userMGM.gif"));
            document.write(myBar);
        </script>
    </span>
        <span class="separator">&nbsp;</span>
        <c:if test="${sc.user.login != 'anonymous'}">
            <c:choose>
                <c:when test="${tabUserList.selected}">
                    <c:if test="${canViewUser}">
                        <html:link  href="${contextPath}/UserViewAction.do?method=page&amp;id=${id}"><html:img  src="${contextPath}${ImageServlet}/cssimages/ico.userinfo.gif" border="0" altKey="VIEW_USER"/><I18n:message key="VIEW_USER"/></html:link>
                    </c:if>
                </c:when>
                <c:otherwise>
                    <c:if test="${canViewUserList}">
                        <html:link  href="${contextPath}/UserListAction.do?method=page&amp;id=${id}"><html:img  src="${contextPath}${ImageServlet}/cssimages/ico.userlist.gif" border="0" altKey="USERS_LIST"/>
                            <I18n:message key="USERS_LIST"/>
                        </html:link>
                    </c:if>
                </c:otherwise>
            </c:choose>
            <c:if test="${canCreateUser && !empty prstatusesCreate}">
                <c:forEach items="${prstatusesCreate}" var="prstatus">
                    <a  href="${contextPath}/UserEditAction.do?method=page&amp;id=${id}&newUser=true&amp;prstatus=${prstatus.id}"><html:img  src="${contextPath}${ImageServlet}/cssimages/ico.adduser.gif" border="0" altKey="ADD_USER_WITH_PRSTATUS"  hspace="0" vspace="0"/><I18n:message key="ADD_USER_WITH_PRSTATUS"/> <c:out value="${prstatus.name}"/></a>
                </c:forEach>

            </c:if>
            <c:if test="${canCreateUser && !empty additionalPrstatuses}">
    <span class="additional">
    <script type="text/javascript">
        var taskAddBar =new TSMenuBar();
        taskAddBar.add(new TSMenuBut("<I18n:message key="OTHER_ACTIONS"/>", null, taskAddMenu, "<c:out value="${contextPath}"/>${ImageServlet}/cssimages/add.png"));
        document.write(taskAddBar);
    </script>
    </span>
            </c:if>
        </c:if>
    </div>
    <div class="logopath">
        <c:forEach var="user" items="${currentUser.ancestors}" varStatus="varCounter">
            <html:link styleClass="internal" href="${contextPath}/user/${user.login}?thisframe=true" title="${user.login}">
                <c:out value="${user.name}" escapeXml="true"/>
            </html:link>&nbsp;/
        </c:forEach>
    </div>
    <div class="taskTitle">
        <html:link styleClass="internal" href="${contextPath}/user/${currentUser.login}?thisframe=true" title="${currentUser.login}">
            <html:img  border="0" src="${contextPath}${ImageServlet}/cssimages/${currentUser.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
            <c:out value="${currentUser.name}" escapeXml="true"/>
        </html:link>
    </div>
    <c:if test="${isNotices}">
        <table cellspacing="0" cellpadding="0" class="notice">
            <caption>
                <I18n:message key="EMERGENCY_NOTICE"/>
            </caption>
            <tbody>
            <c:forEach items="${notices}" var="notice">
                <tr>
                    <td>
                        <div><b><c:out value="${notice.key}"/></b></div>
                        <div class="notice"><pre><c:out value="${notice.value}"/></pre></div>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:if>
</div>