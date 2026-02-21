# Создание и инициализация базы данных под ORACLE

1. Создайте Tablespace.
2. Создайте пользователя для TrackStudio
3. Установите права **DBA** и **Resource** созданному пользователю.
4. Обновите JDBC драйвер до вашей текущей версии.
5. Настройте свойства соединения с СУБД. Строка параметров соединения Oracle содержит **Database URL**, **название драйвера JDBC**** **,**логин** и **пароль**. Первая часть (до "@") строки параметров общая, Вам не нужно ее менять. После символа @ вы должны ввести адрес в формате **HostAddress:Port:ORACLE_SID**. Если Вы используете локально установленный экземпляр Oracle, **HostAddress**будет *localhost*. Порт по-умолчанию в Oracle *1521* (не путать с кодировкой Windows-1251), **ORACLE_SID** по-умолчанию равен *ORCL*. Название **драйвера JDBC** не меняйте.

- **TrackStudio SA:** На вкладке **База данных** выберите из списка СУБД ORACLE и отредактируйте свойства соединения JDBC
- **TrackStudio WAR:** Отредактируйте свойства соединения в файле **trackstudio.hibernate.properties**:

```
hibernate.dialect org.hibernate.dialect.Oracle10gDialect
hibernate.connection.url jdbc:oracle:thin:@localhost:1521:ORCL
hibernate.connection.driver_class oracle.jdbc.OracleDriver
hibernate.connection.username trackstudio
hibernate.connection.password trackstudio
```

1. Инициализируйте базу данных:

- **TrackStudio SA:** На вкладке **База данных** нажмите кнопку **Создать БД**
- **TrackStudio WAR**: Выполните скрипт **sql\install\ru\trackstudio-oracle.sql**

```
>sqlplus
SQL*Plus: Release 10.1.0.2.0 - Production
Copyright (c) 1982, 2004, Oracle.  All rights reserved.
Enter user-name: trackstudio
Enter password:
Connected to:
Oracle Database 10g Enterprise Edition Release 10.1.0.2.0 - Production
With the Partitioning, OLAP and Data Mining options
SQL> set define off;
SQL> @@ trackstudio-oracle.sql
```
