[Home](../../index.md) | [Up (Connecting to a Database)](index.md)

---

# Connecting to PostgreSQL

1. Run postmaster.

```
> postmaster -D ../data/ -i -h host.mycompany.com
```

1. Create the database:

```
> createdb -E UNICODE -U postgres trackstudio
```

1. Configure the properties of DBMS connection:

```
hibernate.dialect org.hibernate.dialect.PostgreSQLDialecthibernate.connection.url jdbc:postgresql://127.0.0.1:5432/trackstudiohibernate.connection.driver_class org.postgresql.Driverhibernate.connection.username postgreshibernate.connection.password postgres
```

1. Initialize the database:

```
> psql --user=postgres -d trackstudio -f trackstudio-pgsql.sql
```

## Note

- For creating the backup of database:

```
> pg_dump -U postgres -Fc -Z9 trackstudio > trackstudio.dmp
```

- For restoring the database:

```
> createdb -E UNICODE -U postgres trackstudio> pg_restore -U postgres --disable-triggers -S postgres -d trackstudio trackstudio.dmp

```

---

[Home](../../index.md) | [Up (Connecting to a Database)](index.md)
