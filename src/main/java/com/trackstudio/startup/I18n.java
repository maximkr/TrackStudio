package com.trackstudio.startup;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentSkipListMap;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.session.SessionContext;
import com.trackstudio.exception.GranException;
import com.trackstudio.tools.textfilter.HTMLEncoder;

import freemarker.template.SimpleSequence;
import freemarker.template.TemplateModelException;
import net.jcip.annotations.Immutable;

/**
 * Класс используется для работы с локализациями системы
 */
@Immutable
public class I18n {
    private final ServletContext cl;
    private static final Log log = LogFactory.getLog(I18n.class);
    private final static String DEFAULT = "en"; // нельзя удалять, потому что других локалей, которые выставлены в default, может не быть
    /**
     * Хранилище рессурсов
     */
    protected final ConcurrentSkipListMap<String, TrackStudioBundle> resources;
    /**
     * это поле используется для меппигна бандлов в случае отсутствия бандла для какого-то языка. Ключи - коды языков, значения - ключи в resources
     */
    protected final ConcurrentSkipListMap<String, String> mapper;

    private static String makePath(String a) {
        return "/WEB-INF/classes/language_" + a + ".properties";
    }

    /**
     * Выводит локализованный текст с хинтом
     *
     * @param sc  сессия пользователя
     * @param key ключ
     * @return локализованный текст
     * @throws GranException при необходимости
     */
    public static String getString(SessionContext sc, String key) throws GranException {
        return getString(sc.getLocale(), key);
    }

    private static class TrackStudioBundle extends PropertyResourceBundle {
        public TrackStudioBundle(InputStream in) throws IOException {
           super(new java.io.InputStreamReader(in, java.nio.charset.StandardCharsets.UTF_8));
        }

        public void setParent(ResourceBundle b) {
            super.setParent(b);
        }

    }

    private String getKey(String language) {
        if (mapper.containsKey(language)) {
            return mapper.get(language);
        } else
            return language;
    }

    /**
     * Возвращает рессурсы для указанного языка
     *
     * @param language язык
     * @return рессурсы
     * @throws GranException при необходимости
     */
    protected ResourceBundle getResource(String language) throws GranException {
        if (this.resources.containsKey(getKey(language))) {
            return this.resources.get(getKey(language));
        } else
            return loadResource(language);
    }

    private static I18n i18n;

    private I18n(ServletContext cl) {
        this.cl = cl;
        this.resources = new ConcurrentSkipListMap<String, TrackStudioBundle>();
        this.mapper = new ConcurrentSkipListMap<String, String>();
    }

    /**
     * Загружает настройки
     *
     * @param cl настройки
     */
    public static synchronized void loadConfig(ServletContext cl) {
        if (i18n == null) {
            i18n = new I18n(cl);
            try {
                getString("en", "TASK");
                /*dnikitin: у I18 есть глюк, что если попытаться достать строку для локали, к
                которой нет файла language_xx_.properties, и эта попытка будет первой (со времени загрузки), tо ts виснет со stack overflow.
                цель I18n.getString("en", "TERM_LOGIN"); - избежать угазанного глюка*/
            } catch (Exception e) {/*Empty*/}
        }
    }

    /**
     * ВОзвращает экземпляр текущего класса
     *
     * @return экземпляр I18n
     * @throws GranException при необходимости
     */
    public static synchronized I18n getInstance() throws GranException {
        if (i18n == null) {
            throw new GranException("I18n must be initialized before use");
        }
        return i18n;
    }

    /**
     * Возвращает рессурсы для указанного языка
     *
     * @param lang язык
     * @return рессурсы
     * @throws GranException при необходимости
     */
    protected ResourceBundle loadResource(String lang) throws GranException {
        TrackStudioBundle resource = null;
        String key = lang;
        String surrogate = getKey(DEFAULT);
        try {
            int j = 0;
            while (j < 50) {
                resource = new TrackStudioBundle(cl.getResourceAsStream(makePath(key)));
                if (!key.equals(DEFAULT)) {
                    resource.setParent(this.resources.get(surrogate));
                }
                surrogate = lang + j;

                this.mapper.put(key, surrogate);
                this.mapper.put(lang, surrogate);
                this.resources.put(surrogate, resource);
                j++;
                key = lang + j;
            }
        } catch (Exception e) {
            if (key.equals(DEFAULT)) {
                throw new GranException(e);
            } else {
                this.mapper.put(key, surrogate);
                this.mapper.put(lang, surrogate);
                return getResource(key);
            }
        }
        return resource;
    }

