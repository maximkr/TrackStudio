# Архитектура TrackStudio (для разработчиков)

Этот документ — инженерный гайд по архитектуре TrackStudio. Он фиксирует ключевые архитектурные решения и действующие технические ограничения. Если изменение кода затрагивает описанные здесь положения (ACL, кеши, запуск, многопоточность, API, модель данных), такие изменения нужно согласовывать отдельно.

## 1. Назначение и контекст
TrackStudio — stateful серверное приложение для управления задачами с иерархией задач и пользователей, ACL-моделью прав, workflow, фильтрами, уведомлениями, пользовательскими полями и скриптовой расширяемостью.

Базовый стек в текущей кодовой базе:
- Java 21, Tomcat 9+, WAR.
- PostgreSQL + Hibernate (`.hbm.xml`).
- Lucene для поиска.
- Файловое хранилище для вложений и плагинов.
- Логирование: SLF4J + Logback (миграция с Log4j 1.2).

## 2. Слои и основные пакеты
1. Web/UI слой:
`com.trackstudio.action`, JSP/Tiles, сервлеты (`/task/*`, `/user/*`, `/download/*`, `/template/*`, `/rest/*`), `app-shell.html/js` (Shell UI).
2. Прикладной слой:
`AdapterManager`, `Secured*`-адаптеры и `SessionContext`.
3. Ядро:
`KernelManager` и менеджеры (`TaskManager`, `UserManager`, `CategoryManager`, `FilterManager`, `UdfManager`, `AttachmentManager`, `TriggerManager`).
4. Кеши и runtime:
`TaskRelatedManager`, `UserRelatedManager`, `CategoryCacheManager`, `PluginCacheManager`, `SessionManager`.
5. Persistence:
Hibernate-модели и SQL/Liquibase.
6. Инфраструктура:
Docker (`Dockerfile`, `docker-compose.yml`), Tomcat 9+.

## 3. Старт системы и конфигурация
### 3.1 Порядок запуска
Инициализация происходит в `TSLoader.init`:
1. `I18n.loadConfig`.
2. `Config.loadConfig`.
3. `Config.checkAndSetConfigParameters`.
4. `AttachmentValidator.validate` и `DatabaseValidator.validate`.
5. Инициализация кешей: `TaskRelatedManager`, затем `UserRelatedManager`.
6. `KernelManager.getIndex`, `SessionManager`, JMX MBeans.
7. `PluginCacheManager`, `SchedulerManager`.
8. Регистрация адаптеров через `Config.registerAdapters("trackstudio.adapters.properties")`.

### 3.2 Где читаются конфиги
`Config.initTrackStudioHome` использует приоритет:
1. `TS_CONFIG` (переменная окружения).
2. `-Dtrackstudio.Home`.
3. `/WEB-INF` (если там есть `trackstudio.adapters.properties`).

### 3.3 Файлы конфигурации
Загружаются пары default/override (override-перекрывает ключи):
- `trackstudio.default.properties` / `trackstudio.properties`.
- `trackstudio.mail.default.properties` / `trackstudio.mail.properties`.
- `trackstudio.hibernate.default.properties` / `trackstudio.hibernate.properties`.
- `trackstudio.security.default.properties` / `trackstudio.security.properties`.
- `trackstudio.adapters.properties`.

Папки плагинов берутся из `Config`:
- `getPluginsDir()` -> `.../plugins/`.
- `getWebDir()` -> `.../plugins/web/`.
- `getEmailDir()` -> `.../plugins/e-mail/`.

## 4. Данные: БД, диск, память
### 4.1 БД как persistence-слой
База хранит устойчивое состояние: задачи, пользователей, ACL, workflow, фильтры, уведомления, UDF, вложения, свойства.

### 4.2 In-memory как рабочее состояние
Ключевая производительность достигается за счет постоянного in-memory представления иерархий:
- дерево задач + ACL в `TaskRelatedManager`;
- дерево пользователей + ACL в `UserRelatedManager`;
- category visibility/validity в `CategoryCacheManager`;
- сессии в `SessionManager`.

После изменения в БД ядро вызывает `invalidate*`-методы и актуализирует кеш, чтобы чтения шли из памяти без повторного тяжелого обхода БД.

### 4.3 Длинные тексты (`gr_longtext`)
`LongTextManager` хранит длинный текст чанками (историческая совместимость с ограничениями старых Oracle):
- master-запись (`order=0`);
- дочерние чанки (`reference=masterId`, `order=1..N`).

`TaskRelatedInfo`, `MessageCacheItem`, `UdfvalCacheItem` читают longtext лениво и кешируют в памяти.

## 5. Схема хранения вложений
### 5.1 Что в БД
`gr_attachment` хранит метаданные:
- `attachment_id`;
- `attachment_name`;
- `attachment_description`;
- ссылки на сущности: `attachment_task`, `attachment_message`, `attachment_user`.

