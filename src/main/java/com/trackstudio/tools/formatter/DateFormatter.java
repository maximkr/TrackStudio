package com.trackstudio.tools.formatter;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.exception.GranException;
import com.trackstudio.startup.Config;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.Null;
import com.trackstudio.tools.textfilter.HTMLEncoder;

import net.jcip.annotations.Immutable;


/**
 * Класс содержит методы для работы с датой. Производятся ее преобразования на основании указанной локали и таймзоны пользователя
 */
@Immutable
public class DateFormatter implements Serializable {

    private static final Log log = LogFactory.getLog(DateFormatter.class);
    private final String[] ignoreLocales = new String[]{"ar", "ar_AE", "ar_BH", "ar_DZ", "ar_EG", "ar_IQ", "ar_JO", "ar_KW", "ar_LB", "ar_LY", "ar_MA", "ar_OM", "ar_QA", "ar_SA", "hi_IN", "ar_SD", "ko", "ko_KR", "th", "th_TH", "th_TH_TH", "zh", "zh_CN", "zh_HK", "zh_TW", "ar_SY", "ar_TN", "ar_YE", "el", "el_GR"};
    private final TimeZone timezone;
    private final Locale locale;
    private static final CopyOnWriteArrayList<Locale> availableLocales = new CopyOnWriteArrayList(Null.removeNullElementsFromList(Arrays.asList(Locale.getAvailableLocales())));
    private static final AtomicReference<CopyOnWriteArrayList<Locale>> allowedLocales = new AtomicReference(null);
    /**
     * Список интервалов
     */
    private final int[] intervalVal = {30, 60, 120, 180, 360, 720, 1440, 2880, 4320, 7200, 10080, 20160, 30240, 43200};
    /**
     * Строковые представления интервалов
     */
    private final String[] intervalStr;

    /**
     * Возвращает локаль
     *
     * @return локаль
     */
    public Locale getLocale() {
        return this.locale;
    }

    /**
     * Возвращает таймзону
     *
     * @return таймзона
     */
    public TimeZone getTimeZone() {
        return this.timezone;
    }

    /**
     * Возвращает календарь с локальб и таймзоной
     *
     * @return календарь
     */
    public Calendar getCalendar() {
        return new GregorianCalendar(this.timezone, this.locale);
    }

