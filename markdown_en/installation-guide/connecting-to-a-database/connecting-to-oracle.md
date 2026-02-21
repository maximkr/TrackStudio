[Home](../../index.md) | [Up (Connecting to a Database)](index.md)

---

# Connecting to Oracle

1. Create the Tablespace.
2. Create the user for TrackStudio.
3. Set the **DBA** and **Resource** permissions for the created user.
4. Configure the DBMC connection properties. String of parameters of Oracle connection contains **Database URL**, **name of JDBC driver**,**login** and **password**. The first part (before "@") of the string of parameters is common, you don’t need to change it. After the symbol @, you must add the address in the format **HostAddress:Port:ORACLE_SID**. If you are using the locally installed Oracle, **HostAddress** will be *localhost*. The default port in Oracle is *1521** *(do not confuse with the Windows code – 1251), default **ORACLE_SID** is equal to *ORCL*. Do not change the name of **JDBC driver**.

- **TrackStudio SA:** On the tab **Database** select ORACLE from the DBMS list and edit the properties of JDBC connection.
- **TrackStudio WAR:** Edit the connection properties in the file

**trackstudio.hibernate.properties**:

```
hibernate.dialect org.hibernate.dialect.Oracle10gDialecthibernate.connection.url jdbc:oracle:thin:@localhost:1521:ORCLhibernate.connection.driver_class oracle.jdbc.OracleDriverhibernate.connection.username trackstudiohibernate.connection.password trackstudio
```

1. Initialize the database:

- **TrackStudio SA:** On the tab **Database**** **press the button **Create DB**
- **TrackStudio WAR**: Run the script **sql\install\ru\trackstudio-oracle.sql**

```
>sqlplusSQL*Plus: Release 10.1.0.2.0 - ProductionCopyright (c) 1982, 2004, Oracle.  All rights reserved.Enter user-name: trackstudioEnter password:Connected to:Oracle Database 10g Enterprise Edition Release 10.1.0.2.0 - ProductionWith the Partitioning, OLAP and Data Mining optionsSQL> set define off;SQL> @@ trackstudio-oracle.sql

```

---

[Home](../../index.md) | [Up (Connecting to a Database)](index.md)
