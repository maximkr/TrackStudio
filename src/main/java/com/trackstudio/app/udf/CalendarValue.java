package com.trackstudio.app.udf;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.trackstudio.app.filter.FValue;
import com.trackstudio.app.filter.customizer.Customizer;
import com.trackstudio.app.filter.customizer.DateCustomizer;
import com.trackstudio.common.FieldMap;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.UdfvalCacheItem;
import com.trackstudio.tools.formatter.DateFormatter;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.Immutable;
import net.jcip.annotations.ThreadSafe;

/**
 * Класс, описывает значение пользовательского поля типа Дата
 */
@Immutable
public class CalendarValue extends GenericValue {
    private final Calendar value;

    /**
     * Конструктор по умолчанию
     *
     * @param udfId ID поля
     */
    public CalendarValue(String udfId) {
        super(udfId);
        value = null;
    }

    public CalendarValue(UdfvalCacheItem val) {
        super(val);
        value = val.getDat();
    }

    /**
     * Возвращает значение пользовательского поля
     *
     * @param calculated Вычисляемое поле или нет
     * @return дата
     */
    public Calendar getValue(Object calculated) {
        if (calculated != null && calculated.toString().length() != 0) {
            if (calculated instanceof Date) {
                long time = ((Date) calculated).getTime();
                Calendar c = new GregorianCalendar();
                c.setTimeInMillis(time);
                return c;
            } else
                return (Calendar) calculated;
        }

        return value;
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
            Calendar v = (Calendar) o;
            return Long.toString(v.getTimeInMillis());
        } else {
            return null;
        }
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
    public Customizer getCustomizer(String caption, String timezone, String locale, String sortcolumn, boolean disabled) throws GranException {
        return new DateCustomizer(FieldMap.createUDF(caption, FValue.UDF + udfId, FValue.UDF_SORT + sortcolumn), new DateFormatter(timezone, locale));

    }
}
