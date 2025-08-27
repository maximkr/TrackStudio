package com.trackstudio.tools;

import java.util.Comparator;

import net.jcip.annotations.Immutable;

/**
 * Класс-компаратор для сравнения настроек
 */
@Immutable
public class PropertyComparator implements Comparator {
    /**
     * Конструктор
     */
    public PropertyComparator() {
    }

    /**
     * Сравнивает два свойства
     *
     * @param a одно свойства
     * @param b второе свойство
     * @return +1, 0, -1
     */
    protected static int compareProperties(PropertyContainer a, PropertyContainer b) {
        int res = 0;
        if (a==b) return 0; //check for equal a and b
        a.reset();
        b.reset();
        while (res == 0) {
            String sAa = a.next();
            String sBb = b.next();
            if (sAa == null && sBb == null) {
                return 0;
            } else if (sAa == null)
                return -1;
            else if (sBb == null)
                return 1;
            else {
                res = sAa.compareTo(sBb);
            }
        }
        return res;
    }

    /**
     * Сравнивает два свойства без учета регистра
     *
     * @param a одно свойства
     * @param b второе свойство
     * @return +1, 0, -1
     */
    protected static int comparePropertiesIgnoreCase(PropertyContainer a, PropertyContainer b) {
        int res = 0;
        if (a==b) return 0; //check for equal a and b
        a.reset();
        b.reset();
        while (res == 0) {
            String sAa = a.next();
            String sBb = b.next();
            if (sAa == null && sBb == null) {
                return 0;
            } else if (sAa == null)
                return -1;
            else if (sBb == null)
                return 1;
            else {
                res = sAa.compareToIgnoreCase(sBb);
            }
        }
        return res;
    }

    /**
     * Сравнивает два свойства без учета регистра
     *
     * @param a     одно свойства
     * @param b     второе свойство
     * @param limit предел
     * @return +1, 0, -1
     */
    protected static int comparePropertiesIgnoreCase(PropertyContainer a, PropertyContainer b, int limit) {
    	if (a==b) return 0; //check for equal a and b
        a.reset();
        b.reset();
        String sAa = a.next();
        String sBb = b.next();
        if (sAa == null && sBb == null) {
            return 0;
        } else if (sAa == null)
            return -1;
        else if (sBb == null)
            return 1;
        else {
            if (sAa.length() > limit) sAa = sAa.substring(0, limit);
            if (sBb.length() > limit) sBb = sBb.substring(0, limit);
            return sAa.compareToIgnoreCase(sBb);
        }
    }

    /**
     * Сравнивает два свойства
     *
     * @param a одно свойства
     * @param b второе свойство
     * @return TRUE - равны, FALSE - нет
     */
    private static boolean equProperties(PropertyContainer a, PropertyContainer b) {
        if (a == b) return true;

        boolean res = true;
        
        a.reset();
        b.reset();
        while (res) {
            String sAa = a.next();
            String sBb = b.next();
            if (sAa == null && sBb == null) {
                return true;
            } else if (sAa == null)
                return false;
            else if (sBb == null)
                return false;
            else {
                res = sAa.equals(sBb);
            }
        }
        return res;
    }

    /**
     * Сравнивает два свойства
     *
     * @param a одно свойства
     * @param b второе свойство
     * @return +1, 0, -1
     */
    public static int compare(PropertyContainer a, PropertyContainer b) {

        int k = compareProperties(a, b);
        a.reset();
        b.reset();
        if (a.inverse) return -k;
        else return k;
    }

    /**
     * Сравнивает два свойства
     *
     * @param a одно свойства
     * @param b второе свойство
     * @return +1, 0, -1
     */
    public static int compareIgnoreCase(PropertyContainer a, PropertyContainer b) {

        int k = comparePropertiesIgnoreCase(a, b);
        a.reset();
        b.reset();
        if (a.inverse) return -k;
        else return k;
    }

    /**
     * Сравнивает два свойства без учета регистра
     *
     * @param a     одно свойства
     * @param b     второе свойство
     * @param limit предел
     * @return +1, 0, -1
     */
    public static int compareIgnoreCase(PropertyContainer a, PropertyContainer b, int limit) {

        int k = comparePropertiesIgnoreCase(a, b, limit);
        a.reset();
        b.reset();
        if (a.inverse) return -k;
        else return k;
    }

    /**
     * Сравнивает два свойства
     *
     * @param a одно свойства
     * @param b второе свойство
     * @return TRUE - равны, FALSE - нет
     */
    public static boolean isEquals(PropertyContainer a, PropertyContainer b) {
        boolean k = equProperties(a, b);
        a.reset();
        b.reset();
        return k;
    }

    /**
     * Сравнивает два свойства
     *
     * @param o1 одно свойства
     * @param o2 второе свойство
     */
    public int compare(Object o1, Object o2) {
        return compare((PropertyContainer) o1, (PropertyContainer) o2);
    }
}