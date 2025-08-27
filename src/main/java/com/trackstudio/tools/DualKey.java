package com.trackstudio.tools;

import java.io.Serializable;

import net.jcip.annotations.Immutable;

/**
 * Класс для двух ключей
 */
@Immutable
public class DualKey implements Serializable, Comparable {

    /**
     * Левый ключ
     */
    protected final String leftPart;
    /**
     * Правый ключ
     */
    protected final String rightPart;

    /**
     * Конструктор
     *
     * @param l левый ключ
     * @param r правый ключ
     */
    public DualKey(String l, String r) {
        this.leftPart = l;
        this.rightPart = r;
    }

    /**
     * Возвращает левый ключ
     *
     * @return левый ключ
     */
    public String getLeftPart() {
        return this.leftPart;
    }

    /**
     * Возвращает правый ключ
     *
     * @return правый ключ
     */
    public String getRightPart() {
        return this.rightPart;
    }

    /**
     * Возвращает хеш
     *
     * @return хеш
     */
    public int hashCode() {
        return toString().hashCode();
    }

    /**
     * Возвращает строковое представление
     *
     * @return строковое представление
     */
    public String toString() {
        return getLeftPart() + '\n' + getRightPart();
    }

    /**
     * Сравнивает объекты
     *
     * @param obj Сравниваемый обхект
     * @return TRUE - равны, FALSE - нет
     */
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof DualKey) {
            DualKey d = (DualKey) obj;
            return getLeftPart().equals(d.getLeftPart()) && getRightPart().equals(getRightPart());
        } else
            return false;
    }

    /**
     * Сравнивает объекты
     *
     * @param o Сравниваемый обхект
     * @return +1, 0, -1
     */
    public int compareTo(Object o) {
        return compareTo(o.toString());
    }

    /**
     * Сравнивает объекты
     *
     * @param anotherString Сравниваемый обхект
     * @return +1, 0, -1
     */
    public int compareTo(String anotherString) {
        return this.toString().compareTo(anotherString);
    }

    /**
     * Сравнивает объекты
     *
     * @param d Сравниваемый обхект
     * @return +1, 0, -1
     */
    public int compareTo(DualKey d) {
        return compareTo(d.toString());
    }
}