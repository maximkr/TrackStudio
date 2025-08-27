package com.trackstudio.app.udf;

import com.trackstudio.app.filter.FValue;
import com.trackstudio.app.filter.customizer.Customizer;
import com.trackstudio.app.filter.customizer.TextCustomizer;
import com.trackstudio.common.FieldMap;
import com.trackstudio.containers.Link;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.UdfvalCacheItem;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

/**
 * Класс, описывает значение пользовательского поля типа Ссылка
 */
@ThreadSafe
public class LinkValue extends GenericValue {

	private final Link value;

	private final boolean hasSimpleValue;

	/**
	 * Конструктор
	 *
	 * @param udfId ID поля
	 */
	public LinkValue(String udfId) {
		super(udfId);
		value = null;
		hasSimpleValue = false;
	}

	public LinkValue(UdfvalCacheItem val) {
		super(val);
		if (val.getStr() != null) {
			hasSimpleValue = true;
			value = format(val.getStr());
		} else {
			hasSimpleValue = false;
			value = null;
		}
	}

	/**
	 * Возвращает значение типа ссылка
	 *
	 * @param calculated вычисляемое поле или нет
	 * @return ссылка
	 */
	public Link getValue(Object calculated) {
		String strValue;
		if (calculated != null) {
			strValue = calculated.toString();
			return format(strValue);
		} else if (hasSimpleValue) {
			return value;
		} else {
			return null;
		}
	}

	/**
	 * Возвращает значение поля в виде строки
	 *
	 * @param o значение
	 * @return строка
	 * @throws GranException при необходимости
	 */
	public String getValueAsString(Object o) throws GranException {
		if (o != null) {
			Link v = (Link)o;
			return v.getLink() + "\n" + v.getDescription();
		} else {
			return null;
		}
	}

	/**
	 * Возвращает отформатированную ссылку на основании строки
	 *
	 * @param strValue строковое значение
	 * @return при необходимости
	 */
	private static Link format(String strValue) {
		String url = "";
		String description = "";
		if (strValue != null) {
			int j = strValue.indexOf('\n');
			if (j > 1 && j < strValue.length() - 1) {
				url = strValue.substring(0, j);
				description = strValue.substring(j + 1);
			} else if (j == 0) {
				url = "";
				description = strValue;
			} else {
				url = strValue;
				description = strValue;
			}
		}

		url = url.replaceAll("\\\\", "/");
		if (url.indexOf("//") == 0) {
			url = "file:" + url;
		}
		return new Link(url, description);
	}

	/**
	 * Кастомизатор для данного типа поля
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
