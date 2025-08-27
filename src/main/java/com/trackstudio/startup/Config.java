package com.trackstudio.startup;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.SimpleTimeZone;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.mail.Session;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import com.trackstudio.sman.MailReceiver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;
import org.apache.xerces.parsers.DOMParser;
import org.concurrent.FJTaskRunnerGroup;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.exception.GranException;
import com.trackstudio.exception.UserException;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.kernel.manager.TSPropertyManager;
import com.trackstudio.model.Acl;
import com.trackstudio.model.Attachment;
import com.trackstudio.model.Bookmark;
import com.trackstudio.model.Category;
import com.trackstudio.model.Catrelation;
import com.trackstudio.model.Cprstatus;
import com.trackstudio.model.CurrentFilter;
import com.trackstudio.model.Filter;
import com.trackstudio.model.Fvalue;
import com.trackstudio.model.Longtext;
import com.trackstudio.model.MailImport;
import com.trackstudio.model.Message;
import com.trackstudio.model.Mprstatus;
import com.trackstudio.model.Mstatus;
import com.trackstudio.model.Notification;
import com.trackstudio.model.Priority;
import com.trackstudio.model.Property;
import com.trackstudio.model.Prstatus;
import com.trackstudio.model.Registration;
import com.trackstudio.model.Report;
import com.trackstudio.model.Resolution;
import com.trackstudio.model.Rolestatus;
import com.trackstudio.model.Status;
import com.trackstudio.model.Subscription;
import com.trackstudio.model.Task;
import com.trackstudio.model.Template;
import com.trackstudio.model.Transition;
import com.trackstudio.model.Trigger;
import com.trackstudio.model.Udf;
import com.trackstudio.model.Udflist;
import com.trackstudio.model.Udfsource;
import com.trackstudio.model.Udfval;
import com.trackstudio.model.Umstatus;
import com.trackstudio.model.Uprstatus;
import com.trackstudio.model.User;
import com.trackstudio.model.Usersource;
import com.trackstudio.model.Workflow;
import com.trackstudio.sman.tools.MailOutputStream;
import com.trackstudio.tools.Env;
import com.trackstudio.tools.Null;
import com.trackstudio.tools.Pair;
import com.trackstudio.tools.PasswordValidator;
import com.trackstudio.tools.formatter.DateFormatter;

import net.jcip.annotations.ThreadSafe;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Класс для работы с настройками системы
 */
@ThreadSafe
public class Config implements Serializable {
    public static final String TRACKSTUDIO_VERSION = "TrackStudio Enterprise/OS 6.0.0 Apache License 2.0";

    private static final Log log = LogFactory.getLog(Config.class);
    private static volatile Config instance;
    private static volatile SessionFactory sessions;
    public static final Properties properties = new Properties();
    private static volatile Configuration dataStore;
    private static volatile FJTaskRunnerGroup fjtrg;
    private static volatile DatabaseMetaData databaseMetadata;
    private final ServletContext servletContext;
    private volatile Pair<String> trackStudioHome;
    public final int limit;

    public DatabaseMetaData getDatabaseMetadata() {
        return databaseMetadata;
    }

    /**
     * Конструктор
     * <br/>
     * Инициализирует все встроенные переменные
     *
     * @param servletContext контекст сервлета
     * @throws GranException при необходимости
     */
    private Config(ServletContext servletContext) throws GranException {
        this.servletContext = servletContext;
        initTrackStudioHome();
        if (validateFile("trackstudio.mail.default.properties")) {
            loadConfigFile("trackstudio.mail.default.properties");
        }
        if (validateFile("trackstudio.mail.properties")) {
            loadConfigFile("trackstudio.mail.properties");
        }
        if (validateFile("trackstudio.default.properties")) {
            loadConfigFile("trackstudio.default.properties");
        }
        if (validateFile("trackstudio.properties")) {
            loadConfigFile("trackstudio.properties");
        }
        PropertyConfigurator.configure(loadConfigFile("trackstudio.log4j.properties"));
        loadConfigFile("trackstudio.log4j.properties");
        if (validateFile("trackstudio.default.properties")) {
            loadConfigFile("trackstudio.default.properties");
        }
        if (validateFile("trackstudio.properties")) {
            loadConfigFile("trackstudio.properties");
        }
        if (validateFile("trackstudio.mail.default.properties")) {
            loadConfigFile("trackstudio.mail.default.properties");
        }
        if (validateFile("trackstudio.mail.properties")) {
            loadConfigFile("trackstudio.mail.properties");
        }
        loadConfigFile("trackstudio.adapters.properties");
        loadHibernateProperties();
        if (validateFile("trackstudio.security.default.properties")) {
            loadConfigFile("trackstudio.security.default.properties");
        }
        if (validateFile("trackstudio.security.properties")) {
            loadConfigFile("trackstudio.security.properties");
        }
        PasswordValidator.setPassFilePath(this.servletContext.getRealPath("/WEB-INF"));
        limit = Integer.valueOf(Config.getProperty("trackstudio.user.list.limit", "-1"));
    }

