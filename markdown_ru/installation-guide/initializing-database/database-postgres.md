[Домой](../../index.md) | [Наверх (Создание и настройка базы данных)](index.md)

---

# Создание и инициализация базы данных под PostgreSQL

1. Запустите postmaster.

```
> postmaster -D ../data/ -i -h localhost
```

1. Создайте базу данных:

```
> createdb -E UNICODE -h localhost -U postgres -W trackstudio
```

1. Настройте свойства соединения с СУБД:

- **TrackStudio SA:** На вкладке **База данных** выберите из списка СУБД PostgreSQL и отредактируйте свойства соединения JDBC

![](../../images/postgres_init_db.png)

- **TrackStudio WAR:** Отредактируйте свойства соединения в файле **trackstudio.hibernate.properties**:

```
hibernate.dialect org.hibernate.dialect.PostgreSQLDialect
hibernate.connection.url jdbc:postgresql://127.0.0.1:5432/trackstudio
hibernate.connection.driver_class org.postgresql.Driver
hibernate.connection.username postgres
hibernate.connection.password postgres
```

1. Инициализируйте базу данных:

- **TrackStudio SA:** На вкладке **База данных** нажмите кнопку **Создать БД**

![](../../images/postgres_db_create.png)

- **TrackStudio WAR**: Выполните скрипт **sql\install\ru\trackstudio-pgsql.sql**

```
> psql -U postgres -W -h localhost -d trackstudio -f trackstudio-pgsql.sql
```

##     Примечание

- Чтобы сделать резервную копию базы данных:

```
> pg_dump -U postgres -Fc -Z9 trackstudio > trackstudio.dmp
```

- Чтобы восстановить базу данных:

```
> createdb -E UNICODE -U postgres trackstudio
> pg_restore -U postgres --disable-triggers -S postgres -d trackstudio trackstudio.dmp
```

---

[Домой](../../index.md) | [Наверх (Создание и настройка базы данных)](index.md)
