<%@ taglib uri="struts/html-el" prefix="html" %>
<%@ taglib uri="jstl/c" prefix="c" %>
<%@ taglib uri="jstl/fmt" prefix="I18n" %>
<I18n:setLocale value='${sc.locale}'/>
<I18n:setTimeZone value='${sc.timezone}'/>
<I18n:setBundle basename='language'/>
<script language="javascript">

    function switchValue(Sender) {
        if (Sender.checked) {
            Sender.form.elements[Sender.value].disabled = true;
            Sender.form.elements[Sender.value].style.textDecoration = 'line-through';
        } else {
            Sender.form.elements[Sender.value].disabled = false;
            Sender.form.elements[Sender.value].style.textDecoration = 'none';
        }
        checkSelectBox(Sender.form);
    }


    function checkSelectBox(Frm) {
        var t = Frm.elements['addlist'].value.split("\n");
        var list = Frm.elements['def'].options;
        list.length = 0;
        var where = (navigator.appName == "Microsoft Internet Explorer") ? -1 : null;
        var opt = document.createElement("option");
        opt.value = "";
        opt.text = "<I18n:message key='CHOOSE_ONE'/>";
        list.add(opt, where);
        var listOld = Frm.elements;
        for (var i = 0; i < listOld.length; i++) {
            var element = listOld[i];
            if (element.className == 'oldvalue' && element.disabled == false) {
                var opt = document.createElement("option");
                opt.value = element.name.substring(6, element.name.length - 1);
                opt.text = element.value;
                list.add(opt, where);
            }
        }
        for (var i = 0; i < t.length; i++) {
            var str = t[i];
            if (str.indexOf("\r") > -1) str = str.substring(0, str.indexOf("\r"));
            if (str.length > 0) {
                var opt = document.createElement("option");
                opt.value = str + i;
                opt.text = str;
                list.add(opt, where);
            }
        }
        Frm.elements['def'].options[0].selected = true;
    }

    function cleanDefaultValue(checked) {
        var elSel = document.getElementById("udf${udf.id}");
        if (${type eq 3 || type eq 6}) {
            if (checked) {
                elSel.selectedIndex = 0;
                elSel.disabled = true;
            } else {
                elSel.disabled = false;
            }
        }
    }

    function showSelectScript(ch, name) {
        lookCalcEn(ch, false);
        $('selectScript').innerHTML = name;
        $('calculen').checked = true;
        $('cachevalues').disabled = false;
    }

    function showSelectScriptLookup(ch, name) {
        lookCalcEn(ch, true);
        $('showLookupScript').innerHTML = name;
        $('lookupen').checked = true;
    }

    function cleanSelectScript(check) {
        if (!check) {
            $('selectScript').innerHTML = "";
        }
    }

    function cleanSelectScriptLookup(check) {
        if (!check) {
            $('showLookupScript').innerHTML = "";
        }
    }

</script>
<c:import url="/jsp/TinyMCE.jsp"/>
<html:form method="post" styleId="checkunload" action="${editUdfAction}" onsubmit="return validate(this);">
<html:hidden property="session" value="${session}"/>
<html:hidden property="method" value="save" styleId="udfListId"/>
<html:hidden property="type"/>
<html:hidden property="udfId" value="${udfId}"/>
<html:hidden property="id" value="${id}"/>
<html:hidden property="workflowId" value="${workflowId}"/>
<c:if test="${createNewUdf ne null}">
    <html:hidden property="type" value="${type}"/>
    <html:hidden property="createNewUdf" value="true"/>
</c:if>
<div class="general">
<table class="general" cellpadding="0" cellspacing="0">
<caption><I18n:message key="GENERAL_SETTINGS"/></caption>
<COLGROUP>
    <COL class="col_1">
    <COL class="col_2">
</COLGROUP>
<tr>
    <th><label for="caption"><I18n:message key="CAPTION"/>*</label></th>
    <td>
        <c:choose>
            <c:when test="${_can_modify eq true}">
                <html:text styleId="caption" property="caption" maxlength="200" size="50" alt=">0"/>

            </c:when>
            <c:otherwise>
                <c:out value="${udf.caption}" escapeXml='true'/>
            </c:otherwise>
        </c:choose>
    </td>
