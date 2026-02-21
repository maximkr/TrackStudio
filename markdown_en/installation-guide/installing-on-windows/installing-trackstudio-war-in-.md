[Home](../../index.md) | [Up (Installing on Windows)](index.md)

---

# Installing TrackStudio War in Windows

Note. Beginning TrackStudio 4.0.10, the system comes with the uninitialized database. The method for initializing it has been described here.

Installing and configuring TrackStudio WAR in Windows:

1. Download the package TrackStudio WAR
2. Unpack the archive to a new folder.

This folder contains:

- Folder **javadoc** with TrackStudio API documentation
- Folder **etc** with configuration files. In future, this folder will be used as a root for TrackStudio. This folder also contains the folder **plugins** with scripts, triggers and email templates.
- Folder **sql** containing scripts for database initialization.
- File **TrackStudio.war**

1. Copy the file **TrackStudio.war** to the folder, where Tomcat web-applications are located. For example, C:\Program Files (x86)/Tomcat 6/webapps/
2. [Create a new database](../connecting-to-a-database/index.md). Indicate the database connection in the file **trackstudio.hibernate.properties**.
3. Set the system environment variable **TS_CONFIG** in the Windows “Control Panel.” Select the menu item “System”, tab “Advanced” and below click the button “Environment Variables”. This variable must point to the folder **etc** of your TrackStudio copy:
4. Define the location, where indices and files uploaded to TrackStudio will be saved. For example, these can be the folders **index** and **upload**, respectively.

Set the absolute path or path w.r.t. the executable Tomcat file to these folders in the file trackstudio.properties:

```
trackstudio.uploadDir uploadtrackstudio.indexDir index
```

1. In the same file trackstudio.properties, indicate the URL, where TrackStudio (in accordance with Tomcat settings) will open.

```
trackstudio.siteURL [http://localhost:8080/TrackStudio](http://localhost:8080/TrackStudio)
```

1. Start the applications server Tomcat using the command

```
startup.bat
```

1. After starting Tomcat in the browser at address [http://localhost:8080/TrackStudio/](http://localhost:8080/TrackStudio/), a page looking like the below given must open:

![](../../images/login.png)

**Use the following user account details:**

| Administrator | Administrator | root | root |
| --- | --- | --- | --- |
| Sergei Managerov | Managers | manager | 123 |
| Ivan Analytikov | Analysts | analitik | 123 |
| Dmitry Pisatelev | Technical Writers | writer | 123 |
| Stepan Razrabotchikov | Developers | developer | 123 |
| Maxim Testerov | Testers | tester | 123 |

## Note

You can’t run more than one instance of TrackStudio under the same applications server.

---

[Home](../../index.md) | [Up (Installing on Windows)](index.md)
