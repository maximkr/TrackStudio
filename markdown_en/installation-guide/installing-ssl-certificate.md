[Home](../index.md) | [Up (Installation Guide)](index.md)

---

# Installing SSL Certificate

For configuring SSL certificate for jetty:

1. Create keystore with the help of utility **keytool**, included in Sun JDK. For creating the certificate, you must indicate the *keystorePassword *and *keyPassword*

```
> jdk/bin/keytool -genkey -alias my-cert -keyalg RSA        -keystore .mykeystore
```

1. Generate the certificate request ‘CSR’ in the file **cert.csr**.

```
> jdk/bin/keytool -certreq -alias my-cert -file cert.csr        -keystore .mykeystore
```

1. Send the CSR file to the Certificate Authority (CA) like Verisign or XRamp and purchase the SSL certificate. The CA returns the file **cert.crt**. You may check your certificate by going to the link https://www.thawte.com/cgi/server/test.exe
2. Convert the file cert.crt from the format PEM to DER (**cert.der**). You may use **opnessl** for this purpose:

```
openssl x509 -in cert.crt -out cert.der –outform DER
```

1. Import the certificate to the store:

```
> jdk/bin/keytool -import -alias my-cert -file cert.der -keystore .mykeystore
```

1. Edit **jetty.xml**:

```
<Call name="addListener">    <Arg>      <New class="org.mortbay.http.SunJsseListener">        <Set name="Port">8443</Set>        <Set name="MinThreads">5</Set>        <Set name="MaxThreads">100</Set>        <Set name="MaxIdleTimeMs">30000</Set>        <Set name="LowResourcePersistTimeMs">2000</Set>        <Set name="Keystore"><SystemProperty name="jetty.home"             default="."/>/.mykeystore</Set>        <Set name="Password">keystorePassword</Set>        <Set name="KeyPassword">keyPassword</Set>      </New>    </Arg>  </Call>
```

1. Change the protocol and port in the property **siteURL** in the file**trackstudio.properties**.

```
# URL of your site. Host name and port should be correct.# We use this address in e-mail notification messages.trackstudio.siteURL https://localhost:8443/TrackStudio
```

1. Run jetty.
2. Open the URL **https://localhost:8443/TrackStudio** in your browser.

## For creating the self-signed certificate

1. Create your own certificate authority:

```
perl ./CA.pl -newca
```

or

```
./CA -newca
```

1. Generate the certificate request:

```
jdk/bin/keytool -certreq -alias my-cert -file cert.csr        -keystore .mykeystore
```

1. Generate the certificate:

```
openssl ca -config /usr/share/ssl/openssl.cnf           -out cert.crt -infiles cert.csr
```

1. Verify the certificate:

```
openssl verify -CAfile ./demoCA/cacert.pem cert.crt
```

1. Convert the certificate from the format PEM to DER:

```
openssl x509 -in cert.crt -out cert.der -outform DER
```

1. Import **cert.der** to the store.

## Note

Remember that the part of functionality (e.g. Excel reports) will not be accessible with the demo (signed by you) certificate in MS Internet Explorer.

---

[Home](../index.md) | [Up (Installation Guide)](index.md)
