package com.trackstudio.app.filter.customizer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.trackstudio.app.adapter.AdapterManager;
import com.trackstudio.app.filter.FValue;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.common.FieldMap;
import com.trackstudio.exception.GranException;
import com.trackstudio.secured.Secured;
import com.trackstudio.secured.SecuredUDFBean;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.Null;
import com.trackstudio.tools.formatter.HourFormatter;
import com.trackstudio.tools.textfilter.HTMLEncoder;

import net.jcip.annotations.NotThreadSafe;

/**
 * Кастомный вывод текста (для вывода в фильтрах)
 */
@NotThreadSafe
public class TextCustomizer extends Customizer implements Serializable {
    /**
     * Дробное число
     */
    public static final int FLOAT = 0;
    /**
     * Символ
     */
    public static final int CHARS = 1;
    /**
     * кванты
     */
    public static final int QUANTITIES = 3;
    /**
     * Целое число
     */
    public static final int INTEGER = 5;
    /**
     * Натуральное число
     */
    public static final int NATURAL = 6;
    /**
     * Нет фильтрации
     */
    public static final int NOT_FILTERED = 7;

    private int theType = 0;
    private Secured secured = null;//for lookup udf
    private boolean isUdf = false;

    /**
     * Конструктор
     *
     * @param type тип
     * @param map  карта полей
     */
    public TextCustomizer(int type, FieldMap map) {
        super(map);
        this.theType = type;
        isUdf = map.getFilterKey() != null && map.getFilterKey().startsWith(FValue.UDF);
    }

