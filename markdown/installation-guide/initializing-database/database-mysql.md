[Домой](../../index.md) | [Наверх (Создание и настройка базы данных)](index.md)

---

# Создание и инициализация базы данных под MySQL

1. Запустите терминальный клиент для MySQL.

```
shell> mysql -u root
```

1. Создайте базу данных:

```
mysql> create database trackstudio character set utf8;
mysql> commit;
mysql> use trackstudio;
```

1. Настройте свойства соединения с СУБД:

- **TrackStudio SA:** На вкладке **База данных** выберите из списка СУБД MySQL и отредактируйте свойства соединения JDBC
- **TrackStudio WAR:** Отредактируйте свойства соединения в файле **trackstudio.hibernate.properties**:

```
hibernate.connection.driver_class=com.mysql.jdbc.Driver
hibernate.connection.password=
hibernate.connection.url=jdbc\:mysql\://localhost/trackstudio?useUnicode\=true&characterEncoding\=UTF-8&autoReconnect\=true
hibernate.connection.username=root
hibernate.dialect=org.hibernate.dialect.MySQLInnoDBDialect
```

1. Инициализируйте базу данных:

- **TrackStudio SA:** На вкладке **База данных** нажмите кнопку **Создать БД**
- **TrackStudio WAR**: Выполните скрипт **sql\install\ru\trackstudio-mysql.sql**

```
mysql -u <логин> -p <пароль> trackstudio --default-character-set=utf8 <./trackstudio-mysql.sql
```

---

[Домой](../../index.md) | [Наверх (Создание и настройка базы данных)](index.md)
