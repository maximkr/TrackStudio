[Домой](../../index.md) | [Наверх (Как писать скрипты и триггеры в TrackStudio)](index.md)

---

# Как использовать интерфейс SOAP для TrackStudio

Для работы с SOAP в папке **etc/deveopment/soap** лежит проект.

Нужные ресурсы генерируются при помощи команды

```
mvn generate-resources
```

Открыв проект в среде разработки, папку **target/generated-sources/cxf** отметить как sources.

![](../../images/soapidea.PNG)

После этого можно приступить к написанию своего SOAP-клиента либо воспользоваться уже готовым примером из проекта.

В SOAP вы можете оперировать сервисами или контейнерами. Сервисы нужны для выполнения различных функций, в контейнерах, соответственно, переносятся данные.

Сервис для манипулирования задачами можно получить, например, так:

**public** **static** **final** String url = "http://localhost:8888/TrackStudio/services/";**public** Task getTaskService(url) **throws** MalformedURLException { TaskService service = **new** TaskService(**new** URL(url + Task.**class**.getSimpleName() + "?wsdl"), **new** QName("http://task.service.soap.trackstudio.com/", "TaskService")); **return** service.getTaskPort(); }

url, разумеется, нужно подставить свой.

Теперь чтобы, например, создать задачу, нужно выполнить метод createTask сервиса TaskService:

**public** String createTask(@WebParam(name = "sessionId") String sessionId, @WebParam(name = "categoryId") String categoryId, @WebParam(name = "shortname") String shortname, @WebParam(name = "name") String name, @WebParam(name = "description") String description, @WebParam(name = "budget") **long** budget, @WebParam(name = "deadline") **long** deadline, @WebParam(name = "priorityId") String priorityId, @WebParam(name = "parentId") String parentId, @WebParam(name = "handlerUserId") String handlerUserId, @WebParam(name = "handlerGroupId") String handlerGroupId, @WebParam(name = "udfNames") String[] udfNames, @WebParam(name = "udfValues") String[] udfValues) **throws** Exception;

Для получения списка задач у которых определенное поле (UDF) равно заданному значению нужно использовать следующий код

TaskFvalueBean taskFvalueBean = **new** TaskFvalueBean(); taskFvalueBean.setSubtask("1"); taskFvalueBean.getUdfs().add("UDF8a80828f4a0619c0014a062a5cc00004->_eq_test"); TaskSliderBean sliderBean = task.getTaskList(sessionId, taskId, taskFvalueBean, **true**, 20, **new** ArrayList<String>());

Если необходимо найти задачи закрытые в определенный период используется код

GregorianCalendar date = **new** GregorianCalendar(2014, 12, 1); TaskFvalueBean taskFvalueBean = **new** TaskFvalueBean(); taskFvalueBean.setSubtask("1"); taskFvalueBean.setCloseDate("_"+String.valueOf(date.getTimeInMillis())); */*c указанной даты */* taskFvalueBean.setCloseDate(String.valueOf(date.getTimeInMillis())); */*по указанную дату */* TaskSliderBean sliderBean = task.getTaskList(sessionId, taskId, taskFvalueBean, **true**, 20, **new** ArrayList<String>());

---

[Домой](../../index.md) | [Наверх (Как писать скрипты и триггеры в TrackStudio)](index.md)
