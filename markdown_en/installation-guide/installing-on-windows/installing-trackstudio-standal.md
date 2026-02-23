[Home](../../index.md) | [Up (Installing on Windows)](index.md)

---

# Installing TrackStudio Standalone in Windows

Important! Beginning version TrackStudio 4.0.10, the system is shipped with the uninitialized database. Here is the description about how to initialize it.

For the purpose of installing TrackStudio Standalone:

1. Run TrackStudio Enterprise Server Manager (**sman.exe**).
2. Select “English” in the menu File->Language, if inscriptions are in The Russian language.
3. In the menu, press the button “"**Create Database"**”
4. In the window that appears, select the type of database for initialization.

![](../../images/database_create_database_en.png)

1. Press the button "**ОК**"
2. After that, when the database has been created, press the button **Start**, so as to run TrackStudio server.
3. When the server starts, press the button "** ****Login**** **".

**Use the following user account details:**

| Administrator | Administrator | root | root |
| --- | --- | --- | --- |
| Sergei Managerov | Managers | manager | 123 |
| Ivan Analytikov | Analysts | analitik | 123 |
| Dmitry Pisatelev | Technical Writers | writer | 123 |
| Stepan Razrabotchikov | Developers | developer | 123 |
| Maxim Testerov | Testers | tester | 123 |

## Note:

If you are using DBMS HSQLDB, files of the database will have the form *tsdb.lck, tsdb.log, tsdb.properties* and *tsdb.script*. Do not delete them.

---

[Home](../../index.md) | [Up (Installing on Windows)](index.md)
