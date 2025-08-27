
<@std.script>
if (request.get("value").get("method")!=null && request.get("value").get("method").equals("task_comment")) {
	String prefix="";
	String description_1 = request.get("value").get("forms_task_comment");
	if (description_1!=null && description_1.length()>0) {
		if (request.get("value").get("temp_user_name")!=null) {
			prefix+="From: "+request.get("value").get("temp_user_name")+"\n";
		}
		if (request.get("value").get("temp_user_email")!=null) {
			prefix+="Email: "+request.get("value").get("temp_user_email")+"\n";
		}
		String taskId = task.getId();
		SecuredMessageTriggerBean input = new SecuredMessageTriggerBean(null,      prefix+description_1, null,          null,     null,              null,        taskId,        sc.getUserId(),     null,                null,              null,             null,                 null,                  CSVImport.findMessageTypeIdByName("note", task.getCategory().getName()), null, task.getSecure(), null).create(false);
                                   //  public SecuredMessageTriggerBean(String id, String description,   Calendar time, Long hrs, Calendar deadline, Long budget, String taskId, String submitterId, String resolutionId, String priorityId, String handlerId, String handlerUserId, String handlerGroupId, String mstatusId, Map udfMap, SessionContext secure, ArrayList<AttachmentArray> atts) throws GranException {
		return "";
	}
}
return "";
</@std.script>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<#macro tab name description>
<td width="10%" class="tab"
<#if !currentTab?exists && name==defaultFilter?default("All") && !request.value.__search?exists>id="active"<#assign currentTab=description/></#if>><a href="${contextPath}/template/${template}/task/${task.number}/filter/${name}">${name}</a></td>
</#macro>

<#macro tabsearch name search>
<td width="10%" class="tab" <#if !currentTab?exists && request.value.__search?exists && search==request.value.__search><#assign currentTab=search/>id="active"</#if>><a href="${contextPath}/template/${template}/task/${task.number}/?__search=${search}">${name}</a></td>
</#macro>

<#macro tabpanel>
<table border=0 cellpadding=0 cellspacing=1 width="100%">
<tr>
<#nested>
<td width="40%" class="tabbottom">&nbsp;</td>
</tr>
</table>
</#macro>

<#macro controlpanel>
<table border=0 cellpadding=0 cellspacing=1 width="100%">
<tr>
<td width="80%" class="controltop">&nbsp;</td>
<td width="20%" class="control">
<#nested>
</td>
</tr>
</table>
</#macro>

<HTML>
<HEAD>
<TITLE>TrackStudio: ${task.name?html}</TITLE>
<META NAME="Generator" CONTENT="EditPlus">
<META NAME="Author" CONTENT="">
<META NAME="Keywords" CONTENT="">
<META NAME="Description" CONTENT="">
    <link rel="shortcut icon" href="${contextPath}/favicon.ico" type="image/x-icon"/>
</HEAD>
<style>
div.title{
font-family: Tahoma, sans-serif;
color: #666666;
font-size: 11px;
font-weight: normal;
}
div.data{
font-family: Tahoma, sans-serif;
color: #000000;
font-size: 20px;
margin-top: 0px;
padding-left: 20px;
}
BODY {
font-family: Tahoma;
font-size: 12px;
padding: 8px 8px 8px 8px;
}
TABLE{
	    border-collapse: collapse;
	}

div.fullpath {
padding: 4px 4px 4px 4px;
vertical-align: middle;
margin-left: 20px;
}
div.fullpath A {
color: blue;
font-weight: normal;
padding-top: 4px;
padding-bottom: 4px;
padding-left: 2px;
padding-right: 2px;
text-decoration: underline;
font-size: 11px;
}
div.fullpath A:hover{
color: black;
text-decoration: underline;
}
div.fullpath A.last {
color: black;
text-decoration: none;
}

table.products {
border: #F3C76A 1px solid;
background-color: #F8F8F8;
width: 100%;
}
table.products TD {
vertical-align: middle;
border-right: #F8F8F8 3px solid;
}
table.products TD#active {
border-right: #F3C76A 3px solid;
}
table.products caption{
font-family: Tahoma, sans-serif;
font-size: 12px;
font-weight: bolder;
background-color: #F8DBA1;
text-align: left;
padding-left: 20px;
color: black;
}
table.products A {
color: #000000;
font-size: 12px;
}
table.taskList {
font-family: Tahoma, sans-serif;
    color: #000000;
    font-size: 12px;
    vertical-align: middle;
    width: 100%;
    border: #cc9f85 1px solid;
    margin-bottom: 8px;
}
table.taskList caption{
vertical-align: middle;
    font-family: Tahoma, sans-serif;
    color: #000000;
    text-align: left;
    font-weight: bolder;
    background-color: #decb9d;
    padding-left: 8px;
    padding-top: 4px;
    padding-bottom: 4px;
    border-top: #cc9f85 1px solid;
    border-left: #cc9f85 1px solid;
    border-right: #cc9f85 1px solid;
    margin-left: -1px;
    margin-right: 0;
    font-size: 12px;
    border-bottom: #cc9f85 1px solid;
    letter-spacing: 0;
}
table.taskList TH{
color: #000000;
    text-align: left;
    font-weight: bold;
    background-color: #fcc671;

    vertical-align: middle;
    font-size: 12px;
}
table.taskList TD{
background-color: #F8F8F8;
    font-size: 12px;
    text-align: left;
    font-weight: normal;
}
table.taskList TR.new TD{
background-color: #f2ddac;
font-size: 12px;
font-style: italic;
}

