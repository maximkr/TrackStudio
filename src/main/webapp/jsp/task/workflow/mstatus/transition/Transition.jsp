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
			<div class="indent">

				<script type="text/javascript">

					var param = 'wf=${flow.id}' + '&ms=${mstatus.id}' + '&sc=${sc.id}';
					var url = '${contextPath}/TransitionAction.do?';

					function request() {
						updateTransition(param, url);
					}

					function updateTransition (param, url) {
						var upParam = param + '&update=true';
						if ($('#start').val().length > 0 && $('#finish').val() != null) {
							var options = $('#start').val();

							for (var i = 0; i < options.length; i++) {
								upParam = upParam + '&start=' + options[i];
							}

							upParam = upParam + '&finish=' + $('#finish').val();

							$.ajax("${contextPath}/transitionCreate", {
								data : upParam,
								method : 'get',
								success: function(data) {
									onResponse();
								}
							});
						}
					}

					function onResponse() {
						startSelectRequest(param, url);
						tableRequest(param, url);
					}

					function startSelectRequest() {
						$.ajax(url, {
							data : param + '&select=true&method=start',
							success: function(data) {
								$('#ajaxStartSelect').html(data);
							}
						});
					}

					function tableRequest(param, url) {
						$.ajax(url + param + '&table=true&method=table', {
							method: 'get',
							success: function(data) {
								$('#ajaxTable').html(data);
							}
						});
					}

				</script>

				<div class="blueborder">
					<div class="caption"><I18n:message key="CREATE_TRANSITION"/></div>
					<div class="indent">

						<table class="allowdeny">
							<tr>
								<td>
									<table>
										<tr>
											<th class="allowed">
												<I18n:message key="START"/>
											</th>
										</tr>
										<td>
											<div id="ajaxStartSelect">
												<c:import url="/jsp/task/workflow/mstatus/transition/StartStatus.jsp"/>
											</div>
										</td>
									</table>
								</td>
								<td>
									<table>
										<tr>
											<th class="allowed">
												<I18n:message key="FINAL"/>
											</th>
										</tr>
										<td>
											<select Id="finish"
											        size="6"
											        class="monospaced fixedwidth"
											        style="width:auto;">
												<c:forEach var="stateSelect" items="${finalStateList}">
													<option value="${stateSelect.id}">
														<c:out value="${stateSelect.name}" escapeXml="true"/>
													</option>
												</c:forEach>
											</select>
										</td>
									</table>
								</td>
							</tr>
						</table>
					</div>
				</div>

				<div class="controls">
					<input type="button" onclick="request()" class="iconized"
					       value="<I18n:message key="CREATE"/>"
					       name="CRTATE">
				</div>

				<html:form action="/TransitionAction" method="post" styleId="transitionForm" onsubmit="return onSubmitFunction();">
					<html:hidden property="method" value="delete"/>
					<html:hidden property="id" value="${id}"/>
					<html:hidden property="session" value="${session}"/>
					<html:hidden property="workflowId" value="${flow.id}"/>
					<html:hidden property="mstatusId" value="${mstatus.id}"/>
					<div id="ajaxTable">
						<c:import url="/jsp/task/workflow/mstatus/transition/TableStatus.jsp"/>
					</div>
					<script type="text/javascript">
						var submitForm = false;

						function onSubmitFunction() {
							return submitForm;
						}

						function checkDeleteSelected() {
							submitForm = deleteConfirmForCurrentForm('<I18n:message key="DELETE_TRANSITION_REQ"/>', document.getElementById('transitionForm'));
							return submitForm;
						}
					</script>
				</html:form>
			</div>
		</div>
	</tiles:put>
</tiles:insert>
