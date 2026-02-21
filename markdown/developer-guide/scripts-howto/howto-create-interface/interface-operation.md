# Как добавить комментарий

Чтобы добавить в интерфейс возможность для пользователей оставлять комментарии к задаче, добавьте на страницу следующую форму:

```
<form method="post" enctype="multipart/form-data">
Комментарий:<br>
<textarea name="forms_task_comment" cols=70 rows=10></textarea>
Имя:<br>
<@std.saveCookies field="temp_user_name">
<input type="text" name="temp_user_name"
value="${request.cookies.temp_user_name?default("")}">
</@std.saveCookies>
Мой email:<br>
<@std.saveCookies field="temp_user_email">
<input type="text" name="temp_user_email"
value="${request.cookies.temp_user_email?default("")}">
</@std.saveCookies>
<input type="hidden" name="method" value="task_comment">
<input type="submit" name="submit">
</form>
```