    private void loadHibernateProperties() throws GranException {
        if (validateFile("trackstudio.hibernate.default.properties")) {
            loadConfigFile("trackstudio.hibernate.default.properties");
        }
        if (validateFile("trackstudio.hibernate.properties")) {
            loadConfigFile("trackstudio.hibernate.properties");
        }
        //(#29151) winzard  : Fix For http://host.trackstudio.com/TrackStudio/task/29151
    }

    /**
     * Загружает настройки смстемы
     *
     * @param servletContext контекст сервлета
     * @throws GranException при необзодимости
     */
    public static synchronized boolean loadConfig(ServletContext servletContext) throws GranException {
        if (instance == null) {
            instance = new Config(servletContext);
        }
        return true;
    }

    /**
     * Возвращает экземпляр текущего класса
     *
     * @return экземпляр Config
     */
    public static synchronized Config getInstance() {
        if (instance == null) {
            log.fatal("Config must be initialized before use");
            throw new RuntimeException("Config must be initialized before use");
        }
        return instance;
    }

    /**
     * Возвращает интервал импорта почты
     *
     * @return интервал
     */
    public Long getMailImportInterval() {
        String v = getProperty("trackstudio.mailimport.interval");
        if (v != null && v.length() > 0) {
            try {
                return Long.valueOf(v);
            } catch (NumberFormatException n) {
                log.error("Mail Import Interval Incorrect", n);
            }
        }
        return 1 * 60000L; //1 minutes default
    }

    /**
     * Возвращает почтовую сессию
     *
     * @return почтовая сессия
     */
    public Session getSession() {
        MailReceiver mailReceiver = getMailReceivers().get(0);
        return getSession(mailReceiver);
    }

    public Session getSession(MailReceiver mailReceiver) {
        Properties prop = new Properties();
        prop.putAll(getProperties());
        prop.setProperty("mail.store.user", mailReceiver.getUser());
        prop.setProperty("mail.store.password", mailReceiver.getPassword());
        prop.setProperty("mail.store.port", mailReceiver.getPort());
        prop.setProperty("mail.store.host", mailReceiver.getHost());
        prop.setProperty("mail.store.protocol", mailReceiver.getProtocol());
        Session sess = Session.getInstance(prop, null);
        PrintStream printStream = new PrintStream(new MailOutputStream(true));
        sess.setDebugOut(printStream);
        return sess;
    }

    /**
     * Возвращает кодировку
     *
     * @return возвращает кодировку
     */
    public static String getEncoding() {
        if (getProperty("trackstudio.encoding") != null && getProperty("trackstudio.encoding").length() != 0) {
            return getProperty("trackstudio.encoding");
        } else {
            return "Windows-1251";
        }
    }

    /**
     * Возвращает максимальный размер приложенного файла
     *
     * @return максимальный размер приложенного файла
     */
    public ArrayList<String> getAllowedLocales() {
        String localesString = getProperty("trackstudio.allowedLocales");
        if (localesString != null) {
            ArrayList<String> locales = new ArrayList<String>();
            StringTokenizer k = new StringTokenizer(localesString, ";,");
            while (k.hasMoreTokens()) {
                locales.add(k.nextToken());
            }
            return locales;
        }
        return null;
    }

    /**
     * Возвращает список доступных локалей
     *
     * @return список локалей
     */
    public ArrayList<String> getAllowedEncodings() {
        ArrayList<String> encodings = new ArrayList<String>();
        String encodingsString = getProperty("trackstudio.allowedEncodings");
        if (encodingsString != null) {
            StringTokenizer k = new StringTokenizer(encodingsString, ";,");
            while (k.hasMoreTokens()) {
                encodings.add(k.nextToken());
            }
        }
        ArrayList<String> avail = new ArrayList<String>(Charset.availableCharsets().keySet());
        if (!encodings.isEmpty()) {
            avail.retainAll(encodings);
        }
        return avail;
    }