### 5.2 Что на диске
Файл лежит в `trackstudio.uploadDir` (или `trackstudio.archiveDir` для архива), имя файла = `attachmentId`.

Путь вычисляется `AttachmentManager.getAttachmentDirPath(taskId, userId, archive)`:
- для задачи: `<uploadDir>/<taskId...>/<attachmentId>`;
- для пользователя: `<uploadDir>/user<userId...>/<attachmentId>`;
- если id длиннее 20 символов, путь делится на 2 уровня (первые 20 символов + остаток).

### 5.3 Связь БД и файла
`AttachmentManager.createAttachment`:
1. создает запись в `gr_attachment`;
2. пишет бинарный файл на диск под именем `attachmentId`;
3. обновляет индекс (`reIndexAttachment`).

`deleteAttachment` удаляет и запись, и файл.

### 5.4 Доступ
`DownloadServlet` перед отдачей файла проверяет права через `SessionContext` (`viewTaskAttachments` / `viewUserAttachments`).

## 6. ACL и проверка прав
### 6.1 Точки входа
Проверки инициируются из `SessionContext`:
- `allowedByACL`, `taskOnSight` (задачи);
- `allowedByUser`, `userOnSight` (пользователи);
- `canAction(...)` для проверок действий.

### 6.2 Task ACL
`TaskRelatedManager` хранит ACL в map вида:
- ключ `userId` (персональный ACL),
- ключ `GROUP_<prstatusId>` (групповой ACL).

Алгоритм учитывает:
- цепочку предков задачи;
- `override` для прерывания наследования;
- ограничение owner-chain (`getUserIdChain(owner, user)`);
- category visibility (`CategoryManager.isCategoryViewable`).

### 6.3 User ACL
`UserRelatedManager` применяет аналогичный алгоритм для иерархии пользователей.

### 6.4 Непереопределяемые действия
`Action.taskUnOverridedActions` и `Action.userUnOverridedActions` учитываются в `canAction`.

## 7. Кеши и производительность
### 7.1 Основная идея
Высокая производительность строится на том, что:
- БД используется как persistence;
- актуальная иерархия задач-пользователей-прав поддерживается в памяти и обновляется инкрементально.

### 7.2 Основные кеш-компоненты
- `TaskRelatedManager` / `UserRelatedManager`: деревья + ACL + быстрые цепочки доступа.
- `CategoryCacheManager`: `isCategoryViewable`, `isCategoryValid`.
- `TaskRelatedInfo` / `UserRelatedInfo`: cached UDF/ACL/derived fields.
- `PluginCacheManager`: скрипты/классы плагинов.

### 7.3 Инвалидация
После изменения ACL/перемещения задач/смены статусов используются:
- `invalidateAcl`, `invalidateAclWhenMove`, `invalidateAclWhenChangeStatus`;
- инвалидация category cache.

Это гарантирует: к следующему запросу in-memory модель уже согласована с persistence.

## 8. Многопоточность и правила изменения ядра
### 8.1 Базовая дисциплина
- В kernel использовать только lock API (`LockManager`, `ReadWriteLock`).
- Не смешивать lock и `synchronized` в одном методе.
- Лочить public-операции целиком (или выносить в отдельные методы).
- `acquireConnection` / `releaseConnection` всегда парно в `try/finally`.

### 8.2 Управление состоянием в классах
- Mutable-состояние изолировать в классе.
- Для состояния использовать `Atomic*`/`Concurrent*`/`CopyOnWrite*` и явные lock’и.
- Доступ к shared-состоянию оборачивать read/write lock’ами.

### 8.3 Правила экспорта внутренних структур
- Наружу отдавать копию или read-only view.
- Не возвращать внутренние mutable-коллекции напрямую.
- Для ACL использовать подход как в `getAcl()`/`getReadOnlyAcl()`.

### 8.4 Запреты и deadlock discipline
- Не делать upgrade lock (read -> write в рамках одного потока и одной секции).
- Соблюдать единый порядок захвата локов по сущностям (не менять порядок между методами).
- При добавлении новых lock-сценариев сохранять текущий порядок и документировать его.

## 9. UI-архитектура и Java-поддержка UI
### 9.1 Общая модель UI
- Legacy UI: Struts + JSP/Tiles.
- Shell UI: `app-shell.html/js` (обертка, sidebar + контент).
- Дерево: данные приходят из `TreeLoaderAction`.

### 9.2 Фильтры запросов
- `CompressResponseFilter`: сжатие ответов для `/task/*`, `/user/*`, `/JSServlet/*`, `/CSSServlet/*`, `*.do`, `*.jsp`, `*.html`.
- `CORSFilter`: разрешает CORS для `/rest/*`.

### 9.3 Списки и фильтры
- `TaskFilter`, `MessageFilter`, `FilterSettings`, `TaskFValue`, `UserFValue`.
- Отрисовка списков зависит от filter settings (колонки, сортировки, параметры).
- Формирование данных идет через `Secured*Bean` и cache-слой.

