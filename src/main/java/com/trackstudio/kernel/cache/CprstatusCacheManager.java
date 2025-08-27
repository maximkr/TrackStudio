package com.trackstudio.kernel.cache;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.exception.GranException;

import net.jcip.annotations.Immutable;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 * Rласс предназначен для кеширования прав доступа к категориям
 */
@Immutable
public class CprstatusCacheManager extends com.trackstudio.kernel.cache.CacheManager {
    private static final Log log = LogFactory.getLog(CprstatusCacheManager.class);
    private final Cache cprsCache;
    private static final CprstatusCacheManager instance = new CprstatusCacheManager();

    /**
     * Конструктор по умолчанию. Инициализирует кеш прав доступа к категориям
     *
     * @throws GranException при необходимости
     */
    //lock: write
    private CprstatusCacheManager() {
        CacheManager.getInstance().addCache("cprstatusCache");
        cprsCache = CacheManager.getInstance().getCache("cprstatusCache");
    }

    /**
     * Возвращает экземпляр текущего класса
     *
     * @return экземпляр CprstatusCacheManager
     * @throws GranException при необходимости
     */
    //lock: don't need
    public static CprstatusCacheManager getInstance() throws GranException {
        return instance;
    }


    /**
     * Изет значение права доступа статуса к категории
     *
     * @param prstatusId ID статуса
     * @param categoryId ID категории
     * @return Право доступа статуса к категории
     * @throws GranException при необходимости
     * @see com.trackstudio.kernel.cache.CprstatusCacheItem
     */
    //lock: read
    public CprstatusCacheItem find(String prstatusId, String categoryId) throws GranException {
        if (prstatusId == null || categoryId == null) return null;

        Element e = cprsCache.get(getKey(prstatusId, categoryId));
        if (e != null)
            return (CprstatusCacheItem) e.getObjectValue();

        CprstatusCacheItem vObj = new CprstatusCacheItem(prstatusId, categoryId); // this will init value from db automatically
        cprsCache.putIfAbsent(new Element(getKey(prstatusId, categoryId), vObj));
        return vObj;
    }

    /**
     * Очищает кеш прав доступа статусов к категориям
     *
     * @throws GranException при необходимости
     */
    //lock: write
    public void invalidate() {
        log.trace("invalidateAll");
        cprsCache.removeAll();
    }

    /**
     * Очищает кеш для указанного статуса
     *
     * @param prstatusId ID статуса
     * @throws GranException при необходимости
     */
    public void invalidateRemovePrstatus(String prstatusId) throws GranException {
        log.trace("invalidateRemovePrstatus");
        //todo maximkr: we shouldn't touch db here, should be enough to iterate elements in memory and remove invalid
        for (Object o : hu.getList("select cp.category.id from com.trackstudio.model.Cprstatus cp where cp.prstatus=?", prstatusId)) {
            String categoryId = (String) o;
            cprsCache.remove(getKey(prstatusId, categoryId));
        }
    }

    /**
     * Очищает кеш для указанной категории
     *
     * @param categoryId ID категории
     * @throws GranException при необходимости
     */
    public void invalidateRemoveCategory(String categoryId) throws GranException {
        log.trace("invalidateRemoveCategory");
        //todo maximkr: we shouldn't touch db here, should be enough to iterate elements in memory and remove invalid
        for (Object o : hu.getList("select cp.prstatus.id from com.trackstudio.model.Cprstatus cp where cp.category=?", categoryId)) {
            String prstatusId = (String) o;
            cprsCache.remove(getKey(prstatusId, categoryId));
        }
    }

    /**
     * Удаляет из кеша указанное право доступа
     *
     * @param prstatusId ID статуса
     * @param categoryId ID категории
     * @throws GranException при необзодимости
     */
    public void invalidateSingle(String prstatusId, String categoryId) throws GranException {
        log.trace("invalidateSingle");
        cprsCache.remove(getKey(prstatusId, categoryId));
    }

    /**
     * Возвращает ключ, по которому производится кеширование
     *
     * @param prstatusid ID статуса
     * @param categoryId ID категории
     * @return ключ
     */
    //lock: don't need
    private String getKey(String prstatusid, String categoryId) {
        return prstatusid + '*' + categoryId;
    }
}
