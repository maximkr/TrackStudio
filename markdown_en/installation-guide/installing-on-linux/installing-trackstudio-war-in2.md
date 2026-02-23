[Home](../../index.md) | [Up (Installing on Linux)](index.md)

---

# Installing TrackStudio WAR in Linux

Note. Beginning version TrackStudio 4.0.10, the system comes with the uninitialized database. The method for initializing the database has been described here.

For installing and configuring TrackStudio WAR:

1. Download the package TrackStudio WAR
2. Unpack the archive to a new folder.

This folder contains:

- Folder **javadoc** with TrackStudio API documentation
- Folder **etc** containing configuration files. In future, this folder will be used as a root for TrackStudio. This folder also contains the folder **plugins** with scripts, triggers, and email templates.
- Folder **sql** with scripts for data base initialization.
- File **TrackStudio.war**

1. Copy the file **TrackStudio.war** to the folder, where web-applications Tomcat are located. For example, /var/lib/tomcat6/webapps/
2. [Create a new database](../connecting-to-a-database/index.md). Indicate the parameters for connecting the database in the file **trackstudio.hibernate.properties**.
3. Indicate the variable **TS_CONFIG** in the file **setenv.sh** in the folder **bin** of Tomcat6. This variable must point to the folder **etc** of your copy of TrackStudio:

```
TS_CONFIG="/home/winzard/TS_WAR/etc"export TS_CONFIG
```

1. Define the location, where indices and files uploaded to TrackStudio will be saved. For example, these can be the folders **index** and **upload**, respectively.

Indicate these folders in the file trackstudio.properties:

```
trackstudio.uploadDir /home/winzard/TS_WAR/uploadtrackstudio.indexDir /home/winzard/TS_WAR/index
```

1. **It is important**** **who starts Tomcat, had permission to make entries in these folders. This can be done by changing their owner. Execute the following commands in the terminal:

```
sudo chown -R tomcat6:tomcat6 /home/winzard/TS_WAR/uploadsudo chown -R tomcat6:tomcat6 /home/winzard/TS_WAR/index
```

1. In the same file trackstudio.properties, indicate the URL, where TrackStudio will open (in accordance with Tomcat settings)

```
trackstudio.siteURL [http://localhost:8080/TrackStudio](http://localhost:8080/TrackStudio)
```

1. Start the applications server Tomcat using command

```
sudo service tomcat6 start
```

OR

```
sudo service tomcat6 restart
```

or, if you start Tomcat, not as a service, execute the file **startup.sh** from the folder **bin** of Tomcat 6

1. After starting Tomcat in the browser at the address [http://localhost:8080/TrackStudio/](http://localhost:8080/TrackStudio/), a page looking like the following one must open:

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

You can’t run several instances of TrackStudio in one applications server.

---

[Home](../../index.md) | [Up (Installing on Linux)](index.md)
