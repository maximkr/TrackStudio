[Home](../../index.md) | [Up (Connecting to a Database)](index.md)

---

# Connecting to HSQL

1. Configure the DBMC connection properties:

- **TrackStudio SA:** On the tab **Database** select HSQL from the DBMS list and edit the properties of JDBC connection
- **TrackStudio WAR:** Edit the connection properties in the file **trackstudio.hibernate.properties**:

```
hibernate.connection.driver_class=org.hsqldb.jdbcDriverhibernate.connection.password=hibernate.connection.url=jdbc\:hsqldb\:file\:tsdb/ru/tsdb;shutdown\=truehibernate.connection.username=sahibernate.dialect=org.hibernate.dialect.HSQLDialect
```

1. Initialize the database:

```
> java -cp hsqldb.jar org.hsqldb.util.DatabaseManager

```

---

[Home](../../index.md) | [Up (Connecting to a Database)](index.md)
