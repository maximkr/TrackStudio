package com.trackstudio.tools;

import java.util.List;

import net.jcip.annotations.ThreadSafe;

/**
 * Класс-контейнер для хранения пары значений
 */
@ThreadSafe
public class Pair<T> implements Comparable {

    private final String key;
    private volatile String value;
    private final String valueSort;
    private final T t;
    private final boolean boolValue;

    /**
     * Конструктор
     *
     * @param key   ключ
     * @param value значение
     */
    public Pair(String key, String value) {
        this.key = key;
        this.t = null;
        this.value = value;
        this.valueSort = value;
        this.boolValue = false;
    }

    public Pair(T t, boolean boolValue) {
        this.key = null;
        this.t = t;
        this.boolValue = boolValue;
        this.valueSort = null;
    }

    public Pair(String key, String value, T t, boolean boolValue) {
        this.key = key;
        this.t = t;
        this.boolValue = boolValue;
        this.valueSort = null;
        this.value = value;
    }

    /**
     * Конструктор
     *
     * @param key       ключ
     * @param value     значение
     * @param valueSort значение сортировки
     */
    public Pair(String key, String value, String valueSort) {
        this.key = key;
        this.value = value;
        this.valueSort = valueSort;
        this.t = null;
        this.boolValue = false;
    }

    /**
     * ВОзвращает ключ
     *
     * @return ключ
     */
    public String getKey() {
        return key;
    }


    /**
     * Возвращает значение
     *
     * @return значение
     */
    public String getValue() {
        return value;
    }

    /**
     * Устанавливает значение
     *
     * @param value значение
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Возвращает строковое представление объекта
     *
     * @return строковое представление объекта
     */
    public String toString() {
        return key;
    }




    /**
     * Сравнивает два объекта текущего класса
     *
     * @param o сравниваемый объект
     * @return +1, 0, -1
     */
    public int compareTo(Object o) {
        Pair pair = (Pair) o;
        return compare(key, valueSort, pair.key, pair.valueSort);
    }

    private static int compare(String ida, String namea, String idb, String nameb) {
        if (namea == null) {
            return 1;
        }
        if (nameb == null) {
            return -1;
        }
        int res = namea.compareTo(nameb);
        if (res == 0) {
            res = (namea + ida).compareTo(nameb + idb);
        }
        return res;
    }

    /**
     * Ищет объект в массиве по ключу
     *
     * @param list список значений
     * @param key  ключ
     * @return объект Pair
     */
    public static Pair findPair(List<Pair> list, Object key) {
        int j = list.indexOf(new Pair(key.toString(), ""));
        if (j > -1)
            return list.get(j);
        else
            return null;
    }

    public String getValueSort() {
        return valueSort;
    }


    public boolean isBoolValue() {
        return boolValue;
    }

    public T getT() {
        return t;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pair pair = (Pair) o;

        if (key != null ? !key.equals(pair.key) : pair.key != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return key != null ? key.hashCode() : 0;
    }
}