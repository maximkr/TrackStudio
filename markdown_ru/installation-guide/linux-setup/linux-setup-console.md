[Домой](../../index.md) | [Наверх (Установка под Linux)](index.md)

---

# Установка TrackStudio Standalone из консоли

```
Для работы с TrackStudio на вашем сервере должны быть установлены библиотеки X11, т.к. они требуются Java для работы с графическими файлами. При этом для установки запуска TrackStudio графического окружения не нужно, достаточно доступа к серверу по SSH.
```

1. Скачайте дистрибутив TrackStudio **Standalone** для Linux
2. Распакуйте архив TrackStudio_4013_unix_with_jre.tar.gz *название может отличаться) в нужную вам папку

```
tar xvfz TrackStudio_4013_unix_with_jre.tar.gz
```

1. Перейдите в папку

```
cd TrackStudio-4013
```

В этой папке расположены:

- Папка **docs** с документацией по TrackStudio API
- Папка **etc** с конфигурационными файлами. В этой папке также содержится папка **plugins** со скриптами, триггерами и почтовыми шаблонами.
- Папка **sql** со скриптами инициализации баз данных, которые вам, скорее всего, не понадобятся, т.к.
- С версии TrackStudio Enterprise 4.0.10 в состав дистрибутива Standalone входит консольная утилита для работы с базой данных **dbtool**
- Скрипт запуска сервера TrackStudio: **startJetty**
- Скрипт останова: **stopJetty**
- Скрипт для запуска в качестве службы **trackstudio**

## Настройка и запуск

1. [Создайте новую базу данных](../initializing-database/index.md), но не инициализируйте ее. Например, для PostgreSQL выполните в консоли

```
createdb -E UNICODE -h localhost -U postgres -W trackstudio4013
```

1. Укажите параметры соединения с базой в файле **etc/trackstudio.hibernate.default.properties**

```
vim etc/trackstudio.hibernate.default.properties
```

```
hibernate.dialect org.hibernate.dialect.PostgreSQLDialect
hibernate.connection.url jdbc:postgresql://127.0.0.1:5432/trackstudio4013
hibernate.connection.driver_class org.postgresql.Driver
hibernate.connection.username postgres
hibernate.connection.password postgres
```

1. Для инициализации базы данных в консоли выполните команду

```
./dbtool import --file=./etc/resources/sql/LAST/ru_def.xml
```

1. Запустите сервер приложений(**jetty**)

```
./startJetty
```

1. Откройте в браузере URL: [http://hostname:8888/TrackStudio](<%LINK_CAPTION%>)

**Используйте следующие учетные записи:**

| **Пользователь** | **Группа** | **Логин** | **Пароль** |
| --- | --- | --- | --- |
| Администратор | Администратор | root | root |
| Сергей Менеджеров | Менеджеры | manager | 123 |
| Иван Аналитиков | Аналитики | analyst | 123 |
| Дмитрий Писателев | Технические писатели | writer | 123 |
| Степан Разработчиков | Разработчики | developer | 123 |
| Максим Тестеров | Тестеры | tester | 123 |

## Примечания

Если вы используете СУБД HSQLDB, файлы базы данных будут иметь вид *tsdb.lck, tsdb.log, tsdb.properties* and *tsdb.script.* Не удаляйте их.

---

[Домой](../../index.md) | [Наверх (Установка под Linux)](index.md)
