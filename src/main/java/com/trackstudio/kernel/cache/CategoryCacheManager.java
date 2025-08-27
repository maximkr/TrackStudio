package com.trackstudio.kernel.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.exception.GranException;
import com.trackstudio.tools.Key;
import com.trackstudio.tools.Null;

import net.jcip.annotations.ThreadSafe;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 * Класс преднезначен для кеширования категорий
 */
@ThreadSafe
public class CategoryCacheManager extends com.trackstudio.kernel.cache.CacheManager {
    private static final Log log = LogFactory.getLog(CategoryCacheManager.class);
    private static final CategoryCacheManager instance = new CategoryCacheManager();
    private static Cache categoryCache;
    private final ConcurrentMap<String, Boolean> categoryIsValidCache = new ConcurrentHashMap<String, Boolean>();
    private final ConcurrentMap<Key.KeyID, Boolean> categoryIsViewableCache = new ConcurrentHashMap<Key.KeyID, Boolean>();

    /**
     * Конструктор по умолчанию. Инициализирует кеш категорий
     *
     * @throws GranException при необзодимости
     */
    //lock: write
    private CategoryCacheManager() {
        CacheManager.getInstance().addCache("categoryCache");
        categoryCache = CacheManager.getInstance().getCache("categoryCache");
        UserRelatedManager.getInstance();
    }

    /**
     * Возвращает экземпляр текущего класса
     *
     * @return экземпляр CategoryCacheManager
     * @throws GranException при необходимости
     */
    //lock: don't need
    public static CategoryCacheManager getInstance()  {
        return instance;
    }

    /**
     * Возвращает все доступные подкатегории, рекурсивно вглубь
     *
     * @param id  ID категории, для которой получаем подкатегории
     * @param ret список зподкатегорий
     * @throws GranException при необходимости
     */
    private void getAllPossibleSubcategoriesRecursive(String id, ArrayList<String> ret) throws GranException {
        CategoryCacheItem item = find(id);
        List<String> sub = item.getSubcategories();
        if (sub.isEmpty())
            return;
        //System.out.println(item.getName()+ " : "+sub);
        for (String catId : sub) {
            if (!ret.contains(catId)) {
                ret.add(catId);
                getAllPossibleSubcategoriesRecursive(catId, ret);
            }
        }
    }

    /**
     * Возвращает все доступные подкатегории
     *
     * @param id ID категории, для которой получаем подкатегории
     * @return список ID подкатегорий
     * @throws GranException при необходимости
     */
    public ArrayList<String> getAllPossibleSubcategories(String id) throws GranException {
        ArrayList<String> possible = new ArrayList<String>();
        getAllPossibleSubcategoriesRecursive(id, possible);
        return possible;
    }


    /**
     * Ищет категорию по ID
     *
     * @param id ID категории
     * @return категория
     * @throws GranException при необходимости
     * @see com.trackstudio.kernel.cache.CategoryCacheItem
     */
    //lock: read-write
    public CategoryCacheItem find(String id) throws GranException {
        if (Null.isNull(id)) {
            return null;
        }

        Element e = categoryCache.get(id);
        if (e != null)
            return (CategoryCacheItem) e.getObjectValue();

        for (Object o : hu.getList("select new com.trackstudio.kernel.cache.CategoryCacheItem(category.id, category.name, category.workflow.id, category.workflow.name, category.task.id, category.handlerRequired, category.groupHandlerAllowed, category.budget, category.preferences, category.icon) from com.trackstudio.model.Category as category where category.id=?", id)) {
            CategoryCacheItem vObj = (CategoryCacheItem) o;
            categoryCache.putIfAbsent(new Element(id, vObj));
            return vObj;
        }

        // should never reach this
        throw new GranException("No categories found");
    }

    /**
     * Очищает кеш категорий
     *
     * @throws GranException при необходимости
     */
    //lock: write
    public void invalidate() throws GranException {
        log.trace("invalidateAll");
        categoryCache.removeAll();
    }

    /**
     * Очищает кеш для конктретной указанной категории
     *
     * @param categoryId ID категории
     * @throws GranException при необходимости
     */
    //lock: write
    public void invalidateCategory(String categoryId) throws GranException {
        log.trace("invalidateCategory");
        categoryCache.removeAll();
    }

    /**
     * Возвращает значение валидности категории из кеша
     *
     * @param categoryId ID категории
     * @return TRUE - если категория валидна, FALSE - если нет, NULL - если значение не нвйдено
     * @throws GranException при необходимости
     */
    public Boolean getCategoryIsValid(String categoryId) throws GranException {
        return categoryIsValidCache.get(categoryId);
    }

    /**
     * Устанаваливает значение валидности категории в кеш
     *
     * @param categoryId ID категории
     * @param isValid    Валидность категории
     * @throws GranException при неорбходимости
     */
    public void setCategoryIsValid(String categoryId, Boolean isValid) throws GranException {
        categoryIsValidCache.put(categoryId, isValid);
    }

    /**
     * Очищает кеш валидности для конкретной категории
     *
     * @param categoryId ID категории
     * @throws GranException при необходимости
     */
    public void invalidateCategoryIsValid(String categoryId) throws GranException {
        categoryIsValidCache.remove(categoryId);
        invalidateCategoryIsViewable();
    }

    /**
     * Устанавливает данные о видимости категории в кеш
     *
     * @param keyItem    данные о категории
     * @param isViewable Видимость категории
     * @throws GranException при необходимости
     * @see com.trackstudio.kernel.cache.CategoryCacheItem
     */
    public void setCategoryIsViewable(Key.KeyID keyItem, Boolean isViewable) throws GranException {
        categoryIsViewableCache.put(keyItem, isViewable);
    }

    /**
     * Возвращает данные о видимости категории
     *
     * @return TRUE - если категория видима, FALSE - если нет, NULL - если значение в кеше не найдено
     * @throws GranException при необходимости
     */
    public Boolean getCategoryIsViewable(Key.KeyID key) throws GranException {
        return categoryIsViewableCache.get(key);
    }

    /**
     * Очищает кеш видимости категорий
     *
     * @throws GranException при необходимости
     */
    public void invalidateCategoryIsViewable() throws GranException {
        categoryIsViewableCache.clear();
    }
}