</tr>
<c:if test="${(type eq 7) || (type eq 8)}">
    <tr>
        <th title="<I18n:message key="BACK_REFERENCE_CAPTION_COMMENT"/>"><I18n:message
                key="BACK_REFERENCE_CAPTION"/></th>
        <td>
            <c:choose>
                <c:when test="${_can_modify eq true}">
                    <html:text property="referencedbycaption" maxlength="200" size="50"/>
                </c:when>
                <c:otherwise>
                    <c:out value="${udf.referencedbycaption}" escapeXml='true'/>
                </c:otherwise>
            </c:choose>
            <span class="sample"><I18n:message key="FILL_BI_DIRECTIONAL"/></span>
        </td>
    </tr>
</c:if>
<tr>
    <th><I18n:message key="TYPE"/></th>
    <c:choose>
        <c:when test="${type eq 0}">
            <td><I18n:message key="UDF_STRING"/></td>
        </c:when>
        <c:when test="${type eq 1}">
            <td><I18n:message key="UDF_FLOAT"/></td>
        </c:when>
        <c:when test="${type eq 4}">
            <td><I18n:message key="UDF_INTEGER"/></td>
        </c:when>
        <c:when test="${type eq 2}">
            <td><I18n:message key="UDF_DATE"/></td>
        </c:when>
        <c:when test="${type eq 9}">
            <td><I18n:message key="UDF_URL"/></td>
        </c:when>
        <c:when test="${type eq 5}">
            <td><I18n:message key="UDF_MEMO"/></td>
        </c:when>
        <c:when test="${type eq 3}">
            <td><I18n:message key="UDF_LIST"/></td>
        </c:when>
        <c:when test="${type eq 6}">
            <td><I18n:message key="UDF_MULTILIST"/></td>
        </c:when>
        <c:when test="${type eq 7}">
            <td><I18n:message key="UDF_TASK"/></td>
        </c:when>
        <c:when test="${type eq 8}">
            <td><I18n:message key="UDF_USER"/></td>
        </c:when>
    </c:choose>
</tr>
<tr>
    <th><label for="order"><I18n:message key="ORDER"/></label></th>
    <td>
        <c:choose>
            <c:when test="${_can_modify eq true}">
                <html:text styleId="order" property="order" size="2" maxlength="3" alt="natural,>0"/>

            </c:when>
            <c:otherwise>
                <c:out value="${udf.order}"/>
            </c:otherwise>
        </c:choose>
    </td>
</tr>

<tr>
    <th><label for="required"><I18n:message key="REQUIRED"/></label></th>
    <td>
        <html:checkbox property="require" styleId="required"/>
    </td>
</tr>
<c:if test="${(type eq 0) || (type eq 5)}">
    <tr>
        <th><label for="htmlview"><I18n:message key="HTML_VIEW"/></label></th>
        <td>
            <html:checkbox property="htmlview" styleId="htmlview"/>
        </td>
    </tr>
</c:if>