    /**
     * Возвращает строку на основании локали и ключа
     *
     * @param locale локаль
     * @param key    ключ
     * @return локализованная строка
     * @throws GranException при необходимости
     */
    public static String getString(Locale locale, String key) throws GranException {
        return getString(locale.toString(), key);
    }

    /**
     * Возвращает строку на основании дефолтной локали и ключа
     *
     * @param key ключ
     * @return локализованная строка
     * @throws GranException при необходимости
     */
    public static String getString(String key) throws GranException {
        try {
            return getInstance().getResource(DEFAULT).getString(key);
        } catch (MissingResourceException me) {
            return key;
        }
    }

    /**
     * Возвращает строку для пользовательского исключения
     *
     * @param key ключ
     * @return строка
     */
    public static String getUserExceptionString(String key) {
        try {
            return getString(key);
        } catch (GranException ge) {
            return "I18n must be initialized before use";
        }
    }

    /**
     * Возвращает локализованную строку на основании люча и локали
     *
     * @param locale локаль
     * @param key    ключ
     * @return строка
     * @throws GranException при необходимости
     */
    public static String getString(String locale, String key) throws GranException {
        String suffix = DEFAULT;
        if (locale != null && locale.length() >= 2) {
            suffix = locale;
            suffix = suffix.substring(0, 2);
        }
        try {
            return getInstance().getResource(suffix).getString(key);
        } catch (MissingResourceException me) {
            return key;
        }
    }

    /**
     * Возвращает локализованную строку
     *
     * @param locale локаль
     * @param key    ключ
     * @param params параметры
     * @return строка
     * @throws GranException при необходимости
     */
    public static String getString(String locale, String key, Object[] params) throws GranException {
        if (key == null) {
            return "null";
        }
        String pattern = getString(locale, key);
        MessageFormat form = new MessageFormat(pattern, Locale.ENGLISH);
        return form.format(params);
    }
    
    public static String getString(SessionContext sc, String key, Object[] params) throws GranException {
        return getString(sc.getLocale(), key, params);
    }
    
    /**
     * Возвращает локализованную строку
     *
     * @param key    ключ
     * @param params параметры
     * @return строка
     * @throws GranException при необходимости
     */
    public static String getString(String key, Object[] params) throws GranException {
        return getString(DEFAULT, key, params);
    }

    /**
     * Возвращает локализованную строку
     *
     * @param locale локаль
     * @param key    ключ
     * @param params параметры
     * @return строка
     * @throws GranException при необходимости
     */
    public static String getString(String locale, String key, SimpleSequence params) throws GranException {
        HTMLEncoder sb = new HTMLEncoder(getString(locale, key));
        sb.replace("'", "''");
        Object[] c = new Object[params.size()];
        try {
            Collection coll = (Collection) freemarker.template.utility.DeepUnwrap.unwrap(params);
            c = coll.toArray();
        } catch (TemplateModelException e) {
            log.error("Exception ", e);
        }
        //fix for email templates
        MessageFormat form = new MessageFormat(sb.toString());
        return form.format(c);
    }

    /**
     * Возвращает локализованную строку
     *
     * @param key    ключ
     * @param params параметры
     * @return строка
     * @throws GranException при необходимости
     */
    public static String getString(String key, SimpleSequence params) throws GranException {
        return getString(DEFAULT, key, params);
    }

    /**
     * Возвращает строку для пользовательского исключения
     *
     * @param locale локаль
     * @param key    ключ
     * @param params параметры
     * @return строка
     */
    public static String getUserExceptionString(String locale, String key, Object[] params) {
        try {
            return getString(locale, key, params);
        } catch (GranException ge) {
            return "I18n must be initialized before use";
        }
    }
}
