package com.trackstudio.app.filter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import net.jcip.annotations.ThreadSafe;

/**
 * Класс для хранения настроек фильтрации
 */
@ThreadSafe
public class FilterSettings implements Serializable, Comparable {
    private volatile FValue settings;
    private final String objectId;
    private volatile String filterId;

    private volatile CopyOnWriteArrayList<String> sortedBy = new CopyOnWriteArrayList<String>();
    private volatile int currentPage = 1;
    private volatile String fieldId;
    private volatile boolean all;

    public boolean isAll() {
        return all;
    }

    public void setAll(boolean all) {
        this.all = all;
    }

    /**
     * Конструктор
     *
     * @param settings настройки параметров фильтрации
     * @param object   объект
     * @param filter   фильтр
     */
    public FilterSettings(FValue settings, String object, String filter) {
        this.settings = settings;
        this.objectId = object;
        this.filterId = filter;
    }

    /**
     * Возвращает экземпляр текущего класса
     *
     * @return экземпляр текущего класса
     */
    public FValue getSettings() {
        return settings;
    }

    /**
     * Устанавливают настройки параметров фильтрации
     *
     * @param settings настройки параметров фильтрации
     */
    public void setSettings(FValue settings) {
        this.settings = settings;
    }

    /**
     * Возвращает ID объекта
     *
     * @return ID объекта
     */
    public String getObjectId() {
        return objectId;
    }

    /**
     * Возвращает ID фильтра
     *
     * @return ID фильтра
     */
    public String getFilterId() {
        return filterId;
    }

    /**
     * Устанавливает ID фильтра
     *
     * @param filterId ID фильтра
     */
    public void setFilterId(String filterId) {
        this.filterId = filterId;
    }

    /**
     * Возвращает параметры сортировки
     *
     * @return список параметров сортировки
     */
    public List<String> getSortedBy() {
        return sortedBy;
    }

    /**
     * Устанавливает параметры сортировки
     *
     * @param sortedBy список параметров сортировки
     */
    public void setSortedBy(ArrayList<String> sortedBy) {
        this.sortedBy = new CopyOnWriteArrayList(sortedBy);
    }

    /**
     * Возвращает текущую страницу результатов поиска
     *
     * @return текущая страница
     */
    public int getCurrentPage() {
        return currentPage;
    }

    /**
     * Устанавливает текущую страницу результатов поиска
     *
     * @param currentPage текущая страница
     */
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    /**
     * Возвращает ID фильтра
     *
     * @return ID фильтра
     */
    public String getFieldId() {
        return fieldId;
    }

    /**
     * Устанавливает ID фильтра
     *
     * @param fieldId ID фильтра
     */
    public void setFieldId(String fieldId) {
        this.fieldId = fieldId;
    }

    /**
     * Сравнивает два объекта текущего класса
     *
     * @param obj сравниваемый объект
     * @return TRUE - если равны, FALSE - если нет
     */
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof FilterSettings))
            return false;
        FilterSettings filterSettings = (FilterSettings) obj;
        if (!filterId.equals(filterSettings.filterId))
            return false;
        return objectId.equals(filterSettings.objectId);
    }

    /**
     * Сравнивает два объекта текущего класса
     *
     * @param o сравниваемый объект
     * @return +1, 0 или -1
     */
    public int compareTo(Object o) {
        FilterSettings fs = (FilterSettings) o;
        if (!this.equals(fs)) return -1;
        Set<String> k = new HashSet<String>(this.settings.keySet());
        k.addAll(fs.settings.keySet());
        for (String key : k) {
            List<String> a = this.settings.get(key);
            List<String> b = fs.settings.get(key);
            if (a != null && b == null) return -1;
            if (a == null && b != null) return -1;
            if (a != null && b != null)
                if (a.toString().compareTo(b.toString()) != 0)
                    return a.toString().compareTo(b.toString());
        }
        return 0;
    }

    /**
     * ВОзвращает хеш объекта
     *
     * @return хеш объекта
     */
    public int hashCode() {
        int result = objectId.hashCode();
        result = 29 * result + filterId.hashCode();
        return result;
    }
}
