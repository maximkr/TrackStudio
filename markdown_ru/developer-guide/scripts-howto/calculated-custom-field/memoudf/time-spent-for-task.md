[Домой](../../../../index.md) | [Наверх (Текст (Text))](index.md)

---

# Время, затраченное пользователями на задачу.

Скрипт используется в поле типа текст с включенной опцией HTML-вид. Скрипт возвращает таблицу с временем, которое потратил на задачу каждый пользователь.

**package** scripts.task_custom_field_value;**import** com.trackstudio.exception.GranException;**import** com.trackstudio.external.TaskUDFValueScript;**import** com.trackstudio.secured.SecuredMessageBean;**import** com.trackstudio.secured.SecuredTaskBean;**import** java.util.HashMap;**import** java.util.Map;**public** **class** TextUdf **implements** TaskUDFValueScript{ */* Метод, возвращающий таблицу на основе HashMap с данными о затраченном времени*/* **public** String createtable(HashMap<String ,Long> map) { String table = "<table>"; **for**(Map.Entry entry:map.entrySet()) { table = table + "<tr><td>" + entry.getKey().toString() + "</td><td>" + entry.getValue().toString() + "</td></tr>"; } table = table + "</table>"; **return** table; } **public** Object calculate(SecuredTaskBean task) **throws** GranException{ HashMap<String, Long> hrs = **new** HashMap<String, Long>(); **if**(task.getMessageCount()!=0) { **for**(SecuredMessageBean message:task.getMessages()) { String username = message.getSubmitter().getName(); **if**(!hrs.containsKey(username)) { **if**(message.getHrs()==**null**) hrs.put(username, 0l); **else** hrs.put(username, message.getHrs()/3600); } **else** { **if**(message.getHrs()!=**null**) { Long value = hrs.get(username)/3600 + message.getHrs()/3600; hrs.put(username, value); } } } **return** createtable(hrs); } **else** **return** **null**; }}

---

[Домой](../../../../index.md) | [Наверх (Текст (Text))](index.md)