    /**
     * Влзвращает список доступных кодировок
     *
     * @return список кодировок
     */
    public boolean isFormMailNotification() {
        return Config.isTurnItOn("trackstudio.emailSubmission");
    }

    /**
     * Возвращает необходимость рассылки подписок
     *
     * @return TRUE - надо, FALSE - нет
     */
    public boolean isSendMail() {
        return isTurnItOn("trackstudio.emailNotification");
    }


    public boolean isUseGZIP() {
        return isTurnItOn("trackstudio.gzipHTTP");
    }

    /**
     * Возвращает необходимость GZIP-сжатия
     *
     * @return TRUE - надо, FALSE - нет
     */
    public boolean isForwardUnprocessed() {
        return isTurnItOn("mail.store.forward");
    }

    /**
     * Возвращает необходимость пересылки обработанной почты
     *
     * @return TRUE - надо, FALSE - не надо
     */
    public String getForwardEmail() {
        return getProperty("mail.store.fwdaddress");
    }

    /**
     * Возвращает адрес почты, на который надо пересылать почту
     *
     * @return адрес почты
     */
    public String getUploadDir() {
        return getProperty("trackstudio.uploadDir");
    }

    public String getArchiveDir() {
        return getProperty("trackstudio.archiveDir", "archive");
    }

    /**
     * Возвращает директорию в которой хранятся приложенные файлы
     *
     * @return директория
     */
    public String getIndexDir() {
        return getProperty("trackstudio.indexDir");
    }

    /**
     * Возвращает дирекорию для хранения индексов
     *
     * @return директория индексов
     */
    public String getSiteURL() {
        return getProperty("trackstudio.siteURL");
    }

    /**
     * Возвращает ссылку на сайт
     *
     * @return ссылка на сайт
     */
    public String getDefaultLocale() {
        return getProperty("trackstudio.defaultLocale").equals("default") ? Locale.getDefault().getLanguage() : getProperty("trackstudio.defaultLocale");
    }

    /**
     * Возвращает локаль по умолчанию
     *
     * @return локаль по умолчанию
     */
    public Boolean getDefaultLocaleTrue() {
        return getProperty("trackstudio.defaultLocale").equals("default");
    }

    /**
     * Возвращает дефолтная локаль или нет
     *
     * @return TRUE - дефолтная, FALSE - нет
     */
    public String getDefaultTimezone() {
        log.debug("trackstudio.defaultTimezone : " + getProperty("trackstudio.defaultTimezone"));
        log.debug("trackstudio.getDefault : " + SimpleTimeZone.getDefault().getID());
        return getProperty("trackstudio.defaultTimezone") == null ? SimpleTimeZone.getDefault().getID() : getProperty("trackstudio.defaultTimezone");
    }

    /**
     * Возвращает таймзону по умолчанию
     *
     * @return таймзона по умолчанию
     */
    public int getStartupDelay() {
        String strDelay = getProperty("trackstudio.startupDelay");
        int delay = 0;
        try {
            delay = Integer.parseInt(strDelay);
        } catch (Exception ex) {
            log.error("Delay incorrect ",ex);
        }
        return delay;
    }

    /**
     * Возвращает задержку старта системы
     *
     * @return задержка
     */
    public String getLogoutURL(String context) {
        if (Null.isNotNull(getProperty("trackstudio.logoutURL"))) {
            return getProperty("trackstudio.logoutURL");
        } else {
            return context + "/";
        }
    }

    /**
     * Возвращает ссылку, по которой переходим после выхода пользователя из системы
     *
     * @return ссылка
     */
    public boolean isLDAP() {
        return isTurnItOn("trackstudio.useLDAP");
    }

    /**
     * Возвращает активность фичи логина под другим пользователем
     *
     * @return TRUE - активно, FALSE - нет
     */
    public boolean isLogonAsAnotherUser() {
        return isTurnItOn("trackstudio.loginAsAnotherUser");
    }

