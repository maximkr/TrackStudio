[Домой](../index.md) | [Наверх (Руководство по установке)](index.md)

---

# Как сменить URL TrackStudio

По-умолчанию TrackStudio открывается по адресу [http://localhost:8888/TrackStudio](http://localhost:8888/TrackStudio).

Что нужно сделать для того, чтобы "перенести" TrackStudio на [http://trackstudio.mysite.com](http://trackstudio.mysite.com/)?

## Jetty

В файле **webapps/TrackStudio.xml** нужно поменять

```
<Set name="contextPath">/</Set>
<Set name="war">
<SystemProperty name="jetty.home" default="."/>/webapps/TrackStudio</Set>
```

Затем в файле etc/jetty.xml нужно закомментировать

```
<!--
<Call name="addLifeCycle">
<Arg>
<New class="org.mortbay.jetty.deployer.WebAppDeployer">
<Set name="contexts"><Ref id="Contexts"/></Set>
<Set name="webAppDir">
<SystemProperty name="jetty.home" default="."/>/webapps</Set>
<Set name="parentLoaderPriority">false</Set>
<Set name="extract">true</Set>
<Set name="allowDuplicates">false</Set>
<Set name="defaultsDescriptor">
<SystemProperty name="jetty.home" default="."/>/etc/webdefault.xml</Set>
</New>
</Arg>
</Call>
-->
```

В этом же файле нужно поменять порт jetty:

```
<Call name="addConnector">
<Arg>
<New class="org.mortbay.jetty.bio.SocketConnector">
<Set name="host">
<SystemProperty default="0.0.0.0" name="jetty.host"/>
</Set>
<Set name="port">
<SystemProperty default="80" name="jetty.port"/>
</Set>
<Set name="maxIdleTime">50000</Set>
<Set name="lowResourceMaxIdleTime">1500</Set>
<Set name="responseBufferSize">1024</Set>
</New>
</Arg>
</Call>
```

В файле etc/trackstudio/trackstudio.properties нужно отредактировать (или дописать) следующую строку:

```
trackstudio.siteURL [http://trackstudio.mysite.com](http://trackstudio.mysite.com/)
```

**Внимание! В конце адреса обратный слеш ставить не нужно!**

Затем запустите TrackStudio и откройте адрес [http://trackstudio.mysite.com/](http://trackstudio.mysite.com/)

---

[Домой](../index.md) | [Наверх (Руководство по установке)](index.md)
