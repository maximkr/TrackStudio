package com.trackstudio.tools;

import java.util.concurrent.atomic.AtomicReference;

import net.jcip.annotations.Immutable;

/**
 * Абстрактный класс, наследуемые от него классы получают созможность сортировки по свойствам
 */
@Immutable
public abstract class PropertyComparable implements Comparable {

    /**
     * Контейнер свойств
     */
    protected final AtomicReference<PropertyContainer> container = new AtomicReference<PropertyContainer>(null);

    /**
     * Возвращает контейнер свойств
     *
     * @return контейнер свойств
     */
    protected abstract PropertyContainer getContainer();

    /**
     * Сравнивает два объекта текущего класса
     *
     * @param o Сравниваемый обхект
     * @return +1, 0 или -1
     */
    public int compareTo(Object o) {
        PropertyComparable li = (PropertyComparable) o;
        return PropertyComparator.compare(getContainer(), li.getContainer());
    }

    /**
     * Сравнивает два объекта текущего класса без учета регистра
     *
     * @param o Сравниваемый обхект
     * @return +1, 0 или -1
     */
    public int compareToIgnoreCase(Object o) {
        PropertyComparable li = (PropertyComparable) o;
        return PropertyComparator.compareIgnoreCase(getContainer(), li.getContainer());
    }

    /**
     * Сравнивает два объекта текущего класса без учета регистра, с пределом
     *
     * @param o     Сравниваемый обхект
     * @param limit лимит
     * @return +1, 0 или -1
     */
    public int compareToIgnoreCase(Object o, int limit) {
        PropertyComparable li = (PropertyComparable) o;
        return PropertyComparator.compareIgnoreCase(getContainer(), li.getContainer(), limit);
    }

    /**
     * Сравнивает два обхекта текущего класса
     *
     * @param obj Скравниваемый обхект
     * @return TREU если равны, FALSE если нет
     */
    public boolean equals(Object obj) {
        if (obj instanceof PropertyComparable) {
            PropertyComparable li = (PropertyComparable) obj;
            return PropertyComparator.isEquals(getContainer(), li.getContainer());
        }
        return false;
    }

    /**
     * Возвращает hash code
     *
     * @return hashCode
     */
    public int hashCode() {
        return getContainer().getHashCode();
    }
}
