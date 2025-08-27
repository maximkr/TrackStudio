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
 * Вспомогательный класс, используется для кеширования прав доступа статусов к сообщениям
 */
@Immutable
public class MprstatusCacheItem implements Serializable {
    private final static HibernateUtil hu = new HibernateUtil();
    private final String prstatusid;
    private final String mstatusid;
    private final CopyOnWriteArrayList<String> mprsList;

    /**
     * Возвращает ID статуса
     *
     * @return ID статуса
     */
    public String getPrstatusid() {
        return prstatusid;
    }

    /**
     * Возвращает ID типа сообщения
     *
     * @return ID типа сообщения
     */
    public String getMstatusid() {
        return mstatusid;
    }

   /**
     * Возвращает список прав доступа
     *
     * @return список статусов
     */
    public List<String> getMprsList() {
        return Collections.unmodifiableList(mprsList);
    }

    /**
     * Конструктор по умолчанию
     *
     * @param prstatusId ID статуса
     * @param mstatusId  ID типа сообщения
     * @throws GranException при необходимости
     */
    //lock: read
    public MprstatusCacheItem(String prstatusId, String mstatusId) throws GranException {
            this.mstatusid = Intern.process(mstatusId);
            this.prstatusid = Intern.process(prstatusId);
            mprsList = new CopyOnWriteArrayList(Intern.process(Null.removeNullElementsFromList(hu.getList("select mprs.type from com.trackstudio.model.Mprstatus mprs where mprs.prstatus=? and mprs.mstatus=?", prstatusId, mstatusId))));
    }
}