# Как сделать автоматическую эскалацию задачи

В общем смысле эскалация задачи - это перевод ее в другое состояние, другому исполнителю или повышение ее приоритета в зависимости от условий. Например, по прошествии дедлайна, или спустя какое-то время с момента последнего обновления. Чаще всего эскалация делается автоматически.

Для примера мы будем [автоматически переводить задачи](scheduler/index.md), находящиеся в состоянии "Новая" или "В процессе" или "Приостановлена" в состояние "Просрочена". Идея метода состоит в том, что мы будем выполнять операцию, переводящую задачу в нужное состояние при помощи schedule скрипта.

Зайдем как root в базу данных по-умолчанию (["Управление требованиями"](../../user-guide/requirements-management-overview/requirements-managment-config-overview.md)). Перейдем к "Управлению пользователями" и создадим роль "Бот". Права на нее настраивать пока не будем.

![](../../images/clip0044.png)

Перейдем на "Управление пользователями" и создадим пользователя. Можно дать ему роль "Бот", но не обязательно, мы можем назначить ее через доступ к задачам позднее.

![](../../images/clip0045.png)

Вернемся к "Управлению задачами". Мы будем делать скрипт для эскалации Ошибок, так что перейдем к этому процессу.

Создадим новое состояние "Просрочена". Затем создадим тип операции "escalate" с переходами:

![](../../images/clip0046.png)

Настроим разрешения так, чтобы выполнять эту операцию могли только Боты.

![](../../images/clip0047.png)

Также нам понадобится операция, которая переводит задачу из состояния "Просрочено" в другое. Выполнять ее смогут, например, только менеджеры.

![](../../images/clip0048.png)

Настала очередь скрипта:

**package** scripts.scheduler.example;**import** com.trackstudio.app.csv.CSVImport;**import** com.trackstudio.app.session.SessionContext;**import** com.trackstudio.app.session.SessionManager;**import** com.trackstudio.external.ICategoryScheduler;**import** com.trackstudio.kernel.cache.TaskRelatedInfo;**import** com.trackstudio.kernel.cache.UserRelatedInfo;**import** com.trackstudio.kernel.cache.UserRelatedManager;**import** com.trackstudio.kernel.lock.LockManager;**import** com.trackstudio.secured.SecuredMessageTriggerBean;**import** com.trackstudio.secured.SecuredTaskBean;**import** java.util.ArrayList;**import** java.util.Calendar;**public** **class** Escalate **implements** ICategoryScheduler{ @Override **public** String getCategoryId() { **return** "4028808a1951e21b01195245ff4200c1"; } @Override **public** String getCronTime() { **return** "0 0 22 * * ?"; } @Override **public** String getName() { **return** "escalate"; } **public** **void** execute(TaskRelatedInfo task) { **final** LockManager lockManager = LockManager.getInstance(); **boolean** r = lockManager.acquireConnection(); **try** { String botId = "8a80828f4c08f430014c0929609f006a"; */* id бота*/* UserRelatedInfo bot = UserRelatedManager.getInstance().find(botId); Calendar deadline = task.getDeadline(); Calendar now = Calendar.getInstance(); ArrayList<String> statuslist = **new** ArrayList<String>(); */*список состояний задачи*/* String sessionId = SessionManager.getInstance().create(bot); SessionContext sc = SessionManager.getInstance().getSessionContext(sessionId); SecuredTaskBean tsk = **new** SecuredTaskBean(task, sc); String status = tsk.getStatus().getName(); String mstatusId = CSVImport.findMessageTypeIdByName("escalate", tsk.getCategory().getName()); statuslist.add("В процессе"); statuslist.add("Новая"); statuslist.add("Приостановлена"); **if** (deadline!=**null** && statuslist.contains(status) && deadline.getTimeInMillis() < now.getTimeInMillis()) { SecuredMessageTriggerBean message = **new** SecuredMessageTriggerBean( **null** */* идентификатор */*, "задача была просрочена" */* текст комментария */*, Calendar.getInstance() */* время выполнения операции */*, **null** */* потраченное время */*, tsk.getDeadline() */* Сроки выполнения задачи (deadline) */*, tsk.getBudget() */* бюджет */*, tsk.getId() */* задача */*, sc.getUserId() */* автор операции */*, **null** */* резолюция */*, tsk.getPriorityId() */* приоритет */*, tsk.getHandlerId() */* ответственные */*, tsk.getHandlerUserId() */* ответственный */*, tsk.getHandlerGroupId() */*группа в качестве ответственного */*, mstatusId */* тип операции */*, **null** */* Map с дополнительными полями */*, sc */* SessionContext */*, **null** */* вложения */*); message.create(**true**); } } **catch** (Exception e) { e.printStackTrace(); } **finally** { **if**(r) lockManager.releaseConnection(); } }}

Скомпилированный класс [Escalate.class](http://www.trackstudio.ru/sites/default/files/ru/187/Escalate.class) нужно поместить в **\etc\plugins\scripts\scheduler\example**** **и в веб-интерфейсе подключить к операции "Начать работу"

![](../../images/scheduleon.PNG)

Теперь все задачи, над которыми была проведена операция "Начать работу", будут отслеживаться schedule скриптом и, в случае превышения дедлайна, переводиться в состояние "Просрочена".
