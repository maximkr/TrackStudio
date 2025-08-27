package com.trackstudio.tools;

import net.jcip.annotations.Immutable;

/**
 * Абстрактный класс для регистронезависимых свойств
 */
@Immutable
public abstract class CaseInsensitivePropertyComparable extends PropertyComparable {

    /**
     * Сравнивает объекты
     *
     * @param o Сравниваемый обхект
     * @return +1, 0, -1
     */
    public int compareTo(Object o) {
        PropertyComparable li = (PropertyComparable) o;
        return PropertyComparator.compareIgnoreCase(getContainer(), li.getContainer());
    }

    /**
     * Сравнивает объекты
     *
     * @param o     Сравниваемый обхект
     * @param limit предел
     * @return +1, 0, -1
     */
    public int compareTo(Object o, int limit) {
        PropertyComparable li = (PropertyComparable) o;
        return PropertyComparator.compareIgnoreCase(getContainer(), li.getContainer(), limit);
    }

    /**
     * Сравнивает объекты
     *
     * @param obj Сравниваемый обхект
     * @return TRUE - равны, FALSE - нет
     */
    public boolean equals(Object obj) {
        if (obj instanceof PropertyComparable) {
            PropertyComparable li = (PropertyComparable) obj;
            return PropertyComparator.isEquals(getContainer(), li.getContainer());
        }
        return false;
    }
}