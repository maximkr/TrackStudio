<#macro I18n key value=[]>
<#if session?exists && value?exists>
${i18n.getString(session.locale?default("en"),key, value)}
<#else>
<#if session?exists>
${i18n.getString(session.locale?default("en"),key)}
<#else>
${i18n.getString("en", key)}
</#if>
</#if>
</#macro>
<#macro time date>
<span class="date">${DateFormatter.parse(date)}</span>
</#macro>
<#macro img src align="" class="" border="" hspace="" vspace="">
<#if Util.attachmentExists(src)>
<img src="${src}" <#if align?exists>align="${align}"</#if> <#if class?exists>class="${class}"</#if> <#if border?exists>border="${border}"</#if> <#if hspace?exists>hspace="${hspace}"</#if> <#if vspace?exists>vspace="${vspace}"</#if>/>
</#if>
</#macro>

<#macro subtasks localTask filter="All" search="">
<#if request?exists && request.value.sliderPage?exists>
<#assign page =request.value.sliderPage />
<#else>
<#assign page ="1"/>
</#if>
<#if search=="">
<#assign tList = Util.subtasks(localTask,filter, page)/>
<#else>
<#assign tList = Util.search(localTask, search, page)/>
</#if>
<#if !tList.isEmpty() && !tList.getCol().isEmpty()>
<#nested tList.getCol()/>
<#if contextPath?exists>
<#if defaultFilter?exists>
${tList.drawSlider(contextPath+"/template/"+template+"/task/"+task.number+"/filter/"+defaultFilter, "div", "slider")}
<#else>
<#if search!="">
${tList.drawSlider(contextPath+"/template/"+template+"/task/"+task.number+"/?__search="+search, "div", "slider")}
<#else>
${tList.drawSlider(contextPath+"/template/"+template+"/task/"+task.number, "div", "slider")}
</#if>
</#if>
</#if>
</#if>
</#macro>

<#macro messages localTask filter="All">
<#assign mList = Util.messages(localTask,filter)/>
<#if !mList.isEmpty()>
<#nested mList/>
</#if>
</#macro>

<#macro path from="" to="" separator="">
<#if to?exists && to!=""><#list Util.path(from, to) as pathElement><#if pathElement_has_next><#nested pathElement.getNumber(), pathElement.getName()>${separator}<#else><#nested pathElement.getNumber(), pathElement.getName()></#if></#list></#if>
</#macro>

<#macro script>
<#assign nest>
<#nested>
</#assign>
${Bsh.eval(nest)}
</#macro>

<#macro saveCookies field>
<input type="hidden" name="__save_cookies" value="${field}">
<#nested>
</#macro>
<#macro space width="20" align="left"><#if align=="left"><#assign align_code="-"/><#else><#assign align_code=""/></#if><#assign nest><#nested></#assign>${Util.format("%"+align_code+width+"s", nest)}</#macro>

