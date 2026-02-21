# Как создать новую задачу

Для того, чтобы дать пользователям возможность создавать задачи из вашего интерфейса, вставьте на странице форму:

```
<form method="post" enctype="multipart/form-data"
action="${contextPath}/template/${template}/action.ftl/task/${task.number}">
<input type="hidden" name="forms_task_edit_id" value="${task.id}">
Заголовок задачи: <input type="text" name="forms_task_edit_name" value="" size="80" maxlength="200">
Описание: <textarea name="forms_task_edit_description" cols=70 rows=10></textarea>
Вложенные файлы<br>:
<input type="file" name="forms_task_edit_attachment" value="" size="70" maxlength="200"><br>
<input type="file" name="forms_task_edit_attachment" value="" size="70" maxlength="200"><br>
<input type="file" name="forms_task_edit_attachment" value="" size="70" maxlength="200"><br>
Мое имя:<br>
<@std.saveCookies field="temp_user_name">
<input type="text" name="temp_user_name" value="${request.cookies.temp_user_name?default("")}">
</@std.saveCookies>
Мой email:<br>
<@std.saveCookies field="temp_user_email">
<input type="text" name="temp_user_email" value="${request.cookies.temp_user_email?default("")}">
</@std.saveCookies>
<input type="hidden" name="method" value="task_edit">
<input type="submit">
</form>
```

На странице action.ftl (или на другой, на которую будет отправляться форма) должен быть такой код:

```
<@std.script>
if (request.get("value").get("method")!=null && request.get("value").get("method").equals("task_edit")) {
if (!Util.validateCreateCategory(task, "bug")) {
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
String taskCategory_1 = CSVImport.findCategoryIdByName("bug");
SecuredTaskTriggerBean newTask = new SecuredTaskTriggerBean(taskId, prefix+taskDescription_1,taskName_1,
null, null, null, null, null, null,
null, null, sc.getUserId(), task.getHandlerId(),
task.getHandlerUserId(), task.getHandlerGroupId(), taskId, taskCategory_1,
null, null, null,  null,  null, sc).create();
Uploader.upload(newTask);
return newTask.getNumber();
}
return "";
</@std.script>
```
