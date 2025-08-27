package com.trackstudio.tools;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;

import net.jcip.annotations.Immutable;

@Immutable
public class DetectTimeZone {
    private DetectTimeZone() {
    }

    public static String getCurrentTimeZone(String tz) {
        TimeZone timeZone = TimeZone.getDefault();
        if (tz != null && tz.indexOf(",") != -1) {
            tz = tz.substring(0, tz.indexOf(","));
            timeZone = TimeZone.getTimeZone("GMT"+tz);
        }
        return timeZone.getID();
    }

    public static String getDetectTimeZone(String timezone) {
        return getTimeZone().get(timezone);
    }

    public static String getDetectKey(String timezone) {
        if (timezone != null && timezone.indexOf("(") != -1 && timezone.indexOf(")") != -1) {
            for (Map.Entry<String, String> entry : getTimeZone().entrySet()) {
                if (entry.getValue().equals(timezone)) {
                    return entry.getKey();
                }
            }
            return "";
        }
        return "";
    }

    public static String currentTime(String tz) {
        TimeZone timeZone = TimeZone.getDefault();
        if (tz != null) {
            if (tz.indexOf("(") != -1 && tz.indexOf(")") != -1) {
                String time = tz.substring(tz.indexOf("(") + "(".length(), tz.indexOf(")"));
                timeZone = TimeZone.getTimeZone("GMT"+time);
            }
        }
        DateFormat dfm = new SimpleDateFormat("HH:mm:ss");
        Calendar now = Calendar.getInstance();
        dfm.setTimeZone(timeZone);
        return dfm.format(now.getTime());
    }

    public static Map<String, String> getTimeZone() {
        Map<String, String> timezones = new LinkedHashMap<String, String>();
        timezones.put("-12:00,0", "(-12:00) International Date Line West");
        timezones.put("-11:00,0", "(-11:00) Midway Island, Samoa");
        timezones.put("-10:00,0", "(-10:00) Hawaii");
        timezones.put("-09:00,1", "(-09:00) Alaska");
        timezones.put("-08:00,1", "(-08:00) Pacific Time (US &amp; Canada)");
        timezones.put("-07:00,0", "(-07:00) Arizona");
        timezones.put("-07:00,1", "(-07:00) Mountain Time (US &amp; Canada)");
        timezones.put("-06:00,0", "(-06:00) Central America, Saskatchewan");
        timezones.put("-06:00,1", "(-06:00) Central Time (US &amp; Canada), Guadalajara, Mexico city");
        timezones.put("-05:00,0", "(-05:00) Indiana, Bogota, Lima, Quito, Rio Branco");
        timezones.put("-05:00,1", "(-05:00) Eastern time (US &amp; Canada)");
        timezones.put("-04:00,1", "(-04:00) Atlantic time (Canada), Manaus, Santiago");
        timezones.put("-04:00,0", "(-04:00) Caracas, La Paz");
        timezones.put("-03:30,1", "(-03:30) Newfoundland");
        timezones.put("-03:00,1", "(-03:00) Greenland, Brasilia, Montevideo");
        timezones.put("-03:00,0", "(-03:00) Buenos Aires, Georgetown");
        timezones.put("-02:00,1", "(-02:00) Mid-Atlantic");
        timezones.put("-01:00,1", "(-01:00) Azores");
        timezones.put("-01:00,0", "(-01:00) Cape Verde Is.");
        timezones.put("00:00,0", "(00:00) Casablanca, Monrovia, Reykjavik");
        timezones.put("00:00,1", "(00:00) GMT: Dublin, Edinburgh, Lisbon, London");
        timezones.put("+01:00,1", "(+01:00) Amsterdam, Berlin, Rome, Vienna, Prague, Brussels");
        timezones.put("+01:00,0", "(+01:00) West Central Africa");
        timezones.put("+02:00,1", "(+02:00) Amman, Athens, Istanbul, Beirut, Cairo, Jerusalem");
        timezones.put("+02:00,0", "(+02:00) Harare, Pretoria");
        timezones.put("+03:00,1", "(+03:00) Baghdad, Moscow, St. Petersburg, Volgograd");
        timezones.put("+03:00,0", "(+03:00) Kuwait, Riyadh, Nairobi, Tbilisi");
        timezones.put("+03:30,0", "(+03:30) Tehran");
        timezones.put("+04:00,0", "(+04:00) Abu Dhadi, Muscat");
        timezones.put("+04:00,1", "(+04:00) Baku, Yerevan");
        timezones.put("+04:30,0", "(+04:30) Kabul");
        timezones.put("+05:00,1", "(+05:00) Ekaterinburg");
        timezones.put("+05:00,0", "(+05:00) Islamabad, Karachi, Tashkent");
        timezones.put("+05:30,0", "(+05:30) Chennai, Kolkata, Mumbai, New Delhi, Sri Jayawardenepura");
        timezones.put("+05:45,0", "(+05:45) Kathmandu");
        timezones.put("+06:00,0", "(+06:00) Astana, Dhaka");
        timezones.put("+06:00,1", "(+06:00) Almaty, Nonosibirsk");
        timezones.put("+06:30,0", "(+06:30) Yangon (Rangoon)");
        timezones.put("+07:00,1", "(+07:00) Krasnoyarsk");
        timezones.put("+07:00,0", "(+07:00) Bangkok, Hanoi, Jakarta");
        timezones.put("+08:00,0", "(+08:00) Beijing, Hong Kong, Singapore, Taipei");
        timezones.put("+08:00,1", "(+08:00) Irkutsk, Ulaan Bataar, Perth");
        timezones.put("+09:00,1", "(+09:00) Yakutsk");
        timezones.put("+09:00,0", "(+09:00) Seoul, Osaka, Sapporo, Tokyo");
        timezones.put("+09:30,0", "(+09:30) Darwin");
        timezones.put("+09:30,1", "(+09:30) Adelaide");
        timezones.put("+10:00,0", "(+10:00) Brisbane, Guam, Port Moresby");
        timezones.put("+10:00,1", "(+10:00) Canberra, Melbourne, Sydney, Hobart, Vladivostok");
        timezones.put("+11:00,0", "(+11:00) Magadan, Solomon Is., New Caledonia");
        timezones.put("+12:00,1", "(+12:00) Auckland, Wellington");
        timezones.put("+12:00,0", "(+12:00) Fiji, Kamchatka, Marshall Is.");
        timezones.put("+13:00,0", "(+13:00) Nuku'alofa");
        return timezones;
    }
}
