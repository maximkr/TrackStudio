# Как настроить уведомления через Jabber/XMPP

В TrackStudio есть возможность посылать уведомления о событиях по протоколу XMPP вместе с или вместо уведомлений по электронной почте.

В файле **trackstudio.mail.properties** нужно настроить параметры соединения с jabber-сервером:

```
xmpp.server.addr=jabber.org
xmpp.server.name=jabber.org
xmpp.server.port=5222
xmpp.login=trackstudio
xmpp.password=masterpassword
xmpp.sendToSender=true
```

Параметр **xmpp.sendToSender** (true/false) включает и выключает отправку сообщений по XMPP.

Пользователи версии **Standalone** могут настраивать параметры соединения с Jabber-сервером на вкладке jabber-уведомления в Server Manager.

![](../images/jabber_settings.PNG)

Далее для учетных записей пользователей в TrackStudio нужно создать дополнительное поле "jabber" строкового типа и в нем вводить jabber id (например, [trackstudio@jabber.org](mailto:trackstudio@jabber.org)).

В настройках оповещений нужно выбрать подходящий вариант оповещений.

![](../images/jabber_not.PNG)

Для уведомлений по XMPP действуют те же самые шаблоны и правила, что и для e-mail. Также для jabber-уведомлений можно использовать свои шаблоны. Для этого название шаблона нужно указать в файле trackstudio.mail.default.properties

```
## This template is used for a jabber sender
jabber.template=default_plain.ftl
```
