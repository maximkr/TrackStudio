# Как писать скрипты и триггеры в TrackStudio

[Как собрать проект из maven шаблона.](howto-build-maven-project.md)

[Как писать и отлаживать скрипты и триггеры под Eclipse](scripts-debug-eclipse.md)

[Как писать и отлаживать скрипты и триггеры под IntelliJ IDEA](scripts-debug-idea.md)

[Как получить значения дополнительных полей](howto-get-udfvalue.md)

[Как сделать значение дополнительного поля вычисляемым](calculated-custom-field/index.md)

[Дата (Calendar)](calculated-custom-field/date/index.md)

[Вычисляем дату и время начала работ по задаче](calculated-custom-field/date/start-date.md)

[Множественный список (Multilist)](calculated-custom-field/multilist/index.md)

[E-mail адреса пользователей для оповещений через CC](calculated-custom-field/multilist/emails-for-cc-udf.md)

[Список Пользователей](calculated-custom-field/userudf/index.md)

[Список участников для задачи](calculated-custom-field/userudf/participants-list.md)

[Список задач (Task)](calculated-custom-field/taskudf/index.md)

[Список новых подзадач](calculated-custom-field/taskudf/children-list.md)

[Список похожих задач](calculated-custom-field/taskudf/similar-tasks.md)

[Ссылка (URL)](calculated-custom-field/linkudf/index.md)

[Список задач пользователя в проекте.](calculated-custom-field/linkudf/tasks-list-for-user.md)

[Текст (Text)](calculated-custom-field/memoudf/index.md)

[Время, затраченное пользователями на задачу.](calculated-custom-field/memoudf/time-spent-for-task.md)

[Целое (Integer)](calculated-custom-field/integerudf/index.md)

[Число дней, прошедших с обновления задачи](calculated-custom-field/integerudf/days-since-update.md)

[Как использовать подстановочные (Lookup) скрипты](howto-use-lookup/index.md)

[Как сделать динамический список возможных значений дополнительного поля](howto-use-lookup/script-sprinlist.md)

[Альтернативная реализация механизма рассылки](subscriptions-schedule.md)

[Как сделать автоматическую эскалацию задачи](task-escalation.md)

[Как выполнить действия над несколькими задачами](process-note-operations.md)

[Как проверить данные перед сохранением задачи (Before Task Edit Trigger)](before-trigger-check.md)

[Как создать свой отчет в TrackStudio.](howto-create-report.md)

[Копируем права доступа в другой проект одной кнопкой](script-coping-access-rules.md)

[Как быстро и просто сделать свой интерфейс для TrackStudio](howto-create-interface/index.md)

[Как организовать переход по ссылкам внутри шаблона](howto-create-interface/interface-links.md)

[Как вывести на страницу текущую задачу](howto-create-interface/interface-task.md)

[Как вывести историю операций с задачей](howto-create-interface/interface-audit.md)

[Как создать новую задачу](howto-create-interface/interface-create-task.md)

[Как добавить комментарий](howto-create-interface/interface-operation.md)

[Как изменить значение дополнительного поля в задаче](howto-change-udf.md)

[Как использовать REST API](rest-api.md)

[Как использовать интерфейс SOAP для TrackStudio](soap.md)

[Как использовать настройки операций по расписанию](scheduler/index.md)

[Как построить графики в TrackStudio по вашим данным](charts.md)

[Как изменить проект по умолчанию для нескольких пользователей](bulk-user-change.md)

[Как сделать свои шаблоны для почтовых уведомлений](howto-change-email-templates.md)

[Как уведомить нескольких пользователей о задаче](notification-trigger.md)

[Как сделать свой Dashboard](howto-make-own-dashboard/index.md)

[Как построить графики на Dashboard](howto-make-own-dashboard/dashboard-charts.md)

[Как собрать TrackStudio из исходного кода](compile.md)

[Как установить Deadline создаваемой задачи в зависимости от приоритета](scripts-deadline-by-priority.md)

Принципы, на которых основана TrackStudio: универсальность и масштабируемость. Каким бы сложным и хитрым не был Ваш бизнес-процесс, можно найти способ, как реализовать его в TrackStudio. Возможно, полученный результат будет не столь изящным, как в специализированных под требуемую задачу системах (если таковые вообще существуют), и тем не менее, это будет рабочий инструмент.

Дополнительную функциональность в TrackStudio можно реализовать с помощью скриптов и триггеров. Скрипты обычно соответствуют вычисляемым дополнительным полям различных типов. Триггеры соответствуют событиям, происходящим с задачами, а именно: созданию задач, их редактированию и выполнению операций над ними. Скрипты вычисляются всякий раз при просмотре задачи, когда нужно отобразить значение поля, триггеры - при выполнении ассоциированных действий.

Скрипты и триггеры могут быть двух видов: интерпретируемые и скомпилированные. Интерпретируемые скрипты - это java-код, обрабатываемый интерпретатором Beanshell. Писать такие скрипты несколько проще, чем скомпилированные, т.к. не требуется согласования типов данных и наличия JDK для компиляции.

Скомпилированные же скрипты имеют ряд серьезных преимуществ:

- во-первых, они выполняются быстрее из-за того, что не нужно загружать и инициализировать интерпретатор
- во-вторых, если скрипт скомпилировался - там хотя бы нет синтаксических ошибок
- в-третьих, в скомпилированных скриптах вы можете использовать всю мощь языка Java. Например, наследование и статические переменные

## Как писать скрипты и триггеры?

