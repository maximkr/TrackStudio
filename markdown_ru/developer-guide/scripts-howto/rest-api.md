[Домой](../../index.md) | [Наверх (Как писать скрипты и триггеры в TrackStudio)](index.md)

---

# Как использовать REST API

В настоящее время TrackStudio REST API находится в экспериментальной фазе. Мы планируем развивать этот интерфейс, поэтому структура запросов и возвращаемых данных может меняться

## Структура запросов

В общем виде запросы в TrackStudio REST API имеют вид:

```
http://host:port/TrackStudio/rest/resource-name?paramname=value
```

На данный момент времени с каждым запросом через REST API требуется передавать обязательные параметры:

login - учетная запись

password - пароль

Поддерживаются следующие ресурсы

- **Список названий фильтров, доступных в задаче {parent}**

**/task/filter/{parent}**

**Пример использования:**

```
Запрос: http://localhost:8888/TrackStudio/rest/task/filter/95?login=root&password=root
```

Ответ:

```
["Все задачи (включая вложенные)","Список требований (включая вложенные)","Список требований","Открытые задачи (включая вложенные)","Открытые задачи","Мои задачи (включая вложенные)","Мои задачи","Все задачи"]
```

- **Список подзадач задачи {parent}, отфильтрованных фильтром {filter}**

**/task/tasks/{parent}/{filter}**

**Пример использования:**

```
Запрос: http://localhost:8888/TrackStudio/rest/task/tasks/95?filter=All&login=root&password=root
```

Ответ:

```
[{"name":"Функциональные требования","id":"4028808a1951e21b0119526f85440163","number":"104","attachment":null,"description":"&nbsp;\n","abudget":0,"budget":0,"closedate":-1,"deadline":-1,"shortname":"","submitdate":1208268850000,"updatedate":1208286872000,"childrenCount":1,"messageCount":0,"hasAttachments":false,"onSight":true,"messages":null,"udfs":null,"categoryName":"Список требований","statusName":"Новый","resolutionName":null,"priorityName":"Нормальный","submitterName":"root","handlerUserName":"manager","handlerGroupName":null,"parentName":"Требования","workflowName":"Папка","abudgetView":"-","budgetView":"","closedateView":"","deadlineView":"","submitdateView":"15.04.08 10:14","updatedateView":"15.04.08 15:14","categoryLink":"http://localhost:8888/TrackStudio/icons/categories/default_folder.png","stateLink":"http://localhost:8888/TrackStudio/cssimages/startstate.png"},{"name":"Пользовательские требования","id":"4028808a1951e21b0119526f56e10162","number":"103","attachment":null,"description":"&nbsp;\n","abudget":0,"budget":0,"closedate":-1,"deadline":-1,"shortname":"","submitdate":1208268838000,"updatedate":1208286853000,"childrenCount":1,"messageCount":0,"hasAttachments":false,"onSight":true,"messages":null,"udfs":null,"categoryName":"Список требований","statusName":"Новый","resolutionName":null,"priorityName":"Нормальный","submitterName":"root","handlerUserName":"manager","handlerGroupName":null,"parentName":"Требования","workflowName":"Папка","abudgetView":"-","budgetView":"","closedateView":"","deadlineView":"","submitdateView":"15.04.08 10:13","updatedateView":"15.04.08 15:14","categoryLink":"http://localhost:8888/TrackStudio/icons/categories/default_folder.png","stateLink":"http://localhost:8888/TrackStudio/cssimages/startstate.png"},{"name":"Нефункциональные требования","id":"4028808a1951e21b0119526f2be90161","number":"102","attachment":null,"description":"&nbsp;\n","abudget":0,"budget":0,"closedate":-1,"deadline":-1,"shortname":"","submitdate":1208268827000,"updatedate":1208268827000,"childrenCount":0,"messageCount":0,"hasAttachments":false,"onSight":true,"messages":null,"udfs":null,"categoryName":"Список требований","statusName":"Новый","resolutionName":null,"priorityName":"Нормальный","submitterName":"root","handlerUserName":"manager","handlerGroupName":null,"parentName":"Требования","workflowName":"Папка","abudgetView":"-","budgetView":"","closedateView":"","deadlineView":"","submitdateView":"15.04.08 10:13","updatedateView":"15.04.08 10:13","categoryLink":"http://localhost:8888/TrackStudio/icons/categories/default_folder.png","stateLink":"http://localhost:8888/TrackStudio/cssimages/startstate.png"},{"name":"Бизнес требования","id":"4028808a1951e21b0119526efcca0160","number":"101","attachment":null,"description":"&nbsp;\n","abudget":0,"budget":0,"closedate":-1,"deadline":-1,"shortname":"","submitdate":1208268815000,"updatedate":1208268815000,"childrenCount":0,"messageCount":0,"hasAttachments":false,"onSight":true,"messages":null,"udfs":null,"categoryName":"Список требований","statusName":"Новый","resolutionName":null,"priorityName":"Нормальный","submitterName":"root","handlerUserName":"manager","handlerGroupName":null,"parentName":"Требования","workflowName":"Папка","abudgetView":"-","budgetView":"","closedateView":"","deadlineView":"","submitdateView":"15.04.08 10:13","updatedateView":"15.04.08 10:13","categoryLink":"http://localhost:8888/TrackStudio/icons/categories/default_folder.png","stateLink":"http://localhost:8888/TrackStudio/cssimages/startstate.png"}]
```

