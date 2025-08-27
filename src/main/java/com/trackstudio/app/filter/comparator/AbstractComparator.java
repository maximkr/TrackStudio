package com.trackstudio.app.filter.comparator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.trackstudio.app.filter.AbstractFilter;
import com.trackstudio.app.filter.FValue;
import com.trackstudio.exception.GranException;
import com.trackstudio.secured.AbstractBeanWithUdf;
import com.trackstudio.secured.SecuredUDFBean;
import com.trackstudio.secured.SecuredUDFValueBean;
import com.trackstudio.tools.Null;

import net.jcip.annotations.Immutable;

/**
 * Абстрактный компаратор, от него наследуются другие компараторы
 */
@Immutable
public abstract class AbstractComparator extends AbstractFilter implements Comparator {

    /**
     * Карта полей
     */
    protected final ConcurrentMap<String, Boolean> fieldMap = new ConcurrentHashMap<String, Boolean>();
    /**
     * Список пользовательских полей
     */
    protected final CopyOnWriteArrayList<SecuredUDFBean> udfs;
    /**
     * Порядок сортировки
     */
    protected final CopyOnWriteArrayList<String> sortedOrder = new CopyOnWriteArrayList<String>();

    /**
     * Конструкторп по умолчанию
     *
     * @param sortOrder        порядок сортировки
     * @param udfHash          карта пользовательских полей
     * @param defaultSortOrder порядок сортировки по умолчанию (используется если другой н задан)
     */
    public AbstractComparator(List<String> sortOrder, ArrayList<SecuredUDFBean> udfHash, String defaultSortOrder) {
        if (sortOrder == null || sortOrder.isEmpty()) {
            boolean desc = defaultSortOrder.startsWith(FValue.SUB);
            fieldMap.put(desc ? defaultSortOrder.substring(1) : defaultSortOrder, desc);
            sortedOrder.add(desc ? defaultSortOrder.substring(1) : defaultSortOrder);
        } else {
            for (String token : sortOrder) {
                if (token.startsWith(FValue.SUB)) {
                    fieldMap.put(token.substring(1).toLowerCase(Locale.ENGLISH), Boolean.TRUE);
                    sortedOrder.add(token.substring(1).toLowerCase(Locale.ENGLISH));
                } else {
                    fieldMap.put(token.toLowerCase(Locale.ENGLISH), Boolean.FALSE);
                    sortedOrder.add(token.toLowerCase(Locale.ENGLISH));
                }
            }
        }
        this.udfs = new CopyOnWriteArrayList<SecuredUDFBean>(Null.removeNullElementsFromList(udfHash));
    }

    /**
     * Сравнивает пользовательские поля
     *
     * @param field  поле
     * @param sb1    первый объект с данными о пользовательском поле
     * @param sb2    второй объект с данными о пользовательском поле
     * @param retVal результат
     * @return +1, 0 или -1
     * @throws GranException при необзодимости
     */

    public int compareUdf(String field, AbstractBeanWithUdf sb1, AbstractBeanWithUdf sb2, int retVal) throws GranException {
        if (field.startsWith(FValue.UDF_SORT_LOWER_CAUSE) && udfs != null && !udfs.isEmpty()) {
            String udfid = field.substring(8); //FValue.UDF_SORT_LOWER_CAUSE.length()
            List<SecuredUDFValueBean> udfValues1 = sb1.getFilteredUDFValues();
            List<SecuredUDFValueBean> udfValues2 = sb2.getFilteredUDFValues();
            for (SecuredUDFBean udf : udfs) {
                if (udf.getUdfId().equals(udfid)) {
                    retVal = super.compare(getValue(udfValues1, udf.getId()), getValue(udfValues2, udf.getId()));
                }
            }
        }
        return retVal;
    }

    private Object getValue(List<SecuredUDFValueBean> udfs, String id) throws GranException {
        Object result = null;
        for (SecuredUDFValueBean udf: udfs) {
            if (udf.getId().equals(id)) {
                result = udf.getValue();
            }
        }
        return result;
    }


}
