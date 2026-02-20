<%@ page buffer="128kb" errorPage="/jsp/Error.jsp" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value="${sc.timezone}"/>
<I18n:setBundle basename="language"/>

<script type="text/javascript">
	function testScheduler(className) {
		$.ajax('${contextPath}/ScriptAction.do?method=testScheduler&className=' + className, {
			method: 'get',
			success: function(data) {
				document.getElementById(className).innerHTML = data;
			}
		});
	}

	function editParam(key) {
		var selectEl = document.getElementById("cfield");
		for (var i=0;i!=selectEl.options.length;i++) {
			if (selectEl.options[i].text.toLowerCase().toString() === key.trim().toLowerCase().toString()) {
				selectEl.selectedIndex = i;
				var evt = document.createEvent("HTMLEvents");
				evt.initEvent("change", false, true);
				selectEl.dispatchEvent(evt);
				break;
			}
		}
	}

	function loadUdfForm(form) {
		var chb = document.getElementById('cfield');
		chb.disabled = !chb.disabled;
		document.getElementById('customizer').style.visibility = 'hidden';
		form['method'].value = 'changeField';
		$.ajax(form.action + "?"+ buildParam(form), {
			success: function(data) {
				$('#postFiltering').html(data);
			}
		});
	}

	function buildParam(form) {
		var poststr = "";
		for (var i = 0; i < form.elements.length; i++) {
			if (form.elements[i].type != "button" && form.elements[i].type != "submit" && form.elements[i].type != "reset") {
				if (form.elements[i].type == "select" || form.elements[i].type == "select-multiple") {
					for (var j = 0; j < form.elements[i].options.length; j++) {
						if (form.elements[i].options[j].selected) {
							poststr += form.elements[i].name + "=" + encodeURI(form.elements[i].options[j].value) + "&";
						}
					}
				} else {
					if (form.elements[i].alt != null && form.elements[i].alt.length != 0 && form.elements[i].alt.indexOf("listcheckbox") != -1) {
						if (form.elements[i].checked) {
							poststr += form.elements[i].name + "=" + encodeURI(form.elements[i].value) + "&";
						}
					} else if (form.elements[i].type == "checkbox") {
						poststr += form.elements[i].name + "=" + form.elements[i].value + "&";
					} else {
						if (encodeURI(form.elements[i].value).length != 0) {
							poststr += form.elements[i].name + "=" + encodeURI(form.elements[i].value) + "&";
						}
					}
				}
			}
		}
		return poststr.substr(0, poststr.length - 1);
	}

	function filterTaskUdf(content, popup) {
		try {
			var items = content.getElementsByTagName("item");
			var respNode = content.getElementsByTagName("response")[0];
			if (respNode.getAttribute("source")) {
				var source = respNode.getAttribute("source");
				var sourceId = source.substring(6);
				for (var i = 0; i < items.length; i++) {
					var item = items[i];
					if (item.getAttribute("class") && item.getAttribute("class") == "task" && (item.getAttribute("selected") == "true")) {
						var number = item.getElementsByTagName("number")[0].firstChild.nodeValue;
						if (number.indexOf(" ") != -1) {
							number = number.substr(number.indexOf(" ") + 1, number.length);
						}
						var udfTarget = source.substring("searchudflist(".length, source.length -1);
						var target = document.getElementById(udfTarget);
						if (target.value != "") {
							target.value = target.value + ";#" + number;
						} else {
							target.value = "#" + number;
						}
					}
				}
				document.getElementById(source).value = "";
			}
		} catch(err) {
			showError("filterTaskUdf", err);
		}
	}

	function submitPostFilter(apply, form) {
		if (validate(document.getElementById('filterForm'))) {
			if (apply) {
				document.getElementById('go').value = "true";
				document.getElementById('method').value = 'changeTaskFilter';
				document.getElementById('filterForm').submit();
			} else {
				new AjaxForm().submit(document.getElementById('filterForm'));
			}
		}
	}

	function forbidEmptyLineSearch(form) {
		return form.key.value == "";
	}

	function deleteUser() {
		TSDialog.confirm("<I18n:message key="DELETE_USER_REQ"><c:out value="${currentUser.name}" escapeXml="true"/></I18n:message>", function(ok) {
			if (ok) location.replace("<c:out value="${contextPath}/UserListAction.do?deleteButton=true&amp;method=deleteUsers&amp;id=${currentUser.id}&amp;SELUSER=${currentUser.id}" escapeXml='false'/>");
		});
	}

	function deleteTaskFromMenu() {
		TSDialog.confirm("<c:if test="${tci.childrenCount != null &&  tci.childrenCount > 0}"><I18n:message key="CONTAINS_SUBTASKS"/></c:if> <I18n:message key="DELETE_TASK"><I18n:param value="\#${tci.number}"/></I18n:message>", function(ok) {
			if (ok) document.location = "<c:out value="${contextPath}/SubtaskAction.do?method=delete&amp;DELETE=TRUE&amp;SELTASK=${tci.id}&amp;id=${tci.parentId}" escapeXml='false'/>";
		});
	}

	var tsMenuDefImagePath = '<c:out value="${contextPath}"/>${ImageServlet}/cssimages/';
	var tsMenuImagePath = '<c:out value="${contextPath}"/>${ImageServlet}/cssimages/';
