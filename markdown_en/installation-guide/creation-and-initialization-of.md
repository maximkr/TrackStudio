[Home](../index.md) | [Up (Installation Guide)](index.md)

---

# Creation and initialization of database in PostgreSQL

1. Run postmaster

```
> postmaster -D ../data/ -i -h host.mycompany.com
```

1. Create the database:

```
> createdb -E UNICODE -U postgres trackstudio
```

1. Configure the properties of DBMS connection:

list and edit the properties of JDBC connection

- **TrackStudio WAR:** Edit the connection properties in the file **trackstudio.hibernate.properties**:

```
hibernate.dialect org.hibernate.dialect.PostgreSQLDialecthibernate.connection.url jdbc:postgresql://127.0.0.1:5432/trackstudiohibernate.connection.driver_class org.postgresql.Driverhibernate.connection.username postgreshibernate.connection.password postgres
```

1. Initialize the database:

Press the button **Create DB** on the tab **Database**

- **TrackStudio WAR**: Run the script **sql\install\ru\trackstudio-pgsql.sql**

```
> psql --user=postgres -d trackstudio -f trackstudio-pgsql.sql
```

## Note

- For backing up the database:

```
> pg_dump -U postgres -Fc -Z9 trackstudio > trackstudio.dmp
```

- For restoring the database:

```
> createdb -E UNICODE -U postgres trackstudio   > pg_restore -U postgres --disable-triggers -S postgres -d trackstudio trackstudio.dmp

```

---

[Home](../index.md) | [Up (Installation Guide)](index.md)
