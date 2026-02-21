[Домой](../../index.md) | [Наверх (Установка под Linux)](index.md)

---

# Установка TrackStudio WAR

```
Внимание! Начиная с версии TrackStudio 4.0.10 система поставляется с неинициализированной базой данных. Как ее инициализировать, описано ниже.
```

Чтобы установить и настроить TrackStudio WAR:

1. Скачайте дистрибутив TrackStudio WAR
2. Разверните архив в новую папку.

В этой папке расположены:

- Папка **javadoc** с документацией по TrackStudio API
- Папка **etc** с конфигурационными файлами. В дальнейшем эта папка будет использоваться в качестве корневой для TrackStudio. В этой папке также содержится папка **plugins** со скриптами, триггерами и почтовыми шаблонами.
- Папка **sql** со скриптами инициализации баз данных
- Собственно файл **TrackStudio.war**

1. Скопируйте файл **TrackStudio.war** в папку, где расположены веб-приложения Tomcat. Например /var/lib/tomcat6/webapps/
2. [Создайте новую базу данных](../initializing-database/index.md). Укажите параметры соединения с базой в файле **trackstudio.hibernate.properties**.
3. Укажите переменную **TS_CONFIG** в файле **setenv.sh** в директории **bin** Tomcat6. Эта переменная должна указывать на директорию**etc** вашего экземпляра TrackStudio:

```
TS_CONFIG="/home/winzard/TS_WAR/etc"
export TS_CONFIG
```

1. Определите место, где будут храниться индексы и загруженные в TrackStudio файлы. Например, это могут быть директории **index** и**upload**, соответственно. Укажите эти директории в файле trackstudio.properties:

```
trackstudio.uploadDir /home/winzard/TS_WAR/upload
trackstudio.indexDir /home/winzard/TS_WAR/index
```

1. **Важно**, чтобы тот пользователь, от которого запускается Tomcat имел право на запись в эти директории. Это можно сделать, сменив их владельца. Выполните в терминале следующие команды:

```
sudo chown -R tomcat6:tomcat6 /home/winzard/TS_WAR/upload
sudo chown -R tomcat6:tomcat6 /home/winzard/TS_WAR/index
```

1. В том же файле trackstudio.properties укажите URL, по которому будет открываться TrackStudio (в соответствии с настройками Tomcat)

```
trackstudio.siteURL [http://localhost:8080/TrackStudio](http://localhost:8080/TrackStudio)
```

1. В файле server.xml, который находится по адресу tomcat/conf/server.xml необходимо добавить параметр URIEncoding="UTF-8"

```
<Connector port="8080" protocol="HTTP/1.1"
connectionTimeout="20000"
redirectPort="8443"
URIEncoding="UTF-8"/>
```

1. Для правильной генерации отчетов в PDF установите шрифты из папки **etc/fonts** архива в одну из этих папок

```
/usr/X/lib/X11/fonts/TrueType
/usr/share/fonts/default/TrueType
/usr/share/fonts/truetype
```

1. Запустите сервер приложений Tomcat командой

```
sudo service tomcat6 start
```

либо

```
sudo service tomcat6 restart
```

либо, если вы запускаете Tomcat не в качестве службы, выполните файл **startup.sh** из директории **bin** Tomcat 6

1. После запуска Tomcat в браузере по адресу [http://localhost:8080/TrackStudio/](http://localhost:8080/TrackStudio/) должна открыться страница, похожая на эту:

![](../../images/login.png)

**Используйте следующие учетные записи:**

| **Пользователь** | **Группа** | **Логин** | **Пароль** |
| --- | --- | --- | --- |
| Администратор | Администратор | root | root |
| Сергей Менеджеров | Менеджеры | manager | 123 |
| Иван Аналитиков | Аналитики | analyst | 123 |
| Дмитрий Писателев | Технические писатели | writer | 123 |
| Степан Разработчиков | Разработчики | developer | 123 |
| Максим Тестеров | Тестеры | tester | 123 |

## Примечания

- Вы не сможете запустить несколько экземпляров TrackStudio под одним сервером приложений
- Для работы с TrackStudio на вашем сервере должны быть установлены библиотеки X11, т.к. они требуются Java для работы с графическими файлами. При этом для установки запуска TrackStudio графического окружения не нужно, достаточно доступа к серверу по SSH.

---

[Домой](../../index.md) | [Наверх (Установка под Linux)](index.md)
