package com.trackstudio.app.udf;

import java.util.HashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.trackstudio.app.filter.customizer.Customizer;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.UdfvalCacheItem;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.tools.Pair;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.Immutable;
import net.jcip.annotations.ThreadSafe;

/**
 * Абстрактный класс, на основании которого строятся все виды пользовательских полей
 */
@Immutable
public abstract class GenericValue {
    /**
     * ID пользовательского поля
     */
    protected final String udfId;


    /**
     * Возвращает ID пользовательского поля
     *
     * @return ID поля
     */
    public String getUdfId() {
        return udfId;
    }

    /**
     * Конструктор по умолчанию
     *
     * @param udfId ID пользовательского поля
     */
    public GenericValue(String udfId) {
        this.udfId = udfId;
    }

    public GenericValue(UdfvalCacheItem val) {
        this.udfId = val.getUdfId();
    }

    /**
     * Возвращает значение пользовательского поля
     *
     * @param o Вычисляемое поле или нет
     * @return дата
     * @throws GranException при необходимости
     */
    public abstract Object getValue(Object o) throws GranException;

    /**
     * Возвращает значение поля в виде строки
     *
     * @param o значение
     * @return строковое представление
     * @throws GranException при необходимости
     */
    public abstract String getValueAsString(Object o) throws GranException;

    /**
     * Сравнивает два объекта текущего класса
     *
     * @param obj йобъект текущего класса
     * @return TRUE - если поля равны, FALSE - если нет
     */
    public boolean equals(Object obj) {
        return obj instanceof GenericValue && getUdfId().equals(((GenericValue) obj).getUdfId());
    }

    /**
     * Возвращает кастомизатор для этого типа поля
     *
     * @param caption    заголовок поля
     * @param timezone   таймзона
     * @param locale     локаль
     * @param sortcolumn сортировка
     * @param disabled   активное поле или нет
     * @return кастомизатор
     * @throws GranException при необходимости
     * @see com.trackstudio.app.filter.customizer.Customizer
     */
    public abstract Customizer getCustomizer(String caption, String timezone, String locale, String sortcolumn, boolean disabled) throws GranException;

    /**
     * Возвращает пару значения
     *
     * @param value значение
     * @return пара-значение
     * @throws GranException при необходимости
     * @see com.trackstudio.tools.Pair
     */
    protected Pair findListValue(String value) throws GranException {
            HashMap<String, String> m = KernelManager.getUdf().getUdflist(udfId);
            for (String o : m.keySet()) {
                Object val = m.get(o);
                if (val.equals(value)) return new Pair(o, value);
            }
            return null;
    }
}
