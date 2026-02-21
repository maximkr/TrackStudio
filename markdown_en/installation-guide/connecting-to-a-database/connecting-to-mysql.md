[Home](../../index.md) | [Up (Connecting to a Database)](index.md)

---

# Connecting to MySQL

1. Start the terminal client for MySQL.

```
shell> mysql -u root
```

1. Create the database:

```
mysql> create database trackstudio character set utf8;mysql> commit;mysql> use trackstudio;
```

1. Configure the DBMS connection properties:

- **TrackStudio SA:** On the tab **Database**, select MySQL from the DB list and edit the JDBC connection properties
- **TrackStudio WAR:** Edit the connection properties in the file **trackstudio.hibernate.properties**:

```
hibernate.connection.driver_class=com.mysql.jdbc.Driverhibernate.connection.password=hibernate.connection.url=jdbc\:mysql\://localhost/trackstudio?useUnicode\=true&characterEncoding\=UTF-8&autoReconnect\=truehibernate.connection.username=roothibernate.dialect=org.hibernate.dialect.MySQLInnoDBDialect
```

1. Initialize the database:

- **TrackStudio SA:** On the tab **Database**** **press the button **Create DB**
- **TrackStudio WAR**: Run the script **sql\install\ru\trackstudio-mysql.sql**

```
shell>mysql -u <login> -p <password> trackstudio --default-character-set=utf8 <./trackstudio-mysql.sql

```

---

[Home](../../index.md) | [Up (Connecting to a Database)](index.md)