</script>
<script type="text/javascript">
	<c:set var="urlHtml" value="html"/>
	<c:if test="${jsEvent ne null}">
	<c:choose>
	<c:when test="${jsEvent.type eq 'Task Added'}">
	self.top.frames[0].reloadTsTree(${jsEvent.parentHint});
	</c:when>
	<c:when test="${jsEvent.type eq 'Task Deleted'}">
	self.top.frames[0].reloadTsTree(${jsEvent.parentHint});
	</c:when>
	<c:when test="${jsEvent.type eq 'Task Copied'}">
	self.top.frames[0].selectNodesTsTree(${jsEvent.copiedHints});
	</c:when>
	<c:when test="${jsEvent.type eq 'Task Copied Recursively'}">
	self.top.frames[0].selectNodesTsTree(${jsEvent.copiedHints});
	</c:when>
	<c:when test="${jsEvent.type eq 'Task Cut'}">
	self.top.frames[0].selectNodesTsTree(${jsEvent.copiedHints});
	</c:when>
	<c:when test="${jsEvent.type eq 'Task Pasted Cut'}">
	self.top.frames[0].reloadTsTree(${jsEvent.parentHint});
	</c:when>
	<c:when test="${jsEvent.type eq 'Task Pasted Copied'}">
	self.top.frames[0].reloadTsTree(${jsEvent.parentHint});
	</c:when>
	<c:when test="${jsEvent.type eq 'Task Pasted Copied Recursively'}">
	self.top.frames[0].reloadTsTree(${jsEvent.parentHint});
	</c:when>
	<c:when test="${jsEvent.type eq 'Task Updated'}">
    self.top.frames[0].reloadTsTree(${jsEvent.parentHint});
	</c:when>
	<c:when test="${jsEvent.type eq 'User Added'}">
	self.top.frames[0].reloadTsTree(${jsEvent.parentHint});
	</c:when>
	<c:when test="${jsEvent.type eq 'User Deleted'}">
	self.top.frames[0].reloadTsTree(${jsEvent.parentHint});
	</c:when>
	<c:when test="${jsEvent.type eq 'User Renamed'}">
	self.top.frames[0].reloadTsTree(${jsEvent.parentHint});
	</c:when>
	<c:when test="${jsEvent.type eq 'User Cut'}">
	self.top.frames[0].reloadTsTree(${jsEvent.parentHint});
	</c:when>
	<c:when test="${jsEvent.type eq 'User Pasted Cut'}">
	self.top.frames[0].reloadTsTree(${jsEvent.parentHint});
	</c:when>
	</c:choose>
	</c:if>
	var taskPath = new Array();
	var taskTab = true;
	<c:if test="${jsTaskPath ne null}">
	var openTree = setInterval(function() {
		if (self.top.frames[0].TREE_LOADED) {
            console.log("try load inside");
            self.top.frames[0].expandTsTree(${jsTaskPath});
			clearTimeout(openTree);
		}
	}, 1000);
	</c:if>

	<c:if test="${jsUserPath ne null}">
	taskTab = false;
	<c:out value="${jsUserPath}" escapeXml="false"/>
	</c:if>

	$(function() {
		$("#key").bind("keydown", function( event ) {
			if ( event.keyCode === $.ui.keyCode.ENTER) {
				submitSearchForm();
			}
		}).autocomplete({
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
			minLength: 3,
			select: function( event, ui ) {
				var value = ui.item.value;
				if (ui.item.label === ui.item.value) {
                    document.location.href = '${contextPath}/TaskDispatchAction.do?method=go&id=${tci.id}&key=' + ui.item.value.replace(/<(?:.|\n)*?>/gm, '');
                } else if (value.match("^u-")) {
					document.location.href = '${contextPath}/UserAction.do?method=page&id=' + value.substr(value.indexOf('_') + 3);
				} else {
					document.location.href = '${contextPath}/TaskAction.do?method=page&id=' + value.substr(value.indexOf('_') + 1);
				}
				return false;
			},
			focus: function( event, ui ) {
				var value = ui.item.value;
				var label = '';
				if (value.match("^u-")) {
					label = value.substr(0, value.indexOf('_'));
				} else {
					label = value.substr(0, value.indexOf('_'));
				}
				$("#key").val(label);
			}
		}).data("ui-autocomplete")._renderItem = function (ul, item) {
			return $("<li></li>")
					.data("item.autocomplete", item)
					.append(item.label)
					.appendTo(ul);
		};
	});
