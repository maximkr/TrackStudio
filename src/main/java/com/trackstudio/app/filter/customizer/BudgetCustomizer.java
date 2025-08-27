package com.trackstudio.app.filter.customizer;

import javax.servlet.http.HttpServletRequest;

import com.trackstudio.app.filter.FValue;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.common.FieldMap;
import com.trackstudio.exception.GranException;
import com.trackstudio.startup.Config;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.Null;
import com.trackstudio.tools.formatter.HourFormatter;
import com.trackstudio.tools.textfilter.HTMLEncoder;

import net.jcip.annotations.NotThreadSafe;

import static com.trackstudio.constants.CategoryConstants.*;

/**
 * Кастомный вывод бюджета (для вывода в фильтрах)
 */
@NotThreadSafe
public class BudgetCustomizer extends Customizer {

    /**
     * Формат бюджета
     */
    protected String format;
    /**
     * пользовательское поле или нет
     */
    protected boolean isUdf;

    //значения по умолчанию, но на самом деле мы их будем брать
    //из файла свойств
    /**
     * Колличество рабочих часов в день
     */
    public static int HOURS_IN_DAY = 8;
    /**
     * Колличество рабочих дней в неделе
     */
    public static int DAYS_IN_WEEK = 5;
    /**
     * Колличество рабочих вней в месяце
     */
    public static int DAYS_IN_MONTH = 22;
    /**
     * Колличество рабочих часов в году
     */
    public static int HOURS_IN_YEAR = 2000;
    /**
     * Колличество рабочих месяцев в году
     */
    public static int MONTHS_IN_YEAR = 12;
    /**
     * Пполный паттерн по умолчанию
     */
    public static final String ALL = "YMWDhms";

    /**
     * Конструктор по умолчанию
     *
     * @param format формат вывода бюджета
     * @param map    карта полей
     */
    public BudgetCustomizer(String format, FieldMap map) {
        super(map);
        initConstants();
        this.format = format;
        isUdf = map.getFilterKey() != null && map.getFilterKey().startsWith(FValue.UDF);
    }

    /**
     * Инициализация констант
     */
    public static void initConstants() {
        if (Config.getInstance().getProperty("trackstudio.hoursInDay") != null)
            HOURS_IN_DAY = Integer.parseInt(Config.getInstance().getProperty("trackstudio.hoursInDay"));
        if (Config.getInstance().getProperty("trackstudio.daysInWeek") != null)
            DAYS_IN_WEEK = Integer.parseInt(Config.getInstance().getProperty("trackstudio.daysInWeek"));
        if (Config.getInstance().getProperty("trackstudio.daysInMonth") != null)
            DAYS_IN_MONTH = Integer.parseInt(Config.getInstance().getProperty("trackstudio.daysInMonth"));
        if (Config.getInstance().getProperty("trackstudio.hoursInYear") != null)
            HOURS_IN_YEAR = Integer.parseInt(Config.getInstance().getProperty("trackstudio.hoursInYear"));
        if (Config.getInstance().getProperty("trackstudio.monthsInYear") != null)
            MONTHS_IN_YEAR = Integer.parseInt(Config.getInstance().getProperty("trackstudio.monthsInYear"));
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
        StringBuffer outp = new StringBuffer(200);
        //ArrayList<String> sortdorderList = filter.getOriginValues(FValue.SORTORDER);
        outp.append("<td style=\"vertical-align: middle\"><label for=\"").append(Null.stripNullText(map.getFilterKey())).append("\">").append(HTMLEncoder.encode(I18n.getString(sc, map.getAltKey()))).append("</label></td>\n");
        outp.append(drawInput(sc, filter, contextPath));
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
        String valueYears = checkedParam(request.getParameter(map.getFilterKey() + "_YEARS"));
        String valueMonths = checkedParam(request.getParameter(map.getFilterKey() + "_MONTHS"));
        String valueWeeks = checkedParam(request.getParameter(map.getFilterKey() + "_WEEKS"));
        String valueDays = checkedParam(request.getParameter(map.getFilterKey() + "_DAYS"));
        String valueHrs = checkedParam(request.getParameter(map.getFilterKey() + "_HRS"));
        String valueMns = checkedParam(request.getParameter(map.getFilterKey() + "_MNS"));
        String valueSec = checkedParam(request.getParameter(map.getFilterKey() + "_SEC"));


        Long value = HourFormatter.parseInput(valueYears, valueMonths, valueWeeks, valueDays, valueHrs, valueMns, valueSec);

        Object avalue = request.getParameter(FValue.SUB + map.getFilterKey());
        avalue = (avalue == null) ? "" : avalue;
        if (value > 0.00001f && avalue != null) {
            filter.putItem(map.getFilterKey(), (String) avalue + value);
        } else {
            filter.remove(map.getFilterKey());
        }
    }