- **Информация о задаче с номером {number}**

**/task/info/{number}**

**Обязательные параметры:**

login - учетная запись

password - пароль

**Необязательные параметры:**

message - если 'true', вместе с задачей будут выведены сообщения

attach - если 'true', вместе с задачей будут выведены вложения

udf - если 'true', будут выведены значения дополнительных полей

Примеры использования:

```
Запрос: http://localhost:8888/TrackStudio/rest/task/info/120?login=root&password=root
```

Ответ:

```
{"name":"Проверка страницы логин","id":"4028808a1953022d0119531c0e5500c8","number":"120","attachment":null,"description":"<p><strong>ПРЕДВАРИТЕЛЬНЫЕ ТРЕБОВАНИЯ:</strong></p>\n<p>Все задания для дизайнеров и разработчиков по работе над страницей Логин должны быть завершены.&nbsp;</p>\n<p><strong>ПЛАН ДЕЙСТВИЙ:</strong></p>\n<p>Откройте страницу Логин.&nbsp;</p>\n<p><strong>ПЛАН ПРОВЕРКИ:</strong></p>\n<p>- Окно Логин открыто<br /> - Название окна - Логин<br /> - Логотип компании отображается в правом верхнем углу&nbsp;<br /> - На форме 2 поля - Имя и Пароль<br /> - Кнопка Логин доступна<br /> - Линк забыл пароль - доступен&nbsp;</p>\n<p>- Еще нужно добавить ссылку на страницу авторегистрации.</p>\n<p>- И ссылку для входе через аннонимного пользователя.</p>","abudget":0,"budget":0,"closedate":-1,"deadline":-1,"shortname":"","submitdate":1208280157000,"updatedate":1291304234000,"childrenCount":0,"messageCount":2,"hasAttachments":false,"onSight":true,"messages":null,"udfs":null,"categoryName":"Набор тестовых данных","statusName":"Новый","resolutionName":null,"priorityName":"Обычный","submitterName":"manager","handlerUserName":"manager","handlerGroupName":null,"parentName":"Тестовая документация","workflowName":"Набор тестовых данных","abudgetView":"-","budgetView":"","closedateView":"","deadlineView":"","submitdateView":"15.04.08 13:22","updatedateView":"02.12.10 10:37","categoryLink":"http://localhost:8888/TrackStudio/icons/categories/document.png","stateLink":"http://localhost:8888/TrackStudio/cssimages/startstate.png"}
```

- **Информация о пользователе с логином {userLogin}**

**/user/info/{userLogin}**

**Пример использования:**

```
Запрос: http://localhost:8888/TrackStudio/rest/user/info/root?login=root&password=root
```

Ответ:

