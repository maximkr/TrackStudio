<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<%@ taglib uri="http://trackstudio.com" prefix="ts" %>
<I18n:setLocale value="${sc.locale}"/>
<I18n:setTimeZone value="${sc.timezone}"/>
<I18n:setBundle basename="language"/>
<c:forEach var="entry" items="${udfMap}" varStatus="varCounter">
<c:set var="udf" value="${entry.key}"/>
<c:set var="edit" value="${entry.value}"/>
<c:set var="val" value="${udf.value}"/>
<c:if test="${edit || val != null}">
<tr id="udf_edit${udf.id}">
<th>
    <label for="udf${udf.id}"><c:out value="${udf.caption}"/><c:if test="${udf.required}">*</c:if></label>
</th>
<td>
<c:choose>
<c:when test="${udf.type eq 'string'}">
    <c:choose>
        <c:when test="${!edit}">
            <c:if test="${val ne null}">
                <c:out value="${val}" escapeXml="${!udf.htmlview}"/>
            </c:if>
        </c:when>
        <c:when test="${udf.lookup}">
            <c:if test="${!udf.lookupOnly}">
                <html:text styleId="udf(${udf.id})" property="udf(${udf.id})" size="80" maxlength="1800"
                           alt="${udf.required && !udf.lookupOnly ? '>0' : ''}"/>
            </c:if>
            <html:select styleId="script_udf(${udf.id})" onchange="copyToInput(this)" property="udf(${udf.id})"
                         alt="${udf.required && udf.lookupOnly ? '>0,mustChoose' : ''}">
                <html:option value=""><I18n:message key="CHOOSE_ONE"/></html:option>
                <c:forEach var="item" items="${udf.list}">
                    <html:option value="${item.key}"><c:out value="${item.value}"/></html:option>
                </c:forEach>
            </html:select>
            <script type="text/javascript">
                if (${udf.lookupOnly}) {
                    var el = document.getElementById("udf(${udf.id})");
                    if (el) {
                        el.value = document.getElementById("script_udf(${udf.id})").value;
                    }
                }
            </script>
        </c:when>
        <c:otherwise>
            <c:choose>
                <c:when test="${udf.calculated}">
                    <c:choose>
                        <c:when test="${udf.htmlview}">
                            <c:out value="${udf.value}" escapeXml="false"/>
                        </c:when>
                        <c:otherwise>
                            <pre><c:out value="${udf.value}"/></pre>
                        </c:otherwise>
                    </c:choose>
                </c:when>
                <c:otherwise>
                    <html:text property="udf(${udf.id})" styleId="udf${udf.id}" size="80" maxlength="1800"
                               alt="${udf.required ? '>0' : ''}"/>
                </c:otherwise>
            </c:choose>

        </c:otherwise>
    </c:choose>
</c:when>

<c:when test="${udf.type eq 'memo'}">
    <c:choose>
        <c:when test="${!edit}">
            <c:choose>
                <c:when test="${udf.htmlview}">
                    <ts:htmlfilter session="${sc.id}" macros="true" audit="false" request="<%=request%>">
                        <c:out value="${val}" escapeXml="false"/>
                    </ts:htmlfilter>
                </c:when>
                <c:otherwise>
                    <c:if test="${udf.htmlview}">
                        <ts:htmlfilter session="${sc.id}" macros="true" audit="false" request="<%=request%>">
                            <c:out value="${val}" escapeXml="false"/>
                        </ts:htmlfilter>
                    </c:if>
                    <c:if test="${!udf.htmlview}">
                        <span><c:out value="${val}" escapeXml="${!udf.htmlview}"/></span>
                    </c:if>
                </c:otherwise>
            </c:choose>
        </c:when>
        <c:otherwise>
            <c:if test="${!udf.calculated}">
                <c:choose>
                    <c:when test="${udf.htmlview}">
                        <html:textarea styleId="udf${udf.id}" styleClass="mceEditor" property="udf(${udf.id})" cols="70" rows="10" alt="${udf.required ? '>0' : ''}"/>
                    </c:when>
                    <c:otherwise>
                        <html:textarea styleId="udf${udf.id}" property="udf(${udf.id})" cols="70" rows="10" alt="${udf.required ? '>0' : ''}"/>
                    </c:otherwise>
                </c:choose>
            </c:if>
            <c:if test="${udf.calculated}">
                <c:choose>
                    <c:when test="${udf.htmlview}">
                        <c:out value="${udf.value}" escapeXml="false"/>
                    </c:when>
                    <c:otherwise>
                        <span><c:out value="${udf.value}"/></span>
                    </c:otherwise>
                </c:choose>
            </c:if>
        </c:otherwise>
    </c:choose>
