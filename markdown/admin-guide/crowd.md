[Домой](../index.md) | [Наверх (Руководство администратора)](index.md)

---

# Как интегрировать TrackStudio с Atlassian Crowd

Atlassian Crowd это веб-приложение для управления учетными записями пользователей в различных приложениях, реализующее технологию единого входа (Single sign on).

TrackStudio поддерживает авторизацию пользователей через Crowd. В этом документе мы полагаем, что у вас уже есть [установленный и настроенный сервер Crowd](http://confluence.atlassian.com/display/CROWD/Installing+Crowd+and+CrowdID), и в нем созданы нужные вам учетные записи.

## Настройка интеграции

- Запустите сервер Atlassian Crowd, если он еще не запущен.

- start_crowd.bat для Windows.
- start_crowd.sh для систем, основанных на Unix.

- Запустите TrackStudio Server Manager
- В разделе "Безопасность" TrackStudio Server Manager, на вкладке Crowd укажите адрес вашего сервера OpenID

![](../images/crowd_security.png)

Либо в файле trackstudio.security.properties, который находится в папке **etc** вашего экземпляра TrackStudio укажите

```
trackstudio.useCROWD=yes
trackstudio.crowd.openid.url [http://localhost:8095/openidserver/](http://localhost:8095/openidserver/)
```

- Запустите TrackStudio
- Откройте страницу входа в систему TrackStudio.
- Для того, чтобы интеграция заработала, в TrackStudio должны быть зарегистрированы пользователи с логинами, совпадающими с пользователями в Crowd. Пароль при этом используется от учетной записи в Crowd.
- При создании учетной записи пользователя в TrackStudio нужно также создавать его в Crowd вручную, если вы хотите, чтобы авторизация пользователя проходила через сервис Crowd. В противном случае авторизация будет осуществляться системой TrackStudio по указанному в учетной записи паролю.

---

[Домой](../index.md) | [Наверх (Руководство администратора)](index.md)
