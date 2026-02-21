[Home](../../index.md) | [Up (Connecting to a Database)](index.md)

---

# Connecting to DB2

1. Create user tablespace and temp system tablespace.
2. Open the console.
3. Establish connection to the database:

```
> db2 connect to <databasename> user <dbuser> using <password>
```

1. Configure the DBMS connection properties:

- **TrackStudio SA:** On the tab **Database**** **, select DB2 from the DBMS list and edit the JDBC connection properties.
- **TrackStudio WAR:** Edit the connection properties in the file **trackstudio.hibernate.properties**:

```
hibernate.dialect org.hibernate.dialect.DB2Dialecthibernate.connection.url jdbc:db2://127.0.0.1/trackstudiohibernate.connection.driver_class COM.ibm.db2.jdbc.net.DB2Driverhibernate.connection.username db2adminhibernate.connection.password db2admin
```

1. Initialize the database:

- **TrackStudio SA:** Press the button **Create DB**** **on the tab **Database**.
- **TrackStudio WAR**: Run the script **sql\install\ru\trackstudio-db2.sql:**

```
> db2 -tvf trackstudio-db2.sql

```

---

[Home](../../index.md) | [Up (Connecting to a Database)](index.md)
