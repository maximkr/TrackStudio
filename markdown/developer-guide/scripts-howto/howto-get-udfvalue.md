# Как получить значения дополнительных полей

Получить значение дополнительного поля, **если известен id поля**, можно через HashMap

Object result = task.getUDFValues().get("udfid").getValue(); Object result = message.getUDFValues().get("udfid").getValue();

Если **id поля нет**, можно получить значение через список

Object result; **for**(SecuredUDFValueBean valueBean:task.getUdfValuesList()) { **if**(valueBean.getCaption().equals("udf name")) result = valueBean.getValue(); } Object result; **for**(SecuredUDFValueBean valueBean:message.getUdfValuesList()) { **if**(valueBean.getCaption().equals("udf name")) result = valueBean.getValue(); }

Чтобы использовать такой результат в скрипте, его необходимо привести к нужному типу данных.

| **Тип поля** | **Тип результата** | **Примечание** |
| --- | --- | --- |
| Строка | String |  |
| Целое | Integer |  |
| Дата | Calendar |  |
| Список | com.trackstudio.tools.Pair | Содержится в trackstudio.jar. Конструктор Pair(String key, String value) |
| Дробное | Double |  |
| Текст | String |  |
| Множественный список | List<Pair> |  |
| Задача | ArrayList<SecuredTaskBean> |  |
| Пользователь | ArrayList<SecuredUserBean> |  |
| URL | com.trackstudio.containers.Link | Содержится в trackstudio.jar. Конструктор Link(String link, String description) |

Ниже приведен пример скрипта, который возвращает список имен задач, которые являются значениями поля типа задача.

**package** scripts.task_custom_field_value; **import** com.trackstudio.exception.GranException; **import** com.trackstudio.external.TaskUDFValueScript; **import** com.trackstudio.secured.SecuredTaskBean; **import** com.trackstudio.secured.SecuredUDFValueBean; **import** java.util.ArrayList; **class** Example **implements** TaskUDFValueScript { **public** Object calculate(SecuredTaskBean task) **throws** GranException { String udfid = "4028808a1947f52201194818b51900ad"; */*ID поля, из которого нужно получить значение*/* ArrayList<String> names = **new** ArrayList<String>(); SecuredUDFValueBean result = task.getUDFValues().get(udfid); **if**(result!= **null**) { **for**(SecuredTaskBean securedTaskBean:(ArrayList<SecuredTaskBean>)result.getValue()) { names.add(securedTaskBean.getName()); } } **return** names; } }

```

```
