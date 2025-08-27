package com.trackstudio.app.filter.customizer;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.servlet.http.HttpServletRequest;

import com.trackstudio.action.GeneralAction;
import com.trackstudio.app.filter.FValue;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.common.FieldMap;
import com.trackstudio.exception.GranException;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.Null;
import com.trackstudio.tools.formatter.DateFormatter;

import net.jcip.annotations.NotThreadSafe;

/**
 * Кастомный вывод даты (для вывода в фильтрах)
 */
@NotThreadSafe
public class DateCustomizer extends Customizer implements Serializable {

    private DateFormatter df = null;

    /**
     * Конструктор по умолчанию
     *
     * @param map карта полей
     * @param d   форматтер даты
     */
    public DateCustomizer(FieldMap map, DateFormatter d) {
        super(map);
        this.df = d;
    }

    /**
     * Возвращает форматтер даты
     *
     * @return форматтер даты
     */
    private DateFormatter getDateFormatter() {
        return this.df;
    }

    /**
     * Метод для вывода информативного блока о полях
     *
     * @param sc          сессия пользователя
     * @param filter      фильтр
     * @param contextPath относительный путь контекста
     * @return строка вывода
     * @throws GranException при необзодимости
     */
    public String draw(SessionContext sc, FValue filter, String contextPath) throws GranException {
        return drawForDateOrPopUp(sc, "hide", filter, contextPath);
    }

