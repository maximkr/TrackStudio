package com.trackstudio.app.udf;

import com.trackstudio.app.filter.FValue;
import com.trackstudio.app.filter.customizer.Customizer;
import com.trackstudio.app.filter.customizer.TextCustomizer;
import com.trackstudio.common.FieldMap;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.UdfvalCacheItem;
import com.trackstudio.tools.Intern;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.Immutable;
import net.jcip.annotations.ThreadSafe;

/**
 * Класс, описывает значение пользовательского поля типа Строка
 */
@Immutable
public class StringValue extends GenericValue {

	private final String value;

	/**
	 * Конструктор
	 *
	 * @param udfId ID поля
	 */
	public StringValue(String udfId) {
		super(udfId);
		value = null;
	}

	/**
	 * Устанавливает значение
	 *
	 * @param val значение
	 * @throws GranException при необходимости
	 */
	public StringValue(UdfvalCacheItem val) throws GranException {
			super(val);
			if (val.getStr() != null) {
				value = Intern.process(val.getStr());
			} else {
				value = null;
			}
	}

	/**
	 * Возвращает значение пользовательского поля
	 *
	 * @param calculated Вычисляемое поле или нет
	 * @return дата
	 */
	public String getValue(Object calculated) {
		if (calculated != null) {
			return calculated.toString();
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
			return o.toString();
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
			return new TextCustomizer(TextCustomizer.CHARS, FieldMap.createUDF(caption, FValue.UDF + udfId, FValue.UDF_SORT + sortcolumn));
	}
}
