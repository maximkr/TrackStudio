package com.trackstudio.app.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArrayList;

import com.trackstudio.action.task.items.FieldListItem;
import com.trackstudio.app.session.SessionContext;
import com.trackstudio.common.FieldMap;
import com.trackstudio.exception.GranException;
import com.trackstudio.startup.I18n;
import com.trackstudio.tools.EggBasket;
import com.trackstudio.tools.Null;

import net.jcip.annotations.Immutable;

/**
 * Класс для хранения настроек параметров фильтрации
 */
@Immutable
public abstract class FValue extends EggBasket<String, String> {

    /**
     * Константа, для колличества результатов на странице
     */
    public static final String ONPAGE = "onpage";
    /**
     * Разделитель
     */
    public static final String DELIM = ";";
    /**
     * Разделитель
     */
    public static final String AMP = "&";
    /**
     * Порядок сортировки
     */
    public static final String SUB = "_";// порядок сортировки
    /**
     * Период времени
     */
    public static final String PERIOD = "period_";
    /**
     * Префикс времени
     */
    public static final String AMNT = "amnt_";
    /**
     * Интервал
     */
    public static final String INTERVAL = "interval_";
    /**
     * Префикс ba_
     */
    public static final String BA = "ba_";
    /**
     * Префикс el_
     */
    public static final String EL = "el_";
    /**
     * Префикс _ne_
     */
    public static final String NE = "_ne_";
    /**
     * Префикс _eq_
     */
    public static final String EQ = "_eq_";
    /**
     * Префикс _in_
     */
    public static final String IN = "_in_";
    /**
     * Префикс _re_
     */
    public static final String RE = "_re_";
    /**
     * Префикс _empty_
     */
    public static final String EMPTY = "_empty_";
    /**
     * Порядок сортировки
     */
    public static final String SORTORDER = "sortorder";
    /**
     * Поле пользовательское
     */
    public static final String UDF = "UDF";
    /**
     * Порядок сортировки пользовательских полей
     */
    public static final String UDF_SORT = "UDF_SORT";

    public static final String UDF_SORT_LOWER_CAUSE = "udf_sort";
    /**
     * Отображение сообщений
     */
    public static final String DISPLAY = "display";
    /**
     * Глубокий поиск
     */
    public static final String SUBTASK = "subtask";

    /**
     * Конструктор
     */
    public FValue() {
        super();
    }

    /**
     * Возвращает список значений в виде строки
     *
     * @param s свойства фильтрации
     * @return строка
     */
    public String getOriginalAsString(String s) {
        List<String> list = super.get(s);
        if (list != null && !list.isEmpty())
            return list.get(0);
        else
            return null;
    }

    /**
     * Возвращает список значений в виде строки
     *
     * @param s свойства фильтрации
     * @return строка
     */
    public String getAsString(String s) {
        String orig = getOriginalAsString(s);
        if (orig != null)
            return orig.substring(getPrefix(s).length());
        else
            return null;
    }

    /**
     * Устанавливает параметр фильтрации
     *
     * @param key   параметр
     * @param value значение
     */
    public void set(String key, String value) {
        this.remove(key);
        if (value != null && value.length() > 0) {
            this.putItem(key, value);
        }
    }

    /**
     * Устанавливает порядок сортировки
     *
     * @param values порядок сортировки
     */
    public void setSortOrder(String[] values) {
        StringBuffer intValues = new StringBuffer(200);
        this.remove(SORTORDER);
        if (values != null)
            for (int i = 0; i < values.length; i++) {
                if (values[i] != null)
                    intValues.append(values[i]);
                if (i < values.length - 1) intValues.append(DELIM);
            }
        this.putItem(SORTORDER, intValues.toString());
    }

    /**
     * Устанавливает список значений для параметра фильтрации
     *
     * @param key    параметр фильтрации
     * @param values список значений
     */
    public void setList(String key, List<String> values) {
        this.put(key, new CopyOnWriteArrayList<String>(Null.removeNullElementsFromList(values)));
    }

    /**
     * Возвращает список значений для параметров фильтрации
     *
     * @param key параметр фильтрации
     * @return список значений
     */
    public List<String> getOriginValues(String key) {
        return super.get(key);

    }

    /**
     * Возвращает порядок сортировки
     *
     * @return порядок сортировки
     */
    public List<String> getSortOrder() {
        String values = getOriginalAsString(SORTORDER);
        if (values != null) {
            return Arrays.asList(values.split(DELIM));
        } else {
            return new ArrayList<String>();
        }
    }