</script>
<div class="login">
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
					<input type="hidden" name="id" value="<c:out value="${tci.id}"/>"/>
					<input type="hidden" name="searchIn" value="tasks"/>
					<input type="hidden" name="session" value="<c:out value="${session}"/>"/>
					<script type="text/javascript">
						function submitSearchForm(){
							if ($("#key").val() != "") {
								document.forms['searchForm'].submit();
							}
						}
					</script>
					<div class="link" style="vertical-align:top;padding-right:10px;">
						<input type="text" style="font-weight: bold;" class="form-autocomplete" name="key" id="key" size="46" value="${key}">
					</div>
				</form>
			</td>
		</tr>
		<tr>
			<td colspan="2">
				<div class="ts-header-user-row">
					<c:if test="${sc.user.login != 'anonymous'}">
						<span class="ts-header-user-info">
							<html:link href="javascript:{self.top.frames[1].location = '${contextPath}/UserViewAction.do?id=${sc.userId}';};" styleClass="ts-header-user-link">
								<c:out value="${sc.user.name}"/>
							</html:link>
							<c:if test="${not empty prstatuses}">
								<span class="ts-header-user-sep">·</span>
								<span class="ts-header-user-roles">${prstatuses}</span>
							</c:if>
						</span>
					</c:if>
					<c:if test="${regRole && sc.user.login == 'anonymous'}">
						<a class="ts-header-action-link" href="${contextPath}/LoginAction.do?method=registerPage"><I18n:message key="REGISTER"/></a>
					</c:if>
					<html:link styleClass="ts-header-action-link" href="${contextPath}/LoginAction.do?method=logoutPage">
						<c:choose>
							<c:when test="${sc.user.login == 'anonymous'}">
								<I18n:message key="LOG_IN"/>
							</c:when>
							<c:otherwise>
								<I18n:message key="LOGOUT"/>
							</c:otherwise>
						</c:choose>
					</html:link>
				</div>
			</td>
		</tr>
	</table>
