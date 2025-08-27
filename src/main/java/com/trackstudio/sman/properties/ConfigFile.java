package com.trackstudio.sman.properties;

public class ConfigFile {

    private ConfigFile() {
        /*empty*/
    }

    private static String getPropertiesPath() {
        String result;
        String trackstudioHome = System.getProperty("trackstudio.Home");
        if (trackstudioHome != null)
            result = "file:" + trackstudioHome + "/";
        else {
            String smanHome = System.getProperty("sman.Home");
            result = smanHome != null ? "file:" + smanHome + "/etc/trackstudio/" : "file:./etc/trackstudio/";
        }
        return result;
    }

    public final static String PROPERTIES_PATH = getPropertiesPath();

    public final static String PROPERTY_URI = PROPERTIES_PATH + "trackstudio.default.properties";
    public final static String PROPERTY_URI_USER = PROPERTIES_PATH + "trackstudio.properties";

    public final static String MAIL_SERVICE_URI = PROPERTIES_PATH + "trackstudio.mail.default.properties";
    public final static String MAIL_SERVICE_URI_USER = PROPERTIES_PATH + "trackstudio.mail.properties";

    public final static String HIBERNATE_URI = PROPERTIES_PATH + "trackstudio.hibernate.default.properties";
    public final static String HIBERNATE_URI_USER = PROPERTIES_PATH + "trackstudio.hibernate.properties";

    public final static String SECURITY_URI = PROPERTIES_PATH + "trackstudio.security.default.properties";
    public final static String SECURITY_URI_USER = PROPERTIES_PATH + "trackstudio.security.properties";

    public final static String LICENSE_URI = PROPERTIES_PATH + "trackstudio.license.properties";

    public final static String SMAN_URI = PROPERTIES_PATH + "sman.default.properties";
    public final static String SMAN_URI_USER = PROPERTIES_PATH + "sman.properties";

    public static final String SMAN_LOCALE = "sman.defaultLocale";//sman.defaultLocale
    public static final String TRACKSTUDIO_ENCODING_PROPERTY = "trackstudio.encoding";
    public static final String TRACKSTUDIO_LOCALE_PROPERTY = "trackstudio.defaultLocale";
    public static final String LDAP_HOST_PROPERTY = "ldap.host";
    public static final String COMMENT_PREFIX = "#";
    public static final String HIBERNATE_CONNECTION_DRIVER_CLASS_PROPERTY = "hibernate.connection.driver_class";
    public static final String HIBERNATE_CONNECTION_PASSWORD_PROPERTY = "hibernate.connection.password";
    public static final String HIBERNATE_CONNECTION_URL_PROPERTY = "hibernate.connection.url";
    public static final String HIBERNATE_CONNECTION_USERNAME_PROPERTY = "hibernate.connection.username";
    public static final String HIBERNATE_DIALECT_PROPERTY = "hibernate.dialect";
    public static final String LDAP_BASEDN_PROPERTY = "ldap.baseDN";
    public static final String LDAP_USE_SSL_PROPERTY = "ldap.useSSL";
    public static final String LDAP_FILTER = "ldap.filter";
    public static final String LDAP_FILTER_FOR_IMPORT = "trackstudio.import.ldap.filter";
    public static final String LDAP_LOGIN_ATTR_TS_PROPERTY = "ldap.loginAttrTS";
    public static final String LDAP_PORT_PROPERTY = "ldap.port";
    public static final String LDAP_USERDN_PROPERTY = "ldap.userDN";
    public static final String LDAP_USERDN_PASSWORD_PROPERTY = "ldap.userDNpass";
    public static final String MAIL_FROM_PROPERTY = "mail.from";
    public static final String MAIL_SMTP_HOST_PROPERTY = "mail.smtp.host";
    public static final String MAIL_SMTP_PASSWORD_PROPERTY = "mail.smtp.password";
    public static final String MAIL_SMTP_PORT_PROPERTY = "mail.smtp.port";
    public static final String MAIL_SMTP_USER_PROPERTY = "mail.smtp.user";
    public static final String MAIL_SMTP_STARTTLS_PROPERTY = "mail.smtp.starttls.enable";
    public static final String MAIL_SMTP_SOCKETFACTORY_PROPERTY = "mail.smtp.socketFactory.class";
    public static final String MAIL_SMTP_SOCKETFACTORY_FALLBACK_PROPERTY = "mail.smtp.socketFactory.fallback";
    public static final String MAIL_STORE_FORWARD_PROPERTY = "mail.store.forward";
    public static final String MAIL_STORE_FORWARD_ADDRESS_PROPERTY = "mail.store.fwdaddress";
    public static final String MAIL_STORE_HOST_PROPERTY = "mail.store.host";
    public static final String MAIL_STORE_PASSWORD_PROPERTY = "mail.store.password";
    public static final String MAIL_SMTP_USE_APOP_AUTHORIZATION = "mail.pop3.apop.enable";
    public static final String MAIL_SMTP_INTERVAL_IMPORT_EMAIL = "trackstudio.mailimport.interval";
    public static final String MAIL_STORE_PORT_PROPERTY = "mail.store.port";
    public static final String MAIL_STORE_PROTOCOL_PROPERTY = "mail.store.protocol";
    public static final String MAIL_STORE_USER_PROPERTY = "mail.store.user";
    public static final String MAIL_TRANSPORT_PROTOCOL_PROPERTY = "mail.transport.protocol";
    public static final String TRACKSTUDIO_FORM_MAIL_NOTIFICATION_PROPERTY = "trackstudio.FormMailNotification";
    public static final String TRACKSTUDIO_FORM_MAIL_NOTIFICATION_PROPERTY_NEW = "trackstudio.emailSubmission";
    public static final String TRACKSTUDIO_LICENSE_PROPERTY = "trackstudio.License";
    public static final String TRACKSTUDIO_INDEXDIR_PROPERTY = "trackstudio.indexDir";
    public static final String TRACKSTUDIO_LOGIN_AS_ANOTHER_USER_PROPERTY = "trackstudio.loginAsAnotherUser";
    public static final String TRACKSTUDIO_GZIP_HTTP = "trackstudio.gzipHTTP";
    public static final String TRACKSTUDIO_SEND_MAIL_PROPERTY_NEW = "trackstudio.emailNotification";
    public static final String TRACKSTUDIO_SITE_URL_PROPERTY = "trackstudio.siteURL";
    public static final String TRACKSTUDIO_UPLOAD_DIR_PROPERTY = "trackstudio.uploadDir";
    public static final String TRACKSTUDIO_USE_LDAP_PROPERTY = "trackstudio.useLDAP";
    public static final String TRACKSTUDIO_REPORT_BUGS_TO = "trackstudio.reportBugsTo";

