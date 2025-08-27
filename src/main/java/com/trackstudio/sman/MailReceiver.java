package com.trackstudio.sman;

public class MailReceiver {

    private String name;
    private String host;
    private String protocol;
    private String port;
    private String user;
    private String password;

    public MailReceiver(String name, String host, String protocol, String port, String user, String password) {
        this.name = name;
        this.host = host;
        this.protocol = protocol;
        this.port = port;
        this.user = user;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getHost() {
        return host;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getPort() {
        return port;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

}
