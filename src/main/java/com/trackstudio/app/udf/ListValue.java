package com.trackstudio.app.udf;

import com.trackstudio.app.filter.FValue;
import com.trackstudio.app.filter.customizer.Customizer;
import com.trackstudio.app.filter.customizer.ListCustomizer;
import com.trackstudio.common.FieldMap;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.UdfvalCacheItem;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.tools.Intern;
import com.trackstudio.tools.Pair;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.Immutable;
import net.jcip.annotations.ThreadSafe;

/**
 * Класс, описывает значение пользовательского поля типа Список
 */
@Immutable
public class ListValue extends GenericValue {

	private final String valueId;

	private final String valueStr;

	/**
	 * Конструктор
	 *
	 * @param udfId ID поля
	 */
	public ListValue(String udfId) {
		super(udfId);
		valueId = null;
		valueStr = null;
	}

	/**
	 * Устанавливает значение
	 *
	 * @param val значение
	 * @throws GranException при необходимости
	 */
	public ListValue(UdfvalCacheItem val) throws GranException {
		super(val);
		valueId = Intern.process(val.getUdflistId());
		valueStr = Intern.process(val.getUdflistVal());
	}

	/**
	 * Возвращает значение пользовательского поля
	 *
	 * @param calculated Вычисляемое поле или нет
	 * @return дата
	 * @throws GranException при необходимости
	 */
	public Pair getValue(Object calculated) throws GranException {
		if (calculated != null && calculated.toString().length() != 0) {
			return findListValue(calculated.toString());
		}
		if (valueId != null) {
			return new Pair(valueId, valueStr);
		} else {
			return null;
		}
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
			Pair v = (Pair)o;
			return v.getKey() + "=" + v.getValue();
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
		return new ListCustomizer(
			KernelManager.getUdf().getUdflist(udfId),
			ListCustomizer.LIST_EQUAL,
			FieldMap.createUDF(caption, FValue.UDF + udfId, FValue.UDF_SORT + sortcolumn),
			true);
	}
}
