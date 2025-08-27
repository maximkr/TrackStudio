package com.trackstudio.app.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.trackstudio.common.FieldMap;

import net.jcip.annotations.Immutable;

/**
 * Класс для работы с параметрами фильтрации пользователей
 */
@Immutable
public class UserFValue extends FValue {
    public final static String[] strings = new String[]{ONPAGE, DEEPSEARCH};

    /**
     * Конструктор
     */
    public UserFValue() {
        super();
    }

    /**
     * Возвращает список используемых параметров фильтрации для пользователей
     *
     * @return список параметров фильтрации
     */
    public List<String> getUseForUser() {
        Set<String> use = getUse();
        List<String> ret = new ArrayList<String>();
        for (FieldMap field : FieldMap.userFields) {
            if (use.contains(field.getFilterKey()))
                ret.add(field.getFilterKey());
        }
        return ret;
    }

    /**
     * Возвращает список используемых параметров фильтрации для пользовательских полей
     *
     * @return список параметров фильтрации
     */
    public Set<String> getUseForUdf() {
        Set<String> use = getUse();
        if (use != null) {
            for (FieldMap field : FieldMap.userFields) {
                use.remove(field.getFilterKey());
            }
            use.remove(DEEPSEARCH);
            use.remove(ONPAGE);
        }
        return use;
    }
}
