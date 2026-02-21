[Home](../../index.md) | [Up (Connecting to a Database)](index.md)

---

# Using a console app dbtool for exporting and importing a database

In TrackStudio Enterprise 4.0.10 we added a console app **dbtool** export and import database. Now you can get an anonymized backup file without windows graphic environment.

It's very easy to use the application:

```
Attention! This app uses TrackStudio properties files for database connection parameters. Please, stop TrackStudio Server before use.
```

1. Go to TrackStudio directory
2. Type following in console

```
dbtool export -a --file=export-a-20100129.xml
```

Now you got an anonymized export file.

To get export file with real data:

```
dbtool export --file=export-a-20100129.xml
```

To import you data into new database:

```
dbtool import --file=export-a-20100129.xml
```

To initialize default database:

```
dbtool import --file=./etc/resources/sql/LAST/en_def.xml

```

---

[Home](../../index.md) | [Up (Connecting to a Database)](index.md)
