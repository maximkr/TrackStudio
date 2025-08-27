package com.trackstudio.kernel.cache;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.exception.GranException;

import net.jcip.annotations.Immutable;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 * Класс для работы с правами доступа к типам сообщений
 */
@Immutable
public class MprstatusCacheManager extends com.trackstudio.kernel.cache.CacheManager {

    private static final Log log = LogFactory.getLog(MprstatusCacheManager.class);
    private final Cache mprsCache;
    private static final MprstatusCacheManager instance = new MprstatusCacheManager();

    /**
     * Конструктор по умолчанию. Производится инициализация кеша
     *
     * @throws GranException при необходимости
     */
    private MprstatusCacheManager() {
        CacheManager.getInstance().addCache("mprstatusCache");
        mprsCache = CacheManager.getInstance().getCache("mprstatusCache");
    }

    /**
     * Возвращает экземпляр текущего класса
     *
     * @return экземпляр MprstatusCacheManager
     * @throws GranException при необходимости
     */
    public static MprstatusCacheManager getInstance() throws GranException {
        return instance;
    }


    /**
     * Ищет права доступа статуса к типу сообщения
     *
     * @param prstatusid ID статуса
     * @param mstatusid  ID типа сообщения
     * @return права доступа к типу сообщения
     * @throws GranException при необходимости
     * @see com.trackstudio.kernel.cache.MprstatusCacheItem
     */
    public MprstatusCacheItem find(String prstatusid, String mstatusid) throws GranException {
        if (prstatusid == null || mstatusid == null)
            return null;

        Element e = mprsCache.get(getKey(prstatusid, mstatusid));
        if (e != null)
            return (MprstatusCacheItem) e.getObjectValue();

        MprstatusCacheItem vObj = new MprstatusCacheItem(prstatusid, mstatusid);
        mprsCache.putIfAbsent(new Element(getKey(prstatusid, mstatusid), vObj));
        return vObj;
    }

    /**
     * Очищает кеш прав доступа
     *
     * @throws GranException при необзодимости
     */
    public void invalidate() throws GranException {
        log.trace("invalidateAll");
        mprsCache.removeAll();
    }

    /**
     * Очищает кеш прав доступа для определенного статуса
     *
     * @param prstatusId ID статуса
     * @throws GranException при необходимости
     */
    public void invalidateRemovePrstatus(String prstatusId) throws GranException {
        log.trace("invalidateRemovePrstatus");
        for (Object o : hu.getList("select mp.mstatus.id from com.trackstudio.model.Mprstatus mp where mp.prstatus=?", prstatusId)) {
            String mstatusId = (String) o;
            mprsCache.remove(getKey(prstatusId, mstatusId));
        }
    }

    /**
     * Очищает кеш прав доступа для определенного типа сообщения
     *
     * @param mstatusId ID типа сообщения
     * @throws GranException при необходимости
     */
    public void invalidateRemoveMstatus(String mstatusId) throws GranException {
        log.trace("invalidateRemoveMstatus");
        for (Object o : hu.getList("select mp.prstatus.id from com.trackstudio.model.Mprstatus mp where mp.mstatus=?", mstatusId)) {
            String prstatusId = (String) o;
            mprsCache.remove(getKey(prstatusId, mstatusId));
        }
    }

    /**
     * Удаляет из кеша указанное право доступа
     *
     * @param prstatusId ID статуса
     * @param mstatusId  ID типа сообщения
     * @throws GranException при необзодимости
     */
    public void invalidateSingle(String prstatusId, String mstatusId) throws GranException {
        log.trace("invalidateSingle");
        mprsCache.remove(getKey(prstatusId, mstatusId));
    }

    /**
     * Возвращает ключ, по которому производится кеширование
     *
     * @param prstatusid ID статуса
     * @param mstatusid  ID типа сообщения
     * @return ключ
     */
    private String getKey(String prstatusid, String mstatusid) {
        return prstatusid + '*' + mstatusid;
    }
}