# Создание и инициализация базы данных под DB2

1. Создайте user tablespace и temp system tablespace.
2. Откройте консоль.
3. Присоединитесь к базе данных:

```
> db2 connect to <databasename> user <dbuser> using <password>
```

1. Настройте свойства соединения с СУБД:

- **TrackStudio SA:** На вкладке **База данных** выберите из списка СУБД DB2 и отредактируйте свойства соединения JDBC
- **TrackStudio WAR:** Отредактируйте свойства соединения в файле **trackstudio.hibernate.properties**:

```
hibernate.dialect org.hibernate.dialect.DB2Dialect
hibernate.connection.url jdbc:db2://127.0.0.1/trackstudio
hibernate.connection.driver_class COM.ibm.db2.jdbc.net.DB2Driver
hibernate.connection.username db2admin
hibernate.connection.password db2admin
```

1. Инициализируйте базу данных:

- **TrackStudio SA:** На вкладке **База данных** нажмите кнопку **Создать БД**
- **TrackStudio WAR**: Выполните скрипт **sql\install\ru\trackstudio-db2.sql:**

```
> db2 -tvf trackstudio-db2.sql
```