### 9.3 Кастомайзеры
`app/filter/customizer`:
- `Customizer` (база);
- `TextCustomizer`, `DateCustomizer`, `ListCustomizer`, `CheckboxCustomizer`, `BudgetCustomizer`, `PopUpCustomizer`.

Они отвечают за HTML-представление параметров фильтра и обратный парсинг в `FValue`.

## 10. Расширяемость: скрипты, триггеры, e-mail
### 10.1 Типы плагинов
`PluginType` покрывает:
- before/instead/after task/message/mail import;
- UDF value/lookup;
- bulk/multi-bulk;
- scheduler/report/macros;
- e-mail/web/icon/xslt/txt.

### 10.2 Выполнение триггеров
`TriggerManager` и `TriggerExecute`:
- before -> instead_of (или default действие) -> after;
- поддержка BeanShell и compiled plugin class.

### 10.3 E-mail шаблоны
- Шаблоны берутся из `PluginType.EMAIL`.
- Поддержаны маркеры:
  - `<#-- includeAttachments-->`;
  - `<#-- includeAllAttachments-->`.
- `AddressDictionary` поддерживает `TO`, `CC`, `BCC`, `FromEmail`, `FromUser`, `ReplyTo`, `Subject`, `Headers`, `ContentType`.

## 11. REST и SOAP
### 11.1 REST
`RestDispatcherServlet` маршрутизирует:
- `/rest/auth`: `login`, `session`;
- `/rest/task`: `task`, `tasks`, `messages`;
- `/rest/user`: `login`.

REST stateful: после login используется `sessionId`.
CORS включён через `CORSFilter` для всех `/rest/*` запросов.

### 11.2 SOAP
В коде есть DTO-пакет `com.trackstudio.soap.bean.*`.
Прямого SOAP сервлет-маппинга в текущем `web.xml` нет; DTO используются как формат представления в части сервисов и REST-конвертации.

## 12. Иерархическая модель: задачи, фильтры, процессы, UDF
### 12.1 Привязка к задачам
- Все ключевые сущности привязаны к task tree: категории, workflow, фильтры, notifications/subscriptions.
- Наследование работает через обход цепочки предков/потомков.

### 12.2 Наследование фильтров
- `FilterManager.getTaskFilterList(taskId,userId)` поднимается по предкам задачи.
- Берутся публичные и приватные фильтры (приватные только owner).
- `getAllTaskFilterList` включает дочерний контекст через `hasPath`.

### 12.3 Процессы и категории
- Категория привязана к задаче и указывает workflow.
- Валидность категории для задачи проверяется по parent-category relation + workflow validity + permission consistency.
- Права на операции по категории задаются `gr_cprstatus`.

### 12.4 Кастом-поля
Типы (`UdfConstants`):
- `STRING`, `FLOAT`, `DATE`, `LIST`, `MLIST`, `INTEGER`, `MEMO`, `TASK`, `USER`, `URL`.

Источники (`Udfsource`):
- task-level,
- user-level,
- workflow-level.

Наследование:
- task UDF наследуются по дереву задач;
- user UDF — по дереву пользователей;
- workflow UDF — по выбранному процессу.

## 13. Схема БД: таблицы и назначение
- `gr_task`: задачи, parent/submitter/handler/category/status/resolution/priority.
- `gr_user`: пользователи, manager/prstatus/template/default project и профильные параметры.
- `gr_acl`: правила ACL для задач/пользователей через `usersource`, `owner`, `prstatus`, `override`.
- `gr_usersource`: ссылка на user или prstatus (используется как источник handler/ACL/subscription).
- `gr_category`, `gr_catrelation`, `gr_cprstatus`: категории, связи категорий и права на категории.
- `gr_workflow`, `gr_status`, `gr_mstatus`, `gr_transition`, `gr_resolution`, `gr_priority`: процессы и статусы.
- `gr_filter`, `gr_fvalue`, `gr_currentfilter`: фильтры и их параметры/текущее состояние.
- `gr_notification`, `gr_subscription`: уведомления и подписки.
- `gr_udf`, `gr_udfsource`, `gr_udflist`, `gr_udfval`, `gr_uprstatus`, `gr_umstatus`: пользовательские поля, источники, значения и права.
- `gr_message`: сообщения задач.
- `gr_attachment`: метаданные вложений.
- `gr_longtext`: длинные тексты (чанки).
- `gr_trigger`: ссылки на before/instead/after скрипты.
- `gr_property`: runtime свойства.
- `gr_report`, `gr_template`, `gr_mailimport`, `gr_registration`, `gr_bookmark`, `gr_rolestatus`, `gr_mprstatus`: дополнительные функциональные таблицы.

## 14. Практическое правило для изменений
Если изменение влияет на:
- порядок локов,
- модель ACL,
- структуру/инвалидацию кешей,
- процесс запуска и конфиг,
- контракты REST,

то вместе с кодом нужно обновить этот документ и отдельно согласовать изменение архитектуры.
