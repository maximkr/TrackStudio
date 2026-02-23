[Home](../../index.md) | [Up (Connecting to a Database)](index.md)

---

# Connecting to Firebird

1. Run the program **isql** included in Firebird

1. Create the database:

```
SQL> create database 'c:\trackstudio.gdb' user 'sysdba' password 'masterkey';SQL> connect 'c:\trackstudio.gdb' user 'sysdba' password 'masterkey';Commit current transaction (y/n)?yCommitting.Database: 'c:\trackstudio.gdb', User: sysdba
```

1. Configure the DBMS connection properties:

- **TrackStudio SA:** On the tab **Database**, select Firebird from the DBMS list and edit the JDBC connection properties.
- **TrackStudio WAR:** Edit the connection properties in the file

**trackstudio.hibernate.properties**:

```
hibernate.connection.driver_class=org.firebirdsql.jdbc.FBDriverhibernate.connection.password=masterkeyhibernate.connection.url=jdbc\:firebirdsql\://localhost/c\:/trackstudio.gdbhibernate.connection.username=sysdbahibernate.dialect=org.hibernate.dialect.FirebirdDialect
```

1. Initialize the database:

- **TrackStudio SA:** Press the button **Create DB**** **on the tab **Database**
- **TrackStudio WAR**: Run the script**sql\install\ru\trackstudio-firebird.sql**

```
SQL> in trackstudio-firebird.sql;

```

---

[Home](../../index.md) | [Up (Connecting to a Database)](index.md)