</c:when>

<c:when test="${udf.type eq 'list'}">
    <c:choose>
        <c:when test="${!edit}">
            <c:out value="${val.value}" escapeXml="true"/>
        </c:when>
        <c:otherwise>
            <c:if test="${!udf.calculated}">
                <html:select property="udf(${udf.id})" styleId="udf${udf.id}" alt="${udf.required ? 'mustChoose(' : ''}udf${udf.id})">
                    <option value="NotChoosen" selected="true"><I18n:message key="CHOOSE_ONE"/></option>
                    <c:forEach var="item" items="${udf.list}">
                        <html:option value="${item.key}"><c:out value="${item.value}"/></html:option>
                    </c:forEach>
                </html:select>
            </c:if>
            <c:if test="${udf.calculated}">
                <c:out value="${udf.value}"/>
            </c:if>
        </c:otherwise>
    </c:choose>
</c:when>

<c:when test="${udf.type eq 'multilist'}">
    <c:choose>
        <c:when test="${!edit}">
            <c:forEach items="${val}" var="item" varStatus="c">
                <c:out value="${item.value}" escapeXml="true"/><c:if test="${!c.last}">,</c:if>
            </c:forEach>
        </c:when>
        <c:otherwise>
            <input type="hidden" alt="${udf.required ? 'divbox' : ''}" id="${udf.id}">
            <input type="text" class="form-autocomplete" name="searchudflist(${udf.id})" size="50"
                   onkeyup="__localsearch(this);">
            <div class="selectbox" id="udf${udf.id}">
                <c:forEach items="${udf.list}" var="item" varStatus="c">
                    <label for="${udf.caption}_${item.key}" class="sel${c.index mod 2}">
                        <html:multibox property="udflist(${udf.id})" value="${item.key}"
                                       styleId="${udf.caption}_${item.key}"/>
                        <c:out value="${item.value}" escapeXml="false"/>
                    </label>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</c:when>

<c:when test="${udf.type eq 'float'}">
    <c:choose>
        <c:when test="${!edit}">
            <I18n:formatNumber value="${val}" groupingUsed="true" maxFractionDigits="${decimalFormatUdfFloat}"/>
        </c:when>
        <c:when test="${udf.lookup}">
            <c:if test="${!udf.lookupOnly}">
                <html:text styleId="udf${udf.id}" property="udf(${udf.id})" alt="${udf.required ? '>0,' : ''}float"
                           size="80" maxlength="40"/>
            </c:if>
            <html:select styleId="script_udf(${udf.id})" onchange="copyToInput(this)" property="udf(${udf.id})"
                         alt="${udf.required && udf.lookupOnly ? '>0,mustChoose' : ''}">
                <html:option value=""><I18n:message key="CHOOSE_ONE"/></html:option>
                <c:forEach var="item" items="${udf.list}">
                    <html:option value="${item.key}"><c:out value="${item.value}"/></html:option>
                </c:forEach>
            </html:select>
            <script type="text/javascript">
                if (${udf.lookupOnly || udf.lookup}) {
                    var el = document.getElementById("udf(${udf.id})");
                    if (el) {
                        el.value = document.getElementById("script_udf(${udf.id})").value;
                    }
                }
            </script>
        </c:when>
        <c:otherwise>
            <c:choose>
                <c:when test="${!udf.calculated}">
                    <html:text styleId="udf${udf.id}" property="udf(${udf.id})" alt="${udf.required ? '>0,' : ''}float"
                               size="80" maxlength="40"/>
                </c:when>
                <c:otherwise>
                    <c:out value="${udf.value}"/>
                </c:otherwise>
            </c:choose>
        </c:otherwise>
    </c:choose>
