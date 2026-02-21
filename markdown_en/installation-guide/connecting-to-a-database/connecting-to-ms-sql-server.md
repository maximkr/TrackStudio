[Home](../../index.md) | [Up (Connecting to a Database)](index.md)

---

# Connecting to MS SQL Server

1. Start **Enterprise Manager**.
2. Create the database.
3. Configure the database connection properties:

**TrackStudio Standalone**:

- Open the tab **Database**.
- Select **MS SQL Server**** **from the drop down list.
- Indicate the connection address in the form **jdbc:jtds:sqlserver://<host>:<port>/<database>**
- Enter the login id in MS SQL Server and the password.

**TrackStudio WAR**: Edit the corresponding parameters in the file trackstudio.hibernate.properties

```
hibernate.connection.driver_class=net.sourceforge.jtds.jdbc.Driverhibernate.connection.password=hibernate.connection.url=jdbc\:jtds\:sqlserver\://127.0.0.1\:1433/trackstudiohibernate.connection.username=sahibernate.dialect=org.hibernate.dialect.SQLServerDialect
```

1. Initialize the database:

- **TrackStudio SA:** Press the button **Create DB**** **on the tab **Database**
- **TrackStudio WAR**: Run the Script **sql\install\ru\trackstudio-mssql.sql**

---

[Home](../../index.md) | [Up (Connecting to a Database)](index.md)
