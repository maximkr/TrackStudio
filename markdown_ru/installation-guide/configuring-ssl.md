[Домой](../index.md) | [Наверх (Руководство по установке)](index.md)

---

# Установка сертификата SSL

Чтобы установить сертификат SSL для jetty:

1. Создайте хранилище ключей с помощью утилиты **keytool**, поставляемой вместе с Sun JDK. Для создания сертификата вы должны указать *keystorePassword* и *keyPassword.*

```
> jdk/bin/keytool -genkey -alias my-cert -keyalg RSA -keystore .mykeystore
```

1. Создайте запрос сертификата "CSR" в файл **cert.csr**.

```
> jdk/bin/keytool -certreq -alias my-cert -file cert.csr -keystore .mykeystore
```

1. Вышлите файл CSR в центр авторизации, например Verisign или XRamp, и купите сертификат SSL. Центр авторизации вернет файл **cert.crt**. Вы можете проверить ваш сертификат, перейдя по ссылке https://www.thawte.com/cgi/server/test.exe+
2. Сконвертируйте cert.crt из формата PEM в DER (**cert.der**). Можно использовать **openssl**, чтобы сделать это, наберите:

```
openssl x509 -in cert.crt -out cert.der –outform DER
```

1. Импортируйте сертификат в хранилище:

```
> jdk/bin/keytool -import -alias my-cert -file cert.der -keystore .mykeystore
```

1. Отредактируйте **jetty-ssl.xml**:

```
<Configure class="org.mortbay.jetty.Server" id="Server">
<Call name="addConnector">
<Arg>
<New class="org.mortbay.jetty.security.SslSocketConnector">
<Set name="Port">
<SystemProperty default="8443" name="jetty.ssl.port"/>
</Set>
<Set name="maxIdleTime">30000</Set>
<Set name="handshakeTimeout">2000</Set>
<Set name="keystore">
<SystemProperty default="." name="jetty.home"/>/jetty/etc/ssl/keystore</Set>
<Set name="password">changeit</Set>
<Set name="keyPassword">changeit</Set>
<Set name="truststore">
<SystemProperty default="." name="jetty.home"/>/jetty/etc/ssl/keystore</Set>
<Set name="trustPassword">changeit</Set>
<Set name="handshakeTimeout">2000</Set>
<Set name="ThreadPool">
<New class="org.mortbay.thread.BoundedThreadPool">
<Set name="minThreads">10</Set>
<Set name="maxThreads">250</Set>
</New>
</Set>
</New>
</Arg>
</Call>
</Configure>
```

1. Смените протокол и порт в свойстве **siteURL** в файле **trackstudio.properties**.

```
# URL of your site. Host name and port should be correct.
# We use this address in e-mail notification messages.
trackstudio.siteURL https://localhost:8443/TrackStudio
```

1. Запустите jetty.
2. Откройте URL **https://localhost:8443/TrackStudio** в вашем браузере

## Чтобы создать сертификат, подписанный самостоятельно

```
Инструкция ниже предназначена для операционных систем семейства GNU/Linux. Для Windows воспользуйтесь [инструкцией компании Microsoft](http://msdn.microsoft.com/ru-ru/library/bfsktky3.aspx)
```

1. Создайте свой центр сертификации:

```
perl ./CA.pl -newca
```

или

```
./CA -newca
```

1. Создайте запрос сертификата:

```
jdk/bin/keytool -certreq -alias my-cert -file cert.csr -keystore .mykeystore
```

1. Создайте сертификат:

```
openssl ca -config /usr/share/ssl/openssl.cnf
-out cert.crt -infiles cert.csr
```

1. Подтвердите сертификат:

```
openssl verify -CAfile ./demoCA/cacert.pem cert.crt
```

1. Конвертируйте сертификат из формата PEM в DER:

```
openssl x509 -in cert.crt -out cert.der -outform DER
```

1. Импортируйте **cert.der** в хранилище

## Примечание

Имейте в виду, что часть функциональности (отчеты Excel, например) не будет доступна с демонстрационным (подписанным вами самими) сертификатом под MS Internet Explorer.

---

[Домой](../index.md) | [Наверх (Руководство по установке)](index.md)