</c:when>

<c:when test="${udf.type eq 'integer'}">
    <c:choose>
        <c:when test="${!edit}">
            <I18n:formatNumber value="${val}" groupingUsed="false" maxFractionDigits="0"/>
        </c:when>
        <c:when test="${udf.lookup}">
            <c:if test="${!udf.lookupOnly}">
                <html:text styleId="udf${udf.id}" property="udf(${udf.id})" alt="${udf.required ? '>0,' : ''}integer"
                           size="80" maxlength="40"/>
            </c:if>
            <html:select styleId="script_udf(${udf.id})" onchange="copyToInput(this)" property="udf(${udf.id})"
                         alt="${udf.required && udf.lookupOnly ? '>0,mustChoose' : ''}">
                <html:option value=""><I18n:message key="CHOOSE_ONE"/></html:option>
                <c:forEach var="item" items="${udf.list}">
                    <html:option value="${item.key}"><c:out value="${item.value}"/></html:option>
                </c:forEach>
            </html:select>
            <script type="text/javascript">
                if (${udf.lookupOnly || udf.lookup}) {
                    var el = document.getElementById("udf(${udf.id})");
                    if (el) {
                        el.value = document.getElementById("script_udf(${udf.id})").value;
                    }
                }
            </script>
        </c:when>
        <c:otherwise>
            <c:choose>
                <c:when test="${!udf.calculated}">
                    <html:text styleId="udf${udf.id}" property="udf(${udf.id})" alt="${udf.required ? '>0,' : ''}integer"
                               size="80" maxlength="40"/>
                </c:when>
                <c:otherwise>
                    <c:out value="${udf.value}"/>
                </c:otherwise>
            </c:choose>
        </c:otherwise>
    </c:choose>
</c:when>

<c:when test="${udf.type eq 'date'}">
    <c:choose>
        <c:when test="${!edit}">
            <I18n:formatDate value="${val.time}" type="both" dateStyle="short" timeStyle="short"/>
        </c:when>
        <c:when test="${udf.lookup}">
            <c:if test="${!udf.lookupOnly}">
                <html:text styleId="udf${udf.id}" property="udf(${udf.id})"
                           alt="${udf.required ? '>0,' : ''}date(${sc.user.dateFormatter.pattern2}" size="20"
                           maxlength="40"/>&nbsp;<html:img
                    src="${contextPath}${ImageServlet}/cssimages/ico.calendar.gif" border="0" styleClass="calendaricon"
                    onclick="return showCalendar('udf${udf.id}', '${sc.user.dateFormatter.pattern2}', '24', true);"
                    altKey="SELECT_DATE"/>
            </c:if>
            <html:select styleId="script_udf(${udf.id})" onchange="copyToInput(this)" property="udf(${udf.id})"
                         alt="${udf.required && udf.lookupOnly ? '>0,mustChoose' : ''}">
                <html:option value=""><I18n:message key="CHOOSE_ONE"/></html:option>
                <c:forEach var="item" items="${udf.list}">
                    <html:option value="${item.key}"><c:out value="${item.value}"/></html:option>
                </c:forEach>
            </html:select>
            <script type="text/javascript">
                if (${udf.lookupOnly || udf.lookup}) {
                    var el = document.getElementById("udf(${udf.id})");
                    if (el) {
                        el.value = document.getElementById("script_udf(${udf.id})").value;
                    }
                }
            </script>
        </c:when>
        <c:otherwise>
            <c:choose>
                <c:when test="${!udf.calculated}">
                    <html:text styleId="udf${udf.id}" property="udf(${udf.id})"
                               alt="${udf.required ? '>0,' : ''}date(${sc.user.dateFormatter.pattern2}" size="20"
                               maxlength="40"/>&nbsp;<html:img
                        src="${contextPath}${ImageServlet}/cssimages/ico.calendar.gif" border="0" styleClass="calendaricon"
                        onclick="return showCalendar('udf${udf.id}', '${sc.user.dateFormatter.pattern2}', '24', true);"
                        altKey="SELECT_DATE"/>
                </c:when>
                <c:otherwise>
                    <c:out value="${udf.value}"/>
                </c:otherwise>
            </c:choose>
        </c:otherwise>
    </c:choose>
