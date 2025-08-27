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
	<tiles:put name="customHeader" type="string"/>
	<tiles:put name="tabs" type="string"/>
	<tiles:put name="main" type="string">
		<c:if test="${canEdit}">
			<c:import url="/jsp/TinyMCE.jsp"/>
		</c:if>
		<div>
		<html:form method="POST" enctype="multipart/form-data" action="/TaskEditAction" styleId="checkunload"
		           onsubmit="if (validate(this)) {$(window).unbind('beforeunload'); showButton(true); return true;} else {return false}">
			<table width="100%">
				<tr>
					<td width="60%">
						<div class="caption" style="display:inline;border-color:#FFFFFF; font-size: 20px; background-color:#FFFFFF"></div>
						<input type="text" spellcheck="true" name="name" value='${tci.name}' styleId="name" style="font-size: 20px;width:60%;" property="name" maxlength="200" alt=">0"/>
					</td>
					<td width="40%" style="float:right;">
						<input type="submit" class="iconized" value="<I18n:message key="SAVE" />" name="SAVE"
						       disabled="true"/>
						<html:button styleClass="iconized secondary" property="cancelButton" disabled="true"
						             onclick="$(window).unbind('beforeunload'); showButton(true); document.location='${referer}';">
							<I18n:message key="CANCEL"/>
						</html:button>
					</td>
				</tr>
			</table>
			<div class="indent">
			<html:hidden property="method" value="saveDocument" styleId="editTaskId"/>
			<html:hidden property="id"/>
			<html:hidden property="session"/>
			<html:hidden property="parentForCancel"/>
			<html:hidden property="workflowId"/>
			<c:if test="${newTask eq true}">
				<html:hidden property="newTask" value="true"/>
				<html:hidden property="category"/>
			</c:if>
			<br>
			<c:choose>
				<c:when test="${canEditTaskDescription}">
					<html:textarea rows="50" property="description" styleClass="mceEditor" cols="150"
					               style="margin:0px; padding:0px; width: 100%;" styleId="description"></html:textarea>
				</c:when>
				<c:otherwise>
					<html:hidden property="description"/>
					<c:out value="${wikiDescription}" escapeXml='false'/>
				</c:otherwise>
			</c:choose>
			<c:if test="${canCreateTaskAttachments}">
				<br>
				<script type="text/javascript">
					var countFiles=0;
					function createNewForm() {
						var inn = document.getElementById("upload");

						var div = document.createElement("div");
						countFiles++;
						var fileinput = document.createElement("input");
						fileinput.type="file";
						fileinput.size=80;
						fileinput.name="file["+countFiles+"]";
						fileinput.onchange=createNewForm;

						var filedesc = document.createElement("input");
						filedesc.type="text";
						filedesc.size=80;
						filedesc.name="filedesc";



						div.appendChild(document.createTextNode("<I18n:message key="CHOOSE_FILE"/>"));
						div.appendChild(document.createElement("br"));
						div.appendChild(fileinput);
						div.appendChild(document.createElement("br"));
						div.appendChild(document.createTextNode("<I18n:message key="DESCRIPTION"/>"));
						div.appendChild(document.createElement("br"));
						div.appendChild(filedesc);
						div.appendChild(document.createElement("br"));
						inn.appendChild(div);
					}

					function createMultiForm(previoues) {
						countFiles++;
						showSelectedFiles(previoues);
						var inn = document.getElementById("upload");
						var div = document.createElement("div");
						var fileinput = document.createElement("input");
						fileinput.type="file";
						fileinput.size=80;
						fileinput.multiple='multiple';
						fileinput.name="file";
						fileinput.setAttribute('onchange', 'createMultiForm(this)');
						div.appendChild(document.createTextNode("<I18n:message key="CHOOSE_FILE"/>"));
						var span = document.createElement("span");
						span.id = 'span' + countFiles;

						var filedesc = document.createElement("input");
						filedesc.type="text";
						filedesc.size=80;
						filedesc.name="filedesc";

						div.appendChild(span);
						div.appendChild(document.createElement("br"));
						div.appendChild(fileinput);
						div.appendChild(document.createElement("br"));
						div.appendChild(document.createTextNode("<I18n:message key="DESCRIPTION"/>"));
						div.appendChild(document.createElement("br"));
						div.appendChild(filedesc);
						div.appendChild(document.createElement("br"));
						inn.appendChild(div);
					}
					function showSelectedFiles(el) {
						var files = el.files;
						var size = files.length;
						var html = '<I18n:message key="SELECTED"/> : ';
						for (var index=0;index!=size;++index) {
							html += '<span style="padding-left: 10px">' + files[index].name + '</span>';
						}
						var div = document.createElement("div");
						div.name = 'selects';
						div.innerHTML = html;
						var last = el.parentNode.childNodes[el.parentNode.childNodes.length-1];
						console.log(last.name);
						if (last.name == 'selects') {
							last.innerHTML = html;
						} else {
							el.parentNode.appendChild(div);
						}
					}

				</script>
				<table cellpadding="0" cellspacing="0">
					<tr>
						<td id="upload">
							<div>
								<I18n:message key="CHOOSE_FILE"/>
								<c:if test="${header['user-agent'] == 'MSIE'}">
									<input type="file" id="file0" name="file[0]" size="80" onchange="createNewForm()"/>
								</c:if>
								<c:if test="${header['user-agent'] != 'MSIE'}">
									<input type="file" multiple="multiple" name="file" onchange="createMultiForm(this);" />
								</c:if>
								<br>
							</div>
						</td>
					</tr>
				</table>
			</c:if>
		</html:form>
	</tiles:put>
</tiles:insert>
<script type="text/javascript">
	showButton(false);
	function showButton(result) {
		var save = document.getElementsByName("SAVE");
		for (var s = 0; s != save.length; s++) {
			save[s].disabled = result;
		}
		var cancel = document.getElementsByName("cancelButton");
		for (var c = 0; c != cancel.length; c++) {
			cancel[c].disabled = result;
		}
	}

	$(window).bind('beforeunload', function() {
		return true;
	});
</script>
