package com.trackstudio.kernel.cache;

import java.awt.image.BufferedImage;

import net.jcip.annotations.ThreadSafe;

/**
 * Вспомогательный класс для кеширования бинарных плагинов
 */
@ThreadSafe
public class BinaryPluginCacheItem extends AbstractPluginCacheItem {

    private volatile BufferedImage data;

    /**
     * КОнструктор
     *
     * @param type тип плагина
     * @param name НАзвание плагина
     */
    public BinaryPluginCacheItem(PluginType type, String name) {
        super(type, name);

    }

    /**
     * Возвращает бинарные данные плагина
     *
     * @return бинарные данные
     */
    public BufferedImage getData() {
        return data;
    }

    /**
     * Устанавливает бинарные данные в плагин
     *
     * @param data бинарные данные
     */
    public void setData(BufferedImage data) {
        this.data = data;
    }
}