</div>
<div class="controlPanel">
	<script type="text/javascript">
		var taskAddMenu = {};
	</script>
	<span id="topleft" onclick="showTree();">
                        <span id="closepanel" class="tree-toggle-button" aria-label="<I18n:message key="CLOSE"/>" title="<I18n:message key="CLOSE"/>"
							  style="display: inline">&#x276E;</span>
                        <span id="openpanel" class="tree-toggle-button" aria-label="<I18n:message key="OPEN"/>" title="<I18n:message key="OPEN"/>"
							  style="display: none">&#x276F;</span>
                        <script type="text/javascript">
	                        try{
								if (parent.document.getElementsByTagName("frameset")[0] && typeof parent.document.getElementsByTagName("frameset")[0].attributes['border'] != "undefined" && parent.document.getElementsByTagName("frameset")[0].attributes['border'].value == '0') {
									document.getElementById('closepanel').style.display='none';
									document.getElementById('openpanel').style.display='inline';
								} else {
									document.getElementById('closepanel').style.display='inline';
									document.getElementById('openpanel').style.display='none';
								}
							}catch (e){
								document.getElementById('closepanel').style.display='inline';
								document.getElementById('openpanel').style.display='none';
							}
                        </script>
                    </span>
	<script type="text/javascript">
		//        var isShowTree = getTabCookie("isShowTree");
		//        if (isShowTree == "false") {
		//            showTree();
		//        }
	</script>
	<span class="mainmenu">
                    <script type="text/javascript">
	                    //       var myBar = new TSMenuBar;
						<c:if test="${canViewSubtasks}">
						tsMenu.add(new TSMenuItem("<I18n:message key="TASKS_LIST"/>", "<c:out value="${contextPath}"/>/SubtaskAction.do?method=page&amp;id=<c:out value="${tci.id}"/>", false, false, "<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.subtasks.gif", "", ""));
						</c:if>
						<c:if test="${canArchive}">
							tsMenu.add(new TSMenuItem("<I18n:message key="ARCHIVED_TASKS"/>", "<c:out value="${contextPath}"/>/SubtaskAction.do?method=archiveView&amp;id=<c:out value="${tci.id}"/>", false, false, "<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.subtasks.gif", "", ""));
                        </c:if>
						<c:if test="${canViewTask}">
						tsMenu.add(new TSMenuItem("<I18n:message key="TASK"/>", "<c:out value="${contextPath}"/>/TaskViewAction.do?method=page&amp;id=<c:out value="${tci.id}"/>", false, false, "<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.task.gif", "", ""));
						</c:if>
						<c:if test="${canViewTaskFilters}">
						tsMenu.add(new TSMenuItem("<I18n:message key="TASK_FILTERS_LIST"/>", "<c:out value="${contextPath}"/>/TaskFilterAction.do?method=page&amp;id=<c:out value="${tci.id}"/>", false, false, "<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.filters.gif", "", ""));
						</c:if>
						<c:if test="${canViewReports}">
						tsMenu.add(new TSMenuItem("<I18n:message key="REPORTS_LIST"/>", "<c:out value="${contextPath}"/>/ReportAction.do?method=page&amp;id=<c:out value="${tci.id}"/>", false, false, "<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.reports.gif", "", ""));
						</c:if>
						<c:if test="${canViewTask}">
						tsMenu.add(new TSMenuItem("<I18n:message key="SIMILAR"/>", "<c:out value="${contextPath}"/>/SimilarAction.do?method=page&amp;id=<c:out value="${tci.id}"/>", false, false, "<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.similar.gif", "", ""));
						</c:if>
						<c:if test="${canManageEmailSchedules}">
						tsMenu.add(new TSMenuSeparator());
						tsMenu.add(new TSMenuItem("<I18n:message key="NOTIFICATIONS_LIST"/>", "<c:out value="${contextPath}"/>/TaskNotifyAction.do?method=page&amp;id=<c:out value="${tci.id}"/>", false, false, "<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.notifications.gif", "", ""));
						tsMenu.add(new TSMenuItem("<I18n:message key="SUBSCRIPTIONS_LIST"/>", "<c:out value="${contextPath}"/>/TaskSubscribeAction.do?method=page&amp;id=<c:out value="${tci.id}"/>", false, false, "<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.subscription.gif", "", ""));
						</c:if>
						<c:if test="${canViewACL || canViewTaskCustomization || canMailImport || canCategory || canWorkflow || canExport || canTaskTemplate || canManageRegistrations}">
						tsMenu.add(new TSMenuSeparator());
						</c:if>
						<c:if test="${canViewACL}">
						<c:if test="${canEditTaskACL}">
						tsMenu.add(new TSMenuItem("<I18n:message key="ACCESS_CONTROL_LIST"/>", "<c:out value="${contextPath}"/>/ACLAction.do?method=page&amp;id=<c:out value="${tci.id}"/>", false, false, "<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.accesscontrol.gif", "", ""));
						</c:if>
						tsMenu.add(new TSMenuItem("<I18n:message key="EFFECTIVE_PRSTATUSES"/>", "<c:out value="${contextPath}"/>/EffectivePermissionAction.do?method=page&amp;id=<c:out value="${tci.id}"/>", false, false, "<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.effective.gif", "", ""));
						</c:if>
						<c:if test="${canViewTaskCustomization}">
						tsMenu.add(new TSMenuItem("<I18n:message key="CUSTOM_FIELDS_LIST_TASK"/>", "<c:out value="${contextPath}"/>/TaskCustomizeAction.do?id=<c:out value="${tci.id}"/>", false, false, "<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.customfields.gif", "", ""));
						</c:if>
						<c:if test="${canMailImport}">
						tsMenu.add(new TSMenuItem("<I18n:message key="EMAIL_IMPORT_LIST"/>", "<c:out value="${contextPath}"/>/MailImportAction.do?method=page&amp;id=<c:out value="${tci.id}"/>", false, false, "<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.emailimport.gif", "", ""));
						</c:if>
						<c:if test="${canManageRegistrations}">
						tsMenu.add(new TSMenuItem("<I18n:message key="REGISTRATIONS"/>", "<c:out value="${contextPath}"/>/RegistrationAction.do?method=page&amp;id=<c:out value="${tci.id}"/>", false, false, "<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.registration.gif", "", ""));
						</c:if>
						<c:if test="${canTaskTemplate}">
						tsMenu.add(new TSMenuItem("<I18n:message key="TEMPLATES_LIST"/>", "<c:out value="${contextPath}"/>/TemplateAction.do?method=page&amp;id=<c:out value="${tci.id}"/>", false, false, "<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.template.gif", "", ""));
						</c:if>
						<c:if test="${canCategory}">
						tsMenu.add(new TSMenuItem("<I18n:message key="CATEGORIES"/>", "<c:out value="${contextPath}"/>/CategoryAction.do?method=page&amp;id=<c:out value="${tci.id}"/>", false, false, "<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.categories.gif", "", ""));
						</c:if>
						<c:if test="${canWorkflow}">
						tsMenu.add(new TSMenuItem("<I18n:message key="WORKFLOWS"/>", "<c:out value="${contextPath}"/>/WorkflowAction.do?method=page&amp;id=<c:out value="${tci.id}"/>", false, false, "<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.workflow.gif", "", ""));
						</c:if>
						<c:if test="${canViewAttachment}">
						tsMenu.add(new TSMenuItem("<I18n:message key="ATTACHMENTS"/>", "<c:out value="${contextPath}"/>/AttachmentBrowserAction.do?method=page&amp;id=<c:out value="${tci.id}"/>", false, false, "<c:out value="${contextPath}"/>${ImageServlet}/cssimages/ico.attachment.png", "", ""));
						</c:if>
						<c:if test="${viewScriptsBrowser}">
						tsMenu.add(new TSMenuItem("<I18n:message key="LIST_TIRGGERS"/>", "<c:out value="${contextPath}"/>/ScriptAction.do?method=page&amp;id=<c:out value="${tci.id}"/>", false, false, "<c:out value="${contextPath}"/>${ImageServlet}/cssimages/script.gif", "", ""));
						</c:if>
						<c:if test="${viewTemplatesBrowser}">
						tsMenu.add(new TSMenuItem("<I18n:message key="LIST_TEMPLATES"/>", "<c:out value="${contextPath}"/>/TemplatesAction.do?method=page&amp;id=<c:out value="${tci.id}"/>", false, false, "<c:out value="${contextPath}"/>${ImageServlet}/cssimages/script.gif", "", ""));
						</c:if>
						<c:if test="${canViewUserList}">
						tsMenu.add(new TSMenuSeparator());
						tsMenu.add(new TSMenuItem("<I18n:message key="USER_MANAGEMENT"/>", "<c:out value="${contextPath}"/>/UserListAction.do?method=page&amp;id=<c:out value="${currentUser.id}"/> ", false, false, "${contextPath}${ImageServlet}/${urlHtml}/xtree/images/userMGM.gif", "", ""));
						</c:if>

						var myBar =new TSMenuBar();
						myBar.add(new TSMenuBut("<I18n:message key="TASK_MANAGEMENT"/>", null, tsMenu, "${contextPath}${ImageServlet}/${urlHtml}/xtree/images/taskMGM.gif"));
						document.write(myBar);
                    </script>
                    </span>
	<span class="separator">&nbsp;</span>
	<c:choose>
	<c:when test="${archive == null}">
	<c:if test="${showViewSubtasks}">
		<html:link  href="${contextPath}/SubtaskAction.do?method=page&amp;id=${id}">
			<html:img src="${contextPath}${ImageServlet}/cssimages/ico.subtasks.gif" border="0" altKey="VIEW"/>
			<I18n:message key="VIEW_SUBTASKS"/>
		</html:link>
	</c:if>
	<c:if test="${showViewTask}">
		<html:link  href="${contextPath}/TaskViewAction.do?method=page&amp;id=${id}">
			<html:img src="${contextPath}${ImageServlet}/cssimages/ico.task.gif" border="0" altKey="VIEW"/>
			<I18n:message key="TASK_OVERVIEW"/>
		</html:link>
	</c:if>
	<c:if test="${!empty categories}">
		<c:forEach items="${categories}" var="category">
			<c:choose>
				<c:when test="${fn:indexOf(category.action,'/')>0}">
					<c:set var="menuGroup" value="${fn:substringBefore(category.action,'/')}"/>
					<script type="text/javascript">
						if (!taskAddMenu['${menuGroup}']){
							taskAddMenu['${menuGroup}'] = new TSMenu();
							taskAddMenu['${menuGroup}'].width = 320;
						}
						taskAddMenu['${menuGroup}'].add(new TSMenuItem("${fn:substringAfter(category.action,'/')}", "<c:out value="${contextPath}/TaskEditAction.do?method=page&id=${id}&newTask=true&category=${category.id}"/>", false, false, "<c:out value="${contextPath}${ImageServlet}/icons/categories/${category.icon}"/>"));
					</script>
				</c:when>

				<c:otherwise>
					<c:choose>
						<c:when test="${fn:indexOf(category.preferences,'T')>-1}">
							<html:link  title="${category.description}"
										href="${contextPath}/TaskEditAction.do?method=page&amp;id=${id}&amp;newTask=true&amp;category=${category.id}">
								<html:img src="${contextPath}${ImageServlet}/icons/categories/${category.icon}" border="0" altKey="ADD"/>
								<c:choose>
									<c:when test="${category.action ne null && !empty category.action}">
										<c:out value="${category.action}"/>
									</c:when>
									<c:otherwise>
										<I18n:message key="ADD"/>
										<c:out value="${category.name}"/>
									</c:otherwise>
								</c:choose>
							</html:link>
						</c:when>
						<c:otherwise>
							<script type="text/javascript">
								if (!taskAddMenu['<I18n:message key="OTHER_CATEGORIES"/>']){
									taskAddMenu['<I18n:message key="OTHER_CATEGORIES"/>'] = new TSMenu();
									taskAddMenu['<I18n:message key="OTHER_CATEGORIES"/>'].width = 320;
								}
								taskAddMenu['<I18n:message key="OTHER_CATEGORIES"/>'].add(new TSMenuItem("<c:choose><c:when test="${category.action ne null && !empty category.action}"><c:out value="${category.action}"/></c:when><c:otherwise><I18n:message key="ADD"/>&nbsp;<c:out value="${category.name}"/></c:otherwise></c:choose>", "<c:out value="${contextPath}/TaskEditAction.do?method=page&id=${id}&newTask=true&category=${category.id}"/>", false, false, "<c:out value="${contextPath}${ImageServlet}/icons/categories/${category.icon}"/>"));
							</script>
						</c:otherwise>
					</c:choose>
				</c:otherwise>
			</c:choose>
		</c:forEach>
		<c:if test="${!empty categories}">
    <span class="additional">
    <script type="text/javascript">
	    var taskAddMenuBar =new TSMenuBar();
		for(var h in taskAddMenu){
			taskAddMenuBar.add(new TSMenuBut(h, null, taskAddMenu[h], "<c:out value="${contextPath}"/>${ImageServlet}/cssimages/add.png"));
		}
		document.write(taskAddMenuBar);
    </script>
    </span>
		</c:if>
	</c:if>
	</c:when>
		<c:otherwise>
			<html:link  href="${contextPath}/SubtaskAction.do?method=archiveView&amp;id=${tci.id}">
				<html:img src="${contextPath}${ImageServlet}/cssimages/ico.subtasks.gif" border="0" altKey="VIEW"/>
				<I18n:message key="ARCHIVED_TASKS"/>
			</html:link>
		</c:otherwise>
	</c:choose>

