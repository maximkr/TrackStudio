[Home](../index.md) | [Up (Administrator's Guide)](index.md)

---

# How to change TrackStudio URL

By default, TrackStudio opens at the address [http://localhost:8888/TrackStudio](http://localhost:8888/TrackStudio).

What needs to be done so as to “transfer” TrackStudio to [http://trackstudio.mysite.com](http://trackstudio.mysite.com/)?

**Jetty**

In the file **webapps/TrackStudio.xml **, change the following

```
<Set name="contextPath">/</Set>  <Set name="war"><SystemProperty name="jetty.home" default="."/>/webapps/TrackStudio</Set>
```

Thereafter, in the file etc/jetty.xml, write the comment

```
<!--    <Call name="addLifeCycle">      <Arg>        <New class="org.mortbay.jetty.deployer.WebAppDeployer">          <Set name="contexts"><Ref id="Contexts"/></Set>          <Set name="webAppDir"><SystemProperty name="jetty.home" default="."/>/webapps</Set>          <Set name="parentLoaderPriority">false</Set>        <Set name="extract">true</Set>         <Set name="allowDuplicates">false</Set>          <Set name="defaultsDescriptor"><SystemProperty name="jetty.home" default="."/>/etc/webdefault.xml</Set>        </New>      </Arg>    </Call>    -->
```

In the same file, you need to change the jetty port:

<Call name="addConnector"> <Arg> <New class="org.mortbay.jetty.bio.SocketConnector"> <Set name="host"> <SystemProperty default="0.0.0.0" name="jetty.host"/> </Set> <Set name="port"> <SystemProperty default="80" name="jetty.port"/> </Set> <Set name="maxIdleTime">50000</Set> <Set name="lowResourceMaxIdleTime">1500</Set> <Set name="responseBufferSize">1024</Set> </New> </Arg> </Call> In the file etc/trackstudio/trackstudio.properties, edit (or complete) the following string:

```
trackstudio.siteURL [http://trackstudio.mysite.com](http://trackstudio.mysite.com/)
```

**Note! Back slash is not required at the end of the address!**

Thereafter run TrackStudio and open the address [http://trackstudio.mysite.com/](http://trackstudio.mysite.com/)

---

[Home](../index.md) | [Up (Administrator's Guide)](index.md)
