# Как проверить данные перед сохранением задачи (Before Task Edit Trigger)

Чтобы проверять данные перед сохранением существующей задачи, нужно подключить к ее процессу триггер Before Edit Task.

Он должен соответствовать интерфейсу **com.trackstudio.external.TaskTrigger** и располагаться в папке

```
./etc/plugins/scripts/before_edit_task/
```

Ниже пример триггера, который проверяет, указан ли для задачи правильный deadline. Для того, чтобы указать пользователю на ошибку и не сохранять задачу, мы выбрасываем в триггере UserException. При этом пользователь возвращается на страницу редактирования задачи. Данные остаются введенными.

**package** scripts.before_edit_task;**import** com.trackstudio.exception.GranException;**import** com.trackstudio.exception.UserException;**import** com.trackstudio.external.TaskTrigger;**import** com.trackstudio.secured.SecuredTaskTriggerBean;**import** java.util.Calendar;**public** **class** CheckDeadline **implements** TaskTrigger { **public** SecuredTaskTriggerBean execute(SecuredTaskTriggerBean securedTaskTriggerBean) **throws** GranException { Calendar deadline = securedTaskTriggerBean.getDeadline(); Calendar now = Calendar.getInstance(); **if** (deadline == **null** || deadline.before(now)) **throw** **new** UserException("Укажите срок выполнения задачи"); **return** securedTaskTriggerBean; }}

С помощью триггеров Before Edit Task обычно выполняются действия, которые необходимо совершить до сохранения задачи, так, чтобы если эти действия завершились неуспешно, пользователь вернулся на страницу ввода и мог повторить действия.
