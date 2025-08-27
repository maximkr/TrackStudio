package com.trackstudio.kernel.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.exception.GranException;
import com.trackstudio.model.Uprstatus;

import net.jcip.annotations.Immutable;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 * Класс предназначен для кеширования прав доступа статусов в пользовательским полям
 */
@Immutable
public class UprstatusCacheManager extends com.trackstudio.kernel.cache.CacheManager {
    private static final Log log = LogFactory.getLog(UprstatusCacheManager.class);
    private final Cache uprsCache;
    private static final UprstatusCacheManager instance = new UprstatusCacheManager();

    /**
     * Конструктор по умолчанию
     *
     * @throws GranException при необзодимости
     */
    private UprstatusCacheManager() {
        CacheManager.getInstance().addCache("uprstatusCache");
        uprsCache = net.sf.ehcache.CacheManager.getInstance().getCache("uprstatusCache");
    }

    /**
     * Взвращает экземпляр текущего класса
     *
     * @return экземпляр текущего класса
     * @throws GranException при необходимости
     */
    public static UprstatusCacheManager getInstance() throws GranException {
        return instance;
    }

    /**
     * Возвращает права доступа для статуса к полю
     *
     * @param prstatusId ID статуса
     * @param udfId      ID поля
     * @return права доступа
     * @throws GranException при необзодимости
     */
    public UprstatusCacheItem find(String prstatusId, String udfId) throws GranException {
        if (prstatusId == null || udfId == null)
            return null;

        Element e = uprsCache.get(getKey(prstatusId, udfId));
        if (e != null)
            return (UprstatusCacheItem) e.getObjectValue();

        UprstatusCacheItem vObj = new UprstatusCacheItem(prstatusId, udfId);
        uprsCache.putIfAbsent(new Element(getKey(prstatusId, udfId), vObj));
        return vObj;
    }

    /**
     * Очищает кеш статусов
     *
     * @throws GranException при необходимости
     */
    public void invalidate() throws GranException {
        log.trace("invalidateAll");
        uprsCache.removeAll();
    }

    /**
     * Очищает кеш прав для указаного статуса
     *
     * @param prstatusId ID статуса
     * @throws GranException при необходимости
     */
    public void invalidateRemovePrstatus(String prstatusId) throws GranException {
        log.trace("invalidateRemovePrstatus");
        for (Object o : hu.getList("select up.udf.id from com.trackstudio.model.Uprstatus up where up.prstatus=?", prstatusId)) {
            String udfId = (String) o;
            uprsCache.remove(getKey(prstatusId, udfId));
        }
    }

    /**
     * Очищает кеш прав для указаного поля
     *
     * @param udfId ID поля
     * @throws GranException при необходимости
     */
    public void invalidateRemoveUdf(String udfId) throws GranException {
        log.trace("invalidateRemoveUdf");
        for (Object o : hu.getList("select up.prstatus.id from com.trackstudio.model.Uprstatus up where up.udf=?", udfId)) {
            String prstatusId = (String) o;
            uprsCache.remove(getKey(prstatusId, udfId));
        }
    }

    /**
     * Удаляет из кеша указанное право
     *
     * @param prstatusId ID статуса
     * @param udfId      ID поля
     * @throws GranException при необходимости
     */
    public void invalidateSingle(String prstatusId, String udfId) throws GranException {
        log.trace("invalidateSingle");
        uprsCache.remove(getKey(prstatusId, udfId));
    }

    /**
     * Возвращает ключ, по которому производится кеширование
     *
     * @param prstatusid ID статуса
     * @param udfId      ID поля
     * @return ключ
     */
    private String getKey(String prstatusid, String udfId) {
        return prstatusid + '*' + udfId;
    }

    /**
     * Загружает права доступа
     *
     * @param udfSet      список полей
     * @param prstatusSet список статусов
     * @throws GranException при необходимости
     */
    protected void initUDFPermissions(Set<String> udfSet, Set<String> prstatusSet) throws GranException {
        if (udfSet.isEmpty() || prstatusSet.isEmpty())
            return;

        Map<String, Collection> params = new LinkedHashMap<String, Collection>();
        params.put("prstatusSet", prstatusSet);
        params.put("udfSet", udfSet);
        List<Uprstatus> uprstatusItems = hu.getListMap("select uprs from com.trackstudio.model.Uprstatus uprs where uprs.prstatus.id in (:prstatusSet) and uprs.udf.id in (:udfSet) order by uprs.udf.id", new LinkedHashMap<String, String>(0), params);
        String curUDFId = "";
        Map<String, List<String>> permMap = null;
        for (Uprstatus upr : uprstatusItems) {
            if (!curUDFId.equals(upr.getUdf().getId())) {
                if (curUDFId.length() > 0) {
                    for (String prstId : permMap.keySet()) {
                        uprsCache.put(new Element(getKey(prstId, curUDFId), new UprstatusCacheItem(prstId, curUDFId, permMap.get(prstId))));
                    }
                }
                permMap = new HashMap<String, List<String>>();
            }
            String prstatusId = upr.getPrstatus().getId();
            List<String> permList = permMap.get(prstatusId) == null ? new ArrayList<String>() : permMap.get(prstatusId);
            permList.add(upr.getType());
            permMap.put(prstatusId, permList);
            curUDFId = upr.getUdf().getId();
        }

        // fill permissions
        if (curUDFId.length() > 0) {
            for (String prstId : permMap.keySet()) {
                uprsCache.put(new Element(getKey(prstId, curUDFId), new UprstatusCacheItem(prstId, curUDFId, permMap.get(prstId))));
            }
        }
    }

}