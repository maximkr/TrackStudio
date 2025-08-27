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
 * Класс, описывает значение пользовательского поля типа Дробное
 */
@Immutable
public class FloatValue extends GenericValue {

	private final double value;

	/**
	 * Указывает простое значение или нет
	 */
	private final boolean hasSimpleValue;

	/**
	 * Конструктор
	 *
	 * @param udfId ШВ поля
	 */
	public FloatValue(String udfId) {
		super(udfId);
		value = 0;
		hasSimpleValue = false;
	}

	public FloatValue(UdfvalCacheItem val) {
		super(val);
		if (val.getNum()!=null) {
			value = val.getNum();
			hasSimpleValue = true;
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
	public Double getValue(Object calculated) {
		try {
			if (calculated != null && calculated.toString().length() != 0) {
				return new Double(calculated.toString());
			}

			if (hasSimpleValue) {
				return value;
			}

		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		}
		return null;
	}

	/**
	 * Возвращает значение в виде строки
	 *
	 * @param o значение
	 * @return строковое представление
	 * @throws GranException при необходимости
	 */
	public String getValueAsString(Object o) throws GranException {
		if (o != null) {
			Double v = (Double)o;
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
		return new TextCustomizer(TextCustomizer.FLOAT, FieldMap.createUDF(caption, FValue.UDF + udfId, FValue.UDF_SORT + sortcolumn));
	}
}
