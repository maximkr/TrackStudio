# Создание и инициализация базы данных под HSQL

1. Настройте свойства соединения с СУБД:

- **TrackStudio SA:** На вкладке **База данных** выберите из списка СУБД HSQL Russian и отредактируйте свойства соединения JDBC

![](../../images/hsql_db_init.png)

- **TrackStudio WAR:** Отредактируйте свойства соединения в файле **trackstudio.hibernate.properties**:

```
hibernate.connection.driver_class=org.hsqldb.jdbcDriver
hibernate.connection.password=
hibernate.connection.url=jdbc\:hsqldb\:file\:tsdb/ru/tsdb;shutdown\=true
hibernate.connection.username=sa
hibernate.dialect=org.hibernate.dialect.HSQLDialect
```

1. Инициализируйте базу данных:

- **TrackStudio SA:** На вкладке **База данных** нажмите кнопку **Создать БД**

![](../../images/postgres_db_create.png)

- **TrackStudio WAR**: Выполните скрипт **sql\install\ru\trackstudio-hsql.sql**

```
> java -cp hsqldb.jar org.hsqldb.util.DatabaseManager
```
