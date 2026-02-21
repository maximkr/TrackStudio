[Home](../index.md) | [Up (Installation Guide)](index.md)

---

# X.509 Authorization Settings

TrackStudio supports the login with the usage of client certificates ([X.509](http://en.wikipedia.org/wiki/X.509)).

Create your own certification center, server and client keys, as described in [Virgo's Naive Stories](http://virgo47.wordpress.com/2010/08/23/tomcat-web-application-with-ssl-client-certificates/)

You must get the files ca.crt, ca.key and client.p12

## Server settings

Open the configuration file jetty/etc/jetty-ssl.xml and de-comment the below given lines, if they are commented.

```
<Call name="addConnector">    <Arg>      <New class="org.mortbay.jetty.security.SslSocketConnector">        <Set name="Port">8443</Set>        <Set name="maxIdleTime">30000</Set>        <Set name="handshakeTimeout">2000</Set>        <Set name="keystore">ssl/keystore</Set>        <Set name="password">changeit</Set>        <Set name="keyPassword">chagneit</Set>        <Set name="truststore">ssl/keystore</Set>        <Set name="trustPassword">chagneit</Set>        <Set name="handshakeTimeout">2000</Set>     <Set name="wantClientAuth">true</Set>      </New>    </Arg></Call>
```

1. For Jetty server, it is required to create a **common **repository of certificates.

```
openssl pkcs12 -inkey ca.key -in ca.crt -export -out jetty.pkcs12
```

1. Copy the file jetty.pkcs12 to the folder where your copy of TrackStudio is installed.
2. Go to this folder.
3. Execute the following command in the terminal

```
java -classpath jetty/lib/jetty-6.1.26.jar:jetty/lib/jetty-util-6.1.26.jar org.mortbay.jetty.security.PKCS12Import jetty.pkcs12 keystore
```

## Browser Settings.

1. Import ca.crt to the browser as Authority

![](../images/import_ca.png)

1. Import client.p12 to the browser as user certificate

![](../images/import_client_p12.png)

1. Login to the system by pressing the button **X.509**

![](../images/login_x509.png)

---

[Home](../index.md) | [Up (Installation Guide)](index.md)