<c:choose>
    <c:when test="${type eq 0}">
        <tr>
            <th><label for="udf<c:out value="${udf.id}"/>"><I18n:message key="DEFAULT"/></label></th>
            <td>
                <html:text styleId="udf${udf.id}" property="def" size="50" maxlength="${defaultLimit}"/>
            </td>
        </tr>
    </c:when>
    <c:when test="${type eq 1}">
        <tr>
            <th><label for="udf<c:out value="${udf.id}"/>"><I18n:message key="DEFAULT"/></label></th>
            <td>
                <html:text styleId="udf${udf.id}" property="def" alt="float" size="18" maxlength="40"/>
            </td>
        </tr>
    </c:when>
    <c:when test="${type eq 4}">
        <tr>
            <th><label for="udf<c:out value="${udf.id}"/>"><I18n:message key="DEFAULT"/></label></th>
            <td>
                <html:text styleId="udf${udf.id}" property="def" alt="integer" size="20" maxlength="40"/>
            </td>
        </tr>
    </c:when>
    <c:when test="${type eq 2}">
        <tr>
            <th><label for="udf<c:out value="${udf.id}"/>"><I18n:message key="DEFAULT"/></label></th>
            <td>
                <html:text styleId="udf${udf.id}" property="def" alt="date(${pattern})" size="20" maxlength="40"/>&nbsp;<html:img
                    src="${contextPath}${ImageServlet}/cssimages/ico.calendar.gif" border="0"
                    styleClass="calendaricon"
                    onclick="return showCalendar('udf${udf.id}', '${pattern}', '24', true);" altKey="SELECT_DATE"/>

            </td>
        </tr>
    </c:when>
    <c:when test="${type eq 9}">
        <tr>
            <th><label for="udf<c:out value="${udf.id}"/>"><I18n:message key="DEFAULT"/></label></th>
            <td>
                <html:text styleId="udf${udf.id}" property="def" size="50" alt="url" maxlength="${defaultLimit}"/>

            </td>
        </tr>
    </c:when>
    <c:when test="${type eq 5}">
        <tr>
            <th><label for="udf<c:out value="${udf.id}"/>"><I18n:message key="DEFAULT"/></label></th>
            <td>
                <c:choose>
                    <c:when test="${udf.htmlview}">
                        <html:textarea styleId="udf${udf.id}" styleClass="mceEditor" property="def" cols="70" rows="10" alt='<${defaultLimit},tinymce'/>
                    </c:when>
                    <c:otherwise>
                        <html:textarea styleId="udf${udf.id}" property="def" cols="70" rows="10" alt="<${defaultLimit}"/>
                    </c:otherwise>
                </c:choose>
            </td>
        </tr>
    </c:when>
    <c:when test="${type eq 3 || type eq 6}">
        <tr>
            <th><label for="udf<c:out value="${udf.id}"/>"><I18n:message key="DEFAULT"/></label></th>
            <td>
                <html:select styleId="udf${udf.id}" property="def" alt="select<${defaultLimit}">
                    <option value="" selected="true"><I18n:message key="CHOOSE_ONE"/></option>
                    <c:if test="${udflist ne null}">
                        <c:forEach var="ul" items="${udflist}">
                            <html:option value="${ul.key}">
                                <c:out value="${ul.value}"/>
                            </html:option>
                        </c:forEach>
                    </c:if>
                </html:select>

            </td>
        </tr>
    </c:when>
    <c:when test="${type eq 7}">
        <tr>
            <th><label for="udf<c:out value="${udf.id}"/>"><I18n:message key="INITIAL"/></label></th>
            <td>
                <c:choose>
                    <c:when test="${!empty customForm.initial}">
                        <html:text styleId="udf${udf.id}" property="initial" size="20" maxlength="${defaultLimit}"
                                   onfocus="if (this.value=='#') this.value='';"/>

                    </c:when>
                    <c:otherwise>
                        <html:text styleId="udf${udf.id}" property="initial" size="20" maxlength="${defaultLimit}"
                                   onfocus="if (this.value=='#') this.value='';" value="#"/>

                    </c:otherwise>
                </c:choose>
            </td>
        </tr>
    </c:when>

</c:choose>

<c:if test="${owner ne null}">
    <tr>
        <th><I18n:message key="OWNER"/></th>
        <td>
            <html:img src="${contextPath}${ImageServlet}/cssimages/arw.usr.a.gif"/>
            <c:out value="${owner}" escapeXml='true'/>
        </td>
    </tr>
</c:if>
</table>
<c:if test="${type eq 3 || type eq 6}">

    <table class="general" cellpadding="0" cellspacing="0">
        <caption><I18n:message key="LIST_VALUES"/></caption>
        <c:if test="${!empty udflist}"><c:forEach var="ul" items="${udflist}">
            <tr>
                <td><input type="checkbox" name="value${ul}" value="lists(${ul.key})" class="checkbox"
                           quickCheckboxSelectGroup="delete2" onclick="switchValue(this);"></td>
                <td><html:text styleClass="oldvalue" property="lists(${ul.key})" value="${ul.value}" maxlength="${defaultLimit}"
                               size="80" alt=">0" onchange="checkSelectBox(this.form);"/></td>
            </tr>
        </c:forEach>
        </c:if>
        <tr>
            <td>&nbsp;</td>
            <td>
                <textarea id="addlist" name="addlist" onchange="checkSelectBox(this.form);" cols=80 rows=5></textarea>
                <br/><span class="sample"><I18n:message key="USE_CTRL_V"/></span>
            </td>
        </tr>

    </table>
</c:if>
</div>