    /**
     * Устанавливает секурность
     *
     * @param secured секурность
     */
    public void setSecured(Secured secured) {
        this.secured = secured;
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
        Object value;
        filter.remove(map.getFilterKey());
        String avalue = request.getParameter(FValue.SUB + map.getFilterKey());
        switch (theType) {
            case FLOAT:
                if (avalue!=null && avalue.equals(FValue.IN))
                    value = request.getParameter(map.getFilterKey());
                else
                    value = Null.isNotNull(request.getParameter(map.getFilterKey())) ? HourFormatter.parseDouble(request.getParameter(map.getFilterKey())) : "";
                break;
            default:
                value = request.getParameter(map.getFilterKey());

        }
        if (avalue == null) {
            avalue = "";
        }
        if ((value != null && value.toString().length() > 0) || avalue.equals(FValue.EMPTY)) {
            filter.putItem(map.getFilterKey(), avalue + (avalue.equals(FValue.EMPTY) ? "" : value));
        } else {
            filter.remove(map.getFilterKey());
        }
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
        if (theType == NOT_FILTERED)
            return "<td>&nbsp;</td>";
        String locale = sc.getLocale();

        String value = HTMLEncoder.encode(filter.getAsString(map.getFilterKey()));
        String prefix = filter.getPrefix(map.getFilterKey());
        String defVal = value;
        StringBuffer outp = new StringBuffer(200);

        outp.append("<td style=\"vertical-align: middle;white-space: nowrap\">\n<nobr>");

        outp.append("<select name=\"_").append(map.getFilterKey()).append("\" id=\"_").append(map.getFilterKey()).append("\"").append(theType == CHARS ? " onchange=\"if (document.getElementById('_"+ map.getFilterKey() + "').value == '" + FValue.EMPTY + "'){ document.getElementById('" + map.getFilterKey() + "').style.display='none';} else {document.getElementById('" + map.getFilterKey() + "').style.display='block';}  \"" : "").append(">");
        if (theType == FLOAT || theType == INTEGER || theType == NATURAL) {
            outp.append("<option ").append(value == null || prefix.equals(FValue.SUB) ? "selected" : "").append(" value=\"_\">&lt;=");
            outp.append("<option ").append(value != null && prefix.equals("") ? "selected" : "").append(" value=\"\">&gt;=");
            outp.append("<option ").append(value != null && prefix.equals(FValue.NE) ? "selected" : "").append(" value=\"").append(FValue.NE).append("\">&gt;&lt;");
            outp.append("<option ").append(value != null && prefix.equals(FValue.EQ) ? "selected" : "").append(" value=\"").append(FValue.EQ).append("\">=");
            outp.append("<option ").append(value != null && prefix.equals(FValue.IN) ? "selected" : "").append(" value=\"").append(FValue.IN).append("\">").append(I18n.getString(locale, "IN_SET"));
        } else {
            if (theType == CHARS) {
                outp.append("<option ").append(value == null || prefix.equals(FValue.EQ) ? "selected" : "").append(" value=\"").append(FValue.EQ).append("\">").append(I18n.getString(locale, "EQUALS"));
                outp.append("<option ").append(value != null && prefix.equals(FValue.NE) ? "selected" : "").append(" value=\"").append(FValue.NE).append("\">").append(I18n.getString(locale, "UNEQUALS"));
                outp.append("<option ").append(value != null && prefix.equals(FValue.RE) ? "selected" : "").append(" value=\"").append(FValue.RE).append("\">").append(I18n.getString(locale, "REGEXP"));
                outp.append("<option ").append(value != null && prefix.equals(FValue.EMPTY) ? "selected" : "").append(" value=\"").append(FValue.EMPTY).append("\">").append(I18n.getString(locale, "EMPTY"));
            }
            outp.append("<option ").append(value != null && prefix.equals("") ? "selected" : "").append(" value=\"\">");
            if (theType == CHARS) {
                outp.append(I18n.getString(locale, "CONTAINS"));
            } else if (theType == QUANTITIES) {
                outp.append(I18n.getString(locale, "FIRST"));
            } else
                outp.append(I18n.getString(locale, "ASC"));

            outp.append("<option ").append(value != null && prefix.equals(FValue.SUB) ? "selected" : "").append(" value=\"_\">");

            if (theType == CHARS) {
                outp.append(I18n.getString(locale, "STARTS_WITH"));
            } else if (theType == QUANTITIES) {
                outp.append(I18n.getString(locale, "LAST"));
            } else
                outp.append(I18n.getString(locale, "SORT_DESC"));
        }

        outp.append("</select>&nbsp;");

        switch (theType) {

            case QUANTITIES:
            case NATURAL:
                outp.append("<input type=\"text\"  alt=\"natural\"" + " name=\"").append(map.getFilterKey()).append("\" value=\"").append(value != null ? defVal : "").append("\">\n");
                break;
            case INTEGER:
                outp.append("<input type=\"text\"  alt=\"integer\"" + " name=\"").append(map.getFilterKey()).append("\" value=\"").append(value != null ? defVal : "").append("\">\n");
                break;
            case FLOAT:
                outp.append("<input type=\"text\" alt=\"float\"" + " name=\"").append(map.getFilterKey()).append("\" value=\"").append(value != null && defVal.length() != 0 ? (!prefix.equals(FValue.IN) ? defVal : defVal) : "").append("\">\n");
                break;
            default:
                if (theType == CHARS && map.getFilterKey().substring(0, 3).equals(FValue.UDF)) {
                    SecuredUDFBean udfls = AdapterManager.getInstance().getSecuredFindAdapterManager().findUDFById(sc, map.getFilterKey().substring(3));
                    outp.append("<input type=\"text\" alt=\"").append(udfls.isLookuponly()).append('\"').append(udfls.isLookuponly() ? " disabled" : "").append(" name=\"").append(map.getFilterKey()).append("\"").append(" id=\"").append(map.getFilterKey()).append("\" value=\"").append(value != null ? defVal : "").append("\"").append(prefix.equals(FValue.EMPTY) ? "style=\"display:none;\"" : "").append(">\n");
                    if (udfls.getLookupscript() != null) {
                        Object val2 = udfls.getLookupscriptCalc(secured);
                        outp.append("<select onchange=\"copyToInput(this)\" ").append(" name=\"").append(map.getFilterKey()).append("\">\n");
                        outp.append("<option").append((value != null ? " selected" : "")).append(" value=\"\" >-------").append(I18n.getString(locale, "NONE")).append("-------</option>\n");
                        if (val2 != null) {
                            List<String> set = Collections.synchronizedList(new ArrayList<String>((Collection<String>) val2));
                            for (String key : set) {
                                if (key != null) {
                                    outp.append("<option").append((key.equals(defVal) ? " selected" : "")).append(" value=\"").append(key).append("\" >").append(key).append("</option>\n");
                                }
                            }
                        }
                        outp.append("</select>\n");
                    }
                } else
                    outp.append("<input type=\"text\" ").append(" name=\"").append(map.getFilterKey()).append("\" id=\"").append(map.getFilterKey()).append("\" value=\"").append(value != null ? defVal : "").append("\">\n");
        }
        outp.append("</nobr></td>\n");
        return outp.toString();
    }
}
