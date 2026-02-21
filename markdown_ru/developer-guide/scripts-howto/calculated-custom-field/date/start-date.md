[Домой](../../../../index.md) | [Наверх (Дата (Calendar))](index.md)

---

# Вычисляем дату и время начала работ по задаче

**package** scripts.task_custom_field_value;**import** com.trackstudio.exception.GranException;**import** com.trackstudio.external.TaskUDFValueScript;**import** com.trackstudio.secured.SecuredMessageBean;**import** com.trackstudio.secured.SecuredTaskBean;**import** java.util.ArrayList;*/**** * Возвращает дату и время начала работ по задаче - время отправки первого сообщения типа "start"** */***public** **class** ActualStartDate **implements** TaskUDFValueScript { **public** Object calculate(SecuredTaskBean task) **throws** GranException { ArrayList<SecuredMessageBean> list = task.getMessages(); **if** (list != **null** && !list.isEmpty()) { **for** (SecuredMessageBean message : list) { **if** (message.getMstatus().getName().equals("start")) { **return** message.getTime(); } } } **return** **null**; }}

```

```

---

[Домой](../../../../index.md) | [Наверх (Дата (Calendar))](index.md)