    private synchronized Configuration getConfiguration() throws GranException {
        try {
            if (dataStore == null) {
                dataStore = new Configuration();
                Properties hibernateProperties = new Properties();
                String[] props = {"trackstudio.hibernate.default.properties", "trackstudio.hibernate.properties"};
                for (String nameFile : props) {
                    if (validateFile(nameFile)) {
                        final String path;
                        if (trackStudioHome.isBoolValue()) {
                            path = trackStudioHome.getT() + File.separator + "trackstudio" + File.separator + nameFile;
                        } else {
                            path = trackStudioHome.getT() + File.separator + nameFile;
                        }
                        FileInputStream fis = new FileInputStream(path);
                        hibernateProperties.load(fis);
                        fis.close();
                    }
                }
                dataStore.setProperties(hibernateProperties);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new GranException(e);
        }
        return dataStore;
    }

    private void uploadEntity() {
        Class[] entityHiber = {Bookmark.class, User.class, Prstatus.class, Rolestatus.class, Subscription.class,
                Mprstatus.class, Udfsource.class, Filter.class, Fvalue.class, Udf.class, Udflist.class,
                Udfval.class, Acl.class, CurrentFilter.class, Task.class, Category.class, Workflow.class,
                Status.class, Mstatus.class, Transition.class, Resolution.class, Message.class, Report.class,
                Priority.class, Catrelation.class, Cprstatus.class, Notification.class, MailImport.class, Template.class,
                Longtext.class, Registration.class, Attachment.class, Property.class, Uprstatus.class, Trigger.class,
                Umstatus.class, Usersource.class};
        List<Class> entitys = new ArrayList<Class>(Arrays.asList(entityHiber));
        for (Class entity : entitys) {
            dataStore.addClass(entity);
        }
    }

    /**
     * Возвращает эзкемпляр SessionFactory
     *
     * @return экземпляр SessionFactory
     * @throws GranException      при необходимости
     * @throws HibernateException при необходимости
     */
    public synchronized SessionFactory getSessionFactory() throws GranException, HibernateException {
        if (sessions == null) {
            dataStore = getConfiguration();
            uploadEntity();
            sessions = dataStore.buildSessionFactory();
            fillDatabaseMetaInfo();
        }
        return sessions;
    }

    private void fillDatabaseMetaInfo() {
        try {
            Connection conn = sessions.
                    getSessionFactoryOptions().getServiceRegistry().
                    getService(ConnectionProvider.class).getConnection();
            databaseMetadata = conn.getMetaData();
            conn.close();
        } catch (SQLException e) {
            log.error("Failed to get database metainfo", e);
        }
    }

    /**
     * Возвращает экземпляр FJTaskRunnerGroup
     *
     * @return экземпляр FJTaskRunnerGroup
     */
    public synchronized FJTaskRunnerGroup getFJTaskRunnerGroup() {
        if (fjtrg == null) {
            fjtrg = new FJTaskRunnerGroup(Runtime.getRuntime().availableProcessors());
        }
        return fjtrg;
    }

    private void checkDir(File dir, String errorMessage) throws GranException {
        try {
            if (!dir.exists()) {
                log.info("Directory " + dir + " not exist, create new...  ");
                if (dir.mkdir()) {
                    log.info("successful.");
                } else {
                    throw new GranException("Can't create the directory " + dir);
                }
            }
            if (!dir.isDirectory())
                throw new GranException(dir + " not a directory");
            if (!dir.canRead())
                throw new GranException("Permission denied to read directory " + dir);
            if (!dir.canWrite())
                throw new GranException("Permission denied to write directory " + dir);
            try {
                File tmpFile = new File(dir, "trackstudio_test_file.tmp");
                tmpFile.createNewFile();
                tmpFile.delete();
            } catch (IOException e) {
                throw new GranException("Permission denied to use directory " + dir);
            }
        } catch (Exception e) {
            throw new GranException(e, errorMessage);
        }
    }


    /**
     * Проверяет параметры в командной строке и записывает их
     *
     * @throws GranException при необходимости
     */
    public void checkAndSetConfigParameters() throws GranException {
        if (getProperty("trackstudio.siteURL") == null)
            throw new UserException("ERROR_TS_SITE_URL_IS_NULL");

        if (getProperty("trackstudio.encoding") == null) {
            throw new UserException("ERROR_INVALID_ENCODING_PARAMETER");
        }

        try {
            new String("123".getBytes("ISO-8859-1"), getProperty("trackstudio.encoding"));
        } catch (UnsupportedEncodingException e) {
            throw new UserException("ERROR_UNSUPPORTED_ENCODING", new Object[]{getProperty("trackstudio.encoding")});
        }

        if (!Config.getInstance().isSendMail() && Config.getInstance().isFormMailNotification()) {
            throw new UserException("ERROR_SUBMISSION_WITHOUT_NOTIFICATION");
        }

        String login = getProperty("mail.smtp.user");
        if (login != null && login.length() != 0) {
            setProperty("mail.smtp.auth", "true");
        }

        String smtpport = getProperty("mail.smtp.port");
        if (Config.getInstance().isSendMail() && (smtpport == null || smtpport.length() == 0))
            setProperty("mail.smtp.port", "25");
        String storePort = getProperty("mail.store.port");

        if (Config.getInstance().isFormMailNotification() && (storePort == null || storePort.length() == 0))
            setProperty("mail.store.port", "110");

        String fwd = getProperty("mail.store.forward");
        if (Config.getInstance().isFormMailNotification() && (fwd == null || !fwd.equals("no") && !fwd.equals("yes"))) {
            throw new UserException("ERROR_INVALID_FORWARD_PARAMETER");
        }
        String fwdAddress = getProperty("mail.store.fwdaddress");
        if (fwd != null && fwd.equals("yes") && (fwdAddress == null || fwdAddress.trim().length() == 0)) {
            throw new UserException("ERROR_INVALID_FWDADDRESS_PARAMETER");
        }

        if (Config.getInstance().isSendMail()) {
            try {
                Session session = getSession();
                String s = session.getProperty("mail.from");
                if (s == null || s.length() == 0) {
                    throw new UserException("ERROR_INVALID_SESSION_MAILFROM_PARAMETER");
                }
                session.getDebugOut().flush();
            } catch (Exception e) {
                throw new UserException(e, "ERROR_INVALID_MAILFROM_PARAMETER");
            }
        }

        if (getProperty("trackstudio.uploadDir") == null) {
            throw new UserException("ERROR_INVALID_UPLOADDIR_PARAMETER");
        }
        checkDir(new File(getProperty("trackstudio.uploadDir")), "Invalid trackstudio.uploadDir parameter");

        if (getProperty("trackstudio.indexDir") == null) {
            throw new UserException("ERROR_INVALID_INDEXDIR_PARAMETER");
        }
        checkDir(new File(getProperty("trackstudio.indexDir")), "Invalid trackstudio.indexDir parameter");

        String ldap = getProperty("trackstudio.useLDAP");
        if (ldap == null || !ldap.equals("yes") && !ldap.equals("no")) {
            throw new UserException("ERROR_INVALID_USELDAP_PARAMETER");
        }
    }

    public Properties getProperties() {
        return properties;
    }

    /**
     * Возвращает значение настройки TS по ее имени
     *
     * @param prop название настройки
     * @return значение нгастройки
     */
    public static String getProperty(String prop) {
        String ret = properties.getProperty(prop);
        return ret == null ? null : ret.trim();
    }

    public static String getProperty(String prop, String defaultValue) {
        String ret = properties.getProperty(prop);
        return ret == null || ret.isEmpty() ? defaultValue : ret.trim();
    }

    /**
     * Проверяет наличие настройки TS
     *
     * @param prop название настройки
     * @return TRUE - настройка существует, FALSE - нет
     */
    public static boolean isTurnItOn(String prop) {
        return isTurnItOn(prop, null);
    }

    public static boolean isTurnItOn(String prop, String defaultValue) {
        String ret = properties.getProperty(prop);
        ret = ret == null ? defaultValue : ret.trim();
        return "true".equalsIgnoreCase(ret) || "yes".equalsIgnoreCase(ret);
    }

    private static void setProperty(String prop, String value) {
        properties.setProperty(prop, value);
    }

    private void initTrackStudioHome() throws GranException {
        Env.getInstance().getTSConfig();
        if (Env.getInstance().getTSConfig() != null) {
            trackStudioHome = new Pair<String>(Env.getInstance().getTSConfig(), true);
            log.info("loading properties from TS_CONFIG=" + Env.getInstance().getTSConfig());
        } else if (System.getProperty("trackstudio.Home") != null) {
            trackStudioHome = new Pair<String>(System.getProperty("trackstudio.Home"), true);
            log.info("loading properties from -Dtrackstudio.Home=" + trackStudioHome);
        } else if (new File(servletContext.getRealPath("/WEB-INF") + File.separator + "trackstudio.adapters.properties").isFile()) {
            trackStudioHome = new Pair<String>(servletContext.getRealPath("/WEB-INF"), false);
            log.info("loading properties from WEB-INF");
        } else {
            throw new GranException("Can not find properties. Put them in /WEB-INF, or in -Dtrackstudio.Home, or in TS_CONFIG");
        }
    }

    private Properties loadConfigFile(String name) throws GranException {
        Properties properties = new Properties();
        try {
            final String path;
            if (trackStudioHome.isBoolValue()) {
                path = trackStudioHome.getT() + File.separator + "trackstudio" + File.separator + name;
            } else {
                path = trackStudioHome.getT() + File.separator + name;
            }
            FileInputStream fis = new FileInputStream(path);
            properties.load(fis);
            fis.close();
        } catch (IOException e) {
            //for #23630
            log.error(I18n.getUserExceptionString(null, "ERROR_CAN_NOT_FIND_PROPERTIES_FILE", new Object[]{e.getMessage()}));
            throw new UserException("ERROR_CAN_NOT_FIND_PROPERTIES_FILE", new Object[]{e.getMessage()});
        }

        Enumeration en = properties.propertyNames();
        while (en.hasMoreElements()) {
            String propName = (String) en.nextElement();
            String val = properties.getProperty(propName);
            if (val != null){
                log.debug("File = " + name + " property = " + propName + ", value = " + hiddenPassword(propName, val));
                setProperty(propName, val);
            }
        }
        return properties;
    }

    private boolean validateFile(String name) {
        final String path;
        if (trackStudioHome.isBoolValue()) {
            path = trackStudioHome.getT() + File.separator + "trackstudio" + File.separator + name;
        } else {
            path = trackStudioHome.getT() + File.separator + name;
        }
        if (new File(path).exists()) {
            log.info("Loading file: " + path);
            return true;
        } else {
            return false;
        }
    }

    /**
     * This method registers adapters properties
     * @param file Adapters properties
     * @throws GranException - unpredictable situation
     */
    public void registerAdapters(String file) throws GranException {
        Properties adaptersProperties = loadConfigFile(file);
        Enumeration en = adaptersProperties.propertyNames();
        while (en.hasMoreElements()) {
            String name = (String) en.nextElement();
            if (name.startsWith("adapter")) {
                if (adaptersProperties.getProperty(name) == null) {
                    throw new GranException("Can't load adapter list for " + name);
                }
                StringTokenizer st = new StringTokenizer(adaptersProperties.getProperty(name), ";");
                if (st.countTokens() == 0) {
                    log.warn("Empty adapter list in trackstudio.adapters.properties");
                }
                while (st.hasMoreTokens()) {
                    String adapter = st.nextToken().trim();
                    AdapterManager.getInstance().registerAdapter(adapter);
                }
            }
        }
    }

    /**
     * Проверяет является ли текущая запущенная копия экземпляром на TS HOST или нет
     *
     * @return TRUE - является, FALSE - нет
     */
    public boolean isTSHost() {
        return "TSL105580-da831e3a89f83fe025595effa39d1a98".equals(getProperty("trackstudio.license.signature"));
    }

    /**
     * Возращает путь до контекста
     *
     * @param request запрос
     * @return путь до контекста
     */
    public static String getContextPath(HttpServletRequest request) {
        return request.getContextPath();
    }

    /**
     * Возвращает версию
     *
     * @return версия
     * @throws com.trackstudio.exception.GranException for need
     */
    public static String getVersionPath() throws GranException {
            return "/cssjs";
    }

    /**
     * Возвращает путь до web-папки
     *
     * @return путь
     */
    public String getWebDir() {
        return trackStudioHome.getT() + "/plugins/web/";
    }

    /**
     * Возвращает путь до папки плагинов
     *
     * @return путь
     */
    public String getPluginsDir() {
        return trackStudioHome.getT() + "/plugins/";
    }

    /**
     * Возвращает путь до папки с шаблонами почты
     *
     * @return путь
     */
    public String getEmailDir() {
        return trackStudioHome.getT() + "/plugins/e-mail/";
    }

    /**
     * Возвращает список доступных локалей
     *
     * @param locale локаль
     * @return список локалей
     */
    public TreeSet<Pair> getAvailableLocales(Locale locale) {
        List<Locale> lcs = DateFormatter.getAllowedLocales();
        TreeSet<Pair> locales = new TreeSet<Pair>();
        locales.add(new Pair("az", "Azerbaijani (az)"));
        for (Locale lc : lcs) {
            locales.add(new Pair(lc.toString(), lc.getDisplayName(locale)));
        }
        return locales;
    }

    /**
     * Возвращает карту доступных локалей
     *
     * @param locale локаль
     * @return карта локалей
     */
    public TreeMap<String, String> getAvailableLocalesMap(Locale locale) {
        List<Locale> lcs = DateFormatter.getAllowedLocales();
        TreeMap<String, String> locales = new TreeMap<String, String>();
        for (Locale lc : lcs) {
            locales.put(lc.toString(), lc.getDisplayName(locale));
        }
        return locales;
    }

    /**
     * Возвращает список доступных таймзон
     *
     * @param lc локаль
     * @return список таймзон
     */
    public TreeSet<Pair> getAvailableTimeZones(Locale lc) {
        TreeSet<Pair> timezones = new TreeSet<Pair>();
        String[] tz = SimpleTimeZone.getAvailableIDs();
        for (String aTz : tz) {
            TimeZone t = SimpleTimeZone.getTimeZone(aTz);
            log.debug("aTz : " + aTz + " display : " + t.getDisplayName(lc) + " (" + aTz + ")");
            timezones.add(new Pair(aTz, t.getDisplayName(lc) + " (" + aTz + ")"));
        }
        return timezones;
    }

    /**
     * Возвращает карту доступных таймзон
     *
     * @param lc локаль
     * @return карта таймзон
     */
    public TreeMap<String, String> getAvailableTimeZonesMap(Locale lc) {
        TreeMap<String, String> timezones = new TreeMap<String, String>();
        String[] tz = SimpleTimeZone.getAvailableIDs();
        for (String aTz : tz) {
            TimeZone t = SimpleTimeZone.getTimeZone(aTz);
            timezones.put(aTz, t.getDisplayName(lc) + "(" + aTz + ")");
        }
        return timezones;
    }

    private String hiddenPassword(String propertyName, String value) {
        String valueHidden = "";
        String[] hiddenProperty = {
                "hibernate.connection.password",
                "ldap.userDNpass",
                "mail.smtp.password",
                "mail.store.password"};
        for (String prop : hiddenProperty)
            if (prop.equals(propertyName)) {
                for (int i = 0; i != value.length(); i++)
                    valueHidden += "*";
                return valueHidden;
            }
        return value;
    }

    /**
     * Возвращает значение проверки в базу первычных ключей
     * @return   результат проверки
     */
    public static boolean isValidePrimaryKey() throws GranException {
        return "true".equals(TSPropertyManager.getInstance().get("trackstudio.validatePrimaryKey"));
    }

    /**
     * Возвращает список настроек почтовых ящиков для импорта
     * @return
     */
    public  ArrayList<MailReceiver> getMailReceivers() {
        ArrayList<MailReceiver> mailReceivers = new ArrayList<MailReceiver>();
        try {
            DOMParser parser = new DOMParser();
            parser.parse(new InputSource(trackStudioHome.getT() + File.separator + "mailreceiver.xml"));
            Document xmlDoc = parser.getDocument();
            if (xmlDoc == null)
                return mailReceivers;
            Element el = xmlDoc.getDocumentElement();
            NodeList nodes = el.getElementsByTagName("mailbox");
            int numElem = nodes.getLength();
            for (int i = 0; i < numElem; i++) {
                Element n = (Element) nodes.item(i);
                if (n == null)
                    continue;
                String name = n.getAttribute("name");
                String user = ((Element) (n.getElementsByTagName("user").item(0))).getAttribute("name");
                String password = ((Element) (n.getElementsByTagName("password").item(0))).getAttribute("name");
                String host = ((Element) (n.getElementsByTagName("host").item(0))).getAttribute("name");
                String port = ((Element) (n.getElementsByTagName("port").item(0))).getAttribute("name");
                String protocol = ((Element) (n.getElementsByTagName("protocol").item(0))).getAttribute("name");
                mailReceivers.add(new MailReceiver(name, host, protocol, port, user, password));
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e);
        }
        return mailReceivers;
    }

}