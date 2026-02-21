# Как изменить значение дополнительного поля в задаче

Менять значение дополнительного поля при редактировании задачи достаточно просто: все дополнительные поля содержатся в HashMap внутри **SecuredTaskTriggerBean**, с которым оперируют триггеры для задач. Хранятся они в виде пар "Заголовок", "Значение", причем значение передается в строковом виде.

Таким образом, чтобы изменить значение поля, нужно в Task Edit/Before триггере вызвать

**public** SecuredTaskTriggerBean execute(SecuredTaskTriggerBean task) **throws** GranException {...task.setUdfValue("Заголовок поля", "Значение");...

Значение, установленное в Before-триггере запишется автоматически. В InsteadOf и After триггерах нужно записывать принудительно, через вызов task.update();

**Примечание: здесь**** *****task***** ****- это передаваемый в триггер параметр**

Можно также изменить значение дополнительного поля задачи в After-триггере, выполнив

SecuredUDFAdapterManager am = AdapterManager.getInstance().getSecuredUDFAdapterManager();am.setTaskUDFValueSimple(task.getSecure(), task.getId(), "date_udf", "11.03.11 17:55");

**Примечания:**

- здесь **task** - это передаваемый в триггер параметр
- **date_udf** - заголовок поля
- **передаваемый параметр** - это дата и время. Их формат зависит от **локали** пользователя, выполняющего скрипт. Поэтому более правильный способ передачи даты и времени:

Calendar now = Calendar.getInstance(); now.setTimeZone(TimeZone.getTimeZone(securedTaskBean.getSecure().getUser().getTimezone())); now.set(Calendar.HOUR_OF_DAY, 17); now.set(Calendar.MINUTE, 55); now.set(Calendar.SECOND, 0); now.set(Calendar.MILLISECOND, 0); now.set(Calendar.MONTH, Calendar.MARCH); now.set(Calendar.YEAR, 2011); now.set(Calendar.DAY_OF_MONTH, 11); String df = securedTaskBean.getSecure().getUser().getDateFormatter().parse(now);

В этом случае значение поля будет соответствовать 11 марта 2011 года 17:55 по часовому поясу пользователя, вызвавшего скрипт. Если нужно установить время в другом формате или в другом часовом поясе - используйте соответствующие Locale и TimeZone.

При смене значения дополнительного поля способом, указанным выше, это значение не попадет в HashMap триггера, однако после After-триггера эти данные все равно не используются.

Если же значение дополнительного поля нужно поменять не при редактировании задачи, а, например, в bulk-скрипте, нам придется сначала создать и инициализировать SecuredTaskTriggerBean.

Проще всего это сделать, если значение требуется установить в уже существующей задаче и известен ее **id** или **номер** (их значения можно получить, например, перебирая задачи из списка).

.../* Создаем SecuredTaskBean через id */SecuredTaskBean theTask = new SecuredTaskBean(id, sc); // sc - это SessionContext, его можно получить из любого secured-объекта, вызвав getSecure()/* Создаем SecuredTaskTriggerBean */SecuredTaskTriggerBean sttb = new SecuredTaskTriggerBean(theTask, new HashMap()); // HashMap - это как раз наш список дополнительных полей, можно передавать его уже инициализированным/* Устанавливаем нужное значение */sttb.setUdfValue("Заголовок поля", "Значение");sttb.update();

...
