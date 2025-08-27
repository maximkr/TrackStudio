<#assign number>
<@std.script>
if (request.get("value").get("method")!=null && request.get("value").get("method").equals("task_edit")) {
if (!(Util.validateCreateCategory(task, "Bug") || Util.validateCreateCategory(task, "Ошибка"))) {
return "";
}
String prefix="";
if (request.get("value").get("temp_user_name")!=null) {
prefix+="From: "+request.get("value").get("temp_user_name")+"\n";
}
if (request.get("value").get("temp_user_email")!=null){
prefix+="Email: "+request.get("value").get("temp_user_email")+"\n";
}
String taskName_1 = request.get("value").get("forms_task_edit_name");
String taskDescription_1 = request.get("value").get("forms_task_edit_description");
String taskId = request.get("value").get("forms_task_edit_id");
String taskCategory_1 = CSVImport.findCategoryIdByName("Bug");
if (taskCategory_1 == null) {
    taskCategory_1 = CSVImport.findCategoryIdByName("Ошибка");
}
SecuredTaskTriggerBean newTask = new SecuredTaskTriggerBean(taskId, prefix+taskDescription_1,taskName_1, null,        null,                null,                null,               null,                  null,        null,          null,            sc.getUserId(),   task.getHandlerId(), task.getHandlerUserId(), task.getHandlerGroupId(), taskId,            taskCategory_1,  null,                null,              null,                  null,            new HashMap(),           sc).create();
//                             new SecuredTaskTriggerBean(tci.getId(), getDescription(), getName(), getShortname(), tci.getSubmitdate(), tci.getUpdatedate(), tci.getClosedate(), tci.getActualBudget(), getBudget(), getDeadline(), tci.getNumber(), getSubmitterId(), getHandlerId(),      getHandlerUserId(),      getHandlerGroupId(),      tci.getParentId(), getCategoryId(), tci.getWorkflowId(), tci.getStatusId(), tci.getResolutionId(), getPriorityId(), getUdfValues(), getSecure());

Uploader.upload(newTask);
return newTask.getNumber();
}
return "";
</@std.script>
</#assign>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<HTML>
<HEAD>
    <TITLE>Thank you</TITLE>
    <META NAME="Generator" CONTENT="EditPlus">
    <META NAME="Author" CONTENT="">
    <META NAME="Keywords" CONTENT="">
    <META NAME="Description" CONTENT="">
    <link rel="shortcut icon" href="${contextPath}/favicon.ico" type="image/x-icon"/>
    <meta http-equiv="refresh" content="3;url=${contextPath}/template/${template}/task/${task.getNumber()}">
    <style>
        body{
        }

        div {
            border: #F3C76A 1px solid;
            background-color: #F8F8F8;
            position: absolute;
            margin-top: 10%;
            width: 60%;
            margin-left: 20%;
            margin-right: 20%;
            padding: 16px 16px 16px 16px;
        }
        h1{
            font-family: Tahoma;
            font-size: 14px;

        }
        a{
        }
    </style>
</HEAD>

<BODY>

<div>
<#if (Util.validateCreateCategory(task, "Bug") || Util.validateCreateCategory(task, "������"))>
    <h1><@std.I18n key="LABEL_CREATE_BUG"/></h1>
</#if>
<#if !(Util.validateCreateCategory(task, "Bug") && Util.validateCreateCategory(task, "������"))>
    <h1><@std.I18n key="LABEL_CANNOT_CREATE_BUG"/></h1>
</#if>
<#if request.cookies.temp_user_name?exists><@std.I18n key="LABEL_CANNOT_CREATE_BUG"  value=[request.cookies.temp_user_name]/></#if><br>
    <@std.I18n key="NOW_YOU_CAN"/>
    <ul>
        <li><a href="${contextPath}/template/${template}/task/${task.number}"><@std.I18n key="LIST_TASK"/></a></li>
    <#if (Util.validateCreateCategory(task, "Bug") || Util.validateCreateCategory(task, "������"))>
        <li><a href="${contextPath}/template/${template}/view.ftl/task/${number}"><@std.I18n key="VIEW_BUG"/></a></li>
    </#if>
        <li><a href="${contextPath}/template/${template}/report.ftl/task/${task.number}"><@std.I18n key="ANOTHER_BUG"/></a></li>
    </ul>
</div>

</BODY>
</HTML>
