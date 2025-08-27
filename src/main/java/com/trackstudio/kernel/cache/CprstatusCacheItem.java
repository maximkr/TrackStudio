package com.trackstudio.kernel.cache;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.trackstudio.exception.GranException;
import com.trackstudio.tools.HibernateUtil;
import com.trackstudio.tools.Intern;
import com.trackstudio.tools.Null;

import net.jcip.annotations.Immutable;

/**
 * Вспомогательный класс, используется для кеширования прав доступа статусов к категориям
 */
@Immutable
public class CprstatusCacheItem implements Serializable {
    private static final HibernateUtil hu = new HibernateUtil();
    private final CopyOnWriteArrayList<String> cprsList;
    private final String prstatusId;
    private final String categoryId;

    /**
     * Возвращает ID статуса
     *
     * @return ID статуса
     */
    //lock: don't need
    public String getPrstatusId() {
        return prstatusId;
    }

    /**
     * Возвращает ID категории
     *
     * @return ID категории
     */
    //lock: don't need
    public String getCategoryId() {
        return categoryId;
    }

    /**
     * Возвращает список типов прав доступа статуса к категории
     *
     * @return список прав
     */
    //lock: don't need
    public List<String> getCprsList() {
        return cprsList;
    }

    /**
     * Конструктор
     *
     * @param prstatusId ID статуса
     * @param categoryId ID категории
     * @throws GranException при необходимости
     */
    //lock: read
    public CprstatusCacheItem(String prstatusId, String categoryId) throws GranException {
        this.categoryId = Intern.process(categoryId);
        this.prstatusId = Intern.process(prstatusId);
        List<String> list = hu.getList("select cprs.type from com.trackstudio.model.Cprstatus cprs where cprs.prstatus=? and cprs.category=?", prstatusId, categoryId);
        if (list == null) {
            list = Collections.emptyList();
        }
        cprsList = new CopyOnWriteArrayList(Intern.process(Null.removeNullElementsFromList(list)));
    }
}