</div>
<c:choose>
	<c:when test="${archive == null}">
	<div class="logopath">
		<c:forEach var="task" items="${tci.ancestors}" varStatus="varCounter">
			<html:link styleClass="internal" href="${contextPath}/task/${task.number}?thisframe=true" title="#${task.number}">
				<c:out value="${task.name}" escapeXml="true"/>&nbsp;/
			</html:link>
		</c:forEach>
	</div>
	<div class="taskTitle">
		<c:if test="${ asView == null || asView == 'task'}"><html:link styleClass="internal" href="${contextPath}/task/${tci.number}?thisframe=true" title="#${tci.number}">
			<html:img   border="0" src="${contextPath}${ImageServlet}/icons/categories/${tci.category.icon}"/>
			<html:img styleClass="state" border="0" style="background-color: ${tci.status.color}" src="${contextPath}${ImageServlet}${tci.status.image}"/>
			<c:out value="${tci.name}" escapeXml="true"/><em class="number">[<c:if test="${!empty tci.shortname}"><span style="vertical-align: top;padding-left: 0;" id="alias"><c:out value="${tci.shortname}" escapeXml="true"/></span></c:if>#<c:out value="${tci.number}" escapeXml="true"/>]</em>
		</html:link></c:if>
	</div>
	</c:when>
	<c:otherwise>
		<div class="logopath">
			<c:out value="${archive.path}" escapeXml="true"/>
		</div>
		<div class="taskTitle">
			<c:if test="${ asView == null || asView == 'task'}"><html:link styleClass="internal" href="${contextPath}/TaskViewAction.do?id=1&archiveId=${archive.id}&method=archive" title="#${archive.number}">
				<html:img   border="0" src="${contextPath}${ImageServlet}/icons/categories/${archive.category.icon}"/>
				<html:img styleClass="state" border="0" style="background-color: ${archive.status.color}" src="${contextPath}${ImageServlet}${archive.status.image}"/>
				<c:out value="${archive.name}" escapeXml="true"/><em class="number">[<c:if test="${!empty archive.shortname}"><span style="vertical-align: top;padding-left: 0;" id="alias"><c:out value="${archive.shortname}" escapeXml="true"/></span></c:if>#<c:out value="${archive.number}" escapeXml="true"/>]</em>
			</html:link>
			<html:link styleClass="internal" href="${contextPath}/SubtaskAction.do?method=archiveView&amp;id=${tci.id}">
				| <I18n:message key="ARCHIVED"/>
				</html:link>
			</c:if>
		</div>
	</c:otherwise>
</c:choose>
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
					<div class="notice"><pre><c:out value="${notice.value}" escapeXml="false"/></pre></div>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
</c:if>