</c:when>

<c:when test="${udf.type eq 'url'}">
    <c:choose>
        <c:when test="${!edit}">
            <html:link styleClass="internal" href="${val.link}" target="_blank">
                <c:out value="${val.description ne null ? val.description : val.link}"
                       escapeXml="true"/>
            </html:link>
        </c:when>
        <c:when test="${udf.lookup}">
            <c:if test="${!udf.lookupOnly}">
                <table>
                    <tr>
                        <td>
                            <I18n:message key="URL"/>
                        </td>
                        <td>
                            <html:text styleId="udf${udf.id}" property="udf(${udf.id})" size="80" maxlength="1800"
                                       alt="${udf.required ? 'url,>0' : 'url'}"/>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <I18n:message key="DESCRIPTION"/>
                        </td>
                        <td>
                            <html:text styleId="udf${udf.id}" property="url(${udf.id})" size="80" maxlength="1800"/>
                        </td>
                    </tr>
                </table>
            </c:if>
            <html:select styleId="script_udf(${udf.id})" onchange="copyToInput(this)" property="udf(${udf.id})"
                         alt="${udf.required && udf.lookupOnly ? '>0,mustChoose' : ''}">
                <html:option value=""><I18n:message key="CHOOSE_ONE"/></html:option>
                <c:forEach var="item" items="${udf.list}">
                    <html:option value="${item.key}"><c:out value="${item.value}"/></html:option>
                </c:forEach>
            </html:select>
            <script type="text/javascript">
                if (${udf.lookupOnly || udf.lookup}) {
                    var el = document.getElementById("udf(${udf.id})");
                    if (el) {
                        el.value = document.getElementById("script_udf(${udf.id})").value;
                    }
                }
            </script>
        </c:when>
        <c:otherwise>
            <table>
                <tr>
                    <td>
                        <I18n:message key="URL"/>
                    </td>
                    <td>
                        <html:text styleId="udf${udf.id}" property="udf(${udf.id})" size="80" maxlength="1800"
                                   alt="${udf.required ? 'url,>0' : 'url'}"/>
                    </td>
                </tr>
                <tr>
                    <td>
                        <I18n:message key="DESCRIPTION"/>
                    </td>
                    <td>
                        <html:text styleId="udf${udf.id}" property="url(${udf.id})" size="80" maxlength="1800"/>
                    </td>
                </tr>
            </table>
        </c:otherwise>
    </c:choose>
