[Home](../../index.md) | [Up (Installing on Linux)](index.md)

---

# Installing TrackStudio Standalone in Linux

**Important! Beginning with version TrackStudio 4.0.10, the system is shipped with a uninitialized database. Here is the description, how to initialize it.**

For the purpose of configuring and running TrackStudio Standalone on the server with graphic shell:

1. Run TrackStudio Enterprise Server Manager (**sman**).
2. In the menu, press the button “**Create Database**”
3. In the window that appears, select the type of database for initialization.

![](../../images/database_create_database_sman_ru.png)

1. Press the button “**OK**”
2. After the database has been created, press the button **Start** so as to launch TrackStudio server.
3. When the server is started, press the button **Login**. A window appears with the address, which needs to be entered into your browser.

For configuring and starting TrackStudio Standalone on the server with installied, but not started X11:

TrackStudio Enterprise 4.0.10 Standalone now has a console app dbtool for importing and exporting the database, which can be used for initialization.

1. Go to the folder, in which TrackStudio is installed.
2. In the console, execute the command

```
dbtool import --file=./etc/resources/sql/LAST/ru_def.xml
```

1. Run the applications server (**jetty**), included in the package.
2. In the browser, open the URL: [http://hostname:8888/TrackStudio](<%LINK_CAPTION%>)

**Use the following user account details:**

| Administrator | Administrator | root | root |
| --- | --- | --- | --- |
| Sergei Managerov | Managers | manager | 123 |
| Ivan Analytikov | Analysts | analitik | 123 |
| Dmitry Pisatelev | Technical Writers | writer | 123 |
| Stepan Razrabotchikov | Developers | developer | 123 |
| Maxim Testerov | Testers | tester | 123 |

## Note

If you run TrackStudio in *nix VPS (Virtual Private Server), you can get the errors “VM object heap”, and the reason for them can be the method of VPS memory management. JVM tries to estimate how much memory is available to it based upon the information from the same sources, which are used by the utilities ‘free’ and ‘top’. For example, VServer VPS returns the total memory on the physical machine and does not reflect the limits of memory for virtual machines.

For correcting the situations, JVM must be instructed how much memory needs to be used:

```
> sman -J-Xmx256m> jetty -J-Xmx256m
```

If you are using DBMS HSQLDB, the data base files will look like *tsdb.lck, tsdb.log, tsdb.properties* and *tsdb.script.* Do not delete them.

---

[Home](../../index.md) | [Up (Installing on Linux)](index.md)