```
{"name":"Администратор","id":"1","string":"Администратор [root]","password":"63a9f0ea7bb98050796b649e85481845","enabled":true,"locale":"ru","defaultProjectId":"ff8080812bd3eb78012bd3ed956a0027","timezone":"America/New_York","login":"root","childAllowed":0,"company":"","email":"","expireDate":-1,"preferences":null,"prstatusId":"5","tel":"","lastLogonDate":1416590652803,"managerId":null,"passwordChangedDate":1207767756000}
```

- **Список подчиненных пользователей для пользователя с логином {userLogin}**

**/user/subusers/{userLogin}**

**Пример использования:**

```
Запрос: http://localhost:8888/TrackStudio/rest/user/subusers/root?login=root&password=root
```

Ответ:

```
[{"name":"Сергей Менеджеров","id":"4028808a192e43e801192e48f4fd0002","string":"Сергей Менеджеров [manager]","password":"1d0258c2440a8d19e716292b231e3190","enabled":true,"locale":"ru","defaultProjectId":"1","timezone":null,"login":"manager","childAllowed":0,"company":"","email":"","expireDate":-1,"preferences":null,"prstatusId":"4028808a193230e3011932be7da8010a","tel":"","lastLogonDate":1291129607000,"managerId":"1","passwordChangedDate":1211189686000},{"name":"Иван Аналитиков","id":"4028808a1934933b011934c2e27703d4","string":"Иван Аналитиков [analyst]","password":"05d5c5dfb743a5bd8fd7494fdc9bdb00","enabled":true,"locale":null,"defaultProjectId":null,"timezone":null,"login":"analyst","childAllowed":0,"company":"","email":"","expireDate":-1,"preferences":null,"prstatusId":"4028808a1934933b011934c1e26c020c","tel":"","lastLogonDate":1208286392000,"managerId":"1","passwordChangedDate":1211189733000},{"name":"Степан Разработчиков","id":"4028808a1934933b011934c336e003d5","string":"Степан Разработчиков [developer]","password":"5e8edd851d2fdfbd7415232c67367cc3","enabled":true,"locale":null,"defaultProjectId":null,"timezone":null,"login":"developer","childAllowed":0,"company":"","email":"","expireDate":-1,"preferences":null,"prstatusId":"4028808a1934933b011934c2214a02a4","tel":"","lastLogonDate":1208290114000,"managerId":"1","passwordChangedDate":1211189861000},{"name":"Дмитрий Писателев","id":"4028808a1934933b011934c65e400486","string":"Дмитрий Писателев [writer]","password":"a82feee3cc1af8bcabda979e8775ef0f","enabled":true,"locale":null,"defaultProjectId":null,"timezone":null,"login":"writer","childAllowed":0,"company":"","email":"","expireDate":-1,"preferences":null,"prstatusId":"4028808a1934933b011934c5ea5803ee","tel":"","lastLogonDate":1208289935000,"managerId":"1","passwordChangedDate":1211189881000},{"name":"Максим Тестеров","id":"4028808a1934933b011934ca3b3404af","string":"Максим Тестеров [tester]","password":"bf3453193f289cd54c84cb6f5d3728c0","enabled":true,"locale":null,"defaultProjectId":null,"timezone":null,"login":"tester","childAllowed":0,"company":"","email":"","expireDate":-1,"preferences":null,"prstatusId":"4028808a1934933b011934c25dc6033c","tel":"","lastLogonDate":1208289964000,"managerId":"1","passwordChangedDate":1211189985000},{"name":"Анонимный пользователь","id":"ff8080812c350210012c3507475700de","string":"Анонимный пользователь [anonymous]","password":"63a9f0ea7bb98050796b649e85481845","enabled":true,"locale":"ru","defaultProjectId":"1","timezone":"America/New_York","login":"anonymous","childAllowed":0,"company":"","email":"","expireDate":-1,"preferences":null,"prstatusId":"ff8080812c350210012c350506ee0002","tel":"","lastLogonDate":1289379893000,"managerId":"1","passwordChangedDate":1289379859000}]
```

- **Список названий фильтров, доступных пользователю с логином {userLogin}**

