package com.trackstudio.tools;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.jcip.annotations.Immutable;

/**
 * Класс проверки на null
 */
@Immutable
public class Null implements Serializable {

    private static final String magicNull = "null";

    /**
     * Проверка на not null
     *
     * @param str что проверяем
     * @return TRUE - не null, FALSE - null
     */
    public static boolean isNotNull(Object str) {
        return str != null && !str.toString().equals(magicNull) && str.toString().trim().length() > 0;
    }

    /**
     * Проверка на null
     *
     * @param str что проверяем
     * @return TRUE - null, FALSE - not null
     */
    public static boolean isNull(Object str) {
        return !isNotNull(str);
    }

    /**
     * В случае null возвращает пустую строку
     *
     * @param str строка
     * @return строка
     */
    public static String stripNullHtml(Object str) {
        if (isNull(str)) {
            return "";
        } else {
            return str.toString();
        }
    }

    /**
     * Если null, то null
     *
     * @param str строка
     * @return строка
     */
    public static String beNull(String str) {
        return isNotNull(str) ? str : null;
    }

    /**
     * В случае null возвращает пустую строку
     *
     * @param str строка
     * @return строка
     */
    public static String stripNullText(Object str) {
        if (isNull(str)) {
            return "";
        } else {
            return str.toString();
        }
    }

    public static Integer getIntegerOrDefaultValue(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    public static Integer intOrDef(Integer value, int defaultValue) {
        return value != null ? value : defaultValue;
    }

    /**
     * Required to initialize CopyOnWriteArrayList
     * @param lst
     * @return
     */
    public static <V> List<V> removeNullElementsFromList(Collection<V> lst)
    {
        if (lst == null)
            return new ArrayList<V>();

        ArrayList ret = new ArrayList<V>(lst.size());
        for (V o:lst) {
            if (o!=null)
                ret.add(o);
        }
        return ret;
    }

    /**
     * Required to initialize ConcurrentHashMap()
     * @return
     */
    public static <K,V> Map<K,V> removeNullElementsFromMap(Map<K,V> map)
    {
        if (map == null)
            return new HashMap<K,V>();

        HashMap<K,V> ret = new HashMap<K,V>(map.size());
        for (Map.Entry<K,V> item: map.entrySet()) {
            if (item.getKey() != null && item.getValue()!=null)
                ret.put(item.getKey(), item.getValue());
        }
        return ret;
    }

}