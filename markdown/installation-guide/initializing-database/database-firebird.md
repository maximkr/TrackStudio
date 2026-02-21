[Домой](../../index.md) | [Наверх (Создание и настройка базы данных)](index.md)

---

# Создание и инициализация базы данных под Firebird

1. Запустите поставляемую с Firebird программу **isql**

1. Создайте базу данных:

```
> create database 'c:\trackstudio.gdb' user 'sysdba' password asterkey';
SQL> connect 'c:\trackstudio.gdb' user 'sysdba' password 'masterkey';
Commit current transaction (y/n)?y
Committing.
Database: 'c:\trackstudio.gdb', User: sysdba
```

1. Настройте свойства соединения с СУБД:

- **TrackStudio SA:** На вкладке **База данных** выберите из списка СУБД Firebird и отредактируйте свойства соединения JDBC
- **TrackStudio WAR:** Отредактируйте свойства соединения в файле **trackstudio.hibernate.properties**:

```
hibernate.connection.driver_class=org.firebirdsql.jdbc.FBDriver
hibernate.connection.password=masterkey
hibernate.connection.url=jdbc\:firebirdsql\://localhost/c\:/trackstudio.gdb
hibernate.connection.username=sysdba
hibernate.dialect=org.hibernate.dialect.FirebirdDialect
```

1. Инициализируйте базу данных:

- **TrackStudio SA:** На вкладке **База данных** нажмите кнопку **Создать БД**
- **TrackStudio WAR**: Выполните скрипт **sql\install\ru\trackstudio-firebird.sql**

```
SQL> in trackstudio-firebird.sql;
```

---

[Домой](../../index.md) | [Наверх (Создание и настройка базы данных)](index.md)