    /**
     * Возвращает порядок сортировки в виде строки
     *
     * @param list параметры сортировки
     * @return параметры сортировки
     */
    public static String getSortOrderAsString(List<String> list) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
            if (i != list.size() - 1) {
                sb.append(DELIM);
            }
        }
        return sb.toString();
    }

    /**
     * Возвращает список значений для параметра фильтрации
     *
     * @param key параметр
     * @return список значений
     */
    public List<String> get(String key) {
        String pref = getPrefix(key);
        if (pref.equals(""))
            return getOriginValues(key);
        else {
            List<String> that = getOriginValues(key);
            List<String> thisA = new ArrayList<String>();
            if (that != null && !that.isEmpty()) {
                for (String a : that) {
                    thisA.add(a.substring(pref.length()));
                }
            }
            return thisA;
        }
    }

    /**
     * ВОзвращает префикс для параметра сортировки
     *
     * @param key параметр сортировки
     * @return префикс
     */
    public String getPrefix(String key) {
        return getValuePrefix(getOriginalAsString(key));
    }

    /**
     * Проверяет необходимость фильтрации пользовательских полей
     *
     * @return TRUE - недо фильтровать, FALSE - ненадо
     */
    public boolean needFilterUDF() {
        return displayUdfs() || filterUdfs();
    }

    /**
     * Надо ли показывать пользовательские поля
     *
     * @return TRUE - надо, FALSE - нет
     */
    private boolean displayUdfs() {
        List<String> list = this.get(DISPLAY);
        boolean need = false;
        if (list != null) {
            need = list.toString().indexOf(UDF) > -1;
        }
        return need;
    }

    /**
     * Проверяет надо ли фильтровать пользовательские поля или нет
     *
     * @return TRUE - надо, FALSE - не надо
     */
    private boolean filterUdfs() {
        Set<String> list = getUse();
        boolean need = false;
        if (list != null)
            need = list.toString().indexOf(UDF) > -1;
        return need;
    }

    /**
     * Глубокий поиск
     */
    public static final String DEEPSEARCH = "deepsearch";

    /**
     * Проверяет необходимость глубокого поиска
     *
     * @return TRUE - надо, FALSE - нет
     */
    public boolean needDeepSearch() {
        return (getOriginValues(SUBTASK) != null) || (getOriginValues(DEEPSEARCH) != null);
    }

    /**
     * Проверяет наличие значения в списке
     *
     * @param key   параметр
     * @param value значение
     * @return TRUE - есть в наличии, FALSE - нет
     */
    public boolean hasListValue(String key, String value) {
        if (value == null)
            return false;
        List<String> list = this.get(key);
        return (list != null && list.indexOf(value) > -1);
    }

    /**
     * Возвращает список значений для параметра
     *
     * @param prop параметр
     * @return список значений
     */
    public List<String> toList(String prop) {
        List<String> list = this.get(prop);
        if (list != null && !list.isEmpty())
            list.remove("0");
        else {
            return new ArrayList<String>();    
        }
        return list;
    }

    /**
     * Разбирает список значений для параметра
     *
     * @param s параметр
     * @return список значений
     */
    public static ArrayList<String> parseFilterValue(String s) {
        if (s == null)
            return new ArrayList<String>();
        if (s.indexOf(AMP) > 0)
            return parseFilterValue(s, AMP);
        return parseFilterValue(s, DELIM);
    }

    /**
     * Разбирает список значений для параметра с указанным разделитилем
     *
     * @param value параметр
     * @param delim разделитель
     * @return список значений
     */
    public static ArrayList<String> parseFilterValue(String value, String delim) {
        ArrayList<String> result = new ArrayList<String>();
        if (value != null && value.length() != 0) {
            boolean prefix = value.charAt(0) == '_';
            String prefixValue = prefix ? "_" : "";
            String[] values;
            if (prefix) {
                values = value.substring(1).split(delim);
            } else {
                values = value.split(delim);
            }
            for (String v : values) {
                result.add(prefixValue + v);
            }
        }
        return result;
    }

    /**
     * Возвращает префикс для значения
     *
     * @param s значение
     * @return префикс
     */
    public static String getValuePrefix(String s) {
        String appender = "";
        if (s == null || s.length() == 0)
            return "";
        if (s.startsWith(PERIOD)) {
            appender = PERIOD;
        } else if (s.startsWith(INTERVAL)) {
            appender = INTERVAL;
        } else if (s.startsWith(AMNT)) {
            appender = AMNT;
        } else if (s.startsWith(BA)) {
            appender = BA;
        } else if (s.startsWith(EL)) {
            appender = EL;
        } else if (s.startsWith(NE)) {
            appender = NE;
        } else if (s.startsWith(EQ)) {
            appender = EQ;
        } else if (s.startsWith(IN)) {
            appender = IN;
        } else if (s.startsWith(RE)) {
            appender = RE;
        } else if (s.startsWith(EMPTY)) {
            appender = EMPTY;
        } else if (s.startsWith(SUB)) {
            appender = SUB;
        }
        return appender;
    }

    public boolean checkValueDate(String udfId) {
        String[] prefixDate = {AMNT, BA, EL, INTERVAL, SUB, PERIOD};
        for (String prefix : prefixDate) {
            if (getAsString(prefix + udfId) != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Возвращает используемые параметры фильтрации
     *
     * @return список параметров
     */
    public Set<String> getUse() {
        Set<String> keyse = keySet();
        Set<String> usedKeys = new TreeSet<String>();
        for (String key : keyse) {
            usedKeys.add(key.substring(getValuePrefix(key).length()));
        }
        return usedKeys;
    }

    /**
     * Возвращает список полей для отображения
     *
     * @return список полей
     */
    public List<String> getView() {
        List<String> use = new ArrayList<String>();
        List<String> display = get(DISPLAY);
        if (display != null) {
            use.addAll(display);
        }

        String filterKey = FieldMap.MESSAGEVIEW.getFilterKey();
        if (filterKey != null && get(filterKey) != null && getOriginalAsString(filterKey) != null && !getOriginalAsString(filterKey).equals("0")) {
            use.add(filterKey);
        }
        return use;
    }

    /**
     * Абстрактный метод для возвращаения списка используемых для фильтрации пользовательских полей
     *
     * @return список пользовательских полей
     */
    public abstract Set<String> getUseForUdf();

    /**
     * Устанавливает поля
     *
     * @param sc             сессия пользователя
     * @param map            карта полей
     * @param fields         поля
     * @param sortorderList  список параметров сортировки
     * @param selectedFields выбранные поля
     * @param field          поле
     * @return поле
     * @throws GranException при необходимости
     */
    public String setFields(SessionContext sc, FieldMap map, ArrayList<FieldListItem> fields, List<String> sortorderList, ArrayList<FieldListItem> selectedFields, String field) throws GranException {
        boolean checked = hasListValue(DISPLAY, FieldMap.getFilterKeyByFieldKey(map.getFieldKey()));

        if (!checked) {
            fields.add(new FieldListItem(map.getOrder(), map.getFieldKey(), I18n.getString(sc, map.getAltKey()), I18n.getString(sc, map.getAltKey())));
        } else {
            boolean asc = sortorderList != null && sortorderList.contains(FValue.SUB + map.getFieldKey());
            boolean desc = sortorderList != null && sortorderList.contains(map.getFieldKey());
            if (!asc && !desc) {
                selectedFields.add(new FieldListItem(map.getOrder(), "\u00a0\u00a0\u00a0\u00a0\u00a0" + map.getFieldKey(), I18n.getString(sc, map.getAltKey()), "\u00a0\u00a0\u00a0\u00a0\u00a0" + I18n.getString(sc, map.getAltKey())));
                field += "\u00a0\u00a0\u00a0\u00a0\u00a0" + map.getFieldKey() + ";";
            } else if (asc) {
                int order = sortorderList.indexOf(FValue.SUB + map.getFieldKey()) + 1;
                selectedFields.add(new FieldListItem(map.getOrder(), "+(" + order + ")\u00a0" + map.getFieldKey(), I18n.getString(sc, map.getAltKey()), "\u2191(" + order + ")\u00a0" + I18n.getString(sc, map.getAltKey())));
                field += "+(" + order + ")\u00a0" + map.getFieldKey() + ";";
            } else {
                int order = sortorderList.indexOf(map.getFieldKey()) + 1;
                selectedFields.add(new FieldListItem(map.getOrder(), "-(" + order + ")\u00a0" + map.getFieldKey(), I18n.getString(sc, map.getAltKey()), "\u2193(" + order + ")\u00a0" + I18n.getString(sc, map.getAltKey())));
                field += "-(" + order + ")\u00a0" + map.getFieldKey() + ";";
            }
        }
        return field;
    }

    /**
     * Возвращает список ID используемых для фильтрации пользовательских полей
     *
     * @return список ID полей
     */
    public Set<String> getUsedUdfIds() {
        Set<String> use = getUseForUdf();
        Set<String> ids = new TreeSet<String>();
        for (String key : use) {
            if (key.toUpperCase(Locale.ENGLISH).startsWith(UDF)) {
                ids.add(key.substring(UDF.length()));
            }
        }
        return ids;
    }
}
