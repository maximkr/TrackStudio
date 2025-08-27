package com.trackstudio.tools.formatter;

import java.io.Serializable;

import com.trackstudio.app.filter.customizer.BudgetCustomizer;
import com.trackstudio.constants.CategoryConstants;
import com.trackstudio.exception.GranException;
import com.trackstudio.startup.I18n;

import net.jcip.annotations.Immutable;

/**
 * Класс содержит методы для работы Форматированием вывода бюджета и потраченного времени.
 */
@Immutable
public class HourFormatter implements Serializable {

    private final Double hours;
    private final Double minutes;
    private final Long seconds;
    private final Double days;
    private final Double weeks;
    private final Double months;
    private final Double years;
    private final Long time; //seconds
    private final String format;
    private final String locale; // локаль
    private static final double LIMIT = 0.000001d;

    /**
     * Возвращает формат
     *
     * @return формат
     */
    public String getFormat() {
        return format;
    }

    /**
     * Разбирает входные данные и выводит на выход дату в формате long
     *
     * @param years   года
     * @param months  месяцы
     * @param weeks   недели
     * @param days    дни
     * @param hrs     часы
     * @param mins    минуты
     * @param seconds секунды
     * @return дата в формате long
     */
    public static Long parseInput(Object years, Object months, Object weeks, Object days, Object hrs, Object mins, Object seconds) {
        BudgetCustomizer.initConstants();
        Long time = 0L;

        if (years != null) {
            double ye = parseDouble(years.toString());
            if (ye < 0) ye = 0;
            time += Math.round(ye * BudgetCustomizer.HOURS_IN_DAY * BudgetCustomizer.DAYS_IN_MONTH * BudgetCustomizer.MONTHS_IN_YEAR * 3600L);
        }

        if (months != null) {
            double mo = parseDouble(months.toString());
            if (mo < 0) mo = 0;
            time += Math.round(mo * BudgetCustomizer.HOURS_IN_DAY * BudgetCustomizer.DAYS_IN_MONTH * 3600L);
        }
        if (weeks != null) {
            double w = parseDouble(weeks.toString());
            if (w < 0) w = 0;
            time += Math.round(w * BudgetCustomizer.HOURS_IN_DAY * BudgetCustomizer.DAYS_IN_WEEK * 3600L);
        }

        if (days != null) {
            double d = parseDouble(days.toString());
            if (d < 0) d = 0;

            time += Math.round(d * BudgetCustomizer.HOURS_IN_DAY * 3600L);
        }

        if (hrs != null) {
            double h = parseDouble(hrs.toString());
            if (h < 0) h = 0;
            time += Math.round(h * 3600L);
        }

        if (mins != null) {
            double m = parseDouble(mins.toString());
            if (m < 0) m = 0;
            time += Math.round(m * 60L);
        }

        if (seconds != null) {
            long s = getLongOrZero(seconds.toString());
            if (s < 0) s = 0;
            time += s;
        }
        return time;
    }

    private static long getLongOrZero(String value) {
        long result;
        try {
            result = Long.valueOf(value);
        } catch (NumberFormatException e) {
            result = 0l;
        }
        return result;
    }

    public static double parseDouble (String value) {

        if (value == null || value.trim().length() == 0)
            return 0d;
        try{
            return Double.parseDouble(value.trim().replaceAll(",", "."));
        } catch (Exception e){
            try{
                return Double.parseDouble(value.trim().replaceAll("\\.", ","));
            } catch (Exception ee){
                return 0d;
            }
        }
    }

