[Home](../index.md) | [Up (Administrator's Guide)](index.md)

---

# Security Settings

Generally we ship the examples of customized configurations, in which the root user has the login — **root** and password — **root**, and for all other users the password will be the same as the name of user account. **For test copies, do not open access for external IPs, until you have changed all the passwords**. For restricting the possibility of accessing the system from external IPs, edit the property jetty.host in the file jetty/etc/jetty.xml:

```
<Call name="addConnector">        <Arg>            <New class="org.mortbay.jetty.bio.SocketConnector">                <Set name="host">                    <SystemProperty default="127.0.0.1" name="jetty.host"/>                </Set>                <Set name="port">                    <SystemProperty default="8888" name="jetty.port"/>                </Set>                <Set name="maxIdleTime">50000</Set>                <Set name="lowResourceMaxIdleTime">1500</Set>                <Set name="responseBufferSize">1024</Set>            </New>        </Arg>    </Call>
```

Or, simply you can go for more preferred method by selecting Network Interface as **127.0.0.1**, or IP-address of the server in LAN on the tab “**General” in the server manager (SMAN) program of TrackStudio.**

![](../images/security_netinterface_en.png)

## Requirements for passwords

In TrackStudio you can specify the requirements for passwords with the purpose of ensuring better security of users and your package of TrackStudio.

If you are using TrackStudio Standalone, run the server manager program of TrackStudio (SMAN). Click on the tab “Security”. Set the Security Policy. If you are using TrackStudio WAR, edit the specified properties in the file trackstudio.properties (if this file does not exist, create it).

**trackstudio.security.password.min 6**

You can configure the minimum required length of the password (e.g., six characters).

**trackstudio.security.password.complex yes**

Similarly you may put the restrictions that the passwords fulfilled particular criteria of complexity. Like you may ask that the passwords must include different cases of letters, digits, and special characters, and most not be the words from dictionary.

**trackstudio.security.password.maxage 30**

You can also indicate the maximum age (validity) of a password in days. Upon the expiry of this period, the system will ask the user to change the password.

**trackstudio.security.password.history 4**

You can enable the registering of passwords and specify how many passwords the system must ‘remember’. User will not be able to change his password to one of the older ones. At the same time the passwords will be stored in cached form.

**trackstudio.security.password.changefirst yes**

You can require from user to change his password upon his first login. This is useful, e.g. in automatic registration through email. In such cases, the password is sent to the user in an open form in the message and its security can be compromised.

**trackstudio.security.password.case no**

You can enable the usage of case-insensitive names of login accounts, in that case username, Username, USERNAME and UserName will be considered as different login names. In general situations, system considers all these as one and the same username and doesn’t allow registration options in different cases.

**trackstudio.loginAsAnotherUser yes**

If this parameter is enabled (by default it is **enabled**** **), the higher user can log into the system using the name of user account of lower user and his **own**** **password. root with his password can login, while using the user account of any user. This is very useful during customization stage of the system.

## Changing and recovery of password.

User can change his password, if he is allowed to do so in the settings of his role:

![](../images/security_change_pass_permission_en.png)

User can also change the password of subordinate users, if he is allowed to do so in the settings.

If SMTP-server is enabled and configured in the system, the user can reset his password, by pressing the respective button on the login page of the system. The user will receive the message with new password through email, mentioned in the settings of that user in the system.

![](../images/login_screen_en.png)

![](../images/reset_password_en.png)

## Sessions.

Key security element in TrackStudio is the session identifier. It must never be passed on to the user directly. Session identifier is written in cookie, corresponding to particular site and particular package of TrackStudio. If intruders get hold of session identifier, they can imitate the cookie and enter the system by your name. We filter the data, entered by the user, through email as well as through custom fields. The potential security threat is basically from scripts, triggers, email and web-templates. Never write session identifier in them in whatever form it is.

Sessions are stored inside the active package of TrackStudio and are not written in the database. After TrackStudio is restarted, all the old session identifiers become invalid, and the user must once again enter the password in the login page of the system.

---

[Home](../index.md) | [Up (Administrator's Guide)](index.md)