</c:when>
<c:when test="${udf.type eq 'task'}">
    <c:choose>
        <c:when test="${!edit}">
            <c:forEach var="t" items="${val}" varStatus="status">
                <c:choose>
                    <c:when test="${t.canManage}">
                        <html:link styleClass="internal"
                                   href="${contextPath}/TaskViewAction.do?method=page&amp;id=${t.id}">
                            <html:img styleClass="icon" border="0"
                                      src="${contextPath}${ImageServlet}/icons/categories/${t.category.icon}"/>
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
                <br>
            </c:forEach>
        </c:when>
        <c:otherwise>
            <c:if test="${!udf.lookupOnly}">
                    <span style="white-space: nowrap;">
         <input type="text" class="form-autocomplete" name="searchudflist(${udf.id})"
                size="50" maxlength="1800" id="searchudflist_${udf.id}" alt="${udf.required ? '>0' : ''}"/>
	                    <script type="text/javascript">
		                    $(function() {
			                    $("#searchudflist_${udf.id}").autocomplete({
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
					                    var label = document.createElement("label");
					                    var value = ui.item.value;
					                    label.innerHTML = "<input type='checkbox' name='udflist(${udf.id})' value='"+value.substr(1, value.indexOf('_')-1)+"' checked/>&nbsp;" + ui.item.label;
					                    var div = document.getElementById('_searchudflist(${udf.id})');
                                        var inputs = div.getElementsByTagName("input");
					                    var duplicate = false;
					                    for (var i=0;i!=inputs.length;++i) {
						                    if (!duplicate && value.indexOf(inputs[i].value) != -1) {
							                    duplicate = true;
							                    break;
						                    }
					                    }
					                    if (!duplicate) {
						                    if (div.innerHTML.indexOf(value) == -1) {
							                    div.style.display = 'block';
							                    div.appendChild(label);
						                    }
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
                        <html:image src="${contextPath}${ImageServlet}/cssimages/dot.gif" border="0" alt=""/>
                        <input type="image" id="IMGudf${udf.id}"
                               src="${contextPath}${ImageServlet}/cssimages/ico.selecttask.gif"
                               onclick="if (top.udfWin!=null) { top.udfWin.close(); top.udfWin=null;}; top.udfWin = window.open('${contextPath}/TaskSelectAction.do?udffield=searchudflist(${udf.id})&amp;udfvalue=${udf.id}', 'udfWin', 'dependent=yes,menubar=no,toolbar=no,status=no,scrollbars=yes,titlebar=no,left=0,top=20,width=1000,height=500,resizable=yes');is_new=true; return false;">
                    </span>
            </c:if>
            <div
                    <c:if test="${empty udf.list && empty udfsTasks}">style="display: none;"</c:if> class="selectbox shrinked"
                    id="_searchudflist(${udf.id})">
                <c:forEach items="${udf.list}" var="t" varStatus="c">
                    <label for="udflist(${udf.id})_${t.id}">
                        <input type="hidden" value="${t.updatedate.timeInMillis}"/>
                        <html:multibox property="udflist(${udf.id})" value="${t.number}" styleId="udflist(${udf.id})_${t.id}"/>
                        <c:choose>
                            <c:when test="${t.canManage}">
                                <html:img styleClass="icon" border="0" src="${contextPath}${ImageServlet}/icons/categories/${t.category.icon}"/>
                                <c:out value="${t.name}"/>&nbsp;[#<c:out value="${t.number}"/>]
                            </c:when>
                            <c:otherwise>
                                #<c:out value="${t.number}"/>
                            </c:otherwise>
                        </c:choose>
                    </label>
                </c:forEach>
                <c:if test="${udfsTasks[udf.id] ne null}">
                    <c:forEach items="${udfsTasks[udf.id]}" var="t" varStatus="c">
                        <label for="udflist(${udf.id})_${t.id}">
                            <input type="hidden" value="${t.updatedate.timeInMillis}"/>
                            <html:multibox property="udflist(${udf.id})" value="${t.number}" styleId="udflist(${udf.id})_${t.id}"/>
                            <c:choose>
                                <c:when test="${t.canManage}">
                                    <html:img styleClass="icon" border="0" src="${contextPath}${ImageServlet}/icons/categories/${t.category.icon}"/>
                                    <c:out value="${t.name}"/>&nbsp;<em class="number">[#<c:out value="${t.number}"/>]</em>
                                </c:when>
                                <c:otherwise>
                                    #<c:out value="${t.number}"/>
                                </c:otherwise>
                            </c:choose>
                        </label>
                    </c:forEach>
                </c:if>
            </div>
        </c:otherwise>
    </c:choose>
</c:when>

<c:when test="${udf.type eq 'user'}">
    <c:choose>
        <c:when test="${!edit}">
            <c:forEach var="user" items="${val}" varStatus="status">
                                    <span class="user">
                                        <html:img styleClass="icon" border="0"
                                                  src="${contextPath}${ImageServlet}/cssimages/${user.active ? 'arw.usr.a.gif' : 'arw.usr.gif'}"/>
                                        <c:out value="${user.name}"/>
                                    </span>
                <br>
            </c:forEach>
        </c:when>
        <c:otherwise>
            <input type="text" class="form-autocomplete" name="searchudflist(${udf.id})" size="50"
                   onkeyup="searchUserUdf('${udf.id}', this.value, SEARCH_DELAY)" alt="${udf.required ? '>0' : ''}" autocomplete='off'>
            <div class="selectbox" id="_searchudflist(${udf.id})" alt="${udf.required ? '>0' : ''}">
                <c:out value="${udf.buildDivBox}" escapeXml="false"/>
            </div>
        </c:otherwise>
    </c:choose>
</c:when>

</c:choose>
</c:if>
</td>
</tr>
</c:forEach>
<script type="text/javascript">

    function cleanUserUdf(udfId) {
        var div = document.getElementById("_searchudflist("+udfId+")");
        var size = div.children.length;
        var shouldClean = false;
        for (var i=0;i!=size;i++) {
            var labelId = div.children[i].id;
            if (shouldClean) {
                div.removeChild(div.children[i]);
                i--;
                size--;
            }
            if ("users_list_"+udfId == labelId) {
                shouldClean = true;
            }
        }
    }

    function buildUserLabel(content, udfId) {
        try {
            var labelUserList = document.getElementById("users_list_"+udfId);
			labelUserList.innerHTML = "<I18n:message key="USERS_LIST"/>";
            var row = 0;
            for (var i = 0; i < content.length; i++) {
                var item = content[i];
                var name = item.label;
                var id = item.value.substring(item.value.indexOf("_")+1, item.value.length);
                var login = id;
                var checkboxId = udfId + "_" + id;
                if (!document.getElementById(checkboxId)) {
                    var label = document.createElement('label');
                    label.className = "sel" + (row % 2);
                    label.htmlFor = checkboxId;
                    label.innerHTML = "<input type=\"checkbox\" onclick=\"moveToParticipate(this.id, this.checked, 'participants_');\" name=\"udflist("+udfId+")\" value=\""+login+"\" id=\""+checkboxId+"\">" +
                            "<span class=\"user\">"+name+"</span>" +
                            "</label>";
                    labelUserList.appendChild(label);
                    row++;
                }
            }
        } catch (err) {
            showError("buildUserLabel", err);
        }
    }

    var sender;

    function searchUserUdf(udfId, value, dl) {
        cleanUserUdf(udfId);
        var delay = parseInt(dl);
        if (delay > 0) {
            if (sender != null) {
                clearTimeout(sender);
            }
            sender = setTimeout(
                    function() {
                        $.ajax('${contextPath}/predictor/', {
                            dataType: "json",
                            data: {key : value, byUdf : true, udfId : udfId, taskId : '${id}', create : '${isNew}', workflowId : '${workflowId}'},
                            success: function (data) {
                                buildUserLabel(data, udfId);
                            }
                        });
                    },
                    delay);
        } else {
            $.ajax('${contextPath}/predictor/', {
                dataType: "json",
                data: {key : value, byUdf : true, udfId : udfId, taskId : '${id}', create : '${isNew}', workflowId : '${workflowId}'},
                success: function (data) {
                    buildUserLabel(data, udfId);
                }
            });
        }
    }
</script>