    public static final String YES = "yes";
    public static final String NO = "no";
    public static final String TRUE = "true";
    public static final String FALSE = "false";

    public static final String LDAP_IMPORT_SSL_PROPERTY = "trackstudio.import.ldap.ssl";
    public static final String LDAP_IMPORT_BASEDN_PROPERTY = "trackstudio.import.ldap.baseDN";
    public static final String LDAP_IMPORT_USERDN_PROPERTY = "trackstudio.import.ldap.userDN";
    public static final String LDAP_IMPORT_USERDN_PASSWORD_PROPERTY = "trackstudio.import.ldap.userDNpass";
    public static final String LDAP_IMPORT_PORT_PROPERTY = "trackstudio.import.ldap.port";
    public static final String LDAP_IMPORT_LOGIN_ATTR_TS_PROPERTY = "trackstudio.import.ldap.loginAttrTS";
    public static final String LDAP_IMPORT_LOGIN_ATTR_LDAP_PROPERTY = "trackstudio.import.ldap.loginAttrLDAP";

    public static final String LDAP_IMPORT_USER_LOCALE_PROPERTY = "trackstudio.import.ldap.locale";
    public static final String LDAP_IMPORT_TIMEZONE_PROPERTY = "trackstudio.import.ldap.timezone";
    public static final String TRACKSTUDIO_HOST_IP = "trackstudio.host.ip";
    public static final String TRACKSTUDIO_HOST = "jetty.host";

    public static final String TRACKSTUDIO_MINIMUM_PASSWORD_LENGTH = "trackstudio.security.password.min";
    public static final String TRACKSTUDIO_MAXIMUM_PASSWORD_AGE = "trackstudio.security.password.maxage";
    public static final String TRACKSTUDIO_PASSWORD_COMPLEX = "trackstudio.security.password.complex";
    public static final String TRACKSTUDIO_PASSWORDS_HISTORY = "trackstudio.security.password.history";
    public static final String TRACKSTUDIO_CHANGE_PASSWORD_FIRST = "trackstudio.security.password.changefirst";
    public static final String TRACKSTUDIO_CASE_LOGIN = "trackstudio.security.password.case";
    public static final String TRACKSTUDIO_USE_X509 = "trackstudio.useX509.authorization";
}