Для того, чтобы писать и компилировать скрипты и триггеры вам понадобится: JDK (java development kit), который можно скачать с сайта [http://java.com](http://java.com/) (обратите внимание, именно JDK, а не JRE).

Вам потребуется файл trackstudio.jar (располагается в /webapps/TrackStudio/WEB-INF/lib/trackstudio.jar), который есть в любом варианте поставки TrackStudio. И лучше, если вы будете использовать какую-либо IDE, например IDEA, Eclipse или Netbeans. **Javadoc** от TrackStudio находится в папке **docs/javadoc** дистрибутива.

Все скрипты и триггеры должны реализовывать один из интерфейсов:

```
com.trackstudio.external.OperationTrigger - для триггеров, выполняющихся при операциях с задачами
com.trackstudio.external.TaskBulkProcessor - для триггеров, выполняющихся над выбранными из списка задачами
com.trackstudio.external.TaskTrigger - для триггеров, выполняющихся при создании или редактировании задачи
com.trackstudio.external.TaskUDFLookupScript - для наборов возможных значений дополнительных полей к задачам
com.trackstudio.external.TaskUDFValueScript - для значений дополнительных полей к задачам
com.trackstudio.external.UserUDFLookupScript - для наборов возможных значений дополнительных полей к пользователям
com.trackstudio.external.UserUDFValueScript - для значений дополнительных полей к пользователям
```

Кроме того, скомпилированные скрипты должны находиться в пакетах в соответствии со структурой папок внутри plugins в TrackStudio.

| **Тип скрипта** | **Назначение** | **Пакет** | **Папка (относительно etc/plugins)** |
| --- | --- | --- | --- |
| Create Task/Before | Выполняется до создания задачи | scripts.before_create_task | [scripts/before_create_task](https://github.com/winzard/ts-customize/tree/master/samples/scripts/before_create_task) |
| Create Task/InsteadOf | Выполняется вместо создания задачи | scripts.instead_of_create_task | scripts/instead_of_create_task |
| Create Task/After | Выполняется после того, как задача создана | scripts.after_create_task | [scripts/after_create_task](https://github.com/winzard/ts-customize/tree/master/samples/scripts/after_create_task) |
| Edit Task/Before | Выполняется до редактирования задачи | scripts.before_edit_task | [scripts/before_edit_task](https://github.com/winzard/ts-customize/tree/master/samples/scripts/before_edit_task) |
| Edit Task/InsteadOf | Выполняется вместо редактирования задачи | scripts.instead_of_edit_task | scripts/instead_of_edit_task |
| Edit Task/After | Выполняется после редактирования свойств задачи | scripts.after_edit_task | [scripts/after_edit_task](https://github.com/winzard/ts-customize/tree/master/samples/scripts/after_edit_task) |
| Add Message/Before | Выполняется до выполнения операции с задачей | scripts.before_add_message | [scripts/before_add_message](https://github.com/winzard/ts-customize/tree/master/samples/scripts/before_add_message) |
| Add Message/InsteadOf | Выполняется вместо операции с задачей | scripts.instead_of_add_message | [scripts/instead_of_add_message](https://github.com/winzard/ts-customize/tree/master/samples/scripts/instead_of_add_message) |
| Add Message/After | Выполняется после операции с задачей | scripts.after_add_message | scripts/after_add_message |
| Bulk | Выполняет действия над выбранными в списке задачами | scripts.bulk | [scripts/bulk](https://github.com/winzard/ts-customize/tree/master/samples/scripts/bulk) |
| Task Custom Field/Value | Вычисляет значение дополнительного поля для задачи | scripts.task_custom_field_value | [scripts/task_custom_field_value](https://github.com/winzard/ts-customize/tree/master/samples/scripts/task_custom_field_value) |
| Task Custom Field/Lookup | Формирует список возможных значений для дополнительного поля типа Строка к задаче | scripts.task_custom_field_lookup | [scripts/task_custom_field_lookup](https://github.com/winzard/ts-customize/tree/master/samples/scripts/task_custom_field_lookup) |
| User Custom Field/Value | Вычисляет значение дополнительного поля для пользователя | scripts.user_custom_field_value | [scripts/user_custom_field_value](https://github.com/winzard/ts-customize/tree/master/samples/scripts/user_custom_field_value) |
| User Custom Field/Lookup | Формирует список возможных значений для дополнительного поля типа Строка к пользователю | scripts.user_custom_field_lookup | scripts/user_custom_field_lookup |

Примеры скриптов вы также можете посмотреть и скачать из [репозитория на Github](https://github.com/winzard/ts-customize).

**package** scripts.before_add_message;

**import** com.trackstudio.exception.GranException;**import** com.trackstudio.exception.UserException;**import** com.trackstudio.external.OperationTrigger;**import** com.trackstudio.secured.SecuredMessageTriggerBean;**import** com.trackstudio.tools.textfilter.HTMLEncoder;

**public** **class** CheckSpentTimeScript **implements** OperationTrigger {

**public** **static** **final** **int** TIME_LIMIT = 1; */** time limit in milliseconds */* **public** **static** **final** **int** K = 20; */** conversion factors */* @Override **public** SecuredMessageTriggerBean execute(SecuredMessageTriggerBean message) **throws** GranException { String desc = HTMLEncoder.stripHtmlTags(message.getDescription()); **if** (message.getHrs() != **null** && message.getHrs() > TIME_LIMIT && desc.length()/message.getHrs() < K){ **throw** **new** UserException("Please "+ message.getSubmitter().getName()+", describe what you did at that time!"); } **return** message; }}

Скомпилировать такой скрипт можно командой

```
javac -cp path/trackstudio.jar CheckSpentTimeScript.java
```

Но лучше воспользоваться какой-нибудь IDE.

Далее можно либо положить полученный **класс** в папку **./etc/plugins/scripts/before_add_message/**, либо создать jar

```
cd ../../
jar cf myscripts.jar scripts
```

И положить его в папку **./etc/plugins/scripts**.
