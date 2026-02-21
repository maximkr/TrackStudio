[Home](../../index.md) | [Up (Updating TrackStudio)](index.md)

---

# Upgrade from version 3.5

## For TrackStudio Standalone:

1. Download the latest TrackStudio Standalone
2. Stop TrackStudio
3. Install downloaded TrackStudio Standalone in separate directory.
4. Configure new TrackStudio instance using **Server Manager (sman)**.
5. If you using **HSQLDB**, copy all database files (tsdb.*) from old instance into new one.
6. Copy all files from your UPLOAD directory into new one. See **trackstudio.uploadDir** in **trackstudio.properties** to locate upload directory.
7. Remove files containing in index directory, but not the directory itself. See **trackstudio.indexDir** property in your **trackstudio.properties **file to locate index directory.
8. Update your database with Server Manager or SQL update files.
9. Start TrackStudio. System will update data in you database on first run and saves scripts, triggers and e-mail templates in **etc/plugins **directory.
10. Stop TrackStudio.
11. You should fix you e-mail templates in **etc/plugins/email** directory manually or use some of our default templates.
12. Start TrackStudio again after that.

## For TrackStudio WAR:

1. Download the latest TrackStudio WAR.
2. Stop TrackStudio
3. Replace old TrackStudio WAR with the new one
4. In your **trackstudio.properties** file insert following

```
trackstudio.reportBugsTo## Time constantstrackstudio.hoursInDay 8trackstudio.daysInWeek 5trackstudio.daysInMonth 22trackstudio.hoursInYear 2000trackstudio.monthsInYear 12## Maximum number of items per tree leveltrackstudio.maxTreeItems 100## DecimalFormat for custom-floattrackstudio.decimalFormatUdfFloat 7## Use compress htmltrackstudio.compressHTML yes
```

1. Replace **trackstudio.adapter.properties** file with same from new WAR
2. Save current DBMS connection settings from **trackstudio.hibernate.properties** in some place. (Current settings are located in the end of properties file
3. Replace your **trackstudio.hibernate.properties** file with new one from WAR.
4. Then insert DBMS connection strings in this properties file and comment others
5. Remove files containing in index directory, but not the directory itself. See **trackstudio.indexDir** property in your **trackstudio.properties **file to locate index directory.
6. Update your database with SQL update files.
7. Start TrackStudio. The system will update data in your database on the first run and saves scripts, triggers and e-mail templates in **etc/plugins **directory.
8. Stop TrackStudio.
9. You should fix you e-mail templates in **etc/plugins/email** directory manually or use some of our default templates.
10. Start TrackStudio again after that.

## Notes

- Standalone and WAR versions are totally compatible. The difference is only in supplied components.
- If you use HSQLDB, do not delete *tsdb.lck, tsdb.log, tsdb.properties and tsdb.script.* files
- Directories with indices and attached files can be saved anywhere, but they just need to be mentioned in respective entries in trackstudio.properties.

---

[Home](../../index.md) | [Up (Updating TrackStudio)](index.md)