table.taskList A{
color: black;
font-weight: bold;
font-size: 12px;
}
table.taskList A.task{
color: black;
font-size: 12px;
font-weight: bold;
text-decoration: none;
}

table.common {
width: 100%;
}
table.common TD {
vertical-align: top;
}

span.date{
color: black;
font-weight: normal;
font-size: 12px;
font-family: Tahoma;
}
div.viewtask{
padding: 4px 4px 4px 4px;
margin-bottom: 8px;

}
div.viewtask span.number{
font-size: 14px;
font-family: Tahoma;
color: #666666;
}
div.viewtask h1{
font-size: 20px;
font-family: Tahoma;
color: black;
}
div.viewtask td{
padding: 4px 4px 4px 4px;
}
div.viewtask td.submitted{
font-size: 12px;
font-family: Tahoma;
color: #666666;
text-align: left;

}

div.viewtask span.state{
font-size: 12px;
font-family: Tahoma;
color: #666666;
text-align: right;
padding: 4px 4px 4px 4px;

}

div.viewtask span.user{
color: black;
font-weight: bold;
font-size: 12px;
}
div.viewtask div.description{
border: #C7D9E3 1px dashed;
border-top: #C7D9E3 3px solid;
padding: 4px 4px 4px 4px;
background-color: #F8F8F8;
}
div.login {
font-family: Tahoma, sans-serif;
font-size: 11px;
text-align: right;
padding: 0px 0px 0px 0px;
font-weight: normal;
}
div.login form{
vertical-align: middle;
}
img.sitelogo{

}
td.tab{
background-color: white;
border-bottom: #F3C76A 3px solid;
border-top: #F3C76A 1px dotted;
color: black;
border-left: #F3C76A 1px dotted;
border-right: #F3C76A 1px dotted;
white-space: nowrap;
padding-left: 8px;
}
td.tab#active{
border-bottom: white 1px solid;
border-top: #F3C76A 3px solid;
}
td.tab A{
text-decoration: none;
color: black;
font-size: 14px;
font-weight: bold;
}
td.tabbottom{
background-color: white;
border-bottom: #F3C76A 3px solid;
}
td.controltop{
background-color: white;
border-top: #F3C76A 3px solid;
}
td.control{
background-color: white;
border-top: white 1px solid;
border-bottom: #F3C76A 3px solid;
color: black;
border-left: #F3C76A 1px dotted;
border-right: #F3C76A 1px dotted;
white-space: nowrap;
text-align: center;
padding-bottom: 4px;
padding-top: 2px;
}

div.sitesearch{
float: right;
}
    DIV.slider {
        width: 100%;
        font-family: Tahoma, Tahoma, sans-serif;
        font-size: 11px;
        background-color: #FFFFFF;
        text-align: center;
        color: #a90a08;
	vertical-align: top;
        font-weight: bold;
	padding-top: 2px;
	padding-bottom: 2px;
    }
    DIV.slider A {
        color: #000000;
        font-weight: bold;
        font-size: 11px;
        text-decoration: none;
		vertical-align: top;
    }
DIV.slider SPAN {
        border: 1px solid #a90a08;
		padding-left: 2px;
		padding-right: 2px;
    }
    DIV.slider A:hover {
	color: #5E88BF;
    font-weight: bold;
    font-size: 11px;
    text-decoration: none;
	vertical-align: top;
    }


</style>
<BODY>
<#assign siteTitle="TrackStudio. An hierarchical issue management."/>
<div class="sitesearch">
<form method="post" action="${contextPath}/template/${template}/task/${templateTask.number}">
<input type="text" size="60" name="__search" value="${request.value.__search?default("")}"><input type="submit" value="<@std.I18n key="SEARCH"/>">
</form>
</div>
<img class="sitelogo" src="${contextPath}/style/trackstudio.gif">
<br>
<br>