    private String checkedParam(String param) {
        return param != null && param.length() > 0 ? param : param;
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
        String value = HTMLEncoder.encode(filter.getAsString(map.getFilterKey()));
        String prefix = filter.getPrefix(map.getFilterKey());
//        String defVal = value;
        StringBuffer outp = new StringBuffer(200);

        outp.append("<td  style=\"vertical-align: middle; white-space: nowrap\">\n");
        outp.append("<div class=\"budget\">\n");
        outp.append("<table>\n<tr>");
        outp.append("<td style=\"vertical-align: middle\" rowspan=\"2\"><select ").append(" name=\"_").append(map.getFilterKey()).append("\">");

        outp.append("<option ").append(value == null ? "selected" : "").append(" value=\"\">&gt;=");
        outp.append("<option ").append(value != null && prefix.equals(FValue.SUB) ? "selected" : "").append(" value=\"_\">&lt;=");
        outp.append("<option ").append(value != null && prefix.equals(FValue.NE) ? "selected" : "").append(" value=\"").append(FValue.NE).append("\">&gt;&lt;");
        outp.append("<option ").append(value != null && prefix.equals(FValue.EQ) ? "selected" : "").append(" value=\"").append(FValue.EQ).append("\">=");
//        outp.append("<option ").append(value != null && prefix.equals(FValue.IN) ? "selected" : "").append(" value=\"").append(FValue.IN).append("\">").append(I18n.getString(locale, "IN_SET"));
        outp.append("</select>\n");
        outp.append("</td>\n");

        HourFormatter hf = new HourFormatter(value != null && value.length() > 0 ? Long.valueOf(value) : 0L, format, "en");
        /*DoubleHourFormatter hf = new DoubleHourFormatter(defVal != null ? Double.valueOf(defVal) : 0d, format);*/

        outp.append("<td style=\"vertical-align: middle\">\n");
        if (format.indexOf(Y) > -1) {
            outp.append("<input class=\"budget\" id=\"years\" type=\"text\" ").append(" alt=\"float\"" + " name=\"").append(map.getFilterKey()).append("_YEARS\" value=\"").append(hf.getYears()).append("\" size=\"3\" maxLength=\"4\"><label for=\"years\">").append(I18n.getString(locale, "BUDGET_YEARS")).append("</label>");
        }
        outp.append("</td>\n");
        outp.append("<td style=\"vertical-align: middle\">\n");
        if (format.indexOf(M) > -1) {
            outp.append("<input class=\"budget\" id=\"months\" type=\"text\" ").append(" alt=\"float\"" + " name=\"").append(map.getFilterKey()).append("_MONTHS\" value=\"").append(hf.getMonths()).append("\" size=\"3\" maxLength=\"4\"><label for=\"months\">").append(I18n.getString(locale, "BUDGET_MONTHS")).append("</label>");
        }
        outp.append("</td>\n");
        outp.append("<td style=\"vertical-align: middle\">\n");
        if (format.indexOf(W) > -1) {
            outp.append("<input class=\"budget\" id=\"weeks\" type=\"text\" ").append(" alt=\"float\"" + " name=\"").append(map.getFilterKey()).append("_WEEKS\" value=\"").append(hf.getWeeks()).append("\" size=\"3\" maxLength=\"4\"><label for=\"weeks\">").append(I18n.getString(locale, "BUDGET_WEEKS")).append("</label>");
        }
        outp.append("</td>\n");
        outp.append("<td style=\"vertical-align: middle\">\n");
        if (format.indexOf(D) > -1) {
            outp.append("<input class=\"budget\" id=\"days\" type=\"text\" ").append(" alt=\"float\"" + " name=\"").append(map.getFilterKey()).append("_DAYS\" value=\"").append(hf.getDays()).append("\" size=\"3\" maxLength=\"4\"><label for=\"days\">").append(I18n.getString(locale, "BUDGET_DAYS")).append("</label>");
        }
        outp.append("</td>\n");
        outp.append("</tr>\n");
        outp.append("<tr>\n");
        outp.append("<td style=\"vertical-align: middle\">\n");
        outp.append("</td>\n");
        outp.append("<td style=\"vertical-align: middle\">\n");
        if (format.indexOf(h) > -1) {
            outp.append("<input class=\"budget\" id=\"hours\" type=\"text\" ").append(" alt=\"float\"" + " name=\"").append(map.getFilterKey()).append("_HRS\" value=\"").append(hf.getHours()).append("\" size=\"3\" maxLength=\"4\"><label for=\"hours\">").append(I18n.getString(locale, "BUDGET_HOURS")).append("</label>");
        }
        outp.append("</td>\n");
        outp.append("<td style=\"vertical-align: middle\">\n");
        if (format.indexOf(m) > -1) {
            outp.append("<input class=\"budget\" id=\"mins\" type=\"text\" ").append(" alt=\"float\"" + " name=\"").append(map.getFilterKey()).append("_MNS\" value=\"").append(hf.getMinutes()).append("\" size=\"3\" maxLength=\"4\"><label for=\"mins\">").append(I18n.getString(locale, "BUDGET_MINUTES")).append("</label>");
        }
        outp.append("</td>\n");
        outp.append("<td style=\"vertical-align: middle\">\n");
        if (format.indexOf(s) > -1) {

            outp.append("<input class=\"budget\" id=\"secs\" type=\"text\" ").append(" alt=\"natural\"" + " name=\"").append(map.getFilterKey()).append("_SEC\" value=\"").append(hf.getSeconds()).append("\" size=\"3\" maxLength=\"4\"><label for=\"secs\">").append(I18n.getString(locale, "BUDGET_SECONDS")).append("</label>");
        }
        outp.append("</td>\n");
        outp.append("</tr></table></div></td>\n");
        return outp.toString();
    }
}
