[Home](../index.md) | [Up (Installation Guide)](index.md)

---

# Configuring NTLM Authentication

For authentication of users through NTLM:

1. Login as Administrator in **Microsoft Windows**** **.
2. Run **Configure Your Server** (Server configuration). (**Control panel/Administrative Tools/Sever Configuration**).
3. If required, configure the service DHCP on the tab **Networking/DHCP**.
4. If required, configure the DNS on the tab **Networking/ DNS**.
5. Configure domain controller on the tab **Active Directory**.
6. Use **Windows Components**** **Wizard for installing WINS.
7. Include NTLM in the file **trackstudio.security.properties**

```
trackstudio.useNTLM yes
```

1. Indicate the domain name and WINS Address in **trackstudio.security.properties**

```
jcifs.smb.client.domain=WORKGROUP jcifs.netbios.wins=192.168.100.1
```

1. Press the button **Check Connection**

```
How it works: If trackstudio.useNTLM is set to yes, TrackStudio will use NTLM for user authentication. This authentication scheme allows saving the data about user authentication using WINS service, and therefore relieves the users to enter the login and password every time they logon TrackStudio from the same workplace.
```

## Note

- NTLM uses WINS and DNS services, therefore your network must have host, configured as Windows Domain Controller. Domain controller can be set only in server versions of Windows OS.
- If you are using NTLM authentication, you need to create the login account. Moreover, login must be the same as the login in Windows.

---

[Home](../index.md) | [Up (Installation Guide)](index.md)