<table border=0 cellpadding=12 class="common">
<COLGROUP>
<COL width="80%">
<COL width="20%">
</COLGROUP>
<tr>
<td>
<div class="viewtask">
<h1><span class="number">[#${task.number}]</span> ${task.name?html}</h1>
<#assign desc = Util.parseFields(task.description)/>
<div class="fullpath">
<@std.path from=templateTask to=task.parent ; number, name>
/&nbsp;<a href="${contextPath}/template/${template}/task/${number}">${name}</a>
</@std.path>
</div>
</div>
</td>
<td>
</td>
</tr>
<tr>
<td>
<div class="viewtask">
<table border=0 cellpadding=0 cellspacing=0 width="100%">
<tr>
<td class="submitted">
<@std.I18n key="SUBMITTER"/> : <span class="user"><#if desc.From?exists>${desc.From?html}<#else>${task.submitter.name?html}</#if></span> at <@std.time date=task.submitdate/>. <@std.I18n key="CHANGE_UPDATE_TASK_TERM"/> : <@std.time date=task.updatedate/></td>
<td  align="right"><span class="state" style="border: ${task.status.color} 2px solid">${task.status.name?html}<#if task.resolutionId?exists >/${task.resolution.name?html}</#if></span></td>
</tr>
</table>
        <#if desc.text?exists>
<div class="description">
${desc.text}
</div>
</#if>
<#if task.attachments?exists>
<#list task.attachments as attachment>
<table border="0" cellspacing=0 cellpadding=4 style="display: inline">
<tr>
<th>
<a href="${contextPath}/download/task/${task.number}/${attachment.id}/${attachment.name}"><img align="middle" border="0" src="${contextPath}/TSImageServlet?session=${task.secure.id}&attId=${attachment.id}&width=100&height=75"/></a>
</th>
</tr>
<tr>
<th>
<a href="${contextPath}/download/task/${task.number}/${attachment.id}/${attachment.name}">${attachment.name} (<#assign atSize = attachment.size/><#if atSize< 1024><1 kB<#else>${(atSize/1024)?int} kB</#if>)</a>
</th>
</tr>
</table>
</#list>                                     
    </#if>
</div>
<#assign msgs = task.messages/>
<#if msgs?exists && (msgs?size>0)>
<div class="title"><@std.I18n key="COMMENT"/></div>
<#list msgs?reverse as message>
<#if message.description?exists>
<#assign desc = Util.parseFields(message.description)/>
<div class="viewtask">
<table border=0 cellpadding=0 cellspacing=0>
<tr>
<td class="submitted"><@std.I18n key="SUBMITTER"/> : <span class="user"><#if desc.From?exists>${desc.From?html}<#else>${message.submitter.name?html}</#if></span></td><td class="submitted">[<@std.time date=message.time/>]</td>
</tr>
</table>
<div class="description">${desc.text}</div>
</div>
</#if>
</#list>
</#if>
<hr>
<form method="post" enctype="multipart/form-data">
<table class="products" style="margin-left: 12px; margin-right: 12px; margin-top: 12px; margin-bottom: 12px;">
<tr>
<th><@std.I18n key="COMMENT"/>*</th>
<td><textarea name="forms_task_comment" cols=70 rows=10></textarea></td>
</tr>
<tr>
<th><@std.I18n key="NAME"/><div class="title">(<@std.I18n key="OPTIONAL"/>)</div></th>
<td>
<@std.saveCookies field="temp_user_name">
<input type="text" name="temp_user_name" value="${request.cookies.temp_user_name?default("")}">
</@std.saveCookies>
</td>
</tr>
<tr>
<th><@std.I18n key="EMAIL"/><div class="title">(<@std.I18n key="OPTIONAL"/>)</div></th>
<td>
<@std.saveCookies field="temp_user_email">
<input type="text" name="temp_user_email" value="${request.cookies.temp_user_email?default("")}">
</@std.saveCookies>
</td>
</tr>
</table>
<div style="text-align: right">
<input type="hidden" name="method" value="task_comment">
<input type="submit" name="submit" value="<@std.I18n key="SAVE"/>">
</div>
</form>
</td>
<td>
<div class="general">
<table class="products" cellpadding=6 cellspacing=0><caption><@std.I18n key="ACTION"/></caption>
<tr>
<td><a href="${contextPath}/template/${template}/task/${task.parent.number}/filter/${defaultFilter?default("1")}"><@std.I18n key="LIST_TASK"/></a></td>
</tr>
<tr>
<td><a href="${contextPath}/template/${template}/report.ftl/task/${task.parent.number}/filter/${defaultFilter?default("1")}"><@std.I18n key="BUG_REPORT"/></a></td>
</tr>
</table>
</div>
</td>
</tr>
</table>
</BODY>
</HTML>
