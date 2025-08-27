package com.trackstudio.app.udf;

import com.trackstudio.app.filter.FValue;
import com.trackstudio.app.filter.customizer.Customizer;
import com.trackstudio.app.filter.customizer.TextCustomizer;
import com.trackstudio.common.FieldMap;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.UdfvalCacheItem;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.Immutable;
import net.jcip.annotations.ThreadSafe;

/**
 * Класс, описывает значение пользовательского поля типа Цедое
 */
@Immutable
public class NumericValue extends GenericValue {
    private final int value;
    private final boolean hasSimpleValue;

    /**
     * Конструктор
     *
     * @param udfId ID поля
     */
    public NumericValue(String udfId) {
        super(udfId);
        value = 0;
        hasSimpleValue = false;
    }

    /**
     * Устанавливает значение поля
     *
     * @param val значение
     * @throws GranException при необходимости
     */
    public NumericValue(UdfvalCacheItem val) throws GranException {
            super(val);
            if (val.getNum() != null) {
                hasSimpleValue = true;
                value = val.getNum() != null ? val.getNum().intValue() : null;
            } else {
                value = 0;
                hasSimpleValue = false;
            }
    }

    /**
     * Возвращает значение пользовательского поля
     *
     * @param calculated Вычисляемое поле или нет
     * @return дата
     */
    public Integer getValue(Object calculated) {
        try {
            if (calculated != null && calculated.toString().length() != 0)
                return Integer.parseInt(calculated.toString());

                if (hasSimpleValue)
                    return value;
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
        }
        return null;
    }

    /**
     * Возвращает значение поля в виде строки
     *
     * @param o значение
     * @return строковое представление
     * @throws GranException при необходимости
     */
    public String getValueAsString(Object o) throws GranException {
        if (o != null) {
            Integer v = (Integer) o;
            return v.toString();
        } else {
            return null;
        }
    }

    /**
     * Возвращает кастомизатор для данного типа поля
     *
     * @param caption    заголовок поля
     * @param timezone   таймзона
     * @param locale     локаль
     * @param sortcolumn сортировка
     * @param disabled   активное поле или нет
     * @return кастомизатор
     * @throws GranException при необходимости
     */
    public Customizer getCustomizer(String caption, String timezone, String locale, String sortcolumn, boolean disabled) throws GranException {
        return new TextCustomizer(TextCustomizer.INTEGER, FieldMap.createUDF(caption, FValue.UDF + udfId, FValue.UDF_SORT + sortcolumn));
    }
}
