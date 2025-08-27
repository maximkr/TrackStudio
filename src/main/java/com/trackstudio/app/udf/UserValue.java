package com.trackstudio.app.udf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.trackstudio.app.filter.FValue;
import com.trackstudio.app.filter.customizer.Customizer;
import com.trackstudio.app.filter.customizer.PopUpCustomizer;
import com.trackstudio.common.FieldMap;
import com.trackstudio.exception.GranException;
import com.trackstudio.kernel.cache.UdfvalCacheItem;
import com.trackstudio.kernel.manager.KernelManager;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.Immutable;
import net.jcip.annotations.ThreadSafe;

import static com.trackstudio.kernel.cache.UserRelatedManager.getInstance;

/**
 * Класс, описывает значение пользовательского поля типа Пользователь
 */
@Immutable
public class UserValue extends GenericValue {
    private final ArrayList<String> value;

    /**
     * Конструктор по умолчанию
     *
     * @param udfId ID поля
     */
    public UserValue(String udfId) {
        super(udfId);
        value = null;
    }

    /**
     * Устанавливает значение поля
     *
     * @param val значение
     * @throws GranException при необзодимости
     */
    public UserValue(UserValue prev, UdfvalCacheItem val) throws GranException {
            super(val);
            if (prev==null || prev.value == null) {
                value = new ArrayList<String>();
            } else {
                value = prev.value;
            }
            if (!value.contains(val.getUserId()))
                value.add(val.getUserId());
    }

    /**
         * Возвращает список значений
         *
         * @param calculated вычисляемое поле или нет
         * @return список значений
         * @throws GranException при необходимости
         */
        public List<String> getValue(Object calculated) throws GranException {
            ArrayList<String> r = new ArrayList<String>();
            if (calculated != null && calculated.toString().length() != 0) {
                if (calculated instanceof Collection) {
                    Collection coll = Collections.synchronizedCollection(new ArrayList((Collection) calculated));
                    if (coll != null)
                        synchronized (coll) {
                            for (Object aColl : coll) {
                                String userLogin = aColl.toString();
                                String userId = KernelManager.getUser().findByLogin(userLogin);
                                if (userId != null && getInstance().isUserExists(userId))
                                    r.add(userId);
                            }
                        }
                    return r;
                }
            }
            return value;
        }


    /**
     * Возврашает строковое представление значения
     *
     * @param o значение
     * @return строковое представление
     * @throws GranException при необходимости
     */
    public String getValueAsString(Object o) throws GranException {
        if (o != null) {
            List<String> list = (List) o;
            StringBuffer v = new StringBuffer();
            if (!list.isEmpty()) {
                for (String val : list) {
                    v.append("\n");
                    v.append(val);
                }
                return v.substring(1);
            }
        }
        return null;
    }


    /**
     * Возвращает кастомизатор для данного типа поля
     * @deprecated Мы используем список выбора для этого типа поля, а не поп-ап
     * @param caption    заголовок поля
     * @param timezone   таймзона
     * @param locale     локаль
     * @param sortcolumn сортировка
     * @param disabled   активное поле или нет
     * @return кастомизатор
     * @throws GranException при необходимости
     */
    public Customizer getCustomizer(String caption, String timezone, String locale, String sortcolumn, boolean disabled) throws GranException {
            return new PopUpCustomizer(FieldMap.createUDF(caption, FValue.UDF + udfId, FValue.UDF_SORT + sortcolumn), PopUpCustomizer.USER, disabled);
    }

}
