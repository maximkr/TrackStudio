package com.trackstudio.app.filter.customizer;

import javax.servlet.http.HttpServletRequest;

import com.trackstudio.app.filter.FValue;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.common.FieldMap;
import com.trackstudio.exception.GranException;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.Null;

import net.jcip.annotations.NotThreadSafe;

/**
 * Абстрактный класс кастомного вывода в фильтрах
 */
@NotThreadSafe
public abstract class Customizer implements Comparable<Customizer> {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Карта полей
     */
    protected FieldMap map;
    /**
     * Активно поле или нет
     */
    protected boolean disabled;

    /**
     * Конструктор
     *
     * @param map карта полей
     */
    protected Customizer(FieldMap map) {
        this.map = map;
    }

    /**
     * Абстрактный метод для вывода информативного блока о полях
     *
     * @param sc          сессия пользователя
     * @param filter      фильтр
     * @param contextPath относительный путь контекста
     * @return строка вывода
     * @throws GranException при необзодимости
     */
    public abstract String draw(SessionContext sc, FValue filter, String contextPath) throws GranException;

    /**
     * Абстрактный фильтр, который устанавливает фильтр
     *
     * @param sc      сессия пользователя
     * @param request запрос
     * @param filter  фильтр
     * @throws GranException при необходимости
     */
    public abstract void setFilter(SessionContext sc, HttpServletRequest request, FValue filter) throws GranException;

    /**
     * Абстрактный метод для вывода блока ввода для полей
     *
     * @param sc          сессия пользователя
     * @param filter      фильтр
     * @param contextPath относительный путь контекста
     * @return строка вывода
     * @throws GranException при необзодимости
     */
    public abstract String drawInput(SessionContext sc, FValue filter, String contextPath) throws GranException;

    /**
     * Абстрактный метод для вывода информативного блока о дате и попапах
     *
     * @param sc          сессия пользователя
     * @param s           Строка
     * @param filter      фильтр
     * @param contextPath относительный путь контекста
     * @return строка вывода
     * @throws GranException при необходимости
     */
    protected String drawForDateOrPopUp(SessionContext sc, String s, FValue filter, String contextPath) throws GranException {
        StringBuffer outp = new StringBuffer(200);
        outp.append("<td style=\"vertical-align: top\"><label for=\"").append(Null.stripNullText(map.getFilterKey())).append("\">").append(I18n.getString(sc, map.getAltKey())).append("</label></td><span id =\"").append(map.getFilterKey()).append("_dis\">");
        outp.append(drawInput(sc, filter, contextPath));
        return outp.toString();
    }

    @Override
    public int compareTo(Customizer o) {
        if (name != null && o.name != null) {
            return name.compareTo(o.name);
        } else {
            Integer first = map.getOrder();
            Integer second = o.map.getOrder();
            int result = first.compareTo(second);
            if (result == 0 && map.getFilterKey().startsWith(FValue.UDF)) {
                result = map.getAltKey().compareTo(o.map.getAltKey());
            }
            return result;
        }
    }
}