<div class="general">
    <table class="general" cellpadding="0" cellspacing="0">
        <COLGROUP>
            <COL class="col_1">
            <COL class="col_2">
        </COLGROUP>
        <caption><I18n:message key="CALCULATED_SETTINGS"/></caption>
        <tr>
            <th><label for="calculen"><I18n:message key="CALCULATE_CUSTOM_FIELD_VALUE"/></label></th>
            <td>
                <html:checkbox property="calculen" styleId="calculen"
                               onclick="lookCalcEn(this,false); cleanSelectScript(this.checked);"/>
                <div style="diplay:inline;" id="selectScript"></div>
            </td>
        </tr>
        <tr>
            <th><label><I18n:message key="SCRIPT"/></label></th>
            <td>
                <c:choose>
                    <c:when test="${!(empty scriptCollection)}">
                        <div class="selectbox">
                            <c:forEach items="${scriptCollection}" var="script" varStatus="c">
                                <label for="script_${c.index}" class="sel${c.index mod 2}">
                                    <html:radio property="script" value="${script.name}" styleId="script_${c.index}" onclick="showSelectScript(this, this.value);"/>
                                    <c:out value="${script.name}" escapeXml="true"/>
                                    <div class="sel${c.index mod 2}"><c:out value="${script.description}" escapeXml="true"/></div>
                                </label>
                            </c:forEach>
                            <script type="text/javascript"> if ('${udf.script}' == '') { document.getElementById("script_0").checked = true; }</script>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <I18n:message key="NO_SCRIPTS_AVAILABLE"/>
                    </c:otherwise>
                </c:choose>
            </td>
        </tr>
        <tr>
            <th><label for="cachevalues"><I18n:message key="CACHE_VALUES"/></label></th>
            <td>
                <html:checkbox property="cachevalues" disabled="${calculen}" styleId="cachevalues"/>
            </td>
        </tr>
    </table>
</div>

<div class="general">
    <table class="general" cellpadding="0" cellspacing="0">
        <COLGROUP>
            <COL class="col_1">
            <COL class="col_2">
        </COLGROUP>
        <caption><I18n:message key="LOOKUP_SETTINGS"/></caption>
        <tr>
            <th><I18n:message key="SHOW_LOOKUP_DROPDOWN"/></th>
            <td>
                <html:checkbox property="lookupen" styleId="lookupen"
                               onclick="lookCalcEn(this, true); cleanSelectScriptLookup(this.checked);"/>
                <div id="showLookupScript" style="display:inline;"></div>
            </td>
        </tr>
        <tr>
            <th><label><I18n:message key="LOOKUP_SCRIPT"/></label></th>
            <td>
                <c:choose>
                    <c:when test="${!(empty lookupscriptCollection)}">
                        <div class="selectbox">
                            <c:forEach items="${lookupscriptCollection}" var="lscript" varStatus="c">
                                <label for="lscript_${c.index}" class="sel${c.index mod 2}">
                                    <html:radio property="lscript" value="${lscript.name}" styleId="lscript_${c.index}" onclick="showSelectScriptLookup(this, this.value);"/>
                                    <c:out value="${lscript.name}" escapeXml="true"/>
                                    <div class="sel${c.index mod 2}"><c:out value="${lscript.description}" escapeXml="true"/></div>
                                </label>
                            </c:forEach>
                            <script type="text/javascript"> if ('${udf.lookupscript}' == '') { document.getElementById("lscript_0").checked = true; }</script>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <I18n:message key="NO_LOOKUP_SCRIPTS_AVAILABLE"/>
                    </c:otherwise>
                </c:choose>
            </td>
        </tr>
        <tr>
            <th><label for="lookuponly"><I18n:message key="LOOKUP_ONLY"/></label></th>
            <td>
                <html:checkbox property="lookuponly" styleId="lookuponly" disabled="${lookupen}"/>
            </td>
        </tr>
    </table>
</div>


<c:if test="${_can_modify eq true}">
    <div class="controls">
        <html:submit property="saveButton" styleClass="iconized"><I18n:message key="SAVE"/></html:submit>
        <html:button styleClass="iconized secondary" property="cancelButton" onclick="document.location='${contextPath}${cancelAction}?method=page&amp;id=${id}&amp;workflowId=${workflowId}&amp;udfId=${udfId}';">
            <I18n:message key="CANCEL"/>
        </html:button>
        <SCRIPT type="text/javascript">
            function set(target) {
                document.getElementById('udfListId').value = target;
            }
            ;
        </SCRIPT>
    </div>
</c:if>
</html:form>