    /**
     * Конструктор
     *
     * @param seconds      Время в секундах, как в базе
     * @param budgetFormat формат бюджета
     * @param locale       локаль
     */
    public HourFormatter(Long seconds, String budgetFormat, String locale) {
        //YMWDhms ( 1 years 2 months 3 weeks 4 days 5 hours )

        // we cannot modify final fields multiple times, so introduce this local version
        Double locHours = 0d;
        Double locMinutes = 0d;
        Long locSeconds = 0L;
        Double locDays = 0d;
        Double locWeeks = 0d;
        Double locMonths = 0d;
        Double locYears = 0d;
        Long locTime; //seconds

        BudgetCustomizer.initConstants();
        Long val = 0L;
        if (seconds != null) val = seconds;
        locTime = val;
        if (locTime != null && locTime > 0) {
            long localTime = locTime;

            if (budgetFormat.contains(CategoryConstants.Y)) {
                // год есть. Считаем ЦЕЛОЕ количество лет, именно так, сначала обрезаем до int, потом делаем double
                locYears = (double) (localTime / (BudgetCustomizer.HOURS_IN_DAY * BudgetCustomizer.DAYS_IN_MONTH * BudgetCustomizer.MONTHS_IN_YEAR * 3600));
                // остаток, если года больше одного
                if (locYears >= 1d)
                    localTime = localTime - Math.round((locYears * BudgetCustomizer.HOURS_IN_DAY * BudgetCustomizer.DAYS_IN_MONTH * BudgetCustomizer.MONTHS_IN_YEAR * 3600L));

            }
            if (budgetFormat.contains(CategoryConstants.M)) {
                // есть месяц. Считаем целое количество из остатка
                locMonths = (double) (localTime / (BudgetCustomizer.HOURS_IN_DAY * BudgetCustomizer.DAYS_IN_MONTH * 3600));
                if (locMonths >= 1d)
                    localTime = localTime - Math.round((locMonths * BudgetCustomizer.HOURS_IN_DAY * BudgetCustomizer.DAYS_IN_MONTH * 3600L));
            }
            if (budgetFormat.contains(CategoryConstants.W)) {
                // неделя. Тот же принцип
                locWeeks = (double) (localTime / (BudgetCustomizer.HOURS_IN_DAY * BudgetCustomizer.DAYS_IN_WEEK * 3600));
                if (locWeeks >= 1d)
                    localTime = localTime - Math.round((locWeeks * BudgetCustomizer.HOURS_IN_DAY * BudgetCustomizer.DAYS_IN_WEEK * 3600L));
            }
            if (budgetFormat.contains(CategoryConstants.D)) {
                locDays = (double) (localTime / (BudgetCustomizer.HOURS_IN_DAY * 3600));
                if (locDays >= 1d)
                    localTime = localTime - Math.round((locDays * BudgetCustomizer.HOURS_IN_DAY * 3600L));
            }
            if (budgetFormat.contains(CategoryConstants.h)) {
                locHours = (double) (localTime / 3600);
                if (locHours >= 1d)
                    localTime = localTime - Math.round((locHours * 3600L));
            }
            if (budgetFormat.contains(CategoryConstants.m)) {
                locMinutes = (double) (localTime / 60);
                if (locMinutes >= 1d)
                    localTime = localTime - Math.round((locMinutes * 60L));
            }
            if (budgetFormat.contains(CategoryConstants.s)) {
                locSeconds = localTime;
                localTime = 0L;
            }

            // на этом этапе мы распределили средние. Если были указаны секунды, то дробей никаких не должно получиться. Однакое если секунд не было, будем засовывать остаток в виде офигенной дробы
            if (localTime > 0) {
                if (budgetFormat.contains(CategoryConstants.m)) {
                    locMinutes += (double) localTime / 60d;
                } else if (budgetFormat.contains(CategoryConstants.h)) {
                    locHours += (double) localTime / 3600d;
                } else if (budgetFormat.contains(CategoryConstants.D)) {
                    locDays += (double) localTime / (BudgetCustomizer.HOURS_IN_DAY * 3600d);
                } else if (budgetFormat.contains(CategoryConstants.W)) {
                    locWeeks += (double) localTime / (BudgetCustomizer.HOURS_IN_DAY * BudgetCustomizer.DAYS_IN_WEEK * 3600d);
                } else if (budgetFormat.contains(CategoryConstants.M)) {
                    locMonths += (double) localTime / (BudgetCustomizer.HOURS_IN_DAY * BudgetCustomizer.DAYS_IN_MONTH * 3600d);
                } else if (budgetFormat.contains(CategoryConstants.Y)) {
                    locYears += (double) localTime / (BudgetCustomizer.HOURS_IN_DAY * BudgetCustomizer.DAYS_IN_MONTH * BudgetCustomizer.MONTHS_IN_YEAR * 3600d);
                }
            }
        }

        this.locale = locale;
        this.format = budgetFormat;

        this.hours = locHours;
        this.minutes = locMinutes;
        this.seconds = locSeconds;
        this.days = locDays;
        this.weeks = locWeeks;
        this.months = locMonths;
        this.years = locYears;
        this.time = locTime;

    }

    /**
     * Возвращает количество часов
     *
     * @return количество часов
     */
    public Double getHours() {
        return hours;
    }

    /**
     * Возвращает количество минут
     *
     * @return количество минут
     */
    public Double getMinutes() {
        return minutes;
    }

    /**
     * Возвращает количество секунд
     *
     * @return количество секунд
     */
    public Long getSeconds() {
        return seconds;
    }

    /**
     * Возвращает количество дней
     *
     * @return количество дней
     */
    public Double getDays() {
        return days;
    }

    /**
     * Возвращает количество недель
     *
     * @return количество недель
     */
    public Double getWeeks() {
        return weeks;
    }

    /**
     * Возвращает количество месяцев
     *
     * @return количество месяцев
     */
    public Double getMonths() {
        return months;
    }

    /**
     * Возвращает количество годов
     *
     * @return количество годов
     */
    public Double getYears() {
        return years;
    }

    /**
     * Возвращает время
     *
     * @return время
     */
    public Long getTime() {
        return time;
    }

    /**
     * Возвращает совокупность
     *
     * @param n число
     * @return совокупность
     */
    private Integer plurals(Double n) {
        if (n == 0) return 0;
        if (n != n.longValue()) return 2;
        n = Math.abs(n) % 100;
        Long n1 = n.longValue() % 10;
        if (n > 10 && n < 20) return 5;
        if (n1 > 1 && n1 < 5) return 2;
        if (n1 == 1) return 1;
        return 5;
    }

    /**
     * Возвращает строковое представление даты
     *
     * @return строковое представление
     * @throws GranException при необходимости
     */
    public String getString() throws GranException {
        // специально для русского языка:
        Integer year = plurals(getYears());
        Integer month = plurals(getMonths());
        Integer week = plurals(getWeeks());
        Integer day = plurals(getDays());
        Integer hour = plurals(getHours());
        Integer minute = plurals(getMinutes());
        Integer second = plurals(getSeconds().doubleValue());
        return I18n.getString(this.locale, "BUDGET_FORMAT", new Object[]{getYears(), getMonths(), getWeeks(), getDays(), getHours(), getMinutes(), getSeconds(), year, month, week, day, hour, minute, second});
    }


}