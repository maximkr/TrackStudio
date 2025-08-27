package com.trackstudio.kernel.cache;

import java.io.Serializable;

import com.trackstudio.tools.PropertyComparable;
import com.trackstudio.tools.PropertyContainer;

import net.jcip.annotations.ThreadSafe;

/**
 * Вспомогательный класс, используется для кеширования плагинов
 */
@ThreadSafe
public abstract class AbstractPluginCacheItem extends PropertyComparable implements Serializable {

    /**
     * Тип плагина
     */
    protected final PluginType type;
    /**
     * Название и описание плагина
     */
    protected final String name;

    protected volatile String description;

    /**
     * Дата последнего изменения плагина
     */
    protected volatile long lastModified;


    /**
     * Возвращает тип плагина
     *
     * @return тип плагина
     */
    public PluginType getType() {
        return type;
    }


    /**
     * Возвращает название плагина
     *
     * @return название плагина
     */
    public String getName() {
        return name;
    }


    /**
     * Возвращает дату последнего изменения плагина
     *
     * @return дата последнего изменения плагина
     */
    public long getLastModified() {
        return lastModified;
    }

    /**
     * Устанавливает дату последнего изменения плагина
     *
     * @param lastModified дата последнего изменения плагина
     */
    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    /**
     * Возвращает описание плагина
     *
     * @return описание плагина
     */
    public String getDescription() {
        return description;
    }

    /**
     * Устанавливает описание плагина
     *
     * @param description описание плагина
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Конструктор
     *
     * @param type тип плагина
     * @param name название плагина
     */
    protected AbstractPluginCacheItem(PluginType type, String name) {
        this.type = type;
        this.name = name;
    }

    /**
     * Возвращает настройки плагина
     *
     * @return настройки плагина
     */
    protected PropertyContainer getContainer() {
        PropertyContainer pc = container.get();
        if (pc != null)
            return pc; // object in cache, return it

        PropertyContainer newPC = new PropertyContainer();
        newPC.put(name).put(type.toString());

        if (container.compareAndSet(null, newPC)) // try to update
            return newPC; // we can update - return loaded value
        else
            return container.get(); // some other thread already updated it - use saved value
    }
    
}
