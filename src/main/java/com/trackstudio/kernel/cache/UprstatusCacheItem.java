package com.trackstudio.kernel.cache;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.trackstudio.exception.GranException;
import com.trackstudio.tools.HibernateUtil;
import com.trackstudio.tools.Intern;
import com.trackstudio.tools.Null;

import net.jcip.annotations.Immutable;

/**
 * Вспомогательный класс, используемый для кеширования пользовательских статусов
 */
@Immutable
public class UprstatusCacheItem implements Serializable {
    private final static HibernateUtil hu = new HibernateUtil();
    private final CopyOnWriteArrayList<String> uprsList;
    private final String prstatusId;
    private final String udfId;

    /**
     * Возвращает ID статуса
     *
     * @return ID статуса
     */
    public String getPrstatusId() {
        return prstatusId;
    }

    /**
     * Возвращает ID поля
     *
     * @return ID поля
     */
    public String getUdfId() {
        return udfId;
    }

    /**
     * Возвращает список типов прав
     *
     * @return список типов прав
     */

    public List<String> getUprsList() {
        return uprsList;
    }

    /**
     * Конструктор
     *
     * @param prstatusId ID статуса
     * @param udfId      ID поля
     * @throws GranException при необзодимости
     */
    public UprstatusCacheItem(String prstatusId, String udfId) throws GranException {
            this.udfId = Intern.process(udfId);
            this.prstatusId = Intern.process(prstatusId);

            List<String> tmpList = Null.removeNullElementsFromList(hu.getList("select uprs.type from com.trackstudio.model.Uprstatus uprs where uprs.prstatus=? and uprs.udf=?", prstatusId, udfId));
            uprsList = new CopyOnWriteArrayList(Intern.process(tmpList));
    }

    /**
     * Конструктор
     *
     * @param prstatusId ID статуса
     * @param udfId      ID поля
     * @param uprsList   список типов прав
     */
    public UprstatusCacheItem(String prstatusId, String udfId, List<String> uprsList) {
        this.udfId = Intern.process(udfId);
        this.prstatusId = Intern.process(prstatusId);
        this.uprsList = new CopyOnWriteArrayList(Intern.process(Null.removeNullElementsFromList(uprsList)));
    }
}