**/user/filters/{userLogin}**

**Пример использования:**

```
Запрос: http://localhost:8888/TrackStudio/rest/user/filters/root?login=root&password=root
```

Ответ:

```
[{"name":"Все пользователи","id":"0","private":false,"taskId":null,"description":"Список всех пользователей","ownerId":"1","preferences":"T","priv":false}]
```

- **Cписок подчиненных пользователей для пользователя с логином {userLogin} по фильтру {filter}**

**/user/subusers/{userLogin}/{filter}**

**Пример использования:**

```
Запрос: http://localhost:8888/TrackStudio/rest/user/subusers/root/1?login=root&password=root
```

Ответ:

```
[{"name":"Иван Аналитиков","id":"4028808a1934933b011934c2e27703d4","string":"Иван Аналитиков [analyst]","password":"05d5c5dfb743a5bd8fd7494fdc9bdb00","enabled":true,"locale":null,"defaultProjectId":null,"timezone":null,"login":"analyst","childAllowed":0,"company":"","email":"","expireDate":-1,"preferences":null,"prstatusId":"4028808a1934933b011934c1e26c020c","tel":"","lastLogonDate":1208286392000,"managerId":"1","passwordChangedDate":1211189733000},{"name":"Анонимный пользователь","id":"ff8080812c350210012c3507475700de","string":"Анонимный пользователь [anonymous]","password":"63a9f0ea7bb98050796b649e85481845","enabled":true,"locale":"ru","defaultProjectId":"1","timezone":"America/New_York","login":"anonymous","childAllowed":0,"company":"","email":"","expireDate":-1,"preferences":null,"prstatusId":"ff8080812c350210012c350506ee0002","tel":"","lastLogonDate":1289379893000,"managerId":"1","passwordChangedDate":1289379859000},{"name":"Степан Разработчиков","id":"4028808a1934933b011934c336e003d5","string":"Степан Разработчиков [developer]","password":"5e8edd851d2fdfbd7415232c67367cc3","enabled":true,"locale":null,"defaultProjectId":null,"timezone":null,"login":"developer","childAllowed":0,"company":"","email":"","expireDate":-1,"preferences":null,"prstatusId":"4028808a1934933b011934c2214a02a4","tel":"","lastLogonDate":1208290114000,"managerId":"1","passwordChangedDate":1211189861000},{"name":"Сергей Менеджеров","id":"4028808a192e43e801192e48f4fd0002","string":"Сергей Менеджеров [manager]","password":"1d0258c2440a8d19e716292b231e3190","enabled":true,"locale":"ru","defaultProjectId":"1","timezone":null,"login":"manager","childAllowed":0,"company":"","email":"","expireDate":-1,"preferences":null,"prstatusId":"4028808a193230e3011932be7da8010a","tel":"","lastLogonDate":1291129607000,"managerId":"1","passwordChangedDate":1211189686000},{"name":"Максим Тестеров","id":"4028808a1934933b011934ca3b3404af","string":"Максим Тестеров [tester]","password":"bf3453193f289cd54c84cb6f5d3728c0","enabled":true,"locale":null,"defaultProjectId":null,"timezone":null,"login":"tester","childAllowed":0,"company":"","email":"","expireDate":-1,"preferences":null,"prstatusId":"4028808a1934933b011934c25dc6033c","tel":"","lastLogonDate":1208289964000,"managerId":"1","passwordChangedDate":1211189985000},{"name":"Дмитрий Писателев","id":"4028808a1934933b011934c65e400486","string":"Дмитрий Писателев [writer]","password":"a82feee3cc1af8bcabda979e8775ef0f","enabled":true,"locale":null,"defaultProjectId":null,"timezone":null,"login":"writer","childAllowed":0,"company":"","email":"","expireDate":-1,"preferences":null,"prstatusId":"4028808a1934933b011934c5ea5803ee","tel":"","lastLogonDate":1208289935000,"managerId":"1","passwordChangedDate":1211189881000}]
```

---

[Домой](../../index.md) | [Наверх (Как писать скрипты и триггеры в TrackStudio)](index.md)
