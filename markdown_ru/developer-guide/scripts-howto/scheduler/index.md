[Домой](../../../index.md) | [Наверх (Как писать скрипты и триггеры в TrackStudio)](../index.md)

---

# Как использовать настройки операций по расписанию

В системе введен новый тип скриптов. Они используются для работы с автоматическими задачами : рассылка, импорт, синхронизация с репозиториями (стандартные автоматические работы). Пользователи могут написать свои скрипты.

Скрипты разделяются на два типа.

**- Базовый -**

*/***** This interface should be implement general scheduler job***/***public** **interface** IGeneralScheduler {*/***** This method returns a mask for cron*** @return mask cron time. For example 0 1 * * ****/***public** String getCronTime();*/***** This method returns a name of job*** @return names job***/***public** String getName();*/***** This method implements jobs actions***/***public** String execute();*/***** Switcher*** @return true if scheduler should be used***/***public** **boolean** isUse();}

Скрипт должен расширять IGeneralScheduler интерфейс. Частота вызова скрипты прописывается [в синтаксисе quartz](http://quartz-scheduler.org/documentation/quartz-2.2.x/tutorials/tutorial-lesson-06)

Пример.

**package** scripts.scheduler.example;**import** com.trackstudio.external.IGeneralScheduler;**public** **class** CommonJob **implements** IGeneralScheduler { @Override **public** String getCronTime() { **return** "0 0/1 * * * ?"; *//every minute* } @Override **public** String getName() { **return** "scheduler example" } @Override **public** **void** execute() { System.out.println(" execute "); } @Override **public** **boolean** isUse() { **return** **true**; }}

далее нужно прописать данный скрипт в настройках trackstudio.adapter.properties

```
handler.service example.CommonJob.class;
```

**- Конкретизированный для категории -**

Второй тип скриптов конкретизирован для категорий. Интерфейс ICategoryScheduler

**package** com.trackstudio.external;**import** com.trackstudio.kernel.cache.TaskRelatedInfo;*/***** This interface should be implement for observed tasks concrete category***/***public** **interface** ICategoryScheduler {*/***** This method returns a category ID*** @return category ID***/***public** String getCategoryId();*/***** This method returns a mask for cron*** @return mask cron time. For example 0 1 * * ****/***public** String getCronTime();*/***** This method returns a name of job*** @return names job***/***public** String getName();*/***** This method defines actions for task.*** @param task TaskRelatedInfo***/***public** **void** execute(TaskRelatedInfo task);}

Пример перевода задачи из закрытого состояния в папку архив.

**package** scripts.scheduler.example;**import** com.trackstudio.app.adapter.AdapterManager;**import** com.trackstudio.app.filter.TaskFValue;**import** com.trackstudio.app.session.SessionContext;**import** com.trackstudio.app.session.SessionManager;**import** com.trackstudio.external.ICategoryScheduler;**import** com.trackstudio.kernel.cache.TaskRelatedInfo;**import** com.trackstudio.kernel.cache.UserRelatedManager;**import** com.trackstudio.kernel.manager.KernelManager;**import** com.trackstudio.secured.SecuredTaskBean;**import** java.util.List;**import** **static** com.trackstudio.tools.textfilter.MacrosUtil.getListTask;**public** **class** ArchiveBugsJob **implements** ICategoryScheduler { @Override **public** String getCategoryId() { **return** "4028808a1951e21b01195245ff4200c1"; } @Override **public** String getCronTime() { **return** "0/30 * * * * ?"; } @Override **public** String getName() { **return** "example category scheduler"; } @Override **public** **void** execute(TaskRelatedInfo task) { **try** { System.out.println("Scheduler actions : " + task.getId()); String sessionId = SessionManager.getInstance().create(UserRelatedManager.getInstance().find("1")); *// root* SessionContext sc = SessionManager.getInstance().getSessionContext(sessionId); String filterId = "1"; TaskFValue taskFValue = KernelManager.getFilter().getTaskFValue(filterId); TaskRelatedInfo parent = **new** TaskRelatedInfo(task.getParentId()); List<SecuredTaskBean> list = getListTask(**new** SecuredTaskBean(parent, sc), taskFValue, filterId); **for** (SecuredTaskBean stb : list) { **if** (stb.getStatus().isFinish()) { AdapterManager.getInstance().getSecuredTaskAdapterManager().pasteTasks(sc, "4028808a1951e21b011952d1e6ef03a0", stb.getId(), "CUT"); } } } **catch** (Exception e) { e.printStackTrace(); } }}

Далее для этого типа скриптов нет необходимости прописывать их в properties. Активация привязана в состоянию задачи данной категории. В настройках процесса данной категории в разделе операции, есть закладка - "Настройка операции по наблюдению".

Для активации нужно отметить операцию - Наблюдать по расписанию.

![](../../../images/scheduleopr.PNG)

После выполнения этой операции задача попадает в список для обработки скриптом task - public void execute(TaskRelatedInfo task).

Для деактивации нужно создавать новую операцию -Завершить наблюдение по расписанию. После выполнения этой операции задача удаляется из списка для обработки скриптом.

Все скрипты нужно собирать в jar архив и размешать в etc/plugins/scripts/

---

[Домой](../../../index.md) | [Наверх (Как писать скрипты и триггеры в TrackStudio)](../index.md)
