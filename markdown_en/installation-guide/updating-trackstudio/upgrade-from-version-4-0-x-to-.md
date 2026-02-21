[Home](../../index.md) | [Up (Updating TrackStudio)](index.md)

---

# Upgrade from version 4.0.x to 5.0.x

## If you possess the version TrackStudio WAR:

1. Download the latest version of TrackStudio WAR.
2. Replace trackstudio.war by the new version.
3. Restart TrackStudio.

## For upgrading TrackStudio Standalone, you may:

1. Download the latest version of TrackStudio SA.
2. Stop your copy of TrackStudio.
3. Install TrackStudio SA in another directory
4. Copy the entire content of directories etc, **except for trackstudio.adapters.properties, trackstudio.hibernate.default.properties, e-mail templates,** into the analogous directory in the new version.
5. If you are using **HSQLDB:**** **Copy all the files with base (tsdb.*) from the old version into the new one.
6. Copy all the files from the directory, where you have attached files from TrackStudio tasks (see the parameter **trackstudio.uploadDir** in the file **trackstudio.properties**) to the directory with the new version of TrackStudio, or change the parameter trackstudio.uploadDir, so that it pointed to the old directory.

.

1. Delete the contents of the directory with indices (see the parameter **trackstudio.indexDir**)
2. Start TrackStudio

## Notes

- Upgrade of minor versions does not require any changes to be made in the database.
- Standalone and WAR versions are completely compatible with each other and differ only in their supplied components.
- If you are using default DBMS (HSQLDB), then the files with a database are named as *tsdb.lck, tsdb.log, tsdb.properties** *and *tsdb.script.** *Do not delete them.
- Directories with indices and attached files can be stored anywhere, but they must be registered in the corresponding items in **trackstudio.properties**.

---

[Home](../../index.md) | [Up (Updating TrackStudio)](index.md)