    /**
     * Метод для вывода блока ввода для полей
     *
     * @param sc          сессия пользователя
     * @param filter      фильтр
     * @param contextPath относительный путь контекста
     * @return строка вывода
     * @throws GranException при необзодимости
     */
    public String drawInput(SessionContext sc, FValue filter, String contextPath) throws GranException {
        String locale = sc.getLocale();
        StringBuffer outp = new StringBuffer(200);

        String fromdef = filter.getAsString(FValue.SUB + map.getFilterKey());
        String todef = filter.getAsString(map.getFilterKey());
        String amnt = filter.getAsString(FValue.AMNT + map.getFilterKey());
        String period = filter.getAsString(FValue.PERIOD + map.getFilterKey());
        String interval = filter.getAsString(FValue.INTERVAL + map.getFilterKey());
        String ba = filter.getAsString(FValue.BA + map.getFilterKey());
        String el = filter.getAsString(FValue.EL + map.getFilterKey());
        /*используется для дорисовки значений в кастом полях и deadline*/
        boolean periodNext = "deadline".equals(map.getFilterKey()) || map.getFilterKey().startsWith("UDF");

        if (amnt == null)
            amnt = "";

        if (interval == null)
            interval = "0";

        if (period == null)
            period = "0";

        if (ba == null)
            ba = "0";

        if (el == null)
            el = "0";
        String fromdefStr = "";

        if (fromdef != null)
            try {
                fromdefStr = df.parse(new Timestamp((Long.valueOf(fromdef))));
            } catch (Exception e) {
                throw new GranException(e);
            }
        String todefStr = "";

        if (todef != null) {
            try {
                todefStr = df.parse(new Timestamp((Long.valueOf(todef))));
            } catch (Exception e) {
                throw new GranException(e);
            }
        }
        String imageServlet = "/ImageServlet/" + GeneralAction.SERVLET_KEY;
        outp.append("<td style=\"white-space: nowrap; vertical-align : middle\">\n");
        outp.append("<div class=\"budget\" style=\"white-space: nowrap; \">\n");
        outp.append("<label for=\"_").append(map.getFilterKey()).append("\">").append(I18n.getString(locale, "FROM")).append("</label>\n");
        outp.append("<input ").append("  alt=\"date(").append(df.getPattern2()).append(")\"  type=\"text\" id=\"_").append(map.getFilterKey()).append("\" name=\"_").append(map.getFilterKey()).append("\" value=\"").append(Null.stripNullText(fromdefStr)).append("\"><img src=\"").append(contextPath).append(imageServlet).append("/cssimages/ico.calendar.gif" + "\" border=\"0\" alt=\"").append(I18n.getString(locale, "SELECT_DATE")).append("\" style=\"cursor:pointer; \" onclick=\"return showCalendar('_").append(map.getFilterKey()).append("', '").append(sc.getUser().getDateFormatter().getPattern2()).append("', '24', true);\">\n");
        outp.append("<label for=\"").append(map.getFilterKey()).append("\">").append(I18n.getString(locale, "TO")).append("</label>\n");
        outp.append("<input alt=\"date(").append(df.getPattern2()).append(")\"  type=\"text\" id=\"").append(map.getFilterKey()).append("\" name=\"").append(map.getFilterKey()).append("\" value=\"").append(Null.stripNullText(todefStr)).append("\"><img src=\"").append(contextPath).append(imageServlet).append("/cssimages/ico.calendar.gif" + "\" border=\"0\" alt=\"").append(I18n.getString(locale, "SELECT_DATE")).append("\" style=\"cursor:pointer; \" onclick=\"return showCalendar('").append(map.getFilterKey()).append("', '").append(sc.getUser().getDateFormatter().getPattern2()).append("', '24', true);\">\n");
        outp.append("</div>\n");
        outp.append("<div class=\"budget\" style=\"white-space: nowrap; \">\n");
        // количество
        outp.append("<input maxlength='8' type=\"text\" style=\"width: 35px\" alt=\"natural\"" + " name=\"amnt_").append(map.getFilterKey()).append("\" value=\"").append(amnt).append("\">\n");

        // часов/месяцев/дней
        outp.append("<select  name=\"interval_").append(map.getFilterKey()).append("\">\n");

        if (interval.equals("0")) {
            outp.append("<option selected value=\"0\">").append(I18n.getString(locale, "MINUTES")).append("</option>\n");
            outp.append("<option value=\"1\">").append(I18n.getString(locale, "HOURS")).append("</option>\n");
            outp.append("<option value=\"2\">").append(I18n.getString(locale, "DAY")).append("</option>\n");
            outp.append("<option value=\"3\">").append(I18n.getString(locale, "MONTH")).append("</option>\n");
        }
        if (interval.equals("1")) {
            outp.append("<option value=\"0\">").append(I18n.getString(locale, "MINUTES")).append("</option>\n");
            outp.append("<option selected value=\"1\">").append(I18n.getString(locale, "HOURS")).append("</option>\n");
            outp.append("<option value=\"2\">").append(I18n.getString(locale, "DAY")).append("</option>\n");
            outp.append("<option value=\"3\">").append(I18n.getString(locale, "MONTH")).append("</option>\n");
        }
        if (interval.equals("2")) {
            outp.append("<option value=\"0\">").append(I18n.getString(locale, "MINUTES")).append("</option>\n");
            outp.append("<option value=\"1\">").append(I18n.getString(locale, "HOURS")).append("</option>\n");
            outp.append("<option selected value=\"2\">").append(I18n.getString(locale, "DAY")).append("</option>\n");
            outp.append("<option value=\"3\">").append(I18n.getString(locale, "MONTH")).append("</option>\n");
        }
        if (interval.equals("3")) {
            outp.append("<option value=\"0\">").append(I18n.getString(locale, "MINUTES")).append("</option>\n");
            outp.append("<option value=\"1\">").append(I18n.getString(locale, "HOURS")).append("</option>\n");
            outp.append("<option value=\"2\">").append(I18n.getString(locale, "DAY")).append("</option>\n");
            outp.append("<option selected value=\"3\">").append(I18n.getString(locale, "MONTH")).append("</option>\n");
        }

        outp.append("</select>\n");

        // до/после
        outp.append("<select  name=\"ba_").append(map.getFilterKey()).append("\">");
        if (ba.equals("0")) {
            outp.append("<option selected value=\"0\">").append(I18n.getString(locale, "BEFORE")).append("</option>");
            outp.append("<option value=\"1\">").append(I18n.getString(locale, "AFTER")).append("</option>");
        }
        if (ba.equals("1")) {
            outp.append("<option value=\"0\">").append(I18n.getString(locale, "BEFORE")).append("</option>");
            outp.append("<option selected value=\"1\">").append(I18n.getString(locale, "AFTER")).append("</option>");
        }
        outp.append("</select>\n");

        // or earlier / or later
        outp.append("<select name=\"el_").append(map.getFilterKey()).append("\">");
        if (el.equals("0")) {
            outp.append("<option selected value=\"0\">").append(I18n.getString(locale, "EARLIER")).append("</option>");
            outp.append("<option value=\"1\">").append(I18n.getString(locale, "LATER")).append("</option>");
        }
        if (el.equals("1")) {
            outp.append("<option value=\"0\">").append(I18n.getString(locale, "EARLIER")).append("</option>");
            outp.append("<option selected value=\"1\">").append(I18n.getString(locale, "LATER")).append("</option>");
        }
        outp.append("</select>\n");
        outp.append("</div>\n");

        outp.append("<div class=\"budget\" style=\"white-space: nowrap; \">\n");
        outp.append("<label for=\"_").append(map.getFilterKey()).append("\">").append(I18n.getString(locale, "PERIOD")).append("</label>\n");
        // период
        outp.append("<select  name=\"period_").append(map.getFilterKey()).append("\">");
        if (period.equals("0")) {
            outp.append("<option selected value=\"0\">").append(I18n.getString(locale, "NOT_CHOOSEN")).append("</option>\n");
            outp.append("<option value=\"1\">").append(I18n.getString(locale, "FOR_CURRENT_WEEK")).append("</option>\n");
            outp.append("<option value=\"2\">").append(I18n.getString(locale, "FOR_CURRENT_MONTH")).append("</option>\n");
            outp.append("<option value=\"3\">").append(I18n.getString(locale, "FOR_CURRENT_YEAR")).append("</option>\n");
            outp.append("<option value=\"4\">").append(I18n.getString(locale, "FOR_CURRENT_QUARTER")).append("</option>\n");
            outp.append("<option value=\"5\">").append(I18n.getString(locale, "FOR_PREVIOUS_WEEK")).append("</option>\n");
            outp.append("<option value=\"6\">").append(I18n.getString(locale, "FOR_PREVIOUS_MONTH")).append("</option>\n");
            outp.append("<option value=\"7\">").append(I18n.getString(locale, "FOR_PREVIOUS_YEAR")).append("</option>\n");
            outp.append("<option value=\"8\">").append(I18n.getString(locale, "FOR_PREVIOUS_QUARTER")).append("</option>\n");
            if (periodNext) {
                outp.append("<option value=\"9\">").append(I18n.getString(locale, "FOR_NEXT_WEEK")).append("</option>\n");
                outp.append("<option value=\"10\">").append(I18n.getString(locale, "FOR_NEXT_MONTH")).append("</option>\n");
                outp.append("<option value=\"11\">").append(I18n.getString(locale, "FOR_NEXT_YEAR")).append("</option>\n");
                outp.append("<option value=\"12\">").append(I18n.getString(locale, "FOR_NEXT_QUARTER")).append("</option>\n");
            }
        }
        if (period.equals("1")) {
            outp.append("<option value=\"0\">").append(I18n.getString(locale, "NOT_CHOOSEN")).append("</option>\n");
            outp.append("<option selected value=\"1\">").append(I18n.getString(locale, "FOR_CURRENT_WEEK")).append("</option>\n");
            outp.append("<option value=\"2\">").append(I18n.getString(locale, "FOR_CURRENT_MONTH")).append("</option>\n");
            outp.append("<option value=\"3\">").append(I18n.getString(locale, "FOR_CURRENT_YEAR")).append("</option>\n");
            outp.append("<option value=\"4\">").append(I18n.getString(locale, "FOR_CURRENT_QUARTER")).append("</option>\n");
            outp.append("<option value=\"5\">").append(I18n.getString(locale, "FOR_PREVIOUS_WEEK")).append("</option>\n");
            outp.append("<option value=\"6\">").append(I18n.getString(locale, "FOR_PREVIOUS_MONTH")).append("</option>\n");
            outp.append("<option value=\"7\">").append(I18n.getString(locale, "FOR_PREVIOUS_YEAR")).append("</option>\n");
            outp.append("<option value=\"8\">").append(I18n.getString(locale, "FOR_PREVIOUS_QUARTER")).append("</option>\n");
            if (periodNext) {
                outp.append("<option value=\"9\">").append(I18n.getString(locale, "FOR_NEXT_WEEK")).append("</option>\n");
                outp.append("<option value=\"10\">").append(I18n.getString(locale, "FOR_NEXT_MONTH")).append("</option>\n");
                outp.append("<option value=\"11\">").append(I18n.getString(locale, "FOR_NEXT_YEAR")).append("</option>\n");
                outp.append("<option value=\"12\">").append(I18n.getString(locale, "FOR_NEXT_QUARTER")).append("</option>\n");
            }
        }
        if (period.equals("2")) {
            outp.append("<option value=\"0\">").append(I18n.getString(locale, "NOT_CHOOSEN")).append("</option>\n");
            outp.append("<option value=\"1\">").append(I18n.getString(locale, "FOR_CURRENT_WEEK")).append("</option>\n");
            outp.append("<option selected value=\"2\">").append(I18n.getString(locale, "FOR_CURRENT_MONTH")).append("</option>\n");
            outp.append("<option value=\"3\">").append(I18n.getString(locale, "FOR_CURRENT_YEAR")).append("</option>\n");
            outp.append("<option value=\"4\">").append(I18n.getString(locale, "FOR_CURRENT_QUARTER")).append("</option>\n");
            outp.append("<option value=\"5\">").append(I18n.getString(locale, "FOR_PREVIOUS_WEEK")).append("</option>\n");
            outp.append("<option value=\"6\">").append(I18n.getString(locale, "FOR_PREVIOUS_MONTH")).append("</option>\n");
            outp.append("<option value=\"7\">").append(I18n.getString(locale, "FOR_PREVIOUS_YEAR")).append("</option>\n");
            outp.append("<option value=\"8\">").append(I18n.getString(locale, "FOR_PREVIOUS_QUARTER")).append("</option>\n");
            if (periodNext) {
                outp.append("<option value=\"9\">").append(I18n.getString(locale, "FOR_NEXT_WEEK")).append("</option>\n");
                outp.append("<option value=\"10\">").append(I18n.getString(locale, "FOR_NEXT_MONTH")).append("</option>\n");
                outp.append("<option value=\"11\">").append(I18n.getString(locale, "FOR_NEXT_YEAR")).append("</option>\n");
                outp.append("<option value=\"12\">").append(I18n.getString(locale, "FOR_NEXT_QUARTER")).append("</option>\n");
            }
        }
        if (period.equals("3")) {
            outp.append("<option value=\"0\">").append(I18n.getString(locale, "NOT_CHOOSEN")).append("</option>\n");
            outp.append("<option value=\"1\">").append(I18n.getString(locale, "FOR_CURRENT_WEEK")).append("</option>\n");
            outp.append("<option value=\"2\">").append(I18n.getString(locale, "FOR_CURRENT_MONTH")).append("</option>\n");
            outp.append("<option selected value=\"3\">").append(I18n.getString(locale, "FOR_CURRENT_YEAR")).append("</option>\n");
            outp.append("<option value=\"4\">").append(I18n.getString(locale, "FOR_CURRENT_QUARTER")).append("</option>\n");
            outp.append("<option value=\"5\">").append(I18n.getString(locale, "FOR_PREVIOUS_WEEK")).append("</option>\n");
            outp.append("<option value=\"6\">").append(I18n.getString(locale, "FOR_PREVIOUS_MONTH")).append("</option>\n");
            outp.append("<option value=\"7\">").append(I18n.getString(locale, "FOR_PREVIOUS_YEAR")).append("</option>\n");
            outp.append("<option value=\"8\">").append(I18n.getString(locale, "FOR_PREVIOUS_QUARTER")).append("</option>\n");
            if (periodNext) {
                outp.append("<option value=\"9\">").append(I18n.getString(locale, "FOR_NEXT_WEEK")).append("</option>\n");
                outp.append("<option value=\"10\">").append(I18n.getString(locale, "FOR_NEXT_MONTH")).append("</option>\n");
                outp.append("<option value=\"11\">").append(I18n.getString(locale, "FOR_NEXT_YEAR")).append("</option>\n");
                outp.append("<option value=\"12\">").append(I18n.getString(locale, "FOR_NEXT_QUARTER")).append("</option>\n");
            }
        }
        if (period.equals("4")) {
            outp.append("<option value=\"0\">").append(I18n.getString(locale, "NOT_CHOOSEN")).append("</option>\n");
            outp.append("<option value=\"1\">").append(I18n.getString(locale, "FOR_CURRENT_WEEK")).append("</option>\n");
            outp.append("<option value=\"2\">").append(I18n.getString(locale, "FOR_CURRENT_MONTH")).append("</option>\n");
            outp.append("<option value=\"3\">").append(I18n.getString(locale, "FOR_CURRENT_YEAR")).append("</option>\n");
            outp.append("<option selected value=\"4\">").append(I18n.getString(locale, "FOR_CURRENT_QUARTER")).append("</option>\n");
            outp.append("<option value=\"5\">").append(I18n.getString(locale, "FOR_PREVIOUS_WEEK")).append("</option>\n");
            outp.append("<option value=\"6\">").append(I18n.getString(locale, "FOR_PREVIOUS_MONTH")).append("</option>\n");
            outp.append("<option value=\"7\">").append(I18n.getString(locale, "FOR_PREVIOUS_YEAR")).append("</option>\n");
            outp.append("<option value=\"8\">").append(I18n.getString(locale, "FOR_PREVIOUS_QUARTER")).append("</option>\n");
            if (periodNext) {
                outp.append("<option value=\"9\">").append(I18n.getString(locale, "FOR_NEXT_WEEK")).append("</option>\n");
                outp.append("<option value=\"10\">").append(I18n.getString(locale, "FOR_NEXT_MONTH")).append("</option>\n");
                outp.append("<option value=\"11\">").append(I18n.getString(locale, "FOR_NEXT_YEAR")).append("</option>\n");
                outp.append("<option value=\"12\">").append(I18n.getString(locale, "FOR_NEXT_QUARTER")).append("</option>\n");
            }
        }
        if (period.equals("5")) {
            outp.append("<option value=\"0\">").append(I18n.getString(locale, "NOT_CHOOSEN")).append("</option>\n");
            outp.append("<option value=\"1\">").append(I18n.getString(locale, "FOR_CURRENT_WEEK")).append("</option>\n");
            outp.append("<option value=\"2\">").append(I18n.getString(locale, "FOR_CURRENT_MONTH")).append("</option>\n");
            outp.append("<option value=\"3\">").append(I18n.getString(locale, "FOR_CURRENT_YEAR")).append("</option>\n");
            outp.append("<option value=\"4\">").append(I18n.getString(locale, "FOR_CURRENT_QUARTER")).append("</option>\n");
            outp.append("<option selected value=\"5\">").append(I18n.getString(locale, "FOR_PREVIOUS_WEEK")).append("</option>\n");
            outp.append("<option value=\"6\">").append(I18n.getString(locale, "FOR_PREVIOUS_MONTH")).append("</option>\n");
            outp.append("<option value=\"7\">").append(I18n.getString(locale, "FOR_PREVIOUS_YEAR")).append("</option>\n");
            outp.append("<option value=\"8\">").append(I18n.getString(locale, "FOR_PREVIOUS_QUARTER")).append("</option>\n");
            if (periodNext) {
                outp.append("<option value=\"9\">").append(I18n.getString(locale, "FOR_NEXT_WEEK")).append("</option>\n");
                outp.append("<option value=\"10\">").append(I18n.getString(locale, "FOR_NEXT_MONTH")).append("</option>\n");
                outp.append("<option value=\"11\">").append(I18n.getString(locale, "FOR_NEXT_YEAR")).append("</option>\n");
                outp.append("<option value=\"12\">").append(I18n.getString(locale, "FOR_NEXT_QUARTER")).append("</option>\n");
            }
        }
        if (period.equals("6")) {
            outp.append("<option value=\"0\">").append(I18n.getString(locale, "NOT_CHOOSEN")).append("</option>\n");
            outp.append("<option value=\"1\">").append(I18n.getString(locale, "FOR_CURRENT_WEEK")).append("</option>\n");
            outp.append("<option value=\"2\">").append(I18n.getString(locale, "FOR_CURRENT_MONTH")).append("</option>\n");
            outp.append("<option value=\"3\">").append(I18n.getString(locale, "FOR_CURRENT_YEAR")).append("</option>\n");
            outp.append("<option value=\"4\">").append(I18n.getString(locale, "FOR_CURRENT_QUARTER")).append("</option>\n");
            outp.append("<option value=\"5\">").append(I18n.getString(locale, "FOR_PREVIOUS_WEEK")).append("</option>\n");
            outp.append("<option selected value=\"6\">").append(I18n.getString(locale, "FOR_PREVIOUS_MONTH")).append("</option>\n");
            outp.append("<option value=\"7\">").append(I18n.getString(locale, "FOR_PREVIOUS_YEAR")).append("</option>\n");
            outp.append("<option value=\"8\">").append(I18n.getString(locale, "FOR_PREVIOUS_QUARTER")).append("</option>\n");
            if (periodNext) {
                outp.append("<option value=\"9\">").append(I18n.getString(locale, "FOR_NEXT_WEEK")).append("</option>\n");
                outp.append("<option value=\"10\">").append(I18n.getString(locale, "FOR_NEXT_MONTH")).append("</option>\n");
                outp.append("<option value=\"11\">").append(I18n.getString(locale, "FOR_NEXT_YEAR")).append("</option>\n");
                outp.append("<option value=\"12\">").append(I18n.getString(locale, "FOR_NEXT_QUARTER")).append("</option>\n");
            }
        }
        if (period.equals("7")) {
            outp.append("<option value=\"0\">").append(I18n.getString(locale, "NOT_CHOOSEN")).append("</option>\n");
            outp.append("<option value=\"1\">").append(I18n.getString(locale, "FOR_CURRENT_WEEK")).append("</option>\n");
            outp.append("<option value=\"2\">").append(I18n.getString(locale, "FOR_CURRENT_MONTH")).append("</option>\n");
            outp.append("<option value=\"3\">").append(I18n.getString(locale, "FOR_CURRENT_YEAR")).append("</option>\n");
            outp.append("<option value=\"4\">").append(I18n.getString(locale, "FOR_CURRENT_QUARTER")).append("</option>\n");
            outp.append("<option value=\"5\">").append(I18n.getString(locale, "FOR_PREVIOUS_WEEK")).append("</option>\n");
            outp.append("<option value=\"6\">").append(I18n.getString(locale, "FOR_PREVIOUS_MONTH")).append("</option>\n");
            outp.append("<option selected value=\"7\">").append(I18n.getString(locale, "FOR_PREVIOUS_YEAR")).append("</option>\n");
            outp.append("<option value=\"8\">").append(I18n.getString(locale, "FOR_PREVIOUS_QUARTER")).append("</option>\n");
            if (periodNext) {
                outp.append("<option value=\"9\">").append(I18n.getString(locale, "FOR_NEXT_WEEK")).append("</option>\n");
                outp.append("<option value=\"10\">").append(I18n.getString(locale, "FOR_NEXT_MONTH")).append("</option>\n");
                outp.append("<option value=\"11\">").append(I18n.getString(locale, "FOR_NEXT_YEAR")).append("</option>\n");
                outp.append("<option value=\"12\">").append(I18n.getString(locale, "FOR_NEXT_QUARTER")).append("</option>\n");
            }
        }
        if (period.equals("8")) {
            outp.append("<option value=\"0\">").append(I18n.getString(locale, "NOT_CHOOSEN")).append("</option>\n");
            outp.append("<option value=\"1\">").append(I18n.getString(locale, "FOR_CURRENT_WEEK")).append("</option>\n");
            outp.append("<option value=\"2\">").append(I18n.getString(locale, "FOR_CURRENT_MONTH")).append("</option>\n");
            outp.append("<option value=\"3\">").append(I18n.getString(locale, "FOR_CURRENT_YEAR")).append("</option>\n");
            outp.append("<option value=\"4\">").append(I18n.getString(locale, "FOR_CURRENT_QUARTER")).append("</option>\n");
            outp.append("<option value=\"5\">").append(I18n.getString(locale, "FOR_PREVIOUS_WEEK")).append("</option>\n");
            outp.append("<option value=\"6\">").append(I18n.getString(locale, "FOR_PREVIOUS_MONTH")).append("</option>\n");
            outp.append("<option value=\"7\">").append(I18n.getString(locale, "FOR_PREVIOUS_YEAR")).append("</option>\n");
            outp.append("<option selected value=\"8\">").append(I18n.getString(locale, "FOR_PREVIOUS_QUARTER")).append("</option>\n");
            if (periodNext) {
                outp.append("<option value=\"9\">").append(I18n.getString(locale, "FOR_NEXT_WEEK")).append("</option>\n");
                outp.append("<option value=\"10\">").append(I18n.getString(locale, "FOR_NEXT_MONTH")).append("</option>\n");
                outp.append("<option value=\"11\">").append(I18n.getString(locale, "FOR_NEXT_YEAR")).append("</option>\n");
                outp.append("<option value=\"12\">").append(I18n.getString(locale, "FOR_NEXT_QUARTER")).append("</option>\n");
            }
        }
        if (period.equals("9")) {
            outp.append("<option value=\"0\">").append(I18n.getString(locale, "NOT_CHOOSEN")).append("</option>\n");
            outp.append("<option value=\"1\">").append(I18n.getString(locale, "FOR_CURRENT_WEEK")).append("</option>\n");
            outp.append("<option value=\"2\">").append(I18n.getString(locale, "FOR_CURRENT_MONTH")).append("</option>\n");
            outp.append("<option value=\"3\">").append(I18n.getString(locale, "FOR_CURRENT_YEAR")).append("</option>\n");
            outp.append("<option value=\"4\">").append(I18n.getString(locale, "FOR_CURRENT_QUARTER")).append("</option>\n");
            outp.append("<option value=\"5\">").append(I18n.getString(locale, "FOR_PREVIOUS_WEEK")).append("</option>\n");
            outp.append("<option value=\"6\">").append(I18n.getString(locale, "FOR_PREVIOUS_MONTH")).append("</option>\n");
            outp.append("<option value=\"7\">").append(I18n.getString(locale, "FOR_PREVIOUS_YEAR")).append("</option>\n");
            outp.append("<option value=\"8\">").append(I18n.getString(locale, "FOR_PREVIOUS_QUARTER")).append("</option>\n");
            if (periodNext) {
                outp.append("<option selected value=\"9\">").append(I18n.getString(locale, "FOR_NEXT_WEEK")).append("</option>\n");
                outp.append("<option value=\"10\">").append(I18n.getString(locale, "FOR_NEXT_MONTH")).append("</option>\n");
                outp.append("<option value=\"11\">").append(I18n.getString(locale, "FOR_NEXT_YEAR")).append("</option>\n");
                outp.append("<option value=\"12\">").append(I18n.getString(locale, "FOR_NEXT_QUARTER")).append("</option>\n");
            }
        }
        if (period.equals("10")) {
            outp.append("<option value=\"0\">").append(I18n.getString(locale, "NOT_CHOOSEN")).append("</option>\n");
            outp.append("<option value=\"1\">").append(I18n.getString(locale, "FOR_CURRENT_WEEK")).append("</option>\n");
            outp.append("<option value=\"2\">").append(I18n.getString(locale, "FOR_CURRENT_MONTH")).append("</option>\n");
            outp.append("<option value=\"3\">").append(I18n.getString(locale, "FOR_CURRENT_YEAR")).append("</option>\n");
            outp.append("<option value=\"4\">").append(I18n.getString(locale, "FOR_CURRENT_QUARTER")).append("</option>\n");
            outp.append("<option value=\"5\">").append(I18n.getString(locale, "FOR_PREVIOUS_WEEK")).append("</option>\n");
            outp.append("<option value=\"6\">").append(I18n.getString(locale, "FOR_PREVIOUS_MONTH")).append("</option>\n");
            outp.append("<option value=\"7\">").append(I18n.getString(locale, "FOR_PREVIOUS_YEAR")).append("</option>\n");
            outp.append("<option value=\"8\">").append(I18n.getString(locale, "FOR_PREVIOUS_QUARTER")).append("</option>\n");
            if (periodNext) {
                outp.append("<option value=\"9\">").append(I18n.getString(locale, "FOR_NEXT_WEEK")).append("</option>\n");
                outp.append("<option selected value=\"10\">").append(I18n.getString(locale, "FOR_NEXT_MONTH")).append("</option>\n");
                outp.append("<option value=\"11\">").append(I18n.getString(locale, "FOR_NEXT_YEAR")).append("</option>\n");
                outp.append("<option value=\"12\">").append(I18n.getString(locale, "FOR_NEXT_QUARTER")).append("</option>\n");
            }
        }
        if (period.equals("11")) {
            outp.append("<option value=\"0\">").append(I18n.getString(locale, "NOT_CHOOSEN")).append("</option>\n");
            outp.append("<option value=\"1\">").append(I18n.getString(locale, "FOR_CURRENT_WEEK")).append("</option>\n");
            outp.append("<option value=\"2\">").append(I18n.getString(locale, "FOR_CURRENT_MONTH")).append("</option>\n");
            outp.append("<option value=\"3\">").append(I18n.getString(locale, "FOR_CURRENT_YEAR")).append("</option>\n");
            outp.append("<option value=\"4\">").append(I18n.getString(locale, "FOR_CURRENT_QUARTER")).append("</option>\n");
            outp.append("<option value=\"5\">").append(I18n.getString(locale, "FOR_PREVIOUS_WEEK")).append("</option>\n");
            outp.append("<option value=\"6\">").append(I18n.getString(locale, "FOR_PREVIOUS_MONTH")).append("</option>\n");
            outp.append("<option value=\"7\">").append(I18n.getString(locale, "FOR_PREVIOUS_YEAR")).append("</option>\n");
            outp.append("<option value=\"8\">").append(I18n.getString(locale, "FOR_PREVIOUS_QUARTER")).append("</option>\n");
            if (periodNext) {
                outp.append("<option value=\"9\">").append(I18n.getString(locale, "FOR_NEXT_WEEK")).append("</option>\n");
                outp.append("<option value=\"10\">").append(I18n.getString(locale, "FOR_NEXT_MONTH")).append("</option>\n");
                outp.append("<option selected value=\"11\">").append(I18n.getString(locale, "FOR_NEXT_YEAR")).append("</option>\n");
                outp.append("<option value=\"12\">").append(I18n.getString(locale, "FOR_NEXT_QUARTER")).append("</option>\n");
            }
        }
        if (period.equals("12")) {
            outp.append("<option value=\"0\">").append(I18n.getString(locale, "NOT_CHOOSEN")).append("</option>\n");
            outp.append("<option value=\"1\">").append(I18n.getString(locale, "FOR_CURRENT_WEEK")).append("</option>\n");
            outp.append("<option value=\"2\">").append(I18n.getString(locale, "FOR_CURRENT_MONTH")).append("</option>\n");
            outp.append("<option value=\"3\">").append(I18n.getString(locale, "FOR_CURRENT_YEAR")).append("</option>\n");
            outp.append("<option value=\"4\">").append(I18n.getString(locale, "FOR_CURRENT_QUARTER")).append("</option>\n");
            outp.append("<option value=\"5\">").append(I18n.getString(locale, "FOR_PREVIOUS_WEEK")).append("</option>\n");
            outp.append("<option value=\"6\">").append(I18n.getString(locale, "FOR_PREVIOUS_MONTH")).append("</option>\n");
            outp.append("<option value=\"7\">").append(I18n.getString(locale, "FOR_PREVIOUS_YEAR")).append("</option>\n");
            outp.append("<option value=\"8\">").append(I18n.getString(locale, "FOR_PREVIOUS_QUARTER")).append("</option>\n");
            if (periodNext) {
                outp.append("<option value=\"9\">").append(I18n.getString(locale, "FOR_NEXT_WEEK")).append("</option>\n");
                outp.append("<option value=\"10\">").append(I18n.getString(locale, "FOR_NEXT_MONTH")).append("</option>\n");
                outp.append("<option value=\"11\">").append(I18n.getString(locale, "FOR_NEXT_YEAR")).append("</option>\n");
                outp.append("<option selected value=\"12\">").append(I18n.getString(locale, "FOR_NEXT_QUARTER")).append("</option>\n");
            }
        }

        outp.append("</select>");
        outp.append("</div>\n");
        outp.append("</td>\n");
        return outp.toString();
    }

