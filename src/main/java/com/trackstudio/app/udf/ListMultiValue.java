package com.trackstudio.app.udf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.trackstudio.app.filter.FValue;
import com.trackstudio.app.filter.customizer.Customizer;
import com.trackstudio.app.filter.customizer.ListCustomizer;
import com.trackstudio.common.FieldMap;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.UdfvalCacheItem;
import com.trackstudio.kernel.manager.KernelManager;
import com.trackstudio.tools.Pair;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.Immutable;
import net.jcip.annotations.ThreadSafe;

/**
 * Класс, описывает значение пользовательского поля типа Множественный Список
 */
@Immutable
public class ListMultiValue extends GenericValue {

	protected final ArrayList<Pair> value;

	public ListMultiValue(String udfId) {
		super(udfId);
		value = null;
	}

	public ListMultiValue(ListMultiValue prev, UdfvalCacheItem val) {
		super(val);
		if (prev == null || prev.value == null) {
			value = new ArrayList<Pair>();
		} else {
			value = prev.value;
		}
		if (val.getUdflistVal() == null && val.getUdflistId() != null) {
			value.remove(new Pair(val.getUdflistId(), null));
		} else {
			Pair o = new Pair(val.getUdflistId(), val.getUdflistVal());
			int j = value.indexOf(o);
			if (j > -1) {
				value.get(j).setValue(val.getUdflistVal());
			} else {
				value.add(o);
			}
		}
	}

	/**
	 * Возвращает значение
	 *
	 * @param calculated вычисляемое поле или нет
	 * @return список значений
	 * @throws GranException при необходимости
	 */
	public List<Pair> getValue(Object calculated) throws GranException {
		if (calculated != null) {
			if (calculated instanceof Collection) {
				List<String> r = Collections.synchronizedList(new ArrayList<String>((Collection)calculated));
				ArrayList<Pair> value2 = new ArrayList<Pair>();
				for (String key : r) {
					Pair pair = findListValue(key);
					if (pair != null) {
						value2.add(findListValue(key));
					}
				}
				return value2;
			}
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
			List<Pair> list = (List)o;
			StringBuffer v = new StringBuffer();
			if (!list.isEmpty()) {
				for (Pair val : list) {
					v.append("\n");
					v.append(val.getKey());
					v.append("=");
					v.append(val.getValue());
				}
				return v.substring(1);
			}
		}
		return null;
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
			ListCustomizer.LIST_CONTAINS,
			FieldMap.createUDF(caption, FValue.UDF + udfId, FValue.UDF_SORT + sortcolumn),
			true);
	}
}