    /**
     * Возвращает форматтер даты
     *
     * @return форматтер даты
     */
    public DateFormat getDateFormat() {
        DateFormat f = SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, this.getLocale());
        f.setTimeZone(timezone);
        f.setCalendar(this.getCalendar());
        return f;
    }

    /**
     * Возвращает форматтер даты в сокращенном виде
     *
     * @return форматтер даты
     */
    DateFormat getShortDateFormat() {
        DateFormat f = SimpleDateFormat.getDateInstance(DateFormat.SHORT, this.getLocale());
        f.setCalendar(this.getCalendar());
        return f;
    }

    //todo tdc108 время приходит в формате Timestamp, а не в формате, с которым работает DateFormat.

    /**
     * Разбирает дату в виде строки и переводит ее в вид календаря
     *
     * @param param исходная строка
     * @return календарь
     * @throws GranException при необходимости
     */
    public Calendar parseToCalendar(String param) throws GranException {
        log.trace("parse");
        log.debug("param: (" + param + ')');
        if (param == null)
            return null;

        try {
            DateFormat df = this.getDateFormat();
            String str = param.trim(); // delete spaces
            Calendar c = new GregorianCalendar();
            c.setTime(df.parse(str));
            return c;
        } catch (Exception e) {
            try {
                DateFormat df2 = this.getShortDateFormat();
                String str2 = param.trim(); // delete spaces
                Calendar c = new GregorianCalendar();
                c.setTime(df2.parse(str2));
                return c;
            } catch (Exception ex) {
                try {
                    Calendar c = new GregorianCalendar();
                    c.setTimeInMillis(Timestamp.valueOf(param).getTime());
                    return c;
                } catch (Exception e2) {
                    throw new GranException("Can't parse this date");
                }
            }

        }
    }

    /**
     * Преобразует Timestamp в строку
     *
     * @param param объект Timestamp
     * @return строка
     */
    public String parse(Timestamp param) {
        if (param == null)
            return null;

        DateFormat df = this.getDateFormat();
        return df.format(param);
    }

    /**
     * Преобразует Calendar в строку
     *
     * @param param объект Calendar
     * @return строка
     */
    public String parse(Calendar param) {
        if (param == null)
            return "";
        DateFormat df = this.getDateFormat();
        df.setTimeZone(timezone);
        return df.format(param.getTime());
    }

    /**
     * Конструктор
     *
     * @param tz Таймзона
     * @param lc Локаль
     * @throws GranException при необходимости
     */
    public DateFormatter(TimeZone tz, Locale lc) throws GranException {
        this.timezone = tz;
        Locale newLoc = lc != null ? lc : Locale.getDefault();
        for (int i = 0; i < ignoreLocales.length; i++)
            if (ignoreLocales[i].equals(newLoc.toString())) {
                newLoc = Locale.US;
                break;
            }
        this.locale = newLoc;
        this.intervalStr = new String[]{I18n.getString(locale, "MIN30"),
                I18n.getString(locale, "HOUR1"),
                I18n.getString(locale, "HOUR2"),
                I18n.getString(locale, "HOUR3"),
                I18n.getString(locale, "HOUR6"),
                I18n.getString(locale, "HOUR12"),
                I18n.getString(locale, "DAY1"),
                I18n.getString(locale, "DAY2"),
                I18n.getString(locale, "DAY3"),
                I18n.getString(locale, "DAY5"),
                I18n.getString(locale, "WEEK1"),
                I18n.getString(locale, "WEEK2"),
                I18n.getString(locale, "WEEK3"),
                I18n.getString(locale, "DAYS30")};

    }

    /**
     * Преобразует строку в локаль
     * <br/>
     * Language is always lower case, and country is always upper case.
     * If the language is missing, the string will begin with an underbar.
     * If both the language and country fields are missing, this function will return the empty string,
     * even if the variant field is filled in (you can't have a locale with just a variant--
     * the variant must accompany a valid language or country code). Examples: "en", "de_DE", "_GB", "en_US_WIN", "de__POSIX", "fr_MAC"
     *
     * @param lc локаль
     * @return локаль
     */
    public static Locale toLocale(String lc) {
        if (lc == null || lc.length() == 0) lc = Config.getInstance().getDefaultLocale();
        StringTokenizer tk = new StringTokenizer(lc, "_", false);
        if (tk.countTokens() < 1 && lc.length() == 2) return new Locale(lc, "", "");
        String lang = "";
        lang = tk.nextToken();
        String country = "";
        if (tk.hasMoreTokens()) country = tk.nextToken();
        String variant = "";
        if (tk.hasMoreTokens()) variant = tk.nextToken();
        return new Locale(lang, country, variant);
    }

    /**
     * Преобразует строку в таймзону
     *
     * @param tz таймзона
     * @return таймзона
     */
    public static TimeZone getTimeZoneFromString(String tz) {
        if (tz != null && tz.length() > 0) {
            return SimpleTimeZone.getTimeZone(tz);
        } else {
            return SimpleTimeZone.getTimeZone(Config.getInstance().getDefaultTimezone());
        }
    }

    /**
     * Преобразует строку в локаль
     *
     * @param lc локаль
     * @return локаль
     */
    public static Locale getLocaleFromString(String lc) {
        Locale l = null;
        if (lc == null || lc.length() == 0)
            lc = Config.getInstance().getDefaultLocale();
        List<Locale> lcs = getAvailableLocales();
        for (Locale n : lcs) {
            if (n.toString().equals(lc))
                l = n;
        }
        return l;
    }

    /**
     * Конструктор
     *
     * @param tz таймзона
     * @param lc локаль
     * @throws GranException при необходимости
     */
    public DateFormatter(String tz, String lc) throws GranException {
        this(getTimeZoneFromString(tz), getLocaleFromString(lc));
    }

    /**
     * Возвращает паттерн
     *
     * @return паттерн
     */
    public String getPattern() {
        return ((SimpleDateFormat) this.getDateFormat()).toPattern();
    }

    /**
     * Возвращает паттерн
     *
     * @return паттерн
     */
    public String getPattern2() {
        HTMLEncoder sb = new HTMLEncoder(((SimpleDateFormat) this.getDateFormat()).toPattern());
        sb.replace("yyyy", "%Y");
        sb.replace("yy", "%y");
        if (sb.getResult().indexOf("%y") == -1)
            sb.replace("y", "%y");
        sb.replace("dd", "%d");
        if (sb.getResult().indexOf("%d") == -1)
            sb.replace("d", "%e");
        sb.replace("MM", "%m");
        sb.replace("M", "%z");//dnikitin При переходе на новую версию календарика это работать не будет
        sb.replace("HH", "%H");
        if (sb.getResult().indexOf("%H") == -1)
            sb.replace("H", "%k");
        sb.replace("hh", "%I");
        sb.replace("h", "%l");
        sb.replace("mm", "%M");
        sb.replace("ss", "%S");

        //HTMLEncoder.replace(sb, "M","%m");

        sb.replace("a", "%p");
        //HTMLEncoder.replace(sb, "A","%P");
        return sb.toString();
    }

    /**
     * Возвращает теги для указанного интервала
     *
     * @param interval интервал
     * @return теги
     * @throws GranException при необходимости
     */
    public String getIntervalSelectTag(int interval) throws GranException {
        log.trace("getIntervalSelectTag1(" + interval + ')');
        StringBuffer result = new StringBuffer();
        result.append("<select name='interval'>");
        boolean selected = false;
        for (int i = 0; i < intervalVal.length; i++) {
            if (interval == intervalVal[i]) {
                selected = true;
                result.append("<option selected='selected' value='").append(intervalVal[i]).append("'>").append(intervalStr[i]).append("</option>");
            } else
                result.append("<option value='").append(intervalVal[i]).append("'>").append(intervalStr[i]).append("</option>");
        }
        if (!selected)
            result.append("<option selected='selected' value='").append(interval).append("'>").append(interval).append(' ').append(I18n.getString(locale, "MIN")).append("</option>");
        result.append("</select>");
        return result.toString();
    }

    /**
     * Возвращает строковое значение для интервала
     *
     * @param interval интервал
     * @return строковое значение
     * @throws GranException при необходимости
     */
    public String getInterval(int interval) throws GranException {
        for (int i = 0; i < intervalVal.length; i++)
            if (interval == intervalVal[i])
                return intervalStr[i];
        return interval + " " + I18n.getString(locale, "MIN");
    }

    /**
     * Возвращает доступные локали
     *
     * @return список локалей
     */
    public static List<Locale> getAvailableLocales() {
        return availableLocales;
    }

    /**
     * Возвращает знакомые локали
     *
     * @return список локалей
     */
    public static List<Locale> getAllowedLocales() {
        if (allowedLocales.get() != null)
            return allowedLocales.get();

        CopyOnWriteArrayList ret;
        CopyOnWriteArrayList locales = new CopyOnWriteArrayList<Locale>();
            ArrayList<String> allowed = Config.getInstance().getAllowedLocales();
            if (allowed != null)
                for (String p : allowed) {
                    Locale l = getLocaleFromString(p);
                    if (l != null)
                        locales.add(l);
            }

        if (locales.isEmpty())
            ret = availableLocales;
        else
            ret = locales;

        allowedLocales.set(ret);
        return ret;
    }

    /**
     * Проверка
     *
     * @param args аргументы
     */
    public static void main(String[] args) throws Exception {
        DateFormat df = SimpleDateFormat.getDateInstance(DateFormat.SHORT, Locale.UK);
        df.setTimeZone(TimeZone.getTimeZone("Etc/GMT-1"));
        df.setCalendar(Calendar.getInstance());
//
        System.out.println(df.parse("15/02/2012").toString());

        for (String timeId : TimeZone.getAvailableIDs()) {
            if (timeId.contains("GMT-1")) {
                System.out.println(" timeid : " + timeId + " : " + TimeZone.getTimeZone(timeId));
            }
        }
//        for (int i = 0; i < Locale.getAvailableLocales().length; i++) {
//            SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.getAvailableLocales()[i]);
//            System.out.println(sdf.toPattern());
//            System.out.println(sdf.format(new Date(System.currentTimeMillis())));
//        }
    }
}