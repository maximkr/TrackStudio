[Домой](../../index.md) | [Наверх (Создание и настройка базы данных)](index.md)

---

# Создание и инициализация базы данных под MS SQL Server

1. Запустите **Enterprise Manager**.
2. Создайте базу данных.
3. Настройте свойства соединения с базой:

**TrackStudio Standalone**:

- Откройте вкладку **База данных**.
- Выберите из выпадающего списка **MS SQL Server**
- Укажите адрес соединения в виде **jdbc:jtds:sqlserver://<host>:<port>/<database>**
- Введите название учетной записи в MS SQL Server и пароль

**TrackStudio WAR**: Отредактируйте соответствующие параметры в файле trackstudio.hibernate.properties

```
hibernate.connection.driver_class=net.sourceforge.jtds.jdbc.Driver
hibernate.connection.password=
hibernate.connection.url=jdbc\:jtds\:sqlserver\://127.0.0.1\:1433/trackstudio
hibernate.connection.username=sa
```

1. Инициализируйте базу данных:

- **TrackStudio SA:** На вкладке **База данных** нажмите кнопку **Создать БД**
- **TrackStudio WAR**: Выполните скрипт **sql\install\ru\trackstudio-mssql.sql**

---

[Домой](../../index.md) | [Наверх (Создание и настройка базы данных)](index.md)
