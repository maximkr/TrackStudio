package com.trackstudio.app.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.trackstudio.common.FieldMap;

import net.jcip.annotations.Immutable;

/**
 * Класс для работы с параметрами фильтрации задач
 */
@Immutable
public class TaskFValue extends FValue {
    public final static String[] strings = new String[]{ONPAGE, SUBTASK, FieldMap.SEARCH.getFilterKey()};

    /**
     * Конструктор
     */
    public TaskFValue() {
        super();
    }

    /**
     * Возвращает список используемых параметров фильтрации для задач
     *
     * @return список параметров фильтрации
     */
    public List<String> getUseForTask() {
        Set<String> use = getUse();
        List<String> ret = new ArrayList<String>();
        for (FieldMap field : FieldMap.taskFields) {
            if (use.contains(field.getFilterKey()))
                ret.add(field.getFilterKey());
        }
        return ret;
    }

    /**
     * Возвращает список используемых параметров фильтрации для сообщений
     *
     * @return список параметров фильтрации
     */
    public List<String> getUseForMessages() {
        Set<String> use = getUse();
        List<String> ret = new ArrayList<String>();
        for (FieldMap field : FieldMap.messageFields) {
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
        for (FieldMap field : FieldMap.messageFields) {
            use.remove(field.getFilterKey());
        }
        for (FieldMap field : FieldMap.taskFields) {
            use.remove(field.getFilterKey());
        }
        use.remove(ONPAGE);
        use.remove(SUBTASK);
        use.remove(FieldMap.SEARCH.getFilterKey());
        return use;
    }

    /**
     * тип сообщения
     */
    public static final String MSG_TYPE = "msg_type";
    /**
     * текст сообщения
     */
    public static final String MSG_TEXT = "msg_text";
}