    /**
     * Устанавливает фильтр
     *
     * @param sc      сессия пользователя
     * @param request запрос
     * @param filter  фильтр
     * @throws GranException при необходимости
     */
    public void setFilter(SessionContext sc, HttpServletRequest request, FValue filter) throws GranException {
        filter.remove(map.getFilterKey());
        filter.remove(FValue.PERIOD + map.getFilterKey());
        filter.remove(FValue.SUB + map.getFilterKey());
        filter.remove(FValue.AMNT + map.getFilterKey());
        filter.remove(FValue.INTERVAL + map.getFilterKey());
        filter.remove(FValue.BA + map.getFilterKey());
        filter.remove(FValue.EL + map.getFilterKey());

        Object value = request.getParameter(map.getFilterKey());
        Object avalue = request.getParameter('_' + map.getFilterKey());
        Object valuePeriod = request.getParameter(FValue.PERIOD + map.getFilterKey());

        Object valueAmnt = request.getParameter(FValue.AMNT + map.getFilterKey());
        Object valueInterval = request.getParameter(FValue.INTERVAL + map.getFilterKey());
        Object valueBa = request.getParameter(FValue.BA + map.getFilterKey());
        Object valueEl = request.getParameter(FValue.EL + map.getFilterKey());

        if (value != null)
            if (value.toString().length() != 0)
                filter.putItem(map.getFilterKey(), (new Long(getDateFormatter().parseToCalendar((String) value).getTimeInMillis())).toString());
            else
                filter.remove(map.getFilterKey());

        if (avalue != null)
            if (avalue.toString().length() != 0)
                filter.putItem(FValue.SUB + map.getFilterKey(), (Long.valueOf(getDateFormatter().parseToCalendar((String) avalue).getTimeInMillis())).toString());
            else
                filter.remove(FValue.SUB + map.getFilterKey());

        if (valuePeriod != null && !"0".equals(valuePeriod)) {
            if (valuePeriod.toString().length() != 0) {
                filter.putItem(FValue.PERIOD + map.getFilterKey(), Long.toString(Long.parseLong((String) valuePeriod)));
            }  else {
                filter.remove(FValue.PERIOD + map.getFilterKey());
            }
        }

        if (valueAmnt != null)
            if (valueAmnt.toString().length() != 0)
                filter.putItem(FValue.AMNT + map.getFilterKey(), Long.toString(Long.parseLong((String) valueAmnt)));
            else
                filter.remove(FValue.AMNT + map.getFilterKey());

        if (valueInterval != null)
            if (valueInterval.toString().length() != 0 && valueAmnt != null && valueAmnt.toString().length() != 0)
                filter.putItem(FValue.INTERVAL + map.getFilterKey(), valueInterval.toString());
            else
                filter.remove(FValue.INTERVAL + map.getFilterKey());

        if (valueBa != null)
            if (valueBa.toString().length() != 0 && valueAmnt != null && valueAmnt.toString().length() != 0)
                filter.putItem(FValue.BA + map.getFilterKey(), valueBa.toString());
            else
                filter.remove(FValue.BA + map.getFilterKey());

        if (valueEl != null)
            if (valueEl.toString().length() != 0 && valueAmnt != null && valueAmnt.toString().length() != 0)
                filter.putItem(FValue.EL + map.getFilterKey(), valueEl.toString());
            else
                filter.remove(FValue.EL + map.getFilterKey());
    }
}
